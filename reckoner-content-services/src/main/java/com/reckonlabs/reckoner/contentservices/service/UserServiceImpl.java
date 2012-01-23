package com.reckonlabs.reckoner.contentservices.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.contentservices.client.AuthClient;
import com.reckonlabs.reckoner.contentservices.client.GoogleAuthClient;

import com.reckonlabs.reckoner.contentservices.repo.AuthSessionRepo;
import com.reckonlabs.reckoner.contentservices.repo.AuthSessionRepoCustom;
import com.reckonlabs.reckoner.contentservices.repo.UserRepo;
import com.reckonlabs.reckoner.contentservices.repo.UserRepoCustom;
import com.reckonlabs.reckoner.contentservices.utility.ServiceProps;
import com.reckonlabs.reckoner.domain.message.PostActionEnum;
import com.reckonlabs.reckoner.domain.message.UserServiceResponse;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.user.AuthSession;
import com.reckonlabs.reckoner.domain.user.GroupEnum;
import com.reckonlabs.reckoner.domain.user.PermissionEnum;
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
	
	@Resource
	ServiceProps serviceProps;
	
	private static final Logger log = LoggerFactory
			.getLogger(UserServiceImpl.class);

	@Override
	public UserServiceResponse authenticateOAuthUser(String userToken, ProviderEnum provider, 
			String expires, String refreshToken) {
		
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
			// 2) If a user was returned but has a Sentinel ID, send the corresponding response.
			// 3) Otherwise, check the DB to see if this user already exists.
			//   a) If not, write the user to the DB and retrieve it to get its ID.
			//   b) If so, write an updated login date to the DB and retrieve it.
			//   c) Finally, write a session mapping the userToken to the user in the AuthSession bank.
			
			if (authUser == null) {
				return new UserServiceResponse(null, null, new Message(MessageEnum.R702_AUTH_USER), false);
			} else {
				if (authUser.getAuthProviderId() != null) {
					if (authUser.getAuthProviderId().equalsIgnoreCase(GoogleAuthClient.NON_GPLUS_ACCOUNT_SENTINEL)) {
						return new UserServiceResponse(null, null, new Message(MessageEnum.R707_AUTH_USER), false);
					}
				}
				
				List<User> existingUser = userRepo.findByAuthProviderAndAuthProviderId
						(provider.getProvider(), authUser.getAuthProviderId());
				
				if (existingUser.isEmpty()) {
					newUser = true;
					authUser.setFirstLogin(DateUtility.now());
					authUser.setLastLogin(DateUtility.now());
					authUser.addGroup(GroupEnum.USER);
					authUser.setActive(true);
					authUser.setBio(serviceProps.getDefaultBio());
					userRepoCustom.insertNewUser(authUser);
					
					existingUser = userRepo.findByAuthProviderAndAuthProviderId
							(provider.getProvider(), authUser.getAuthProviderId());
				} else {
					existingUser.set(0, mergeOAuthUser(existingUser.get(0), authUser));
					existingUser.get(0).setLastLogin(DateUtility.now());
					
					userRepoCustom.updateUser(existingUser.get(0));
				}
				
				reckonerUser = existingUser.get(0);
				authSession = new AuthSession(userToken, reckonerUser, expires, refreshToken);
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
	public UserServiceResponse logoutUser(String sessionId) {
		try {
			List<AuthSession> authSessions = authSessionRepo.findById(sessionId);
			for (AuthSession authSession : authSessions) {
				authSessionRepoCustom.removeAuthSession(authSession);
			}
		} catch (Exception e) {
			log.error("General exception when logging out a user: " + e.getMessage(), e);			
			return (new UserServiceResponse(null, null, new Message(MessageEnum.R01_DEFAULT), false));	
		}		
		
		return new UserServiceResponse(null, null, new Message(), true);	
	}
	
	@Override
	public UserServiceResponse getUserSummaries(Boolean active, String sortBy,
			Boolean ascending, Integer page, Integer size) {
		List<User> userSummaries = new LinkedList<User>();
		
		try {
			userSummaries = userRepoCustom.getUserSummaries(active, sortBy, ascending, page, size);
		} catch (Exception e) {
			log.error("General exception when fetching user summaries: " + 
					": " + e.getMessage(), e);			
			return (new UserServiceResponse(null, null, new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return (new UserServiceResponse(userSummaries, new Message(), true));	
	}

	@Override
	public UserServiceResponse getUserBySessionId(String sessionId) {
		User authUser = null;
		AuthSession currentSession = null;
		
		try {
			// Check to see if a session pairs up with the passed Session ID.
			// If not, return an A-OK message with null message and authSession.
			List<AuthSession> authSessions = authSessionRepo.findById(sessionId);
			if (!authSessions.isEmpty()) {
				currentSession = authSessions.get(0);
				
				// Check to see if the session is expired.  
				//  * If so, attempt to refresh it, write the new session to the DB, and proceed using this new session.
				//    Leave the old session in the DB in case we're doing something for the user on their behalf.
				//    This will give them a chance to stay logged in.
				//
				//  * If we can't refresh it (error or no refresh_token), delete the old one and return a null message.
				if (currentSession.isExpired()) {
					AuthSession newSession = refreshAuthSession(currentSession);
					if (newSession != null) {
						authSessionRepoCustom.insertNewAuthSession(newSession);
						currentSession = newSession;
					} else {
						authSessionRepoCustom.removeAuthSession(currentSession);
						return (new UserServiceResponse(null, null, new Message(MessageEnum.R706_AUTH_USER), false));	
					}
				}
				
				String userId = currentSession.getReckonerUserId();
				List<User> authUsers = userRepo.findById(userId);
				if (!authUsers.isEmpty()) {
					authUser = authUsers.get(0);
				}
			}
		} catch (Exception e) {
			log.error("General exception when fetching user from the session ID " + sessionId + 
					": " + e.getMessage(), e);			
			return (new UserServiceResponse(null, null, new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		if (authUser == null) {
			return (new UserServiceResponse(null, null, new Message(MessageEnum.R704_AUTH_USER), false));				
		}
		
		return (new UserServiceResponse(authUser, currentSession, new Message(), true));		
	}
	
	@Override
	public UserServiceResponse getUserByUserId(String userId) {
		return getUserByUserId(userId, false);
	}
	
	@Override
	public UserServiceResponse getUserByUserId(String userId, boolean summary) {
		User authUser = null;
		List<User> authUsers = null;
		
		try {
			if (summary) {
				authUsers = userRepo.findByIdSummary(userId);
			} else {
				authUsers = userRepo.findById(userId);
			}
			if (!authUsers.isEmpty()) {
				authUser = authUsers.get(0);
			}
		} catch (Exception e) {
			log.error("General exception when fetching user from the user ID " + userId + 
					": " + e.getMessage(), e);			
			return (new UserServiceResponse(null, null, new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		if (authUser == null) {
			return (new UserServiceResponse(null, null, new Message(MessageEnum.R704_AUTH_USER), false));				
		}
		return (new UserServiceResponse(authUser, null, new Message(), true));		
	}
	
	private AuthSession refreshAuthSession(AuthSession oldSession) {
		
		if (oldSession.getRefreshToken() != null) {
			if (oldSession.getAuthProvider() == ProviderEnum.GOOGLE) {
				return googleAuthClient.refreshUserToken(oldSession);
			}
		}

		return null;
	}
	
	@Override
	public UserServiceResponse updateUserPermissions(PostActionEnum action,
			Set<GroupEnum> groups, Boolean active, String userId) {
		
		try {
			List<User> userInfo = userRepo.findById(userId);
			if (!userInfo.isEmpty()) {
				User changeUser = userInfo.get(0);
				
				if (changeUser.getGroups() != null) {
					if (action == PostActionEnum.ADD) {
						changeUser.getGroups().addAll(groups);
					} else if (action == PostActionEnum.REMOVE) {
						changeUser.getGroups().removeAll(groups);		
					} else if (action == PostActionEnum.REPLACE) {
						changeUser.getGroups().clear();
						changeUser.getGroups().addAll(groups);
					}
				}
				
				if (active != null) {
					changeUser.setActive(active.booleanValue());
				}
				
				userRepoCustom.updateUser(changeUser);
			} else {
				return (new UserServiceResponse(null, null, new Message(MessageEnum.R710_AUTH_USER), false));					
			}
		} catch (Exception e) {
			log.error("General exception when changing user permissions: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new UserServiceResponse(null, null, new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new UserServiceResponse(null, null, new Message(), true);
	}
	
	@Override
	public UserServiceResponse updateUserInformation(User user) {
		
		try {
			List<User> currentUser = userRepo.findById(user.getId());
			if (!currentUser.isEmpty()) {
				User changeUser = mergeUpdatedUser(currentUser.get(0), user);
				if (user.getUsername() != null) {
					List<User> existingUsernameUser = userRepo.findByUsername(user.getUsername());
					if (existingUsernameUser != null && !existingUsernameUser.isEmpty()) {
						return (new UserServiceResponse(null, null, new Message(MessageEnum.R715_AUTH_USER), false));						
					}
				}
				
				userRepoCustom.updateUser(changeUser);
			} else {
				return (new UserServiceResponse(null, null, new Message(MessageEnum.R710_AUTH_USER), false));					
			}
		} catch (Exception e) {
			log.error("General exception when updating a user: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new UserServiceResponse(null, null, new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new UserServiceResponse(null, null, new Message(), true);
	}

	@Override
	public boolean hasPermission(String sessionId, PermissionEnum perm) {
		if (sessionId != null) {
			UserServiceResponse userResponse = getUserBySessionId(sessionId);
			
			if (userResponse.getUser() != null) {
				return userResponse.getUser().hasPermission(perm);
			}
		}
		
		return GroupEnum.getPermissions(GroupEnum.ANONYMOUS).contains(perm);
	}
	
	// This method is responsible for controlling how an existing user gets updated
	// when a user re-authenticates.
	private static User mergeOAuthUser(User existingUser, User newUser) {
		existingUser.setFirstName(newUser.getFirstName());
		existingUser.setLastName(newUser.getLastName());
		existingUser.setEmail(newUser.getEmail());
		existingUser.setProfilePictureUrl(newUser.getProfilePictureUrl());
		existingUser.setProfileUrl(newUser.getProfileUrl());
		
		return existingUser;
	}
	
	// This method is responsible for controlling how an existing user gets updated
	// when an update call is made.  This only updates information that isn't bound to an OAuth account.
	// SEE mergeOAuthUser for OAuth bound fields.
	private static User mergeUpdatedUser(User existingUser, User newUser) {
		if (newUser.getBio() != null) 
			{existingUser.setBio(newUser.getBio());}
		if (newUser.isHideProfile() != null) 
			{existingUser.setHideProfile(newUser.isHideProfile());}
		if (newUser.isHideVotes() != null) 		
			{existingUser.setHideVotes(newUser.isHideVotes());}
		if (newUser.isUseUsername() != null) 
			{existingUser.setUseUsername(newUser.isUseUsername());}
		if (newUser.getUsername() != null) 
			{existingUser.setUsername(newUser.getUsername());}
		
		return existingUser;
	}
}
