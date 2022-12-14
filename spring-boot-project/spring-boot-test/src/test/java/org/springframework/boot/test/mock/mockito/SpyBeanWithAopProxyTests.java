/*
 * Copyright 2012-2017 the original author or authors.
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

package org.springframework.boot.test.mock.mockito;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test {@link SpyBean} when mixed with Spring AOP.
 *
 * @author Phillip Webb
 * @see <a href="https://github.com/spring-projects/spring-boot/issues/5837">5837</a>
 */
@RunWith(SpringRunner.class)
public class SpyBeanWithAopProxyTests {

	@SpyBean
	private DateService dateService;

	@Test
	public void verifyShouldUseProxyTarget() throws Exception {
		Long d1 = this.dateService.getDate(false);
		Thread.sleep(200);
		Long d2 = this.dateService.getDate(false);
		assertThat(d1).isEqualTo(d2);
		verify(this.dateService, times(1)).getDate(false);
		verify(this.dateService, times(1)).getDate(matchesFalse());
		verify(this.dateService, times(1)).getDate(matchesAnyBoolean());
	}

	private boolean matchesFalse() {
		if (isTestingMockito1()) {
			Method method = ReflectionUtils.findMethod(
					ClassUtils.resolveClassName("org.mockito.Matchers", null), "eq",
					Boolean.TYPE);
			return (boolean) ReflectionUtils.invokeMethod(method, null, false);
		}
		return ArgumentMatchers.eq(false);
	}

	private boolean matchesAnyBoolean() {
		if (isTestingMockito1()) {
			Method method = ReflectionUtils.findMethod(
					ClassUtils.resolveClassName("org.mockito.Matchers", null),
					"anyBoolean");
			return (boolean) ReflectionUtils.invokeMethod(method, null);
		}
		return ArgumentMatchers.anyBoolean();
	}

	private boolean isTestingMockito1() {
		return ClassUtils.isPresent("org.mockito.ReturnValues", null);
	}

	@Configuration
	@EnableCaching(proxyTargetClass = true)
	@Import(DateService.class)
	static class Config {

		@Bean
		public CacheResolver cacheResolver(CacheManager cacheManager) {
			SimpleCacheResolver resolver = new SimpleCacheResolver();
			resolver.setCacheManager(cacheManager);
			return resolver;
		}

		@Bean
		public ConcurrentMapCacheManager cacheManager() {
			ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
			cacheManager.setCacheNames(Arrays.asList("test"));
			return cacheManager;
		}

	}

	@Service
	static class DateService {

		@Cacheable(cacheNames = "test")
		public Long getDate(boolean arg) {
			return System.nanoTime();
		}

	}

}
