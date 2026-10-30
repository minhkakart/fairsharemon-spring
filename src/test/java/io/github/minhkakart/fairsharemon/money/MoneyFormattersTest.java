package io.github.minhkakart.fairsharemon.money;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MoneyFormattersTest {

	@Autowired
	MoneyFormatters formatters;

	@Autowired
	Map<String, MoneyFormatter> injected;

	@Test
	void everyFormatterBeanIsInjectedUnderItsBeanName() {
		assertThat(injected).containsOnlyKeys("plain", "grouped");
	}

	@Test
	void formatsByStyle() {
		Money money = Money.of(800_000);
		assertThat(formatters.format("plain", money)).isEqualTo("800000");
		assertThat(formatters.format("grouped", money)).isEqualTo("800.000");
	}

	@Test
	void unknownStyleIsRejected() {
		assertThatThrownBy(() -> formatters.format("fancy", Money.of(1)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Unknown money style: fancy");
	}

}
