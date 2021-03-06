package com.reckonlabs.reckoner.contentservices.cache;

import java.util.Date;
import java.util.List;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

public interface ReckoningCache {

	public void setCachedReckoning(List<Reckoning> reckoning, String id);

	public List<Reckoning> getCachedReckoning(String id);

	public void removeCachedReckoning(String id);
	
	public void setCachedReckoningSummaries(List<Reckoning> reckonings,
			Integer page, Integer size, Date postedAfter, Date postedBefore,
			Date closedAfter, Date closedBefore);

	public List<Reckoning> getCachedReckoningSummaries(Integer page,
			Integer size, Date postedAfter, Date postedBefore,
			Date closedAfter, Date closedBefore);

	public void removeCachedReckoningSummaries(Integer page,
			Integer size, Date postedAfter, Date postedBefore,
			Date closedAfter, Date closedBefore);
	
	public void setCachedUserReckoningSummaries(String userId, List<Reckoning> reckonings);

	public List<Reckoning> getCachedUserReckoningSummaries(String userId);

	public void removeCachedUserReckoningSummaries(String userId);
	
	public void setCachedUserCommentedReckonings(List<Reckoning> reckonings, 
			String userId);

	public List<Reckoning> getCachedUserCommentedReckonings(String userId);

	public void removeCachedUserCommentedReckonings(String userId);
	
	public void setCachedUserVotedReckonings (List<Reckoning> reckonings, String userId);

	public List<Reckoning> getCachedUserVotedReckonings(String userId);

	public void removeCachedUserVotedReckonings(String userId);
	
	public void setCachedUserFavoritedReckonings (List<Reckoning> reckonings, String userId);

	public List<Reckoning> getCachedUserFavoritedReckonings(String userId);

	public void removeCachedUserFavoritedReckonings(String userId);	
	
	public void setCachedUserFavoritedReckoningComments (List<Reckoning> commentedReckonings, String userId);

	public List<Reckoning> getCachedUserFavoritedReckoningComments(String userId);

	public void removeCachedUserFavoritedReckoningComments(String userId);	
}
