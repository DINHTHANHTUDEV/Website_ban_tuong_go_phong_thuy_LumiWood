package com.example.websitebantuonggolumiwood.service;

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
    public Page<ProductReviews> getApprovedReviewsByProductId(Integer product, Pageable pageable) {
        return productReviewsRepositories.findByProduct_IdAndIsApprovedTrue(product, pageable);
    }
}
