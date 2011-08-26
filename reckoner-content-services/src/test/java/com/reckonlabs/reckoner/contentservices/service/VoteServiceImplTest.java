package com.reckonlabs.reckoner.contentservices.service;

import java.util.List;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.reckonlabs.reckoner.contentservices.cache.ReckoningCache;
import com.reckonlabs.reckoner.contentservices.cache.VoteCache;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepo;
import com.reckonlabs.reckoner.contentservices.repo.ReckoningRepoCustom;

import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.reckoning.Vote;

import static org.easymock.EasyMock.*;

public class VoteServiceImplTest {
	
	VoteServiceImpl voteService;
	
	ReckoningRepo mockReckoningRepo;
	ReckoningRepoCustom mockReckoningRepoCustom;
	VoteCache mockVoteCache;
	ReckoningCache mockReckoningCache;
	
	private static final String VALID_RECKONING = "reck01";
	private static final int VALID_ANSWER = 1;
	private static final String USER_ID = "joe";
	
	private static final String BAD_RECKONING = "wreck01";
	private static final int BAD_ANSWER = 2;
	
	
	@Before 
	public void initialize() {
		voteService = new VoteServiceImpl();
		
		mockReckoningRepo = createMock(ReckoningRepo.class);
		mockReckoningRepoCustom = createMock(ReckoningRepoCustom.class);
		mockVoteCache = createMock(VoteCache.class);
		mockReckoningCache = createMock(ReckoningCache.class);
		
		voteService.setReckoningRepo(mockReckoningRepo);
		voteService.setReckoningRepoCustom(mockReckoningRepoCustom);
		voteService.setVoteCache(mockVoteCache);
		voteService.setReckoningCache(mockReckoningCache);
	}

	@Test
	public void testValidReckoningAnswerCheck() {
		expect(mockReckoningRepoCustom.confirmReckoningAndAnswerExists(BAD_RECKONING, BAD_ANSWER)).andReturn(false);
		replay(mockReckoningRepoCustom);
		
		ServiceResponse response = voteService.postReckoningVote(new Vote(), BAD_RECKONING, BAD_ANSWER);
		Assert.assertFalse(response.isSuccess());
	}
	
	@Test
	public void testAlreadyVotedCache() {
		expect(mockReckoningRepoCustom.confirmReckoningAndAnswerExists(VALID_RECKONING, VALID_ANSWER)).andReturn(true);
		expect(mockVoteCache.getCachedUserReckoningVote(USER_ID, VALID_RECKONING)).andReturn(buildVoteList(USER_ID, VALID_ANSWER, false, null, null));
		replay(mockReckoningRepoCustom);
		replay(mockVoteCache);
		
		ServiceResponse response = voteService.postReckoningVote(buildVote(USER_ID, VALID_ANSWER, false, null, null), VALID_RECKONING, VALID_ANSWER);
		Assert.assertFalse(response.isSuccess());
	}
	
	private static List<Vote> buildVoteList(String voterId, int votingIndex, boolean anon, String ip, String userAgent) {
		List<Vote> returnVal = new LinkedList<Vote> ();
		returnVal.add(buildVote(voterId, votingIndex, anon, ip, userAgent));
		
		return returnVal;
	}
	
	private static Vote buildVote(String voterId, int votingIndex, boolean anon, String ip, String userAgent) {
		Vote returnVal = new Vote();
		returnVal.setVoterId(voterId);
		returnVal.setAnswerIndex(votingIndex);
		returnVal.setIp(ip);
		returnVal.setUserAgent(userAgent);
		returnVal.setAnonymous(anon);
		
		return returnVal;
	}

}
