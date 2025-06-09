package com.example.websitebantuonggolumiwood.repository;


import com.example.OrderHistory.entity.OrdersHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryRepository extends JpaRepository<OrdersHistory,Integer> {
}
