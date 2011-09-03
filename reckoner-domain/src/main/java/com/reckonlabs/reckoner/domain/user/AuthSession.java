package com.reckonlabs.reckoner.domain.user;

import java.util.Date;
import java.util.List;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.utility.DateUtility;

@XmlRootElement(name = "auth_session")
public class AuthSession implements Serializable {
	
	private static final long serialVersionUID = 363810288896217706L;
	
	@Id
	private String id;	
	
	@Column (name = "user_token")
	private String userToken;
	@Column (name = "reckoner_user_id")
	private String reckonerUserId;
	@Column (name = "auth_provider")
	private ProviderEnum authProvider;
	@Column (name = "created_date")
	private Date createdDate;
	@Column (name = "expiration_date")
	private Date expirationDate;	
	
	public AuthSession() {
		
	}
	
	public AuthSession(String userToken, User user, String expires) {
		this.userToken = userToken;
		this.reckonerUserId = user.getId();
		this.authProvider = user.getAuthProvider();
		this.createdDate = DateUtility.now();
		
		if (expires != null) {
			this.expirationDate = new Date(this.createdDate.getTime() + Integer.parseInt(expires) * 1000);
		}
	}

	@XmlElement(name = "user_token")
	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	
	@XmlElement(name = "reckoner_user_id")
	public String getReckonerUserId() {
		return reckonerUserId;
	}

	public void setReckonerUserId(String reckonerUserId) {
		this.reckonerUserId = reckonerUserId;
	}

	@XmlElement(name = "auth_provider")
	public ProviderEnum getAuthProvider() {
		return authProvider;
	}

	public void setAuthProvider(ProviderEnum authProvider) {
		this.authProvider = authProvider;
	}

	@XmlElement(name = "created_date")
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@XmlElement(name = "expiration_date")
	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
}
