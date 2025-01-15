package com.fasthub.backend.cmm.img;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@Slf4j
public class ImgHandler {
     public String getFileName(String originfileName){
        String fileName = UUID.randomUUID() + "_" + originfileName;
        log.info("fileName : " + fileName);
        return fileName;
    }

    public String getFilePath(String filePath, String fileName) throws IOException {
        Path path = Paths.get(filePath,filePath);
        log.info("path : " + path);
        Files.createDirectories(path.getParent());
         return path.toString();
    }
}
