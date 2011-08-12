package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.notes.Comment;

@XmlRootElement(name = "comment_post")
public class PostComment implements Serializable {

	private static final long serialVersionUID = 7515605775041162483L;
	
	private Comment comment;
	private String userToken;
	
	@XmlElement (name="comment")
	public Comment getComment() {
		return comment;
	}
	public void setComment(Comment comment) {
		this.comment = comment;
	}
	
	@XmlElement (name="user_token")
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
}
