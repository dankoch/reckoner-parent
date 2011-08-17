package com.reckonlabs.reckoner.contentservices.cache;

import java.util.List;

import net.spy.memcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.domain.reckoning.Vote;

@Component
public class VoteCacheMemImpl implements VoteCache {

	public static final int USER_RECKONING_VOTE_CACHE = 604800;
	
	public static final String USER_RECKONING_VOTE_CACHE_PREFIX = "user_reckoning_vote_";
	
	@Autowired
	MemcachedClient memcachedClient;
	
	private static final Logger log = LoggerFactory
			.getLogger(VoteCacheMemImpl.class);

	@Override
	public void setCachedUserReckoningVote(List<Vote> vote, String userId,
			String reckoningId) {
		setCacheElement(USER_RECKONING_VOTE_CACHE_PREFIX + userId + "_" + reckoningId, 
				USER_RECKONING_VOTE_CACHE, vote);
	}

	@Override
	public List<Vote> getCachedUserReckoningVote(String userId,
			String reckoningId) {
		
		return getCacheElement(USER_RECKONING_VOTE_CACHE_PREFIX + userId + "_" + reckoningId);
	}

	@Override
	public void removeUserReckoningVote(String userId, String reckoningId) {
		removeCacheElement(USER_RECKONING_VOTE_CACHE_PREFIX + userId + "_" + reckoningId);
	}
	
	private void setCacheElement(String key, int lifespan, List<Vote> value) {
		try {
			memcachedClient.set(key, lifespan, value);
		} catch (Exception e) {
			log.warn("Error when caching vote list: " + e.getMessage());
			log.debug("Stack Trace:", e);	
		}
	}

	private List<Vote> getCacheElement(String key) {
		List<Vote> returnComment = null;
		
		try {
			returnComment = (List<Vote>) memcachedClient.get(key);
		} catch (Exception e) {
			log.warn("Error when retrieving vote list: " + e.getMessage());
			log.debug("Stack Trace:", e);	
		}
		
		return returnComment;
	}

	private void removeCacheElement(String key) {
		try {
			memcachedClient.delete(key);
		} catch (Exception e) {
			log.warn("Error when removing vote list: " + e.getMessage());
			log.debug("Stack Trace:", e);	
		}
	}
}
