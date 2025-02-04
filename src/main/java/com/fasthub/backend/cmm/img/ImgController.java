package com.fasthub.backend.cmm.img;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/img")
public class ImgController {
    @Value("${file.path-product}")
    private String productFilePath;

    @GetMapping("/get")
    public ResponseEntity<?> returnImage(@RequestParam("imgNm") String imgNm) {
        String path = productFilePath; //이미지가 저장된 위치
        Resource resource = new FileSystemResource(path + imgNm);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
}
