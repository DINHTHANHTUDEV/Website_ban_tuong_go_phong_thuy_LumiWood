package com.example.websitebantuonggolumiwood.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder // Dùng Builder pattern để dễ tạo đối tượng
public class OrderSummaryDTO {
    private Integer orderId;
    private String orderStatus;
    private LocalDateTime orderDate;
    private BigDecimal finalAmount; // Tổng tiền cuối cùng
    private String paymentMethod;
    private String successMessage; // Thông báo thành công
    private BigDecimal depositAmount; // Số tiền đặt cọc (30% nếu totalAmount ≥ 10 triệu)
    private String depositStatus; // Trạng thái đặt cọc (ví dụ: PENDING, PAID)
    private String paymentUrl; // Nếu thanh toán online
}