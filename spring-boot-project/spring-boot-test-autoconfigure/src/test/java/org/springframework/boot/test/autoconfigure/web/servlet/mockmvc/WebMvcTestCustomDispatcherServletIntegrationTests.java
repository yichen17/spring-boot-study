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

package org.springframework.boot.test.autoconfigure.web.servlet.mockmvc;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.DispatcherServlet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for Test {@link DispatcherServlet} customizations.
 *
 * @author Stephane Nicoll
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@WithMockUser
@TestPropertySource(properties = { "spring.mvc.throw-exception-if-no-handler-found=true",
		"spring.mvc.static-path-pattern=/static/**" })
public class WebMvcTestCustomDispatcherServletIntegrationTests {

	@Autowired
	private MockMvc mvc;

	@Test
	public void dispatcherServletIsCustomized() throws Exception {
		this.mvc.perform(get("/does-not-exist")).andExpect(status().isBadRequest())
				.andExpect(content().string("Invalid request: /does-not-exist"));
	}

}
