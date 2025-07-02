package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    // Tìm Cart mới nhất theo userId dựa trên updatedAt
    Optional<Cart> findTopByUserIdOrderByUpdatedAtDesc(Integer userId);

    // Tìm Cart mới nhất theo sessionId dựa trên updatedAt
    Optional<Cart> findTopBySessionIdOrderByUpdatedAtDesc(String sessionId);

    // Tìm tất cả Cart theo userId
    List<Cart> findByUserId(Integer userId);

    // Tìm tất cả Cart theo sessionId
    List<Cart> findBySessionId(String sessionId);

    boolean existsByUserId(Integer userId);

}