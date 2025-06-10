package com.example.websitebantuonggolumiwood.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ArticleDetailAdminDTO {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private String featuredImageUrl;
    private String authorUsername;
    private String authorFullName;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean IsPublished;
}