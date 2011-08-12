package com.reckonlabs.reckoner.domain.notes;

import java.util.Date;
import java.util.List;

import java.io.Serializable;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "comment")
public class Comment implements Serializable{
	
	private static final long serialVersionUID = -5734637740785695328L;
	
	@Column (name="id")
	String id;
	@Column (name="comment")
	String comment;
	@Column (name="poster_id")
	String posterId;
	@Column (name="posting_date")
	Date postingDate;
	
	@Column (name="favorites")
	List<Favorite> favorites;
	@Column (name="flags")
	List<Flag> flags;
	
	@XmlElement(name="id")	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	
	@XmlElement(name="favorites")
	public List<Favorite> getFavorites() {
		return favorites;
	}
	public void setFavorites(List<Favorite> favorites) {
		this.favorites = favorites;
	}
	
	@XmlElement(name="flags")
	public List<Flag> getFlags() {
		return flags;
	}
	public void setFlags(List<Flag> flags) {
		this.flags = flags;
	}

}
