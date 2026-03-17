package com.example.wybory_demo.data.api.requests;

import lombok.Data;

@Data
public class MakeVoteRequest {
    String campaignUuid;
    String voterUuid;
    String option;
}
