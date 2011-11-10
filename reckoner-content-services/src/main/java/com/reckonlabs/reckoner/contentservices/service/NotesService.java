package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;

import com.reckonlabs.reckoner.domain.notes.Favorite;
import com.reckonlabs.reckoner.domain.notes.Flag;

import com.reckonlabs.reckoner.domain.message.CommentServiceList;
import com.reckonlabs.reckoner.domain.message.ContentServiceList;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;

public interface NotesService {
	
	// Reckonings
	public ServiceResponse postReckoningFavorite (Favorite favorite, String reckoningId, String sessionId);
	
	public ServiceResponse postReckoningFlag (Flag flag, String reckoningId, String sessionId);
	
	public ServiceResponse postReckoningCommentFavorite (Favorite favorite, String commentId, String sessionId);
	
	public ServiceResponse postReckoningCommentFlag (Flag flag, String commentId, String sessionId);
	
	public ReckoningServiceList getFavoritedReckonings (Date favoritedAfter, Integer page, Integer size, String sessionId);
	
	public ReckoningServiceList getFlaggedReckonings (Date flaggedAfter, Integer page, Integer size, String sessionId);	
	
	public ReckoningServiceList getFavoritedReckoningComments (Date favoritedAfter, Integer page, Integer size, String sessionId);	
	
	public ReckoningServiceList getFlaggedReckoningComments (Date flaggedAfter, Integer page, Integer size, String sessionId);	
	
	public ReckoningServiceList getFavoritedReckoningCommentsByUser (String userId, Integer page, Integer size, String sessionId);

	public ReckoningServiceList getFavoritedReckoningsByUser (String userId, Integer page, Integer size, String sessionId);
	
	// Content
	public ServiceResponse postContentFavorite (Favorite favorite, String reckoningId, String sessionId);
	
	public ServiceResponse postContentFlag (Flag flag, String reckoningId, String sessionId);
	
	public ServiceResponse postContentCommentFavorite (Favorite favorite, String commentId, String sessionId);
	
	public ServiceResponse postContentCommentFlag (Flag flag, String commentId, String sessionId);
	
	public ContentServiceList getFavoritedContents(Date favoritedAfter, Integer page, Integer size, String sessionId);
	
	public ContentServiceList getFlaggedContents(Date flaggedAfter, Integer page, Integer size, String sessionId);	
	
	public ContentServiceList getFavoritedContentComments (Date favoritedAfter, Integer page, Integer size, String sessionId);	
	
	public ContentServiceList getFlaggedContentComments (Date flaggedAfter, Integer page, Integer size, String sessionId);
}
