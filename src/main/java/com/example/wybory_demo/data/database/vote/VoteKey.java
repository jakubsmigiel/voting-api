package com.example.wybory_demo.data.database.vote;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class VoteKey implements Serializable {
    private UUID voter;
    private UUID campaign;
}
