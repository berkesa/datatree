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

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import io.datatree.dom.Cache;
import io.datatree.dom.Config;

/**
 * Utilities for "Basic" and "Bson" converter sets.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
abstract class AbstractConverterSet {

	// --- DATE COVERTER CACHES ---

	private static final Cache<Object, Date> objectToDateCache;
	private static final Cache<Long, String> dateToStringCache;

	// --- DATE PATTERNS ---

	private static final String[] DATE_PATTERNS = new String[] { "yyyy-MM-dd'T'HH:mm:ss.SSSX",
			"EEE MMM dd HH:mm:ss zzz yyyy", "MMM dd, yyyy hh:mm:ss a", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss",
			"yyyy-MM-dd HH:mm z", "yyyy-MM-dd HH:mm zz", "yyyy-MM-dd HH:mm zzz", "yyyy-MM-dd HH:mmX",
			"yyyy-MM-dd HH:mmXX", "yyyy-MM-dd HH:mmXXX", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss z",
			"yyyy-MM-dd HH:mm:ss zz", "yyyy-MM-dd HH:mm:ss zzz", "yyyy-MM-dd HH:mm:ssX", "yyyy-MM-dd HH:mm:ssXX",
			"yyyy-MM-dd HH:mm:ssXXX", "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss.SSS z",
			"yyyy-MM-dd HH:mm:ss.SSS zz", "yyyy-MM-dd HH:mm:ss.SSS zzz", "yyyy-MM-dd HH:mm:ss.SSSX",
			"yyyy-MM-dd HH:mm:ss.SSSXX", "yyyy-MM-dd HH:mm:ss.SSSXXX", "yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd'T'HH:mm z",
			"yyyy-MM-dd'T'HH:mm zz", "yyyy-MM-dd'T'HH:mm zzz", "yyyy-MM-dd'T'HH:mmX", "yyyy-MM-dd'T'HH:mmXX",
			"yyyy-MM-dd'T'HH:mmXXX", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss z", "yyyy-MM-dd'T'HH:mm:ss zz",
			"yyyy-MM-dd'T'HH:mm:ss zzz", "yyyy-MM-dd'T'HH:mm:ssX", "yyyy-MM-dd'T'HH:mm:ssXX",
			"yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ss.SSS z",
			"yyyy-MM-dd'T'HH:mm:ss.SSS zz", "yyyy-MM-dd'T'HH:mm:ss.SSS zzz", "yyyy-MM-dd'T'HH:mm:ss.SSSXX",
			"yyyy-MM-dd'T'HH:mm:ss.SSSXXX", "yyyy-MM-dd", "yyyy-MM-dd z", "yyyy-MM-dd zz", "yyyy-MM-dd zzz",
			"yyyy-MM-ddX", "yyyy-MM-ddXX", "yyyy-MM-ddXXX", };

	static {

		// Init "Date to String" and "String to Date" caches
		objectToDateCache = new Cache<>(Config.CACHE_SIZE);
		dateToStringCache = new Cache<>(Config.CACHE_SIZE);
	}

	// --- PROTECTED CONSTRUCTOR ---

	protected AbstractConverterSet() {
	}

	// --- UTILITY METHODS ---

	protected static final UUID byteArrayToUUID(byte[] from) {
		ByteBuffer b12;
		if (from.length != 16) {
			b12 = ByteBuffer.wrap(Arrays.copyOf(from, 16));
		} else {
			b12 = ByteBuffer.wrap(from);
		}
		long msb = b12.getLong();
		long lsb = b12.getLong();
		return new UUID(msb, lsb);
	}

	protected static final UUID objectToUUID(Object from) {
		String txt = String.valueOf(from);
		if (txt.length() == 36) {
			return UUID.fromString(txt);
		}
		byte[] bytes = new BigInteger(toNumericString(txt, false)).toByteArray();
		if (bytes.length != 16) {
			bytes = Arrays.copyOf(bytes, 16);
		}
		ByteBuffer b12 = ByteBuffer.wrap(bytes);
		long msb = b12.getLong();
		long lsb = b12.getLong();
		return new UUID(msb, lsb);
	}

	protected static final boolean numberStringToBoolean(String number) {
		String num = toNumericString(number, true);
		return !(num.isEmpty() || "0".equals(num) || "0.0".equals(num) || num.indexOf('-') > -1);
	}

	protected static final byte[] numberStringToBytes(String number) {
		BigDecimal num = new BigDecimal(number);
		BigInteger sig = new BigInteger(num.unscaledValue().toString());
		byte[] sigBytes = sig.toByteArray();
		byte[] totalBytes = new byte[sigBytes.length + 4];
		System.arraycopy(sigBytes, 0, totalBytes, 4, sigBytes.length);
		int scale = num.scale();
		totalBytes[0] = (byte) (scale >>> 24);
		totalBytes[1] = (byte) (scale >>> 16);
		totalBytes[2] = (byte) (scale >>> 8);
		totalBytes[3] = (byte) (scale);
		return totalBytes;
	}

	protected static final BigDecimal bytesToBigDecimal(byte[] raw) {
		if (raw.length < 5) {
			return new BigDecimal(new BigInteger(raw));
		}
		int scale = (raw[0] & 0xFF) << 24 | (raw[1] & 0xFF) << 16 | (raw[2] & 0xFF) << 8 | (raw[3] & 0xFF);
		if (scale > Short.MAX_VALUE) {
			return new BigDecimal(new BigInteger(raw));
		}
		byte[] sigBytes = new byte[raw.length - 4];
		System.arraycopy(raw, 4, sigBytes, 0, sigBytes.length);
		BigInteger sig = new BigInteger(sigBytes);
		if (sig.compareTo(BigInteger.ZERO) == 0) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(sig, scale);
	}

	protected static final String toNumericString(String str, boolean enableFractions) {
		if (str.isEmpty()) {
			return "0";
		}
		StringBuilder tmp = new StringBuilder(str.length());
		for (char c : str.toCharArray()) {
			if (c >= '0' && c <= '9') {
				tmp.append(c);
			} else if (c == '.' || c == ',') {
				if (enableFractions) {
					tmp.append('.');
				} else {
					break;
				}
			}
		}
		String number = tmp.toString();
		if (number.isEmpty() || "0".equals(number)) {
			return "0";
		} else if (str.startsWith("-")) {
			return "-" + number;
		}
		return number;
	}

	protected static final LinkedHashMap<Object, Object> toMap(Object from) {
		LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();
		if (from instanceof Collection) {
			int count = 0;
			for (Object value : (Collection<?>) from) {
				map.put("key" + count, value);
			}
		} else if (from instanceof Map) {
			map.putAll((Map<?, ?>) from);
		} else if (from.getClass().isArray()) {
			int len = Array.getLength(from);
			for (int i = 0; i < len; i++) {
				map.put("key" + i, Array.get(from, i));
			}
		} else {
			map.put("key", from);
		}
		return map;
	}

	protected static final LinkedList<Object> toList(Object from) {
		if (from instanceof Collection) {
			return new LinkedList<Object>((Collection<?>) from);
		}
		if (from instanceof Map) {
			return new LinkedList<Object>(((Map<?, ?>) from).values());
		}
		if (from.getClass().isArray()) {
			LinkedList<Object> list = new LinkedList<>();
			int len = Array.getLength(from);
			for (int i = 0; i < len; i++) {
				list.addLast(Array.get(from, i));
			}
			return list;
		}
		return new LinkedList<Object>(Collections.singleton(from));
	}

	protected static final LinkedHashSet<Object> toSet(Object from) {
		if (from instanceof Collection) {
			return new LinkedHashSet<Object>((Collection<?>) from);
		}
		if (from instanceof Map) {
			return new LinkedHashSet<Object>(((Map<?, ?>) from).values());
		}
		if (from.getClass().isArray()) {
			LinkedHashSet<Object> list = new LinkedHashSet<>();
			int len = Array.getLength(from);
			for (int i = 0; i < len; i++) {
				list.add(Array.get(from, i));
			}
			return list;
		}
		return new LinkedHashSet<Object>(Collections.singleton(from));
	}

	protected static final InetAddress toInetAddress(byte[] from) {
		try {
			return InetAddress.getByAddress(from);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Unable to convert byte array to InetAddress!");
		}
	}

	protected static final InetAddress toInetAddress(Object from) {
		if (from instanceof Number) {
			return toInetAddress(((Number) from).longValue());
		}
		Object key = from;
		if (from instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) from;
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				Object object = entry.getKey();
				if (object != null) {
					String test = String.valueOf(object);
					if (test.toLowerCase().contains("hostname")) {
						object = entry.getValue();
						if (object != null) {
							key = object;
							break;
						}
					}
				}
			}
		}
		String ipOrHost = String.valueOf(key);
		int i = ipOrHost.indexOf('/');
		if (i > -1 && i < ipOrHost.length() - 1) {
			ipOrHost = ipOrHost.substring(0, i);
		}
		try {
			return InetAddress.getByName(ipOrHost);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to convert \"" + ipOrHost + "\" to InetAddress!");
		}
	}

	protected static final InetAddress toInetAddress(long number) {
		ByteBuffer b8 = ByteBuffer.allocate(8);
		b8.putLong(number);
		try {
			byte[] ipv4 = new byte[4];
			System.arraycopy(b8.array(), 0, ipv4, 0, 4);
			return InetAddress.getByAddress(ipv4);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Unable to convert \"" + number + "\" to InetAddress!");
		}
	}

	protected static final Date parse(SimpleDateFormat parser, ParsePosition pos, String parsePattern, String txt) {
		String pattern = parsePattern;
		if (parsePattern.endsWith("ZZ")) {
			pattern = pattern.substring(0, pattern.length() - 1);
		}
		parser.applyPattern(pattern);
		pos.setIndex(0);
		String str2 = txt;
		if (parsePattern.endsWith("ZZ")) {
			str2 = txt.replaceAll("([-+][0-9][0-9]):([0-9][0-9])$", "$1$2");
		}
		final Date date = parser.parse(str2, pos);
		if (date != null && pos.getIndex() == str2.length()) {
			return date;
		}
		return null;
	}

	protected static final boolean isBase64String(String txt) {
		if (txt == null) {
			return false;
		}
		int n = txt.length();
		if (n < 4 || n % 4 != 0) {
			return false;
		}
		char[] chars = txt.toCharArray();
		byte e = 0;
		for (n--; n >= 0; n--) {
			if (chars[n] == '=') {
				e++;
				if (e > 3) {
					return false;
				}
			} else {
				break;
			}
		}
		char c;
		for (; n >= 0; n--) {
			c = chars[n];
			if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '+' || c == '/')) {
				return false;
			}
		}
		return true;
	}

	protected static final String dateToString(Date date) {
		long millis = date.getTime();
		String txt = dateToStringCache.get(millis);
		if (txt == null) {
			if (Config.USE_TIMESTAMPS) {
				synchronized (Config.TIMESTAMP_FORMATTER) {
					txt = Config.TIMESTAMP_FORMATTER.format(date);
				}
			} else {
				txt = Long.toString(date.getTime());
			}
			dateToStringCache.put(millis, txt);
		}
		return txt;
	}

	protected static final Date longToDate(long millis) {
		Date date = objectToDateCache.get(millis);
		if (date != null) {
			return date;
		}
		date = new Date(millis);
		objectToDateCache.put(millis, date);
		return date;
	}

	protected static final Date objectToDate(Object from) {

		// "now"
		String txt = String.valueOf(from);
		if (txt.equalsIgnoreCase("now")) {
			return new Date();
		}

		// Find in cache
		Date date = objectToDateCache.get(from);
		if (date != null) {
			return date;
		}

		// Milliseconds since epoch?
		boolean isNumber = true;
		for (char c : txt.toCharArray()) {
			if (!Character.isDigit(c) && c != '.') {
				isNumber = false;
				break;
			}
		}
		if (isNumber) {
			date = new Date(Long.parseLong(toNumericString(txt, false)));
			objectToDateCache.put(from, date);
			return date;
		}

		// Formatted text to date (full date)
		SimpleDateFormat parser = new SimpleDateFormat("", Locale.US);
		parser.setTimeZone(TimeZone.getTimeZone(Config.DEFAULT_TIME_ZONE));
		ParsePosition pos = new ParsePosition(0);
		for (String parsePattern : DATE_PATTERNS) {
			date = parse(parser, pos, parsePattern, txt);
			if (date != null) {
				objectToDateCache.put(from, date);
				return date;
			}
		}

		// Formatted text to date (time only)
		HashSet<String> testedPatterns = new HashSet<String>();
		for (String parsePattern : DATE_PATTERNS) {
			int i = parsePattern.indexOf("HH");
			if (i == -1) {
				continue;
			}
			parsePattern = parsePattern.substring(i);
			if (testedPatterns.contains(parsePattern)) {
				continue;
			}
			testedPatterns.add(parsePattern);
			date = parse(parser, pos, parsePattern, txt);
			if (date != null) {
				objectToDateCache.put(from, date);
				return date;
			}
		}

		// Milliseconds since epoch
		date = new Date(Long.parseLong(toNumericString(txt, false)));
		objectToDateCache.put(from, date);
		return date;
	}

}