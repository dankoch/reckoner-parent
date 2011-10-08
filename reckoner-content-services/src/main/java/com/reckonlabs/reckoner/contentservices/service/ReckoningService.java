package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.reckoning.ReckoningTypeEnum;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;

public interface ReckoningService {
	
	public ServiceResponse postReckoning (Reckoning reckoning, String sessionId);
	
	public ServiceResponse updateReckoning (Reckoning reckoning, boolean overwrite, String sessionId);
	
	public ServiceResponse approveReckoning (String id, String sessionId);
	
	public ServiceResponse rejectReckoning (String id, String sessionId);
	
	public ReckoningServiceList getReckoning (String id, String sessionId);
	
	public ReckoningServiceList getReckoning (String id, boolean includeUnaccepted, String sessionId);
	
	public ReckoningServiceList getApprovalQueue (Integer page, Integer size, Boolean latestFirst, String sessionId);
	
	public ReckoningServiceList getReckoningSummariesByUser (String submitterId, Integer page, Integer size, String sessionId);
	
	public ReckoningServiceList getHighlightedReckonings (Boolean open, String sessionId);
	
	public ReckoningServiceList getReckoningSummaries (Integer page, Integer size, Date postedAfter, Date postedBefore,
			Date closedAfter, Date closedBefore, String sessionId);

	public ReckoningServiceList getReckoningSummariesByTag (String tag, Integer page, Integer size, String sessionId);

	public ReckoningServiceList getRandomReckoning(ReckoningTypeEnum reckoningType);
}
