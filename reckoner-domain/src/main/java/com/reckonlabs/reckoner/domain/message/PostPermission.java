package com.reckonlabs.reckoner.domain.message;

import java.util.Set;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.user.GroupEnum;

@XmlRootElement(name = "permission_post")
public class PostPermission implements Serializable {
	
	private static final long serialVersionUID = -8238766148925943050L;
	
	private PostActionEnum action;
	private Set<GroupEnum> groups;
	private Boolean active;
	private String userId;
	private String sessionId;
	
	@XmlElement (name="action")	
	public PostActionEnum getAction() {
		return action;
	}
	public void setAction(PostActionEnum action) {
		this.action = action;
	}
	
	@XmlElementWrapper (name="groups")
	@XmlElement (name="group")
	public Set<GroupEnum> getGroups() {
		return groups;
	}
	public void setGroups(Set<GroupEnum> groups) {
		this.groups = groups;
	}
	
	@XmlElement (name="active")	
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	@XmlElement (name="user_id")	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@XmlElement (name="session_id")
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
