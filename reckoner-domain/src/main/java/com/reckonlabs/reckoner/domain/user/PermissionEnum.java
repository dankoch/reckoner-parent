package com.reckonlabs.reckoner.domain.user;

public enum PermissionEnum {

	VOTE("vote"), COMMENT("comment"), FLAG("flag"), FAVORITE("favorite"), VIEW_PROFILE("view_profile"), APPROVAL("approval"),
	BLOG_POST("blog_post"), HIGHLIGHT("highlight");
	
	private final String code;
	
	/**
	 * @return The associated code.
	 */
	public String getCode() {
		return code;
	}

	PermissionEnum(String code) {
		this.code = code;
	}	
}
