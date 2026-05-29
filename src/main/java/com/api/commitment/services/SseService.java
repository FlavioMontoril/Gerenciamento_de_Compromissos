package com.api.commitment.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.api.commitment.domain.dtos.NotificationResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SseService {

    private final Map<UUID, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID userId) {
        // Timeout de 30 minutos (1800000 ms)
        SseEmitter emitter = new SseEmitter(1800000L);

        this.emitters.computeIfAbsent(userId, k -> new ArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError((e) -> removeEmitter(userId, emitter));

        log.info("Novo SSE subscription para o usuário: {}. Total de conexões: {}", userId, this.emitters.get(userId).size());

        return emitter;
    }

    public void sendNotification(UUID userId, NotificationResponseDTO notification) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            List<SseEmitter> deadEmitters = new ArrayList<>();
            
            // Envia para todas as conexões ativas do usuário (pode estar logado em mais de uma aba)
            for (SseEmitter emitter : userEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(notification));
                } catch (IOException e) {
                    deadEmitters.add(emitter);
                }
            }
            
            if (!deadEmitters.isEmpty()) {
                userEmitters.removeAll(deadEmitters);
                if (userEmitters.isEmpty()) {
                    emitters.remove(userId);
                }
                log.info("Removidos {} emissores inativos para o usuário {}", deadEmitters.size(), userId);
            }
        }
    }

    private void removeEmitter(UUID userId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            if (userEmitters.isEmpty()) {
                emitters.remove(userId);
            }
            log.info("SSE emitter removido para o usuário: {}", userId);
        }
    }
}
