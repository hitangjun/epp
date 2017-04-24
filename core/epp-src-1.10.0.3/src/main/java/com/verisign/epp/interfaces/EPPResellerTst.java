/***********************************************************
Copyright (C) 2015 VeriSign, Inc.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

http://www.verisign.com/nds/naming/namestore/techdocs.html
 ***********************************************************/
package com.verisign.epp.interfaces;

import java.util.Random;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.reseller.EPPResellerAddress;
import com.verisign.epp.codec.reseller.EPPResellerCheckResp;
import com.verisign.epp.codec.reseller.EPPResellerContact;
import com.verisign.epp.codec.reseller.EPPResellerCreateResp;
import com.verisign.epp.codec.reseller.EPPResellerDisclose;
import com.verisign.epp.codec.reseller.EPPResellerInfoResp;
import com.verisign.epp.codec.reseller.EPPResellerPostalDefinition;
import com.verisign.epp.codec.reseller.State;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.Environment;
import com.verisign.epp.util.TestThread;

/**
 * Is a unit test of the <code>EPPReseller</code> class. The unit test will
 * initialize a session with an EPP Server, will invoke <code>EPPReseller</code>
 * operations, and will end a session with an EPP Server. The configuration file
 * used by the unit test defaults to epp.config, but can be changed by passing
 * the file path as the first command line argument. The unit test can be run in
 * multiple threads by setting the "threads" system property. For example, the
 * unit test can be run in 2 threads with the configuration file
 * ../../epp.config with the following command: <br>
 * <br>
 * java com.verisign.epp.interfaces.EPPResellerTst -Dthreads=2 ../../epp.config <br>
 * <br>
 * The unit test is dependent on the use of <a
 * href=http://www.mcwestcorp.com/Junit.html>JUNIT 3.5</a><br>
 * <br>
 * <br>
 * <br>
 *
 * @author $Author: jim $
 * @version $Revision: 1.3 $
 */
public class EPPResellerTst extends TestCase {
	/**
	 * Handle to the Singleton EPP Application instance (
	 * <code>EPPApplicationSingle</code>)
	 */
	private static EPPApplicationSingle app = EPPApplicationSingle
			.getInstance();

	/** Name of configuration file to use for test (default = epp.config). */
	private static String configFileName = "epp.config";

