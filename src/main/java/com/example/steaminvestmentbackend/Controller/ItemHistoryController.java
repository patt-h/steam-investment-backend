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

    @GetMapping("/all/id/{id}")
    public List<ItemHistory> getAll(@PathVariable Long id) throws IOException {
        return itemHistoryService.getItemHistory(id);
    }

    @GetMapping("/all/name/{name}")
    public List<ItemHistory> getAllByName(@PathVariable String name) throws IOException {
        return itemHistoryService.getItemHistoryByName(name);
    }

    @GetMapping("/today/{id}")
    public List<ItemHistory> getToday(@PathVariable List<Long> id) throws IOException {
        return itemHistoryService.getTodayItemPrice(id);
    }

    @GetMapping("/current/{name}")
    public ItemHistory getCurrent(@PathVariable String name) throws IOException {
        return itemHistoryService.fetchCurrentData(name, null);
    }
}
