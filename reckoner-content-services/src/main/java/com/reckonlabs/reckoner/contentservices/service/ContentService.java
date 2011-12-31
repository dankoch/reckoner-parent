package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;
import java.util.List;

import com.reckonlabs.reckoner.domain.ApprovalStatusEnum;
import com.reckonlabs.reckoner.domain.content.Content;
import com.reckonlabs.reckoner.domain.content.ContentTypeEnum;
import com.reckonlabs.reckoner.domain.message.ContentServiceList;
import com.reckonlabs.reckoner.domain.message.TagServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;

public interface ContentService {
	
	public ServiceResponse postContent (Content content);
	
	public ServiceResponse updateContent (Content content, boolean merge);
	
	public ContentServiceList getContent (String id);
	
	public ContentServiceList getContent (String id, boolean includeUnaccepted, boolean pageVisit);
		
	public ContentServiceList getContentSummaries (ContentTypeEnum contentType, 
			Date postedAfter, Date postedBefore,
			List<String> includeTags,
			String submitterId,
			ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending, Integer page, Integer size, Boolean randomize);
	
	public ContentServiceList getContentCount (ContentTypeEnum contentType, 
			Date postedAfter, Date postedBefore,
			List<String> includeTags,
			String submitterId,
			ApprovalStatusEnum approvalStatus);

	public ServiceResponse rejectContent (String id, String sessionId);
	
	public TagServiceList getTagList(ContentTypeEnum contentType);
}
