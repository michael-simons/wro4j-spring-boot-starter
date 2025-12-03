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
package ac.simons.spring.boot.wro4j.processors;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michael J. Simons
 * @since 2016-01-18
 */
class RemoveSourceMapsProcessorTests {

	private final String lineSeparator = System.lineSeparator();

	@Test
	void processShouldWork() throws IOException {
		final String input = """
				normal
				 blanks am anfang und ende\s
				Javascript Single line
				//# sourceMappingURL=jquery.min.map
				Javascript Multi line
				/*# sourceMappingURL=jquery.min.map */
				CSS Multi line
				/*# sourceMappingURL=bootstrap.css.map */
				Depecrated formats
				//@ sourceMappingURL=jquery.min.map
				/*@ sourceMappingURL=bootstrap.css.map */
				ende""";

		final StringReader reader = new StringReader(input);
		final StringWriter writer = new StringWriter();
		final RemoveSourceMapsProcessor processor = new RemoveSourceMapsProcessor();
		processor.process(null, reader, writer);
		assertThat(writer.toString()).isEqualTo("normal" + this.lineSeparator + " blanks am anfang und ende "
				+ this.lineSeparator + "Javascript Single line" + this.lineSeparator + "Javascript Multi line"
				+ this.lineSeparator + "CSS Multi line" + this.lineSeparator + "Depecrated formats" + this.lineSeparator
				+ "ende" + this.lineSeparator);
	}

}
