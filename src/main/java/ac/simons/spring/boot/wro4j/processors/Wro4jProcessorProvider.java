/*
 * Copyright 2015-2024 the original author or authors.
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

package ac.simons.spring.boot.wro4j.processors;

import java.util.Map;

import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;
import ro.isdc.wro.model.resource.processor.support.ProcessorProvider;

/**
 * Configures the additional processors this starter provides.
 *
 * @author Michael J. Simons, 2016-01-18
 */
public class Wro4jProcessorProvider implements ProcessorProvider {

	private final Map<String, ResourcePreProcessor> preProcessors;

	private final Map<String, ResourcePostProcessor> postProcessors;

	/**
	 * Required no-arg constructor for the service loader mechanism.
	 */
	public Wro4jProcessorProvider() {
		this.preProcessors = Map.of("removeSourceMaps", new RemoveSourceMapsProcessor());
		this.postProcessors = Map.of();
	}

	@Override
	public Map<String, ResourcePreProcessor> providePreProcessors() {
		return this.preProcessors;
	}

	@Override
	public Map<String, ResourcePostProcessor> providePostProcessors() {
		return this.postProcessors;
	}
}
