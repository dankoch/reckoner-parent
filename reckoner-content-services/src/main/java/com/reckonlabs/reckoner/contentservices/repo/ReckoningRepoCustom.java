package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;

import com.reckonlabs.reckoner.domain.ApprovalStatusEnum;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;

import com.reckonlabs.reckoner.domain.media.Media;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.notes.Favorite;
import com.reckonlabs.reckoner.domain.notes.Flag;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.reckoning.ReckoningTypeEnum;
import com.reckonlabs.reckoner.domain.reckoning.Vote;

public interface ReckoningRepoCustom {
	
	public void insertNewReckoning (Reckoning reckoning);
	
	public void updateReckoning (Reckoning reckoning);
	
	public void mergeReckoning(Reckoning reckoning) throws DBUpdateException;
	
	public void approveReckoning (String id, String accepter, Date postingDate, Date closingDate) throws DBUpdateException;
	
	public void rejectReckoning (String id, String rejecter) throws DBUpdateException;
	
	public List<Reckoning> getReckoningSummaries (ReckoningTypeEnum reckoningType, 
			Date postedBeforeDate, Date postedAfterDate,
			Date closedBeforeDate, Date closedAfterDate, 
			List<String> includeTag, List<String> excludeTag, 
			Boolean highlighted,
			String submitterId,
			ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending, Integer page, Integer size, Boolean randomize);
	
	public Long getReckoningCount (ReckoningTypeEnum reckoningType, 
			Date postedBeforeDate, Date postedAfterDate,
			Date closedBeforeDate, Date closedAfterDate, 
			List<String> includeTag, List<String> excludeTag,
			Boolean highlighted,
			String submitterId,
			ApprovalStatusEnum approvalStatus);
	
	public List<Reckoning> getRandomReckoningSummary (ReckoningTypeEnum type);
	
	public List<Reckoning> getUserVotedReckonings (String userId);
	
	public List<Reckoning> getUserCommentedReckonings (String userId);
	
	public List<Reckoning> getFavoritedReckonings (Date favoritedAfter, Integer page, Integer size);
	
	public List<Reckoning> getFlaggedReckonings (Date flaggedAfter, Integer page, Integer size);
	
	public List<Reckoning> getFavoritedReckoningComments (Date favoritedAfter, Integer page, Integer size);
	
	public List<Reckoning> getFlaggedReckoningComments (Date flaggedAfter, Integer page, Integer size);
	
	public void insertReckoningComment (Comment comment, String reckoningId) throws DBUpdateException;
	
	public void updateComment(Comment comment) throws DBUpdateException;
	
	public void deleteComment (String commentId) throws DBUpdateException;
	
	public void insertReckoningVote (Vote vote) throws DBUpdateException;
	
	public void updateReckoningVote (Vote vote) throws DBUpdateException;
	
	public void insertReckoningFavorite (Favorite favorite, String reckoningId) throws DBUpdateException;
	
	public void insertReckoningFlag (Flag flag, String reckoningId) throws DBUpdateException;
	
	public void incrementReckoningViews (String reckoningId) throws DBUpdateException;
	
	public void insertReckoningMedia (Media media, String reckoningId) throws DBUpdateException;
	
	public void deleteReckoningMedia (String mediaId) throws DBUpdateException;	
	
	// Waiting for Mongo DB to fix SERVER-831.
	// public void insertCommentFavorite (Favorite favorite, String commentId) throws DBUpdateException;
	
	// public void insertCommentFlag (Flag flag, String commentId) throws DBUpdateException;
	
	public boolean confirmReckoningExists (String reckoningId);
	
	public boolean confirmReckoningIsVotingEligible(String reckoningId, int answerIndex);
}
