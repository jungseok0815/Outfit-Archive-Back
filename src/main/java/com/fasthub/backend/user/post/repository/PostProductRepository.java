package com.fasthub.backend.user.post.repository;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.user.post.entity.Post;
import com.fasthub.backend.user.post.entity.PostProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostProductRepository extends JpaRepository<PostProduct, Long> {

    void deleteByPost(Post post);

    @Modifying
    @Query("DELETE FROM PostProduct pp WHERE pp.product = :product")
    void deleteByProduct(@Param("product") Product product);
}
