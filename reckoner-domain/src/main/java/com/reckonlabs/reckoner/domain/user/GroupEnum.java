package com.reckonlabs.reckoner.domain.user;

import java.util.LinkedList;
import java.util.List;

public enum GroupEnum {

	ANONYMOUS("anon"), USER("user"), ADMIN ("admin");
	
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
	
	public List<PermissionEnum> getPermissions(GroupEnum group) {
		List<PermissionEnum> permissions = new LinkedList<PermissionEnum> ();
		
		switch (group) {
			case ANONYMOUS:
				permissions.add(PermissionEnum.VOTE);
			case USER:
				permissions.add(PermissionEnum.VOTE);
				permissions.add(PermissionEnum.COMMENT);
				permissions.add(PermissionEnum.FLAG);
				permissions.add(PermissionEnum.FAVORITE);
				permissions.add(PermissionEnum.VIEW_PROFILE);
			case ADMIN:
				permissions.addAll(getPermissions(GroupEnum.USER));
				permissions.add(PermissionEnum.APPROVAL);
				permissions.add(PermissionEnum.BLOG_POST);
				permissions.add(PermissionEnum.HIGHLIGHT);
		}
		
		return permissions;
	}
}
