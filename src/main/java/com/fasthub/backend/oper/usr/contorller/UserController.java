package com.fasthub.backend.oper.usr.contorller;

import com.fasthub.backend.cmm.security.UserDetailService;
import com.fasthub.backend.oper.usr.dto.JoinDto;
import com.fasthub.backend.oper.usr.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usr")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class UserController {
    private final UserDetailService userDetailService;

    @PostMapping("/insert")
    public void insert(JoinDto joinDto){
        userDetailService.join(joinDto);
    }

    @PostMapping("/update")
    public void update(@ModelAttribute UserDto usrDto){
        log.info("log : usr update : " + usrDto.getUsrId());
    }

    @PostMapping("/delete")
    public void delete(@ModelAttribute UserDto usrDto){
        log.info("log : usr delete : " + usrDto.getUsrId());
    }

    @PostMapping("/list")
    public void list(@ModelAttribute UserDto usrDto){
        log.info("log : usr list : " + usrDto.getUsrId());
    }

    @PostMapping("/select")
    public void select(@ModelAttribute UserDto usrDto){
        log.info("log : usr select : " + usrDto.getUsrId());
    }

}

