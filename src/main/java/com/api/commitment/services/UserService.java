package com.api.commitment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.commitment.domain.dtos.RegisterDTO;
import com.api.commitment.domain.dtos.UserResponseDTO;
import com.api.commitment.domain.entities.User;
import com.api.commitment.domain.repositories.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    public UserResponseDTO findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return new UserResponseDTO(user);
    }

    public User createUser(RegisterDTO data) {
        if (this.userRepository.findByEmail(data.email()) != null) {
            throw new RuntimeException("Usuário já existe com este email");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = User.builder()
                .name(data.name())
                .email(data.email())
                .password(encryptedPassword)
                .build();

        return this.userRepository.save(newUser);
    }

    public void updateFcmToken(java.util.UUID userId, String fcmToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }
}
