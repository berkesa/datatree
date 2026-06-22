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
package io.datatree.dom.converters;

import static io.datatree.dom.converters.DataConverterRegistry.convert;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import org.junit.jupiter.api.Test;

/**
 * Tests for the converters added in 2.0.0: all eight primitive target types,
 * {@code char}/{@link Character}, and the {@code java.time} (JSR-310) type set.
 * These previously had to be patched per-consumer (e.g. the qnet7
 * {@code Environment} workarounds).
 *
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class DataConverterRegistryTest {

	// --- PRIMITIVE TARGET TYPES (X.TYPE) ---

	@Test
	public void testPrimitiveTargets() throws Exception {

		// The three types qnet7 had to patch...
		assertEquals(Integer.valueOf(5), convert(int.class, "5"));
		assertEquals(Double.valueOf(3.5), convert(double.class, "3.5"));
		assertEquals(Boolean.TRUE, convert(boolean.class, "true"));

		// ...and the ones it never patched but are broken by the same cause
		assertEquals(Long.valueOf(10L), convert(long.class, "10"));
		assertEquals(Float.valueOf(2.5f), convert(float.class, "2.5"));
		assertEquals(Short.valueOf((short) 7), convert(short.class, "7"));
		assertEquals(Byte.valueOf((byte) 3), convert(byte.class, "3"));
		assertEquals(Character.valueOf('A'), convert(char.class, "A"));

		// Primitive target from a non-String source type (qnet7 only handled
		// int<-Integer and int<-String, never int<-Long)
		assertEquals(Integer.valueOf(42), convert(int.class, Long.valueOf(42)));
		assertEquals(Long.valueOf(7L), convert(long.class, Integer.valueOf(7)));
		assertEquals(Boolean.TRUE, convert(boolean.class, Boolean.TRUE));
	}

	// --- CHARACTER / CHAR (completely missing before) ---

	@Test
	public void testCharacter() throws Exception {
		assertEquals(Character.valueOf('h'), convert(Character.class, "hello"));
		assertEquals(Character.valueOf('h'), convert(char.class, "hello"));
		assertEquals(Character.valueOf('5'), convert(Character.class, Integer.valueOf(53)));
		assertEquals(Character.valueOf('1'), convert(Character.class, Boolean.TRUE));
		assertEquals(Character.valueOf('0'), convert(Character.class, Boolean.FALSE));
	}

	// --- JAVA.TIME: INSTANT + DATE/EPOCH INTEROP ---

	@Test
	public void testInstantInterop() throws Exception {
		long millis = 1700000000000L;
		Instant instant = Instant.ofEpochMilli(millis);
		Date date = new Date(millis);

		assertEquals(instant, convert(Instant.class, millis));
		assertEquals(instant, convert(Instant.class, instant.toString()));
		assertEquals(instant, convert(Instant.class, date));

		// reverse interop
		assertEquals(date, convert(Date.class, instant));
		assertEquals(Long.valueOf(millis), convert(long.class, instant));
	}

	// --- JAVA.TIME: PARSED LOCAL / OFFSET / ZONED TYPES ---

	@Test
	public void testLocalAndZonedTypes() throws Exception {
		assertEquals(LocalDate.parse("2007-12-03"), convert(LocalDate.class, "2007-12-03"));
		assertEquals(LocalDateTime.parse("2007-12-03T10:15:30"), convert(LocalDateTime.class, "2007-12-03T10:15:30"));
		assertEquals(OffsetDateTime.parse("2007-12-03T10:15:30+01:00"),
				convert(OffsetDateTime.class, "2007-12-03T10:15:30+01:00"));
		assertEquals(ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]"),
				convert(ZonedDateTime.class, "2007-12-03T10:15:30+01:00[Europe/Paris]"));
		assertEquals(YearMonth.parse("2023-11"), convert(YearMonth.class, "2023-11"));
		assertEquals(MonthDay.parse("--12-03"), convert(MonthDay.class, "--12-03"));
		assertEquals(Year.of(2023), convert(Year.class, "2023"));
		assertEquals(Year.of(2023), convert(Year.class, Integer.valueOf(2023)));
	}

	// --- JAVA.TIME: DURATION (covers the qnet7 workaround cases) ---

	@Test
	public void testDuration() throws Exception {
		assertEquals(Duration.ofMillis(500), convert(Duration.class, "500ms"));
		assertEquals(Duration.ofSeconds(10), convert(Duration.class, "10s"));
		assertEquals(Duration.ofSeconds(10), convert(Duration.class, "PT10S"));
		assertEquals(Duration.ofMillis(1500), convert(Duration.class, Integer.valueOf(1500)));
		assertEquals(Duration.ofMillis(1500), convert(Duration.class, "1500"));
	}

	// --- JAVA.TIME: PERIOD / ENUM-LIKE / ZONE TYPES ---

	@Test
	public void testPeriodEnumsAndZones() throws Exception {
		assertEquals(Period.parse("P1Y2M3D"), convert(Period.class, "1Y2M3D"));
		assertEquals(Period.ofDays(3), convert(Period.class, "P3D"));
		assertEquals(DayOfWeek.MONDAY, convert(DayOfWeek.class, "monday"));
		assertEquals(DayOfWeek.MONDAY, convert(DayOfWeek.class, Integer.valueOf(1)));
		assertEquals(Month.MARCH, convert(Month.class, "march"));
		assertEquals(Month.MARCH, convert(Month.class, Integer.valueOf(3)));
		assertEquals(ZoneId.of("Europe/Paris"), convert(ZoneId.class, "Europe/Paris"));
		assertEquals(ZoneOffset.of("+01:00"), convert(ZoneOffset.class, "+01:00"));
	}

	// --- JAVA.TIME VALUE -> STRING (already covered by the default String converter) ---

	@Test
	public void testTimeToString() throws Exception {
		assertEquals("PT10S", convert(String.class, Duration.ofSeconds(10)));
		assertEquals("2007-12-03", convert(String.class, LocalDate.parse("2007-12-03")));
	}

}
