package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.contentservices.cache.ReckoningCache;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepo;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepoCustom;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepoImpl;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.reckoning.Answer;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.utility.DateUtility;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;
import com.reckonlabs.reckoner.domain.utility.ListPagingUtility;

@Component
public class ReckoningServiceImpl implements ReckoningService {
	
	@Autowired
	ReckoningRepo reckoningRepo;
	
	@Autowired
	ReckoningRepoCustom reckoningRepoCustom;
	
	@Autowired
	ReckoningCache reckoningCache;
	
	@Autowired
	UserService userService;
	
	private static final Logger log = LoggerFactory
			.getLogger(ReckoningServiceImpl.class);
	
	@Override
	public ServiceResponse postReckoning (Reckoning reckoning, String sessionId) {
		
		try {
			// Compute the answer index for each provided answer.
			int answerIndex = 0;
			for (Answer answer : reckoning.getAnswers()) {
				answer.setIndex(answerIndex);
				answerIndex ++;
			}
			
			// Clean up the reckoning for fields that can't be set for a new posting.
			reckoning.setId(null);
			reckoning.setApproved(false);
			reckoning.setRejected(false);
			reckoning.setSubmissionDate(DateUtility.now());
			reckoning.setPostingDate(null);
			reckoning.setClosingDate(null);
			reckoning.setHighlighted(false);
			reckoning.setFlags(null);
			reckoning.setFavorites(null);
			reckoning.setComments(null);
			
			
			reckoningRepoCustom.insertNewReckoning(reckoning);
			
			reckoningCache.removeCachedUserReckoningSummaries(reckoning.getSubmitterId());
		} catch (Exception e) {
			log.error("General exception when inserting a new reckoning: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}
	
	@Override
	public ServiceResponse updateReckoning (Reckoning reckoning, boolean merge, String sessionId) {
		
		try {
			if (merge) {
				if (reckoningRepoCustom.confirmReckoningExists(reckoning.getId())) {
					reckoningRepoCustom.mergeReckoning(reckoning);
				}
				else {
					return (new ServiceResponse(new Message(MessageEnum.R106_POST_RECKONING), false));
				}
			} else {
				reckoningRepoCustom.updateReckoning(reckoning);
			}
			
			reckoningCache.removeCachedReckoning(reckoning.getId());
		} catch (Exception e) {
			log.error("General exception when updating a reckoning: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}
	
	@Override
	public ServiceResponse approveReckoning (String id, String sessionId) {
		
		try {
			List<Reckoning> approvedReckoning = reckoningRepo.findById(id);
			
			if (approvedReckoning != null && approvedReckoning.size() > 0) {
				reckoningRepoCustom.approveReckoning(id, userService.getUserBySessionId(sessionId).getUser().getId(), DateUtility.now(), 
						new Date(DateUtility.now().getTime() + approvedReckoning.get(0).getInterval() * 60000));
			} else {
				log.info("Request to approve non-existent reckoning: " + id);
				return (new ServiceResponse(new Message(MessageEnum.R300_APPROVE_RECKONING), false));					
			}
			
		} catch (DBUpdateException dbE) {
			log.error("Database exception when accepting a reckoning: " + dbE.getMessage());
			log.debug("Stack Trace:", dbE);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));				
		}
		  catch (Exception e) {
			log.error("General exception when accepting a reckoning: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}
	
	@Override
	public ServiceResponse rejectReckoning (String id, String sessionId) {
		
		try {
			List<Reckoning> rejectedReckoning = reckoningRepo.findById(id);
			
			if (rejectedReckoning != null && rejectedReckoning.size() > 0) {
				reckoningRepoCustom.rejectReckoning(id, userService.getUserBySessionId(sessionId).getUser().getId());
			} else {
				log.info("Request to reject non-existent reckoning: " + id);
				return (new ServiceResponse(new Message(MessageEnum.R300_APPROVE_RECKONING), false));					
			}
			
		} catch (DBUpdateException dbE) {
			log.error("Database exception when rejecting a reckoning: " + dbE.getMessage());
			log.debug("Stack Trace:", dbE);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));				
		}
		  catch (Exception e) {
			log.error("General exception when rejecting a reckoning: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}
	
	@Override
	public ReckoningServiceList getReckoning (String id, String sessionId) {
		List<Reckoning> reckoning = null;
		try {
			reckoning = reckoningCache.getCachedReckoning(id);
			if (reckoning == null) {
				reckoning = reckoningRepo.findById(id);
				reckoningCache.setCachedReckoning(reckoning, id);
			}
		} catch (Exception e) {
			log.error("General exception when retrieving a reckoning: " + e.getMessage());
			log.debug("Stack Trace:", e);	
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}	
		
		return new ReckoningServiceList(reckoning, new Message(), true);
	}
	
	@Override
	public ReckoningServiceList getApprovalQueue (Integer page, Integer size, Boolean latestFirst, String sessionId) {
		List<Reckoning> approvalQueue = null;
		
		try {
			if (page != null && size != null) {
				if (latestFirst == null) {
					latestFirst = false;
				}
				if (latestFirst.booleanValue()) {
					approvalQueue = reckoningRepo.findByApprovedAndRejected(false, false, 
							new PageRequest(page, size, Sort.Direction.DESC, "submissionDate")).getContent();
				} else {
					approvalQueue = reckoningRepo.findByApprovedAndRejected(false, false, 
							new PageRequest(page, size, Sort.Direction.ASC, "submissionDate")).getContent();					
				}
			} else {
				approvalQueue = reckoningRepo.findByApprovedAndRejected(false, false);
			}
		} catch (Exception e) {
			log.error("General exception when getting the approval queue: " + e.getMessage());
			log.debug("Stack Trace:", e);	
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(approvalQueue, new Message(), true);
	}
	
	@Override
	public ReckoningServiceList getReckoningSummariesByUser(String submitterId, Integer page, Integer size, String sessionId) {
		List<Reckoning> reckonings = null;
		
		try {
			reckonings = reckoningCache.getCachedUserReckoningSummaries(submitterId);
			if (reckonings == null) {
				reckonings = reckoningRepo.findBySubmitterIdSummary(submitterId);
				reckoningCache.setCachedUserReckoningSummaries(submitterId, reckonings);
			}
			
			reckonings = (List<Reckoning>) ListPagingUtility.pageList(reckonings, page, size);
		} catch (Exception e) {
			log.error("General exception when getting reckoning summaries by user: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(reckonings, new Message(), true);
	}

	@Override
	public ReckoningServiceList getHighlightedReckonings(Boolean open, String sessionId) {
		List<Reckoning> reckonings = null;
		
		try {
			if (open == null) {
				reckonings = reckoningRepo.findByHighlighted(true);
			} else if (open.booleanValue()) {
				reckonings = reckoningRepo.findByHighlightedAndClosingDateGreaterThan(true, DateUtility.now());
			} else {
				reckonings = reckoningRepo.findByHighlightedAndClosingDateLessThan(true, DateUtility.now());				
			}
		} catch (Exception e) {
			log.error("General exception when getting highlighted reckonings: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(reckonings, new Message(), true);
	}

	@Override
	public ReckoningServiceList getReckoningSummaries(Integer page,
			Integer size, Date postedAfter, Date postedBefore,
			Date closedAfter, Date closedBefore, String sessionId) {
		List<Reckoning> reckonings = null;

		try {
			if (postedAfter != null || postedBefore != null) {
				reckonings = reckoningRepoCustom.getReckoningSummariesByPostingDate(page, size, postedBefore, postedAfter);
			} else if (closedAfter != null || closedBefore != null) {
				reckonings = reckoningRepoCustom.getReckoningSummariesByClosingDate(page, size, closedBefore, closedAfter);
			} else {
				reckonings = reckoningRepoCustom.getReckoningSummaries(page, size);
			} 
		}
		catch (Exception e) {
			log.error("General exception when getting reckoning summaries: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(reckonings, new Message(), true);
	}

	@Override
	public ReckoningServiceList getReckoningSummariesByTag(String tag, Integer page,
			Integer size, String sessionId) {
		List<Reckoning> reckonings = null;		
		try {
			reckonings = reckoningCache.getCachedTagReckoningSummaries(tag, page, size);
			if (reckonings == null) {
				reckonings = reckoningRepoCustom.getReckoningSummariesByTag(tag, page, size);
				reckoningCache.setCachedTagReckoningSummaries(reckonings, tag, page, size);
			}
		}
		catch (Exception e) {
			log.error("General exception when getting reckonings by tag: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(reckonings, new Message(), true);
	}	

}
