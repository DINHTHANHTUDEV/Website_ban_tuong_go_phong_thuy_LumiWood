package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.dto.*;
import com.example.websitebantuonggolumiwood.entity.*;
import com.example.websitebantuonggolumiwood.exception.BadRequestException;
import com.example.websitebantuonggolumiwood.exception.ResourceNotFoundException;
import com.example.websitebantuonggolumiwood.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Dịch vụ xử lý các thao tác liên quan đến quy trình thanh toán và đặt hàng
 */
@Service
@RequiredArgsConstructor
public class CheckoutService {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutService.class);
    private static final String CART_SESSION_HEADER = "X-Cart-Session-Id";

    // --- Các dependency được tiêm vào ---
    private final CartService cartService; // Dịch vụ quản lý giỏ hàng
    private final PromotionService promotionService; // Dịch vụ quản lý khuyến mãi
    private final ProductRepository productRepository; // Repository cho sản phẩm
    private final OrderRepository orderRepository; // Repository cho đơn hàng
    private final UserRepository userRepository; // Repository cho người dùng
    private final AddressRepository addressRepository; // Repository cho địa chỉ
    private final ShippingMethodRepository shippingMethodRepository; // Repository cho phương thức vận chuyển
    private final PromotionRepository promotionRepository; // Repository cho khuyến mãi

    /**
     * Lớp nội bộ để lưu thông tin khuyến mãi áp dụng
     */
    @RequiredArgsConstructor
    @Getter
    private static class PromotionDetails {
        private final BigDecimal discount;
        private final Promotion promotion;
    }

    /**
     * Lấy thông tin kiểm tra trước khi đặt hàng
     * @param username Tên người dùng đã đăng nhập
     * @return CheckoutInfoDTO chứa danh sách địa chỉ và phương thức vận chuyển
     * @throws ResourceNotFoundException Nếu không tìm thấy người dùng
     */
    @Transactional // Giao dịch không yêu cầu readOnly để đơn giản hóa, chỉ đọc dữ liệu
    public CheckoutInfoDTO getCheckoutInfo(String username) {
        logger.info("Retrieving checkout information for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        CheckoutInfoDTO info = new CheckoutInfoDTO();
        // Lấy tất cả địa chỉ của người dùng từ repository
        List<Address> addresses = addressRepository.findByUser_UserId(user.getUserId().longValue());
        info.setSavedAddresses(addresses.stream().map(this::convertToAddressDTO).toList());

        // Lấy địa chỉ mặc định nếu có
        info.setDefaultShippingAddress(addresses.stream()
                .filter(Address::getIsDefaultShipping)
                .findFirst()
                .map(this::convertToAddressDTO)
                .orElse(null));

        // Lấy các phương thức vận chuyển đang hoạt động
        List<ShippingMethod> shippingMethods = shippingMethodRepository.findByIsActiveTrue();
        info.setAvailableShippingMethods(shippingMethods.stream().map(this::convertToShippingMethodDTO).toList());

        return info;
    }

    /**
     * Xử lý đặt hàng dựa trên yêu cầu từ client
     * @param request Dữ liệu yêu cầu đặt hàng
     * @param username Tên người dùng (null nếu là guest)
     * @return OrderSummaryDTO chứa thông tin tóm tắt đơn hàng
     * @throws BadRequestException Nếu có lỗi trong dữ liệu hoặc logic
     * @throws ResourceNotFoundException Nếu không tìm thấy tài nguyên
     */
    @Transactional
    public OrderSummaryDTO placeOrder(PlaceOrderRequestDTO request, String username) {
        logger.info("Processing order for user: {}, request: {}", username, request);

        try {
            // 1. Xác định người dùng hoặc xử lý cho guest
            User user = username != null ? userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", username)) : null;
            validateGuestRequest(request, user);

            // 2. Lấy sessionId từ header (ưu tiên header X-Cart-Session-Id)
            String sessionId = extractCartSessionId();
            logger.debug("Using sessionId from header: {}", sessionId);

            // 3. Lấy giỏ hàng hiện tại từ CartService với cart_id mới nhất
// Sử dụng user hoặc sessionId để đảm bảo lấy đúng Cart mới nhất
            Cart cart = cartService.getOrCreateCart(user, sessionId); // Truyền user thay vì user.getUserId()
            logger.debug("Using cart_id: {} for order processing", cart.getId());

            // 4. Lấy danh sách CartItem từ repository để đảm bảo đồng bộ
            List<CartItem> cartItems = itemRepo.findByCart_Id(cart.getId());
            if (cartItems.isEmpty()) {
                throw new BadRequestException("Giỏ hàng trống, không thể đặt hàng.");
            }

            // 5. Kiểm tra tính hợp lệ của sản phẩm trong giỏ
            Map<Integer, Product> availableProducts = validateCartProducts(cartItems);

            // 6. Xác nhận phương thức vận chuyển
            ShippingMethod shippingMethod = shippingMethodRepository.findById(request.getShippingMethodId())
                    .filter(ShippingMethod::getIsActive)
                    .orElseThrow(() -> new ResourceNotFoundException("Shipping Method", "id", request.getShippingMethodId()));
            BigDecimal shippingFee = shippingMethod.getBaseCost();

            // 7. Tính tổng tiền sản phẩm
            BigDecimal subTotal = cartService.calculateSubtotalForCart(user, sessionId); // Truyền user thay vì user.getUserId()
            logger.debug("Subtotal calculated: {}", subTotal);

            // 8. Áp dụng mã khuyến mãi nếu có
            PromotionDetails promoDetails = new PromotionDetails(BigDecimal.ZERO, null);
            if (request.getAppliedPromotionCode() != null && !request.getAppliedPromotionCode().trim().isEmpty()) {
                try {
                    promoDetails = applyPromotion(request.getAppliedPromotionCode(), subTotal, user);
                    logger.debug("Applied promotion discount: {}, promo ID: {}",
                            promoDetails.getDiscount(),
                            promoDetails.getPromotion() != null ? promoDetails.getPromotion().getId() : "none");
                } catch (Exception e) {
                    logger.error("Error applying promotion: {}", e.getMessage());
                    throw new BadRequestException("Lỗi khi áp dụng mã khuyến mãi: " + e.getMessage());
                }
            }

            // 9. Tính tổng tiền cuối cùng
            BigDecimal totalAmount = computeTotalAmount(subTotal, shippingFee,
                    promoDetails != null ? promoDetails.getDiscount() : BigDecimal.ZERO);
            logger.debug("Final total amount: {}", totalAmount);

            // 10. Tính toán số tiền đặt cọc nếu tổng tiền ≥ 10 triệu
            BigDecimal depositAmount = BigDecimal.ZERO;
            if (totalAmount.compareTo(new BigDecimal("10000000")) >= 0) {
                depositAmount = totalAmount.multiply(new BigDecimal("0.30"))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            // 11. Tạo và cấu hình đơn hàng
            Order order = new Order();
            order.setUser(user);
            order.setTotalAmount(totalAmount);
            order.setShippingMethod(shippingMethod);

            order.setShippingMethodId(shippingMethod.getId()); // KHÔNG lỗi nữa ✅

            order.setShippingCost(shippingFee);
            order.setPaymentMethod(request.getPaymentMethod());
            order.setStatus("PENDING");
            order.setOrderNote(request.getOrderNote());
            order.setDiscountAmount(promoDetails != null ? promoDetails.getDiscount() : BigDecimal.ZERO);
            order.setPromotion(promoDetails != null ? promoDetails.getPromotion() : null);
            order.setOrderDate(LocalDateTime.now());
            order.setDepositAmount(depositAmount);
            order.setDepositStatus("PENDING");

            // 12. Gán địa chỉ giao hàng
            assignShippingAddress(order, request, user);
            if (user == null && request.getShippingAddressInput() != null) {
                order.setGuestEmail(request.getShippingAddressInput().getRecipientEmail());
            }

            // 13. Tạo danh sách sản phẩm và cập nhật tồn kho
            Set<OrderItem> orderItems = createOrderItems(order, cartItems, availableProducts);
            order.setOrderItems(new ArrayList<>(orderItems));

            // 14. Lưu đơn hàng vào cơ sở dữ liệu
            Order savedOrder = orderRepository.save(order);
            logger.info("Order saved successfully with ID: {}", savedOrder.getId());

            // 15. Cập nhật sử dụng khuyến mãi
            if (promoDetails != null && promoDetails.getPromotion() != null) {
                try {
                    promotionService.recordPromotionUsage(promoDetails.getPromotion().getId());
                } catch (Exception e) {
                    logger.error("Error recording promotion usage: {}", e.getMessage());
                    // Không throw lỗi ở đây vì đơn hàng đã được tạo thành công
                }
            }

            // 16. Xóa giỏ hàng sau khi đặt thành công
            try {
                cartService.clearCart(user, sessionId); // Truyền user thay vì user.getUserId()
                logger.debug("Cart cleared successfully");
            } catch (Exception e) {
                logger.error("Error clearing cart: {}", e.getMessage());
                // Không throw lỗi ở đây vì đơn hàng đã được tạo thành công
            }
            // 17. Trả về thông tin đơn hàng
            return OrderSummaryDTO.builder()
                    .orderId(savedOrder.getId())
                    .orderStatus(savedOrder.getStatus())
                    .orderDate(savedOrder.getOrderDate())
                    .finalAmount(savedOrder.getTotalAmount())
                    .depositAmount(savedOrder.getDepositAmount())
                    .depositStatus(savedOrder.getDepositStatus())
                    .paymentMethod(savedOrder.getPaymentMethod())
                    .successMessage("Đơn hàng #" + savedOrder.getId() + " đã được đặt thành công!")
                    .build();

        } catch (ResourceNotFoundException | BadRequestException e) {
            logger.error("Validation error in placeOrder: {}", e.getMessage());
            throw e; // Re-throw các lỗi đã được xử lý
        } catch (Exception e) {
            logger.error("Unexpected error in placeOrder: {}", e.getMessage(), e);
            throw new BadRequestException("Đã xảy ra lỗi khi xử lý đơn hàng. Vui lòng thử lại sau.");
        }
    }

    // Helper methods
    private User findUserByUsernameOrThrow(String username) {
        // Tìm người dùng theo username, ném exception nếu không tìm thấy
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private void validateGuestRequest(PlaceOrderRequestDTO request, User user) {
        // Xác thực dữ liệu cho khách (guest) khi không có user
        if (user == null) {
            if (request.getShippingAddressInput() == null) {
                throw new BadRequestException("Vui lòng cung cấp thông tin địa chỉ giao hàng cho khách.");
            }
            PlaceOrderRequestDTO.ShippingAddressInput address = request.getShippingAddressInput();
            if (address.getRecipientEmail() == null || address.getRecipientEmail().trim().isEmpty()) {
                throw new BadRequestException("Email người nhận là bắt buộc cho khách.");
            }
            if (address.getRecipientName() == null || address.getRecipientName().trim().isEmpty()) {
                throw new BadRequestException("Tên người nhận không được để trống.");
            }
            if (address.getRecipientPhone() == null || address.getRecipientPhone().trim().isEmpty()) {
                throw new BadRequestException("Số điện thoại người nhận không được để trống.");
            }
            if (address.getStreetAddress() == null || address.getStreetAddress().trim().isEmpty()) {
                throw new BadRequestException("Địa chỉ chi tiết không được để trống.");
            }
            if (address.getDistrict() == null || address.getDistrict().trim().isEmpty()) {
                throw new BadRequestException("Quận/Huyện không được để trống.");
            }
            if (address.getCity() == null || address.getCity().trim().isEmpty()) {
                throw new BadRequestException("Tỉnh/Thành phố không được để trống.");
            }
        }
    }

    private Map<Integer, Product> validateCartProducts(List<CartItem> cartItems) {
        // Kiểm tra tính hợp lệ của các sản phẩm trong giỏ hàng
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BadRequestException("Giỏ hàng không có sản phẩm.");
        }
        Set<Integer> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .collect(Collectors.toSet());
        List<Product> products = productRepository.findAllById(productIds);
        Map<Integer, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (CartItem item : cartItems) {
            Product product = productMap.get(item.getProductId());
            if (product == null) {
                throw new BadRequestException("Sản phẩm với ID " + item.getProductId() + " không tồn tại.");
            }
            if (!Boolean.TRUE.equals(product.getIsActive())) {
                throw new BadRequestException("Sản phẩm '" + product.getName() + "' không còn hoạt động.");
            }
            if (product.getStock() == null || product.getStock() < item.getQuantity()) {
                throw new BadRequestException("Sản phẩm '" + product.getName() + "' không đủ tồn kho. Còn: " +
                        (product.getStock() != null ? product.getStock() : 0) + ", Yêu cầu: " + item.getQuantity());
            }
        }
        return productMap;
    }

    private PromotionDetails applyPromotion(String promoCode, BigDecimal subTotal, User user) {
        // Áp dụng mã khuyến mãi nếu có
        if (promoCode == null || promoCode.trim().isEmpty()) {
            return new PromotionDetails(BigDecimal.ZERO, null);
        }
        Promotion promotion = promotionRepository.findByCodeIgnoreCase(promoCode)
                .orElseThrow(() -> new BadRequestException("Mã khuyến mãi '" + promoCode + "' không hợp lệ."));
        LocalDateTime now = LocalDateTime.now(); // 06:31 PM +07, 02/07/2025
        if (!Boolean.TRUE.equals(promotion.getIsActive())) {
            throw new BadRequestException("Mã khuyến mãi '" + promoCode + "' không còn hoạt động.");
        }
        if (now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
            throw new BadRequestException("Mã khuyến mãi '" + promoCode + "' đã hết hạn.");
        }
        if (promotion.getMaxUsage() != null && promotion.getCurrentUsage() >= promotion.getMaxUsage()) {
            throw new BadRequestException("Mã khuyến mãi '" + promoCode + "' đã hết lượt sử dụng.");
        }
        if (promotion.getMinOrderValue() != null && subTotal.compareTo(promotion.getMinOrderValue()) < 0) {
            throw new BadRequestException("Đơn hàng chưa đạt giá trị tối thiểu " + promotion.getMinOrderValue());
        }
        if (promotion.getTargetTiers() != null && !promotion.getTargetTiers().isEmpty()) {
            if (user == null || user.getTier() == null) {
                throw new BadRequestException("Mã khuyến mãi chỉ áp dụng cho thành viên có hạng.");
            }
            if (!Arrays.asList(promotion.getTargetTiers().toUpperCase().split(",")).contains(user.getTier().toUpperCase())) {
                throw new BadRequestException("Hạng '" + user.getTier() + "' không đủ điều kiện cho mã này.");
            }
        }
        BigDecimal discount = calculateDiscount(promotion, subTotal);
        return new PromotionDetails(discount, promotion);
    }

    private BigDecimal computeTotalAmount(BigDecimal subTotal, BigDecimal shippingFee, BigDecimal discount) {
        // Tính tổng tiền cuối cùng, đảm bảo không âm
        BigDecimal amount = subTotal.add(shippingFee).subtract(discount);
        return amount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : amount;
    }

    private void assignShippingAddress(Order order, PlaceOrderRequestDTO request, User user) {
        // Gán địa chỉ giao hàng từ địa chỉ đã lưu hoặc thông tin mới
        if (request.getSelectedShippingAddressId() != null) {
            if (user == null) {
                throw new BadRequestException("Khách không thể sử dụng địa chỉ đã lưu.");
            }
            Address address = addressRepository.findById(request.getSelectedShippingAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "id", request.getSelectedShippingAddressId()));
            if (!address.getUser().getUserId().equals(user.getUserId())) {
                throw new BadRequestException("Địa chỉ không thuộc về người dùng này.");
            }
            order.setShippingRecipientName(address.getRecipientName());
            order.setShippingRecipientPhone(address.getRecipientPhone());
            order.setShippingStreetAddress(address.getStreetAddress());
            order.setShippingWard(address.getWard());
            order.setShippingDistrict(address.getDistrict());
            order.setShippingCity(address.getCity());
        } else if (request.getShippingAddressInput() != null) {
            PlaceOrderRequestDTO.ShippingAddressInput input = request.getShippingAddressInput();
            order.setShippingRecipientName(input.getRecipientName());
            order.setShippingRecipientPhone(input.getRecipientPhone());
            order.setShippingStreetAddress(input.getStreetAddress());
            order.setShippingWard(input.getWard());
            order.setShippingDistrict(input.getDistrict());
            order.setShippingCity(input.getCity());
        } else {
            throw new BadRequestException("Vui lòng cung cấp địa chỉ giao hàng.");
        }
        order.setCustomerName(order.getShippingRecipientName());
        order.setCustomerPhone(order.getShippingRecipientPhone());
        order.setCustomerAddress(buildFullAddress(order));
    }

    private Set<OrderItem> createOrderItems(Order order, List<CartItem> cartItems, Map<Integer, Product> products) {
        // Tạo danh sách sản phẩm trong đơn hàng và cập nhật tồn kho
        Set<OrderItem> items = new HashSet<>();
        for (CartItem cartItem : cartItems) {
            Product product = products.get(cartItem.getProductId());
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException("Sản phẩm '" + product.getName() + "' không đủ tồn kho.");
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());
            items.add(orderItem);
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product); // Lưu thay đổi tồn kho
        }
        return items;
    }

    private AddressDTO convertToAddressDTO(Address address) {
        // Chuyển đổi entity Address thành DTO
        if (address == null) return null;
        AddressDTO dto = new AddressDTO();
        BeanUtils.copyProperties(address, dto, "id", "user"); // Loại bỏ id và user để tránh tham chiếu
        dto.setId(address.getId());
        return dto;
    }

    private ShippingMethodDTO convertToShippingMethodDTO(ShippingMethod method) {
        // Chuyển đổi entity ShippingMethod thành DTO
        if (method == null) return null;
        ShippingMethodDTO dto = new ShippingMethodDTO();
        BeanUtils.copyProperties(method, dto);
        // dto.setEstimatedDelivery(estimateDeliveryTime(method));
        return dto;
    }

    private String extractCartSessionId() {
        // Lấy ID phiên từ header hoặc tạo mới nếu không có
        HttpServletRequest request = getCurrentHttpRequest();
        return request != null ? request.getHeader(CART_SESSION_HEADER) : UUID.randomUUID().toString();
    }

    private HttpServletRequest getCurrentHttpRequest() {
        // Lấy request hiện tại từ context, xử lý ngoại lệ nếu không có
        try {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception e) {
            logger.warn("No request context available", e);
            return null;
        }
    }

    private BigDecimal calculateDiscount(Promotion promotion, BigDecimal amount) {
        // Tính giảm giá dựa trên loại khuyến mãi
        if (promotion.getDiscountType() == null || promotion.getDiscountValue() == null) return BigDecimal.ZERO;
        return "PERCENTAGE".equalsIgnoreCase(promotion.getDiscountType())
                ? amount.multiply(promotion.getDiscountValue().divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP))
                : promotion.getDiscountValue().min(amount);
    }

    private String buildFullAddress(Order order) {
        // Xây dựng địa chỉ đầy đủ từ các trường của order
        return String.join(", ",
                Objects.toString(order.getShippingStreetAddress(), ""),
                Objects.toString(order.getShippingWard(), ""),
                order.getShippingDistrict(),
                order.getShippingCity()
        ).replaceAll(", ,", ",").replaceAll("^,|,$", "").trim();
    }

    private String estimateDeliveryTime(ShippingMethod method) {
        // Tính thời gian giao hàng dự kiến
        if (method.getEstimatedDaysMin() != null && method.getEstimatedDaysMax() != null) {
            return method.getEstimatedDaysMin().equals(method.getEstimatedDaysMax())
                    ? method.getEstimatedDaysMin() + " ngày"
                    : method.getEstimatedDaysMin() + "-" + method.getEstimatedDaysMax() + " ngày";
        }
        return method.getEstimatedDaysMin() != null ? "Từ " + method.getEstimatedDaysMin() + " ngày" : "Không xác định";
    }

    // Thêm itemRepo để sử dụng trực tiếp trong CheckoutService
    @Autowired
    private CartItemRepository itemRepo;
}