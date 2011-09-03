package com.reckonlabs.reckoner.contentservices.client;

import com.reckonlabs.reckoner.domain.user.ProviderEnum;
import com.reckonlabs.reckoner.domain.user.User;

public class GoogleAuthClient implements AuthClient {
	
	@Override
	public User getAuthenticatedUser(String userToken) {
		return null;
	}

}
