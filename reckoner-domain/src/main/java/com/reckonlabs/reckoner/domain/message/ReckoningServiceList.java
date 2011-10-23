package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import java.util.List;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "reckoning_service_list")
public class ReckoningServiceList extends ServiceResponse implements Serializable {
	
	private static final long serialVersionUID = 4832400854585916297L;
	
	@Column (name = "reckonings")
	private List<Reckoning> reckonings;
	
	@Column (name = "count")
	private Long count;

	public ReckoningServiceList() {
		setReckonings(null);
		setCount(null);
		setMessage(new Message());
		setSuccess(true);
	}
	
	public ReckoningServiceList(List<Reckoning> reckoning, Message message, boolean success) {
		setReckonings(reckoning);
		setCount(null);
		setMessage(message);
		setSuccess(success);	
	}	
	
	public ReckoningServiceList(List<Reckoning> reckoning, Long count, Message message, boolean success) {
		setReckonings(reckoning);
		setCount(count);
		setMessage(message);
		setSuccess(success);		
	}
	
	@XmlElementWrapper (name = "reckonings")
	@XmlElement (name = "reckoning")
	public List<Reckoning> getReckonings() {
		return reckonings;
	}

	public void setReckonings(List<Reckoning> reckonings) {
		this.reckonings = reckonings;
	}

	@XmlElement (name = "count")
	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
