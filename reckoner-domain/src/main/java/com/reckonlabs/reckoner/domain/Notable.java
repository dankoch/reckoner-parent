package com.reckonlabs.reckoner.domain;

import java.io.Serializable;
import java.util.List;
import java.util.LinkedList;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.reckonlabs.reckoner.domain.notes.Favorite;
import com.reckonlabs.reckoner.domain.notes.Flag;

public abstract class Notable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3249251435572616667L;
	@Column(name="flags")
	private List<Flag> flags;
	@Column(name="favorites")
	private List<Favorite> favorites;

	@XmlElementWrapper(name = "flags")
	@XmlElement(name = "flag")
	public List<Flag> getFlags() {
		return flags;
	}

	public void setFlags(List<Flag> flags) {
		this.flags = flags;
	}	
	
	public void addFlag (Flag flag) {
		if (this.flags == null) {
			this.flags = new LinkedList<Flag> ();
		}
		this.flags.add(flag);
	}
	
	@XmlElementWrapper(name = "favorites")
	@XmlElement(name = "favorite")
	public List<Favorite> getFavorites() {
		return favorites;
	}

	public void setFavorites(List<Favorite> favorites) {
		this.favorites = favorites;
	}
	
	public void addFavorite (Favorite favorite) {
		if (this.favorites == null) {
			this.favorites = new LinkedList<Favorite> ();
		}
		this.favorites.add(favorite);
	}
	
	// Responsible for finding the favorite that the given user has made for this particular notable object.
	public Favorite getFavoriteByUser (String userId) {
		if (getFavorites() != null) {
			for (Favorite favorite : getFavorites()) {
				if (favorite.getUserId().equalsIgnoreCase(userId)) {
					return favorite;
				}
			}
		}
		
		return null;
	}
	
	// Responsible for finding the flag that the given user has made for this particular notable object.
	public Flag getFlagByUser (String userId) {
		if (getFlags() != null) {
			for (Flag flag : getFlags()) {
				if (flag.getUserId().equalsIgnoreCase(userId)) {
					return flag;
				}
			}
		}

		return null;
	}
}
