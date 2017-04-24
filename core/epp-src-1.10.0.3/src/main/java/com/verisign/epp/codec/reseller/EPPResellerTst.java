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

package com.verisign.epp.codec.reseller;

import java.util.GregorianCalendar;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPCodecTst;
import com.verisign.epp.codec.gen.EPPEncodeDecodeStats;
import com.verisign.epp.codec.gen.EPPFactory;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.util.TestThread;

/**
 * Is a unit test of the com.verisign.epp.codec.reseller package.
 */
public class EPPResellerTst extends TestCase {
	/**
	 * Number of unit test iterations to run. This is set in
	 * <code>EPPCodecTst.main</code>
	 */
	static private long numIterations = 1;

	/**
	 * Creates a new EPPResellerTst object.
	 *
	 * @param name
	 *            Name of test
	 */
	public EPPResellerTst(String name) {
		super(name);
	}

	/**
	 * Unit test of the Reseller Check Command. The following are the tests:<br>
	 * <ol>
	 * <li>TEST1 - Check an individual reseller identifier.</li>
	 * <li>TEST2 - Check three reseller identifiers.</li>
	 * </ol>
	 */
	public void testCheckCommand() {
		EPPCodecTst.printStart("testCheckCommand");

		EPPEncodeDecodeStats theStats;

		// TEST1 - Check an individual reseller identifier.
		EPPResellerCheckCmd theCommand = new EPPResellerCheckCmd("ABC-12345",
				"res1523");

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		// TEST2 - Check three reseller identifiers.
		theCommand = new EPPResellerCheckCmd("ABC-12345");

		theCommand.addResellerId("res1523");
		theCommand.addResellerId("re1523");
		theCommand.addResellerId("1523res");

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testCheckCommand");
	}

	/**
	 * Unit test of the Reseller Check Response. The following are the tests:<br>
	 * <ol>
	 * <li>TEST1 - Check response with an individual result.</li>
	 * <li>TEST2 - Check response with three results.</li>
	 * </ol>
	 */
	public void testCheckResponse() {
		EPPCodecTst.printStart("testCheckResponse");

		EPPEncodeDecodeStats theStats;

		EPPTransId theTransId = new EPPTransId("ABC-12345", "54321-XYZ");

		// TEST1 - Check response with an individual result.
		EPPResellerCheckResp theResp = new EPPResellerCheckResp(theTransId,
				new EPPResellerCheckResult("res1523", true));

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		// TEST2 - Check response with three results.
		theResp = new EPPResellerCheckResp(theTransId);

		theResp.addCheckResult(new EPPResellerCheckResult("res1523", true));
		theResp.addCheckResult(new EPPResellerCheckResult("re1523", "In use"));
		theResp.addCheckResult(new EPPResellerCheckResult("1523res", true));

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testCheckResponse");
	}

	/**
	 * Unit test of the Reseller Info Command.
	 */
	public void testInfoCommand() {
		EPPCodecTst.printStart("testInfoCommand");

		EPPEncodeDecodeStats theStats;

		EPPResellerInfoCmd theCommand = new EPPResellerInfoCmd("ABC-12345",
				"res1523");

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testInfoCommand");
	}

	/**
	 * Unit test of the Reseller Info Response.
	 */
	public void testInfoResponse() {
		EPPCodecTst.printStart("testInfoResponse");

		EPPEncodeDecodeStats theStats;

		EPPTransId theTransId = new EPPTransId("ABC-12345", "54321-XYZ");
		
		EPPResellerInfoResp theResp = new EPPResellerInfoResp(theTransId,
				"res1523");
		theResp.setRoid("res1523-REP");
		theResp.setState(State.OK);
		theResp.setParentId("1523res");
		
		EPPResellerAddress theAddress = new EPPResellerAddress();
		theAddress.setStreets("124 Example Dr.", "Suite 200");
		theAddress.setCity("Dulles");
		theAddress.setStateProvince("VA");
		theAddress.setPostalCode("20166-6503");
		theAddress.setCountry("US");
		
		theResp.addPostalInfo(new EPPResellerPostalDefinition(
				EPPResellerPostalDefinition.Type.INT, "Example Reseller Inc.", theAddress));
		theResp.setVoice("+1.7035555555");
		theResp.setVoiceExt("1234");
		theResp.setFax("+1.7035555556");
		theResp.setEmail("contact@reseller.example");
		theResp.setUrl("http://reseller.example");
		theResp.addContact(new EPPResellerContact("sh8013", EPPResellerContact.Type.ADMINISTRATIVE));
		theResp.addContact(new EPPResellerContact("sh8013", EPPResellerContact.Type.BILLING));
		theResp.setClientId("ClientY");
		theResp.setCreatedBy("ClientX");
		theResp.setCreatedDate(new GregorianCalendar(1999, 04, 03, 22, 0).getTime());
		theResp.setLastUpdatedBy("ClientX");
		theResp.setLastUpdatedDate(new GregorianCalendar(1999, 12, 03, 0, 0).getTime());
		
		EPPResellerDisclose disclose = new EPPResellerDisclose(false);
		disclose.setVoice(true);
		disclose.setEmail(true);
		
		theResp.setDisclose(disclose);
	
		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testInfoResponse");
	}
	
