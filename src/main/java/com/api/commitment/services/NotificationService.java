package com.api.commitment.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.commitment.domain.entities.Commitment;
import com.api.commitment.domain.entities.User;
import com.api.commitment.domain.repositories.CommitmentRepository;
import com.api.commitment.domain.types.CommitmentStatus;

import com.api.commitment.domain.entities.Notification;
import com.api.commitment.domain.repositories.NotificationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private CommitmentRepository commitmentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    // @Scheduled(fixedRate = 60000) // Runs every minute
    @Transactional
    public void sendReminders() {
        // log.info("Checking for reminders at {}", LocalDateTime.now());
        
        List<Commitment> commitmentsToNotify = commitmentRepository
                .findByReminderDateBeforeAndReminderSentFalseAndStatus(LocalDateTime.now(), CommitmentStatus.PENDING);

        for (Commitment commitment : commitmentsToNotify) {
            processNotification(commitment);
            commitment.setReminderSent(true);
            commitmentRepository.save(commitment);
        }
    }

    private void processNotification(Commitment commitment) {
        String title = "Lembrete de Compromisso";
        String message = String.format("Seu compromisso '%s' ocorrerá em breve: %s", 
                commitment.getDescription(), 
                commitment.getAppointmentDate());

        // Notify Owner
        createAndSend(commitment.getOwner(), title, message);

        // Notify Participants
        for (User participant : commitment.getParticipants()) {
            createAndSend(participant, title, message);
        }
    }

    private void createAndSend(User user, String title, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .isRead(false)
                .build();
        
        notificationRepository.save(notification);
        pushNotificationService.sendPushNotification(user, title, message);
        
        log.info("Notification created and push triggered for user: {}", user.getEmail());
    }
}
