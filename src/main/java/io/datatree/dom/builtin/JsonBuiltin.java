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
package io.datatree.dom.builtin;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.datatree.Tree;
import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.converters.DataConverterRegistry;

/**
 * <b>BUILT-IN JSON ADAPTER</b><br>
 * <br>
 * Description: Built-in JSON reader / writer.<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.builtin.JsonBuiltin<br>
 * -Ddatatree.json.writer=io.datatree.dom.builtin.JsonBuiltin<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonBuiltin json = new JsonBuiltin();<br>
 * TreeReaderRegistry.setReader("json", json);<br>
 * TreeWriterRegistry.setWriter("json", json);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(1)
public class JsonBuiltin extends AbstractTextAdapter {

	// --- CONSTANTS ---

	protected static final char[] APOS = "\\\"".toCharArray();
	protected static final char[] CR_LF = "\r\n".toCharArray();
	protected static final char[] INDENT = "  ".toCharArray();
	protected static final char[] NULL = "null".toCharArray();

	// --- BUILDER CACHE ---

	public Queue<StringBuilder> builders = new ConcurrentLinkedQueue<>();

	// --- STATIC WRITER METHOD ---

	protected static final JsonBuiltin instance = new JsonBuiltin();

	public static final String serialize(Object value, Object meta) {
		return instance.toString(value, meta, false, true);
	}

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		if (value == null) {
			return "";
		}
		StringBuilder builder = builders.poll();
		if (builder == null) {
			builder = new StringBuilder(512);
		} else {
			builder.setLength(0);
		}
		toString(builder, value, insertMeta ? meta : null, pretty ? 1 : 0);
		final String json = builder.toString();
		if (builders.size() > Config.POOL_SIZE) {
			return json;
		}
		builders.add(builder);
		return json;
	}

	// --- protected UTILITIES ---

	@SuppressWarnings("rawtypes")
	protected static final void toString(StringBuilder builder, Object value, Object meta, int indent) {

		// Null value
		if (value == null) {
			builder.append(NULL);
			return;
		}

		// Numeric or boolean values
		if (value instanceof Number || value instanceof Boolean) {
			builder.append(value);
			return;
		}

		// Map
		if (value instanceof Map) {
			Map map = (Map) value;
			int max = map.size();
			int pos = 0;
			int newIndent = indent == 0 ? 0 : indent + 1;
			builder.append('{');
			if (indent != 0) {
				appendIndent(builder, indent);
			}
			for (Object child : map.entrySet()) {
				Map.Entry entry = (Map.Entry) child;
				appendString(builder, entry.getKey(), false);
				builder.append(':');
				toString(builder, entry.getValue(), null, newIndent);
				if (++pos < max || meta != null) {
					builder.append(',');
					if (indent != 0) {
						appendIndent(builder, indent);
					}
				}
			}
			if (meta != null) {
				appendString(builder, Tree.META, false);
				builder.append(':');
				toString(builder, meta, null, newIndent);
			}
			if (indent != 0) {
				appendIndent(builder, indent - 1);
			}
			builder.append('}');
			return;

		}

		// List or Set
		if (value instanceof Collection) {
			builder.append('[');
			if (indent != 0) {
				appendIndent(builder, indent);
			}
			Collection array = (Collection) value;
			int max = array.size();
			int pos = 0;
			int newIndent = indent == -1 ? -1 : indent + 1;
			for (Object child : array) {
				toString(builder, child, null, newIndent);
				if (++pos < max) {
					builder.append(',');
					if (indent != 0) {
						appendIndent(builder, indent);
					}
				}
			}
			if (indent != 0) {
				appendIndent(builder, indent - 1);
			}
			builder.append(']');
			return;
		}

		// Byte array
		if (value instanceof byte[]) {
			builder.append('"');
			builder.append(DataConverterRegistry.convert(String.class, value));
			builder.append('"');
			return;
		}

		// Array
		if (value.getClass().isArray()) {
			builder.append('[');
			if (indent != 0) {
				appendIndent(builder, indent);
			}
			int max = Array.getLength(value);
			int newIndent = indent == -1 ? -1 : indent + 1;
			for (int i = 0; i < max; i++) {
				toString(builder, Array.get(value, i), null, newIndent);
				if (i < max - 1) {
					builder.append(',');
					if (indent != 0) {
						appendIndent(builder, indent);
					}
				}
			}
			if (indent != 0) {
				appendIndent(builder, indent - 1);
			}
			builder.append(']');
			return;
		}

		// String and other types
		appendString(builder, value, true);
	}

	protected static final void appendString(StringBuilder builder, Object value, boolean convert) {
		String txt;
		if (convert) {
			txt = DataConverterRegistry.convert(String.class, value);
			if (txt == null) {
				builder.append(NULL);
				return;
			}
			if (DataConverterRegistry.isUnquotedClass(value.getClass())) {
				builder.append(txt);
				return;
			}
		} else {
			txt = String.valueOf(value);
		}
		builder.append('"');
		char[] chars = txt.toCharArray();
		for (char c : chars) {
			if (c == '"') {
				builder.append(APOS);
			} else {
				builder.append(c);
			}
		}
		builder.append('"');
	}

	protected static final void appendIndent(StringBuilder builder, int indent) {
		builder.append(CR_LF);
		for (int i = 0; i < indent; i++) {
			builder.append(INDENT);
		}
	}

	// --- JSON SOURCE HOLDER ---

	protected static final class Source {

		private char[] chars;
		private int last;
		private int idx;
		private char ch;

		private Source(char[] chars) {
			this.chars = chars;
			this.last = chars.length - 1;
		}

		private final void set(char[] chars) {
			this.chars = chars;
			this.last = chars.length - 1;
			this.idx = 0;
		}
	}

	// --- SOURCE CACHE ---

	public Queue<Source> sources = new ConcurrentLinkedQueue<>();

	// --- IMPLEMENTED PARSER METHODS ---

	@Override
	public Object parse(String source) throws Exception {
		Source s = sources.poll();
		final char[] chars = source.toCharArray();
		if (s == null) {
			s = new Source(chars);
		} else {
			s.set(chars);
		}
		final Object result = parseNext(s);
		if (sources.size() > Config.POOL_SIZE) {
			return result;
		}
		sources.add(s);
		return result;
	}

	// --- PRIVATE PARSER METHODS ---

	protected static final Object parseNext(Source src) throws IOException {
		skipWhitespaces(src);
		switch (src.ch) {

		case '"':
			return parseString(src);

		case 't':
			src.idx += 4;
			return Boolean.TRUE;

		case 'f':
			src.idx += 5;
			return Boolean.FALSE;

		case 'n':
			src.idx += 4;
			return null;

		case '[':
			return parseList(src);

		case '{':
			return parseMap(src);

		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case '-':
			return parseNumber(src);

		default:
			throw new IOException(
					"Unable to determine the next character, it is not a string, number, array, or object!");
		}
	}

	// --- MAP (~= JSON OBJECT) PARSER ---

	protected static final Object parseMap(Source src) throws IOException {
		if (src.ch == '{') {
			src.idx++;
		}
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		String childName;
		for (; src.idx <= src.last; src.idx++) {
			skipWhitespaces(src);
			if (src.ch == '"') {
				childName = parseString(src);
				skipWhitespaces(src);
				if (src.ch != ':') {
					throw new IOException("Expecting ':' character, but got " + (int) src.ch + "!");
				}
				src.idx++;
				skipWhitespaces(src);
				map.put(childName, parseNext(src));
				skipWhitespaces(src);
			}
			if (src.ch == '}') {
				src.idx++;
				break;
			}
			if (src.ch == ',') {
				continue;
			}
			throw new IOException("Expecting '}' or ',' but got char " + (int) src.ch + "!");
		}
		return map;
	}

	// --- LIST (~= JSON ARRAY) PARSER ---

	protected static final Object parseList(Source src) throws IOException {
		final LinkedList<Object> list = new LinkedList<Object>();
		boolean foundEnd = false;
		try {
			if (src.ch == '[') {
				src.idx++;
			}
			skipWhitespaces(src);
			if (src.ch == ']') {
				src.idx++;
				return list;
			}
			char c;
			loop: while (src.idx < src.last) {
				list.add(parseNext(src));
				while (true) {
					c = src.chars[src.idx];
					if (c == ',') {
						src.idx++;
						continue loop;
					}
					if (c == ']') {
						foundEnd = true;
						src.idx++;
						break loop;
					}
					if (c <= 32) {
						src.idx++;
						continue;
					}
					break;
				}
				c = src.chars[src.idx];
				if (c == ',') {
					src.idx++;
					continue;
				}
				if (c == ']') {
					src.idx++;
					foundEnd = true;
					break;
				}
				throw new IOException(String.format(
						"Expecting a ',' or a ']', " + " but got ch character of  %s " + " on array idx of %s!",
						(int) c, list.size()));
			}
		} catch (Exception cause) {
			if (cause instanceof IOException) {
				throw cause;
			}
			throw new IOException("Issue parsing JSON array!", cause);
		}
		if (!foundEnd) {
			throw new IOException("Did not find end of JSON Array!");
		}
		return list;
	}

	// --- STRING READER ---

	protected static final String parseString(Source src) {
		char c = src.chars[src.idx];
		if (src.idx <= src.last && c == '"') {
			src.idx++;
		}
		final int start = src.idx;
		loop: for (; src.idx <= src.last; src.idx++) {
			c = src.chars[src.idx];
			if (c == '"') {
				break;
			}
			if (c == '\\') {
				boolean escape = false;
				for (; src.idx <= src.last; src.idx++) {
					c = src.chars[src.idx];
					if (c == '"') {
						if (!escape) {
							break loop;
						}
					} else if (c == '\\') {
						escape = !escape;
					} else {
						escape = false;
					}
				}
				break;
			}
		}
		if (src.idx == start) {
			src.idx++;
			return "";
		}
		char[] chars = new char[src.idx - start];
		int to = 0;
		int from = start;
		while (true) {
			c = src.chars[from];
			if (c == '\\') {
				from++;
				c = chars[from];
				if (c != 'u') {
					switch (c) {
					case 'n':
						chars[to++] = '\n';
						break;
					case 'b':
						chars[to++] = '\b';
						break;
					case '/':
						chars[to++] = '/';
						break;
					case 'f':
						chars[to++] = '\f';
						break;
					case 'r':
						chars[to++] = '\r';
						break;
					case 't':
						chars[to++] = '\t';
						break;
					case '\\':
						chars[to++] = '\\';
						break;
					case '"':
						chars[to++] = '"';
						break;
					}
				} else {
					if (from + 4 < to) {
						String hex = new String(chars, from + 1, 4);
						char unicode = (char) Integer.parseInt(hex, 16);
						chars[to++] = unicode;
						from += 4;
					}
				}
			} else {
				chars[to++] = c;
			}
			if (to >= chars.length) {
				break;
			}
			from++;
		}
		String value = new String(chars, 0, to);
		if (src.idx <= src.last) {
			src.idx++;
		}
		return value;
	}

	// --- NUMBER READER ---

	protected static final Object parseNumber(Source src) {
		final int from = src.idx;
		boolean dot = false;
		for (; src.idx < src.chars.length; src.idx++) {
			char c = src.chars[src.idx];
			if (c <= 32 || c == ',' || c == '}' || c == ']') {
				break;
			} else if (c == '.') {
				dot = true;
			}
		}
		final int len = src.idx - from;
		if (dot) {
			return Double.parseDouble(new String(src.chars, from, len));
		}
		return Long.parseLong(new String(src.chars, from, len));
	}

	// --- SKIP WHITESPACE CHARACTERS ---

	protected static final void skipWhitespaces(Source src) {
		if (src.idx <= src.last) {
			src.ch = src.chars[src.idx];
		}
		if (src.ch <= 32) {
			for (; src.idx <= src.last; src.idx++) {
				if (src.chars[src.idx] > 32) {
					break;
				}
			}
			src.ch = src.chars[src.idx];
		}
	}

	// --- EXTERNAL JSON FORMATTER ---

	public static final String format(String json) {
		final int len = json.length();
		if (len < 3) {
			return json;
		}
		final StringTokenizer st = new StringTokenizer(json, ",{}[]", true);
		final StringBuilder out = new StringBuilder(len * 2);
		String token;
		int indent = 0;
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if ("{".equals(token) || "[".equals(token)) {
				indent++;
				out.append(token);
				appendIndent(out, indent);
				continue;
			}
			if ("}".equals(token) || "]".equals(token)) {
				indent--;
				appendIndent(out, indent);
				out.append(token);
				continue;
			}
			if (",".equals(token)) {
				out.append(',');
				appendIndent(out, indent);
				continue;
			}
			out.append(token);
		}
		return out.toString();
	}

}
