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
package ac.simons.spring.boot.wro4j;

import java.util.ArrayList;
import java.util.List;

import ac.simons.spring.boot.wro4j.Wro4jProperties.WroManagerFactoryProperties;
import org.junit.jupiter.api.Test;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michael J. Simons
 * @since 2016-01-31
 */
class Wro4jPropertiesTests {

	@Test
	void defaultsShouldWork() {
		final Wro4jProperties properties = new Wro4jProperties();
		assertThat(properties.isDebug()).isTrue();
		assertThat(properties.isMinimizeEnabled()).isTrue();
		assertThat(properties.isGzipResources()).isTrue();
		assertThat(properties.getResourceWatcherUpdatePeriod()).isEqualTo(0);
		assertThat(properties.isResourceWatcherAsync()).isFalse();
		assertThat(properties.getCacheUpdatePeriod()).isEqualTo(0);
		assertThat(properties.getModelUpdatePeriod()).isEqualTo(0);
		assertThat(properties.getHeader()).isNull();
		assertThat(properties.isParallelPreprocessing()).isFalse();
		assertThat(properties.getConnectionTimeout()).isEqualTo(2000L);
		assertThat(properties.getEncoding()).isEqualTo("UTF-8");
		assertThat(properties.isIgnoreMissingResources()).isTrue();
		assertThat(properties.isIgnoreEmptyGroup()).isTrue();
		assertThat(properties.isIgnoreFailingProcessor()).isFalse();
		assertThat(properties.isCacheGzippedContent()).isTrue();
		assertThat(properties.isJmxEnabled()).isFalse();
		assertThat(properties.getMbeanName()).isNull();
		assertThat(properties.getFilterUrl()).isEqualTo("/wro4j");
		assertThat(properties.getModel()).isEqualTo("/wro.xml");
		assertThat(properties.getManagerFactory()).isNull();
		assertThat(properties.getPreProcessors()).isNull();
		assertThat(properties.getPostProcessors()).isNull();
		assertThat(properties.getCacheName()).isNull();
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

		assertThat(properties.isDebug()).isFalse();
		assertThat(properties.isMinimizeEnabled()).isFalse();
		assertThat(properties.isGzipResources()).isFalse();
		assertThat(properties.getResourceWatcherUpdatePeriod()).isEqualTo(23);
		assertThat(properties.isResourceWatcherAsync()).isTrue();
		assertThat(properties.getCacheUpdatePeriod()).isEqualTo(42);
		assertThat(properties.getModelUpdatePeriod()).isEqualTo(7);
		assertThat(properties.getHeader()).isEqualTo("If-Unmodified-Since: Sat, 29 Oct 1994 19:43:31 GMT");
		assertThat(properties.isParallelPreprocessing()).isTrue();
		assertThat(properties.getConnectionTimeout()).isEqualTo(100L);
		assertThat(properties.getEncoding()).isEqualTo("ISO-8859-1");
		assertThat(properties.isIgnoreMissingResources()).isFalse();
		assertThat(properties.isIgnoreEmptyGroup()).isFalse();
		assertThat(properties.isIgnoreFailingProcessor()).isTrue();
		assertThat(properties.isCacheGzippedContent()).isFalse();
		assertThat(properties.isJmxEnabled()).isTrue();
		assertThat(properties.getMbeanName()).isEqualTo("wro4jmbean");
		assertThat(properties.getFilterUrl()).isEqualTo("/owr");
		assertThat(properties.getModel()).isEqualTo("/owr.xml");
		assertThat(properties.getManagerFactory()).isEqualTo(managerFactory);
		assertThat(properties.getPreProcessors()).isEqualTo(preProcessors);
		assertThat(properties.getPostProcessors()).isEqualTo(postProcessors);
		assertThat(properties.getCacheName()).isEqualTo("super-duper-cache");
	}

	@Test
	void WroManagerFactoryPropertiesBeanShouldWork() {
		final WroManagerFactoryProperties wroManagerFactoryProperties = new WroManagerFactoryProperties();
		assertThat(wroManagerFactoryProperties.getPreProcessors()).isNull();
		assertThat(wroManagerFactoryProperties.getPostProcessors()).isNull();
		wroManagerFactoryProperties.setPreProcessors("preProcessors");
		wroManagerFactoryProperties.setPostProcessors("postProcessors");
		assertThat(wroManagerFactoryProperties.getPreProcessors()).isEqualTo("preProcessors");
		assertThat(wroManagerFactoryProperties.getPostProcessors()).isEqualTo("postProcessors");
	}

}
