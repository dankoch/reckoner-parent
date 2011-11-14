package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import java.util.List;

import com.reckonlabs.reckoner.domain.notes.Tag;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tag_service_list")
public class TagServiceList extends ServiceResponse implements Serializable {
	
	private static final long serialVersionUID = 4832400854585916297L;
	
	@Column (name = "tags")
	private List<Tag> tags;
	
	@Column (name = "count")
	private Long count;

	public TagServiceList() {
		setTags(null);
		setCount(null);
		setMessage(new Message());
		setSuccess(true);
	}
	
	public TagServiceList(List<Tag> tags, Message message, boolean success) {
		setTags(tags);
		setCount(null);
		setMessage(message);
		setSuccess(success);	
	}	
	
	public TagServiceList(List<Tag> tag, Long count, Message message, boolean success) {
		setTags(tag);
		setCount(count);
		setMessage(message);
		setSuccess(success);		
	}
	
	@XmlElementWrapper (name = "tags")
	@XmlElement (name = "tag")
	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	@XmlElement (name = "count")
	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
