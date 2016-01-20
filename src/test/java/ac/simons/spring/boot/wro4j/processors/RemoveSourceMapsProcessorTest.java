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

package ac.simons.spring.boot.wro4j.processors;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael J. Simons, 2016-01-18
 */
public class RemoveSourceMapsProcessorTest {

	@Test
	public void processShouldWork() throws IOException {
		final String input
				= "normal\n"
				+ " blanks am anfang und ende \n"
				+ "Javascript Single line\n"
				+ "//# sourceMappingURL=jquery.min.map\n"
				+ "Javascript Multi line\n"
				+ "/*# sourceMappingURL=jquery.min.map */\n"
				+ "CSS Multi line\n"
				+ "/*# sourceMappingURL=bootstrap.css.map */\n"
				+ "Depecrated formats\n"
				+ "//@ sourceMappingURL=jquery.min.map\n"
				+ "/*@ sourceMappingURL=bootstrap.css.map */\n"
				+ "ende";

		final StringReader reader = new StringReader(input);
		final StringWriter writer = new StringWriter();
		final RemoveSourceMapsProcessor processor = new RemoveSourceMapsProcessor();
		processor.process(null, reader, writer);
		Assert.assertEquals(
				"normal\n"
				+ " blanks am anfang und ende \n"
				+ "Javascript Single line\n"
				+ "Javascript Multi line\n"
				+ "CSS Multi line\n"
				+ "Depecrated formats\n"
				+ "ende\n",
				writer.toString()
		);
	}
}
