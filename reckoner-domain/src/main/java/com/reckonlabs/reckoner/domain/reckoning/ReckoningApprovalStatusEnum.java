package com.reckonlabs.reckoner.domain.reckoning;

public enum ReckoningApprovalStatusEnum {

	// Various statuses for which reckoning can be queried.
	APPROVED("APPROVED"), REJECTED("REJECTED"), PENDING("PENDING"), 
	APPROVED_AND_PENDING("APPROVED_AND_PENDING"), ALL_STATUSES("ALL_STATUSES");
	
	private final String code;
	
	/**
	 * @return The associated code.
	 */
	public String getCode() {
		return code;
	}

	ReckoningApprovalStatusEnum(String code) {
		this.code = code;
	}
	
	public static ReckoningApprovalStatusEnum getStatusCode(boolean approved, boolean rejected) {
		if (!approved && !rejected) {
			return PENDING;
		} else if (rejected) {
			return REJECTED;
		} else if (approved) {
			return APPROVED;
		}
		
		return null;
	}
}