	/**
	 * Unit test of the Reseller Create Command.
	 */
	public void testCreateCommand() {
		EPPCodecTst.printStart("testCreateCommand");

		EPPEncodeDecodeStats theStats;

		EPPResellerCreateCmd theCommand = new EPPResellerCreateCmd();
		
		theCommand.setResellerId("res1523");
		theCommand.setState(State.OK);
		theCommand.setParentId("1523res");
		
		EPPResellerAddress theAddress = new EPPResellerAddress();
		theAddress.setStreets("124 Example Dr.", "Suite 200");
		theAddress.setCity("Dulles");
		theAddress.setStateProvince("VA");
		theAddress.setPostalCode("20166-6503");
		theAddress.setCountry("US");
		
		theCommand.addPostalInfo(new EPPResellerPostalDefinition(
				EPPResellerPostalDefinition.Type.INT, "Example Reseller Inc.", theAddress));
		theCommand.setVoice("+1.7035555555");
		theCommand.setVoiceExt("1234");
		theCommand.setFax("+1.7035555556");
		theCommand.setEmail("contact@reseller.example");
		theCommand.setUrl("http://reseller.example");
		theCommand.addContact(new EPPResellerContact("sh8013", EPPResellerContact.Type.ADMINISTRATIVE));
		theCommand.addContact(new EPPResellerContact("sh8013", EPPResellerContact.Type.BILLING));
		
		EPPResellerDisclose disclose = new EPPResellerDisclose(false);
		disclose.setVoice(true);
		disclose.setEmail(true);
		
		theCommand.setDisclose(disclose);
		
		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testCreateCommand");
	}
	
	/**
	 * Unit test of the Reseller Create Response.
	 */
	public void testCreateResponse() {
		EPPCodecTst.printStart("testCreateResponse");

		EPPEncodeDecodeStats theStats;

		EPPTransId theTransId = new EPPTransId("ABC-12345", "54321-XYZ");
		
		EPPResellerCreateResp theResp = new EPPResellerCreateResp(theTransId,
				"res1523", new GregorianCalendar(1999, 04, 03, 22, 0).getTime());
		
	
		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testCreateResponse");
	}

	/**
	 * Unit test of the Reseller Delete Command.
	 */
	public void testDeleteCommand() {
		EPPCodecTst.printStart("testDeleteCommand");

		EPPEncodeDecodeStats theStats;

		EPPResellerDeleteCmd theCommand = new EPPResellerDeleteCmd("ABC-12345",
				"res1523");

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testDeleteCommand");
	}
	
	/**
	 * Unit test of the Reseller Update Command.
	 */
	public void testUpdateCommand() {
		EPPCodecTst.printStart("testUpdateCommand");

		EPPEncodeDecodeStats theStats;

		EPPResellerUpdateCmd theCommand = new EPPResellerUpdateCmd();
		
		theCommand.setResellerId("res1523");
		theCommand.setState(State.OK);
		theCommand.setParentId("1523res");
		
		EPPResellerAddress theAddress = new EPPResellerAddress();
		theAddress.setStreets("124 Example Dr.", "Suite 200");
		theAddress.setCity("Dulles");
		theAddress.setStateProvince("VA");
		theAddress.setPostalCode("20166-6503");
		theAddress.setCountry("US");
		
		theCommand.addPostalInfo(new EPPResellerPostalDefinition(
				EPPResellerPostalDefinition.Type.INT, "Example Reseller Inc.", theAddress));
		theCommand.setVoice("+1.7035555555");
		theCommand.setVoiceExt("1234");
		theCommand.setFax("+1.7035555556");
		theCommand.setEmail("contact@reseller.example");
		theCommand.setUrl("http://reseller.example");
		theCommand.addAddContact(new EPPResellerContact("sh8014", EPPResellerContact.Type.ADMINISTRATIVE));
		theCommand.addRemContact(new EPPResellerContact("sh8013", EPPResellerContact.Type.BILLING));
		
		EPPResellerDisclose disclose = new EPPResellerDisclose(false);
		disclose.setVoice(true);
		disclose.setEmail(true);
		
		theCommand.setDisclose(disclose);
		
		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testUpdateCommand");
	}
	
	/**
	 * JUNIT <code>setUp</code> method, which sets the default client Id to
	 * "theRegistrar" and initializes the <code>EPPResellerMapFactory</code>
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
	 * associated with <code>EPPResellerTst</code>.
	 *
	 * @return Tests to run
	 */
	public static Test suite() {
		EPPCodecTst.initEnvironment();

		TestSuite suite = new TestSuite(EPPResellerTst.class);

		// iterations Property
		String numIterProp = System.getProperty("iterations");

		if (numIterProp != null) {
			numIterations = Integer.parseInt(numIterProp);
		}

		// Add the EPPResellerMapFactory to the EPPCodec.
		try {
			EPPFactory.getInstance().addMapFactory(
					"com.verisign.epp.codec.contact.EPPContactMapFactory");
			EPPFactory.getInstance().addMapFactory(
					"com.verisign.epp.codec.reseller.EPPResellerMapFactory");
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
	 *            Program arguments
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
				TestThread thread = new TestThread(
						"EPPResellerTst Thread " + i, EPPResellerTst.suite());
				thread.start();
			}
		}
		else { // Single threaded mode.
			junit.textui.TestRunner.run(EPPResellerTst.suite());
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
