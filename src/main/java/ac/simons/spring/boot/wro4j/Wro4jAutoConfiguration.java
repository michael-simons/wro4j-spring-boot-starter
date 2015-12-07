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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import ro.isdc.wro.config.jmx.ConfigConstants;
import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.http.WroFilter;
import ro.isdc.wro.manager.factory.BaseWroManagerFactory;
import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.factory.XmlModelFactory;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;
import ro.isdc.wro.model.resource.processor.factory.DefaultProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.ProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.SimpleProcessorsFactory;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures a Wro4j filter.
 *
 * @author Michael J. Simons, 2015-07-11
 */
@Configuration
@ConditionalOnClass(WroFilter.class)
@ConditionalOnMissingBean(WroFilter.class)
@EnableConfigurationProperties(Wro4jProperties.class)
public class Wro4jAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean({WroManagerFactory.class, WroModelFactory.class})
	WroModelFactory wroModelFactory(final Wro4jProperties wro4jProperties) {
		return new XmlModelFactory() {
			@Override
			protected InputStream getModelResourceAsStream() {
				return this.getClass().getResourceAsStream(wro4jProperties.getModel());
			}
		};
	}

	/**
	 * Instantiates a {@link ProcessorsFactory} if there is no such factory
	 * available. If the user decided to provide Pre- or PostProcessors through
	 * {@link Wro4jProperties#preProcessors} or
	 * {@link Wro4jProperties#postProcessors} those are used. If either type of
	 * processor is configured, a {@link DefaultProcessorsFactory} will be
	 * returned, using the default set of processors Wro4j provides.
	 *
	 * @param wro4jProperties Configurational properties used
	 * @return A DefaultProcessorsFactory or a ProcessorsFactory configured to
	 * used the specified processors
	 */
	@Bean
	@ConditionalOnMissingBean({WroManagerFactory.class, ProcessorsFactory.class})
	ProcessorsFactory defaultProcessorsFactory(final Wro4jProperties wro4jProperties) {
		final List<ResourcePreProcessor> preProcessors = Optional.ofNullable(wro4jProperties.getPreProcessors()).orElseGet(ArrayList::new).stream()
				.map(c -> (ResourcePreProcessor) new BeanWrapperImpl(c).getWrappedInstance())
				.collect(Collectors.toList());
		final List<ResourcePostProcessor> postProcessors = Optional.ofNullable(wro4jProperties.getPostProcessors()).orElseGet(ArrayList::new).stream()
				.map(c -> (ResourcePostProcessor) new BeanWrapperImpl(c).getWrappedInstance())
				.collect(Collectors.toList());

		ProcessorsFactory rv;
		if (preProcessors.isEmpty() && postProcessors.isEmpty()) {
			rv = new DefaultProcessorsFactory();
		}
		else {
			rv = new SimpleProcessorsFactory();
			((SimpleProcessorsFactory) rv).setResourcePreProcessors(preProcessors);
			((SimpleProcessorsFactory) rv).setResourcePostProcessors(postProcessors);
		}

		return rv;
	}

	/**
	 * Builds the {@link WroManagerFactory} used for the Wro4j filter to be
	 * created if no WroManagerFactory is already registered.
	 *
	 * @param wroModelFactory THe model factory to use for the manager factory
	 * @param processorsFactory The processors factory to use for the manager factory
	 * @return A new WroManagerFactory
	 */
	@Bean
	@ConditionalOnMissingBean(WroManagerFactory.class)
	WroManagerFactory wroManagerFactory(final WroModelFactory wroModelFactory, final ProcessorsFactory processorsFactory) {
		return new BaseWroManagerFactory() {
			@Override
			protected WroModelFactory newModelFactory() {
				return wroModelFactory;
			}

			@Override
			protected ProcessorsFactory newProcessorsFactory() {
				return processorsFactory;
			}
		};
	}

	@Bean
	ConfigurableWroFilter wroFilter(WroManagerFactory wroManagerFactory, Wro4jProperties wro4jProperties) {
		ConfigurableWroFilter wroFilter = new ConfigurableWroFilter();
		wroFilter.setProperties(wroFilterProperties(wro4jProperties));
		wroFilter.setWroManagerFactory(wroManagerFactory);
		return wroFilter;
	}

	Properties wroFilterProperties(Wro4jProperties wro4jProperties) {
		Properties properties = new Properties();

		properties.setProperty(ConfigConstants.debug.name(), String.valueOf(wro4jProperties.isDebug()));
		properties.setProperty(ConfigConstants.gzipResources.name(), String.valueOf(wro4jProperties.isGzipResources()));
		if (wro4jProperties.getCacheUpdatePeriod() != null) {
			properties.setProperty(ConfigConstants.cacheUpdatePeriod.name(), String.valueOf(wro4jProperties.getCacheUpdatePeriod()));
		}
		if (wro4jProperties.getModelUpdatePeriod() != null) {
			properties.setProperty(ConfigConstants.modelUpdatePeriod.name(), String.valueOf(wro4jProperties.getModelUpdatePeriod()));
		}
		properties.setProperty(ConfigConstants.cacheGzippedContent.name(), String.valueOf(wro4jProperties.isCacheGzippedContent()));
		properties.setProperty(ConfigConstants.jmxEnabled.name(), String.valueOf(wro4jProperties.isJmxEnabled()));
		if (wro4jProperties.getMbeanName() != null) {
			properties.setProperty(ConfigConstants.mbeanName.name(), wro4jProperties.getMbeanName());
		}

		return properties;
	}

	@Bean
	FilterRegistrationBean wro4jFilterRegistration(ConfigurableWroFilter wroFilter, Wro4jProperties wro4jProperties) {
		final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(wroFilter);
		filterRegistrationBean.addUrlPatterns(wro4jProperties.getFilterUrlPattern());
		return filterRegistrationBean;
	}
}
