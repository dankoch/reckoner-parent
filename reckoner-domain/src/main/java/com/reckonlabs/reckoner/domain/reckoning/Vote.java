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
	@Column (name = "voting_date")
	private Date votingDate;
	
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

	@XmlElement(name = "voter_date")
	public Date getVotingDate() {
		return votingDate;
	}

	public void setVotingDate(Date date) {
		this.votingDate = date;
	}
}
