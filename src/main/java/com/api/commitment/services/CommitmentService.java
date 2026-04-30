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
    public void complete(UUID id) {
        Commitment commitment = findById(id);

        if (commitment.getStatus() == CommitmentStatus.CANCELED) {
            throw new RuntimeException("Não é possível completar um compromisso cancelado.");
        }
        
        if (LocalDateTime.now().isBefore(commitment.getAppointmentDate())) {
            throw new RuntimeException("Somente após a data e horário do compromisso ele poderá ser marcado como completo.");
        }

        commitment.complete();
        commitmentRepository.save(commitment);
    }

    @Transactional
    public void cancel(UUID id) {
        Commitment commitment = findById(id);

        if (commitment.getStatus() == CommitmentStatus.COMPLETED) {
            throw new RuntimeException("Não é possível cancelar um compromisso já finalizado.");
        }
        
        if (commitment.getStatus() == CommitmentStatus.CANCELED) {
            throw new RuntimeException("O compromisso já está cancelado.");
        }

        commitment.cancel();
        commitmentRepository.save(commitment);
    }

    @Transactional
    public void archive(UUID id) {
        Commitment commitment = findById(id);

        if (commitment.getStatus() != CommitmentStatus.COMPLETED && commitment.getStatus() != CommitmentStatus.CANCELED) {
            throw new RuntimeException("Só poderá ser arquivado o compromisso quando estiver com status completo ou cancelado.");
        }

        commitment.archive();
        commitmentRepository.save(commitment);
    }

    public List<CommitmentResponseDTO> findAll() {
        return commitmentRepository.findAll().stream()
                .map(CommitmentResponseDTO::new)
                .collect(Collectors.toList());
    }

    private Commitment findById(UUID id) {
        return commitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compromisso não encontrado"));
    }
}
