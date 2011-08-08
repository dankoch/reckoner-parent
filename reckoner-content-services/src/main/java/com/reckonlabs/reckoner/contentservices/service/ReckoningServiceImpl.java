package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepo;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepoCustom;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

@Component
public class ReckoningServiceImpl implements ReckoningService {
	
	@Autowired
	ReckoningRepo reckoningRepo;
	
	@Autowired
	ReckoningRepoCustom reckoningRepoCustomImpl;
	
	private static final Logger log = LoggerFactory
			.getLogger(ReckoningServiceImpl.class);
	
	public ServiceResponse postReckoning (Reckoning reckoning, String userToken) {
		Message response = null;
		
		try {
			reckoningRepoCustomImpl.insertNewReckoning(reckoning);
		} catch (DataAccessException daoE) {
			response = new Message(MessageEnum.R104_POST_RECKONING);
			log.error("Database exception when inserting reckoning \"" + reckoning.getQuestion() + "\": " + daoE.getMessage());
			log.debug("Stack Trace:", daoE);
		} catch (Exception e) {
			response = new Message(MessageEnum.R01_DEFAULT);
			log.error("General exception when getting the approval queue: " + e.getMessage());
			log.debug("Stack Trace:", e);			
		}
		
		if (response == null) {
			return new ServiceResponse();
		}
		
		return (new ServiceResponse(response, false));		
	}
	
	public ReckoningServiceList getApprovalQueue (Integer page, Integer size, Boolean latestFirst, String userToken) {
		List<Reckoning> approvalQueue = null;
		Message response = null;
		
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
		} catch (DataAccessException daoE) {
			response = new Message(MessageEnum.R200_GET_RECKONING);
			log.error("Database exception when getting the approval queue: " + daoE.getMessage());
			log.debug("Stack Trace:", daoE);
		} catch (Exception e) {
			response = new Message(MessageEnum.R01_DEFAULT);
			log.error("General exception when getting the approval queue: " + e.getMessage());
			log.debug("Stack Trace:", e);			
		}
		
		if (response == null) {
			return new ReckoningServiceList(approvalQueue, new Message(), true);
		} else {
			return new ReckoningServiceList(null, response, false);
		}
	}
	
	public ReckoningServiceList queryReckoning(Integer page, Integer size, Boolean approved,
			Boolean rejected, String id, String submitterId, Date postedAfter,
			Date postedBefore, Date closedAfter, Date closedBefore, String tag,
			String sortBy, String filter, String userToken) {
		
		return new ReckoningServiceList();
	}
}
