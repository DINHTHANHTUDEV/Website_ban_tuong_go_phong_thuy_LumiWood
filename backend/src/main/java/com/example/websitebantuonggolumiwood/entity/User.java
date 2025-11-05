package com.example.websitebantuonggolumiwood.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "users")  // tên bảng trong DB
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // id tự tăng
    @Column(name = "id")  // tên cột trong DB
    private Long userId;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 255)
    private String phone_number;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(name = "role", nullable = false, length = 50)
    private String role;  // Ví dụ: "ADMIN", "CUSTOMER"


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "total_spent", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalSpent;

    @Column(name = "tier", nullable = false, length = 20)
    private String tier;  // "BRONZE", "SILVER", "GOLD", "DIAMOND"

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // Có thể thêm phương thức @PrePersist để tự set giá trị mặc định khi insert mới
    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (totalSpent == null) totalSpent = BigDecimal.ZERO;
        if (tier == null) tier = "BRONZE";
        if (isActive == null) isActive = true;
    }
}
