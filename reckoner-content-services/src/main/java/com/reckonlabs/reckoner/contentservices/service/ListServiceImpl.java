package com.reckonlabs.reckoner.contentservices.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.domain.message.DataServiceList;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.message.UserServiceResponse;
import com.reckonlabs.reckoner.domain.user.GroupEnum;
import com.reckonlabs.reckoner.domain.user.PermissionEnum;
import com.reckonlabs.reckoner.domain.user.ProviderEnum;
import com.reckonlabs.reckoner.domain.user.User;

@Component
public class ListServiceImpl implements ListService {

	private static final Logger log = LoggerFactory
			.getLogger(ListServiceImpl.class);
	
	@Override
	public DataServiceList<String> getValidGroups() {
		List<String> groups = null;
		
		try {
			groups = GroupEnum.getGroups();
		} catch (Exception e) {
			log.error("General exception when fetching group list ", e);			
			return (new DataServiceList<String>(null, new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return (new DataServiceList<String>(groups, new Message(), true));
	}

	@Override
	public DataServiceList<String> getValidPermissions() {
		List<String> permissions = null;
		
		try {
			permissions = PermissionEnum.getPermissions();
		} catch (Exception e) {
			log.error("General exception when fetching permission list ", e);			
			return (new DataServiceList<String>(null, new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return (new DataServiceList<String>(permissions, new Message(), true));
	}
	
	@Override
	public DataServiceList<String> getValidProviders() {
		List<String> providers = null;
		
		try {
			providers = ProviderEnum.getProviders();
		} catch (Exception e) {
			log.error("General exception when fetching provider list ", e);			
			return (new DataServiceList<String>(null, new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return (new DataServiceList<String>(providers, new Message(), true));
	}
}
