package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;

import com.reckonlabs.reckoner.domain.message.UserServiceResponse;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.user.ProviderEnum;

public interface UserService {
	
	public UserServiceResponse authenticateOAuthUser(String userToken, ProviderEnum provider, String expires);
	
	public ServiceResponse logoutUser(String userToken);
	
	public UserServiceResponse getUserByToken(String userToken);
}
