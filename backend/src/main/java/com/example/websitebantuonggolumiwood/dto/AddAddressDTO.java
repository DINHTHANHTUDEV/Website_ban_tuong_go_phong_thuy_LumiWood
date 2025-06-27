package com.example.websitebantuonggolumiwood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddAddressDTO {
//    @NotNull(message = "ID địa chỉ không được để trống")
//    @Positive(message = "ID địa chỉ phải lớn hơn 0")
//    private Long id;

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(min = 2, max = 100, message = "Tên người nhận phải từ 2 đến 100 ký tự")
    private String recipientName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^\\+?[0-9]{10,12}$", message = "Số điện thoại phải có 10-12 chữ số và có thể bắt đầu bằng +")
    private String recipientPhone;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Size(max = 200, message = "Địa chỉ chi tiết không được vượt quá 200 ký tự")
    private String streetAddress;

    @NotBlank(message = "Phường không được để trống")
    @Size(max = 100, message = "Phường không được vượt quá 100 ký tự")
    private String ward;

    @NotBlank(message = "Quận/Huyện không được để trống")
    @Size(max = 100, message = "Quận/Huyện không được vượt quá 100 ký tự")
    private String district;

    @NotBlank(message = "Thành phố không được để trống")
    @Size(max = 100, message = "Thành phố không được vượt quá 100 ký tự")
    private String city;
}
