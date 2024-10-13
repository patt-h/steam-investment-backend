package com.example.steaminvestmentbackend.Repository;


import com.example.steaminvestmentbackend.Entity.SteamToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SteamTokenRepository extends JpaRepository<SteamToken, Long> {

    SteamToken findFirstByOrderByIdDesc();

}
