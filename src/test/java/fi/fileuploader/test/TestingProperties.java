package fi.fileuploader.test;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("fileuploader.test")
public class TestingProperties {

}
