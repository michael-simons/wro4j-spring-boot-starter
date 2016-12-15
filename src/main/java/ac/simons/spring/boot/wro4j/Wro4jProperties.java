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

import java.util.List;

import ro.isdc.wro.cache.CacheStrategy;
import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Represents the
 * <a href="http://wro4j.readthedocs.org/en/stable/ConfigurationOptions/">configuration
 * options</a> of Wro4j.
 *
 * @author Michael J. Simons, 2015-07-11
 */
@ConfigurationProperties("wro4j")
public class Wro4jProperties {

	/**
	 * boolean flag (former known as configuration), with possible values: true
	 * (DEVELOPMENT) or false (PRODUCTION). Find out more about differences at
	 * the bottom of this page.
	 */
	private boolean debug = true;

	/**
	 * Flag for turning minimization on/off.
	 */
	private boolean minimizeEnabled = true;

	/**
	 * When this flag is enabled response will be gziped. Defaults to true.
	 */
	private boolean gzipResources = true;

	/**
	 * integer value for specifying how often (in seconds) the resource changes
	 * should be checked. When this value is 0, the cache is never refreshed.
	 * When a resource change is detected, the cached group containing changed
	 * resource will be invalidated. This is useful during development, when
	 * resources are changed often.
	 */
	private Integer resourceWatcherUpdatePeriod = 0;

	/**
	 * A boolean which enables/disables asynchronous resource watcher. The true
	 * value does make sense when resourceWatcherUpdatePeriod is greater than 0.
	 */
	private boolean resourceWatcherAsync = false;

	/**
	 * integer value for specifying how often (in seconds) the cache should be
	 * refreshed. When this value is 0, the cache is never refreshed. Defaults
	 * to 0.
	 */
	private Integer cacheUpdatePeriod = 0;

	/**
	 * Integer value for specifying how often (in seconds) the model (wro.xml)
	 * should be refreshed. When this value is 0, the model is never refreshed.
	 * Defaults to 0.
	 */
	private Integer modelUpdatePeriod = 0;

	/**
	 * allow explicit configuration of headers (for controlling expiration date,
	 * etc). The implementation was inspired from
	 * [http://juliusdev.blogspot.com/2008/06/tomcat-add-expires-header.html
	 * here]. The headers can be defined using this format: ```:
	 */
	private String header;

	/**
	 * A flag for enabling parallel execution of pre processors which may
	 * improve overall performance, especially when there are slow
	 * preProcessors.
	 */
	private boolean parallelPreprocessing = false;

	/**
	 * Timeout (milliseconds) of the url connection for external resources. This
	 * is used to ensure that locator doesn't spend too much time on slow
	 * end-point.
	 */
	private Long connectionTimeout = 2000L;

	/**
	 * Encoding to use when reading and writing bytes from/to stream.
	 */
	private String encoding = "UTF-8";

	/**
	 * When this flag is disabled (false), any missing resource will cause an
	 * exception. This is useful to easy identify invalid resources.
	 */
	private boolean ignoreMissingResources = true;

	/**
	 * When a group is empty and this flag is false, the processing will fail.
	 * This is useful for runtime solution to allow filter chaining when there
	 * is nothing to process for a given request.
	 */
	private boolean ignoreEmptyGroup = true;

	/**
	 * Available since 1.4.7. When this flag is true, any failure during
	 * processing will leave the content unchanged.
	 */
	private boolean ignoreFailingProcessor = false;

	/**
	 * When this flag is enabled, the raw processed content will be gzipped only
	 * the first time and all subsequent requests will use the cached gzipped
	 * content. Otherwise, the gzip operation will be performed for each
	 * request. This flag allow to control the memory vs processing power
	 * trade-off. Defaults to true.
	 */
	private boolean cacheGzippedContent = true;

	/**
	 * A flag used for turning on/off JMX. Defaults to false.
	 */
	private boolean jmxEnabled = false;

	/**
	 * The name of MBean object (how it is displayed in JMX console). If
	 * contextPath is empty, the name is wro4j-ROOT.
	 */
	private String mbeanName;

	/**
	 * Url under which the wro4j filter is registered. Defaults to
	 * "/wro4j".
	 */
	private String filterUrl = "/wro4j";

	/**
	 * The model for wro4j. Defaults to /wro.xml.
	 */
	private String model = "/wro.xml";

	private WroManagerFactoryProperties managerFactory;

	/**
	 * A comma separated list of pre processor classes to be used during
	 * processing. Those can either be classes of existing Spring beans or
	 * "normal" classes. If there is a bean of the given type, than that bean is
	 * used. Otherwise, it tries to instantiate a new instance using the default
	 * constructor.
	 */
	private List<Class<? extends ResourcePreProcessor>> preProcessors;

	/**
	 * A comma separated list of post processor classes to be used during
	 * processing. Those can either be classes of existing Spring beans or
	 * "normal" classes. If there is a bean of the given type, than that bean is
	 * used. Otherwise, it tries to instantiate a new instance using the default
	 * constructor.
	 */
	private List<Class<? extends ResourcePostProcessor>> postProcessors;

