package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.AdminPromotions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdminPromotionRepository extends JpaRepository<AdminPromotions, Integer>,
        JpaSpecificationExecutor<AdminPromotions> {

    boolean existsByCode(String code);
}
