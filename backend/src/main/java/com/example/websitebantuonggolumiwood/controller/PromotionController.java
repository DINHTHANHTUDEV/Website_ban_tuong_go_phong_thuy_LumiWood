package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.entity.PromotionOrderHistory;
import com.example.websitebantuonggolumiwood.entity.PromotionResponse;
import com.example.websitebantuonggolumiwood.service.ProductsService;
import com.example.websitebantuonggolumiwood.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotion")
public class PromotionController {
    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }


    @GetMapping("/hienthi")
    public List<PromotionOrderHistory> getPromotionOrderHistory(@RequestParam String userRank) {
        return promotionService.getAllValidPromotions(userRank);
    }
    @PostMapping("/apply")
    public ResponseEntity<PromotionResponse> applyPromotion(
            @RequestParam String code,
            @RequestParam double subtotal) {

        PromotionResponse response = promotionService.applyPromotion(code, subtotal);
        return ResponseEntity.ok(response);
    }


}
