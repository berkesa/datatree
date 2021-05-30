/**
 * This software is licensed under the Apache 2 license, quoted below.<br>
 * <br>
 * Copyright 2018 Andras Berkes [andras.berkes@programmer.net]<br>
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
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * Simple and fast memory cache. This implementation uses optimistic locking for
 * the maximum performance.
 * 
 * @param <K>
 *            Type (class) of cache keys
 * @param <V>
 *            Type (class) of cached values
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class Cache<K, V> {

	// --- INTERNAL VARIABLES ---

	protected final ReadLock readLock;
	protected final WriteLock writeLock;

	protected final LinkedHashMap<K, V> map;

	// --- CONSTRUCTORS ---

	/**
	 * Creates a cache with the specified capacity.
	 * 
	 * @param capacity
	 *            maximum capacity of the cache
	 */
	public Cache(int capacity) {

		// Insertion-order is thread safe, access-order is not
		map = new LinkedHashMap<K, V>(capacity, 1.0f, false) {

			private static final long serialVersionUID = 5994447707758047152L;

			protected final boolean removeEldestEntry(Map.Entry<K, V> entry) {
				return size() > capacity;
			};
		};

		// Create locks
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		readLock = lock.readLock();
		writeLock = lock.writeLock();
	}

	// --- GET / PUT / ETC ---

	public V get(K key) {
		V value = null;
		readLock.lock();
		try {
			value = map.get(key);
		} finally {
			readLock.unlock();
		}
		return value;
	}

	public void put(K key, V value) {
		writeLock.lock();
		try {
			map.put(key, value);
		} finally {
			writeLock.unlock();
		}
	}

	public V putIfAbsent(K key, V value) {
		V prev;
		writeLock.lock();
		try {
			prev = map.putIfAbsent(key, value);
		} finally {
			writeLock.unlock();
		}
		if (prev == null) {
			return value;
		}
		return prev;
	}
	
	public void remove(K key) {
		writeLock.lock();
		try {
			map.remove(key);
		} finally {
			writeLock.unlock();
		}
	}

	public void clear() {
		writeLock.lock();
		try {
			map.clear();
		} finally {
			writeLock.unlock();
		}
	}

	public int size() {
		return map.size();
	}

}