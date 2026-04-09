package com.fasthub.backend.user.usr.controller;

import com.fasthub.backend.cmm.jwt.JwtService;
import com.fasthub.backend.user.usr.dto.*;
import com.fasthub.backend.user.usr.service.PasswordResetService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/usr")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        jwtService.logoutUser(request, response);
        return ResponseEntity.noContent().build();
    }

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

    // 프로필 조회
    @GetMapping("/profile/{id}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfile(id));
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

    // 프로필 이미지 수정
    @PutMapping("/profile-img")
    public ResponseEntity<Map<String, String>> updateProfileImg(
            @RequestParam Long id,
            @RequestParam MultipartFile profileImg) {
        String fileName = userService.updateProfileImg(id, profileImg);
        return ResponseEntity.ok(Map.of("profileImgNm", fileName));
    }

    // 유저 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 아이디 찾기 (전화번호 → 마스킹된 이메일)
    @GetMapping("/find-id")
    public ResponseEntity<Map<String, String>> findId(@RequestParam String phone) {
        String maskedEmail = userService.findIdByPhone(phone);
        return ResponseEntity.ok(Map.of("userId", maskedEmail));
    }

    // 비밀번호 재설정 이메일 발송
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordDto dto) {
        passwordResetService.sendResetEmail(dto);
        return ResponseEntity.ok().build();
    }

    // 비밀번호 재설정 (토큰 검증 + 변경)
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDto dto) {
        passwordResetService.resetPassword(dto);
        return ResponseEntity.ok().build();
    }
}
