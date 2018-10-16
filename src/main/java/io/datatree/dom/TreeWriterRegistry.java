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

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.datatree.dom.builtin.JsonBuiltin;

/**
 * Registry of TreeWriter instances.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class TreeWriterRegistry {

	// --- DEFAULT OUTPUT FORMAT ---

	public static final String JSON = "json";

	// --- WRITER REGISTRY ---

	private static final ConcurrentHashMap<String, TreeWriter> writers = new ConcurrentHashMap<String, TreeWriter>();

	// --- DEPENDENCY SAMPLES ---

	private static final HashMap<String, String[]> dependencySamples = new HashMap<>();

	// --- DEFAULT JSON WRITER ---

	private static TreeWriter cachedJsonWriter = getWriter(JSON, false);

	// --- PRIVATE CONSTRUCTOR ---

	private TreeWriterRegistry() {
	}

	// --- SET THE DEFAULT WRITER FOR FORMAT ---

	/**
	 * Binds the given TreeWriter instance to the specified data format.
	 * 
	 * @param format
	 *            name of the format (eg. "json", "xml", "csv", etc.)
	 * @param writer
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

	// --- FORMAT INFO ---
	
	/**
	 * Check the availability of the specified format.
	 * 
	 * @param format
	 *            name of the format (eg. "json", "xml", etc.)
	 * 
	 * @return returns true, if the proper writer is installed
	 */
	public static final boolean isAvailable(String format) {
		return getWriter(format, false).getFormat().equalsIgnoreCase(format);
	}

	/**
	 * Returns the supported format names.
	 * 
	 * @return Set of format names (eg. "json", "xml", etc.)
	 */
	public static final Set<String> getSupportedFormats() {
		return Collections.unmodifiableSet(writers.keySet());
	}
	
	/**
	 * Get detected deserializers by format name.
	 * 
	 * @param format
	 *            name of the format (eg. "json", "xml", etc.)
	 *            
	 * @return Set of class names (eg. "io.datatree.dom.builtin.JsonBuiltin")
	 */
	public static final Set<String> getWritersByFormat(String format) {
		return PackageScanner.getWritersByFormat(format);
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
		// -Ddatatree.json.writer=io.datatree.writers.JacksonjsonWriter
		// -Ddatatree.xml.writer=io.datatree.writers.BuiltinxmlWriter
		// -Ddatatree.yaml.writer=io.datatree.writers.SnakeyamlWriter
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
			if (className == null || className.isEmpty()) {
				throw new IllegalArgumentException("System Property \"" + propertyName + "\" not found! "
						+ "This property defines a custom writer class for the \"" + format + "\" format.");
			}
			writer = (TreeWriter) Class.forName(className).newInstance();
			writers.put(format, writer);
			return writer;
		} catch (Throwable cause) {
			if (throwException) {
				suggestDependency(format);
				cause.printStackTrace();
				throw new IllegalArgumentException("Unable to create writer for format \"" + format + "\"! Set the -D"
						+ propertyName + "=package.WriterClass initial parameter to specify the proper writer class.",
						cause);
			}
		}
		return new JsonBuiltin();
	}

	// --- DEPENDENCY HELPER ---

	static {
		addSample("yaml", "org.yaml", "snakeyaml", "1.19");
		addSample("csv", "net.sf.opencsv", "opencsv", "2.3");
		addSample("xstream", "xstream", "xstream", "1.2.2");
		addSample("toml", "com.moandjiezana.toml", "toml4j", "0.7.2");
		addSample("smile", "com.fasterxml.jackson.dataformat", "jackson-dataformat-smile", "2.9.3");
		addSample("cbor", "com.fasterxml.jackson.dataformat", "jackson-dataformat-cbor", "2.9.3");
		addSample("bson", "de.undercouch", "bson4jackson", "2.9.0");
		addSample("msgpack", "org.msgpack", "msgpack", "0.6.12");
		addSample("ion", "software.amazon.ion", "ion-java", "1.0.3");
		addSample("kryo", "com.esotericsoftware", "kryo", "4.0.1");
	}

	private static final void addSample(String format, String group, String name, String version) {
		dependencySamples.put(format, new String[] { group, name, version });
	}

	static final void suggestDependency(String format) {
		if (format == null || format.isEmpty()) {
			return;
		}
		String[] samples = dependencySamples.get(format.contains("pack") ? "msgpack" : format);
		if (samples == null) {
			return;
		}
		System.err.println("It looks like you are missing some dependencies!");
		System.err.println("Sample Maven dependency:");
		System.err.println();
		System.err.println("<dependency>");
		System.err.println("    <groupId>" + samples[0] + "</groupId>");
		System.err.println("    <artifactId>" + samples[1] + "</artifactId>");
		System.err.println("    <version>" + samples[2] + "</version>");
		System.err.println("</dependency>");
		System.err.println();
		System.err.println("Sample Gradle dependency:");
		System.err.println();
		System.err.println(
				"compile group: '" + samples[0] + "', name: '" + samples[1] + "', version: '" + samples[2] + "'");
		System.err.println();
		System.err.println("...or download the JAR from https://mvnrepository.com/artifact/" + samples[0] + "/" + samples[1]);
		System.err.println();
	}

}