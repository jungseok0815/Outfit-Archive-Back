package com.fasthub.backend.cmm.img;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Component
@Slf4j
public class ImgHandler {
     public String getFileName(String originfileName){
        String fileName = UUID.randomUUID() + "_" + originfileName;
        return fileName;
    }

    public String getFilePath(MultipartFile img, String filePath, String fileName) throws IOException {
        Path path = Paths.get(filePath,fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, img.getBytes());
         return path.toString();
    }

//    public String getBase64Image(String imagePath) {
//        try {
//            byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
//            return Base64.getEncoder().encodeToString(imageBytes);
//        } catch (IOException e) {
//            return "";
//        }
//    }
}
