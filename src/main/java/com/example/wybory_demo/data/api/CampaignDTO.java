package com.example.wybory_demo.data.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;
import java.util.HashMap;

@Data
public class CampaignDTO {
    @JsonProperty
    private String uuid;

    @JsonProperty
    private String name;

    @JsonProperty
    private HashMap<String, BigInteger> voteCounts;

    public CampaignDTO() {
        voteCounts = new HashMap<>();
    }
}
