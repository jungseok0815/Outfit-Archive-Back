package com.fasthub.backend.oper.usr.service;

import com.fasthub.backend.oper.usr.repository.UserRepository;
import com.fasthub.backend.oper.usr.dto.JoinDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository usrRepository;

    public void insert(JoinDto joinDto){
        log.info("joinDto : " +  joinDto);
    }


}
