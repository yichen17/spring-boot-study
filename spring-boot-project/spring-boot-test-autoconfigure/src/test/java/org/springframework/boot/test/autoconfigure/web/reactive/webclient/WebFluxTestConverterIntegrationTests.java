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

package org.springframework.boot.test.autoconfigure.web.reactive.webclient;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Tests for {@link WebFluxTest} to validate converters are discovered.
 *
 * @author Stephane Nicoll
 */
@RunWith(SpringRunner.class)
@WithMockUser
@WebFluxTest(controllers = ExampleController2.class)
public class WebFluxTestConverterIntegrationTests {

	@Autowired
	private WebTestClient webClient;

	@Test
	public void shouldFindConverter() {
		UUID id = UUID.randomUUID();
		this.webClient.get().uri("/two/" + id).exchange().expectStatus().isOk()
				.expectBody(String.class).isEqualTo(id + "two");
	}

}
