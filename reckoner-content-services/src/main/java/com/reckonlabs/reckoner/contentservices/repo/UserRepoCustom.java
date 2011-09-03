package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;

import com.reckonlabs.reckoner.domain.user.User;

public interface UserRepoCustom {
	
	public void insertNewUser (User user);

	public void updateUser (User user);
}
