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

    // 게시글 목록 조회
    @GetMapping("/list")
    public ResponseEntity<Page<ResponsePostDto>> list(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.list(keyword, pageable));
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
    public ResponseEntity<Void> update(@ModelAttribute @Valid UpdatePostDto dto) {
        postService.update(dto);
        return ResponseEntity.ok().build();
    }

    // 게시글 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
