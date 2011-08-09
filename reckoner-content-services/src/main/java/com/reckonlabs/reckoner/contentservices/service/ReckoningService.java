package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;

public interface ReckoningService {
	
	public ServiceResponse postReckoning (Reckoning reckoning, String userToken);
	
	public ReckoningServiceList getReckoning (String id, String userToken);
	
	public ReckoningServiceList getApprovalQueue (Integer page, Integer size, Boolean latestFirst, String userToken);
	
	public ReckoningServiceList getReckoningsByUser (String submitterId, Integer page, Integer size, String userToken);
	
	public ReckoningServiceList getReckoningSummariesByUser (String submitterId, String userToken);
	
	public ReckoningServiceList getOpenReckoningsByUser (String submitterId, Integer page, Integer size, String userToken);
	
	public ReckoningServiceList getClosedReckoningsByUser (String submitterId, Integer page, Integer size, String userToken);
	
	public ReckoningServiceList getApprovalQueueByUser (String submitterId, String userToken);
	
	public ReckoningServiceList getHighlightedReckonings (Boolean open, String userToken);
	
	public ReckoningServiceList getReckoningSummaries (Integer page, Integer size, Date postedAfter, Date postedBefore,
			Date closedAfter, Date closedBefore, String userToken);

	public ReckoningServiceList getReckoningsByTag (String tag, Integer page, Integer size, String userToken);
}
