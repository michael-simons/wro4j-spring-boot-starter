/*
 * Copyright 2015-2019 the original author or authors.
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.mockito.Mockito.times;

/**
 * @author Michael J. Simons
 *
 * @since 2016-01-31
 */
@ExtendWith(MockitoExtension.class)
class SpringCacheStrategyTest {

	@Mock
	private CacheManager cacheManager;

	@Mock
	private Cache cache;

	private static final String CACHE_NAME = "CACHE_NAME";

	@BeforeEach
	void initCache() {
		Mockito.when(this.cacheManager.getCache(CACHE_NAME)).thenReturn(this.cache);
	}

	@AfterEach
	void verifiyInteractions() {
		Mockito.verifyNoMoreInteractions(this.cacheManager, this.cache);
	}

	@Test
	void putShouldWork() {
		final SpringCacheStrategy<Object, Object> cacheStrategy = new SpringCacheStrategy<>(this.cacheManager, CACHE_NAME);
		cacheStrategy.put("foo", "bar");

		Mockito.verify(this.cacheManager, times(1)).getCache(CACHE_NAME);
		Mockito.verify(this.cache, times(1)).put("foo", "bar");
	}

	@Test
	void getShouldWork() {
		Mockito.when(this.cache.get("foobar")).thenReturn(null);
		Mockito.when(this.cache.get("bazbar")).thenReturn(() -> "bazbaz");

		final SpringCacheStrategy<Object, Object> cacheStrategy = new SpringCacheStrategy<>(this.cacheManager, CACHE_NAME);

		Assertions.assertNull(cacheStrategy.get("foobar"));
		Assertions.assertEquals(cacheStrategy.get("bazbar"), "bazbaz");

		Mockito.verify(this.cacheManager, times(2)).getCache(CACHE_NAME);
		Mockito.verify(this.cache, times(1)).get("foobar");
		Mockito.verify(this.cache, times(1)).get("bazbar");
		Mockito.verifyNoMoreInteractions(this.cacheManager, this.cache);
	}

	@Test
	void clearAndDestroyShouldWork() {
		final SpringCacheStrategy<Object, Object> cacheStrategy = new SpringCacheStrategy<>(this.cacheManager, CACHE_NAME);
		cacheStrategy.clear();
		cacheStrategy.destroy();

		Mockito.verify(this.cacheManager, times(2)).getCache(CACHE_NAME);
		Mockito.verify(this.cache, times(2)).clear();
	}
}
