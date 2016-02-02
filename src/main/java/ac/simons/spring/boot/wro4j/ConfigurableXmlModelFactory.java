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

import ro.isdc.wro.model.factory.XmlModelFactory;

/**
 * This model factory extends the default Wro4j {@link XmlModelFactory} to
 * provide a configurational hook to the path of the XML model.
 *
 * @author Michael J. Simons, 2016-02-02
 */
class ConfigurableXmlModelFactory extends XmlModelFactory {

	/**
	 * Fully qualified path to the XML model resource.
	 */
	private final String xmlModelResource;

	ConfigurableXmlModelFactory(String xmlModelResource) {
		this.xmlModelResource = xmlModelResource;
	}

	@Override
	protected InputStream getModelResourceAsStream() {
		return this.getClass().getResourceAsStream(this.xmlModelResource);
	}
}
