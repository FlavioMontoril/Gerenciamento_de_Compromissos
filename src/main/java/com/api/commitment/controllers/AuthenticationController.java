package com.api.commitment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.commitment.domain.dtos.AuthenticationDTO;
import com.api.commitment.domain.dtos.LoginResponseDTO;
import com.api.commitment.domain.dtos.TokenRefreshRequestDTO;
import com.api.commitment.domain.entities.User;
import com.api.commitment.domain.repositories.UserRepository;
import com.api.commitment.infra.security.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data) {
        System.out.println("AUTH -- " + data);
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var user = (User) auth.getPrincipal();
        var token = tokenService.generateToken(user);
        var refreshToken = tokenService.generateRefreshToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(token, refreshToken, user.getName(), user.getEmail()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Valid TokenRefreshRequestDTO data) {
        var email = tokenService.validateToken(data.refreshToken());
        if (email.isEmpty()) {
            return ResponseEntity.status(401).body("Refresh token inválido ou expirado");
        }

        var user = (User) userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(401).body("Usuário não encontrado");
        }

        var token = tokenService.generateToken(user);
        var refreshToken = tokenService.generateRefreshToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(token, refreshToken, user.getName(), user.getEmail()));
    }
}
