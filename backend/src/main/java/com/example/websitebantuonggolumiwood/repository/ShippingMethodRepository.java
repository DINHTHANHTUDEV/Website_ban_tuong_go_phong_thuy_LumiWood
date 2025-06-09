package com.example.websitebantuonggolumiwood.repository;


import com.example.OrderHistory.entity.ShippingMethodOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethodOrder,Integer> {
}
