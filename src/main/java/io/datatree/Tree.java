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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.datatree.dom.BASE64;
import io.datatree.dom.Config;
import io.datatree.dom.DeepCloner;
import io.datatree.dom.TreeReaderRegistry;
import io.datatree.dom.TreeWriterRegistry;
import io.datatree.dom.builtin.JavaBuiltin;
import io.datatree.dom.builtin.JsonBuiltin;
import io.datatree.dom.converters.DataConverterRegistry;

/**
 * The Tree class is the container object for a hierarchial document. It
 * represents a single node in the document tree. The Tree provides basic
 * functions for manipulating (get/set, merge, sort, join, filter, stream, etc.)
 * hierarchial data. Many functions can use a simplified path parameter,
 * containing only direct paths: "<code>name1.name2.name3</code>", or
 * "<code>name.array[1].subarray[0]</code>" , like in JavaScript.<br>
 * <br>
 * By using Tree the developers can easily and quickly manipulate complex data
 * structures, and convert these structures into various output formats (eg.
 * JSON, TOML, YAML, XML, CBOR, CSV, TSV, SMILE, MESSAGEPACK, BSON, etc.).<br>
 * <br>
 * Sample code:<br>
 * <br>
 * Tree node = new Tree("{\"a\":1, \"b\":[1,2,3,4]}");<br>
 * node.put("a", 5);<br>
 * node.put("b[0]", 6);<br>
 * System.out.println(node.toString());<br>
 * <br>
 * Output:<br>
 * {<br>
 * "a":5,<br>
 * "b":[6,2,3,4]<br>
 * }<br>
 * <br>
 * for (Tree item: node.get("b")) {<br>
 * System.out.println(item.asInteger());<br>
 * }<br>
 * <br>
 * Output:<br>
 * 6<br>
 * 2<br>
 * 3<br>
 * 4<br>
 * <br>
 * To use another format, use the following syntax:<br>
 * <br>
 * String yaml = node.toString("yaml");<br>
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class Tree implements Iterable<Tree>, Cloneable, Serializable {

	// --- SERIAL VERSION UID ---

	private static final long serialVersionUID = 7044752881424364875L;

	// --- VARIABLES OF THE CURRENT NODE ---

	/**
	 * Parent node (or null if it's the root node).
	 */
	private transient Tree parent;

	/**
	 * Metadata structure (only the document's root node has an optional
	 * metadata node, otherwise it's null).
	 */
	private transient Object meta;

	/**
	 * Name or index of this node.
	 */
	private transient Object key;

	/**
	 * Node's value (String, Integer, List, etc.).
	 */
	private transient Object value;

	// --- PUBLIC CONSTRUCTORS ---

	/**
	 * Creates an empty root structure. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", 123);<br>
	 * System.out.println(node);
	 */
	public Tree() {
		createEmptyNode();
	}

	/**
	 * Constructs a Tree containing the elements of the specified Map.
	 * 
	 * @param value
	 *            the map whose elements are to be placed into this Tree
	 */
	public Tree(Map<String, Object> value) {
		if (value == null) {
			createEmptyNode();
		} else {
			this.value = value;
			moveMeta();
		}
	}

	/**
	 * Constructs a Tree containing the elements of the specified Collection
	 * (eg. List or Set).
	 * 
	 * @param value
	 *            the map whose elements are to be placed into this Collection.
	 */
	public Tree(Collection<Object> value) {
		if (value == null) {
			createEmptyNode();
		} else {
			this.value = value;
		}
	}

	// --- PUBLIC CONSTRUCTORS / TEXT SOURCE ---

	/**
	 * Creates a hierarchial structure by a JSON String. Sample:<br>
	 * <br>
	 * Tree node = new Tree("{\"a\":2,\"b\":\"text\"}");<br>
	 * 
	 * @param source
	 *            JSON source (eg.: {\"c\":[1,2,3,4]})
	 * 
	 * @throws Exception
	 *             any JSON format exception
	 */
	public Tree(String source) throws Exception {
		initFromString(source, null);
	}

	/**
	 * Creates a hierarchial structure by a String. Sample:<br>
	 * <br>
	 * Tree node = new Tree(yamlString, "yaml");<br>
	 * 
	 * @param source
	 *            source in the specified format (JSON, YAML, etc.)
	 * @param format
	 *            name of the format (eg. "json", "xml", "csv", "yaml",
	 *            "properties", "toml", or the name of the reader's class, for
	 *            example "JsonJackson")
	 * 
	 * @throws Exception
	 *             any data format exception
	 */
	public Tree(String source, String format) throws Exception {
		initFromString(source, format);
	}

	// --- PUBLIC CONSTRUCTORS / FILE SOURCE ---

	/**
	 * Loads a hierarchial structure from the specified File. The constructor
	 * tries to guess the file format based on its extension (for example,
	 * "file.json" will be in JSON format, "file.bson" will be in BSON format).
	 * If it fails to figure out the format, it will use the JSON format.
	 * Sample:<br>
	 * <br>
	 * Tree node = new Tree(new File("/path/to/source.json"));<br>
	 * or<br>
	 * Tree node = new Tree(new File("/path/to/source.msgpack"));<br>
	 * 
	 * @param source
	 *            source File in a supported format (JSON, YAML, etc.)
	 * 
	 * @throws Exception
	 *             any data format exception
	 */
	public Tree(File source) throws Exception {
		this(source, getFormatByExtension(source));
	}

	/**
	 * Loads a hierarchial structure by the specified File and format. Sample:
	 * <br>
	 * <br>
	 * Tree node = new Tree(new File("/path/to/source.data"), "json");<br>
	 * or<br>
	 * Tree node = new Tree(new File("/path/to/source.data"), "msgpack");<br>
	 * 
	 * @param source
	 *            source File in the specified format (JSON, YAML, etc.)
	 * @param format
	 *            name of the format (eg. "json", "xml", "csv", "yaml",
	 *            "properties", "toml", or the name of the reader's class, for
	 *            example "JsonJackson")
	 * 
	 * @throws Exception
	 *             any data format exception
	 */
	public Tree(File source, String format) throws Exception {
		this(new FileInputStream(source), format, true);
	}

	// --- PUBLIC CONSTRUCTORS / URL SOURCE ---

	/**
	 * Loads a hierarchial structure from the specified URL. The method tries to
	 * guess the file format based on its extension (for example, "file.json"
	 * will be in JSON format, "file.bson" will be in BSON format). If it fails
	 * to figure out the format, it will use the JSON format. Sample:<br>
	 * <br>
	 * Tree node = new Tree(new URL("http://server/source.json"));<br>
	 * or<br>
	 * Tree node = new Tree(new URL("http://server/source.msgpack"));<br>
	 * 
	 * @param source
	 *            URL of the source
	 * 
	 * @throws Exception
	 *             any data format exception
	 */
	public Tree(URL source) throws Exception {
		this(source, getFormatByExtension(source));
	}

	/**
	 * Loads a hierarchial structure by the specified URL and format. Sample:
	 * <br>
	 * <br>
	 * Tree node = new Tree(new URL("http://server/source.data"), "json");<br>
	 * or<br>
	 * Tree node = new Tree(new URL("http://server/source.data"), "msgpack");
	 * <br>
	 * 
	 * @param source
	 *            source URL
	 * @param format
	 *            name of the format (eg. "json", "xml", "csv", "yaml",
	 *            "properties", "toml", or the name of the reader's class, for
	 *            example "JsonJackson")
	 * 
	 * @throws Exception
	 *             any data format exception
	 */
	public Tree(URL source, String format) throws Exception {
		this(source.openStream(), format);
	}

	// --- PUBLIC CONSTRUCTORS / BYTE CHANNEL SOURCE ---

	/**
	 * Loads a hierarchial structure from the specified ReadableByteChannel, in
	 * JSON format. Closes the source Channel. Sample: <br>
	 * <br>
	 * Tree node = new Tree(source);<br>
	 * 
	 * @param source
	 *            source Channel
	 * 
	 * @throws Exception
	 *             any data format exception
	 */
	public Tree(ReadableByteChannel source) throws Exception {
		this(source, null, true);
	}

	/**
	 * Loads a hierarchial structure from the specified ReadableByteChannel, in
	 * the specified format. Closes the source Channel. Sample: <br>
	 * <br>
	 * Tree node = new Tree(source, "json");<br>
	 * 
	 * @param source
	 *            source Channel
	 * @param format
	 *            name of the format (eg. "json", "xml", "csv", "yaml",
	 *            "properties", "toml", or the name of the reader's class, for
	 *            example "JsonJackson")
	 * 
	 * @throws Exception
	 *             any data format exception
	 */
	public Tree(ReadableByteChannel source, String format) throws Exception {
		this(source, format, true);
	}

	/**
	 * Loads a hierarchial structure from the specified ReadableByteChannel, in
	 * the specified format. Sample: <br>
	 * <br>
	 * Tree node = new Tree(source, "json", true);<br>
	 * 
	 * @param source
	 *            source Channel
	 * @param format
	 *            name of the format (eg. "json", "xml", "csv", "yaml",
	 *            "properties", "toml", or the name of the reader's class, for
	 *            example "JsonJackson")
	 * @param closeSource
	 *            close the source Channel (true = close)
	 * 
	 * @throws Exception
	 *             any data format exception
	 */
	public Tree(ReadableByteChannel source, String format, boolean closeSource) throws Exception {
		try {
			ByteBuffer packet = ByteBuffer.allocate(4096);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream(4096);
			int count;
			while ((count = source.read(packet)) != -1) {
				buffer.write(packet.array(), 0, count);
				packet.rewind();
			}
			initFromBytes(buffer.toByteArray(), format);
		} finally {
			if (closeSource && source != null) {
				try {
					source.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	// --- PUBLIC CONSTRUCTORS / STREAM SOURCE ---

	/**
	 * Loads a hierarchial structure from the specified InputStream, in JSON
	 * format. Closes the source Stream. Sample: <br>
	 * <br>
	 * Tree node = new Tree(source);<br>
	 * 
	 * @param source
	 *            source Stream
	 * 
	 * @throws Exception
	 *             any data format exception
	 */
	public Tree(InputStream source) throws Exception {
		this(source, null, true);
	}

	/**
	 * Loads a hierarchial structure from the specified InputStream, in the
	 * specified format. Closes the source Stream. Sample: <br>
	 * <br>
	 * Tree node = new Tree(source, "json");<br>
	 * 
	 * @param source
	 *            source Stream
	 * @param format
	 *            name of the format (eg. "json", "xml", "csv", "yaml",
	 *            "properties", "toml", or the name of the reader's class, for
	 *            example "JsonJackson")
	 * 
	 * @throws Exception
	 *             any data format exception
	 */
	public Tree(InputStream source, String format) throws Exception {
		this(source, format, true);
	}

	/**
	 * Loads a hierarchial structure from the specified InputStream, in the
	 * specified format. Sample: <br>
	 * <br>
	 * Tree node = new Tree(source, "json", true);<br>
	 * 
	 * @param source
	 *            source Stream
	 * @param format
	 *            name of the format (eg. "json", "xml", "csv", "yaml",
	 *            "properties", "toml", or the name of the reader's class, for
	 *            example "JsonJackson")
	 * @param closeSource
	 *            close the source Stream (true = close)
	 * 
	 * @throws Exception
	 *             any data format exception
	 */
	public Tree(InputStream source, String format, boolean closeStream) throws Exception {
		try {
			byte[] bytes = new byte[0];
			byte[] packet = new byte[4096];
			int count;
			while ((count = source.read(packet)) != -1) {
				byte[] resized = new byte[bytes.length + count];
				System.arraycopy(bytes, 0, resized, 0, bytes.length);
				System.arraycopy(packet, 0, resized, bytes.length, count);
				bytes = resized;
			}
			initFromBytes(bytes, format);
		} finally {
			if (closeStream && source != null) {
				try {
					source.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	// --- PUBLIC CONSTRUCTORS / BINARY ARRAY SOURCE ---

	/**
	 * Creates a hierarchial structure by a JSON byte array. Sample:<br>
	 * <br>
	 * byte[] bytes = // JSON bytes<br>
	 * Tree node = new Tree(bytes);<br>
	 * 
	 * @param source
	 *            JSON source
	 * 
	 * @throws Exception
	 *             any JSON format exception
	 */
	public Tree(byte[] source) throws Exception {
		initFromBytes(source, null);
	}

	/**
	 * Creates a hierarchial structure by a JSON, XML, YAML, CBOR, SMILE, CSV,
	 * TOML or other binary source array. Sample code:<br>
	 * <br>
	 * byte[] bytes = // loaded from file<br>
	 * Tree node = new Tree(bytes, "msgpack");<br>
	 * 
	 * @param source
	 *            data structure in binary format
	 * @param format
	 *            name of the format (eg. "java", "smile", "cbor", "ion",
	 *            "bson", or the name of the reader's class, for example
	 *            "BsonJackson")
	 * 
	 * @throws Exception
	 *             any format exception
	 */
	public Tree(byte[] source, String format) throws Exception {
		initFromBytes(source, format);
	}

	protected void initFromString(String source, String format) throws Exception {
		if (source == null || source.isEmpty()) {
			createEmptyNode();
		} else {
			value = TreeReaderRegistry.getReader(format).parse(source);
			moveMeta();
		}
	}

	protected void initFromBytes(byte[] source, String format) throws Exception {
		if (source == null || source.length == 0) {
			createEmptyNode();
		} else {
			value = TreeReaderRegistry.getReader(format).parse(source);
			moveMeta();
		}
	}

	/**
	 * Initalizes this node value with an empty Map.
	 */
	protected void createEmptyNode() {
		value = new LinkedHashMap<String, Object>();
	}

	/**
	 * Removes "_meta" entry from the node's Map.
	 */
	@SuppressWarnings("rawtypes")
	protected void moveMeta() {
		if (isMap()) {
			meta = ((Map) value).remove(Config.META);
		}
	}

	protected static String getFormatByExtension(URL url) {
		if (url != null) {
			String path = url.toString();
			int i = path.lastIndexOf('?');
			if (i > -1) {
				path = path.substring(0, i);
			}
			return getFormatByExtension(path);
		}
		return null;
	}

	protected static String getFormatByExtension(File file) {
		if (file != null) {
			return getFormatByExtension(file.getName());
		}
		return null;
	}

	protected static String getFormatByExtension(String path) {
		if (path != null) {
			int i = path.lastIndexOf('.');
			if (i > -1 && i < path.length() - 1) {
				String ext = path.substring(i + 1).toLowerCase();
				if ("yml".equals(ext)) {
					ext = "yaml";
				}
				if (TreeReaderRegistry.isAvailable(ext)) {
					return ext;
				}
			}
		}
		return null;
	}

	// --- PROTECTED CONSTRUCTORS ---

	protected Tree(Tree parent, Object key, Object value) {
		this.parent = parent;
		this.key = key;
		this.value = value;
	}

	/**
	 * Creates a new Tree by raw value and meta Objects.
	 * 
	 * @param value
	 *            value (Map, Set, List, or Object array)
	 * @param meta
	 *            meta container (Map or {@code null})
	 */
	protected Tree(Object value, Object meta) {
		if (value == null) {
			createEmptyNode();
		} else {
			this.value = value;
		}
		this.meta = meta;
	}

	// --- NAME OF THIS NODE ----

	/**
	 * Returns the name of this node. Sample code:<br>
	 * <br>
	 * Tree node = new Tree("{\"a\":3}");<br>
	 * System.out.println(node.get("a").getName());<br>
	 * <br>
	 * This code above prints "a".
	 * 
	 * @return name
	 */
	public String getName() {
		if (key == null) {
			return null;
		}
		return String.valueOf(key);
	}

	/**
	 * Changes this node's name. Sample code:<br>
	 * <br>
	 * Tree node = new Tree("{\"a\":3}");<br>
	 * node.get("a").setName("b");<br>
	 * System.out.println(node.toJSON());<br>
	 * <br>
	 * This code above prints "{\"b\":3}".
	 * 
	 * @param name
	 *            the new name
	 * 
	 * @return this node
	 */
	public Tree setName(String name) {
		if (parent == null) {
			throw new UnsupportedOperationException("Root node has no name!");
		}
		if (!parent.isMap()) {
			throw new UnsupportedOperationException("Unable to set name (this node's parent is not a Map)!");
		}
		parent.remove((String) key);
		key = name;
		parent.putObjectInternal(name, value, false);
		return this;
	}

	// --- PATH ---

	/**
	 * Returns the absolute path of this node. Sample:<br>
	 * <br>
	 * Tree node = new Tree("{\"a\":{\"b\":[1,2,3,4]}}");<br>
	 * String path = node.get("a").get("b").get(1).getPath();
	 * System.out.println(path);<br>
	 * <br>
	 * This code above prints "a.b[1]".
	 * 
	 * @return path of this node
	 */
	public String getPath() {
		return getPath(new StringBuilder(32), 0, false).toString();
	}

	/**
	 * Returns the absolute path of this node. Sample:<br>
	 * <br>
	 * Tree node = new Tree("{\"a\":{\"b\":[1,2,3,4]}}");<br>
	 * String path = node.get("a").get("b").get(1).getPath(1);<br>
	 * System.out.println(path);<br>
	 * <br>
	 * This code above prints "a.b[2]".
	 * 
	 * @param startIndex
	 *            first index within array (startIndex = 0 -&gt; zero based
	 *            array-indexing)
	 * 
	 * @return path of this node
	 */
	public String getPath(int startIndex) {
		return getPath(new StringBuilder(32), startIndex, false).toString();
	}

	/**
	 * Recursive path-builder method.
	 * 
	 * @param path
	 *            path builder
	 * @param startIndex
	 *            first index within array (startIndex = 0 -&gt; zero based
	 *            array-indexing)
	 * @param addPoint
	 *            a point is insertable into the path
	 * 
	 * @return path of this node
	 */
	protected StringBuilder getPath(StringBuilder path, int startIndex, boolean addPoint) {
		boolean simple = true;
		if (key != null) {
			if (addPoint && path.length() > 0) {
				path.insert(0, '.');
			}
			if (key instanceof Integer) {
				path.insert(0, ']');
				if (startIndex == 0) {
					path.insert(0, key);
				} else {
					path.insert(0, startIndex + (int) key);
				}
				path.insert(0, '[');
				simple = false;
			} else {
				path.insert(0, key);
			}
		}
		if (parent != null) {
			parent.getPath(path, startIndex, simple);
		}
		return path;
	}

	// --- NODE TYPE ---

	/**
	 * Returns the value's class of this node.
	 * 
	 * @return class of value (or null)
	 */
	public Class<?> getType() {
		return value == null ? null : value.getClass();
	}

	/**
	 * Sets this node's type and converts the value into the specified type.
	 * 
	 * @param type
	 *            new type
	 * 
	 * @return this node
	 */
	public Tree setType(Class<?> type) {
		if (value == null || value.getClass() == type) {
			return this;
		}
		value = DataConverterRegistry.convert(type, value);
		if (parent != null && key != null) {
			if (key instanceof String) {
				parent.putObjectInternal((String) key, value, false);
			} else {
				parent.remove((int) key);
				parent.insertObjectInternal((int) key, value);
			}
		}
		return this;
	}

	// --- PARENT NODE ---

	/**
	 * Returns this node's parent. If this node is a root node, returns null.
	 * 
	 * @return parent node, or null
	 */
	public Tree getParent() {
		return parent;
	}

	// --- ROOT NODE ---

	/**
	 * Returns the top-level (document) node of the structure.
	 * 
	 * @return top-level document node
	 */
	public Tree getRoot() {
		if (parent != null) {
			return parent.getRoot();
		}
		return this;
	}

	// --- METADATA CONTAINER ---

	/**
	 * Returns the metadata node of this document structure. Creates new
	 * metadata node, if it doesn't exist. Meta node contains optional
	 * processing instructions and variables (language code, session ID, request
	 * and response variables, etc). One document (hierarchial node structure)
	 * has only one meta node at root level.
	 * 
	 * @return metadata node
	 */
	public Tree getMeta() {
		return getMeta(true);
	}

	/**
	 * Returns the metadata node (or null, if it doesn't exist and the
	 * "createIfNotExists" parameter is false).
	 * 
	 * @param createIfNotExists
	 *            create metadata node, if it doesn't exist
	 * 
	 * @return metadata node
	 */
	public Tree getMeta(boolean createIfNotExists) {
		Tree root = getRoot();
		if (root.meta == null) {
			if (createIfNotExists) {
				root.meta = new LinkedHashMap<String, Object>();
			} else {
				return null;
			}
		}
		return new Tree(root, Config.META, root.meta);
	}

	/**
	 * Returns {@code true}, if this document contains a metadata node.
	 * 
	 * @return {@code true}, if this document has a metadata node
	 */
	public boolean hasMeta() {
		return getMeta(false) != null;
	}

	/**
	 * Returns {@code true}, if this node is the part of the metadata structure.
	 * Sample: <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("_meta.property", true);<br>
	 * or<br>
	 * node.getMeta().put("property", true);<br>
	 * <br>
	 * The node.get("_meta.property").isMeta() returns {@code true}.
	 * 
	 * @return {@code true}, if the document's metadata structure contains this
	 *         node
	 */
	public boolean isMeta() {
		if (parent != null) {
			if (value != null && value == parent.meta) {
				return true;
			}
			return parent.isMeta();
		}
		return false;
	}

	// --- VALUE SETTER METHODS ---

	/**
	 * Sets the current node's value to the specified byte. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set((byte) 4);
	 * 
	 * @param value
	 *            new value
	 * 
	 * @return this node
	 */
	public Tree set(byte value) {
		return setObjectInternal(value).parent;
	}

	/**
	 * Sets the current node's value to the specified short value. Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set((short) 4);
	 * 
	 * @param value
	 *            new value
	 * 
	 * @return this node
	 */
	public Tree set(short value) {
		return setObjectInternal(value);
	}

	/**
	 * Sets the current node's value to the specified integer value. Sample
	 * code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set(4);
	 * 
	 * @param value
	 *            new value
	 * 
	 * @return this node
	 */
	public Tree set(int value) {
		return setObjectInternal(value);
	}

	/**
	 * Sets the current node's value to the specified long value. Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set(4L);
	 * 
	 * @param value
	 *            new value
	 * 
	 * @return this node
	 */
	public Tree set(long value) {
		return setObjectInternal(value);
	}

	/**
	 * Sets the current node's value to the specified float value. Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set(4f);
	 * 
	 * @param value
	 *            new value
	 * 
	 * @return this node
	 */
	public Tree set(float value) {
		return setObjectInternal(value);
	}

	/**
	 * Sets the current node's value to the specified double value. Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set(3.1415);
	 * 
	 * @param value
	 *            new value
	 * 
	 * @return this node
	 */
	public Tree set(double value) {
		return setObjectInternal(value);
	}

	/**
	 * Sets the current node's value to the specified boolean value. Sample
	 * code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set(false);
	 * 
	 * @param value
	 *            new value
	 * 
	 * @return this node
	 */
	public Tree set(boolean value) {
		return setObjectInternal(value);
	}

	/**
	 * Sets the current node's value to the specified byte array. Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set("xyz".getBytes());
	 * 
	 * @param value
	 *            new value
	 * 
	 * @return this node
	 */
	public Tree set(byte[] value) {
		return setObjectInternal(value);
	}

	/**
	 * Sets the current node's value to the specified byte array. Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set("xyz".getBytes(), true);
	 * 
	 * @param value
	 *            new value
	 * @param asBase64String
	 *            set value as BASE64 String
	 * 
	 * @return this node
	 */
	public Tree set(byte[] value, boolean asBase64String) {
		if (asBase64String) {
			return setObjectInternal(BASE64.encode(value));
		}
		return setObjectInternal(value);
	}

	/**
	 * Sets the current node's value to the specified text. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set("new value");
	 * 
	 * @param value
	 *            new value
	 * 
	 * @return this node
	 */
	public Tree set(String value) {
		return setObjectInternal(value);
	}

	/**
	 * Sets the current node's value to the specified date. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set(new Date());
	 * 
	 * @param value
	 *            new value
	 * 
	 * @return this node
	 */
	public Tree set(Date value) {
		return setObjectInternal(value);
	}

	/**
	 * Sets the current node's value to the specified UUID. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set(UUID.randomUUID());
	 * 
	 * @param value
	 *            new value
	 * 
	 * @return this node
	 */
	public Tree set(UUID value) {
		return setObjectInternal(value);
	}

	/**
	 * Sets the current node's value to the specified BigDecimal. Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set(BigDecimal.ONE);
	 * 
	 * @param value
	 *            new value
	 * 
	 * @return this node
	 */
	public Tree set(BigDecimal value) {
		return setObjectInternal(value);
	}

	/**
	 * Sets the current node's value to the specified BigInteger. Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set(BigInteger.ZERO);
	 * 
	 * @param value
	 *            new value
	 * 
	 * @return this node
	 */
	public Tree set(BigInteger value) {
		return setObjectInternal(value);
	}

	/**
	 * Sets the current node's value to the specified InetAddress. Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the value:<br>
	 * node.get("a.b.c").set(InetAddress.getLocalHost());
	 * 
	 * @param value
	 *            new value
	 * 
	 * @return this node
	 */
	public Tree set(InetAddress value) {
		return setObjectInternal(value);
	}

	/**
	 * Sets the current node's type to Map (~= JSON object). Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the boolean value to Map:<br>
	 * Tree map = node.get("a.b.c").setMap();
	 * 
	 * @return this node
	 */
	public Tree setMap() {
		return setObjectInternal(new LinkedHashMap<String, Object>());
	}

	/**
	 * Sets the current node's type to List (~= JSON array). Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the boolean value to List:<br>
	 * Tree list = node.get("a.b.c").setList();
	 * 
	 * @return this node
	 */
	public Tree setList() {
		return setObjectInternal(new LinkedList<Object>());
	}

	/**
	 * Sets the current node's type to Set. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);<br>
	 * <br>
	 * For change the boolean value to Set:<br>
	 * Tree set = node.get("a.b.c").setSet();
	 * 
	 * @return this node
	 */
	public Tree setSet() {
		return setObjectInternal(new LinkedHashSet<Object>());
	}

	public Tree setObject(Object value) {
		return setObjectInternal(getNodeValue(value));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Tree setObjectInternal(Object value) {
		if (parent != null) {
			if (parent.isMap()) {

				((Map) parent.value).put(key, value);

			} else if (parent.isList()) {

				((List) parent.value).set((Integer) key, value);

			} else if (parent.isSet()) {

				Set set = (Set) parent.value;
				set.remove(this.value);
				set.add(value);

			} else if (parent.isArray()) {

				if (parent.value.getClass().getComponentType().isAssignableFrom(value.getClass())) {
					Array.set(parent.value, (Integer) key, value);
				} else {
					LinkedList<Object> list = new LinkedList<>();
					int len = Array.getLength(parent.value);
					for (int i = 0; i < len; i++) {
						list.addLast(Array.get(parent.value, i));
					}
					list.set((Integer) key, value);
					parent.setObjectInternal(list);
				}

			} else {
				throw new UnsupportedOperationException(
						"Unable to replace child (parent isn't a List, Set, Array or Map)!");
			}
		}
		this.value = value;
		return new Tree(parent, key, value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object getNodeValue(Object value) {
		if (value != null) {
			if (value instanceof Tree) {
				return ((Tree) value).value;
			}
			Class clazz = value.getClass();
			Object test;
			if (clazz.isArray()) {
				if (clazz.getComponentType().isPrimitive()) {
					return value;
				}
				test = new LinkedList<>(Arrays.asList((Object[]) value));
			} else {
				test = value;
			}
			if (test instanceof Collection) {
				Collection<Object> c = (Collection) test;
				boolean foundNode = false;
				for (Object o : c) {
					if (o != null && o instanceof Tree) {
						foundNode = true;
						break;
					}
				}
				if (!foundNode) {
					return value;
				}
				LinkedList<Object> list = new LinkedList<>();
				for (Object o : c) {
					if (o != null && o instanceof Tree) {
						list.addLast(((Tree) o).value);
					} else {
						list.addLast(o);
					}
				}
				return list;
			}
		}
		return value;
	}

	// --- ADD ITEM TO LIST / SET (~=JSON ARRAY) ---

	/**
	 * Appends the specified byte value to the end of this List (or adds the
	 * value to this Set - it depends on the type of this node). Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add(1).add(2);
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(byte value) {
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified short value to the end of this List (or adds the
	 * value to this Set - it depends on the type of this node). Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add((short) 1);
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(short value) {
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified int value to the end of this List (or adds the
	 * value to this Set - it depends on the type of this node). Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add(1).add(2);
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(int value) {
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified long value to the end of this List (or adds the
	 * value to this Set - it depends on the type of this node). Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add(1L).add(2L);
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(long value) {
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified float value to the end of this List (or adds the
	 * value to this Set - it depends on the type of this node). Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add((float) 3);
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(float value) {
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified double value to the end of this List (or adds the
	 * value to this Set - it depends on the type of this node). Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add(1.2).add(1.3);
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(double value) {
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified boolean value to the end of this List (or adds the
	 * value to this Set - it depends on the type of this node). Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add(true).add(false);
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(boolean value) {
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified byte array to the end of this List (or adds the
	 * value to this Set - it depends on the type of this node). Sample code:
	 * <br>
	 * <br>
	 * byte[] b1 = QFiles.readFile("path/to/file1");<br>
	 * byte[] b2 = QFiles.readFile("path/to/file2");<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add(b1).add(b2);
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(byte[] value) {
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified byte array to the end of this List (or adds the
	 * value to this Set - it depends on the type of this node). Sample code:
	 * <br>
	 * <br>
	 * byte[] b1 = QFiles.readFile("path/to/file1");<br>
	 * byte[] b2 = QFiles.readFile("path/to/file2");<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add(b1, true).add(b2, true);
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * @param asBase64String
	 *            store the byte array as BASE64 String
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(byte[] value, boolean asBase64String) {
		if (asBase64String) {
			return addObjectInternal(BASE64.encode(value));
		}
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified String to the end of this List (or adds the value
	 * to this Set - it depends on the type of this node). Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add("a").add("b");
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(String value) {
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified Date value to the end of this List (or adds the
	 * value to this Set - it depends on the type of this node). Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add(new Date());
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(Date value) {
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified UUID value to the end of this List (or adds the
	 * value to this Set - it depends on the type of this node). Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add(UUID.randomUUID());
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(UUID value) {
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified BigDecimal to the end of this List (or adds the
	 * value to this Set - it depends on the type of this node). Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add(new BigDecimal("1234567"));
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(BigDecimal value) {
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified BigInteger to the end of this List (or adds the
	 * value to this Set - it depends on the type of this node). Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add(new BigInteger("1234567"));
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(BigInteger value) {
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified InetAddress to the end of this List (or adds the
	 * value to this Set - it depends on the type of this node). Sample code:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("path.to.array").add(InetAddress.getLocalHost());
	 * 
	 * @param value
	 *            new value to be added to this list (or set)
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree add(InetAddress value) {
		return addObjectInternal(value);
	}

	/**
	 * Appends the specified Map to the end of this List (or adds the value to
	 * this Set - it depends on the type of this node). Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * Tree array = node.putList("path.to.array");<br>
	 * Tree map1 = array.addMap().put("a", 1).put("b", 2);<br>
	 * Tree map2 = array.addMap().put("c", 3).put("d", 4);
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree addMap() {
		return addObjectInternal(new LinkedHashMap<String, Object>());
	}

	/**
	 * Appends the specified List to the end of this List (or adds the value to
	 * this Set - it depends on the type of this node). Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * Tree array = node.putList("path.to.array");<br>
	 * Tree map1 = array.addList().add(1).add(2);<br>
	 * Tree map2 = array.addList().add("a").add("b");
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree addList() {
		return addObjectInternal(new LinkedList<Object>());
	}

	/**
	 * Appends the specified Set to the end of this List (or adds the value to
	 * this Set - it depends on the type of this node). Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * Tree array = node.putList("path.to.array");<br>
	 * Tree map1 = array.addSet().add(1).add(2);<br>
	 * Tree map2 = array.addSet().add("a").add("b");
	 * 
	 * @return this (the List or Set) node
	 */
	public Tree addSet() {
		return addObjectInternal(new LinkedHashSet<Object>());
	}

	/**
	 * Adds a new node to this node. This node must be a Set or List or an empty
	 * Map.
	 * 
	 * @param value
	 *            value of the new node
	 * 
	 * @return container node
	 */
	public Tree addObject(Object value) {
		return addObjectInternal(getNodeValue(value));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Tree addObjectInternal(Object value) {
		if (isList() || isSet()) {

			Collection<Object> col = (Collection) this.value;
			try {
				col.add(value);
			} catch (UnsupportedOperationException unableToModify) {
				LinkedList<Object> list = new LinkedList<>(col);
				list.add(value);
				setObjectInternal(list);
			}

		} else if (isMap()) {

			// Change type from Map to List
			List<Object> list = asList(Object.class);
			list.add(value);
			setObjectInternal(list);

		} else if (isArray()) {

			LinkedList<Object> list = new LinkedList<>(asList(Object.class));
			list.add(value);
			setObjectInternal(list);

		} else {
			throw new UnsupportedOperationException("Unable to add child (this node's type is a " + value.getClass()
					+ ", not a List, Set or an empty Map)!");
		}
		if (isStructure(value)) {
			return new Tree(this, 0, value);
		}
		return this;
	}

	// --- INSERT INTO LIST (~=JSON ARRAY) FUNCTIONS ---

	/**
	 * Inserts the specified byte value at the specified position in this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            byte value to be inserted
	 * 
	 * @return this node
	 */
	public Tree insert(int index, byte value) {
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified short value at the specified position in this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            short value to be inserted
	 * 
	 * @return this node
	 */
	public Tree insert(int index, short value) {
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified integer value at the specified position in this
	 * List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            integer value to be inserted
	 * 
	 * @return this node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insert(int index, int value) {
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified long value at the specified position in this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            long value to be inserted
	 * 
	 * @return this node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insert(int index, long value) {
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified float value at the specified position in this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            float value to be inserted
	 * 
	 * @return this node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insert(int index, float value) {
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified double value at the specified position in this
	 * List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            double value to be inserted
	 * 
	 * @return this node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insert(int index, double value) {
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified boolean at the specified position in this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            boolean value to be inserted
	 * 
	 * @return this node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insert(int index, boolean value) {
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified byte array at the specified position in this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            array value to be inserted
	 * 
	 * @return this node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insert(int index, byte[] value) {
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified byte array at the specified position in this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            array value to be inserted
	 * @param asBase64String
	 *            store byte array as BASE64 String
	 * 
	 * @return this node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insert(int index, byte[] value, boolean asBase64String) {
		if (asBase64String) {
			return insertObjectInternal(index, BASE64.encode(value));
		}
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified String at the specified position in this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            String to be inserted
	 * 
	 * @return this node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insert(int index, String value) {
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified Date at the specified position in this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            Date to be inserted
	 * 
	 * @return this node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insert(int index, Date value) {
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified UUID at the specified position in this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            UUID to be inserted
	 * 
	 * @return this node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insert(int index, UUID value) {
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified BigDecimal at the specified position in this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            BigDecimal to be inserted
	 * 
	 * @return this node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insert(int index, BigDecimal value) {
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified BigInteger at the specified position in this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            BigInteger to be inserted
	 * 
	 * @return this node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insert(int index, BigInteger value) {
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified InetAddress at the specified position in this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param value
	 *            InetAddress to be inserted
	 * 
	 * @return this node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insert(int index, InetAddress value) {
		return insertObjectInternal(index, value);
	}

	/**
	 * Inserts the specified Map (~=JSON object) at the specified position in
	 * this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * 
	 * @return the new newly inserted Map node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insertMap(int index) {
		return insertObjectInternal(index, new LinkedHashMap<String, Object>());
	}

	/**
	 * Inserts the specified List (~=JSON array) at the specified position in
	 * this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * 
	 * @return the new newly inserted List node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insertList(int index) {
		return insertObjectInternal(index, new LinkedList<Object>());
	}

	/**
	 * Inserts the specified Set at the specified position in this List.
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * 
	 * @return the new newly inserted Set node
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 */
	public Tree insertSet(int index) {
		return insertObjectInternal(index, new LinkedHashSet<Object>());
	}

	/**
	 * Inserts a new node at the specified position.
	 * 
	 * @param index
	 *            index of the new node
	 * @param value
	 *            value of the new node
	 * 
	 * @return container node
	 */
	public Tree insertObject(int index, Object value) {
		return insertObjectInternal(index, getNodeValue(value));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Tree insertObjectInternal(int index, Object value) {
		if (isList()) {

			Collection<Object> col = (Collection) this.value;
			try {
				((List) this.value).add(index, value);
			} catch (UnsupportedOperationException unableToModify) {
				LinkedList<Object> list = new LinkedList<>(col);
				list.add(index, value);
				setObjectInternal(list);
			}

		} else if (isSet()) {

			return addObjectInternal(value);

		} else if (isArray()) {

			LinkedList<Object> list = new LinkedList();
			int len = Array.getLength(this.value);
			for (int i = 0; i < len; i++) {
				list.addLast(Array.get(this.value, i));
			}
			list.add(index, value);
			setObjectInternal(list);

		} else if (isMap()) {

			if (isEmpty()) {

				// Change type to List
				setObjectInternal(new LinkedList(Collections.singleton(value)));

			} else {
				throw new UnsupportedOperationException("Unable to insert element into a non-empty Map! "
						+ "Use \"put\" method to insert an element into a Map.");
			}

		} else {
			throw new UnsupportedOperationException("Unable to insert child (this node's type is a " + value.getClass()
					+ ", not a List, Set, Array or an empty Map)!");
		}
		if (isStructure(value)) {
			return new Tree(this, index, value);
		}
		return this;
	}

	// --- PATH-BASED VALUE SETTERS ---

	/**
	 * Associates the specified String value with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", "new value");
	 * 
	 * @param path
	 *            path with which the specified UTF8 is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * 
	 * @return this node
	 */
	public Tree put(String path, String value) {
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified int value with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", 123);
	 * 
	 * @param path
	 *            path with which the specified INT32 is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * 
	 * @return this node
	 */
	public Tree put(String path, int value) {
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified double value with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("myProperty", 3.14);
	 * 
	 * @param path
	 *            path with which the specified DOUBLE is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * 
	 * @return this node
	 */
	public Tree put(String path, double value) {
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified byte value with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", (byte) 2);
	 * 
	 * @param path
	 *            path with which the specified BYTE is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * 
	 * @return this node
	 */
	public Tree put(String path, byte value) {
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified float value with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", 3f);
	 * 
	 * @param path
	 *            path with which the specified FLOAT is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * 
	 * @return this node
	 */
	public Tree put(String path, float value) {
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified long value with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("longValue", 4L);
	 * 
	 * @param path
	 *            path with which the specified LONG is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * 
	 * @return this node
	 */
	public Tree put(String path, long value) {
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified boolean value with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", true);
	 * 
	 * @param path
	 *            path with which the specified BOOLEAN is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * 
	 * @return this node
	 */
	public Tree put(String path, boolean value) {
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified byte array with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", "test".getBytes());
	 * 
	 * @param path
	 *            path with which the specified BYTES is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * 
	 * @return this node
	 */
	public Tree put(String path, byte[] value) {
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified byte array with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", "test".getBytes());
	 * 
	 * @param path
	 *            path with which the specified BYTES is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * @param asBase64String
	 *            store byte array as BASE64 String
	 * 
	 * @return this node
	 */
	public Tree put(String path, byte[] value, boolean asBase64String) {
		if (asBase64String) {
			return putObjectInternal(path, BASE64.encode(value), false);
		}
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified short value with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", (short) 4);
	 * 
	 * @param path
	 *            path with which the specified SHORT is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * 
	 * @return this node
	 */
	public Tree put(String path, short value) {
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified UUID value with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", UUID.randomUUID());
	 * 
	 * @param path
	 *            path with which the specified UUID is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * 
	 * @return this node
	 */
	public Tree put(String path, UUID value) {
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified Date value with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", new Date());
	 * 
	 * @param path
	 *            path with which the specified TIMESTAMP is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * 
	 * @return this node
	 */
	public Tree put(String path, Date value) {
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified InetAddress value with the specified path. If
	 * the structure previously contained a mapping for the path, the old value
	 * is replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", InetAddress.getLocalHost());
	 * 
	 * @param path
	 *            path with which the specified INET_ADDRESS is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * 
	 * @return this node
	 */
	public Tree put(String path, InetAddress value) {
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified BigInteger value with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", BigInteger.ONE);
	 * 
	 * @param path
	 *            path with which the specified INTEGER is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * 
	 * @return this node
	 */
	public Tree put(String path, BigInteger value) {
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified BigDecimal value with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a.b.c", BigDecimal.ONE);
	 * 
	 * @param path
	 *            path with which the specified DECIMAL is to be associated
	 * @param value
	 *            value to be associated with the specified path
	 * 
	 * @return this node
	 */
	public Tree put(String path, BigDecimal value) {
		return putObjectInternal(path, value, false);
	}

	/**
	 * Associates the specified Map (~= JSON object) container with the
	 * specified path. If the structure previously contained a mapping for the
	 * path, the old value is replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * Tree map = node.putMap("a.b.c");<br>
	 * map.put("d.e.f", 123);
	 * 
	 * @param path
	 *            path with which the specified Map is to be associated
	 * 
	 * @return Tree of the new Map
	 */
	public Tree putMap(String path) {
		return putObjectInternal(path, new LinkedHashMap<String, Object>(), false);
	}

	/**
	 * Associates the specified Map (~= JSON object) container with the
	 * specified path. If the structure previously contained a mapping for the
	 * path, the old value is replaced. Sample code:<br>
	 * <br>
	 * Tree response = ...<br>
	 * Tree headers = response.getMeta().putMap("headers", true);<br>
	 * headers.put("Content-Type", "text/html");
	 * 
	 * @param path
	 *            path with which the specified Map is to be associated
	 * @param putIfAbsent
	 *            if true and the specified key is not already associated with a
	 *            value associates it with the given value and returns the new
	 *            Map, else returns the previous Map
	 * 
	 * @return Tree of the new Map
	 */
	public Tree putMap(String path, boolean putIfAbsent) {
		return putObjectInternal(path, new LinkedHashMap<String, Object>(), putIfAbsent);
	}

	/**
	 * Associates the specified List (~= JSON array) container with the
	 * specified path. If the structure previously contained a mapping for the
	 * path, the old value is replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * Tree list = node.putList("a.b.c");<br>
	 * list.add(1).add(2).add(3);
	 * 
	 * @param path
	 *            path with which the specified List is to be associated
	 * 
	 * @return Tree of the new List
	 */
	public Tree putList(String path) {
		return putObjectInternal(path, new LinkedList<Object>(), false);
	}

	/**
	 * Associates the specified List (~= JSON array) container with the
	 * specified path. If the structure previously contained a mapping for the
	 * path, the old value is replaced. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * <br>
	 * Tree list1 = node.putList("a.b.c");<br>
	 * list1.add(1).add(2).add(3);<br>
	 * <br>
	 * Tree list2 = node.putList("a.b.c", true);<br>
	 * list2.add(4).add(5).add(6);<br>
	 * <br>
	 * The "list2" contains 1, 2, 3, 4, 5 and 6.
	 * 
	 * @param path
	 *            path with which the specified List is to be associated
	 * @param putIfAbsent
	 *            if true and the specified key is not already associated with a
	 *            value associates it with the given value and returns the new
	 *            List, else returns the previous List
	 * 
	 * @return Tree of the new List
	 */
	public Tree putList(String path, boolean putIfAbsent) {
		return putObjectInternal(path, new LinkedList<Object>(), putIfAbsent);
	}

	/**
	 * Associates the specified Set container with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Set similar to List, but contains no duplicate elements. Sample
	 * code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * Tree set = node.putSet("a.b.c");<br>
	 * set.add(1).add(2).add(3);
	 * 
	 * @param path
	 *            path with which the specified Set is to be associated
	 * 
	 * @return Tree of the new Set
	 */
	public Tree putSet(String path) {
		return putObjectInternal(path, new LinkedHashSet<Object>(), false);
	}

	/**
	 * Associates the specified Set container with the specified path. If the
	 * structure previously contained a mapping for the path, the old value is
	 * replaced. Set similar to List, but contains no duplicate elements. Sample
	 * code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * <br>
	 * Tree set1 = node.putSet("a.b.c");<br>
	 * set1.add(1).add(2).add(3);<br>
	 * <br>
	 * Tree set2 = node.putSet("a.b.c", true);<br>
	 * set2.add(4).add(5).add(6);<br>
	 * <br>
	 * The "set2" contains 1, 2, 3, 4, 5 and 6.
	 * 
	 * @param path
	 *            path with which the specified Set is to be associated
	 * @param putIfAbsent
	 *            if true and the specified key is not already associated with a
	 *            value associates it with the given value and returns the new
	 *            Set, else returns the previous Set
	 * 
	 * @return Tree of the new Set
	 */
	public Tree putSet(String path, boolean putIfAbsent) {
		return putObjectInternal(path, new LinkedHashSet<Object>(), putIfAbsent);
	}

	/**
	 * Puts a node with the specified value into the specified path.
	 * 
	 * @param path
	 *            path (e.g. "path.to.node[0]")
	 * @param value
	 *            the new value
	 * 
	 * @return container node
	 */
	public Tree putObject(String path, Object value) {
		return putObjectInternal(path, getNodeValue(value), false);
	}

	/**
	 * Puts a node with the specified value into the specified path.
	 * 
	 * @param path
	 *            path (e.g. "path.to.node")
	 * @param value
	 *            the new value
	 * @param putIfAbsent
	 *            if true and the specified key is not already associated with a
	 *            value associates it with the given value and returns the new
	 *            container, else returns the previous container
	 * 
	 * @return container node
	 */
	public Tree putObject(String path, Object value, boolean putIfAbsent) {
		return putObjectInternal(path, getNodeValue(value), false);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Tree putObjectInternal(String path, Object value, boolean putIfAbsent) {
		Tree parent = getChild(path, true);
		String name;
		int i = path.replace(']', '.').lastIndexOf('.');
		if (i > -1) {
			name = path.substring(i + 1);
		} else {
			name = path;
			if (parent.isEnumeration()) {
				parent.setType(Map.class);
				return parent.putObjectInternal(name, value, putIfAbsent);
			}
		}
		if (parent.isMap()) {

			Map map = (Map) parent.value;
			if (putIfAbsent) {
				Object previous = map.putIfAbsent(name, value);
				if (previous != null) {
					value = previous;
				}
			} else {
				map.put(name, value);
			}

		} else if (parent.isList()) {

			int index = 0;
			int end = path.length() - 1;
			if (end > -1 && path.charAt(end) == ']') {
				int start = path.lastIndexOf('[');
				index = Integer.parseInt(path.substring(start + 1, end));
			}
			if (putIfAbsent) {
				if (index < parent.size()) {
					Tree previous = parent.get(index);
					if (previous.isNull()) {
						parent.resizeAndGetList(index, true, false).set(index, value);
					} else {
						value = previous.value;
					}
				} else {
					parent.resizeAndGetList(index, true, false).set(index, value);
				}
			} else {
				parent.resizeAndGetList(index, true, false).set(index, value);
			}

		} else if (parent.isArray()) {

			int index = 0;
			int end = path.length() - 1;
			if (end > -1 && path.charAt(end) == ']') {
				int start = path.lastIndexOf('[');
				index = Integer.parseInt(path.substring(start + 1, end));
			}
			parent.setType(List.class);
			if (putIfAbsent) {
				if (index < parent.size()) {
					Tree previous = parent.get(index);
					if (previous.isNull()) {
						parent.resizeAndGetList(index, true, false).set(index, value);
					} else {
						value = previous.value;
					}
				} else {
					parent.resizeAndGetList(index, true, false).set(index, value);
				}
			} else {
				parent.resizeAndGetList(index, true, false).set(index, value);
			}

		} else if (parent.isSet()) {

			Set set = (Set) parent.value;
			set.remove(value);
			set.add(value);

		} else {
			if (name.isEmpty()) {
				parent.setObjectInternal(value);
			} else {
				LinkedHashMap<String, Object> map = new LinkedHashMap<>();
				map.put(name, value);
				parent.setObjectInternal(map);
			}
		}
		if (isStructure(value)) {
			return new Tree(parent, name, value);
		}
		return parent;
	}

	protected Tree getChild(String path, boolean setMode) {

		// Empty path?
		if (path == null || path.isEmpty()) {
			return this;
		}

		// Get child by index
		if (path.charAt(0) == '[') {
			int end = path.indexOf(']');
			if (end == -1) {
				throw new IllegalArgumentException("End bracket is missing from path (" + path + ")!");
			}
			int index = Integer.parseInt(path.substring(1, end));
			if (index < 0) {
				return null;
			}
			path = path.substring(end + 1);
			while (!path.isEmpty() && path.charAt(0) == '.') {
				path = path.substring(1);
			}
			if (value != null) {
				if (isList()) {

					final List<?> list = resizeAndGetList(index, setMode, false);
					if (list == null) {
						return null;
					}
					return new Tree(this, index, list.get(index)).getChild(path, setMode);

				} else if (isArray()) {

					if (index >= Array.getLength(value)) {
						if (!setMode) {
							return null;
						}
						setType(List.class);
						final List<?> list = resizeAndGetList(index, true, true);
						return new Tree(this, index, list.get(index)).getChild(path, setMode);
					}
					return new Tree(this, index, Array.get(value, index)).getChild(path, setMode);

				} else if (isMap()) {

					if (setMode && isEmpty()) {
						setType(List.class);
						final List<?> list = resizeAndGetList(index, setMode, true);
						if (list == null) {
							return null;
						}
						return new Tree(this, index, list.get(index)).getChild(path, setMode);
					} else {
						List<Tree> list = asList();
						if (index >= list.size()) {
							return null;
						}
						return list.get(index).getChild(path, setMode);
					}

				}
			}
			int count = 0;
			final Iterator<Tree> i = iterator();
			while (i.hasNext()) {
				Tree child = i.next();
				if (count == index) {
					return child.getChild(path, setMode);
				}
				count++;
			}
			return null;
		}

		// Get child by path
		int point = path.indexOf('.');
		if (point == -1) {
			point = Integer.MAX_VALUE;
		}
		int bracket = path.indexOf('[');
		if (bracket == -1) {
			bracket = Integer.MAX_VALUE;
		}
		String rest = null;
		if (point == Integer.MAX_VALUE && bracket == Integer.MAX_VALUE) {
			if (setMode) {
				return this;
			}
		} else if (point < bracket) {
			rest = path.substring(point + 1);
			path = path.substring(0, point);
		} else if (point > bracket) {
			rest = path.substring(bracket);
			path = path.substring(0, bracket);
		}
		if (rest != null && rest.isEmpty()) {
			rest = null;
		}

		// Meta node?
		if (Config.META.equals(path)) {
			final Tree meta = getMeta(setMode);
			if (meta == null) {
				return null;
			}
			return meta.getChild(rest, setMode);
		}

		// Is value a Map?
		if (!isMap()) {
			if (!setMode) {
				return null;
			}
			setType(Map.class);
		}

		// Cast to Map
		@SuppressWarnings("unchecked")
		final Map<Object, Object> map = (Map<Object, Object>) value;
		final Object object = map.get(path);
		if (object == null && !map.containsKey(path)) {

			// Node not found, create new node
			if (setMode) {
				if (rest != null) {

					// Create new child
					final LinkedHashMap<String, Object> child = new LinkedHashMap<>();
					map.put(path, child);
					return new Tree(this, path, child).getChild(rest, true);
				} else {

					// Return this node
					return this;
				}
			}
			return null;
		}

		// We found the proper child
		final Tree child = new Tree(this, path, object);
		if (rest == null || (setMode && rest.indexOf('.') == -1 && rest.indexOf('[') < 1)) {
			return child;
		}

		// Recursive searching
		return child.getChild(rest, setMode);
	}

	@SuppressWarnings("unchecked")
	protected List<Object> resizeAndGetList(int index, boolean setMode, boolean convertToList) {
		List<Object> list = (List<Object>) value;
		if (index >= list.size()) {
			if (!setMode) {
				return null;
			}
			if (convertToList) {
				LinkedList<Object> copy = new LinkedList<>();
				copy.addAll(list);
				while (copy.size() <= index) {
					copy.add(null);
				}
				setObjectInternal(copy);
				return copy;
			}
			while (list.size() <= index) {
				list.add(null);
			}
		}
		return list;
	}

	// --- BASIC VALUE GETTERS ---

	/**
	 * Return raw value of this node.
	 * 
	 * @return value as unconverted Object
	 */
	public Object asObject() {
		return value;
	}

	/**
	 * Converts this node's value to a byte. This method does not change the
	 * node's internal value and type. Sample code:<br>
	 * <br>
	 * // Put value as String:<br>
	 * node.put("name", "2");<br>
	 * <br>
	 * // The value will be (byte) 2:<br>
	 * byte value = node.get("name").asByte();<br>
	 * 
	 * @return this node's value as Byte (or null)
	 */
	public Byte asByte() {
		return DataConverterRegistry.convert(Byte.class, value);
	}

	/**
	 * Converts this node's value to a short. This method does not change the
	 * node's internal value and type. Sample code:<br>
	 * <br>
	 * // Put value as String:<br>
	 * node.put("a", "2");<br>
	 * <br>
	 * // The value will be (short) 2:<br>
	 * short value = node.get("a").asShort();<br>
	 * 
	 * @return this node's value as Short (or null)
	 */
	public Short asShort() {
		return DataConverterRegistry.convert(Short.class, value);
	}

	/**
	 * Converts this node's value to a integer. This method does not change the
	 * node's internal value and type. Sample code:<br>
	 * <br>
	 * // Put value as String:<br>
	 * node.put("name", "2");<br>
	 * <br>
	 * // The value will be (int) 2:<br>
	 * int value = node.get("name").asInteger();<br>
	 * 
	 * @return this node's value as Integer (or null)
	 */
	public Integer asInteger() {
		return DataConverterRegistry.convert(Integer.class, value);
	}

	/**
	 * Converts this node's value to a long. This method does not change the
	 * node's internal value and type. Sample code:<br>
	 * <br>
	 * // Put value as String:<br>
	 * node.put("name", "2");<br>
	 * <br>
	 * // The value will be (long) 2:<br>
	 * long value = node.get("name").asLong();<br>
	 * 
	 * @return this node's value as Long (or null)
	 */
	public Long asLong() {
		return DataConverterRegistry.convert(Long.class, value);
	}

	/**
	 * Converts this node's value to a float. This method does not change the
	 * node's internal value and type. Sample code:<br>
	 * <br>
	 * // Put value as String:<br>
	 * node.put("name", "2");<br>
	 * <br>
	 * // The value will be (float) 2:<br>
	 * float value = node.get("name").asFloat();<br>
	 * 
	 * @return this node's value as Float (or null)
	 */
	public Float asFloat() {
		return DataConverterRegistry.convert(Float.class, value);
	}

	/**
	 * Converts this node's value to a double. This method does not change the
	 * node's internal value and type. Sample code:<br>
	 * <br>
	 * // Put value as String:<br>
	 * node.put("name", "2");<br>
	 * <br>
	 * // The value will be (double) 2:<br>
	 * double value = node.get("name").asDouble();<br>
	 * 
	 * @return this node's value as Double (or null)
	 */
	public Double asDouble() {
		return DataConverterRegistry.convert(Double.class, value);
	}

	/**
	 * Converts this node's value to a boolean. This method does not change the
	 * node's internal value and type. Sample code:<br>
	 * <br>
	 * // Put value as String:<br>
	 * node.put("name", "1");<br>
	 * <br>
	 * // The value will be "true" (1 is positive):<br>
	 * boolean value = node.get("name").asBoolean();<br>
	 * 
	 * @return this node's value as Boolean (or null)
	 */
	public Boolean asBoolean() {
		return DataConverterRegistry.convert(Boolean.class, value);
	}

	/**
	 * Converts this node's value to byte array. This method does not change the
	 * node's internal value and type. Sample code:<br>
	 * <br>
	 * // Put value as BASE64 String:<br>
	 * node.put("name", "FFG432B2 ...etc... ==");<br>
	 * // Get value as byte array:<br>
	 * byte[] value = node.get("name").asBytes();<br>
	 * 
	 * @return this node's value as byte array (or null)
	 */
	public byte[] asBytes() {
		return DataConverterRegistry.convert(byte[].class, value);
	}

	/**
	 * Converts this node's value to a String. This method does not change the
	 * node's internal value and type. Sample code:<br>
	 * <br>
	 * // Put value as Date:<br>
	 * node.put("name", new Date());<br>
	 * <br>
	 * // The value will be "2017-12-06T16:40:30.238Z":<br>
	 * String value = node.get("name").asString();<br>
	 * 
	 * @return this node's value as String (or null)
	 */
	public String asString() {
		return DataConverterRegistry.convert(String.class, value);
	}

	/**
	 * Converts this node's value to Date. This method does not change the
	 * node's internal value and type. Sample code:<br>
	 * <br>
	 * // Put value as String:<br>
	 * node.put("name", "2017-12-06T16:40:30.238Z");<br>
	 * <br>
	 * // The value will be a parsed Date:<br>
	 * Date value = node.get("name").asDate();<br>
	 * 
	 * @return this node's value as Date (or null)
	 */
	public Date asDate() {
		return DataConverterRegistry.convert(Date.class, value);
	}

	/**
	 * Converts this node's value to UUID. This method does not change the
	 * node's internal value and type. Sample code:<br>
	 * <br>
	 * // Put value as String:<br>
	 * node.put("name", "03000000-0000-0000-0000-000000000000");<br>
	 * <br>
	 * // The value will be a parsed UUID:<br>
	 * UUID value = node.get("name").asUUID();<br>
	 * 
	 * @return this node's value as UUID (or null)
	 */
	public UUID asUUID() {
		return DataConverterRegistry.convert(UUID.class, value);
	}

	/**
	 * Converts this node's value to BigDecimal. This method does not change the
	 * node's internal value and type. Sample code:<br>
	 * <br>
	 * // Put value as String:<br>
	 * node.put("name", "3423434234234");<br>
	 * <br>
	 * // The value will be a parsed BigDecimal:<br>
	 * BigDecimal value = node.get("name").asBigDecimal();<br>
	 * 
	 * @return this node's value as BigDecimal (or null)
	 */
	public BigDecimal asBigDecimal() {
		return DataConverterRegistry.convert(BigDecimal.class, value);
	}

	/**
	 * Converts this node's value to BigInteger. This method does not change the
	 * node's internal value and type. Sample code:<br>
	 * <br>
	 * // Put value as String:<br>
	 * node.put("name", "3423434234234");<br>
	 * <br>
	 * // The value will be a parsed BigInteger:<br>
	 * BigInteger value = node.get("name").asBigInteger();<br>
	 * 
	 * @return this node's value as BigInteger (or null)
	 */
	public BigInteger asBigInteger() {
		return DataConverterRegistry.convert(BigInteger.class, value);
	}

	/**
	 * Converts this node's value to InetAddress. This method does not change
	 * the node's internal value and type. Sample code:<br>
	 * <br>
	 * // Put value as String:<br>
	 * node.put("name", "122.123.124.125");<br>
	 * <br>
	 * // The value will be a parsed InetAddress:<br>
	 * InetAddress value = node.get("name").asInetAddress();<br>
	 * 
	 * @return this node's value as InetAddress (or null)
	 */
	public InetAddress asInetAddress() {
		return DataConverterRegistry.convert(InetAddress.class, value);
	}

	// --- BASIC VALUE GETTERS FOR CHILDREN ---

	/**
	 * Returns the int value to which the specified path is mapped. The method
	 * returns the default value argument if the path is not valid. This method
	 * performs automatic type conversion if needed (eg. String to int, etc.).
	 * Sample code:<br>
	 * <br>
	 * int value = node.get("path.to.value[0]", -1);
	 * 
	 * @param path
	 *            the path whose associated value is to be returned
	 * @param defaultValue
	 *            default value
	 * 
	 * @return the value in this node structure on the specified path
	 */
	public int get(String path, int defaultValue) {
		return getObject(path, defaultValue);
	}

	/**
	 * Returns the double value to which the specified path is mapped. The
	 * method returns the default value argument if the path is not valid. This
	 * method performs automatic type conversion if needed (eg. String to
	 * double, etc.). Sample code:<br>
	 * <br>
	 * double value = node.get("path.to[1].value", -1d);
	 * 
	 * @param path
	 *            the path whose associated value is to be returned
	 * @param defaultValue
	 *            default value
	 * 
	 * @return the value in this node structure on the specified path
	 */
	public double get(String path, double defaultValue) {
		return getObject(path, defaultValue);
	}

	/**
	 * Returns the byte value to which the specified path is mapped. The method
	 * returns the default value argument if the path is not valid. This method
	 * performs automatic type conversion if needed (eg. int to byte, etc.).
	 * Sample code:<br>
	 * <br>
	 * byte value = node.get("path.to.value[0]", (byte) 0);
	 * 
	 * @param path
	 *            the path whose associated value is to be returned
	 * @param defaultValue
	 *            default value
	 * 
	 * @return the value in this node structure on the specified path
	 */
	public byte get(String path, byte defaultValue) {
		return getObject(path, defaultValue);
	}

	/**
	 * Returns the float value to which the specified path is mapped. The method
	 * returns the default value argument if the path is not valid. This method
	 * performs automatic type conversion if needed (eg. double to float, etc.).
	 * Sample code:<br>
	 * <br>
	 * float value = node.get("path.to.value", -1f);
	 * 
	 * @param path
	 *            the path whose associated value is to be returned
	 * @param defaultValue
	 *            default value
	 * 
	 * @return the value in this node structure on the specified path
	 */
	public float get(String path, float defaultValue) {
		return getObject(path, defaultValue);
	}

	/**
	 * Returns the long value to which the specified path is mapped. The method
	 * returns the default value argument if the path is not valid. This method
	 * performs automatic type conversion if needed (eg. String to long, etc.).
	 * Sample code:<br>
	 * <br>
	 * long value = node.get("path.to.value[3]", -1L);
	 * 
	 * @param path
	 *            the path whose associated value is to be returned
	 * @param defaultValue
	 *            default value
	 * 
	 * @return the value in this node structure on the specified path
	 */
	public long get(String path, long defaultValue) {
		return getObject(path, defaultValue);
	}

	/**
	 * Returns the boolean value to which the specified path is mapped. The
	 * method returns the default value argument if the path is not valid. This
	 * method performs automatic type conversion if needed (eg. String to
	 * boolean, etc.). Sample code:<br>
	 * <br>
	 * boolean value = node.get("path.to[0].value[2]", false);
	 * 
	 * @param path
	 *            the path whose associated value is to be returned
	 * @param defaultValue
	 *            default value
	 * 
	 * @return the value in this node structure on the specified path
	 */
	public boolean get(String path, boolean defaultValue) {
		return getObject(path, defaultValue);
	}

	/**
	 * Returns the byte array to which the specified path is mapped. The method
	 * returns the default value argument if the path is not valid. This method
	 * performs automatic type conversion if needed (eg. BASE64 String to byte
	 * array, etc.). Sample code:<br>
	 * <br>
	 * byte[] bytes = node.get("path.to.value", new byte[0]);
	 * 
	 * @param path
	 *            the path whose associated value is to be returned
	 * @param defaultValue
	 *            default value
	 * 
	 * @return the value in this node structure on the specified path
	 */
	public byte[] get(String path, byte[] defaultValue) {
		if (defaultValue == null) {
			return getObject(path, byte[].class);
		}
		return getObject(path, defaultValue);
	}

	/**
	 * Returns the short value to which the specified path is mapped. The method
	 * returns the default value argument if the path is not valid. This method
	 * performs automatic type conversion if needed (eg. int to short, etc.).
	 * Sample code:<br>
	 * <br>
	 * short value = node.get("path.to.value[2]", (short) -1);
	 * 
	 * @param path
	 *            the path whose associated value is to be returned
	 * @param defaultValue
	 *            default value
	 * 
	 * @return the value in this node structure on the specified path
	 */
	public short get(String path, short defaultValue) {
		return getObject(path, defaultValue);
	}

	/**
	 * Returns the String to which the specified path is mapped. The method
	 * returns the default value argument if the path is not valid. This method
	 * performs automatic type conversion if needed (eg. boolean or int to
	 * String, etc.). Sample code:<br>
	 * <br>
	 * String value = node.get("path.to[0].value", "");
	 * 
	 * @param path
	 *            the path whose associated value is to be returned
	 * @param defaultValue
	 *            default value
	 * 
	 * @return the value in this node structure on the specified path
	 */
	public String get(String path, String defaultValue) {
		if (defaultValue == null) {
			return getObject(path, String.class);
		}
		return getObject(path, defaultValue);
	}

	/**
	 * Returns the UUID to which the specified path is mapped. The method
	 * returns the default value argument if the path is not valid. This method
	 * performs automatic type conversion if needed (eg. String to UUID object,
	 * etc.). Sample code:<br>
	 * <br>
	 * UUID value = node.get("path.to.value", (UUID) null);
	 * 
	 * @param path
	 *            the path whose associated value is to be returned
	 * @param defaultValue
	 *            default value
	 * 
	 * @return the value in this node structure on the specified path
	 */
	public UUID get(String path, UUID defaultValue) {
		if (defaultValue == null) {
			return getObject(path, UUID.class);
		}
		return getObject(path, defaultValue);
	}

	/**
	 * Returns the Date to which the specified path is mapped. The method
	 * returns the default value argument if the path is not valid. This method
	 * performs automatic type conversion if needed (eg. date string to Date
	 * object, etc.). Sample code:<br>
	 * <br>
	 * Date value = node.get("path.to.value[0]", (Date) null);
	 * 
	 * @param path
	 *            the path whose associated value is to be returned
	 * @param defaultValue
	 *            default value
	 * 
	 * @return the value in this node structure on the specified path
	 */
	public Date get(String path, Date defaultValue) {
		if (defaultValue == null) {
			return getObject(path, Date.class);
		}
		return getObject(path, defaultValue);
	}

	/**
	 * Returns the BigDecimal to which the specified path is mapped. The method
	 * returns the default value argument if the path is not valid. This method
	 * performs automatic type conversion if needed (eg. String to BigDecimal,
	 * etc.). Sample code:<br>
	 * <br>
	 * BigDecimal value = node.get("path.to.value", BigDecimal.ZERO);
	 * 
	 * @param path
	 *            the path whose associated value is to be returned
	 * @param defaultValue
	 *            default value
	 * 
	 * @return the value in this node structure on the specified path
	 */
	public BigDecimal get(String path, BigDecimal defaultValue) {
		if (defaultValue == null) {
			return getObject(path, BigDecimal.class);
		}
		return getObject(path, defaultValue);
	}

	/**
	 * Returns the BigInteger to which the specified path is mapped. The method
	 * returns the default value argument if the path is not valid. This method
	 * performs automatic type conversion if needed (eg. String to BigInteger,
	 * etc.). Sample code:<br>
	 * <br>
	 * BigInteger value = node.get("path.to[1].value[2]", BigInteger.ZERO);
	 * 
	 * @param path
	 *            the path whose associated value is to be returned
	 * @param defaultValue
	 *            default value
	 * 
	 * @return the value in this node structure on the specified path
	 */
	public BigInteger get(String path, BigInteger defaultValue) {
		if (defaultValue == null) {
			return getObject(path, BigInteger.class);
		}
		return getObject(path, defaultValue);
	}

	/**
	 * Returns the InetAddress to which the specified path is mapped. The method
	 * returns the default value argument if the path is not valid. This method
	 * performs automatic type conversion if needed (eg. IP String to
	 * InetAddress, etc.). Sample code:<br>
	 * <br>
	 * InetAddress value = node.get("path.to[0].value", (InetAddress) null);
	 * 
	 * @param path
	 *            the path whose associated value is to be returned
	 * @param defaultValue
	 *            default value
	 * 
	 * @return the value in this node structure on the specified path
	 */
	public InetAddress get(String path, InetAddress defaultValue) {
		if (defaultValue == null) {
			return getObject(path, InetAddress.class);
		}
		return getObject(path, defaultValue);
	}

	/**
	 * Returns the Object to which the specified path is mapped. The method
	 * returns the default value argument if the path is not valid. This method
	 * performs automatic type conversion if needed (eg. IP String to
	 * InetAddress, etc.). Sample code:<br>
	 * 
	 * @param <TO>
	 *            output's type of this method
	 * @param path
	 *            path (e.g. "path.to.node[0]")
	 * @param defaultValue
	 *            default value (if the node is not exists)
	 * 
	 * @return the value of the node (or the defaultValue)
	 */
	@SuppressWarnings("unchecked")
	public <TO> TO getObject(String path, TO defaultValue) {
		Tree child = getChild(path, false);
		if (child != null) {
			if (defaultValue == null) {
				return (TO) child.value;
			}
			TO converted = DataConverterRegistry.convert((Class<TO>) defaultValue.getClass(), child.value);
			if (converted == null && (defaultValue instanceof Number || defaultValue instanceof Boolean)) {
				return defaultValue;
			}
			return converted;
		}
		return defaultValue;
	}

	/**
	 * Returns the Object (or null) to which the specified path is mapped.
	 * 
	 * @param path
	 *            path (e.g. "path.to.node[0]")
	 * @param to
	 *            target type
	 * 
	 * @return the value of the node (or null)
	 */
	private <TO> TO getObject(String path, Class<TO> to) {
		Tree child = getChild(path, false);
		if (child == null) {
			return null;
		}
		return DataConverterRegistry.convert(to, child.value);
	}

	// --- SEARCH FOR CHILD BY PATH ---

	/**
	 * Returns the sub-node to which the specified path is mapped. Sample code:
	 * <br>
	 * <br>
	 * node.put("a.b.c", 1);<br>
	 * Tree subNode = node.get("a.b.c");<br>
	 * return subNode.asString();<br>
	 * <br>
	 * This code above returns "1".
	 * 
	 * @param path
	 *            the path whose associated sub-node is to be returned
	 * 
	 * @return sub-node on the specified path (or null)
	 */
	public Tree get(String path) {
		return getChild(path, false);
	}

	/**
	 * Returns {@code true} if this node contains a mapping for the specified
	 * path. Sample code:<br>
	 * <br>
	 * boolean found = node.get("path.to.child[2]");
	 * 
	 * @param path
	 *            path whose presence in this node is to be tested
	 * 
	 * @return {@code true} if this node contains a mapping for the specified
	 *         path
	 */
	public boolean isExists(String path) {
		return getChild(path, false) != null;
	}

	// --- SEARCH FOR CHILD BY INDEX ---

	/**
	 * The first child of this node. If there is no such node, this returns
	 * {@code null}.
	 * 
	 * @return first child or {@code null}
	 */
	public Tree getFirstChild() {
		return isEmpty() ? null : get(0);
	}

	/**
	 * The last child of this node. If there is no such node, this returns
	 * {@code null}.
	 * 
	 * @return last child of this node or {@code null}
	 */
	public Tree getLastChild() {
		return isEmpty() ? null : get(size() - 1);
	}

	/**
	 * The node immediately following this node. If there is no such node, this
	 * returns {@code null}.
	 * 
	 * @return next sibling node or {@code null}
	 */
	public Tree getNextSibling() {
		if (parent != null && key != null) {
			if (key instanceof Integer) {
				int idx = (int) key;
				if (parent.size() >= idx - 1) {
					return null;
				}
				return parent.get(idx + 1);
			}
			boolean found = false;
			for (Tree child : parent) {
				if (key.equals(child.key)) {
					found = true;
					continue;
				}
				if (found) {
					return child;
				}
			}
		}
		return null;
	}

	/**
	 * The node immediately preceding this node. If there is no such node, this
	 * returns {@code null}.
	 * 
	 * @return previous node or {@code null}
	 */
	public Tree getPreviousSibling() {
		if (parent != null && key != null) {
			if (key instanceof Integer) {
				int idx = (int) key;
				if (idx < 1) {
					return null;
				}
				return parent.get(idx - 1);
			}
			Tree previous = null;
			for (Tree child : parent) {
				if (key.equals(child.key)) {
					return previous;
				}
				previous = child;
			}
		}
		return null;
	}

	/**
	 * Returns the sub-node at the specified position in this node. Sample code:
	 * <br>
	 * <br>
	 * node.putList("list").add(10).add(20).add(30);<br>
	 * Tree subNode = node.get(1);<br>
	 * return subNode.asLong();<br>
	 * <br>
	 * This code above returns 20.
	 * 
	 * @param index
	 *            index of the sub-node to return
	 * 
	 * @return the sub-node at the specified position
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range (index &lt; 0 || index &gt;=
	 *             size())
	 */
	@SuppressWarnings("unchecked")
	public Tree get(int index) {
		if (isList()) {
			return new Tree(this, index, ((List<Object>) value).get(index));
		}
		return asList().get(index);
	}

	// --- CLEAR VALUE(S) ---

	/**
	 * Clears the sub-nodes on the specified path. If no matching node exists,
	 * creates a new empty node. Sample code:<br>
	 * <br>
	 * Tree array = node.clear("a.b").add(1).add(2);
	 * 
	 * @param path
	 *            path of the sub-node
	 * 
	 * @return the empty sub-node at the specified position
	 */
	public Tree clear(String path) {
		Tree child = getChild(path, false);
		if (child == null) {
			child = putMap(path);
		} else {
			child.clear();
		}
		return child;
	}

	/**
	 * Removes all of the sub-nodes from this node or sets the value to null.
	 * The node will be empty after this call returns. Sample code:<br>
	 * <br>
	 * node.clear().put("a", 1).put("b", 2);
	 * 
	 * @return this (empty) node
	 */
	public Tree clear() {
		if (value != null) {
			if (isMap()) {

				((Map<?, ?>) value).clear();

			} else if (value instanceof Collection) {

				((Collection<?>) value).clear();

			} else if (isArray()) {

				setObjectInternal(new LinkedList<Object>());

			} else {

				setObjectInternal(null);

			}
		}
		return this;
	}

	// --- REMOVE FUNCTIONS ---

	/**
	 * Removes the sub-node at the specified path. Returns the node that was
	 * removed. Samples:<br>
	 * <br>
	 * Tree removed = node.remove("path.to.node");<br>
	 * or<br>
	 * Tree removed = node.remove("array[3].subarray[2]");
	 * 
	 * @param path
	 *            path to a sub-node node
	 * 
	 * @return removed child, or null
	 */
	public Tree remove(String path) {
		Tree child = getChild(path, false);
		if (child != null) {
			child.remove();
		}
		return child;
	}

	/**
	 * Removes the first sub-node that satisfy the given predicate. Sample code:
	 * <br>
	 * <br>
	 * boolean found = node.remove((child) -&gt; {<br>
	 * return child.getName().startsWith("a");<br>
	 * });
	 * 
	 * @param filter
	 *            predicate which returns {@code true} for node to be removed
	 * 
	 * @return {@code true} if any sub-nodes were removed
	 */
	public boolean remove(Predicate<Tree> filter) {
		return remove(filter, false);
	}

	/**
	 * Removes all of the sub-nodes of this node that satisfy the given
	 * predicate. Sample code:<br>
	 * <br>
	 * boolean found = node.remove((child) -&gt; {<br>
	 * return child.getName().startsWith("a");<br>
	 * }, true);
	 * 
	 * @param filter
	 *            predicate which returns {@code true} for node to be removed
	 * @param allOccurences
	 *            removes all ({@code true}) or just the first ({@code false})
	 *            occurence(s)
	 * 
	 * @return {@code true} if any sub-nodes were removed
	 */
	public boolean remove(Predicate<Tree> filter, boolean allOccurences) {
		Iterator<Tree> i = iterator();
		boolean removed = false;
		while (i.hasNext()) {
			Tree child = i.next();
			if (filter.test(child)) {
				i.remove();
				removed = true;
				if (!allOccurences) {
					break;
				}
			}
		}
		return removed;
	}

	/**
	 * Removes the first occurrence of the specified sub-node from this node, if
	 * it is present.
	 * 
	 * @param child
	 *            sub-node to be removed from this node, if present
	 * 
	 * @return {@code true} if this node contained the specified sub-node
	 */
	public boolean remove(Tree child) {
		if (Config.META.equals(child.key)) {
			Tree meta = getMeta();
			if (meta != null && meta.parent != null) {
				meta.parent.meta = null;
				return true;
			}
		}
		return remove((test) -> {
			return test.key.equals(child.key);
		});
	}

	/**
	 * Removes this node from its parent.
	 * 
	 * @return false, if this node is a root node
	 */
	public boolean remove() {
		if (parent == null) {
			return false;
		}
		boolean removed = parent.remove(this);
		parent = null;
		return removed;
	}

	/**
	 * Removes the child node at the specified position. Returns the node that
	 * was removed.
	 * 
	 * @param index
	 *            zero-based index of the sub-node
	 * 
	 * @return the removed node
	 */
	public Tree remove(int index) {
		Tree child = asList().get(index);
		if (child.remove()) {
			return child;
		}
		return null;
	}

	/**
	 * Removes and returns the first sub-node from this node.
	 * 
	 * @return the first sub-node
	 * 
	 * @throws NoSuchElementException
	 *             if this node has no any sub-nodes
	 */
	public Tree removeFirst() {
		if (size() == 0) {
			throw new NoSuchElementException();
		}
		return remove(0);
	}

	/**
	 * Removes and returns the last sub-node from this node.
	 * 
	 * @return the last sub-node
	 * 
	 * @throws NoSuchElementException
	 *             if this node has no any sub-nodes
	 */
	public Tree removeLast() {
		int size = size();
		if (size == 0) {
			throw new NoSuchElementException();
		}
		return remove(size - 1);
	}

	// --- COLLECTION HELPERS ---

	/**
	 * Indicates whether some other Tree is equal to this one.
	 * 
	 * @return {@code true} if this node's JSON String is the same as the
	 *         another node's JSON String, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Tree) {
			Tree node = (Tree) obj;
			return JsonBuiltin.serialize(value, meta).equals(JsonBuiltin.serialize(node.value, node.meta));
		}
		return false;
	}

	/**
	 * Returns a hash code value for this node.
	 * 
	 * @return hash code of this node
	 */
	@Override
	public int hashCode() {
		return JsonBuiltin.serialize(value, meta).hashCode();
	}

	// --- ITERATOR ---

	/**
	 * Returns an iterator over sub-nodes of this node. Samples:<br>
	 * <br>
	 * for (Tree child: node) {<br>
	 * int i = node.get("path.to.value", -1);<br>
	 * }<br>
	 * <br>
	 * node.forEach((child) -&gt; {<br>
	 * int i = node.get("path.to.value", -1);<br>
	 * });<br>
	 * 
	 * @return an Iterator.
	 */
	@Override
	public Iterator<Tree> iterator() {
		if (value != null) {

			// Map iterator
			if (value instanceof Map) {
				return mapIterator();
			}

			// Collection (eg. List or Set) iterator
			if (value instanceof Collection) {
				return collectionIterator();
			}

			// Array iterator
			if (value.getClass().isArray()) {
				return arrayIterator();
			}
		}

		// Not a List, Map, array or Set
		return Collections.singleton(this).iterator();
	}

	protected Iterator<Tree> mapIterator() {
		final Tree self = this;
		return new Iterator<Tree>() {

			@SuppressWarnings("rawtypes")
			private final Iterator children = ((Map) value).entrySet().iterator();

			@Override
			public final boolean hasNext() {
				return children.hasNext();
			}

			@Override
			@SuppressWarnings("rawtypes")
			public final Tree next() {
				Map.Entry entry = (Map.Entry) children.next();
				return new Tree(self, entry.getKey(), entry.getValue());
			}

			@Override
			public final void remove() {
				children.remove();
			}
		};
	}

	protected Iterator<Tree> collectionIterator() {
		final Tree self = this;
		return new Iterator<Tree>() {

			@SuppressWarnings("rawtypes")
			private final Iterator children = ((Collection) value).iterator();

			private int counter = 0;

			@Override
			public final boolean hasNext() {
				return children.hasNext();
			}

			@Override
			public final Tree next() {
				return new Tree(self, counter++, children.next());
			}

			@Override
			public final void remove() {
				children.remove();
				counter--;
			}
		};
	}

	protected Iterator<Tree> arrayIterator() {
		final Tree self = this;
		return new Iterator<Tree>() {

			private int index = 0;
			private int len = Array.getLength(value);

			@Override
			public final boolean hasNext() {
				return index < len;
			}

			@Override
			public final Tree next() {
				return new Tree(self, index, Array.get(value, index++));
			}

			@Override
			public final void remove() {
				Object[] copy = new Object[len - 1];
				int i = 0;
				for (int n = 0; n < len; n++) {
					if (n != index - 1) {
						copy[i] = Array.get(value, n);
						i++;
					}
				}
				self.setObjectInternal(copy);
				index--;
			}
		};
	}

	// --- ASSIGN FUNCTION ---

	/**
	 * This method is used to copy the value(s) of all enumerable own properties
	 * from the source node to a this node. Sample code:<br>
	 * <br>
	 * Tree root = new Tree();<br>
	 * Tree child1 = root.putList("first").add(1).add(2).add(3);<br>
	 * Tree child2 = root.putList("second");<br>
	 * child2.assign(child1);<br>
	 * <br>
	 * The child2's value will be "[1,2,3]".
	 * 
	 * @param source
	 *            source node
	 * 
	 * @return this node
	 */
	public Tree assign(Tree source) {
		return setObjectInternal(source.clone().value);
	}

	// --- COPY SUB-NODES ---

	/**
	 * Appends all of the sub-nodes from the specified source node into this
	 * node.
	 * 
	 * @param source
	 *            source node
	 * 
	 * @return this node
	 */
	public Tree copyFrom(Tree source) {
		return copyFrom(source, true);
	}

	/**
	 * Appends the sub-nodes from the specified source node into this node.
	 * 
	 * @param source
	 *            source node
	 * @param overwriteExisting
	 *            replace existing sub-nodes with same name
	 * 
	 * @return this node
	 */
	public Tree copyFrom(Tree source, boolean overwriteExisting) {
		return copyFrom(source, child -> {
			if (!overwriteExisting) {
				if (isMap()) {
					return !isExists(child.getName());
				}
				for (Tree test : this) {
					if (test.equals(child)) {
						return false;
					}
				}
			}
			return true;
		});
	}

	/**
	 * Appends the specified sub-nodes from the specified source node into this
	 * node.
	 * 
	 * @param source
	 *            source node
	 * @param fields
	 *            names of the nodes to be copied
	 * 
	 * @return this node
	 */
	public Tree copyFrom(Tree source, String... fields) {
		if (fields == null || fields.length == 0) {
			return copyFrom(source);
		}
		return copyFrom(source, child -> {
			for (String field : fields) {
				if (field != null && field.equals(child.getName())) {
					return true;
				}
			}
			return false;
		});
	}

	/**
	 * Appends the sub-nodes from the source node into this node by the
	 * specified Predicate. Sample code:<br>
	 * <br>
	 * target.copyFrom(another, (child) -&gt; {<br>
	 * return child.getName().startsWith("bl");<br>
	 * });<br>
	 * <br>
	 * The code above copies sub-nodes with "bl" name prefix ("blue", "black",
	 * etc.) from the "another" node into the "target" node.
	 * 
	 * @param source
	 *            source node
	 * @param filter
	 *            a Predicate to select nodes
	 * 
	 * @return this node
	 */
	public Tree copyFrom(Tree source, Predicate<Tree> filter) {
		if (source != null) {
			if (isMap()) {
				for (Tree child : source) {
					if (filter.test(child)) {
						putObjectInternal(child.getName(), child.clone().value, false);
					}
				}
			} else {
				for (Tree child : source) {
					if (filter.test(child)) {
						addObjectInternal(child.clone().value);
					}
				}
			}
		}
		return this;
	}

	// --- JAVA 8 STREAMS ---

	/**
	 * Returns a sequential Stream with this node as its source. Sample code:
	 * <br>
	 * <br>
	 * Tree node = ...<br>
	 * int[] values = node.stream().mapToInt((child) -&gt; {<br>
	 * return child.get("path.to.value", -1);<br>
	 * }).toArray();
	 * 
	 * @return a sequential Stream over the elements in this node
	 */
	public Stream<Tree> stream() {
		return StreamSupport.stream(Spliterators.spliterator(iterator(), size(), Spliterator.ORDERED), false);
	}

	// --- COPY VALUES TO LIST ---

	/**
	 * Returns the node's value(s) in a List.
	 * 
	 * @param <T>
	 *            type of elements in the output List
	 * @param castTo
	 *            List type (eg. Integer, String)
	 * 
	 * @return value(s) as List
	 */
	public <T> List<T> asList(Class<T> castTo) {
		LinkedList<T> list = new LinkedList<T>();
		for (Tree node : this) {
			list.add(DataConverterRegistry.convert(castTo, node.value));
		}
		return list;
	}

	/**
	 * Returns the node's value(s) as a list of QNodes.
	 * 
	 * @return value(s) as List
	 */
	protected List<Tree> asList() {
		final LinkedList<Tree> list = new LinkedList<>();
		for (Tree child : this) {
			list.addLast(child);
		}
		return list;
	}

	// --- CONVERT TO STRING ---

	/**
	 * Converts this node structure to a formatted JSON String, with the meta
	 * structure. Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a", 1);<br>
	 * node.put("b", "c");<br>
	 * System.out.println(node.toString());<br>
	 * <br>
	 * {<br>
	 * "a":1,<br>
	 * "b":"c"<br>
	 * }
	 * 
	 * @return this node in JSON format
	 */
	@Override
	public String toString() {
		return TreeWriterRegistry.getWriter(null).toString(value, meta, true, true);
	}

	/**
	 * Converts this node structure to JSON String, without meta structure.
	 * Sample code:<br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * node.put("a", 1);<br>
	 * node.put("b", "c");<br>
	 * System.out.println(node.toString(true));<br>
	 * <br>
	 * {<br>
	 * "a":1,<br>
	 * "b":"c"<br>
	 * }
	 * 
	 * @param pretty
	 *            enable pretty print JSON output
	 * 
	 * @return this node in JSON format
	 */
	public String toString(boolean pretty) {
		return TreeWriterRegistry.getWriter(null).toString(value, meta, pretty, false);
	}

	/**
	 * Convert this node structure to a String using the specified format,
	 * without meta structure. Sample code:<br>
	 * <br>
	 * Tree node = new Tree().put("a", 1).put("b", "c");<br>
	 * String txt = node.toString("yaml");<br>
	 * 
	 * @param format
	 *            name of the format (eg. "debug", "json", "xml", "yaml", "csv",
	 *            "toml", etc.)
	 * 
	 * @return this node in custom text format
	 */
	public String toString(String format) {
		return TreeWriterRegistry.getWriter(format).toString(value, meta, false, false);
	}

	/**
	 * Convert this node structure to a String using the specified format,
	 * without meta structure. Sample code:<br>
	 * <br>
	 * Tree node = new Tree().put("a", 1).put("b", "c");<br>
	 * String txt = node.toString("json", true, true);<br>
	 * 
	 * @param format
	 *            name of the format (eg. "debug", "json", "xml", "yaml", "csv",
	 *            "toml", etc.)
	 * @param pretty
	 *            enable pretty print
	 * 
	 * @return this node in custom text format
	 */
	public String toString(String format, boolean pretty) {
		return TreeWriterRegistry.getWriter(format).toString(value, meta, pretty, false);
	}

	/**
	 * Convert this node structure to a String using the specified format.
	 * Sample code:<br>
	 * <br>
	 * Tree node = new Tree().put("a", 1).put("b", "c");<br>
	 * String txt = node.toString("json", true, true);<br>
	 * 
	 * @param format
	 *            name of the format (eg. "debug", "json", "xml", "yaml", "csv",
	 *            "toml", etc.)
	 * @param pretty
	 *            enable pretty print
	 * @param insertMeta
	 *            include meta structure
	 * 
	 * @return this node in custom text format
	 */
	public String toString(String format, boolean pretty, boolean insertMeta) {
		return TreeWriterRegistry.getWriter(format).toString(value, meta, pretty, insertMeta);
	}

	// --- CONVERT TO BINARY ARRAY ---

	/**
	 * Converts this node structure to a JSON byte array, without the meta
	 * structure. Sample code:<br>
	 * <br>
	 * Tree node = new Tree().put("a", 1).put("b", "c");<br>
	 * byte[] bytes = node.toBinary();<br>
	 * Tree copy = new Tree(bytes);
	 * 
	 * @return this node in binary format
	 */
	public byte[] toBinary() {
		return TreeWriterRegistry.getWriter(null).toBinary(value, meta, false);
	}

	/**
	 * Convert this node structure to a byte array using the specified format,
	 * without the meta structure. Sample code:<br>
	 * <br>
	 * Tree node = new Tree().put("a", 1).put("b", "c");<br>
	 * byte[] bytes = node.toBinary("cbor");<br>
	 * 
	 * @param format
	 *            name of the format (eg. "java", "smile", "cbor", etc.)
	 * 
	 * @return this node in custom binary format
	 */
	public byte[] toBinary(String format) {
		return TreeWriterRegistry.getWriter(format).toBinary(value, meta, false);
	}

	/**
	 * Convert this node structure to a byte array using the specified format.
	 * Sample code:<br>
	 * <br>
	 * Tree node = new Tree().put("a", 1).put("b", "c");<br>
	 * byte[] bytes = node.toBinary("smile", true);<br>
	 * 
	 * @param format
	 *            name of the format (eg. "java", "smile", "cbor", etc.)
	 * @param insertMeta
	 *            include meta structure
	 * 
	 * @return this node in custom binary format
	 */
	public byte[] toBinary(String format, boolean insertMeta) {
		return TreeWriterRegistry.getWriter(format).toBinary(value, meta, insertMeta);
	}

	// --- WRITE TO FILE ---

	/**
	 * Writes the contents of the Tree (without meta) to the specified File. The
	 * method tries to guess the file format based on its extension (for
	 * example, "file.json" will be in JSON format, "file.bson" will be in BSON
	 * format). If it fails to figure out the format, it will use the JSON
	 * format. Sample code:<br>
	 * <br>
	 * Tree tree = new Tree();<br>
	 * tree.put("key", "value");<br>
	 * tree.writeTo("/path/to/file.json");<br>
	 * 
	 * @param destinationFilePath
	 *            path of the destination File
	 * 
	 * @throws IOException
	 *             Any I/O Exception
	 */
	public void writeTo(String destinationFilePath) throws IOException {
		writeTo(new File(destinationFilePath));
	}

	/**
	 * Writes the contents of the Tree (without meta) to the specified File. The
	 * method tries to guess the file format based on its extension (for
	 * example, "file.json" will be in JSON format, "file.bson" will be in BSON
	 * format). If it fails to figure out the format, it will use the JSON
	 * format.
	 * 
	 * @param destination
	 *            the destination File
	 * 
	 * @throws IOException
	 *             Any I/O Exception
	 */
	public void writeTo(File destination) throws IOException {
		writeTo(destination, getFormatByExtension(destination));
	}

	/**
	 * Writes the contents of the Tree (without meta) to the specified File in
	 * the specified format.
	 * 
	 * @param destination
	 *            the destination File
	 * @param format
	 *            name of the format (eg. "json", "yaml", "csv", "toml", etc.)
	 * 
	 * @throws IOException
	 *             Any I/O Exception
	 */
	public void writeTo(File destination, String format) throws IOException {
		writeTo(destination, format, false);
	}

	/**
	 * Writes the contents of the Tree to the specified File in the specified
	 * format.
	 * 
	 * @param destination
	 *            the destination File
	 * @param format
	 *            name of the format (eg. "msgpack", "bson", "cbor", etc.)
	 * @param insertMeta
	 *            serialize the meta structure or not
	 * 
	 * @throws IOException
	 *             Any I/O Exception
	 */
	public void writeTo(File destination, String format, boolean insertMeta) throws IOException {
		writeTo(new FileOutputStream(destination), format, insertMeta, true);
	}

	// --- WRITE TO CHANNEL ---

	/**
	 * Writes the contents of the Tree (without meta) to the specified Channel
	 * in JSON format. This method closes the Channel after writing.
	 * 
	 * @param destination
	 *            the destination WritableByteChannel
	 * 
	 * @throws IOException
	 *             Any I/O Exception
	 */
	public void writeTo(WritableByteChannel destination) throws IOException {
		writeTo(destination, null);
	}

	/**
	 * Writes the contents of the Tree (without meta) to the specified Channel
	 * in the specified format. This method closes the Channel after writing.
	 * 
	 * @param destination
	 *            the destination WritableByteChannel
	 * @param format
	 *            name of the format (eg. "json", "yaml", "csv", "toml", etc.)
	 * 
	 * @throws IOException
	 *             Any I/O Exception
	 */
	public void writeTo(WritableByteChannel destination, String format) throws IOException {
		writeTo(destination, format, false, true);
	}

	/**
	 * Writes the contents of the Tree to the specified Channel in the specified
	 * format.
	 * 
	 * @param destination
	 *            the destination WritableByteChannel
	 * @param format
	 *            name of the format (eg. "msgpack", "bson", "cbor", etc.)
	 * @param insertMeta
	 *            serialize the meta structure or not
	 * @param closeDestination
	 *            close the Channel after writing
	 * 
	 * @throws IOException
	 *             Any I/O Exception
	 */
	public void writeTo(WritableByteChannel destination, String format, boolean insertMeta, boolean closeDestination)
			throws IOException {
		try {
			ByteBuffer buffer = ByteBuffer.wrap(toBinary(format, insertMeta));
			while (buffer.hasRemaining()) {
				destination.write(buffer);
			}
		} finally {
			if (closeDestination && destination != null) {
				try {
					destination.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	// --- WRITE TO OUTPUT STREAM ---

	/**
	 * Writes the contents of the Tree (without meta) to the specified Stream in
	 * JSON format. This method closes the Stream after writing.
	 * 
	 * @param destination
	 *            the destination OutputStream
	 * 
	 * @throws IOException
	 *             Any I/O Exception
	 */
	public void writeTo(OutputStream destination) throws IOException {
		writeTo(destination, null);
	}

	/**
	 * Writes the contents of the Tree (without meta) to the specified Stream in
	 * the specified format. This method closes the Stream after writing.
	 * 
	 * @param destination
	 *            the destination OutputStream
	 * @param format
	 *            name of the format (eg. "json", "yaml", "smile", "cbor", etc.)
	 * 
	 * @throws IOException
	 *             Any I/O Exception
	 */
	public void writeTo(OutputStream destination, String format) throws IOException {
		writeTo(destination, format, false, true);
	}

	/**
	 * Writes the contents of the Tree to the specified Stream in the specified
	 * format.
	 * 
	 * @param destination
	 *            the destination OutputStream
	 * @param format
	 *            name of the format (eg. "json", "yaml", "smile", "cbor", etc.)
	 * @param insertMeta
	 *            serialize the meta structure or not
	 * @param closeDestination
	 *            close the OutputStream after writing
	 * 
	 * @throws IOException
	 *             Any I/O Exception
	 */
	public void writeTo(OutputStream destination, String format, boolean insertMeta, boolean closeDestination)
			throws IOException {
		try {
			destination.write(toBinary(format, insertMeta));
		} finally {
			if (closeDestination && destination != null) {
				try {
					destination.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	// --- SORT SUB-NODES ---

	/**
	 * Sorts the sub-nodes of this node. The type of sorting (numeric or
	 * alphanumeric) is autodetected by the type of the first child. Samples:
	 * <br>
	 * <br>
	 * Tree node = new Tree();<br>
	 * Tree list = node.putList("list").add(3).add(2).add(1);<br>
	 * list.sort();<br>
	 * <br>
	 * The content of the "list" node will be "[1,2,3]".<br>
	 * <br>
	 * Tree map = new Tree().put("c",1).put("b",1).put("a",1);<br>
	 * map.sort();<br>
	 * <br>
	 * The content of the "map" node will be "{a:1,b:1,c:1}".
	 * 
	 * @return this (sorted) node
	 */
	public Tree sort() {

		// Check number of sortable values
		if (size() < 2) {
			return this;
		}

		// Lists, arrays sorted as a numeric / alphanumeric array
		if (isEnumeration()) {

			// Numeric array?
			boolean numeric = true;
			for (Tree child : this) {
				if (!Number.class.isAssignableFrom(child.getType())) {
					numeric = false;
					break;
				}
			}

			// Numeric sorting
			if (numeric) {
				return sort((node1, node2) -> {
					return node1.asBigDecimal().compareTo(node2.asBigDecimal());
				});
			}

			// Alphanumeric sorting
			return sort((node1, node2) -> {
				return String.CASE_INSENSITIVE_ORDER.compare(node1.asString(), node2.asString());
			});
		}

		// Map sorted by alphanumeric order by the name of the sub-nodes
		if (isMap()) {
			return sort((node1, node2) -> {
				return String.CASE_INSENSITIVE_ORDER.compare(node1.getName(), node2.getName());
			});
		}

		// Unable to sort
		return sort(null);
	}

	/**
	 * Sorts the sub-nodes of this node by the specified Comparator. Sample
	 * code:<br>
	 * <br>
	 * Tree node = new Tree().put("a",3).put("b",2).put("c",1);<br>
	 * node.sort((node1, node2) -&gt; {<br>
	 * return node1.asInteger() - node2.asInteger();<br>
	 * });<br>
	 * <br>
	 * The content of the "node" node will be "{c:1,b:2,a:3}".
	 * 
	 * @param comparator
	 *            custom comparator
	 * 
	 * @return this (sorted) node
	 */
	public Tree sort(Comparator<Tree> comparator) {
		int size = size();
		if (size < 2) {
			return this;
		}
		Tree[] array = new Tree[size];
		int index = 0;
		for (Tree child : this) {
			array[index++] = child;
		}
		Arrays.sort(array, comparator);
		clear();
		if (isMap()) {
			for (Tree child : array) {
				putObjectInternal(child.getName(), child.value, false);
			}
		} else {
			for (Tree child : array) {
				addObjectInternal(child.value);
			}
		}
		return this;
	}

	// --- FIND A CHILD ---

	/**
	 * Iterates over child elements, returning the first element predicate
	 * returns truthy for. Sample code:<br>
	 * <br>
	 * Tree found = node.find((child) -&gt; {<br>
	 * return child.get("path.to.value").asDouble() &gt; 10;<br>
	 * });
	 * 
	 * @param search
	 *            the function invoked per iteration
	 * 
	 * @return child node where the input argument matches the predicate (or
	 *         null)
	 */
	public Tree find(Predicate<Tree> search) {
		for (Tree child : this) {
			if (search.test(child)) {
				return child;
			}
		}
		return null;
	}

	// --- OTHER UTILITIES ---

	/**
	 * Returns {@code true} if the value of this node is {@code null}.
	 * 
	 * @return {@code true} if the value is {@code null}
	 */
	public boolean isNull() {
		return value == null;
	}

	/**
	 * Returns {@code true} if this node is the top-level (document) node of in
	 * its strucuture.
	 * 
	 * @return {@code true} if this node has no parent node
	 */
	public boolean isRoot() {
		return parent == null;
	}

	/**
	 * Returns {@code true} if the value of this node is a "primitive" value
	 * (eg. int, String, Date), not a structure (List, Set, Map, Array).
	 * 
	 * @return {@code true} if the value is a scalar (eg. Float, UUID) value
	 */
	public boolean isPrimitive() {
		return value == null || !isStructure();
	}

	/**
	 * Returns {@code true} if the value of this node is a "structure" (List,
	 * Set, Map, or Array), not a primitive value (eg. int, String, Date).
	 * 
	 * @return {@code true} if the value is not a scalar (eg. Double, Boolean)
	 *         value
	 */
	public boolean isStructure() {
		return isStructure(value);
	}

	/**
	 * Returns {@code true} if the given value is a "structure" (List, Set, Map,
	 * or Array), not a primitive value (eg. int, String, Date).
	 * 
	 * @param value
	 *            input value
	 * 
	 * @return {@code true} if the value is not a scalar (eg. String, Integer)
	 *         value
	 */
	protected static final boolean isStructure(Object value) {
		return value != null && (value instanceof Map || value instanceof Collection || value.getClass().isArray());
	}

	/**
	 * Returns {@code true} if the value of this node is a List, Set, or Array.
	 * 
	 * @return {@code true} if the value is a List
	 */
	public boolean isEnumeration() {
		return value != null && (value instanceof Collection || value.getClass().isArray());
	}

	/**
	 * Returns {@code true} if the value of this node is a Map (~= JSON object).
	 * 
	 * @return {@code true} if the value is a Map
	 */
	public boolean isMap() {
		return value != null && value instanceof Map;
	}

	/**
	 * Returns {@code true} if the value of this node is a List.
	 * 
	 * @return {@code true} if the value is a List
	 */
	public boolean isList() {
		return value != null && value instanceof List;
	}

	/**
	 * Returns {@code true} if the value of this node is an Array.
	 * 
	 * @return {@code true} if the value is a List
	 */
	public boolean isArray() {
		return value != null && value.getClass().isArray();
	}

	/**
	 * Returns {@code true} if the value of this node is a Set.
	 * 
	 * @return {@code true} if the value is a Set
	 */
	public boolean isSet() {
		return value != null && value instanceof Set;
	}

	/**
	 * Returns {@code true} if this node contains no sub-nodes or any not-null
	 * value.
	 * 
	 * @return {@code true} if this node contains no elements
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Returns the number of sub-nodes or values in this node. A node with a
	 * {@code null} value has zero size. The size a node with a scalar (int,
	 * double, etc.) value is 1.
	 * 
	 * @return the number of values in this node
	 */
	@SuppressWarnings("rawtypes")
	public int size() {
		if (value == null) {
			return 0;
		}
		if (value instanceof Map) {
			return ((Map) value).size();
		}
		if (value instanceof Collection) {
			return ((Collection) value).size();
		}
		if (value.getClass().isArray()) {
			return Array.getLength(value);
		}
		return 1;
	}

	// --- RECURSIVE DEEP CLONE ---

	/**
	 * Creates and returns a copy of this node. Performs a "deep copy";
	 * recursively copies every sub-nodes and values. Sample code:<br>
	 * <br>
	 * Tree original = new Tree();<br>
	 * Tree copy = original.clone();
	 * 
	 * @return copy of this node
	 */
	@Override
	public Tree clone() {

		// Normal deep cloning
		try {
			return new Tree(null, key, DeepCloner.clone(value));
		} catch (Exception ignored) {

			// Unknown and/or unserializable objects
		}

		// JSON-based deep cloning
		try {
			return new Tree(toString(null, false, true));
		} catch (Exception cause) {
			throw new IllegalArgumentException("Unable to clone node!", cause);
		}
	}

	// --- SERIALIZATION / DESERIALIZATION ---

	/**
	 * Serializes this node into the target stream.
	 * 
	 * @param out
	 *            target stream
	 * 
	 * @throws IOException
	 *             any I/O exception
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		byte[] bytes = JavaBuiltin.serialize(value, meta, true);
		out.writeInt(bytes.length);
		out.write(bytes);
	}

	/**
	 * Loads (deserializes) this node's content from the source stream.
	 * 
	 * @param in
	 *            source stream
	 * 
	 * @throws IOException
	 *             any I/O exception
	 * @throws ClassNotFoundException
	 *             class not found
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		byte[] bytes = new byte[in.readInt()];
		in.readFully(bytes);
		try {
			value = JavaBuiltin.deserialize(bytes);
		} catch (Exception e) {
			throw new IOException(e);
		}
		moveMeta();
	}

}