package com.example.wybory_demo.data.database.campaign;

import com.example.wybory_demo.data.database.option.CampaignOption;
import com.example.wybory_demo.data.database.vote.Vote;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column
    private String name;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private Set<Vote> votes;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private Set<CampaignOption> options;

    @Column
    private Boolean isOpen = true;

    public Campaign() {
        options = new HashSet<>();
    }
}
