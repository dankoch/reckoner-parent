package com.reckonlabs.reckoner.contentservices.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.contentservices.cache.ContentCache;
import com.reckonlabs.reckoner.contentservices.cache.ReckoningCache;
import com.reckonlabs.reckoner.contentservices.repo.ContentRepo;
import com.reckonlabs.reckoner.contentservices.repo.ContentRepoCustom;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepo;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepoCustom;
import com.reckonlabs.reckoner.contentservices.utility.ServiceProps;
import com.reckonlabs.reckoner.domain.content.Content;
import com.reckonlabs.reckoner.domain.media.Media;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.message.ContentServiceList;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;

@Component
public class MediaServiceImpl implements MediaService {
	
	@Autowired
	ReckoningRepo reckoningRepo;
	@Autowired
	ReckoningRepoCustom reckoningRepoCustom;
	
	@Autowired
	ContentRepo contentRepo;
	@Autowired
	ContentRepoCustom contentRepoCustom;
	
	@Resource
	ReckoningCache reckoningCache;
	@Resource
	ContentCache contentCache;
	
	@Autowired
	UserService userService;
	
	@Resource
	ServiceProps serviceProps;
	
	private static final Logger log = LoggerFactory
			.getLogger(MediaServiceImpl.class);

	@Override
	public ServiceResponse postReckoningMedia(Media media,
			String reckoningId) {

		try {
			if (!reckoningRepoCustom.confirmReckoningExists(reckoningId)) {
				log.warn("Attempted to attach media to non-existent reckoning: " + reckoningId);
				return (new ServiceResponse(new Message(MessageEnum.R1201_MEDIA), false));
			}
			reckoningRepoCustom.insertReckoningMedia(media, reckoningId);
			
			// Cache management. Clear out the Reckoning if cached.
			if (serviceProps.isEnableCaching()) {reckoningCache.removeCachedReckoning(reckoningId);}
		} catch (DBUpdateException dbE) {
			log.error("Database exception when inserting a new reckoning comment: " + dbE.getMessage());
			log.debug("Stack Trace:", dbE);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));				
		} catch (Exception e) {
			log.error("General exception when inserting a new reckoning comment: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}

	@Override
	public ReckoningServiceList getReckoningMediaById(String mediaId) {
		List<Reckoning> mediaReckonings = new LinkedList<Reckoning> ();
		
		try {
			   mediaReckonings = reckoningRepo.getReckoningMediaById(mediaId);
			} catch (Exception e) {
			   log.error("General exception when getting comments by user: " + e.getMessage());
			   log.debug("Stack Trace:", e);			
			   return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		    }
			
			if (mediaReckonings.isEmpty()) {
				return new ReckoningServiceList(null, new Message(MessageEnum.R1202_MEDIA), false);
			}
			return new ReckoningServiceList(mediaReckonings, new Message(MessageEnum.R00_DEFAULT), true);
	}

	@Override
	public ServiceResponse deleteReckoningMedia(String mediaId) {
		try {
			List<Reckoning> mediaReckoning = getReckoningMediaById(mediaId).getReckonings();
			if (mediaReckoning == null || mediaReckoning.isEmpty()) {
				return (new ServiceResponse(new Message(MessageEnum.R1202_MEDIA), false));				
			}
			reckoningRepoCustom.deleteReckoningMedia(mediaId);
			
			// Cache management. 
			// Delete the individual reckoning cache (these should be rare, so we're not doing an in-place update).
			// Ignoring the user caches -- those will clear soon enough.
			if (serviceProps.isEnableCaching()) {reckoningCache.removeCachedReckoning(mediaReckoning.get(0).getId());}
		} catch (Exception e) {
		    log.error("General exception when deleting media " + mediaId + " : " + e.getMessage());
		    log.debug("Stack Trace:", e);			
		    return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ServiceResponse();
	}

	@Override
	public ServiceResponse postContentMedia(Media media, String contentId) {

		try {
			if (!reckoningRepoCustom.confirmReckoningExists(contentId)) {
				log.warn("Attempted to attach media to non-existent content: " + contentId);
				return (new ServiceResponse(new Message(MessageEnum.R1201_MEDIA), false));
			}
			reckoningRepoCustom.insertReckoningMedia(media, contentId);
			
			// Cache management. Clear out the Reckoning if cached.
			if (serviceProps.isEnableCaching()) {contentCache.removeCachedContent(contentId);}
		} catch (DBUpdateException dbE) {
			log.error("Database exception when inserting a new reckoning comment: " + dbE.getMessage());
			log.debug("Stack Trace:", dbE);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));				
		} catch (Exception e) {
			log.error("General exception when inserting a new reckoning comment: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}

	@Override
	public ServiceResponse deleteContentMedia(String mediaId) {
		try {
			List<Content> mediaContent = getContentMediaById(mediaId).getContents();
			if (mediaContent == null || mediaContent.isEmpty()) {
				return (new ServiceResponse(new Message(MessageEnum.R1202_MEDIA), false));				
			}
			contentRepoCustom.deleteContentMedia(mediaId);
			
			// Cache management. 
			// Delete the individual reckoning cache (these should be rare, so we're not doing an in-place update).
			// Ignoring the user caches -- those will clear soon enough.
			if (serviceProps.isEnableCaching()) {contentCache.removeCachedContent(mediaContent.get(0).getId());}
		} catch (Exception e) {
		    log.error("General exception when deleting media " + mediaId + " : " + e.getMessage());
		    log.debug("Stack Trace:", e);			
		    return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ServiceResponse();
	}

	@Override
	public ContentServiceList getContentMediaById(String mediaId) {
		List<Content> mediaContent = new LinkedList<Content> ();
		
		try {
			   mediaContent = contentRepo.getContentMediaById(mediaId);
			} catch (Exception e) {
			   log.error("General exception when getting comments by user: " + e.getMessage());
			   log.debug("Stack Trace:", e);			
			   return new ContentServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		    }
			
			if (mediaContent.isEmpty()) {
				return new ContentServiceList(null, new Message(MessageEnum.R1202_MEDIA), false);
			}
			return new ContentServiceList(mediaContent, new Message(MessageEnum.R00_DEFAULT), true);

	}
}
