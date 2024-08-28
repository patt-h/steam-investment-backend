package com.example.steaminvestmentbackend.Controller;

import com.example.steaminvestmentbackend.Entity.ItemHistory;
import com.example.steaminvestmentbackend.Service.ItemHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/history")
public class ItemHistoryController {

    private final ItemHistoryService itemHistoryService;

    @GetMapping("/{id}")
    public List<ItemHistory> getAll(@PathVariable Long id) throws IOException {
        return itemHistoryService.getItemHistory(id);
    }
}
