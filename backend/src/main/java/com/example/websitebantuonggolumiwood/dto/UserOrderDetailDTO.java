package com.example.websitebantuonggolumiwood.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class UserOrderDetailDTO {
    private Long orderId;
    private LocalDateTime orderDate;
    private String status;
    private String paymentMethod;
    private String orderNote;

    private String shippingRecipientName;
    private String shippingRecipientPhone;
    private String shippingStreetAddress;
    private String shippingWard;
    private String shippingDistrict;
    private String shippingCity;
    private String shippingMethodName;
    private BigDecimal shippingCost;

    private String promotionCode;
    private BigDecimal discountAmount;

    private BigDecimal subtotal;
    private BigDecimal totalAmount;

    private BigDecimal depositAmount;     // 30%
    private BigDecimal remainingAmount;   // 70%

    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private Integer quantity;
        private BigDecimal priceAtPurchase;
        private String productName;
        private String productImageUrl;
    }
}

