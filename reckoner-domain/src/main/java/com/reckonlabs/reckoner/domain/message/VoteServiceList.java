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
	
	@Column (name = "count")
	private Long count;

	public VoteServiceList() {
		setVotes(null);
		setCount(null);
		setMessage(new Message());
		setSuccess(true);
	}
	
	public VoteServiceList(List<Vote> Vote, Message message, boolean success) {
		setVotes(Vote);
		setCount(null);
		setMessage(message);
		setSuccess(success);
	}	
	
	public VoteServiceList(List<Vote> Vote, Long count, Message message, boolean success) {
		setVotes(Vote);
		setCount(count);
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

	@XmlElement (name = "count")
	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
