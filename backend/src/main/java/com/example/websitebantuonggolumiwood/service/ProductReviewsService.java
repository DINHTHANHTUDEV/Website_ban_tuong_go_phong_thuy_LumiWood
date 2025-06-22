package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.dto.ProductReviewDTO;
import com.example.websitebantuonggolumiwood.entity.ProductReviews;
import com.example.websitebantuonggolumiwood.entity.ProductsEntity;
import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.repository.ProductReviewsRepositories;
import com.example.websitebantuonggolumiwood.repository.ProductsRepositories;
import com.example.websitebantuonggolumiwood.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service

public class ProductReviewsService {
    private final ProductReviewsRepositories productReviewsRepositories;
    private final ProductsRepositories productsRepositories;
    @Autowired
    private UserRepository userRepository;

    public ProductReviewsService(ProductReviewsRepositories productReviewsRepositories, ProductsRepositories productsRepositories) {
        this.productReviewsRepositories = productReviewsRepositories;
        this.productsRepositories = productsRepositories;
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


//    public ProductReviews addReview(Integer productId, ProductReviews review, String username) {
//        ProductsEntity product = productsRepositories.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
//    //String username = "customer2";
////        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//
////        User user = userRepository.findByUsername(review.getUser().getUsername())
////                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
//        User user = userRepository.findByUsername(review.)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
//
//        ProductReviews reviews = new ProductReviews();
//        reviews.setProduct(product);
//        reviews.setUser(user);
//        reviews.setRating(review.getRating());
//        reviews.setComment(review.getComment());
//        reviews.setIsApproved(true);
//        reviews.setCreatedAt(LocalDateTime.now());
//        reviews.setUpdatedAt(LocalDateTime.now());
//
//        return productReviewsRepositories.save(reviews);
//    }

public ProductReviews addReview(Integer productId, ProductReviews review, String username) {
    // Lấy sản phẩm
    ProductsEntity product = productsRepositories.findById(productId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

    // Lấy user
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

    // Tạo review mới
    ProductReviews reviews = new ProductReviews();
    reviews.setProduct(product);
    reviews.setUser(user);
    reviews.setRating(review.getRating());
    reviews.setComment(review.getComment());
    reviews.setIsApproved(true);
    reviews.setCreatedAt(LocalDateTime.now());
    reviews.setUpdatedAt(LocalDateTime.now());

    return productReviewsRepositories.save(reviews);
}
}
