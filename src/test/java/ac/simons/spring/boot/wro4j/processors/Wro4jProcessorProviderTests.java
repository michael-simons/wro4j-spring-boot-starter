/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ac.simons.spring.boot.wro4j.processors;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michael J. Simons
 * @since 2016-01-31
 */
class Wro4jProcessorProviderTests {

	@Test
	void shouldProvideExpectedProcessors() {
		final Wro4jProcessorProvider processorProvider = new Wro4jProcessorProvider();

		final Map<String, ResourcePreProcessor> preProcessors = processorProvider.providePreProcessors();
		assertThat(preProcessors).isNotNull().hasSize(1).containsKey("removeSourceMaps");
		assertThat(preProcessors.get("removeSourceMaps")).isInstanceOf(RemoveSourceMapsProcessor.class);
		assertThat(processorProvider.providePostProcessors()).isEqualTo(new HashMap<String, ResourcePostProcessor>());
	}

}
