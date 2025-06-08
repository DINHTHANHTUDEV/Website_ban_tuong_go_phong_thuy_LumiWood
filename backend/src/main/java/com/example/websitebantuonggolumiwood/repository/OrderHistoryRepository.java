package com.example.OrderHistory.repo;

import com.example.OrderHistory.entity.OrdersHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryRepository extends JpaRepository<OrdersHistory,Integer> {
}
