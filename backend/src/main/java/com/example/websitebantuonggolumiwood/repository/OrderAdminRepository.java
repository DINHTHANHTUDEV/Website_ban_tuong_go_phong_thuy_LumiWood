package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.OrderAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderAdminRepository extends JpaRepository<OrderAdmin, Integer> {

    @Query(value = """
        SELECT * FROM orders o
        WHERE (:keyword IS NULL OR 
               o.customer_name COLLATE SQL_Latin1_General_CP1_CI_AI LIKE CONCAT('%', :keyword, '%') OR
               o.customer_phone COLLATE SQL_Latin1_General_CP1_CI_AI LIKE CONCAT('%', :keyword, '%') OR
               o.guest_email COLLATE SQL_Latin1_General_CP1_CI_AI LIKE CONCAT('%', :keyword, '%'))
          AND (:status IS NULL OR o.status = :status)
          AND (:startDate IS NULL OR o.order_date >= :startDate)
          AND (:endDate IS NULL OR o.order_date <= :endDate)
        ORDER BY o.order_date DESC
        """,
            countQuery = """
        SELECT COUNT(*) FROM orders o
        WHERE (:keyword IS NULL OR 
               o.customer_name COLLATE SQL_Latin1_General_CP1_CI_AI LIKE CONCAT('%', :keyword, '%') OR
               o.customer_phone COLLATE SQL_Latin1_General_CP1_CI_AI LIKE CONCAT('%', :keyword, '%') OR
               o.guest_email COLLATE SQL_Latin1_General_CP1_CI_AI LIKE CONCAT('%', :keyword, '%'))
          AND (:status IS NULL OR o.status = :status)
          AND (:startDate IS NULL OR o.order_date >= :startDate)
          AND (:endDate IS NULL OR o.order_date <= :endDate)
        """,
            nativeQuery = true)
    Page<OrderAdmin> findAllWithFilters(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

}
