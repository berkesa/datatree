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

import static io.datatree.dom.converters.DataConverterRegistry.convert;

import java.net.InetAddress;
import java.util.Date;

import org.junit.Test;

import io.datatree.dom.BASE64;
import io.datatree.dom.Config;
import junit.framework.TestCase;

/**
 * Memory cache tests.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class ConverterTest extends TestCase {

	@Test
	public void testConverters() throws Exception {

		// --- VALUE TO STRING CONVERTERS ---

		assertConverted("xyz", String.class, this);
		assertConverted(BASE64.encode("hello".getBytes()), String.class, "hello".getBytes());
		Date d = new Date();
		assertConverted(Config.TIMESTAMP_FORMATTER.format(d), String.class, d);
		assertConverted("true", String.class, true);
		assertConverted("false", String.class, false);
		InetAddress i = InetAddress.getLocalHost();
		assertConverted(i.getCanonicalHostName(), String.class, i);
		
	}

	public static final void assertConverted(Object expected, Class<?> to, Object from)	{
		assertEquals(expected, convert(to, from));
	}

	@Override
	public String toString() {
		return "xyz";
	}
	
}