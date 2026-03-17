package com.example.wybory_demo.data.api.requests;

import lombok.Data;

import java.util.List;

@Data
public class CreateCampaignRequest {
    String name;
    List<String> options;
}
