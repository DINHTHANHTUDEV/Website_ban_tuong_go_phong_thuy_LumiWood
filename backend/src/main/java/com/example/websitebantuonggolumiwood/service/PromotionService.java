package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.entity.PromotionOrderHistory;
import com.example.websitebantuonggolumiwood.entity.PromotionResponse;
import com.example.websitebantuonggolumiwood.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public List<PromotionOrderHistory> getAllValidPromotions(String userRank) {
        return promotionRepository.findValidPromotions(LocalDateTime.now(), userRank);
    }

    //button Áp dụng
    public PromotionResponse applyPromotion(String code, double subtotal) {
        PromotionOrderHistory promo = promotionRepository.findByCodeAndIsActiveTrue(code.trim().toUpperCase())
                .orElse(null);

        if (promo == null) {
            return new PromotionResponse(false, "Mã khuyến mãi không tồn tại hoặc đã ngưng hoạt động.", 0, null);
        }

//        LocalDateTime now = LocalDateTime.now();
//        if (promo.getStartDate() != null && now.isBefore(promo.getStartDate())) {
//            return new PromotionResponse(false, "Mã khuyến mãi chưa bắt đầu.", 0, null);
//        }
//
//        if (promo.getEndDate() != null && now.isAfter(promo.getEndDate())) {
//            return new PromotionResponse(false, "Mã khuyến mãi đã hết hạn.", 0, null);
//        }
//
//        if (promo.getMaxUsage() != null && promo.getCurrentUsage() != null &&
//                promo.getCurrentUsage() >= promo.getMaxUsage()) {
//            return new PromotionResponse(false, "Mã khuyến mãi đã được sử dụng hết.", 0, null);
//        }
        DecimalFormat df = new DecimalFormat("#,##0"); // hoặc "###0" nếu không muốn phân tách nghìn

        String minValueStr = df.format(promo.getMinOrderValue());
        if (promo.getMinOrderValue() != null &&
                BigDecimal.valueOf(subtotal).compareTo(promo.getMinOrderValue()) < 0) {
            return new PromotionResponse(false, "Đơn hàng phải đạt tối thiểu." + minValueStr+"VNĐ mới có thể áp dụng", 0, null);
        }

        double discountAmount = 0;
        if ("PERCENTAGE".equalsIgnoreCase(promo.getDiscountType())) {
            discountAmount = subtotal * promo.getDiscountValue().doubleValue() / 100.0;
        } else if ("FIXED_AMOUNT".equalsIgnoreCase(promo.getDiscountType())) {
            discountAmount = promo.getDiscountValue().doubleValue();
        }

        // đảm bảo giảm không vượt quá tổng
        if (discountAmount > subtotal) {
            discountAmount = subtotal;
        }

        return new PromotionResponse(true,
                "Áp dụng mã " + promo.getCode() + " thành công!",
                discountAmount,
                promo.getCode());
    }
}
