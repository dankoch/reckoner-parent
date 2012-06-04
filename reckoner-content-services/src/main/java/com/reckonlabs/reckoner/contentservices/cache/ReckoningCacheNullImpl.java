package com.reckonlabs.reckoner.contentservices.cache;

import java.util.Date;
import java.util.List;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

public class ReckoningCacheNullImpl implements ReckoningCache {

	@Override
	public void setCachedReckoning(List<Reckoning> reckoning, String id) {

	}

	@Override
	public List<Reckoning> getCachedReckoning(String id) {
		return null;
	}

	@Override
	public void removeCachedReckoning(String id) {

	}

	@Override
	public void setCachedReckoningSummaries(List<Reckoning> reckonings,
			Integer page, Integer size, Date postedAfter, Date postedBefore,
			Date closedAfter, Date closedBefore) {
	}

	@Override
	public List<Reckoning> getCachedReckoningSummaries(Integer page,
			Integer size, Date postedAfter, Date postedBefore,
			Date closedAfter, Date closedBefore) {

		return null;
	}

	@Override
	public void removeCachedReckoningSummaries(Integer page, Integer size,
			Date postedAfter, Date postedBefore, Date closedAfter,
			Date closedBefore) {

	}

	@Override
	public void setCachedUserReckoningSummaries(String userId,
			List<Reckoning> reckonings) {
	}

	@Override
	public List<Reckoning> getCachedUserReckoningSummaries(String userId) {

		return null;
	}

	@Override
	public void removeCachedUserReckoningSummaries(String userId) {

	}

	@Override
	public void setCachedUserCommentedReckonings(List<Reckoning> reckonings,
			String userId) {

	}

	@Override
	public List<Reckoning> getCachedUserCommentedReckonings(String userId) {

		return null;
	}

	@Override
	public void removeCachedUserCommentedReckonings(String userId) {

	}

	@Override
	public void setCachedUserVotedReckonings(List<Reckoning> reckonings,
			String userId) {

	}

	@Override
	public List<Reckoning> getCachedUserVotedReckonings(String userId) {

		return null;
	}

	@Override
	public void removeCachedUserVotedReckonings(String userId) {

	}

	@Override
	public void setCachedUserFavoritedReckonings(List<Reckoning> reckonings,
			String userId) {

	}

	@Override
	public List<Reckoning> getCachedUserFavoritedReckonings(String userId) {

		return null;
	}

	@Override
	public void removeCachedUserFavoritedReckonings(String userId) {

	}

	@Override
	public void setCachedUserFavoritedReckoningComments(
			List<Reckoning> commentedReckonings, String userId) {

	}

	@Override
	public List<Reckoning> getCachedUserFavoritedReckoningComments(String userId) {

		return null;
	}

	@Override
	public void removeCachedUserFavoritedReckoningComments(String userId) {

	}

}
