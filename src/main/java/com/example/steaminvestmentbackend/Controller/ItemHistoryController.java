package com.example.steaminvestmentbackend.Controller;

import com.example.steaminvestmentbackend.Entity.ItemHistory;
import com.example.steaminvestmentbackend.Service.ItemHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/history")
public class ItemHistoryController {

    private final ItemHistoryService itemHistoryService;

    @GetMapping("/all/{id}")
    public List<ItemHistory> getAll(@PathVariable Long id) throws IOException {
        return itemHistoryService.getItemHistory(id);
    }

    @GetMapping("/all")
    public List<ItemHistory> getAllByName(@RequestParam String name) throws IOException {
        return itemHistoryService.getItemHistoryByName(name);
    }

    @GetMapping("/today/{id}")
    public List<ItemHistory> getToday(@PathVariable List<Long> id) throws IOException {
        return itemHistoryService.getTodayItemPrice(id);
    }
}
