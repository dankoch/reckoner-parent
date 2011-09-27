package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.reckoning.Vote;

public interface VoteRepoCustom {
	
	public void insertVote (Vote vote);
}
