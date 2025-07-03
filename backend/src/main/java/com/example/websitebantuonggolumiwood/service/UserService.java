package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.exception.UsernameAlreadyExistsException;
import com.example.websitebantuonggolumiwood.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service xử lý nghiệp vụ liên quan đến User
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // Bean mã hóa mật khẩu, ví dụ BCryptPasswordEncoder

    /**
     * Kiểm tra username đã tồn tại trong DB chưa
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Lưu user mới vào DB
     * Mã hóa password trước khi lưu
     */
    public void saveUser(User user) {
        if (existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Tên đăng nhập đã tồn tại: " + user.getUsername());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }



    // Bạn có thể thêm các method như updateUser, findByUsername, ...

//    public boolean existsByEmail(String email) {
//        return userRepository.existsByEmail(email); // dùng hàm repo đã có ở trên
//    }
}
