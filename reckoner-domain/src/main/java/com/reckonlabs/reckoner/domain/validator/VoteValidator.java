package com.reckonlabs.reckoner.domain.validator;

import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.reckoning.Vote;

@Component
public class VoteValidator {
	
	public static Message validateVotePost(Vote vote, String reckoningId, Integer answerIndex) {
		return null;
	}
}
