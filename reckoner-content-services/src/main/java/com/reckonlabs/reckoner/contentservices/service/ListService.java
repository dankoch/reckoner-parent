package com.reckonlabs.reckoner.contentservices.service;

import com.reckonlabs.reckoner.domain.message.DataServiceList;

public interface ListService {
	
	public DataServiceList<String> getValidGroups();
	
	public DataServiceList<String> getValidPermissions();
	
	public DataServiceList<String> getValidProviders();
	
	public DataServiceList<String> getContentTypes();
	
	public DataServiceList<String> getMediaTypes();
}
