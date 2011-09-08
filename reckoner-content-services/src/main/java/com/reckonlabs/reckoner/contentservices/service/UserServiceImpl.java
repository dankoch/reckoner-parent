package com.reckonlabs.reckoner.contentservices.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.contentservices.client.AuthClient;

import com.reckonlabs.reckoner.contentservices.repo.AuthSessionRepo;
import com.reckonlabs.reckoner.contentservices.repo.AuthSessionRepoCustom;
import com.reckonlabs.reckoner.contentservices.repo.UserRepo;
import com.reckonlabs.reckoner.contentservices.repo.UserRepoCustom;
import com.reckonlabs.reckoner.domain.message.UserServiceResponse;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.user.AuthSession;
import com.reckonlabs.reckoner.domain.user.GroupEnum;
import com.reckonlabs.reckoner.domain.user.ProviderEnum;
import com.reckonlabs.reckoner.domain.user.User;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;
import com.reckonlabs.reckoner.domain.utility.DateUtility;
import com.reckonlabs.reckoner.domain.utility.ListPagingUtility;

@Component
public class UserServiceImpl implements UserService {

	@Autowired
	AuthSessionRepo authSessionRepo;
	
	@Autowired
	AuthSessionRepoCustom authSessionRepoCustom;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	UserRepoCustom userRepoCustom;
	
	@Resource
	AuthClient facebookAuthClient;

	@Resource
	AuthClient googleAuthClient;
	
	private static final Logger log = LoggerFactory
			.getLogger(UserServiceImpl.class);

	@Override
	public UserServiceResponse authenticateOAuthUser(
			String userToken, ProviderEnum provider, String expires) {
		
		User authUser = null;
		User reckonerUser = null;
		AuthSession authSession = null;
		boolean newUser = false;
		
		try {
			// Query the OAuth provider with the userToken to extract the user information.
			if (provider == ProviderEnum.FACEBOOK) {
				authUser = facebookAuthClient.getAuthenticatedUser(userToken);
			} else if (provider == ProviderEnum.GOOGLE) {
				authUser = googleAuthClient.getAuthenticatedUser(userToken);			
			}
			
			// 1) If no information was returned, something went wrong - chuck back an error.
			// 2) Otherwise, check the DB to see if this user already exists.
			//   a) If not, write the user to the DB and retrieve it to get its ID.
			//   b) If so, write an updated login date to the DB and retrieve it.
			//   c) Finally, write a session mapping the userToken to the user in the AuthSession bank.
			
			if (authUser == null) {
				return new UserServiceResponse(null, null, new Message(MessageEnum.R702_AUTH_USER), false);
			} else {
				List<User> existingUser = userRepo.findByAuthProviderAndAuthProviderId
						(provider.getProvider(), authUser.getAuthProviderId());
				
				if (existingUser.isEmpty()) {
					newUser = true;
					authUser.setFirstLogin(DateUtility.now());
					authUser.setLastLogin(DateUtility.now());
					authUser.addGroup(GroupEnum.USER);
					authUser.setActive(true);
					userRepoCustom.insertNewUser(authUser);
					
					existingUser = userRepo.findByAuthProviderAndAuthProviderId
							(provider.getProvider(), authUser.getAuthProviderId());
				} else {
					existingUser.get(0).setLastLogin(DateUtility.now());
					userRepoCustom.updateUser(existingUser.get(0));
				}
				
				reckonerUser = existingUser.get(0);
				authSession = new AuthSession(userToken, reckonerUser, expires);
				authSessionRepoCustom.insertNewAuthSession(authSession);
			}
		} catch (Exception e) {
			log.error("General exception when authenticating a user: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new UserServiceResponse(null, null, new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		if (newUser) {
			return new UserServiceResponse(reckonerUser, 
				authSession, new Message(MessageEnum.R703_AUTH_USER), true);
		} else {
			return new UserServiceResponse(reckonerUser, 
					authSession, new Message(), true);			
		}
	}

	@Override
	public UserServiceResponse logoutUser(String userToken) {
		try {
			List<AuthSession> authSessions = authSessionRepo.findByUserToken(userToken);
			for (AuthSession authSession : authSessions) {
				authSessionRepoCustom.removeAuthSession(authSession);
			}
		} catch (Exception e) {
			log.error("General exception when logging out a user: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new UserServiceResponse(null, null, new Message(MessageEnum.R01_DEFAULT), false));	
		}		
		
		return new UserServiceResponse(null, null, new Message(), true);	
	}

	@Override
	public UserServiceResponse getUserByToken(String userToken) {
		User authUser = null;
		
		try {
			List<AuthSession> authSessions = authSessionRepo.findByUserToken(userToken);
			if (!authSessions.isEmpty()) {
				String userId = authSessions.get(0).getReckonerUserId();
				List<User> authUsers = userRepo.findById(userId);
				if (!authUsers.isEmpty()) {
					authUser = authUsers.get(0);
				}
			}
		} catch (Exception e) {
			log.error("General exception when fetching user from the user token: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new UserServiceResponse(null, null, new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		if (authUser == null) {
			return (new UserServiceResponse(null, null, new Message(MessageEnum.R704_AUTH_USER), false));				
		}
		
		return (new UserServiceResponse(authUser, null, new Message(), true));		
	}
}
