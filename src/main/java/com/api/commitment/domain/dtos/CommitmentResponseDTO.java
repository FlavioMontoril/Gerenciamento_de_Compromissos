package com.api.commitment.domain.dtos;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.api.commitment.domain.entities.Commitment;
import com.api.commitment.domain.types.CommitmentStatus;

public record CommitmentResponseDTO(
        UUID id,
        String description,
        LocalDateTime appointmentDate,
        LocalDateTime reminderDate,
        CommitmentStatus status,
        Boolean isArchived,
        String ownerName,
        Set<String> participantsNames) {

    public CommitmentResponseDTO(Commitment commitment) {
        this(
                commitment.getId(),
                commitment.getDescription(),
                commitment.getAppointmentDate(),
                commitment.getReminderDate(),
                commitment.getStatus(),
                commitment.getIsArchived(),
                commitment.getOwner().getName(),
                commitment.getParticipants().stream().map(p -> p.getName()).collect(Collectors.toSet()));
    }
}
