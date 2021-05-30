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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Recursive deep cloner utility.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class DeepCloner {

	// --- IMMUTABLE CLASSES ---

	/**
	 * List of immutable objects (Long, Integer, String, etc.).
	 */
	private static final HashSet<Class<?>> immutableClasses = new HashSet<>(64);

	static {
		addImmutableClass(String.class, Boolean.class, BigDecimal.class, BigInteger.class, InetAddress.class,
				Inet4Address.class, Inet6Address.class, Long.class, Double.class, Float.class, Short.class, Byte.class,
				Integer.class);
	}

	// --- PRIVATE CONSTRUCTOR ---

	private DeepCloner() {
	}

	/**
	 * Adds immutable classes (for faster cloning). This method is not
	 * thread-safe!
	 * 
	 * @param immutableClass
	 *            classes
	 */
	public static final void addImmutableClass(Class<?>... immutableClass) {
		immutableClasses.addAll(Arrays.asList(immutableClass));
	}

	/**
	 * Removes mutable classes from internal Set. This method is not
	 * thread-safe!
	 * 
	 * @param mutableClass
	 *            classes
	 */
	public static final void removeImmutableClass(Class<?>... mutableClass) {
		immutableClasses.removeAll(Arrays.asList(mutableClass));
	}

	// --- VALUE / MAP / LIST / SET CLONER ---

	@SuppressWarnings("unchecked")
	public static final Object clone(Object from) throws Exception {

		// Cloning "null" value
		if (from == null) {
			return null;
		}

		// Cloning Maps
		if (from instanceof Map) {
			Map<Object, Object> fromMap = (Map<Object, Object>) from;
			Map<Object, Object> toMap = new LinkedHashMap<>(fromMap.size() + 1, 1);
			for (Map.Entry<Object, Object> entry : fromMap.entrySet()) {
				toMap.put(entry.getKey(), clone(entry.getValue()));
			}
			return toMap;
		}

		// Cloning collections (Lists and Sets)
		if (from instanceof Collection) {
			Collection<Object> fromCollection = (Collection<Object>) from;
			Collection<Object> toCollection = from instanceof List ? new ArrayList<Object>(fromCollection.size() + 1)
					: new LinkedHashSet<Object>();
			for (Object object : fromCollection) {
				toCollection.add(clone(object));
			}
			return toCollection;
		}

		// "Cloning" immutable objects
		if (immutableClasses.contains(from.getClass())) {
			return from;
		}

		// Cloning byte arrays
		if (from instanceof byte[]) {
			byte[] src = (byte[]) from;
			byte[] dst = new byte[src.length];
			System.arraycopy(src, 0, dst, 0, src.length);
			return dst;
		}

		// Cloning via object serialization
		return deserialize(serialize(from));
	}

	private static final byte[] serialize(Object object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		oos.flush();
		return baos.toByteArray();
	}

	private static final Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
		return ois.readObject();
	}

}