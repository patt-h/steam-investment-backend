package com.example.steaminvestmentbackend.Repository;

import com.example.steaminvestmentbackend.Entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
    Settings findByUserId(Long userId);

}
