package com.fasthub.backend.admin.banner.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "banner")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "HIGHLIGHT")
    private String highlight;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "BUTTON_TEXT")
    private String buttonText;

    @Column(name = "SORT_ORDER", nullable = false)
    private int sortOrder;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "banner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BannerImg> images = new ArrayList<>();

    public void update(String title, String highlight, String description, String buttonText, int sortOrder, boolean active) {
        this.title = title;
        this.highlight = highlight;
        this.description = description;
        this.buttonText = buttonText;
        this.sortOrder = sortOrder;
        this.active = active;
    }
}
