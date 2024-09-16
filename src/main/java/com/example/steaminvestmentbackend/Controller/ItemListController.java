package com.example.steaminvestmentbackend.Controller;

import com.example.steaminvestmentbackend.Entity.ItemList;
import com.example.steaminvestmentbackend.Service.ItemListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemListController {

    private final ItemListService itemListService;

    @GetMapping("/search")
    public List<ItemList> searchItems(@RequestParam String prefix) {
        return itemListService.searchByMarketHashNamePrefix(prefix);
    }

}
