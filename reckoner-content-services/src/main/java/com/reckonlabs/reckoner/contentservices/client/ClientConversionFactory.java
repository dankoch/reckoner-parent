package com.reckonlabs.reckoner.contentservices.client;

import com.reckonlabs.reckoner.domain.client.google.GoogleTokenResponse;
import com.reckonlabs.reckoner.domain.client.google.profile.PluralSubfield;
import com.reckonlabs.reckoner.domain.client.google.profile.Profile;
import com.reckonlabs.reckoner.domain.user.AuthSession;
import com.reckonlabs.reckoner.domain.user.ProviderEnum;
import com.reckonlabs.reckoner.domain.user.User;

public final class ClientConversionFactory {

	public static final User createReckonerUserFromFacebook(com.restfb.types.User facebookUser, 
															String graphApiUrl) {
		
		User reckonerUser = new User();
		reckonerUser.setAuthProvider(ProviderEnum.FACEBOOK);
		reckonerUser.setAuthProviderId(facebookUser.getId());
		reckonerUser.setEmail(facebookUser.getEmail());
		reckonerUser.setFirstName(facebookUser.getFirstName());
		reckonerUser.setLastName(facebookUser.getLastName());
		reckonerUser.setUsername(facebookUser.getUsername());
		
		reckonerUser.setProfileUrl(facebookUser.getLink());
		reckonerUser.setProfilePictureUrl(graphApiUrl + facebookUser.getId() + "/picture");
		
		return reckonerUser;
		
	}
	
	public static final User createReckonerUserFromGoogle(Profile googleUser) {
		User reckonerUser = new User();
		
		reckonerUser.setAuthProvider(ProviderEnum.GOOGLE);
		reckonerUser.setAuthProviderId(googleUser.getId());
		if (!googleUser.getEmails().isEmpty()) {
			for (PluralSubfield email : googleUser.getEmails()) {
				if (email.getPrimary()) {
					reckonerUser.setEmail(email.getValue());
				}
			}
		}
		reckonerUser.setUsername(googleUser.getDisplayName());
		
		// The Google Buzz API Currently Sets the 'DisplayName' field as <First> <Last>
		if (reckonerUser.getUsername() != null) {
			String[] names = reckonerUser.getUsername().split("\\s+");
			
			for (int i = 0; i < names.length; i ++) {
				if (i == 0) {
					reckonerUser.setFirstName(names[0]);
				} else {
					reckonerUser.setLastName(reckonerUser.getLastName() + names[i]);
				}
			}
		}
		
		// Override the above, just in case.
		if (googleUser.getName() != null) {
			reckonerUser.setFirstName(googleUser.getName().getGivenName());
			reckonerUser.setLastName(googleUser.getName().getFamilyName());
		}
		
		reckonerUser.setProfileUrl(googleUser.getProfileUrl());
		reckonerUser.setProfilePictureUrl(googleUser.getThumbnailUrl());
		
		return reckonerUser;
	}
	
}
