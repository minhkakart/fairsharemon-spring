package io.github.minhkakart.fairsharemon.money;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class EvenSplit {

    private EvenSplit() {
    }

    /**
     * Splits {@code total} into {@code parts} shares that add up to exactly {@code total}.
     * The dong left over after an even split goes one each to the first shares.
     */
    public static List<Money> of(Money total, int parts) {
        if (parts < 1) {
            throw new IllegalArgumentException("parts must be >= 1");
        }
        BigDecimal count = BigDecimal.valueOf(parts);
        BigDecimal base = total.amount().divideToIntegralValue(count);
        int leftover = total.amount().remainder(count).intValueExact();

        List<Money> shares = new ArrayList<>(parts);
        for (int i = 0; i < parts; i++) {
            shares.add(new Money(i < leftover ? base.add(BigDecimal.ONE) : base));
        }
        return List.copyOf(shares);
    }
}
