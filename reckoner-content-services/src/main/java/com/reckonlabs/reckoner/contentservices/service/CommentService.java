package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;

import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.message.CommentServiceList;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;

public interface CommentService {
	
	public ServiceResponse postReckoningComment (Comment comment, String reckoningId);
	
	public ReckoningServiceList getReckoningComment(String commentId);
	
	public ReckoningServiceList getReckoningCommentsByUser (String userId, Integer page, Integer size);

	public ServiceResponse updateReckoningComment (Comment comment);
	
	public ServiceResponse deleteReckoningComment (String commentId);
}
