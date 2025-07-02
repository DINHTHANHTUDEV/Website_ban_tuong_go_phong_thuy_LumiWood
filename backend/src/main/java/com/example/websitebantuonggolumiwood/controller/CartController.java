package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.CartDTO;
import com.example.websitebantuonggolumiwood.dto.CartItemDTO;
import com.example.websitebantuonggolumiwood.dto.MergeCartDTO;
import com.example.websitebantuonggolumiwood.entity.Cart;
import com.example.websitebantuonggolumiwood.repository.PromotionRepository;
import com.example.websitebantuonggolumiwood.security.model.UserPrincipal;
import com.example.websitebantuonggolumiwood.service.CartService;
import com.example.websitebantuonggolumiwood.service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173") // Cho phép frontend gọi API từ localhost
public class CartController {

    @Autowired private CartService cartService;
    @Autowired private PromotionRepository promotionRepository;
    @Autowired private ProductsService productsService;

    /**
     * ✅ API lấy giỏ hàng hiện tại.
     * Không tạo cart nếu chưa có (để đúng theo logic business mới).
     */
    @GetMapping
    public ResponseEntity<?> viewCart(
            Authentication authentication,
            @RequestParam(required = false) String sessionId) {

        Integer userId = (authentication != null && authentication.isAuthenticated())
                ? ((UserPrincipal) authentication.getPrincipal()).getUserId().intValue()
                : null;

        Cart cart = cartService.getOrCreateCart(userId, sessionId);

        if (cart == null) {
            return ResponseEntity.ok(new CartDTO()); // Trả về giỏ rỗng nếu chưa tồn tại
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

        Integer userId = (authentication != null && authentication.isAuthenticated())
                ? ((UserPrincipal) authentication.getPrincipal()).getUserId().intValue()
                : null;

        CartDTO cartDTO = cartService.addToCart(userId, sessionId, request);
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

        Integer userId = (authentication != null && authentication.isAuthenticated())
                ? ((UserPrincipal) authentication.getPrincipal()).getUserId().intValue()
                : null;

        CartDTO cartDTO = cartService.updateQuantity(userId, sessionId, request);
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

        Integer userId = (authentication != null && authentication.isAuthenticated())
                ? ((UserPrincipal) authentication.getPrincipal()).getUserId().intValue()
                : null;

        CartDTO cartDTO = cartService.removeItem(userId, sessionId, productId);
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * ✅ API xóa toàn bộ giỏ hàng.
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(
            Authentication authentication,
            @RequestParam(required = false) String sessionId) {

        Integer userId = (authentication != null && authentication.isAuthenticated())
                ? ((UserPrincipal) authentication.getPrincipal()).getUserId().intValue()
                : null;

        cartService.clearCart(userId, sessionId);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ API gộp giỏ hàng của guest vào giỏ hàng user sau khi đăng nhập.
     * Khi user thêm hàng khi chưa login, login rồi merge.
     */
    @PostMapping("/merge")
    public ResponseEntity<CartDTO> mergeGuestCart(
            Authentication authentication,
            @RequestBody MergeCartDTO request) {

        Integer userId = (authentication != null && authentication.isAuthenticated())
                ? ((UserPrincipal) authentication.getPrincipal()).getUserId().intValue()
                : null;

        CartDTO cartDTO = cartService.mergeGuestCart(userId, request.getSessionId());
        return ResponseEntity.ok(cartDTO);
    }
}
