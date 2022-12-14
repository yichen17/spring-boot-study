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

package org.springframework.boot.actuate.autoconfigure.audit;

import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.AuditEventsEndpoint;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for the {@link LoggersEndpoint}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @author Vedran Pavic
 * @since 2.0.0
 */
@Configuration
@AutoConfigureAfter(AuditAutoConfiguration.class)
public class AuditEventsEndpointAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(AuditEventRepository.class)
	@ConditionalOnEnabledEndpoint
	public AuditEventsEndpoint auditEventsEndpoint(
			AuditEventRepository auditEventRepository) {
		return new AuditEventsEndpoint(auditEventRepository);
	}

}
