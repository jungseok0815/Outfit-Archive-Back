package com.fasthub.backend.admin.category.repository;

import com.fasthub.backend.admin.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByActiveTrue();

    Optional<Category> findByName(String name);
}
