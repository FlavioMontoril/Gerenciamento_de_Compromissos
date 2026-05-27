package com.api.commitment.domain.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.api.commitment.domain.entities.Notification;

public record NotificationResponseDTO(
        UUID id,
        String title,
        String message,
        LocalDateTime createdAt,
        Boolean isRead) {
    
    public NotificationResponseDTO(Notification notification) {
        this(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.getIsRead());
    }
}
