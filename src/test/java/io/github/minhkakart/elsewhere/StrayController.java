package io.github.minhkakart.elsewhere;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A perfectly valid controller that lives outside the package of the
 * {@code @SpringBootApplication} class. Lesson 02 shows that Spring never finds it.
 */
@RestController
public class StrayController {

	@GetMapping("/api/v1/stray")
	public String stray() {
		return "you will never see this";
	}

}
