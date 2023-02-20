package fi.fileuploader.feature.application.config.controller;

import fi.fileuploader.feature.application.config.api.FrontEndConfigApi;
import fi.fileuploader.feature.application.config.domain.FrontEndConfigurationDTO;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FrontEndConfigController implements FrontEndConfigApi {

    @Override
    public FrontEndConfigurationDTO frontendConfigurations() {
        return FrontEndConfigurationDTO.builder().build();
    }
}
