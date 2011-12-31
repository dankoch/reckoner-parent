package com.reckonlabs.reckoner.domain.media;

import java.util.LinkedList;
import java.util.List;

public enum MediaTypeEnum {

	// Various media types that can be attached to a Reckoning or Content
	AUDIO("AUDIO"), VIDEO("VIDEO"), IMAGE("IMAGE");
	
	private final String code;
	
	/**
	 * @return The associated code.
	 */
	public String getCode() {
		return code;
	}

	MediaTypeEnum(String code) {
		this.code = code;
	}
	
	public static boolean isMediaType(String mediaType) {
		try {
			MediaTypeEnum value = MediaTypeEnum.valueOf(mediaType);
		} catch (IllegalArgumentException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		
		return true;
	}
	
	public static List<String> getMediaTypes() {
		  List<String> mediaTypes = new LinkedList<String>();

		  for (MediaTypeEnum mediaType : MediaTypeEnum.values()) {
			  mediaTypes.add(mediaType.name());  
		  }

		  return mediaTypes;
		}
}
