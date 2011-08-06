package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import java.util.List;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class ReckoningServiceList extends ServiceResponse implements Serializable {
	
	private static final long serialVersionUID = 4832400854585916297L;
	
	@Column (name = "reckonings")
	private List<Reckoning> reckonings;

	@XmlElementWrapper (name = "reckonings")
	@XmlElement (name = "reckoning")
	public List<Reckoning> getReckonings() {
		return reckonings;
	}

	public void setReckonings(List<Reckoning> reckonings) {
		this.reckonings = reckonings;
	}

}
