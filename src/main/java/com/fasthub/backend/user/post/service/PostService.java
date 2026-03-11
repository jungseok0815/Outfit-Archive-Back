package com.fasthub.backend.user.post.service;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import com.fasthub.backend.user.post.dto.InsertPostDto;
import com.fasthub.backend.user.post.dto.ResponsePostDto;
import com.fasthub.backend.user.post.dto.UpdatePostDto;
import com.fasthub.backend.user.post.entity.Post;
import com.fasthub.backend.user.post.entity.PostImg;
import com.fasthub.backend.user.post.entity.PostProduct;
import com.fasthub.backend.user.post.repository.PostCommentRepository;
import com.fasthub.backend.user.post.repository.PostImgRepository;
import com.fasthub.backend.user.post.repository.PostLikeRepository;
import com.fasthub.backend.user.post.repository.PostProductRepository;
import com.fasthub.backend.user.post.repository.PostRepository;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostImgRepository postImgRepository;
    private final PostProductRepository postProductRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final AuthRepository authRepository;
    private final ProductRepository productRepository;
    private final ImgHandler imgHandler;

    // 로그인 사용자의 게시글 목록 조회
    public Page<ResponsePostDto> myList(Long userId, Pageable pageable) {
        return postRepository.findByUser_Id(userId, pageable)
                .map(post -> new ResponsePostDto(
                        post,
                        postLikeRepository.countByPostId(post.getId()),
                        postCommentRepository.countByPostId(post.getId())
                ));
    }

    // 게시글 목록 조회 (제목으로 키워드 검색, 페이징)
    public Page<ResponsePostDto> list(String keyword, Pageable pageable) {
        return postRepository.findAllByKeyword(keyword, pageable)
                .map(post -> new ResponsePostDto(
                        post,
                        postLikeRepository.countByPostId(post.getId()),
                        postCommentRepository.countByPostId(post.getId())
                ));
    }

    // 게시글 등록
    @Transactional
    public void insert(InsertPostDto dto, Long userId) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.save(Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .build());

        if (dto.getImages() != null) {
            dto.getImages().forEach(image ->
                    postImgRepository.save(imgHandler.createImg(image, PostImg::new, post)));
        }

        if (dto.getProductIds() != null) {
            dto.getProductIds().forEach(productId -> {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FAIL_SELECT));
                postProductRepository.save(PostProduct.builder().post(post).product(product).build());
            });
        }
    }

    // 게시글 수정
    @Transactional
    public void update(UpdatePostDto dto, Long userId) {
        Post post = postRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_FAIL_SELECT));

        if (!post.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.BOARD_UNAUTHORIZED);
        }

        post.update(dto.getTitle(), dto.getContent());

        if (dto.getImages() != null) {
            postImgRepository.deleteByPost(post);
            dto.getImages().forEach(image ->
                    postImgRepository.save(imgHandler.createImg(image, PostImg::new, post)));
        }

        if (dto.getProductIds() != null) {
            postProductRepository.deleteByPost(post);
            dto.getProductIds().forEach(productId -> {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FAIL_SELECT));
                postProductRepository.save(PostProduct.builder().post(post).product(product).build());
            });
        }
    }

    // 게시글 삭제
    @Transactional
    public void delete(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_FAIL_SELECT));

        if (!post.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.BOARD_UNAUTHORIZED);
        }

        postRepository.delete(post);
    }
}
