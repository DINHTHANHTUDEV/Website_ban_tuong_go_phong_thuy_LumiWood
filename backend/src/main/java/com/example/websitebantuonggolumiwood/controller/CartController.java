package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.CartItemDTO;
import com.example.websitebantuonggolumiwood.dto.CartDTO;
import com.example.websitebantuonggolumiwood.dto.MergeCartDTO;
import com.example.websitebantuonggolumiwood.entity.Cart;
import com.example.websitebantuonggolumiwood.entity.PromotionOrderHistory;
import com.example.websitebantuonggolumiwood.repository.PromotionRepository;
import com.example.websitebantuonggolumiwood.service.CartService;
import com.example.websitebantuonggolumiwood.service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {
    @Autowired private CartService cartService;
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private ProductsService productsService;

    @GetMapping
    public ResponseEntity<CartDTO> viewCart(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String sessionId) {
        Cart cart = cartService.getOrCreateCart(userId, sessionId);
        return ResponseEntity.ok(cartService.getCartResponse(cart));
    }

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String sessionId,
            @RequestBody CartItemDTO request) {
        return ResponseEntity.ok(cartService.addToCart(userId, sessionId, request));
    }

    @PutMapping("/update")
    public ResponseEntity<CartDTO> updateQuantity(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String sessionId,
            @RequestBody CartItemDTO request) {
        return ResponseEntity.ok(cartService.updateQuantity(userId, sessionId, request));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDTO> removeItem(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String sessionId,
            @PathVariable Integer productId) {
        return ResponseEntity.ok(cartService.removeItem(userId, sessionId, productId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<CartDTO> clearCart(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String sessionId) {
        return ResponseEntity.ok(cartService.clearCart(userId, sessionId));
    }

    @PostMapping("/merge")
    public ResponseEntity<CartDTO> mergeGuestCart(
            @RequestParam Integer userId,
            @RequestBody MergeCartDTO request) {
        return ResponseEntity.ok(cartService.mergeGuestCart(userId, request.getSessionId()));
    }


}