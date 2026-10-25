package io.github.minhkakart.fairsharemon.lesson01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

/**
 * One test per claim in the Lesson 01 post, so each one can be checked by running it.
 * Details 5 and 6 need JPA and Spring transactions, so they are not here.
 */
class JavaDetailsTest {

    @Test
    void detail1_bigDecimalEqualsComparesScaleToo() {
        BigDecimal a = new BigDecimal("2.0");
        BigDecimal b = new BigDecimal("2.00");

        assertThat(a.equals(b)).isFalse();
        assertThat(a.compareTo(b)).isZero();
        assertThat(a).isEqualByComparingTo(b);
    }

    @Test
    void detail2_bigDecimalFromDoubleKeepsTheDoubleError() {
        BigDecimal fromDouble = new BigDecimal(0.1);

        assertThat(fromDouble.toPlainString())
                .isEqualTo("0.1000000000000000055511151231257827021181583404541015625");
        assertThat(fromDouble.scale()).isEqualTo(55);
        assertThat(new BigDecimal("0.1")).isEqualTo(BigDecimal.valueOf(0.1));
    }

    @Test
    void detail3_divideThrowsWhenTheResultDoesNotTerminate() {
        BigDecimal total = new BigDecimal("800000");
        BigDecimal three = new BigDecimal("3");

        assertThatExceptionOfType(ArithmeticException.class)
                .isThrownBy(() -> total.divide(three))
                .withMessage("Non-terminating decimal expansion; no exact representable decimal result.");

        BigDecimal each = total.divide(three, 0, RoundingMode.DOWN);
        assertThat(each).isEqualByComparingTo("266666");
        assertThat(total.subtract(each.multiply(three))).isEqualByComparingTo("2");
    }

    @Test
    @SuppressWarnings("NumberEquality")
    void detail4_doubleEqualsComparesReferencesAndOnlySmallIntegersAreCached() {
        Integer a127 = 127;
        Integer b127 = 127;
        Integer a128 = 128;
        Integer b128 = 128;

        assertThat(a127 == b127).isTrue();
        assertThat(a128 == b128).isFalse();
        assertThat(a128.equals(b128)).isTrue();
    }

    @Test
    void detail7_theSameInstantIsADifferentDayInUtc() {
        ZonedDateTime breakfast = ZonedDateTime.of(2026, 3, 6, 6, 0, 0, 0, ZoneId.of("Asia/Ho_Chi_Minh"));
        Instant instant = breakfast.toInstant();

        assertThat(instant).isEqualTo(Instant.parse("2026-03-05T23:00:00Z"));
        assertThat(breakfast.toLocalDate()).isEqualTo(LocalDate.of(2026, 3, 6));
        assertThat(instant.atZone(ZoneOffset.UTC).toLocalDate()).isEqualTo(LocalDate.of(2026, 3, 5));
    }
}
