package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleAdminRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
    Optional<Article> findBySlugAndIsPublishedTrue(String slug);

    Page<Article> findByIsPublishedTrue(Pageable pageable);

    Optional<Article> findBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    boolean existsBySlug(String slug);
}
