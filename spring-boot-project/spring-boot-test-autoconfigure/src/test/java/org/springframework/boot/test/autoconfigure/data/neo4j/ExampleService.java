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

package org.springframework.boot.test.autoconfigure.data.neo4j;

import org.neo4j.ogm.session.Session;

import org.springframework.stereotype.Service;

/**
 * Example service used with {@link DataNeo4jTest} tests.
 *
 * @author Eddú Meléndez
 */
@Service
public class ExampleService {

	private final Session session;

	public ExampleService(Session session) {
		this.session = session;
	}

	public boolean hasNode(Class<?> clazz) {
		return this.session.countEntitiesOfType(clazz) == 1;
	}

}
