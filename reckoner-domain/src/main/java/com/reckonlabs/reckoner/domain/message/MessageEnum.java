package com.reckonlabs.reckoner.domain.message;

public enum MessageEnum {

	R00_DEFAULT("R00"), R01_DEFAULT("R01"), R02_DEFAULT("R02"),
	
	R100_POST_RECKONING ("R100"), R101_POST_RECKONING ("R101"), R102_POST_RECKONING ("R102"), R103_POST_RECKONING ("R103"),
    R104_POST_RECKONING ("R104"), R105_POST_RECKONING ("R105"), R106_POST_RECKONING ("R106"),
	
	R200_GET_RECKONING ("R200"), R201_GET_RECKONING ("R201"), R202_GET_RECKONING ("R202"), R203_GET_RECKONING ("R203"),
	R204_GET_RECKONING ("R204"), R205_GET_RECKONING ("R205"), R206_GET_RECKONING("R206"), R207_GET_RECKONING ("R207"),
	
	R300_APPROVE_RECKONING("R300"),
	
	R400_POST_COMMENT("R400"), R401_POST_COMMENT("R401"), R402_POST_COMMENT("R402"),
	
	R500_GET_COMMENT("R500"), R501_GET_COMMENT("R501"),
	
	R600_POST_VOTE("R600"), R601_POST_VOTE("R601"), R602_POST_VOTE("R602"),
	
	R700_AUTH_USER("R700"), R701_AUTH_USER("R701"), R702_AUTH_USER("R702"), R703_AUTH_USER("R703"), R704_AUTH_USER("R704"),
	R705_AUTH_USER("R705"), R706_AUTH_USER("R706"), R707_AUTH_USER("R707"), R708_AUTH_USER("R708"), R709_AUTH_USER("R709"),
	R710_AUTH_USER("R710"),
	
	R800_POST_NOTE("R800"), R801_POST_NOTE("R801"), R802_POST_NOTE("R802"), R803_POST_NOTE("R803"), R804_POST_NOTE("R804"),
	R805_POST_NOTE("R805"),
	
	R900_GET_NOTE("R900");
	
	private final String code;

	/**
	 * @return the message
	 */
	public String getCode() {
		return code;
	}

	MessageEnum(String code) {
		this.code = code;
	}	
	
	public static String getText(MessageEnum enumeration) {
		switch (enumeration) {
			case R00_DEFAULT:  return ("Success");
			case R01_DEFAULT:  return ("The service returned an error when processing this request.  Please try again later.");
			case R02_DEFAULT:  return ("Invalid reckoning/object ID.  Object IDs must be supplied and be 24 characters.");
			
			case R100_POST_RECKONING:  return ("No question text in posted Reckoning.");
			case R101_POST_RECKONING:  return ("No answer text in posted Reckoning.");
			case R102_POST_RECKONING:  return ("Less than two answers attached to posted Reckoning.");
			case R103_POST_RECKONING:  return ("No submitter ID attached to associated Reckoning.");
			case R104_POST_RECKONING:  return ("Database write failure for posted reckoning.");
			case R105_POST_RECKONING:  return ("Need to specify IDs for updated reckonings.");
			case R106_POST_RECKONING:  return ("ID not found for merged reckoning.");
			
			case R200_GET_RECKONING:  return ("Error retrieving reckonings from database.");			
			case R201_GET_RECKONING:  return ("Page value for reckoning queries cannot be negative.");
			case R202_GET_RECKONING:  return ("Size value for reckoning queries cannot be less than one.");
			case R203_GET_RECKONING:  return ("Paged reckoning queries need both a valid page and size value.");
			case R204_GET_RECKONING:  return ("You need to request either open or closed reckonings (or both).");
			case R205_GET_RECKONING:  return ("The 'Posted Before' date cannot be before the 'Posted After' date.");
			case R206_GET_RECKONING:  return ("The 'Closed Before' date cannot be before the 'Closed After' date.");
			case R207_GET_RECKONING:  return ("Invalid 'SortBy' value.");
			
			case R300_APPROVE_RECKONING: return ("No reckoning of that ID is available to approve.");
			
			case R400_POST_COMMENT: return ("No text included with comment.");
			case R401_POST_COMMENT: return ("No user ID included with comment.");
			case R402_POST_COMMENT: return ("No reckoning associated with the provided ID.");
			
			case R500_GET_COMMENT: return ("Paged comment queries need both a valid page and size value.");
			case R501_GET_COMMENT: return ("No comment found with specified ID.");			
			
			case R600_POST_VOTE: return ("Attempted to vote for a non-existent reckoning/answer pairing.");	
			case R601_POST_VOTE: return ("This user has already voted for this reckoning.");
			case R602_POST_VOTE: return ("An anonymous user with the same IP and user agent voted for this reckoning recently.  Vote is in escrow.");
			
			case R700_AUTH_USER: return ("No provider specified for OAuth user.");
			case R701_AUTH_USER: return ("Invalid provider specified for OAuth user.");
			case R702_AUTH_USER: return ("OAuth provider did not recognize provided user token. Returned null user.");		
			case R703_AUTH_USER: return ("New user account created.");	
			case R704_AUTH_USER: return ("No user associated with the specified information. Nothing returned.");	
			case R705_AUTH_USER: return ("Invalid expiration value provided.");
			case R706_AUTH_USER: return ("Session has expired.  Please log in again.");
			case R707_AUTH_USER: return ("This Google account is not a G+ enabled account.  Only G+ accounts (with a profile) are valid.");
			case R708_AUTH_USER: return ("Action needs to be specified when changing the permissions for this user account.");
			case R709_AUTH_USER: return ("User ID needs to be specified when changing this user account.");
			case R710_AUTH_USER: return ("No user found with the specified ID.");
			
			case R800_POST_NOTE: return ("No user ID specified as favoriting/flagging agent.");
			case R801_POST_NOTE: return ("Specified reckoning does not exist.");
			case R802_POST_NOTE: return ("Specified comment does not exist.");
			case R803_POST_NOTE: return ("Specified user does not exist.");		
			case R804_POST_NOTE: return ("Specified user has already favorited / flagged this reckoning.");
			case R805_POST_NOTE: return ("Specified user is trying to favorite their own comment/reckoning.");
			
			case R900_GET_NOTE: return ("Paged flag/favorite queries need both a valid page and size value.");
			
			default:  return ("Message not found.");
		}
	}
}
