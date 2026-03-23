package com.fasthub.backend.admin.banner.repository;

import com.fasthub.backend.admin.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findAllByOrderBySortOrderAsc();

    List<Banner> findAllByActiveTrueOrderBySortOrderAsc();
}
