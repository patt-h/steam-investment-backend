package com.example.steaminvestmentbackend.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SteamHistoryDTO {

    private boolean success;

    @JsonProperty("price_prefix")
    private String pricePrefix;

    @JsonProperty("price_suffix")
    private String priceSuffix;

    private List<List<String>> prices;

}
