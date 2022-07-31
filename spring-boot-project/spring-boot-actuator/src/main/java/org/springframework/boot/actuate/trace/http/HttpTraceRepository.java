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

package org.springframework.boot.actuate.trace.http;

import java.util.List;

/**
 * A repository for {@link HttpTrace}s.
 *
 * @author Dave Syer
 * @author Andy Wilkinson
 * @since 2.0.0
 */
public interface HttpTraceRepository {

	/**
	 * Find all {@link HttpTrace} objects contained in the repository.
	 * @return the results
	 */
	List<HttpTrace> findAll();

	/**
	 * Adds a trace to the repository.
	 * @param trace the trace to add
	 */
	void add(HttpTrace trace);

}
