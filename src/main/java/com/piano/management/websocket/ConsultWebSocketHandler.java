package com.piano.management.websocket;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简易咨询通道：同一 room 内广播文本；仅学生在线时回复系统提示。
 */
@Component
public class ConsultWebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, Set<WebSocketSession>> ROOMS = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String room = String.valueOf(session.getAttributes().getOrDefault("room", "default"));
        ROOMS.computeIfAbsent(room, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String room = String.valueOf(session.getAttributes().getOrDefault("room", "default"));
        Set<WebSocketSession> set = ROOMS.get(room);
        if (set != null) {
            set.remove(session);
            if (set.isEmpty()) {
                ROOMS.remove(room);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String room = String.valueOf(session.getAttributes().getOrDefault("room", "default"));
        Set<WebSocketSession> set = ROOMS.get(room);
        if (set == null) {
            return;
        }
        String payload = message.getPayload();
        int others = 0;
        for (WebSocketSession s : set) {
            if (s != session && s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
                others++;
            }
        }
        if (others == 0 && session.isOpen()) {
            JSONObject sys = new JSONObject();
            sys.put("role", "system");
            sys.put("text", "对方暂未连接，消息已保存为留言。");
            session.sendMessage(new TextMessage(sys.toJSONString()));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
}
