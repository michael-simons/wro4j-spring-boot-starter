/*
 * Copyright 2015-2020 the original author or authors.
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

package ac.simons.spring.boot.wro4j.processors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Pattern;

import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;

/**
 * Entfernt {@code SourceMappingURLs} aus zu minimierenden Dateien (siehe
 * <a href="https://blog.mayflower.de/4491-Source-Maps-in-JavaScript.html">Komprimiertes
 * JavaScript unter Kontrolle: Source Maps</a>.
 *
 * @author Michael J. Simons
 * @since 2016-01-18
 */
public class RemoveSourceMapsProcessor implements ResourcePreProcessor {

	/**
	 * Pattern to match sourceMappingUrls.
	 */
	public static final Pattern SOURCE_MAP_PATTERN = Pattern
		.compile("^/(?:\\*|/)?(?:#|@) sourceMappingURL=.+(?:\\s+\\*/)?$");

	@Override
	public void process(Resource resource, Reader reader, Writer writer) throws IOException {

		try (
			BufferedReader bufferedReader = new BufferedReader(reader);
			BufferedWriter bufferedWriter = new BufferedWriter(writer)
		) {

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (!SOURCE_MAP_PATTERN.matcher(line).matches()) {
					bufferedWriter.write(line);
					bufferedWriter.newLine();
				}
			}
			bufferedWriter.flush();
		}
	}
}
