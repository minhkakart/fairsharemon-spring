package io.github.minhkakart.fairsharemon.money;

import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * Spring injects every MoneyFormatter bean as a map keyed by bean name,
 * so adding a style later means adding one class and touching nothing here.
 */
@Service
public class MoneyFormatters {

	private final Map<String, MoneyFormatter> byStyle;

	public MoneyFormatters(Map<String, MoneyFormatter> byStyle) {
		this.byStyle = byStyle;
	}

	public String format(String style, Money money) {
		MoneyFormatter formatter = byStyle.get(style);
		if (formatter == null) {
			throw new IllegalArgumentException("Unknown money style: " + style);
		}
		return formatter.format(money);
	}

}
