package com.example.wybory_demo.logic.voting;

import com.example.wybory_demo.data.database.campaign.Campaign;
import com.example.wybory_demo.data.database.campaign.CampaignRepo;
import com.example.wybory_demo.data.database.option.CampaignOption;
import com.example.wybory_demo.data.database.option.CampaignOptionRepo;
import com.example.wybory_demo.data.database.vote.Vote;
import com.example.wybory_demo.data.database.vote.VoteKey;
import com.example.wybory_demo.data.database.vote.VoteRepo;
import com.example.wybory_demo.data.database.voter.Voter;
import com.example.wybory_demo.data.database.voter.VoterRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class VotingService {
    @Autowired
    private CampaignRepo campaignRepo;

    @Autowired
    private VoterRepo voterRepo;

    @Autowired
    private VoteRepo voteRepo;

    @Autowired
    private CampaignOptionRepo campaignOptionRepo;

    @Transactional
    public VotingResult makeVote(UUID campaignUuid, UUID voterUuid, String option) {
        log.debug("Voting \"{}\" in campaign {} by voter {}.", option, campaignUuid, voterUuid);

        Campaign campaign = campaignRepo.findById(campaignUuid).orElse(null);
        Voter voter = voterRepo.findById(voterUuid).orElse(null);

        if (campaign == null) {
            log.debug("Campaign {} doesn't exist.", campaignUuid);
            return VotingResult.CAMPAIGN_NOT_FOUND;
        }

        if (campaign.getIsOpen() == null || !campaign.getIsOpen()) {
            log.debug("Campaign {} has been closed.", campaignUuid);
            return VotingResult.CAMPAIGN_CLOSED;
        }

        if (voter == null) {
            log.debug("Voter {} doesn't exist.", voterUuid);
            return VotingResult.VOTER_NOT_FOUND;
        }

        CampaignOption campaignOption = campaignOptionRepo.findByCampaignAndId_OptionName(campaign, option).orElse(null);
        if (campaignOption == null) {
            log.debug("Option \"{}\" not available on campaign {}.", option, campaign.getId());
            return VotingResult.OPTION_NOT_AVAILABLE;
        }

        Optional<Vote> existingVote = voteRepo.findByCampaignAndVoter(campaign, voter);
        if (existingVote.isPresent()) {
            log.debug("Vote in campaign {} by voter {} was rejected because a vote already exists.",
                    campaign.getId(), voter.getId());
            return VotingResult.ALREADY_EXISTS;
        }

        if (voter.getIsBlocked()) {
            log.debug("Vote in campaign {} by voter {} was rejected because the voter is blocked.",
                    campaign.getId(), voter.getId());
            return VotingResult.VOTER_BLOCKED;
        }

        Vote vote = new Vote();
        vote.setCampaign(campaign);
        vote.setVoter(voter);
        vote.setOption(campaignOption);
        vote.setId(new VoteKey(voter.getId(), campaign.getId()));

        try {
            voteRepo.save(vote);
        } catch (DataIntegrityViolationException e) {
            log.debug("Vote in campaign {} by voter {} was rejected because a vote already exists.",
                    campaign.getId(), voter.getId());
            return VotingResult.ALREADY_EXISTS;
        }

        log.debug("Added vote in campaign {} by voter {}.",
                campaign.getId(), voter.getId());

        return VotingResult.SUCCESS;
    }
}
