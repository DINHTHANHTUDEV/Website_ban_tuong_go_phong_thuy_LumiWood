package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.dto.CartItemDTO;
import com.example.websitebantuonggolumiwood.dto.CartDTO;
import com.example.websitebantuonggolumiwood.entity.Cart;
import com.example.websitebantuonggolumiwood.entity.CartItem;
import com.example.websitebantuonggolumiwood.repository.CartItemRepository;
import com.example.websitebantuonggolumiwood.repository.CartRepository;
import com.example.websitebantuonggolumiwood.repository.ProductsRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired private CartRepository cartRepo;
    @Autowired private CartItemRepository itemRepo;
    @Autowired private ProductsRepositories productRepo;

    public Cart getOrCreateCart(Integer userId, String sessionId) {
        return userId != null
                ? cartRepo.findByUserId(userId).orElseGet(() -> createCart(userId, null))
                : cartRepo.findBySessionId(sessionId).orElseGet(() -> createCart(null, sessionId));
    }

    private Cart createCart(Integer userId, String sessionId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setSessionId(sessionId);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepo.save(cart);
    }

    public CartDTO getCartResponse(Cart cart) {
        List<CartItem> items = itemRepo.findByCartId(cart.getId());
        List<CartItemDTO> itemDTOs = items.stream().map(item -> {
            CartItemDTO dto = new CartItemDTO();
            dto.setProductId(item.getProductId());
            dto.setQuantity(item.getQuantity());

            // Thêm thông tin sản phẩm vào DTO
            productRepo.findById(item.getProductId()).ifPresent(product -> {
                dto.setProductName(product.getName());
                dto.setImageUrl(product.getImage_url());
                dto.setPrice(product.getPrice());
                dto.setProductSlug(product.getSlug());
            });

            return dto;
        }).collect(Collectors.toList());

        CartDTO response = new CartDTO();
        response.setCartId(cart.getId());
        response.setItems(itemDTOs);
        return response;
    }

    public CartDTO addToCart(Integer userId, String sessionId, CartItemDTO request) {
        Cart cart = getOrCreateCart(userId, sessionId);
        CartItem item = itemRepo.findByCartIdAndProductId(cart.getId(), request.getProductId()).orElse(null);

        var product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        int newQuantity = request.getQuantity();
        if (item != null) {
            newQuantity += item.getQuantity();
        }

        if (newQuantity > product.getStock()) {
            throw new RuntimeException("Số lượng vượt quá tồn kho! Tối đa: " + product.getStock());
        }

        if (item != null) {
            item.setQuantity(newQuantity);
        } else {
            item = new CartItem();
            item.setCart(cart);
            item.setProductId(request.getProductId());
            item.setQuantity(request.getQuantity());
            item.setCreatedAt(LocalDateTime.now());
        }
        item.setUpdatedAt(LocalDateTime.now());
        itemRepo.save(item);

        return getCartResponse(cart);
    }



    public CartDTO updateQuantity(Integer userId, String sessionId, CartItemDTO request) {
        Cart cart = getOrCreateCart(userId, sessionId);
        CartItem item = itemRepo.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        // ✅ Lấy sản phẩm từ repo
        var product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // ✅ Kiểm tra nếu số lượng yêu cầu vượt quá tồn kho
        if (request.getQuantity() > product.getStock()) {
            throw new RuntimeException("Số lượng vượt quá tồn kho! Tối đa: " + product.getStock());
        }

        // ✅ Nếu <= 0 thì xóa khỏi giỏ
        if (request.getQuantity() <= 0) {
            itemRepo.delete(item);
        } else {
            item.setQuantity(request.getQuantity());
            item.setUpdatedAt(LocalDateTime.now());
            itemRepo.save(item);
        }

        return getCartResponse(cart);
    }


    public CartDTO removeItem(Integer userId, String sessionId, Integer productId) {
        Cart cart = getOrCreateCart(userId, sessionId);
        CartItem item = itemRepo.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        itemRepo.delete(item);
        return getCartResponse(cart);
    }

    public CartDTO clearCart(Integer userId, String sessionId) {
        Cart cart = getOrCreateCart(userId, sessionId);

        // Xóa toàn bộ item của cart
        List<CartItem> items = itemRepo.findByCartId(cart.getId());
        itemRepo.deleteAll(items);

        // Cập nhật lại updatedAt của giỏ hàng
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(cart);

        return getCartResponse(cart);
    }

    public CartDTO mergeGuestCart(Integer userId, String guestSessionId) {
        if (userId == null || guestSessionId == null) {
            throw new IllegalArgumentException("User ID and Guest Session ID are required for merge.");
        }

        // Tìm cart của user và guest
        Cart userCart = cartRepo.findByUserId(userId).orElseGet(() -> createCart(userId, null));
        Cart guestCart = cartRepo.findBySessionId(guestSessionId).orElse(null);

        if (guestCart != null) {
            List<CartItem> guestItems = itemRepo.findByCartId(guestCart.getId());

            for (CartItem guestItem : guestItems) {
                CartItem existingItem = itemRepo.findByCartIdAndProductId(userCart.getId(), guestItem.getProductId()).orElse(null);

                if (existingItem != null) {
                    existingItem.setQuantity(existingItem.getQuantity() + guestItem.getQuantity());
                    existingItem.setUpdatedAt(LocalDateTime.now());
                    itemRepo.save(existingItem);
                } else {
                    CartItem newItem = new CartItem();
                    newItem.setCart(userCart);
                    newItem.setProductId(guestItem.getProductId());
                    newItem.setQuantity(guestItem.getQuantity());
                    newItem.setCreatedAt(LocalDateTime.now());
                    newItem.setUpdatedAt(LocalDateTime.now());
                    itemRepo.save(newItem);
                }
            }

            // Xóa guest cart và các item của nó
            itemRepo.deleteAll(guestItems);
            cartRepo.delete(guestCart);
        }

        return getCartResponse(userCart);
    }

}