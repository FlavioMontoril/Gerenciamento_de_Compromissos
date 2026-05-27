package com.api.commitment.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.commitment.domain.dtos.CommitmentRequestDTO;
import com.api.commitment.domain.dtos.CommitmentResponseDTO;
import com.api.commitment.domain.entities.User;
import com.api.commitment.services.CommitmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/commitments")
public class CommitmentController {

    @Autowired
    private CommitmentService commitmentService;

    @PostMapping
    public ResponseEntity<CommitmentResponseDTO> create(
            @RequestBody @Valid CommitmentRequestDTO data,
            @AuthenticationPrincipal User owner) {
        var response = commitmentService.create(data, owner);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommitmentResponseDTO>> listAll(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Boolean archived) {
        return ResponseEntity.ok(commitmentService.findAllByUser(user, archived));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        commitmentService.complete(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        commitmentService.cancel(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<Void> archive(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        commitmentService.archive(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/unarchive")
    public ResponseEntity<Void> unarchive(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        commitmentService.unarchive(id, user);
        return ResponseEntity.noContent().build();
    }
}
