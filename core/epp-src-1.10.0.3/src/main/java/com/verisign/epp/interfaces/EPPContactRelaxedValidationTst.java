/***********************************************************

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-0107  USA

 http://www.verisign.com/nds/naming/namestore/techdocs.html
 ***********************************************************/

package com.verisign.epp.interfaces;

import java.util.Random;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.contact.EPPContactAddress;
import com.verisign.epp.codec.contact.EPPContactDisclose;
import com.verisign.epp.codec.contact.EPPContactDiscloseAddress;
import com.verisign.epp.codec.contact.EPPContactDiscloseName;
import com.verisign.epp.codec.contact.EPPContactDiscloseOrg;
import com.verisign.epp.codec.contact.EPPContactInfoResp;
import com.verisign.epp.codec.contact.EPPContactPostalDefinition;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EPPEnv;
import com.verisign.epp.util.Environment;
import com.verisign.epp.util.TestThread;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Is a unit test of the <code>EPPContact</code> class when relaxed validation
 * is enabled when the EPP.Contact.RelaxedValidation property is set to
 * &quot;true&quot;. Only tests associated with relaxed validation are executed.
 * If the EPP.Contact.RelaxedValidation property is not set to &quot;true&quot;
 * a skipped message is written to standard out and the test will exit
 * successfully without executing any of the tests.
 */
public class EPPContactRelaxedValidationTst extends TestCase {
	/**
	 * Handle to the Singleton EPP Application instance (
	 * <code>EPPApplicationSingle</code>)
	 */
	private static EPPApplicationSingle	app				= EPPApplicationSingle.getInstance();

	/** Name of configuration file to use for test (default = epp.config). */
	private static String					configFileName	= "epp.config";

	/** Logging category */
	private static final Logger			cat				= Logger.getLogger(EPPContactRelaxedValidationTst.class.getName(),
	      EPPCatFactory.getInstance().getFactory());

	/** EPP Contact associated with test */
	private EPPContact						contact			= null;

	/** EPP Session associated with test */
	private EPPSession						session			= null;

	/** Current test iteration */
	private int									iteration		= 0;

	/** Need this for testing */
	private Random								rd					= new Random(System.currentTimeMillis());

	/**
	 * Allocates an <code>EPPContactTst</code> with a logical name. The
	 * constructor will initialize the base class <code>TestCase</code> with the
	 * logical name.
	 * 
	 * @param name
	 *           Logical name of the test
	 */
	public EPPContactRelaxedValidationTst(String name) {
		super(name);
	}

	/**
	 * JUNIT test method to implement the <code>EPPContactTst TestCase</code>.
	 * Each sub-test will be invoked in order to satisfy testing the EPPContact
	 * interface.
	 */
	public void testContact() {
		int numIterations = 1;
		String iterationsStr = System.getProperty("iterations");

		if (iterationsStr != null) {
			numIterations = Integer.parseInt(iterationsStr);
		}

		for (this.iteration = 0; (numIterations == 0) || (this.iteration < numIterations); this.iteration++) {
			printStart("Test Suite");

			contactCreateMinimal();
			contactCreatePartial();
			contactCreateWithAllAttributes();
			contactInfo("Minimal");
			contactInfo("Partial");
			contactInfo("Default");

			printEnd("Test Suite");
		}
	}

