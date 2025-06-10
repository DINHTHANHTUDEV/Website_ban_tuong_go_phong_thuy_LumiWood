package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.ArticleDetailAdminDTO;
import com.example.websitebantuonggolumiwood.dto.ArticleSummaryAdminDTO;
import com.example.websitebantuonggolumiwood.service.ArticleAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {


    private final ArticleAdminService articleService;

    // Lấy danh sách bài viết đã publish (phân trang)
    @GetMapping
    public ResponseEntity<Page<ArticleSummaryAdminDTO>> getPublishedArticles(
            @PageableDefault(size = 10, sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ArticleSummaryAdminDTO> articlePage = articleService.getPublishedArticles(pageable);
        return ResponseEntity.ok(articlePage);
    }

    // Lấy chi tiết bài viết đã publish theo slug
    @GetMapping("/{slug}")
    public ResponseEntity<ArticleDetailAdminDTO> getPublishedArticleBySlug(@PathVariable String slug) {
        ArticleDetailAdminDTO article = articleService.getPublishedArticleBySlug(slug);
        return ResponseEntity.ok(article);
    }
}