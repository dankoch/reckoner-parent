package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.notes.Comment;

@XmlRootElement(name = "comment_post")
public class PostComment implements Serializable {

	private static final long serialVersionUID = 7515605775041162483L;
	
	private Comment comment;
	private String sessionId;
	
	@XmlElement (name="comment")
	public Comment getComment() {
		return comment;
	}
	public void setComment(Comment comment) {
		this.comment = comment;
	}
	
	@XmlElement (name="session_id")
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
