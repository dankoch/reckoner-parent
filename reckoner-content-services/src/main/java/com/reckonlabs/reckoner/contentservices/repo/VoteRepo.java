package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.reckonlabs.reckoner.domain.reckoning.Vote;

public interface VoteRepo extends MongoRepository<Vote, String> {

	List<Vote> findById(String id);
	List<Vote> findByReckoningIdAndIpAndUserAgent(String reckoningId, String ip, String userAgent);
}
