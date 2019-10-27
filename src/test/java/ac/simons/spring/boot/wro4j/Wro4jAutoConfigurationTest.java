/*
 * Copyright 2015-2019 the original author or authors.
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

import ac.simons.spring.boot.wro4j.Wro4jProperties.WroManagerFactoryProperties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import ro.isdc.wro.config.jmx.ConfigConstants;
import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.model.resource.processor.decorator.ProcessorDecorator;
import ro.isdc.wro.model.resource.processor.factory.ConfigurableProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.DefaultProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.ProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.SimpleProcessorsFactory;
import ro.isdc.wro.model.resource.processor.impl.js.JSMinProcessor;
import ro.isdc.wro.model.resource.processor.impl.js.SemicolonAppenderPreProcessor;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;

/**
 * @author Michael J. Simons
 *
 * @since 2016-02-01
 */
class Wro4jAutoConfigurationTest {

	private final ApplicationContext applicationContext;

	Wro4jAutoConfigurationTest() {
		this.applicationContext = mock(ApplicationContext.class);
		when(this.applicationContext.getBean(Mockito.any(Class.class))).thenThrow(new NoSuchBeanDefinitionException("foo"));
	}

	@Test
	void processorsFactoryShouldWork() {
		final Wro4jProperties wro4jProperties = new Wro4jProperties();

		final Wro4jAutoConfiguration wro4jAutoConfiguration = new Wro4jAutoConfiguration(this.applicationContext,
			Optional.empty());

		ProcessorsFactory processorsFactory;

		final WroManagerFactoryProperties managerFactory = new WroManagerFactoryProperties();
		wro4jProperties.setManagerFactory(managerFactory);
		processorsFactory = wro4jAutoConfiguration.processorsFactory(wro4jProperties);
		Assertions.assertTrue(processorsFactory instanceof ConfigurableProcessorsFactory);
		Assertions.assertTrue(processorsFactory.getPreProcessors().isEmpty());
		Assertions.assertTrue(processorsFactory.getPostProcessors().isEmpty());

		managerFactory.setPreProcessors("semicolonAppender");
		managerFactory.setPostProcessors("jsMin");

		processorsFactory = wro4jAutoConfiguration.processorsFactory(wro4jProperties);
		Assertions.assertTrue(processorsFactory instanceof ConfigurableProcessorsFactory);
		Assertions.assertEquals(1, processorsFactory.getPreProcessors().size());
		Assertions.assertTrue(processorsFactory.getPreProcessors().iterator().next() instanceof SemicolonAppenderPreProcessor);
		Assertions.assertEquals(1, processorsFactory.getPostProcessors().size());
		Assertions.assertTrue(((ProcessorDecorator) processorsFactory.getPostProcessors().iterator().next()).getDecoratedObject() instanceof JSMinProcessor);

		wro4jProperties.setManagerFactory(null);
		processorsFactory = wro4jAutoConfiguration.processorsFactory(wro4jProperties);
		Assertions.assertTrue(processorsFactory instanceof DefaultProcessorsFactory);

		wro4jProperties.setPreProcessors(Arrays.asList(SemicolonAppenderPreProcessor.class));
		wro4jProperties.setPostProcessors(Arrays.asList(JSMinProcessor.class));
		processorsFactory = wro4jAutoConfiguration.processorsFactory(wro4jProperties);
		Assertions.assertTrue(processorsFactory instanceof SimpleProcessorsFactory);
		Assertions.assertEquals(1, processorsFactory.getPreProcessors().size());
		Assertions.assertTrue(processorsFactory.getPreProcessors().iterator().next() instanceof SemicolonAppenderPreProcessor);
		Assertions.assertEquals(1, processorsFactory.getPostProcessors().size());
		Assertions.assertTrue(processorsFactory.getPostProcessors().iterator().next() instanceof JSMinProcessor);
	}

	@Test
	void wroFilterShouldWork() {
		final WroManagerFactory wroManagerFactory = Mockito.mock(WroManagerFactory.class);

		final Wro4jAutoConfiguration wro4jAutoConfiguration = new Wro4jAutoConfiguration(this.applicationContext, Optional.empty());
		final ConfigurableWroFilter wroFilter = wro4jAutoConfiguration.wroFilter(wroManagerFactory, new Wro4jProperties());
		Assertions.assertSame(wroManagerFactory, wroFilter.getWroManagerFactory());
	}

