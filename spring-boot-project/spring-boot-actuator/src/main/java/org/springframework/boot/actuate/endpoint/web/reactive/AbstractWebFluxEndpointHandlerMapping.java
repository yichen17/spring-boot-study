/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.actuate.endpoint.web.reactive;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.core.scheduler.Schedulers;

import org.springframework.boot.actuate.endpoint.InvalidEndpointRequestException;
import org.springframework.boot.actuate.endpoint.InvocationContext;
import org.springframework.boot.actuate.endpoint.OperationType;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.endpoint.invoke.OperationInvoker;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.WebOperation;
import org.springframework.boot.actuate.endpoint.web.WebOperationRequestPredicate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.result.condition.ConsumesRequestCondition;
import org.springframework.web.reactive.result.condition.PatternsRequestCondition;
import org.springframework.web.reactive.result.condition.ProducesRequestCondition;
import org.springframework.web.reactive.result.condition.RequestMethodsRequestCondition;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * A custom {@link HandlerMapping} that makes web endpoints available over HTTP using
 * Spring WebFlux.
 *
 * @author Andy Wilkinson
 * @author Madhura Bhave
 * @author Phillip Webb
 * @since 2.0.0
 */
public abstract class AbstractWebFluxEndpointHandlerMapping
		extends RequestMappingInfoHandlerMapping {

	private static final PathPatternParser pathPatternParser = new PathPatternParser();

	private final EndpointMapping endpointMapping;

	private final Collection<ExposableWebEndpoint> endpoints;

	private final EndpointMediaTypes endpointMediaTypes;

	private final CorsConfiguration corsConfiguration;

	private final Method linksMethod = ReflectionUtils.findMethod(getClass(), "links",
			ServerWebExchange.class);

	private final Method handleWriteMethod = ReflectionUtils.findMethod(
			WriteOperationHandler.class, "handle", ServerWebExchange.class, Map.class);

	private final Method handleReadMethod = ReflectionUtils
			.findMethod(ReadOperationHandler.class, "handle", ServerWebExchange.class);

	/**
	 * Creates a new {@code AbstractWebFluxEndpointHandlerMapping} that provides mappings
	 * for the operations of the given {@code webEndpoints}.
	 * @param endpointMapping the base mapping for all endpoints
	 * @param endpoints the web endpoints
	 * @param endpointMediaTypes media types consumed and produced by the endpoints
	 * @param corsConfiguration the CORS configuration for the endpoints
	 */
	public AbstractWebFluxEndpointHandlerMapping(EndpointMapping endpointMapping,
			Collection<ExposableWebEndpoint> endpoints,
			EndpointMediaTypes endpointMediaTypes, CorsConfiguration corsConfiguration) {
		this.endpointMapping = endpointMapping;
		this.endpoints = endpoints;
		this.endpointMediaTypes = endpointMediaTypes;
		this.corsConfiguration = corsConfiguration;
		setOrder(-100);
	}

	@Override
	protected void initHandlerMethods() {
		for (ExposableWebEndpoint endpoint : this.endpoints) {
			for (WebOperation operation : endpoint.getOperations()) {
				registerMappingForOperation(endpoint, operation);
			}
		}
		if (StringUtils.hasText(this.endpointMapping.getPath())) {
			registerLinksMapping();
		}
	}

	private void registerMappingForOperation(ExposableWebEndpoint endpoint,
			WebOperation operation) {
		OperationInvoker invoker = operation::invoke;
		if (operation.isBlocking()) {
			invoker = new ElasticSchedulerInvoker(invoker);
		}
		ReactiveWebOperation reactiveWebOperation = wrapReactiveWebOperation(endpoint,
				operation, new ReactiveWebOperationAdapter(invoker));
		if (operation.getType() == OperationType.WRITE) {
			registerMapping(createRequestMappingInfo(operation),
					new WriteOperationHandler((reactiveWebOperation)),
					this.handleWriteMethod);
		}
		else {
			registerMapping(createRequestMappingInfo(operation),
					new ReadOperationHandler((reactiveWebOperation)),
					this.handleReadMethod);
		}
	}

	/**
	 * Hook point that allows subclasses to wrap the {@link ReactiveWebOperation} before
	 * it's called. Allows additional features, such as security, to be added.
	 * @param endpoint the source endpoint
	 * @param operation the source operation
	 * @param reactiveWebOperation the reactive web operation to wrap
	 * @return a wrapped reactive web operation
	 */
	protected ReactiveWebOperation wrapReactiveWebOperation(ExposableWebEndpoint endpoint,
			WebOperation operation, ReactiveWebOperation reactiveWebOperation) {
		return reactiveWebOperation;
	}

	private RequestMappingInfo createRequestMappingInfo(WebOperation operation) {
		WebOperationRequestPredicate predicate = operation.getRequestPredicate();
		PatternsRequestCondition patterns = new PatternsRequestCondition(pathPatternParser
				.parse(this.endpointMapping.createSubPath(predicate.getPath())));
		RequestMethodsRequestCondition methods = new RequestMethodsRequestCondition(
				RequestMethod.valueOf(predicate.getHttpMethod().name()));
		ConsumesRequestCondition consumes = new ConsumesRequestCondition(
				StringUtils.toStringArray(predicate.getConsumes()));
		ProducesRequestCondition produces = new ProducesRequestCondition(
				StringUtils.toStringArray(predicate.getProduces()));
		return new RequestMappingInfo(null, patterns, methods, null, null, consumes,
				produces, null);
	}

	private void registerLinksMapping() {
		PatternsRequestCondition patterns = new PatternsRequestCondition(
				pathPatternParser.parse(this.endpointMapping.getPath()));
		RequestMethodsRequestCondition methods = new RequestMethodsRequestCondition(
				RequestMethod.GET);
		ProducesRequestCondition produces = new ProducesRequestCondition(
				StringUtils.toStringArray(this.endpointMediaTypes.getProduced()));
		RequestMappingInfo mapping = new RequestMappingInfo(patterns, methods, null, null,
				null, produces, null);
		registerMapping(mapping, this, this.linksMethod);
	}

	@Override
	protected CorsConfiguration initCorsConfiguration(Object handler, Method method,
			RequestMappingInfo mapping) {
		return this.corsConfiguration;
	}

	@Override
	protected boolean isHandler(Class<?> beanType) {
		return false;
	}

	@Override
	protected RequestMappingInfo getMappingForMethod(Method method,
			Class<?> handlerType) {
		return null;
	}

	protected abstract Object links(ServerWebExchange exchange);

	/**
	 * Return the web endpoints being mapped.
	 * @return the endpoints
	 */
	public Collection<ExposableWebEndpoint> getEndpoints() {
		return this.endpoints;
	}

	/**
	 * An {@link OperationInvoker} that performs the invocation of a blocking operation on
	 * a separate thread using Reactor's {@link Schedulers#elastic() elastic scheduler}.
	 */
	protected static final class ElasticSchedulerInvoker implements OperationInvoker {

		private final OperationInvoker invoker;

		public ElasticSchedulerInvoker(OperationInvoker invoker) {
			this.invoker = invoker;
		}

		@Override
		public Object invoke(InvocationContext context) {
			return Mono.create(
					(sink) -> Schedulers.elastic().schedule(() -> invoke(context, sink)));
		}

		private void invoke(InvocationContext context, MonoSink<Object> sink) {
			try {
				Object result = this.invoker.invoke(context);
				sink.success(result);
			}
			catch (Exception ex) {
				sink.error(ex);
			}
		}

	}

	/**
	 * A reactive web operation that can be handled by WebFlux.
	 */
	@FunctionalInterface
	protected interface ReactiveWebOperation {

		Mono<ResponseEntity<Object>> handle(ServerWebExchange exchange,
				Map<String, String> body);

	}

	/**
	 * Adapter class to convert an {@link OperationInvoker} into a
	 * {@link ReactiveWebOperation}.
	 */
	private static final class ReactiveWebOperationAdapter
			implements ReactiveWebOperation {

		private final OperationInvoker invoker;

		private final Supplier<Mono<? extends SecurityContext>> securityContextSupplier;

		private ReactiveWebOperationAdapter(OperationInvoker invoker) {
			this.invoker = invoker;
			this.securityContextSupplier = getSecurityContextSupplier();
		}

		private Supplier<Mono<? extends SecurityContext>> getSecurityContextSupplier() {
			if (ClassUtils.isPresent(
					"org.springframework.security.core.context.ReactiveSecurityContextHolder",
					getClass().getClassLoader())) {
				return this::springSecurityContext;
			}
			return this::emptySecurityContext;
		}

		public Mono<? extends SecurityContext> springSecurityContext() {
			return ReactiveSecurityContextHolder.getContext()
					.map((securityContext) -> new ReactiveSecurityContext(
							securityContext.getAuthentication()))
					.switchIfEmpty(Mono.just(new ReactiveSecurityContext(null)));
		}

		public Mono<SecurityContext> emptySecurityContext() {
			return Mono.just(SecurityContext.NONE);
		}

		@Override
		public Mono<ResponseEntity<Object>> handle(ServerWebExchange exchange,
				Map<String, String> body) {
			Map<String, Object> arguments = getArguments(exchange, body);
			return this.securityContextSupplier.get()
					.map((securityContext) -> new InvocationContext(securityContext,
							arguments))
					.flatMap((invocationContext) -> handleResult(
							(Publisher<?>) this.invoker.invoke(invocationContext),
							exchange.getRequest().getMethod()));
		}

		private Map<String, Object> getArguments(ServerWebExchange exchange,
				Map<String, String> body) {
			Map<String, Object> arguments = new LinkedHashMap<>();
			arguments.putAll(getTemplateVariables(exchange));
			if (body != null) {
				arguments.putAll(body);
			}
			exchange.getRequest().getQueryParams().forEach((name, values) -> arguments
					.put(name, (values.size() != 1) ? values : values.get(0)));
			return arguments;
		}

		private Map<String, String> getTemplateVariables(ServerWebExchange exchange) {
			return exchange.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		}

		private Mono<ResponseEntity<Object>> handleResult(Publisher<?> result,
				HttpMethod httpMethod) {
			return Mono.from(result).map(this::toResponseEntity)
					.onErrorMap(InvalidEndpointRequestException.class,
							(ex) -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
									ex.getReason()))
					.defaultIfEmpty(new ResponseEntity<>((httpMethod != HttpMethod.GET)
							? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND));
		}

		private ResponseEntity<Object> toResponseEntity(Object response) {
			if (!(response instanceof WebEndpointResponse)) {
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			WebEndpointResponse<?> webEndpointResponse = (WebEndpointResponse<?>) response;
			return new ResponseEntity<>(webEndpointResponse.getBody(),
					HttpStatus.valueOf(webEndpointResponse.getStatus()));
		}

	}

	/**
	 * Handler for a {@link ReactiveWebOperation}.
	 */
	private final class WriteOperationHandler {

		private final ReactiveWebOperation operation;

		WriteOperationHandler(ReactiveWebOperation operation) {
			this.operation = operation;
		}

		@ResponseBody
		public Publisher<ResponseEntity<Object>> handle(ServerWebExchange exchange,
				@RequestBody(required = false) Map<String, String> body) {
			return this.operation.handle(exchange, body);
		}

	}

	/**
	 * Handler for a {@link ReactiveWebOperation}.
	 */
	private final class ReadOperationHandler {

		private final ReactiveWebOperation operation;

		ReadOperationHandler(ReactiveWebOperation operation) {
			this.operation = operation;
		}

		@ResponseBody
		public Publisher<ResponseEntity<Object>> handle(ServerWebExchange exchange) {
			return this.operation.handle(exchange, null);
		}

	}

	private static final class ReactiveSecurityContext implements SecurityContext {

		private final RoleVoter roleVoter = new RoleVoter();

		private final Authentication authentication;

		ReactiveSecurityContext(Authentication authentication) {
			this.authentication = authentication;
		}

		@Override
		public Principal getPrincipal() {
			return this.authentication;
		}

		@Override
		public boolean isUserInRole(String role) {
			if (!role.startsWith(this.roleVoter.getRolePrefix())) {
				role = this.roleVoter.getRolePrefix() + role;
			}
			return this.roleVoter.vote(this.authentication, null,
					Collections.singletonList(new SecurityConfig(
							role))) == AccessDecisionVoter.ACCESS_GRANTED;
		}

	}

}
