package com.example.steaminvestmentbackend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "registration_tokens")
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "token")
    private String token;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    public ConfirmationToken() {
        token = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        expiresAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(15);
    }

}
