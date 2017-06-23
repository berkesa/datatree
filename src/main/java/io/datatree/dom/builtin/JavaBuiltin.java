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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import io.datatree.dom.BASE64;
import io.datatree.dom.Priority;
import io.datatree.dom.TreeReaderRegistry;
import io.datatree.dom.TreeWriterRegistry;

/**
 * <b>BUILT-IN JAVA OBJECT SERIALIZATOR ADAPTER</b><br>
 * <br>
 * Description: Built-in binary reader / writer. Based on Java Object Serialization.<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * -Ddatatree.java.reader=io.datatree.dom.builtin.JavaBuiltin<br>
 * -Ddatatree.java.writer=io.datatree.dom.builtin.JavaBuiltin<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JavaBuiltin java = new JavaBuiltin();<br>
 * TreeReaderRegistry.setReader("java", java);<br>
 * TreeWriterRegistry.setWriter("java", java);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputBytes, "java");<br>
 * byte[] outputBytes = node.toBytes("java");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(1)
public class JavaBuiltin extends AbstractAdapter {

	// --- NAME OF THE FORMAT ---

	@Override
	public String getFormat() {
		return "java";
	}

	// --- IMPLEMENTED WRITER METHODS ---

	public byte[] toBinary(Object value, Object meta, boolean insertMeta) {
		return serialize(value, meta, insertMeta);
	}

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return BASE64.encode(serialize(value, meta, insertMeta));
	}

	public static final byte[] serialize(Object value, Object meta, boolean insertMeta) {
		if (value == null) {
			return new byte[0];
		}

		// Try to serialize content (type-safe serialization)
		if ((value == null || value instanceof Serializable) && (meta == null || meta instanceof Serializable)) {
			Map<Object, Object> map = insertMeta(value, meta, insertMeta);
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
				baos.write(1);
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(value);
				oos.flush();
				return baos.toByteArray();
			} catch (Throwable ignored) {
			} finally {
				removeMeta(map);
			}
		}

		// Write content as JSON
		byte[] bytes = TreeWriterRegistry.getWriter(null).toString(value, meta, false, insertMeta)
				.getBytes(StandardCharsets.UTF_8);
		byte[] copy = new byte[bytes.length + 1];
		System.arraycopy(bytes, 0, copy, 1, bytes.length);
		return copy;
	}

	// --- IMPLEMENTED PARSER METHODS ---

	public Object parse(byte[] source) throws Exception {
		return deserialize(source);
	}

	@Override
	public Object parse(String source) throws Exception {
		return deserialize(BASE64.decode(source));
	}

	// --- COMMON OBJECT DESERIALIZER ---

	public static final Object deserialize(byte[] bytes) throws Exception {
		if (bytes == null || bytes.length < 4) {
			return new LinkedHashMap<>();
		}

		// Try to deserialize content as Object (type-safe serialization)
		if (bytes[0] == 1) {
			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				bais.skip(1);
				return new ObjectInputStream(bais).readObject();
			} catch (Throwable ignored) {
			}
		}

		// Read content as JSON
		bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
		String json = new String(bytes, StandardCharsets.UTF_8);
		return TreeReaderRegistry.getReader(null).parse(json);
	}

}