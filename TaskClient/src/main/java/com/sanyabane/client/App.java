package com.sanyabane.client;

import java.io.*;
import java.net.*;
import com.sanyabane.message.*;

public class App {

	private static final int PORT = 4444;

	public static void main(String[] args) throws IOException {

		System.out.println("Welcome to Client side");

		Socket fromserver = null;

		if (args.length != 1) {
			System.out.println("First argument should be IP of server.");
			System.exit(-1);
		}

		System.out.println("Connecting to " + args[0] + " on port: " + PORT + " ...");

		try {
			fromserver = new Socket(args[0], PORT);
		} catch (ConnectException ex) {
			System.out.println("Can't connect to server. ConnectException.");
			System.exit(-1);
		}

		BufferedReader inu = new BufferedReader(new InputStreamReader(System.in));
		ObjectOutputStream outputStream = new ObjectOutputStream(fromserver.getOutputStream());
		ObjectInputStream inputStream = new ObjectInputStream(fromserver.getInputStream());

		System.out.println("Connection to " + args[0] + " successfull.");
		System.out.println("Type command (\"help\" to get list of avaliable commands).\n");

		String strFromUser;
		while ((strFromUser = inu.readLine()) != null) {
			try {
			
				Message messageToServer = new Message(strFromUser);
				outputStream.writeObject(messageToServer);
				
				if (strFromUser.equalsIgnoreCase("exit")) {
					break;
				}

				Message msgFromServer = null;
				try {
					msgFromServer = (Message) inputStream.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					System.out.println("Can't read message from server.");
					continue;
				}

				switch (msgFromServer.getMessageType()) {
				case ONLY_TEXT:
					String messageFromServer = msgFromServer.getMessage();
					System.out.println(messageFromServer);
					break;

				case ONE_DEPOSIT:
					Deposit deposit = msgFromServer.getDeposit();
					System.out.println("Result of query: one deposit:");
					System.out.println(deposit.toString());
					break;

				case MULTIPLE_DEPOSITS:
					Deposits deposits = msgFromServer.getDeposits();
					System.out.println("Result of query: list of deposits (" + deposits.getListOfDeposites().size() + "):");
					for (Deposit dep : deposits.getListOfDeposites()) {
						System.out.println(dep.toString());
					}

					break;

				default:
					System.out.println("Incorrect msgFromServer.getMessageType()");
					break;
				}

				System.out.println();

				/*
				 * if (msgFromServer.isMessageOnlyText()) { String
				 * messageFromServer = msgFromServer.getMessage();
				 * System.out.println(messageFromServer); } else { Deposits
				 * deposits = msgFromServer.getListOfDeposits();
				 * System.out.println("We've got list of deposits:");
				 * for(Deposit dp : deposits.getListOfDeposites()){
				 * System.out.println(dp.toString()); }
				 * 
				 * System.out.println(); }
				 */

			} catch (SocketException ex) {
				System.out.println("SocketException. Perhaps server crash.");
			}
		}

		outputStream.close();
		inputStream.close();

		inu.close();
		fromserver.close();
	}

}
