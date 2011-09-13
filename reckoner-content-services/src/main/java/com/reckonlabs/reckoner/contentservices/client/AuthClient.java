package com.reckonlabs.reckoner.contentservices.client;

import com.reckonlabs.reckoner.domain.user.AuthSession;
import com.reckonlabs.reckoner.domain.user.User;

public interface AuthClient {
	
	public User getAuthenticatedUser(String userToken);
	
	public AuthSession refreshUserToken(AuthSession refreshToken);
}
