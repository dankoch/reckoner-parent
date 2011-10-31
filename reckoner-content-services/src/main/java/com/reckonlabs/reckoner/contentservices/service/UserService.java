package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.reckonlabs.reckoner.domain.message.PostActionEnum;
import com.reckonlabs.reckoner.domain.message.UserServiceResponse;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.user.GroupEnum;
import com.reckonlabs.reckoner.domain.user.PermissionEnum;
import com.reckonlabs.reckoner.domain.user.ProviderEnum;
import com.reckonlabs.reckoner.domain.user.User;

public interface UserService {
	
	public UserServiceResponse authenticateOAuthUser(String sessionId, ProviderEnum provider, 
			String expires, String refreshToken);
	
	public UserServiceResponse logoutUser(String sessionId);
	
	public UserServiceResponse getUserBySessionId(String sessionId);
	
	public UserServiceResponse getUserByUserId(String userId);
	
	public UserServiceResponse getUserByUserId(String userId, boolean summary);
	
	public UserServiceResponse updateUserPermissions(PostActionEnum action, Set<GroupEnum> groups,
			Boolean active, String userId);
	
	public UserServiceResponse updateUserInformation(User user);
	
	public boolean hasPermission (String sessionId, PermissionEnum perm);
}
