package com.example.wybory_demo.data.database.vote;

import com.example.wybory_demo.data.database.voter.Voter;
import com.example.wybory_demo.data.database.campaign.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepo extends JpaRepository<Vote, VoteKey> {
     Optional<Vote> findByCampaignAndVoter(Campaign campaignUuid, Voter voterUuid);
}