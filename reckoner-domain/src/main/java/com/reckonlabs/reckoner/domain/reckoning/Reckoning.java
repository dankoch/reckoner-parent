package com.reckonlabs.reckoner.domain.reckoning;

import java.util.Date;
import java.util.List;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.notes.Favorite;
import com.reckonlabs.reckoner.domain.notes.Flag;
import com.reckonlabs.reckoner.domain.utility.DateUtility;

@Entity
@XmlRootElement(name = "reckoning")
public class Reckoning implements Serializable {

	private static final long serialVersionUID = 9172808928286702823L;

	@Id
	private String id;
	
	@Column(name="question")
	private String question;
	@Column(name="answers")
	private List<Answer> answers;
	
	@Column(name="submitter_id")
	private String submitterId;
	@Column(name="approver_id")
	private String approverId;

	@Column(name="approved")
	private boolean approved;
	@Column(name="rejected")
	private boolean rejected;
	@Transient
	private boolean open;
	
	@Column(name="anonymous_requested")
	private boolean anonymousRequested;
	@Column(name="anonymous")
	private boolean anonymous;
	
	@Column(name="submission_date")
	private Date submissionDate;
	@Column(name="posting_date")
	private Date postingDate;
	@Column(name="closing_date")
	private Date closingDate;
	@Column(name="interval")
	private Integer interval;
	
	@Column(name="comments")
	private List<Comment> comments;
	@Column(name="comment_index")
	private int commentIndex;
	
	@Column(name="flags")
	private List<Flag> flags;
	@Column(name="favorites")
	private List<Favorite> favorites;
	
	@Column(name="tags")
	private List<String> tags;
	
	@Column(name="highlighted")
	private boolean highlighted;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(name = "question")
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	@XmlElementWrapper(name = "answers")
	@XmlElement(name = "answer")
	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	@XmlElement(name = "submitter_id")
	public String getSubmitterId() {
		return submitterId;
	}

	public void setSubmitterId(String submitterId) {
		this.submitterId = submitterId;
	}

	@XmlElement(name = "approver_id")
	public String getApproverId() {
		return approverId;
	}

	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}

	@XmlElement(name = "approved")
	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}
	
	@XmlElement(name = "rejected")
	public boolean isRejected() {
		return rejected;
	}

	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}
	
	@XmlElement(name = "open")
	public boolean isOpen() {
		if (isApproved() && getClosingDate() != null) {
			if (!DateUtility.isBeforeNow(getClosingDate())) {
				this.open = true;
				return this.open;
			} 
		}
		
		this.open = false;
		return this.open;
	}

	@XmlElement(name = "anonymous_requested")
	public boolean isAnonymousRequested() {
		return anonymousRequested;
	}

	public void setAnonymousRequested(boolean anonymousRequested) {
		this.anonymousRequested = anonymousRequested;
	}

	@XmlElement(name = "anonymous")
	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	@XmlElement(name = "submission_date")
	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	@XmlElement(name = "posting_date")
	public Date getPostingDate() {
		return postingDate;
	}

	public void setpostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}

	@XmlElement(name = "closing_date")
	public Date getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(Date closingDate) {
		this.closingDate = closingDate;
	}

	@XmlElement(name = "interval")
	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	@XmlElementWrapper(name = "comments")
	@XmlElement(name = "comment")
	public List<Comment> getComments() {
		return comments;
	}

	public void setCommentIndex(int commentIndex) {
		this.commentIndex = commentIndex;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@XmlElementWrapper(name = "flags")
	@XmlElement(name = "flag")
	public List<Flag> getFlags() {
		return flags;
	}

	public void setFlags(List<Flag> flags) {
		this.flags = flags;
	}

	@XmlElementWrapper(name = "favorites")
	@XmlElement(name = "favorite")
	public List<Favorite> getFavorites() {
		return favorites;
	}

	public void setFavorites(List<Favorite> favorites) {
		this.favorites = favorites;
	}

	@XmlElementWrapper(name = "tags")
	@XmlElement(name = "tag")
	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@XmlElement(name = "highlighted")
	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}
}
