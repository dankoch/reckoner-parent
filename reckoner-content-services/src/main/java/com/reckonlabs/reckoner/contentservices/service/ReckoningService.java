package com.reckonlabs.reckoner.contentservices.service;

import java.util.Date;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;

public interface ReckoningService {
	
	public ServiceResponse postReckoning (Reckoning reckoning, String userToken);

	public ReckoningServiceList queryReckoning(Long page, Long size, boolean approved,
			boolean rejected, String id, String submitterId, Date postedAfter,
			Date postedBefore, Date closedAfter, Date closedBefore, String tag,
			String sortBy, String filter, String userToken);
}
