package com.example.wybory_demo.data.database.option;

import com.example.wybory_demo.data.database.campaign.Campaign;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Options available to vote for in a campaign.
 */
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CampaignOption {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private CampaignOptionKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("campaign")
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    public String getOptionName() {
        return id != null ? id.getOptionName() : null;
    }

    public void setOptionName(String optionName) {
        if (this.id == null)
            this.id = new CampaignOptionKey();
        this.id.setOptionName(optionName);
    }
}