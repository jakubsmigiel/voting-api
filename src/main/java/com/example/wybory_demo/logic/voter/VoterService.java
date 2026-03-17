package com.example.wybory_demo.logic.voter;

import com.example.wybory_demo.data.database.voter.Voter;
import com.example.wybory_demo.data.database.voter.VoterRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class VoterService {
    @Autowired
    VoterRepo voterRepo;

    public List<Voter> getAllVoters() {
        log.debug("Fetching all voters");
        return voterRepo.findAll();
    }

    public Voter addNewVoter(String name) {
        log.debug("Adding new voter with name \"{}\"", name);
        Voter voter = new Voter();
        voter.setName(name);
        Voter saved = voterRepo.save(voter);
        log.debug("New voter saved with id {}", saved.getId());
        return saved;
    }

    public Voter setVoteBlock(UUID uuid, boolean isBlocked) {
        log.debug("Attempting to set vote block status for voter {}", uuid);
        Optional<Voter> voterOptional = voterRepo.findById(uuid);
        if (voterOptional.isEmpty()) {
            log.debug("Voter {} not found, cannot set block status", uuid);
            return null;
        }

        Voter voter = voterOptional.get();
        voter.setIsBlocked(isBlocked);
        voterRepo.save(voter);
        log.debug("Voter {} block status set to {}", uuid, isBlocked);
        return voter;
    }

    public Voter getVoterById(UUID id) {
        log.debug("Retrieving voter by id {}", id);
        Voter voter = voterRepo.findById(id).orElse(null);
        if (voter == null) {
            log.debug("No voter found with id {}", id);
        } else {
            log.debug("Voter {} retrieved successfully", id);
        }
        return voter;
    }
}
