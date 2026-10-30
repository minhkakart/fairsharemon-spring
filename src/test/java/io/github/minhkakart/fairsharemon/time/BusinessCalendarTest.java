package io.github.minhkakart.fairsharemon.time;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessCalendarTest {

	private static BusinessCalendar at(String instant) {
		return new BusinessCalendar(Clock.fixed(Instant.parse(instant), ZoneOffset.UTC));
	}

	@Test
	void breakfastInHanoiIsAlreadyTheNextDayInUtc() {
		assertThat(at("2026-03-05T23:00:00Z").today()).isEqualTo(LocalDate.of(2026, 3, 6));
	}

	@Test
	void justBeforeMidnightInHanoiIsStillTheSameDay() {
		assertThat(at("2026-03-05T16:59:59Z").today()).isEqualTo(LocalDate.of(2026, 3, 5));
	}

}
