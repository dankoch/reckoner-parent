package com.reckonlabs.reckoner.domain.notes;

/*
 * NOTE:
 * This class is kept in sync with the MapReduce function used to aggregate tags in the Content
 * and Reckoner database collections.
 * 
 */

import java.util.Date;
import java.util.List;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tag")
public class Tag implements Serializable{
	
	@Id
	private String tag;
	@Column(name="count")
	private Integer count;
	@Column(name="last_date")
	private Date lastPostingDate;
	
	public Tag() {
		
	}
	
	public Tag(String tag, int count) {
		this.tag = tag;
		this.count = count;
	}
	
	@XmlElement(name = "tag")
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	@XmlElement(name = "count")
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	@XmlElement(name = "last_posting_date")
	public Date getLastPostingDate() {
		return lastPostingDate;
	}
	public void setLastPostingDate(Date lastPostingDate) {
		this.lastPostingDate = lastPostingDate;
	}
}
