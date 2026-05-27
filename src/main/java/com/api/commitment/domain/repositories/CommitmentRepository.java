package com.api.commitment.domain.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.commitment.domain.entities.Commitment;
import com.api.commitment.domain.entities.User;
import com.api.commitment.domain.types.CommitmentStatus;

import java.time.LocalDateTime;

public interface CommitmentRepository extends JpaRepository<Commitment, UUID> {

    @Query("SELECT c FROM Commitment c WHERE c.owner = :user OR :user MEMBER OF c.participants")
    List<Commitment> findAllByUser(@Param("user") User user);

    @Query("SELECT c FROM Commitment c WHERE (c.owner = :user OR :user MEMBER OF c.participants) AND c.isArchived = :isArchived")
    List<Commitment> findAllByUserAndIsArchived(@Param("user") User user, @Param("isArchived") boolean isArchived);

    List<Commitment> findByReminderDateBeforeAndReminderSentFalseAndStatus(LocalDateTime now, CommitmentStatus status);

    boolean existsByOwnerAndAppointmentDate(User owner, LocalDateTime appointmentDate);
}
