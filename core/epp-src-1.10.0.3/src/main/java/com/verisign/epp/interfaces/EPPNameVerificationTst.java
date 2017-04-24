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
import com.verisign.epp.codec.gen.EPPResult;
import com.verisign.epp.codec.nv.EPPDomainNameVerification;
import com.verisign.epp.codec.nv.EPPNameVerificationCheckResp;
import com.verisign.epp.codec.nv.EPPNameVerificationCreateFailed;
import com.verisign.epp.codec.nv.EPPNameVerificationCreateResp;
import com.verisign.epp.codec.nv.EPPNameVerificationCreateSuccess;
import com.verisign.epp.codec.nv.EPPNameVerificationDocument;
import com.verisign.epp.codec.nv.EPPNameVerificationInfoCmd;
import com.verisign.epp.codec.nv.EPPNameVerificationInfoResp;
import com.verisign.epp.codec.nv.EPPRealNameVerification;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.Environment;
import com.verisign.epp.util.TestThread;

/**
 * Is a unit test of the <code>EPPNameVerification</code> class for checking for
 * the availability to create Name Verification (NV) objects, create Name
 * Verification (NV) objects, info Name Verification (NV) objects for input and
 * signed codes, and update the Name Verification (NV) object authorization
 * information. The configuration file used by the unit test defaults to
 * epp.config, but can be changed by passing the file path as the first command
 * line argument. The unit test can be run in multiple threads by setting the
 * "threads" system property. For example, the unit test can be run in 2 threads
 * with the configuration file ../../epp.config with the following command: <br>
 * <br>
 * java com.verisign.epp.interfaces.EPPNameVerificationTst -Dthreads=2
 * ../../epp.config <br>
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
public class EPPNameVerificationTst extends TestCase {
	/**
	 * Handle to the Singleton EPP Application instance (
	 * <code>EPPApplicationSingle</code>)
	 */
	private static EPPApplicationSingle app = EPPApplicationSingle
			.getInstance();

	/** Name of configuration file to use for test (default = epp.config). */
	private static String configFileName = "epp.config";

	/** Logging category */
	private static final Logger cat = Logger.getLogger(
			EPPNameVerificationTst.class.getName(), EPPCatFactory.getInstance()
					.getFactory());

	/** EPP Domain associated with test */
	private EPPNameVerification nameVerification = null;

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
	 * Allocates an <code>EPPIdnMapDomainTst</code> with a logical name. The
	 * constructor will initialize the base class <code>TestCase</code> with the
	 * logical name.
	 *
	 * @param name
	 *            Logical name of the test
	 */
	public EPPNameVerificationTst(String name) {
		super(name);
	}

	/**
	 * JUNIT test method to implement the
	 * <code>EPPNameVerification TestCase</code>. Each sub-test will be invoked
	 * in order to satisfy testing the EPPNameVerification interface.
	 */
	public void testNameVerification() {
		int numIterations = 1;

		String iterationsStr = System.getProperty("iterations");

		if (iterationsStr != null) {
			numIterations = Integer.parseInt(iterationsStr);
		}

		for (iteration = 0; (numIterations == 0) || (iteration < numIterations); iteration++) {
			printStart("Test Suite");

			nvCheck();

			nvInfo();

			nvCreate();

			nvUpdate();

			printEnd("Test Suite");
		}
	}

	/**
	 * Unit test of <code>EPPNameVerification.sendCheck()</code>.
	 */
	public void nvCheck() {
		printStart("nvCheck");

		EPPNameVerificationCheckResp response;

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out
					.println("nvCheck: Check command with three domain labels");

			this.nameVerification.setTransId("ABC-12345");
			this.nameVerification.addLabel("example1");
			this.nameVerification.addLabel("example2");
			this.nameVerification.addLabel("example3");

			response = this.nameVerification.sendCheck();

			// -- Output all of the response attributes
			System.out.println("nvCheck: Check Response = [" + response
					+ "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("nvCheck");
	}

	/**
	 * Unit test of <code>EPPNameVerification.sendInfo()</code>.
	 */
	public void nvInfo() {
		printStart("nvInfo");

		EPPNameVerificationInfoResp response;

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out.println("nvInfo: Info command for signed code");

			this.nameVerification.setTransId("ABC-12345");

			this.nameVerification
					.setInfoType(EPPNameVerificationInfoCmd.Type.SIGNED_CODE);
			this.nameVerification.setCode("1-abc123");

			response = this.nameVerification.sendInfo();

			// -- Output all of the response attributes
			System.out.println("nvInfo: Signed Code Type Response = ["
					+ response + "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out.println("nvInfo: Info command for input data (DNV)");

			this.nameVerification.setTransId("ABC-12345");

			this.nameVerification
					.setInfoType(EPPNameVerificationInfoCmd.Type.INPUT);
			this.nameVerification.setCode("1-abc123");

			response = this.nameVerification.sendInfo();

			// -- Output all of the response attributes
			System.out.println("nvInfo: DNV Input Type Response = [" + response
					+ "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out.println("nvInfo: Info command for input data (RNV)");

			this.nameVerification.setTransId("ABC-12345");

			this.nameVerification
					.setInfoType(EPPNameVerificationInfoCmd.Type.INPUT);
			this.nameVerification.setCode("abc-rnv123");

			response = this.nameVerification.sendInfo();

			// -- Output all of the response attributes
			System.out.println("nvInfo: RNV Input Type Response = [" + response
					+ "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("nvInfo");
	}

	/**
	 * Unit test of <code>EPPNameVerification.sendCreate()</code>.
	 */
	public void nvCreate() {
		printStart("nvCreate");

		EPPNameVerificationCreateResp response;

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out
					.println("nvCreate: Create Domain Name Verification (DNV) object");

			this.nameVerification.setTransId("ABC-12345");
			this.nameVerification.setDnv(new EPPDomainNameVerification(
					"example"));
			this.nameVerification.setAuthInfo("2fooBAR");

			response = this.nameVerification.sendCreate();

			// -- Output all of the response attributes
			System.out.println("nvCreate: DNV Create Response = [" + response
					+ "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out
					.println("nvCreate: Create pending Domain Name Verification (DNV) object");

			this.nameVerification.setTransId("ABC-12345");
			this.nameVerification.setDnv(new EPPDomainNameVerification(
					"PENDING"));
			this.nameVerification.setAuthInfo("2fooBAR");

			response = this.nameVerification.sendCreate();

			// -- Output all of the response attributes
			System.out.println("nvCreate: DNV Create Response = [" + response
					+ "]\n\n");

			Assert.assertTrue(response.getResult().getCode() == EPPResult.SUCCESS_PENDING);

			session.setPollOp(EPPSession.OP_REQ);
			EPPResponse pollResponse = session.sendPoll();

			System.out.println("nvCreate: 1 - Poll message = [" + pollResponse
					+ "]\n\n");

			if (pollResponse.getMsgQueueCount() > 0) {

				session.setPollOp(EPPSession.OP_ACK);
				session.setMsgID(pollResponse.getMsgQueue().getId());
				pollResponse = session.sendPoll();

				System.out.println("nvCreate: 2 - Poll message = ["
						+ pollResponse + "]\n\n");
			}

		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out
					.println("nvCreate: Failed Create Domain Name Verification (DNV) object");

			this.nameVerification.setTransId("ABC-12345");
			this.nameVerification
					.setDnv(new EPPDomainNameVerification("FAILED"));
			this.nameVerification.setAuthInfo("2fooBAR");

			response = this.nameVerification.sendCreate();

			// -- Output all of the response attributes
			System.out.println("nvCreate: DNV Failed Create Response = ["
					+ response + "]\n\n");

			Assert.assertTrue(response.getResult().getCode() == EPPResult.SUCCESS);

			Assert.assertTrue(response.getCreateResult() instanceof EPPNameVerificationCreateFailed);

		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out
					.println("nvCreate: Create reserved Domain Name Verification (DNV) object");

			this.nameVerification.setTransId("ABC-12345");
			this.nameVerification.setDnv(new EPPDomainNameVerification(
					"example", "1-abc123"));
			this.nameVerification.setAuthInfo("2fooBAR");

			response = this.nameVerification.sendCreate();

			// -- Output all of the response attributes
			System.out.println("nvCreate: DNV Create Response = [" + response
					+ "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out
					.println("nvCreate: Create Domain Name Verification (DNV) object that returns invalid code type");

			this.nameVerification.setTransId("ABC-12345");
			this.nameVerification.setDnv(new EPPDomainNameVerification(
					"invalid-type"));
			this.nameVerification.setAuthInfo("2fooBAR");

			response = this.nameVerification.sendCreate();

			Assert.assertTrue(response.getCreateResult() instanceof EPPNameVerificationCreateSuccess);

			Assert.assertEquals("invalid-type",
					((EPPNameVerificationCreateSuccess) response
							.getCreateResult()).getSignedCode().getCodeType());

			// -- Output all of the response attributes
			System.out.println("nvCreate: DNV \"invalid-type\" Create Response = [" + response
					+ "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out
					.println("nvCreate: Create Real Name Verification (RNV) object for person");

			this.nameVerification.setTransId("ABC-12345");
			this.nameVerification.setRnv(new EPPRealNameVerification(
					EPPRealNameVerification.Role.PERSON, "John Xie",
					"1234567890", EPPRealNameVerification.ProofType.POE,
					new EPPNameVerificationDocument(
							EPPNameVerificationDocument.FileType.JPG,
							"EABQRAQAAAAAAAAAAAAAAAAAAAAD")));
			this.nameVerification.setAuthInfo("2fooBAR");

			response = this.nameVerification.sendCreate();

			// -- Output all of the response attributes
			System.out.println("nvCreate: RNV Create Response = [" + response
					+ "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out
					.println("nvCreate: Create Real Name Verification (RNV) object for org");

			this.nameVerification.setTransId("ABC-12345");
			this.nameVerification.setRnv(new EPPRealNameVerification(
					EPPRealNameVerification.Role.ORG, "John Xie", "1234567890",
					EPPRealNameVerification.ProofType.POE,
					new EPPNameVerificationDocument(
							EPPNameVerificationDocument.FileType.JPG,
							"EABQRAQAAAAAAAAAAAAAAAAAAAAD")));
			this.nameVerification.setAuthInfo("2fooBAR");

			response = this.nameVerification.sendCreate();

			// -- Output all of the response attributes
			System.out.println("nvCreate: Response = [" + response + "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("nvCreate");
	}

	/**
	 * Unit test of <code>EPPNameVerification.sendUpdate()</code>.
	 */
	public void nvUpdate() {
		printStart("nvUpdate");

		EPPResponse response;

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out.println("nvUpdate: Update command with authInfo");

			this.nameVerification.setTransId("ABC-12345");
			this.nameVerification.setCode("1-abc123");
			this.nameVerification.setAuthInfo("2BARfoo");

			response = this.nameVerification.sendUpdate();

			// -- Output all of the response attributes
			System.out.println("nvUpdate: Update Response = [" + response
					+ "]\n\n");
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("nvUpdate");
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

		nameVerification = new EPPNameVerification(session);
	}

	/**
	 * JUNIT <code>tearDown</code>, which currently does nothing.
	 */
	protected void tearDown() {
		endSession();
	}

	/**
	 * JUNIT <code>suite</code> static method, which returns the tests
	 * associated with <code>EPPIdnMapDomainTst</code>.
	 *
	 * @return Test suite
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite(EPPNameVerificationTst.class);

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
						EPPNameVerificationTst.suite());

				thread.start();
			}
		}

		else { // Single threaded mode.
			junit.textui.TestRunner.run(EPPNameVerificationTst.suite());
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
