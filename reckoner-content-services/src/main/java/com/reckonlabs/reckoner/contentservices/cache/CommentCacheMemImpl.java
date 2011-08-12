package com.reckonlabs.reckoner.contentservices.cache;

import java.util.List;

import net.spy.memcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.domain.notes.Comment;

@Component
public class CommentCacheMemImpl implements CommentCache {
	
	public static final int USER_COMMENT_CACHE_LIFESPAN = 604800;
	
	public static final String USER_COMMENT_CACHE_PREFIX = "user_comments_";
	
	@Autowired
	MemcachedClient memcachedClient;
	
	private static final Logger log = LoggerFactory
			.getLogger(CommentCacheMemImpl.class);
	
	@Override
	public void setUserCommentCache(String userId, List<Comment> value) {
		setCacheElement(USER_COMMENT_CACHE_PREFIX + userId, USER_COMMENT_CACHE_LIFESPAN, value);	
	}

	@Override
	public List<Comment> getUserCommentCache(String userId) {
		return getCacheElement (USER_COMMENT_CACHE_PREFIX + userId);
	}

	@Override
	public void removeUserCommentCache(String userId) {
		removeCacheElement (USER_COMMENT_CACHE_PREFIX + userId);
	}
	
	private void setCacheElement(String key, int lifespan, List<Comment> value) {
		try {
			memcachedClient.set(key, lifespan, value);
		} catch (Exception e) {
			log.warn("Error when caching comment list: " + e.getMessage());
			log.debug("Stack Trace:", e);	
		}
	}

	private List<Comment> getCacheElement(String key) {
		List<Comment> returnComment = null;
		
		try {
			returnComment = (List<Comment>) memcachedClient.get(key);
		} catch (Exception e) {
			log.warn("Error when retrieving comment list: " + e.getMessage());
			log.debug("Stack Trace:", e);	
		}
		
		return returnComment;
	}

	private void removeCacheElement(String key) {
		try {
			memcachedClient.delete(key);
		} catch (Exception e) {
			log.warn("Error when removing comment list: " + e.getMessage());
			log.debug("Stack Trace:", e);	
		}
	}

}
