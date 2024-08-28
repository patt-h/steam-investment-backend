package com.example.steaminvestmentbackend.Service;

import com.example.steaminvestmentbackend.DTO.SteamHistoryDTO;
import com.example.steaminvestmentbackend.Entity.ItemHistory;
import com.example.steaminvestmentbackend.Entity.ItemList;
import com.example.steaminvestmentbackend.Repository.ItemHistoryRepository;
import com.example.steaminvestmentbackend.Repository.ItemListRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
        Optional<ItemList> item = itemListRepository.findById(id);
        List<ItemHistory> history = itemHistoryRepository.findByItemId(id);

        if (item.isPresent() && history.isEmpty()) {
            System.out.println("Fetching data from Steam");
            List<ItemHistory> fetchHistory = fetchAndParseData(item.get().getMarketHashName(), item.get().getId());
            itemHistoryRepository.saveAll(fetchHistory);
        }
        return itemHistoryRepository.findByItemId(id);
    }

    public List<ItemHistory> fetchAndParseData(String marketHashName, Long itemId) throws IOException {
        String url = "https://steamcommunity.com/market/pricehistory/?country=PL&currency=6&appid=730&market_hash_name=" + marketHashName;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", "steamtoken");
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        String jsonData = responseEntity.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        SteamHistoryDTO priceResponse = objectMapper.readValue(jsonData, SteamHistoryDTO.class);

        List<ItemHistory> itemHistoryList = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = today.minusYears(1);

        Set<LocalDate> processedDates = new HashSet<>();

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

        return itemHistoryList;
    }

}
