package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.notes.Favorite;

@XmlRootElement(name = "favorite_post")
public class PostFavorite implements Serializable {
	
	private Favorite favorite;
	private String sessionId;

	@XmlElement (name="favorite")	
	public Favorite getFavorite() {
		return favorite;
	}
	public void setFavorite(Favorite favorite) {
		this.favorite = favorite;
	}
	@XmlElement (name="session_id")
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
