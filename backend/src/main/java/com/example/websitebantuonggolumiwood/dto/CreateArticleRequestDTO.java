package com.example.websitebantuonggolumiwood.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateArticleRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 250, message = "Tiệu đề cần từ 5 đến 250 ký tự")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 5, message = "nội dung quá ngắn")
    private String content;

    @Size(max = 500, message = "tóm tắt quá dài")
    private String excerpt;

    @Size(max = 512, message = "Image URL quá dài")
    private String featuredImageUrl;

}