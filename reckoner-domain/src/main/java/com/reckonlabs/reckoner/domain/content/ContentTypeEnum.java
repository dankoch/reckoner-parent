package com.reckonlabs.reckoner.domain.content;

import java.util.LinkedList;
import java.util.List;

public enum ContentTypeEnum {

	// Various statuses for which reckoning can be queried.
	BLOG("BLOG"), VIDEO("VIDEO"), PODCAST("PODCAST");
	
	private final String code;
	
	/**
	 * @return The associated code.
	 */
	public String getCode() {
		return code;
	}

	ContentTypeEnum(String code) {
		this.code = code;
	}
	
	public static boolean isContentType(String contentType) {
		try {
			ContentTypeEnum value = ContentTypeEnum.valueOf(contentType);
		} catch (IllegalArgumentException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		
		return true;
	}
	
	public static List<String> getContentTypes() {
		  List<String> contentTypes = new LinkedList<String>();

		  for (ContentTypeEnum contentType : ContentTypeEnum.values()) {
			  contentTypes.add(contentType.name());  
		  }

		  return contentTypes;
		}
}
