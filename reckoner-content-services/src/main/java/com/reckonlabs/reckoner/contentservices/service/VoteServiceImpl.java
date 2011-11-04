package com.reckonlabs.reckoner.contentservices.service;

import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.contentservices.cache.VoteCache;
import com.reckonlabs.reckoner.contentservices.cache.ReckoningCache;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepo;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepoCustom;
import com.reckonlabs.reckoner.contentservices.repo.VoteRepo;
import com.reckonlabs.reckoner.contentservices.repo.VoteRepoCustom;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.VoteServiceList;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.reckoning.Answer;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.reckoning.Vote;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;
import com.reckonlabs.reckoner.domain.utility.DateUtility;
import com.reckonlabs.reckoner.domain.utility.ListPagingUtility;

@Component
public class VoteServiceImpl implements VoteService {
	
	@Autowired
	ReckoningRepo reckoningRepo;
	@Autowired
	ReckoningRepoCustom reckoningRepoCustom;
	
	@Autowired
	VoteRepo voteRepo;
	@Autowired
	VoteRepoCustom voteRepoCustom;
	
	@Autowired
	VoteCache voteCache;
	
	@Autowired
	ReckoningCache reckoningCache;
	
	@Autowired
	UserService userService;
	
	private static final Logger log = LoggerFactory
			.getLogger(VoteServiceImpl.class);

