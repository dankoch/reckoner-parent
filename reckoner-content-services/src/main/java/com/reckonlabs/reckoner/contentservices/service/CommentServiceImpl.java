package com.reckonlabs.reckoner.contentservices.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.contentservices.cache.CommentCache;
import com.reckonlabs.reckoner.contentservices.cache.ReckoningCache;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepo;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepoCustom;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.message.CommentServiceList;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;
import com.reckonlabs.reckoner.domain.utility.DateUtility;
import com.reckonlabs.reckoner.domain.utility.ListPagingUtility;

@Component
public class CommentServiceImpl implements CommentService {
	
	@Autowired
	ReckoningRepo reckoningRepo;
	
	@Autowired
	ReckoningRepoCustom reckoningRepoCustom;
	
	@Autowired
	CommentCache commentCache;
	
	@Autowired
	ReckoningCache reckoningCache;
	
	private static final Logger log = LoggerFactory
			.getLogger(CommentServiceImpl.class);

	@Override
	public ServiceResponse postReckoningComment(Comment comment, String userToken, String reckoningId) {
		
		try {
			if (!reckoningRepoCustom.confirmReckoningExists(reckoningId)) {
				log.warn("Attempted to write comment to non-existent reckoning: " + reckoningId);
				return (new ServiceResponse(new Message(MessageEnum.R402_POST_COMMENT), false));
			}
			comment.setPostingDate(DateUtility.now());
			reckoningRepoCustom.insertReckoningComment(comment, reckoningId);
			
			// Cache management. Remove this user's comment cache entry because of the update.  
			// Also remove the commented reckoning cache.
			commentCache.removeUserCommentCache(comment.getPosterId());
			reckoningCache.removeCachedUserCommentedReckonings(comment.getPosterId());
			
			// Cache management. Check to see if the reckoning is already in cache.  If so, update it.  Otherwise, forget it.
			List<Reckoning> cacheReckoning = reckoningCache.getCachedReckoning(reckoningId);
			if (cacheReckoning != null) {
				if (cacheReckoning.get(0) != null) {
					if (cacheReckoning.get(0).getComments() == null) {
						cacheReckoning.get(0).setComments(new LinkedList<Comment> ());
					} 
					cacheReckoning.get(0).getComments().add(comment);
				}
				
				reckoningCache.setCachedReckoning(cacheReckoning, cacheReckoning.get(0).getId());
			}
		} catch (DBUpdateException dbE) {
			log.error("Database exception when inserting a new reckoning comment: " + dbE.getMessage());
			log.debug("Stack Trace:", dbE);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));				
		} catch (Exception e) {
			log.error("General exception when inserting a new reckoning comment: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}
	
	@Override
	public CommentServiceList getCommentsByUser(String userId, Integer page,
			Integer size, String userToken) {
		List<Comment> userComments = null;
		
		try {
			// Check the cache to see if the full list of comments made by this user are already available.
			// If not, pull all the reckonings this user has commented on and create the complete list.
			// Then cache them for next time.
			
			userComments = commentCache.getUserCommentCache(userId);
			
			if (userComments == null) {
				userComments = new LinkedList<Comment> ();
				List<Reckoning> commentedReckonings = reckoningRepo.getReckoningCommentsCommentedOnByUser(userId);
				
				if (commentedReckonings != null) {
					for (Reckoning reckoning : commentedReckonings) {
						for (Comment comment : reckoning.getComments()) {
							if (comment.getPosterId().equals(userId)) {
								userComments.add(comment);
							}
						}
					}
				}

				commentCache.setUserCommentCache(userId, userComments);
			}
			
			// Implement pagination.
			userComments = (List<Comment>) ListPagingUtility.pageList(userComments, page, size);
		} catch (Exception e) {
		   log.error("General exception when getting comments by user: " + e.getMessage());
		   log.debug("Stack Trace:", e);			
		   return new CommentServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
	    }
		
		return new CommentServiceList(userComments, new Message(), true);
	}

	@Override
	public ReckoningServiceList getCommentedReckoningsByUser(String userId,
			Integer page, Integer size, String userToken) {
		List<Reckoning> commentedReckonings = null;		
		
		try {
			commentedReckonings = reckoningCache.getCachedUserCommentedReckonings(userId);
			
			if (commentedReckonings == null) {
				commentedReckonings = reckoningRepo.getReckoningSummariesCommentedOnByUser(userId);
				reckoningCache.setCachedUserCommentedReckonings(commentedReckonings, userId);
			}
			commentedReckonings = (List <Reckoning>) ListPagingUtility.pageList(commentedReckonings, page, size);
		} catch (Exception e) {
		   log.error("General exception when getting commented reckonings by user: " + e.getMessage());
		   log.debug("Stack Trace:", e);			
		   return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
	    }
		
		return new ReckoningServiceList(commentedReckonings, new Message(), true);		
	}
}
