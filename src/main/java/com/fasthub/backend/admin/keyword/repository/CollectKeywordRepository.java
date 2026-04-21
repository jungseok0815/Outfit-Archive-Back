package com.fasthub.backend.admin.keyword.repository;

import com.fasthub.backend.admin.keyword.entity.CollectKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectKeywordRepository extends JpaRepository<CollectKeyword, Long> {

    List<CollectKeyword> findAllByActiveTrue();
}
