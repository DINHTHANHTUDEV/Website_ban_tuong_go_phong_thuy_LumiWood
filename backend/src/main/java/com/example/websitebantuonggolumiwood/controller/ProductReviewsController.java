package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.ProductReviewDTO;
import com.example.websitebantuonggolumiwood.entity.ProductReviews;
import com.example.websitebantuonggolumiwood.service.ProductReviewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productReview")
@CrossOrigin("http://localhost:5173")
public class ProductReviewsController {
    private final ProductReviewsService productReviewsService;

    public ProductReviewsController(ProductReviewsService productReviewsService) {
        this.productReviewsService = productReviewsService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Page<ProductReviewDTO>> getReviewsByProduct(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductReviewDTO> reviewDTOs = productReviewsService.getApprovedReviewDTOsByProductId(productId, pageable);
        return ResponseEntity.ok(reviewDTOs);
    }
    // create review where product slug

    @PostMapping("/add/{productId}")
    public ResponseEntity<ProductReviews> addReview(
            @PathVariable Integer productId,
            @RequestBody ProductReviews review,
            @AuthenticationPrincipal UserDetails userDetails) {

        ProductReviews savedReview = productReviewsService.addReview(productId, review, userDetails.getUsername());
        return ResponseEntity.ok(savedReview);
    }


}
