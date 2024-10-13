package com.example.steaminvestmentbackend.Controller;

import com.example.steaminvestmentbackend.DTO.UserDTO;
import com.example.steaminvestmentbackend.Entity.SteamToken;
import com.example.steaminvestmentbackend.Service.SteamTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/steam")
public class SteamTokenController {

    private final SteamTokenService steamTokenService;

    @PostMapping("/add")
    public SteamToken addToken(@RequestBody String token, @AuthenticationPrincipal UserDTO userDTO) {
        return steamTokenService.addNew(token, userDTO);
    }

}
