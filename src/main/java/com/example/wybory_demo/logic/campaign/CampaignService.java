package com.example.wybory_demo.logic.campaign;

import com.example.wybory_demo.data.database.campaign.Campaign;
import com.example.wybory_demo.data.database.campaign.CampaignRepo;
import com.example.wybory_demo.data.database.option.CampaignOption;
import com.example.wybory_demo.data.database.option.CampaignOptionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CampaignService {
    @Autowired
    CampaignRepo campaignRepo;

    @Autowired
    CampaignOptionRepo campaignOptionRepo;

    public List<Campaign> getAllCampaigns() {
        log.debug("Retrieving all campaigns.");
        List<Campaign> campaigns = campaignRepo.findAll();
        log.debug("Retrieved {} campaigns.", campaigns.size());
        return campaigns;
    }

    public Campaign getCampaignById(UUID id) {
        log.debug("Retrieving campaign with ID {}.", id);
        Optional<Campaign> campaignOptional = campaignRepo.findById(id);

        Campaign campaign = campaignOptional.orElse(null);
        if (campaign == null) {
            log.debug("Campaign with ID {} not found.", id);
        } else {
            log.debug("Campaign with ID {} retrieved successfully.", id);
        }
        return campaign;
    }

    @Transactional
    public Campaign addNewCampaign(String name, List<String> options) {
        log.debug("Adding new campaign with name {} and {} options.", name, options.size());
        Campaign campaign = new Campaign();
        campaign.setName(name);

        Set<CampaignOption> campaignOptions = options.stream().map(option -> {
            CampaignOption campaignOption = new CampaignOption();
            campaignOption.setCampaign(campaign);
            campaignOption.setOptionName(option);
            return campaignOption;
        }).collect(Collectors.toSet());

        campaign.setOptions(campaignOptions);

        Campaign saved = campaignRepo.save(campaign);
        log.debug("Campaign {} saved successfully.", saved.getId());

        campaignOptionRepo.saveAll(campaignOptions);
        log.debug("Saved {} campaign options for campaign {}.", campaignOptions.size(), saved.getId());

        return saved;
    }


    public Campaign closeCampaign(UUID id) {
        log.debug("Closing campaign with ID {}.", id);
        Optional<Campaign> campaignOptional = campaignRepo.findById(id);

        if (campaignOptional.isEmpty()) {
            log.debug("Campaign with ID {} not found when attempting to close.", id);
            return null;
        }

        Campaign campaign = campaignOptional.get();
        campaign.setIsOpen(false);
        Campaign closedCampaign = campaignRepo.save(campaign);
        log.debug("Campaign {} closed successfully.", id);
        return closedCampaign;
    }
}