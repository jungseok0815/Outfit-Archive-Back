package com.fasthub.backend.admin.banner.mapper;

import com.fasthub.backend.admin.banner.dto.ResponseBannerDto;
import com.fasthub.backend.admin.banner.entity.Banner;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BannerMapper {

    ResponseBannerDto bannerToResponseDto(Banner banner);
}
