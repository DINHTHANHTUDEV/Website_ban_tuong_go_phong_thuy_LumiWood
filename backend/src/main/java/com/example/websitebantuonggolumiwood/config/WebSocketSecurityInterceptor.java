package com.example.websitebantuonggolumiwood.config;

import com.example.websitebantuonggolumiwood.security.jwt.JwtTokenProvider;
import com.example.websitebantuonggolumiwood.security.model.UserPrincipal;
import com.example.websitebantuonggolumiwood.security.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
public class WebSocketSecurityInterceptor implements HandshakeInterceptor, ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public WebSocketSecurityInterceptor(JwtTokenProvider jwtTokenProvider,
                                        UserDetailsServiceImpl userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    // ✅ Bắt token từ request WebSocket và lưu vào attributes để xử lý sau
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        log.info("🔐 Bắt đầu kiểm tra JWT khi kết nối WebSocket...");

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();

            // Lấy token từ query param hoặc Authorization header
            String token = httpServletRequest.getParameter("token");
            if (token == null || token.isBlank()) {
                String authHeader = httpServletRequest.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }

            if (token != null && jwtTokenProvider.validateToken(token)) {
                Long userId = jwtTokenProvider.getUserIdFromJWT(token);

                attributes.put("token", token);   // dùng cho preSend
                attributes.put("userId", userId); // có thể dùng nếu cần sau này

                log.info("✅ WebSocket hợp lệ. userId: {}", userId);
                return true;
            } else {
                log.warn("❌ JWT không hợp lệ hoặc không tồn tại trong WebSocket handshake.");
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        log.info("🔄 Handshake WebSocket kết thúc.");
    }

    // ✅ Khi client CONNECT tới WebSocket, gán Authentication dựa theo token đã lấy từ trước
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && accessor.getCommand() == StompCommand.CONNECT) {
            Map<String, Object> attributes = accessor.getSessionAttributes();
            if (attributes != null && attributes.get("token") != null) {
                String token = attributes.get("token").toString();

                if (jwtTokenProvider.validateToken(token)) {
                    Long userId = jwtTokenProvider.getUserIdFromJWT(token);
                    UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserById(userId);

                    // ✅ Sử dụng UserPrincipal làm principal thay vì chỉ username
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userPrincipal, // 👈 Gán nguyên object UserPrincipal làm principal
                                    null,
                                    userPrincipal.getAuthorities()
                            );

                    // ✅ Gán vào WebSocket session
                    accessor.setUser(authentication);

                    // ✅ (Tùy chọn) Gán vào SecurityContext nếu muốn xử lý thêm ở service layer
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("✅ Xác thực WebSocket thành công: userId={}, username={}",
                            userPrincipal.getUserId(), userPrincipal.getUsername());
                } else {
                    log.warn("❌ Token không hợp lệ trong WebSocket preSend.");
                }
            } else {
                log.warn("⚠️ Không có token trong session attributes WebSocket.");
            }
        }
        return message;
    }
}
