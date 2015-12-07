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

import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;

import org.springframework.boot.context.properties.ConfigurationProperties;

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
	 * When this flag is enabled response will be gziped. Defaults to true.
	 */
	private boolean gzipResources = true;

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
	 * Url Pattern under which the wro4j filter is registered. Defaults to
	 * "wro4j/*".
	 */
	private String filterUrlPattern = "/wro4j/*";

	/**
	 * The model for wro4j. Defaults to /wro.xml.
	 */
	private String model = "/wro.xml";

	private List<Class<? extends ResourcePreProcessor>> preProcessors;

	private List<Class<? extends ResourcePostProcessor>> postProcessors;

	public boolean isDebug() {
		return this.debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isGzipResources() {
		return this.gzipResources;
	}

	public void setGzipResources(boolean gzipResources) {
		this.gzipResources = gzipResources;
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

	public String getFilterUrlPattern() {
		return this.filterUrlPattern;
	}

	public void setFilterUrlPattern(String filterUrlPattern) {
		this.filterUrlPattern = filterUrlPattern;
	}

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
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
}
