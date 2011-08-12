package com.reckonlabs.reckoner.contentservices.cache;

import java.util.Date;
import java.util.List;
import java.util.LinkedList;

import net.spy.memcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

@Component
public class ReckoningCacheMemImpl implements ReckoningCache {

	public static final int RECKONING_CACHE_LIFESPAN = 604800;
	public static final int RECKONING_SUMMARY_CACHE_LIFESPAN = 604800;
	public static final int USER_RECKONING_SUMMARY_CACHE_LIFESPAN = 604800;
	// Cut to 30 minutes.  We don't manually clear the tag caches when a new reckoning gets posted.
	public static final int TAG_RECKONING_SUMMARY_CACHE_LIFESPAN = 1800;	
	public static final int USER_COMMENTED_RECKONING_CACHE_LIFESPAN = 604800;
	
	public static final String RECKONING_CACHE_PREFIX = "reckoning_";
	public static final String RECKONING_SUMMARY_CACHE_PREFIX = "reckoning_summary_";
	public static final String USER_RECKONING_SUMMARY_CACHE_PREFIX = "user_reckoning_summary_";
	public static final String TAG_RECKONING_SUMMARY_CACHE_PREFIX = "tag_reckoning_summary_";
	public static final String USER_COMMENTED_RECKONING_CACHE_PREFIX = "user_commented_reckoning_";
	
	@Autowired
	MemcachedClient memcachedClient;
	
	private static final Logger log = LoggerFactory
			.getLogger(ReckoningCacheMemImpl.class);
	
	@Override
	public void setCachedReckoning(List<Reckoning> reckoning, String id) {
		
		setCacheElement(RECKONING_CACHE_PREFIX + id, RECKONING_CACHE_LIFESPAN, reckoning);
	}

	@Override
	public List<Reckoning> getCachedReckoning(String id) {
		return getCacheElement(RECKONING_CACHE_PREFIX + id);
	}

	@Override
	public void removeCachedReckoning(String id) {
		removeCacheElement(RECKONING_CACHE_PREFIX + id);
	}

	@Override
	public void setCachedReckoningSummaries(List<Reckoning> reckonings,
			Integer page, Integer size, Date postedAfter, Date postedBefore,
			Date closedAfter, Date closedBefore) {
		
		setCacheElement(buildSummaryKeyString(page, size, postedAfter, postedBefore, closedAfter, closedBefore), 
				RECKONING_SUMMARY_CACHE_LIFESPAN, reckonings);
	}

	@Override
	public List<Reckoning> getCachedReckoningSummaries(Integer page,
			Integer size, Date postedAfter, Date postedBefore,
			Date closedAfter, Date closedBefore) {
		
		return getCacheElement(buildSummaryKeyString(page, size, postedAfter, 
				postedBefore, closedAfter, closedBefore));
	}

	@Override
	public void removeCachedReckoningSummaries(Integer page, Integer size,
			Date postedAfter, Date postedBefore, Date closedAfter,
			Date closedBefore) {

		removeCacheElement(buildSummaryKeyString(page, size, postedAfter, 
				postedBefore, closedAfter, closedBefore));
	}

	@Override
	public void setCachedUserReckoningSummaries(String userId,
			List<Reckoning> reckonings) {
		setCacheElement(USER_RECKONING_SUMMARY_CACHE_PREFIX + userId, USER_RECKONING_SUMMARY_CACHE_LIFESPAN, reckonings);
	}

	@Override
	public List<Reckoning> getCachedUserReckoningSummaries(String userId) {
		return getCacheElement(USER_RECKONING_SUMMARY_CACHE_PREFIX + userId);
	}

	@Override
	public void removeCachedUserReckoningSummaries(String userId) {
		removeCacheElement(USER_RECKONING_SUMMARY_CACHE_PREFIX + userId);
	}

	@Override
	public void setCachedTagReckoningSummaries(List<Reckoning> reckonings,
			String tag, Integer page, Integer size) {
		setCacheElement(TAG_RECKONING_SUMMARY_CACHE_PREFIX + tag + page + size, 
				TAG_RECKONING_SUMMARY_CACHE_LIFESPAN, reckonings);
	}

	@Override
	public List<Reckoning> getCachedTagReckoningSummaries(String tag, Integer page,
			Integer size) {
		return getCacheElement(TAG_RECKONING_SUMMARY_CACHE_PREFIX + tag + page + size);
	}

	@Override
	public void removeCachedTagReckoningSummaries(String tag, Integer page,
			Integer size) {
		removeCacheElement(TAG_RECKONING_SUMMARY_CACHE_PREFIX + tag + page + size);
	}
	
	@Override
	public void setCachedUserCommentedReckonings(List<Reckoning> reckonings,
			String userId) {
		setCacheElement(TAG_RECKONING_SUMMARY_CACHE_PREFIX + userId, 
				TAG_RECKONING_SUMMARY_CACHE_LIFESPAN, reckonings);
	}

	@Override
	public List<Reckoning> getCachedUserCommentedReckonings(String userId) {
		return getCacheElement(TAG_RECKONING_SUMMARY_CACHE_PREFIX + userId);
	}

	@Override
	public void removeCachedUserCommentedReckonings(String userId) {
		removeCacheElement(TAG_RECKONING_SUMMARY_CACHE_PREFIX + userId);
	}
	
	private void setCacheElement(String key, int lifespan, List<Reckoning> value) {
		try {
			memcachedClient.set(key, lifespan, value);
		} catch (Exception e) {
			log.warn("Error when caching comment list: " + e.getMessage());
			log.debug("Stack Trace:", e);	
		}
	}

	private List<Reckoning> getCacheElement(String key) {
		List<Reckoning> returnComment = null;
		
		try {
			returnComment = (List<Reckoning>) memcachedClient.get(key);
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
	
	private static String buildSummaryKeyString (Integer page, Integer size, Date postedAfter, Date postedBefore,
			Date closedAfter, Date closedBefore) {
			String keyString = RECKONING_SUMMARY_CACHE_PREFIX;
			
			if (page != null && size != null) {
				keyString += page.toString() + size.toString();
			}
			
			if (postedAfter != null) {keyString += new Long(postedAfter.getTime());}
			if (postedBefore != null) {keyString += new Long(postedBefore.getTime());}
			if (closedAfter != null) {keyString += new Long(closedAfter.getTime());}
			if (closedBefore != null) {keyString += new Long(closedBefore.getTime());}
			
			return keyString;
	}
}
