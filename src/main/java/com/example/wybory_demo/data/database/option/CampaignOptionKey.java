package com.example.wybory_demo.data.database.option;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
public class CampaignOptionKey implements Serializable {
    private UUID campaign;

    @Column(name = "option_name", nullable = false)
    private String optionName;
}