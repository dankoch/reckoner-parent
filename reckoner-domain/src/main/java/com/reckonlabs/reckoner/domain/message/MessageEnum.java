package com.reckonlabs.reckoner.domain.message;

public enum MessageEnum {

	R00_DEFAULT("R00"), R01_DEFAULT("R01"),
	
	R100_POST_RECKONING ("R100"), R101_POST_RECKONING ("R101"), R102_POST_RECKONING ("R102"), R103_POST_RECKONING ("R103"),
    R104_POST_RECKONING ("R104"),
	
	R200_GET_RECKONING ("R200"), R201_GET_RECKONING ("R201"), R202_GET_RECKONING ("R202"), R203_GET_RECKONING ("R203");
	
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
			
			case R100_POST_RECKONING:  return ("No question text in posted Reckoning.");
			case R101_POST_RECKONING:  return ("No answer text in posted Reckoning.");
			case R102_POST_RECKONING:  return ("Less than two answers attached to posted Reckoning.");
			case R103_POST_RECKONING:  return ("No submitter ID attached to associated Reckoning.");
			case R104_POST_RECKONING:  return ("Database write failure for posted reckoning.");
			
			case R200_GET_RECKONING:  return ("Error retrieving reckonings from database.");			
			case R201_GET_RECKONING:  return ("Page value for reckoning queries cannot be negative.");
			case R202_GET_RECKONING:  return ("Size value for reckoning queries cannot be less than one.");
			case R203_GET_RECKONING:  return ("Paged reckoning queries need both a valid page and size value.");
			
			default:  return ("Message not found.");
		}
	}
}
