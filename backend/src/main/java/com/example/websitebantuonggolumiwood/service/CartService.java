package com.example.websitebantuonggolumiwood.service;

import com.example.websitebantuonggolumiwood.dto.CartItemDTO;
import com.example.websitebantuonggolumiwood.dto.CartDTO;
import com.example.websitebantuonggolumiwood.entity.Cart;
import com.example.websitebantuonggolumiwood.entity.CartItem;
import com.example.websitebantuonggolumiwood.entity.Product;
import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.repository.CartItemRepository;
import com.example.websitebantuonggolumiwood.repository.CartRepository;
import com.example.websitebantuonggolumiwood.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired private CartRepository cartRepo;
    @Autowired private CartItemRepository itemRepo;
    @Autowired private ProductRepository productRepo;

    @Transactional
    public Cart getOrCreateCart(User user, String sessionId) {
        logger.debug("➡️ [getOrCreateCart] Đầu vào user: {}, sessionId: {}",
                user != null ? user.getUserId() : "null", sessionId);

        Cart existingCart = null;

        if (user != null) {
            logger.debug("🔍 Tìm cart theo userId: {}", user.getUserId());
            existingCart = cartRepo.findTopByUserOrderByUpdatedAtDesc(user).orElse(null);
            if (existingCart != null) {
                logger.debug("✅ Tìm thấy cart theo user: cartId={}", existingCart.getId());
                existingCart.setUpdatedAt(LocalDateTime.now());
                return cartRepo.save(existingCart);
            }
        } else if (sessionId != null && !sessionId.isBlank()) {
            logger.debug("🔍 Tìm cart theo sessionId: {}", sessionId);
            existingCart = cartRepo.findTopBySessionIdOrderByUpdatedAtDesc(sessionId).orElse(null);
            if (existingCart != null) {
                logger.debug("✅ Tìm thấy cart theo sessionId: cartId={}", existingCart.getId());
                existingCart.setUpdatedAt(LocalDateTime.now());
                return cartRepo.save(existingCart);
            }
        }

        if (user == null && (sessionId == null || sessionId.isBlank())) {
            logger.warn("❌ Không có user và sessionId => lỗi");
            throw new IllegalArgumentException("Session ID is required for guest cart access.");
        }

        logger.warn("🆕 Không tìm thấy cart, tạo mới cho userId={} / sessionId={}",
                user != null ? user.getUserId() : "null", sessionId);

        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setSessionId(user == null ? sessionId : null);
        newCart.setCreatedAt(LocalDateTime.now());
        newCart.setUpdatedAt(LocalDateTime.now());

        Cart savedCart = cartRepo.save(newCart);
        logger.info("✅ Cart mới được tạo: cartId={}, userId={}, sessionId={}",
                savedCart.getId(), user != null ? user.getUserId() : "null", sessionId);
        return savedCart;
    }

    @Transactional
    public Cart createCart(User user, String sessionId) {
        logger.debug("🧪 [createCart] user={}, sessionId={}", user != null ? user.getUserId() : "null", sessionId);

        if (user != null && cartRepo.existsByUser(user)) {
            logger.debug("📦 User đã có cart, không tạo mới");
            return cartRepo.findTopByUserOrderByUpdatedAtDesc(user)
                    .orElseThrow(() -> new IllegalStateException("Cart exists but cannot be retrieved"));
        }

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setSessionId(user == null ? sessionId : null);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        logger.info("✅ Cart mới được tạo trong createCart: userId={}, sessionId={}", user != null ? user.getUserId() : "null", sessionId);
        return cartRepo.save(cart);
    }

    public CartDTO getCartResponse(Cart cart) {
        List<CartItem> items = cart.getItems();
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

    @Transactional
    public CartDTO addToCart(User user, String sessionId, CartItemDTO request) {
        logger.debug("🛒 [addToCart] user={}, sessionId={}, productId={}, quantity={}",
                user != null ? user.getUserId() : "null", sessionId, request.getProductId(), request.getQuantity());

        Cart cart = getOrCreateCart(user, sessionId);
        var product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        int quantityToAdd = request.getQuantity();
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (item != null) {
            int newQuantity = item.getQuantity() + quantityToAdd;
            if (product.getStock() != null && product.getStock() < newQuantity) {
                throw new RuntimeException("Số lượng vượt quá tồn kho! Tối đa: " + product.getStock());
            }
            item.setQuantity(newQuantity);
            logger.debug("↪️ Cập nhật số lượng product ID {} trong cart ID {} thành {}", request.getProductId(), cart.getId(), newQuantity);
        } else {
            if (product.getStock() != null && product.getStock() < quantityToAdd) {
                throw new RuntimeException("Số lượng vượt quá tồn kho! Tối đa: " + product.getStock());
            }
            logger.debug("➕ Thêm sản phẩm mới product ID {} với số lượng {} vào cart ID {}", request.getProductId(), quantityToAdd, cart.getId());
            item = new CartItem();
            cart.addCartItem(item);
            item.setProductId(request.getProductId());
            item.setQuantity(quantityToAdd);
            item.setCreatedAt(LocalDateTime.now());
        }
        item.setUpdatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(cart);

        return getCartResponse(cart);
    }

    @Transactional
    public CartDTO updateQuantity(User user, String sessionId, CartItemDTO request) {
        logger.debug("🔁 [updateQuantity] user={}, sessionId={}, productId={}, quantity={}",
                user != null ? user.getUserId() : "null", sessionId, request.getProductId(), request.getQuantity());

        if (request.getQuantity() <= 0) {
            return removeItem(user, sessionId, request.getProductId());
        }

        Cart cart = getOrCreateCart(user, sessionId);
        var product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sản phẩm chưa có trong giỏ"));

        if (product.getStock() != null && product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Số lượng vượt quá tồn kho! Tối đa: " + product.getStock());
        }

        logger.debug("✏️ Cập nhật số lượng sản phẩm product ID {} thành {} trong cart ID {}", request.getProductId(), request.getQuantity(), cart.getId());
        item.setQuantity(request.getQuantity());
        item.setUpdatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(cart);

        return getCartResponse(cart);
    }

    @Transactional
    public CartDTO removeItem(User user, String sessionId, Integer productId) {
        logger.debug("❌ [removeItem] user={}, sessionId={}, productId={}",
                user != null ? user.getUserId() : "null", sessionId, productId);

        Cart cart = getOrCreateCart(user, sessionId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if (item != null) {
            logger.info("🗑️ Xoá sản phẩm product ID {} khỏi cart ID {}", productId, cart.getId());
            cart.removeCartItem(item);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepo.save(cart);
        } else {
            logger.warn("⚠️ Không tìm thấy sản phẩm product ID {} trong cart ID {}", productId, cart.getId());
        }

        return getCartResponse(cart);
    }

    @Transactional
    public void clearCart(User user, String sessionId) {
        logger.info("🧹 [clearCart] user={}, sessionId={} ", user != null ? user.getUserId() : "null", sessionId);
        Cart cart = getOrCreateCart(user, sessionId);
        if (cart != null && !cart.getItems().isEmpty()) {
            logger.info("🧹 Xoá toàn bộ sản phẩm khỏi cart ID: {}", cart.getId());
            cart.getItems().clear();
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepo.save(cart);
        }
    }

    @Transactional
    public CartDTO mergeGuestCart(User user, String guestSessionId) {
        logger.info("🔀 [mergeGuestCart] userId={}, guestSessionId={}",
                user != null ? user.getUserId() : "null", guestSessionId);

        if (user == null || guestSessionId == null || guestSessionId.isBlank()) {
            logger.warn("❌ Không thể gộp vì thiếu user hoặc sessionId");
            return getCartResponse(getOrCreateCart(user, null));
        }

        Cart guestCart = cartRepo.findTopBySessionIdOrderByUpdatedAtDesc(guestSessionId).orElse(null);
        if (guestCart == null) {
            logger.info("ℹ️ Không có giỏ hàng guest để gộp");
            return getCartResponse(getOrCreateCart(user, null));
        }

        logger.info("🔁 Gộp guestCart ID={} vào userId={}", guestCart.getId(), user.getUserId());
        guestCart.setUser(user);
        guestCart.setSessionId(null);
        guestCart.setUpdatedAt(LocalDateTime.now());
        Cart userCart = cartRepo.save(guestCart);

        return getCartResponse(userCart);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateSubtotalForCart(User user, String sessionId) {
        logger.debug("🧮 [calculateSubtotalForCart] userId={}, sessionId={}",
                user != null ? user.getUserId() : "null", sessionId);

        Cart cart = getOrCreateCart(user, sessionId);
        BigDecimal total = BigDecimal.ZERO;

        List<CartItem> items = cart.getItems();
        if (items.isEmpty()) {
            logger.debug("🧮 Cart ID {} trống, subtotal = 0", cart.getId());
            return total;
        }

        List<Integer> productIds = items.stream().map(CartItem::getProductId).collect(Collectors.toList());
        Map<Integer, BigDecimal> productPrices = productRepo.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Product::getPrice));

        for (CartItem item : items) {
            BigDecimal currentPrice = productPrices.getOrDefault(item.getProductId(), BigDecimal.ZERO);
            if (currentPrice.compareTo(BigDecimal.ZERO) > 0 && item.getQuantity() > 0) {
                BigDecimal lineTotal = currentPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(lineTotal);
                logger.trace("🧾 Item ID: {}, Qty: {}, Price: {}, LineTotal: {}, RunningTotal: {}",
                        item.getProductId(), item.getQuantity(), currentPrice, lineTotal, total);
            }
        }

        logger.debug("✅ Tổng tiền giỏ hàng cart ID {} = {}", cart.getId(), total);
        return total.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
