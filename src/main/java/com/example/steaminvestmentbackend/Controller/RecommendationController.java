package com.example.steaminvestmentbackend.Controller;

import com.example.steaminvestmentbackend.DTO.UserDTO;
import com.example.steaminvestmentbackend.Service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

//    @GetMapping("/get")
//    public List<Map<String, Object>> getRecommendations(@AuthenticationPrincipal UserDTO userDTO) {
//        //return recommendationService.recommendItemsForUser(userDTO);
//        return recommendationService.recommendItemsForUser(userDTO);
//    }

    @GetMapping("/get")
    public List<String> getRecommendations(@AuthenticationPrincipal UserDTO userDTO) {
        //return recommendationService.recommendItemsForUser(userDTO);
        return recommendationService.getRecommendations(userDTO, 2);
    }
}
