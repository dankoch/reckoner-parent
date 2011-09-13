package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;

import com.reckonlabs.reckoner.domain.message.UserServiceResponse;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.user.ProviderEnum;

public interface UserService {
	
	public UserServiceResponse authenticateOAuthUser(String sessionId, ProviderEnum provider, 
			String expires, String refreshToken);
	
	public UserServiceResponse logoutUser(String sessionId);
	
	public UserServiceResponse getUserBySessionId(String sessionId);
}
