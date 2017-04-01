package com.sanyabane.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.sanyabane.message.*;
import com.sanyabane.message.Deposit.Type;
import com.sanyabane.message.Message.MessageType;

public class ServerThreadForClient extends Thread {
	protected Socket socket;
	protected int clientID;
	protected Deposits mainServerDeposits;

	public ServerThreadForClient(Socket clientSocket, int clientID, Deposits deposits) {
		this.socket = clientSocket;
		this.clientID = clientID;
		this.mainServerDeposits = deposits;
	}

	public void run() {

		ObjectInputStream inputStream = null;
		ObjectOutputStream outputStream = null;

		try {
			outputStream = new ObjectOutputStream(this.socket.getOutputStream());
			inputStream = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (true) {
			try {
				Message inputFromClient = null;
				
				try{
					inputFromClient = (Message) inputStream.readObject();
				} catch (EOFException ex){
					System.out.println("Client #" + clientID + " disconnected.");
					break;
				}
				
				if (inputFromClient.getMessageType() == MessageType.ONLY_TEXT) {
					String textMessageFromClient = inputFromClient.getMessage().trim().replaceAll("\\s+", " ");

					if (textMessageFromClient.isEmpty()) {
						outputStream.writeObject(new Message(""));
						continue;
					}

					String[] splitedTextMessageFromClient = textMessageFromClient.split("\\s");

					switch (splitedTextMessageFromClient[0]) {
					case "exit":
						socket.close();
						return;

					case "help":
						outputStream.writeObject(new Message(getListOfAvaliableCommandsForClient()));
						break;

					case "list":
						outputStream.reset();
						outputStream.writeObject(new Message(mainServerDeposits));
						break;

					case "sum":
						double sumOfAmountOfDeposits = 0;
						for (Deposit dp : mainServerDeposits.getListOfDeposites()) {
							sumOfAmountOfDeposits += dp.getAmountOnDeposit();
						}

						outputStream.writeObject(new Message("Sum of all deposits: " + sumOfAmountOfDeposits));
						break;

					case "types":
						StringBuilder resultTypes = new StringBuilder();
						for (Deposit.Type depType : Deposit.Type.values()) {
							resultTypes.append(depType.name() + '\n');
						}

						outputStream.writeObject(new Message("Types:\n" + resultTypes.toString()));
						break;

					case "count":
						outputStream.writeObject(
								new Message("Count of deposits: " + mainServerDeposits.getListOfDeposites().size()));
						break;

					case "add":
						String strIncorrectAddCommand = "Incorrect typing. Correct \"add\" command: "
								+ "\'add <bank name> <country> <type> <depositor> <account ID> <amount on deposit> <profitability> <time constraints>\'";

						if (splitedTextMessageFromClient.length == 11) {

							String bankName = splitedTextMessageFromClient[1].trim();
							String country = splitedTextMessageFromClient[2].trim();

							String strType = splitedTextMessageFromClient[3].trim().toUpperCase();
							Deposit.Type type;
							try {
								type = Deposit.Type.valueOf(strType);
							} catch (IllegalArgumentException ex) {
								StringBuilder possibleDepositTypes = new StringBuilder();
								for (Type possibleType : Deposit.Type.class.getEnumConstants()) {
									possibleDepositTypes.append(possibleType.toString() + '\n');
								}
								outputStream.writeObject(new Message(
										strIncorrectAddCommand + '\n' + "ERROR in <type>." + '\n' + "You have typed: "
												+ strType + '\n' + "Possible types:" + '\n' + possibleDepositTypes));
								break;
							}

							String depositor = splitedTextMessageFromClient[4].trim();
							Pattern pattern = Pattern.compile("\\s");
							Matcher matcher = pattern.matcher(depositor);
							if (matcher.find()) {
								outputStream.writeObject(new Message(strIncorrectAddCommand + '\n'
										+ "ERROR in <depositor>." + '\n' + "You have typed: " + depositor + '\n'
										+ " Shouldn't contain spaces."));
								break;
							}

							String strAccountID = splitedTextMessageFromClient[5].trim();
							int accountID;
							try {
								accountID = Integer.parseInt(strAccountID);
							} catch (NumberFormatException ex) {
								outputStream.writeObject(
										new Message(strIncorrectAddCommand + '\n' + "ERROR in <account ID>." + '\n'
												+ "You have typed: " + strAccountID + '\n' + "Should be integer."));
								break;
							}
							if (accountID < 0) {
								outputStream.writeObject(new Message(strIncorrectAddCommand + '\n'
										+ "ERROR in <account ID>." + '\n' + "Cannot be less then zero."));
								break;
							}

							String strAmountOfDeposit = splitedTextMessageFromClient[6].trim();
							double amountOfDeposit;
							try {
								amountOfDeposit = Double.parseDouble(strAmountOfDeposit);
							} catch (NumberFormatException ex) {
								outputStream.writeObject(new Message(strIncorrectAddCommand + '\n'
										+ "ERROR in <amount on deposit>." + '\n' + "You have typed: "
										+ strAmountOfDeposit + '\n' + " Should be float."));
								break;
							}
							if (amountOfDeposit <= 0) {
								outputStream.writeObject(
										new Message(strIncorrectAddCommand + '\n' + "ERROR in <amount on deposit>."
												+ '\n' + "Cannot be equal or less then zero."));
								break;
							}

							String strProfitability = splitedTextMessageFromClient[7].trim();
							double profitability;
							try {
								profitability = Double.parseDouble(strProfitability);
							} catch (NumberFormatException ex) {
								outputStream.writeObject(
										new Message(strIncorrectAddCommand + '\n' + "ERROR in <profitability>." + '\n'
												+ "You have typed: " + strProfitability + '\n' + "Should be float."));
								break;
							}
							if (profitability <= 0) {
								outputStream.writeObject(new Message(strIncorrectAddCommand + '\n'
										+ "ERROR in <profitability>." + '\n' + "Cannot be equal or less then zero."));
								break;
							}

							String strInputedDate = splitedTextMessageFromClient[8].trim() + ' '
									+ splitedTextMessageFromClient[9].trim() + ' '
									+ splitedTextMessageFromClient[10].trim();

							Date timeConstraints = Deposit.convertStringToDate(strInputedDate,
									Deposit.getDateFormatRule());
							if (timeConstraints == null) {
								outputStream.writeObject(new Message(strIncorrectAddCommand + '\n'
										+ "ERROR in <time constraints>." + '\n' + "You have typed: " + strInputedDate
										+ '\n' + "Should match pattern: " + Deposit.getDateFormatRule()));
								break;
							}

							boolean isSameDepositExist = false;
							for (Deposit d : mainServerDeposits.getListOfDeposites()) {
								if (d.getAccountID() == accountID) {
									isSameDepositExist = true;
									break;
								}
							}

							if (isSameDepositExist) {
								outputStream.writeObject(new Message(
										"There is already exist Deposit with typed <account id> (" + accountID + ")"));
								break;
							}

							Deposit dep = new Deposit(bankName, country, type, depositor, accountID, amountOfDeposit,
									profitability, timeConstraints);

							mainServerDeposits.addDepositToListAndSerializeToFile(dep);

							outputStream.writeObject(new Message("Success. Deposit created and added to list."));
						} else {
							outputStream.writeObject(new Message(strIncorrectAddCommand));
						}

						break;

					case "info":

						if (splitedTextMessageFromClient.length == 3) {

							switch (splitedTextMessageFromClient[1]) {

							case "account":
								int accountID;
								try {
									accountID = Integer.parseInt(splitedTextMessageFromClient[2]);
								} catch (NumberFormatException ex) {
									outputStream.writeObject(new Message("<account id> must be integer."));
									break;
								}

								Deposit depositByAccountID = getOneDepositByAccountID(accountID);

								if (depositByAccountID != null) {
									outputStream.writeObject(new Message(depositByAccountID));
								} else {
									outputStream.writeObject(
											new Message("Can't find deposit with specified <account id>."));
								}
								break;

							case "depositor":
								String depositor = splitedTextMessageFromClient[2];

								Deposits deposits = getMultipleDepositsByDepositorName(depositor);

								if (deposits.getListOfDeposites().size() > 0) {
									outputStream.reset();
									outputStream.writeObject(new Message(deposits));
								} else {
									outputStream.writeObject(
											new Message("Can't find deposits with specified <depositor>."));
								}

								break;
							}
						} else {
							outputStream.writeObject(new Message(
									"Incorrect using of command \"info\". Correct example: \'info depositor Ivanov.V.A.\' or \'info account 2\'"));
						}

						break;

					case "show":

						if (splitedTextMessageFromClient.length == 3) {

							switch (splitedTextMessageFromClient[1]) {

							case "type":
								String depositType = splitedTextMessageFromClient[2];
								Deposits deposits1 = getMultipleDepositsByDepositType(
										Deposit.Type.valueOf(depositType));
								if (deposits1.getListOfDeposites().size() > 0) {
									outputStream.reset();
									outputStream.writeObject(new Message(deposits1));
								} else {
									outputStream.writeObject(new Message("Can't find deposits with specified <type>."));
								}

								break;

							case "bank":
								String bankTitle = splitedTextMessageFromClient[2];
								Deposits deposits2 = getMultipleDepositsByBankTitle(bankTitle);
								if (deposits2.getListOfDeposites().size() > 0) {
									outputStream.reset();
									outputStream.writeObject(new Message(deposits2));
								} else {
									outputStream.writeObject(
											new Message("Can't find deposits with specified <bank title>."));
								}

								break;
							}
						} else {
							outputStream.writeObject(new Message(
									"Incorrect using of command \"show\". Correct example: \'show type ON_DEMAND\' or \'show bank Privat\'"));
						}

						break;

					case "delete":
						if (splitedTextMessageFromClient.length == 2) {
							int accountID;
							try {
								accountID = Integer.parseInt(splitedTextMessageFromClient[1]);
							} catch (NumberFormatException ex) {
								outputStream.writeObject(new Message("<account id> must be integer."));
								break;
							}

							boolean deleteResult = deleteOneDepositByAccountID(accountID);
							if (deleteResult) {
								outputStream.writeObject(new Message("Delete operation success."));
							} else {
								outputStream.writeObject(new Message("Delete operation error."));
							}
						} else {
							outputStream.writeObject(new Message(
									"Incorrect using of command \"delete\". Correct example: \'delete 3\'"));
							break;
						}

						break;

					default:
						sendToClientMessageWithUnknownCommand(outputStream);
						break;
					}
				} else {
					// throw new Exception("");
					System.out.println("Wut? Client can send to Server only text messages...");
				}
			} catch (SocketException ex) {
				System.out.println("SocketException. Client #" + clientID + " disconnected.");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				break;
			}
		}

		try {
			inputStream.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String getListOfAvaliableCommandsForClient() {
		String helpString = new String();
		helpString = "===================\n" + "Avaliable commands:\n" + "list\n" + "sum\n" + "count\n"
				+ "info account <account id>\n" + "info depositor <depositor>\n" + "show type <type>\n"
				+ "show bank <name>\n" + "add <deposit info>\n" + "delete <account id>\n" + "types\n"
				+ "===================";
		return helpString;
	}

	private Deposit getOneDepositByAccountID(int accountID) {
		for (Deposit dep : mainServerDeposits.getListOfDeposites()) {
			if (dep.getAccountID() == accountID) {
				return dep;
			}
		}

		return null;
	}

	private Deposits getMultipleDepositsByDepositorName(String depositor) {
		List<Deposit> depositList = new ArrayList<Deposit>();

		for (Deposit dep : mainServerDeposits.getListOfDeposites()) {
			if (dep.getDepositor().equalsIgnoreCase(depositor)) {
				depositList.add(dep);
			}
		}

		return new Deposits(depositList);
	}

	private Deposits getMultipleDepositsByDepositType(Deposit.Type depositType) {
		List<Deposit> depositList = new ArrayList<Deposit>();

		for (Deposit dep : mainServerDeposits.getListOfDeposites()) {
			if (dep.getType() == depositType) {
				depositList.add(dep);
			}
		}

		return new Deposits(depositList);
	}

	private Deposits getMultipleDepositsByBankTitle(String bankTitle) {
		List<Deposit> depositList = new ArrayList<Deposit>();

		for (Deposit dep : mainServerDeposits.getListOfDeposites()) {
			if (dep.getName().equalsIgnoreCase(bankTitle)) {
				depositList.add(dep);
			}
		}

		return new Deposits(depositList);
	}

	private boolean deleteOneDepositByAccountID(int accountID) {
		for (Iterator<Deposit> iter = mainServerDeposits.getListOfDeposites().listIterator(); iter.hasNext();) {
			Deposit dep = iter.next();
			if (dep.getAccountID() == accountID) {
				mainServerDeposits.removeDepositFromListAndSerializeToFile(dep);
				return true;
			}
		}

		return false;
	}

	private void sendToClientMessageWithUnknownCommand(ObjectOutputStream objOutStream) throws IOException {
		Message msgFromServerToClient = new Message("Unknown command. Type 'help' to get list of avaliable commands.");
		objOutStream.writeObject(msgFromServerToClient);
	}
}
