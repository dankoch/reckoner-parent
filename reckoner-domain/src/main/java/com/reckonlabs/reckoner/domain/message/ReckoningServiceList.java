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

	public ReckoningServiceList() {
		setReckonings(null);
		setMessage(new Message());
		setSuccess(true);
	}
	
	public ReckoningServiceList(List<Reckoning> reckoning, Message message, boolean success) {
		setReckonings(reckoning);
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

}
