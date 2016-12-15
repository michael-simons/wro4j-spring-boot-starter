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
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.isdc.wro.cache.CacheStrategy;
import ro.isdc.wro.cache.impl.LruMemoryCacheStrategy;
import ro.isdc.wro.config.jmx.ConfigConstants;
import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.http.WroFilter;
import ro.isdc.wro.manager.factory.BaseWroManagerFactory;
import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;
import ro.isdc.wro.model.resource.processor.factory.ConfigurableProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.DefaultProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.ProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.SimpleProcessorsFactory;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Configures a Wro4j filter.
 *
 * @author Michael J. Simons, 2015-07-11
 */
@Configuration
@ConditionalOnClass(WroFilter.class)
@ConditionalOnMissingBean(WroFilter.class)
@EnableConfigurationProperties(Wro4jProperties.class)
@AutoConfigureAfter(CacheAutoConfiguration.class)
public class Wro4jAutoConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(Wro4jAutoConfiguration.class.getName());
	
	/**
	 * We use this to access possible processor beans inside the appplication context.
	 */
	private final ApplicationContext applicationContext;

	public Wro4jAutoConfiguration(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Bean
	@ConditionalOnMissingBean({WroManagerFactory.class, WroModelFactory.class})
	WroModelFactory wroModelFactory(final Wro4jProperties wro4jProperties) {
		return new ConfigurableXmlModelFactory(wro4jProperties.getModel());
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
	ProcessorsFactory processorsFactory(final Wro4jProperties wro4jProperties) {
		final List<ResourcePreProcessor> preProcessors = new ArrayList<ResourcePreProcessor>();
		if (wro4jProperties.getPreProcessors() != null) {
			for (Class<? extends ResourcePreProcessor> c : wro4jProperties.getPreProcessors()) {
				preProcessors.add(getBeanOrInstantiateProcessor(c));
			}
		}
		final List<ResourcePostProcessor> postProcessors = new ArrayList<ResourcePostProcessor>();
		if (wro4jProperties.getPostProcessors() != null) {
			for (Class<? extends ResourcePostProcessor> c : wro4jProperties.getPostProcessors()) {
				postProcessors.add(getBeanOrInstantiateProcessor(c));
			}
		}

		ProcessorsFactory rv;
		
		if (wro4jProperties.getManagerFactory() != null) {
			final Properties properties = new Properties();
			if (wro4jProperties.getManagerFactory().getPreProcessors() != null) {
				properties.setProperty("preProcessors", wro4jProperties.getManagerFactory().getPreProcessors());
			}
			if (wro4jProperties.getManagerFactory().getPostProcessors() != null) {
				properties.setProperty("postProcessors", wro4jProperties.getManagerFactory().getPostProcessors());
			}
			rv = new ConfigurableProcessorsFactory();
			((ConfigurableProcessorsFactory) rv).setProperties(properties);
		}
		else if (preProcessors.isEmpty() && postProcessors.isEmpty()) {
			rv = new DefaultProcessorsFactory();
		}
		else {
			rv = new SimpleProcessorsFactory();
			((SimpleProcessorsFactory) rv).setResourcePreProcessors(preProcessors);
			((SimpleProcessorsFactory) rv).setResourcePostProcessors(postProcessors);
		}
		LOGGER.debug("Using ProcessorsFactory of type '{}'", rv.getClass().getName());

		return rv;
	}
	
	/**
	 * This method tries to load a processor from the application context by class name.
	 * 
	 * If it fails, the processor is instantiated manually bot not added to the context.
	 * 
	 * @param <T> Type of the processor to load
	 * @param c Class of the processor to load
	 * @return A processor instance
	 */
	<T> T getBeanOrInstantiateProcessor(final Class<? extends T> c) {
		T rv;
		try {
			rv = this.applicationContext.getBean(c);
		} catch (NoSuchBeanDefinitionException e) {
			LOGGER.warn("Could not get processor from context: {}, instantiating new instance instead", e.getMessage());
			rv = (T) new BeanWrapperImpl(c).getWrappedInstance();
		}
		return rv;
	}

	/**
	 * This cache strategy will be configured if there's not already a cache
	 * strategy, a {@link CacheManager} is present and the name of the cache to
	 * use is configured.
	 *
	 * @param <K> Type of the cache keys
	 * @param <V> Type of the cache values
	 * @param cacheManager The cache manager to use
	 * @param wro4jProperties The properties (needed for the cache name)
	 * @return The Spring backed cache strategy
	 */
	@Bean
	@ConditionalOnBean(CacheManager.class)
	@ConditionalOnProperty("wro4j.cacheName")
	@ConditionalOnMissingBean(CacheStrategy.class)
	@Order(-100)
	<K, V> CacheStrategy<K, V> springCacheStrategy(CacheManager cacheManager, Wro4jProperties wro4jProperties) {
		LOGGER.debug("Creating cache strategy 'SpringCacheStrategy'");
		return new SpringCacheStrategy<K, V>(cacheManager, wro4jProperties.getCacheName());
	}

	/**
	 * This is the default "Least recently used memory cache" strategy of Wro4j
	 * which will be configured per default.
	 *
	 * @param <K> Type of the cache keys
	 * @param <V> Type of the cache values
	 * @return A default Wro4j cache strategy
	 */
	@Bean
	@ConditionalOnMissingBean(CacheStrategy.class)
	@Order(-90)
	<K, V> CacheStrategy<K, V> defaultCacheStrategy() {
		LOGGER.debug("Creating cache strategy 'LruMemoryCacheStrategy'");
		return new LruMemoryCacheStrategy<K, V>();
	}

	/**
	 * Builds the {@link WroManagerFactory} used for the Wro4j filter to be
	 * created if no WroManagerFactory is already registered.
	 *
	 * @param wroModelFactory THe model factory to use for the manager factory
	 * @param processorsFactory The processors factory to use for the manager
	 * @param cacheStrategy The cache strategy to use
	 *
	 * @return A new WroManagerFactory
	 */
	@Bean
	@ConditionalOnMissingBean(WroManagerFactory.class)
	WroManagerFactory wroManagerFactory(final WroModelFactory wroModelFactory, final ProcessorsFactory processorsFactory, final CacheStrategy cacheStrategy) {
		return new BaseWroManagerFactory()
				.setModelFactory(wroModelFactory)
				.setProcessorsFactory(processorsFactory)
				.setCacheStrategy(cacheStrategy);
	}

	/**
	 * The final step in configuring the Wro4j filter based on the existing or
	 * previously configured {@code WroManagerFactory} and the additional
	 * properties.
	 *
	 * @param wroManagerFactory An existing or the newly configured manager
	 * @param wro4jProperties The properties used to setup this starter
	 *
	 * @return A servlet filter which later is registered through Spring means
	 */
	@Bean
	ConfigurableWroFilter wroFilter(WroManagerFactory wroManagerFactory, Wro4jProperties wro4jProperties) {
		ConfigurableWroFilter wroFilter = new ConfigurableWroFilter();
		wroFilter.setProperties(wroFilterProperties(wro4jProperties));
		wroFilter.setWroManagerFactory(wroManagerFactory);
		return wroFilter;
	}

	@SuppressWarnings({"squid:MethodCyclomaticComplexity"})
	Properties wroFilterProperties(Wro4jProperties wro4jProperties) {
		final Properties properties = new Properties();

		properties.setProperty(ConfigConstants.debug.name(), String.valueOf(wro4jProperties.isDebug()));
		properties.setProperty(ConfigConstants.minimizeEnabled.name(), String.valueOf(wro4jProperties.isMinimizeEnabled()));
		properties.setProperty(ConfigConstants.gzipResources.name(), String.valueOf(wro4jProperties.isGzipResources()));
		if (wro4jProperties.getResourceWatcherUpdatePeriod() != null) {
			properties.setProperty(ConfigConstants.resourceWatcherUpdatePeriod.name(), String.valueOf(wro4jProperties.getResourceWatcherUpdatePeriod()));
		}
		properties.setProperty(ConfigConstants.resourceWatcherAsync.name(), String.valueOf(wro4jProperties.isResourceWatcherAsync()));
		if (wro4jProperties.getCacheUpdatePeriod() != null) {
			properties.setProperty(ConfigConstants.cacheUpdatePeriod.name(), String.valueOf(wro4jProperties.getCacheUpdatePeriod()));
		}
		if (wro4jProperties.getModelUpdatePeriod() != null) {
			properties.setProperty(ConfigConstants.modelUpdatePeriod.name(), String.valueOf(wro4jProperties.getModelUpdatePeriod()));
		}
		if (!(wro4jProperties.getHeader() == null || wro4jProperties.getHeader().trim().isEmpty())) {
			properties.setProperty(ConfigConstants.header.name(), wro4jProperties.getHeader());
		}
		properties.setProperty(ConfigConstants.parallelPreprocessing.name(), String.valueOf(wro4jProperties.isParallelPreprocessing()));
		if (wro4jProperties.getConnectionTimeout() != null) {
			properties.setProperty(ConfigConstants.connectionTimeout.name(), String.valueOf(wro4jProperties.getConnectionTimeout()));
		}
		if (!(wro4jProperties.getEncoding() == null || wro4jProperties.getEncoding().trim().isEmpty())) {
			properties.setProperty(ConfigConstants.encoding.name(), wro4jProperties.getEncoding());
		}
		properties.setProperty(ConfigConstants.ignoreMissingResources.name(), String.valueOf(wro4jProperties.isIgnoreMissingResources()));
		properties.setProperty(ConfigConstants.ignoreEmptyGroup.name(), String.valueOf(wro4jProperties.isIgnoreEmptyGroup()));
		properties.setProperty(ConfigConstants.ignoreFailingProcessor.name(), String.valueOf(wro4jProperties.isIgnoreFailingProcessor()));
		properties.setProperty(ConfigConstants.cacheGzippedContent.name(), String.valueOf(wro4jProperties.isCacheGzippedContent()));
		properties.setProperty(ConfigConstants.jmxEnabled.name(), String.valueOf(wro4jProperties.isJmxEnabled()));
		if (!(wro4jProperties.getMbeanName() == null || wro4jProperties.getMbeanName().trim().isEmpty())) {
			properties.setProperty(ConfigConstants.mbeanName.name(), wro4jProperties.getMbeanName());
		}

		return properties;
	}

	/**
	 * Registeres the {@code wroFilter} through a Spring
	 * {@link FilterRegistrationBean}.
	 *
	 * @param wroFilter The configured {@code wroFilter}
	 * @param wro4jProperties Needed for the url pattern to which the filter
	 * should be registered
	 *
	 * @return The Spring {@code FilterRegistrationBean}
	 */
	@Bean
	FilterRegistrationBean wro4jFilterRegistration(ConfigurableWroFilter wroFilter, Wro4jProperties wro4jProperties) {
		final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(wroFilter);
		filterRegistrationBean.addUrlPatterns(wro4jProperties.getFilterUrl() + "/*");
		return filterRegistrationBean;
	}
}
