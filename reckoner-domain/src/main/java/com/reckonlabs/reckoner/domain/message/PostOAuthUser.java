package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.user.ProviderEnum;

@XmlRootElement(name = "oauth_user_post")
public class PostOAuthUser implements Serializable {
	
	private String userToken;
	private String provider;
	private String expires;
	
	@XmlElement (name="user_token")
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	
	@XmlElement (name="provider")
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	@XmlElement (name="expires")
	public String getExpires() {
		return expires;
	}
	public void setExpires(String expires) {
		this.expires = expires;
	}
}
