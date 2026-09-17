package com.vityarthi.cove.service;

import com.vityarthi.cove.model.Notification;
import com.vityarthi.cove.model.User;
import com.vityarthi.cove.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service managing user notifications and background broadcast dispatching.
 * Demonstrates:
 * - CSE2006 Unit 3 & 4: Thread-safe collections (ConcurrentLinkedQueue) and concurrency handling.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    
    // Concurrent, thread-safe memory queue for real-time dispatching
    private final ConcurrentLinkedQueue<Notification> dispatchQueue = new ConcurrentLinkedQueue<>();

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification sendNotification(User recipient, String title, String message, String type) {
        Notification notification = new Notification(recipient, title, message, type);
        Notification saved = notificationRepository.save(notification);
        dispatchQueue.offer(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByRecipientIdAndReadFalseOrderByCreatedAtDesc(userId);
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }
}
