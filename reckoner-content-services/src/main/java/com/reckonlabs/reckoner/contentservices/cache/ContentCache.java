package com.reckonlabs.reckoner.contentservices.cache;

import java.util.Date;
import java.util.List;

import com.reckonlabs.reckoner.domain.ApprovalStatusEnum;
import com.reckonlabs.reckoner.domain.content.Content;
import com.reckonlabs.reckoner.domain.content.ContentTypeEnum;

public interface ContentCache {

	public void setCachedContent(List<Content> content, String id);

	public List<Content> getCachedContent(String id);

	public void removeCachedContent(String id);
	
	public void setCachedContentSummaries(ContentTypeEnum contentType, 
			Date postedAfter, Date postedBefore,
			List<String> includeTags,
			String submitterId,
			ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending,
			List<Content> content);

	public List<Content> getCachedContentSummaries(ContentTypeEnum contentType, 
			Date postedAfter, Date postedBefore,
			List<String> includeTags,
			String submitterId,
			ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending);
	
	public void removeCachedContentSummaries(ContentTypeEnum contentType, 
			Date postedAfter, Date postedBefore,
			List<String> includeTags,
			String submitterId,
			ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending);
	
	public void setCachedContentCount(ContentTypeEnum contentType, 
			Date postedAfter, Date postedBefore,
			List<String> includeTags,
			String submitterId,
			ApprovalStatusEnum approvalStatus,
			Long count);
	
	public Long getCachedContentCount(ContentTypeEnum contentType, 
			Date postedAfter, Date postedBefore,
			List<String> includeTags,
			String submitterId,
			ApprovalStatusEnum approvalStatus);
	
	public void removeCachedContentCount(ContentTypeEnum contentType, 
			Date postedAfter, Date postedBefore,
			List<String> includeTags,
			String submitterId,
			ApprovalStatusEnum approvalStatus);
}
