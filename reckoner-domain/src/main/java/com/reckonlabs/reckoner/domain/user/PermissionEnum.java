package com.reckonlabs.reckoner.domain.user;

import java.util.LinkedList;
import java.util.List;

public enum PermissionEnum {

	VOTE("vote"), COMMENT("comment"), FLAG("flag"), FAVORITE("favorite"), VIEW_PROFILE("view_profile"), APPROVAL("approval"),
	BLOG_POST("blog_post"), HIGHLIGHT("highlight"), UPDATE_PERMS("update_perms");
	
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
	
	public static List<String> getPermissions() {
		  List<String> permissions = new LinkedList<String>();

		  for (PermissionEnum permission : PermissionEnum.values()) {
			  permissions.add(permission.name());  
		  }

		  return permissions;
		}
}
