package com.fasthub.backend.user.post.repository;

import com.fasthub.backend.user.post.entity.Post;
import com.fasthub.backend.user.post.entity.PostImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImgRepository extends JpaRepository<PostImg, Long> {

    void deleteByPost(Post post);
}
