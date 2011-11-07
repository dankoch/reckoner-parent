package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;

import com.reckonlabs.reckoner.domain.notes.Favorite;
import com.reckonlabs.reckoner.domain.notes.Flag;

import com.reckonlabs.reckoner.domain.message.CommentServiceList;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;

public interface NotesService {
	
	public ServiceResponse postReckoningFavorite (Favorite favorite, String reckoningId, String sessionId);
	
	public ServiceResponse postReckoningFlag (Flag flag, String reckoningId, String sessionId);
	
	public ServiceResponse postReckoningCommentFavorite (Favorite favorite, String commentId, String sessionId);
	
	public ServiceResponse postReckoningCommentFlag (Flag flag, String commentId, String sessionId);
	
	public ReckoningServiceList getFavoritedReckonings (Date favoritedAfter, Integer page, Integer size, String sessionId);
	
	public ReckoningServiceList getFlaggedReckonings (Date flaggedAfter, Integer page, Integer size, String sessionId);	
	
	public ReckoningServiceList getFavoritedReckoningComments (Date favoritedAfter, Integer page, Integer size, String sessionId);	
	
	public ReckoningServiceList getFlaggedReckoningComments (Date flaggedAfter, Integer page, Integer size, String sessionId);	
	
	public ReckoningServiceList getFavoritedCommentsByUser (String userId, Integer page, Integer size, String sessionId);

	public ReckoningServiceList getFavoritedReckoningsByUser (String userId, Integer page, Integer size, String sessionId);
}
