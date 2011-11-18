package com.reckonlabs.reckoner.contentservices.repo;

import java.util.List;

import com.reckonlabs.reckoner.domain.user.User;

public interface UserRepoCustom {
	
	public void insertNewUser (User user);

	public void updateUser (User user);
	
	public List<User> getUserSummaries(Boolean active, 
			String sortBy, Boolean ascending, 
			Integer page, Integer size);
}
