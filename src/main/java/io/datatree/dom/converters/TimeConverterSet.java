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
package io.datatree.dom.converters;

import static io.datatree.dom.converters.DataConverterRegistry.register;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

import io.datatree.dom.Config;

/**
 * Converters for the modern Java Date and Time API (JSR-310, {@code java.time}).
 * The legacy {@link java.util.Date} converters live in {@code BasicConverterSet};
 * this set adds {@link Instant}, {@link LocalDate}, {@link LocalDateTime},
 * {@link ZonedDateTime}, {@link Duration}, etc. as conversion targets, and the
 * reverse interop ({@code java.time} value &rarr; {@link Date} / epoch millis).
 *
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
final class TimeConverterSet extends AbstractConverterSet {

	// --- DEFAULT TIME ZONE (same source as the legacy Date converters) ---

	private static final ZoneId ZONE = TimeZone.getTimeZone(Config.DEFAULT_TIME_ZONE).toZoneId();

	// --- INIT JAVA.TIME CONVERTERS ---

	static {

		// --- VALUE TO INSTANT ---

		register(Instant.class, (from) -> {
			if (from instanceof Date) {
				return ((Date) from).toInstant();
			}
			if (from instanceof Number) {
				return Instant.ofEpochMilli(((Number) from).longValue());
			}
			String txt = String.valueOf(from).trim();
			if (isEpochString(txt)) {
				return Instant.ofEpochMilli(Long.parseLong(txt));
			}
			return Instant.parse(txt);
		});

		// --- VALUE TO LOCAL DATE ---

		register(LocalDate.class, (from) -> {
			if (from instanceof Number) {
				return LocalDate.ofInstant(Instant.ofEpochMilli(((Number) from).longValue()), ZONE);
			}
			if (from instanceof Date) {
				return LocalDate.ofInstant(((Date) from).toInstant(), ZONE);
			}
			String txt = String.valueOf(from).trim();
			if (isEpochString(txt)) {
				return LocalDate.ofInstant(Instant.ofEpochMilli(Long.parseLong(txt)), ZONE);
			}
			return LocalDate.parse(txt);
		});

		// --- VALUE TO LOCAL TIME ---

		register(LocalTime.class, (from) -> {
			if (from instanceof Number) {
				return LocalTime.ofInstant(Instant.ofEpochMilli(((Number) from).longValue()), ZONE);
			}
			if (from instanceof Date) {
				return LocalTime.ofInstant(((Date) from).toInstant(), ZONE);
			}
			String txt = String.valueOf(from).trim();
			if (isEpochString(txt)) {
				return LocalTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(txt)), ZONE);
			}
			return LocalTime.parse(txt);
		});

		// --- VALUE TO LOCAL DATE TIME ---

		register(LocalDateTime.class, (from) -> {
			if (from instanceof Number) {
				return LocalDateTime.ofInstant(Instant.ofEpochMilli(((Number) from).longValue()), ZONE);
			}
			if (from instanceof Date) {
				return LocalDateTime.ofInstant(((Date) from).toInstant(), ZONE);
			}
			String txt = String.valueOf(from).trim();
			if (isEpochString(txt)) {
				return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(txt)), ZONE);
			}
			return LocalDateTime.parse(txt);
		});

		// --- VALUE TO OFFSET DATE TIME ---

		register(OffsetDateTime.class, (from) -> {
			if (from instanceof Number) {
				return OffsetDateTime.ofInstant(Instant.ofEpochMilli(((Number) from).longValue()), ZONE);
			}
			if (from instanceof Date) {
				return OffsetDateTime.ofInstant(((Date) from).toInstant(), ZONE);
			}
			String txt = String.valueOf(from).trim();
			if (isEpochString(txt)) {
				return OffsetDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(txt)), ZONE);
			}
			return OffsetDateTime.parse(txt);
		});

		// --- VALUE TO OFFSET TIME ---

		register(OffsetTime.class, (from) -> {
			if (from instanceof Number) {
				return OffsetTime.ofInstant(Instant.ofEpochMilli(((Number) from).longValue()), ZONE);
			}
			if (from instanceof Date) {
				return OffsetTime.ofInstant(((Date) from).toInstant(), ZONE);
			}
			String txt = String.valueOf(from).trim();
			if (isEpochString(txt)) {
				return OffsetTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(txt)), ZONE);
			}
			return OffsetTime.parse(txt);
		});

		// --- VALUE TO ZONED DATE TIME ---

		register(ZonedDateTime.class, (from) -> {
			if (from instanceof Number) {
				return ZonedDateTime.ofInstant(Instant.ofEpochMilli(((Number) from).longValue()), ZONE);
			}
			if (from instanceof Date) {
				return ZonedDateTime.ofInstant(((Date) from).toInstant(), ZONE);
			}
			String txt = String.valueOf(from).trim();
			if (isEpochString(txt)) {
				return ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(txt)), ZONE);
			}
			return ZonedDateTime.parse(txt);
		});

		// --- VALUE TO DURATION ---
		// (matches the legacy qnet7 workaround: "500ms" / "10s" / "PT10S" / millis)

		register(Duration.class, (from) -> {
			if (from instanceof Number) {
				return Duration.ofMillis(((Number) from).longValue());
			}
			String txt = String.valueOf(from).trim().toUpperCase();
			if (isEpochString(txt)) {
				return Duration.ofMillis(Long.parseLong(txt));
			}
			if (txt.endsWith("MS")) {
				return Duration.ofMillis(Long.parseLong(toNumericString(txt, false)));
			}
			if (txt.startsWith("P") || txt.startsWith("-P")) {
				return Duration.parse(txt);
			}
			return Duration.parse("PT" + txt);
		});

		// --- VALUE TO PERIOD ---

		register(Period.class, (from) -> {
			String txt = String.valueOf(from).trim().toUpperCase();
			if (txt.startsWith("P") || txt.startsWith("-P")) {
				return Period.parse(txt);
			}
			return Period.parse("P" + txt);
		});

		// --- VALUE TO YEAR ---

		register(Year.class, (from) -> {
			if (from instanceof Number) {
				return Year.of(((Number) from).intValue());
			}
			if (from instanceof Date) {
				return Year.from(((Date) from).toInstant().atZone(ZONE));
			}
			String txt = String.valueOf(from).trim();
			if (isEpochString(txt)) {
				return Year.of(Integer.parseInt(txt));
			}
			return Year.parse(txt);
		});

		// --- VALUE TO YEAR-MONTH ---

		register(YearMonth.class, (from) -> {
			if (from instanceof Number) {
				return YearMonth.from(Instant.ofEpochMilli(((Number) from).longValue()).atZone(ZONE));
			}
			if (from instanceof Date) {
				return YearMonth.from(((Date) from).toInstant().atZone(ZONE));
			}
			String txt = String.valueOf(from).trim();
			if (isEpochString(txt)) {
				return YearMonth.from(Instant.ofEpochMilli(Long.parseLong(txt)).atZone(ZONE));
			}
			return YearMonth.parse(txt);
		});

		// --- VALUE TO MONTH-DAY ---

		register(MonthDay.class, (from) -> {
			if (from instanceof Number) {
				return MonthDay.from(Instant.ofEpochMilli(((Number) from).longValue()).atZone(ZONE));
			}
			if (from instanceof Date) {
				return MonthDay.from(((Date) from).toInstant().atZone(ZONE));
			}
			return MonthDay.parse(String.valueOf(from).trim());
		});

		// --- VALUE TO DAY-OF-WEEK ---

		register(DayOfWeek.class, (from) -> {
			if (from instanceof Number) {
				return DayOfWeek.of(((Number) from).intValue());
			}
			String txt = String.valueOf(from).trim();
			if (isEpochString(txt)) {
				return DayOfWeek.of(Integer.parseInt(txt));
			}
			return DayOfWeek.valueOf(txt.toUpperCase());
		});

		// --- VALUE TO MONTH ---

		register(Month.class, (from) -> {
			if (from instanceof Number) {
				return Month.of(((Number) from).intValue());
			}
			String txt = String.valueOf(from).trim();
			if (isEpochString(txt)) {
				return Month.of(Integer.parseInt(txt));
			}
			return Month.valueOf(txt.toUpperCase());
		});

		// --- VALUE TO ZONE-ID / ZONE-OFFSET ---

		register(ZoneId.class, (from) -> {
			return ZoneId.of(String.valueOf(from).trim());
		});
		register(ZoneOffset.class, (from) -> {
			return ZoneOffset.of(String.valueOf(from).trim());
		});

		// --- JAVA.TIME VALUE TO LEGACY DATE (interop) ---

		register(Date.class, Instant.class, (from) -> {
			return Date.from(from);
		});
		register(Date.class, ZonedDateTime.class, (from) -> {
			return Date.from(from.toInstant());
		});
		register(Date.class, OffsetDateTime.class, (from) -> {
			return Date.from(from.toInstant());
		});
		register(Date.class, LocalDateTime.class, (from) -> {
			return Date.from(from.atZone(ZONE).toInstant());
		});
		register(Date.class, LocalDate.class, (from) -> {
			return Date.from(from.atStartOfDay(ZONE).toInstant());
		});

		// --- JAVA.TIME VALUE TO EPOCH MILLIS (interop) ---

		register(Long.class, Instant.class, (from) -> {
			return from.toEpochMilli();
		});
		register(Long.class, ZonedDateTime.class, (from) -> {
			return from.toInstant().toEpochMilli();
		});
		register(Long.class, OffsetDateTime.class, (from) -> {
			return from.toInstant().toEpochMilli();
		});
		register(Long.class, LocalDateTime.class, (from) -> {
			return from.atZone(ZONE).toInstant().toEpochMilli();
		});
	}

	// --- UTILITY ---

	/**
	 * Returns {@code true} if the text is a plain (optionally negative) integer
	 * number, i.e. an "epoch milliseconds" value rather than a formatted
	 * date/time string.
	 *
	 * @param txt input text
	 * @return true if the text contains digits only (with an optional leading '-')
	 */
	private static final boolean isEpochString(String txt) {
		int len = txt.length();
		if (len == 0) {
			return false;
		}
		int start = (txt.charAt(0) == '-') ? 1 : 0;
		if (start == len) {
			return false;
		}
		for (int i = start; i < len; i++) {
			char c = txt.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	// --- PROTECTED CONSTRUCTOR ---

	protected TimeConverterSet() {
	}

}
