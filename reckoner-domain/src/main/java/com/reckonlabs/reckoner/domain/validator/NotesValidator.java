package com.reckonlabs.reckoner.domain.validator;

import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.notes.Favorite;
import com.reckonlabs.reckoner.domain.notes.Flag;

@Component
public class NotesValidator {
	
	public static Message validateFavoritePost(Favorite favorite, String targetId) {
		
		if (favorite.getUserId() == null) {
			return (new Message(MessageEnum.R800_POST_NOTE));
		}
		
		return ReckoningValidator.validateReckoningId(targetId);
	}
	
	public static Message validateFlagPost(Flag flag, String targetId) {
		
		if (flag.getUserId() == null) {
			return (new Message(MessageEnum.R800_POST_NOTE));
		}
		
		return ReckoningValidator.validateReckoningId(targetId);
	}	
	
	public static Message validateUserFavoriteQuery(Integer page, Integer size) {
		
		if (page != null ^ size != null) {
			return (new Message(MessageEnum.R500_GET_COMMENT));
		}
		if (page != null) {
			if (page < 0) {
				return (new Message(MessageEnum.R500_GET_COMMENT));
			}
		}
		if (size != null) {
			if (size < 1) {
				return (new Message(MessageEnum.R500_GET_COMMENT));				
			}
		}
		
		return null;
	}
}
