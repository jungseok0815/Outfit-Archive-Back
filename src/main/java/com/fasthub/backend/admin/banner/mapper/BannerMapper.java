package com.fasthub.backend.admin.banner.mapper;

import com.fasthub.backend.admin.banner.dto.ResponseBannerDto;
import com.fasthub.backend.admin.banner.entity.Banner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BannerMapper {

    @Mapping(target = "imgPath", expression = "java(banner.getImages().isEmpty() ? null : banner.getImages().get(0).getImgPath())")
    ResponseBannerDto bannerToResponseDto(Banner banner);
}
