package fi.example.feature.userinfo.service.impl;

import fi.example.feature.userinfo.service.IUserInfoService;
import fi.example.persistence.userinfo.QUserInfo;
import fi.example.persistence.userinfo.UserInfo;
import fi.example.persistence.userinfo.UserInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserInfoServiceImpl implements IUserInfoService {

    private final UserInfoRepository userInfoRepository;

    public UserInfoServiceImpl(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    @Transactional
    public UserInfo createOrUpdateUserInfo(
        final String registrationNumber,
        final String firstName,
        final String lastName
    ) {
        final UserInfo userInfo = this.findUserInfoByRegistrationNumber(
                registrationNumber
            )
            .orElseGet(() -> {
                final var newUser = new UserInfo();
                newUser.setRegistrationNumber(registrationNumber);
                return this.userInfoRepository.save(newUser);
            });

        userInfo.setFirstName(firstName);
        userInfo.setLastName(lastName);

        return userInfo;
    }


    private Optional<UserInfo> findUserInfoByRegistrationNumber(
        final String registrationNumber
    ) {
        final QUserInfo userInfo = QUserInfo.userInfo;
        return this.userInfoRepository.findOne(
            userInfo.registrationNumber.eq(registrationNumber)
        );
    }
}
