package com.example.wybory_demo.data.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VotingDTO {
    @JsonProperty
    private String voterUuid;

    @JsonProperty
    private String campaignUuid;

    @JsonProperty
    private String option;
}