	/**
	 * Unit test of using <code>EPPContact.sendInfo</code> that takes a prefix to
	 * use in setting the contact id. The contact info command is sent and the
	 * contact info response is printed to standard out. When executing the info
	 * command against the EPP SDK Stub Server, the following prefixes are
	 * supported:<br>
	 * <ol>
	 * <li>Minimal - Will return the minimal amount in the info response that is
	 * not RFC compliant. The <code>EPP.Contact.RelaxedValidation</code> property
	 * must be set to true for the response to pass XML schema validation.
	 * <li>Partial - Will return partial information in the info response that is
	 * not RFC compliant. The <code>EPP.Contact.RelaxedValidation</code> property
	 * must be set to true for the response to pass XML schema validation.
	 * <li>Anything else (e.g. Default) - Will return the default information in
	 * the info response that is RFC compliant.
	 * </ol>
	 * 
	 * @param aPrefix
	 *           Prefix to use in generating the contact identifier
	 */
	public void contactInfo(String aPrefix) {
		printStart("contactInfo - " + aPrefix);

		EPPContactInfoResp response;

		String theName = this.makeContactId(aPrefix);

		try {
			System.out.println("\n----------------------------------------------------------------");
			System.out.println("\ncontactInfo - " + aPrefix + ": Contact info for " + theName);
			this.contact.setTransId("ABC-12345-XYZ");
			this.contact.addContactId(theName);

			response = this.contact.sendInfo();

			// -- Output all of the response attributes
			System.out.println("contactInfo - " + aPrefix + ": Response = [" + response + "]\n\n");

			// -- Output required response attributes using accessors
			System.out.println("contactInfo - " + aPrefix + ": id = " + response.getId());

			Vector postalContacts = null;

			if (response.getPostalInfo().size() > 0) {
				postalContacts = response.getPostalInfo();

				for (int j = 0; j < postalContacts.size(); j++) {
					EPPContactPostalDefinition postalInfo = (EPPContactPostalDefinition) postalContacts.elementAt(j);

					// Type
					System.out.println("contactInfo - " + aPrefix + ":\t\tpostal info type = " + postalInfo.getType());

					// Name
					System.out.println("contactInfo - " + aPrefix + ":\t\tname = " + postalInfo.getName());

					// Organization
					System.out.println("contactInfo - " + aPrefix + ":\t\torganization = " + postalInfo.getOrg());

					// Has address?
					if (postalInfo.hasAddress()) {
						EPPContactAddress address = postalInfo.getAddress();

						if (address.hasStreets()) {
							for (int i = 0; i < address.getStreets().size(); i++) {
								System.out.println("contactInfo - " + aPrefix + ":\t\tstreet" + (i + 1) + " = "
								      + address.getStreets().elementAt(i));
							}
						}
						else {
							System.out.println("contactInfo - " + aPrefix + ":\t\tstreets = null");
						}

						// Address City
						System.out.println("contactInfo - " + aPrefix + ":\t\tcity = " + address.getCity());

						// Address State/Province
						System.out
						      .println("contactInfo - " + aPrefix + ":\t\tstate province = " + address.getStateProvince());

						// Address Postal Code
						System.out.println("contactInfo - " + aPrefix + ":\t\tpostal code = " + address.getPostalCode());

						// Address County
						System.out.println("contactInfo - " + aPrefix + ":\t\tcountry = " + address.getCountry());
					}
					else {
						System.out.println("contactInfo - " + aPrefix + ":\t\taddress = null");
					}

				}
			}

			// Contact E-mail
			System.out.println("contactInfo - " + aPrefix + ":\temail = " + response.getEmail());

			// Contact Voice
			System.out.println("contactInfo - " + aPrefix + ":\tvoice = " + response.getVoice());

			// Contact Voice Extension
			System.out.println("contactInfo - " + aPrefix + ":\tvoice ext = " + response.getVoiceExt());

			// Contact Fax
			System.out.println("contactInfo - " + aPrefix + ":\tfax = " + response.getFax());

			// Contact Fax Extension
			System.out.println("contactInfo - " + aPrefix + ":\tfax ext = " + response.getFaxExt());

			// Client Id
			System.out.println("contactInfo - " + aPrefix + ": client id = " + response.getClientId());

			// Created By
			System.out.println("contactInfo - " + aPrefix + ": created by = " + response.getCreatedBy());

			// Created Date
			System.out.println("contactInfo - " + aPrefix + ": create date = " + response.getCreatedDate());

			// Last Updated By
			System.out.println("contactInfo - " + aPrefix + ": last updated by = " + response.getLastUpdatedBy());

			// Last Updated Date
			System.out.println("contactInfo - " + aPrefix + ": last updated date = " + response.getLastUpdatedDate());

			// Last Transfer Date
			System.out.println("contactInfo - " + aPrefix + ": last transfer date = " + response.getLastTransferDate());

			// Authorization Id
			if (response.hasAuthInfo()) {
				System.out.println(
				      "contactInfo - " + aPrefix + ": authorization info = " + response.getAuthInfo().getPassword());
			}
			else {
				System.out.println("contactInfo - " + aPrefix + ": authorization info = null");
			}

			// Has disclose?
			if (response.hasDisclose()) {
				System.out.println("contactInfo - " + aPrefix + ": disclose info = " + response.getDisclose());
			}

		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("contactInfo - " + aPrefix);
	}

	/**
	 * Unit test of <code>EPPContact.sendCreate</code> with relaxed validations
	 * that has includes the minimal set of attributes needed to pass relaxed validation.  
	 * The following attributes are set for the contact:<br>
	 * <ol>
	 * <li>contact id</li>
	 * <li>auth info</li>
	 * <li>postal info type</li>
	 * </ol>
	 * <br>
	 * The following attributes that are required by RFC are not set:<br>
	 * <ol>
	 * <li>name</li>
	 * <li>street</li>
	 * <li>city</li>
	 * <li>country</li>
	 * <li>e-mail</li>
	 * </ol>
	 */
	public void contactCreateMinimal() {
		printStart("contactCreateMinimal");

		EPPResponse response;

		String theName = this.makeContactId();

		try {
			System.out.println("\n----------------------------------------------------------------");
			System.out.println("contactCreateMinimal: Contact create for " + theName);
			this.contact.setTransId("ABC-12345-XYZ");
			this.contact.setAuthorizationId("ClientXYZ");
			this.contact.addContactId(theName);

			EPPContactPostalDefinition postalInfo = new EPPContactPostalDefinition(EPPContactPostalDefinition.ATTR_TYPE_INT);

			this.contact.addPostalInfo(postalInfo);

			response = this.contact.sendCreate();

			// -- Output all of the response attributes
			System.out.println(String.format("contactCreateMinimal: Response = [%s]\n\n", response));
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("contactCreateMinimal");
	}

	/**
	 * Unit test of <code>EPPContact.sendCreate</code> with relaxed validations
	 * that has some of the mandatory fields defined by the RFC set.  The following 
	 * attributes are set for the contact:<br>
	 * <ol>
	 * <li>contact id</li>
	 * <li>auth info</li>
	 * <li>postal info type</li>
	 * <li>address country</li>
	 * <li>e-mail address</li>
	 * </ol>
	 * <br>
	 * The following attributes that are required by RFC are not set:<br>
	 * <ol>
	 * <li>name</li>
	 * <li>street</li>
	 * <li>city</li>
	 * </ol>
	 */
	public void contactCreatePartial() {
		printStart("contactCreatePartial");

		EPPResponse response;

		String theName = this.makeContactId();

		try {
			System.out.println("\n----------------------------------------------------------------");
			System.out.println("contactCreatePartial: Contact create for " + theName);
			this.contact.setTransId("ABC-12345-XYZ");
			this.contact.setAuthorizationId("ClientXYZ");
			this.contact.addContactId(theName);

			EPPContactPostalDefinition postalInfo = new EPPContactPostalDefinition(EPPContactPostalDefinition.ATTR_TYPE_INT);
			
			EPPContactAddress address = new EPPContactAddress();
			address.setCountry("US");
			
			postalInfo.setAddress(address);

			this.contact.addPostalInfo(postalInfo);
			this.contact.setEmail("jdoe@example.com");

			response = this.contact.sendCreate();

			// -- Output all of the response attributes
			System.out.println(String.format("contactCreatePartial: Response = [%s]\n\n", response));
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("contactCreatePartial");
	}

	/**
	 * Unit test of <code>EPPContact.sendCreate</code>.
	 */
	public void contactCreateWithAllAttributes() {
		printStart("contactCreateWithAllAttributes");

		EPPResponse response;

		String theName = this.makeContactId();

		try {
			System.out.println("\n----------------------------------------------------------------");
			System.out.println("contactCreateWithAllAttributes: Contact create for " + theName);
			contact.setTransId("ABC-12345-XYZ");
			contact.setAuthorizationId("ClientXYZ");
			contact.addContactId(theName);
			contact.setVoicePhone("+1.7035555555");
			contact.setVoiceExt("123");
			contact.setFaxNumber("+1.7035555556");
			contact.setFaxExt("456");
			contact.setEmail("jdoe@example.com");

			// Streets
			Vector streets = new Vector();
			streets.addElement("123 Example Dr.");
			streets.addElement("Suite 100");
			streets.addElement("This is third line");

			EPPContactAddress address = new EPPContactAddress();

			address.setStreets(streets);
			address.setCity("Dulles");
			address.setStateProvince("VA");
			address.setPostalCode("20166-6503");
			address.setCountry("US");

			EPPContactPostalDefinition name = new EPPContactPostalDefinition(EPPContactPostalDefinition.ATTR_TYPE_LOC);

			name.setName("John Doe");
			name.setOrg("Example Inc.");
			name.setAddress(address);

			contact.addPostalInfo(name);

			// this is not a valid Example but it will do
			EPPContactAddress Intaddress = new EPPContactAddress();

			Intaddress.setStreets(streets);
			Intaddress.setCity("Dulles");
			Intaddress.setStateProvince("VA");
			Intaddress.setPostalCode("20166-6503");
			Intaddress.setCountry("US");

			EPPContactPostalDefinition Intname = new EPPContactPostalDefinition(EPPContactPostalDefinition.ATTR_TYPE_INT);

			Intname.setName("John Doe");
			Intname.setOrg("Example Inc.");
			Intname.setAddress(Intaddress);

			contact.addPostalInfo(Intname);

			// disclose names
			Vector names = new Vector();

			// names.addElement(new
			// EPPContactDiscloseName(EPPContactDiscloseName.ATTR_TYPE_LOC));
			names.addElement(new EPPContactDiscloseName(EPPContactDiscloseName.ATTR_TYPE_INT));

			// disclose orgs
			Vector orgs = new Vector();
			orgs.addElement(new EPPContactDiscloseOrg(EPPContactDiscloseOrg.ATTR_TYPE_LOC));
			orgs.addElement(new EPPContactDiscloseOrg(EPPContactDiscloseOrg.ATTR_TYPE_INT));

			// disclose addresses
			Vector addresses = new Vector();
			addresses.addElement(new EPPContactDiscloseAddress(EPPContactDiscloseAddress.ATTR_TYPE_LOC));
			addresses.addElement(new EPPContactDiscloseAddress(EPPContactDiscloseAddress.ATTR_TYPE_INT));

			// disclose
			EPPContactDisclose disclose = new EPPContactDisclose();
			disclose.setFlag("0");
			disclose.setNames(names);
			disclose.setOrgs(orgs);
			disclose.setAddresses(addresses);
			disclose.setVoice("");
			disclose.setFax("");
			disclose.setEmail("");

			contact.setDisclose(disclose);

			response = contact.sendCreate();

			// -- Output all of the response attributes
			System.out.println("contactCreateWithAllAttributes: Response = [" + response + "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("contactCreateWithAllAttributes");
	}

	/**
	 * Unit test of <code>EPPSession.initSession</code>. The session attribute is
	 * initialized with the attributes defined in the EPP sample files.
	 */
	private void initSession() {
		printStart("initSession");

		// Set attributes for initSession
		this.session.setTransId("ABC-12345-XYZ");
		this.session.setVersion("1.0");
		this.session.setLang("en");

		// Initialize the session
		try {
			this.session.initSession();
		}
		catch (EPPCommandException e) {
			EPPResponse response = this.session.getResponse();

			// Is a server specified error?
			if ((response != null) && (!response.isSuccess())) {
				Assert.fail("Server Error : " + response);
			}
			else {
				e.printStackTrace();
				Assert.fail("initSession Error : " + e);
			}
		}

		printEnd("initSession");
	}

	/**
	 * Unit test of <code>EPPSession.endSession</code>. The session with the EPP
	 * Server will be terminated.
	 */
	private void endSession() {
		printStart("endSession");
		this.session.setTransId("ABC-12345-XYZ");

		// End the session
		try {
			this.session.endSession();
		}
		catch (EPPCommandException e) {
			EPPResponse response = this.session.getResponse();

			// Is a server specified error?
			if ((response != null) && (!response.isSuccess())) {
				Assert.fail("Server Error : " + response);
			}
			else {
				e.printStackTrace();
				Assert.fail("initSession Error : " + e);
			}
		}

		printEnd("endSession");
	}

	/**
	 * JUNIT <code>setUp</code> method, which sets the default client Id to
	 * "theRegistrar".
	 */
	protected void setUp() {
		try {
			String theSessionClassName = System.getProperty("EPP.SessionClass");

			if (theSessionClassName != null) {
				try {
					Class theSessionClass = Class.forName(theSessionClassName);

					if (!EPPSession.class.isAssignableFrom((theSessionClass))) {
						Assert.fail(theSessionClassName + " is not a subclass of EPPSession");
					}

					this.session = (EPPSession) theSessionClass.newInstance();
				}
				catch (Exception ex) {
					Assert.fail("Exception instantiating EPP.SessionClass value " + theSessionClassName + ": " + ex);
				}
			}
			else {
				this.session = new EPPSession();
			}

			this.session.setClientID(Environment.getProperty("EPP.Test.clientId", "ClientX"));
			this.session.setPassword(Environment.getProperty("EPP.Test.password", "foo-BAR2"));
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Error initializing the session: " + e);
		}

		initSession();

		this.contact = new EPPContact(this.session);
	}

	/**
	 * JUNIT <code>tearDown</code>, which currently does nothing.
	 */
	protected void tearDown() {
		endSession();
	}

	/**
	 * JUNIT <code>suite</code> static method, which returns the tests associated
	 * with <code>EPPContact</code>.
	 * 
	 * @return <code>Test</code> to run
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite(EPPContactRelaxedValidationTst.class);

		String theConfigFileName = System.getProperty("EPP.ConfigFile");

		if (theConfigFileName != null) {
			configFileName = theConfigFileName;
		}

		try {
			app.initialize(configFileName);
		}
		catch (EPPCommandException e) {
			e.printStackTrace();
			Assert.fail("Error initializing the EPP Application: " + e);
		}

		// Relaxed validation not enabled?
		if (!EPPEnv.isContactRelaxedValidation()) {
			// Skip tests
			System.out.println(EPPContactRelaxedValidationTst.class.getName()
			      + " skipped due to EPP.Contact.RelaxedValidation property != \"true\"");
			return new TestSuite();
		}

		return suite;
	}

	/**
	 * Handle an <code>EPPCommandException</code>, which can be either a server
	 * generated error or a general exception. If the exception was caused by a
	 * server error, "Server Error : &lt;Response XML&gt;" will be specified. If
	 * the exception was caused by a general algorithm error,
	 * "General Error : &lt;Exception Description&gt;" will be specified.
	 * 
	 * @param aException
	 *           Exception thrown during test
	 */
	public void handleException(EPPCommandException aException) {
		EPPResponse theResponse = null;
		if (aException instanceof EPPCommandException) {
			theResponse = ((EPPCommandException) aException).getResponse();
		}

		aException.printStackTrace();

		// Is a server specified error?
		if ((theResponse != null) && (!theResponse.isSuccess())) {
			Assert.fail("Server Error : " + theResponse);
		}

		else {
			Assert.fail("General Error : " + aException);
		}
	}

	/**
	 * Unit test main, which accepts the following system property options:<br>
	 * <ul>
	 * <li>iterations Number of unit test iterations to run</li>
	 * <li>validate Turn XML validation on (<code>true</code>) or off (
	 * <code>false</code>). If validate is not specified, validation will be off.
	 * </li>
	 * </ul>
	 * 
	 * @param args
	 *           Program arguments
	 */
	public static void main(String[] args) {
		// Override the default configuration file name?
		if (args.length > 0) {
			configFileName = args[0];
		}

		// Number of Threads
		int numThreads = 1;
		String threadsStr = System.getProperty("threads");

		if (threadsStr != null) {
			numThreads = Integer.parseInt(threadsStr);
		}

		// Run test suite in multiple threads?
		if (numThreads > 1) {
			// Spawn each thread passing in the Test Suite
			for (int i = 0; i < numThreads; i++) {
				TestThread thread = new TestThread("EPPSessionTst Thread " + i, EPPContactTst.suite());
				thread.start();
			}
		}
		else {
			junit.textui.TestRunner.run(EPPContactTst.suite());
		}

		try {
			app.endApplication();
		}
		catch (EPPCommandException e) {
			e.printStackTrace();
			Assert.fail("Error ending the EPP Application: " + e);
		}
	}

	/**
	 * This method tries to generate a unique String as contact Name depending on
	 * the prefix sent
	 * 
	 * @return String client contact id
	 */
	private String makeContactId(String aPrefix) {
		long tm = System.currentTimeMillis();
		if (aPrefix != null && !aPrefix.isEmpty()) {
			return new String(aPrefix + String.valueOf(tm + this.rd.nextInt(5)).substring(7));
		}
		return new String("Con" + String.valueOf(tm + this.rd.nextInt(5)).substring(7));
	}

	/**
	 * This method tries to generate a unique String as contact Name using
	 * default prefix
	 * 
	 * @return String client contact id
	 */
	private String makeContactId() {
		return makeContactId("");
	}

	/**
	 * Print the start of a test with the <code>Thread</code> name if the current
	 * thread is a <code>TestThread</code>.
	 * 
	 * @param aTest
	 *           name for the test
	 */
	private void printStart(String aTest) {
		if (Thread.currentThread() instanceof TestThread) {
			System.out.print(Thread.currentThread().getName() + ", iteration " + this.iteration + ": ");
			cat.info(Thread.currentThread().getName() + ", iteration " + this.iteration + ": " + aTest + " Start");
		}

		System.out.println("Start of " + aTest);
		System.out.println("****************************************************************\n");
	}

	/**
	 * Print the end of a test with the <code>Thread</code> name if the current
	 * thread is a <code>TestThread</code>.
	 * 
	 * @param aTest
	 *           name for the test
	 */
	private void printEnd(String aTest) {
		System.out.println("****************************************************************");

		if (Thread.currentThread() instanceof TestThread) {
			System.out.print(Thread.currentThread().getName() + ", iteration " + this.iteration + ": ");
			cat.info(Thread.currentThread().getName() + ", iteration " + this.iteration + ": " + aTest + " End");
		}

		System.out.println("End of " + aTest);
		System.out.println("\n");
	}

	/**
	 * Print message
	 * 
	 * @param aMsg
	 *           message to print
	 */
	private void printMsg(String aMsg) {
		if (Thread.currentThread() instanceof TestThread) {
			System.out.println(Thread.currentThread().getName() + ", iteration " + this.iteration + ": " + aMsg);
			cat.info(Thread.currentThread().getName() + ", iteration " + this.iteration + ": " + aMsg);
		}
		else {
			System.out.println(aMsg);
			cat.info(aMsg);
		}
	}

	/**
	 * Print error message
	 * 
	 * @param aMsg
	 *           errpr message to print
	 */
	private void printError(String aMsg) {
		if (Thread.currentThread() instanceof TestThread) {
			System.err.println(Thread.currentThread().getName() + ", iteration " + this.iteration + ": " + aMsg);
			cat.error(Thread.currentThread().getName() + ", iteration " + this.iteration + ": " + aMsg);
		}
		else {
			System.err.println(aMsg);
			cat.error(aMsg);
		}
	}

	private boolean runTestsForRelaxedValidations() {
		if (EPPEnv.isContactRelaxedValidation()) {
			return true;
		}
		return false;
	}
}

// End class EPPContactTst
