package com.fasthub.backend.cmm.img;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Supplier;

@Component
@Slf4j
public class ImgHandler {

    // 이미지 타입별 최대 크기 상수
    public static final int PRODUCT_MAX_WIDTH  = 800;
    public static final int PRODUCT_MAX_HEIGHT = 800;
    public static final int BRAND_MAX_WIDTH    = 600;
    public static final int BRAND_MAX_HEIGHT   = 600;
    public static final int BANNER_MAX_WIDTH   = 1920;
    public static final int BANNER_MAX_HEIGHT  = 600;

    private final S3Client s3Client;
    private final String bucket;
    private final String region;

    public ImgHandler(
            S3Client s3Client,
            @Value("${cloud.aws.s3.bucket}") String bucket,
            @Value("${cloud.aws.region.static}") String region) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.region = region;
    }

    public String getFileName(String originFileName) {
        return UUID.randomUUID() + "_" + originFileName;
    }

    // 이미지 리사이징 → 비율 유지하면서 maxWidth x maxHeight 이내로 축소
    // 원본이 이미 작으면 확대하지 않음 (outputQuality 0.85로 용량 최적화)
    private byte[] resizeImage(byte[] originalBytes, int maxWidth, int maxHeight) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Thumbnails.of(new java.io.ByteArrayInputStream(originalBytes))
                    .size(maxWidth, maxHeight)
                    .keepAspectRatio(true)
                    .outputQuality(0.85)
                    .toOutputStream(out);
            byte[] resized = out.toByteArray();
            log.info("[Resize] {}B → {}B (max {}x{})", originalBytes.length, resized.length, maxWidth, maxHeight);
            return resized;
        } catch (IOException e) {
            log.warn("[Resize] 리사이징 실패, 원본 사용: {}", e.getMessage());
            return originalBytes;
        }
    }

    // S3 업로드 (원본)
    public String upload(MultipartFile file, String fileName) {
        try {
            byte[] bytes = file.getBytes();
            return uploadBytes(bytes, fileName, file.getContentType());
        } catch (IOException e) {
            log.error("[S3] 파일 읽기 실패 - 파일명: {}, 원인: {}", fileName, e.getMessage());
            throw new RuntimeException("S3 업로드 실패: " + fileName, e);
        }
    }

    // S3 업로드 (리사이징 후)
    public String upload(MultipartFile file, String fileName, int maxWidth, int maxHeight) {
        try {
            byte[] resized = resizeImage(file.getBytes(), maxWidth, maxHeight);
            return uploadBytes(resized, fileName, file.getContentType());
        } catch (IOException e) {
            log.error("[S3] 파일 읽기 실패 - 파일명: {}, 원인: {}", fileName, e.getMessage());
            throw new RuntimeException("S3 업로드 실패: " + fileName, e);
        }
    }

    private String uploadBytes(byte[] bytes, String fileName, String contentType) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromBytes(bytes)
            );
            String url = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
            log.info("[S3] 업로드 완료: {}", url);
            return url;
        } catch (S3Exception e) {
            log.error("[S3] 업로드 실패 - 파일명: {}, 버킷: {}, 상태코드: {}, 원인: {}",
                    fileName, bucket, e.statusCode(), e.awsErrorDetails().errorMessage());
            throw new RuntimeException("S3 업로드 실패: " + fileName, e);
        }
    }

    public void deleteFile(String imgNm) {
        if (imgNm == null || imgNm.isBlank()) return;
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(imgNm)
                    .build());
            log.info("[S3] 삭제 완료: {}", imgNm);
        } catch (S3Exception e) {
            log.error("[S3] 삭제 실패 - 파일명: {}, 버킷: {}, 상태코드: {}, 원인: {}",
                    imgNm, bucket, e.statusCode(), e.awsErrorDetails().errorMessage());
            throw new RuntimeException("S3 삭제 실패: " + imgNm, e);
        }
    }

    // 리사이징 포함 이미지 엔티티 생성 (maxWidth, maxHeight 지정)
    public <T extends BaseImg<U>, U> T createImg(MultipartFile file, Supplier<T> entitySupplier, U mappingEntity, int maxWidth, int maxHeight) {
        String fileName = getFileName(file.getOriginalFilename());
        String s3Url = upload(file, fileName, maxWidth, maxHeight);
        T imgEntity = entitySupplier.get();
        imgEntity.setImgOriginNm(file.getOriginalFilename());
        imgEntity.setImgPath(s3Url);
        imgEntity.setMappingEntity(mappingEntity);
        imgEntity.setImgNm(fileName);
        return imgEntity;
    }

    // 기존 호환 메서드 → 기본값 PRODUCT 사이즈로 리사이징
    public <T extends BaseImg<U>, U> T createImg(MultipartFile file, Supplier<T> entitySupplier, U mappingEntity) {
        return createImg(file, entitySupplier, mappingEntity, PRODUCT_MAX_WIDTH, PRODUCT_MAX_HEIGHT);
    }

    // ZIP 벌크용 - 바이트 배열 리사이징 후 업로드
    public <T extends BaseImg<U>, U> T createImgFromBytes(byte[] bytes, String originalFilename, String contentType, Supplier<T> entitySupplier, U mappingEntity) {
        String fileName = getFileName(originalFilename);
        byte[] resized = resizeImage(bytes, PRODUCT_MAX_WIDTH, PRODUCT_MAX_HEIGHT);
        String s3Url = uploadBytes(resized, fileName, contentType != null ? contentType : "image/jpeg");
        T imgEntity = entitySupplier.get();
        imgEntity.setImgOriginNm(originalFilename);
        imgEntity.setImgPath(s3Url);
        imgEntity.setMappingEntity(mappingEntity);
        imgEntity.setImgNm(fileName);
        return imgEntity;
    }

    // 매핑 엔티티 없이 이미지 엔티티만 생성
    public <T extends BaseImg<U>, U> T createImg(MultipartFile file, Supplier<T> entitySupplier) {
        String fileName = getFileName(file.getOriginalFilename());
        String s3Url = upload(file, fileName, PRODUCT_MAX_WIDTH, PRODUCT_MAX_HEIGHT);
        T imgEntity = entitySupplier.get();
        imgEntity.setImgOriginNm(file.getOriginalFilename());
        imgEntity.setImgPath(s3Url);
        imgEntity.setImgNm(fileName);
        return imgEntity;
    }
}
