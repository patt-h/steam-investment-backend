package com.example.steaminvestmentbackend.Service;

import com.example.steaminvestmentbackend.DTO.CredentialsDTO;
import com.example.steaminvestmentbackend.DTO.SignUpDTO;
import com.example.steaminvestmentbackend.DTO.UserDTO;
import com.example.steaminvestmentbackend.Entity.ConfirmationToken;
import com.example.steaminvestmentbackend.Entity.Settings;
import com.example.steaminvestmentbackend.Entity.User;
import com.example.steaminvestmentbackend.Exceptions.AppException;
import com.example.steaminvestmentbackend.Mappers.UserMapper;
import com.example.steaminvestmentbackend.Repository.ConfirmationTokenRepository;
import com.example.steaminvestmentbackend.Repository.SettingsRepository;
import com.example.steaminvestmentbackend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;

    public UserDTO login(CredentialsDTO credentialsDTO) {
        User user = userRepository.findByUsername(credentialsDTO.getUsername())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDTO.getPassword()), user.getPassword())) {
            if (!user.getEnabled()) {
                throw new AppException("Account registration not verified", HttpStatus.BAD_REQUEST);
            }
            return userMapper.toUserDto(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> register(SignUpDTO userDTO) {
        Optional<User> optionalUser = userRepository.findByUsername(userDTO.getUsername());
        if (optionalUser.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }

        Optional<User> secondOptionalUser = userRepository.findByEmail(userDTO.getEmail());
        if (secondOptionalUser.isPresent()) {
            throw new AppException("Email already exists", HttpStatus.BAD_REQUEST);
        }

        User user = userMapper.signUpToUser(userDTO);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDTO.getPassword())));
        user.setEnabled(false);

        User savedUser = userRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setUserId(savedUser.getId());

        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(savedUser.getEmail());
        mailMessage.setSubject("Hey! Confirm your registration!");
        mailMessage.setText("""
                You are one step away from tracking your budget.
                Click the link below to confirm your account registration!
                
                """ + "http://localhost:3000/confirm-account?token=" + confirmationToken.getToken());
        emailService.sendEmail(mailMessage);

        return ResponseEntity.ok("Complete the registration using the link that was sent to your email address");
    }

    public UserDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }

    public ResponseEntity<?> confirmEmail(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);

        if (confirmationToken != null) {
            User user = userRepository.findById(confirmationToken.getUserId())
                    .orElseThrow(() -> new AppException("Couldn't find user with provided ID", HttpStatus.NOT_FOUND));

            if (user.getEnabled()) {
                throw new AppException("Account is already enabled", HttpStatus.BAD_REQUEST);
            }

            if (confirmationToken.getExpiresAt().isBefore(ChronoLocalDateTime.from(LocalDateTime.now()))) {
                throw new AppException("Token has expired", HttpStatus.FORBIDDEN);
            }

            user.setEnabled(true);
            userRepository.save(user);

            Settings settings = Settings.builder()
                            .userId(user.getId())
                            .currency("USD")
                            .build();
            settingsRepository.save(settings);

            confirmationToken.setConfirmedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            confirmationTokenRepository.save(confirmationToken);
        } else {
            throw new AppException("Token doesn't exist", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok("Your account has been successfully verified!");
    }

    public ResponseEntity<?> resendToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);

        if (confirmationToken != null) {
            User user = userRepository.findById(confirmationToken.getUserId())
                    .orElseThrow(() -> new AppException("Couldn't find user with provided ID", HttpStatus.NOT_FOUND));

            if (user.getEnabled()) {
                throw new AppException("Account is already enabled", HttpStatus.BAD_REQUEST);
            }

            ConfirmationToken newConfirmationToken = new ConfirmationToken();
            newConfirmationToken.setUserId(user.getId());
            confirmationTokenRepository.save(newConfirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Hey! Here is your new registration link!");
            mailMessage.setText("""
                You are one step away from tracking your budget.
                Click the link below to confirm your account registration!
                
                """ + "http://localhost:3000/confirm-account?token=" + newConfirmationToken.getToken());
            emailService.sendEmail(mailMessage);
        } else {
            throw new AppException("Token doesn't exist", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok("New token has been successfully sent");

    }
}