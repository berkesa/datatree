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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Common configurator for reader/writer APIs. Used System Properties:
 * <ul>
 * <li>-Ddatatree.time.zone=os</li>
 * <li>-Ddatatree.timestamp.format="yyyy-MM-dd'T'HH:mm:ss.SSSX"</li>
 * <li>-Ddatatree.cache.size=1024</li>
 * <li>-Ddatatree.pool.size=16</li>
 * <li>-Ddatatree.use.timestamps=true</li>
 * <li>-Ddatatree.adapter.packages=your.adapter.package1,your.adapter.package2</li>
 * <li>-Ddatatree.base64.codec=your.base64.Codec</li>
 * </ul>
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class Config {

	// Default time zone
	// -Ddatatree.time.zone=os
	public static final String DEFAULT_TIME_ZONE;

	// Timestamp format
	// -Ddatatree.timestamp.format="yyyy-MM-dd'T'HH:mm:ss.SSSX"
	public static final String TIMESTAMP_FORMAT;

	// Timestamp formatter
	// -Ddatatree.timestamp.format="yyyy-MM-dd'T'HH:mm:ss.SSSX"
	public static final DateFormat TIMESTAMP_FORMATTER;

	// Default cache size (eg. size of the date parser's cahce)
	// -Ddatatree.cache.size=1024
	public static final int CACHE_SIZE;

	// Default object pool size (parsers, readers/writers, etc.)
	// -Ddatatree.pool.size=16
	public static final int POOL_SIZE;

	// Write dates as formatted timestamps instead of msec values
	// -Ddatatree.use.timestamps=true
	public static final boolean USE_TIMESTAMPS;

	// List of Java packages (NodeReaders and NodeWriters)
	// -Ddatatree.adapter.packages=your.adapter.package1,your.adapter.package2
	public static final String ADAPTER_PACKAGES;

	// BASE64 implementation (class name)
	// -Ddatatree.base64.codec=your.base64.Codec
	public static final String BASE64_CODEC;
	
	// --- INIT PROPERTIES ---

	static {

		// Default time zone is the Operating System's time zone.
		// Set the "datatree.time.zone" system property to "GMT",
		// to use "fixed" GMT (or other) time zone.
		String timeZoneID = System.getProperty("datatree.time.zone", "os");
		DEFAULT_TIME_ZONE = "os".equalsIgnoreCase(timeZoneID) ? TimeZone.getDefault().getID() : timeZoneID;
		TimeZone timeZone = TimeZone.getTimeZone(DEFAULT_TIME_ZONE);

		// Timestamp format
		TIMESTAMP_FORMAT = System.getProperty("datatree.timestamp.format", "yyyy-MM-dd'T'HH:mm:ss.SSSX");
		TIMESTAMP_FORMATTER = new SimpleDateFormat(TIMESTAMP_FORMAT);
		TIMESTAMP_FORMATTER.setTimeZone(timeZone);

		// Cache size
		int size;
		try {
			size = Integer.parseInt(System.getProperty("datatree.cache.size", "1024"));
		} catch (Exception cause) {
			cause.printStackTrace();
			size = 1024;
		}
		CACHE_SIZE = size;

		// Pool size of pooled reader and writer instances
		try {
			int defaultPoolSize = (Runtime.getRuntime().availableProcessors() + 1) * 2;
			size = Integer.parseInt(System.getProperty("datatree.pool.size", Integer.toString(defaultPoolSize)));
		} catch (Exception cause) {
			cause.printStackTrace();
			size = 16;
		}
		POOL_SIZE = size;

		// Use formatted timestamps (Date -> JSON)
		USE_TIMESTAMPS = "true".equalsIgnoreCase(System.getProperty("datatree.use.timestamps", "true"));
		
		// List of Java packages to scan for adapters (readers and writers)
		ADAPTER_PACKAGES = System.getProperty("datatree.adapter.packages");
		
		// BASE64 implementation (class name)
		BASE64_CODEC = System.getProperty("datatree.base64.codec");
	}

}