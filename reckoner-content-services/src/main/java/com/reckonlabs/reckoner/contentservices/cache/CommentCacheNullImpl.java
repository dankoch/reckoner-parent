package com.reckonlabs.reckoner.contentservices.cache;

import java.util.List;

import com.reckonlabs.reckoner.domain.notes.Comment;

public class CommentCacheNullImpl implements CommentCache {

	@Override
	public void setUserCommentCache(String userId, List<Comment> value) {
	}

	@Override
	public List<Comment> getUserCommentCache(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeUserCommentCache(String userId) {
	}

}
