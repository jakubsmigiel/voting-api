package com.example.wybory_demo.data.database.voter;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoterRepo extends JpaRepository<Voter, UUID> {

}
