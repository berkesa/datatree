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
package org.datatree.dom;

/**
 * TreeWriter provides functionality for converting structured data to String or
 * byte array format.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public interface TreeWriter {

	/**
	 * Method to convert content into byte array (eg. CBOR, SMILE, etc.).
	 * 
	 * @param value
	 *            content (Maps, Lists and other Java types)
	 * @param meta
	 *            optional meta structure (Map or null)
	 * @param insertMeta
	 *            insert meta into the byte array
	 * 
	 * @return serialized Java Object as byte array
	 */
	public byte[] toBinary(Object value, Object meta, boolean insertMeta);

	/**
	 * Method to convert content into String (eg. JSON, XML, etc.).
	 * 
	 * @param value
	 *            content (Maps, Lists and other Java types)
	 * @param meta
	 *            optional meta structure (Map or null)
	 * @param pretty
	 *            try to generate formatted output
	 * @param insertMeta
	 *            insert meta structure into the String
	 * 
	 * @return serialized Java Object as String
	 */
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta);

	/**
	 * Returns the supported format's name of this writer.
	 * 
	 * @return name of the format (eg. "json", "xml", "yaml", "toml", "bson",
	 *         etc.)
	 */
	public String getFormat();
	
}