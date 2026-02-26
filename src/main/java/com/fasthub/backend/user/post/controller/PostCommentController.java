package com.fasthub.backend.user.post.controller;

import com.fasthub.backend.user.post.dto.InsertCommentDto;
import com.fasthub.backend.user.post.dto.ResponseCommentDto;
import com.fasthub.backend.user.post.dto.UpdateCommentDto;
import com.fasthub.backend.user.post.service.PostCommentService;
import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usr/post/comment")
@RequiredArgsConstructor
public class PostCommentController {

    private final PostCommentService postCommentService;

    // 댓글 목록 조회
    @GetMapping("/list")
    public ResponseEntity<Page<ResponseCommentDto>> list(
            @RequestParam Long postId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postCommentService.list(postId, pageable));
    }

    // 댓글 등록
    @PostMapping("/insert")
    public ResponseEntity<Void> insert(
            @RequestBody @Valid InsertCommentDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postCommentService.insert(dto, userDetails.getId());
        return ResponseEntity.status(201).build();
    }

    // 댓글 수정
    @PutMapping("/update")
    public ResponseEntity<Void> update(
            @RequestBody @Valid UpdateCommentDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postCommentService.update(dto, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    // 댓글 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(
            @RequestParam Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postCommentService.delete(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
