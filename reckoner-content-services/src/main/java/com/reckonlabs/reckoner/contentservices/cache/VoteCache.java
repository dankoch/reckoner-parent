package com.reckonlabs.reckoner.contentservices.cache;

import java.util.List;

import com.reckonlabs.reckoner.domain.reckoning.Vote;

public interface VoteCache {

	public void setCachedUserReckoningVote(List<Vote> vote, String userId, String reckoningId);

	public List<Vote> getCachedUserReckoningVote(String userId, String reckoningId);

	public void removeUserReckoningVote(String userId, String reckoningId);
	
}
