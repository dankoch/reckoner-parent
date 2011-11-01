package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.contentservices.cache.CommentCache;
import com.reckonlabs.reckoner.contentservices.cache.ReckoningCache;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepo;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepoCustom;

import com.reckonlabs.reckoner.domain.Notable;

import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.notes.Favorite;
import com.reckonlabs.reckoner.domain.notes.Flag;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;
import com.reckonlabs.reckoner.domain.utility.DateUtility;
import com.reckonlabs.reckoner.domain.utility.ListPagingUtility;

import com.reckonlabs.reckoner.domain.message.CommentServiceList;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;

@Component
public class NotesServiceImpl implements NotesService {

	@Autowired
	ReckoningRepo reckoningRepo;
	
	@Autowired
	ReckoningRepoCustom reckoningRepoCustom;
	
	@Autowired
	ReckoningCache reckoningCache;
	
	@Autowired
	CommentCache commentCache;
	
	@Autowired
	UserService userService;
	
	@Autowired
	ReckoningService reckoningService;
	
	@Autowired
	CommentService commentService;
	
	private static final Logger log = LoggerFactory
			.getLogger(NotesServiceImpl.class);

	@Override
	public ServiceResponse postReckoningFavorite(Favorite favorite,
			String reckoningId, String sessionId) {
		try {
			// Verify the data in the request:
			//   * Confirm that the specified user ID exists.
			//   * If so, confirm that the reckoning itself exists to favorite.
			//   * Verify that the user isn't trying to favorite their own reckoning.
			//   * Verify that the user hasn't already favorited the reckoning.
			
			if (userService.getUserByUserId(favorite.getUserId()).getUser() == null) {
				log.warn("Attempted to favorite on behalf of non-existent user: " + favorite.getUserId());
				return (new ServiceResponse(new Message(MessageEnum.R803_POST_NOTE), false));				
			} else {
				List<Reckoning> favoritedReckoning = reckoningService.getReckoning(reckoningId, sessionId).getReckonings();
				if (favoritedReckoning == null || favoritedReckoning.isEmpty()) {
					log.warn("Attempted to favorite non-existent reckoning: " + reckoningId);
					return (new ServiceResponse(new Message(MessageEnum.R801_POST_NOTE), false));
				} else {
					for (Reckoning reckoning : favoritedReckoning) {
						if (reckoning.getFavoriteByUser(favorite.getUserId()) != null) {
							log.warn("User " + favorite.getUserId() + " attempted to favorite reckoning multiple times: " + reckoningId);
							return (new ServiceResponse(new Message(MessageEnum.R804_POST_NOTE), false));							
						}
					}
				}
			}  
			
			favorite.setFavoriteDate(DateUtility.now());
			reckoningRepoCustom.insertReckoningFavorite(favorite, reckoningId);
			
			// Cache management. Remove this user's favorited reckonings cache entry because of the update.  
			reckoningCache.removeCachedUserFavoritedReckonings(favorite.getUserId());
			
			// Cache management. Check to see if the reckoning is already in cache.  If so, update it.  Otherwise, forget it.
			List<Reckoning> cacheReckoning = reckoningCache.getCachedReckoning(reckoningId);
			if (cacheReckoning != null && !cacheReckoning.isEmpty()) {
				if (cacheReckoning.get(0) != null) {
					cacheReckoning.get(0).addFavorite(favorite);
				}
				
				reckoningCache.setCachedReckoning(cacheReckoning, cacheReckoning.get(0).getId());
			}
		} catch (DBUpdateException dbE) {
			log.error("Database exception when favoriting a reckoning: " + dbE.getMessage());
			log.debug("Stack Trace:", dbE);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));				
		} catch (Exception e) {
			log.error("General exception when favoriting a reckoning: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}

	@Override
	public ServiceResponse postReckoningFlag(Flag flag, String reckoningId, String sessionId) {
		try {
			// Verify the data in the request:
			//   * Confirm that the specified user ID exists.
			//   * If so, confirm that the reckoning itself exists to flag.
			//   * Verify that the user hasn't already flagged the reckoning.
			
			if (userService.getUserByUserId(flag.getUserId()).getUser() == null) {
				log.warn("Attempted to flag on behalf of non-existent user: " + flag.getUserId());
				return (new ServiceResponse(new Message(MessageEnum.R803_POST_NOTE), false));				
			} else {
				List<Reckoning> flaggedReckoning = reckoningService.getReckoning(reckoningId, sessionId).getReckonings();
				if (flaggedReckoning == null || flaggedReckoning.isEmpty()) {
					log.warn("Attempted to flag non-existent reckoning: " + reckoningId);
					return (new ServiceResponse(new Message(MessageEnum.R801_POST_NOTE), false));
				} else {
					for (Reckoning reckoning : flaggedReckoning) {
						if (reckoning.getFlagByUser(flag.getUserId()) != null) {
							log.warn("User " + flag.getUserId() + " attempted to flag reckoning multiple times: " + reckoningId);
							return (new ServiceResponse(new Message(MessageEnum.R804_POST_NOTE), false));							
						}
					}
				}
			} 
			
			flag.setFlagDate(DateUtility.now());
			reckoningRepoCustom.insertReckoningFlag(flag, reckoningId);
			
			// Cache management. Check to see if the reckoning is already in cache.  If so, update it.  Otherwise, forget it.
			List<Reckoning> cacheReckoning = reckoningCache.getCachedReckoning(reckoningId);
			if (cacheReckoning != null && !cacheReckoning.isEmpty()) {
				if (cacheReckoning.get(0) != null) {
					cacheReckoning.get(0).addFlag(flag);
				}
				
				reckoningCache.setCachedReckoning(cacheReckoning, cacheReckoning.get(0).getId());
			}
		} catch (DBUpdateException dbE) {
			log.error("Database exception when flagging a reckoning: " + dbE.getMessage());
			log.debug("Stack Trace:", dbE);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));				
		} catch (Exception e) {
			log.error("General exception when flaggin a reckoning: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}

	@Override
	public ServiceResponse postReckoningCommentFavorite(Favorite favorite,
			String commentId, String sessionId) {
		try {
			List<Reckoning> commentedReckoning = null;
			List<Comment> favoritedComment = null;
			
			// Verify the data in the request:
			//   * Confirm that the specified user ID exists.
			//   * If so, confirm that the reckoning itself exists to favorite.
			//   * Verify that the user isn't favoriting their own comment.
			//   * Verify that the user hasn't already favorited the comment.
			
			if (userService.getUserByUserId(favorite.getUserId()).getUser() == null) {
				log.warn("Attempted to favorite comment on behalf of non-existent user: " + favorite.getUserId());
				return (new ServiceResponse(new Message(MessageEnum.R803_POST_NOTE), false));				
			} else {
				commentedReckoning = commentService.getReckoningComment(commentId).getReckonings();
				if ((commentedReckoning != null) && (!commentedReckoning.isEmpty()) && 
						(commentedReckoning.get(0).getComments() != null) && (!commentedReckoning.get(0).getComments().isEmpty())) 
				{
					favoritedComment = commentedReckoning.get(0).getComments();
					for (Comment comment : favoritedComment) {
						if (comment.getPosterId().equals(favorite.getUserId())){
							log.info("User " + favorite.getUserId() + " attempted to favorite their own comment: " + comment.getCommentId());
							return (new ServiceResponse(new Message(MessageEnum.R805_POST_NOTE), false));
						}
						if (comment.getFavoriteByUser(favorite.getUserId()) != null) {
							log.warn("User " + favorite.getUserId() + " attempted to favorite comment multiple times: " + commentId);
							return (new ServiceResponse(new Message(MessageEnum.R804_POST_NOTE), false));							
						}
					}
				} else {
					log.warn("Attempted to favorite non-existent comment: " + commentId);
					return (new ServiceResponse(new Message(MessageEnum.R802_POST_NOTE), false));					
				}
			}  
			
			// We now know the favorited comment exists and hasn't been favorited by this user yet.  Rewrite the comment to the DB with
			// the favorite attached to it.  We'll be able to insert the favorite directly once MongoDB fixes SERVER-831.
			favorite.setFavoriteDate(DateUtility.now());
			favoritedComment.get(0).addFavorite(favorite);
			reckoningRepoCustom.updateComment(favoritedComment.get(0));
			
			// Cache management. Remove this user's favorited comments cache entry because of the update.  
			reckoningCache.removeCachedUserFavoritedReckoningComments(favorite.getUserId());
			
			// Cache management.  If the Reckoning is in the cache, pull it, add the favorite to the comment yourself, and roll it back in.
			List<Reckoning> cacheReckoning = reckoningCache.getCachedReckoning(commentedReckoning.get(0).getId());
			if (cacheReckoning != null && !cacheReckoning.isEmpty()) {
				for (Comment comment : cacheReckoning.get(0).getComments()) {
					if (comment.getCommentId().equals(commentId)) {
						comment.addFavorite(favorite);
					}
				}
				
				reckoningCache.setCachedReckoning(cacheReckoning, cacheReckoning.get(0).getId());
			}
		} catch (DBUpdateException dbE) {
			log.error("Database exception when favoriting a reckoning comment: " + dbE.getMessage());
			log.debug("Stack Trace:", dbE);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));				
		} catch (Exception e) {
			log.error("General exception when favoriting a reckoning comment: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}

	@Override
	public ServiceResponse postReckoningCommentFlag(Flag flag, String commentId, String sessionId) {
		try {
			List<Reckoning> commentedReckoning = null;
			List<Comment> flaggedComment = null;
			
			// Verify the data in the request:
			//   * Confirm that the specified user ID exists.
			//   * If so, confirm that the reckoning itself exists to flag.
			//   * Verify that the user hasn't already flagged the reckoning.
			
			if (userService.getUserByUserId(flag.getUserId()).getUser() == null) {
				log.warn("Attempted to flag comment on behalf of non-existent user: " + flag.getUserId());
				return (new ServiceResponse(new Message(MessageEnum.R803_POST_NOTE), false));				
			} else {
				commentedReckoning = commentService.getReckoningComment(commentId).getReckonings();
				if ((commentedReckoning != null) && (!commentedReckoning.isEmpty()) && 
						(commentedReckoning.get(0).getComments() != null) && (!commentedReckoning.get(0).getComments().isEmpty())) 
				{
					flaggedComment = commentedReckoning.get(0).getComments();
					for (Comment comment : flaggedComment) {
						if (comment.getFlagByUser(flag.getUserId()) != null) {
							log.warn("User " + flag.getUserId() + " attempted to flag comment multiple times: " + commentId);
							return (new ServiceResponse(new Message(MessageEnum.R804_POST_NOTE), false));							
						}
					}
				} else {
					log.warn("Attempted to flag non-existent comment: " + commentId);
					return (new ServiceResponse(new Message(MessageEnum.R802_POST_NOTE), false));					
				}
			}  
			
			// We now know the flagged comment exists and hasn't been flagged by this user yet.  Rewrite the comment to the DB with
			// the flag attached to it.  We'll be able to insert the flag directly once MongoDB fixes SERVER-831.
			flag.setFlagDate(DateUtility.now());
			flaggedComment.get(0).addFlag(flag);
			reckoningRepoCustom.updateComment(flaggedComment.get(0));
			
			// Cache management.  If the Reckoning is in the cache, pull it, add the flag to the comment yourself, and roll it back in.
			List<Reckoning> cacheReckoning = reckoningCache.getCachedReckoning(commentedReckoning.get(0).getId());
			if (cacheReckoning != null && !cacheReckoning.isEmpty()) {
				for (Comment comment : cacheReckoning.get(0).getComments()) {
					if (comment.getCommentId().equals(commentId)) {
						comment.addFlag(flag);
					}
				}
				
				reckoningCache.setCachedReckoning(cacheReckoning, cacheReckoning.get(0).getId());
			}
		} catch (DBUpdateException dbE) {
			log.error("Database exception when flagging a reckoning comment: " + dbE.getMessage());
			log.debug("Stack Trace:", dbE);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));				
		} catch (Exception e) {
			log.error("General exception when flagging a reckoning comment: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}
	
	@Override
	public ReckoningServiceList getFavoritedReckoningsByUser(String userId, Integer page, Integer size, String sessionId) {
		List<Reckoning> favoritedReckonings = null;
		long count = 0;
		
		try {
			favoritedReckonings = reckoningCache.getCachedUserFavoritedReckonings(userId);
			if (favoritedReckonings == null) {
				favoritedReckonings = reckoningRepo.getReckoningSummariesFavoritedByUser(userId);
				for (Reckoning reckoning : favoritedReckonings) {
					reckoning.setPostingUser(userService.getUserByUserId
									(reckoning.getSubmitterId(), true).getUser());
				}
				
				reckoningCache.setCachedUserFavoritedReckonings(favoritedReckonings, userId);
			}
			count = Long.valueOf(favoritedReckonings.size());
			favoritedReckonings = (List<Reckoning>) ListPagingUtility.pageList(favoritedReckonings, page, size);
		} catch (Exception e) {
			log.error("General exception when getting favorited reckoning summaries by user: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(favoritedReckonings, count, new Message(), true);
	}

	@Override
	public ReckoningServiceList getFavoritedCommentsByUser(String userId,
			Integer page, Integer size, String sessionId) {
		List<Reckoning> commentedReckonings = null;
		long count = 0;
		
		try {
			commentedReckonings = reckoningCache.getCachedUserFavoritedReckoningComments(userId);
			if (commentedReckonings == null) {
				commentedReckonings = reckoningRepo.getReckoningCommentsFavoritedByUser(userId);
				
				if (commentedReckonings != null) {
					for (Reckoning commentedReckoning : commentedReckonings) {
						commentedReckoning.setComments(commentedReckoning.getFavoritedCommentsByUser(userId));
						count += commentedReckoning.getFavoritedCommentsByUser(userId).size();
					}
				}
			
				reckoningCache.setCachedUserFavoritedReckoningComments(commentedReckonings, userId);
			} else {
				for (Reckoning commentedReckoning : commentedReckonings) {
					count += commentedReckoning.getComments().size();
				}				
			}
			
			commentedReckonings = (List<Reckoning>) ListPagingUtility.pageList(commentedReckonings, page, size);
		} catch (Exception e) {
			log.error("General exception when getting favorited reckoning summaries by user: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(commentedReckonings, count, new Message(), true);
	}
	
}
