package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.OrderItemAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemAdminRepository extends JpaRepository<OrderItemAdmin, Integer> {
    List<OrderItemAdmin> findByOrderId(Integer orderId);
}
