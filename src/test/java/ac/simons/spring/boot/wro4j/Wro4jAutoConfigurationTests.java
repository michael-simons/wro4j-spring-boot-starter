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

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

import ac.simons.spring.boot.wro4j.Wro4jProperties.WroManagerFactoryProperties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ro.isdc.wro.config.support.ConfigConstants;
import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.model.resource.processor.decorator.ProcessorDecorator;
import ro.isdc.wro.model.resource.processor.factory.ConfigurableProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.DefaultProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.ProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.SimpleProcessorsFactory;
import ro.isdc.wro.model.resource.processor.impl.js.JSMinProcessor;
import ro.isdc.wro.model.resource.processor.impl.js.SemicolonAppenderPreProcessor;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author Michael J. Simons
 * @since 2016-02-01
 */
class Wro4jAutoConfigurationTests {

	private final ApplicationContext applicationContext;

	Wro4jAutoConfigurationTests() {
		this.applicationContext = mock(ApplicationContext.class);
		Class<?> any = Mockito.any(Class.class);
		given(this.applicationContext.getBean(any)).willThrow(new NoSuchBeanDefinitionException("foo"));
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
		assertThat(processorsFactory instanceof ConfigurableProcessorsFactory).isTrue();
		assertThat(processorsFactory.getPreProcessors().isEmpty()).isTrue();
		assertThat(processorsFactory.getPostProcessors().isEmpty()).isTrue();

		managerFactory.setPreProcessors("semicolonAppender");
		managerFactory.setPostProcessors("jsMin");

		processorsFactory = wro4jAutoConfiguration.processorsFactory(wro4jProperties);
		assertThat(processorsFactory instanceof ConfigurableProcessorsFactory).isTrue();
		assertThat(processorsFactory.getPreProcessors().size()).isEqualTo(1);
		assertThat(processorsFactory.getPreProcessors().iterator().next() instanceof SemicolonAppenderPreProcessor)
			.isTrue();
		assertThat(processorsFactory.getPostProcessors().size()).isEqualTo(1);
		assertThat(((ProcessorDecorator) processorsFactory.getPostProcessors().iterator().next())
			.getDecoratedObject() instanceof JSMinProcessor).isTrue();

		wro4jProperties.setManagerFactory(null);
		processorsFactory = wro4jAutoConfiguration.processorsFactory(wro4jProperties);
		assertThat(processorsFactory instanceof DefaultProcessorsFactory).isTrue();

		wro4jProperties.setPreProcessors(Arrays.asList(SemicolonAppenderPreProcessor.class));
		wro4jProperties.setPostProcessors(Arrays.asList(JSMinProcessor.class));
		processorsFactory = wro4jAutoConfiguration.processorsFactory(wro4jProperties);
		assertThat(processorsFactory instanceof SimpleProcessorsFactory).isTrue();
		assertThat(processorsFactory.getPreProcessors().size()).isEqualTo(1);
		assertThat(processorsFactory.getPreProcessors().iterator().next() instanceof SemicolonAppenderPreProcessor)
			.isTrue();
		assertThat(processorsFactory.getPostProcessors().size()).isEqualTo(1);
		assertThat(processorsFactory.getPostProcessors().iterator().next() instanceof JSMinProcessor).isTrue();
	}

	@Test
	void wroFilterShouldWork() {
		final WroManagerFactory wroManagerFactory = Mockito.mock(WroManagerFactory.class);

		final Wro4jAutoConfiguration wro4jAutoConfiguration = new Wro4jAutoConfiguration(this.applicationContext,
				Optional.empty());
		final ConfigurableWroFilter wroFilter = wro4jAutoConfiguration.wroFilter(wroManagerFactory,
				new Wro4jProperties());
		assertThat(wroFilter.getWroManagerFactory()).isSameAs(wroManagerFactory);
	}

