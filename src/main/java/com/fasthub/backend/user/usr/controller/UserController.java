package com.fasthub.backend.user.usr.controller;

import com.fasthub.backend.user.usr.dto.*;
import com.fasthub.backend.user.usr.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/usr")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.login(loginDto, response));
    }

    @PostMapping("/join")
    public ResponseEntity<UserDto> join(@RequestBody JoinDto joinDto) {
        log.info("joinDto : " + joinDto.toString());
        return ResponseEntity.status(201).body(userService.join(joinDto));
    }

    @GetMapping("/validate")
    public ResponseEntity<CustomUserDetails> validate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication : " + authentication.getName());
        if (!Objects.equals(authentication.getName(), "anonymousUser")) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            return ResponseEntity.ok(customUserDetails);
        }
        return ResponseEntity.status(401).build();
    }

    // 유저 목록 조회 (keyword: 이름 검색, pageable: 페이징)
    @GetMapping("/list")
    public ResponseEntity<Page<ResponseUserDto>> list(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(userService.list(keyword, pageable));
    }

    // 유저 정보 수정
    @PutMapping("/update")
    public ResponseEntity<Void> update(@RequestBody @Valid UpdateUserDto updateUserDto) {
        userService.update(updateUserDto);
        return ResponseEntity.ok().build();
    }

    // 유저 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
