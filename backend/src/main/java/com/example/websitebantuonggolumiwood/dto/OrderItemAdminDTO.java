package com.example.websitebantuonggolumiwood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderItemAdminDTO {

    private Integer id;
    private Integer productId;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private String productImageUrl;
    private String productName; // nếu muốn hiển thị tên

}
