package com.fasthub.backend.oper.board.service;

import com.fasthub.backend.oper.board.dto.BoardDto;
import com.fasthub.backend.oper.board.repositoty.BoardRepositoty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepositoty boardRepositoty;

    public String insert(BoardDto boardDto){
        return null;
    }

    public String select(BoardDto boardDto){
        return  null;
    }

    public String update(BoardDto boardDto){
        return null;
    }

    public String delete(BoardDto boardDto){
        return null;
    }

}
