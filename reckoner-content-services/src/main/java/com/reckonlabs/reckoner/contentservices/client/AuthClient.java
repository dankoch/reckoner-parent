package com.reckonlabs.reckoner.contentservices.client;

import com.reckonlabs.reckoner.domain.user.User;

public interface AuthClient {
	
	public User getAuthenticatedUser(String userToken);
}
