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
package io.datatree.dom;

import static io.datatree.dom.TreeWriterRegistry.suggestDependency;

import java.util.concurrent.ConcurrentHashMap;

import io.datatree.dom.builtin.JsonBuiltin;

/**
 * Registry of TreeReader instances.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class TreeReaderRegistry {

	// --- DEFAULT INPUT FORMAT ---

	public static final String JSON = "json";

	// --- PRIVATE CONSTRUCTOR ---

	private TreeReaderRegistry() {
	}

	// --- READER REGISTRY ---

	private static final ConcurrentHashMap<String, TreeReader> readers = new ConcurrentHashMap<>();

	// --- SET THE DEFAULT READER ---

	/**
	 * Binds the given TreeReader instance to the specified data format.
	 * 
	 * @param format
	 *            name of the format (eg. "json", "xml", "csv", etc.)
	 * @param reader
	 *            TreeReader instance for parsing the specified format
	 */
	public static final void setReader(String format, TreeReader reader) {
		format = format.toLowerCase();
		readers.put(format, reader);
		if (JSON.equals(format)) {
			cachedJsonReader = reader;
		}
	}

	// --- DEREGISTER READER ---

	/**
	 * Deregisters the given format from the registry.
	 * 
	 * @param format
	 *            name of the format (eg. "custom", "csv", etc.)
	 */
	public static final void removeReader(String format) {
		format = format.toLowerCase();
		if (format.equals(JSON)) {
			throw new IllegalArgumentException("Unable to delete the default JSON reader!");
		}
		readers.remove(format);
	}

	// --- DEFAULT JSON READER ---

	private static TreeReader cachedJsonReader = getReader(JSON, false);

	// --- GET READER BY FORMAT NAME ---

	/**
	 * Returns the TreeReader instance to which the specified format is mapped.
	 * 
	 * @param format
	 *            name of the format (eg. "json", "xml", etc.)
	 * 
	 * @return TreeReader instance
	 */
	public static final TreeReader getReader(String format) {
		return getReader(format, true);
	}

	/**
	 * Check the availability of the specified format.
	 * 
	 * @param format
	 *            name of the format (eg. "json", "xml", etc.)
	 * 
	 * @return returns true, if the proper writer is installed
	 */
	public static final boolean isAvailable(String format) {
		return getReader(format, false) != null;
	}

	// --- FACTORY FINDER METHOD ---

	private static final TreeReader getReader(String format, boolean throwException) {
		TreeReader reader;
		if (format == null) {
			reader = cachedJsonReader;
		} else {
			format = format.toLowerCase();
			if (JSON.equals(format)) {
				reader = cachedJsonReader;
			} else {
				reader = readers.get(format);
			}
		}
		if (reader != null) {
			return reader;
		}

		// Search reader class by system property:
		//
		// -Ddatatree.json.reader=io.datatree.adapters.JsonBoon
		// -Ddatatree.xml.reader=io.datatree.adapters.XmlJackson
		// -Ddatatree.yaml.reader=your.yaml.reader.ClassName
		// -Ddatatree.csv.reader=your.csv.reader.ClassName
		// -Ddatatree.properties.reader=your.properties.reader.ClassName
		//
		// ...or to use your "custom" format:
		//
		// -Ddatatree.custom.reader=your.custom.reader.ClassName
		// Tree node = new Tree(source, "custom");
		//
		String propertyName = "datatree." + format + ".reader";
		try {
			String className = System.getProperty(propertyName);
			if (className == null || className.isEmpty()) {
				className = PackageScanner.findByFormat(format, true);
			}
			if (className == null || className.isEmpty()) {
				throw new IllegalArgumentException("System Property \"" + propertyName + "\" not found! "
						+ "This property defines a custom reader class for the \"" + format + "\" format.");
			}
			reader = (TreeReader) Class.forName(className).newInstance();
			readers.put(format, reader);
			return reader;
		} catch (Throwable cause) {
			if (throwException) {
				suggestDependency(format);
				cause.printStackTrace();
				throw new RuntimeException("Unable to create reader for format \"" + format + "\"! Set the -D"
						+ propertyName + "=package.ReaderClass initial parameter to specify the proper reader class.",
						cause);
			}
		}
		return new JsonBuiltin();
	}

}