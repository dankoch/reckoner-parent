package com.reckonlabs.reckoner.contentservices.client;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;

import com.reckonlabs.reckoner.domain.user.ProviderEnum;
import com.reckonlabs.reckoner.domain.user.User;

public class FacebookAuthClient implements AuthClient {

	public static final String GRAPH_API_URL = "http://graph.facebook.com/";
	
	@Override
	public User getAuthenticatedUser(String userToken) {
		FacebookClient fbClient = new DefaultFacebookClient(userToken);
		com.restfb.types.User authUser = fbClient.fetchObject("me", com.restfb.types.User.class);
		
		User reckonerUser = null;
		
		if (authUser != null) {
			reckonerUser = new User();
			reckonerUser.setAuthProvider(ProviderEnum.FACEBOOK);
			reckonerUser.setAuthProviderId(authUser.getId());
			reckonerUser.setEmail(authUser.getEmail());
			reckonerUser.setFirstName(authUser.getFirstName());
			reckonerUser.setLastName(authUser.getLastName());
			reckonerUser.setUsername(authUser.getUsername());
			
			reckonerUser.setProfileUrl(authUser.getLink());
			reckonerUser.setProfilePictureUrl(GRAPH_API_URL + authUser.getId() + "/picture");
		}
		
		return reckonerUser;
	}

}
