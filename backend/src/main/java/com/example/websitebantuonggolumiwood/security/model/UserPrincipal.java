package com.example.websitebantuonggolumiwood.security.model;

import com.example.websitebantuonggolumiwood.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Lớp UserPrincipal cài đặt UserDetails để Spring Security sử dụng
 * Đại diện cho user trong hệ thống bảo mật,
 * cung cấp thông tin đăng nhập, quyền hạn, và trạng thái tài khoản.
 */
public class UserPrincipal implements UserDetails {

    private Long userId;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Boolean isActive;

    // Constructor chính
    public UserPrincipal(Long userId, String username, String password,
                         Collection<? extends GrantedAuthority> authorities,
                         Boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.isActive = isActive;
    }

    /**
     * Chuyển đổi từ entity User sang UserPrincipal.
     * Từ trường role kiểu String (ví dụ: "ADMIN", "CUSTOMER") tạo danh sách quyền.
     */
    public static UserPrincipal create(User user) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase());
        return new UserPrincipal(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(authority),
                user.getIsActive()
        );
    }



    public Long getUserId() {
        return userId;
    }

    // Các phương thức bắt buộc của UserDetails:

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Tài khoản không hết hạn
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Tài khoản không bị khóa
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Mật khẩu không hết hạn
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Tài khoản đang kích hoạt (dựa trên trường isActive của entity User).
     */
    // Thực tế bạn có thể truyền trạng thái từ entity User nếu cần
    @Override
    public boolean isEnabled() {
        return isActive != null ? isActive : true;
    }

}
