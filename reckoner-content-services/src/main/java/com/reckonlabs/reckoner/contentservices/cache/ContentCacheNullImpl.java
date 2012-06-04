package com.reckonlabs.reckoner.contentservices.cache;

import java.util.Date;
import java.util.List;

import com.reckonlabs.reckoner.domain.ApprovalStatusEnum;
import com.reckonlabs.reckoner.domain.content.Content;
import com.reckonlabs.reckoner.domain.content.ContentTypeEnum;
import com.reckonlabs.reckoner.domain.notes.Tag;

public class ContentCacheNullImpl implements ContentCache {

	@Override
	public void setCachedContent(List<Content> content, String id) {
	}

	@Override
	public List<Content> getCachedContent(String id) {
		return null;
	}

	@Override
	public void removeCachedContent(String id) {
	}

	@Override
	public void setCachedContentSummaries(ContentTypeEnum contentType,
			Date postedAfter, Date postedBefore, List<String> includeTags,
			String submitterId, ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending, List<Content> content,
			Integer page, Integer size) {
	}

	@Override
	public List<Content> getCachedContentSummaries(ContentTypeEnum contentType,
			Date postedAfter, Date postedBefore, List<String> includeTags,
			String submitterId, ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending, Integer page, Integer size) {

		return null;
	}

	@Override
	public void removeCachedContentSummaries(ContentTypeEnum contentType,
			Date postedAfter, Date postedBefore, List<String> includeTags,
			String submitterId, ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending, Integer page, Integer size) {
	}

	@Override
	public void setCachedContentCount(ContentTypeEnum contentType,
			Date postedAfter, Date postedBefore, List<String> includeTags,
			String submitterId, ApprovalStatusEnum approvalStatus, Long count) {
	}

	@Override
	public Long getCachedContentCount(ContentTypeEnum contentType,
			Date postedAfter, Date postedBefore, List<String> includeTags,
			String submitterId, ApprovalStatusEnum approvalStatus) {

		return null;
	}

	@Override
	public void removeCachedContentCount(ContentTypeEnum contentType,
			Date postedAfter, Date postedBefore, List<String> includeTags,
			String submitterId, ApprovalStatusEnum approvalStatus) {

	}

	@Override
	public void setCachedTagList(List<Tag> tagList, ContentTypeEnum contentType) {

	}

	@Override
	public List<Tag> getCachedTagList(ContentTypeEnum contentType) {
		return null;
	}

	@Override
	public void removeCachedTagList(ContentTypeEnum contentType) {
	}

}
