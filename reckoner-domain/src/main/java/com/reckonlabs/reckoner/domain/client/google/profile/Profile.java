package com.reckonlabs.reckoner.domain.client.google.profile;

import java.util.Date;
import java.util.List;

import java.io.Serializable;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class Profile implements Serializable{
	
	private static final long serialVersionUID = -4221596555043277010L;
	
	String kind;
	String id;
	String displayName;
	Name name;
	String nickname;
	Date published;
	Date updated;
	Date birthday;
	Date anniversary;
	String gender;
	String note;
	String preferredUsername;
	String utcOffset;
	String connected;
	String profileUrl;
	String thumbnailUrl;
	
	// Extended OpenSocial Fields
	String aboutMe;
	String bodyType;
	String currentLocation;
	String drinker;
	String ethnicity;
	String fashion;
	String happiestWhen;
	String humor;
	String livingArrangement;
	String lookingFor;
	String profileSong;
	String profileVideo;
	String relationshipStatus;
	String religion;
	String romance;
	String scaredOf;
	String sexualOrientation;
	String smoker;
	String status;
	
	// Plural Element Fields
	List<PluralSubfield> emails;
	List<PluralSubfield> urls;
 	List<PluralSubfield> phoneNumbers;
 	List<PluralSubfield> ims;
 	List<Photo> photos;
 	List<PluralSubfield> tags;
 	List<PluralSubfield> relationships;
 	
 	// Special Subelement Fields
 	List<Address> addresses;
 	List<Organization> organizations;
 	List<Account> accounts;
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public Name getName() {
		return name;
	}
	public void setName(Name name) {
		this.name = name;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Date getPublished() {
		return published;
	}
	public void setPublished(Date published) {
		this.published = published;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public Date getAnniversary() {
		return anniversary;
	}
	public void setAnniversary(Date anniversary) {
		this.anniversary = anniversary;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getPreferredUsername() {
		return preferredUsername;
	}
	public void setPreferredUsername(String preferredUsername) {
		this.preferredUsername = preferredUsername;
	}
	public String getUtcOffset() {
		return utcOffset;
	}
	public void setUtcOffset(String utcOffset) {
		this.utcOffset = utcOffset;
	}
	public String getConnected() {
		return connected;
	}
	public void setConnected(String connected) {
		this.connected = connected;
	}
	public String getProfileUrl() {
		return profileUrl;
	}
	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public String getAboutMe() {
		return aboutMe;
	}
	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}
	public String getBodyType() {
		return bodyType;
	}
	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}
	public String getCurrentLocation() {
		return currentLocation;
	}
	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}
	public String getDrinker() {
		return drinker;
	}
	public void setDrinker(String drinker) {
		this.drinker = drinker;
	}
	public String getEthnicity() {
		return ethnicity;
	}
	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}
	public String getFashion() {
		return fashion;
	}
	public void setFashion(String fashion) {
		this.fashion = fashion;
	}
	public String getHappiestWhen() {
		return happiestWhen;
	}
	public void setHappiestWhen(String happiestWhen) {
		this.happiestWhen = happiestWhen;
	}
	public String getHumor() {
		return humor;
	}
	public void setHumor(String humor) {
		this.humor = humor;
	}
	public String getLivingArrangement() {
		return livingArrangement;
	}
	public void setLivingArrangement(String livingArrangement) {
		this.livingArrangement = livingArrangement;
	}
	public String getLookingFor() {
		return lookingFor;
	}
	public void setLookingFor(String lookingFor) {
		this.lookingFor = lookingFor;
	}
	public String getProfileSong() {
		return profileSong;
	}
	public void setProfileSong(String profileSong) {
		this.profileSong = profileSong;
	}
	public String getProfileVideo() {
		return profileVideo;
	}
	public void setProfileVideo(String profileVideo) {
		this.profileVideo = profileVideo;
	}
	public String getRelationshipStatus() {
		return relationshipStatus;
	}
	public void setRelationshipStatus(String relationshipStatus) {
		this.relationshipStatus = relationshipStatus;
	}
	public String getReligion() {
		return religion;
	}
	public void setReligion(String religion) {
		this.religion = religion;
	}
	public String getRomance() {
		return romance;
	}
	public void setRomance(String romance) {
		this.romance = romance;
	}
	public String getScaredOf() {
		return scaredOf;
	}
	public void setScaredOf(String scaredOf) {
		this.scaredOf = scaredOf;
	}
	public String getSexualOrientation() {
		return sexualOrientation;
	}
	public void setSexualOrientation(String sexualOrientation) {
		this.sexualOrientation = sexualOrientation;
	}
	public String getSmoker() {
		return smoker;
	}
	public void setSmoker(String smoker) {
		this.smoker = smoker;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<PluralSubfield> getEmails() {
		return emails;
	}
	public void setEmails(List<PluralSubfield> emails) {
		this.emails = emails;
	}
	public List<PluralSubfield> getUrls() {
		return urls;
	}
	public void setUrls(List<PluralSubfield> urls) {
		this.urls = urls;
	}
	public List<PluralSubfield> getPhoneNumbers() {
		return phoneNumbers;
	}
	public void setPhoneNumbers(List<PluralSubfield> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
	public List<PluralSubfield> getIms() {
		return ims;
	}
	public void setIms(List<PluralSubfield> ims) {
		this.ims = ims;
	}
	public List<Photo> getPhotos() {
		return photos;
	}
	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}
	public List<PluralSubfield> getTags() {
		return tags;
	}
	public void setTags(List<PluralSubfield> tags) {
		this.tags = tags;
	}
	public List<PluralSubfield> getRelationships() {
		return relationships;
	}
	public void setRelationships(List<PluralSubfield> relationships) {
		this.relationships = relationships;
	}
	public List<Address> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
	public List<Organization> getOrganizations() {
		return organizations;
	}
	public void setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
	}
	public List<Account> getAccounts() {
		return accounts;
	}
	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

}
