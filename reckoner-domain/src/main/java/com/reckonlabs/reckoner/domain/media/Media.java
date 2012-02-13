package com.reckonlabs.reckoner.domain.media;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reckonlabs.reckoner.domain.Notable;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.notes.Favorite;
import com.reckonlabs.reckoner.domain.user.User;

@Entity
@XmlRootElement(name = "media")
public class Media extends Notable implements Serializable  {

	private static final Logger log = LoggerFactory
			.getLogger(Media.class);

	@Column(name="media_id")
	private String mediaId;
	
	@Column(name="media_type")
	private MediaTypeEnum mediaType;
	
	@Column(name="name")
	private String name;
	@Column(name="small_name")
	private String smallName;
	@Column(name="thumbnail_name")
	private String thumbnailName;
	@Column(name="full_name")
	private String fullName;
	
	@Column(name="url")
	private String url;
	@Column(name="small_url")
	private String smallUrl;
	@Column(name="thumbnail_url")
	private String thumbnailUrl;
	@Column(name="full_url")
	private String fullUrl;
	
	@Column(name="file_type")
	private String fileType;
	@Column(name="duration")
	private String duration;
	@Column(name="size")
	private String size;	

	@XmlElement(name = "media_id")
	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	@XmlElement(name = "media_type")
	public MediaTypeEnum getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaTypeEnum mediaType) {
		this.mediaType = mediaType;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "small_name")
	public String getSmallName() {
		return smallName;
	}
	public void setSmallName(String smallName) {
		this.smallName = smallName;
	}
	
	@XmlElement(name = "thumbnail_name")
	public String getThumbnailName() {
		return thumbnailName;
	}
	public void setThumbnailName(String thumbnailName) {
		this.thumbnailName = thumbnailName;
	}
	
	@XmlElement(name = "full_name")
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@XmlElement(name = "url")
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@XmlElement(name = "small_url")
	public String getSmallUrl() {
		return smallUrl;
	}
	public void setSmallUrl(String smallUrl) {
		this.smallUrl = smallUrl;
	}
	
	@XmlElement(name = "thumbnail_url")
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	
	@XmlElement(name = "full_url")
	public String getFullUrl() {
		return fullUrl;
	}
	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}

	@XmlElement(name = "file_type")
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	@XmlElement(name = "duration")
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	@XmlElement(name = "size")
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
}
