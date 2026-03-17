package com.example.wybory_demo.data.api.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class BlockVoterRequest {
    UUID id;
    Boolean isBlocked;
}
