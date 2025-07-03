// src/main/java/com/example/websitebantuonggolumiwood/repository/CartRepository.java
package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.Cart;
import com.example.websitebantuonggolumiwood.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    // Tìm Cart mới nhất theo User dựa trên updatedAt
    Optional<Cart> findTopByUserOrderByUpdatedAtDesc(User user);

    // Tìm Cart mới nhất theo sessionId dựa trên updatedAt
    Optional<Cart> findTopBySessionIdOrderByUpdatedAtDesc(String sessionId);

    // Tìm tất cả Cart theo User
    List<Cart> findByUser(User user);

    // Tìm tất cả Cart theo sessionId
    List<Cart> findBySessionId(String sessionId);

    // Kiểm tra xem có Cart nào thuộc User không
    boolean existsByUser(User user);

    // (Giữ nguyên các phương thức hiện có nếu cần, nhưng có thể không cần findByUserId nữa)
}