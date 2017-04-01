package com.sanyabane.message;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Deposit implements Serializable {

	public enum Type {
		/**
		 * Do vostrebovaniya
		 */
		ON_DEMAND,
		/**
		 * Srochniy
		 */
		URGENT,
		/**
		 * Raschetniy
		 */
		CALCULATED,
		/**
		 * Nakopitelniy
		 */
		CUMULATIVE,
		/**
		 * Sberegatelniy
		 */
		SAVINGS,
		/**
		 * Metalicheskiy
		 */
		METALLIC
	}

	// ================================================================================
	// Properties
	// ================================================================================

	private static String dateFormatRule = "MMMM d, yyyy";
	
	/**
	 * Bank name
	 */
	private String name;
	/**
	 * Country name
	 */
	private String country;
	/**
	 * Deposit type
	 */
	private Type type;
	/**
	 * Depositor name
	 */
	private String depositor;
	/**
	 * Account ID
	 */
	private int accountID;
	/**
	 * Deposit sum
	 */
	private double amountOnDeposit;
	/**
	 * Annual percentage
	 */
	private double profitability;
	/**
	 * Term of deposit
	 */
	private Date timeConstraints;
	
	// ================================================================================
	// Accessors
	// ================================================================================

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getDepositor() {
		return depositor;
	}

	public void setDepositor(String depositor) {
		this.depositor = depositor;
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	public double getAmountOnDeposit() {
		return amountOnDeposit;
	}

	public void setAmountOnDeposit(double amountOnDeposit) {
		this.amountOnDeposit = amountOnDeposit;
	}

	public double getProfitability() {
		return profitability;
	}

	public void setProfitability(double profitability) {
		this.profitability = profitability;
	}

	public Date getTimeConstraints() {
		return timeConstraints;
	}

	public void setTimeConstraints(Date timeConstraints) {
		this.timeConstraints = timeConstraints;
	}

	public void setTimeConstraints(String strTimeConstraints) {
		this.timeConstraints = convertStringToDate(strTimeConstraints, Deposit.getDateFormatRule());
	}
	public static String getDateFormatRule() {
		return dateFormatRule;
	}

	// ================================================================================
	// Constructors
	// ================================================================================

	public Deposit() {

	}

	public Deposit(String bankTitle, String country, Deposit.Type type, String depositor, int accountID,
			double amountOfDeposit, double profitability, Date timeConstraints) {
		this.name = bankTitle;
		this.country = country;
		this.type = type;
		this.depositor = depositor;
		this.accountID = accountID;
		this.amountOnDeposit = amountOfDeposit;
		this.profitability = profitability;
		this.timeConstraints = timeConstraints;
	}

	// ================================================================================
	// Methods
	// ================================================================================

	public static Date convertStringToDate(String dateString, String dateParseRule){
		DateFormat format = new SimpleDateFormat(dateParseRule, Locale.ENGLISH);
		try {
			return (format.parse(dateString));
		} catch (ParseException e) {
			return null;
		}
	}
	
	public String toString() {
		String result = new String();

		result += "accountID: " + accountID + '\n';
		result += "amountOnDeposit: " + amountOnDeposit + '\n';
		result += "country: " + country + '\n';
		result += "depositor: " + depositor + '\n';
		result += "name: " + name + '\n';
		result += "profitability: " + profitability + '\n';
		result += "timeConstraints: " + timeConstraints + '\n';
		result += "type: " + type + '\n';

		return result;
	}

}