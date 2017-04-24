/***********************************************************
Copyright (C) 2004 VeriSign, Inc.

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

package com.verisign.epp.codec.resellerext;

//----------------------------------------------
//
// imports...
//
//----------------------------------------------

// JUNIT Imports
import java.util.Date;
import java.util.Vector;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.verisign.epp.codec.domain.EPPDomainCreateCmd;
import com.verisign.epp.codec.domain.EPPDomainInfoResp;
import com.verisign.epp.codec.domain.EPPDomainStatus;
import com.verisign.epp.codec.domain.EPPDomainUpdateCmd;
import com.verisign.epp.codec.gen.EPPAuthInfo;
import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPCodecTst;
import com.verisign.epp.codec.gen.EPPEncodeDecodeStats;
import com.verisign.epp.codec.gen.EPPFactory;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPResult;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.codec.resellerext.EPPResellerExtUpdate.Action;
import com.verisign.epp.util.TestThread;

/**
 * Is a unit test of the com.verisign.epp.codec.resellerext package. The unit
 * test will execute, gather statistics, and output the results of a test of
 * each com.verisign.epp.codec.resellerext package concrete extension
 * <code>EPPCodecComponent</code>'s.
 */
public class EPPResellerExtTst extends TestCase {
	/**
	 * Number of unit test iterations to run. This is set in
	 * <code>EPPCodecTst.main</code>
	 */
	static private long numIterations = 1;

	/**
	 * Creates a new EPPResellerExtTst object.
	 *
	 * @param name
	 *            Name of test
	 */
	public EPPResellerExtTst(String name) {
		super(name);
	}

