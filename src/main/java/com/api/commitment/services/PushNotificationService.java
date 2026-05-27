package com.api.commitment.services;

import org.springframework.stereotype.Service;

import com.api.commitment.domain.entities.User;
// import com.google.firebase.messaging.FirebaseMessaging;
// import com.google.firebase.messaging.Message;
// import com.google.firebase.messaging.Notification;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PushNotificationService {

    public void sendPushNotification(User user, String title, String message) {
        if (user.getFcmToken() == null || user.getFcmToken().isEmpty()) {
            log.warn("User {} has no FCM token. Push notification skipped.", user.getEmail());
            return;
        }

        try {
            // This will only work after you initialize Firebase in a @Configuration class
            // with your serviceAccountKey.json
            
            /* 
            Message fcmMessage = Message.builder()
                    .setToken(user.getFcmToken())
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(message)
                            .build())
                    .build();

            FirebaseMessaging.getInstance().send(fcmMessage);
            */

            log.info("PUSH READY to be sent via Firebase to {}: {} - {}", user.getFcmToken(), title, message);
            
        } catch (Exception e) {
            log.error("Error sending push notification to user {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
