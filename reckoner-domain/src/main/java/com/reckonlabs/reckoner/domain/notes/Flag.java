package com.reckonlabs.reckoner.domain.notes;

import java.io.Serializable;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "flag")
public class Flag implements Serializable {
	
	private static final long serialVersionUID = -5472288977048272007L;
	
	@Column (name="user_id")
	String userId;
	@Column (name="flag_date")
	String flagDate;
	@Column (name="reason")
	String reason;
	
	@XmlElement (name="user_id")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@XmlElement (name="flag_date")
	public String getFlagDate() {
		return flagDate;
	}
	public void setFlagDate(String flagDate) {
		this.flagDate = flagDate;
	}
	
	@XmlElement (name="reason")
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}
