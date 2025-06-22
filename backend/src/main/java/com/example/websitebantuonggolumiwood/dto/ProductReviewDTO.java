package com.example.websitebantuonggolumiwood.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductReviewDTO {
    private Long id;
    private Byte rating;
    private String comment;
    private LocalDateTime createdAt;
    private String reviewerName;
    private String slugProduct;
}
