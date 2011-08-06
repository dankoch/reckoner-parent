package com.reckonlabs.reckoner.domain.validator;

import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

@Component
public class ReckoningValidator {
	
	public static Message validateReckoningPost(Reckoning posting) {
		
		if (posting.getQuestion() == null) {
			return (new Message(MessageEnum.R100_POST_RECKONING));
		} else if (posting.getAnswers() == null) {
			return (new Message(MessageEnum.R101_POST_RECKONING));			
		} else if (posting.getAnswers().size() < 2) {
			return (new Message(MessageEnum.R102_POST_RECKONING));
		} else if (posting.getSubmitterId() == null) {
			return (new Message(MessageEnum.R103_POST_RECKONING));
		}
		
		return null;
	}

}
