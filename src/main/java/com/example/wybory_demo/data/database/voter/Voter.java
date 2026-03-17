package com.example.wybory_demo.data.database.voter;

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
public class Voter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column
    private String name;

    @Column
    private Boolean isBlocked = false;

    @OneToMany(mappedBy = "voter", fetch = FetchType.LAZY)
    private Set<Vote> votes;

    public Voter() {
        votes = new HashSet<>();
    }
}
