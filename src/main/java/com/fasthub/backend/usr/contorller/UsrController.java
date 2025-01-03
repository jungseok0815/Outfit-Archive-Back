package com.fasthub.backend.usr.contorller;

import com.fasthub.backend.usr.dto.JoinDto;
import com.fasthub.backend.usr.dto.UsrDto;
import com.fasthub.backend.usr.service.UsrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usr")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class UsrController {

    @Autowired
    private UsrService usrService;

    @PostMapping("/insert")
    public void insert(JoinDto joinDto){
        log.info("userid : " + joinDto.toString());
        usrService.insert(joinDto);
    }

    @PostMapping("/update")
    public void update(@ModelAttribute UsrDto usrDto){
        log.info("log : usr update : " + usrDto.getUsrId());
    }

    @PostMapping("/delete")
    public void delete(@ModelAttribute UsrDto usrDto){
        log.info("log : usr delete : " + usrDto.getUsrId());
    }

    @PostMapping("/list")
    public void list(@ModelAttribute UsrDto usrDto){
        log.info("log : usr list : " + usrDto.getUsrId());
    }

    @PostMapping("/select")
    public void select(@ModelAttribute UsrDto usrDto){
        log.info("log : usr select : " + usrDto.getUsrId());
    }

}

