package com.example.wybory_demo.logic.campaign;

import com.example.wybory_demo.data.api.CampaignDTO;
import com.example.wybory_demo.data.api.ResultDTO;
import com.example.wybory_demo.data.api.requests.CreateCampaignRequest;
import com.example.wybory_demo.data.api.requests.UuidRequest;
import com.example.wybory_demo.data.database.campaign.Campaign;
import com.example.wybory_demo.data.database.option.CampaignOption;
import com.example.wybory_demo.data.database.vote.Vote;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/campaigns")
public class CampaignController {
    @Autowired
    CampaignService campaignService;

    @Operation(summary = "Get all campaigns", description = "Returns a list of all campaigns in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(produces = "application/json")
    public List<CampaignDTO> getAllCampaigns() {
        log.debug("Fetching all campaigns.");
        List<CampaignDTO> campaigns = campaignService.getAllCampaigns().stream()
                .map(this::makeCampaignDTO)
                .toList();
        log.info("Returned {} campaigns.", campaigns.size());
        return campaigns;
    }

    @Operation(summary = "Get a campaign by ID", description = "Returns details of one campaign.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Campaign doesn't exist")
    })
    @GetMapping(value = "/getCampaign", produces = "application/json")
    public ResponseEntity<ResultDTO<CampaignDTO>> getCampaignById(UUID id) {
        log.debug("Retrieving campaign with id {}.", id);
        Campaign campaign = null;
        try {
            campaign = campaignService.getCampaignById(id);
        } catch (Exception e) {
            log.error("Exception when retrieving a campaign.", e);
            return ResponseEntity.internalServerError()
                    .body(new ResultDTO<>(null, "Unexpected server error"));
        }

        if (campaign == null) {
            log.debug("Campaign with id {} not found.", id);
            return ResponseEntity.status(404).body(new ResultDTO<>(null, "Campaign doesn't exist"));
        }

        log.info("Returned campaign with id {}.", id);
        return ResponseEntity.ok(new ResultDTO<>(makeCampaignDTO(campaign)));
    }

    @Operation(summary = "Create a campaign", description = "Adds a new campaign with all the details. " +
            "A campaign must have at least 2 options.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Incorrect data in the request body")
    })
    @PostMapping(value = "/addNewCampaign", produces = "application/json")
    public ResponseEntity<ResultDTO<CampaignDTO>> addNewCampaign(@RequestBody CreateCampaignRequest request) {
        String name = request.getName();
        List<String> options = request.getOptions();

        log.debug("Adding new campaign '{}' with options {}.", name, options);
        if (options == null || options.contains(null)) {
            log.debug("Null options");
            return ResponseEntity.badRequest()
                    .body(new ResultDTO<>(null, "Campaign has to contain options and none of them can be null"));
        }

        if (options.size() != options.stream().distinct().toList().size()) {
            log.debug("Duplicate options detected for campaign.");
            return ResponseEntity.badRequest()
                    .body(new ResultDTO<>(null, "Campaign can't contain duplicate options"));
        }

        if (options.size() < 2) {
            log.debug("Insufficient options ({}) for campaign '{}'.", options.size(), name);
            return ResponseEntity.badRequest()
                    .body(new ResultDTO<>(null, "Campaign must contain at least 2 options"));
        }

        Campaign campaign;
        try {
            campaign = campaignService.addNewCampaign(name, options);
        } catch (Exception e) {
            log.error("Exception when creating a new campaign.", e);
            return ResponseEntity.internalServerError()
                    .body(new ResultDTO<>(null, "Unexpected server error"));
        }

        log.info("Created campaign '{}' with uuid {}.", name, campaign.getId());
        return ResponseEntity.ok(new ResultDTO<>(makeCampaignDTO(campaign)));
    }

    @Operation(summary = "Close a campaign", description = "Permanently closes a campaign, disabling voting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(value = "/closeCampaign", produces = "application/json")
    public ResponseEntity<ResultDTO<CampaignDTO>> closeCampaign(@RequestBody UuidRequest request) {
        UUID id = request.getId();
        log.debug("Closing campaign with id {}.", id);
        Campaign campaign = null;
        try {
            campaign = campaignService.closeCampaign(id);
        } catch (Exception e) {
            log.error("Exception when closing the campaign.", e);
            return ResponseEntity.internalServerError()
                    .body(new ResultDTO<>(null, "Unexpected server error"));
        }

        if (campaign == null) {
            log.debug("Campaign with id {} not found for closing.", id);
            return ResponseEntity.notFound().build();
        }

        log.info("Closed campaign with id {}.", id);
        return ResponseEntity.ok(new ResultDTO<>(makeCampaignDTO(campaign)));
    }

    public CampaignDTO makeCampaignDTO(Campaign campaign) {
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setName(campaign.getName());
        campaignDTO.setUuid(campaign.getId().toString());
        HashMap<String, BigInteger> voteCounts = new HashMap<>();

        for (CampaignOption option : campaign.getOptions()) {
            voteCounts.put(option.getOptionName(), BigInteger.ZERO);
        }

        if (campaign.getVotes() != null)
            for (Vote vote : campaign.getVotes()) {
                voteCounts.put(
                    vote.getOption().getOptionName(),
                    voteCounts.get(vote.getOption().getOptionName()).add(BigInteger.ONE)
                );
            }

        campaignDTO.setVoteCounts(voteCounts);
        return campaignDTO;
    }
}
