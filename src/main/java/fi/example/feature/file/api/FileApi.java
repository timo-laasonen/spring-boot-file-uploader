package fi.example.feature.file.api;

import fi.example.feature.file.validator.ValidSizeFile;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/files")
@Validated
public interface FileApi {

    @PostMapping("/upload-users")
    ResponseEntity<?> uploadUsersCsv(
        @Valid @RequestParam("file") @ValidSizeFile MultipartFile file
    ) throws Exception;
}
