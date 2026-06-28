package com.piano.management.config;

import com.piano.management.websocket.ConsultHandshakeInterceptor;
import com.piano.management.websocket.ConsultWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

@Configuration
@EnableWebSocket
public class WebSocketConsultConfig implements WebSocketConfigurer {

    @Resource
    private ConsultWebSocketHandler consultWebSocketHandler;
    @Resource
    private ConsultHandshakeInterceptor consultHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(consultWebSocketHandler, "/ws/consult")
                .addInterceptors(consultHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}
