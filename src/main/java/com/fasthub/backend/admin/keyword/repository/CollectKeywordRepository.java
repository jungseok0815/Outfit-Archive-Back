package com.fasthub.backend.admin.keyword.repository;

import com.fasthub.backend.admin.keyword.entity.CollectKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CollectKeywordRepository extends JpaRepository<CollectKeyword, Long> {

    @Query("SELECT k FROM CollectKeyword k JOIN FETCH k.category WHERE k.active = true")
    List<CollectKeyword> findAllByActiveTrue();

    @Query("SELECT k FROM CollectKeyword k JOIN FETCH k.category WHERE k.id IN :ids")
    List<CollectKeyword> findAllByIdWithCategory(@Param("ids") List<Long> ids);
}
