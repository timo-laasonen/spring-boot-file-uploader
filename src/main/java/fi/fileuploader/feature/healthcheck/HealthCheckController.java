package fi.fileuploader.feature.healthcheck;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 */
@RestController
public class HealthCheckController {

    @GetMapping("/health-check")
    public String index() {
        return "{\"status\": \"OK\"}";
    }
}
