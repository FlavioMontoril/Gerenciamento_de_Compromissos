package com.api.commitment.domain.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.commitment.domain.entities.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
