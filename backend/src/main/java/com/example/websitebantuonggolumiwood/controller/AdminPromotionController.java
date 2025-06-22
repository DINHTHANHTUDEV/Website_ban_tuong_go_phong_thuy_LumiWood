package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.AdminPromotionDto;
import com.example.websitebantuonggolumiwood.service.AdminPromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/promotions")
@CrossOrigin(origins = "http://localhost:5173") // hoặc * nếu đang dev

public class AdminPromotionController {

    @Autowired
    private AdminPromotionService promotionService;

    // GET: Danh sách có phân trang + tìm kiếm
    @GetMapping("/listPromotions")
    public Page<AdminPromotionDto> searchPromotions(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String discountType,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String statusFilter, // THÊM VÀO ĐÂY
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return promotionService.searchPromotions(code, discountType, isActive, statusFilter, pageable);
    }

    // GET: Chi tiết
    @GetMapping("/getPromotionById/{id}")
    public AdminPromotionDto getPromotionById(@PathVariable Integer id) {
        return promotionService.getPromotionById(id);
    }

    // POST: Tạo mới
    @PostMapping("/addPromotions")
    public AdminPromotionDto createPromotion(@Valid @RequestBody AdminPromotionDto dto) {
        return promotionService.createPromotion(dto);
    }

    // PUT: Cập nhật
    @PutMapping("/updatePromotions/{id}")
    public AdminPromotionDto updatePromotion(@PathVariable Integer id,@Valid @RequestBody AdminPromotionDto dto) {
        return promotionService.updatePromotion(id, dto);
    }

    // DELETE: Xoá
    @DeleteMapping("/deletePromotions/{id}")
    public void deletePromotion(@PathVariable Integer id) {
        promotionService.deletePromotion(id);
    }
    @PatchMapping("/toggleStatus/{id}")
    public AdminPromotionDto togglePromotionStatus(@PathVariable Integer id, @RequestParam Boolean isActive) {
        return promotionService.togglePromotionStatus(id, isActive);
    }

}
