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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.times;

/**
 * @author Michael J. Simons, 2016-01-31
 */
@RunWith(MockitoJUnitRunner.class)
public class SpringCacheStrategyTest {

	@Mock
	private CacheManager cacheManager;

	@Mock
	private Cache cache;

	private final String cacheName = "cacheName";

	@Before
	public void initCache() {
		Mockito.when(this.cacheManager.getCache(this.cacheName)).thenReturn(this.cache);
	}

	@After
	public void verifiyInteractions() {
		Mockito.verifyNoMoreInteractions(this.cacheManager, this.cache);
	}

	@Test
	public void putShouldWork() {
		final SpringCacheStrategy<Object, Object> cacheStrategy = new SpringCacheStrategy<Object, Object>(this.cacheManager, this.cacheName);
		cacheStrategy.put("foo", "bar");

		Mockito.verify(this.cacheManager, times(1)).getCache(this.cacheName);
		Mockito.verify(this.cache, times(1)).put("foo", "bar");
	}

	@Test
	public void getShouldWork() {
		Mockito.when(this.cache.get("bazbar")).thenReturn(new Cache.ValueWrapper() {
			@Override
			public Object get() {
				return "bazbaz";
			}
		});

		final SpringCacheStrategy<Object, Object> cacheStrategy = new SpringCacheStrategy<Object, Object>(this.cacheManager, this.cacheName);

		Assert.assertNull(cacheStrategy.get("foobar"));
		Assert.assertThat((String) cacheStrategy.get("bazbar"), is("bazbaz"));

		Mockito.verify(this.cacheManager, times(2)).getCache(this.cacheName);
		Mockito.verify(this.cache, times(1)).get("foobar");
		Mockito.verify(this.cache, times(1)).get("bazbar");
		Mockito.verifyNoMoreInteractions(this.cacheManager, this.cache);
	}

	@Test
	public void clearAndDestroyShouldWork() {
		final SpringCacheStrategy<Object, Object> cacheStrategy = new SpringCacheStrategy<Object, Object>(this.cacheManager, this.cacheName);
		cacheStrategy.clear();
		cacheStrategy.destroy();

		Mockito.verify(this.cacheManager, times(2)).getCache(this.cacheName);
		Mockito.verify(this.cache, times(2)).clear();
	}
}
