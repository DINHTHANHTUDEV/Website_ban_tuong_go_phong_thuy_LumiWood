package com.example.websitebantuonggolumiwood.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ArticleSummaryAdminDTO {
    private Long id;
    private String title;
    private String slug;
    private String excerpt;
    private String featuredImageUrl;
    private String authorUsername;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private boolean IsPublished;
}