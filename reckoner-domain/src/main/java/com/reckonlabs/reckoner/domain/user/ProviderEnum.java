package com.reckonlabs.reckoner.domain.user;

import java.util.EnumSet;
import java.util.Map;
import java.util.HashMap;

public enum ProviderEnum {

	FACEBOOK("FACEBOOK"), GOOGLE("GOOGLE");
	
	private final String provider;
	
	/**
	 * @return The associated provider string.
	 */
	public String getProvider() {
		return provider;
	}

	ProviderEnum(String provider) {
		this.provider = provider;
	}	
	
	public static boolean isProvider(String provider) {
		try {
			ProviderEnum value = ProviderEnum.valueOf(provider);
		} catch (IllegalArgumentException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		
		return true;
	}
}
