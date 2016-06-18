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

import ac.simons.spring.boot.wro4j.Wro4jAutoConfigurationIntegrationTests.CustomCacheStrategyShouldWork;
import ac.simons.spring.boot.wro4j.Wro4jAutoConfigurationIntegrationTests.DefaultConfigurationShouldWork;
import ac.simons.spring.boot.wro4j.Wro4jAutoConfigurationIntegrationTests.NoAutoConfigurationShouldWork;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import ro.isdc.wro.cache.CacheStrategy;
import ro.isdc.wro.cache.impl.LruMemoryCacheStrategy;
import ro.isdc.wro.http.ConfigurableWroFilter;
import ro.isdc.wro.http.WroFilter;
import ro.isdc.wro.manager.factory.BaseWroManagerFactory;
import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.resource.processor.factory.ProcessorsFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests various szenarios of Autoconfiguration.
 *
 * @author Michael J. Simons, 2016-02-02
 */
@RunWith(Wro4jAutoConfigurationIntegrationTests.class)
@SuiteClasses({NoAutoConfigurationShouldWork.class, DefaultConfigurationShouldWork.class, CustomCacheStrategyShouldWork.class})
public class Wro4jAutoConfigurationIntegrationTests extends Suite {

	public Wro4jAutoConfigurationIntegrationTests(Class<?> klass, RunnerBuilder builder) throws InitializationError {
		super(klass, builder);
	}

	@RunWith(SpringJUnit4ClassRunner.class)
	@SpringBootTest(classes = ApplicationWithWroFilter.class)
	public static class NoAutoConfigurationShouldWork {

		@Autowired
		ApplicationContext applicationContext;

		@Test
		public void expectedBeansShouldBePresent() {
			Assert.assertEquals(0, this.applicationContext.getBeansOfType(WroModelFactory.class).size());
			Assert.assertEquals(0, this.applicationContext.getBeansOfType(ProcessorsFactory.class).size());
			Assert.assertEquals(0, this.applicationContext.getBeansOfType(CacheStrategy.class).size());
			Assert.assertEquals(0, this.applicationContext.getBeansOfType(WroManagerFactory.class).size());
			Assert.assertEquals(0, this.applicationContext.getBeansOfType(FilterRegistrationBean.class).size());

			final ConfigurableWroFilter wroFilter = this.applicationContext.getBean(ConfigurableWroFilter.class);
			Assert.assertNotNull(wroFilter);
			Assert.assertTrue(wroFilter instanceof ConfigurableWroFilter);
		}
	}

	@EnableAutoConfiguration
	@Configuration
	public static class ApplicationWithWroFilter {

		@Bean
		public WroFilter wroFilter() {
			return new ConfigurableWroFilter();
		}
	}

	@RunWith(SpringJUnit4ClassRunner.class)
	@SpringBootTest(classes = EmptyApplication.class)
	public static class DefaultConfigurationShouldWork {

		@Autowired
		ApplicationContext applicationContext;

		@Test
		public void expectedBeansShouldBePresent() {
			final WroModelFactory wroModelFactory = this.applicationContext.getBean(WroModelFactory.class);
			Assert.assertNotNull(wroModelFactory);
			Assert.assertTrue(wroModelFactory instanceof ConfigurableXmlModelFactory);

			final ProcessorsFactory processorsFactory = this.applicationContext.getBean(ProcessorsFactory.class);
			Assert.assertNotNull(processorsFactory);

			final CacheStrategy cacheStrategy = this.applicationContext.getBean(CacheStrategy.class);
			Assert.assertNotNull(cacheStrategy);
			Assert.assertTrue(cacheStrategy instanceof LruMemoryCacheStrategy);

			final WroManagerFactory wroManagerFactory = this.applicationContext.getBean(WroManagerFactory.class);
			Assert.assertNotNull(wroManagerFactory);
			Assert.assertTrue(wroManagerFactory instanceof BaseWroManagerFactory);

			final ConfigurableWroFilter wroFilter = this.applicationContext.getBean(ConfigurableWroFilter.class);
			Assert.assertNotNull(wroFilter);
			Assert.assertTrue(wroFilter instanceof ConfigurableWroFilter);

			final FilterRegistrationBean wro4jFilterRegistration = this.applicationContext.getBean(FilterRegistrationBean.class);
			Assert.assertNotNull(wro4jFilterRegistration);
		}
	}

	@EnableAutoConfiguration
	public static class EmptyApplication {
	}

	@RunWith(SpringJUnit4ClassRunner.class)
	@SpringBootTest(classes = ApplicationWithCacheManager.class)
	public static class CustomCacheStrategyShouldWork {

		@Autowired
		ApplicationContext applicationContext;

		@Test
		public void expectedBeansShouldBePresent() {
			final CacheStrategy cacheStrategy = this.applicationContext.getBean(CacheStrategy.class);
			Assert.assertNotNull(cacheStrategy);
			Assert.assertTrue(cacheStrategy instanceof SpringCacheStrategy);
		}
	}

	@EnableAutoConfiguration
	@EnableCaching
	@PropertySource("applicationWithCacheManager.properties")
	public static class ApplicationWithCacheManager {

	}

}
