package com.reckonlabs.reckoner.domain.user;

import java.util.LinkedList;
import java.util.List;

public enum PermissionEnum {

	VIEW_RECKONING("view_reckoning"),
	VIEW_LIST("view_list"),
	VIEW_CONTENT("view_content"),
	POST_RECKONING("post_reckoning"),
	POST_CONTENT("post_content"),
	UPDATE_RECKONING("update_reckoning"), 
	UPDATE_ALL_RECKONINGS("update_all_reckonings"),
	UPDATE_ALL_COMMENTS("update_all_comments"),
	UPDATE_ALL_CONTENT("update_content"),
	VOTE("vote"), 
	COMMENT("comment"), 
	FLAG("flag"), 
	FAVORITE("favorite"), 
	VIEW_PROFILE("view_profile"), 
	APPROVAL("approval"), 
	BLOG_POST("blog_post"), 
	HIGHLIGHT("highlight"), 
	UPDATE_PERMS("update_perms"),
	UPDATE_PROFILE_INFO("update_info"),
	CONTACT_US("contact_us"),
	SITEMAP("sitemap");
	
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
