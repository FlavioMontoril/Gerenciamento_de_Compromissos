package com.api.commitment.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.commitment.domain.dtos.NotificationResponseDTO;
import com.api.commitment.domain.entities.Notification;
import com.api.commitment.domain.entities.User;
import com.api.commitment.domain.repositories.NotificationRepository;

@RestController
@RequestMapping("api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> listAll(@AuthenticationPrincipal User user) {
        var notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        var response = notifications.stream().map(NotificationResponseDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Você não tem permissão para alterar esta notificação.");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.noContent().build();
    }
}
