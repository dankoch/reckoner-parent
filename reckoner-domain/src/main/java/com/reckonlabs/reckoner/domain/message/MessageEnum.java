package com.reckonlabs.reckoner.domain.message;

public enum MessageEnum {

	R00_DEFAULT("R00"), R01_DEFAULT("R01"), R02_DEFAULT("R02"),
	
	R100_POST_RECKONING ("R100"), R101_POST_RECKONING ("R101"), R102_POST_RECKONING ("R102"), R103_POST_RECKONING ("R103"),
    R104_POST_RECKONING ("R104"),
	
	R200_GET_RECKONING ("R200"), R201_GET_RECKONING ("R201"), R202_GET_RECKONING ("R202"), R203_GET_RECKONING ("R203"),
	
	R300_APPROVE_RECKONING("R300"),
	
	R400_POST_COMMENT("R400"), R401_POST_COMMENT("R401"), R402_POST_COMMENT("R402"),
	
	R500_GET_COMMENT("R500"),
	
	R600_POST_VOTE("R600"), R601_POST_VOTE("R601"), R602_POST_VOTE("R602"),
	
	R700_AUTH_USER("R700"), R701_AUTH_USER("R701"), R702_AUTH_USER("R702"), R703_AUTH_USER("R703"), R704_AUTH_USER("R704"),
	R705_AUTH_USER("R705");
	
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
			case R02_DEFAULT:  return ("Invalid reckoning ID.  Reckoning ID must be supplied and be 24 characters.");
			
			case R100_POST_RECKONING:  return ("No question text in posted Reckoning.");
			case R101_POST_RECKONING:  return ("No answer text in posted Reckoning.");
			case R102_POST_RECKONING:  return ("Less than two answers attached to posted Reckoning.");
			case R103_POST_RECKONING:  return ("No submitter ID attached to associated Reckoning.");
			case R104_POST_RECKONING:  return ("Database write failure for posted reckoning.");
			
			case R200_GET_RECKONING:  return ("Error retrieving reckonings from database.");			
			case R201_GET_RECKONING:  return ("Page value for reckoning queries cannot be negative.");
			case R202_GET_RECKONING:  return ("Size value for reckoning queries cannot be less than one.");
			case R203_GET_RECKONING:  return ("Paged reckoning queries need both a valid page and size value.");
			
			case R300_APPROVE_RECKONING: return ("No reckoning of that ID is available to approve.");
			
			case R400_POST_COMMENT: return ("No text included with comment.");
			case R401_POST_COMMENT: return ("No user ID included with comment.");
			case R402_POST_COMMENT: return ("No reckoning associated with the provided ID.");
			
			case R500_GET_COMMENT: return ("Paged comment queries need both a valid page and size value.");
			
			case R600_POST_VOTE: return ("Attempted to vote for a non-existent reckoning/answer pairing.");	
			case R601_POST_VOTE: return ("This user has already voted for this reckoning.");
			case R602_POST_VOTE: return ("An anonymous user with the same IP and user agent voted for this reckoning recently.  Vote is in escrow.");
			
			case R700_AUTH_USER: return ("No provider specified for OAuth user.");
			case R701_AUTH_USER: return ("Invalid provider specified for OAuth user.");
			case R702_AUTH_USER: return ("OAuth provider did not recognize provided user token. Returned null user.");		
			case R703_AUTH_USER: return ("New user account created.");	
			case R704_AUTH_USER: return ("No user associated with the specified user token. Nothing returned.");	
			case R705_AUTH_USER: return ("Invalid expiration value provided.");	
			
			default:  return ("Message not found.");
		}
	}
}