	/**
	 * The name of a Spring Cache. If this property is set and a
	 * {@link CacheManager} is configured (for example through
	 * {@link EnableCaching @EnableCaching}, then a {@link CacheStrategy} based
	 * on Spring cache abstraction will be used.
	 */
	private String cacheName;

	public boolean isDebug() {
		return this.debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isMinimizeEnabled() {
		return this.minimizeEnabled;
	}

	public void setMinimizeEnabled(boolean minimizeEnabled) {
		this.minimizeEnabled = minimizeEnabled;
	}

	public boolean isGzipResources() {
		return this.gzipResources;
	}

	public void setGzipResources(boolean gzipResources) {
		this.gzipResources = gzipResources;
	}

	public Integer getResourceWatcherUpdatePeriod() {
		return this.resourceWatcherUpdatePeriod;
	}

	public void setResourceWatcherUpdatePeriod(Integer resourceWatcherUpdatePeriod) {
		this.resourceWatcherUpdatePeriod = resourceWatcherUpdatePeriod;
	}

	public boolean isResourceWatcherAsync() {
		return this.resourceWatcherAsync;
	}

	public void setResourceWatcherAsync(boolean resourceWatcherAsync) {
		this.resourceWatcherAsync = resourceWatcherAsync;
	}

	public Integer getCacheUpdatePeriod() {
		return this.cacheUpdatePeriod;
	}

	public void setCacheUpdatePeriod(Integer cacheUpdatePeriod) {
		this.cacheUpdatePeriod = cacheUpdatePeriod;
	}

	public Integer getModelUpdatePeriod() {
		return this.modelUpdatePeriod;
	}

	public void setModelUpdatePeriod(Integer modelUpdatePeriod) {
		this.modelUpdatePeriod = modelUpdatePeriod;
	}

	public String getHeader() {
		return this.header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public boolean isParallelPreprocessing() {
		return this.parallelPreprocessing;
	}

	public void setParallelPreprocessing(boolean parallelPreprocessing) {
		this.parallelPreprocessing = parallelPreprocessing;
	}

	public Long getConnectionTimeout() {
		return this.connectionTimeout;
	}

	public void setConnectionTimeout(Long connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public String getEncoding() {
		return this.encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isIgnoreMissingResources() {
		return this.ignoreMissingResources;
	}

	public void setIgnoreMissingResources(boolean ignoreMissingResources) {
		this.ignoreMissingResources = ignoreMissingResources;
	}

	public boolean isIgnoreEmptyGroup() {
		return this.ignoreEmptyGroup;
	}

	public void setIgnoreEmptyGroup(boolean ignoreEmptyGroup) {
		this.ignoreEmptyGroup = ignoreEmptyGroup;
	}

	public boolean isIgnoreFailingProcessor() {
		return this.ignoreFailingProcessor;
	}

	public void setIgnoreFailingProcessor(boolean ignoreFailingProcessor) {
		this.ignoreFailingProcessor = ignoreFailingProcessor;
	}

	public boolean isCacheGzippedContent() {
		return this.cacheGzippedContent;
	}

	public void setCacheGzippedContent(boolean cacheGzippedContent) {
		this.cacheGzippedContent = cacheGzippedContent;
	}

	public boolean isJmxEnabled() {
		return this.jmxEnabled;
	}

	public void setJmxEnabled(boolean jmxEnabled) {
		this.jmxEnabled = jmxEnabled;
	}

	public String getMbeanName() {
		return this.mbeanName;
	}

	public void setMbeanName(String mbeanName) {
		this.mbeanName = mbeanName;
	}

	public String getFilterUrl() {
		return this.filterUrl;
	}

	public void setFilterUrl(String filterUrl) {
		this.filterUrl = filterUrl;
	}

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public WroManagerFactoryProperties getManagerFactory() {
		return this.managerFactory;
	}

	public void setManagerFactory(WroManagerFactoryProperties managerFactory) {
		this.managerFactory = managerFactory;
	}

	public List<Class<? extends ResourcePreProcessor>> getPreProcessors() {
		return this.preProcessors;
	}

	public void setPreProcessors(List<Class<? extends ResourcePreProcessor>> preProcessors) {
		this.preProcessors = preProcessors;
	}

	public List<Class<? extends ResourcePostProcessor>> getPostProcessors() {
		return this.postProcessors;
	}

	public void setPostProcessors(List<Class<? extends ResourcePostProcessor>> postProcessors) {
		this.postProcessors = postProcessors;
	}

	public String getCacheName() {
		return this.cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	/**
	 * Encapsulates all properties for a {@code ConfigurableWroManagerFactory}.
	 */
	public static class WroManagerFactoryProperties {

		private String preProcessors;

		private String postProcessors;

		public String getPreProcessors() {
			return this.preProcessors;
		}

		public void setPreProcessors(String preProcessors) {
			this.preProcessors = preProcessors;
		}

		public String getPostProcessors() {
			return this.postProcessors;
		}

		public void setPostProcessors(String postProcessors) {
			this.postProcessors = postProcessors;
		}
	}
}
