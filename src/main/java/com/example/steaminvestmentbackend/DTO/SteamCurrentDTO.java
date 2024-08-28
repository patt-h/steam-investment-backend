package com.example.steaminvestmentbackend.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SteamCurrentDTO {

    private boolean success;

    @JsonProperty("lowest_price")
    private String lowestPrice;

    private String volume;

    @JsonProperty("median_price")
    private String medianPrice;
}
