package com.reckonlabs.reckoner.domain.notes;

import java.io.Serializable;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "favorite")
public class Favorite implements Serializable {
	
	private static final long serialVersionUID = 8130379128198955302L;
	
	@Column (name="user_id")
	String userId;
	@Column (name="favorite_date")
	String favoriteDate;
	
	@XmlElement (name="user_id")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@XmlElement (name="favorite_date")
	public String getFavoriteDate() {
		return favoriteDate;
	}
	public void setFavoriteDate(String favoriteDate) {
		this.favoriteDate = favoriteDate;
	}
}
