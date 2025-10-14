package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // lay don hang gan nhat theo user
     Optional<Order> findTopByUserOrderByOrderDateDesc(User user) ;


    // Lấy 5 đơn hàng gần nhất theo user, order theo ngày giảm dần
    List<Order> findTop5ByUserOrderByOrderDateDesc(User user);

    // Lấy danh sách đơn hàng theo userId (quan hệ User) với phân trang
    Page<Order> findByUser_UserId(Long userId, Pageable pageable);


}
