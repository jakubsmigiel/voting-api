package com.example.wybory_demo.logic.voter;

import com.example.wybory_demo.data.api.ResultDTO;
import com.example.wybory_demo.data.api.VoterDTO;
import com.example.wybory_demo.data.api.requests.BlockVoterRequest;
import com.example.wybory_demo.data.api.requests.CreateVoterRequest;
import com.example.wybory_demo.data.database.vote.Vote;
import com.example.wybory_demo.data.database.voter.Voter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/voters")
public class VoterController {
    @Autowired
    VoterService voterService;

    @Operation(summary = "List all voters", description = "Returns a list of all voters and their details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(produces = "application/json")
    public List<VoterDTO> getAllVoters() {
        log.debug("Retrieving all voters.");
        List<Voter> voters = voterService.getAllVoters();
        log.debug("Retrieved {} voters.", voters.size());
        return voters.stream().map(this::makeVoterDTO).toList();
    }

    @Operation(summary = "Get details of a voter", description = "Returns details about one voter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Voter doeesn't exist")
    })
    @GetMapping(value = "getVoter", produces = "application/json")
    public ResponseEntity<ResultDTO<VoterDTO>> getVoiterById(@RequestParam UUID id) {
        log.debug("Retrieving voter with id {}.", id);
        Voter voter = null;
        try {
            voter = voterService.getVoterById(id);
        } catch (Exception e) {
            log.error("Exception when retrieving a voter.", e);
            return ResponseEntity.internalServerError().body(new ResultDTO<>(null, "Unexpected server error."));
        }

        if (voter == null) {
            log.debug("Voter {} not found.", id);
            return ResponseEntity.status(404).body(new ResultDTO<>(null, "Voter doesn't exist"));
        }

        log.debug("Voter {} retrieved successfully.", id);
        return ResponseEntity.ok(new ResultDTO<>(makeVoterDTO(voter)));
    }

    @Operation(summary = "Add a new voter", description = "Creates a new voter and returns their id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(value = "addVoter", produces = "application/json")
    public ResponseEntity<ResultDTO<VoterDTO>> addVoter(@RequestBody CreateVoterRequest request) {
        String name = request.getName();
        log.debug("Adding new voter with name {}.", name);

        if (name == null) {
            log.debug("Voter name was null.");
            return ResponseEntity.status(400).body(new ResultDTO<>(null, "Voter name can't be null."));
        }

        Voter savedVoter = null;
        try {
            savedVoter = voterService.addNewVoter(name);
        } catch (Exception e) {
            log.error("Exception when adding a voter.", e);
            return ResponseEntity.internalServerError().body(new ResultDTO<>(null, "Unexpected server error."));
        }
        log.info("Added voter {} with name {}", savedVoter.getId(), name);
        return ResponseEntity.ok(new ResultDTO<>(makeVoterDTO(savedVoter)));
    }

    @Operation(summary = "Block or unblock a voter", description = "Temporarily blocks a voter, disabling them from voting, or removes the block.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Voter doesn't exist")
    })
    @PostMapping(value = "setVoteBlock", produces = "application/json")
    public ResponseEntity<ResultDTO<VoterDTO>> blockVoter(@RequestBody BlockVoterRequest request) {
        UUID id = request.getId();
        Boolean isBlocked = request.getIsBlocked();
        log.debug("Setting vote block for voter {} to {}", id, isBlocked);

        if (id == null || isBlocked == null) {
            log.debug("At least one of the parameters is null.");
            return ResponseEntity.status(400).body(new ResultDTO<>(null, "At least one of the required parameters was null."));
        }

        Voter voter = null;
        try {
            voter = voterService.setVoteBlock(id, isBlocked);
        } catch (Exception e) {
            log.error("Exception when setting a vote block for voter {}.", id, e);
            return ResponseEntity.internalServerError().body(new ResultDTO<>(null, "Unexpected server error."));
        }

        if (voter == null) {
            log.debug("Voter {} not found for vote block operation.", id);
            return ResponseEntity.status(404).body(new ResultDTO<>(null, "Voter doesn't exist"));
        }

        log.info("Set vote block for voter {} to {}.", id, isBlocked);
        return ResponseEntity.ok(new ResultDTO<>(makeVoterDTO(voter)));
    }

    public VoterDTO makeVoterDTO(Voter voter) {
        VoterDTO voterDTO = new VoterDTO();
        voterDTO.setUuid(voter.getId().toString());
        voterDTO.setName(voter.getName());
        voterDTO.setIsBlocked(voter.getIsBlocked());

        for (Vote vote : voter.getVotes()) {
            voterDTO.getCampaignsVoted().put(vote.getCampaign().getId().toString(), vote.getOption().getOptionName());
        }

        return voterDTO;
    }
}
