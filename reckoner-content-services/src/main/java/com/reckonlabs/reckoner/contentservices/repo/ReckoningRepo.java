package com.reckonlabs.reckoner.contentservices.repo;

import org.springframework.data.document.mongodb.repository.MongoRepository;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

public interface ReckoningRepo extends MongoRepository<Reckoning, String> {

}
