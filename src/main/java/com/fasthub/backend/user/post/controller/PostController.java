package com.fasthub.backend.user.post.controller;

import com.fasthub.backend.user.post.dto.InsertPostDto;
import com.fasthub.backend.user.post.dto.ResponsePostDto;
import com.fasthub.backend.user.post.dto.UpdatePostDto;
import com.fasthub.backend.user.post.service.PostService;
import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/usr/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 로그인 사용자의 게시글 목록 조회
    @GetMapping("/my")
    public ResponseEntity<Page<ResponsePostDto>> myList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.myList(userDetails.getId(), pageable));
    }

    // 게시글 목록 조회
    @GetMapping("/list")
    public ResponseEntity<Page<ResponsePostDto>> list(
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(postService.list(keyword, userId, pageable));
    }

    // 브랜드명 또는 제목으로 게시글 검색 (비로그인 접근 가능)
    @GetMapping("/search")
    public ResponseEntity<Page<ResponsePostDto>> search(
            @RequestParam String keyword,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(postService.searchByKeyword(keyword, userId, pageable));
    }

    // 특정 상품이 태그된 게시글 목록 (비로그인 접근 가능)
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ResponsePostDto>> listByProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(postService.listByProduct(productId, userId, pageable));
    }

    // 특정 유저의 게시글 목록 조회 (비로그인 접근 가능)
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ResponsePostDto>> listByUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long loginUserId = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(postService.listByUser(userId, loginUserId, pageable));
    }

    // 게시글 등록
    @PostMapping("/insert")
    public ResponseEntity<Void> insert(
            @ModelAttribute @Valid InsertPostDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.insert(dto, userDetails.getId());
        return ResponseEntity.status(201).build();
    }

    // 게시글 수정
    @PutMapping("/update")
    public ResponseEntity<Void> update(
            @ModelAttribute @Valid UpdatePostDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.update(dto, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    // 게시글 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(
            @RequestParam Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.delete(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
