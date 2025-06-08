package com.example.OrderHistory.entity;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersOrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "role")
    private String role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "total_spent")
    private BigDecimal totalSpent;

    @Column(name = "tier")
    private String tier;

    @Column(name = "is_active")
    private Boolean isActive;

    // Quan hệ 1-n với Order
    @OneToMany(mappedBy = "users")
    private List<OrdersHistory> orders;

    // Quan hệ 1-n với Address

}
