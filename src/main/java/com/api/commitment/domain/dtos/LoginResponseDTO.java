package com.api.commitment.domain.dtos;

public record LoginResponseDTO(String token, String refreshToken, String name, String email) {
}
