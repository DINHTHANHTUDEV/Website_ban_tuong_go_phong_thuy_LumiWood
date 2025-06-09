package com.example.websitebantuonggolumiwood.repository;

import com.example.OrderHistory.entity.PromotionOrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<PromotionOrderHistory,Integer> {
}
