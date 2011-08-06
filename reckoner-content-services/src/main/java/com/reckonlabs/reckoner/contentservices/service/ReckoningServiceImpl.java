package com.reckonlabs.reckoner.contentservices.service;

import java.util.Date;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepo;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepoCustom;
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
		reckoningRepoCustomImpl.insertNewReckoning(reckoning);
		
		return new ServiceResponse();
	}
	
	public ReckoningServiceList queryReckoning(Long page, Long size, boolean approved,
			boolean rejected, String id, String submitterId, Date postedAfter,
			Date postedBefore, Date closedAfter, Date closedBefore, String tag,
			String sortBy, String filter, String userToken) {
		
		return new ReckoningServiceList();
	}
}
