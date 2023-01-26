package fi.fileuploader.feature.file.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileService {
    @Transactional
    void importUsersFromCsvFile(MultipartFile file) throws IOException;
}
