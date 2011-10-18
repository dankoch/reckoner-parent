package com.reckonlabs.reckoner.domain.validator;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.reckoning.ReckoningTypeEnum;

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
		
		return validateReckoningQuery (ReckoningTypeEnum.OPEN_AND_CLOSED, null, null, null,
										null, null, null, null, null, page, size);
	}
	
	public static Message validateReckoningQuery(ReckoningTypeEnum reckoningType, 
			Date postedAfter, Date postedBefore,
			Date closedAfter, Date closedBefore,
			List<String> includeTags, List<String> excludeTags,
			String sortBy, Boolean ascending,
			Integer page, Integer size) {
		
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
		
		if (postedAfter != null && postedBefore != null) {
			if (postedBefore.before(postedAfter)) {
				return (new Message(MessageEnum.R205_GET_RECKONING));					
			}
		}
		if (closedAfter != null && closedBefore != null) {
			if (closedBefore.before(closedAfter)) {
				return (new Message(MessageEnum.R206_GET_RECKONING));					
			}
		}
		if (sortBy != null) {
			if (!validSortCriteria(sortBy)) {
				return (new Message(MessageEnum.R207_GET_RECKONING));
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
	
	public static boolean validSortCriteria(String sortCriteria) {
		String[] validCriteria = {"postingDate", "closingDate", "views"};
		
		return Arrays.asList(validCriteria).contains(sortCriteria);
	}

}
