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

import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepo;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepoCustom;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepoImpl;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.utility.DateUtility;

@Component
public class ReckoningServiceImpl implements ReckoningService {
	
	@Autowired
	ReckoningRepo reckoningRepo;
	
	@Autowired
	ReckoningRepoCustom reckoningRepoCustomImpl;
	
	private static final Logger log = LoggerFactory
			.getLogger(ReckoningServiceImpl.class);
	
	@Override
	public ServiceResponse postReckoning (Reckoning reckoning, String userToken) {
		
		try {
			reckoningRepoCustomImpl.insertNewReckoning(reckoning);
		} catch (Exception e) {
			log.error("General exception when getting the approval queue: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}
	
	@Override
	public ReckoningServiceList getReckoning (String id, String userToken) {
		List<Reckoning> reckoning = null;
		
		try {
			reckoning = reckoningRepo.findById(id);
		} catch (Exception e) {
			log.error("General exception when retrieving a reckoning: " + e.getMessage());
			log.debug("Stack Trace:", e);	
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}	
		
		return new ReckoningServiceList(reckoning, new Message(), true);
	}
	
	@Override
	public ReckoningServiceList getApprovalQueue (Integer page, Integer size, Boolean latestFirst, String userToken) {
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
	public ReckoningServiceList getReckoningsByUser(String submitterId,
			Integer page, Integer size, String userToken) {
		List<Reckoning> reckonings = null;
		
		try {
			if (page != null && size != null) {
				reckonings = reckoningRepo.findBySubmitterId(submitterId, 
						new PageRequest(page, size, Sort.Direction.DESC, "submissionDate")).getContent();					
			} else {
				reckonings = reckoningRepo.findBySubmitterId(submitterId);
			}
		} catch (Exception e) {
			log.error("General exception when getting reckonings by user: " + e.getMessage());
			log.debug("Stack Trace:", e);		
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(reckonings, new Message(), true);
	}
	
	@Override
	public ReckoningServiceList getReckoningSummariesByUser(String submitterId, String userToken) {
		List<Reckoning> reckonings = null;
		
		try {
			reckonings = reckoningRepo.findBySubmitterIdSummary(submitterId);
		} catch (Exception e) {
			log.error("General exception when getting reckoning summaries by user: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(reckonings, new Message(), true);
	}

	@Override
	public ReckoningServiceList getOpenReckoningsByUser(String submitterId,
			Integer page, Integer size, String userToken) {
		List<Reckoning> reckonings = null;
		
		try {
			if (page != null && size != null) {
				reckonings = reckoningRepo.findBySubmitterIdAndClosingDateGreaterThan(submitterId, DateUtility.now(),
						new PageRequest(page, size, Sort.Direction.DESC, "submissionDate")).getContent();					
			} else {
				reckonings = reckoningRepo.findBySubmitterIdAndClosingDateGreaterThan(submitterId, DateUtility.now());
			}
		} catch (Exception e) {
			log.error("General exception when getting open reckonings by user: " + e.getMessage());
			log.debug("Stack Trace:", e);		
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(reckonings, new Message(), true);
	}

	@Override
	public ReckoningServiceList getClosedReckoningsByUser(String submitterId,
			Integer page, Integer size, String userToken) {
		List<Reckoning> reckonings = null;
		
		try {
			if (page != null && size != null) {
				reckonings = reckoningRepo.findBySubmitterIdAndClosingDateLessThan(submitterId, DateUtility.now(),
						new PageRequest(page, size, Sort.Direction.DESC, "submissionDate")).getContent();					
			} else {
				reckonings = reckoningRepo.findBySubmitterIdAndClosingDateLessThan(submitterId, DateUtility.now());
			}
		} catch (Exception e) {
			log.error("General exception when getting closed reckonings by user: " + e.getMessage());
			log.debug("Stack Trace:", e);		
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(reckonings, new Message(), true);
	}
	
	@Override
	public ReckoningServiceList getApprovalQueueByUser(String submitterId, String userToken) {
		List<Reckoning> reckonings = null;
		
		try {
			reckonings = reckoningRepo.findBySubmitterIdAndApprovedAndRejected(submitterId, false, false);
		} catch (Exception e) {
			log.error("General exception when getting approval queue reckonings by user: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(reckonings, new Message(), true);
	}

	@Override
	public ReckoningServiceList getHighlightedReckonings(Boolean open, String userToken) {
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
			Date closedAfter, Date closedBefore, String userToken) {
		List<Reckoning> reckonings = null;
		
		try {
			if (postedAfter != null || postedBefore != null) {
				reckonings = reckoningRepoCustomImpl.getReckoningSummariesByPostingDate(page, size, postedBefore, postedAfter);
			} else if (closedAfter != null || closedBefore != null) {
				reckonings = reckoningRepoCustomImpl.getReckoningSummariesByClosingDate(page, size, closedBefore, closedAfter);			
			} else {
				reckonings = reckoningRepoCustomImpl.getReckoningSummaries(page, size);
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
	public ReckoningServiceList getReckoningsByTag(String tag, Integer page,
			Integer size, String userToken) {
		List<Reckoning> reckonings = null;
		
		try {
			reckonings = reckoningRepoCustomImpl.getReckoningSummariesByTag(tag, page, size);
		}
		catch (Exception e) {
			log.error("General exception when getting reckonings by tag: " + e.getMessage());
			log.debug("Stack Trace:", e);
			return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
		}
		
		return new ReckoningServiceList(reckonings, new Message(), true);
	}	

}
