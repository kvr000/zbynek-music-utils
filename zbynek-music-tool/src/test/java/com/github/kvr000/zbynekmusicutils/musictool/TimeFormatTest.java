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
