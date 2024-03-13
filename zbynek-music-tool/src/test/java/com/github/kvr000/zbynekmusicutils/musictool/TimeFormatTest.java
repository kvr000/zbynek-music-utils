/*
 * zbynek-music-tool - various music files manipulation utilities
 *
 * Copyright 2024-2024 Zbynek Vyskovsky mailto:kvr000@gmail.com http://github.com/kvr000/ https://github.com/kvr000/zbynek-music-utils/ https://www.linkedin.com/in/zbynek-vyskovsky/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.kvr000.zbynekmusicutils.musictool;


import org.testng.annotations.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.testng.Assert.assertEquals;


public class TimeFormatTest
{
	private final TimeFormat timeFormat = new TimeFormat(Clock.fixed(Instant.parse("1977-03-12T06:00:00Z"), ZoneOffset.UTC));


	@Test
	public void parseSince_relativeDays_success()
	{
		Instant result = timeFormat.parseLast("1d", ZoneOffset.UTC);

		assertEquals(result, Instant.parse("1977-03-11T06:00:00Z"));
	}
	@Test
	public void parseSince_relativeHours_success()
	{
		Instant result = timeFormat.parseLast("1h", ZoneOffset.UTC);

		assertEquals(result, Instant.parse("1977-03-12T05:00:00Z"));
	}

	@Test
	public void parseSince_relativeMinutes_success()
	{
		Instant result = timeFormat.parseLast("60m", ZoneOffset.UTC);

		assertEquals(result, Instant.parse("1977-03-12T05:00:00Z"));
	}

	@Test
	public void parseSince_relativeSeconds_success()
	{
		Instant result = timeFormat.parseLast("2s", ZoneOffset.UTC);

		assertEquals(result, Instant.parse("1977-03-12T05:59:58Z"));
	}

	@Test
	public void parseSince_absolute_success()
	{
		Instant result = timeFormat.parseLast("1977-03-12T07:00:01", ZoneOffset.UTC);

		assertEquals(result, Instant.parse("1977-03-12T07:00:01Z"));
	}
}
