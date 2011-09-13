package com.reckonlabs.reckoner.domain.client.google;

import java.util.Date;
import java.util.List;

import java.io.Serializable;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class GoogleTokenResponse implements Serializable{
	
	private static final long serialVersionUID = -5734637740785695328L;
	
	@Column (name="access_token")
	String accessToken;
	@Column (name="expires_in")
	String expiresIn;
	@Column (name="token_type")
	String tokenType;
	@Column (name="refresh_token")
	String refreshToken;
	
	@XmlElement(name="access_token")	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	@XmlElement(name="expires_in")	
	public String getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}
	
	@XmlElement(name="token_type")	
	public String getTokenType() {
		return tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	
	@XmlElement(name="refresh_token")	
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}
