package com.example.websitebantuonggolumiwood.service.impl;

import com.example.websitebantuonggolumiwood.dto.ArticleCreateUpdateDTO;
import com.example.websitebantuonggolumiwood.dto.ArticleDetailAdminDTO;
import com.example.websitebantuonggolumiwood.dto.ArticleSummaryAdminDTO;
import com.example.websitebantuonggolumiwood.entity.Article;
import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.exception.BadRequestException;
import com.example.websitebantuonggolumiwood.exception.ResourceNotFoundException;
import com.example.websitebantuonggolumiwood.repository.ArticleAdminRepository;
import com.example.websitebantuonggolumiwood.repository.UserRepository;
import com.example.websitebantuonggolumiwood.repository.specification.ArticleSpecifications;
import com.example.websitebantuonggolumiwood.service.ArticleAdminService;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleServiceImpl implements ArticleAdminService {

    private static final Slugify slugify = Slugify.builder().build();

    private final ArticleAdminRepository articleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleSummaryAdminDTO> getPublishedArticles(Pageable pageable) {
        Page<Article> articlePage = articleRepository.findByIsPublishedTrue(pageable);
        return articlePage.map(this::mapToArticleSummaryDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDetailAdminDTO getPublishedArticleBySlug(String slug) {
        Article article = articleRepository.findBySlugAndIsPublishedTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Published Article", "slug", slug));
        return mapToArticleDetailDTO(article);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleSummaryAdminDTO> getAdminArticles(String keyword, Boolean isPublished, Pageable pageable) {
        Specification<Article> spec = ArticleSpecifications.filterAdminArticles(keyword, isPublished);
        Page<Article> articlePage = articleRepository.findAll(spec, pageable);
        return articlePage.map(this::mapToArticleSummaryDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDetailAdminDTO getArticleByIdAdmin(Long id) {
        Article article = findArticleByIdOrThrow(id);
        return mapToArticleDetailDTO(article);
    }

    @Override
    @Transactional
    public ArticleDetailAdminDTO createArticle(ArticleCreateUpdateDTO dto, String authorUsername) {
        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authorUsername));

        String slug = generateUniqueSlug(dto.getTitle());
        if (articleRepository.existsBySlug(slug)) {
            throw new DataIntegrityViolationException("Slug '" + slug + "' đã tồn tại. Vui lòng chọn tiêu đề khác.");
        }

        Article newArticle = new Article();
        BeanUtils.copyProperties(dto, newArticle);
        newArticle.setUser(author);
        newArticle.setSlug(slug);
        newArticle.setIsPublished(false);
        newArticle.setPublishedAt(null);

        Article savedArticle = articleRepository.save(newArticle);
        return mapToArticleDetailDTO(savedArticle);
    }

    @Override
    @Transactional
    public ArticleDetailAdminDTO updateArticle(Long id, ArticleCreateUpdateDTO dto) {
        Article article = findArticleByIdOrThrow(id);

        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setExcerpt(dto.getExcerpt());
        article.setFeaturedImageUrl(dto.getFeaturedImageUrl());

        if (!article.getTitle().equalsIgnoreCase(dto.getTitle())) {
            String newSlug = generateUniqueSlug(dto.getTitle(), id);
            if (!newSlug.equals(article.getSlug())) {
                if (articleRepository.existsBySlugAndIdNot(newSlug, id)) {
                    throw new DataIntegrityViolationException("Slug '" + newSlug + "' đã tồn tại. Vui lòng chọn tiêu đề khác.");
                }
                article.setSlug(newSlug);
            }
        }

        Article updatedArticle = articleRepository.save(article);
        return mapToArticleDetailDTO(updatedArticle);
    }

    @Override
    @Transactional
    public void deleteArticle(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Article", "id", id);
        }
        articleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ArticleDetailAdminDTO publishArticle(Long id) {
        Article article = findArticleByIdOrThrow(id);
        if (Boolean.TRUE.equals(article.getIsPublished())) {
            throw new BadRequestException("Bài viết đã được xuất bản rồi.");
        }
        article.setIsPublished(true);
        article.setPublishedAt(LocalDateTime.now());
        Article publishedArticle = articleRepository.save(article);
        return mapToArticleDetailDTO(publishedArticle);
    }

    @Override
    @Transactional
    public ArticleDetailAdminDTO unpublishArticle(Long id) {
        Article article = findArticleByIdOrThrow(id);
        if (Boolean.FALSE.equals(article.getIsPublished())) {
            throw new BadRequestException("Bài viết đang ở trạng thái nháp.");
        }
        article.setIsPublished(false);
        article.setPublishedAt(null);
        Article unpublishedArticle = articleRepository.save(article);
        return mapToArticleDetailDTO(unpublishedArticle);
    }

    private Article findArticleByIdOrThrow(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));
    }

    private ArticleSummaryAdminDTO mapToArticleSummaryDTO(Article article) {
        if (article == null) return null;
        ArticleSummaryAdminDTO dto = new ArticleSummaryAdminDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setSlug(article.getSlug());
        dto.setExcerpt(article.getExcerpt());
        dto.setFeaturedImageUrl(article.getFeaturedImageUrl());
        if (article.getUser() != null) {
            dto.setAuthorUsername(article.getUser().getUsername());
        }
        dto.setPublishedAt(article.getPublishedAt());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setIsPublished(article.getIsPublished());
        return dto;
    }

    private ArticleDetailAdminDTO mapToArticleDetailDTO(Article article) {
        if (article == null) return null;
        ArticleDetailAdminDTO dto = new ArticleDetailAdminDTO();
        BeanUtils.copyProperties(article, dto, "user");
        if (article.getUser() != null) {
            dto.setAuthorUsername(article.getUser().getUsername());
            dto.setAuthorFullName(article.getUser().getFullName());
        }
        dto.setIsPublished(article.getIsPublished());
        return dto;
    }

    private String generateUniqueSlug(String title) {
        return generateUniqueSlug(title, null);
    }

    private String generateUniqueSlug(String title, Long currentArticleId) {
        String baseSlug = slugify.slugify(title);
        if (baseSlug.isEmpty()) {
            baseSlug = "bai-viet-" + System.currentTimeMillis();
        }
        String finalSlug = baseSlug;
        int counter = 1;
        while (true) {
            Optional<Article> existing = articleRepository.findBySlug(finalSlug);
            if (existing.isEmpty() || (currentArticleId != null && existing.get().getId().equals(currentArticleId))) {
                break;
            }
            finalSlug = baseSlug + "-" + counter++;
            if (counter > 100) {
                throw new IllegalStateException("Không thể tạo slug duy nhất cho bài viết.");
            }
        }
        return finalSlug;
    }
}