package com.api.commitment.domain.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.api.commitment.domain.types.CommitmentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "commitments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Commitment {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Setter(AccessLevel.NONE)
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    // @Column(name = "owner_id", updatable = false, nullable = false)
    // private String ownerId;
    private User owner;

    @Setter
    @Column(nullable = false)
    private String description;

    // appointmentDate Data que ocorre o compromisso
    @Setter
    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;

    @Setter
    @Column(name = "reminder_date", nullable = true)
    private LocalDateTime reminderDate;

    // @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommitmentStatus status = CommitmentStatus.PENDING;

    @Builder.Default
    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;

    @Setter
    @Builder.Default
    @Column(name = "reminder_sent", nullable = false)
    private Boolean reminderSent = false;

    @ManyToMany
    @JoinTable(name = "commitments_participants", joinColumns = @JoinColumn(name = "commitment_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    private Set<User> participants = new HashSet<>();

    public void complete() {
        this.status = CommitmentStatus.COMPLETED;
    }

    public void cancel() {
        this.status = CommitmentStatus.CANCELED;
    }

    public void archive() {
        this.isArchived = true;
    }

    public void unarchive() {
        this.isArchived = false;
    }
}
