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
	
	public static Message validateReckoningUpdate(Reckoning posting, boolean merge) {
		if (posting.getId() == null || posting.getId().equals("")) {
			return (new Message(MessageEnum.R105_POST_RECKONING));
		}
		if (!merge) {
			return validateReckoningPost(posting);
		}
		
		return null;
	}
	
	public static Message validateReckoningQuery(Integer page, Integer size) {
		
		if (page != null ^ size != null) {
			return (new Message(MessageEnum.R203_GET_RECKONING));
		}
		if (page != null) {
			if (page < 0) {
				return (new Message(MessageEnum.R201_GET_RECKONING));
			}
		}
		if (size != null) {
			if (size < 1) {
				return (new Message(MessageEnum.R202_GET_RECKONING));				
			}
		}
		
		return null;
	}
	
	public static Message validateReckoningId(String id) {
		
		if (id == null) {
			return (new Message(MessageEnum.R02_DEFAULT));
		} else if (id.length() != 24) {
			return (new Message(MessageEnum.R02_DEFAULT));			
		}
		
		return null;
	}

}
