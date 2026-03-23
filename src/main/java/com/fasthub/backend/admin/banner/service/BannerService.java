package com.fasthub.backend.admin.banner.service;

import com.fasthub.backend.admin.banner.dto.InsertBannerDto;
import com.fasthub.backend.admin.banner.dto.ResponseBannerDto;
import com.fasthub.backend.admin.banner.dto.UpdateBannerDto;
import com.fasthub.backend.admin.banner.entity.Banner;
import com.fasthub.backend.admin.banner.entity.BannerImg;
import com.fasthub.backend.admin.banner.mapper.BannerMapper;
import com.fasthub.backend.admin.banner.repository.BannerImgRepository;
import com.fasthub.backend.admin.banner.repository.BannerRepository;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;
    private final BannerImgRepository bannerImgRepository;
    private final BannerMapper bannerMapper;
    private final ImgHandler imgHandler;

    public List<ResponseBannerDto> list() {
        return bannerRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(bannerMapper::bannerToResponseDto)
                .collect(Collectors.toList());
    }

    public List<ResponseBannerDto> listActive() {
        return bannerRepository.findAllByActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(bannerMapper::bannerToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void insert(InsertBannerDto dto) {
        Banner banner = bannerRepository.save(Banner.builder()
                .title(dto.getTitle())
                .highlight(dto.getHighlight())
                .description(dto.getDescription())
                .buttonText(dto.getButtonText())
                .sortOrder(dto.getSortOrder())
                .active(dto.isActive())
                .build());

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            bannerImgRepository.save(imgHandler.createImg(dto.getImage(), BannerImg::new, banner));
        }
    }

    @Transactional
    public void update(UpdateBannerDto dto) {
        Banner banner = bannerRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BANNER_NOT_FOUND));
        banner.update(dto.getTitle(), dto.getHighlight(), dto.getDescription(),
                dto.getButtonText(), dto.getSortOrder(), dto.isActive());

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            bannerImgRepository.findByBanner(banner)
                    .forEach(img -> imgHandler.deleteFile(img.getImgNm()));
            bannerImgRepository.deleteByBanner(banner);
            bannerImgRepository.save(imgHandler.createImg(dto.getImage(), BannerImg::new, banner));
        }
    }

    @Transactional
    public void delete(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BANNER_NOT_FOUND));
        bannerImgRepository.findByBanner(banner)
                .forEach(img -> imgHandler.deleteFile(img.getImgNm()));
        bannerRepository.delete(banner);
    }
}
