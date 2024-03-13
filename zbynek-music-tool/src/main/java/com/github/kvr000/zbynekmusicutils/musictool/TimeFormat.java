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

import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RequiredArgsConstructor()
public class TimeFormat
{
	public static final Pattern SINCE_PATTERN = Pattern.compile("^(?:((?:(\\d+):)?(\\d+)\\s*([dhms]))|((\\d+)-(\\d+)-(\\d+)T(\\d+):(\\d+):(\\d+)))$");

	private final Clock clock;

	@Inject
	public TimeFormat()
	{
		this(Clock.systemUTC());
	}

	/**
	 * Parses last time period, in format of:
	 * - yyyy-MM-ddThh:mm:ss
	 * - N:N h (N*7+N) days before
	 * - N:N h (N*24+N) hours before
	 * - N:N m (N*60+N) minutes before
	 * - N:N s (N*60+N) seconds before
	 *
	 * @param time
	 *      time to parse
	 * @param zone
	 *      zone for translating time
	 *
	 * @return
	 *      Instant representing the time
	 */
	public Instant parseLast(String time, ZoneId zone)
	{
		Matcher match = SINCE_PATTERN.matcher(time);
		if (!match.matches()) {
			throw new IllegalArgumentException("Expected number{d|h|m|s} or yyyy-MM-ddThh:mm:ss but got: " + time);
		}
		if (match.group(1) != null) {
			long leading = Optional.ofNullable(match.group(2)).map(Long::parseLong).orElse(0L);
			long number = Long.parseLong(match.group(3));
			return switch (match.group(4)) {
				case "d" -> clock.instant().minus(leading * 7 + number, ChronoUnit.DAYS);
				case "h" -> clock.instant().minus(leading * 24 + number, ChronoUnit.HOURS);
				case "m" -> clock.instant().minus(leading * 60 + number, ChronoUnit.MINUTES);
				case "s" -> clock.instant().minus(leading * 60 + number, ChronoUnit.SECONDS);
				default ->
					throw new IllegalStateException("Unexpected unit from pattern: " + match.group(4));
			};
		}
		else {
			return DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(zone)
				.parse(match.group(5), Instant::from);
		}
	}
}
