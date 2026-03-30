package com.fasthub.backend.admin.banner.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@RequiredArgsConstructor
public class InsertBannerDto {
    private String title;
    private String highlight;
    private String description;
    private String buttonText;
    private String buttonUrl;
    private int sortOrder;
    private boolean active;
    private MultipartFile image;
}
