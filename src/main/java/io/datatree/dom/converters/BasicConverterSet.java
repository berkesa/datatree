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

import static io.datatree.dom.converters.DataConverterRegistry.register;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.datatree.dom.BASE64;
import io.datatree.dom.Config;

/**
 * Converters for basic Java types.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
final class BasicConverterSet extends AbstractConverterSet {

	// --- PRIVATE CONSTRUCTOR ---

	private BasicConverterSet() {
	}

	// --- INIT DEFAULT CONVERTERS ---

	static {

		// --- VALUE TO STRING CONVERTERS ---

		register(String.class, (from) -> {
			return String.valueOf(from);
		});
		register(String.class, byte[].class, (from) -> {
			return BASE64.encode(from);
		});
		register(String.class, Date.class, (from) -> {
			return dateToString(from);
		});
		register(String.class, Boolean.class, (from) -> {
			return from ? "true" : "false";
		});

		register(String.class, InetAddress.class, (from) -> {
			return from.getCanonicalHostName();
		});
		register(String.class, Inet4Address.class, (from) -> {
			return from.getCanonicalHostName();
		});
		register(String.class, Inet6Address.class, (from) -> {
			return from.getCanonicalHostName();
		});

		// --- VALUE TO BYTE ARRAY CONVERTERS ---

		register(byte[].class, (from) -> {
			if (from instanceof Number) {
				return numberStringToBytes(String.valueOf(from));
			}
			if (from instanceof Collection) {
				Collection<?> c = (Collection<?>) from;
				byte[] bytes = new byte[c.size()];
				int idx = 0;
				for (Object o : c) {
					bytes[idx++] = DataConverterRegistry.convert(Byte.class, o);
				}
				return bytes;
			}
			if (from.getClass().isArray()) {
				int len = Array.getLength(from);
				byte[] bytes = new byte[len];
				for (int idx = 0; idx < len; idx++) {
					Object o = Array.get(from, idx);
					bytes[idx] = DataConverterRegistry.convert(Byte.class, o);
				}
				return bytes;
			}			
			String txt = String.valueOf(from);
			if (isBase64String(txt)) {
				return BASE64.decode(txt);
			}
			return txt.getBytes(StandardCharsets.UTF_8);
		});
		register(byte[].class, UUID.class, (from) -> {
			ByteBuffer b12 = ByteBuffer.allocate(16);
			b12.putLong(from.getMostSignificantBits());
			b12.putLong(from.getLeastSignificantBits());
			return b12.array();
		});
		register(byte[].class, Boolean.class, (from) -> {
			return from ? new byte[] { 1 } : new byte[] { 0 };
		});
		register(byte[].class, Byte.class, (from) -> {
			return new byte[] { from };
		});
		register(byte[].class, Date.class, (from) -> {
			return new BigInteger(Long.toString(from.getTime())).toByteArray();
		});
		register(byte[].class, InetAddress.class, (from) -> {
			return from.getAddress();
		});
		register(byte[].class, Inet4Address.class, (from) -> {
			return from.getAddress();
		});
		register(byte[].class, Inet6Address.class, (from) -> {
			return from.getAddress();
		});

		// --- VALUE TO BYTE CONVERTERS ---

		register(Byte.class, (from) -> {
			if (from instanceof Number) {
				return ((Number) from).byteValue();
			}
			return (byte) Long.parseLong(toNumericString(String.valueOf(from), false));
		});
		register(Byte.class, Boolean.class, (from) -> {
			return from ? (byte) 1 : (byte) 0;
		});
		register(Byte.class, Date.class, (from) -> {
			return (byte) from.getTime();
		});
		register(Byte.class, byte[].class, (from) -> {
			return from.length > 0 ? from[0] : 0;
		});
		register(Byte.class, UUID.class, (from) -> {
			return (byte) from.getMostSignificantBits();
		});

		register(Byte.class, InetAddress.class, (from) -> {
			return from.getAddress()[0];
		});
		register(Byte.class, Inet4Address.class, (from) -> {
			return from.getAddress()[0];
		});
		register(Byte.class, Inet6Address.class, (from) -> {
			return from.getAddress()[0];
		});

		// --- VALUE TO SHORT CONVERTERS ---

		register(Short.class, (from) -> {
			if (from instanceof Number) {
				return ((Number) from).shortValue();
			}
			return Short.parseShort(toNumericString(String.valueOf(from), false));
		});
		register(Short.class, Date.class, (from) -> {
			return (short) from.getTime();
		});
		register(Short.class, Boolean.class, (from) -> {
			return from ? (short) 1 : (short) 0;
		});
		register(Short.class, byte[].class, (from) -> {
			return bytesToBigDecimal(from).shortValue();
		});
		register(Short.class, UUID.class, (from) -> {
			return (short) from.getMostSignificantBits();
		});

		register(Short.class, InetAddress.class, (from) -> {
			return new BigInteger(from.getAddress()).shortValue();
		});
		register(Short.class, Inet4Address.class, (from) -> {
			return new BigInteger(from.getAddress()).shortValue();
		});
		register(Short.class, Inet6Address.class, (from) -> {
			return new BigInteger(from.getAddress()).shortValue();
		});

		// --- VALUE TO INTEGER CONVERTERS ---

		register(Integer.class, (from) -> {
			if (from instanceof Number) {
				return ((Number) from).intValue();
			}
			return Integer.parseInt(toNumericString(String.valueOf(from), false));
		});
		register(Integer.class, Date.class, (from) -> {
			return (int) from.getTime();
		});
		register(Integer.class, Boolean.class, (from) -> {
			return from ? 1 : 0;
		});
		register(Integer.class, byte[].class, (from) -> {
			return bytesToBigDecimal(from).intValue();
		});
		register(Integer.class, UUID.class, (from) -> {
			return (int) from.getMostSignificantBits();
		});

		register(Integer.class, InetAddress.class, (from) -> {
			return new BigInteger(from.getAddress()).intValue();
		});
		register(Integer.class, Inet4Address.class, (from) -> {
			return new BigInteger(from.getAddress()).intValue();
		});
		register(Integer.class, Inet6Address.class, (from) -> {
			return new BigInteger(from.getAddress()).intValue();
		});

		// --- VALUE TO LONG CONVERTERS ---

		register(Long.class, (from) -> {
			if (from instanceof Number) {
				return ((Number) from).longValue();
			}
			return Long.parseLong(toNumericString(String.valueOf(from), false));
		});
		register(Long.class, Date.class, (from) -> {
			return from.getTime();
		});
		register(Long.class, Boolean.class, (from) -> {
			return from ? 1l : 0l;
		});
		register(Long.class, byte[].class, (from) -> {
			return bytesToBigDecimal(from).longValue();
		});
		register(Long.class, UUID.class, (from) -> {
			return from.getMostSignificantBits();
		});

		register(Long.class, InetAddress.class, (from) -> {
			return new BigInteger(from.getAddress()).longValue();
		});
		register(Long.class, Inet4Address.class, (from) -> {
			return new BigInteger(from.getAddress()).longValue();
		});
		register(Long.class, Inet6Address.class, (from) -> {
			return new BigInteger(from.getAddress()).longValue();
		});

		// --- VALUE TO FLOAT CONVERTERS ---

		register(Float.class, (from) -> {
			if (from instanceof Number) {
				return ((Number) from).floatValue();
			}
			return Float.parseFloat(toNumericString(String.valueOf(from), true));
		});
		register(Float.class, Date.class, (from) -> {
			return (float) from.getTime();
		});
		register(Float.class, Boolean.class, (from) -> {
			return from ? 1f : 0f;
		});
		register(Float.class, byte[].class, (from) -> {
			return bytesToBigDecimal(from).floatValue();
		});
		register(Float.class, UUID.class, (from) -> {
			return (float) from.getMostSignificantBits();
		});

		register(Float.class, InetAddress.class, (from) -> {
			return new BigInteger(from.getAddress()).floatValue();
		});
		register(Float.class, Inet4Address.class, (from) -> {
			return new BigInteger(from.getAddress()).floatValue();
		});
		register(Float.class, Inet6Address.class, (from) -> {
			return new BigInteger(from.getAddress()).floatValue();
		});

		// --- VALUE TO DOUBLE CONVERTERS ---

		register(Double.class, (from) -> {
			if (from instanceof Number) {
				return ((Number) from).doubleValue();
			}
			return Double.parseDouble(toNumericString(String.valueOf(from), true));
		});
		register(Double.class, Float.class, (from) -> {
			return Double.parseDouble(Float.toString(from));
		});
		register(Double.class, Date.class, (from) -> {
			return (double) from.getTime();
		});
		register(Double.class, Boolean.class, (from) -> {
			return from ? 1d : 0d;
		});
		register(Double.class, byte[].class, (from) -> {
			return bytesToBigDecimal(from).doubleValue();
		});
		register(Double.class, UUID.class, (from) -> {
			return (double) from.getMostSignificantBits();
		});

		register(Double.class, InetAddress.class, (from) -> {
			return new BigInteger(from.getAddress()).doubleValue();
		});
		register(Double.class, Inet4Address.class, (from) -> {
			return new BigInteger(from.getAddress()).doubleValue();
		});
		register(Double.class, Inet6Address.class, (from) -> {
			return new BigInteger(from.getAddress()).doubleValue();
		});

		// --- VALUE TO BIGINTEGER CONVERTERS ---

		register(BigInteger.class, (from) -> {
			return new BigInteger(toNumericString(String.valueOf(from), false));
		});
		register(BigInteger.class, Date.class, (from) -> {
			return new BigInteger(Long.toString(from.getTime()));
		});
		register(BigInteger.class, Boolean.class, (from) -> {
			return from ? BigInteger.ONE : BigInteger.ZERO;
		});
		register(BigInteger.class, byte[].class, (from) -> {
			return bytesToBigDecimal(from).toBigInteger();
		});
		register(BigInteger.class, UUID.class, (from) -> {
			ByteBuffer b12 = ByteBuffer.allocate(12);
			b12.putLong(from.getMostSignificantBits());
			b12.putInt((int) from.getLeastSignificantBits());
			return new BigInteger(b12.array());
		});

		register(BigInteger.class, InetAddress.class, (from) -> {
			return new BigInteger((from).getAddress());
		});
		register(BigInteger.class, Inet4Address.class, (from) -> {
			return new BigInteger((from).getAddress());
		});
		register(BigInteger.class, Inet6Address.class, (from) -> {
			return new BigInteger((from).getAddress());
		});

		// --- VALUE TO BIGDECIMAL CONVERTERS ---

		register(BigDecimal.class, (from) -> {
			return new BigDecimal(toNumericString(String.valueOf(from), true));
		});
		register(BigDecimal.class, Double.class, (from) -> {
			return new BigDecimal(Double.toString(from));
		});
		register(BigDecimal.class, Float.class, (from) -> {
			return new BigDecimal(Float.toString(from));
		});
		register(BigDecimal.class, Byte.class, (from) -> {
			return new BigDecimal(from);
		});
		register(BigDecimal.class, Short.class, (from) -> {
			return new BigDecimal(from);
		});
		register(BigDecimal.class, Integer.class, (from) -> {
			return new BigDecimal(from);
		});
		register(BigDecimal.class, Long.class, (from) -> {
			return new BigDecimal(from);
		});
		register(BigDecimal.class, BigInteger.class, (from) -> {
			return new BigDecimal(from);
		});
		register(BigDecimal.class, Boolean.class, (from) -> {
			return from ? BigDecimal.ONE : BigDecimal.ZERO;
		});
		register(BigDecimal.class, Date.class, (from) -> {
			return new BigDecimal(from.getTime());
		});
		register(BigDecimal.class, byte[].class, (from) -> {
			return bytesToBigDecimal(from);
		});
		register(BigDecimal.class, UUID.class, (from) -> {
			ByteBuffer b12 = ByteBuffer.allocate(16);
			b12.putLong(from.getMostSignificantBits());
			b12.putLong(from.getLeastSignificantBits());
			return new BigDecimal(new BigInteger(b12.array()));
		});

		register(BigDecimal.class, InetAddress.class, (from) -> {
			return new BigDecimal(new BigInteger(from.getAddress()));
		});
		register(BigDecimal.class, Inet4Address.class, (from) -> {
			return new BigDecimal(new BigInteger(from.getAddress()));
		});
		register(BigDecimal.class, Inet6Address.class, (from) -> {
			return new BigDecimal(new BigInteger(from.getAddress()));
		});

		// --- VALUE TO BOOLEAN CONVERTERS ---

		register(Boolean.class, (from) -> {
			return numberStringToBoolean(String.valueOf(from));
		});
		register(Boolean.class, String.class, (from) -> {
			from = from.toLowerCase();
			if ("true".equals(from) || "yes".equals(from) || "on".equals(from)) {
				return true;
			}
			if ("false".equals(from) || "no".equals(from) || "off".equals(from)) {
				return false;
			}
			from = toNumericString(String.valueOf(from), true);
			return !(from.isEmpty() || from.equals("0") || from.contains("-"));
		});
		register(Boolean.class, Date.class, (from) -> {
			return from.getTime() > 0;
		});
		register(Boolean.class, UUID.class, (from) -> {
			return from.getMostSignificantBits() > 0 || from.getLeastSignificantBits() > 0;
		});
		register(Boolean.class, byte[].class, (from) -> {
			for (byte b : from) {
				if (b > 0) {
					return true;
				}
			}
			return false;
		});
		register(Boolean.class, InetAddress.class, (from) -> {
			byte[] bytes = from.getAddress();
			for (byte b : bytes) {
				if (b > 0) {
					return true;
				}
			}
			return false;
		});

		// --- VALUE TO DATE CONVERTERS ---

		register(Date.class, (from) -> {
			return objectToDate(from);
		});
		register(Date.class, Double.class, (from) -> {
			return longToDate(from.longValue());
		});
		register(Date.class, Float.class, (from) -> {
			return longToDate(from.longValue());
		});
		register(Date.class, Byte.class, (from) -> {
			return longToDate(from);
		});
		register(Date.class, Short.class, (from) -> {
			return longToDate(from);
		});
		register(Date.class, Integer.class, (from) -> {
			return longToDate(from);
		});
		register(Date.class, Long.class, (from) -> {
			return longToDate(from);
		});
		register(Date.class, BigInteger.class, (from) -> {
			return longToDate(from.longValue());
		});
		register(Date.class, Boolean.class, (from) -> {
			return longToDate(from ? 1 : 0);
		});
		register(Date.class, byte[].class, (from) -> {
			return longToDate(new BigInteger(from).longValue());
		});
		register(Date.class, UUID.class, (from) -> {
			return longToDate(from.getMostSignificantBits());
		});
		register(Date.class, InetAddress.class, (from) -> {
			return longToDate(new BigInteger(from.getAddress()).longValue());
		});
		register(Date.class, Inet4Address.class, (from) -> {
			return longToDate(new BigInteger(from.getAddress()).longValue());
		});
		register(Date.class, Inet6Address.class, (from) -> {
			return longToDate(new BigInteger(from.getAddress()).longValue());
		});

		// --- VALUE TO UUID CONVERTERS ---

		register(UUID.class, (from) -> {
			return objectToUUID(from);
		});
		register(UUID.class, byte[].class, (from) -> {
			return byteArrayToUUID(from);
		});
		register(UUID.class, Boolean.class, (from) -> {
			return new UUID(from ? 1 : 0, 0);
		});

		// --- VALUE TO INETADDRESS CONVERTERS ---

		register(InetAddress.class, (from) -> {
			return toInetAddress(from);
		});
		register(InetAddress.class, byte[].class, (from) -> {
			return toInetAddress(from);
		});
		register(InetAddress.class, UUID.class, (from) -> {
			return toInetAddress(from.getMostSignificantBits());
		});
		register(InetAddress.class, Date.class, (from) -> {
			return toInetAddress(from.getTime());
		});

		register(Inet4Address.class, (from) -> {
			return (Inet4Address) toInetAddress(from);
		});
		register(Inet4Address.class, byte[].class, (from) -> {
			return (Inet4Address) toInetAddress(from);
		});
		register(Inet4Address.class, UUID.class, (from) -> {
			return (Inet4Address) toInetAddress(from.getMostSignificantBits());
		});
		register(Inet4Address.class, Date.class, (from) -> {
			return (Inet4Address) toInetAddress(from.getTime());
		});

		register(Inet6Address.class, (from) -> {
			return (Inet6Address) toInetAddress(from);
		});
		register(Inet6Address.class, byte[].class, (from) -> {
			return (Inet6Address) toInetAddress(from);
		});
		register(Inet6Address.class, UUID.class, (from) -> {
			return (Inet6Address) toInetAddress(from.getMostSignificantBits());
		});
		register(Inet6Address.class, Date.class, (from) -> {
			return (Inet6Address) toInetAddress(from.getTime());
		});

		// --- VALUE TO OBJECT ARRAY CONVERTERS ---

		register(Object[].class, (from) -> {
			return toList(from).toArray();
		});
		
		// --- VALUE TO SET CONVERTERS ---

		register(Set.class, (from) -> {
			return toSet(from);
		});
		register(HashSet.class, (from) -> {
			return toSet(from);
		});
		register(LinkedHashSet.class, (from) -> {
			return toSet(from);
		});

		// --- VALUE TO LIST CONVERTERS ---

		register(Collection.class, (from) -> {
			return toList(from);
		});
		register(List.class, (from) -> {
			return toList(from);
		});
		register(LinkedList.class, (from) -> {
			return toList(from);
		});

		// --- VALUE TO MAP CONVERTERS ---

		register(Map.class, (from) -> {
			return toMap(from);
		});
		register(HashMap.class, (from) -> {
			return toMap(from);
		});
		register(LinkedHashMap.class, (from) -> {
			return toMap(from);
		});
		
		// --- JSON FORMATTING ---

		if (!Config.USE_TIMESTAMPS) {
			DataConverterRegistry.addUnquotedClass(Date.class);
		}
	}
		
}