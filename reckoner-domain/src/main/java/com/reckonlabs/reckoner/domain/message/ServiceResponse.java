package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

@XmlRootElement(name = "service_response")
public class ServiceResponse implements Serializable {
	
	private static final long serialVersionUID = 2373432119295978898L;
	
	private Message message;
	private boolean success;
	
	public ServiceResponse () {
		setMessage(new Message());
		setSuccess(true);
	}
	
	public ServiceResponse (Message message, boolean success) {
		setMessage(message);
		setSuccess(success);
	}
	
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	
	@XmlElement (name = "message_description")
	public String getMessageDescription () {
		return message.getMessageText();
	}
	
	@XmlElement (name = "success")
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