	@Override
	public ServiceResponse postReckoningVote(Vote vote, String reckoningId, Integer answerIndex) {
		
		try {
			// Confirm that the reckoning exists and isn't closed.
			if (!reckoningRepoCustom.confirmReckoningIsVotingEligible(reckoningId, answerIndex)) {
				log.warn("Attempted to post vote to non-existent reckoning: " + reckoningId);
				return (new ServiceResponse(new Message(MessageEnum.R600_POST_VOTE), false));
			}
			
			// Confirm that the user hasn't already voted.  Check the cache first -- if nothing is there, check the DB.
			List<Vote> userReckoningVote = getUserReckoningVote(vote.getVoterId(), reckoningId).getVotes();
			if (userReckoningVote != null) {
				if (!userReckoningVote.isEmpty()) {
					return (new ServiceResponse(new Message(MessageEnum.R601_POST_VOTE), false));
				}
			}
			
			// If the user isn't logged in, let's check the IP/User-Agent combo to see if they're pulling a fast one.
			if (vote.isAnonymous()) {
				List<Vote> previousVote = voteRepo.findByReckoningIdAndIpAndUserAgent(reckoningId, 
						vote.getIp(), vote.getUserAgent());
				
				if (!previousVote.isEmpty()) {
					// Got a recent IP / User Agent match.  Reject the vote.
					return (new ServiceResponse(new Message(MessageEnum.R602_POST_VOTE), false));					
				}
			}
			
			// Update both the Reckoning and the Vote collections with the new vote.  The Reckoning is the authoritative store,
			// and the Vote collection is used for anonymous duplicate vote checking.
			vote.setVotingDate(DateUtility.now());
			vote.setReckoningId(reckoningId);
			reckoningRepoCustom.insertReckoningVote(vote.getVoterId(), answerIndex, reckoningId);
			voteRepoCustom.insertVote(vote);
			
			// Cache management.  Cache the vote to confirm the user has voted for this one.
			List<Vote> voteCacheEntry = new LinkedList<Vote>();
			voteCacheEntry.add(vote);
			voteCache.setCachedUserReckoningVote(voteCacheEntry, vote.getVoterId(), reckoningId);
			
			// Cache management.  Remove the list of reckonings voted by this user from the cache.  We'll recompile on the next request.
			reckoningCache.removeCachedUserVotedReckonings(vote.getVoterId());
			
			// Cache management. Check to see if the reckoning is already in cache.  If so, update it.  Otherwise, forget it.
			// Assume that ONLY THE VOTE TOTALS are in the cache, not the votes themselves.
			List<Reckoning> cacheReckoning = reckoningCache.getCachedReckoning(reckoningId);
			if (cacheReckoning != null) {
				if (cacheReckoning.get(0) != null) {
					if (cacheReckoning.get(0).getAnswers() != null) {
						if (cacheReckoning.get(0).getAnswers().get(answerIndex) != null) {
							cacheReckoning.get(0).getAnswers().get(answerIndex).incrementVoteTotal();							
						}
					}
				}

				reckoningCache.setCachedReckoning(cacheReckoning, cacheReckoning.get(0).getId());
			}
		} catch (Exception e) {
			log.error("General exception when inserting a new reckoning vote: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}

	@Override
	public ReckoningServiceList getUserVotedReckonings(String userId, Integer page, Integer size) {
		List<Reckoning> userVotedReckonings = null;
		long count = 0;
		
		try {
			userVotedReckonings = reckoningCache.getCachedUserVotedReckonings(userId);
			if (userVotedReckonings == null || userVotedReckonings.isEmpty()) {
				userVotedReckonings = reckoningRepoCustom.getUserVotedReckonings(userId);
				for (Reckoning reckoning : userVotedReckonings) {
					List<Vote> vote = reckoning.getVoteByUser(userId);
					
					// Clear out the answer vote lists so they only contain the user's vote.
					if (!vote.isEmpty()) {
						for (Answer answer : reckoning.getAnswers()) {
							if (answer.getIndex() == vote.get(0).getAnswerIndex()) {
								Hashtable<String, String> answerVote = new Hashtable<String, String>();
								answerVote.put(userId, "1");
								answer.setVotes(answerVote);
								answer.setVoteTotal(1);
							} else {
								answer.setVotes(new Hashtable<String, String> ());
								answer.setVoteTotal(0);
							}
						}
					}
					
					reckoning.setPostingUser(userService.getUserByUserId
							(reckoning.getSubmitterId(), true).getUser());
				}
				reckoningCache.setCachedUserVotedReckonings(userVotedReckonings, userId);
			}
			
			count = userVotedReckonings.size();
			userVotedReckonings = (List<Reckoning>) ListPagingUtility.pageList(userVotedReckonings, page, size);
		} catch (Exception e) {
		   log.error("General exception when getting reckonings voted by user: " + e.getMessage());
		   log.debug("Stack Trace:", e);			
		   return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
	    }
		
		return new ReckoningServiceList(userVotedReckonings, count, new Message(), true);		
	}

	@Override
	public VoteServiceList getUserReckoningVote(String userId, String reckoningId) {
		List<Vote> userReckoningVote = null;
		
		try {
			userReckoningVote = voteCache.getCachedUserReckoningVote(userId, reckoningId);
			
			if (userReckoningVote == null) {
				List<Reckoning> votedReckonings = reckoningRepo.getReckoningVotesByReckoningId(reckoningId);
				
				if (votedReckonings != null) {
					if (!votedReckonings.isEmpty()) {
						userReckoningVote = votedReckonings.get(0).getVoteByUser(userId);	
					}
				}

				voteCache.setCachedUserReckoningVote(userReckoningVote, userId, reckoningId);
			}
		} catch (Exception e) {
		   log.error("General exception when getting votes on a reckoning by a user: " + e.getMessage());
		   log.debug("Stack Trace:", e);			
		   return new VoteServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
	    }
		
		// An empty list signifies 'checked and empty'.  Convert to null for a cleaner return value.
		if (userReckoningVote != null) {
			if (userReckoningVote.isEmpty()) {
				userReckoningVote = null;
			}
		}
		
		return new VoteServiceList(userReckoningVote, new Message(), true);		
	}

	public ReckoningRepo getReckoningRepo() {
		return reckoningRepo;
	}

	public void setReckoningRepo(ReckoningRepo reckoningRepo) {
		this.reckoningRepo = reckoningRepo;
	}

	public ReckoningRepoCustom getReckoningRepoCustom() {
		return reckoningRepoCustom;
	}

	public void setReckoningRepoCustom(ReckoningRepoCustom reckoningRepoCustom) {
		this.reckoningRepoCustom = reckoningRepoCustom;
	}

	public VoteCache getVoteCache() {
		return voteCache;
	}

	public void setVoteCache(VoteCache voteCache) {
		this.voteCache = voteCache;
	}

	public ReckoningCache getReckoningCache() {
		return reckoningCache;
	}

	public void setReckoningCache(ReckoningCache reckoningCache) {
		this.reckoningCache = reckoningCache;
	}
}
