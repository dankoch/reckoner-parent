package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;

import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.message.CommentServiceList;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;

public interface CommentService {
	
	public ServiceResponse postReckoningComment (Comment comment, String sessionId, String reckoningId);
	
	public ReckoningServiceList getComment(String commentId, String sessionId);
	
	public ReckoningServiceList getCommentsByUser (String userId, Integer page, Integer size, String sessionId);
}
