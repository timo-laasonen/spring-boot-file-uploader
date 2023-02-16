package fi.fileuploader.feature.userinfo.mapper;

import fi.fileuploader.common.PagedResponseDTO;
import fi.fileuploader.feature.userinfo.domain.UserInfoDTO;
import fi.fileuploader.persistence.userinfo.UserInfo;
import org.springframework.data.domain.Page;

public final class UserInfoMapper {
    private UserInfoMapper() {
    }

    public static UserInfoDTO toDto(
        final UserInfo userInfo
    ) {

        return UserInfoDTO.builder()
            .id(userInfo.getId())
            .username(userInfo.getUsername())
            .firstName(userInfo.getFirstName())
            .lastName(userInfo.getLastName())
            .build();
    }

    public static PagedResponseDTO<UserInfoDTO> toPagedResponseDTO(
        final Page<UserInfo> userInfoPage
    ) {
        final var accessLogDtoPage = userInfoPage.map(UserInfoMapper::toDto);

        return PagedResponseDTO.<UserInfoDTO>builder()
            .size(accessLogDtoPage.getSize())
            .page(accessLogDtoPage.getNumber())
            .totalCount(accessLogDtoPage.getTotalElements())
            .content(accessLogDtoPage.getContent())
            .build();
    }
}
