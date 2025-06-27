package com.example.websitebantuonggolumiwood.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PromotionResponse {
    private boolean success;
    private String message;
    private double discountAmount;
    private String appliedCode;


}
