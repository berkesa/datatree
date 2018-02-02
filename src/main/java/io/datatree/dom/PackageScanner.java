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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Package scanner.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public final class PackageScanner {

	// --- SCAN PACKAGES ---

	private static final AtomicBoolean scanned = new AtomicBoolean();

	private static final HashMap<String, LinkedHashSet<String>> readers = new HashMap<>();
	private static final HashMap<String, LinkedHashSet<String>> writers = new HashMap<>();

	static final Set<String> getReadersByFormat(String format) {
		LinkedHashSet<String> set = readers.get(format);
		if (set == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(set);
	}

	static final Set<String> getWritersByFormat(String format) {
		LinkedHashSet<String> set = writers.get(format);
		if (set == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(set);
	}

	static synchronized final String findByFormat(String format, boolean isReader) {
		if (scanned.compareAndSet(false, true)) {

			// Get scanned packages
			if (Config.ADAPTER_PACKAGES != null && !Config.ADAPTER_PACKAGES.isEmpty()) {

				// Add default packages
				HashSet<String> packageNames = new HashSet<>();
				packageNames.add("io.datatree.dom.builtin");
				packageNames.add("io.datatree.dom.adapters");
				String[] userPackages = Config.ADAPTER_PACKAGES.split(",");
				for (String userPackage : userPackages) {
					userPackage = userPackage.trim();
					if (!userPackage.isEmpty()) {
						packageNames.add(userPackage);
					}
				}

				// Package scan
				for (String packageName : packageNames) {
					try {
						LinkedList<String> classNames = scan(packageName);
						for (String className : classNames) {
							tryToLoad(packageName + '.' + className);
						}
					} catch (Exception cause) {
						cause.printStackTrace();
					}
				}
			} else {

				// Try to load default adapters
				tryToLoadAll("io.datatree.dom.builtin", "DebugBuiltin", "JavaBuiltin", "JsonBuiltin");
				tryToLoadAll("io.datatree.dom.adapters", "BsonJackson", "CborJackson", "CsvOpenCSV", "IonIon",
						"JsonBoon", "JsonBson", "JsonDSL", "JsonFast", "JsonFlex", "JsonGenson", "JsonGson", "JsonIon",
						"JsonJackson", "JsonJodd", "JsonJohnzon", "JsonJsonIO", "JsonNano", "JsonSimple", "JsonSmart",
						"JsonSojo", "JsonUtil", "KryoKryo", "MsgPackJackson", "MsgPackOrg", "PropertiesBuiltin",
						"PropertiesJackson", "SmileJackson", "TomlJtoml", "TomlJtoml2", "TomlToml4j", "TsvOpenCSV",
						"XmlBuiltin", "XmlJackson", "XmlRpcSojo", "XmlXStream", "YamlJackson", "YamlSnakeYaml");
			}

			// Sort reader and writers by priority
			sortByPriority(readers);
			sortByPriority(writers);
		}

		// Get reader / writer by format
		HashMap<String, LinkedHashSet<String>> map = isReader ? readers : writers;
		HashSet<String> implementation = map.get(format);
		if (implementation != null && !implementation.isEmpty()) {
			return implementation.iterator().next();
		}

		// Get reader / writer by class name
		for (HashSet<String> classNames : map.values()) {
			for (String className : classNames) {
				int i = className.lastIndexOf('.');
				String test = i > -1 ? className.substring(i + 1) : className;
				if (format.equalsIgnoreCase(test)) {
					return className;
				}
			}
		}

		// Not found
		return null;
	}

	private static final void tryToLoadAll(String packageName, String... classNames) {
		for (String className : classNames) {
			tryToLoad(packageName + '.' + className);
		}
	}

	private static final void tryToLoad(String className) {
		try {
			Object o = Class.forName(className).newInstance();
			if (o instanceof TreeReader) {
				TreeReader reader = (TreeReader) o;
				String formatName = reader.getFormat();
				LinkedHashSet<String> set = readers.get(formatName);
				if (set == null) {
					set = new LinkedHashSet<>();
					readers.put(formatName, set);
				}
				set.add(reader.getClass().getName());
			}
			if (o instanceof TreeWriter) {
				TreeWriter writer = (TreeWriter) o;
				String formatName = writer.getFormat();
				LinkedHashSet<String> set = writers.get(formatName);
				if (set == null) {
					set = new LinkedHashSet<>();
					writers.put(formatName, set);
				}
				set.add(writer.getClass().getName());
			}
		} catch (Throwable ignored) {

			// Class not loadable (eg. protected)
		}
	}

	private static final void sortByPriority(HashMap<String, LinkedHashSet<String>> map) {
		for (LinkedHashSet<String> set : map.values()) {
			try {
				ArrayList<Class<?>> classes = new ArrayList<>();
				for (String className : set) {
					classes.add(Class.forName(className));
				}
				classes.sort((c1, c2) -> {
					Priority p1 = c1.getAnnotation(Priority.class);
					Priority p2 = c2.getAnnotation(Priority.class);
					int v1 = p1 == null ? 1000 : p1.value();
					int v2 = p2 == null ? 1000 : p2.value();
					return v2 - v1;
				});
				set.clear();
				for (Class<?> c : classes) {
					set.add(c.getName());
				}
			} catch (Throwable cause) {
				cause.printStackTrace();
			}
		}
	}

	public static final LinkedList<String> scan(String packageName) throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		LinkedList<String> names = new LinkedList<>();
		packageName = packageName.replace('.', '/');
		URL packageURL = classLoader.getResource(packageName);
		if (packageURL == null) {
			String classPath = System.getProperty("java.class.path");
			String separator = System.getProperty("path.separator");
			if (classPath != null && separator != null) {
				String[] paths = classPath.split(separator);
				for (String path : paths) {
					if (path.toLowerCase().endsWith(".jar")) {
						scanJar(path, packageName, names);
					} else {
						File folder = new File(path, packageName);
						if (folder.isDirectory()) {
							scanDir(folder, names);
						}
					}
					if (!names.isEmpty()) {
						return names;
					}
				}
			}
			return names;
		}
		if (packageURL.getProtocol().equals("jar")) {

			String jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
			jarFileName = jarFileName.substring(5, jarFileName.indexOf("!"));
			scanJar(jarFileName, packageName, names);

		} else {

			URI uri = new URI(packageURL.toString());
			scanDir(new File(uri.getPath()), names);

		}
		return names;
	}

	private static final void scanDir(File folder, LinkedList<String> names) {
		File[] files = folder.listFiles();
		String entryName;
		for (File actual : files) {
			entryName = actual.getName();
			if (entryName.endsWith(".class")) {
				entryName = entryName.substring(0, entryName.lastIndexOf('.'));
				if (entryName.indexOf('$') == -1) {
					names.add(entryName);
				}
			}
		}
	}

	private static final void scanJar(String jarFileName, String packageName, LinkedList<String> names) {
		JarFile jar = null;
		try {
			jar = new JarFile(jarFileName);
			Enumeration<JarEntry> jarEntries = jar.entries();
			while (jarEntries.hasMoreElements()) {
				String entryName = jarEntries.nextElement().getName();
				if (entryName.startsWith(packageName) && entryName.endsWith(".class")) {
					entryName = entryName.substring(packageName.length() + 1, entryName.lastIndexOf('.'));
					if (entryName.indexOf('$') == -1 && entryName.indexOf('/') == -1) {
						names.add(entryName);
					}
				}
			}
		} catch (Exception ignored) {
		} finally {
			if (jar != null) {
				try {
					jar.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

}