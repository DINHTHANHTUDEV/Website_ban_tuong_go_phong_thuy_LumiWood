package com.example.websitebantuonggolumiwood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserListDTO {
    @NotNull(message = "ID người dùng không được để trống")
    @Positive(message = "ID người dùng phải lớn hơn 0")
    private Long userId;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
    private String username;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    private String fullName;

    @NotBlank(message = "Bậc khách hàng không được để trống")
    @Pattern(regexp = "BRONZE|SILVER|GOLD|DIAMOND", message = "Bậc khách hàng phải là BRONZE, SILVER, GOLD hoặc DIAMOND")
    private String tier;

    @NotNull(message = "Tổng chi tiêu không được để trống")
    @PositiveOrZero(message = "Tổng chi tiêu phải lớn hơn hoặc bằng 0")
    private BigDecimal totalSpent;

    @NotNull(message = "Ngày tạo không được để trống")
    @PastOrPresent(message = "Ngày tạo phải là trong quá khứ hoặc hiện tại")
    private LocalDateTime createdAt;

    @NotNull(message = "Trạng thái hoạt động không được để trống")
    private Boolean isActive;
}