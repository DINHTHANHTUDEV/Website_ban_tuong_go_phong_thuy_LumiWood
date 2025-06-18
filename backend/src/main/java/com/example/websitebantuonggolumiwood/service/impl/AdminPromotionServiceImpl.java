package com.example.websitebantuonggolumiwood.service.impl;

import com.example.websitebantuonggolumiwood.dto.AdminPromotionDto;
import com.example.websitebantuonggolumiwood.entity.AdminPromotions;
import com.example.websitebantuonggolumiwood.exception.PromotionNotFoundException;
import com.example.websitebantuonggolumiwood.repository.AdminPromotionRepository;
import com.example.websitebantuonggolumiwood.service.AdminPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminPromotionServiceImpl implements AdminPromotionService {

    @Autowired
    private AdminPromotionRepository promotionRepository;

    //    @Override
//    public Page<AdminPromotionDto> searchPromotions(String code, String discountType, Boolean isActive, String statusFilter, Pageable pageable) {
//        Page<AdminPromotions> page = promotionRepository.findAll((root, query, cb) -> {
//            var predicates = cb.conjunction();
//
//            // Lọc theo mã code (nếu có)
//            if (code != null && !code.isEmpty()) {
//                predicates = cb.and(predicates, cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%"));
//            }
//
//            // Lọc theo loại khuyến mãi (discountType)
//            if (discountType != null && !discountType.isEmpty()) {
//                predicates = cb.and(predicates, cb.equal(root.get("discountType"), discountType));
//            }
//
//            // Lọc theo trạng thái bật/tắt
//            if (isActive != null) {
//                predicates = cb.and(predicates, cb.equal(root.get("isActive"), isActive));
//            }
//
//            // Lọc theo statusFilter (ACTIVE, INACTIVE, UPCOMING, EXPIRED)
//            if (statusFilter != null && !statusFilter.isEmpty()) {
//                LocalDateTime now = LocalDateTime.now();
//
//                switch (statusFilter.toUpperCase()) {
//                    case "ACTIVE":
//                        predicates = cb.and(predicates,
//                                cb.lessThanOrEqualTo(root.get("startDate"), now),
//                                cb.greaterThanOrEqualTo(root.get("endDate"), now)
//                        );
//                        break;
//                    case "UPCOMING":
//                        predicates = cb.and(predicates, cb.greaterThan(root.get("startDate"), now));
//                        break;
//                    case "EXPIRED":
//                        predicates = cb.and(predicates, cb.lessThan(root.get("endDate"), now));
//                        break;
//                    default:
//                        // Không lọc gì nếu statusFilter không hợp lệ
//                        break;
//                }
//            }
//
//            return predicates;
//        }, pageable);
//
//        return page.map(this::mapToDto);
//    }
    @Override
    public Page<AdminPromotionDto> searchPromotions(String code, String discountType, Boolean isActive, String statusFilter, Pageable pageable) {
        Page<AdminPromotions> page = promotionRepository.findAll((root, query, cb) -> {
            var predicates = cb.conjunction();

            // Lọc theo mã code (nếu có)
            if (code != null && !code.isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%"));
            }

            // Lọc theo loại khuyến mãi (discountType)
            if (discountType != null && !discountType.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("discountType"), discountType));
            }

            LocalDateTime now = LocalDateTime.now();

            // Lọc theo statusFilter (ACTIVE, INACTIVE, UPCOMING, EXPIRED)
            if (statusFilter != null && !statusFilter.isEmpty()) {
                switch (statusFilter.toUpperCase()) {
                    case "ACTIVE":
                        predicates = cb.and(predicates,
                                cb.lessThanOrEqualTo(root.get("startDate"), now),
                                cb.greaterThanOrEqualTo(root.get("endDate"), now),
                                cb.equal(root.get("isActive"), true)
                        );
                        break;
                    case "UPCOMING":
                        predicates = cb.and(predicates,
                                cb.greaterThan(root.get("startDate"), now),
                                cb.equal(root.get("isActive"), true)
                        );
                        break;
                    case "EXPIRED":
                        predicates = cb.and(predicates,
                                cb.lessThan(root.get("endDate"), now),
                                cb.equal(root.get("isActive"), true)
                        );
                        break;
                    case "INACTIVE":
                        predicates = cb.and(predicates, cb.equal(root.get("isActive"), false));
                        break;
                    default:
                        // Không lọc gì nếu không hợp lệ
                        break;
                }
            } else {
                // Nếu không có statusFilter thì lọc theo isActive nếu được truyền
                if (isActive != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("isActive"), isActive));
                }
            }

            return predicates;
        }, pageable);

        return page.map(this::mapToDto);
    }


    @Override
    public AdminPromotionDto getPromotionById(Integer id) {
        AdminPromotions entity = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException("Promotion not found with id: " + id));
        return mapToDto(entity);
    }

    @Override
    public AdminPromotionDto createPromotion(AdminPromotionDto dto) {
        if (promotionRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Code already exists");
        }

        validateDates(dto.getStartDate(), dto.getEndDate());

        AdminPromotions entity = mapToEntity(dto);
        return mapToDto(promotionRepository.save(entity));
    }

    @Override
    public AdminPromotionDto updatePromotion(Integer id, AdminPromotionDto dto) {
        AdminPromotions existing = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException("Promotion not found"));

        if (!existing.getCode().equals(dto.getCode()) &&
                promotionRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Code already exists");
        }

        validateDates(dto.getStartDate(), dto.getEndDate());

        AdminPromotions entity = mapToEntity(dto);
        entity.setId(id);
        return mapToDto(promotionRepository.save(entity));
    }

    @Override
    public void deletePromotion(Integer id) {
        if (!promotionRepository.existsById(id)) {
            throw new PromotionNotFoundException("Promotion not found");
        }
        promotionRepository.deleteById(id);
    }

    @Override
    public AdminPromotionDto togglePromotionStatus(Integer id, Boolean isActive) {
        AdminPromotions promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException("Không tìm thấy khuyến mãi có ID = " + id));

        promotion.setIsActive(isActive);
        promotion.setUpdatedAt(LocalDateTime.now());

        AdminPromotions updated = promotionRepository.save(promotion);
        return mapToDto(updated);
    }

    // ============ PRIVATE METHODS ============

    private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và kết thúc không được để trống");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc");
        }
    }

    private AdminPromotionDto mapToDto(AdminPromotions entity) {
        AdminPromotionDto dto = new AdminPromotionDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setDiscountType(entity.getDiscountType() != null ? entity.getDiscountType().name() : null);
        dto.setDiscountValue(entity.getDiscountValue());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setMaxUsage(entity.getMaxUsage());
        dto.setCurrentUsage(entity.getCurrentUsage());
        dto.setMinOrderValue(entity.getMinOrderValue());
        dto.setTargetTiers(entity.getTargetTiers());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private AdminPromotions mapToEntity(AdminPromotionDto dto) {
        AdminPromotions entity = new AdminPromotions();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        if (dto.getDiscountType() != null) {
            try {
                entity.setDiscountType(AdminPromotions.DiscountType.valueOf(dto.getDiscountType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Loại giảm giá không hợp lệ: " + dto.getDiscountType());
            }
        } else {
            throw new IllegalArgumentException("Loại giảm giá không được để trống");
        }

        entity.setDiscountValue(dto.getDiscountValue());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setMaxUsage(dto.getMaxUsage());
//        entity.setCurrentUsage(dto.getCurrentUsage());
        entity.setCurrentUsage(dto.getCurrentUsage() != null ? dto.getCurrentUsage() : 0);

        entity.setMinOrderValue(dto.getMinOrderValue());
        entity.setTargetTiers(dto.getTargetTiers());
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        entity.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return entity;
    }
}
