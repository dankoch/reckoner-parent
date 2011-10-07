package com.reckonlabs.reckoner.domain.notes;

import java.util.Date;
import java.util.List;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.Notable;
import com.reckonlabs.reckoner.domain.user.User;

@XmlRootElement(name = "comment")
public class Comment extends Notable implements Serializable{
	
	private static final long serialVersionUID = -5734637740785695328L;
	
	@Column (name="commentId")
	String commentId;
	@Column (name="comment")
	String comment;
	@Column (name="poster_id")
	String posterId;
	@Column (name="posting_date")
	Date postingDate;
	
	// Used to store more detailed user information for comments returned by the services.
	// Should NOT be stored in the database.
	@Transient
	User user;
	
	@XmlElement(name="comment_id")	
	public String getCommentId() {
		return commentId;
	}
	public void setCommentId(String id) {
		this.commentId = id;
	}
	@XmlElement(name="comment")
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@XmlElement(name="poster_id")
	public String getPosterId() {
		return posterId;
	}
	public void setPosterId(String posterId) {
		this.posterId = posterId;
	}
	
	@XmlElement(name="posting_date")
	public Date getPostingDate() {
		return postingDate;
	}
	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}
	
	@XmlElement(name="user")
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
