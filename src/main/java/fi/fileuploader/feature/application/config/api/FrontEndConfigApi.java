package fi.fileuploader.feature.application.config.api;

import fi.fileuploader.feature.application.config.domain.FrontEndConfigurationDTO;
import org.springframework.web.bind.annotation.GetMapping;

public interface FrontEndConfigApi {
    @GetMapping("/frontend-config")
    FrontEndConfigurationDTO frontendConfigurations();
}
