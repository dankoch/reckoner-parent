package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import java.util.List;

import com.reckonlabs.reckoner.domain.notes.Comment;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "comment_service_list")
public class CommentServiceList extends ServiceResponse implements Serializable {
	
	private static final long serialVersionUID = 1454029733335837752L;
	
	@Column (name = "comments")
	private List<Comment> comments;

	public CommentServiceList() {
		setComments(null);
		setMessage(new Message());
		setSuccess(true);
	}
	
	public CommentServiceList(List<Comment> Comment, Message message, boolean success) {
		setComments(Comment);
		setMessage(message);
		setSuccess(success);
	}	
	
	@XmlElementWrapper (name = "comments")
	@XmlElement (name = "comment")
	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

}
