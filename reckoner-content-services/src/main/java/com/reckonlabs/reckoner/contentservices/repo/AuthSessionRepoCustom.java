package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;

import com.reckonlabs.reckoner.domain.user.AuthSession;

public interface AuthSessionRepoCustom {
	
	public void insertNewAuthSession (AuthSession authSession);

	public void updateAuthSession (AuthSession authSession);
	
	public void removeAuthSession (AuthSession authSession);
}
