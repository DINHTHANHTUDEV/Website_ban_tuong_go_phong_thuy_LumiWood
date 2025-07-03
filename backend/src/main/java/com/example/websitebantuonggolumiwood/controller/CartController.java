// src/main/java/com/example/websitebantuonggolumiwood/controller/CartController.java
package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.CartDTO;
import com.example.websitebantuonggolumiwood.dto.CartItemDTO;
import com.example.websitebantuonggolumiwood.dto.MergeCartDTO;
import com.example.websitebantuonggolumiwood.entity.Cart;
import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.repository.PromotionRepository;
import com.example.websitebantuonggolumiwood.security.model.UserPrincipal;
import com.example.websitebantuonggolumiwood.service.CartService;
import com.example.websitebantuonggolumiwood.service.ProductsService;
import com.example.websitebantuonggolumiwood.service.UserService; // Giả định có UserService
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173") // Cho phép frontend gọi API từ localhost
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private static final String CART_SESSION_HEADER = "X-Cart-Session-Id"; // Tên header chuẩn

    @Autowired private CartService cartService;
    @Autowired private PromotionRepository promotionRepository;
    @Autowired private ProductsService productsService;
    @Autowired private UserService userService; // Inject UserService

    private User getUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            Long userId = principal.getUserId();
            if (userId != null) {
                return userService.findById(userId) // Lấy User từ UserService
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            }
        }
        return null;
    }

    // Helper lấy session ID từ header
    private String getCartSessionId(HttpServletRequest request) {
        return request.getHeader(CART_SESSION_HEADER);
    }

    /**
     * ✅ API lấy giỏ hàng hiện tại.
     * Không tạo cart nếu chưa có (để đúng theo logic business mới).
     */
    @GetMapping
    public ResponseEntity<?> viewCart(
            Authentication authentication,
            @RequestParam(required = false) String sessionId) {

        User user = getUser(authentication);
        logger.debug("GET /cart - UserId: {}, SessionId: {}", user != null ? user.getUserId() : null, sessionId);
        Cart cart = cartService.getOrCreateCart(user, sessionId);
        if (cart == null) {
            return ResponseEntity.ok(new CartDTO());
        }
        return ResponseEntity.ok(cartService.getCartResponse(cart));
    }

    /**
     * ✅ API thêm sản phẩm vào giỏ hàng.
     * Tự tạo giỏ nếu chưa có (chỉ lúc thêm sp mới tạo giỏ).
     */
    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(
            Authentication authentication,
            @RequestParam(required = false) String sessionId,
            @RequestBody CartItemDTO request) {

        User user = getUser(authentication);
        logger.info("POST /cart/add - UserId: {}, SessionId: {}, Payload: {}", user != null ? user.getUserId() : null, sessionId, request);
        CartDTO cartDTO = cartService.addToCart(user, sessionId, request);
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * ✅ API cập nhật số lượng sản phẩm trong giỏ hàng.
     */
    @PutMapping("/update")
    public ResponseEntity<CartDTO> updateQuantity(
            Authentication authentication,
            @RequestParam(required = false) String sessionId,
            @RequestBody CartItemDTO request) {

        User user = getUser(authentication);
        logger.info("PUT /cart/update - UserId: {}, SessionId: {}, Payload: {}", user != null ? user.getUserId() : null, sessionId, request);
        CartDTO cartDTO = cartService.updateQuantity(user, sessionId, request);
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * ✅ API xóa 1 sản phẩm khỏi giỏ hàng.
     */
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDTO> removeItem(
            Authentication authentication,
            @RequestParam(required = false) String sessionId,
            @PathVariable Integer productId) {

        User user = getUser(authentication);
        logger.info("DELETE /cart/remove/{} - UserId: {}, SessionId: {}", productId, user != null ? user.getUserId() : null, sessionId);
        CartDTO cartDTO = cartService.removeItem(user, sessionId, productId);
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * ✅ API xóa toàn bộ giỏ hàng.
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(
            Authentication authentication,
            @RequestParam(required = false) String sessionId) {

        User user = getUser(authentication);
        logger.info("DELETE /cart/clear - UserId: {}, SessionId: {}", user != null ? user.getUserId() : null, sessionId);
        cartService.clearCart(user, sessionId);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ API gộp giỏ hàng của guest vào giỏ hàng user sau khi đăng nhập.
     * Khi user thêm hàng khi chưa login, login rồi merge.
     */
    @PostMapping("/merge")
    @PreAuthorize("isAuthenticated()") // Chỉ user đã đăng nhập mới được merge
    public ResponseEntity<CartDTO> mergeGuestCart(
            Authentication authentication,
            @RequestBody MergeCartDTO request) {

        User user = getUser(authentication);
        if (user == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        String guestSessionId = request.getSessionId();
        logger.info("POST /cart/merge - UserId: {}, Merging SessionId: {}", user.getUserId(), guestSessionId);

        if (guestSessionId == null || guestSessionId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        CartDTO cartDTO = cartService.mergeGuestCart(user, guestSessionId);
        return ResponseEntity.ok(cartDTO);
    }
}