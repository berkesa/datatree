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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import io.datatree.dom.Priority;

/**
 * Shows the specified node's internal structure. Sample:<br>
 * <br>
 * String info = node.toString("debug", true);
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(1)
public class DebugBuiltin extends AbstractTextAdapter {

	// --- NAME OF THE FORMAT ---
	
	@Override
	public String getFormat() {
		return "debug";
	}
	
	// --- IMPLEMENTED WRITER METHODS ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			StringBuilder out = new StringBuilder(1024);
			dump(out, input, pretty ? 0 : -1);
			return out.toString().trim();			
		});
	}

	// --- PRIVATE UTILITIES ---

	@SuppressWarnings("rawtypes")
	protected static final void dump(StringBuilder out, Object source, int indent) {

		// Null value
		if (source == null) {
			out.append("Null");
			return;
		}

		// Numeric values
		if (source instanceof Number) {
			out.append("Number: ");
			out.append(source);
			return;
		}

		// Boolean values
		if (source instanceof Boolean) {
			out.append("Boolean: ");
			out.append(source);
			return;
		}

		// String values
		if (source instanceof Boolean) {
			out.append("String: ");
			out.append(source);
			return;
		}

		// Map
		if (source instanceof Map) {
			out.append("Map:\r\n");
			Map map = (Map) source;
			int newIndent = indent == -1 ? -1 : indent + 1;
			for (Object child : map.entrySet()) {
				Map.Entry entry = (Map.Entry) child;
				appendIndent(out, newIndent);
				out.append(entry.getKey());
				out.append(" -> ");
				dump(out, entry.getValue(), newIndent);
				out.append("\r\n");
			}
			return;

		}

		// List or Set
		if (source instanceof Collection) {
			out.append("Collection:\r\n");
			Collection array = (Collection) source;
			int pos = 0;
			int newIndent = indent == -1 ? -1 : indent + 1;
			for (Object child : array) {
				appendIndent(out, newIndent);
				out.append(pos++);
				out.append(" -> ");
				dump(out, child, newIndent);
				out.append("\r\n");
			}
			return;
		}

		// Array
		if (source.getClass().isArray()) {
			out.append("Array:\r\n");
			int pos = 0;
			int max = Array.getLength(source);
			int newIndent = indent == -1 ? -1 : indent + 1;
			for (int i = 0; i < max; i++) {
				appendIndent(out, newIndent);
				out.append(pos++);
				out.append(" -> ");
				dump(out, Array.get(source, i), newIndent);
				out.append("\r\n");
			}
			return;
		}

		// Object
		out.append(source.getClass());
		out.append(": ");
		out.append(source);
	}

	protected static final void appendIndent(StringBuilder out, int indent) {
		if (indent > 0) {
			for (int i = 0; i < indent; i++) {
				out.append("  ");
			}
		}
	}
	
	// --- IMPLEMENTED PARSER METHODS ---
	
	@Override
	public Object parse(String source) throws Exception {
		throw new UnsupportedOperationException();
	}
	
}