package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.ArticleCreateUpdateDTO;
import com.example.websitebantuonggolumiwood.dto.ArticleDetailAdminDTO;
import com.example.websitebantuonggolumiwood.dto.ArticleSummaryAdminDTO;
import com.example.websitebantuonggolumiwood.service.ArticleAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class AdminArticleController {
    private final ArticleAdminService articleService;

    @GetMapping
    public ResponseEntity<Page<ArticleSummaryAdminDTO>> getAdminArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isPublished,
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ArticleSummaryAdminDTO> articlePage = articleService.getAdminArticles(keyword, isPublished, pageable);
        return ResponseEntity.ok(articlePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDetailAdminDTO> getArticleById(@PathVariable Long id) {
        ArticleDetailAdminDTO article = articleService.getArticleByIdAdmin(id);
        return ResponseEntity.ok(article);
    }

    @PostMapping
    public ResponseEntity<ArticleDetailAdminDTO> createArticle(
            @Valid @RequestBody ArticleCreateUpdateDTO dto) {
        ArticleDetailAdminDTO createdArticle = articleService.createArticle(dto, "admin");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDetailAdminDTO> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleCreateUpdateDTO dto) {
        ArticleDetailAdminDTO updatedArticle = articleService.updateArticle(id, dto);
        return ResponseEntity.ok(updatedArticle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<ArticleDetailAdminDTO> publishArticle(@PathVariable Long id) {
        ArticleDetailAdminDTO publishedArticle = articleService.publishArticle(id);
        return ResponseEntity.ok(publishedArticle);
    }

    @PatchMapping("/{id}/unpublish")
    public ResponseEntity<ArticleDetailAdminDTO> unpublishArticle(@PathVariable Long id) {
        ArticleDetailAdminDTO unpublishedArticle = articleService.unpublishArticle(id);
        return ResponseEntity.ok(unpublishedArticle);
    }
}