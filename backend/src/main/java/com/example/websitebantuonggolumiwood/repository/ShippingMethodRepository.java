package com.example.OrderHistory.repo;

import com.example.OrderHistory.entity.ShippingMethodOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethodOrder,Integer> {
}
