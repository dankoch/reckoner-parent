package com.reckonlabs.reckoner.domain.utility;

public class DBUpdateException extends Exception {
	
	private static final long serialVersionUID = 3663179847874514163L;
	private String message;

	public DBUpdateException() {
	}

	public DBUpdateException(String message) {
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}	

}
