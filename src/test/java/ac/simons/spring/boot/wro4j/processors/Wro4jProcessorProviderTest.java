/*
 * Copyright 2015-2016 the original author or authors.
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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;

/**
 * @author Michael J. Simons, 2016-01-31
 */
public class Wro4jProcessorProviderTest {

	@Test
	public void shouldProvideExpectedProcessors() {
		final Wro4jProcessorProvider processorProvider = new Wro4jProcessorProvider();

		final Map<String, ResourcePreProcessor> preProcessors = processorProvider.providePreProcessors();
		Assert.assertNotNull(preProcessors);
		Assert.assertEquals(1, preProcessors.size());
		Assert.assertTrue(preProcessors.containsKey("removeSourceMaps"));
		Assert.assertTrue(preProcessors.get("removeSourceMaps") instanceof RemoveSourceMapsProcessor);
		Assert.assertEquals(new HashMap<String, ResourcePostProcessor>(), processorProvider.providePostProcessors());
	}

}
