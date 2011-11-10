package com.reckonlabs.reckoner.domain.content;

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
}
