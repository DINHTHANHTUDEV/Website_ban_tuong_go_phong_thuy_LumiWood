package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.dto.OrderDto;
import com.example.websitebantuonggolumiwood.dto.OrderItemDto;
import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.repository.UserRepository;
import com.example.websitebantuonggolumiwood.dto.OrderRecentDTO;
import com.example.websitebantuonggolumiwood.entity.Order;
import com.example.websitebantuonggolumiwood.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
//                        "#MU-" + String.format("%04d", order.getId()), // code ví dụ
                        order.getOrderDate(),
                        order.getStatus(),
                        order.getTotalAmount()
                ))
                .collect(Collectors.toList());
    }

    // Lấy danh sách đơn hàng theo userId với phân trang, trả về DTO
    public Page<OrderDto> getOrdersHistoryByUserId(Long userId, int page, int size) {
        // Lấy danh sách đơn hàng theo userId và phân trang
        Page<Order> ordersPage = orderRepository.findByUser_UserId(userId, PageRequest.of(page, size));

        // Chuyển từng Order thành OrderDto
        return ordersPage.map(order -> {
            OrderDto dto = new OrderDto();
            dto.setId(order.getId());

            // Kiểm tra User null trước khi lấy userId
            dto.setUserId(order.getUser() != null ? order.getUser().getUserId() : null);

            dto.setCustomerName(order.getCustomerName());
            dto.setCustomerPhone(order.getCustomerPhone());
            dto.setCustomerAddress(order.getCustomerAddress());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setStatus(order.getStatus());
            dto.setOrderDate(order.getOrderDate());
            dto.setPaymentMethod(order.getPaymentMethod());

            // Đảm bảo orderItems không null
            if (order.getOrderItems() != null) {
                dto.setOrderItems(order.getOrderItems().stream().map(item -> {
                    OrderItemDto itemDto = new OrderItemDto();
                    itemDto.setProductId(item.getProductId());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setPriceAtPurchase(item.getPriceAtPurchase());

                    // Nếu product không null thì mới set tên và ảnh
                    if (item.getProduct() != null) {
                        itemDto.setProductName(item.getProduct().getName());
                        itemDto.setProductImage(item.getProduct().getImageUrl());
                    }

                    return itemDto;
                }).collect(Collectors.toList()));
            } else {
                dto.setOrderItems(Collections.emptyList()); // Nếu null thì trả list rỗng
            }

            return dto;
        });
    }

}
