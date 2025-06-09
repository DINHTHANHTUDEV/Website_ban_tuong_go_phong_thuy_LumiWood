package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Lấy 5 đơn hàng gần nhất theo user, order theo ngày giảm dần
    List<Order> findTop5ByUserOrderByOrderDateDesc(User user);
}
