package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.dto.ProductReviewDTO;
import com.example.websitebantuonggolumiwood.entity.ProductReviews;
import com.example.websitebantuonggolumiwood.repository.ProductReviewsRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service

public class ProductReviewsService {
    private final ProductReviewsRepositories productReviewsRepositories;

    public ProductReviewsService(ProductReviewsRepositories productReviewsRepositories) {
        this.productReviewsRepositories = productReviewsRepositories;
    }
    public Page<ProductReviewDTO> getApprovedReviewDTOsByProductId(Integer productId, Pageable pageable) {
        Page<ProductReviews> reviews = productReviewsRepositories.findByProduct_IdAndIsApprovedTrue(productId, pageable);
        return reviews.map(review -> {
            ProductReviewDTO dto = new ProductReviewDTO();
            dto.setId(review.getId());
            dto.setRating(review.getRating());
            dto.setComment(review.getComment());
            dto.setCreatedAt(review.getCreatedAt());

            String fullName = review.getUser() != null ? review.getUser().getFullName() : null;
            dto.setReviewerName((fullName != null && !fullName.isBlank()) ? fullName : "Ẩn danh");

            return dto;
        });
    }
}
