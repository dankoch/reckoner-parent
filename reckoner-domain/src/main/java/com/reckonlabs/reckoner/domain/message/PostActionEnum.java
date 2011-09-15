package com.reckonlabs.reckoner.domain.message;

public enum PostActionEnum {

	ADD("add"), REMOVE("remove"), REPLACE("replace"), CLEAR("clear");
	
	private final String code;
	
	/**
	 * @return The associated code.
	 */
	public String getCode() {
		return code;
	}

	PostActionEnum(String code) {
		this.code = code;
	}	
}
