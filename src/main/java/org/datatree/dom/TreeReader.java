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
 * TreeReader provides functionality for reading and parsing structured data
 * from a given String or byte array.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public interface TreeReader {

	/**
	 * Method to deserialize content from given byte array.
	 * 
	 * @param source
	 *            source data (serialized Java Objects, CBOR, SMILE, BSON, etc.)
	 * 
	 * @return parsed data (Maps, Lists and other Java types)
	 * 
	 * @throws Exception
	 *             any data format exception
	 */
	public Object parse(byte[] source) throws Exception;

	/**
	 * Method to deserialize content from given content String.
	 * 
	 * @param source
	 *            source String (JSON, XML, YAML, TOML, CSV, etc.)
	 * 
	 * @return parsed data (Maps, Lists and other Java types)
	 * 
	 * @throws Exception
	 *             any data format exception
	 */
	public Object parse(String source) throws Exception;

	/**
	 * Returns the supported format's name of this reader.
	 * 
	 * @return name of the format (eg. "json", "xml", "yaml", "toml", "bson",
	 *         etc.)
	 */
	public String getFormat();

}