	/** Logging category */
	private static final Logger cat = Logger.getLogger(EPPResellerTst.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/** EPP Reseller associated with test */
	private EPPReseller reseller = null;

	/** EPP Session associated with test */
	private EPPSession session = null;

	/** Current test iteration */
	private int iteration = 0;

	/**
	 * Random instance for the generation of unique objects (hosts, IP
	 * addresses, etc.).
	 */
	private Random rd = new Random(System.currentTimeMillis());

	/**
	 * Allocates an <code>EPPResellerTst</code> with a logical name. The
	 * constructor will initialize the base class <code>TestCase</code> with the
	 * logical name.
	 *
	 * @param name
	 *            Logical name of the test
	 */
	public EPPResellerTst(String name) {
		super(name);
	}

	/**
	 * JUNIT test method to implement the <code>EPPResellerTst TestCase</code>.
	 * Each sub-test will be invoked in order to satisfy testing the EPPReseller
	 * interface.
	 */
	public void testReseller() {
		int numIterations = 1;

		String iterationsStr = System.getProperty("iterations");

		if (iterationsStr != null) {
			numIterations = Integer.parseInt(iterationsStr);
		}

		for (iteration = 0; (numIterations == 0) || (iteration < numIterations); iteration++) {
			printStart("Test Suite");

			resellerCheck();

			resellerInfo();

			resellerCreate();

			resellerDelete();
			
			resellerUpdate();
			
			printEnd("Test Suite");
		}
	}

	/**
	 * Unit test of <code>EPPReseller.sendCheck()</code> for a set of reseller
	 * identifiers.
	 */
	public void resellerCheck() {
		printStart("resellerCheck");

		EPPResellerCheckResp response;

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out
					.println("resellerCheck: Check res1523, re1523, and 1523res reseller identifiers.");

			this.reseller.setTransId("ABC-12345");

			this.reseller.addResellerId("res1523");

			this.reseller.addResellerId("re1523");

			this.reseller.addResellerId("1523res");

			response = this.reseller.sendCheck();

			// -- Output all of the response attributes
			System.out.println("resellerCheck: Reseller Check Response = ["
					+ response + "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("domainCheck");
	}

	/**
	 * Unit test of <code>EPPReseller.sendInfo()</code> to get the information
	 * for a specific reseller identifier.
	 */
	public void resellerInfo() {
		printStart("resellerInfo");

		EPPResellerInfoResp response;

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out.println("resellerInfo: Info res1523.");

			this.reseller.setTransId("ABC-12345");

			this.reseller.addResellerId("res1523");

			response = this.reseller.sendInfo();

			// -- Output all of the response attributes
			System.out.println("resellerInfo: Reseller Info Response = ["
					+ response + "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("resellerInfo");
	}

	/**
	 * Unit test of <code>EPPReseller.sendCreate()</code> to create a reseller.
	 */
	public void resellerCreate() {
		printStart("resellerCreate");

		EPPResellerCreateResp response;

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out.println("resellerCreate: Create res1523.");

			this.reseller.setTransId("ABC-12345");

			this.reseller.addResellerId("res1523");
			
			this.reseller.setState(State.OK);
			this.reseller.setParentId("1523res");
			
			EPPResellerAddress theAddress = new EPPResellerAddress();
			theAddress.setStreets("124 Example Dr.", "Suite 200");
			theAddress.setCity("Dulles");
			theAddress.setStateProvince("VA");
			theAddress.setPostalCode("20166-6503");
			theAddress.setCountry("US");
			
			this.reseller.addPostalInfo(new EPPResellerPostalDefinition(
					EPPResellerPostalDefinition.Type.INT, "Example Reseller Inc.", theAddress));
			this.reseller.setVoice("+1.7035555555");
			this.reseller.setVoiceExt("1234");
			this.reseller.setFax("+1.7035555556");
			this.reseller.setEmail("contact@reseller.example");
			this.reseller.setUrl("http://reseller.example");
			this.reseller.addContact(new EPPResellerContact("sh8013", EPPResellerContact.Type.ADMINISTRATIVE));
			this.reseller.addContact(new EPPResellerContact("sh8013", EPPResellerContact.Type.BILLING));
			
			EPPResellerDisclose disclose = new EPPResellerDisclose(false);
			disclose.setVoice(true);
			disclose.setEmail(true);
			
			this.reseller.setDisclose(disclose);
			

			response = this.reseller.sendCreate();

			// -- Output all of the response attributes
			System.out.println("resellerCreate: Reseller Create Response = ["
					+ response + "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("resellerCreate");
	}

	/**
	 * Unit test of <code>EPPReseller.sendDelete()</code> to delete a specific
	 * reseller identifier.
	 */
	public void resellerDelete() {
		printStart("resellerDelete");

		EPPResponse response;

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out.println("resellerDelete: Delete res1523.");

			this.reseller.setTransId("ABC-12345");

			this.reseller.addResellerId("res1523");

			response = this.reseller.sendDelete();

			// -- Output all of the response attributes
			System.out.println("resellerDelete: Reseller Delete Response = ["
					+ response + "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("resellerDelete");
	}

	/**
	 * Unit test of <code>EPPReseller.sendUpdate()</code> to create a reseller.
	 */
	public void resellerUpdate() {
		printStart("resellerUpdate");

		EPPResponse response;

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out.println("resellerUpdate: Update res1523.");

			this.reseller.setTransId("ABC-12345");

			this.reseller.addResellerId("res1523");
			
			this.reseller.setState(State.OK);
			this.reseller.setParentId("1523res");
			
			EPPResellerAddress theAddress = new EPPResellerAddress();
			theAddress.setStreets("124 Example Dr.", "Suite 200");
			theAddress.setCity("Dulles");
			theAddress.setStateProvince("VA");
			theAddress.setPostalCode("20166-6503");
			theAddress.setCountry("US");
			
			this.reseller.addPostalInfo(new EPPResellerPostalDefinition(
					EPPResellerPostalDefinition.Type.INT, "Example Reseller Inc.", theAddress));
			this.reseller.setVoice("+1.7035555555");
			this.reseller.setVoiceExt("1234");
			this.reseller.setFax("+1.7035555556");
			this.reseller.setEmail("contact@reseller.example");
			this.reseller.setUrl("http://reseller.example");
			this.reseller.addAddContact(new EPPResellerContact("sh8014", EPPResellerContact.Type.ADMINISTRATIVE));
			this.reseller.addRemContact(new EPPResellerContact("sh8013", EPPResellerContact.Type.BILLING));
			
			EPPResellerDisclose disclose = new EPPResellerDisclose(false);
			disclose.setVoice(true);
			disclose.setEmail(true);
			
			this.reseller.setDisclose(disclose);

			response = this.reseller.sendUpdate();

			// -- Output all of the response attributes
			System.out.println("resellerUpdate: Reseller Update Response = ["
					+ response + "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("resellerUpdate");
	}
	
	/**
	 * Unit test of <code>EPPSession.initSession</code>. The session attribute
	 * is initialized with the attributes defined in the EPP sample files.
	 */
	private void initSession() {
		printStart("initSession");

		// Set attributes for initSession
		session.setClientID(Environment.getProperty("EPP.Test.clientId",
				"ClientX"));
		session.setPassword(Environment.getProperty("EPP.Test.password",
				"foo-BAR2"));
		session.setTransId("ABC-12345-XYZ");
		session.setVersion("1.0");
		session.setLang("en");

		// Initialize the session
		try {
			session.initSession();
		}

		catch (EPPCommandException e) {
			EPPResponse response = session.getResponse();

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

		session.setTransId("ABC-12345-XYZ");

		// End the session
		try {
			session.endSession();
		}

		catch (EPPCommandException e) {
			EPPResponse response = session.getResponse();

			// Is a server specified error?
			if ((response != null) && (!response.isSuccess())) {
				Assert.fail("Server Error : " + response);
			}

			else // Other error
			{
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
						Assert.fail(theSessionClassName
								+ " is not a subclass of EPPSession");
					}

					session = (EPPSession) theSessionClass.newInstance();
				}
				catch (Exception ex) {
					Assert.fail("Exception instantiating EPP.SessionClass value "
							+ theSessionClassName + ": " + ex);
				}
			}
			else {
				session = new EPPSession();
			}

		}

		catch (Exception e) {
			e.printStackTrace();

			Assert.fail("Error initializing the session: " + e);
		}

		initSession();

		reseller = new EPPReseller(session);
	}

	/**
	 * JUNIT <code>tearDown</code>, which currently does nothing.
	 */
	protected void tearDown() {
		endSession();
	}

	/**
	 * JUNIT <code>suite</code> static method, which returns the tests
	 * associated with <code>EPPResellerTst</code>.
	 *
	 * @return Test suite
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite(EPPResellerTst.class);

		String theConfigFileName = System.getProperty("EPP.ConfigFile");
		if (theConfigFileName != null)
			configFileName = theConfigFileName;

		try {
			app.initialize(configFileName);
		}

		catch (EPPCommandException e) {
			e.printStackTrace();

			Assert.fail("Error initializing the EPP Application: " + e);
		}

		return suite;
	}

	/**
	 * Handle an <code>EPPCommandException</code>, which can be either a server
	 * generated error or a general exception. If the exception was caused by a
	 * server error, "Server Error :&lt;Response XML&gt;" will be specified. If
	 * the exception was caused by a general algorithm error, "General Error
	 * :&lt;Exception Description&gt;" will be specified.
	 *
	 * @param aException
	 *            Exception thrown during test
	 */
	public void handleException(Exception aException) {
		EPPResponse response = session.getResponse();

		aException.printStackTrace();

		// Is a server specified error?
		if ((response != null) && (!response.isSuccess())) {
			Assert.fail("Server Error : " + response);
		}

		else {
			Assert.fail("General Error : " + aException);
		}
	}

	/**
	 * Unit test main, which accepts the following system property options: <br>
	 * 
	 * <ul>
	 * <li>
	 * iterations Number of unit test iterations to run</li>
	 * <li>
	 * validate Turn XML validation on (<code>true</code>) or off (
	 * <code>false</code>). If validate is not specified, validation will be
	 * off.</li>
	 * </ul>
	 * 
	 *
	 * @param args
	 *            Command line arguments
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

			// Run test suite in multiple threads?
		}

		if (numThreads > 1) {
			// Spawn each thread passing in the Test Suite
			for (int i = 0; i < numThreads; i++) {
				TestThread thread = new TestThread("EPPSessionTst Thread " + i,
						EPPResellerTst.suite());

				thread.start();
			}
		}

		else { // Single threaded mode.
			junit.textui.TestRunner.run(EPPResellerTst.suite());
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
	 * Print the start of a test with the <code>Thread</code> name if the
	 * current thread is a <code>TestThread</code>.
	 *
	 * @param aTest
	 *            name for the test
	 */
	private void printStart(String aTest) {
		if (Thread.currentThread() instanceof TestThread) {
			System.out.print(Thread.currentThread().getName() + ", iteration "
					+ iteration + ": ");

			cat.info(Thread.currentThread().getName() + ", iteration "
					+ iteration + ": " + aTest + " Start");
		}

		System.out.println("Start of " + aTest);

		System.out
				.println("****************************************************************\n");
	}

	/**
	 * Print the end of a test with the <code>Thread</code> name if the current
	 * thread is a <code>TestThread</code>.
	 *
	 * @param aTest
	 *            name for the test
	 */
	private void printEnd(String aTest) {
		System.out
				.println("****************************************************************");

		if (Thread.currentThread() instanceof TestThread) {
			System.out.print(Thread.currentThread().getName() + ", iteration "
					+ iteration + ": ");

			cat.info(Thread.currentThread().getName() + ", iteration "
					+ iteration + ": " + aTest + " End");
		}

		System.out.println("End of " + aTest);

		System.out.println("\n");
	}

	/**
	 * Print message
	 *
	 * @param aMsg
	 *            message to print
	 */
	private void printMsg(String aMsg) {
		if (Thread.currentThread() instanceof TestThread) {
			System.out.println(Thread.currentThread().getName()
					+ ", iteration " + iteration + ": " + aMsg);

			cat.info(Thread.currentThread().getName() + ", iteration "
					+ iteration + ": " + aMsg);
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
	 *            errpr message to print
	 */
	private void printError(String aMsg) {
		if (Thread.currentThread() instanceof TestThread) {
			System.err.println(Thread.currentThread().getName()
					+ ", iteration " + iteration + ": " + aMsg);

			cat.error(Thread.currentThread().getName() + ", iteration "
					+ iteration + ": " + aMsg);
		}

		else {
			System.err.println(aMsg);

			cat.error(aMsg);
		}
	}
}
