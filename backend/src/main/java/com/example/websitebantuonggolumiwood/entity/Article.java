package com.example.websitebantuonggolumiwood.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 300)
    private String slug;

    @Column(name = "content", nullable = false, columnDefinition = "nvarchar(MAX)")
    private String content;

    @Column(length = 500)
    private String excerpt;

    @Column(name = "featured_image_url", length = 512)
    private String featuredImageUrl;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
