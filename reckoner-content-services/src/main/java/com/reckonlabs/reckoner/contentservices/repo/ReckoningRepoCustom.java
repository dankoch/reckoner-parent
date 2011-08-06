package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.document.mongodb.repository.MongoRepository;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

public interface ReckoningRepoCustom {
	
	public void insertNewReckoning (Reckoning reckoning);
	
}
