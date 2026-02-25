package com.fasthub.backend.admin.brand;

import com.fasthub.backend.admin.brand.dto.InsertBrandDto;
import com.fasthub.backend.admin.brand.dto.ResponseBrandDto;
import com.fasthub.backend.admin.brand.dto.UpdateBrandDto;
import com.fasthub.backend.admin.brand.entity.Brand;
import com.fasthub.backend.admin.brand.entity.BrandImg;
import com.fasthub.backend.admin.brand.mapper.BrandMapper;
import com.fasthub.backend.admin.brand.repository.BrandImgRepository;
import com.fasthub.backend.admin.brand.repository.BrandRepository;
import com.fasthub.backend.admin.brand.service.BrandService;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BrandService 테스트")
class BrandServiceTest {

    @InjectMocks
    private BrandService brandService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private BrandImgRepository brandImgRepository;

    @Mock
    private ImgHandler imgHandler;

    @Mock
    private BrandMapper brandMapper;

    private Brand buildBrand() {
        return Brand.builder()
                .brandNm("나이키")
                .brandNum("NK-001")
                .brandLocation("미국")
                .brandDc("스포츠 브랜드")
                .build();
    }

    // ────────────────────────────────────────────────
    // 목록 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("목록 조회")
    class BrandList {

        @Test
        @DisplayName("성공 - 키워드 없이 전체 조회")
        void list_success_noKeyword() {
            Pageable pageable = PageRequest.of(0, 10);
            Brand brand = buildBrand();
            ResponseBrandDto responseDto = new ResponseBrandDto();
            Page<Brand> brandPage = new PageImpl<>(List.of(brand));

            given(brandRepository.findAllByKeyword(null, pageable)).willReturn(brandPage);
            given(brandMapper.brandToResponseDto(brand)).willReturn(responseDto);

            Page<ResponseBrandDto> result = brandService.list(null, pageable);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("성공 - 키워드로 검색")
        void list_success_withKeyword() {
            Pageable pageable = PageRequest.of(0, 10);
            String keyword = "나이키";
            Brand brand = buildBrand();
            ResponseBrandDto responseDto = new ResponseBrandDto();
            Page<Brand> brandPage = new PageImpl<>(List.of(brand));

            given(brandRepository.findAllByKeyword(keyword, pageable)).willReturn(brandPage);
            given(brandMapper.brandToResponseDto(brand)).willReturn(responseDto);

            Page<ResponseBrandDto> result = brandService.list(keyword, pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    // ────────────────────────────────────────────────
    // 등록
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("등록")
    class Insert {

        @Test
        @DisplayName("성공 - 이미지 없이 등록")
        void insert_success_withoutImage() {
            InsertBrandDto dto = new InsertBrandDto();
            dto.setBrandNm("나이키");
            dto.setBrandNum("NK-001");
            dto.setBrandLocation("미국");
            dto.setBrandDc("스포츠 브랜드");

            Brand brand = buildBrand();
            given(brandMapper.insertDtoToBrand(dto)).willReturn(brand);
            given(brandRepository.save(brand)).willReturn(brand);

            brandService.insert(dto);

            then(brandRepository).should().save(brand);
            then(brandImgRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("성공 - 이미지 포함 등록")
        void insert_success_withImage() {
            InsertBrandDto dto = new InsertBrandDto();
            dto.setBrandNm("나이키");
            dto.setBrandImg(mock(MultipartFile.class));

            Brand brand = buildBrand();
            BrandImg brandImg = new BrandImg();

            given(brandMapper.insertDtoToBrand(dto)).willReturn(brand);
            given(brandRepository.save(brand)).willReturn(brand);
            given(imgHandler.createImg(eq(dto.getBrandImg()), any(), eq(brand))).willReturn(brandImg);

            brandService.insert(dto);

            then(brandRepository).should().save(brand);
            then(brandImgRepository).should().save(brandImg);
        }
    }

    // ────────────────────────────────────────────────
    // 수정
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("수정")
    class Update {

        @Test
        @DisplayName("성공 - 이미지 없이 수정")
        void update_success_withoutImage() {
            UpdateBrandDto dto = new UpdateBrandDto();
            dto.setId(1L);
            dto.setBrandNm("아디다스");
            dto.setBrandNum("AD-001");
            dto.setBrandLocation("독일");
            dto.setBrandDc("스포츠 브랜드");

            Brand brand = buildBrand();
            given(brandRepository.findById(1L)).willReturn(Optional.of(brand));

            brandService.update(dto);

            then(brandImgRepository).should(never()).deleteByBrand(any());
            then(brandImgRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("성공 - 이미지 교체 수정")
        void update_success_withImage() {
            UpdateBrandDto dto = new UpdateBrandDto();
            dto.setId(1L);
            dto.setBrandNm("아디다스");
            dto.setBrandImg(mock(MultipartFile.class));

            Brand brand = buildBrand();
            BrandImg newBrandImg = new BrandImg();

            given(brandRepository.findById(1L)).willReturn(Optional.of(brand));
            given(imgHandler.createImg(eq(dto.getBrandImg()), any(), eq(brand))).willReturn(newBrandImg);

            brandService.update(dto);

            then(brandImgRepository).should().deleteByBrand(brand);
            then(brandImgRepository).should().save(newBrandImg);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 브랜드")
        void update_fail_brandNotFound() {
            UpdateBrandDto dto = new UpdateBrandDto();
            dto.setId(999L);

            given(brandRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> brandService.update(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.BRAND_NOT_FOUND.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 삭제
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("삭제")
    class Delete {

        @Test
        @DisplayName("성공")
        void delete_success() {
            Brand brand = buildBrand();

            given(brandRepository.findById(1L)).willReturn(Optional.of(brand));

            brandService.delete("1");

            then(brandRepository).should().delete(brand);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 브랜드")
        void delete_fail_brandNotFound() {
            given(brandRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> brandService.delete("999"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.BRAND_NOT_FOUND.getMessage());

            then(brandRepository).should(never()).delete(any());
        }
    }
}
