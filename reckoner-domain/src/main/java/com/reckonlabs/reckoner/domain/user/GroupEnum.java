package com.reckonlabs.reckoner.domain.user;

import java.util.LinkedList;
import java.util.List;

public enum GroupEnum {

	ANONYMOUS("ANON"), USER("USER"), ADMIN ("ADMIN"), SUPER_ADMIN("S_ADMIN");
	
	private final String code;
	
	/**
	 * @return The associated code.
	 */
	public String getCode() {
		return code;
	}

	GroupEnum(String code) {
		this.code = code;
	}
	
	/**
	 * @return The list of permissions associated with this group.
	 *         NOTE: If permissions need to be added or subtracted, this is the place to do it.
	 */
	
	public static List<PermissionEnum> getPermissions(GroupEnum group) {
		List<PermissionEnum> permissions = new LinkedList<PermissionEnum> ();
		
		switch (group) {
			case ANONYMOUS:
				permissions.add(PermissionEnum.VIEW_LIST);
				permissions.add(PermissionEnum.VIEW_RECKONING);
				permissions.add(PermissionEnum.VIEW_PROFILE);
				permissions.add(PermissionEnum.VOTE);
				break;
			case USER:
				permissions.addAll(getPermissions(GroupEnum.ANONYMOUS));
				permissions.add(PermissionEnum.POST_RECKONING);
				permissions.add(PermissionEnum.VOTE);
				permissions.add(PermissionEnum.COMMENT);
				permissions.add(PermissionEnum.FLAG);
				permissions.add(PermissionEnum.FAVORITE);
				break;
			case ADMIN:
				permissions.addAll(getPermissions(GroupEnum.USER));
				permissions.add(PermissionEnum.APPROVAL);
				permissions.add(PermissionEnum.BLOG_POST);
				permissions.add(PermissionEnum.HIGHLIGHT);
				permissions.add(PermissionEnum.POST_CONTENT);
				permissions.add(PermissionEnum.UPDATE_ALL_COMMENTS);
				permissions.add(PermissionEnum.UPDATE_ALL_RECKONINGS);
				permissions.add(PermissionEnum.UPDATE_PROFILE_INFO);
				break;
			case SUPER_ADMIN:
				permissions.addAll(getPermissions(GroupEnum.ADMIN));
				permissions.add(PermissionEnum.UPDATE_PERMS);
		}
		
		return permissions;
	}
	
	public static List<String> getGroups() {
		  List<String> groups = new LinkedList<String>();

		  for (GroupEnum group : GroupEnum.values()) {
		    groups.add(group.name());  
		  }

		  return groups;
		}
}
