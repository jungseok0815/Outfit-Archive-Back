package com.fasthub.backend.admin.brand.service;

import com.fasthub.backend.admin.brand.dto.InsertBrandDto;
import com.fasthub.backend.admin.brand.dto.ResponseBrandDto;
import com.fasthub.backend.admin.brand.dto.UpdateBrandDto;
import com.fasthub.backend.admin.brand.entity.Brand;
import com.fasthub.backend.admin.brand.entity.BrandImg;
import com.fasthub.backend.admin.brand.mapper.BrandMapper;
import com.fasthub.backend.admin.brand.repository.BrandImgRepository;
import com.fasthub.backend.admin.brand.repository.BrandRepository;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.repository.ProductImgRepository;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.user.post.repository.PostProductRepository;
import com.fasthub.backend.user.review.repository.ReviewRepository;
import com.fasthub.backend.user.wishlist.repository.WishlistRepository;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import static com.fasthub.backend.cmm.img.ImgHandler.BRAND_MAX_WIDTH;
import static com.fasthub.backend.cmm.img.ImgHandler.BRAND_MAX_HEIGHT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandImgRepository brandImgRepository;
    private final ProductRepository productRepository;
    private final ProductImgRepository productImgRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final WishlistRepository wishlistRepository;
    private final PostProductRepository postProductRepository;
    private final ImgHandler imgHandler;
    private final BrandMapper brandMapper;

    public Page<ResponseBrandDto> list(String keyword, Pageable pageable) {
        return brandRepository.findAllByKeyword(keyword, pageable)
                .map(brandMapper::brandToResponseDto);
    }

    public ResponseBrandDto getById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        return brandMapper.brandToResponseDto(brand);
    }

    @Transactional
    public void insert(InsertBrandDto insertBrandDto) {
        Brand brand = brandRepository.save(brandMapper.insertDtoToBrand(insertBrandDto));
        if (insertBrandDto.getBrandImg() != null) {
            brandImgRepository.save(imgHandler.createImg(insertBrandDto.getBrandImg(), BrandImg::new, brand, BRAND_MAX_WIDTH, BRAND_MAX_HEIGHT));
        }
    }

    @Transactional
    public void update(UpdateBrandDto updateBrandDto) {
        Brand brand = brandRepository.findById(updateBrandDto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        brand.update(updateBrandDto.getBrandNm(), updateBrandDto.getBrandNum(),
                updateBrandDto.getBrandLocation(), updateBrandDto.getBrandDc());
        if (updateBrandDto.getBrandImg() != null) {
            // 기존 이미지를 S3에서 먼저 삭제 후 DB 엔티티 제거
            brand.getImages().forEach(img -> imgHandler.deleteFile(img.getImgNm()));
            brandImgRepository.deleteByBrand(brand);
            brandImgRepository.save(imgHandler.createImg(updateBrandDto.getBrandImg(), BrandImg::new, brand, BRAND_MAX_WIDTH, BRAND_MAX_HEIGHT));
        }
    }

    @Transactional
    public void delete(String brandNo) {
        Brand brand = brandRepository.findById(Long.valueOf(brandNo))
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));

        // 1. 브랜드 소속 상품 삭제 (연관 데이터 순서대로 제거)
        productRepository.findAllByBrandIdWithImages(brand.getId()).forEach(product -> {
            // 1-1. 리뷰 삭제
            reviewRepository.deleteByProduct(product);
            // 1-2. 주문 삭제
            orderRepository.findByProduct(product).forEach(orderRepository::delete);
            // 1-3. 위시리스트 삭제
            wishlistRepository.deleteByProductId(product.getId());
            // 1-4. 게시물 상품 태그 삭제
            postProductRepository.deleteByProduct(product);
            // 1-5. 상품 S3 이미지 삭제
            productImgRepository.findByProduct(product).forEach(img -> imgHandler.deleteFile(img.getImgNm()));
            // 1-6. 상품 삭제
            productRepository.delete(product);
        });

        // 2. 브랜드 S3 이미지 삭제 후 브랜드 삭제 (CASCADE ALL로 DB 엔티티는 자동 삭제)
        brand.getImages().forEach(img -> imgHandler.deleteFile(img.getImgNm()));
        brandRepository.delete(brand);
    }
}
