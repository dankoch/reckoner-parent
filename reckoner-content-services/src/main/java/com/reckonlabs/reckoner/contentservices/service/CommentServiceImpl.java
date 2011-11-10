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
import com.reckonlabs.reckoner.contentservices.cache.ContentCache;
import com.reckonlabs.reckoner.contentservices.cache.ReckoningCache;
import com.reckonlabs.reckoner.contentservices.repo.ContentRepo;
import com.reckonlabs.reckoner.contentservices.repo.ContentRepoCustom;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepo;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepoCustom;
import com.reckonlabs.reckoner.domain.content.Content;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.message.CommentServiceList;
import com.reckonlabs.reckoner.domain.message.ContentServiceList;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.user.User;
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
	ContentRepo contentRepo;
	@Autowired
	ContentRepoCustom contentRepoCustom;
	
	@Autowired
	CommentCache commentCache;
	@Autowired
	ReckoningCache reckoningCache;
	@Autowired
	ContentCache contentCache;
	
	@Autowired
	UserService userService;
	
	private static final Logger log = LoggerFactory
			.getLogger(CommentServiceImpl.class);

	// RECKONING
	
	@Override
	public ServiceResponse postReckoningComment(Comment comment, String reckoningId) {
		
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
			if (cacheReckoning != null && !cacheReckoning.isEmpty()) {
				if (cacheReckoning.get(0) != null) {
					if (cacheReckoning.get(0).getComments() == null) {
						cacheReckoning.get(0).setComments(new LinkedList<Comment> ());
					} 
					comment.setUser(userService.getUserByUserId(comment.getPosterId(), true).getUser());
					cacheReckoning.get(0).addComment(comment);
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
	public ReckoningServiceList getReckoningComment(String commentId) {
		List<Reckoning> commentedReckonings = new LinkedList<Reckoning>();
		
		try {
		   commentedReckonings = reckoningRepo.getReckoningCommentById(commentId);
		   if (!commentedReckonings.isEmpty()) {
			   commentedReckonings.get(0).setComments(commentedReckonings.get(0).getCommentById(commentId));
		   }
		} catch (Exception e) {
		   log.error("General exception when getting comments by user: " + e.getMessage());
		   log.debug("Stack Trace:", e);			
		   return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
	    }
		
		if (commentedReckonings.isEmpty()) {
			return new ReckoningServiceList(null, new Message(MessageEnum.R501_GET_COMMENT), false);
		}
		return new ReckoningServiceList(commentedReckonings, new Message(MessageEnum.R00_DEFAULT), true);
	}
	
	@Override
	public ReckoningServiceList getReckoningCommentsByUser(String userId, Integer page, Integer size) {
		List<Reckoning> commentedReckonings = null;
		long commentCount = 0;
		long count = 0;
		
		try {
			// Check the cache to see if the list already exists.  If not, create it and cache.
			commentedReckonings = reckoningCache.getCachedUserCommentedReckonings(userId);
			
			if (commentedReckonings == null) {
				commentedReckonings = reckoningRepoCustom.getUserCommentedReckonings(userId);
				User user = userService.getUserByUserId(userId, true).getUser();
				count = commentedReckonings.size();
				
				// Remove all of the comments from the reckonings except those made by the specified user.
				for (Reckoning commentedReckoning : commentedReckonings) {
					List<Comment> userComments = commentedReckoning.getCommentsByUser(userId);
					commentCount += userComments.size();
					
					// Pull the user profile associated with the user id and attach it to each Reckoning.
					for (Comment comment : userComments) {
						comment.setUser(user);
					}
					
					commentedReckoning.setComments(userComments);
					commentedReckoning.setCommentIndex(userComments.size());
				}
				
				reckoningCache.setCachedUserCommentedReckonings(commentedReckonings, userId);
			} else {
				count = commentedReckonings.size();
				for (Reckoning commentedReckoning : commentedReckonings) {
					commentCount += commentedReckoning.getComments().size();
				}
			}
			
			commentedReckonings = (List <Reckoning>) ListPagingUtility.pageList(commentedReckonings, page, size);
		} catch (Exception e) {
		    log.error("General exception when getting comments by user: " + e.getMessage());
		    log.debug("Stack Trace:", e);			
		    return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
	    }
		
		return new ReckoningServiceList(commentedReckonings, commentCount, count, new Message(), true);
	}

	@Override
	public ServiceResponse updateReckoningComment(Comment comment) {
		try {
			List<Reckoning> commentedReckoning = getReckoningComment(comment.getCommentId()).getReckonings();
			if (commentedReckoning == null || commentedReckoning.isEmpty()) {
				return (new ServiceResponse(new Message(MessageEnum.R501_GET_COMMENT), false));				
			}
			
			Comment updateComment = commentedReckoning.get(0).getComments().get(0);
			updateComment.mergeComment(comment);
			reckoningRepoCustom.updateComment(updateComment);
			
			// Cache management. 
			// Delete the individual reckoning cache (these should be rare, so we're not doing an in-place update).
			// Ignoring the user caches -- those will clear soon enough.
			reckoningCache.removeCachedReckoning(commentedReckoning.get(0).getId());
		} catch (Exception e) {
		    log.error("General exception when deleting comment " + comment.getCommentId() + " : " + e.getMessage());
		    log.debug("Stack Trace:", e);			
		    return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ServiceResponse();
	}
	
	@Override
	public ServiceResponse deleteReckoningComment(String commentId) {
		try {
			List<Reckoning> commentedReckoning = getReckoningComment(commentId).getReckonings();
			if (commentedReckoning == null || commentedReckoning.isEmpty()) {
				return (new ServiceResponse(new Message(MessageEnum.R501_GET_COMMENT), false));				
			}
			reckoningRepoCustom.deleteComment(commentId);
			
			// Cache management. 
			// Delete the individual reckoning cache (these should be rare, so we're not doing an in-place update).
			// Ignoring the user caches -- those will clear soon enough.
			reckoningCache.removeCachedReckoning(commentedReckoning.get(0).getId());
		} catch (Exception e) {
		    log.error("General exception when deleting comment " + commentId + " : " + e.getMessage());
		    log.debug("Stack Trace:", e);			
		    return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ServiceResponse();
	}
	
	// CONTENT //
	
	@Override
	public ServiceResponse postContentComment(Comment comment, String contentId) {
		
		try {
			if (!contentRepoCustom.confirmContentExists(contentId)) {
				log.warn("Attempted to write comment to non-existent content: " + contentId);
				return (new ServiceResponse(new Message(MessageEnum.R402_POST_COMMENT), false));
			}
			comment.setPostingDate(DateUtility.now());
			contentRepoCustom.insertContentComment(comment, contentId);
			
			// Cache management. Check to see if the content is already in cache.  If so, update it.  Otherwise, forget it.
			List<Content> cacheContent = contentCache.getCachedContent(contentId);
			if (cacheContent != null && !cacheContent.isEmpty()) {
				if (cacheContent.get(0) != null) {
					if (cacheContent.get(0).getComments() == null) {
						cacheContent.get(0).setComments(new LinkedList<Comment> ());
					} 
					comment.setUser(userService.getUserByUserId(comment.getPosterId(), true).getUser());
					cacheContent.get(0).addComment(comment);
				}
				
				contentCache.setCachedContent(cacheContent, cacheContent.get(0).getId());
			}
		} catch (DBUpdateException dbE) {
			log.error("Database exception when inserting a new content comment: " + dbE.getMessage());
			log.debug("Stack Trace:", dbE);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));				
		} catch (Exception e) {
			log.error("General exception when inserting a new content comment: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}

	@Override
	public ContentServiceList getContentComment(String commentId) {
		List<Content> commentedContents = new LinkedList<Content>();
		
		try {
		   commentedContents = contentRepo.getContentCommentById(commentId);
		   if (!commentedContents.isEmpty()) {
			   commentedContents.get(0).setComments(commentedContents.get(0).getCommentById(commentId));
		   }
		} catch (Exception e) {
		   log.error("General exception when getting comments by user: " + e.getMessage());
		   log.debug("Stack Trace:", e);			
		   return new ContentServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
	    }
		
		if (commentedContents.isEmpty()) {
			return new ContentServiceList(null, new Message(MessageEnum.R501_GET_COMMENT), false);
		}
		return new ContentServiceList(commentedContents, new Message(MessageEnum.R00_DEFAULT), true);
	}

	@Override
	public ServiceResponse updateContentComment(Comment comment) {
		try {
			List<Content> commentedContent = getContentComment(comment.getCommentId()).getContents();
			if (commentedContent == null || commentedContent.isEmpty()) {
				return (new ServiceResponse(new Message(MessageEnum.R501_GET_COMMENT), false));				
			}
			
			Comment updateComment = commentedContent.get(0).getComments().get(0);
			updateComment.mergeComment(comment);
			contentRepoCustom.updateComment(updateComment);
			
			// Cache management. 
			// Delete the individual content cache (these should be rare, so we're not doing an in-place update).
			// Ignoring the user caches -- those will clear soon enough.
			contentCache.removeCachedContent(commentedContent.get(0).getId());
		} catch (Exception e) {
		    log.error("General exception when deleting comment " + comment.getCommentId() + " : " + e.getMessage());
		    log.debug("Stack Trace:", e);			
		    return new ContentServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ServiceResponse();
	}
	
	@Override
	public ServiceResponse deleteContentComment(String commentId) {
		try {
			List<Content> commentedContent = getContentComment(commentId).getContents();
			if (commentedContent == null || commentedContent.isEmpty()) {
				return (new ServiceResponse(new Message(MessageEnum.R501_GET_COMMENT), false));				
			}
			contentRepoCustom.deleteComment(commentId);
			
			// Cache management. 
			// Delete the individual content cache (these should be rare, so we're not doing an in-place update).
			// Ignoring the user caches -- those will clear soon enough.
			contentCache.removeCachedContent(commentedContent.get(0).getId());
		} catch (Exception e) {
		    log.error("General exception when deleting comment " + commentId + " : " + e.getMessage());
		    log.debug("Stack Trace:", e);			
		    return new ContentServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ServiceResponse();
	}
}
