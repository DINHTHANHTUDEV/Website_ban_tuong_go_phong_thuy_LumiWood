package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.dto.ArticleDetailDto;
import com.example.websitebantuonggolumiwood.dto.ArticleSummaryDto;
import com.example.websitebantuonggolumiwood.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.example.websitebantuonggolumiwood.repository.ArticleRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public Page<ArticleSummaryDto> getPublicArticles(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));

        // Lấy tất cả bài viết
        Page<Article> articlePage = articleRepository.findAll(pageable);

        // Lọc và chuyển đổi sang DTO
        List<ArticleSummaryDto> dtoList = articlePage.getContent().stream()
                .filter(article -> Boolean.TRUE.equals(article.getIsPublished()))
                .map(article -> new ArticleSummaryDto(
                        article.getId(),
                        article.getTitle(),
                        article.getSlug(),
                        article.getExcerpt(),
                        article.getFeaturedImageUrl(),
                        article.getPublishedAt()
                ))
                .collect(Collectors.toList());

        // Trả về Page DTO giữ lại phân trang
        return new PageImpl<>(dtoList, pageable, dtoList.size());
    }

//    public ArticleSummaryDto getArticleBySlug(String slug) {
//        Article article = articleRepository.findBySlug(slug)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
//
//        if (!Boolean.TRUE.equals(article.getIsPublished())) {
//            throw new RuntimeException("Bài viết chưa được xuất bản");
//        }
//
//        return new ArticleSummaryDto(
//                article.getId(),
//                article.getTitle(),
//                article.getSlug(),
//                article.getExcerpt(),
//                article.getFeaturedImageUrl(),
//                article.getPublishedAt()
//        );
//    }
public ArticleDetailDto getArticleBySlug(String slug) {
    Article article = articleRepository.findBySlug(slug)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

    if (!Boolean.TRUE.equals(article.getIsPublished())) {
        throw new RuntimeException("Bài viết chưa được xuất bản");
    }

    return new ArticleDetailDto(
            article.getId(),
            article.getTitle(),
            article.getSlug(),
            article.getExcerpt(),
            article.getContent(),  // <- phần nội dung chi tiết
            article.getFeaturedImageUrl(),
            article.getPublishedAt()
    );
}
}


