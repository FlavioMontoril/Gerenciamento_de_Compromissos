package com.api.commitment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.commitment.domain.dtos.RegisterDTO;
import com.api.commitment.domain.entities.User;
import com.api.commitment.domain.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
}
