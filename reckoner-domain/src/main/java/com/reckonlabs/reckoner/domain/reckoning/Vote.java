package com.reckonlabs.reckoner.domain.reckoning;

import java.util.Date;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.user.User;

@XmlRootElement(name = "vote")
public class Vote implements Serializable {

	private static final long serialVersionUID = 43147529695578005L;
	
	@Column
	private String id;
	@Column (name = "voter_id")
	private String voterId;
	@Column (name = "reckoning_id")
	private String reckoningId;
	@Column (name = "answer_index")
	private int answerIndex;
	@Column (name = "voting_date")
	private Date votingDate;
	@Column (name = "anonymous")
	private boolean anonymous;
	@Column (name = "ip")
	private String ip;
	@Column (name = "user_agent")
	private String userAgent;
	
	// Used to store complete commentary and posting user information for display purposes.
	// Do NOT store in the database;
	@Transient
	private User votingUser;
	
	public Vote() {
		
	}
	
	public Vote (String voterId, String reckoningId, int index) {
		setReckoningId(reckoningId);
		setAnswerIndex(index);
		setVoterId(voterId);
	}
	
	public Vote (String voterId, String reckoningId, int index, boolean anonymous) {
		setReckoningId(reckoningId);
		setAnswerIndex(index);
		setVoterId(voterId);
		setAnonymous(anonymous);
	}
	
	@XmlElement(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(name = "voter_id")
	public String getVoterId() {
		return voterId;
	}

	public void setVoterId(String voterId) {
		this.voterId = voterId;
	}
	
	@XmlElement(name = "reckoning_id")	
	public String getReckoningId() {
		return reckoningId;
	}

	public void setReckoningId(String reckoningId) {
		this.reckoningId = reckoningId;
	}

	@XmlElement(name = "answer_index")
	public int getAnswerIndex() {
		return answerIndex;
	}

	public void setAnswerIndex(int answerIndex) {
		this.answerIndex = answerIndex;
	}

	@XmlElement(name = "voter_date")
	public Date getVotingDate() {
		return votingDate;
	}

	public void setVotingDate(Date date) {
		this.votingDate = date;
	}
	@XmlElement(name = "anonymous")
	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	@XmlElement(name = "ip")
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	@XmlElement(name = "user_agent")
	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@XmlElement(name = "voting_user")
	public User getVotingUser() {
		return votingUser;
	}
	public void setVotingUser(User votingUser) {
		this.votingUser = votingUser;
	}
}
