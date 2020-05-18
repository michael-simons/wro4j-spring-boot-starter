/*
 * Copyright 2015-2020 the original author or authors.
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

package ac.simons.spring.boot.wro4j;

import java.util.ArrayList;
import java.util.List;

import ac.simons.spring.boot.wro4j.Wro4jProperties.WroManagerFactoryProperties;
import org.junit.jupiter.api.Test;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Michael J. Simons
 *
 * @since 2016-01-31
 */
class Wro4jPropertiesTest {

	@Test
	void defaultsShouldWork() {
		final Wro4jProperties properties = new Wro4jProperties();
		assertTrue(properties.isDebug());
		assertTrue(properties.isMinimizeEnabled());
		assertTrue(properties.isGzipResources());
		assertEquals(0, properties.getResourceWatcherUpdatePeriod());
		assertFalse(properties.isResourceWatcherAsync());
		assertEquals(0, properties.getCacheUpdatePeriod());
		assertEquals(0, properties.getModelUpdatePeriod());
		assertNull(properties.getHeader());
		assertFalse(properties.isParallelPreprocessing());
		assertEquals(2000L, properties.getConnectionTimeout());
		assertEquals("UTF-8", properties.getEncoding());
		assertTrue(properties.isIgnoreMissingResources());
		assertTrue(properties.isIgnoreEmptyGroup());
		assertFalse(properties.isIgnoreFailingProcessor());
		assertTrue(properties.isCacheGzippedContent());
		assertFalse(properties.isJmxEnabled());
		assertNull(properties.getMbeanName());
		assertEquals("/wro4j", properties.getFilterUrl());
		assertEquals("/wro.xml", properties.getModel());
		assertNull(properties.getManagerFactory());
		assertNull(properties.getPreProcessors());
		assertNull(properties.getPostProcessors());
		assertNull(properties.getCacheName());
	}

	@Test
	void settersShouldWork() {
		final Wro4jProperties properties = new Wro4jProperties();

		properties.setDebug(false);
		properties.setMinimizeEnabled(false);
		properties.setGzipResources(false);
		properties.setResourceWatcherUpdatePeriod(23);
		properties.setResourceWatcherAsync(true);
		properties.setCacheUpdatePeriod(42);
		properties.setModelUpdatePeriod(7);
		properties.setHeader("If-Unmodified-Since: Sat, 29 Oct 1994 19:43:31 GMT");
		properties.setParallelPreprocessing(true);
		properties.setConnectionTimeout(100L);
		properties.setEncoding("ISO-8859-1");
		properties.setIgnoreMissingResources(false);
		properties.setIgnoreEmptyGroup(false);
		properties.setIgnoreFailingProcessor(true);
		properties.setCacheGzippedContent(false);
		properties.setJmxEnabled(true);
		properties.setMbeanName("wro4jmbean");
		properties.setFilterUrl("/owr");
		properties.setModel("/owr.xml");
		final WroManagerFactoryProperties managerFactory = new WroManagerFactoryProperties();
		properties.setManagerFactory(managerFactory);
		final List<Class<? extends ResourcePreProcessor>> preProcessors = new ArrayList<>();
		properties.setPreProcessors(preProcessors);
		final List<Class<? extends ResourcePostProcessor>> postProcessors = new ArrayList<>();
		properties.setPostProcessors(postProcessors);
		properties.setCacheName("super-duper-cache");

		assertFalse(properties.isDebug());
		assertFalse(properties.isMinimizeEnabled());
		assertFalse(properties.isGzipResources());
		assertEquals(23, properties.getResourceWatcherUpdatePeriod());
		assertTrue(properties.isResourceWatcherAsync());
		assertEquals(42, properties.getCacheUpdatePeriod());
		assertEquals(7, properties.getModelUpdatePeriod());
		assertEquals("If-Unmodified-Since: Sat, 29 Oct 1994 19:43:31 GMT", properties.getHeader());
		assertTrue(properties.isParallelPreprocessing());
		assertEquals(100L, properties.getConnectionTimeout());
		assertEquals("ISO-8859-1", properties.getEncoding());
		assertFalse(properties.isIgnoreMissingResources());
		assertFalse(properties.isIgnoreEmptyGroup());
		assertTrue(properties.isIgnoreFailingProcessor());
		assertFalse(properties.isCacheGzippedContent());
		assertTrue(properties.isJmxEnabled());
		assertEquals("wro4jmbean", properties.getMbeanName());
		assertEquals("/owr", properties.getFilterUrl());
		assertEquals("/owr.xml", properties.getModel());
		assertEquals(managerFactory, properties.getManagerFactory());
		assertEquals(preProcessors, properties.getPreProcessors());
		assertEquals(postProcessors, properties.getPostProcessors());
		assertEquals("super-duper-cache", properties.getCacheName());
	}

	@Test
	void WroManagerFactoryPropertiesBeanShouldWork() {
		final WroManagerFactoryProperties wroManagerFactoryProperties = new WroManagerFactoryProperties();
		assertNull(wroManagerFactoryProperties.getPreProcessors());
		assertNull(wroManagerFactoryProperties.getPostProcessors());
		wroManagerFactoryProperties.setPreProcessors("preProcessors");
		wroManagerFactoryProperties.setPostProcessors("postProcessors");
		assertEquals("preProcessors", wroManagerFactoryProperties.getPreProcessors());
		assertEquals("postProcessors", wroManagerFactoryProperties.getPostProcessors());
	}
}
