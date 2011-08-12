package com.reckonlabs.reckoner.domain.validator;

import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.notes.Comment;

@Component
public class CommentValidator {
	
	public static Message validateCommentPost(Comment comment, String reckoningId) {
		
		if (comment.getComment() == null) {
			return (new Message(MessageEnum.R400_POST_COMMENT));
		} else if (comment.getPosterId() == null) {
			return (new Message(MessageEnum.R401_POST_COMMENT));
		}
		
		return ReckoningValidator.validateReckoningId(reckoningId);
	}
	
	public static Message validateCommentQuery(Integer page, Integer size) {
		
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
