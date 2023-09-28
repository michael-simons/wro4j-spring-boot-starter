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

package ac.simons.spring.boot.wro4j;

import java.lang.reflect.Field;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import ro.isdc.wro.cache.CacheStrategy;
import ro.isdc.wro.cache.impl.LruMemoryCacheStrategy;
import ro.isdc.wro.cache.impl.NoCacheStrategy;
import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.http.WroFilter;
import ro.isdc.wro.manager.factory.BaseWroManagerFactory;
import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.resource.processor.factory.ProcessorsFactory;
import ro.isdc.wro.model.resource.support.ResourceAuthorizationManager;
import ro.isdc.wro.model.resource.support.naming.DefaultHashEncoderNamingStrategy;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests various scenarios of Autoconfiguration.
 *
 * @author Michael J. Simons
 *
 * @since 2016-02-02
 */
class Wro4jAutoConfigurationIntegrationTests {

	final ApplicationContextRunner applicationContextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(CacheAutoConfiguration.class, Wro4jAutoConfiguration.class));

	@Test
	void noAutoConfigurationShouldWork() {
		applicationContextRunner.withUserConfiguration(ApplicationWithWroFilter.class)
				.run(ctx -> assertThat(ctx)
						.doesNotHaveBean(WroModelFactory.class)
						.doesNotHaveBean(ProcessorsFactory.class)
						.doesNotHaveBean(CacheStrategy.class)
						.doesNotHaveBean(WroManagerFactory.class)
						.doesNotHaveBean(FilterRegistrationBean.class)
						.hasSingleBean(ConfigurableWroFilter.class)
				);
	}

	@Test // GH-142
	void customWroManagerFactoryShouldWork() {
		applicationContextRunner.withUserConfiguration(ApplicationWithCustomManagerFactory.class)
			.run(ctx -> {
					assertThat(ctx)
						.hasSingleBean(WroModelFactory.class)
						.doesNotHaveBean(ProcessorsFactory.class)
						.hasSingleBean(CacheStrategy.class)
						.hasSingleBean(WroManagerFactory.class)
						.hasSingleBean(FilterRegistrationBean.class)
						.hasSingleBean(ConfigurableWroFilter.class);
					WroManagerFactory bean = ctx.getBean(WroManagerFactory.class);
					assertThat(bean).isInstanceOf(ConfigurableWroManagerFactory.class);
					Field cacheStrategyField = BaseWroManagerFactory.class.getDeclaredField("cacheStrategy");
					cacheStrategyField.setAccessible(true);
					Object cacheStrategy = ReflectionUtils.getField(cacheStrategyField, bean);
					assertThat(cacheStrategy).isInstanceOf(NoCacheStrategy.class);
				}
			);
	}

	@Test
	void defaultConfigurationShouldWork() {
		final Condition<ProcessorsFactory> configuredProcessorsFactory = new Condition<>(
				p -> p.getPreProcessors().size() == 1 && p.getPreProcessors().toArray()[0] instanceof DefaultResourcePreProcessor,
				"Has one preprocess of type DefaultResourcePreProcessor");

		applicationContextRunner.withPropertyValues("wro4j.preProcessors = ac.simons.spring.boot.wro4j.DefaultResourcePreProcessor")
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
	void customCacheStrategyShouldWork() {
		applicationContextRunner
				.withUserConfiguration(ApplicationWithCacheManager.class)
				.withPropertyValues("wro4j.cacheName = foobar")
				.run(ctx -> assertThat(ctx).getBean(CacheStrategy.class).isExactlyInstanceOf(SpringCacheStrategy.class));
	}

	@Test
	void shouldBeResourceAuthorizationManagerAware() {
		applicationContextRunner.withUserConfiguration(ApplicationWithResourceAuthorizationManager.class)
				.run(ctx -> assertThat(ctx).getBean(WroManagerFactory.class)
						.hasFieldOrPropertyWithValue("authorizationManager", ctx.getBean(ResourceAuthorizationManager.class)));
	}

	static class ApplicationWithWroFilter {
		@Bean
		WroFilter wroFilter() {
			return new ConfigurableWroFilter();
		}
	}

	static class ApplicationWithCustomManagerFactory {
		@Bean
		WroManagerFactory wroManagerFactory(WroModelFactory modelFactory) {

			ConfigurableWroManagerFactory wroManagerFactory = new ConfigurableWroManagerFactory();
			wroManagerFactory.setNamingStrategy(new DefaultHashEncoderNamingStrategy());
			wroManagerFactory.setModelFactory(modelFactory);
			wroManagerFactory.setCacheStrategy(new NoCacheStrategy<>());

			return wroManagerFactory;
		}
	}

	@EnableCaching
	static class ApplicationWithCacheManager {
	}

	static class ApplicationWithResourceAuthorizationManager {
		@Bean
		public ResourceAuthorizationManager resourceAuthorizationManager() {
			return uri -> false;
		}
	}
}
