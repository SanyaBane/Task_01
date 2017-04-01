package com.sanyabane.message;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class Deposits implements Serializable {

	// ================================================================================
	// Properties
	// ================================================================================

	@XmlElement(name = "deposit")
	private List<Deposit> listOfDeposites;
	private static String xmlFileFolderPath = "custom_files/";
	private static String xmlFileName = "deps.xml";

	// ================================================================================
	// Accessors
	// ================================================================================

	public synchronized List<Deposit> getListOfDeposites() {
		return listOfDeposites;
	}

	public synchronized void setListOfDeposites(List<Deposit> listOfDeposites) {
		this.listOfDeposites = listOfDeposites;
	}
	
	public static String getXmlFileName(){
		return xmlFileName;
	}

	// ================================================================================
	// Constructors
	// ================================================================================

	public Deposits() {
	}

	public Deposits(List<Deposit> listOfDeposites) {
		this.listOfDeposites = listOfDeposites;
	}

	// ================================================================================
	// Methods
	// ================================================================================

	public static Deposits createListOfDepositesWithRandomData() {
		List<Deposit> listOfDeposites = new ArrayList<Deposit>();

		Deposit d;

		int newAccountID = 1;

		Random randomAmountOfDeposit = new Random();

		d = new Deposit();
		d.setAccountID(newAccountID++);
		d.setAmountOnDeposit(randomAmountOfDeposit.nextInt((10000 - 5000) + 1) + 5000);
		d.setCountry("Ukraine");
		d.setDepositor("Voichuk.O.O.");
		d.setName("PrivatBank");
		d.setProfitability(1.25);
		d.setTimeConstraints("January 2, 2010");
		d.setType(Deposit.Type.CUMULATIVE);
		listOfDeposites.add(d);

		d = new Deposit();
		d.setAccountID(newAccountID++);
		d.setAmountOnDeposit(randomAmountOfDeposit.nextInt((10000 - 5000) + 1) + 5000);
		d.setCountry("Russia");
		d.setDepositor("Chaika.A.A.");
		d.setName("Sberbank");
		d.setProfitability(2);
		d.setTimeConstraints("July 12, 2017");
		d.setType(Deposit.Type.SAVINGS);
		listOfDeposites.add(d);

		d = new Deposit();
		d.setAccountID(newAccountID++);
		d.setAmountOnDeposit(randomAmountOfDeposit.nextInt((10000 - 5000) + 1) + 5000);
		d.setCountry("Ukraine");
		d.setDepositor("Mister.X.Y.");
		d.setName("Pozitif");
		d.setProfitability(1.2);
		d.setTimeConstraints("Jun 22, 2018");
		d.setType(Deposit.Type.METALLIC);
		listOfDeposites.add(d);

		d = new Deposit();
		d.setAccountID(newAccountID++);
		d.setAmountOnDeposit(randomAmountOfDeposit.nextInt((10000 - 5000) + 1) + 5000);
		d.setCountry("Russia");
		d.setDepositor("Chaika.A.A.");
		d.setName("Sberbank");
		d.setProfitability(2.5);
		d.setTimeConstraints("Jan 27, 2023");
		d.setType(Deposit.Type.URGENT);
		listOfDeposites.add(d);

		Deposits deposits = new Deposits(listOfDeposites);
		// deposits.setListOfDeposites();

		return deposits;
	}

	public synchronized void addDepositToListAndSerializeToFile(Deposit dep){
		getListOfDeposites().add(dep);
		serializeToFile();
	}
	
	public synchronized void removeDepositFromListAndSerializeToFile(Deposit dep){
		getListOfDeposites().remove(dep);
		serializeToFile();
	}
	
	public synchronized void serializeToFile() {

		StringWriter sw = new StringWriter();

		try {
			JAXBContext writeContext = JAXBContext.newInstance(Deposits.class);
			Marshaller marshaller = writeContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			marshaller.marshal(this, sw);
			sw.close();

			File parentFolder = new File(xmlFileFolderPath);
			parentFolder.mkdirs();
			File file = new File(parentFolder, xmlFileName);
			FileWriter fw = new FileWriter(file);
			fw.write(sw.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public synchronized void deserializeFromFile() throws IOException {
		StringBuilder fileContent = new StringBuilder();
		FileReader reader;
		
		File parentFolder = new File(xmlFileFolderPath);
		parentFolder.mkdirs();
		File file = new File(parentFolder, xmlFileName);
		reader = new FileReader(file);
		int c;
		while ((c = reader.read()) != -1) {
			fileContent.append((char) c);
		}

		Deposits readedDeposits = null;
		try {
			JAXBContext readContext = JAXBContext.newInstance(Deposits.class);
			Unmarshaller unmarshaller = readContext.createUnmarshaller();
			readedDeposits = (Deposits) unmarshaller.unmarshal(new StringReader(fileContent.toString()));
			// System.out.println("Deserialized:");
			// System.out.println(readedDeposits.getListOfDeposites());
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		this.setListOfDeposites(readedDeposits.getListOfDeposites());
	}

}
