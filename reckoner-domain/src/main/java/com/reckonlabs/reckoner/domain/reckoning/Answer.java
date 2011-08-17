package com.reckonlabs.reckoner.domain.reckoning;

import java.util.List;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "answer")
public class Answer implements Serializable {

	private static final long serialVersionUID = 2119107561861688434L;
	
	private int index;
	private String text;
	private String subtitle;
	private int voteTotal;
	private List<Vote> votes;
	
	public Answer() {
		
	}
	
	public Answer (String answer, String subtitle) {
		setText(answer);
		setSubtitle(subtitle);
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getSubtitle() {
		return subtitle;
	}
	
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public int getVoteTotal() {
		return voteTotal;
	}

	public void setVoteTotal(int voteTotal) {
		this.voteTotal = voteTotal;
	}

	public List<Vote> getVotes() {
		return votes;
	}

	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}
	
	public void incrementVoteTotal() {
		this.voteTotal ++;
	}
}
