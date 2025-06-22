package com.example.websitebantuonggolumiwood.dto;



import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdminPromotionDto {

    private Integer id;

    @NotBlank(message = "Mã khuyến mãi không được để trống")
    @Size(min = 3, max = 50, message = "Mã phải từ 3 đến 50 ký tự")
    private String code;

    @NotBlank(message = "Tên khuyến mãi không được để trống")
    @Size(min = 3, max = 255, message = "Tên không được vượt quá 255 ký tự")
    private String name;

    private String description;

    @NotBlank(message = "Loại khuyến mãi không được để trống")
    private String discountType;

    @NotNull(message = "Giá trị khuyến mãi không được để trống")
    @DecimalMin(value = "0.01", message = "Giá trị khuyến mãi phải lớn hơn 0")
    private BigDecimal discountValue;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;

    private Integer maxUsage;

    private Integer currentUsage;

    private BigDecimal minOrderValue;

    private Boolean isActive;

    private String targetTiers;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

