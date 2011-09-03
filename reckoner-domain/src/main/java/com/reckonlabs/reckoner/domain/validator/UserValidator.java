package com.reckonlabs.reckoner.domain.validator;

import org.springframework.stereotype.Component;

import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.message.PostOAuthUser;
import com.reckonlabs.reckoner.domain.user.ProviderEnum;
import com.reckonlabs.reckoner.domain.user.User;

@Component
public class UserValidator {
	
	public static Message validateOAuthUserPost(PostOAuthUser postOAuthUser) {
		
		if (postOAuthUser.getProvider() == null) {
			return (new Message(MessageEnum.R700_AUTH_USER));
		} else if (!ProviderEnum.isProvider(postOAuthUser.getProvider())) {
			return (new Message(MessageEnum.R701_AUTH_USER));			
		} 
		
		return null;
	}

}
