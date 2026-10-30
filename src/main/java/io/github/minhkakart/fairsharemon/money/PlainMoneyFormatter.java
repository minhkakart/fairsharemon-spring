package io.github.minhkakart.fairsharemon.money;

import org.springframework.stereotype.Component;

@Component("plain")
public class PlainMoneyFormatter implements MoneyFormatter {

	@Override
	public String format(Money money) {
		return money.amount().toPlainString();
	}

}
