package com.reckonlabs.reckoner.domain.reckoning;

import java.util.Date;

import java.io.Serializable;
import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "vote")
public class Vote implements Serializable {

	private static final long serialVersionUID = 43147529695578005L;
	
	@Column (name = "voter_id")
	private String voterId;
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
	@Column (name = "latitude")
	private String latitude;
	@Column (name = "longitude")
	private String longitude;	
	
	public Vote() {
		
	}
	
	public Vote (String voterId, Date date) {
		setVotingDate(date);
		setVoterId(voterId);
	}

	@XmlElement(name = "voter_id")
	public String getVoterId() {
		return voterId;
	}

	public void setVoterId(String voterId) {
		this.voterId = voterId;
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
	@XmlElement(name = "latitude")
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	@XmlElement(name = "longitude")
	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}
