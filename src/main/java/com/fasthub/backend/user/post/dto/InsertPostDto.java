package com.fasthub.backend.user.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class InsertPostDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private List<MultipartFile> images;
}
