/**
 * This software is licensed under the Apache 2 license, quoted below.<br>
 * <br>
 * Copyright 2018 Andras Berkes [andras.berkes@programmer.net]<br>
 * <br>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at<br>
 * <br>
 * http://www.apache.org/licenses/LICENSE-2.0<br>
 * <br>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.datatree;

import org.junit.Test;

import io.datatree.dom.Cache;
import junit.framework.TestCase;

/**
 * Memory cache tests.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class CacheTest extends TestCase {

	@Test
	public void testCache() throws Exception {
		
		Cache<Integer, Integer> c = new Cache<>(5);
		assertEquals(0, c.size());
		
		c.put(1, 11);
		assertEquals(1, c.size());

		c.put(2, 22);
		assertEquals(2, c.size());

		c.put(3, 33);
		assertEquals(3, c.size());

		c.put(4, 44);
		assertEquals(4, c.size());

		c.put(5, 55);
		assertEquals(5, c.size());

		assertEquals(11, c.get(1).intValue());
		assertEquals(22, c.get(2).intValue());
		assertEquals(33, c.get(3).intValue());
		assertEquals(44, c.get(4).intValue());
		assertEquals(55, c.get(5).intValue());

		assertNull(c.get(6));
		assertNull(c.get(7));
		assertNull(c.get(8));
		
		c.remove(3);
		assertNull(c.get(3));
		assertEquals(4, c.size());

		c.put(3, 333);
		assertEquals(333, c.get(3).intValue());
		assertEquals(5, c.size());

		c.put(6, 66);
		assertEquals(5, c.size());
		assertNull(c.get(1));
		assertEquals(22, c.get(2).intValue());
		assertEquals(66, c.get(6).intValue());
		
		c.put(7, 77);
		assertEquals(5, c.size());
		assertNull(c.get(1));
		assertNull(c.get(2));
		assertEquals(333, c.get(3).intValue());
		assertEquals(44, c.get(4).intValue());
		assertEquals(55, c.get(5).intValue());
		assertEquals(77, c.get(7).intValue());

		c.put(8, 88);
		assertEquals(5, c.size());
		assertNull(c.get(1));
		assertNull(c.get(2));
		assertNull(c.get(4));
		assertEquals(333, c.get(3).intValue());
		assertEquals(55, c.get(5).intValue());
		assertEquals(66, c.get(6).intValue());
		assertEquals(77, c.get(7).intValue());

		c.clear();
		assertNull(c.get(1));
		assertNull(c.get(2));
		assertNull(c.get(3));
		assertNull(c.get(4));
		assertNull(c.get(5));
		assertEquals(0, c.size());		
	}

}