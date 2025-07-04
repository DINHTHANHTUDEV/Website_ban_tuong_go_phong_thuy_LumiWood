package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.OrderDetailAdminDTO;
import com.example.websitebantuonggolumiwood.dto.OrderItemAdminDTO;
import com.example.websitebantuonggolumiwood.entity.OrderAdmin;
import com.example.websitebantuonggolumiwood.entity.OrderItemAdmin;
import com.example.websitebantuonggolumiwood.entity.ShippingMethod;
import com.example.websitebantuonggolumiwood.repository.OrderAdminRepository;
import com.example.websitebantuonggolumiwood.repository.ProductRepository;
import com.example.websitebantuonggolumiwood.repository.ShippingMethodAdminRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/orders")
//@CrossOrigin(origins = "http://localhost:5174")
public class OrderAdminController {

    private final OrderAdminRepository orderAdminRepository;
    private final ShippingMethodAdminRepository shippingMethodAdminRepository;
    private final ProductRepository productRepository;

    public OrderAdminController(OrderAdminRepository orderAdminRepository, ShippingMethodAdminRepository shippingMethodAdminRepository, ProductRepository productRepository) {
        this.orderAdminRepository = orderAdminRepository;
        this.shippingMethodAdminRepository = shippingMethodAdminRepository;
        this.productRepository = productRepository;
    }

    // Lấy danh sách đơn hàng, phan trang, sắp xếp, tìm kếm, tìm theo trạng thái, tìm theo ngày tạo
    @GetMapping
    public ResponseEntity<Page<OrderDetailAdminDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate,desc") String sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        PageRequest pageable = PageRequest.of(page, size);

        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        if (keyword != null && !keyword.isBlank()) {
            try {
                Integer idKeyword = Integer.parseInt(keyword.trim());
                Optional<OrderAdmin> optOrder = orderAdminRepository.findById(idKeyword);
                if (optOrder.isPresent()) {
                    List<OrderAdmin> singleOrderList = List.of(optOrder.get());
                    Page<OrderAdmin> pageResult = new org.springframework.data.domain.PageImpl<>(singleOrderList, pageable, 1);
                    Page<OrderDetailAdminDTO> responsePage = pageResult.map(this::mapToOrderDetailResponse);
                    return ResponseEntity.ok(responsePage);
                }
            } catch (NumberFormatException ignored) {
                // Không phải số, bỏ qua tìm theo ID
            }
        }

        String cleanKeyword = (keyword != null && !keyword.isBlank())
                ? keyword.trim().replaceAll("\\s+", " ")
                : null;

        Page<OrderAdmin> orderPage = orderAdminRepository.findAllWithFilters(
                cleanKeyword, status, startDateTime, endDateTime, pageable
        );

        Page<OrderDetailAdminDTO> responsePage = orderPage.map(this::mapToOrderDetailResponse);
        return ResponseEntity.ok(responsePage);
    }

    // Lấy chi tiết đơn hàng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Integer id) {
        Optional<OrderAdmin> optOrder = orderAdminRepository.findByIdWithShippingMethod(id);
        if (optOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Đơn hàng không tồn tại");
        }
        OrderDetailAdminDTO response = mapToOrderDetailResponse(optOrder.get());
        return ResponseEntity.ok(response);
    }

    // DTO cho cập nhật trạng thái
    public static class UpdateStatusRequest {
        private String newStatus;
        private String cancelReason;

        public String getNewStatus() {
            return newStatus;
        }

        public void setNewStatus(String newStatus) {
            this.newStatus = newStatus;
        }

        public String getCancelReason() {
            return cancelReason;
        }

        public void setCancelReason(String cancelReason) {
            this.cancelReason = cancelReason;
        }
    }

    // Cập nhật trạng thái don hàng
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody UpdateStatusRequest request
    ) {
        String newStatus = request.getNewStatus();
        String cancelReason = request.getCancelReason();

        if (newStatus == null || newStatus.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Trạng thái mới không được để trống");
        }

        if ("CANCELLED".equalsIgnoreCase(newStatus) && (cancelReason == null || cancelReason.trim().isEmpty())) {
            return ResponseEntity.badRequest().body("Cần cung cấp lý do hủy đơn hàng");
        }

        Optional<OrderAdmin> optOrder = orderAdminRepository.findByIdWithShippingMethod(id);
        if (optOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Đơn hàng không tồn tại");
        }

        OrderAdmin order = optOrder.get();
        order.setStatus(newStatus);

        if ("CANCELLED".equalsIgnoreCase(newStatus)) {
            order.setCancelReason(cancelReason);
        } else {
            order.setCancelReason(null);
        }

        orderAdminRepository.save(order);

        OrderDetailAdminDTO response = mapToOrderDetailResponse(order);
        return ResponseEntity.ok(response);
    }

    private OrderDetailAdminDTO mapToOrderDetailResponse(OrderAdmin order) {
        List<OrderItemAdminDTO> itemResponses = order.getOrderItemAdmins().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());

        OrderDetailAdminDTO response = new OrderDetailAdminDTO();
        response.setId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setCustomerPhone(order.getCustomerPhone());
        response.setCustomerAddress(order.getCustomerAddress());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setOrderDate(order.getOrderDate());

        response.setDiscountAmount(order.getDiscountAmount());
        response.setUserId(order.getUserId());
        response.setGuestEmail(order.getGuestEmail());

        response.setShippingRecipientName(order.getShippingRecipientName());
        response.setShippingRecipientPhone(order.getShippingRecipientPhone());
        response.setShippingStreetAddress(order.getShippingStreetAddress());
        response.setShippingWard(order.getShippingWard());
        response.setShippingDistrict(order.getShippingDistrict());
        response.setShippingCity(order.getShippingCity());

        response.setShippingMethodId(order.getShippingMethodId());
        response.setShippingCost(order.getShippingCost());

        if (order.getShippingMethod() != null) {
            response.setShippingMethodName(order.getShippingMethod().getName());
        } else {
            response.setShippingMethodName("N/A");
        }

        response.setPaymentMethod(order.getPaymentMethod());
        response.setOrderNote(order.getOrderNote());
        response.setCancelReason(order.getCancelReason());

        response.setDepositAmount(order.getDepositAmount());
        response.setDepositStatus(order.getDepositStatus());

        response.setItems(itemResponses);

        return response;
    }

    // DTO cho tạo đơn hàng
    private OrderItemAdminDTO mapToOrderItemResponse(OrderItemAdmin item) {
        OrderItemAdminDTO itemResponse = new OrderItemAdminDTO();
        itemResponse.setId(item.getId());
        itemResponse.setProductId(item.getProductId());
        itemResponse.setQuantity(item.getQuantity());
        itemResponse.setPriceAtPurchase(item.getPriceAtPurchase());

        // Thêm phần này để lấy ảnh và tên
        productRepository.findById(item.getProductId()).ifPresent(product -> {
            itemResponse.setProductName(product.getName());
            itemResponse.setProductImageUrl(product.getImage_url());
        });

        return itemResponse;
    }

}
