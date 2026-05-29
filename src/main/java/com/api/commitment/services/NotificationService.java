package com.api.commitment.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.commitment.domain.dtos.NotificationResponseDTO;
import com.api.commitment.domain.entities.Commitment;
import com.api.commitment.domain.entities.Notification;
import com.api.commitment.domain.entities.User;
import com.api.commitment.domain.entities.UserNotification;
import com.api.commitment.domain.repositories.CommitmentRepository;
import com.api.commitment.domain.repositories.NotificationRepository;
import com.api.commitment.domain.repositories.UserNotificationRepository;
import com.api.commitment.domain.types.CommitmentStatus;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private CommitmentRepository commitmentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private SseService sseService;

    // @Scheduled(fixedRate = 60000) // Runs every minute
    @Transactional
    public void sendReminders() {
        List<Commitment> commitmentsToNotify = commitmentRepository
                .findByReminderDateBeforeAndReminderSentFalseAndStatus(LocalDateTime.now(), CommitmentStatus.PENDING);

        for (Commitment commitment : commitmentsToNotify) {
            processNotification(commitment);
            commitment.setReminderSent(true);
            commitmentRepository.save(commitment);
        }
    }

    private void processNotification(Commitment commitment) {
        String content = String.format("Seu compromisso '%s' ocorrerá em breve: %s", 
                commitment.getDescription(), 
                commitment.getAppointmentDate());

        List<User> targets = new ArrayList<>();
        targets.add(commitment.getOwner());
        targets.addAll(commitment.getParticipants());

        createAndSendToMany(null, targets, content);
    }

    @Transactional
    public void createAndSend(User triggeredBy, User receivedBy, String content) {
        createAndSendToMany(triggeredBy, List.of(receivedBy), content);
    }

    @Transactional
    public void createAndSendToMany(User triggeredBy, List<User> receivedByUsers, String content) {
        Notification notification = Notification.builder()
                .content(content)
                .build();
        
        Notification savedNotification = notificationRepository.save(notification);

        for (User user : receivedByUsers) {
            UserNotification userNotification = UserNotification.builder()
                    .receivedBy(user)
                    .triggeredBy(triggeredBy)
                    .notification(savedNotification)
                    .isRead(false)
                    .build();
            
            UserNotification savedUserNotification = userNotificationRepository.save(userNotification);

            // Dispara via SSE em tempo real
            sseService.sendNotification(user.getId(), new NotificationResponseDTO(savedUserNotification));
            
            pushNotificationService.sendPushNotification(user, "Notificação", content);
        }
        
        log.info("Notification created and sent to {} users", receivedByUsers.size());
    }

    public void notifyCommitmentEvent(Commitment commitment, String content, User triggeredBy) {
        List<User> targets = new ArrayList<>();
        
        // Adiciona o dono se não for quem disparou a ação
        if (triggeredBy == null || !commitment.getOwner().getId().equals(triggeredBy.getId())) {
            targets.add(commitment.getOwner());
        }

        // Adiciona participantes se não forem quem disparou a ação
        for (User participant : commitment.getParticipants()) {
            if (triggeredBy == null || !participant.getId().equals(triggeredBy.getId())) {
                targets.add(participant);
            }
        }

        if (!targets.isEmpty()) {
            createAndSendToMany(triggeredBy, targets, content);
        }
    }
}
