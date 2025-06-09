package com.example.websitebantuonggolumiwood.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor

public class ProductUpdateDTO {

    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private String dimensions;
    private String material;
    private Long categoryId;
    private Boolean isActive;

    public ProductUpdateDTO() {
    }
}
