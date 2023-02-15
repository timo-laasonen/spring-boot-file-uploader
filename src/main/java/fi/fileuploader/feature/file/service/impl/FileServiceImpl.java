package fi.fileuploader.feature.file.service.impl;

import com.opencsv.bean.CsvToBeanBuilder;
import fi.fileuploader.feature.file.domain.UserCsvData;
import fi.fileuploader.feature.file.service.IFileService;
import fi.fileuploader.feature.userinfo.service.IUserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class FileServiceImpl implements IFileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    private final IUserInfoService userService;

    public FileServiceImpl(IUserInfoService userService) {
        this.userService = userService;
    }

    @Transactional
    @Override
    public void importUsersFromCsvFile(final MultipartFile file) throws IOException {
        // parse CSV file to create a list of `User` objects

        try (final Reader reader = new BufferedReader(
            new InputStreamReader(
                file.getInputStream(),
                StandardCharsets.UTF_8
            )
        )) {
            // create csv bean reader
            final var csvToBean = new CsvToBeanBuilder<UserCsvData>(reader)
                .withType(UserCsvData.class)
                // skip header line
                .withSkipLines(1)
                .withSeparator(';')
                .build();

            final List<UserCsvData> csvUsers = csvToBean.parse();

            csvUsers.forEach(csvModel -> {
                LOG.debug(
                    "User CSV Import: starting to add user: {} {} "
                        + "with regNo: {}",
                    csvModel.getGivenName(),
                    csvModel.getFamilyName(),
                    csvModel.getRegistrationNumber()
                );

                final var user = this.userService.createOrUpdateUserInfo(
                    csvModel.getRegistrationNumber(),
                    csvModel.getGivenName(),
                    csvModel.getFamilyName(),
                    csvModel.getEmail()
                );

                LOG.debug(
                    "User CSV Import: added user: {} and regNo: {}",
                    user.getFullName(),
                    user.getRegistrationNumber()
                );
            });

        }
    }
}
