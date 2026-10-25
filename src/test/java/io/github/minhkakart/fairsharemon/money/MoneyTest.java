package io.github.minhkakart.fairsharemon.money;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void sameAmountWithDifferentScaleIsEqual() {
        Money a = new Money(new BigDecimal("2.00"));
        Money b = new Money(new BigDecimal("2"));

        assertThat(a).isEqualTo(b);
        assertThat(a).hasSameHashCodeAs(b);
    }

    @Test
    void amountIsNormalizedToScaleZero() {
        assertThat(new Money(new BigDecimal("2.00")).amount().scale()).isZero();
    }

    @Test
    void negativeAmountIsRejected() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Money(new BigDecimal("-1")))
                .withMessage("amount must be >= 0");
    }

    @Test
    void fractionOfADongIsRejected() {
        assertThatExceptionOfType(ArithmeticException.class)
                .isThrownBy(() -> new Money(new BigDecimal("1.5")));
    }

    @Test
    void nullIsRejected() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Money(null))
                .withMessage("amount");
    }

    @Test
    void ofBuildsFromWholeDong() {
        assertThat(Money.of(800_000)).isEqualTo(new Money(new BigDecimal("800000.00")));
    }
}
