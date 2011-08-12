package com.reckonlabs.reckoner.contentservices.cache;

import java.util.List;

import com.reckonlabs.reckoner.domain.notes.Comment;

public interface CommentCache {

	public void setUserCommentCache(String userId, List<Comment> value);

	public List<Comment> getUserCommentCache(String userId);

	public void removeUserCommentCache(String userId);
}
