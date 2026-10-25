package io.github.minhkakart.fairsharemon.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount) {

    public Money {
        Objects.requireNonNull(amount, "amount");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must be >= 0");
        }
        // VND has no minor unit: 2.00 -> 2, 1.5 -> ArithmeticException
        amount = amount.setScale(0, RoundingMode.UNNECESSARY);
    }

    public static Money of(long dong) {
        return new Money(BigDecimal.valueOf(dong));
    }
}
