    package com.example.websitebantuonggolumiwood.repository;

    import com.example.websitebantuonggolumiwood.entity.PromotionOrderHistory;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Optional;

    public interface PromotionRepository extends JpaRepository<PromotionOrderHistory,Integer> {

        Optional<PromotionOrderHistory> findByCodeAndIsActiveTrue(String code);
        @Query("SELECT p FROM PromotionOrderHistory p " +
                "WHERE p.startDate <= :now " +
                "AND p.endDate >= :now " +
                "AND p.isActive = true " +
                "AND LOWER(p.targetTiers) = LOWER(:rank) OR p.targetTiers IS NULL " +
                "AND (p.maxUsage IS NULL OR p.currentUsage < p.maxUsage)")
        List<PromotionOrderHistory> findValidPromotions(@Param("now") LocalDateTime now,
                                                        @Param("rank") String rank);


    }
