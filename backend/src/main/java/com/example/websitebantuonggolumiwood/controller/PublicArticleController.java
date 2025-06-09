package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.ArticleDetailDto;
import com.example.websitebantuonggolumiwood.dto.ArticleSummaryDto;
import com.example.websitebantuonggolumiwood.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/articles")
//@CrossOrigin(origins = "http://localhost:5173")  // Kết nối FE
public class PublicArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/getListArticles")
    public Page<ArticleSummaryDto> getArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "publishedAt") String sort
    ) {
        return articleService.getPublicArticles(page, size, sort);
    }

    //    @GetMapping("/{slug}")
//    public ArticleSummaryDto getArticleBySlug(@PathVariable String slug) {
//        return articleService.getArticleBySlug(slug);
//    }
    @GetMapping("/{slug}")
    public ArticleDetailDto getArticleBySlug(@PathVariable String slug) {
        return articleService.getArticleBySlug(slug);
    }
}

