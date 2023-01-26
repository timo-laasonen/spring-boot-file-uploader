package fi.fileuploader.test;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("omasuuntima.test")
public class TestingProperties {

}
