package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.UpdateUserTierDTO;
import com.example.websitebantuonggolumiwood.dto.OrderRecentDTO;
import com.example.websitebantuonggolumiwood.dto.AddressDTO;
import com.example.websitebantuonggolumiwood.dto.UpdateUserStatusDTO;
import com.example.websitebantuonggolumiwood.dto.UserListDTO;
import com.example.websitebantuonggolumiwood.service.UserManagementService;
import com.example.websitebantuonggolumiwood.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrderService orderService;

    /**
     * Lấy danh sách khách hàng phân trang, lọc theo tier, trạng thái, tìm kiếm username hoặc fullname
     */
    @GetMapping
    public ResponseEntity<Page<UserListDTO>> getUsers(
            @RequestParam(required = false, defaultValue = "all") String tier,
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page
    ) {
        int pageSize = 10;
        Page<UserListDTO> users = userManagementService.getUsers(tier, status, search, page, pageSize);
        return ResponseEntity.ok(users);
    }

    /**
     * Xem chi tiết user kèm 5 đơn hàng gần nhất
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserListDTO> getUserProfile(@PathVariable Long userId) {
        UserListDTO userProfile = userManagementService.getUserProfile(userId);
        if (userProfile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userProfile);
    }

    /**
     * Cập nhật trạng thái user (hoạt động / khóa)
     */
    @PutMapping("/{userId}/status")
    public ResponseEntity<Map<String, String>> updateUserStatus(
            @PathVariable Long userId, @Valid
            @RequestBody UpdateUserStatusDTO updateStatusDTO
    ) {
        boolean success = userManagementService.updateUserStatus(userId, updateStatusDTO.getIsActive());
        if (!success) {
            return ResponseEntity.notFound().build();
        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cập nhật trạng thái tài khoản thành công!");
        return ResponseEntity.ok(response);
    }


    /**
     * Lấy địa chỉ giao hàng của user
     */
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(@PathVariable Long userId) {
        List<AddressDTO> addresses = userManagementService.getAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Lấy 5 đơn hàng gần nhất của user (dùng chung OrderService)
     */
    @GetMapping("/{userId}/recent-orders")
    public ResponseEntity<List<OrderRecentDTO>> getRecentOrders(@PathVariable Long userId) {
        List<OrderRecentDTO> recentOrders = orderService.getRecentOrdersByUserId(userId);
        return ResponseEntity.ok(recentOrders);
    }

    /**
     * Endpoint cập nhật bậc khách hàng (tier)
     * PUT /api/admin/users/{userId}/tier
     * Body JSON: { "tier": "BRONZE" }
     */
    @PutMapping("/{userId}/tier")
    public ResponseEntity<Map<String, String>> updateUserTier(
            @PathVariable Long userId,@Valid
            @RequestBody UpdateUserTierDTO updateUserTierDTO
    ) {
        // Lấy giá trị tier từ DTO
        String newTier = updateUserTierDTO.getTier();

        if (newTier == null || newTier.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Bậc khách hàng (tier) không được để trống");
            return ResponseEntity.badRequest().body(error);
        }

        boolean success = userManagementService.updateUserTier(userId, newTier);
        if (!success) {
            return ResponseEntity.notFound().build();
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Cập nhật bậc khách hàng thành công!");
        return ResponseEntity.ok(response);
    }
}
