package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.entity.Promotion;
import com.example.websitebantuonggolumiwood.entity.PromotionResponse;
import com.example.websitebantuonggolumiwood.repository.PromotionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public List<Promotion> getAllValidPromotions(String userRank) {
        return promotionRepository.findValidPromotions(LocalDateTime.now(), userRank);
    }

    //button Áp dụng
    public PromotionResponse applyPromotion(String code, double subtotal) {
        Promotion promo = promotionRepository.findByCodeAndIsActiveTrue(code.trim().toUpperCase())
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

    /**
     * Ghi nhận việc sử dụng mã khuyến mãi và cập nhật số lần sử dụng
     * @param promotionId ID của mã khuyến mãi
     * @throws RuntimeException Nếu không tìm thấy mã khuyến mãi hoặc lỗi khi cập nhật
     */
    public void recordPromotionUsage(Integer promotionId) {
        Promotion promo = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Mã khuyến mãi với ID " + promotionId + " không tồn tại"));

        // Tăng số lần sử dụng hiện tại lên 1
        promo.setCurrentUsage(promo.getCurrentUsage() != null ? promo.getCurrentUsage() + 1 : 1);

        // Lưu lại thay đổi vào cơ sở dữ liệu
        promotionRepository.save(promo);
    }
}