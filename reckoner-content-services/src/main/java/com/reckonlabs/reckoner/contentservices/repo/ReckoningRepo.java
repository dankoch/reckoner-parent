package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.document.mongodb.repository.Query;
import org.springframework.data.document.mongodb.repository.MongoRepository;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

public interface ReckoningRepo extends MongoRepository<Reckoning, String> {

	List<Reckoning> findById(String id);
	
	List<Reckoning> findByApproved(boolean approved);
	List<Reckoning> findByRejected(boolean rejected);
	List<Reckoning> findByApprovedAndRejected(boolean approved, boolean rejected);
	Page<Reckoning> findByApprovedAndRejected(boolean approved, boolean rejected, Pageable page);	
	
	@Query(value = "{'submitterId' : ?0}", fields="{'id' : 1, 'question' : 1, 'submissionDate' : 1, 'submitterId' : 1}")
	List<Reckoning> findBySubmitterIdSummary(String submitterId);
	List<Reckoning> findBySubmitterId(String submitterId);	
	Page<Reckoning> findBySubmitterId(String submitterId, Pageable page);
	List<Reckoning> findBySubmitterIdAndClosingDateGreaterThan(String submitterId, Date currentDate);
	Page<Reckoning> findBySubmitterIdAndClosingDateGreaterThan(String submitterId, Date currentDate, Pageable page);	
	List<Reckoning> findBySubmitterIdAndClosingDateLessThan(String submitterId, Date currentDate);
	Page<Reckoning> findBySubmitterIdAndClosingDateLessThan(String submitterId, Date currentDate, Pageable page);	
	List<Reckoning> findBySubmitterIdAndApprovedAndRejected(String submitterId, boolean approved, boolean rejected);	
	
	List<Reckoning> findByHighlighted (boolean highlighted);
	List<Reckoning> findByHighlightedAndClosingDateGreaterThan (boolean highlighted, Date currentDate);
	List<Reckoning> findByHighlightedAndClosingDateLessThan (boolean highlighted, Date currentDate);

}
