package com.fasthub.backend.cmm.img;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
@Slf4j
public class ImgHandler {
    @Value("${file.path-product}")
    private String filePath;

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

    public <T extends BaseImg<U>, U> T createImg(MultipartFile file, Supplier<T> entitySupplier, U mappingEntity){
        try {
            String fileName = getFileName(file.getOriginalFilename());
            String savedFilePath = getFilePath(file, filePath, fileName);
            T imgEntity = entitySupplier.get();
            imgEntity.setImgOriginNm(file.getOriginalFilename());
            imgEntity.setImgPath(savedFilePath);
            imgEntity.setMappingEntity(mappingEntity);
            imgEntity.setImgNm(fileName);
            return imgEntity;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}
