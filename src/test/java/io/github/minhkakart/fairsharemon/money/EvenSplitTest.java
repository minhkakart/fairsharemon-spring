package io.github.minhkakart.fairsharemon.money;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class EvenSplitTest {

    @Test
    void leftoverDongGoToTheFirstShares() {
        List<Money> shares = EvenSplit.of(Money.of(800_000), 3);

        assertThat(shares).containsExactly(Money.of(266_667), Money.of(266_667), Money.of(266_666));
    }

    @Test
    void sharesAlwaysAddUpToTheTotal() {
        for (int parts = 1; parts <= 12; parts++) {
            Money total = Money.of(1_000_003);

            BigDecimal sum = EvenSplit.of(total, parts).stream()
                    .map(Money::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            assertThat(sum).isEqualByComparingTo(total.amount());
        }
    }

    @Test
    void evenSplitHasNoLeftover() {
        assertThat(EvenSplit.of(Money.of(900_000), 3))
                .containsOnly(Money.of(300_000));
    }

    @Test
    void zeroPartsIsRejected() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> EvenSplit.of(Money.of(1), 0));
    }
}
