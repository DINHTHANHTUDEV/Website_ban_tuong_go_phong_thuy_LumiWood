package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.*;
import com.example.websitebantuonggolumiwood.entity.ShippingMethod;
import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.repository.OrderRepository;
import com.example.websitebantuonggolumiwood.repository.ShippingMethodAdminRepository;
import com.example.websitebantuonggolumiwood.repository.UserManagementRepository;
import com.example.websitebantuonggolumiwood.service.CheckoutService;
import com.example.websitebantuonggolumiwood.service.OrderService;
import com.example.websitebantuonggolumiwood.service.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer/checkout")
public class CheckoutController {

    // su dung UserManagementService va UserManagementRepository vi truoc do da lay address o day roi
    //nen gio muon lay o day luon de khong phai tao service va repository moi
    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private UserManagementRepository userManagementRepository;

    // su dung ShippingMethodAdminRepository vi truoc do da lay shippingMethod o day roi
    //nen gio muon lay o day luon de khong phai tao service va repository moi
    @Autowired
    private ShippingMethodAdminRepository shippingMethodAdminRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    private final CheckoutService checkoutService;

    @Autowired
    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }
    /**
     * Lấy danh sách địa chỉ giao hàng của user đang đăng nhập.
     * @param userDetails Thông tin người dùng từ Spring Security.
     * @return ResponseEntity chứa danh sách AddressDTO.
     */
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (userDetails == null) {
            throw new IllegalStateException("Người dùng chưa đăng nhập");
        }

        // Lấy username từ UserDetails
        String username = userDetails.getUsername();

        // Tìm user trong database dựa trên username
        Optional<User> userOpt = userManagementRepository.findByUsername(username);
        Long userId = userOpt.map(User::getUserId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy user với username: " + username));

        // Gọi service để lấy danh sách địa chỉ của user
        List<AddressDTO> addresses = userManagementService.getAddresses(userId);

        // Trả về danh sách địa chỉ với mã HTTP 200 OK
        return ResponseEntity.ok(addresses);
    }

    /**
     * Thêm mới địa chỉ giao hàng cho user đang đăng nhập.
     * @param userDetails Thông tin người dùng từ Spring Security.
     * @param addressDTO Thông tin địa chỉ giao hàng từ request body.
     * @return ResponseEntity chứa AddressDTO của địa chỉ vừa thêm.
     */
    @PostMapping("/add-address")
    public ResponseEntity<AddressDTO> addAddress(@AuthenticationPrincipal UserDetails userDetails,
                                                 @Valid @RequestBody AddAddressDTO addressDTO) {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (userDetails == null) {
            throw new IllegalStateException("Người dùng chưa đăng nhập");
        }

        // Lấy username từ UserDetails
        String username = userDetails.getUsername();

        // Tìm user trong database dựa trên username
        Optional<User> userOpt = userManagementRepository.findByUsername(username);
        Long userId = userOpt.map(User::getUserId)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy user với username: " + username));

        // Gọi service để thêm địa chỉ mới
        AddressDTO savedAddress = userManagementService.addAddress(userId, addressDTO);

        // Trả về địa chỉ vừa thêm với mã HTTP 200 OK
        return ResponseEntity.ok(savedAddress);
    }

    // hien thi danh sach phuong thuc giao hang
    @GetMapping("/shipping-methods")
    public Page<ShippingMethodDTO> getAll(Pageable pageable) {
        Page<ShippingMethod> page = shippingMethodAdminRepository.findAll(pageable);
        return page.map(method -> new ShippingMethodDTO(
                method.getId(),
                method.getName(),
                method.getDescription(),
                method.getBaseCost(),
                method.getEstimatedDaysMin(),
                method.getEstimatedDaysMax(),
                method.getIsActive()
        ));
    }

    /**
     * API để đặt hàng
     * @param request Dữ liệu yêu cầu đặt hàng từ frontend
     * @param userDetails Thông tin người dùng từ JWT token
     * @return OrderSummaryDTO chứa thông tin tóm tắt đơn hàng
     */
    @PostMapping("/place-order")
    public ResponseEntity<OrderSummaryDTO> placeOrder(
            @Valid @RequestBody PlaceOrderRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        OrderSummaryDTO summary = checkoutService.placeOrder(request, username);
        return ResponseEntity.ok(summary);
    }

}
