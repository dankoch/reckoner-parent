package com.reckonlabs.reckoner.domain.message;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "message")
public class Message implements Serializable {

	private static final long serialVersionUID = -225295364526979257L;
	
	private MessageEnum message;
	private Object[] args;

	/**
	 * Default.
	 */
	public Message() {
		this.message = MessageEnum.R00_DEFAULT;
	}
	
	/**
	 * Default.
	 */
	public Message(MessageEnum message) {
		this.message = message;
	}

	/**
	 * 
	 * @param message
	 *            {@link MessageEnum}
	 * @param args
	 *            {@link Object} array
	 */
	public Message(MessageEnum message, Object... args) {
		this.message = message;
		this.args = args;
	}

	/**
	 * @return the message
	 */
	public MessageEnum getMessage() {
		return message;
	}

	/**
	 * @return the message code
	 */
	public String getCode() {
		String code = "";
		if (message != null) {
			code = message.getCode();
		}
		return code;
	}
	
	/**
	 * @return the message text
	 */	
	public String getMessageText() {
		return MessageEnum.getText(message);
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(MessageEnum message) {
		this.message = message;
	}

	/**
	 * @return the args
	 */
	public Object[] getArgs() {
		return args;
	}

	/**
	 * @param args
	 *            the args to set
	 */
	public void setArgs(Object... args) {
		this.args = args;
	}
}
