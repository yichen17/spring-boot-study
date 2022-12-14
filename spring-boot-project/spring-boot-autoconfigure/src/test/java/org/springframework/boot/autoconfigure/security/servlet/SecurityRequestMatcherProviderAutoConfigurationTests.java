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
package org.springframework.boot.autoconfigure.security.servlet;

import org.junit.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SecurityRequestMatcherProviderAutoConfiguration}.
 *
 * @author Madhura Bhave
 */
public class SecurityRequestMatcherProviderAutoConfigurationTests {

	private WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
			.withConfiguration(AutoConfigurations
					.of(SecurityRequestMatcherProviderAutoConfiguration.class))
			.withUserConfiguration(TestConfiguration.class);

	@Test
	public void registersMvcRequestMatcherProviderIfMvcPresent() {
		this.contextRunner.run((context) -> assertThat(context)
				.hasSingleBean(MvcRequestMatcherProvider.class));
	}

	@Test
	public void mvcRequestMatcherProviderConditionalOnWebApplication() {
		new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations
						.of(SecurityRequestMatcherProviderAutoConfiguration.class))
				.withUserConfiguration(TestConfiguration.class)
				.run((context) -> assertThat(context)
						.doesNotHaveBean(MvcRequestMatcherProvider.class));
	}

	@Test
	public void mvcRequestMatcherProviderConditionalOnDispatcherServletClass() {
		this.contextRunner
				.withClassLoader(new FilteredClassLoader(
						"org.springframework.web.servlet.DispatcherServlet"))
				.run((context) -> assertThat(context)
						.doesNotHaveBean(MvcRequestMatcherProvider.class));
	}

	@Test
	public void mvcRequestMatcherProviderConditionalOnRequestMatcherClass() {
		this.contextRunner
				.withClassLoader(new FilteredClassLoader(
						"org.springframework.security.web.util.matcher.RequestMatcher"))
				.run((context) -> assertThat(context)
						.doesNotHaveBean(MvcRequestMatcherProvider.class));
	}

	@Test
	public void mvcRequestMatcherProviderConditionalOnHandlerMappingIntrospectorBean() {
		new WebApplicationContextRunner()
				.withConfiguration(AutoConfigurations
						.of(SecurityRequestMatcherProviderAutoConfiguration.class))
				.run((context) -> assertThat(context)
						.doesNotHaveBean(MvcRequestMatcherProvider.class));
	}

	@Configuration
	static class TestConfiguration {

		@Bean
		public HandlerMappingIntrospector introspector() {
			return new HandlerMappingIntrospector();
		}

	}

}
