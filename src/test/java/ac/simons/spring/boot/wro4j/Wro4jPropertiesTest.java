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

package ac.simons.spring.boot.wro4j;

import java.util.ArrayList;
import java.util.List;

import ac.simons.spring.boot.wro4j.Wro4jProperties.WroManagerFactoryProperties;
import org.junit.Test;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


/**
 * @author Michael J. Simons, 2016-01-31
 */
public class Wro4jPropertiesTest {

	@Test
	public void defaultsShouldWork() {
		final Wro4jProperties properties = new Wro4jProperties();
		assertTrue(properties.isDebug());
		assertTrue(properties.isMinimizeEnabled());
		assertTrue(properties.isGzipResources());
		assertThat(properties.getResourceWatcherUpdatePeriod(), is(0));
		assertFalse(properties.isResourceWatcherAsync());
		assertThat(properties.getCacheUpdatePeriod(), is(0));
		assertThat(properties.getModelUpdatePeriod(), is(0));
		assertNull(properties.getHeader());
		assertFalse(properties.isParallelPreprocessing());
		assertThat(properties.getConnectionTimeout(), is(2000L));
		assertThat(properties.getEncoding(), is("UTF-8"));
		assertTrue(properties.isIgnoreMissingResources());
		assertTrue(properties.isIgnoreEmptyGroup());
		assertFalse(properties.isIgnoreFailingProcessor());
		assertTrue(properties.isCacheGzippedContent());
		assertFalse(properties.isJmxEnabled());
		assertNull(properties.getMbeanName());
		assertThat(properties.getFilterUrl(), is("/wro4j"));
		assertThat(properties.getModel(), is("/wro.xml"));
		assertNull(properties.getManagerFactory());
		assertNull(properties.getPreProcessors());
		assertNull(properties.getPostProcessors());
		assertNull(properties.getCacheName());
	}

	@Test
	public void settersShouldWork() {
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
		assertThat(properties.getResourceWatcherUpdatePeriod(), is(23));
		assertTrue(properties.isResourceWatcherAsync());
		assertThat(properties.getCacheUpdatePeriod(), is(42));
		assertThat(properties.getModelUpdatePeriod(), is(7));
		assertThat(properties.getHeader(), is("If-Unmodified-Since: Sat, 29 Oct 1994 19:43:31 GMT"));
		assertTrue(properties.isParallelPreprocessing());
		assertThat(properties.getConnectionTimeout(), is(100L));
		assertThat(properties.getEncoding(), is("ISO-8859-1"));
		assertFalse(properties.isIgnoreMissingResources());
		assertFalse(properties.isIgnoreEmptyGroup());
		assertTrue(properties.isIgnoreFailingProcessor());
		assertFalse(properties.isCacheGzippedContent());
		assertTrue(properties.isJmxEnabled());
		assertThat(properties.getMbeanName(), is("wro4jmbean"));
		assertThat(properties.getFilterUrl(), is("/owr"));
		assertThat(properties.getModel(), is("/owr.xml"));
		assertThat(properties.getManagerFactory(), is(managerFactory));
		assertThat(properties.getPreProcessors(), is(preProcessors));
		assertThat(properties.getPostProcessors(), is(postProcessors));
		assertThat(properties.getCacheName(), is("super-duper-cache"));
	}

	@Test
	public void WroManagerFactoryPropertiesBeanShouldWork() {
		final WroManagerFactoryProperties wroManagerFactoryProperties = new WroManagerFactoryProperties();
		assertNull(wroManagerFactoryProperties.getPreProcessors());
		assertNull(wroManagerFactoryProperties.getPostProcessors());
		wroManagerFactoryProperties.setPreProcessors("preProcessors");
		wroManagerFactoryProperties.setPostProcessors("postProcessors");
		assertThat(wroManagerFactoryProperties.getPreProcessors(), is("preProcessors"));
		assertThat(wroManagerFactoryProperties.getPostProcessors(), is("postProcessors"));
	}
}
