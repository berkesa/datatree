/**
 * This software is licensed under the Apache 2 license, quoted below.<br>
 * <br>
 * Copyright 2017 Andras Berkes [andras.berkes@programmer.net]<br>
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
package org.datatree.dom;

import java.util.concurrent.ConcurrentHashMap;

import org.datatree.dom.builtin.JsonBuiltin;

/**
 * Registry of TreeWriter instances.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class TreeWriterRegistry  {

	// --- DEFAULT OUTPUT FORMAT ---
	
	public static final String JSON = "json";

	// --- PRIVATE CONSTRUCTOR ---

	private TreeWriterRegistry() {
	}

	// --- WRITER REGISTRY ---

	private static final ConcurrentHashMap<String, TreeWriter> writers = new ConcurrentHashMap<String, TreeWriter>();

	// --- SET THE DEFAULT WRITER FOR FORMAT ---

	/**
	 * Binds the given TreeWriter instance to the specified data format.
	 * 
	 * @param format
	 *            name of the format (eg. "json", "xml", "csv", etc.)
	 * @param reader
	 *            TreeWriter instance for generating the specified format
	 */
	public static final void setWriter(String format, TreeWriter writer) {
		format = format.toLowerCase();
		writers.put(format, writer);
		if (JSON.equals(format)) {
			cachedJsonWriter = writer;
		}
	}
	
	// --- DEREGISTER WRITER ---
	
	/**
	 * Deregisters the given format from the registry.
	 * 
	 * @param format
	 *            name of the format (eg. "custom", "csv", etc.)
	 */
	public static final void removeWriter(String format) {
		format = format.toLowerCase();
		if (format.equals(JSON)) {
			throw new IllegalArgumentException("Unable to delete the default JSON writer!");
		}
		writers.remove(format);		
	}

	// --- DEFAULT JSON WRITER ---

	private static TreeWriter cachedJsonWriter = getWriter(JSON, false);

	// --- GET WRITER BY FORMAT NAME ---

	/**
	 * Returns the TreeWriter instance to which the specified format is mapped.
	 * 
	 * @param format
	 *            name of the format (eg. "json", "xml", etc.)
	 * 
	 * @return TreeWriter instance
	 */
	public static final TreeWriter getWriter(String format) {
		return getWriter(format, true);
	}

	// --- FACTORY FINDER METHOD ---

	private static final TreeWriter getWriter(String format, boolean throwException) {
		TreeWriter writer;
		if (format == null) {
			writer = cachedJsonWriter;
		} else {
			format = format.toLowerCase();
			if (JSON.equals(format)) {
				writer = cachedJsonWriter;
			} else {
				writer = writers.get(format);
			}
		}
		if (writer != null) {
			return writer;
		}

		// Searching writer class in system properties... Samples:
		//
		// -Ddatatree.json.writer=org.datatree.writers.JacksonjsonWriter
		// -Ddatatree.xml.writer=org.datatree.writers.BuiltinxmlWriter
		// -Ddatatree.yaml.writer=org.datatree.writers.SnakeyamlWriter
		// -Ddatatree.csv.writer=your.csv.writer.ClassName
		// -Ddatatree.properties.writer=your.properties.writer.ClassName
		//
		// ...and to use your "custom" format:
		//
		// -Ddatatree.custom.writer=your.custom.writer.ClassName
		// String out = node.toString("custom");
		//
		String propertyName = "datatree." + format + ".writer";
		try {
			String className = System.getProperty(propertyName);
			if (className == null || className.isEmpty()) {
				className = PackageScanner.findByFormat(format, false);				
			}
			writer = (TreeWriter) Class.forName(className).newInstance();
			writers.put(format, writer);
			return writer;
		} catch (Exception cause) {
			if (throwException) {
				throw new RuntimeException("Unable to create writer for format \"" + format + "\"! Set the -D"
						+ propertyName + "=package.WriterClass initial parameter to specify the proper writer class.",
						cause);
			}
			cause.printStackTrace();
		}
		return new JsonBuiltin();
	}

}