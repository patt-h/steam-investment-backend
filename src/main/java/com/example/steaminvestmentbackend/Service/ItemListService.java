package com.example.steaminvestmentbackend.Service;

import com.example.steaminvestmentbackend.Entity.ItemList;
import com.example.steaminvestmentbackend.Repository.ItemListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemListService {

    private final ItemListRepository itemListRepository;

    public List<ItemList> searchByMarketHashNamePrefix(String prefix) {
        return itemListRepository.findByMarketHashNameContainingIgnoreCase(prefix);
    }
}
