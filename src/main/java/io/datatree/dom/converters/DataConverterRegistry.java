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
package io.datatree.dom.converters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Common Java object converter.<br>
 * <br>
 * Sample - registering an "InetAddress to String" converter:<br>
 * <br>
 * DataConverterRegistry.register(String.class, InetAddress.class, (from) -&gt;
 * { <br>
 * return from.getCanonicalHostName();<br>
 * });<br>
 * <br>
 * Sample - converting InetAddress to String:<br>
 * <br>
 * InetAddress input = ...<br>
 * String output = DataConverterRegistry.convert(String.class, input);
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class DataConverterRegistry extends AbstractConverterSet {

	// --- FROM -> TO CONVERTER MAP ---

	private static final HashMap<DataConverterKey<?, ?>, DataConverter<?, ?>> converters = new HashMap<>(512);

	// --- ANY -> TO CONVERTER MAP ---

	private static final HashMap<Class<?>, DataConverter<?, ?>> defaultConverters = new HashMap<>(128);

	// --- UNQUOTED TYPES ---

	private static final HashSet<Class<?>> unquotedClasses = new HashSet<>(64);

	// --- LOAD CONVERTER SETS ---

	static {

		// Load converters for basic Java types
		try {
			Class.forName("io.datatree.dom.converters.BasicConverterSet");
		} catch (Throwable ignored) {
		}

		// Load converters for MongoDB / BSON object types
		try {
			Class.forName("io.datatree.dom.converters.BsonConverterSet");
		} catch (Throwable ignored) {
		}
	}
	
	// --- PRIVATE CONSTRUCTOR ---

	private DataConverterRegistry() {
	}

	// --- REGISTER CONVERTER ---

	public static final <TO, FROM> void register(Class<TO> to, Class<FROM> from, DataConverter<TO, FROM> converter) {
		converters.put(new DataConverterKey<TO, FROM>(to, from), converter);
	}

	public static final <TO, FROM> void register(Class<TO> to, DataConverter<TO, ?> converter) {
		defaultConverters.put(to, converter);
	}

	// --- UNQUOTED TYPES ---

	public static final void addUnquotedClass(Class<?>... objectClass) {
		unquotedClasses.addAll(Arrays.asList(objectClass));
	}

	public static final boolean isUnquotedClass(Class<?> objectClass) {
		return unquotedClasses.contains(objectClass);
	}

	// --- VALUE CONVERTER ---

	/**
	 * Converts "FROM" object to "TO" format / class.
	 * 
	 * @param <TO>
	 *            target class to convert the source object
	 * @param <FROM>
	 *            source class
	 * @param to
	 *            target type
	 * @param from
	 *            source object
	 * 
	 * @return converted value in "TO" format / class
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <TO, FROM> TO convert(Class<TO> to, FROM from) {

		// Convert null value
		if (from == null || to == null) {
			return null;
		}

		// Convert "null" String to null
		if ("null".equals(from)) {
			return null;
		}

		// Class is same
		if (to == Object.class || from.getClass() == to) {
			return (TO) from;
		}

		// BSON conversion
		boolean isFromMap = from != null && from instanceof Map;
		if (isFromMap) {
			Object subValue = getSubValue((Map) from);
			if (subValue != null) {
				return convert(to, subValue);
			}
		}

		// Convert FROM -> TO
		DataConverterKey<TO, FROM> key = new DataConverterKey<TO, FROM>(to, (Class<FROM>) from.getClass());
		DataConverter<TO, FROM> converter = (DataConverter<TO, FROM>) converters.get(key);
		if (converter != null) {
			return converter.convert(from);
		}

		// Convert any -> TO
		converter = (DataConverter<TO, FROM>) defaultConverters.get(to);
		if (converter != null) {
			return converter.convert(from);
		}

		// Converter not found
		throw new IllegalArgumentException("Unable to convert " + from.getClass() + " to " + to
				+ ", converter not found in DataConverterRegistry!");
	}

	@SuppressWarnings("rawtypes")
	private static final Object getSubValue(Map map) {
		Map.Entry entry;
		for (Object o : map.entrySet()) {
			entry = (Map.Entry) o;
			if (String.valueOf(entry.getKey()).startsWith("$")) {
				return entry.getValue();
			}
		}
		return null;
	}

}