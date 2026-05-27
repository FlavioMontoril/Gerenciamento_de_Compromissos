package com.api.commitment.domain.dtos;

import java.util.UUID;
import com.api.commitment.domain.entities.User;

public record UserResponseDTO(UUID id, String name, String email) {
    public UserResponseDTO(User user) {
        this(user.getId(), user.getName(), user.getEmail());
    }
}
