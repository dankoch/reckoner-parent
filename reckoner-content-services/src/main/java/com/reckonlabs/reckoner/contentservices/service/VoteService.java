package com.reckonlabs.reckoner.contentservices.service;

import java.lang.Boolean;
import java.util.Date;

import com.reckonlabs.reckoner.domain.reckoning.Vote;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.VoteServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;

public interface VoteService {
	
	public ServiceResponse postReckoningVote (Vote vote, String reckonId, Integer answerIndex, String sessionId);
	
	public ServiceResponse updateReckoningVote (Vote updateVote, String sessionId);
	
	public ReckoningServiceList getUserVotedReckonings (String userId, Integer page, Integer size, String sessionId);
	
	public VoteServiceList getUserReckoningVote (String userId, String reckonId, String sessionId);
	
	public VoteServiceList getReckoningAnswerVotes (String id, Integer answer, Integer page, Integer size);
}
