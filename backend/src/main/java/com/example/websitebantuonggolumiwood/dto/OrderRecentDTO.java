package com.example.websitebantuonggolumiwood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderRecentDTO {
    private Long id;
    private String code;         // Ví dụ: #MU-7001
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
}
