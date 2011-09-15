package com.reckonlabs.reckoner.contentservices.service;

import com.reckonlabs.reckoner.domain.message.DataServiceList;

public interface ListService {
	
	public DataServiceList getValidGroups();
	
	public DataServiceList getValidPermissions();
	
	public DataServiceList getValidProviders();
}
