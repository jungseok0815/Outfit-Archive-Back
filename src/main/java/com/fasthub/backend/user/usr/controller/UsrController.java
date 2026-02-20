package com.fasthub.backend.user.usr.controller;

import com.fasthub.backend.user.usr.dto.JoinDto;
import com.fasthub.backend.user.usr.dto.UserDto;
import com.fasthub.backend.user.usr.service.UsrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usr")
@RequiredArgsConstructor
@Slf4j
public class UsrController {

    private final UsrService usrService;

    @PostMapping("/insert")
    public ResponseEntity<UserDto> insert(@RequestBody JoinDto joinDto) {
        log.info("joinDto : " + joinDto.toString());
        return ResponseEntity.status(201).body(usrService.insert(joinDto));
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
