package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;

public interface ReckoningService {
	
	public ServiceResponse postReckoning (Reckoning reckoning, String userToken);
	
	public ReckoningServiceList getApprovalQueue (Integer page, Integer size, Boolean latestFirst, String userToken);

	public ReckoningServiceList queryReckoning(Integer page, Integer size, Boolean approved,
			Boolean rejected, String id, String submitterId, Date postedAfter,
			Date postedBefore, Date closedAfter, Date closedBefore, String tag,
			String sortBy, String filter, String userToken);
}
