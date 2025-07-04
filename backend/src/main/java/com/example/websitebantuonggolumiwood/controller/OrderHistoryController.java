package com.example.websitebantuonggolumiwood.controller;


import com.example.websitebantuonggolumiwood.dto.OrderDto;
import com.example.websitebantuonggolumiwood.dto.OrderItemDto;
import com.example.websitebantuonggolumiwood.dto.UserOrderDetailDTO;
import com.example.websitebantuonggolumiwood.entity.Order;
import com.example.websitebantuonggolumiwood.repository.OrderRepository;
import com.example.websitebantuonggolumiwood.security.model.UserPrincipal;
import com.example.websitebantuonggolumiwood.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
//@CrossOrigin(origins = "http://localhost:5173")  // Kết nối FE
public class OrderHistoryController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    // 1. Lấy danh sách đơn hàng có phân trang, lay theo userId dang nhap tu authentication
    @GetMapping("/getOrdersHistory")
    public Page<OrderDto> getAllOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        // Lấy userId từ authentication
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getUserId();

        // Gọi service lấy danh sách đơn hàng theo userId với phân trang
        return orderService.getOrdersHistoryByUserId(userId, page, size);
    }

    // 2. Lấy chi tiết đơn hàng theo ID
    @GetMapping("/getOrdersDetail/{id}")
    public ResponseEntity<UserOrderDetailDTO> getOrderDetail(@PathVariable Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);

        if (optionalOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = optionalOrder.get();
        UserOrderDetailDTO dto = new UserOrderDetailDTO();

        // Thông tin đơn hàng
        dto.setOrderId(order.getId().longValue()); // ép sang Long nếu DTO dùng Long
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setOrderNote(order.getOrderNote());

        // Thông tin người nhận và vận chuyển
        dto.setShippingRecipientName(order.getShippingRecipientName());
        dto.setShippingRecipientPhone(order.getShippingRecipientPhone());
        dto.setShippingStreetAddress(order.getShippingStreetAddress());
        dto.setShippingWard(order.getShippingWard());
        dto.setShippingDistrict(order.getShippingDistrict());
        dto.setShippingCity(order.getShippingCity());

        if (order.getShippingMethod() != null) {
            dto.setShippingMethodName(order.getShippingMethod().getName());
            dto.setShippingCost(order.getShippingCost());
        }

        if (order.getPromotion() != null) {
            dto.setPromotionCode(order.getPromotion().getCode());
            dto.setDiscountAmount(order.getDiscountAmount());
        }

        // Tổng tiền và chi tiết món hàng
        dto.setTotalAmount(order.getTotalAmount());
        dto.setSubtotal(order.getTotalAmount().subtract(
                order.getDiscountAmount() == null ? BigDecimal.ZERO : order.getDiscountAmount()
        ));

// 🔽 🔽 🔽 THÊM ĐOẠN NÀY SAU KHI SET SUBTOTAL
        BigDecimal threshold = new BigDecimal("10000000");
        if (dto.getSubtotal().compareTo(threshold) > 0) {
            BigDecimal deposit = dto.getSubtotal().multiply(new BigDecimal("0.3"))
                    .setScale(0, RoundingMode.HALF_UP);
            BigDecimal remaining = dto.getSubtotal().subtract(deposit);

            dto.setDepositAmount(deposit);
            dto.setRemainingAmount(remaining);
        }
// 🔼 🔼 🔼 HẾT ĐOẠN CẦN THÊM

        List<UserOrderDetailDTO.OrderItemDTO> itemDTOs = order.getOrderItems().stream().map(item -> {
            UserOrderDetailDTO.OrderItemDTO itemDto = new UserOrderDetailDTO.OrderItemDTO();
            itemDto.setProductId(item.getProductId().longValue());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPriceAtPurchase(item.getPriceAtPurchase());

            if (item.getProduct() != null) {
                itemDto.setProductName(item.getProduct().getName());
                itemDto.setProductImageUrl(item.getProduct().getImage_url());
            }

            return itemDto;
        }).toList();

        dto.setItems(itemDTOs);

        return ResponseEntity.ok(dto);
    }


}
