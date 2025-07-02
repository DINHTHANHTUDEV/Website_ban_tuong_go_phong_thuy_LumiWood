package com.example.websitebantuonggolumiwood.repository;


import com.example.websitebantuonggolumiwood.entity.ShippingMethod;
import com.example.websitebantuonggolumiwood.entity.ShippingMethodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingMethodRepository extends JpaRepository<ShippingMethod,Integer> {
    List<ShippingMethod> findByIsActiveTrue(); // Tìm các phương thức vận chuyển đang hoạt động
}
