/*
 * Copyright 2015-2018 the original author or authors.
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

import org.assertj.core.api.Condition;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils;
import ro.isdc.wro.cache.CacheStrategy;
import ro.isdc.wro.cache.impl.LruMemoryCacheStrategy;
import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.http.WroFilter;
import ro.isdc.wro.manager.factory.BaseWroManagerFactory;
import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.resource.processor.factory.ProcessorsFactory;
import ro.isdc.wro.model.resource.support.ResourceAuthorizationManager;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests various szenarios of Autoconfiguration.
 *
 * @author Michael J. Simons, 2016-02-02
 */
public class Wro4jAutoConfigurationIntegrationTests {

	final ApplicationContextRunner applicationContextRunner = new ApplicationContextRunner();

	@Test
	public void noAutoConfigurationShouldWork() {
		applicationContextRunner.withConfiguration(AutoConfigurations.of(Wro4jAutoConfiguration.class))
				.withUserConfiguration(ApplicationWithWroFilter.class)
				.run(ctx -> assertThat(ctx)
						.doesNotHaveBean(WroModelFactory.class)
						.doesNotHaveBean(ProcessorsFactory.class)
						.doesNotHaveBean(CacheStrategy.class)
						.doesNotHaveBean(WroManagerFactory.class)
						.doesNotHaveBean(FilterRegistrationBean.class)
						.hasSingleBean(ConfigurableWroFilter.class)
				);
	}

	static class ApplicationWithWroFilter {
		@Bean
		public WroFilter wroFilter() {
			return new ConfigurableWroFilter();
		}
	}

	@Test
	public void defaultConfigurationShouldWork() {
		final Condition<ProcessorsFactory> configuredProcessorsFactory = new Condition<>(
				p -> p.getPreProcessors().size() == 1 && p.getPreProcessors().toArray()[0] instanceof DefaultResourcePreProcessor,
				"Has one preprocess of type DefaultResourcePreProcessor");

		applicationContextRunner.withConfiguration(AutoConfigurations.of(Wro4jAutoConfiguration.class))
				.withPropertyValues("wro4j.preProcessors = ac.simons.spring.boot.wro4j.DefaultResourcePreProcessor")
				.run(ctx -> {
					assertThat(ctx).getBean(WroModelFactory.class).isExactlyInstanceOf(ConfigurableXmlModelFactory.class);
					assertThat(ctx).getBean(CacheStrategy.class).isExactlyInstanceOf(LruMemoryCacheStrategy.class);
					assertThat(ctx).getBean(WroManagerFactory.class).isExactlyInstanceOf(BaseWroManagerFactory.class);
					assertThat(ctx)
							.hasSingleBean(ConfigurableWroFilter.class)
							.hasSingleBean(FilterRegistrationBean.class)
							.getBean(ProcessorsFactory.class)
								.has(configuredProcessorsFactory);
				});
	}

	@Test
	public void customCacheStrategyShouldWork() {
		applicationContextRunner.withConfiguration(AutoConfigurations.of(CacheAutoConfiguration.class, Wro4jAutoConfiguration.class))
				.withUserConfiguration(ApplicationWithCacheManager.class)
				.withPropertyValues("wro4j.cacheName = foobar")
				.run(ctx -> assertThat(ctx).getBean(CacheStrategy.class).isExactlyInstanceOf(SpringCacheStrategy.class));
	}

	@EnableCaching
	static class ApplicationWithCacheManager {
	}

	@Test
	public void shouldBeResourceAuthorizationManagerAware() {
		applicationContextRunner.withConfiguration(AutoConfigurations.of(Wro4jAutoConfiguration.class))
				.withUserConfiguration(ApplicationWithResourceAuthorizationManager.class)
				.run(ctx -> {
					final WroManagerFactory wroManagerFactory = ctx.getBean(WroManagerFactory.class);
					final Field f = ReflectionUtils.findField(BaseWroManagerFactory.class, "authorizationManager");
					f.setAccessible(true);
					final Object actualResourceAuthorizationManager = ReflectionUtils.getField(f, wroManagerFactory);

					assertThat(ctx).getBean(ResourceAuthorizationManager.class).isEqualTo(actualResourceAuthorizationManager);
				});
	}

	static class ApplicationWithResourceAuthorizationManager {
		@Bean
		public ResourceAuthorizationManager resourceAuthorizationManager() {
			return uri -> false;
		}
	}
}
