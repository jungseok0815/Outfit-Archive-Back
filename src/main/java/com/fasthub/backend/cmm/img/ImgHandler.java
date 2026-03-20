package com.fasthub.backend.cmm.img;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Supplier;

// 이미지 저장/삭제 전담 컴포넌트
// 기존: 로컬 디스크(C:\jungseok\img\)에 Files.write()로 저장
// 변경: AWS S3에 업로드, imgPath에 S3 URL 저장
@Component
@Slf4j
public class ImgHandler {

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

    // UUID + 원본 파일명으로 고유한 파일명 생성 (기존과 동일)
    public String getFileName(String originFileName) {
        return UUID.randomUUID() + "_" + originFileName;
    }

    // S3에 파일 업로드 후 접근 가능한 URL 반환
    // 기존 getFilePath()가 로컬 경로 문자열을 반환하던 역할을 대체
    // imgPath 컬럼에 "https://버킷명.s3.리전.amazonaws.com/파일명" 형태로 저장됨
    public String upload(MultipartFile file, String fileName) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
            String url = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
            log.info("[S3] 업로드 완료: {}", url);
            return url;
        } catch (S3Exception e) {
            log.error("[S3] 업로드 실패 - 파일명: {}, 버킷: {}, 상태코드: {}, 원인: {}",
                    fileName, bucket, e.statusCode(), e.awsErrorDetails().errorMessage());
            throw new RuntimeException("S3 업로드 실패: " + fileName, e);
        } catch (IOException e) {
            log.error("[S3] 파일 읽기 실패 - 파일명: {}, 원인: {}", fileName, e.getMessage());
            throw new RuntimeException("S3 업로드 실패: " + fileName, e);
        }
    }

    // S3에서 파일 삭제
    // imgNm(UUID_원본파일명)이 S3의 key로 사용됨
    // 기존 코드는 DB 엔티티만 삭제하고 물리 파일 삭제가 없었음 → 이제 S3도 함께 정리
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

    // 이미지 파일을 S3에 업로드하고 매핑 엔티티(Product, Brand 등)와 연결된 이미지 엔티티 생성
    // imgPath 컬럼에 S3 URL이 저장됨
    public <T extends BaseImg<U>, U> T createImg(MultipartFile file, Supplier<T> entitySupplier, U mappingEntity) {
        String fileName = getFileName(file.getOriginalFilename());
        String s3Url = upload(file, fileName);
        T imgEntity = entitySupplier.get();
        imgEntity.setImgOriginNm(file.getOriginalFilename());
        imgEntity.setImgPath(s3Url);
        imgEntity.setMappingEntity(mappingEntity);
        imgEntity.setImgNm(fileName);
        return imgEntity;
    }

    // ZIP에서 추출한 바이트 배열로 S3 업로드 후 이미지 엔티티 생성
    public <T extends BaseImg<U>, U> T createImgFromBytes(byte[] bytes, String originalFilename, String contentType, Supplier<T> entitySupplier, U mappingEntity) {
        String fileName = getFileName(originalFilename);
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType(contentType != null ? contentType : "image/jpeg")
                            .build(),
                    RequestBody.fromBytes(bytes)
            );
        } catch (S3Exception e) {
            throw new RuntimeException("S3 업로드 실패: " + fileName, e);
        }
        String s3Url = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
        T imgEntity = entitySupplier.get();
        imgEntity.setImgOriginNm(originalFilename);
        imgEntity.setImgPath(s3Url);
        imgEntity.setMappingEntity(mappingEntity);
        imgEntity.setImgNm(fileName);
        return imgEntity;
    }

    // 매핑 엔티티 없이 이미지 엔티티만 생성 (사용처: 추후 확장)
    public <T extends BaseImg<U>, U> T createImg(MultipartFile file, Supplier<T> entitySupplier) {
        String fileName = getFileName(file.getOriginalFilename());
        String s3Url = upload(file, fileName);
        T imgEntity = entitySupplier.get();
        imgEntity.setImgOriginNm(file.getOriginalFilename());
        imgEntity.setImgPath(s3Url);
        imgEntity.setImgNm(fileName);
        return imgEntity;
    }
}
