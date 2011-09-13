package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.reckoning.Vote;

@XmlRootElement(name = "vote_post")
public class PostVote implements Serializable {

	private static final long serialVersionUID = -4218281053538996203L;
	
	private Vote vote;
	private String sessionId;
	
	@XmlElement (name="vote")
	public Vote getVote() {
		return vote;
	}
	public void setVote(Vote vote) {
		this.vote = vote;
	}
	
	@XmlElement (name="session_id")
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
