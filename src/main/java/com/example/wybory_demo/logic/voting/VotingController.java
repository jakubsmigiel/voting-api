package com.example.wybory_demo.logic.voting;

import com.example.wybory_demo.data.api.ResultDTO;
import com.example.wybory_demo.data.api.VotingDTO;
import com.example.wybory_demo.data.api.requests.MakeVoteRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/voting")
public class VotingController {
    @Autowired
    VotingService votingService;

    @Operation(summary = "Cast a vote", description = "Casts an option vote as a voter in a campaign")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(value = "/makeVote")
    public ResponseEntity<ResultDTO<VotingDTO>> makeVote(@RequestBody MakeVoteRequest request) {
        String campaignUuid = request.getCampaignUuid();
        String voterUuid = request.getVoterUuid();
        String option = request.getOption();

        log.info("Processing vote for option \"{}\" in campaign {} by voter {}.", option, campaignUuid, voterUuid);

        if (campaignUuid == null || voterUuid == null || option == null) {
            log.debug("At least one of the parameters was null.");
            return ResponseEntity.status(400).body(new ResultDTO<>(null, "At least one of the request parameters was null"));
        }

        VotingResult votingResult;
        VotingDTO vote = new VotingDTO();
        try {
            votingResult = votingService.makeVote(UUID.fromString(campaignUuid), UUID.fromString(voterUuid), option);
        } catch (Exception e) {
            log.error("Exception when making a vote.", e);
            return ResponseEntity.internalServerError().body(new ResultDTO<>(null, "Unexpected server error."));
        }

        switch (votingResult) {
            case SUCCESS -> {
                log.debug("Vote for option \"{}\" in campaign {} by voter {} was successful.", option, campaignUuid, voterUuid);
                vote.setOption(option);
                vote.setCampaignUuid(campaignUuid);
                vote.setVoterUuid(voterUuid);
                return ResponseEntity.ok(new ResultDTO<>(vote));
            }
            case ALREADY_EXISTS -> {
                log.debug("Vote in campaign {} by voter {} was rejected because a vote already exists.", campaignUuid, voterUuid);
                return ResponseEntity.status(400).body(new ResultDTO<>(null, "Vote already made"));
            }
            case VOTER_BLOCKED -> {
                log.debug("Vote in campaign {} by voter {} was rejected because the voter is blocked.", campaignUuid, voterUuid);
                return ResponseEntity.status(400).body(new ResultDTO<>(null, "Voter blocked"));
            }
            case CAMPAIGN_NOT_FOUND -> {
                log.debug("Vote in campaign {} by voter {} was rejected because the campaign doesn't exist.", campaignUuid, voterUuid);
                return ResponseEntity.status(400).body(new ResultDTO<>(null, "Campaign doesn't exist"));
            }
            case VOTER_NOT_FOUND -> {
                log.debug("Vote in campaign {} by voter {} was rejected because the voter doesn't exist.", campaignUuid, voterUuid);
                return ResponseEntity.status(400).body(new ResultDTO<>(null, "Voter doesn't exist"));
            }
            case OPTION_NOT_AVAILABLE -> {
                log.debug("Vote in campaign {} by voter {} was rejected because the option \"{}\" doesn't exist.", campaignUuid, voterUuid, option);
                return ResponseEntity.status(400).body(new ResultDTO<>(null, "Option doesn't exist"));
            }
            case CAMPAIGN_CLOSED -> {
                log.debug("Vote in campaign {} by voter {} was rejected because the campaign has been closed.", campaignUuid, voterUuid);
                return ResponseEntity.status(400).body(new ResultDTO<>(null, "Campaign has been closed"));
            }
        }

        log.error("Unexpected voting result {} for vote in campaign {} by voter {}.", votingResult, campaignUuid, voterUuid);
        return new ResponseEntity<>(HttpStatusCode.valueOf(500));
    }
}