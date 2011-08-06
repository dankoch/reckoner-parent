package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

@XmlRootElement(name = "reckoner_post")
public class PostReckoning implements Serializable {

	private static final long serialVersionUID = -4218281053538996203L;
	
	private Reckoning reckoning;
	private String userToken;
	
	@XmlElement (name="reckoning")
	public Reckoning getReckoning() {
		return reckoning;
	}
	public void setReckoning(Reckoning reckoning) {
		this.reckoning = reckoning;
	}
	
	@XmlElement (name="user_token")
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
}
