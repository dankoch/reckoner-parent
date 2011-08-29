package com.reckonlabs.reckoner.contentservices.service;

import java.util.Date;
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
	VoteCache voteCache;
	
	@Autowired
	ReckoningCache reckoningCache;
	
	private static final Logger log = LoggerFactory
			.getLogger(VoteServiceImpl.class);
	
	private static final int ANONYMOUS_VOTE_CHECK_NUMBER = 50;

	@Override
	public ServiceResponse postReckoningVote(Vote vote, String reckoningId, Integer answerIndex) {
		
		try {
			// Confirm that the reckoning exists.
			if (!reckoningRepoCustom.confirmReckoningAndAnswerExists(reckoningId, answerIndex)) {
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
				List<Reckoning> reckonings = reckoningRepo.getReckoningVotesByReckoningId(reckoningId);
				for (Reckoning reckoning : reckonings) {
					for (Answer answer : reckoning.getAnswers()) {
						int checked = 0;
						if (answer.getVotes() != null) {
							for (Vote reckonVote : answer.getVotes()) {
								if (reckonVote.isAnonymous()) {
									if (reckonVote.getIp().equalsIgnoreCase(vote.getIp()) && 
											reckonVote.getUserAgent().equalsIgnoreCase(vote.getUserAgent())) {
										// Got a recent IP / User Agent match.  Reject the vote.
										return (new ServiceResponse(new Message(MessageEnum.R602_POST_VOTE), false));
									}							
									checked++;
								}
								if (checked > ANONYMOUS_VOTE_CHECK_NUMBER) {break;}
							}
						}
					}
				}
			}
			vote.setVotingDate(DateUtility.now());
			reckoningRepoCustom.insertReckoningVote(vote, answerIndex, reckoningId);
			
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
	public ReckoningServiceList getUserVotedReckonings(String userId) {
		List<Reckoning> userVotedReckonings = null;
		
		try {
			userVotedReckonings = reckoningCache.getCachedUserVotedReckonings(userId);
			if (userVotedReckonings == null) {
				userVotedReckonings = reckoningRepo.getVotesByUser(userId);
				for (Reckoning reckoning : userVotedReckonings) {
					List<Vote> vote = getUserVoteFromReckoning(userId, reckoning);
					
					// Clear out the answer vote lists so they only contain the user's vote.
					if (vote !=  null) {
						if (!vote.isEmpty()) {
							for (Answer answer : reckoning.getAnswers()) {
								if (answer.getIndex() == vote.get(0).getAnswerIndex()) {
									answer.setVotes(vote);
								} else {
									answer.setVotes(new LinkedList<Vote> ());
								}
							}
						}
					}
				}
				reckoningCache.setCachedUserVotedReckonings(userVotedReckonings, userId);
			}
		} catch (Exception e) {
		   log.error("General exception when getting reckonings voted by user: " + e.getMessage());
		   log.debug("Stack Trace:", e);			
		   return new ReckoningServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);
	    }
		
		return new ReckoningServiceList(userVotedReckonings, new Message(), true);		
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
						userReckoningVote = getUserVoteFromReckoning(userId, votedReckonings.get(0));	
					}
				}
				// Empty array list will be written to cache if no votes turn up.
				if (userReckoningVote == null) {
					userReckoningVote = new LinkedList<Vote> ();
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
	
	// Used to extract a particular user's vote out of a Reckoning object.
	private static List<Vote> getUserVoteFromReckoning(String userId, Reckoning reckoning) {
		List<Vote> userReckoningVote = null;
		boolean search = true;
		
		// Assuming one vote per result set, so stop the search once found to save time.
		for (Answer answer : reckoning.getAnswers()) {
			if (answer.getVotes() != null) {
				for (Vote vote : answer.getVotes()) {
					if (vote.getVoterId().equals(userId)) {
						userReckoningVote = new LinkedList<Vote> ();
						userReckoningVote.add(vote);
						search = false;
						break;
					}
				}
				if (!search) break;
			}
		}	
		
		return userReckoningVote;
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
