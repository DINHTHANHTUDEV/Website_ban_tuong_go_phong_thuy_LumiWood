package com.example.websitebantuonggolumiwood.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebsocketConfig.class);

    private final WebSocketSecurityInterceptor webSocketSecurityInterceptor;

    public WebsocketConfig(WebSocketSecurityInterceptor webSocketSecurityInterceptor) {
        this.webSocketSecurityInterceptor = webSocketSecurityInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ⚠️ Dùng WebSocket thuần (không dùng SockJS)
        registry.addEndpoint("/ws")
                .addInterceptors(webSocketSecurityInterceptor) // ✅ Dùng Handshake Interceptor để kiểm tra JWT
                .setAllowedOriginPatterns("*"); // Cho phép mọi domain (hoặc chỉnh lại nếu triển khai production)
        log.info("✅ STOMP endpoint '/ws' đã được đăng ký (WebSocket thuần).");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Server → Client
        registry.enableSimpleBroker("/topic", "/queue");

        // Client → Server
        registry.setApplicationDestinationPrefixes("/app");

        // ✅ Thêm dòng này để convertAndSendToUser hoạt động đúng
        registry.setUserDestinationPrefix("/user");

        log.info("✅ Simple message broker đã được cấu hình với prefix '/topic'.");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketSecurityInterceptor); // ✅ Gán Auth cho tin nhắn
        log.info("✅ Đã đăng ký WebSocketSecurityInterceptor vào inbound channel.");
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        log.info("✅ Client đã kết nối tới WebSocket. SessionId: {}", sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        log.info("❌ Client đã ngắt kết nối WebSocket. SessionId: {}", sessionId);
    }
}
