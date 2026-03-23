package com.fasthub.backend.admin.banner.repository;

import com.fasthub.backend.admin.banner.entity.Banner;
import com.fasthub.backend.admin.banner.entity.BannerImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerImgRepository extends JpaRepository<BannerImg, Long> {
    List<BannerImg> findByBanner(Banner banner);
    void deleteByBanner(Banner banner);
}
