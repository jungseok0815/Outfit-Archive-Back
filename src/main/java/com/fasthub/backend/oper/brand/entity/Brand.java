package com.fasthub.backend.oper.brand.entity;

import jakarta.persistence.*;

@Entity
@Table(name ="Brand")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
}
