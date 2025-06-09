package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingMethodAdminRepository extends JpaRepository<ShippingMethod, Integer> {
}