	@Test
	void wroFilterPropertiesShouldWork() {
		final Wro4jProperties wro4jProperties = new Wro4jProperties();

		Properties p;

		p = new Wro4jAutoConfiguration(this.applicationContext, Optional.empty()).wroFilterProperties(wro4jProperties);
		Assertions.assertEquals("true", p.get(ConfigConstants.debug.name()));
		Assertions.assertEquals("true", p.get(ConfigConstants.minimizeEnabled.name()));
		Assertions.assertEquals("true", p.get(ConfigConstants.gzipResources.name()));
		Assertions.assertEquals("0", p.get(ConfigConstants.resourceWatcherUpdatePeriod.name()));
		Assertions.assertEquals("false", p.get(ConfigConstants.resourceWatcherAsync.name()));
		Assertions.assertEquals("0", p.get(ConfigConstants.cacheUpdatePeriod.name()));
		Assertions.assertEquals("0", p.get(ConfigConstants.modelUpdatePeriod.name()));
		Assertions.assertNull(p.get(ConfigConstants.header.name()));
		Assertions.assertEquals("false", p.get(ConfigConstants.parallelPreprocessing.name()));
		Assertions.assertEquals("2000", p.get(ConfigConstants.connectionTimeout.name()));
		Assertions.assertEquals("UTF-8", p.get(ConfigConstants.encoding.name()));
		Assertions.assertEquals("true", p.get(ConfigConstants.ignoreMissingResources.name()));
		Assertions.assertEquals("true", p.get(ConfigConstants.ignoreEmptyGroup.name()));
		Assertions.assertEquals("false", p.get(ConfigConstants.ignoreFailingProcessor.name()));
		Assertions.assertEquals("true", p.get(ConfigConstants.cacheGzippedContent.name()));
		Assertions.assertEquals("false", p.get(ConfigConstants.jmxEnabled.name()));
		Assertions.assertNull(p.get(ConfigConstants.mbeanName.name()));

		wro4jProperties.setResourceWatcherUpdatePeriod(null);
		wro4jProperties.setCacheUpdatePeriod(null);
		wro4jProperties.setModelUpdatePeriod(null);
		wro4jProperties.setHeader("   ");
		wro4jProperties.setConnectionTimeout(null);
		wro4jProperties.setEncoding("\t ");
		wro4jProperties.setMbeanName(" ");
		p = new Wro4jAutoConfiguration(this.applicationContext, Optional.empty()).wroFilterProperties(wro4jProperties);
		Assertions.assertNull(p.get(ConfigConstants.resourceWatcherUpdatePeriod.name()));
		Assertions.assertNull(p.get(ConfigConstants.cacheUpdatePeriod.name()));
		Assertions.assertNull(p.get(ConfigConstants.modelUpdatePeriod.name()));
		Assertions.assertNull(p.get(ConfigConstants.header.name()));
		Assertions.assertNull(p.get(ConfigConstants.connectionTimeout.name()));
		Assertions.assertNull(p.get(ConfigConstants.encoding.name()));
		Assertions.assertNull(p.get(ConfigConstants.mbeanName.name()));

		wro4jProperties.setHeader("If-Unmodified-Since: Sat, 29 Oct 1994 19:43:31 GMT");
		wro4jProperties.setEncoding("ISO-8859-1");
		wro4jProperties.setMbeanName("wro4j-bean");
		p = new Wro4jAutoConfiguration(this.applicationContext, Optional.empty()).wroFilterProperties(wro4jProperties);
		Assertions.assertEquals(wro4jProperties.getHeader(), p.get(ConfigConstants.header.name()));
		Assertions.assertEquals(wro4jProperties.getEncoding(), p.get(ConfigConstants.encoding.name()));
		Assertions.assertEquals(wro4jProperties.getMbeanName(), p.get(ConfigConstants.mbeanName.name()));

		wro4jProperties.setEncoding(null);
		p = new Wro4jAutoConfiguration(this.applicationContext, Optional.empty()).wroFilterProperties(wro4jProperties);
		Assertions.assertNull(p.get(ConfigConstants.encoding.name()));
	}

	@Test
	void wro4jFilterRegistrationShouldWork() {
		final ConfigurableWroFilter wroFilter = Mockito.mock(ConfigurableWroFilter.class);

		final FilterRegistrationBean filterRegistrationBean = new Wro4jAutoConfiguration(this.applicationContext, Optional.empty()).wro4jFilterRegistration(wroFilter, new Wro4jProperties());
		final Collection<String> urlPatterns = filterRegistrationBean.getUrlPatterns();
		Assertions.assertEquals(1, urlPatterns.size());
		Assertions.assertEquals("/wro4j/*", urlPatterns.iterator().next());
	}
}
