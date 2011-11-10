package com.reckonlabs.reckoner.contentservices.cache;

import java.util.Date;
import java.util.List;

import net.spy.memcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.domain.ApprovalStatusEnum;
import com.reckonlabs.reckoner.domain.content.Content;
import com.reckonlabs.reckoner.domain.content.ContentTypeEnum;

@Component
public class ContentCacheMemImpl implements ContentCache {
	
	public static final int CONTENT_CACHE_LIFESPAN = 604800;
	public static final int CONTENT_SUMMARY_CACHE_LIFESPAN = 1200;
	public static final int CONTENT_COUNT_CACHE_LIFESPAN = 1200;
	
	public static final String CONTENT_CACHE_PREFIX = "content_";
	public static final String CONTENT_SUMMARY_CACHE_PREFIX = "content_summary_";
	public static final String CONTENT_COUNT_CACHE_PREFIX = "content_count_";
	
	@Autowired
	MemcachedClient memcachedClient;
	
	private static final Logger log = LoggerFactory
			.getLogger(ContentCacheMemImpl.class);
	
	@Override
	public void setCachedContent(List<Content> content, String id) {
		setCacheElement(CONTENT_CACHE_PREFIX + id, CONTENT_CACHE_LIFESPAN, content);
	}

	@Override
	public List<Content> getCachedContent(String id) {
		return getCacheElement(CONTENT_CACHE_PREFIX + id);
	}

	@Override
	public void removeCachedContent(String id) {
		removeCacheElement(CONTENT_CACHE_PREFIX + id);
	}

	@Override
	public void setCachedContentSummaries(ContentTypeEnum contentType,
			Date postedAfter, Date postedBefore, List<String> includeTags,
			String submitterId, ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending, List<Content> content) {
		
		setCacheElement(buildSummaryKeyString(contentType,
				postedAfter, postedBefore, includeTags,
				submitterId, approvalStatus,
				sortBy, ascending), 
				CONTENT_SUMMARY_CACHE_LIFESPAN, content);
	}

	@Override
	public List<Content> getCachedContentSummaries(ContentTypeEnum contentType,
			Date postedAfter, Date postedBefore, List<String> includeTags,
			String submitterId, ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending) {
		
		return getCacheElement(buildSummaryKeyString(contentType,
				postedAfter, postedBefore, includeTags,
				submitterId, approvalStatus,
				sortBy, ascending));
	}

	@Override
	public void removeCachedContentSummaries(ContentTypeEnum contentType,
			Date postedAfter, Date postedBefore, List<String> includeTags,
			String submitterId, ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending) {
		
		removeCacheElement(buildSummaryKeyString(contentType,
				postedAfter, postedBefore, includeTags,
				submitterId, approvalStatus,
				sortBy, ascending));
	}

	@Override
	public void setCachedContentCount(ContentTypeEnum contentType,
			Date postedAfter, Date postedBefore, List<String> includeTags,
			String submitterId, ApprovalStatusEnum approvalStatus, Long count) {
		
		setCountCacheElement(buildCountKeyString(contentType,
				postedAfter, postedBefore, includeTags,
				submitterId, approvalStatus,
				null, null), 
				CONTENT_COUNT_CACHE_LIFESPAN, count);
	}

	@Override
	public Long getCachedContentCount(ContentTypeEnum contentType,
			Date postedAfter, Date postedBefore, List<String> includeTags,
			String submitterId, ApprovalStatusEnum approvalStatus) {
		
		return getCountCacheElement(buildCountKeyString(contentType,
				postedAfter, postedBefore, includeTags,
				submitterId, approvalStatus,
				null, null));
	}

	@Override
	public void removeCachedContentCount(ContentTypeEnum contentType,
			Date postedAfter, Date postedBefore, List<String> includeTags,
			String submitterId, ApprovalStatusEnum approvalStatus) {
		
		removeCacheElement(buildCountKeyString(contentType,
				postedAfter, postedBefore, includeTags,
				submitterId, approvalStatus,
				null, null));
	}
	
	private void setCacheElement(String key, int lifespan, List<Content> value) {
		try {
			memcachedClient.set(key, lifespan, value);
		} catch (Exception e) {
			log.warn("Error when caching content list: " + e.getMessage());
			log.debug("Stack Trace:", e);	
		}
	}

	private List<Content> getCacheElement(String key) {
		List<Content> returnContent = null;
		
		try {
			returnContent = (List<Content>) memcachedClient.get(key);
		} catch (Exception e) {
			log.warn("Error when retrieving content list: " + e.getMessage());
			log.debug("Stack Trace:", e);	
		}
		
		return returnContent;
	}
	
	private void setCountCacheElement(String key, int lifespan, Long count) {
		try {
			memcachedClient.set(key, lifespan, count);
		} catch (Exception e) {
			log.warn("Error when caching content count list: " + e.getMessage());
			log.debug("Stack Trace:", e);	
		}
	}

	private Long getCountCacheElement(String key) {
		Long returnContent = null;
		
		try {
			returnContent = (Long) memcachedClient.get(key);
		} catch (Exception e) {
			log.warn("Error when retrieving content count list: " + e.getMessage());
			log.debug("Stack Trace:", e);	
		}
		
		return returnContent;
	}

	private void removeCacheElement(String key) {
		try {
			memcachedClient.delete(key);
		} catch (Exception e) {
			log.warn("Error when removing content list: " + e.getMessage());
			log.debug("Stack Trace:", e);	
		}
	}
	
	private static String buildSummaryKeyString (ContentTypeEnum contentType,
			Date postedAfter, Date postedBefore, List<String> includeTags,
			String submitterId, ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending) {
		
			String keyString = CONTENT_SUMMARY_CACHE_PREFIX;
			
			if (contentType != null) { keyString += contentType.getCode(); }
			if (postedAfter != null) { keyString += new Long(postedAfter.getTime()); }
			if (postedBefore != null) { keyString += new Long(postedBefore.getTime()); }
			if (includeTags != null) {
				for (String tag : includeTags) { keyString += tag; }
			}
			if (submitterId != null) { keyString += submitterId; }
			if (approvalStatus != null) { keyString += approvalStatus.getCode(); }
			if (sortBy != null) { keyString += sortBy; }
			if (ascending != null) { keyString += ascending.toString(); }
			
			return keyString;
	}
	
	private static String buildCountKeyString (ContentTypeEnum contentType,
			Date postedAfter, Date postedBefore, List<String> includeTags,
			String submitterId, ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending) {
		
			String keyString = CONTENT_COUNT_CACHE_PREFIX;
			
			if (contentType != null) { keyString += contentType.getCode(); }
			if (postedAfter != null) { keyString += new Long(postedAfter.getTime()); }
			if (postedBefore != null) { keyString += new Long(postedBefore.getTime()); }
			if (includeTags != null) {
				for (String tag : includeTags) { keyString += tag; }
			}
			if (submitterId != null) { keyString += submitterId; }
			if (approvalStatus != null) { keyString += approvalStatus.getCode(); }
			if (sortBy != null) { keyString += sortBy; }
			if (ascending != null) { keyString += ascending.toString(); }
			
			return keyString;
	}
}
