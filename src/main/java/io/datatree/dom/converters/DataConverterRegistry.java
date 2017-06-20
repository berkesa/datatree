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

/**
 * Common Java object converter.<br>
 * <br>
 * Sample - registering an "InetAddress to String" converter:<br>
 * <br>
 * DataConverterRegistry.register(String.class, InetAddress.class, (from) -> {
 * <br>
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

	// --- PRIVATE CONSTRUCTOR ---

	private DataConverterRegistry() {
	}

	// --- FROM -> TO CONVERTER MAP ---

	private static final HashMap<DataConverterKey<?, ?>, DataConverter<?, ?>> converters = new HashMap<>(512);

	public static final <TO, FROM> void register(Class<TO> to, Class<FROM> from, DataConverter<TO, FROM> converter) {
		converters.put(new DataConverterKey<TO, FROM>(to, from), converter);
	}

	// --- ANY -> TO CONVERTER MAP ---

	private static HashMap<Class<?>, DataConverter<?, ?>> defaultConverters = new HashMap<>(128);

	public static final <TO, FROM> void register(Class<TO> to, DataConverter<TO, ?> converter) {
		defaultConverters.put(to, converter);
	}
	
	// --- UNQUOTED TYPES ---
	
	private static HashSet<Class<?>> unquotedClasses = new HashSet<>(64);

	public static final void addUnquotedClass(Class<?>... objectClass) {
		unquotedClasses.addAll(Arrays.asList(objectClass));
	}
	
	public static final boolean isUnquotedClass(Class<?> objectClass) {
		return unquotedClasses.contains(objectClass);
	}
	
	// --- LOAD CONVERTER SETS ---

	static {

		// Load converters for basic Java types
		try {
			Class.forName("io.datatree.dom.converters.BasicConverterSet");
		} catch (Exception ignored) {
		}

		// Load converters for MongoDB / BSON object types
		try {
			Class.forName("io.datatree.dom.converters.BsonConverterSet");
		} catch (Exception ignored) {
		}
	}

	// --- VALUE CONVERTER ---

	/**
	 * Converts "FROM" object to "TO" format / class.
	 * 
	 * @param to
	 *            target type (class)
	 * @param from
	 *            source object
	 * 
	 * @return converted value in "TO" format / class
	 */
	@SuppressWarnings("unchecked")
	public static final <TO, FROM> TO convert(Class<TO> to, FROM from) {

		// Convert "null" value
		if (from == null || to == null) {
			return null;
		}

		// Class is same
		if (to == Object.class || from.getClass() == to) {
			return (TO) from;
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

}