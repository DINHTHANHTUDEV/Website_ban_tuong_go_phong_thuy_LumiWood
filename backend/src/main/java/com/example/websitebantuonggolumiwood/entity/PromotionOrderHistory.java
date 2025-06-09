package com.example.websitebantuonggolumiwood.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Promotions")
public class PromotionOrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String code;

    private String name;

    private String description;

    @Column(name = "discount_type")
    private String discountType; // FIXED_AMOUNT hoặc PERCENTAGE

    @Column(name = "discount_value")
    private BigDecimal discountValue;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "max_usage")
    private Integer maxUsage;

    @Column(name = "current_usage")
    private Integer currentUsage;

    @Column(name = "min_order_value")
    private BigDecimal minOrderValue;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "target_tiers")
    private String targetTiers;
}

