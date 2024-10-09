package com.example.steaminvestmentbackend.Service;

import com.example.steaminvestmentbackend.DTO.UserDTO;
import com.example.steaminvestmentbackend.Entity.Item;
import com.example.steaminvestmentbackend.Entity.ItemList;
import com.example.steaminvestmentbackend.Repository.ItemListRepository;
import com.example.steaminvestmentbackend.Repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecommendationService {

    private final ItemRepository itemRepository;
    private final ItemListRepository itemListRepository;

    public List<Item> getUserItems(@AuthenticationPrincipal UserDTO userDTO) {
        return itemRepository.findByUserId(userDTO.getId());
    }

    // This method should be extended to actually find sets of items that occur most frequently
    // Now it finds only that item occurs x times (which is good for small database that I currently have)
    public Map<String, Integer> findFrequentItemsets() {
        Map<String, Integer> itemsets = new HashMap<>();

        List<Item> allItems = itemRepository.findAll();

        for (Item item : allItems) {
            String itemName = item.getMarketHashName();
            itemsets.put(itemName, itemsets.getOrDefault(itemName, 0) + 1);
        }

        // Return itemsets that occur frequently
        return itemsets;
    }


    public List<String> recommendBasedOnContent(@AuthenticationPrincipal UserDTO userDTO) {
        List<Item> userItems = getUserItems(userDTO);

        Set<String> userItemNames = new HashSet<>();
        for (Item item : userItems) {
            userItemNames.add(item.getMarketHashName());
        }

        List<ItemList> allItems = itemListRepository.findAll();

        Set<String> recommendations = new HashSet<>();
        List<String> stickerCandidates = new ArrayList<>();
        int stickerLimit = 10;

        for (Item userItem : userItems) {
            for (ItemList item : allItems) {
                if (item.getMarketHashName().startsWith("Sticker | ") || item.getMarketHashName().startsWith("Autograph Capsule")) {
                    if (isSimilar(userItem, item) && !userItemNames.contains(item.getMarketHashName())) {
                        stickerCandidates.add(item.getMarketHashName());
                    }
                } else {
                    if (!item.getMarketHashName().contains("Key") && !item.getMarketHashName().contains("Pass") && isSimilar(userItem, item) && !userItemNames.contains(item.getMarketHashName())) {
                        recommendations.add(item.getMarketHashName());
                    }
                }
            }
        }

        Collections.shuffle(stickerCandidates);
        int stickersToAdd = Math.min(stickerLimit, stickerCandidates.size());

        for (int i = 0; i < stickersToAdd; i++) {
            recommendations.add(stickerCandidates.get(i));
        }

        List<String> recommendationList = new ArrayList<>(recommendations);
        Collections.shuffle(recommendationList);

        int itemsToSelect = Math.min(10, recommendationList.size());
        return recommendationList.subList(0, itemsToSelect);
    }

    private boolean isSimilar(Item userItem, ItemList otherItem) {
        return userItem.getMarketHashName().contains(otherItem.getMarketHashName().substring(0, 5));
    }


    public List<Map<String, Object>> recommendItemsForUser(@AuthenticationPrincipal UserDTO userDTO) {
        // Get user items
        List<Item> userItems = getUserItems(userDTO);
        Set<String> userItemNames = userItems.stream().map(Item::getMarketHashName).collect(Collectors.toSet());

        // Find frequent itemsets from other users
        Map<String, Integer> frequentItemsets = findFrequentItemsets();

        // Sort by frequency (descending order) and filter out items the user already has
        List<String> filteredItems = frequentItemsets.entrySet().stream()
                .filter(entry -> !userItemNames.contains(entry.getKey())) // user doesn't have the item
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // sort by frequency descending
                .limit(10) // limit to top 10 items
                .map(Map.Entry::getKey) // get the item names
                .collect(Collectors.toList()); // collect to list

        if (filteredItems.isEmpty()) {
            filteredItems = recommendBasedOnContent(userDTO);
        }

        List<Map<String, Object>> recommendedItems = new ArrayList<>();

        // Assign IDs to recommended items
        for (int i = 0; i < filteredItems.size(); i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", i + 1); // ID starts from 1
            item.put("marketHashName", filteredItems.get(i)); // Add the item name
            recommendedItems.add(item);
        }

        return recommendedItems;
    }


}
