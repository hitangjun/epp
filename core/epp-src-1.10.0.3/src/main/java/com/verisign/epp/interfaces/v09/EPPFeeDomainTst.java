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
package com.verisign.epp.interfaces.v09;

// JUNIT Imports
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.Random;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.domain.EPPDomainCheckResp;
import com.verisign.epp.codec.domain.EPPDomainCreateResp;
import com.verisign.epp.codec.domain.EPPDomainRenewResp;
import com.verisign.epp.codec.domain.EPPDomainTransferResp;
import com.verisign.epp.codec.fee.v09.EPPFeeCheck;
import com.verisign.epp.codec.fee.v09.EPPFeeChkData;
import com.verisign.epp.codec.fee.v09.EPPFeeCommand;
import com.verisign.epp.codec.fee.v09.EPPFeeCreData;
import com.verisign.epp.codec.fee.v09.EPPFeeCreate;
import com.verisign.epp.codec.fee.v09.EPPFeeDelData;
import com.verisign.epp.codec.fee.v09.EPPFeeObject;
import com.verisign.epp.codec.fee.v09.EPPFeePeriod;
import com.verisign.epp.codec.fee.v09.EPPFeeRenData;
import com.verisign.epp.codec.fee.v09.EPPFeeRenew;
import com.verisign.epp.codec.fee.v09.EPPFeeTransfer;
import com.verisign.epp.codec.fee.v09.EPPFeeTrnData;
import com.verisign.epp.codec.fee.v09.EPPFeeUpdData;
import com.verisign.epp.codec.fee.v09.EPPFeeUpdate;
import com.verisign.epp.codec.fee.v09.EPPFeeValue;
import com.verisign.epp.codec.gen.EPPCommand;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.interfaces.EPPApplicationSingle;
import com.verisign.epp.interfaces.EPPCommandException;
import com.verisign.epp.interfaces.EPPDomain;
import com.verisign.epp.interfaces.EPPSession;
import com.verisign.epp.transport.EPPClientCon;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.Environment;
import com.verisign.epp.util.TestThread;

