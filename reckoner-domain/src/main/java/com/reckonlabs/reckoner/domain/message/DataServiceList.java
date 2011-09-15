package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import java.util.List;

import com.reckonlabs.reckoner.domain.reckoning.Vote;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

// Generic class for use of lists that don't have their own dedicated list type.
@XmlRootElement(name = "data_service_list")
public class DataServiceList<K> extends ServiceResponse implements Serializable {

	private static final long serialVersionUID = 3424809855325219110L;
	
	@Column (name = "data")
	private List<K> data;

	public DataServiceList() {
		setData(null);
		setMessage(new Message());
		setSuccess(true);
	}
	
	public DataServiceList(List<K> data, Message message, boolean success) {
		setData(data);
		setMessage(message);
		setSuccess(success);
	}
	
	@XmlElementWrapper(name="data")
	@XmlElement(name="item")
	public List<K> getData() {
		return data;
	}

	public void setData(List<K> data) {
		this.data = data;
	}
}
