package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.NotificationDTO;
import com.example.websitebantuonggolumiwood.dto.OrderDetailAdminDTO;
import com.example.websitebantuonggolumiwood.dto.OrderItemAdminDTO;
import com.example.websitebantuonggolumiwood.entity.OrderAdmin;
import com.example.websitebantuonggolumiwood.entity.OrderItemAdmin;
import com.example.websitebantuonggolumiwood.entity.ShippingMethod;
import com.example.websitebantuonggolumiwood.repository.OrderAdminRepository;
import com.example.websitebantuonggolumiwood.repository.ProductRepository;
import com.example.websitebantuonggolumiwood.repository.ShippingMethodAdminRepository;
import com.example.websitebantuonggolumiwood.service.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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

    private static final Logger logger = LoggerFactory.getLogger(OrderAdminController.class);


    private final UserManagementService userManagementService;
    private final OrderAdminRepository orderAdminRepository;
    private final ShippingMethodAdminRepository shippingMethodAdminRepository;
    private final ProductRepository productRepository;
    private  final SimpMessagingTemplate simpMessagingTemplate;

    public OrderAdminController(UserManagementService userManagementService, OrderAdminRepository orderAdminRepository, ShippingMethodAdminRepository shippingMethodAdminRepository, ProductRepository productRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.userManagementService = userManagementService;
        this.orderAdminRepository = orderAdminRepository;
        this.shippingMethodAdminRepository = shippingMethodAdminRepository;
        this.productRepository = productRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
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
        String oldStatus = order.getStatus(); // lưu trạng thái cũ

        // Cập nhật trạng thái mới
        order.setStatus(newStatus.trim().toUpperCase());


        if ("CANCELLED".equalsIgnoreCase(newStatus)) {
            order.setCancelReason(cancelReason);
        } else {
            order.setCancelReason(null);
        }

        // Lưu thay đổi vào DB
        orderAdminRepository.save(order);
        logger.info("ADMIN cập nhật trạng thái đơn hàng ID {}: {} → {}", id, oldStatus, newStatus);

        // === Cập nhật tổng chi tiêu và bậc khách hàng nếu đơn hàng hoàn tất ===
        if ("COMPLETED".equalsIgnoreCase(newStatus) &&
                (oldStatus == null || !"COMPLETED".equalsIgnoreCase(oldStatus))) {
            Long userId = order.getUserId();
            BigDecimal totalAmount = order.getTotalAmount();

            if (userId != null && totalAmount != null) {
                try {
                    logger.info("Đơn hàng ID {} đã hoàn tất. Cập nhật tổng chi tiêu và bậc cho user ID: {}", id, userId);
                    userManagementService.updateTotalSpentAndTier(userId, totalAmount);
                } catch (Exception ex) {
                    logger.error("Lỗi khi cập nhật tổng chi tiêu/bậc cho user ID {} sau khi hoàn tất đơn hàng ID {}: {}",
                            userId, id, ex.getMessage(), ex);
                    // Không throw để tránh rollback transaction đơn hàng
                }
            } else {
                logger.warn("Không thể cập nhật tổng chi tiêu: userId hoặc totalAmount bị null (order ID: {})", id);
            }
        }

        // === Gửi thông báo WebSocket tới người dùng ===
        try {
            Long userId = order.getUserId();
            if (userId != null) {
                String orderCode = "Đơn hàng #" + order.getId(); // dùng làm mã đơn hàng hiển thị

                // Xử lý nội dung thông báo dựa theo trạng thái
                String messageContent;
                BigDecimal total = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;

                switch (newStatus.toUpperCase()) {
                    case "PENDING":
                        messageContent = orderCode + " đang chờ xác nhận.";
                        break;
                    case "PAYMENT_PENDING":
                        messageContent = orderCode + " đang chờ thanh toán.";
                        break;
                    case "PROCESSING":
                        messageContent = total.compareTo(new BigDecimal("10000000")) >= 0
                                ? orderCode + ": Đã nhận được cọc 30%. Đơn hàng đang được xử lý."
                                : orderCode + " đang được xử lý.";
                        break;
                    case "SHIPPING":
                        messageContent = orderCode + " đang được giao đến bạn.";
                        break;
                    case "DELIVERY_FAILED":
                        messageContent = orderCode + " giao không thành công. Vui lòng liên hệ hỗ trợ."
                        + (order.getCancelReason() != null ? " Lý do: " + order.getCancelReason() : "");;

                        break;
                    case "COMPLETED":
                        messageContent = total.compareTo(new BigDecimal("10000000")) >= 0
                                ? orderCode + " đã hoàn tất. Cảm ơn bạn đã đặt cọc và mua hàng!"
                                : orderCode + " đã hoàn tất. Cảm ơn bạn đã mua hàng!";
                        break;
                    case "CANCELLED":
                        messageContent = orderCode + " đã bị hủy." +
                                (order.getCancelReason() != null ? " Lý do: " + order.getCancelReason() : "");
                        break;
                    default:
                        messageContent = orderCode + ": Trạng thái đơn hàng đã được cập nhật thành " + newStatus + ".";
                }

                // Tạo đối tượng NotificationDTO để gửi WebSocket
                NotificationDTO notification = new NotificationDTO();
                notification.setTitle("Cập nhật đơn hàng");
                notification.setContent(messageContent);
                notification.setOrderId(order.getId());
                notification.setCreatedTime(LocalDateTime.now());

                // 🔑 Gửi tới người dùng có đơn hàng này
                String username = userManagementService.getUsernameById(userId);
                if (username != null) {
                    simpMessagingTemplate.convertAndSendToUser(
                            username,
                            "/queue/notify",
                            notification
                    );
                    logger.info("✅ Đã gửi thông báo tới USER: userId={}, username={}, content={}", userId, username, messageContent);
                } else {
                    logger.warn("⚠️ Không tìm thấy username từ userId {} → Không gửi được thông báo WebSocket", userId);
                }

                // 🔔 Gửi thông báo tới admin
                NotificationDTO adminNotification = new NotificationDTO();
                adminNotification.setTitle("Đơn hàng được cập nhật");
                adminNotification.setContent("Trạng thái đơn hàng #" + order.getId() + " đã thay đổi thành: " + newStatus);
                adminNotification.setOrderId(order.getId());
                adminNotification.setCreatedTime(LocalDateTime.now());

                simpMessagingTemplate.convertAndSend("/topic/notify/admin", adminNotification);
                logger.info("✅ Đã gửi thông báo tới ADMIN: {}", adminNotification.getContent());

            } else {
                logger.warn("⚠️ Không gửi được thông báo vì order không có userId (orderId: {})", order.getId());
            }
        } catch (Exception e) {
            logger.error("❌ Lỗi khi gửi thông báo WebSocket cho đơn hàng ID {}: {}", order.getId(), e.getMessage(), e);
        }


        // ================================================================

        OrderDetailAdminDTO response = mapToOrderDetailResponse(order);
        return ResponseEntity.ok(response);
    }


    //  chuyển đổi order thành orderDetailAdminDTO
    // de xu ly du lieu tra ve cho frontend lay danh sach don hang cho admin
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

    // chuyển đổi orderItem thành orderItemAdminDTO
    //de xu ly du lieu tra ve cho frontend lay chi tiet san phẩm trong đơn hàng
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
