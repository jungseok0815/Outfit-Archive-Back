package com.fasthub.backend.cmm.img;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@Slf4j
//@RestController
//@RequestMapping("/api/img")
//public class ImgController {
//    @Value("${file.path-product}")
//    private String productFilePath;
//
//    @GetMapping("/get")
//    public ResponseEntity<?> returnImage(@RequestParam String imageName) {
//        log.info("imageName : " + imageName);
//        String path = productFilePath; //이미지가 저장된 위치
//        Resource resource = new FileSystemResource(path + imageName);
//        return new ResponseEntity<>(resource, HttpStatus.OK);
//    }
//}
