package com.sanyabane.server;

import java.io.*;
import java.net.*;

import org.xml.sax.SAXParseException;

import com.sanyabane.message.Deposits;
import com.sanyabane.message.Message;

public class App {

	private static final int PORT = 4444;

	public static void main(String[] args) throws IOException {
		System.out.println("Welcome to Server side.");

		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println("Couldn't listen to port: " + PORT + " =(");
			System.exit(-1);
		}

		Deposits deposits = new Deposits();

		System.out.println("Trying to deserialize deposits from file: " + Deposits.getXmlFileName() + " . . .");
		
		try{
			deposits.deserializeFromFile();
			System.out.println("Deserialized successfull.");
		} catch (FileNotFoundException ex){
			System.out.println("ERROR. Can't find file: " + Deposits.getXmlFileName());
			System.out.println("Server will create \"default\" deposits and serialize them. Creating . . .");
			deposits = Deposits.createListOfDepositesWithRandomData();
			System.out.println("\"Default\" deposits created successfull.");
			System.out.println("Trying to serialize deposits into: " + Deposits.getXmlFileName() + " . . .");
			deposits.serializeToFile();
			System.out.println("Deposits serialized to \"" + Deposits.getXmlFileName() + "\" successfull.");
			System.out.println("Trying to deserialize deposits from: " + Deposits.getXmlFileName() + " . . .");
			deposits = new Deposits();
			deposits.deserializeFromFile();
			System.out.println("Deserialized successfull.");
		}

		Socket socketFromClient = null;
		int newClientID = 1;

		while (true) {
			try {
				System.out.println("Waiting for a client...");
				socketFromClient = serverSocket.accept();
				System.out.println("Client connected. Assigned id: " + newClientID);

				new ServerThreadForClient(socketFromClient, newClientID++, deposits).start();
			} catch (IOException e) {
				System.out.println("Can't accept");
			}
		}
	}
}
