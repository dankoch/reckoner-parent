package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.document.mongodb.repository.Query;
import org.springframework.data.document.mongodb.repository.MongoRepository;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

public interface ReckoningRepo extends MongoRepository<Reckoning, String> {

	List<Reckoning> findByApproved(boolean approved);
	List<Reckoning> findByRejected(boolean rejected);
	List<Reckoning> findByApprovedAndRejected(boolean approved, boolean rejected);
	Page<Reckoning> findByApprovedAndRejected(boolean approved, boolean rejected, Pageable page);	
	
	List<Reckoning> findById(String id);
	
	List<Reckoning> findBySubmitterId(String submitterId);
	
	List<Reckoning> findBySubmissionDateGreaterThan (Date submissionDate);
	List<Reckoning> findBySubmissionDateLessThan (Date submissionDate);
	List<Reckoning> findBySubmissionDateLessThanAndSubmissionDateGreaterThan (Date submissionDateEnd, Date submissionDateStart);
	
	List<Reckoning> findByInterval (String interval);
}
