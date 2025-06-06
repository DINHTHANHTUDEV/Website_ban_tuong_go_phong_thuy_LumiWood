package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.security.model.UserPrincipal;
import com.example.websitebantuonggolumiwood.dto.ChangePasswordDTO;
import com.example.websitebantuonggolumiwood.dto.OrderRecentDTO;
import com.example.websitebantuonggolumiwood.dto.UpdateUserProfileDTO;
import com.example.websitebantuonggolumiwood.dto.UserProfileDTO;
import com.example.websitebantuonggolumiwood.service.OrderService;
import com.example.websitebantuonggolumiwood.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/my-profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private OrderService orderService;

    // API lấy thông tin hồ sơ cá nhân của người dùng đang đăng nhập
    @GetMapping
    public ResponseEntity<UserProfileDTO> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // Lấy username từ thông tin xác thực (Spring Security cung cấp)
        String username = userDetails.getUsername();
        // Gọi service để lấy dữ liệu hồ sơ cá nhân dựa trên username
        UserProfileDTO profile = userProfileService.getUserProfile(username);
        return ResponseEntity.ok(profile);
    }

    // API cập nhật thông tin hồ sơ cá nhân
    @PutMapping
    public ResponseEntity<UserProfileDTO> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid UpdateUserProfileDTO dto
    ) {
        // Lấy username của người dùng hiện tại
        String username = userDetails.getUsername();
        // Cập nhật hồ sơ cá nhân thông qua service
        UserProfileDTO updatedProfile = userProfileService.updateUserProfile(username, dto);
        return ResponseEntity.ok(updatedProfile);
    }

    // API thay đổi mật khẩu
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangePasswordDTO dto
    ) {
        // Gọi service để thực hiện việc thay đổi mật khẩu
        userProfileService.changePassword(userDetails.getUsername(), dto);
        return ResponseEntity.ok("Mật khẩu đã được thay đổi thành công!");
    }

    // Lấy 5 đơn gần nhất cho user đang đăng nhập
    @GetMapping("/recent-orders")
    public List<OrderRecentDTO> getRecentOrders(Authentication authentication) {
        // Ví dụ lấy userId từ authentication
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getUserId();
        return orderService.getRecentOrdersByUserId(userId);
    }
}
