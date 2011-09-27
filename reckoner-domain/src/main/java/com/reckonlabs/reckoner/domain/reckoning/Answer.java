package com.reckonlabs.reckoner.domain.reckoning;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "answer")
public class Answer implements Serializable {

	private static final long serialVersionUID = 2119107561861688434L;
	
	private int index;
	private String text;
	private String subtitle;
	private int voteTotal;
	private Map<String, Boolean> votes;
	
	public Answer() {
		
	}
	
	public Answer (String answer, String subtitle) {
		setText(answer);
		setSubtitle(subtitle);
	}
	
	@XmlElement(name = "index")
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

	@XmlElement(name = "text")
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@XmlElement(name = "subtitle")	
	public String getSubtitle() {
		return subtitle;
	}
	
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	@XmlElement(name = "vote_total")
	public int getVoteTotal() {
		return voteTotal;
	}

	public void setVoteTotal(int voteTotal) {
		this.voteTotal = voteTotal;
	}

	@XmlTransient
	public Map<String, Boolean> getVotes() {
		return votes;
	}

	public void setVotes(Map<String, Boolean> votes) {
		this.votes = votes;
	}
	
	public void addVote(String voterId) {
		if (this.votes == null) {
			this.votes = new HashMap<String, Boolean> ();
		}
		this.votes.put(voterId, true);

		incrementVoteTotal();
	}
	
	public void incrementVoteTotal() {
		this.voteTotal ++;
	}
}
