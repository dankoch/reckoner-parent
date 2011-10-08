package com.reckonlabs.reckoner.domain.reckoning;

public enum ReckoningTypeEnum {

	OPEN("OPEN"), CLOSED("CLOSED"), OPEN_AND_CLOSED("OPEN_AND_CLOSED");
	
	private final String code;
	
	/**
	 * @return The associated code.
	 */
	public String getCode() {
		return code;
	}

	ReckoningTypeEnum(String code) {
		this.code = code;
	}
	
	public static ReckoningTypeEnum getOpenCode(boolean open, boolean closed) {
		if (open && closed) {
			return OPEN_AND_CLOSED;
		} else if (open) {
			return OPEN;
		} else if (closed) {
			return CLOSED;
		}
		
		return null;
	}
}
