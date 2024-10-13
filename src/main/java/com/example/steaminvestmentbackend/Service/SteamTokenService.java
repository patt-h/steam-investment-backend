package com.example.steaminvestmentbackend.Service;

import com.example.steaminvestmentbackend.DTO.UserDTO;
import com.example.steaminvestmentbackend.Entity.SteamToken;
import com.example.steaminvestmentbackend.Exceptions.AppException;
import com.example.steaminvestmentbackend.Repository.SteamTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class SteamTokenService {

    private final SteamTokenRepository steamTokenRepository;

    public SteamToken addNew(String token, @AuthenticationPrincipal UserDTO userDTO) {
        try {
            if (userDTO.getId() != 1) {
                throw new AppException("Access denied", HttpStatus.FORBIDDEN);
            } else {
                String[] chunks = token.split("\\.");
                String decodedPayload = new String(Base64.getDecoder().decode(chunks[1]));

                ObjectMapper objectMapper = new ObjectMapper();
                Map jsonMap = objectMapper.readValue(decodedPayload, Map.class);

                long expTimestamp = ((Number) jsonMap.get("exp")).longValue();
                LocalDateTime expirationDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(expTimestamp), ZoneOffset.UTC);

                SteamToken steamToken = new SteamToken();
                steamToken.setToken(token);
                steamToken.setExpirationDate(expirationDate);

                return steamTokenRepository.save(steamToken);
            }
        } catch (Exception e) {
            throw new AppException("An error occurred while adding new token", HttpStatus.BAD_REQUEST);
        }
    }
}
