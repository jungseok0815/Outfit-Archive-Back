package com.fasthub.backend.user.usr.controller;

import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import com.fasthub.backend.user.usr.dto.JoinDto;
import com.fasthub.backend.user.usr.dto.LoginDto;
import com.fasthub.backend.user.usr.dto.LoginResponseDto;
import com.fasthub.backend.user.usr.dto.UserDto;
import com.fasthub.backend.user.usr.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/join")
    public ResponseEntity<UserDto> join(@RequestBody JoinDto joinDto) {
        log.info("joinDto : " + joinDto.toString());
        return ResponseEntity.status(201).body(userService.join(joinDto));
    }

    @GetMapping("/list")
    public ResponseEntity<Void> list() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update() {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete() {
        return ResponseEntity.ok().build();
    }
}
