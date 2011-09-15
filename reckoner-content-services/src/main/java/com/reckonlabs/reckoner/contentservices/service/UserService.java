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

public interface UserService {
	
	public UserServiceResponse authenticateOAuthUser(String sessionId, ProviderEnum provider, 
			String expires, String refreshToken);
	
	public UserServiceResponse logoutUser(String sessionId);
	
	public UserServiceResponse getUserBySessionId(String sessionId);
	
	public UserServiceResponse getUserByUserId(String userId);
	
	public UserServiceResponse updateUserPermissions(PostActionEnum action, Set<GroupEnum> groups,
			Boolean active, String userId);
	
	public boolean hasPermission (String sessionId, PermissionEnum perm);
}
