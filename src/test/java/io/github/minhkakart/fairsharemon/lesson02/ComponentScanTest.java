package io.github.minhkakart.fairsharemon.lesson02;

import io.github.minhkakart.elsewhere.StrayController;
import io.github.minhkakart.fairsharemon.health.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class ComponentScanTest {

	@Autowired
	MockMvcTester mvc;

	@Autowired
	ApplicationContext context;

	@Test
	void controllerUnderTheScanRootIsFound() {
		assertThat(mvc.get().uri("/api/v1/health"))
				.hasStatusOk()
				.bodyJson().extractingPath("$.status").isEqualTo("UP");
	}

	@Test
	void controllerOutsideTheScanRootIsNotFoundAndNothingFails() {
		// The test context started fine, yet the controller is not a bean...
		assertThat(context.getBeanNamesForType(StrayController.class)).isEmpty();
		// ...so its route is a plain 404.
		assertThat(mvc.get().uri("/api/v1/stray")).hasStatus(404);
	}

	@Test
	void theScanRootIsThePackageOfTheApplicationClass() {
		assertThat(context.getBeanNamesForType(HealthController.class)).hasSize(1);
	}

}