	/**
	 * Unit test of using the <code>EPPResellerExtCreate</code> extension with a
	 * domain create command.
	 */
	public void testResellerExtCreate() {
		EPPCodecTst.printStart("testResellerExtCreate");

		// Generic objects
		EPPEncodeDecodeStats theStats;

		// -- Extend Domain Create Command
		EPPDomainCreateCmd theDomainCommand;
		theDomainCommand = new EPPDomainCreateCmd("123456",
				"reseller.example", new EPPAuthInfo("2fooBAR"));

		// Add Extension
		theDomainCommand.addExtension(new EPPResellerExtCreate("myreseller"));

		theStats = EPPCodecTst.testEncodeDecode(theDomainCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testResellerExtCreate");
	}

	/**
	 * Unit test of using the <code>EPPResellerInfData</code> extension with a
	 * domain info response. The tests include:<br>
	 * <ol>
	 * <li>TEST1 - Test with just the required reseller identifier</li>
	 * <li>TEST2 - Test with both the reseller identifier and the reseller name</li>
	 * </ol>
	 */
	public void testResellerExtInfData() {
		EPPCodecTst.printStart("testResellerExtInfData");

		// Generic objects
		EPPEncodeDecodeStats theStats;

		// -- TEST1 - Test with just the required reseller identifier
		Vector statuses = new Vector();
		statuses.addElement(new EPPDomainStatus(EPPDomainStatus.ELM_STATUS_OK));
		EPPTransId respTransId = new EPPTransId("ngcl-IvJjzMZc",
				"test142AWQONJZ");
		EPPResponse theResponse = new EPPDomainInfoResp(respTransId,
				"EXAMPLE1-REP", "reseller.example", "ClientX", statuses,
				"ClientY", new Date(), new EPPAuthInfo("2fooBAR"));
		theResponse.setResult(EPPResult.SUCCESS);

		// Add Extension
		theResponse.addExtension(new EPPResellerExtInfData("myreseller"));

		theStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(theStats);

		// -- TEST1 - Test with both the reseller identifier and the reseller
		// name
		theResponse = new EPPDomainInfoResp(respTransId, "EXAMPLE1-REP",
				"reseller.example", "ClientX", statuses, "ClientY", new Date(),
				new EPPAuthInfo("2fooBAR"));
		theResponse.setResult(EPPResult.SUCCESS);

		// Add Extension
		theResponse.addExtension(new EPPResellerExtInfData("myreseller",
				"example"));

		theStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testResellerExtInfData");
	}

	/**
	 * Unit test of using the <code>EPPResellerExtUpdate</code> extension with a
	 * domain update command. The tests include:<br>
	 * <ol>
	 * <li>TEST1 - Test adding a reseller identifier</li>
	 * <li>TEST2 - Test removing a reseller identifier</li>
	 * <li>TEST3 - Test changing a reseller identifier</li>
	 * </ol>
	 */
	public void testResellerExtUpdate() {
		EPPCodecTst.printStart("testResellerExtUpdate");

		// Generic objects
		EPPEncodeDecodeStats theStats;

		// -- TEST1 - Test adding a reseller identifier
		EPPDomainUpdateCmd theDomainCommand;
		theDomainCommand = new EPPDomainUpdateCmd("123456",
				"reseller1.example", null, null, null);

		// Add Extension
		theDomainCommand.addExtension(new EPPResellerExtUpdate(Action.ADD,
				"myreseller"));

		theStats = EPPCodecTst.testEncodeDecode(theDomainCommand);
		System.out.println(theStats);

		// -- TEST2 - Test removing a reseller identifier
		theDomainCommand = new EPPDomainUpdateCmd("123456",
				"reseller2.example", null, null, null);

		// Add Extension
		theDomainCommand.addExtension(new EPPResellerExtUpdate(Action.REM,
				"myreseller"));

		theStats = EPPCodecTst.testEncodeDecode(theDomainCommand);
		System.out.println(theStats);

		// -- TEST3 - Test changing a reseller identifier
		theDomainCommand = new EPPDomainUpdateCmd("123456",
				"reseller3.example", null, null, null);

		// Add Extension
		theDomainCommand.addExtension(new EPPResellerExtUpdate(Action.CHG,
				"myreseller"));

		theStats = EPPCodecTst.testEncodeDecode(theDomainCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testResellerExtUpdate");
	}

	/**
	 * JUNIT <code>setUp</code> method, which sets the default client Id to
	 * "theRegistrar" and initializes the <code>EPPNamestoreExtMapFactory</code>
	 * with the <code>EPPCodec</code>.
	 */
	protected void setUp() {
	}

	/**
	 * JUNIT <code>tearDown</code>, which currently does nothing.
	 */
	protected void tearDown() {
	}

	/**
	 * JUNIT <code>suite</code> static method, which returns the tests
	 * associated with <code>EPPResellerExtTst</code>.
	 *
	 * @return DOCUMENT ME!
	 */
	public static Test suite() {
		EPPCodecTst.initEnvironment();

		TestSuite suite = new TestSuite(EPPResellerExtTst.class);

		// iterations Property
		String numIterProp = System.getProperty("iterations");

		if (numIterProp != null) {
			numIterations = Integer.parseInt(numIterProp);
		}

		// Add the EPPNSProductExtFactory to the EPPCodec.
		try {
			EPPFactory.getInstance().addMapFactory(
					"com.verisign.epp.codec.host.EPPHostMapFactory");
			EPPFactory.getInstance().addMapFactory(
					"com.verisign.epp.codec.domain.EPPDomainMapFactory");
			EPPFactory.getInstance().addExtFactory(
					"com.verisign.epp.codec.resellerext.EPPResellerExtFactory");
		}
		catch (EPPCodecException e) {
			Assert.fail("EPPCodecException adding factories to EPPCodec: " + e);
		}

		return suite;
	}

	/**
	 * Unit test main, which accepts the following system property options:<br>
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
	 * @param args
	 *            DOCUMENT ME!
	 */
	public static void main(String[] args) {
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
				TestThread thread = new TestThread("EPPResellerExtTst Thread "
						+ i, EPPResellerExtTst.suite());
				thread.start();
			}
		}
		else { // Single threaded mode.
			junit.textui.TestRunner.run(EPPResellerExtTst.suite());
		}
	}

	/**
	 * Sets the number of iterations to run per test.
	 *
	 * @param aNumIterations
	 *            number of iterations to run per test
	 */
	public static void setNumIterations(long aNumIterations) {
		numIterations = aNumIterations;
	}

}
