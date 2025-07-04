package com.example.websitebantuonggolumiwood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDetailAdminDTO {

    private Integer id;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime orderDate;

    private BigDecimal discountAmount;
    private Long userId;
    private String guestEmail;

    private String shippingRecipientName;
    private String shippingRecipientPhone;
    private String shippingStreetAddress;
    private String shippingWard;
    private String shippingDistrict;
    private String shippingCity;

    private Integer shippingMethodId;
    private String shippingMethodName;  // **Thêm trường tên phương thức vận chuyển**
    private BigDecimal shippingCost;

    private String paymentMethod;
    private String orderNote;
    private String cancelReason;

    private BigDecimal depositAmount;
    private String depositStatus;

    private List<OrderItemAdminDTO> items;
}
