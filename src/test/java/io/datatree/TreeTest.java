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
package io.datatree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonRegularExpression;
import org.bson.BsonString;
import org.bson.BsonTimestamp;
import org.bson.BsonUndefined;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.bson.types.Symbol;
import org.junit.Test;

import io.datatree.dom.Config;
import io.datatree.dom.TreeReaderRegistry;
import io.datatree.dom.TreeWriterRegistry;
import io.datatree.dom.builtin.JsonBuiltin;
import junit.framework.TestCase;

/**
 * Tree JUnit test cases.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class TreeTest extends TestCase {

	// --- SUPPORTED DATE / TIME FORMATS ---

	protected static final String[] DATE_PATTERNS = new String[] { "yyyy-MM-dd'T'HH:mm:ss.SSSX",
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

	// --- MINIMALIST JSON TEMPLATE ---

	private static final String JSON = "{\"a\":{\"b\":{\"c\":{\"d\":[1,2,3]}}}}";

	// --- CREATE AN EMPTY DATA STRUCTURE ---

	static {
		JsonBuiltin impl = new JsonBuiltin();
		TreeReaderRegistry.setReader("json", impl);
		TreeWriterRegistry.setWriter("json", impl);
	}

	@SuppressWarnings("resource")
	@Test
	public void testFileConstructors() throws Exception {
		File f = null;
		try {
			Tree t1 = new Tree();
			t1.put("a", 1).put("b", "2").put("c", true);

			f = File.createTempFile("tmp", ".json");
			t1.writeTo(f);
			
			assertEquals(t1, new Tree(f));
			assertEquals(t1, new Tree(f, "json"));
			
			assertEquals(t1, new Tree(new FileInputStream(f)));
			assertEquals(t1, new Tree(new FileInputStream(f), "json"));
			assertEquals(t1, new Tree(new FileInputStream(f), "json", true));
			
			assertEquals(t1, new Tree(new FileInputStream(f).getChannel()));
			assertEquals(t1, new Tree(new FileInputStream(f).getChannel(), "json"));
			assertEquals(t1, new Tree(new FileInputStream(f).getChannel(), "json", true));

			t1.getMeta().put("x", "y");
			
			f.delete();
			t1.writeTo(new FileOutputStream(f));
			assertEquals(t1, new Tree(f));
			t1.writeTo(new FileOutputStream(f), "json");
			Tree t3 = new Tree(f);
			assertFalse(t3.hasMeta());
			assertNull(t3.getMeta().get("x", (String) null));
			assertEquals(t1, t3);
			t1.writeTo(new FileOutputStream(f), "json", true, true);
			Tree t2 = new Tree(f);
			assertEquals("y", t2.getMeta().get("x", ""));
			assertEquals(t1, t2);
			
			f.delete();
			t1.writeTo(new FileOutputStream(f).getChannel());
			assertEquals(t1, new Tree(f));
			t1.writeTo(new FileOutputStream(f).getChannel(), "json");
			Tree t4 = new Tree(f);
			assertFalse(t4.hasMeta());
			assertNull(t4.getMeta().get("x", (String) null));
			assertEquals(t1, t4);
			t1.writeTo(new FileOutputStream(f).getChannel(), null, true, true);
			Tree t5 = new Tree(f);
			assertEquals("y", t5.getMeta().get("x", ""));
			assertEquals(t1, t2);
			
			f.delete();
			t1.writeTo(f.getAbsolutePath());
			assertEquals(t1, new Tree(f));
			
		} finally {
			if (f != null) {
				f.delete();
			}
		}
	}
	
	@Test
	public void testGetObjectWithNullDefault() throws Exception {
		Tree t = new Tree();
		t.put("xyz", "abc");
		Object o = t.getObject("xyz", null);
		assertEquals("abc", o);
	}
	
	@Test
	public void testCopyFrom() throws Exception {
		Tree s = new Tree();
		for (int i = 0; i < 10; i++) {
			s.put("k" + i, "v" + i);
		}
		Tree d = new Tree();
		d.copyFrom(s, "k1", "k3", "k6", "k11");
		assertEquals(3, d.size());
		assertTrue(d.isMap());
		assertEquals("v1", d.get("k1", ""));
		assertEquals("v3", d.get("k3", ""));
		assertEquals("v6", d.get("k6", ""));
	}
	
	@Test
	public void testSpecChars() throws Exception {
	  String test = "_\"_\r_\n_\t_\b_\f_\\_";
	  Tree t = new Tree();
	  t.put("test", test);
	  Tree t2 = new Tree(t.toString());
	  assertEquals(t, t2);
	  String test2 = t2.get("test", "");
	  assertEquals(test, test2);
	}
	
	@Test
	public void testConstructor() throws Exception {
		isEmptyTree(new Tree());
		isEmptyTree(new Tree((String) null));
		isEmptyTree(new Tree(""));
		isEmptyTree(new Tree("{}"));
		isEmptyTree(new Tree("{}".getBytes()));
		isEmptyTree(new Tree("{}".getBytes(), "json"));
		isEmptyTree(new Tree(new HashMap<String, Object>()));
		isEmptyTree(new Tree((Map<String, Object>) null));
		isEmptyTree(new Tree((String) null, "json"));
		isEmptyTree(new Tree((Object) null, (Object) null));
		isEmptyTree(new Tree("", "json"));
		isEmptyTree(new Tree((byte[]) null, "json"));
		isEmptyTree(new Tree(new byte[0], "json"));
	}

	private final void isEmptyTree(Tree t) throws Exception {
		assertNotNull(t);
		assertTrue(t.isEmpty());
		assertTrue(t.isStructure());
		assertFalse(t.isList());
		assertFalse(t.isSet());
		assertTrue(t.isMap());
		assertFalse(t.isPrimitive());
		assertEquals(0, t.size());
		assertJsonEquals("{}", t.toString(false));
	}

	// --- GET PATH ---

	@Test
	public void testGetPath() throws Exception {
		Tree t = new Tree();
		t.put("abc", 1);
		String p = t.getFirstChild().getPath();
		assertEquals("abc", p);
		t.put("a.b.c", 2);
		p = t.get("a.b.c").getPath();
		assertEquals("a.b.c", p);
	}

	// --- GET NEXT / PREV ---

	@Test
	public void testSiblings() throws Exception {
		Tree t = new Tree();
		t.put("a", 1);
		t.put("b", 2);
		t.put("c", 3);
		Tree x = t.getFirstChild();
		assertEquals(1, (int) x.asInteger());
		x = x.getNextSibling();
		assertEquals(2, (int) x.asInteger());
		x = x.getNextSibling();
		assertEquals(3, (int) x.asInteger());
		x = x.getPreviousSibling();
		assertEquals(2, (int) x.asInteger());
		x = x.getPreviousSibling();
		assertEquals(1, (int) x.asInteger());
		assertEquals(1, (int) t.removeFirst().asInteger());
		assertEquals(3, (int) t.removeLast().asInteger());
		assertEquals(1, t.size());
		assertEquals(2, (int) t.get(0).asInteger());
	}

	// --- CREATE DATA STRUCTURE BY JSON OR XML SOURCE ---

	@Test
	public void testParsers() throws Exception {
		Tree t = new Tree(JSON);

		assertJsonEquals(JSON, t.toString(false));
		testSerializationAndCloning(t);
	}

	// --- NAME OF THE NODE ----

	@Test
	public void testName() throws Exception {
		Tree t = new Tree(JSON);
		Tree c = t.get("a.b.c");
		assertEquals("c", c.getName());
		c.setName("x");
		assertEquals("x", c.getName());

		String j1 = "{\"x\":{\"d\":[1,2,3]}}";
		String j2 = t.get("a.b").toString(false);
		assertJsonEquals(j1, j2);
		String j3 = t.get("a.b").toString("json", false);
		assertJsonEquals(j1, j3);
		
		byte[] j4 = t.get("a.b").toBinary();
		assertJsonEquals(j1, new String(j4));
		byte[] j5 = t.get("a.b").toBinary("json");
		assertJsonEquals(j1, new String(j5));
		byte[] j6 = t.get("a.b").toBinary("json", false);
		assertJsonEquals(j1, new String(j6));			
	}

	// --- TEST BINARY CONVERTER ----

	@Test
	public void testBinaryConverter() throws Exception {

		testConverter("abcdefghijkl", byte[].class);
		testConverter("abcdefghijkl".getBytes(), byte[].class);
		testConverter(new Tree("[1,2,3,4,5]").asObject(), byte[].class);

		testConverter((byte) 123, byte[].class);
		testConverter((short) 1234, byte[].class);
		testConverter((float) 1234.5678, byte[].class);
		testConverter((double) 1234.5678, byte[].class);
		testConverter(1234, byte[].class);
		testConverter(123456789L, byte[].class);
		testConverter(new BigDecimal("1234.5678"), byte[].class);
		testConverter(new BigInteger("12345678"), byte[].class);

		testConverter(true, byte[].class);
		testConverter(false, byte[].class);

		testConverter(InetAddress.getLocalHost(), byte[].class);
		testConverter(UUID.randomUUID(), byte[].class);
		testConverter(new Date(), byte[].class);

	}

	// --- TEST STRING CONVERTER ---

	@Test
	public void testStringConverter() throws Exception {

		testConverter("abcdefghijkl", String.class);
		testConverter("abcdefghijkl".getBytes(), String.class);

		testConverter((byte) 123, String.class);
		testConverter((short) 1234, String.class);
		testConverter((float) 1234.5678, String.class);
		testConverter((double) 1234.5678, String.class);
		testConverter(1234, String.class);
		testConverter(123456789L, byte[].class);
		testConverter(new BigDecimal("1234.5678"), String.class);
		testConverter(new BigInteger("12345678"), String.class);

		testConverter(true, String.class);
		testConverter(false, String.class);

		testConverter(InetAddress.getLocalHost(), String.class);
		testConverter(UUID.randomUUID(), String.class);
		testConverter(new Date(), String.class);
	}

	private void testConverter(Object value, Class<?> type) throws Exception {

		Tree t = new Tree();

		t.putObject("original", value);
		t.putObject("converted", value);
		Tree a = t.get("converted");
		String expected = a.isPrimitive() ? a.asString() : a.toString(false);
		a.setType(type);

		if (type.isArray()) {
			assertTrue(a.isArray());
		}

		String json = t.toString();

		Class<?> originalType = value instanceof Collection ? List.class : value.getClass();
		a.setType(originalType);
		assertJsonEquals(expected, a.isPrimitive() ? a.asString() : a.toString(false));

		Tree copy = new Tree(json);
		if (a.isPrimitive()) {
			String v = copy.get("converted").setType(type).setType(originalType).asString();
			assertJsonEquals(expected, v);
		} else {
			String v = copy.get("converted").setType(type).setType(originalType).toString(false);
			assertJsonEquals(expected, v);
		}
	}

	// --- NODE TYPE ---

	@Test
	public void testTypes() throws Exception {
		Tree t = new Tree();

		t.put("null", (String) null);
		assertNull(t.get("null").getType());
		assertNull(t.get("null").asString());

		t.put("bigDecimal", new BigDecimal("1.2"));
		assertEquals(new BigDecimal("1.2"), t.get("bigDecimal").asBigDecimal());
		assertEquals(BigDecimal.class, t.get("bigDecimal").getType());

		t.put("bigInteger", new BigInteger("2"));
		assertEquals(new BigInteger("2"), t.get("bigInteger").asBigInteger());
		assertEquals(BigInteger.class, t.get("bigInteger").getType());

		t.put("bool", true);
		assertEquals((Boolean) true, t.get("bool").asBoolean());
		assertEquals(Boolean.class, t.get("bool").getType());

		t.put("byte", (byte) 3);
		assertEquals(new Byte((byte) 3), t.get("byte").asByte());
		assertEquals(Byte.class, t.get("byte").getType());

		t.put("bytes", "test".getBytes());
		assertEquals("test", new String(t.get("bytes").asBytes()));
		assertEquals((new byte[0]).getClass(), t.get("bytes").getType());

		Date date = new Date();
		t.put("date", date);
		assertEquals(date, t.get("date").asDate());
		assertEquals(Date.class, t.get("date").getType());

		t.put("double", 4d);
		assertEquals(new Double(4), t.get("double").asDouble());
		assertEquals(Double.class, t.get("double").getType());

		t.put("float", 5f);
		assertEquals(new Float(5), t.get("float").asFloat());
		assertEquals(Float.class, t.get("float").getType());

		InetAddress inet = InetAddress.getLocalHost();
		t.put("inet", inet);
		assertEquals(inet, t.get("inet").asInetAddress());

		t.put("int", 6);
		assertEquals(new Integer(6), t.get("int").asInteger());
		assertEquals(Integer.class, t.get("int").getType());

		t.put("long", 7l);
		assertEquals(new Long(7), t.get("long").asLong());
		assertEquals(Long.class, t.get("long").getType());

		t.put("short", (short) 8);
		assertEquals(new Short((short) 8), t.get("short").asShort());
		assertEquals(Short.class, t.get("short").getType());

		t.put("string", "abcdefgh");
		assertEquals("abcdefgh", t.get("string").asString());
		assertEquals(String.class, t.get("string").getType());

		UUID uuid = UUID.randomUUID();
		t.put("uuid", uuid);
		assertEquals(uuid, t.get("uuid").asUUID());
		assertEquals(UUID.class, t.get("uuid").getType());

		t.putMap("map");
		assertEquals(LinkedHashMap.class, t.get("map").getType());

		t.putSet("set");
		assertEquals(LinkedHashSet.class, t.get("set").getType());

		t.putList("list");
		assertEquals(LinkedList.class, t.get("list").getType());

		// --- CONVERT TYPE ---

		// String to int
		t.put("c", "123");
		Tree c = t.get("c");
		assertEquals(String.class, c.getType());
		c.setType(Integer.class);
		assertEquals(Integer.class, c.getType());
		assertEquals(123, (int) c.asInteger());

		// Int to string
		c.setType(String.class);
		assertEquals(String.class, c.getType());
		assertEquals("123", c.asString());

		// String to boolean
		c.setType(Boolean.class);
		assertEquals(Boolean.class, c.getType());
		assertEquals(true, (boolean) c.asBoolean());

		// Boolean true and false values
		c.set("-1");
		c.setType(Boolean.class);
		assertEquals(false, (boolean) c.asBoolean());
		c.setType(Boolean.class);
		assertEquals(0, (int) c.asInteger());

		// Zero to boolean
		c.set("0");
		c.setType(Boolean.class);
		assertEquals(false, (boolean) c.asBoolean());

		// Positive to boolean
		c.set("0.1");
		c.setType(Boolean.class);
		assertEquals(true, (boolean) c.asBoolean());
		c.setType(Integer.class);
		assertEquals(1, (int) c.asInteger());

		// Boolean "true" to other types
		c.set(true);
		assertEquals(true, (boolean) c.asBoolean());
		assertEquals(1, (int) c.asInteger());
		assertEquals(1L, (long) c.asLong());
		assertEquals((short) 1, (short) c.asShort());
		assertEquals(1d, (double) c.asDouble());
		assertEquals(1f, (float) c.asFloat());
		assertEquals((byte) 1, (byte) c.asByte());
		assertEquals("true", c.asString());
		assertEquals(new Date(1), c.asDate());
		assertEquals(new UUID(1, 0), c.asUUID());
		assertEquals(BigInteger.ONE, c.asBigInteger());
		assertEquals(BigDecimal.ONE, c.asBigDecimal());

		// Boolean "false" to other types
		c.set(false);
		assertEquals(false, (boolean) c.asBoolean());
		assertEquals(0, (int) c.asInteger());
		assertEquals(0L, (long) c.asLong());
		assertEquals((short) 0, (short) c.asShort());
		assertEquals(0d, (double) c.asDouble());
		assertEquals(0f, (float) c.asFloat());
		assertEquals((byte) 0, (byte) c.asByte());
		assertEquals("false", c.asString());
		assertEquals(new Date(0), c.asDate());
		assertEquals(new UUID(0, 0), c.asUUID());
		assertEquals(BigInteger.ZERO, c.asBigInteger());
		assertEquals(BigDecimal.ZERO, c.asBigDecimal());

		// Int to other types
		c.set(3);
		intTest(c, true);

		// String integer to other types
		c.set("3");
		intTest(c, true);

		// Long to other types
		c.set(3L);
		intTest(c, true);

		// Short to other types
		c.set((short) 3);
		intTest(c, true);

		// BigInteger to other types
		c.set(new BigInteger("3"));
		intTest(c, true);

		// String double to other types
		c.set("4.4");
		decimalTest(c);

		// Double to other types
		c.set(4.4d);
		decimalTest(c);

		// Float to other types
		c.set(4.4f);
		decimalTest(c);

		// BigDecimal to other types
		c.set(new BigDecimal("4.4"));
		decimalTest(c);

		// Byte array to BASE64
		String txt = "abcdefghijkl";
		c.set(txt.getBytes());

		String b64 = c.asString();
		assertTrue(b64.equals("YWJjZGVmZ2hpamts"));
		byte[] bytes = Base64.getDecoder().decode(b64);
		assertEquals(txt, new String(bytes));
		c.set(b64);
		String txt2 = new String(c.asBytes());
		assertEquals(txt, txt2);

		// byte array to UTF8
		c.set("YWJjZGVmZ2hpamts");
		txt2 = new String(c.asBytes());
		assertEquals(txt, txt2);

		// Autodetect non-BASE64 String
		c.set("abc");
		txt2 = new String(c.asBytes());
		assertEquals("abc", txt2);

		c.set("abcdabc");
		txt2 = new String(c.asBytes());
		assertEquals("abcdabc", txt2);

		c.set("abcd bcd");
		txt2 = new String(c.asBytes());
		assertEquals("abcd bcd", txt2);

		// byte array to UUID or BASE64
		c.set(new BigInteger("3").toByteArray());
		intTest(c, false);
		b64 = c.asString();
		bytes = Base64.getDecoder().decode(b64);
		assertEquals(1, bytes.length);
		assertEquals(3, bytes[0]);
		assertEquals(UUID.fromString("03000000-0000-0000-0000-000000000000"), c.asUUID());

		// DATE

		// Long to date
		long now = System.currentTimeMillis();
		c.set(now);
		assertEquals(now, c.asDate().getTime());

		// String to date
		c.set(date);
		SimpleDateFormat timestampFormat = new SimpleDateFormat(Config.TIMESTAMP_FORMAT, Locale.US);
		timestampFormat.setTimeZone(TimeZone.getTimeZone(Config.DEFAULT_TIME_ZONE));

		if (Config.USE_TIMESTAMPS) {
			assertEquals(timestampFormat.format(date), c.asString());
		} else {
			assertEquals(date.getTime(), (long) c.asLong());
		}

		for (String pattern : DATE_PATTERNS) {
			SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
			format.setTimeZone(TimeZone.getTimeZone(Config.DEFAULT_TIME_ZONE));
			String dateString = format.format(date);
			// System.out.println("Testing \"" + dateString + "\" date
			// format...");
			c.set(dateString);
			Date date2 = c.asDate();
			String dateString2 = format.format(date2);
			if (!dateString.equals(dateString2)) {
				System.out.println(date + " " + date2);
			}
			assertEquals(dateString, dateString2);
		}
	}

	private final void intTest(Tree c, boolean numeric) {
		assertEquals(true, (boolean) c.asBoolean());
		assertEquals(3, (int) c.asInteger());
		assertEquals(3L, (long) c.asLong());
		assertEquals((short) 3, (short) c.asShort());
		assertEquals(3d, (double) c.asDouble());
		assertEquals(3f, (float) c.asFloat());
		assertEquals((byte) 3, (byte) c.asByte());
		assertEquals(new Date(3), c.asDate());
		assertEquals(new BigInteger("3"), c.asBigInteger());
		assertEquals(new BigDecimal("3"), c.asBigDecimal());
		if (numeric) {
			assertEquals("3", c.asString());
			assertEquals(UUID.fromString("03000000-0000-0000-0000-000000000000"), c.asUUID());
		}
	}

	private final void decimalTest(Tree c) {
		assertEquals(true, (boolean) c.asBoolean());
		assertEquals(4, (int) c.asInteger());
		assertEquals(4L, (long) c.asLong());
		assertEquals((short) 4, (short) c.asShort());
		assertEquals(4.4d, (double) c.asDouble());
		assertEquals(4.4f, (float) c.asFloat());
		assertEquals((byte) 4, (byte) c.asByte());
		assertEquals("4.4", c.asString());
		assertEquals(new Date(4), c.asDate());
		assertEquals(UUID.fromString("04000000-0000-0000-0000-000000000000"), c.asUUID());
		assertEquals(new BigInteger("4"), c.asBigInteger());
		assertEquals(new BigDecimal("4.4"), c.asBigDecimal());
	}

	// --- PARENT NODE ---

	@Test
	public void testGetParent() throws Exception {
		Tree t = new Tree(JSON);

		Tree a = t.get("a");
		Tree b = t.get("a.b");
		Tree c = t.get("a.b.c");
		Tree d = t.get("a.b.c.d");

		Tree d1 = t.get("a.b.c.d[0]");
		Tree d2 = c.get("d[1]");
		Tree d3 = d.get("[2]");

		assertNull(t.getParent());
		assertEquals(t, a.getParent());
		assertEquals(a, b.getParent());
		assertEquals(b, c.getParent());
		assertEquals(c, d.getParent());

		assertEquals(d, d1.getParent());
		assertEquals(d, d2.getParent());
		assertEquals(d, d3.getParent());

		assertEquals(1, (int) d1.asInteger());
		assertEquals(2, (int) d2.asInteger());
		assertEquals(3, (int) d3.asInteger());

		Tree x = new Tree();
		x.putMap("q").putMap("w").putMap("e");
		c.copyFrom(x);
		assertEquals(c, t.get("a.b.c.q").getParent());

		c.assign(x);
		assertEquals(c, t.get("a.b.c.q").getParent());

		// Serialization and cloning
		testSerializationAndCloning(t);
	}

	// --- ROOT NODE ---

	@Test
	public void testGetRoot() throws Exception {
		Tree t = new Tree(JSON);

		assertEquals(t, t.get("a").getRoot());
		assertEquals(t, t.get("a.b").getRoot());
		assertEquals(t, t.get("a.b.c").getRoot());
		assertEquals(t, t.get("a.b.c.d").getRoot());
	}

	// --- METADATA CONTAINER ---

	@Test
	public void testMeta() throws Exception {
		Tree t = new Tree(JSON);

		assertNull(t.getMeta(false));
		assertNotNull(t.getMeta(true));

		String metaName = Config.META;

		Tree meta = t.getMeta();
		assertEquals(meta, t.get(Config.META));

		meta.put("test", "abc");
		assertEquals("abc", t.get(metaName + ".test").asString());

		assertEquals(t, meta.getParent());
		meta.remove();
		assertNull(t.getMeta(false));

		t = new Tree("{\"a\":3,\"" + metaName + "\":{\"b\":3.5},\"c\":true}");
		assertEquals(3.5, t.get(metaName + ".b", 0d));

		// Serialization and cloning
		testSerializationAndCloning(t);
	}

	// --- VALUE SETTERS / GETTERS ---

	public void testPutToArray() throws Exception {

		Tree t = new Tree();
		t.putList("a").add(1).add(2).add(3);
		t.put("a[3]", 4);
		assertJsonEquals("{\"a\":[1,2,3,4]}", t.toString(false));
		t.put("a[2]", 5);
		assertJsonEquals("{\"a\":[1,2,5,4]}", t.toString(false));

		t.putList("a").add(1).add(2).add(3);
		t.get("a[0]").remove();
		assertJsonEquals("{\"a\":[2,3]}", t.toString(false));
		t.get("a").getLastChild().remove();
		assertJsonEquals("{\"a\":[2]}", t.toString(false));

		t.put("a[3]", 4);
		assertJsonEquals("{\"a\":[2,null,null,4]}", t.toString(false));

		t.putObject("a", new int[] { 1, 2, 3 });
		t.put("a[3]", 4);
		assertJsonEquals("{\"a\":[1,2,3,4]}", t.toString(false));
		t.put("a[2]", 5);
		assertJsonEquals("{\"a\":[1,2,5,4]}", t.toString(false));

		t.putObject("a", new int[] { 1, 2, 3 });
		t.get("a[0]").remove();
		assertJsonEquals("{\"a\":[2,3]}", t.toString(false));
		t.get("a").getLastChild().remove();
		assertJsonEquals("{\"a\":[2]}", t.toString(false));

		t.put("a[3]", 4);
		assertJsonEquals("{\"a\":[2,null,null,4]}", t.toString(false));

		t.putObject("a", new int[] { 1, 2, 3 });
		t.get("a").remove((child) -> {
			return child.asInteger() == 2;
		});
		assertJsonEquals("{\"a\":[1,3]}", t.toString(false));

		// Johnzon bug
		t.putObject("a", new int[] { 1 });
		t.put("a[3]", "b");
		assertJsonEquals("{\"a\":[1,null,null,\"b\"]}", t.toString(false));

		t.putObject("a", new int[] { 1, 2, 3 });
		t.get("a").add(true);
		assertJsonEquals("{\"a\":[1,2,3,true]}", t.toString(false));

		t.putMap("a").put("b", "c");
		assertJsonEquals("{\"a\":{\"b\":\"c\"}}", t.toString(false));
		t.get("a").add(true);
		assertJsonEquals("{\"a\":[\"c\",true]}", t.toString(false));
	}

	@Test
	public void testPutMethods() throws Exception {
		Tree t = new Tree();

		t.put("a.b.c.d", 3);
		assertEquals(3, (int) t.get("a.b.c.d").asInteger());

		t.put("a.b.c.d2", 3d);
		assertEquals(3d, t.get("a.b.c.d2").asDouble());

		t.put("a.b.c.d3", true);
		assertEquals(true, (boolean) t.get("a.b.c.d3").asBoolean());

		t.put("a.b.c.d4", false);
		assertEquals(false, (boolean) t.get("a.b.c.d4").asBoolean());

		t.put("a.b.c2.d", 3L);
		assertEquals(3L, (long) t.get("a.b.c2.d").asLong());

		t.put("a.b.c2.d2", (short) 5);
		assertEquals((short) 5, (short) t.get("a.b.c2.d2").asShort());

		t.put("a.b.c2.d2", (float) 7);
		assertEquals((float) 7, (float) t.get("a.b.c2.d2").asFloat());

		t.put("a.b.c3.d", "X");
		assertEquals("X", t.get("a.b.c3.d").asString());

		// Put value into array
		Tree list = t.putList("li");
		assertEquals(0, list.size());
		t.put("li[0]", "a");
		assertEquals("a", t.get("li[0]", "x"));
		t.put("li[0]", "b");
		assertEquals("b", t.get("li[0]", "x"));
		assertEquals(1, list.size());

		// Modify array using path
		list.clear();
		for (int i = 0; i < 5; i++) {
			list.add(i);
		}
		assertJsonEquals("[0,1,2,3,4]", list.toString(false));
		t.put("li[1]", "q");
		assertJsonEquals("[0,\"q\",2,3,4]", list.toString(false));
		t.put("li[3]", 15.4);
		assertJsonEquals("[0,\"q\",2,15.4,4]", list.toString(false));
		t.put("li[4]", true);
		assertJsonEquals("[0,\"q\",2,15.4,true]", list.toString(false));

		// Serialization and cloning
		testSerializationAndCloning(t);

		UUID uuid = UUID.randomUUID();
		t.put("a.b.c4.d", uuid);
		assertEquals(uuid, t.get("a.b.c4.d").asUUID());

		t.put("a.b.c5.d", "hello".getBytes());
		assertEquals("hello", new String(t.get("a.b.c5.d").asBytes()));

		t.put("a.b.c5.d3", new BigInteger("32"));
		assertEquals(new BigInteger("32"), t.get("a.b.c5.d3").asBigInteger());

		t.put("a.b.c5.d4", new BigDecimal("32.56"));
		assertEquals(new BigDecimal("32.56"), t.get("a.b.c5.d4").asBigDecimal());
	}

	// --- PATH AND VALUE GETTERS FOR CHILDREN ---

	@Test
	public void testGetters() throws Exception {
		Tree t = new Tree();
		for (int i1 = 0; i1 < 10; i1++) {
			for (int i2 = 0; i2 < 10; i2++) {
				for (int i3 = 0; i3 < 10; i3++) {
					int val = (i1 * 100) + (i2 * 10) + i3;
					t.put("i" + i1 + ".i" + i2 + ".i" + i3, val);
				}
			}
		}
		for (int i1 = 0; i1 < 10; i1++) {
			for (int i2 = 0; i2 < 10; i2++) {
				for (int i3 = 0; i3 < 10; i3++) {
					int val = (i1 * 100) + (i2 * 10) + i3;
					assertEquals(val, t.get("i" + i1 + ".i" + i2 + ".i" + i3, -1));

					Tree c = t.get("i" + i1 + ".i" + i2 + ".i" + i3);
					assertEquals("i" + i3, c.getName());
					assertEquals(val, (int) c.asInteger());
					assertEquals(Integer.class, c.getType());
				}
			}
		}

		// Serialization and cloning
		testSerializationAndCloning(t);
	}

	// --- CLEAR VALUE(S) ---

	@Test
	public void testClear() throws Exception {
		Tree t = new Tree(JSON);

		Tree d = t.get("a.b.c.d");
		assertEquals(3, d.size());
		d.clear();
		assertNotNull(d.getParent());
		assertEquals(0, d.size());
		assertTrue(d.isEnumeration());
		assertTrue(d.isStructure());
		assertEquals(1, t.get("a.b.c").size());

		Tree b = d.getParent().getParent();
		assertEquals(1, b.size());
		b.clear();
		assertNotNull(b.getParent());
		assertEquals(0, b.size());
		assertTrue(d.isList());
		assertEquals(1, t.get("a").size());

		t.put("p", 1.2);
		assertEquals(1, t.get("p").size());
		t.clear("p");
		assertEquals(0, t.get("p").size());

		t.clear("a.b").add(1).add(2);
		assertEquals(2, t.get("a.b[1]", -1));

		Tree listToMap = t.get("a.b").clear();
		listToMap.put("a", 4).put("b", 5);
		assertEquals(4, t.get("a.b.a", -1));
		t.get("a.b").removeFirst();
		assertEquals(-1, t.get("a.b.a", -1));
	}

	// --- REMOVE FUNCTIONS ---

	@Test
	public void testRemove() throws Exception {
		Tree t = new Tree(JSON);

		Tree d = t.get("a.b.c.d[0]").getParent();
		assertEquals(3, d.size());
		d.remove();
		assertNull(d.getParent());
		assertEquals(3, d.size());
		assertTrue(d.isEnumeration());
		assertTrue(d.isStructure());
		assertEquals(0, t.get("a.b.c").size());

		Tree b = t.get("a").get("b");
		assertEquals(1, b.size());
		b.remove();
		assertNull(b.getParent());
		assertEquals(1, b.size());
		assertTrue(b.isMap());
		assertEquals(0, t.get("a").size());

		// Remove meta node
		t.getMeta().put("q.w.e.r.t", false);
		assertEquals(0, t.get("_meta.q.w.e.r.t", 1));
		t.remove("_meta.q.w");
		assertEquals(1, t.get("_meta.q.w.e.r.t", 1));
		assertNull(t.get("_meta.q.w"));
		assertNotNull(t.get("_meta.q"));
		t.getMeta().remove();
		assertFalse(t.hasMeta());

		// Remove items from list
		Tree l = t.putList("q.w.list1");
		assertEquals(l.getType(), LinkedList.class);
		l.add(1).add(2).add(3).add(4);
		assertJsonEquals("[1,2,3,4]", l.toString(false));
		t.remove("q.w.list1[1]");
		assertJsonEquals("[1,3,4]", l.toString(false));
		t.remove("q.w.list1[2]");
		assertJsonEquals("[1,3]", l.toString(false));

		// Remove items from set
		Tree s = t.putSet("q.w.set1");
		assertEquals(s.getType(), LinkedHashSet.class);
		s.add(1).add(2).add(3).add(4);
		assertJsonEquals("[1,2,3,4]", s.toString(false));
		s.add(1).add(1).add(1).add(1).add(1);
		assertJsonEquals("[1,2,3,4]", s.toString(false));
		t.remove("q.w.set1[1]");
		assertJsonEquals("[1,3,4]", s.toString(false));
		t.remove("q.w.set1[2]");
		assertJsonEquals("[1,3]", s.toString(false));
	}

	// --- INSERT INTO LIST (~=JSON ARRAY) FUNCTIONS ---

	@Test
	public void testInsert() throws Exception {

		// Last element
		testList((i, l) -> {
			l.insert(i, i);
		});
		testList((i, l) -> {
			l.insert(i, (double) i);
		});
		testList((i, l) -> {
			l.insert(i, (long) i);
		});
		testList((i, l) -> {
			l.insert(i, (float) i);
		});
		testList((i, l) -> {
			l.insert(i, (short) i.intValue());
		});
		testList((i, l) -> {
			l.insert(i, new BigDecimal(i));
		});
		testList((i, l) -> {
			l.insert(i, new BigInteger(Integer.toString(i)));
		});
		testList((i, l) -> {
			l.insert(i, Integer.toString(i));
		});
		testList((i, l) -> {
			l.insert(i, new Date(i));
		});
		testList((i, l) -> {
			l.insert(i, new BigInteger(Integer.toString(i)).toByteArray());
		});
		testList((i, l) -> {
			l.insert(i, new UUID(i, 0));
		});

		// First element
		testList((i, l) -> {
			l.insert(0, (9 - i));
		});
		testList((i, l) -> {
			l.insert(0, (double) (9 - i));
		});
		testList((i, l) -> {
			l.insert(0, (long) (9 - i));
		});
		testList((i, l) -> {
			l.insert(0, (float) (9 - i));
		});
		testList((i, l) -> {
			l.insert(0, (short) (9 - i));
		});
		testList((i, l) -> {
			l.insert(0, new BigDecimal(9 - i));
		});
		testList((i, l) -> {
			l.insert(0, new BigInteger(Integer.toString(9 - i)));
		});
		testList((i, l) -> {
			l.insert(0, Integer.toString(9 - i));
		});
		testList((i, l) -> {
			l.insert(0, new Date(9 - i));
		});
		testList((i, l) -> {
			l.insert(0, new BigInteger(Integer.toString(9 - i)).toByteArray());
		});
		testList((i, l) -> {
			l.insert(0, new UUID(9 - i, 0));
		});
	}

	private void testList(BiConsumer<Integer, Tree> appender) throws Exception {
		Tree t = new Tree();
		Tree l = t.putList("list");
		for (int i = 0; i < 10; i++) {
			appender.accept(i, l);
		}

		assertTrue(t.isStructure());
		assertTrue(l.isStructure());
		assertTrue(l.isList());
		assertEquals(10, l.size());
		assertFalse(l.isEmpty());

		long p = -1;
		for (Tree child : l) {
			long v = child.asLong();
			assertEquals(v - 1, p);
			p = v;

			assertEquals(l, child.getParent());
			assertEquals(t, child.getRoot());
			assertTrue(child.isPrimitive() || child.isArray());
		}

		// Continue with extended tests?
		Class<?> type = t.get("list[0]").getType();
		if (type == UUID.class || type == Date.class || type == byte[].class) {
			return;
		}

		// Serialization and cloning
		testSerializationAndCloning(t);

		// Convert to JSON
		String json = t.toString();
		Tree t3 = new Tree(json);
		Tree l3 = t3.get("list");
		p = -1;
		for (Tree child : l3) {
			long v = child.asLong();
			assertEquals(v - 1, p);
			p = v;
		}
	}

	// --- ADD TO LIST / SET (~=JSON ARRAY) ---

	@Test
	public void testAdd() throws Exception {
		testList((i, l) -> {
			l.add(i);
		});
		testList((i, l) -> {
			l.add((double) i);
		});
		testList((i, l) -> {
			l.add((long) i);
		});
		testList((i, l) -> {
			l.add((float) i);
		});
		testList((i, l) -> {
			l.add((short) i.intValue());
		});
		testList((i, l) -> {
			l.add(new BigDecimal(i));
		});
		testList((i, l) -> {
			l.add(new BigInteger(Integer.toString(i)));
		});
		testList((i, l) -> {
			l.add(Integer.toString(i));
		});
		testList((i, l) -> {
			l.add(new Date(i));
		});
		testList((i, l) -> {
			Tree t = new Tree();
			t.put("a", i);
			byte[] b = t.get("a").setType(byte[].class).asBytes();
			l.add(b);
		});
		testList((i, l) -> {
			l.add(new UUID(i, 0));
		});
	}

	// --- CONVERT TO JSON STRING ---

	@Test
	public void testToJSON() throws Exception {

		// JSON test
		testConvert();

		Tree t = new Tree();
		t.put("a", 1);
		assertJsonEquals("{\"a\":1}", t.toString(false));

		Tree l = t.putList("list");
		l.add(1).add(1).add(1);
		assertJsonEquals("[1,1,1]", l.toString(false));

		t = new Tree();
		t.putList("list").add(1).add(2).add(3);
		t.putSet("set").add(1).add(2).add(3);
		t.putMap("map").put("a", "b").put("c", "d");
		String x = t.toString();
		Tree j = new Tree(x);
		assertEquals(1, j.get("list[0]", -1));
		assertEquals(2, j.get("list[1]", -1));
		assertEquals(3, j.get("list[2]", -1));
		assertJsonEquals("1", j.get("set[0]", "-1"));
		assertJsonEquals("2", j.get("set[1]", "-1"));
		assertJsonEquals("3", j.get("set[2]", "-1"));
		assertEquals("b", j.get("map.a", "x"));
		assertEquals("d", j.get("map.c", "x"));
	}

	private Tree testConvert() throws Exception {
		Tree t = new Tree();
		Date date = new Date();
		InetAddress inet = InetAddress.getLocalHost();
		UUID uuid = UUID.randomUUID();

		// JSON-Simple doesn't support non-standard data types
		String writerClass = TreeWriterRegistry.getWriter(TreeWriterRegistry.JSON).getClass().toString();

		// Standard JSON types
		t.put("null", (String) null);
		t.put("empty", "");
		t.put("bool", true);
		t.put("byte", (byte) 3);
		t.put("double", 4d);
		t.put("float", 5f);
		t.put("int", 6);
		t.put("long", 7l);
		t.put("short", (short) 8);
		t.put("string", "abcdefgh");

		// Non-standard JSON types
		t.put("bigDecimal", new BigDecimal("1.2"));
		t.put("bigInteger", new BigInteger("2"));
		t.put("bytes", "test".getBytes());
		t.put("inet", inet);
		t.put("date", date);
		t.put("uuid", uuid);

		// Collections
		t.putMap("map").put("key1", "value1").put("key2", true).put("key3", 4.5);
		t.putSet("set").add(1).add(2).add(3);
		t.putList("list").add("a").add("b").add("c");

		// Meta
		t.getMeta().put("m1", 1);
		t.getMeta().put("m2", "abc");
		t.getMeta().put("m3", true);

		// Array
		t.getMeta().putObject("m4", new String[] { "a", "b", "c" });

		// Print output format
		System.out.println("-------------------- JSON --------------------");
		System.out.println("Output of " + writerClass + " serializer (Standard and Cassandra types):");
		String source;
		try {
			source = t.toString(TreeWriterRegistry.JSON, true, true);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		System.out.println();
		System.out.println(source);

		// Check formatting
		assertTrue(source.contains("\r") || source.contains("\n"));
		assertTrue(source.contains(" ") || source.contains("\t"));

		Tree t2 = new Tree(source, TreeWriterRegistry.JSON);
		source = t2.toString(TreeWriterRegistry.JSON, false, true);

		// Check formatting
		assertFalse(
				(source.contains("\r") || source.contains("\n")) && (source.contains(" ") || source.contains("\t")));
		t2 = new Tree(source, TreeWriterRegistry.JSON);

		// Standard JSON types
		assertNull(t2.get("null", (String) null));
		assertEquals(true, t2.get("bool", false));
		assertEquals((byte) 3, t2.get("byte", (byte) 0));
		assertEquals(4d, t2.get("double", 0d));
		assertEquals(5f, t2.get("float", 0f));
		assertEquals(6, t2.get("int", 0));
		assertEquals(7l, t2.get("long", 0L));
		assertEquals((short) 8, t2.get("short", (short) 0));
		assertEquals("abcdefgh", t2.get("string", ""));

		// Non-standard JSON types
		assertEquals(new BigDecimal("1.2"), t2.get("bigDecimal", new BigDecimal("0")));
		assertEquals(new BigInteger("2"), t2.get("bigInteger", new BigInteger("0")));
		assertEquals("test", new String(t2.get("bytes", "ppp".getBytes())));
		assertEquals(inet, t2.get("inet", InetAddress.getLocalHost()));
		assertEquals(date.getTime() / 1000L, t2.get("date", new Date(0)).getTime() / 1000L);
		assertEquals(uuid, t2.get("uuid", UUID.randomUUID()));

		// Collections
		assertTrue(t2.get("map").isMap());
		assertTrue(t2.get("set").isEnumeration());
		assertTrue(t2.get("list").isEnumeration());

		assertEquals("value1", t2.get("map.key1", "x"));
		assertEquals(true, t2.get("map.key2", false));
		assertEquals(4.5, t2.get("map.key3", 5.6));

		assertEquals(1, t2.get("set[0]", 2));
		assertTrue(2 == t2.get("set").get(1).asInteger());
		assertEquals(3, t2.get("set[2]", 4));

		assertEquals("a", t2.get("list[0]", "x"));
		assertEquals("b", t2.get("list").get(1).asString());
		assertEquals("c", t2.get("list[2]", "y"));

		return t;
	}

	// --- TO (JSON) STRING ---

	@Test
	public void testToString() throws Exception {
		Tree t = new Tree(JSON);
		assertEquals(t.toString(true), t.toString());

		Tree root = new Tree();
		Tree child1 = root.putList("first").add(1).add(2).add(3);
		Tree child2 = root.putList("second");
		child2.assign(child1);
	}

	// --- COLLECTION HELPERS ---

	@Test
	public void testEquals() throws Exception {
		Tree t1 = new Tree();
		Tree t2 = new Tree();
		assertTrue(t1.equals(t2));

		t1.put("a", 1);
		assertFalse(t1.equals(t2));

		t2.put("a", "1");
		assertFalse(t1.equals(t2));

		t2.get("a").setType(Integer.class);
		assertTrue(t1.equals(t2));

		assertEquals(new Tree(JSON), new Tree(new Tree(JSON).toString()));
	}

	@Test
	public void testHashCode() throws Exception {
		Tree t1 = new Tree();
		Tree t2 = new Tree();
		assertEquals(t1.hashCode(), t2.hashCode());

		t1.put("a", 1);
		assertTrue(t1.hashCode() - t2.hashCode() != 0);

		t2.put("a", "1");
		assertTrue(t1.hashCode() - t2.hashCode() != 0);

		t2.get("a").setType(Integer.class);
		assertEquals(t1.hashCode(), t2.hashCode());

		assertEquals(new Tree(JSON).hashCode(), new Tree(new Tree(JSON).toString()).hashCode());
	}

	// --- ITERATOR ---

	@Test
	public void testIterator() throws Exception {
		Tree t = new Tree();
		for (int i = 0; i < 50; i++) {
			Tree map = t.putMap("map" + i);
			map.put("value", i);
		}
		assertEquals(50, t.size());

		int c = 0;
		Iterator<Tree> it = t.iterator();
		while (it.hasNext()) {
			Tree q = it.next();
			assertEquals(c++, q.get("value", -1));
		}
		c = 0;
		for (Tree q : t) {
			assertEquals(c++, q.get("value", -1));
		}

		t = new Tree();
		for (int i = 0; i < 10; i++) {
			t.put("val" + i, i);
		}
		assertEquals(10, t.size());

		c = 0;
		for (Tree q : t) {
			assertEquals(c++, (int) q.asInteger());
		}
	}

	// --- ASSIGN FUNCTION ---

	@Test
	public void testAssign() throws Exception {
		Tree t = new Tree();
		t.put("a", "b");

		Tree c = new Tree();
		c.put("d", "e");

		Tree a = t.get("a");
		Tree d = c.get("d");
		a.assign(d);

		assertJsonEquals("{\"a\":\"e\"}", t.toString(false));
		assertEquals(t, t.get("a").getParent());

		t = new Tree();
		t.put("a", "b");
		t.get("a").assign(c);

		assertJsonEquals("{\"a\":{\"d\":\"e\"}}", t.toString(false));
		assertEquals(t.get("a"), t.get("a.d").getParent());
	}

	// --- COPY CHILDREN ---

	@Test
	public void testValues() throws Exception {
		Tree t = new Tree(JSON);

		List<?> l = t.get("a.b.c.d").asList(Integer.class);

		assertEquals("[1, 2, 3]", l.toString());

		l = t.get("a.b.c.d").asList(Byte.class);

		assertEquals((byte) 1, l.get(0));
		assertEquals((byte) 2, l.get(1));
		assertEquals((byte) 3, l.get(2));
	}

	// --- COPY CHILDREN ---

	@Test
	public void testCopy() throws Exception {
		Tree o = new Tree();
		o.put("a", 5);
		o.put("b", 6L);
		o.put("c", 7d);
		o.put("d", "8");

		Tree t = createTarget();
		t.copyFrom(o);
		String test = "{\"a\":5,\"b\":6,\"c\":7.0,\"d\":\"8\"}";
		assertJsonEquals(test, t.toString(false));

		t = createTarget();
		t.copyFrom(o, false);
		test = "{\"a\":1,\"b\":2,\"c\":3.0,\"d\":\"4\"}";
		assertJsonEquals(test, t.toString(false));

		t = createTarget();
		t.copyFrom(o, (from) -> {
			return from.asInteger() > 6;
		});
		test = "{\"a\":1,\"b\":2,\"c\":7.0,\"d\":\"8\"}";
		assertJsonEquals(test, t.toString(false));

		t = createTarget();
		t.copyFrom(o, (from) -> {
			return from.asDouble() < 7;
		});
		test = "{\"a\":5,\"b\":6,\"c\":3.0,\"d\":\"4\"}";
		assertJsonEquals(test, t.toString(false));

		t = new Tree();
		t.putObject("a", new Integer[] { 0, 1, 2, 3 });

		Tree s = new Tree();
		s.putObject("b", new Integer[] { 0, 1, 4, 5 });

		t.get(0).copyFrom(s.get(0), false);
		assertEquals(6, t.get("a").size());
		assertEquals(0, t.get("a[0]", -1));
		assertEquals(1, t.get("a[1]", -1));
		assertEquals(2, t.get("a[2]", -1));
		assertEquals(3, t.get("a[3]", -1));
		assertEquals(4, t.get("a[4]", -1));
		assertEquals(5, t.get("a[5]", -1));

		Tree s2 = new Tree();
		s2.putList("c").add(6).add(7);
		t.get("a").copyFrom(s2.get("c"));
		assertEquals(8, t.get(0).size());
		assertEquals(6, t.get("a[6]", -1));
		assertEquals(7, t.get("a[7]", -1));

		s2.get("c").copyFrom(s.get("b"));
		assertEquals(6, s2.get("c").size());
		assertEquals(5, s2.get("c[5]", -1));
	}

	private Tree createTarget() {
		Tree t = new Tree();
		t.put("a", 1);
		t.put("b", 2L);
		t.put("c", 3d);
		t.put("d", "4");
		return t;
	}

	// --- FIND A CHILD ---

	@Test
	public void testFind() throws Exception {
		Tree t = createTarget();
		Tree f = t.find((child) -> {
			return child.asInteger() == 3;
		});
		assertEquals(Double.class, f.getType());
		assertEquals(3d, f.asDouble());
	}

	// --- OTHER UTILITIES ---

	@Test
	public void testNull() throws Exception {
		Tree t = new Tree();
		t.put("a", (String) null);
		Tree a = t.get("a");
		assertNull(a.asString());
		assertNull(a.asDate());
		assertNull(a.asInteger());
		assertNull(a.asBytes());
		assertNull(a.asByte());
		assertNull(a.asFloat());
		assertNull(a.asDouble());
		assertNull(a.asInetAddress());
		assertNull(a.asUUID());
		assertNull(a.asBigDecimal());
		assertNull(a.asBigInteger());
		assertNull(a.asLong());
	}

	@Test
	public void testPrimitive() throws Exception {
		Tree t = new Tree();

		t.put("null", (String) null);
		assertPrimitive(t, "null");

		t.put("bool", true);
		assertPrimitive(t, "bool");

		t.put("byte", (byte) 3);
		assertPrimitive(t, "byte");

		t.put("double", 4d);
		assertPrimitive(t, "double");

		t.put("float", 5f);
		assertPrimitive(t, "float");

		t.put("int", 6);
		assertPrimitive(t, "int");

		t.put("long", 7l);
		assertPrimitive(t, "long");

		t.put("short", (short) 8);
		assertPrimitive(t, "short");

		t.put("string", "abcdefgh");
		assertPrimitive(t, "string");

		Tree set = t.putSet("set");
		set.add("x1");
		set.add("x2");
		set.add("x3");
		assertStructure(t, "set");

		Tree list = t.putList("list");
		list.add("y1");
		list.add("y2");
		list.add("y3");
		assertStructure(t, "list");

		Tree map = t.putMap("map");
		map.put("x1", "y1");
		map.put("x2", "y2");
		map.put("x3", "y3");
		assertStructure(t, "map");

		// Serialization and cloning
		testSerializationAndCloning(t);

		// Special types

		Date date = new Date();
		t.put("date", date);
		assertPrimitive(t, "date");

		t.put("bigDecimal", new BigDecimal("1.2"));
		assertPrimitive(t, "bigDecimal");

		t.put("bigInteger", new BigInteger("2"));
		assertPrimitive(t, "bigInteger");

		UUID uuid = UUID.randomUUID();
		t.put("uuid", uuid);
		assertPrimitive(t, "uuid");

		InetAddress inet = InetAddress.getLocalHost();
		t.put("inet", inet);
		assertPrimitive(t, "inet");

		t.put("bytes", "test".getBytes());
		Tree bytes = t.get("bytes");
		assertTrue(bytes.isArray());
		assertTrue(bytes.isEnumeration());
		assertTrue(bytes.isStructure());
		assertFalse(bytes.isMeta());
		assertFalse(bytes.isMap());
		assertFalse(bytes.isList());
		assertFalse(bytes.isSet());
		assertFalse(bytes.isRoot());
		assertFalse(bytes.hasMeta());
		assertFalse(bytes.isEmpty());
		assertFalse(bytes.isNull());
		assertTrue(bytes.size() == 4);
	}

	private final void assertPrimitive(Tree t, String name) {
		Tree x = t.get(name);
		assertTrue(x.isPrimitive());
		assertFalse(x.isArray());
		assertFalse(x.isEnumeration());
		assertFalse(x.isStructure());
		assertFalse(x.isMeta());
		assertFalse(x.isMap());
		assertFalse(x.isList());
		assertFalse(x.isSet());
		assertFalse(x.isRoot());
		assertFalse(x.hasMeta());
		if ("null".equals(name)) {
			assertTrue(x.isEmpty());
			assertTrue(x.isNull());
			assertTrue(x.size() == 0);
		} else {
			assertFalse(x.isEmpty());
			assertFalse(x.isNull());
			assertTrue(x.size() == 1);
		}
	}

	private final void assertStructure(Tree t, String name) {
		Tree x = t.get(name);
		assertFalse(x.isPrimitive());
		assertTrue(x.isStructure());
		assertFalse(x.isMeta());
		assertFalse(x.isRoot());
		assertFalse(x.hasMeta());
		assertFalse(x.isEmpty());
		assertFalse(x.isNull());
		assertTrue(x.size() == 3);
	}

	@Test
	public void testMap() throws Exception {
		Tree t = new Tree();

		Tree m1 = t.putMap("map1");
		m1.put("v1", 123);
		assertEquals(123, m1.get("v1", 0));
		assertEquals(123, t.get("map1.v1", 0));

		Tree m2 = t.putMap("map2");
		m2.put("v2", 124);
		assertEquals(124, m2.get("v2", 0));
		assertEquals(124, t.get("map2.v2", 0));

		assertEquals(0, t.get("map1.v2", 0));
		assertEquals(0, t.get("map2.v1", 0));

		t.get("map1").copyFrom(t.get("map2"));
		assertEquals(124, t.get("map1.v2", 0));

		// Serialization and cloning
		testSerializationAndCloning(t);
	}

	@Test
	public void testList() throws Exception {
		Tree t = new Tree();

		Tree l1 = t.putList("list1");
		l1.add(true);
		l1.add(true);

		assertEquals(2, l1.size());
		assertTrue(l1.get(0).asBoolean());
		assertTrue(l1.get(1).asBoolean());
		assertTrue(t.get("list1[0]", false));
		assertTrue(t.get("list1[1]", false));
		assertFalse(t.get("list1[2]", false));

		Tree l2 = t.putList("list2");
		l2.add(false);
		l2.add(false);

		assertEquals(2, l2.size());
		assertFalse(l2.get(0).asBoolean());
		assertFalse(l2.get(1).asBoolean());
		assertFalse(t.get("list2[0]", true));
		assertFalse(t.get("list2[1]", true));
		assertTrue(t.get("list2[2]", true));

		t.get("list1").copyFrom(t.get("list2"));
		assertEquals(4, l1.size());
		assertTrue(l1.get(0).asBoolean());
		assertTrue(l1.get(1).asBoolean());
		assertFalse(l1.get(2).asBoolean());
		assertFalse(l1.get(3).asBoolean());

		// Search nameless list in list
		Tree m1 = t.putList("m1");
		Tree m11 = m1.putList("m11");
		Tree m12 = m1.putList("m12");
		Tree m13 = m1.putList("m13");
		m12.add(10).add(20).add(30);

		assertEquals(10, t.get("m1[1].[0]", -1));
		assertEquals(20, t.get("m1[1].[1]", -1));
		assertEquals(30, t.get("m1[1].[2]", -1));

		assertEquals(0, t.get("m1[0]").size());
		assertTrue(t.get("m1[2]").isEmpty());
		assertNull(t.get("m1[3]"));
		assertTrue(m11.isStructure());
		assertTrue(m13.isList());

		// Convert set to list
		Tree struct = t.putSet("set");
		struct.add(1).add(2).add(3).add(1).add(2).add(3);
		assertEquals(3, struct.size());
		assertEquals(LinkedHashSet.class, struct.getType());

		struct.setType(List.class);
		struct.add(1).add(2).add(3);
		assertEquals(6, struct.size());
		assertEquals(LinkedList.class, struct.getType());

		// Convert empty(!) map to list
		struct.clear();
		struct.setType(Map.class);
		struct.put("a", 1);
		struct.put("b", 2);
		struct.put("c", 3);
		assertEquals(LinkedHashMap.class, struct.getType());
		struct.setType(List.class);
		assertEquals(LinkedList.class, struct.getType());
		assertJsonEquals("[1,2,3]", struct.toString(false));

		// Serialization and cloning
		testSerializationAndCloning(t);
	}

	@Test
	public void testSet() throws Exception {
		Tree t = new Tree();

		Tree s1 = t.putSet("set1");
		s1.add(true);
		s1.add(false);

		assertEquals(2, s1.size());
		assertTrue(s1.get(0).asBoolean());
		assertFalse(s1.get(1).asBoolean());

		assertTrue(t.get("set1[0]", false));
		assertFalse(t.get("set1[1]", true));
		assertTrue(t.get("set1[2]", true));

		// Search nameless set in set
		Tree m1 = t.putSet("m1");
		Tree m11 = m1.putSet("m11").add(1);
		Tree m12 = m1.putSet("m12").add(10).add(20).add(30);
		Tree m13 = m1.putSet("m13").add(5);

		assertEquals(10, t.get("m1[1].[0]", -1));
		assertEquals(20, t.get("m1[1].[1]", -1));
		assertEquals(30, t.get("m1[1].[2]", -1));

		assertEquals(1, t.get("m1[0]").size());
		assertFalse(t.get("m1[2]").isEmpty());
		assertNull(t.get("m1[3]"));
		assertTrue(m11.isStructure());
		assertFalse(m12.isPrimitive());
		assertTrue(m13.isSet());

		// Convert list to set
		Tree struct = t.putList("list");
		struct.add(1).add(2).add(3).add(1).add(2).add(3);
		assertEquals(6, struct.size());
		assertEquals(LinkedList.class, struct.getType());

		struct.setType(Set.class);
		struct.add(1).add(2).add(3);
		assertEquals(3, struct.size());
		assertEquals(LinkedHashSet.class, struct.getType());

		// Convert empty(!) map to set
		struct.clear();
		struct.setType(Map.class);
		struct.put("a", 1);
		struct.put("b", 2);
		struct.put("c", 3);
		struct.put("d", 3);
		struct.put("e", 3);
		assertEquals(LinkedHashMap.class, struct.getType());
		struct.setType(Set.class);
		assertEquals(LinkedHashSet.class, struct.getType());
		assertJsonEquals("[1,2,3]", struct.toString(false));

		// Serialization and cloning
		testSerializationAndCloning(t);
	}

	@Test
	public void testSize() throws Exception {
		Tree t = new Tree();
		assertEquals(0, t.size());

		t.put("a", 3);
		assertEquals(1, t.size());
		assertEquals(1, t.get("a").size());
		t.clear("a");
		assertEquals(0, t.get("a").size());

		t.putList("a").add(true).add(true).add(true);
		assertEquals(1, t.size());
		assertEquals(3, t.get("a").size());
	}

	// --- JAVA 8 STREAMS ---

	@Test
	public void testStream() throws Exception {
		Tree t = new Tree();
		assertEquals(0, t.stream().count());
		Tree l = t.putList("list");
		for (int i = 0; i < 10; i++) {
			l.add(i);
		}
		assertEquals(10, l.stream().count());
		Tree s = t.putSet("set");
		for (int i = 0; i < 20; i++) {
			s.add(Integer.toHexString(i));
		}
		assertEquals(20, s.stream().count());
		Tree m = t.putMap("map");
		for (int i = 0; i < 30; i++) {
			m.put("name" + i, i % 1 == 0 ? true : false);
		}
		assertEquals(30, m.stream().count());

		// Serialization and cloning
		testSerializationAndCloning(t);
	}

	// --- EMPTY ARRAY TEST ---

	@Test
	public void testEmptyArrays() throws Exception {
		String json = "{\"a\":[]}";

		Tree t = new Tree(json);
		Tree a = t.get("a");

		assertEquals(0, a.size());
		assertTrue(a.isEmpty());
		assertTrue(a.isEnumeration());

		a.add(1);

		assertEquals(1, a.size());
		assertFalse(t.get("a").isEmpty());
		assertEquals(1, t.get("a[0]", 0));

		a.add("b");

		assertEquals(2, a.size());
		assertEquals("b", t.get("a[1]", "c"));

		t.remove("a[0]");

		assertEquals(1, a.size());
		assertEquals("b", t.get("a[0]", "c"));

		t.clear("a");

		assertTrue(t.get("a").isEmpty());

		t = new Tree(json);
		t.get(0).add(true);
		assertFalse(t.get("a").isEmpty());

		t.get("a").insert(0, 5);
		assertJsonEquals("{\"a\":[5,true]}", t.toString());

		t = new Tree(json);
		t.get("a").insert(0, "b");
		assertJsonEquals("{\"a\":[\"b\"]}", t.toString());

		t = new Tree(json);
		t.get("a").setType(LinkedHashMap.class);
		assertTrue(t.get(0).isMap());
		assertTrue(t.get("a").isEmpty());
	}

	// --- EMPTY ARRAY TESTS ---

	@Test
	public void testTreeArrays() throws Exception {
		Tree t = new Tree(JSON);
		t.clear("a.b");

		Tree[] array = new Tree[3];
		array[0] = new Tree("{\"v0\":0}");
		array[1] = new Tree("{\"v1\" : 1}");
		array[2] = new Tree("{\"v2\"  :  2}");

		t.get("a.b").setObject(array);

		assertTrue(t.get("a.b[1]").getType() != Tree.class);

		assertEquals(0, t.get("a.b[0].v0", -1));
		assertEquals(1, t.get("a.b[1].v1", -1));
		assertEquals(2, t.get("a.b[2].v2", -1));

		Tree a = t.get("a.b");
		assertEquals(3, a.size());
		assertTrue(a.isList());

		a.insertObject(0, new Tree(null, "xyz", 3));
		assertEquals(4, a.size());
		assertEquals(3, t.get("a.b[0]", -1));

		t.clear("a.b");
		a = t.get("a.b");
		assertEquals(0, a.size());

		LinkedList<Tree> list = new LinkedList<>();
		list.addLast(new Tree("{\"v0\":0}"));
		list.addLast(new Tree("{\"v1\" : 1}"));
		list.addLast(new Tree("{\"v2\"  :  2 }"));

		a.setObject(list);

		assertTrue(t.get("a.b[1]").getType() != Tree.class);

		assertEquals(0, t.get("a.b[0].v0", -1));
		assertEquals(1, t.get("a.b[1].v1", -1));
		assertEquals(2, t.get("a.b[2].v2", -1));

		a.insertObject(0, new Tree("{\"v3\":3}"));
		assertEquals(4, a.size());
		assertEquals(3, t.get("a.b[0].v3", -1));

		a.insertObject(1, array);

		assertEquals(3, t.get("a.b[0].v3", -1));

		assertEquals(0, t.get("a.b[1][0].v0", -1));
		assertEquals(1, t.get("a.b[1][1].v1", -1));
		assertEquals(2, t.get("a.b[1][2].v2", -1));

		assertEquals(0, t.get("a.b[2].v0", -1));
		assertEquals(1, t.get("a.b[3].v1", -1));
		assertEquals(2, t.get("a.b[4].v2", -1));

		t.remove("a.b[1][0]");
		assertEquals(1, t.get("a.b[1][0].v1", -1));
		assertEquals(2, t.get("a.b[1][1].v2", -1));

		t.remove("a.b[1]");
		assertEquals(-1, t.get("a.b[1][0].v1", -1));

		assertEquals(3, t.get("a.b[0].v3", -1));
		assertEquals(0, t.get("a.b[1].v0", -1));
		assertEquals(1, t.get("a.b[2].v1", -1));
		assertEquals(2, t.get("a.b[3].v2", -1));
	}

	// --- EMPTY ARRAY TESTS ---

	@Test
	public void testArrays() throws Exception {

		// insert
		Tree t = new Tree();
		t.setObject(new Integer[] { 0, 1, 2 });
		t.insert(0, 3);
		assertTrue(t.isEnumeration());
		assertEquals(4, t.size());
		assertEquals(2, t.get("[3]", -1));

		t.insertObject(0, new Tree("{\"x\":\"y\"}"));
		assertEquals(5, t.size());
		assertTrue(t.get(0).isMap());
		assertFalse(t.get(0).isEmpty());
		assertEquals(1, t.get(0).size());
		assertTrue(t.get(0).getType() != Tree.class);
		assertEquals("y", t.get("[0].x", "a"));

		// remove
		t.setObject(new Integer[] { 0, 1, 2 });
		t.remove(0);
		assertTrue(t.isEnumeration());
		assertEquals(2, t.size());
		assertEquals(1, t.get("[0]", -1));

		// put
		t.setObject(new Integer[] { 0, 1, 2 });
		t.put("[0]", 3);
		assertTrue(t.isEnumeration());
		assertEquals(3, t.size());
		assertEquals(3, t.get("[0]", -1));
		t.putObject("[0]", new Tree("{\"a\":true}"));
		assertTrue(t.isEnumeration());
		assertEquals(3, t.size());
		assertTrue(t.get("[0].a", false));

		// clear
		t.setObject(new Integer[] { 0, 1, 2 });
		t.clear();
		assertTrue(t.isEnumeration());
		assertTrue(t.isEmpty());
		assertEquals(-1, t.get("[0]", -1));
		assertEquals(-1, t.get("[1]", -1));

		t.clear();
		t.put("a.b[3].c", 12);
		assertJsonEquals("{\"a\":{\"b\":[null,null,null,{\"c\":12}]}}", t.toString(false));
		t.remove("a.b[1]");
		assertJsonEquals("{\"a\":{\"b\":[null,null,{\"c\":12}]}}", t.toString(false));
		t.remove("a.b[1]");
		assertJsonEquals("{\"a\":{\"b\":[null,{\"c\":12}]}}", t.toString(false));
		t.remove("a.b[1]");
		assertJsonEquals("{\"a\":{\"b\":[null]}}", t.toString(false));
	}

	// --- EMPTY ARRAY TESTS ---

	@Test
	public void testParseRootArray() throws Exception {
		String json = "[1,2,3]";
		Tree t = new Tree();
		try {
			t = new Tree(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertJsonEquals(json, t.toString(false));
	}

	// --- SORT CHILDREN ---

	@Test
	public void testSort() throws Exception {

		// Sort list
		Tree t = new Tree();
		Tree l = t.putList("list");
		for (int i = 0; i < 10; i++) {
			l.add(i);
		}
		l.sort((n1, n2) -> {
			return n1.asInteger() - n2.asInteger();
		});
		assertJsonEquals("[0,1,2,3,4,5,6,7,8,9]", l.toString(false));
		l.sort((n1, n2) -> {
			return n2.asInteger() - n1.asInteger();
		});
		assertJsonEquals("[9,8,7,6,5,4,3,2,1,0]", l.toString(false));

		// Simple numeric sorting
		l.sort();
		assertJsonEquals("[0,1,2,3,4,5,6,7,8,9]", l.toString(false));

		// Simple alphanumeric sorting
		l.clear().add("e").add("d").add("c").add("b").add("a");
		l.sort();
		assertJsonEquals("[\"a\",\"b\",\"c\",\"d\",\"e\"]", l.toString(false));

		// Sort map
		Tree m = t.putMap("m");
		for (int i = 1; i < 5; i++) {
			m.put("v" + i, i);
		}
		assertJsonEquals("{\"v1\":1,\"v2\":2,\"v3\":3,\"v4\":4}", m.toString(false));
		m.sort((n1, n2) -> {
			return String.CASE_INSENSITIVE_ORDER.compare(n2.getName(), n1.getName());
		});
		assertJsonEquals("{\"v4\":4,\"v3\":3,\"v2\":2,\"v1\":1}", m.toString(false));

		// Simple map sorting
		m.sort();
		assertJsonEquals("{\"v1\":1,\"v2\":2,\"v3\":3,\"v4\":4}", m.toString(false));

		// Sort set
		Tree s = t.putList("list");
		for (int i = 0; i < 10; i++) {
			s.add(i);
		}
		s.sort((n1, n2) -> {
			return n1.asInteger() - n2.asInteger();
		});
		assertJsonEquals("[0,1,2,3,4,5,6,7,8,9]", s.toString(false));
		s.sort((n1, n2) -> {
			return n2.asInteger() - n1.asInteger();
		});
		assertJsonEquals("[9,8,7,6,5,4,3,2,1,0]", s.toString(false));

		// Simple numeric sorting
		s.sort();
		assertJsonEquals("[0,1,2,3,4,5,6,7,8,9]", s.toString(false));

		// Simple alphanumeric sorting
		s.clear().add("e").add("d").add("c").add("b").add("a");
		s.sort();
		assertJsonEquals("[\"a\",\"b\",\"c\",\"d\",\"e\"]", s.toString(false));

		// Array sorting
		s.clear().setObject(new String[] { "e", "d", "c", "b", "a" });
		s.sort();
		assertJsonEquals("[\"a\",\"b\",\"c\",\"d\",\"e\"]", s.toString(false));

		// Serialization and cloning
		testSerializationAndCloning(t);
	}

	// --- TEST "PUT IF ABSENT" MODIFIER ---

	@Test
	public void testPutIfAbsent() throws Exception {
		Tree rsp = new Tree();

		// Map

		Tree meta = rsp.getMeta();
		Tree headers = meta.putMap("headers", true);
		headers.put("a", 1);
		headers.put("b", 2);
		headers.put("c", 3);

		int size = rsp.getMeta().get("headers").size();
		assertEquals(3, size);
		assertEquals(2, rsp.getMeta().get("headers.b", -1));

		meta = rsp.getMeta();
		headers = meta.putMap("headers", true);
		headers.put("d", 4);

		size = rsp.getMeta().get("headers").size();
		assertEquals(4, size);
		assertEquals(2, rsp.getMeta().get("headers.b", -1));
		assertEquals(4, rsp.getMeta().get("headers.d", -1));

		meta = rsp.getMeta();
		headers = meta.putMap("headers", false);
		headers.put("d", 4);

		size = rsp.getMeta().get("headers").size();
		assertEquals(1, size);
		assertEquals(-1, rsp.getMeta().get("headers.b", -1));
		assertEquals(4, rsp.getMeta().get("headers.d", -1));

		// List

		Tree node = new Tree();

		Tree list1 = node.putList("a.b.c");
		list1.add(1).add(2).add(3);

		Tree list2 = node.putList("a.b.c", true);
		list2.add(4).add(5).add(6);

		// The "list2" contains 1, 2, 3, 4, 5 and 6.
		assertEquals(6, list2.size());
		assertJsonEquals("[1,2,3,4,5,6]", list2.toString(false));

		Tree list3 = node.putList("a.b.c", false);
		list3.add(7).add(8).add(9);

		assertEquals(3, list3.size());
		assertJsonEquals("[7,8,9]", list3.toString(false));

		// Set

		node = new Tree();

		Tree set1 = node.putSet("a.b.c");
		set1.add(1).add(2).add(3);

		Tree set2 = node.putSet("a.b.c", true);
		set2.add(4).add(5).add(6);

		// The "list2" contains 1, 2, 3, 4, 5 and 6.
		assertEquals(6, set2.size());
		assertJsonEquals("[1,2,3,4,5,6]", set2.toString(false));

		Tree set3 = node.putSet("a.b.c", false);
		set3.add(7).add(8).add(9);

		assertEquals(3, set3.size());
		assertJsonEquals("[7,8,9]", set3.toString(false));
	}

	// --- TEST NULL DEFAULTS ---

	@Test
	public void testNullDefaults() throws Exception {
		Tree t = new Tree();
		UUID uuid = UUID.randomUUID();
		InetAddress inet = InetAddress.getLocalHost();

		t.put("array", "value1".getBytes());
		t.put("string", "value2");
		t.put("uuid", uuid);
		t.put("inet", inet);
		t.put("bi", BigDecimal.TEN);
		t.put("bd", BigInteger.TEN);

		assertEquals("value1", new String(t.get("array", (byte[]) null)));
		assertEquals("value2", t.get("string", (String) null));
		assertEquals(uuid, t.get("uuid", (UUID) null));
		assertEquals(inet, t.get("inet", (InetAddress) null));
		assertEquals(BigDecimal.TEN, t.get("bi", (BigDecimal) null));
		assertEquals(BigInteger.TEN, t.get("bd", (BigInteger) null));
	}

	// --- TEST MONGO TYPES ---

	@Test
	public void testMongoTypes() throws Exception {

		// JSON-Simple and JsonUtil aren't extendable APIs
		String writerClass = TreeWriterRegistry.getWriter(TreeWriterRegistry.JSON).getClass().toString();

		Document doc = new Document();
		doc.put("BsonBoolean", new BsonBoolean(true));
		long time = System.currentTimeMillis();
		doc.put("BsonDateTime", new BsonDateTime(time));
		doc.put("BsonDouble", new BsonDouble(123.456));
		doc.put("BsonInt32", new BsonInt32(123));
		doc.put("BsonInt64", new BsonInt64(123456));
		doc.put("BsonNull", new BsonNull());
		doc.put("BsonRegularExpression", new BsonRegularExpression("abc"));
		doc.put("BsonString", new BsonString("abcdefgh"));
		doc.put("BsonTimestamp", new BsonTimestamp(12, 23));
		doc.put("BsonUndefined", new BsonUndefined());
		doc.put("Binary", new Binary("abcdefgh".getBytes()));
		doc.put("Code", new Code("var a = 5;"));
		doc.put("Decimal128", new Decimal128(123456789));
		ObjectId objectID = new ObjectId();
		doc.put("ObjectId", objectID);
		doc.put("Symbol", new Symbol("s"));

		Tree t = new Tree(doc, null);
		String json = t.toString();

		System.out.println("-------------------- BSON --------------------");
		System.out.println("Output of " + writerClass + " serializer (MongoDB types):");
		System.out.println(json);

		t = new Tree(json);

		assertTrue(t.get("BsonBoolean", false));

		Date date = t.get("BsonDateTime", new Date());
		assertEquals(time / 1000L, date.getTime() / 1000L);

		assertEquals(123.456, t.get("BsonDouble", 1d));
		assertEquals(123, t.get("BsonInt32", 1));
		assertEquals(123456L, t.get("BsonInt64", 1L));
		assertNull(t.get("BsonNull", "?"));
		assertEquals("abc", t.get("BsonRegularExpression", "?"));
		assertEquals("abcdefgh", t.get("BsonString", "?"));

		// String or Number
		date = t.get("BsonTimestamp", new Date());
		assertEquals(12000L, date.getTime());

		assertNull(t.get("BsonUndefined", "?"));
		assertEquals("abcdefgh", new String(t.get("Binary", "?".getBytes())));
		assertEquals("var a = 5;", t.get("Code", "?"));
		assertEquals(123456789L, t.get("Decimal128", 1L));
		assertEquals(objectID.toHexString(), t.get("ObjectId", "?"));
		assertEquals("s", t.get("Symbol", "?"));
	}

	// --- NULLPOINTER (NUMBER/BOOLEAN) TEST ---

	@Test
	public void testNullPointerNumbers() throws Exception {
		Tree t = new Tree();
		t.put("a", (String) null);
		t.put("b", (String) null);
		t.put("c", (String) null);
		t.put("d", (String) null);
		assertEquals(3, t.get("a", 3));
		assertTrue(t.get("b", true));
		assertFalse(t.get("c", false));
		assertNull(t.get("d", "X"));
	}

	// --- SERIALIZATION / DESERIALIZATION / CLONE ---

	private final void testSerializationAndCloning(Tree node) throws Exception {

		// Serialize
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(node);
		oos.flush();
		byte[] bytes = baos.toByteArray();
		// System.out.println(new String(bytes));

		// Deserialize
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Tree copy = (Tree) ois.readObject();

		String txtOriginal = node.toString("debug");
		String txtCopy = copy.toString("debug");

		assertEquals(txtOriginal, txtCopy);

		// Cloning
		txtCopy = node.clone().toString("debug");
		assertEquals(txtOriginal, txtCopy);
	}

	// --- TEST SIMILAR / SAME NODE ---

	private final void assertEquals(Tree n1, Tree n2) {
		assertEquals(n1.getName(), n2.getName());
		String t1 = n1.toString("debug");
		String t2 = n2.toString("debug");
		assertEquals(t1, t2);
	}

	private static final void assertJsonEquals(String s1, String s2) {
		if (s1 != null) {
			s1 = removeFormatting(s1);
		}
		if (s2 != null) {
			s2 = removeFormatting(s2);
		}
		assertEquals(s1, s2);
	}

	private static final String removeFormatting(String txt) {
		return txt.replace("\t", " ").replace("\r", " ").replace("\n", " ").replace(" ", "").replace(".0", "");
	}

	// --- TEST GETTING DATA FROM "EXTENDED JSON" ---

	@Test
	public void testExtendedJSON() throws Exception {
		Tree t = new Tree();

		long now = System.currentTimeMillis();

		t.putMap("a").put("$date", now);
		t.putMap("b").put("$regex", "abc");
		t.putMap("c").put("$oid", "5926c396121e2710341361da");
		t.putMap("d").put("$numberLong", 123L);
		t.putMap("e").put("$binary", "teszt".getBytes(), true);
		t.putMap("f").put("$symbol", "X");
		t.putMap("g").put("$code", "var a=3;");

		assertEquals(now, t.get("a", 1L));
		assertEquals("abc", t.get("b", "-"));
		assertEquals("5926c396121e2710341361da", t.get("c", "x"));
		assertEquals(123L, t.get("d", 1L));
		assertEquals("teszt", new String(t.get("e").asBytes()));
		assertEquals("X", t.get("f", "x"));
		assertEquals("var a=3;", t.get("g", "-"));

	}

	// --- TEST LARGE NUMBERS ---

	@Test
	public void testLargeNumbers() throws Exception {
		String readerClass = TreeReaderRegistry.getReader(TreeWriterRegistry.JSON).getClass().toString();

		try {
			Tree t = new Tree();
			Integer i = Integer.MAX_VALUE;
			t.put("i", i);
			t = new Tree(t.toString("JsonBuiltin"));
			Integer i2 = t.get("i").asInteger();
			assertEquals(i, i2);
			System.out.println(readerClass + " can deserialize large numbers as Integers.");
		} catch (Throwable e) {
			System.out.println(readerClass + " does NOT able to deserialize large numbers as Integers!");
		}

		try {
			Tree t = new Tree();
			Long l = Long.MAX_VALUE;
			t.put("l", l);
			t = new Tree(t.toString("JsonBuiltin"));
			Long l2 = t.get("l").asLong();
			assertEquals(l, l2);
			System.out.println(readerClass + " can deserialize large numbers as Longs.");
		} catch (Throwable e) {
			System.out.println(readerClass + " does NOT able to deserialize large numbers as Longs!");
		}

		try {
			Tree t = new Tree();
			BigInteger bi = new BigInteger(Long.toString(Long.MAX_VALUE));
			bi = bi.add(BigInteger.TEN);
			t.put("bi", bi);
			t = new Tree(t.toString("JsonBuiltin"));
			BigInteger bi2 = t.get("bi").asBigInteger();
			assertEquals(bi, bi2);
			System.out.println(readerClass + " can deserialize large numbers as BigIntegers.");
		} catch (Throwable e) {
			System.out.println(readerClass + " does NOT able to deserialize large numbers as BigIntegers!");
		}

		try {
			Tree t = new Tree();
			BigDecimal bd = new BigDecimal(Long.toString(Long.MAX_VALUE) + ".123");
			bd = bd.add(BigDecimal.TEN);
			t.put("bd", bd);
			t = new Tree(t.toString("JsonBuiltin"));
			BigDecimal bd2 = t.get("bd").asBigDecimal();
			assertEquals(bd, bd2);
			System.out.println(readerClass + " can deserialize large numbers as BigDecimals.");
		} catch (Throwable e) {
			System.out.println(readerClass + " does NOT able to deserialize large numbers as BigDecimals!");
			e.printStackTrace();
		}

	}

}