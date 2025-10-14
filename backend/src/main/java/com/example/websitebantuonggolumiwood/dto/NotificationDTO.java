package com.example.websitebantuonggolumiwood.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationDTO {

    // Tiêu đề thông báo (vd: "Cập nhật đơn hàng")
    private String title;

    // Nội dung thông báo (vd: "Đơn hàng MĐH#123 đang được xử lý")
    private String content;

    // Mã đơn hàng liên quan (dùng để frontend hiển thị chi tiết)
    private Integer orderId;

    private LocalDateTime createdTime = LocalDateTime.now(); // thêm thời gian gửi

}
