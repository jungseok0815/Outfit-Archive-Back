package com.fasthub.backend.admin.product.service;

import com.fasthub.backend.admin.brand.entity.Brand;
import com.fasthub.backend.admin.brand.repository.BrandRepository;
import com.fasthub.backend.admin.product.dto.InsertProductDto;
import com.fasthub.backend.admin.product.dto.ProductSizeDto;
import com.fasthub.backend.admin.product.dto.ResponseProductDto;
import com.fasthub.backend.admin.product.dto.UpdateProductDto;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductImg;
import com.fasthub.backend.admin.product.entity.ProductSize;
import com.fasthub.backend.admin.product.mapper.ProductMapper;
import com.fasthub.backend.admin.product.repository.ProductImgRepository;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.admin.product.repository.ProductSizeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImgRepository productImgRepository;
    private final ProductSizeRepository productSizeRepository;
    private final BrandRepository brandRepository;
    private final ImgHandler imgHandler;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;
    // private final EmbeddingService embeddingService;  // AI 임베딩 기능 비활성화

    @Transactional
    public void insert(InsertProductDto productDto) {
        Brand brand = brandRepository.findById(productDto.getBrandId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        Product product = productRepository.save(Product.builder()
                .productNm(productDto.getProductNm())
                .productEnNm(productDto.getProductEnNm())
                .productCode(productDto.getProductCode())
                .productPrice(productDto.getProductPrice())
                .productQuantity(productDto.getProductQuantity())
                .category(productDto.getCategory())
                .brand(brand)
                .build());
        if (productDto.getImage() != null) {
            productDto.getImage().forEach(item ->
                    productImgRepository.save(imgHandler.createImg(item, ProductImg::new, product)));
        }

        saveSizes(product, productDto.getSizesJson());
    }

    private void saveSizes(Product product, String sizesJson) {
        if (sizesJson == null || sizesJson.isBlank()) return;
        try {
            List<ProductSizeDto> sizes = objectMapper.readValue(sizesJson, new TypeReference<>() {});
            sizes.stream()
                .filter(s -> s.getSizeNm() != null && !s.getSizeNm().isBlank())
                .forEach(s -> productSizeRepository.save(
                    ProductSize.builder()
                        .product(product)
                        .sizeNm(s.getSizeNm().trim())
                        .quantity(s.getQuantity())
                        .build()
                ));
        } catch (Exception e) {
            log.warn("[Product] sizesJson 파싱 실패: {}", sizesJson);
        }
    }

    public Page<ResponseProductDto> list(String keyword, Pageable pageable) {
        return productRepository.findAllByKeyword(keyword, pageable)
                .map(productMapper::productToProductDto);
    }

    public Page<ResponseProductDto> listForUser(String keyword, ProductCategory category, Pageable pageable) {
        return productRepository.findAllByKeywordAndCategory(keyword, category, pageable)
                .map(productMapper::productToProductDto);
    }

    @Transactional
    public void update(UpdateProductDto productDto) {
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FAIL_UPDATE));
        Brand brand = brandRepository.findById(productDto.getBrandId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        product.update(productDto.getProductNm(), productDto.getProductEnNm(), productDto.getProductCode(),
                productDto.getProductPrice(), productDto.getProductQuantity(), productDto.getCategory(), brand);
        // 선택된 이미지만 삭제
        if (productDto.getDeleteImageIds() != null && !productDto.getDeleteImageIds().isEmpty()) {
            productDto.getDeleteImageIds().forEach(imgId ->
                    productImgRepository.findById(imgId).ifPresent(img -> {
                        imgHandler.deleteFile(img.getImgNm());
                        productImgRepository.delete(img);
                    })
            );
        }
        // 새 이미지 추가
        if (productDto.getImage() != null && !productDto.getImage().isEmpty()) {
            productDto.getImage().forEach(item ->
                    productImgRepository.save(imgHandler.createImg(item, ProductImg::new, product)));
        }

        // 사이즈 전체 교체
        if (productDto.getSizesJson() != null) {
            productSizeRepository.deleteByProduct(product);
            saveSizes(product, productDto.getSizesJson());
        }
    }

    @Transactional
    public int bulkInsert(MultipartFile zipFile, Long overrideBrandId) {
        Map<String, byte[]> imageMap = new HashMap<>();
        byte[] excelBytes = null;

        // ZIP 파일 압축 해제 → 엑셀과 이미지 파일 분리
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                String name = entry.getName().replaceAll(".*/", ""); // 폴더 경로 제거
                byte[] bytes = zis.readAllBytes();
                if (name.endsWith(".xlsx")) {
                    excelBytes = bytes;
                } else {
                    imageMap.put(name, bytes);
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException("ZIP 파일 읽기 실패", e);
        }

        if (excelBytes == null) throw new RuntimeException("ZIP 내에 .xlsx 파일이 없습니다.");

        // 엑셀 파싱 후 상품 저장
        int count = 0;
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 1행부터 (0행은 헤더)
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String productNm   = getCellValue(row, 0);
                String productCode = getCellValue(row, 1);
                int productPrice   = Integer.parseInt(getCellValue(row, 2));
                int productQty     = Integer.parseInt(getCellValue(row, 3));
                String categoryStr = getCellValue(row, 4);
                String imageFile   = getCellValue(row, 5);

                if (overrideBrandId == null) {
                    log.warn("[BulkInsert] brandId가 없습니다. 행 {} 건너뜀", i);
                    continue;
                }
                long brandId = overrideBrandId;

                Brand brand = brandRepository.findById(brandId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));

                ProductCategory category;
                try {
                    category = ProductCategory.valueOf(categoryStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("[BulkInsert] 알 수 없는 카테고리: {}, 행 {} 건너뜀", categoryStr, i);
                    continue;
                }

                Product product = productRepository.save(Product.builder()
                        .productNm(productNm)
                        .productCode(productCode)
                        .productPrice(productPrice)
                        .productQuantity(productQty)
                        .category(category)
                        .brand(brand)
                        .build());

                // 이미지 파일명이 있고 ZIP 내에 해당 파일이 있으면 S3 업로드
                if (imageFile != null && !imageFile.isBlank() && imageMap.containsKey(imageFile)) {
                    String contentType = imageFile.endsWith(".png") ? "image/png" : "image/jpeg";
                    productImgRepository.save(
                            imgHandler.createImgFromBytes(imageMap.get(imageFile), imageFile, contentType, ProductImg::new, product)
                    );
                }
                count++;
            }
        } catch (IOException e) {
            throw new RuntimeException("엑셀 파싱 실패", e);
        }
        log.info("[BulkInsert] {}개 상품 등록 완료", count);
        return count;
    }

    private String getCellValue(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }

    @Transactional
    public void delete(String id) {
        Product product = productRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FAIL_DELETE));
        // 상품 삭제 전 S3 이미지 먼저 제거 (CASCADE로 DB 엔티티는 자동 삭제)
        productImgRepository.findByProduct(product)
                .forEach(img -> imgHandler.deleteFile(img.getImgNm()));
        productRepository.delete(product);
    }
}
