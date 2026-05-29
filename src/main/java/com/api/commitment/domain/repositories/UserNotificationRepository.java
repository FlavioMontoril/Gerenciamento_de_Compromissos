package com.api.commitment.domain.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.commitment.domain.entities.User;
import com.api.commitment.domain.entities.UserNotification;

public interface UserNotificationRepository extends JpaRepository<UserNotification, UUID> {
    List<UserNotification> findByReceivedByOrderByCreatedAtDesc(User receivedBy);
}
