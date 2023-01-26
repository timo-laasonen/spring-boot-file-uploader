package fi.fileuploader.feature.file.controller;

import fi.fileuploader.feature.file.api.FileApi;
import fi.fileuploader.feature.file.service.IFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController implements FileApi {

    private final IFileService fileService;

    public FileController(final IFileService fileService) {
        this.fileService = fileService;
    }

    @PreAuthorize("hasAnyRole('ROLE_admin')")
    @Override
    public ResponseEntity<?> uploadUsersCsv(final MultipartFile file) throws Exception {
        this.fileService.importUsersFromCsvFile(file);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
