package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import java.util.List;

import com.reckonlabs.reckoner.domain.reckoning.Vote;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "vote_service_list")
public class VoteServiceList extends ServiceResponse implements Serializable {

	private static final long serialVersionUID = 2185964220174088946L;
	
	@Column (name = "votes")
	private List<Vote> votes;

	public VoteServiceList() {
		setVotes(null);
		setMessage(new Message());
		setSuccess(true);
	}
	
	public VoteServiceList(List<Vote> Vote, Message message, boolean success) {
		setVotes(Vote);
		setMessage(message);
		setSuccess(success);
	}	
	
	@XmlElementWrapper (name = "votes")
	@XmlElement (name = "vote")
	public List<Vote> getVotes() {
		return votes;
	}

	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}

}
