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

package org.springframework.boot.autoconfigure.condition.scan;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBeanTests.ExampleBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBeanTests.ExampleFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for a factory bean produced by a bean method on a configuration class
 * found via component scanning.
 *
 * @author Andy Wilkinson
 */
@Configuration
public class ScannedFactoryBeanConfiguration {

	@Bean
	public FactoryBean<ExampleBean> exampleBeanFactoryBean() {
		return new ExampleFactoryBean("foo");
	}

}
