package com.example.websitebantuonggolumiwood.security.service;

import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.repository.UserRepository;
import com.example.websitebantuonggolumiwood.security.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Tải UserDetails dựa trên username (bắt buộc khi đăng nhập bằng username/password)
     * @param username tên đăng nhập người dùng
     * @return UserPrincipal để Spring Security xử lý
     * @throws UsernameNotFoundException nếu không tìm thấy user
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm user theo username trong DB
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        // Chuyển User entity sang UserPrincipal (UserDetails)
        return UserPrincipal.create(user);
    }

    /**
     * Tải UserDetails dựa trên userId (dùng trong JwtAuthenticationFilter để xác thực bằng token)
     * @param userId id người dùng
     * @return UserPrincipal tương ứng
     * @throws UsernameNotFoundException nếu không tìm thấy user
     */
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return UserPrincipal.create(user);
    }

}
