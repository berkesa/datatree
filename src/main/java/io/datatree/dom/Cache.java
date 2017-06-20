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
package io.datatree.dom;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Simple and fast memory cache.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class Cache<KEY, VALUE> {

	private final Lock readerLock;
	private final Lock writerLock;

	private final WeakHashMap<KEY, VALUE> weakCache;
	private final LinkedHashMap<KEY, VALUE> hardCache;

	public Cache(final int capacity, final boolean fair) {
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock(fair);
		readerLock = lock.readLock();
		writerLock = lock.writeLock();
		weakCache = new WeakHashMap<KEY, VALUE>(capacity);
		hardCache = new LinkedHashMap<KEY, VALUE>(capacity + 1, 1.0f, true) {

			private static final long serialVersionUID = 5994447707758047152L;

			protected final boolean removeEldestEntry(Map.Entry<KEY, VALUE> entry) {
				if (this.size() > capacity) {
					weakCache.put(entry.getKey(), entry.getValue());
					return true;
				}
				return false;
			};
		};
	}

	public VALUE get(KEY key) {
		readerLock.lock();
		VALUE value;
		try {
			value = hardCache.get(key);
			if (value != null) {
				return value;
			}
			return weakCache.get(key);
		} finally {
			readerLock.unlock();
		}
	}

	public void put(KEY key, VALUE value) {
		writerLock.lock();
		try {
			weakCache.remove(key);
			hardCache.put(key, value);
		} finally {
			writerLock.unlock();
		}
	}

	public void remove(KEY key) {
		writerLock.lock();
		try {
			weakCache.remove(key);
			hardCache.remove(key);
		} finally {
			writerLock.unlock();
		}
	}

	public void clear() {
		writerLock.lock();
		try {
			weakCache.clear();
			hardCache.clear();
		} finally {
			writerLock.unlock();
		}
	}

	public int size() {
		readerLock.lock();
		try {
			return hardCache.size() + weakCache.size();
		} finally {
			readerLock.unlock();
		}
	}

}