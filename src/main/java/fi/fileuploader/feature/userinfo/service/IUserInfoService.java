package fi.fileuploader.feature.userinfo.service;

import fi.fileuploader.persistence.userinfo.UserInfo;
import org.springframework.transaction.annotation.Transactional;

public interface IUserInfoService {
    @Transactional
    UserInfo createOrUpdateUserInfo(
        String registrationNumber,
        String firstName,
        String lastName
    );
}
