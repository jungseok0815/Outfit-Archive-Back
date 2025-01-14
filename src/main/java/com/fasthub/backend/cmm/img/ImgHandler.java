package com.fasthub.backend.cmm.img;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ImgHandler {
    @Value("${file.path}") private String uploadFolder;

     public String getFileName(String originfileName){
        UUID uuid = UUID.randomUUID();
        String fileName =  uuid + "_"+ originfileName;
        System.out.println("fileName : " + fileName);
        return uploadFolder + uuid + "_"+ originfileName;
    }

    public String getFilePath(String fileName){
         return uploadFolder + fileName;
    }
}
