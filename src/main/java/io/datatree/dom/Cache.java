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
import java.util.concurrent.locks.StampedLock;

/**
 * Simple and fast memory cache.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class Cache<KEY, VALUE> {

	protected final StampedLock lock = new StampedLock();

	protected final LinkedHashMap<KEY, VALUE> map;

	public Cache(final int capacity, final boolean fair) {
		map = new LinkedHashMap<KEY, VALUE>(capacity + 1, 1.0f, false) {

			private static final long serialVersionUID = 5994447707758047152L;

			protected final boolean removeEldestEntry(Map.Entry<KEY, VALUE> entry) {
				if (this.size() > capacity) {
					return true;
				}
				return false;
			};
		};
	}

	public VALUE get(KEY key) {
		VALUE value = null;
		long stamp = lock.tryOptimisticRead();
		if (stamp != 0) {
			try {
				value = map.get(key);
			} catch (Exception modified) {
				stamp = 0;
			}
		}
		if (!lock.validate(stamp) || stamp == 0) {
			stamp = lock.readLock();
			try {
				value = map.get(key);
			} finally {
				lock.unlockRead(stamp);
			}
		}
		return value;
	}

	public void put(KEY key, VALUE value) {
		final long stamp = lock.writeLock();
		try {
			map.put(key, value);
		} finally {
			lock.unlockWrite(stamp);
		}
	}

	public void remove(KEY key) {
		final long stamp = lock.writeLock();
		try {
			map.remove(key);
		} finally {
			lock.unlockWrite(stamp);
		}
	}

	public void clear() {
		final long stamp = lock.writeLock();
		try {
			map.clear();
		} finally {
			lock.unlockWrite(stamp);
		}
	}

	public int size() {
		int count = 0;
		long stamp = lock.tryOptimisticRead();
		if (stamp != 0) {
			count = map.size();
		}
		if (!lock.validate(stamp) || stamp == 0) {
			stamp = lock.readLock();
			try {
				count = map.size();
			} finally {
				lock.unlockRead(stamp);
			}
		}
		return count;
	}

}