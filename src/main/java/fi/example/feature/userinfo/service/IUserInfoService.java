package fi.example.feature.userinfo.service;

import fi.example.persistence.userinfo.UserInfo;
import org.springframework.transaction.annotation.Transactional;

public interface IUserInfoService {
    @Transactional
    UserInfo createOrUpdateUserInfo(
        String registrationNumber,
        String firstName,
        String lastName
    );
}
