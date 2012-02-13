package com.reckonlabs.reckoner.domain.validator;

import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.media.Media;

@Component
public class MediaValidator {
	
	public static Message validateMediaPost(Media media, String reckoningId) {
		
		if (media.getName() == null || media.getUrl() == null || media.getMediaType() == null) {
			return (new Message(MessageEnum.R1200_MEDIA));
		} 
		
		return ReckoningValidator.validateReckoningId(reckoningId);
	}
}
