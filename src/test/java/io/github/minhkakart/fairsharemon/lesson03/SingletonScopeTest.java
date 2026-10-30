package io.github.minhkakart.fairsharemon.lesson03;

import java.time.Clock;

import io.github.minhkakart.fairsharemon.time.BusinessCalendar;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SingletonScopeTest {

	/** A service written the way a plain Java class would be: it remembers the current payer in a field. */
	static class ForgetfulService {

		private String payer;

		void startRequest(String payer) {
			this.payer = payer;
		}

		String describe() {
			return "paid by " + payer;
		}

	}

	static class PerUseHelper {
	}

	@TestConfiguration
	static class Beans {

		@Bean
		ForgetfulService forgetfulService() {
			return new ForgetfulService();
		}

		@Bean
		@Scope("prototype")
		PerUseHelper perUseHelper() {
			return new PerUseHelper();
		}

	}

	@Autowired
	ApplicationContext context;

	@Test
	void everyoneAskingForABeanGetsTheSameInstance() {
		assertThat(context.getBean(ForgetfulService.class))
				.isSameAs(context.getBean(ForgetfulService.class));
		assertThat(context.getBean(Clock.class)).isSameAs(context.getBean(Clock.class));
		assertThat(context.getBean(BusinessCalendar.class))
				.isSameAs(context.getBean(BusinessCalendar.class));
	}

	@Test
	void aPrototypeBeanIsANewInstanceEachTime() {
		assertThat(context.getBean(PerUseHelper.class))
				.isNotSameAs(context.getBean(PerUseHelper.class));
	}

	@Test
	void aFieldInASingletonIsSharedByEveryRequest() {
		ForgetfulService service = context.getBean(ForgetfulService.class);

		service.startRequest("An");      // request A begins
		service.startRequest("Binh");    // request B begins before A is done
		String answerForA = service.describe();

		assertThat(answerForA).isEqualTo("paid by Binh"); // A reads B's payer
	}

}
