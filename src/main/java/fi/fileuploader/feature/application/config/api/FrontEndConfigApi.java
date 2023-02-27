package fi.fileuploader.feature.application.config.api;

import fi.fileuploader.common.FileUploadRuntimeException;
import fi.fileuploader.feature.application.config.domain.FrontEndConfigurationDTO;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public interface FrontEndConfigApi {
    @GetMapping("/frontend-config")
    List<FrontEndConfigurationDTO> frontendConfigurations() throws FileUploadRuntimeException;
}
