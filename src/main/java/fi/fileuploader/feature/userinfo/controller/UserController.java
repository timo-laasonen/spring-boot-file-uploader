package fi.fileuploader.feature.userinfo.controller;

import fi.fileuploader.common.PagedResponseDTO;
import fi.fileuploader.feature.userinfo.api.UserApi;
import fi.fileuploader.feature.userinfo.domain.UserInfoDTO;
import fi.fileuploader.feature.userinfo.mapper.UserInfoMapper;
import fi.fileuploader.feature.userinfo.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserApi {

    private final IUserInfoService userInfoService;


    @Autowired
    public UserController(IUserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Override
    public UserInfoDTO userInfo() {
        return null;
    }

    @Override
    public PagedResponseDTO<UserInfoDTO> findUsers(Pageable pageable) {
        return UserInfoMapper.toPagedResponseDTO(
            this.userInfoService.findUserInfos(pageable)
        );
    }
}
