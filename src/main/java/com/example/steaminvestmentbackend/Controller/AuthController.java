package com.example.steaminvestmentbackend.Controller;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;


import com.example.steaminvestmentbackend.Config.UserAuthenticationProvider;
import com.example.steaminvestmentbackend.DTO.CredentialsDTO;
import com.example.steaminvestmentbackend.DTO.SignUpDTO;
import com.example.steaminvestmentbackend.DTO.TokenDTO;
import com.example.steaminvestmentbackend.DTO.UserDTO;
import com.example.steaminvestmentbackend.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody @Valid CredentialsDTO credentialsDto) {
        UserDTO userDto = userService.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getUsername()));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid SignUpDTO user) {
        return userService.register(user);
    }

    @PostMapping("/confirm-account")
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String token) {
        return userService.confirmEmail(token);
    }

    @PostMapping("/verify")
    public Object isValid(@RequestBody @Valid TokenDTO token) {
        try {
            return userAuthenticationProvider.validateToken(token.getToken()).getPrincipal();
        } catch (JWTDecodeException | SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
