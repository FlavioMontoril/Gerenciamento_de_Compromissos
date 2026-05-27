package com.api.commitment.services;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.commitment.domain.dtos.CommitmentRequestDTO;
import com.api.commitment.domain.dtos.CommitmentResponseDTO;
import com.api.commitment.domain.entities.Commitment;
import com.api.commitment.domain.entities.User;
import com.api.commitment.domain.repositories.CommitmentRepository;
import com.api.commitment.domain.repositories.UserRepository;
import com.api.commitment.domain.types.CommitmentStatus;

@Service
public class CommitmentService {

    @Autowired
    private CommitmentRepository commitmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CommitmentResponseDTO create(CommitmentRequestDTO data, User owner) {

        boolean alreadyExists = commitmentRepository.existsByOwnerAndAppointmentDate(owner, data.appointmentDate());

        if (alreadyExists) {
            throw new RuntimeException("Você já possui um compromisso agendado para este dia e horário.");
        }

        var participants = new HashSet<User>();

        if (data.participantIds() != null && !data.participantIds().isEmpty()) {
            participants.addAll(userRepository.findAllById(data.participantIds()));
        }

        Commitment commitment = Commitment.builder()
                .description(data.description())
                .appointmentDate(data.appointmentDate())
                .reminderDate(data.reminderDate())
                .owner(owner)
                .participants(participants)
                .status(CommitmentStatus.PENDING)
                .isArchived(false)
                .build();

        return new CommitmentResponseDTO(commitmentRepository.save(commitment));
    }

    @Transactional
    public void complete(UUID id, User user) {
        Commitment commitment = findById(id);

        validateOwnershipOrParticipation(commitment, user);

        if (commitment.getStatus() != CommitmentStatus.PENDING) {
            throw new RuntimeException("Apenas compromissos pendentes podem ser marcados como completos.");
        }

        if (LocalDateTime.now().isBefore(commitment.getAppointmentDate())) {
            throw new RuntimeException(
                    "Somente após a data e horário do compromisso ele poderá ser marcado como completo.");
        }

        commitment.complete();
        commitmentRepository.save(commitment);
    }

    @Transactional
    public void cancel(UUID id, User user) {
        Commitment commitment = findById(id);

        validateOwnershipOrParticipation(commitment, user);

        if (commitment.getStatus() != CommitmentStatus.PENDING) {
            throw new RuntimeException("Apenas compromissos pendentes podem ser cancelados.");
        }

        if (LocalDateTime.now().isAfter(commitment.getAppointmentDate())) {
            throw new RuntimeException("Não é possível cancelar um compromisso que já deveria ter ocorrido.");
        }

        commitment.cancel();
        commitmentRepository.save(commitment);
    }

    @Transactional
    public void archive(UUID id, User user) {
        Commitment commitment = findById(id);

        validateOwnershipOrParticipation(commitment, user);

        if (commitment.getStatus() != CommitmentStatus.COMPLETED
                && commitment.getStatus() != CommitmentStatus.CANCELED) {
            throw new RuntimeException(
                    "Só poderá ser arquivado o compromisso quando estiver com status completo ou cancelado.");
        }

        commitment.archive();
        commitmentRepository.save(commitment);
    }

    @Transactional
    public void unarchive(UUID id, User user) {
        Commitment commitment = findById(id);

        validateOwnershipOrParticipation(commitment, user);

        if (commitment.getStatus() != CommitmentStatus.COMPLETED
                && commitment.getStatus() != CommitmentStatus.CANCELED) {
            throw new RuntimeException(
                    "Só poderá ser desarquivado o compromisso quando estiver com status completo ou cancelado.");
        }

        commitment.unarchive();
        commitmentRepository.save(commitment);
    }

    private void validateOwnershipOrParticipation(Commitment commitment, User user) {
        boolean isOwner = commitment.getOwner().getId().equals(user.getId());
        boolean isParticipant = commitment.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(user.getId()));

        if (!isOwner && !isParticipant) {
            throw new RuntimeException("Você não tem permissão para alterar este compromisso.");
        }
    }

    public List<CommitmentResponseDTO> findAllByUser(User user, Boolean isArchived) {
        List<Commitment> commitments;
        if (isArchived != null) {
            commitments = commitmentRepository.findAllByUserAndIsArchived(user, isArchived);
        } else {
            commitments = commitmentRepository.findAllByUser(user);
        }

        return commitments.stream()
                .map(CommitmentResponseDTO::new)
                .collect(Collectors.toList());
    }

    private Commitment findById(UUID id) {
        return commitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compromisso não encontrado"));
    }
}
