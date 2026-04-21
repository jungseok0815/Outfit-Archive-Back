package com.fasthub.backend.admin.category.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
@Table(name = "Category")
@EntityListeners(AuditingEntityListener.class)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;        // 식별자 (예: TOP, BOTTOM)

    @Column(name = "KOR_NAME", nullable = false)
    private String korName;     // 한글명 (예: 상의)

    @Column(name = "ENG_NAME", nullable = false)
    private String engName;     // 영문명 (예: Tops)

    @Column(name = "DEFAULT_SIZES")
    private String defaultSizes; // 기본 사이즈 (예: S,M,L,XL)

    @Column(name = "ACTIVE", nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public void update(String korName, String engName, String defaultSizes) {
        this.korName = korName;
        this.engName = engName;
        this.defaultSizes = defaultSizes;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }
}
