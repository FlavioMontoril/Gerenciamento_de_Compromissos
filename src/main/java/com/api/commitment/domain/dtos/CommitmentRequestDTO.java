package com.api.commitment.domain.dtos;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommitmentRequestDTO(
        @NotBlank(message = "A descrição é obrigatória") String description,
        @NotNull(message = "A data do compromisso é obrigatória") @FutureOrPresent(message = "A data do compromisso não pode ser no passado") LocalDateTime appointmentDate,
        LocalDateTime reminderDate,
        Set<UUID> participantIds) {
}
