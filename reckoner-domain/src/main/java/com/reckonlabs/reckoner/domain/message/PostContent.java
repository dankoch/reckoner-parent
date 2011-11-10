package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.content.Content;

@XmlRootElement(name = "content_post")
public class PostContent implements Serializable {
	
	private static final long serialVersionUID = 7159842554577621005L;
	
	private Content content;
	private String sessionId;
	
	@XmlElement (name="content")
	public Content getContent() {
		return content;
	}
	public void setContent(Content content) {
		this.content = content;
	}
	
	@XmlElement (name="session_id")
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
