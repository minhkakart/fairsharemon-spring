package io.github.minhkakart.fairsharemon.money;

import java.util.Locale;

import org.springframework.stereotype.Component;

@Component("grouped")
public class GroupedMoneyFormatter implements MoneyFormatter {

	@Override
	public String format(Money money) {
		// 800000 -> 800.000. Money is always scale 0, so the conversion is exact.
		return String.format(Locale.ROOT, "%,d", money.amount().toBigIntegerExact()).replace(',', '.');
	}

}
