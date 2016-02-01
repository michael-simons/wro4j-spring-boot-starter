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

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import ac.simons.spring.boot.wro4j.Wro4jProperties.WroManagerFactoryProperties;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ro.isdc.wro.config.jmx.ConfigConstants;
import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;
import ro.isdc.wro.model.resource.processor.decorator.ProcessorDecorator;
import ro.isdc.wro.model.resource.processor.factory.ConfigurableProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.DefaultProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.ProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.SimpleProcessorsFactory;
import ro.isdc.wro.model.resource.processor.impl.js.JSMinProcessor;
import ro.isdc.wro.model.resource.processor.impl.js.SemicolonAppenderPreProcessor;

import org.springframework.boot.context.embedded.FilterRegistrationBean;

/**
 * @author Michael J. Simons, 2016-02-01
 */
public class Wro4jAutoConfigurationTest {

	@Test
	public void processorsFactoryShouldWork() {
		final Wro4jProperties wro4jProperties = new Wro4jProperties();

		final Wro4jAutoConfiguration wro4jAutoConfiguration = new Wro4jAutoConfiguration();

		ProcessorsFactory processorsFactory;

		final WroManagerFactoryProperties managerFactory = new WroManagerFactoryProperties();
		wro4jProperties.setManagerFactory(managerFactory);
		processorsFactory = wro4jAutoConfiguration.processorsFactory(wro4jProperties);
		Assert.assertTrue(processorsFactory instanceof ConfigurableProcessorsFactory);
		Assert.assertTrue(processorsFactory.getPreProcessors().isEmpty());
		Assert.assertTrue(processorsFactory.getPostProcessors().isEmpty());

		managerFactory.setPreProcessors("semicolonAppender");
		managerFactory.setPostProcessors("jsMin");

		processorsFactory = wro4jAutoConfiguration.processorsFactory(wro4jProperties);
		Assert.assertTrue(processorsFactory instanceof ConfigurableProcessorsFactory);
		Assert.assertEquals(1, processorsFactory.getPreProcessors().size());
		Assert.assertTrue(processorsFactory.getPreProcessors().iterator().next() instanceof SemicolonAppenderPreProcessor);
		Assert.assertEquals(1, processorsFactory.getPostProcessors().size());
		Assert.assertTrue(((ProcessorDecorator) processorsFactory.getPostProcessors().iterator().next()).getDecoratedObject() instanceof JSMinProcessor);

		wro4jProperties.setManagerFactory(null);
		processorsFactory = wro4jAutoConfiguration.processorsFactory(wro4jProperties);
		Assert.assertTrue(processorsFactory instanceof DefaultProcessorsFactory);

		wro4jProperties.setPreProcessors(Arrays.<Class<? extends ResourcePreProcessor>>asList(SemicolonAppenderPreProcessor.class));
		wro4jProperties.setPostProcessors(Arrays.<Class<? extends ResourcePostProcessor>>asList(JSMinProcessor.class));
		processorsFactory = wro4jAutoConfiguration.processorsFactory(wro4jProperties);
		Assert.assertTrue(processorsFactory instanceof SimpleProcessorsFactory);
		Assert.assertEquals(1, processorsFactory.getPreProcessors().size());
		Assert.assertTrue(processorsFactory.getPreProcessors().iterator().next() instanceof SemicolonAppenderPreProcessor);
		Assert.assertEquals(1, processorsFactory.getPostProcessors().size());
		Assert.assertTrue(processorsFactory.getPostProcessors().iterator().next() instanceof JSMinProcessor);
	}

	@Test
	public void wroFilterShouldWork() {
		final WroManagerFactory wroManagerFactory = Mockito.mock(WroManagerFactory.class);

		final Wro4jAutoConfiguration wro4jAutoConfiguration = new Wro4jAutoConfiguration();
		final ConfigurableWroFilter wroFilter = wro4jAutoConfiguration.wroFilter(wroManagerFactory, new Wro4jProperties());
		Assert.assertSame(wroManagerFactory, wroFilter.getWroManagerFactory());
	}

