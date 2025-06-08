package com.example.Article.repo;

import com.example.Article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // Tìm bài viết theo slug
    Optional<Article> findBySlug(String slug);

    // Lọc các bài viết đã publish và hỗ trợ phân trang
    Page<Article> findByIsPublishedTrue(Pageable pageable);
}

