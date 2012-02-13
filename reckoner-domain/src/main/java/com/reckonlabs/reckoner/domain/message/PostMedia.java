package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.media.Media;

@XmlRootElement(name = "media_post")
public class PostMedia implements Serializable {

	private static final long serialVersionUID = 7515605775041162483L;
	
	private Media media;
	private String sessionId;
	
	@XmlElement (name="media")
	public Media getMedia() {
		return media;
	}
	public void setMedia(Media media) {
		this.media = media;
	}
	
	@XmlElement (name="session_id")
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
