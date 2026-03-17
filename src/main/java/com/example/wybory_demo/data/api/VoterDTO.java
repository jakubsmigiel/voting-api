package com.example.wybory_demo.data.api;

import lombok.Data;

import java.util.HashMap;


@Data
public class VoterDTO {
    private String uuid;
    private String name;
    private Boolean isBlocked = false;
    private HashMap<String, String> campaignsVoted; // campaigns the voter has voted in and the vote - {campaign_uuid -> selected option voted for}

    public VoterDTO() {
        campaignsVoted = new HashMap<>();
    }
}
