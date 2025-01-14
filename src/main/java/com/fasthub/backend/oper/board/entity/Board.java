package com.fasthub.backend.oper.board.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "BOARD")
public class Board {

    @Id
    private Long id;
}
