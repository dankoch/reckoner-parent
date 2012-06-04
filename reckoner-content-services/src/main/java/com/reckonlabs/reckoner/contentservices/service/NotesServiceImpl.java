package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.contentservices.cache.CommentCache;
import com.reckonlabs.reckoner.contentservices.cache.ContentCache;
import com.reckonlabs.reckoner.contentservices.repo.ContentRepo;
import com.reckonlabs.reckoner.contentservices.repo.ContentRepoCustom;
import com.reckonlabs.reckoner.contentservices.cache.ReckoningCache;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepo;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepoCustom;
import com.reckonlabs.reckoner.contentservices.utility.ServiceProps;

import com.reckonlabs.reckoner.domain.Notable;

import com.reckonlabs.reckoner.domain.content.Content;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.notes.Favorite;
import com.reckonlabs.reckoner.domain.notes.Flag;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.user.User;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;
import com.reckonlabs.reckoner.domain.utility.DateUtility;
import com.reckonlabs.reckoner.domain.utility.ListPagingUtility;

import com.reckonlabs.reckoner.domain.message.CommentServiceList;
import com.reckonlabs.reckoner.domain.message.ContentServiceList;
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
	ContentRepo contentRepo;
	@Autowired
	ContentRepoCustom contentRepoCustom;
	@Resource
	ContentCache contentCache;
	
	@Resource
	CommentCache commentCache;
	
	@Autowired
	UserService userService;
	@Autowired
	ReckoningService reckoningService;
	@Autowired
	CommentService commentService;
	@Autowired
	ContentService contentService;
	
	@Resource
	ServiceProps serviceProps;
	
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
			
			if (serviceProps.isEnableCaching()) {
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
			if (serviceProps.isEnableCaching()) {
				List<Reckoning> cacheReckoning = reckoningCache.getCachedReckoning(reckoningId);
				if (cacheReckoning != null && !cacheReckoning.isEmpty()) {
					if (cacheReckoning.get(0) != null) {
						cacheReckoning.get(0).addFlag(flag);
					}
					
					reckoningCache.setCachedReckoning(cacheReckoning, cacheReckoning.get(0).getId());
				}
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
			
			if (serviceProps.isEnableCaching()) {			
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
			if (serviceProps.isEnableCaching()) {
				List<Reckoning> cacheReckoning = reckoningCache.getCachedReckoning(commentedReckoning.get(0).getId());
				if (cacheReckoning != null && !cacheReckoning.isEmpty()) {
					for (Comment comment : cacheReckoning.get(0).getComments()) {
						if (comment.getCommentId().equals(commentId)) {
							comment.addFlag(flag);
						}
					}
					
					reckoningCache.setCachedReckoning(cacheReckoning, cacheReckoning.get(0).getId());
				}
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
	public ReckoningServiceList getFavoritedReckonings(Date favoritedAfter,
			Integer page, Integer size, String sessionId) {
		List<Reckoning> favoritedReckonings = null;
		
		try {
			favoritedReckonings = reckoningRepoCustom.getFavoritedReckonings(favoritedAfter, page, size);
			
			// Pull the user profile associated with the submitter Id and attach it to each Reckoning.
			for (Reckoning reckoning : favoritedReckonings) {
				reckoning.setPostingUser(userService.getUserByUserId
								(reckoning.getSubmitterId(), true).getUser());
			}
			
		} catch (Exception e) {
			log.error("General exception when getting favorited reckoning summaries: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(favoritedReckonings, new Message(), true);
	}

	@Override
	public ReckoningServiceList getFlaggedReckonings(Date flaggedAfter,
			Integer page, Integer size, String sessionId) {
		List<Reckoning> flaggedReckonings = null;
		
		try {
			flaggedReckonings = reckoningRepoCustom.getFlaggedReckonings(flaggedAfter, page, size);
			
			// Pull the user profile associated with the submitter Id and attach it to each Reckoning.
			for (Reckoning reckoning : flaggedReckonings) {
				reckoning.setPostingUser(userService.getUserByUserId
								(reckoning.getSubmitterId(), true).getUser());
			}
		} catch (Exception e) {
			log.error("General exception when getting favorited reckoning summaries: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(flaggedReckonings, new Message(), true);
	}

	@Override
	public ReckoningServiceList getFavoritedReckoningComments(
			Date favoritedAfter, Integer page, Integer size, String sessionId) {
		List<Reckoning> favoritedCommentReckonings = null;
		
		try {
			favoritedCommentReckonings = reckoningRepoCustom.getFavoritedReckoningComments(favoritedAfter, page, size);
			// Sift through each reckoning and include only the comments that were flagged according to the criteria.
			for (Reckoning reckoning : favoritedCommentReckonings) {
				List<Comment> favoritedComments = new LinkedList<Comment> ();
				for (Comment comment : reckoning.getComments()) {
					if (!comment.getFavoriteAfterDate(favoritedAfter).isEmpty()) {
						comment.setUser(userService.getUserByUserId(comment.getPosterId(), true).getUser());
						favoritedComments.add(comment);
					}
				}
				
				reckoning.setComments(favoritedComments);
			}
			
		} catch (Exception e) {
			log.error("General exception when getting favorited reckoning comment summaries: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(favoritedCommentReckonings, new Message(), true);
	}

	@Override
	public ReckoningServiceList getFlaggedReckoningComments(Date flaggedAfter,
			Integer page, Integer size, String sessionId) {
		List<Reckoning> flaggedCommentReckonings = null;
		
		try {
			flaggedCommentReckonings = reckoningRepoCustom.getFlaggedReckoningComments(flaggedAfter, page, size);
			// Sift through each reckoning and include only the comments that were flagged according to the criteria.
			for (Reckoning reckoning : flaggedCommentReckonings) {
				List<Comment> flaggedComments = new LinkedList<Comment> ();
				for (Comment comment : reckoning.getComments()) {
					if (!comment.getFlagAfterDate(flaggedAfter).isEmpty()) {
						comment.setUser(userService.getUserByUserId(comment.getPosterId(), true).getUser());
						flaggedComments.add(comment);
					}
				}
				
				reckoning.setComments(flaggedComments);
			}
			
		} catch (Exception e) {
			log.error("General exception when getting flagged reckoning comment summaries: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(flaggedCommentReckonings, new Message(), true);
	}
	
	@Override
	public ReckoningServiceList getFavoritedReckoningsByUser(String userId, Integer page, Integer size, String sessionId) {
		List<Reckoning> favoritedReckonings = null;
		long count = 0;
		
		try {
			if (serviceProps.isEnableCaching()) {favoritedReckonings = reckoningCache.getCachedUserFavoritedReckonings(userId);}
			if (favoritedReckonings == null) {
				favoritedReckonings = reckoningRepo.getReckoningSummariesFavoritedByUser(userId);
				for (Reckoning reckoning : favoritedReckonings) {
					reckoning.setPostingUser(userService.getUserByUserId
									(reckoning.getSubmitterId(), true).getUser());
				}
				
				if (serviceProps.isEnableCaching()) {reckoningCache.setCachedUserFavoritedReckonings(favoritedReckonings, userId);}
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
	public ReckoningServiceList getFavoritedReckoningCommentsByUser(String userId,
			Integer page, Integer size, String sessionId) {
		List<Reckoning> commentedReckonings = null;
		long count = 0;
		
		try {
			if (serviceProps.isEnableCaching()) {commentedReckonings = reckoningCache.getCachedUserFavoritedReckoningComments(userId);}
			if (commentedReckonings == null) {
				commentedReckonings = reckoningRepo.getReckoningCommentsFavoritedByUser(userId);
				
				if (commentedReckonings != null) {
					for (Reckoning commentedReckoning : commentedReckonings) {
						commentedReckoning.setComments(commentedReckoning.getFavoritedCommentsByUser(userId));
						count += commentedReckoning.getFavoritedCommentsByUser(userId).size();
					}
				}
			
				if (serviceProps.isEnableCaching()) {reckoningCache.setCachedUserFavoritedReckoningComments(commentedReckonings, userId);}
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
	
	@Override
	public ServiceResponse postContentFavorite(Favorite favorite,
			String contentId, String sessionId) {
		try {
			// Verify the data in the request:
			//   * Confirm that the specified user ID exists.
			//   * If so, confirm that the reckoning itself exists to favorite.
			//   * Verify that the user isn't trying to favorite their own content.
			//   * Verify that the user hasn't already favorited the content.
			
			if (userService.getUserByUserId(favorite.getUserId()).getUser() == null) {
				log.warn("Attempted to favorite on behalf of non-existent user: " + favorite.getUserId());
				return (new ServiceResponse(new Message(MessageEnum.R803_POST_NOTE), false));				
			} else {
				List<Content> favoritedContent = contentService.getContent(contentId).getContents();
				if (favoritedContent == null || favoritedContent.isEmpty()) {
					log.warn("Attempted to favorite non-existent content: " + contentId);
					return (new ServiceResponse(new Message(MessageEnum.R801_POST_NOTE), false));
				} else {
					for (Content content : favoritedContent) {
						if (content.getFavoriteByUser(favorite.getUserId()) != null) {
							log.warn("User " + favorite.getUserId() + " attempted to favorite content multiple times: " + contentId);
							return (new ServiceResponse(new Message(MessageEnum.R804_POST_NOTE), false));							
						}
					}
				}
			}  
			
			favorite.setFavoriteDate(DateUtility.now());
			contentRepoCustom.insertContentFavorite(favorite, contentId);
			
			// Cache management. Check to see if the content is already in cache.  If so, update it.  Otherwise, forget it.
			if (serviceProps.isEnableCaching()) {
				List<Content> cacheContent = contentCache.getCachedContent(contentId);
				if (cacheContent != null && !cacheContent.isEmpty()) {
					if (cacheContent.get(0) != null) {
						cacheContent.get(0).addFavorite(favorite);
					}
					
					contentCache.setCachedContent(cacheContent, cacheContent.get(0).getId());
				}
			}
		} catch (DBUpdateException dbE) {
			log.error("Database exception when favoriting a content item: " + dbE.getMessage());
			log.debug("Stack Trace:", dbE);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));				
		} catch (Exception e) {
			log.error("General exception when favoriting a content item: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}

	@Override
	public ServiceResponse postContentFlag(Flag flag, String contentId, String sessionId) {
		try {
			// Verify the data in the request:
			//   * Confirm that the specified user ID exists.
			//   * If so, confirm that the content itself exists to flag.
			//   * Verify that the user hasn't already flagged the content.
			
			if (userService.getUserByUserId(flag.getUserId()).getUser() == null) {
				log.warn("Attempted to flag on behalf of non-existent user: " + flag.getUserId());
				return (new ServiceResponse(new Message(MessageEnum.R803_POST_NOTE), false));				
			} else {
				List<Content> flaggedContent = contentService.getContent(contentId).getContents();
				if (flaggedContent == null || flaggedContent.isEmpty()) {
					log.warn("Attempted to flag non-existent content: " + contentId);
					return (new ServiceResponse(new Message(MessageEnum.R801_POST_NOTE), false));
				} else {
					for (Content content : flaggedContent) {
						if (content.getFlagByUser(flag.getUserId()) != null) {
							log.warn("User " + flag.getUserId() + " attempted to flag content multiple times: " + contentId);
							return (new ServiceResponse(new Message(MessageEnum.R804_POST_NOTE), false));							
						}
					}
				}
			} 
			
			flag.setFlagDate(DateUtility.now());
			contentRepoCustom.insertContentFlag(flag, contentId);
			
			// Cache management. Check to see if the content is already in cache.  If so, update it.  Otherwise, forget it.
			if (serviceProps.isEnableCaching()) {
				List<Content> cacheContent = contentCache.getCachedContent(contentId);
				if (cacheContent != null && !cacheContent.isEmpty()) {
					if (cacheContent.get(0) != null) {
						cacheContent.get(0).addFlag(flag);
					}
					
					contentCache.setCachedContent(cacheContent, cacheContent.get(0).getId());
				}
			}
		} catch (DBUpdateException dbE) {
			log.error("Database exception when flagging a content: " + dbE.getMessage());
			log.debug("Stack Trace:", dbE);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));				
		} catch (Exception e) {
			log.error("General exception when flaggin a content: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}

	@Override
	public ServiceResponse postContentCommentFavorite(Favorite favorite,
			String commentId, String sessionId) {
		try {
			List<Content> commentedContent = null;
			List<Comment> favoritedComment = null;
			
			// Verify the data in the request:
			//   * Confirm that the specified user ID exists.
			//   * If so, confirm that the content itself exists to favorite.
			//   * Verify that the user isn't favoriting their own comment.
			//   * Verify that the user hasn't already favorited the comment.
			
			if (userService.getUserByUserId(favorite.getUserId()).getUser() == null) {
				log.warn("Attempted to favorite comment on behalf of non-existent user: " + favorite.getUserId());
				return (new ServiceResponse(new Message(MessageEnum.R803_POST_NOTE), false));				
			} else {
				commentedContent = commentService.getContentComment(commentId).getContents();
				if ((commentedContent != null) && (!commentedContent.isEmpty()) && 
						(commentedContent.get(0).getComments() != null) && (!commentedContent.get(0).getComments().isEmpty())) 
				{
					favoritedComment = commentedContent.get(0).getComments();
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
			contentRepoCustom.updateComment(favoritedComment.get(0));
			
			// Cache management.  If the Content is in the cache, pull it, add the favorite to the comment yourself, and roll it back in.
			if (serviceProps.isEnableCaching()) {
				List<Content> cacheContent = contentCache.getCachedContent(commentedContent.get(0).getId());
				if (cacheContent != null && !cacheContent.isEmpty()) {
					for (Comment comment : cacheContent.get(0).getComments()) {
						if (comment.getCommentId().equals(commentId)) {
							comment.addFavorite(favorite);
						}
					}
					
					contentCache.setCachedContent(cacheContent, cacheContent.get(0).getId());
				}
			}
		} catch (DBUpdateException dbE) {
			log.error("Database exception when favoriting a content comment: " + dbE.getMessage());
			log.debug("Stack Trace:", dbE);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));				
		} catch (Exception e) {
			log.error("General exception when favoriting a content comment: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}

	@Override
	public ServiceResponse postContentCommentFlag(Flag flag, String commentId, String sessionId) {
		try {
			List<Content> commentedContent = null;
			List<Comment> flaggedComment = null;
			
			// Verify the data in the request:
			//   * Confirm that the specified user ID exists.
			//   * If so, confirm that the content itself exists to flag.
			//   * Verify that the user hasn't already flagged the content.
			
			if (userService.getUserByUserId(flag.getUserId()).getUser() == null) {
				log.warn("Attempted to flag comment on behalf of non-existent user: " + flag.getUserId());
				return (new ServiceResponse(new Message(MessageEnum.R803_POST_NOTE), false));				
			} else {
				commentedContent = commentService.getContentComment(commentId).getContents();
				if ((commentedContent != null) && (!commentedContent.isEmpty()) && 
						(commentedContent.get(0).getComments() != null) && (!commentedContent.get(0).getComments().isEmpty())) 
				{
					flaggedComment = commentedContent.get(0).getComments();
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
			contentRepoCustom.updateComment(flaggedComment.get(0));
			
			// Cache management.  If the Content is in the cache, pull it, add the flag to the comment yourself, and roll it back in.
			if (serviceProps.isEnableCaching()) {
				List<Content> cacheContent = contentCache.getCachedContent(commentedContent.get(0).getId());
				if (cacheContent != null && !cacheContent.isEmpty()) {
					for (Comment comment : cacheContent.get(0).getComments()) {
						if (comment.getCommentId().equals(commentId)) {
							comment.addFlag(flag);
						}
					}
					
					contentCache.setCachedContent(cacheContent, cacheContent.get(0).getId());
				}
			}
		} catch (DBUpdateException dbE) {
			log.error("Database exception when flagging a content comment: " + dbE.getMessage());
			log.debug("Stack Trace:", dbE);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));				
		} catch (Exception e) {
			log.error("General exception when flagging a content comment: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}
	
	@Override
	public ContentServiceList getFavoritedContents(Date favoritedAfter,
			Integer page, Integer size, String sessionId) {
		List<Content> favoritedContents = null;
		
		try {
			favoritedContents = contentRepoCustom.getFavoritedContents(favoritedAfter, page, size);
			
			// Pull the user profile associated with the submitter Id and attach it to each Content.
			for (Content content : favoritedContents) {
				content.setPostingUser(userService.getUserByUserId
								(content.getSubmitterId(), true).getUser());
			}
			
		} catch (Exception e) {
			log.error("General exception when getting favorited content summaries: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ContentServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ContentServiceList(favoritedContents, new Message(), true);
	}

	@Override
	public ContentServiceList getFlaggedContents(Date flaggedAfter,
			Integer page, Integer size, String sessionId) {
		List<Content> flaggedContents = null;
		
		try {
			flaggedContents = contentRepoCustom.getFlaggedContents(flaggedAfter, page, size);
			
			// Pull the user profile associated with the submitter Id and attach it to each Content.
			for (Content content : flaggedContents) {
				content.setPostingUser(userService.getUserByUserId
								(content.getSubmitterId(), true).getUser());
			}
		} catch (Exception e) {
			log.error("General exception when getting favorited content summaries: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ContentServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ContentServiceList(flaggedContents, new Message(), true);
	}

	@Override
	public ContentServiceList getFavoritedContentComments(
			Date favoritedAfter, Integer page, Integer size, String sessionId) {
		List<Content> favoritedCommentContents = null;
		
		try {
			favoritedCommentContents = contentRepoCustom.getFavoritedContentComments(favoritedAfter, page, size);
			// Sift through each content and include only the comments that were flagged according to the criteria.
			for (Content content : favoritedCommentContents) {
				List<Comment> favoritedComments = new LinkedList<Comment> ();
				for (Comment comment : content.getComments()) {
					if (!comment.getFavoriteAfterDate(favoritedAfter).isEmpty()) {
						comment.setUser(userService.getUserByUserId(comment.getPosterId(), true).getUser());
						favoritedComments.add(comment);
					}
				}
				
				content.setComments(favoritedComments);
			}
			
		} catch (Exception e) {
			log.error("General exception when getting favorited content comment summaries: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ContentServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ContentServiceList(favoritedCommentContents, new Message(), true);
	}

	@Override
	public ContentServiceList getFlaggedContentComments(Date flaggedAfter,
			Integer page, Integer size, String sessionId) {
		List<Content> flaggedCommentContents = null;
		
		try {
			flaggedCommentContents = contentRepoCustom.getFlaggedContentComments(flaggedAfter, page, size);
			// Sift through each content and include only the comments that were flagged according to the criteria.
			for (Content content : flaggedCommentContents) {
				List<Comment> flaggedComments = new LinkedList<Comment> ();
				for (Comment comment : content.getComments()) {
					if (!comment.getFlagAfterDate(flaggedAfter).isEmpty()) {
						comment.setUser(userService.getUserByUserId(comment.getPosterId(), true).getUser());
						flaggedComments.add(comment);
					}
				}
				
				content.setComments(flaggedComments);
			}
			
		} catch (Exception e) {
			log.error("General exception when getting flagged content comment summaries: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ContentServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ContentServiceList(flaggedCommentContents, new Message(), true);
	}
	
}
