package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.dto.CartItemDTO;
import com.example.websitebantuonggolumiwood.dto.CartDTO;
import com.example.websitebantuonggolumiwood.entity.Cart;
import com.example.websitebantuonggolumiwood.entity.CartItem;
import com.example.websitebantuonggolumiwood.repository.CartItemRepository;
import com.example.websitebantuonggolumiwood.repository.CartRepository;
import com.example.websitebantuonggolumiwood.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired private CartRepository cartRepo;
    @Autowired private CartItemRepository itemRepo;
    @Autowired private ProductRepository productRepo;

    /**
     * Lấy giỏ hàng theo userId hoặc sessionId.
     * ⚠️ Không tạo cart nếu không tìm thấy.
     */
    @Transactional
    public Cart getOrCreateCart(Integer userId, String sessionId) {
        Cart existingCart = null;

        if (userId != null) {
            existingCart = cartRepo.findTopByUserIdOrderByUpdatedAtDesc(userId).orElse(null);
        } else if (sessionId != null) {
            existingCart = cartRepo.findTopBySessionIdOrderByUpdatedAtDesc(sessionId).orElse(null);
        }

        if (existingCart != null) {
            existingCart.setUpdatedAt(LocalDateTime.now());
            return cartRepo.save(existingCart);
        }

        // ⚠️ Không tạo mới nếu chưa có
        return null;
    }

    /**
     * Tạo cart mới (dành riêng cho lúc đăng ký tài khoản).
     * Tránh tạo trùng nếu user đã có cart.
     */
    @Transactional
    public Cart createCart(Integer userId, String sessionId) {
        // Nếu user đã có giỏ → trả lại cart cũ
        if (userId != null && cartRepo.existsByUserId(userId)) {
            return cartRepo.findTopByUserIdOrderByUpdatedAtDesc(userId).get();
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setSessionId(userId == null ? sessionId : null);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepo.save(cart);
    }

    /**
     * Trả về thông tin giỏ hàng dưới dạng DTO.
     */
    public CartDTO getCartResponse(Cart cart) {
        List<CartItem> items = itemRepo.findByCartId(cart.getId());

        List<CartItemDTO> itemDTOs = items.stream().map(item -> {
            CartItemDTO dto = new CartItemDTO();
            dto.setProductId(item.getProductId());
            dto.setQuantity(item.getQuantity());

            productRepo.findById(item.getProductId()).ifPresent(product -> {
                dto.setProductName(product.getName());
                dto.setImageUrl(product.getImage_url());
                dto.setPrice(product.getPrice().doubleValue());
                dto.setProductSlug(product.getSlug());
            });

            return dto;
        }).collect(Collectors.toList());

        CartDTO response = new CartDTO();
        response.setCartId(cart.getId());
        response.setItems(itemDTOs);
        return response;
    }

    /**
     * Thêm sản phẩm vào giỏ hàng. Nếu user chưa có cart → tạo mới lúc này.
     */
    @Transactional
    public CartDTO addToCart(Integer userId, String sessionId, CartItemDTO request) {
        Cart cart = getOrCreateCart(userId, sessionId);

        if (cart == null) {
            cart = createCart(userId, sessionId); // Tạo cart nếu chưa có
        }

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

    /**
     * Cập nhật số lượng sản phẩm trong giỏ.
     */
    @Transactional
    public CartDTO updateQuantity(Integer userId, String sessionId, CartItemDTO request) {
        Cart cart = getOrCreateCart(userId, sessionId);
        if (cart == null) throw new RuntimeException("Giỏ hàng chưa tồn tại");

        CartItem item = itemRepo.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        var product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (request.getQuantity() > product.getStock()) {
            throw new RuntimeException("Số lượng vượt quá tồn kho! Tối đa: " + product.getStock());
        }

        if (request.getQuantity() <= 0) {
            itemRepo.delete(item);
        } else {
            item.setQuantity(request.getQuantity());
            item.setUpdatedAt(LocalDateTime.now());
            itemRepo.save(item);
        }

        return getCartResponse(cart);
    }

    /**
     * Xóa một sản phẩm khỏi giỏ hàng.
     */
    @Transactional
    public CartDTO removeItem(Integer userId, String sessionId, Integer productId) {
        Cart cart = getOrCreateCart(userId, sessionId);
        if (cart == null) throw new RuntimeException("Giỏ hàng chưa tồn tại");

        CartItem item = itemRepo.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        itemRepo.delete(item);
        return getCartResponse(cart);
    }

    /**
     * Xóa toàn bộ giỏ hàng.
     */
    @Transactional
    public void clearCart(Integer userId, String sessionId) {
        Cart cart = getOrCreateCart(userId, sessionId);
        if (cart == null) return;

        List<CartItem> items = itemRepo.findByCartId(cart.getId());
        if (!items.isEmpty()) {
            itemRepo.deleteAll(items);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(cart);
    }

    /**
     * Gộp cart của guest (sessionId) vào cart của user.
     */
    @Transactional
    public CartDTO mergeGuestCart(Integer userId, String guestSessionId) {
        if (userId == null || guestSessionId == null) {
            throw new IllegalArgumentException("User ID và Session ID là bắt buộc khi merge.");
        }

        Cart userCart = cartRepo.findTopByUserIdOrderByUpdatedAtDesc(userId).orElseGet(() -> createCart(userId, null));
        Cart guestCart = cartRepo.findTopBySessionIdOrderByUpdatedAtDesc(guestSessionId).orElse(null);

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

            itemRepo.deleteAll(guestItems);
            cartRepo.delete(guestCart);
        }

        return getCartResponse(userCart);
    }

    /**
     * Tính tổng tiền của giỏ hàng.
     */
    public BigDecimal calculateSubtotalForCart(Integer userId, String sessionId) {
        Cart cart = getOrCreateCart(userId, sessionId);
        if (cart == null) return BigDecimal.ZERO;

        List<CartItem> items = itemRepo.findByCartId(cart.getId());
        if (items.isEmpty()) return BigDecimal.ZERO;

        BigDecimal subTotal = items.stream()
                .map(item -> productRepo.findById(item.getProductId())
                        .map(product -> product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại: " + item.getProductId())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return subTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
