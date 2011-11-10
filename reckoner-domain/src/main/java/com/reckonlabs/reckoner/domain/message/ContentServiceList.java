package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import java.util.List;

import com.reckonlabs.reckoner.domain.content.Content;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "content_service_list")
public class ContentServiceList extends ServiceResponse implements Serializable {
	
	private static final long serialVersionUID = 4832400854585916297L;
	
	@Column (name = "contents")
	private List<Content> contents;
	
	@Column (name = "count")
	private Long count;

	public ContentServiceList() {
		setContents(null);
		setCount(null);
		setMessage(new Message());
		setSuccess(true);
	}
	
	public ContentServiceList(List<Content> contents, Message message, boolean success) {
		setContents(contents);
		setCount(null);
		setMessage(message);
		setSuccess(success);	
	}	
	
	public ContentServiceList(List<Content> contents, Long count, Message message, boolean success) {
		setContents(contents);
		setCount(count);
		setMessage(message);
		setSuccess(success);		
	}
	
	@XmlElementWrapper (name = "contents")
	@XmlElement (name = "content")
	public List<Content> getContents() {
		return contents;
	}

	public void setContents(List<Content> contents) {
		this.contents = contents;
	}

	@XmlElement (name = "count")
	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
