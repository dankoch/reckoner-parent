package com.reckonlabs.reckoner.domain.client.google.profile;

import java.util.Date;
import java.util.List;

import java.io.Serializable;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class ProfileParent implements Serializable {
	
	Profile data;

	public Profile getData() {
		return data;
	}

	public void setData(Profile data) {
		this.data = data;
	}

}
