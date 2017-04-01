package com.sanyabane.message;

import java.io.Serializable;

public class Message implements Serializable {

	public enum MessageType {
		ONLY_TEXT, ONE_DEPOSIT, MULTIPLE_DEPOSITS
	}

	// ================================================================================
	// Properties
	// ================================================================================

	private static final long serialVersionUID = 1L;
	private MessageType messageType;
	private String message;
	private Deposits deposits;
	private Deposit deposit;

	// ================================================================================
	// Accessors
	// ================================================================================

	public String getMessage() {
		return message;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public Deposit getDeposit() {
		return deposit;
	}

	public void setDeposit(Deposit deposit) {
		this.deposit = deposit;
	}

	public Deposits getDeposits() {
		return deposits;
	}

	public void setDeposits(Deposits listOfDeposits) {
		this.deposits = listOfDeposits;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	// ================================================================================
	// Constructors
	// ================================================================================

	public Message(String message) {
		this.message = message;
		messageType = MessageType.ONLY_TEXT;
	}

	public Message(Deposit deposit) {
		this.deposit = deposit;
		messageType = MessageType.ONE_DEPOSIT;
	}

	public Message(Deposits deposits) {
		this.deposits = deposits;
		messageType = MessageType.MULTIPLE_DEPOSITS;
	}

}
