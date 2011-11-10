package com.reckonlabs.reckoner.domain.validator;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.content.Content;
import com.reckonlabs.reckoner.domain.content.ContentTypeEnum;

@Component
public class ContentValidator {
	
	public static Message validateContentPost(Content posting) {
		
		if (posting.getTitle() == null) {
			return (new Message(MessageEnum.R1000_POST_CONTENT));
		} else if (posting.getBody() == null) {
			return (new Message(MessageEnum.R1001_POST_CONTENT));			
		} else if (posting.getSubmitterId() == null) {
			return (new Message(MessageEnum.R1002_POST_CONTENT));
		}
		
		return null;
	}
	
	public static Message validateContentUpdate(Content posting, boolean merge) {
		if (posting.getId() == null || posting.getId().equals("")) {
			return (new Message(MessageEnum.R1003_POST_CONTENT));
		}
		if (!merge) {
			return validateContentPost(posting);
		}
		
		return null;
	}
	
	public static Message validateContentQuery(Integer page, Integer size) {
		
		return validateContentQuery (null, null, null, null,
										null, null, page, size, null);
	}
	
	public static Message validateContentQuery(ContentTypeEnum contentType, 
			Date postedAfter, Date postedBefore,
			List<String> includeTags,
			String sortBy, Boolean ascending,
			Integer page, Integer size, Boolean randomize) {
		
		// Paging and Sorting rules.  Different if Randomized.
		if (randomize != null && randomize.booleanValue()) {
			if (sortBy != null || page != null) {
				return (new Message(MessageEnum.R1100_GET_CONTENT));				
			} else if (size == null || size < 1) {
				return (new Message(MessageEnum.R1101_GET_CONTENT));				
			}
		} else {
			if (page != null && size == null) {
				return (new Message(MessageEnum.R1102_GET_CONTENT));
			}
			if (page != null) {
				if (page < 0) {
					return (new Message(MessageEnum.R1103_GET_CONTENT));
				}
			}
			if (size != null) {
				if (size < 1) {
					return (new Message(MessageEnum.R1104_GET_CONTENT));				
				}
			}
		}
		
		if (postedAfter != null && postedBefore != null) {
			if (postedBefore.before(postedAfter)) {
				return (new Message(MessageEnum.R1105_GET_CONTENT));					
			}
		}
		if (sortBy != null) {
			if (!validSortCriteria(sortBy)) {
				return (new Message(MessageEnum.R1107_GET_CONTENT));
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
		String[] validCriteria = {"postingDate", "views"};
		
		return Arrays.asList(validCriteria).contains(sortCriteria);
	}

}
