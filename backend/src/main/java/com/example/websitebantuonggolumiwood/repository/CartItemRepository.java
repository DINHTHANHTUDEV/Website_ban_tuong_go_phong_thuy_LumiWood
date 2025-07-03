// src/main/java/com/example/websitebantuonggolumiwood/repository/CartItemRepository.java
package com.example.websitebantuonggolumiwood.repository;

import com.example.websitebantuonggolumiwood.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho entity CartItem, quản lý các truy vấn liên quan đến giỏ hàng.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    /**
     * Tìm tất cả CartItem dựa trên ID của Cart (cart.id).
     * @param cartId ID của Cart
     * @return Danh sách CartItem liên quan
     */
    List<CartItem> findByCart_Id(Long cartId);

    /**
     * Tìm CartItem dựa trên ID của Cart (cart.id) và productId.
     * @param cartId ID của Cart
     * @param productId ID của sản phẩm
     * @return Optional chứa CartItem nếu tồn tại, Optional.empty() nếu không tìm thấy
     */
    Optional<CartItem> findByCart_IdAndProductId(Long cartId, Integer productId);

    /**
     * Xóa tất cả CartItem theo ID của Cart (cart.id).
     * @param cartId ID của Cart cần xóa các CartItem liên quan
     */
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(Long cartId);
}