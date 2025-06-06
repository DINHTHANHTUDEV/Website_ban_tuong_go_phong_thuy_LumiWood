package com.example.websitebantuonggolumiwood.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserProfileDTO {
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;
}
