package fi.fileuploader.feature.userinfo.api;

import fi.fileuploader.common.PagedResponseDTO;
import fi.fileuploader.feature.userinfo.domain.UserInfoDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/user-info")
public interface UserApi {

    @GetMapping
    UserInfoDTO userInfo();

    @GetMapping("/users")
    PagedResponseDTO<UserInfoDTO> findUsers(
        Pageable pageable
    );
}
