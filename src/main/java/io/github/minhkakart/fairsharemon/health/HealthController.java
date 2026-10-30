package io.github.minhkakart.fairsharemon.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

	public record HealthResponse(String status) {
	}

	@GetMapping
	public HealthResponse health() {
		return new HealthResponse("UP");
	}

}
