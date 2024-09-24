package com.example.steaminvestmentbackend.Service;

import com.example.steaminvestmentbackend.DTO.UserDTO;
import com.example.steaminvestmentbackend.Entity.ItemList;
import com.example.steaminvestmentbackend.Entity.Settings;
import com.example.steaminvestmentbackend.Exceptions.AppException;
import com.example.steaminvestmentbackend.Repository.ItemListRepository;
import com.example.steaminvestmentbackend.Repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SettingsService {

    private final SettingsRepository settingsRepository;
    private final ItemListRepository itemListRepository;

    public Settings getSettings(@AuthenticationPrincipal UserDTO userDTO) {
        return settingsRepository.findByUserId(userDTO.getId());
    }

    public Settings update(Settings settings, @AuthenticationPrincipal UserDTO userDTO) {
        // If request doesn't have id
        if (settings.getId() == null) {
            throw new AppException("Settings can't be null", HttpStatus.BAD_REQUEST);
        } else {
            // Find current settings to get data
            Optional<Settings> settingToUpdate = settingsRepository.findById(settings.getId());
            if (settingToUpdate.isEmpty()) {
                throw new AppException("Couldn't find settings with provided id", HttpStatus.NOT_FOUND);
            } else if (!settingToUpdate.get().getUserId().equals(userDTO.getId())) {
                // If change of settings affects data that doesn't belong to user
                throw new AppException("Access denied, user id doesn't match settings user id", HttpStatus.FORBIDDEN);
            } else {
                settings.setUserId(userDTO.getId());
                // Without goal in request
                if (settings.getGoalName() == null) {
                    settings.setGoalName(settingToUpdate.get().getGoalName());
                    settings.setGoalItemId(settingToUpdate.get().getGoalItemId());
                } else if (settings.getGoalName().equals(settingToUpdate.get().getGoalName())) {
                    // Goal same as existing
                    settings.setGoalItemId(settingToUpdate.get().getGoalItemId());
                } else {
                    // If user change goal, first check if provided item exists
                    ItemList item = itemListRepository.findByMarketHashName(settings.getGoalName());
                    if (item == null) {
                        throw new AppException("Couldn't find item with provided name", HttpStatus.BAD_REQUEST);
                    } else {
                        settings.setGoalItemId(item.getId());
                    }
                }
                // Without currency in JSON
                if (settings.getCurrency() == null) {
                    settings.setCurrency(settingToUpdate.get().getCurrency());
                }

                return settingsRepository.save(settings);
            }
        }
    }
}
