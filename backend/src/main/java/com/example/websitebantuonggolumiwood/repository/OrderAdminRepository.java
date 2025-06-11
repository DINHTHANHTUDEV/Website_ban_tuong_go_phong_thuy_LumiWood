package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.OrderAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderAdminRepository extends JpaRepository<OrderAdmin, Integer> {

    @Query("""
SELECT o FROM OrderAdmin o
WHERE (:keyword IS NULL OR
       o.customerName LIKE CONCAT('%', :keyword, '%') OR
       o.customerPhone LIKE CONCAT('%', :keyword, '%') OR
       o.guestEmail LIKE CONCAT('%', :keyword, '%'))
  AND (:status IS NULL OR o.status = :status)
  AND (:startDate IS NULL OR o.orderDate >= :startDate)
  AND (:endDate IS NULL OR o.orderDate <= :endDate)
""")
    Page<OrderAdmin> findAllWithFilters(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

}
