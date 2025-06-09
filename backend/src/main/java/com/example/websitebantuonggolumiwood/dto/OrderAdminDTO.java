package com.example.websitebantuonggolumiwood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderAdminDTO {

    private Integer id;
    private String customerName;
    private String customerPhone;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime orderDate;

}
