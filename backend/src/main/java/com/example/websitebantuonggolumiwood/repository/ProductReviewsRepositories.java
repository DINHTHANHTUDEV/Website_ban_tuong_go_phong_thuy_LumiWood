package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.ProductReviews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

//public interface ProductReviewsRepositories extends JpaRepository<ProductsEntity, Integer> {
//    Page<ProductReviews> findByProduct_IdAndIsApprovedTrue(Integer productId, Pageable pageable);
//}
public interface ProductReviewsRepositories extends JpaRepository<ProductReviews, Integer> {
    Page<ProductReviews> findByProduct_IdAndIsApprovedTrue(Integer productId, Pageable pageable);
}