package com.reckonlabs.reckoner.contentservices.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;

import com.reckonlabs.reckoner.contentservices.service.UserServiceImpl;
import com.reckonlabs.reckoner.domain.user.AuthSession;
import com.reckonlabs.reckoner.domain.user.ProviderEnum;
import com.reckonlabs.reckoner.domain.user.User;

public class FacebookAuthClient implements AuthClient {

	private static final Logger log = LoggerFactory
			.getLogger(FacebookAuthClient.class);
	
	private String graphApiUrl;
	
	@Override
	public User getAuthenticatedUser(String userToken) {
		User reckonerUser = null;
		
		try {
			FacebookClient fbClient = new DefaultFacebookClient(userToken);
			com.restfb.types.User authUser = fbClient.fetchObject("me", com.restfb.types.User.class);
			
			if (authUser != null) {
				reckonerUser = ClientConversionFactory.createReckonerUserFromFacebook
						(authUser, graphApiUrl);
			}
		} catch (Exception e) {
			log.warn("Received error when pulling user information from Facebook: ", e);
			return null;
		}
		
		return reckonerUser;
	}

	@Override
	public AuthSession refreshUserToken(AuthSession refreshToken) {
		// Facebook doesn't currently allow for tokens to be refreshed.
		
		return null;
	}

	public String getGraphApiUrl() {
		return graphApiUrl;
	}

	public void setGraphApiUrl(String graphApiUrl) {
		this.graphApiUrl = graphApiUrl;
	}

}
