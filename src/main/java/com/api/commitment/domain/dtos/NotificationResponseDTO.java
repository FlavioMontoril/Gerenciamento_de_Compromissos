package com.api.commitment.domain.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.api.commitment.domain.entities.UserNotification;

public record NotificationResponseDTO(
        UUID id,
        String content,
        String triggeredByName,
        LocalDateTime createdAt,
        Boolean isRead) {
    
    public NotificationResponseDTO(UserNotification userNotification) {
        this(
                userNotification.getId(),
                userNotification.getNotification().getContent(),
                userNotification.getTriggeredBy() != null ? userNotification.getTriggeredBy().getName() : "Sistema",
                userNotification.getCreatedAt(),
                userNotification.getIsRead());
    }
}
