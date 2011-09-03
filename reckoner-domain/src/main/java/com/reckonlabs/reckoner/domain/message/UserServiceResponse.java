package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;

import com.reckonlabs.reckoner.domain.user.AuthSession;
import com.reckonlabs.reckoner.domain.user.User;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "authentication_service_response")
public class UserServiceResponse extends ServiceResponse implements Serializable {
	
	private static final long serialVersionUID = 913593100323141750L;
	
	private User user;
	private AuthSession authSession;

	public UserServiceResponse() {
		setUser(null);
		setAuthSession(null);
		setMessage(new Message());
		setSuccess(true);
	}
	
	public UserServiceResponse(User user, AuthSession authSession, Message message, boolean success) {
		setUser(user);
		setAuthSession(authSession);
		setMessage(message);
		setSuccess(success);
	}

	@XmlElement (name = "user")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@XmlElement (name = "auth_session")
	public AuthSession getAuthSession() {
		return authSession;
	}

	public void setAuthSession(AuthSession authSession) {
		this.authSession = authSession;
	}	

}
