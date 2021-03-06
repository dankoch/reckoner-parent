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
	@Query(value = "{'id' : ?0, 'approved' : ?1}", fields="{'answers.votes' : 0}")
	List<Reckoning> findByIdAndApproved(String id, boolean approved);
	
	List<Reckoning> findByApproved(boolean approved);
	List<Reckoning> findByRejected(boolean rejected);
	List<Reckoning> findByApprovedAndRejected(boolean approved, boolean rejected);
	Page<Reckoning> findByApprovedAndRejected(boolean approved, boolean rejected, Pageable page);	
	
	@Query(value = "{'comments.commentId' : ?0}", fields="{'answers.votes' : 0, 'flags' : 0, 'favorites' : 0}")
	List<Reckoning> getReckoningCommentById (String commentId);
	
	@Query(value = "{'media.mediaId' : ?0}", fields="{'comments' : 0, 'answers.votes' : 0, 'flags' : 0, 'favorites' : 0}")
	List<Reckoning> getReckoningMediaById (String commentId);
	
	@Query(value = "{'favorites.userId' : ?0}", fields="{'comments' : 0, 'answers.votes' : 0, 'flags' : 0, 'favorites' : 0}")
	List<Reckoning> getReckoningSummariesFavoritedByUser (String userId);	
	@Query(value = "{'comments.favorites.userId' : ?0}", fields="{'answers.votes' : 0, 'flags' : 0, 'favorites' : 0}")
	List<Reckoning> getReckoningCommentsFavoritedByUser (String userId);	
	
	@Query(value = "{ 'id' : ?0 }", fields="{'answers' : 1}")
	List<Reckoning> getReckoningVotesByReckoningId (String reckoningId);
}
