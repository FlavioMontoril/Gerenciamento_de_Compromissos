package com.api.commitment.domain.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
        @NotBlank(message = "O nome é obrigatório") String name,
        @NotBlank(message = "O email é obrigatório") @Email(message = "Formato de email inválido") String email,
        @NotBlank(message = "A senha é obrigatória") String password) {
}
