package com.example.wybory_demo.data.database.vote;

import com.example.wybory_demo.data.database.voter.Voter;
import com.example.wybory_demo.data.database.campaign.Campaign;
import com.example.wybory_demo.data.database.option.CampaignOption;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(
    uniqueConstraints = { // to prevent letting a voter make more than one vote on one campaign
        @UniqueConstraint(columnNames = {"voter_id", "campaign_id"})
    }
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Vote {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private VoteKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("voter")
    @JoinColumn(name = "voter_id")
    private Voter voter;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("campaign")
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "option_campaign_id", referencedColumnName = "campaign_id"),
            @JoinColumn(name = "option_name", referencedColumnName = "option_name")
    })
    private CampaignOption option;
}