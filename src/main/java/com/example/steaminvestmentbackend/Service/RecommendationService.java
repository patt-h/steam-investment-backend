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

    public List<String> getRecommendations(@AuthenticationPrincipal UserDTO userDTO, int minSupportCount) {
        HashMap<Set<String>, Integer> aprioriResults = apriori(minSupportCount);

        List<Item> userItems = itemRepository.findByUserId(userDTO.getId());
        Set<String> userItemNames = userItems.stream()
                .map(Item::getMarketHashName)
                .collect(Collectors.toSet());

        List<String> aprioriRecommendations = aprioriResults.keySet().stream()
                .filter(itemSet -> itemSet.stream().anyMatch(userItemNames::contains))
                .flatMap(Set::stream)
                .distinct()
                .collect(Collectors.toList());

        if (aprioriRecommendations.size() >= 10) {
            return aprioriRecommendations.subList(0, 10);
        }

        List<String> contentBasedRecommendations = recommendBasedOnContent(userDTO);
        Set<String> finalRecommendations = new LinkedHashSet<>(aprioriRecommendations);

        for (String recommendation : contentBasedRecommendations) {
            if (finalRecommendations.size() >= 10) {
                break;
            }
            finalRecommendations.add(recommendation);
        }

        return new ArrayList<>(finalRecommendations);
    }

    public HashMap<Set<String>, Integer> apriori(int minSupportCount) {
        List<Item> userItems = itemRepository.findAll();

        Map<Long, Set<String>> userTransactions = userItems.stream()
                .collect(Collectors.groupingBy(
                        Item::getUserId,
                        Collectors.mapping(Item::getMarketHashName, Collectors.toSet())
                ));


        return Apriori.apriori(new ArrayList<>(userTransactions.values()), minSupportCount);
    }

    public List<String> recommendBasedOnContent(@AuthenticationPrincipal UserDTO userDTO) {
        List<Item> userItems = itemRepository.findByUserId(userDTO.getId());

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

    public static class Apriori {

        public static HashMap<Set<String>, Integer> apriori(List<Set<String>> transactions, int minSupportCount) {
            HashMap<Set<String>, Integer> frequentItemSets = new HashMap<>();

            Map<Set<String>, Integer> currentItemSets = getSingleItemSets(transactions);
            currentItemSets = filterBySupport(currentItemSets, minSupportCount);
            frequentItemSets.putAll(currentItemSets);

            while (!currentItemSets.isEmpty()) {
                currentItemSets = generateNextLevelItemSets(currentItemSets.keySet());
                currentItemSets = countSupport(currentItemSets, transactions);
                currentItemSets = filterBySupport(currentItemSets, minSupportCount);
                frequentItemSets.putAll(currentItemSets);
            }

            return frequentItemSets;
        }

        private static Map<Set<String>, Integer> getSingleItemSets(List<Set<String>> transactions) {
            Map<Set<String>, Integer> itemSets = new HashMap<>();
            for (Set<String> transaction : transactions) {
                for (String item : transaction) {
                    Set<String> singleItemSet = new HashSet<>();
                    singleItemSet.add(item);
                    itemSets.put(singleItemSet, itemSets.getOrDefault(singleItemSet, 0) + 1);
                }
            }
            return itemSets;
        }

        private static Map<Set<String>, Integer> filterBySupport(Map<Set<String>, Integer> itemSets, int minSupportCount) {
            Map<Set<String>, Integer> filteredItemSets = new HashMap<>();
            for (Map.Entry<Set<String>, Integer> entry : itemSets.entrySet()) {
                if (entry.getValue() >= minSupportCount) {
                    filteredItemSets.put(entry.getKey(), entry.getValue());
                }
            }
            return filteredItemSets;
        }

        private static Map<Set<String>, Integer> generateNextLevelItemSets(Set<Set<String>> currentItemSets) {
            Map<Set<String>, Integer> nextLevelItemSets = new HashMap<>();
            List<Set<String>> itemSetsList = new ArrayList<>(currentItemSets);
            for (int i = 0; i < itemSetsList.size(); i++) {
                for (int j = i + 1; j < itemSetsList.size(); j++) {
                    Set<String> unionSet = new HashSet<>(itemSetsList.get(i));
                    unionSet.addAll(itemSetsList.get(j));
                    if (unionSet.size() == itemSetsList.get(i).size() + 1) {
                        nextLevelItemSets.put(unionSet, 0);
                    }
                }
            }
            return nextLevelItemSets;
        }

        private static Map<Set<String>, Integer> countSupport(Map<Set<String>, Integer> itemSets, List<Set<String>> transactions) {
            for (Set<String> transaction : transactions) {
                for (Set<String> itemSet : itemSets.keySet()) {
                    if (transaction.containsAll(itemSet)) {
                        itemSets.put(itemSet, itemSets.get(itemSet) + 1);
                    }
                }
            }
            return itemSets;
        }
    }
}
