package com.reckonlabs.reckoner.domain.user;

import java.util.Date;
import java.util.List;
import java.util.LinkedList;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Id;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
public class User implements Serializable {

	private static final long serialVersionUID = -4687976467744632363L;
	
	@Id
	private String id;
	
	@Column (name = "username")
	private String username;
	@Column (name = "first_name")
	private String firstName;
	@Column (name = "last_name")
	private String lastName;
	@Column (name = "email")
	private String email;
	
	@Column (name = "auth_provider")
	private ProviderEnum authProvider;
	@Column (name = "auth_provider_id")
	private String authProviderId;
	
	@Column (name = "first_login")
	private Date firstLogin;
	@Column (name = "last_login")	
	private Date lastLogin;
	
	@Column (name = "profile_picture_url")	
	private String profilePictureUrl;
	@Column (name = "profile_url")	
	private String profileUrl;
	
	@Column (name = "groups")
	private List<GroupEnum> groups;
	
	@Column (name = "active")
	private boolean active;
	
	public User() {
	}

	@XmlElement(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@XmlElement(name = "username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@XmlElement(name = "first_name")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@XmlElement(name = "last_name")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@XmlElement(name = "email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@XmlElement(name = "auth_provider")
	public ProviderEnum getAuthProvider() {
		return authProvider;
	}

	public void setAuthProvider(ProviderEnum authProvider) {
		this.authProvider = authProvider;
	}

	@XmlElement(name = "auth_provider_id")
	public String getAuthProviderId() {
		return authProviderId;
	}

	public void setAuthProviderId(String authProviderId) {
		this.authProviderId = authProviderId;
	}

	@XmlElement(name = "first_login")
	public Date getFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(Date firstLogin) {
		this.firstLogin = firstLogin;
	}

	@XmlElement(name = "last_login")
	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	@XmlElement(name = "profile_picture_url")
	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}

	@XmlElement(name = "profile_url")
	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	@XmlElementWrapper(name = "groups")
	@XmlElement(name = "group")
	public List<GroupEnum> getGroups() {
		return groups;
	}

	public void setGroups(List<GroupEnum> groups) {
		this.groups = groups;
	}

	@XmlElementWrapper(name = "permissions")
	@XmlElement(name = "permission")
	public List<PermissionEnum> getPermissions() {
		List<PermissionEnum> returnVal = new LinkedList<PermissionEnum>();
		List<GroupEnum> groups = getGroups();
		for (GroupEnum group : groups) {
			returnVal.addAll(GroupEnum.getPermissions(group));
		}
		
		return returnVal;
	}
	
	public void addGroup(GroupEnum group) {
		if (this.groups == null) {
			groups = new LinkedList<GroupEnum> ();
		}
		groups.add(group);
	}
	
	public void removeGroup(GroupEnum group) {
		if (!(this.groups == null)) {
			groups.remove(group);
		}
	}
	
	/*
	@XmlElementWrapper(name = "groups")
	@XmlElement(name = "group")
	public List<String> getGroups() {
		return groups;
	}
	
	public List<GroupEnum> getGroupsAsEnum() {
		List<GroupEnum> returnVal = new LinkedList<GroupEnum>();
		for (String group : this.groups) {
			returnVal.add(GroupEnum.valueOf(group));
		}
		
		return returnVal;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	
	public void setGroupsAsEnum(List<GroupEnum> groups) {
		for (GroupEnum group : groups) {
			addGroup(group);
		}
	}
	
	public void addGroup(GroupEnum group) {
		if (this.groups == null) {
			groups = new LinkedList<String> ();
		}
		groups.add(group.getCode());
	}
	
	public void removeGroup(GroupEnum group) {
		if (!(this.groups == null)) {
			groups.remove(group.getCode());
		}
	}
	
	@XmlElementWrapper(name = "permissions")
	@XmlElement(name = "permission")
	public List<String> getPermissions() {
		List<String> permissions = new LinkedList<String>();
		for (PermissionEnum permission : getPermissionsAsEnum()) {
			permissions.add(permission.getCode());
		}
		
		return permissions;
	}
	
	public List<PermissionEnum> getPermissionsAsEnum() {
		List<PermissionEnum> returnVal = new LinkedList<PermissionEnum>();
		for (GroupEnum group : getGroupsAsEnum()) {
			returnVal.addAll(group.getPermissions(group));
		}
		
		return returnVal;
	} */

	@XmlElement(name = "active")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}