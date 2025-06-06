package com.example.websitebantuonggolumiwood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserTierDTO {
    @NotBlank(message = "Bậc khách hàng không được để trống")
    @Pattern(regexp = "BRONZE|SILVER|GOLD|DIAMOND", message = "Bậc khách hàng phải là BRONZE, SILVER, GOLD hoặc DIAMOND")
    private String tier;
}