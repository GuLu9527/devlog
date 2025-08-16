package com.giao.devlogpulse.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知WebSocket服务
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/notification/{userId}")
public class NotificationWebSocket {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 存放会话对象
     */
    private static final Map<Long, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        sessionMap.put(userId, session);
        log.info("用户{}建立WebSocket连接，当前在线人数为：{}", userId, sessionMap.size());
        
        try {
            // 发送连接成功消息
            NotificationMessage connectMsg = new NotificationMessage();
            connectMsg.setType("CONNECT");
            connectMsg.setMessage("连接成功");
            connectMsg.setUserId(userId);
            
            session.getBasicRemote().sendText(objectMapper.writeValueAsString(connectMsg));
        } catch (IOException e) {
            log.error("发送连接成功消息失败", e);
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        sessionMap.remove(userId);
        log.info("用户{}断开WebSocket连接，当前在线人数为：{}", userId, sessionMap.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("userId") Long userId) {
        log.info("收到来自用户{}的消息：{}", userId, message);
        
        try {
            // 处理心跳消息
            if ("ping".equals(message)) {
                Session session = sessionMap.get(userId);
                if (session != null && session.isOpen()) {
                    session.getBasicRemote().sendText("pong");
                }
                return;
            }
            
            // 这里可以处理其他类型的消息
            // 比如客户端请求获取未读通知数量等
            
        } catch (IOException e) {
            log.error("处理WebSocket消息失败", e);
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误", error);
    }

    /**
     * 发送通知给指定用户
     *
     * @param userId 用户ID
     * @param notification 通知内容
     */
    public static void sendNotificationToUser(Long userId, Object notification) {
        Session session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                NotificationMessage message = new NotificationMessage();
                message.setType("NOTIFICATION");
                message.setData(notification);
                message.setUserId(userId);
                
                String jsonMessage = objectMapper.writeValueAsString(message);
                session.getBasicRemote().sendText(jsonMessage);
                log.info("成功发送WebSocket通知给用户：{}", userId);
            } catch (IOException e) {
                log.error("发送WebSocket通知失败，用户ID：{}", userId, e);
            }
        } else {
            log.warn("用户{}的WebSocket连接不存在或已关闭", userId);
        }
    }

    /**
     * 发送未读数量更新给指定用户
     *
     * @param userId 用户ID
     * @param unreadCount 未读数量
     */
    public static void sendUnreadCountUpdate(Long userId, Long unreadCount) {
        Session session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                NotificationMessage message = new NotificationMessage();
                message.setType("UNREAD_COUNT");
                message.setMessage(String.valueOf(unreadCount));
                message.setUserId(userId);
                
                String jsonMessage = objectMapper.writeValueAsString(message);
                session.getBasicRemote().sendText(jsonMessage);
                log.info("成功发送未读数量更新给用户：{}，未读数量：{}", userId, unreadCount);
            } catch (IOException e) {
                log.error("发送未读数量更新失败，用户ID：{}", userId, e);
            }
        }
    }

    /**
     * 广播消息给所有在线用户
     *
     * @param message 消息内容
     */
    public static void broadcastMessage(String message) {
        for (Map.Entry<Long, Session> entry : sessionMap.entrySet()) {
            Session session = entry.getValue();
            if (session.isOpen()) {
                try {
                    NotificationMessage broadcastMsg = new NotificationMessage();
                    broadcastMsg.setType("BROADCAST");
                    broadcastMsg.setMessage(message);
                    
                    session.getBasicRemote().sendText(objectMapper.writeValueAsString(broadcastMsg));
                } catch (IOException e) {
                    log.error("广播消息失败，用户ID：{}", entry.getKey(), e);
                }
            }
        }
        log.info("广播消息给{}个在线用户：{}", sessionMap.size(), message);
    }

    /**
     * 获取当前在线用户数量
     *
     * @return 在线用户数量
     */
    public static int getOnlineCount() {
        return sessionMap.size();
    }

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    public static boolean isUserOnline(Long userId) {
        Session session = sessionMap.get(userId);
        return session != null && session.isOpen();
    }

    /**
     * 通知消息实体类
     */
    public static class NotificationMessage {
        private String type; // 消息类型：CONNECT, NOTIFICATION, UNREAD_COUNT, BROADCAST
        private String message; // 消息内容
        private Object data; // 数据对象
        private Long userId; // 用户ID
        private Long timestamp = System.currentTimeMillis(); // 时间戳

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }
}