/**
 * Is a unit test of the <code>EPPDomain</code> class. The unit test will
 * initialize a session with an EPP Server, will invoke <code>EPPDomain</code>
 * operations, and will end a session with an EPP Server. The configuration file
 * used by the unit test defaults to epp.config, but can be changed by passing
 * the file path as the first command line argument. The unit test can be run in
 * multiple threads by setting the "threads" system property. For example, the
 * unit test can be run in 2 threads with the configuration file
 * ../../epp.config with the following command: <br>
 * <br>
 * java com.verisign.epp.interfaces.EPPFeeDomainTst -Dthreads=2 ../../epp.config <br>
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
public class EPPFeeDomainTst extends TestCase {
	/**
	 * Handle to the Singleton EPP Application instance (
	 * <code>EPPApplicationSingle</code>)
	 */
	private static EPPApplicationSingle app = EPPApplicationSingle
			.getInstance();

	/** Name of configuration file to use for test (default = epp.config). */
	private static String configFileName = "epp.config";

	/** Logging category */
	private static final Logger cat = Logger.getLogger(EPPFeeDomainTst.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/** EPP Domain associated with test */
	private EPPDomain domain = null;

	/** EPP Session associated with test */
	private EPPSession session = null;

	/** Connection to the EPP Server. */
	private EPPClientCon connection = null;

	/** Current test iteration */
	private int iteration = 0;

	/**
	 * Random instance for the generation of unique objects (hosts, IP
	 * addresses, etc.).
	 */
	private Random rd = new Random(System.currentTimeMillis());

	/**
	 * Allocates an <code>EPPFeeDomainTst</code> with a logical name. The
	 * constructor will initialize the base class <code>TestCase</code> with the
	 * logical name.
	 *
	 * @param name
	 *            Logical name of the test
	 */
	public EPPFeeDomainTst(String name) {
		super(name);
	}

	// End EPPFeeDomainTst(String)

	/**
	 * JUNIT test method to implement the <code>EPPFeeDomainTst TestCase</code>.
	 * Each sub-test will be invoked in order to satisfy testing the EPPDomain
	 * interface.
	 */
	public void testDomain() {
		int numIterations = 1;

		String iterationsStr = System.getProperty("iterations");

		if (iterationsStr != null) {
			numIterations = Integer.parseInt(iterationsStr);
		}

		for (iteration = 0; (numIterations == 0) || (iteration < numIterations); iteration++) {
			printStart("Test Suite");

			domainCheck();

			domainCreate();

			domainRenew();

			domainDelete();

			domainUpdate();

			domainTransfer();

			printEnd("Test Suite");
		}
	}

	/**
	 * Unit test of <code>EPPDomain.sendCheck</code> with fee extension.
	 */
	private void domainCheck() {
		printStart("domainCheck");

		EPPDomainCheckResp response;

		try {
			// Check domain names example.tld and example2.tld
			System.out
					.println("\n----------------------------------------------------------------");

			System.out
					.println("domainCheck: Check domain with fee check extension");

			domain.setTransId("ABC-12345");

			domain.addDomainName("example.com");
			domain.addDomainName("example.net");
			domain.addDomainName("example.org");

			// Add fee check extension
			EPPFeeCheck checkExt = new EPPFeeCheck();
			checkExt.addObject(new EPPFeeObject("example.com", "USD",
					new EPPFeeCommand("create", EPPFeeCommand.PHASE_SUNRISE),
					new EPPFeePeriod(1)));
			checkExt.addObject(new EPPFeeObject("example.net", "EUR",
					new EPPFeeCommand("create", EPPFeeCommand.PHASE_CLAIMS,
							EPPFeeCommand.PHASE_LANDRUSH), new EPPFeePeriod(2)));
			checkExt.addObject(new EPPFeeObject("example.org", "EUR",
					new EPPFeeCommand("transfer"), null));
			checkExt.addObject(new EPPFeeObject("example.xyz",
					new EPPFeeCommand("restore")));
			checkExt.addObject(new EPPFeeObject("default.tld",
					new EPPFeeCommand("create")));

			domain.addExtension(checkExt);

			response = domain.sendCheck();

			System.out.println("Response Type = " + response.getType());

			System.out.println("Response.TransId.ServerTransId = "
					+ response.getTransId().getServerTransId());

			System.out.println("Response.TransId.ServerTransId = "
					+ response.getTransId().getClientTransId());

			// Output all of the response attributes
			System.out.println("\ndomainCheck: Response = [" + response + "]");

			if (response.hasExtension(EPPFeeChkData.class)) {
				EPPFeeChkData theExt = (EPPFeeChkData) response
						.getExtension(EPPFeeChkData.class);
				System.out
						.println("domainCheck: Response fee data = " + theExt);
			}
			else {
				Assert.fail("domainCheck: Response did not contain fee extension");
			}

		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("domainCheck");
	}


	/**
	 * Unit test of <code>EPPDomain.sendCreate</code> with fee extension.
	 */
	public void domainCreate() {
		printStart("domainCreate");

		EPPDomainCreateResp response;

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out.println("domainCreate: Create domain with fee");

			domain.setTransId("ABC-12345-XYZ");

			domain.addDomainName("example.com");

			domain.setAuthString("ABC-12345");

			domain.addExtension(new EPPFeeCreate(new EPPFeeValue(
					new BigDecimal("5.00"))));

			response = domain.sendCreate();

			// -- Output all of the response attributes
			System.out.println("domainCreate: Response = [" + response
					+ "]\n\n");

			if (response.hasExtension(EPPFeeCreData.class)) {
				EPPFeeCreData theExt = (EPPFeeCreData) response
						.getExtension(EPPFeeCreData.class);
				System.out.println("domainCreate: Response fee data = "
						+ theExt);
			}
			else {
				Assert.fail("domainCreate: Response did not contain fee extension");
			}
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("domainCreate");
	}

	/**
	 * Unit test of <code>EPPDomain.sendDelete</code> with the fee extension
	 * expected in the response. The special domain
	 * &quot;delete-credit.com&quot; is used to trigger the Stub Server to
	 * include the fee extension in the response.
	 */
	public void domainDelete() {
		printStart("domainDelete");

		EPPResponse response;

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out
					.println("domainDelete: Delete domain \"delete-credit.com\" and expect response with fee extension");

			domain.setTransId("ABC-12345");

			domain.addDomainName("delete-credit.com");

			response = domain.sendDelete();

			// -- Output all of the response attributes
			System.out.println("domainDelete: Response = [" + response
					+ "]\n\n");

			if (response.hasExtension(EPPFeeDelData.class)) {
				EPPFeeDelData theExt = (EPPFeeDelData) response
						.getExtension(EPPFeeDelData.class);
				System.out.println("domainDelete: Response fee data = "
						+ theExt);
			}
			else {
				Assert.fail("domainDelete: Expected response with the fee extension.");
			}
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("domainDelete");
	}

	/**
	 * Unit test of <code>EPPDomain.sendRenew</code> with fee extension.
	 */
	public void domainRenew() {
		printStart("domainRenew");

		EPPDomainRenewResp response;

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out.println("domainRenew: Renew domain with fee");

			domain.setTransId("ABC-12345-XYZ");

			domain.addDomainName("example.com");
			domain.setExpirationDate(new GregorianCalendar(2000, 4, 3)
					.getTime());

			domain.addExtension(new EPPFeeRenew(new EPPFeeValue(new BigDecimal(
					"5.00"), "USD")));

			response = domain.sendRenew();

			// -- Output all of the response attributes
			System.out
					.println("domainRenew: Response = [" + response + "]\n\n");

			if (response.hasExtension(EPPFeeRenData.class)) {
				EPPFeeRenData theExt = (EPPFeeRenData) response
						.getExtension(EPPFeeRenData.class);
				System.out
						.println("domainRenew: Response fee data = " + theExt);
			}
			else {
				Assert.fail("domainRenew: Renew Response did not contain fee extension");
			}
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("domainRenew");
	}

	/**
	 * Unit test of <code>EPPDomain.sendUpdate</code> with fee extension.
	 */
	public void domainUpdate() {
		printStart("domainUpdate");

		EPPResponse response;

		try {
			System.out
					.println("\n----------------------------------------------------------------");

			System.out.println("domainUpdate: Update domain with fee");

			domain.setTransId("ABC-12345-XYZ");

			domain.addDomainName("example.com");
			domain.setRegistrant("sh8013");

			domain.addExtension(new EPPFeeUpdate(new EPPFeeValue(
					new BigDecimal("5.00"))));

			response = domain.sendUpdate();

			// -- Output all of the response attributes
			System.out.println("domainUpdate: Response = [" + response
					+ "]\n\n");

			if (response.hasExtension(EPPFeeUpdData.class)) {
				EPPFeeUpdData theExt = (EPPFeeUpdData) response
						.getExtension(EPPFeeUpdData.class);
				System.out.println("domainUpdate: Response fee data = "
						+ theExt);
			}
			else {
				Assert.fail("domainUpdate: Update Response did not contain fee extension");
			}
		}
		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("domainUpdate");
	}

	/**
	 * Unit test of <code>EPPDomain.sendTransfer</code> with fee extension.
	 */
	public void domainTransfer() {
		printStart("domainTransfer");

		EPPDomainTransferResp response;

		try {
			System.out
					.println("\ndomainTransfer: Domain transfer request of example.com with fee");

			domain.setTransferOpCode(EPPDomain.TRANSFER_REQUEST);

			domain.setTransId("ABC-12345");

			domain.setAuthString("2fooBAR");

			domain.addDomainName("example.com");

			domain.setPeriodLength(1);
			domain.setPeriodUnit(EPPDomain.PERIOD_YEAR);

			domain.addExtension(new EPPFeeTransfer(new EPPFeeValue(
					new BigDecimal("5.00"))));

			// Execute the transfer request
			response = domain.sendTransfer();

			// -- Output all of the response attributes
			System.out.println("domainTransfer: Response = [" + response
					+ "]\n\n");

			if (response.hasExtension(EPPFeeTrnData.class)) {
				EPPFeeTrnData theExt = (EPPFeeTrnData) response
						.getExtension(EPPFeeTrnData.class);
				System.out.println("domainTransfer: Response fee data = "
						+ theExt);
			}
			else {
				Assert.fail("domainTransfer: Transfer Reqeust Response did not contain fee extension");
			}

			System.out
					.println("\ndomainTransfer: Domain transfer query of fee-transfer-query.com");

			domain.setTransferOpCode(EPPCommand.OP_QUERY);

			domain.setTransId("ABC-12345");

			domain.addDomainName("fee-transfer-query.com");

			// Execute the transfer query
			response = domain.sendTransfer();

			// -- Output all of the response attributes
			System.out.println("domainTransfer: Response = [" + response
					+ "]\n\n");

			if (response.hasExtension(EPPFeeTrnData.class)) {
				EPPFeeTrnData theExt = (EPPFeeTrnData) response
						.getExtension(EPPFeeTrnData.class);
				System.out.println("domainTransfer: Response fee data = "
						+ theExt);
			}
			else {
				Assert.fail("domainTransfer: Transfer Query Response did not contain fee extension");
			}

		}

		catch (EPPCommandException e) {
			handleException(e);
		}

		printEnd("domainTransfer");
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

		// System.out.println("out init");
		domain = new EPPDomain(session);
	}

	/**
	 * JUNIT <code>tearDown</code>, which currently does nothing.
	 */
	protected void tearDown() {
		endSession();
	}

	/**
	 * JUNIT <code>suite</code> static method, which returns the tests
	 * associated with <code>EPPFeeDomainTst</code>.
	 *
	 * @return Test suite
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite(EPPFeeDomainTst.class);

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
						EPPFeeDomainTst.suite());

				thread.start();
			}
		}

		else { // Single threaded mode.
			junit.textui.TestRunner.run(EPPFeeDomainTst.suite());
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
	 * This method tries to generate a unique String as Domain Name and Name
	 * Server
	 *
	 * @return DOCUMENT ME!
	 */
	public String makeDomainName() {
		long tm = System.currentTimeMillis();

		return new String(Thread.currentThread()
				+ String.valueOf(tm + rd.nextInt(12)).substring(10) + ".com");
	}

	/**
	 * Makes a unique IP address based off of the current time.
	 *
	 * @return Unique IP address <code>String</code>
	 */
	public String makeIP() {
		long tm = System.currentTimeMillis();

		return new String(String.valueOf(tm + rd.nextInt(50)).substring(10)
				+ "." + String.valueOf(tm + rd.nextInt(50)).substring(10) + "."
				+ String.valueOf(tm + rd.nextInt(50)).substring(10) + "."
				+ String.valueOf(tm + rd.nextInt(50)).substring(10));
	}

	/**
	 * Makes a unique host name for a domain using the current time.
	 *
	 * @param newDomainName
	 *            DOCUMENT ME!
	 *
	 * @return Unique host name <code>String</code>
	 */
	public String makeHostName(String newDomainName) {
		long tm = System.currentTimeMillis();

		return new String(String.valueOf(tm + rd.nextInt(10)).substring(10)
				+ "." + newDomainName);
	}

	/**
	 * Makes a unique contact name using the current time.
	 *
	 * @return Unique contact name <code>String</code>
	 */
	public String makeContactName() {
		long tm = System.currentTimeMillis();

		return new String("Con"
				+ String.valueOf(tm + rd.nextInt(5)).substring(7));
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
