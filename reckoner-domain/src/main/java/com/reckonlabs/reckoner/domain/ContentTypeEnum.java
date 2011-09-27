package com.reckonlabs.reckoner.domain;

import java.util.LinkedList;
import java.util.List;

public enum ContentTypeEnum {

	RECKONING("reckoning"), POST("post");
	
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
	
	public static List<String> getContentTypes() {
	    List<String> contentTypes = new LinkedList<String>();

	    for (ContentTypeEnum group : ContentTypeEnum.values()) {
		  contentTypes.add(group.name());  
	    }

	    return contentTypes;
	}
}
