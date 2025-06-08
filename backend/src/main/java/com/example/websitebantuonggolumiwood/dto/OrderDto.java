package com.example.OrderHistory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDto {
    private Integer id;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime orderDate;
    private List<OrderItemDto> orderItems;
    private String paymentMethod;

}
