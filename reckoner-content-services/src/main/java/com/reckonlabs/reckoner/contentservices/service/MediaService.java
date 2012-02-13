package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;

import com.reckonlabs.reckoner.domain.media.Media;
import com.reckonlabs.reckoner.domain.message.ContentServiceList;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;

public interface MediaService {
	
	// Reckoning
	public ServiceResponse postReckoningMedia (Media media, String reckoningId);
	
	public ReckoningServiceList getReckoningMediaById (String mediaId);
	
	public ServiceResponse deleteReckoningMedia (String mediaId);

	// Content
	public ServiceResponse postContentMedia (Media media, String contentId);
	
	public ServiceResponse deleteContentMedia (String mediaId);
	
	public ContentServiceList getContentMediaById (String mediaId);
}
