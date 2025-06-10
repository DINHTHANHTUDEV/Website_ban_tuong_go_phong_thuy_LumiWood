package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.dto.ArticleCreateUpdateDTO;
import com.example.websitebantuonggolumiwood.dto.ArticleDetailAdminDTO;
import com.example.websitebantuonggolumiwood.dto.ArticleSummaryAdminDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleAdminService {

    // --- User Facing ---
    Page<ArticleSummaryAdminDTO> getPublishedArticles(Pageable pageable);

    ArticleDetailAdminDTO getPublishedArticleBySlug(String slug);

    // --- Admin Facing ---
    Page<ArticleSummaryAdminDTO> getAdminArticles(String keyword, Boolean isPublished, Pageable pageable);


    ArticleDetailAdminDTO getArticleByIdAdmin(Long id);


    ArticleDetailAdminDTO createArticle(ArticleCreateUpdateDTO dto, String authorUsername);


    ArticleDetailAdminDTO updateArticle(Long id, ArticleCreateUpdateDTO dto);


    void deleteArticle(Long id);

    ArticleDetailAdminDTO publishArticle(Long id);


    ArticleDetailAdminDTO unpublishArticle(Long id);
}