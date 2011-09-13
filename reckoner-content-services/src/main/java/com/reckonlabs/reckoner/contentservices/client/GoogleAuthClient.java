package com.reckonlabs.reckoner.contentservices.client;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.reckonlabs.reckoner.contentservices.service.UserServiceImpl;
import com.reckonlabs.reckoner.domain.client.google.GoogleTokenResponse;
import com.reckonlabs.reckoner.domain.client.google.profile.ProfileParent;
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
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("alt", "json");
			map.put("oauth_token", userToken);
			
			ProfileParent googleProfile = restTemplate.getForObject
					(userSelfProfileUrl, ProfileParent.class, map);
			
			if (googleProfile != null) {
				reckonerUser = ClientConversionFactory.createReckonerUserFromGoogle(googleProfile.getData());
			}
		} catch (HttpClientErrorException e) {
			// Buzz API queries return a 404 when you try to pull information from a Google account
			// that doesn't have a Profile.  In this case, return a User ID with a sentinel for the Service to handle.
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
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
