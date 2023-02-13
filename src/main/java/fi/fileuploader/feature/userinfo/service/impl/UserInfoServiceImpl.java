package fi.fileuploader.feature.userinfo.service.impl;

import fi.fileuploader.feature.userinfo.service.IUserInfoService;
import fi.fileuploader.persistence.userinfo.QUserInfo;
import fi.fileuploader.persistence.userinfo.UserInfo;
import fi.fileuploader.persistence.userinfo.UserInfoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Transactional(readOnly = true)
    @Override
    public Page<UserInfo> findUserInfos(final Pageable pageable) {
        return this.userInfoRepository.findAll(pageable);
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
