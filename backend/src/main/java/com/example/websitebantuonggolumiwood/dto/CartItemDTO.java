package com.example.websitebantuonggolumiwood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CartItemDTO {
    private Integer productId;
    private Integer quantity;
    private String productName;
    private String imageUrl;
    private double price;
    private String productSlug;
}
