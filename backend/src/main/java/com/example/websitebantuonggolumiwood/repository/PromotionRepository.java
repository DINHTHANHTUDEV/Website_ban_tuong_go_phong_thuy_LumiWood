package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

    Optional<Promotion> findByCodeAndIsActiveTrue(String code);

    @Query("SELECT p FROM Promotion p " +
            "WHERE p.startDate <= :now " +
            "AND p.endDate >= :now " +
            "AND p.isActive = true " +
            "AND (LOWER(p.targetTiers) = LOWER(:rank) OR p.targetTiers IS NULL) " +
            "AND (p.maxUsage IS NULL OR p.currentUsage < p.maxUsage)")
    List<Promotion> findValidPromotions(@Param("now") LocalDateTime now,
                                        @Param("rank") String rank);

    /**
     * Tìm mã khuyến mãi dựa trên mã code không phân biệt chữ hoa/thường và kiểm tra trạng thái hoạt động
     * @param code Mã khuyến mãi cần tìm
     * @return Optional chứa Promotion nếu tìm thấy, hoặc empty nếu không
     */
    @Query("SELECT p FROM Promotion p WHERE LOWER(p.code) = LOWER(:code) AND p.isActive = true")
    Optional<Promotion> findByCodeIgnoreCase(@Param("code") String code);
}