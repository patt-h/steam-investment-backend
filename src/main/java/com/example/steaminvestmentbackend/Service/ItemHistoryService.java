package com.example.steaminvestmentbackend.Service;

import com.example.steaminvestmentbackend.DTO.SteamCurrentDTO;
import com.example.steaminvestmentbackend.DTO.SteamHistoryDTO;
import com.example.steaminvestmentbackend.Entity.ItemHistory;
import com.example.steaminvestmentbackend.Entity.ItemList;
import com.example.steaminvestmentbackend.Exceptions.AppException;
import com.example.steaminvestmentbackend.Repository.ItemHistoryRepository;
import com.example.steaminvestmentbackend.Repository.ItemListRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ItemHistoryService {

    private final ItemHistoryRepository itemHistoryRepository;
    private final ItemListRepository itemListRepository;
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MMM dd yyyy")
            .toFormatter(Locale.ENGLISH);

    public List<ItemHistory> getItemHistory(Long id) throws IOException {
        List<ItemHistory> history = itemHistoryRepository.findByItemId(id);

        if (history.isEmpty()) {
            Optional<ItemList> item = itemListRepository.findById(id);
            if (item.isPresent()) {
                try {
                    System.out.println("Fetching data from Steam");
                    List<ItemHistory> fetchHistory = fetchAndParseData(item.get().getMarketHashName(), item.get().getId());
                    itemHistoryRepository.saveAll(fetchHistory);
                } catch (AppException e) {
                    System.out.println(e.getStatus());
                    throw new AppException("Could not fetch item history due to API limits", HttpStatus.SERVICE_UNAVAILABLE);
                }
            } else {
                throw new AppException("Item with provided id doesn't exist", HttpStatus.NOT_FOUND);
            }
        }

        return itemHistoryRepository.findByItemId(id);
    }

    public List<ItemHistory> getTodayItemPrice(List<Long> id) throws IOException {
        List<ItemHistory> prices = new ArrayList<>();

        for (Long itemId : id) {
            ItemHistory history = itemHistoryRepository.findByItemIdAndDate(itemId, LocalDate.now());
            if (history == null) {
                Optional<ItemList> item = itemListRepository.findById(itemId);
                if (item.isPresent()) {
                    System.out.println("Fetching current price data from Steam");
                    ItemHistory fetchCurrent = fetchCurrentData(item.get().getMarketHashName(), item.get().getId());
                    itemHistoryRepository.save(fetchCurrent);
                    prices.add(fetchCurrent);
                } else {
                    throw new AppException("Item with provided id doesn't exist", HttpStatus.NOT_FOUND);
                }
            }
            else {
                prices.add(history);
            }
        }

        return prices;
    }

    public List<ItemHistory> fetchAndParseData(String marketHashName, Long itemId) throws IOException {
        String url = "https://steamcommunity.com/market/pricehistory/?appid=730&market_hash_name=" + marketHashName;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", "steamLoginSecure=76561198407317357%7C%7CeyAidHlwIjogIkpXVCIsICJhbGciOiAiRWREU0EiIH0." +
                "eyAiaXNzIjogInI6MEY0M18yNEVFQkQ4RF82NDFBRiIsICJzdWIiOiAiNzY1NjExOTg0MDczMTczNTciLCAiYXVkI" +
                "jogWyAid2ViOmNvbW11bml0eSIgXSwgImV4cCI6IDE3MjU5NjY3NTQsICJuYmYiOiAxNzE3MjM4OTE2LCAiaWF0I" +
                "jogMTcyNTg3ODkxNiwgImp0aSI6ICIxMDY4XzI1MDEzMjg3XzcyNzA0IiwgIm9hdCI6IDE3MjQzNDMyOTcsICJydF9leHAiOiAxNzQyNzc5ODU1LCAicGVyI" +
                "jogMCwgImlwX3N1YmplY3QiOiAiODkuNjQuMTUuNDIiLCAiaXBfY29uZmlybWVyIjogIjg5LjY0LjE1LjQyIiB9." +
                "HTb7xrY__OPljGYusYI-d409Z3oSgP2hQq9J76ZSCUizUe8bWTS9RzH2iWF-V-xrNNk40E7XPXWmrUEMjJ4yCw");
        HttpEntity<?> requestEntity = new HttpEntity<>(null, requestHeaders);

        List<ItemHistory> itemHistoryList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = today.minusYears(1);
        Set<LocalDate> processedDates = new HashSet<>();

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            String jsonData = responseEntity.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            SteamHistoryDTO priceResponse = objectMapper.readValue(jsonData, SteamHistoryDTO.class);

            for (List<String> priceData : priceResponse.getPrices()) {
                String dateString = priceData.get(0).substring(0, 11); // np. "May 27 2015"
                LocalDate date = LocalDate.parse(dateString, formatter);

                if (date.isAfter(oneYearAgo) && date.isBefore(today)) {
                    if (!processedDates.contains(date)) {
                        Float price = Float.parseFloat(priceData.get(1));

                        ItemHistory itemHistory = new ItemHistory();
                        itemHistory.setDate(date);
                        itemHistory.setPrice(price);
                        itemHistory.setItemId(itemId);

                        itemHistoryList.add(itemHistory);
                        processedDates.add(date);
                    }
                }
            }
        } catch (HttpClientErrorException e) {
            throw new AppException("Too Many Requests", HttpStatus.TOO_MANY_REQUESTS);
        }

        return itemHistoryList;
    }

    public ItemHistory fetchCurrentData(String marketHashName, Long itemId) throws IOException {
        String url = "https://steamcommunity.com/market/priceoverview/?appid=730&market_hash_name=" + marketHashName;
        RestTemplate restTemplate = new RestTemplate();

        String jsonData = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        SteamCurrentDTO priceResponse = objectMapper.readValue(jsonData, SteamCurrentDTO.class);

        String priceString = priceResponse.getLowestPrice()
                .replaceAll("[^0-9.]", "");

        ItemHistory itemHistory = new ItemHistory();
        itemHistory.setItemId(itemId);
        itemHistory.setDate(LocalDate.now());
        itemHistory.setPrice(Float.parseFloat(priceString));

        return itemHistory;
    }
}
