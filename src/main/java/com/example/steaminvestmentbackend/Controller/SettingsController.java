package com.example.steaminvestmentbackend.Controller;

import com.example.steaminvestmentbackend.DTO.UserDTO;
import com.example.steaminvestmentbackend.Entity.Settings;
import com.example.steaminvestmentbackend.Service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/settings")
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping("/all")
    public Settings getSettings(@AuthenticationPrincipal UserDTO userDTO) {
        return settingsService.getSettings(userDTO);
    }

    @PatchMapping("/update")
    public Settings updateSettings(@RequestBody Settings settings, @AuthenticationPrincipal UserDTO userDTO) {
        return settingsService.update(settings, userDTO);
    }
}
