package com.reckonlabs.reckoner.contentservices.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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
import com.reckonlabs.reckoner.contentservices.utility.ServiceProps;
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
import com.reckonlabs.reckoner.domain.user.User;
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
	
	@Resource
	ServiceProps serviceProps;
	
	private static final Logger log = LoggerFactory
			.getLogger(VoteServiceImpl.class);

	@Override
	public ServiceResponse postReckoningVote(Vote vote, String reckoningId, Integer answerIndex, String sessionId) {
		
		try {
			// Confirm that the reckoning exists and isn't closed.
			if (!reckoningRepoCustom.confirmReckoningIsVotingEligible(reckoningId, answerIndex)) {
				log.warn("Attempted to post vote to non-existent reckoning: " + reckoningId);
				return (new ServiceResponse(new Message(MessageEnum.R600_POST_VOTE), false));
			}
			
			// Confirm that the user hasn't already voted.  Check the cache first -- if nothing is there, check the DB.
			List<Vote> userReckoningVote = getUserReckoningVote(vote.getVoterId(), reckoningId, sessionId).getVotes();
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
			vote.setAnswerIndex(answerIndex);
			reckoningRepoCustom.insertReckoningVote(vote);
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
	public ServiceResponse updateReckoningVote(Vote updateVote, String sessionId) {
		// NOTE: This only updates the metadata associated with a vote.  If you specify a different answer index, it'll be ignored.
		// To change a vote, you've got to delete it first and then make a new one.
		
		try {
			// Confirm that the vote in question already exists to be updated.
			// Also confirm that the answer index isn't being changed. (You need to do a delete and insert to do that.)
			List<Vote> userReckoningVote = getUserReckoningVote(updateVote.getVoterId(), updateVote.getReckoningId(), sessionId).getVotes();
			
			if (userReckoningVote == null || userReckoningVote.isEmpty()) {
				return (new ServiceResponse(new Message(MessageEnum.R603_POST_VOTE), false));
			} else if (userReckoningVote.get(0).getAnswerIndex() != updateVote.getAnswerIndex()) {
				return (new ServiceResponse(new Message(MessageEnum.R604_POST_VOTE), false));					
			}		
			
			// Retain the date of the original vote and store it in the DB.
			updateVote.setVotingDate(userReckoningVote.get(0).getVotingDate());
			reckoningRepoCustom.updateReckoningVote(updateVote);
			
			// Cache management.  Cache the vote to confirm the user has voted for this one.
			List<Vote> voteCacheEntry = new LinkedList<Vote>();
			voteCacheEntry.add(updateVote);
			voteCache.setCachedUserReckoningVote(voteCacheEntry, updateVote.getVoterId(), updateVote.getReckoningId());
			
			// Cache management.  Remove the list of reckonings voted by this user from the cache.  We'll recompile on the next request.
			reckoningCache.removeCachedUserVotedReckonings(updateVote.getVoterId());
		} catch (Exception e) {
			log.error("General exception when updating a vote: " + e.getMessage());
			log.debug("Stack Trace:", e);			
			return (new ServiceResponse(new Message(MessageEnum.R01_DEFAULT), false));	
		}
		
		return new ServiceResponse();
	}

	@Override
	public ReckoningServiceList getUserVotedReckonings(String userId, Integer page, Integer size, String sessionId) {
		List<Reckoning> userVotedReckonings = null;
		long count = 0;
		
		try {
			// If somebody is asking for a user's voting history and they aren't that user, check to see if the target user
			// is hiding their voting history.  If so, send an empty list.
			User requestUser = userService.getUserBySessionId(sessionId).getUser();
			if (requestUser == null || (requestUser != null && !requestUser.getId().equalsIgnoreCase(userId))) {
				User targetUser = userService.getUserByUserId(userId).getUser();
				if (targetUser != null && targetUser.isHideVotes()) {
					return new ReckoningServiceList(new LinkedList<Reckoning>(), count, new Message(), true);					
				}
			}
			
			// Removing cache usage for the time being.  Need to cache conditionally whether this is the user accessing their own profile
			// or somebody else.
			// userVotedReckonings = reckoningCache.getCachedUserVotedReckonings(userId);
			
			if (userVotedReckonings == null || userVotedReckonings.isEmpty()) {
				userVotedReckonings = reckoningRepoCustom.getUserVotedReckonings(userId);			
				
				Iterator<Reckoning> reckoningIterator = userVotedReckonings.iterator();
				while (reckoningIterator.hasNext()) {
					Reckoning reckoning = reckoningIterator.next();
					List<Vote> vote = reckoning.getVoteByUser(userId);
					
					// Clear out the answer vote lists so they only contain the user's vote.
					if (!vote.isEmpty()) {
						// Remove the Reckoning if the vote was marked as anonymous and the request user doesn't match the user
						// whose voting record we're retrieving
						if (vote.get(0).isAnonymous() && (requestUser == null || !userId.equalsIgnoreCase(requestUser.getId()))) {
							reckoningIterator.remove();
						}
						else {
							for (Answer answer : reckoning.getAnswers()) {
								if (answer.getIndex() == vote.get(0).getAnswerIndex()) {
									HashMap<String, Vote> answerVote = new HashMap<String, Vote>();
									answerVote.put(userId, new Vote(userId, reckoning.getId(), answer.getIndex(), vote.get(0).isAnonymous()));
									answer.setVotes(answerVote);
									answer.setVoteTotal(1);
								} else {
									answer.setVotes(new HashMap<String, Vote> ());
									answer.setVoteTotal(0);
								}
							}
						}
					}
					
					reckoning.setPostingUser(userService.getUserByUserId
							(reckoning.getSubmitterId(), true).getUser());
				}
				
				// Removing cache usage for the time being.  Need to cache conditionally whether this is the user accessing their own profile
				// or somebody else.
				// reckoningCache.setCachedUserVotedReckonings(userVotedReckonings, userId);
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
	public VoteServiceList getUserReckoningVote(String userId, String reckoningId, String sessionId) {
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
	
	// Returns a list of non-anonymous votes as well as the count of total votes for the provided answer.
	// Note: a 'false' for the value of the votes hash-map is assumed to be non-anonymous
	@Override
	public VoteServiceList getReckoningAnswerVotes (String id, Integer answer, Integer page, Integer size) {
		List<Vote> answerVoteList = null;
		Long count = 0L;
		
		try {
			List<Reckoning> votedReckonings = reckoningRepo.getReckoningVotesByReckoningId(id);
			
			// Get all of the votes and determine which were made by logged in users who didn't make their vote anonymous.
			if (votedReckonings != null && !votedReckonings.isEmpty()) {
				answerVoteList = new LinkedList<Vote>();
				
				if (votedReckonings.get(0).getAnswers() != null && votedReckonings.get(0).getAnswers().size() > answer) {
					if (votedReckonings.get(0).getAnswers().get(answer).getVotes() != null) {
						count = new Long(votedReckonings.get(0).getAnswers().get(answer).getVotes().size());
						for (Map.Entry<String, Vote> entry : votedReckonings.get(0).getAnswers().get(answer).getVotes().entrySet()) {
							if (entry.getValue() != null && !entry.getValue().isAnonymous() && !entry.getKey().startsWith("anon")) {
								answerVoteList.add(new Vote(entry.getKey(), id, answer));
							}
						}
					}
				}
			}	
			
			// Page the vote list, add user summaries in for the users, and make the hidden votes attributable to Anonybot.
			answerVoteList = (List<Vote>) ListPagingUtility.pageList(answerVoteList, page, size);
			User hiddenUser = userService.getUserByUserId(serviceProps.getAnonymousUser(), true).getUser();
			for (Vote vote : answerVoteList) {
				User votingUser = userService.getUserByUserId(vote.getVoterId(), true).getUser();
				
				if (votingUser != null && !votingUser.isHideProfile() && !votingUser.isHideVotes()) {
					vote.setVotingUser(votingUser);
				} else {
					vote.setVotingUser(hiddenUser);
				}
			}
			
		} catch (Exception e) {
		   log.error("General exception when getting votes for a reckoning answer: " + e.getMessage());
		   log.debug("Stack Trace:", e);			
		   return new VoteServiceList(null, new Message(MessageEnum.R01_DEFAULT), false);			
		}
		
		return new VoteServiceList(answerVoteList, count, new Message(), true);
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
