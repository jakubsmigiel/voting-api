package com.example.wybory_demo.data.database.option;

import com.example.wybory_demo.data.database.campaign.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CampaignOptionRepo extends JpaRepository<CampaignOption, UUID> {
    Optional<CampaignOption> findByCampaignAndId_OptionName(Campaign campaign, String optionName);
}

