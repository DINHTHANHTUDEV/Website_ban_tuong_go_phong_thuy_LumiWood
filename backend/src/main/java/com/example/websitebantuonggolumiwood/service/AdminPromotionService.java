package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.dto.AdminPromotionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminPromotionService {

    //    Page<AdminPromotionDto> searchPromotions(String code, String discountType, Boolean isActive, Pageable pageable);
    Page<AdminPromotionDto> searchPromotions(String code, String discountType, Boolean isActive, String statusFilter, Pageable pageable);

    AdminPromotionDto getPromotionById(Integer id);

    AdminPromotionDto createPromotion(AdminPromotionDto dto);

    AdminPromotionDto updatePromotion(Integer id, AdminPromotionDto dto);

    void deletePromotion(Integer id);

    AdminPromotionDto togglePromotionStatus(Integer id, Boolean isActive);
}
