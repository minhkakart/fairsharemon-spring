package io.github.minhkakart.fairsharemon.time;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.stereotype.Service;

/**
 * Answers "which day is it?" for the people using the app. It holds no state of its own:
 * the only thing it keeps is the shared, immutable Clock it was given.
 */
@Service
public class BusinessCalendar {

	private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

	private final Clock clock;

	public BusinessCalendar(Clock clock) {
		this.clock = clock;
	}

	public LocalDate today() {
		return LocalDate.now(clock.withZone(ZONE));
	}

}
