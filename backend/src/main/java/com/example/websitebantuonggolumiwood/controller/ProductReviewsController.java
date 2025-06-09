package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.entity.ProductReviews;
import com.example.websitebantuonggolumiwood.service.ProductReviewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productReview")
@CrossOrigin("http://localhost:5173")
public class ProductReviewsController {
    private final ProductReviewsService productReviewsService;

    public ProductReviewsController(ProductReviewsService productReviewsService) {
        this.productReviewsService = productReviewsService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Page<ProductReviews>> getReviewsByProduct(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductReviews> reviews = productReviewsService.getApprovedReviewsByProductId(productId, pageable);
        return ResponseEntity.ok(reviews);
    }
}
