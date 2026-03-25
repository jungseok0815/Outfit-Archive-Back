package com.fasthub.backend.user.post.repository;

import com.fasthub.backend.user.post.entity.Post;
import com.fasthub.backend.user.post.entity.PostProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostProductRepository extends JpaRepository<PostProduct, Long> {

    void deleteByPost(Post post);

    void deleteByProduct(com.fasthub.backend.admin.product.entity.Product product);
}