	@Test
	public void wroFilterPropertiesShouldWork() {
		final Wro4jProperties wro4jProperties = new Wro4jProperties();

		Properties p;

		p = new Wro4jAutoConfiguration().wroFilterProperties(wro4jProperties);
		Assert.assertEquals("true", p.get(ConfigConstants.debug.name()));
		Assert.assertEquals("true", p.get(ConfigConstants.minimizeEnabled.name()));
		Assert.assertEquals("true", p.get(ConfigConstants.gzipResources.name()));
		Assert.assertEquals("0", p.get(ConfigConstants.resourceWatcherUpdatePeriod.name()));
		Assert.assertEquals("false", p.get(ConfigConstants.resourceWatcherAsync.name()));
		Assert.assertEquals("0", p.get(ConfigConstants.cacheUpdatePeriod.name()));
		Assert.assertEquals("0", p.get(ConfigConstants.modelUpdatePeriod.name()));
		Assert.assertNull(p.get(ConfigConstants.header.name()));
		Assert.assertEquals("false", p.get(ConfigConstants.parallelPreprocessing.name()));
		Assert.assertEquals("2000", p.get(ConfigConstants.connectionTimeout.name()));
		Assert.assertEquals("UTF-8", p.get(ConfigConstants.encoding.name()));
		Assert.assertEquals("true", p.get(ConfigConstants.ignoreMissingResources.name()));
		Assert.assertEquals("true", p.get(ConfigConstants.ignoreEmptyGroup.name()));
		Assert.assertEquals("false", p.get(ConfigConstants.ignoreFailingProcessor.name()));
		Assert.assertEquals("true", p.get(ConfigConstants.cacheGzippedContent.name()));
		Assert.assertEquals("false", p.get(ConfigConstants.jmxEnabled.name()));
		Assert.assertNull(p.get(ConfigConstants.mbeanName.name()));

		wro4jProperties.setResourceWatcherUpdatePeriod(null);
		wro4jProperties.setCacheUpdatePeriod(null);
		wro4jProperties.setModelUpdatePeriod(null);
		wro4jProperties.setHeader("   ");
		wro4jProperties.setConnectionTimeout(null);
		wro4jProperties.setEncoding("\t ");
		wro4jProperties.setMbeanName(" ");
		p = new Wro4jAutoConfiguration().wroFilterProperties(wro4jProperties);
		Assert.assertNull(p.get(ConfigConstants.resourceWatcherUpdatePeriod.name()));
		Assert.assertNull(p.get(ConfigConstants.cacheUpdatePeriod.name()));
		Assert.assertNull(p.get(ConfigConstants.modelUpdatePeriod.name()));
		Assert.assertNull(p.get(ConfigConstants.header.name()));
		Assert.assertNull(p.get(ConfigConstants.connectionTimeout.name()));
		Assert.assertNull(p.get(ConfigConstants.encoding.name()));
		Assert.assertNull(p.get(ConfigConstants.mbeanName.name()));

		wro4jProperties.setHeader("If-Unmodified-Since: Sat, 29 Oct 1994 19:43:31 GMT");
		wro4jProperties.setEncoding("ISO-8859-1");
		wro4jProperties.setMbeanName("wro4j-bean");
		p = new Wro4jAutoConfiguration().wroFilterProperties(wro4jProperties);
		Assert.assertEquals(wro4jProperties.getHeader(), p.get(ConfigConstants.header.name()));
		Assert.assertEquals(wro4jProperties.getEncoding(), p.get(ConfigConstants.encoding.name()));
		Assert.assertEquals(wro4jProperties.getMbeanName(), p.get(ConfigConstants.mbeanName.name()));

		wro4jProperties.setEncoding(null);
		p = new Wro4jAutoConfiguration().wroFilterProperties(wro4jProperties);
		Assert.assertNull(p.get(ConfigConstants.encoding.name()));
	}

	@Test
	public void wro4jFilterRegistrationShouldWork() {
		final ConfigurableWroFilter wroFilter = Mockito.mock(ConfigurableWroFilter.class);

		final FilterRegistrationBean filterRegistrationBean = new Wro4jAutoConfiguration().wro4jFilterRegistration(wroFilter, new Wro4jProperties());
		final Collection<String> urlPatterns = filterRegistrationBean.getUrlPatterns();
		Assert.assertEquals(1, urlPatterns.size());
		Assert.assertEquals("/wro4j/*", urlPatterns.iterator().next());
	}
}
