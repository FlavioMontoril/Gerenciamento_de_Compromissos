package com.api.commitment.domain.dtos;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequestDTO(
        @NotBlank(message = "O refresh token é obrigatório") String refreshToken) {
}
