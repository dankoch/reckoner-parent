package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

@XmlRootElement(name = "reckoning_post")
public class PostReckoning implements Serializable {

	private static final long serialVersionUID = -4218281053538996203L;
	
	private Reckoning reckoning;
	private String sessionId;
	
	@XmlElement (name="reckoning")
	public Reckoning getReckoning() {
		return reckoning;
	}
	public void setReckoning(Reckoning reckoning) {
		this.reckoning = reckoning;
	}
	
	@XmlElement (name="session_id")
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
