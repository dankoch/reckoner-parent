package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.reckoning.Vote;

public interface ReckoningRepo extends MongoRepository<Reckoning, String> {

	// Leave off all of the votes for the regular request.  It's a lot of overhead we won't need.
	@Query(value = "{'id' : ?0}", fields="{'answers.votes' : 0}")
	List<Reckoning> findById(String id);
	
	List<Reckoning> findByApproved(boolean approved);
	List<Reckoning> findByRejected(boolean rejected);
	List<Reckoning> findByApprovedAndRejected(boolean approved, boolean rejected);
	Page<Reckoning> findByApprovedAndRejected(boolean approved, boolean rejected, Pageable page);	
	
	@Query(value = "{'submitterId' : ?0}", fields="{'id' : 1, 'question' : 1, 'postingDate' : 1, 'closingDate' : 1, 'submitterId' : 1}")
	List<Reckoning> findBySubmitterIdSummary(String submitterId);
	
	List<Reckoning> findByHighlighted (boolean highlighted);
	List<Reckoning> findByHighlightedAndClosingDateGreaterThan (boolean highlighted, Date currentDate);
	List<Reckoning> findByHighlightedAndClosingDateLessThan (boolean highlighted, Date currentDate);
	
	@Query(value = "{'comments.posterId' : ?0}", fields="{'comments' : 1}")
	List<Reckoning> getReckoningCommentsCommentedOnByUser (String userId);
	@Query(value = "{'comments.posterId' : ?0}", fields="{'id' : 1, 'question' : 1, 'postingDate' : 1, 'closingDate' : 1, 'submitterId' : 1}")
	List<Reckoning> getReckoningSummariesCommentedOnByUser (String userId);
	
	@Query(value = "{ 'answers.votes.voterId' : ?0 }", fields="{'id' : 1, 'question' : 1, 'postingDate' : 1, 'closingDate' : 1, 'submitterId' : 1, 'answers' : 1}")
	List<Reckoning> getVotesByUser (String userId);	
	@Query(value = "{ 'id' : ?0 }", fields="{'answers' : 1}")
	List<Reckoning> getReckoningVotesByReckoningId (String reckoningId);
}
