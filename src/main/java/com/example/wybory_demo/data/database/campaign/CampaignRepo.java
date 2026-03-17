package com.example.wybory_demo.data.database.campaign;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CampaignRepo extends JpaRepository<Campaign, UUID> {

}
