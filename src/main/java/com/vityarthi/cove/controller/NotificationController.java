package com.vityarthi.cove.controller;

import com.vityarthi.cove.model.Notification;
import com.vityarthi.cove.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> list = notificationService.getUserNotifications(userId);
        long unreadCount = notificationService.getUnreadCount(userId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("notifications", list);
        resp.put("unreadCount", unreadCount);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
