package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;

import com.reckonlabs.reckoner.domain.utility.DBUpdateException;

import com.reckonlabs.reckoner.domain.ApprovalStatusEnum;
import com.reckonlabs.reckoner.domain.content.Content;
import com.reckonlabs.reckoner.domain.content.ContentTypeEnum;
import com.reckonlabs.reckoner.domain.media.Media;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.notes.Favorite;
import com.reckonlabs.reckoner.domain.notes.Flag;
import com.reckonlabs.reckoner.domain.notes.Tag;

public interface ContentRepoCustom {
	
	public void insertNewContent (Content content);
	
	public void updateContent (Content content);
	
	public void mergeContent(Content content) throws DBUpdateException;
	
	public void rejectContent (String id, String rejecter) throws DBUpdateException;
	
	public List<Content> getContentItems (ContentTypeEnum contentType, 
			Date postedBeforeDate, Date postedAfterDate,
			List<String> includeTags, String submitterId,
			ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending, Integer page, Integer size, Boolean randomize);
	
	public Long getContentCount (ContentTypeEnum contentType, 
			Date postedBeforeDate, Date postedAfterDate,
			List<String> includeTag, String submitterId,
			ApprovalStatusEnum approvalStatus);
	
	public void insertContentComment (Comment comment, String contentId) throws DBUpdateException;
	
	public void insertContentFavorite (Favorite favorite, String contentId) throws DBUpdateException;
	
	public void insertContentFlag (Flag flag, String contentId) throws DBUpdateException;
	
	public List<Content> getFavoritedContents (Date favoritedAfter, Integer page, Integer size);
	
	public List<Content> getFlaggedContents (Date flaggedAfter, Integer page, Integer size);
	
	public List<Content> getFavoritedContentComments (Date favoritedAfter, Integer page, Integer size);
	
	public List<Content> getFlaggedContentComments (Date flaggedAfter, Integer page, Integer size);
	
	public void incrementContentViews (String contentId) throws DBUpdateException;
	
	public void insertContentMedia (Media media, String contentId) throws DBUpdateException;
	
	public void deleteContentMedia (String mediaId) throws DBUpdateException; 
	
	public void updateComment(Comment comment) throws DBUpdateException;
	
	public void deleteComment (String contentId) throws DBUpdateException;
	
	public boolean confirmContentExists(String contentId);
}
