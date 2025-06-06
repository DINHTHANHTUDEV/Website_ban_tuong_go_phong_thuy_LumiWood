package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.repository.UserRepository;
import com.example.websitebantuonggolumiwood.dto.OrderRecentDTO;
import com.example.websitebantuonggolumiwood.entity.Order;
import com.example.websitebantuonggolumiwood.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;
    /**
     * Lấy danh sách 5 đơn hàng gần nhất của user theo userId.
     * @param userId id của user
     * @return List<OrderRecentDTO>
     */
    public List<OrderRecentDTO> getRecentOrdersByUserId(Long userId) {
        // Lấy User entity từ UserRepository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        // Lấy 5 đơn hàng gần nhất
        List<Order> orders = orderRepository.findTop5ByUserOrderByOrderDateDesc(user);
        // Mapping sang DTO, tự tạo code từ id
        return orders.stream()
                .map(order -> new OrderRecentDTO(
                        order.getId(),
                        "#MU-" + String.format("%04d", order.getId()), // code ví dụ
                        order.getOrderDate(),
                        order.getStatus(),
                        order.getTotalAmount()
                ))
                .collect(Collectors.toList());
    }
}
