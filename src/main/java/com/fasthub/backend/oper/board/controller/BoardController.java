package com.fasthub.backend.oper.board.controller;

import com.fasthub.backend.oper.board.dto.BoardDto;
import com.fasthub.backend.oper.board.entity.Board;
import com.fasthub.backend.oper.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/insert")
    public String insert(BoardDto boardDto){
        return null;
    }

    @GetMapping("/select")
    public String select(BoardDto boardDto){
        return  null;
    }

    @PutMapping("/update")
    public String update(BoardDto boardDto){
        return null;
    }

    @DeleteMapping("/delete")
    public String delete(BoardDto boardDto){
        return null;
    }
}

