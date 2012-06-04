package com.reckonlabs.reckoner.contentservices.cache;

import java.util.List;

import com.reckonlabs.reckoner.domain.reckoning.Vote;

public class VoteCacheNullImpl implements VoteCache {

	@Override
	public void setCachedUserReckoningVote(List<Vote> vote, String userId,
			String reckoningId) {

	}

	@Override
	public List<Vote> getCachedUserReckoningVote(String userId,
			String reckoningId) {

		return null;
	}

	@Override
	public void removeUserReckoningVote(String userId, String reckoningId) {

	}

}
