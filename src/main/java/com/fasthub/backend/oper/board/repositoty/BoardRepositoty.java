package com.fasthub.backend.oper.board.repositoty;


import com.fasthub.backend.oper.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepositoty extends JpaRepository<Board, Long> {
}
