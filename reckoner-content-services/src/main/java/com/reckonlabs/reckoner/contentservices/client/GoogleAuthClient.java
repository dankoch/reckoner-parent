package com.reckonlabs.reckoner.contentservices.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.plus.model.Person;
import com.google.api.services.plus.Plus;

import com.reckonlabs.reckoner.domain.client.google.GoogleTokenResponse;
import com.reckonlabs.reckoner.domain.user.AuthSession;
import com.reckonlabs.reckoner.domain.user.ProviderEnum;
import com.reckonlabs.reckoner.domain.user.User;
import com.reckonlabs.reckoner.domain.utility.DateUtility;

public class GoogleAuthClient implements AuthClient {
	
	private String userTokenUrl;
	private String userSelfProfileUrl;
	private String clientId;
	private String clientSecret;

	@Autowired
	private RestTemplate restTemplate;
	
	private static final Logger log = LoggerFactory
			.getLogger(GoogleAuthClient.class);
	
	public static final String NON_GPLUS_ACCOUNT_SENTINEL="NON_G+";
	
	@Override
	public User getAuthenticatedUser(String userToken) {
		User reckonerUser = null;
		
		try {
		    GoogleAccessProtectedResource requestInitializer =
		            new GoogleAccessProtectedResource(
		                userToken,
		                new NetHttpTransport(),
		                new GsonFactory(),
		                clientId,
		                clientSecret,
		                null);
		    Plus plus = new Plus(new NetHttpTransport(), requestInitializer, new GsonFactory());
		    
		    plus.setOauthToken(userToken);
		    Person googleProfile = plus.people.get("me").execute();
			
			if (googleProfile != null) {
				reckonerUser = ClientConversionFactory.createReckonerUserFromGoogle(googleProfile);
			}
		} catch (HttpResponseException e) {
			// Buzz API queries return a 404 when you try to pull information from a Google account
			// that doesn't have a Profile.  In this case, return a User ID with a sentinel for the Service to handle.

			if (e.response.statusCode == 404) {
				log.info("Got 404 when pulling a user's profile information from Google.  Probably a non G+ account.",
						e);
				
				reckonerUser = new User();
				reckonerUser.setAuthProviderId(NON_GPLUS_ACCOUNT_SENTINEL);
				reckonerUser.setActive(false);
				return reckonerUser;
			}
			
			log.warn("Received error when pulling user information from Google: ", e);
			return null;
		}
		catch (Exception e) {
			log.warn("Received error when pulling user information from Google: ", e);
			return null;
		}
		
		return reckonerUser;
	}

	@Override
	public AuthSession refreshUserToken (AuthSession oldSession) {
		AuthSession newSession = null;
		
		try {
			// This is should eventually be ported to use the Google Java API.  In the meantime, until
			// this needs to be updated, we're making a manual REST call.
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("client_id", clientId);
			map.add("client_secret", clientSecret);
			map.add("refresh_token", oldSession.getRefreshToken());
			map.add("grant_type", "refresh_token");
			
			GoogleTokenResponse response = restTemplate.postForObject
					(userTokenUrl, map, GoogleTokenResponse.class);
			
			if (response != null) {
				newSession = new AuthSession();
				newSession.setUserToken(response.getAccessToken());
				newSession.setReckonerUserId(oldSession.getReckonerUserId());
				newSession.setCreatedDate(DateUtility.now());
				newSession.setExpirationDate(response.getExpiresIn());
				newSession.setAuthProvider(ProviderEnum.GOOGLE);
				newSession.setRefreshToken(oldSession.getRefreshToken());
			}
			
			return newSession;
			
		} catch (RestClientException e) {
			log.error("Error when refreshing token against Google: ", e);
		}
			
		return null;
	}

	public String getUserTokenUrl() {
		return userTokenUrl;
	}

	public void setUserTokenUrl(String userTokenUrl) {
		this.userTokenUrl = userTokenUrl;
	}

	public String getUserSelfProfileUrl() {
		return userSelfProfileUrl;
	}

	public void setUserSelfProfileUrl(String userSelfProfileUrl) {
		this.userSelfProfileUrl = userSelfProfileUrl;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	
	
}
