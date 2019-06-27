/**
 * This software is licensed under the Apache 2 license, quoted below.<br>
 * <br>
 * Copyright 2019 Andras Berkes [andras.berkes@programmer.net]<br>
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
package io.datatree.dom;

import java.util.Set;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * PackageScanner JUnit test cases.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class ScannerTest extends TestCase {

	@Test
	public void testScanner() throws Exception {
		String txt = PackageScanner.findByFormat("json", true);
		assertTrue(txt.contains("JsonBuiltin"));
		
		Set<String> set = PackageScanner.getReadersByFormat("json");
		assertTrue(set.contains("io.datatree.dom.builtin.JsonBuiltin"));
		
		set = PackageScanner.getWritersByFormat("json");
		assertTrue(set.contains("io.datatree.dom.builtin.JsonBuiltin"));
	}
	
}