	@Test
	@SuppressWarnings({ "squid:S5961" })
	void wroFilterPropertiesShouldWork() {
		final Wro4jProperties wro4jProperties = new Wro4jProperties();

		Properties p;

		p = new Wro4jAutoConfiguration(this.applicationContext, Optional.empty()).wroFilterProperties(wro4jProperties);
		assertThat(p.get(ConfigConstants.debug.name())).isEqualTo("true");
		assertThat(p.get(ConfigConstants.minimizeEnabled.name())).isEqualTo("true");
		assertThat(p.get(ConfigConstants.gzipResources.name())).isEqualTo("true");
		assertThat(p.get(ConfigConstants.resourceWatcherUpdatePeriod.name())).isEqualTo("0");
		assertThat(p.get(ConfigConstants.resourceWatcherAsync.name())).isEqualTo("false");
		assertThat(p.get(ConfigConstants.cacheUpdatePeriod.name())).isEqualTo("0");
		assertThat(p.get(ConfigConstants.modelUpdatePeriod.name())).isEqualTo("0");
		assertThat(p.get(ConfigConstants.header.name())).isNull();
		assertThat(p.get(ConfigConstants.parallelPreprocessing.name())).isEqualTo("false");
		assertThat(p.get(ConfigConstants.connectionTimeout.name())).isEqualTo("2000");
		assertThat(p.get(ConfigConstants.encoding.name())).isEqualTo("UTF-8");
		assertThat(p.get(ConfigConstants.ignoreMissingResources.name())).isEqualTo("true");
		assertThat(p.get(ConfigConstants.ignoreEmptyGroup.name())).isEqualTo("true");
		assertThat(p.get(ConfigConstants.ignoreFailingProcessor.name())).isEqualTo("false");
		assertThat(p.get(ConfigConstants.cacheGzippedContent.name())).isEqualTo("true");
		assertThat(p.get(ConfigConstants.jmxEnabled.name())).isEqualTo("false");
		assertThat(p.get(ConfigConstants.mbeanName.name())).isNull();

		wro4jProperties.setResourceWatcherUpdatePeriod(null);
		wro4jProperties.setCacheUpdatePeriod(null);
		wro4jProperties.setModelUpdatePeriod(null);
		wro4jProperties.setHeader("   ");
		wro4jProperties.setConnectionTimeout(null);
		wro4jProperties.setEncoding("\t ");
		wro4jProperties.setMbeanName(" ");
		p = new Wro4jAutoConfiguration(this.applicationContext, Optional.empty()).wroFilterProperties(wro4jProperties);
		assertThat(p.get(ConfigConstants.resourceWatcherUpdatePeriod.name())).isNull();
		assertThat(p.get(ConfigConstants.cacheUpdatePeriod.name())).isNull();
		assertThat(p.get(ConfigConstants.modelUpdatePeriod.name())).isNull();
		assertThat(p.get(ConfigConstants.header.name())).isNull();
		assertThat(p.get(ConfigConstants.connectionTimeout.name())).isNull();
		assertThat(p.get(ConfigConstants.encoding.name())).isNull();
		assertThat(p.get(ConfigConstants.mbeanName.name())).isNull();

		wro4jProperties.setHeader("If-Unmodified-Since: Sat, 29 Oct 1994 19:43:31 GMT");
		wro4jProperties.setEncoding("ISO-8859-1");
		wro4jProperties.setMbeanName("wro4j-bean");
		p = new Wro4jAutoConfiguration(this.applicationContext, Optional.empty()).wroFilterProperties(wro4jProperties);
		assertThat(p.get(ConfigConstants.header.name())).isEqualTo(wro4jProperties.getHeader());
		assertThat(p.get(ConfigConstants.encoding.name())).isEqualTo(wro4jProperties.getEncoding());
		assertThat(p.get(ConfigConstants.mbeanName.name())).isEqualTo(wro4jProperties.getMbeanName());

		wro4jProperties.setEncoding(null);
		p = new Wro4jAutoConfiguration(this.applicationContext, Optional.empty()).wroFilterProperties(wro4jProperties);
		assertThat(p.get(ConfigConstants.encoding.name())).isNull();
	}

	@Test
	void wro4jFilterRegistrationShouldWork() {
		final ConfigurableWroFilter wroFilter = Mockito.mock(ConfigurableWroFilter.class);

		final FilterRegistrationBean<?> filterRegistrationBean = new Wro4jAutoConfiguration(this.applicationContext,
				Optional.empty())
			.wro4jFilterRegistration(wroFilter, new Wro4jProperties());
		final Collection<String> urlPatterns = filterRegistrationBean.getUrlPatterns();
		assertThat(urlPatterns.size()).isEqualTo(1);
		assertThat(urlPatterns.iterator().next()).isEqualTo("/wro4j/*");
	}

}
