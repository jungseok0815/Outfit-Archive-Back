package com.fasthub.backend.usr.service;

import com.fasthub.backend.usr.dto.JoinDto;
import com.fasthub.backend.usr.repository.UsrRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UsrService {

    @Autowired
    private UsrRepository usrRepository;

    public void insert(JoinDto joinDto){
        log.info("joinDto : " +  joinDto);
    }


}
