package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.dto.ChangePasswordDTO;
import com.example.websitebantuonggolumiwood.dto.UpdateUserProfileDTO;
import com.example.websitebantuonggolumiwood.dto.UserProfileDTO;
import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Lấy thông tin hồ sơ cá nhân của người dùng dựa vào username
    public UserProfileDTO getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserProfileDTO(user.getUsername(), user.getFullName(), user.getCreatedAt(), user.getTier(), user.getTotalSpent());
    }

    // Cập nhật thông tin hồ sơ cá nhân
    public UserProfileDTO updateUserProfile(String username, UpdateUserProfileDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(dto.getFullName());
        userRepository.save(user);

        return new UserProfileDTO(user.getUsername(), user.getFullName(), user.getCreatedAt(), user.getTier(), user.getTotalSpent());
    }

    // Thay đổi mật khẩu người dùng
    public void changePassword(String username, ChangePasswordDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new RuntimeException("Mật khẩu mới không khớp");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}
