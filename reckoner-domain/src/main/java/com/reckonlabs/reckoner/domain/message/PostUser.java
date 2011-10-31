package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.user.User;

@XmlRootElement(name = "user_post")
public class PostUser implements Serializable {

	private static final long serialVersionUID = -4218281053538996203L;
	
	private User user;
	private String sessionId;
	
	@XmlElement (name="user")
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	@XmlElement (name="session_id")
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
