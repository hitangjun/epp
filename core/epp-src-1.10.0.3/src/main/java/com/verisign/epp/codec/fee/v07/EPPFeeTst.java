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

package com.verisign.epp.codec.fee.v07;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.verisign.epp.codec.domain.EPPDomainAddRemove;
import com.verisign.epp.codec.domain.EPPDomainCheckCmd;
import com.verisign.epp.codec.domain.EPPDomainCheckResp;
import com.verisign.epp.codec.domain.EPPDomainCheckResult;
import com.verisign.epp.codec.domain.EPPDomainCreateCmd;
import com.verisign.epp.codec.domain.EPPDomainCreateResp;
import com.verisign.epp.codec.domain.EPPDomainInfoCmd;
import com.verisign.epp.codec.domain.EPPDomainInfoResp;
import com.verisign.epp.codec.domain.EPPDomainPeriod;
import com.verisign.epp.codec.domain.EPPDomainRenewCmd;
import com.verisign.epp.codec.domain.EPPDomainRenewResp;
import com.verisign.epp.codec.domain.EPPDomainTransferCmd;
import com.verisign.epp.codec.domain.EPPDomainTransferResp;
import com.verisign.epp.codec.domain.EPPDomainUpdateCmd;
import com.verisign.epp.codec.gen.EPPAuthInfo;
import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPCodecTst;
import com.verisign.epp.codec.gen.EPPCommand;
import com.verisign.epp.codec.gen.EPPEncodeDecodeStats;
import com.verisign.epp.codec.gen.EPPFactory;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPResult;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.util.TestThread;

/**
 * Is a unit test of the com.verisign.epp.codec.fee.v07 package. The unit test
 * will execute, gather statistics, and output the results of a test of each
 * com.verisign.epp.codec.fee.v07 package concrete extension
 * <code>EPPCodecComponent</code>'s.
 */
public class EPPFeeTst extends TestCase {
	/**
	 * Number of unit test iterations to run. This is set in
	 * <code>EPPCodecTst.main</code>
	 */
	static private long numIterations = 1;

	/**
	 * Creates a new EPPFeeTst object.
	 *
	 * @param name
	 *            Name of the test
	 */
	public EPPFeeTst(String name) {
		super(name);
	}

	/**
	 * Unit test for the extension to the check command and response.
	 */
	public void testDomainCheck() {
		EPPDomainCheckCmd theCommand;
		EPPEncodeDecodeStats commandStats;

		EPPCodecTst.printStart("testDomainCheck");

		// Check three domains
		Vector domains = new Vector();
		domains.addElement("example.com");
		domains.addElement("example.net");
		domains.addElement("example.org");

		theCommand = new EPPDomainCheckCmd("ABC-12345", domains);

		// Add the Fee Check Extension
		EPPFeeCheck theCheckExt = new EPPFeeCheck();
		theCheckExt.addDomain(new EPPFeeDomain("example.com", "USD",
				new EPPFeeCommand("create", EPPFeeCommand.PHASE_SUNRISE),
				new EPPFeePeriod(1)));
		theCheckExt.addDomain(new EPPFeeDomain("example.net", "EUR",
				new EPPFeeCommand("create", EPPFeeCommand.PHASE_CLAIMS,
						EPPFeeCommand.PHASE_LANDRUSH), new EPPFeePeriod(2)));
		theCheckExt.addDomain(new EPPFeeDomain("example.org", "EUR",
				new EPPFeeCommand("transfer"), null));
		theCheckExt.addDomain(new EPPFeeDomain("example.xyz",
				new EPPFeeCommand("restore")));

		theCommand.addExtension(theCheckExt);

		commandStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(commandStats);

		// Domain Check Responses
		EPPDomainCheckResp theResponse;
		EPPEncodeDecodeStats responseStats;

		// Response for a single domain name
		EPPTransId respTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");

		theResponse = new EPPDomainCheckResp(respTransId,
				new EPPDomainCheckResult("example1.com", true));

		theResponse.setResult(EPPResult.SUCCESS);

		// Add the Fee Check Data Extension
		EPPFeeChkData theChkDataExt = new EPPFeeChkData();
		EPPFeeDomainResult theFeeResult;

		// example.com result
		theFeeResult = new EPPFeeDomainResult("example.com", "USD",
				new EPPFeeCommand("create", EPPFeeCommand.PHASE_SUNRISE));
		theFeeResult.addFee(new EPPFeeValue(new BigDecimal("5.00"),
				"Application Fee", false, null, EPPFeeValue.APPLIED_IMMEDIATE));
		theFeeResult.addFee(new EPPFeeValue(new BigDecimal("5.00"),
				"Registration Fee", true, null, null));
		theFeeResult.setPeriod(new EPPFeePeriod(1));
		theChkDataExt.addCheckResult(theFeeResult);

		// example.net result
		theFeeResult = new EPPFeeDomainResult("example.net", "EUR",
				new EPPFeeCommand("create", EPPFeeCommand.PHASE_CLAIMS,
						EPPFeeCommand.PHASE_LANDRUSH), new EPPFeePeriod(2),
				new EPPFeeValue(new BigDecimal("5.00")));
		theChkDataExt.addCheckResult(theFeeResult);

		// example.org result
		theFeeResult = new EPPFeeDomainResult("example.org", "EUR",
				new EPPFeeCommand("transfer"));
		theFeeResult.addFee(new EPPFeeValue(new BigDecimal("2.50"),
				"Transfer Fee", true, null, null));
		theFeeResult.addFee(new EPPFeeValue(new BigDecimal("10.00"),
				"Renewal Fee", true, null, null));
		theFeeResult.setPeriod(new EPPFeePeriod(2));
		theChkDataExt.addCheckResult(theFeeResult);

		// example.xyz result
		theFeeResult = new EPPFeeDomainResult("example.xyz", "GDB",
				new EPPFeeCommand("restore"));
		theFeeResult.addFee(new EPPFeeValue(new BigDecimal("25.00"),
				"Restore Fee", true, null, EPPFeeValue.APPLIED_IMMEDIATE));
		theFeeResult.addFee(new EPPFeeValue(new BigDecimal("5.00"),
				"Renewal Fee", true, null, EPPFeeValue.APPLIED_IMMEDIATE));
		theChkDataExt.addCheckResult(theFeeResult);

		theResponse.addExtension(theChkDataExt);

		responseStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(responseStats);

		EPPCodecTst.printEnd("testDomainCheck");
	}

	/**
	 * Unit test for the extension to the info command and response.
	 */
	public void testDomainInfo() {
		EPPDomainInfoCmd theCommand;
		EPPEncodeDecodeStats commandStats;

		EPPCodecTst.printStart("testDomainInfo");

		// Domain Info Command
		theCommand = new EPPDomainInfoCmd("ABC-12345", "example.com");
		theCommand.setHosts(EPPDomainInfoCmd.HOSTS_ALL);

		// Add the Fee Info Extension
		EPPFeeInfo theInfoExt = new EPPFeeInfo("USD", new EPPFeeCommand(
				"create", EPPFeeCommand.PHASE_SUNRISE), new EPPFeePeriod(1));

		theCommand.addExtension(theInfoExt);

		commandStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(commandStats);

		// Domain Info Response
		EPPDomainInfoResp theResponse = new EPPDomainInfoResp();
		theResponse.setName("example.com");
		theResponse.setTransId(new EPPTransId("ABC-12345", "54322-XYZ"));
		theResponse.setRoid("EXAMPLE1-REP");
		theResponse.setCreatedDate(new Date());
		theResponse.setCreatedBy("ClientY");
		theResponse.setClientId("ClientX");

		// Add the Fee Info Response Extension
		EPPFeeInfData theInfDataExt = new EPPFeeInfData();
		theInfDataExt.addFee(new EPPFeeValue(new BigDecimal("10.00"), null,
				true, "P5D", EPPFeeValue.APPLIED_IMMEDIATE));
		theInfDataExt.setCurrency("USD");
		theInfDataExt.setCommand(new EPPFeeCommand("create", EPPFeeCommand.PHASE_SUNRISE));
		theInfDataExt.setPeriod(new EPPFeePeriod(1));
		theInfDataExt.setClassification("premium-tier1");

		theResponse.addExtension(theInfDataExt);

		commandStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(commandStats);

		EPPCodecTst.printEnd("testDomainInfo");
	}

	/**
	 * Unit test for the extension to the create command and response.
	 */
	public void testDomainCreate() {
		EPPDomainCreateCmd theCommand;
		EPPEncodeDecodeStats commandStats;

		EPPCodecTst.printStart("testDomainCreate");

		// Create Command
		theCommand = new EPPDomainCreateCmd("ABC-12345", "example.com",
				new EPPAuthInfo("2fooBAR"));

		EPPFeeCreate theCreateExt = new EPPFeeCreate(new EPPFeeValue(
				new BigDecimal("5.00")));
		theCreateExt.setCurrency("USD");

		theCommand.addExtension(theCreateExt);

		commandStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(commandStats);

		// Create Response
		EPPDomainCreateResp theResponse;

		EPPTransId respTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");

		theResponse = new EPPDomainCreateResp(respTransId, "example.com",
				new GregorianCalendar(1999, 4, 3).getTime(),
				new GregorianCalendar(2001, 4, 3).getTime());
		theResponse.setResult(EPPResult.SUCCESS);

		EPPFeeCreData theRespExt = new EPPFeeCreData("USD", new EPPFeeValue(
				new BigDecimal("5.00")));

		theCommand.addExtension(theRespExt);

		commandStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(commandStats);

		respTransId = new EPPTransId(theCommand.getTransId(), "54321-XYZ");

		theResponse = new EPPDomainCreateResp(respTransId, "example.com",
				new GregorianCalendar(1999, 4, 3).getTime(),
				new GregorianCalendar(2001, 4, 3).getTime());
		theResponse.setResult(EPPResult.SUCCESS);

		theRespExt = new EPPFeeCreData("USD", new EPPFeeValue(new BigDecimal(
				"5.00")));
		theRespExt.setBalance(new BigDecimal("-5.00"));
		theRespExt.setCreditLimit(new BigDecimal("1000.00"));

		theCommand.addExtension(theRespExt);

		commandStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(commandStats);

		EPPCodecTst.printEnd("testDomainCreate");

	}

	/**
	 * Unit test for the extension to the renew command and response.
	 */
	public void testDomainRenew() {

		EPPCodecTst.printStart("testDomainRenew");

		// Create Command
		Calendar theCal = Calendar.getInstance();
		theCal.setTimeZone(TimeZone.getTimeZone("UTC"));
		theCal.set(2000, 4, 03, 0, 0, 0);
		theCal.set(Calendar.MILLISECOND, 0);
		Date theDate = theCal.getTime();

		EPPDomainRenewCmd theCommand = new EPPDomainRenewCmd("ABC-12345",
				"example.com", theDate, new EPPDomainPeriod(5));

		EPPFeeRenew theRenewExt = new EPPFeeRenew(new EPPFeeValue(
				new BigDecimal("5.00")));

		theCommand.addExtension(theRenewExt);

		EPPEncodeDecodeStats commandStats = EPPCodecTst
				.testEncodeDecode(theCommand);
		System.out.println(commandStats);

		// Create Response
		EPPDomainRenewResp theResponse;

		EPPTransId respTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");
		theResponse = new EPPDomainRenewResp(respTransId, "example.com",
				new GregorianCalendar(2000, 4, 3).getTime());
		theResponse.setResult(EPPResult.SUCCESS);

		EPPFeeRenData theRespExt = new EPPFeeRenData("USD", new EPPFeeValue(
				new BigDecimal("5.00")));

		theResponse.addExtension(theRespExt);

		commandStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(commandStats);

		respTransId = new EPPTransId(theCommand.getTransId(), "54321-XYZ");

		theResponse = new EPPDomainRenewResp(respTransId, "example.com",
				new GregorianCalendar(2000, 4, 3).getTime());
		theResponse.setResult(EPPResult.SUCCESS);

		theRespExt = new EPPFeeRenData();
		theRespExt.setCurrency("USD");
		theRespExt.addFee(new EPPFeeValue(new BigDecimal("5.00"), null, true,
				"P5D", EPPFeeValue.APPLIED_IMMEDIATE));
		theRespExt.setBalance(new BigDecimal("1000.00"));

		theResponse.addExtension(theRespExt);

		commandStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(commandStats);

		EPPCodecTst.printEnd("testDomainRenew");
	}

	/**
	 * Unit test for the extension to the update command and response.
	 */
	public void testDomainUpdate() {

		EPPCodecTst.printStart("testDomainUpdate");

		// Create Command
		EPPDomainAddRemove theChange = new EPPDomainAddRemove();
		theChange.setRegistrant("sh8013");
		EPPDomainUpdateCmd theCommand = new EPPDomainUpdateCmd("ABC-12345",
				"example.com", null, null, theChange);

		EPPFeeUpdate theUpdateExt = new EPPFeeUpdate(new EPPFeeValue(
				new BigDecimal("5.00")));
		theUpdateExt.setCurrency("USD");

		theCommand.addExtension(theUpdateExt);

		EPPEncodeDecodeStats commandStats = EPPCodecTst
				.testEncodeDecode(theCommand);
		System.out.println(commandStats);

		// Create Response
		EPPResponse theResponse;

		EPPTransId respTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");
		theResponse = new EPPResponse(respTransId);
		theResponse.setResult(EPPResult.SUCCESS);

		EPPFeeUpdData theRespExt = new EPPFeeUpdData("USD", new EPPFeeValue(
				new BigDecimal("5.00")));

		theResponse.addExtension(theRespExt);

		commandStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(commandStats);

		respTransId = new EPPTransId(theCommand.getTransId(), "54321-XYZ");
		theResponse = new EPPResponse(respTransId);
		theResponse.setResult(EPPResult.SUCCESS);

		theRespExt = new EPPFeeUpdData();
		theRespExt.setCurrency("USD");
		theRespExt.addFee(new EPPFeeValue(new BigDecimal("5.00"), null, true,
				"P5D", EPPFeeValue.APPLIED_IMMEDIATE));
		theRespExt.setBalance(new BigDecimal("1000.00"));

		theResponse.addExtension(theRespExt);

		commandStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(commandStats);

		EPPCodecTst.printEnd("testDomainUpdate");
	}

	/**
	 * Unit test for the extension to the transfer request command and response.
	 */
	public void testDomainTransfer() {

		EPPCodecTst.printStart("testDomainTransfer");

		EPPDomainTransferCmd theCommand = new EPPDomainTransferCmd("ABC-12345",
				EPPCommand.OP_REQUEST, "example.com",
				new EPPAuthInfo("2fooBAR"), new EPPDomainPeriod(1));

		EPPFeeTransfer theTransferExt = new EPPFeeTransfer(
				new EPPFeeValue(new BigDecimal("5.00")));

		theCommand.addExtension(theTransferExt);

		EPPEncodeDecodeStats commandStats = EPPCodecTst
				.testEncodeDecode(theCommand);
		System.out.println(commandStats);

		// Create Response
		EPPDomainTransferResp theResponse;
		EPPEncodeDecodeStats responseStats;

		EPPTransId respTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");
		theResponse = new EPPDomainTransferResp(respTransId, "example.com");
		theResponse.setResult(EPPResult.SUCCESS);

		theResponse.setRequestClient("ClientX");
		theResponse.setActionClient("ClientY");
		theResponse.setTransferStatus(EPPResponse.TRANSFER_PENDING);
		theResponse.setRequestDate(new GregorianCalendar(2000, 6, 8).getTime());
		theResponse.setActionDate(new GregorianCalendar(2000, 6, 13).getTime());
		theResponse.setExpirationDate(new GregorianCalendar(2002, 9, 8)
				.getTime());

		EPPFeeTrnData theRespExt = new EPPFeeTrnData("USD", new EPPFeeValue(
				new BigDecimal("5.00")));

		theResponse.addExtension(theRespExt);

		responseStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(responseStats);

		// Transfer Query Response
		respTransId = new EPPTransId(theCommand.getTransId(), "54321-XYZ");
		theResponse = new EPPDomainTransferResp(respTransId, "example.com");
		theResponse.setResult(EPPResult.SUCCESS);

		theResponse.setRequestClient("ClientX");
		theResponse.setActionClient("ClientY");
		theResponse.setTransferStatus(EPPResponse.TRANSFER_PENDING);
		theResponse.setRequestDate(new GregorianCalendar(2000, 6, 8).getTime());
		theResponse.setActionDate(new GregorianCalendar(2000, 6, 13).getTime());
		theResponse.setExpirationDate(new GregorianCalendar(2002, 9, 8)
				.getTime());

		theRespExt = new EPPFeeTrnData();
		theRespExt.setCurrency("USD");
		theRespExt.setPeriod(new EPPFeePeriod(1));
		theRespExt.addFee(new EPPFeeValue(new BigDecimal("5.00"), null, true,
				"P5D", EPPFeeValue.APPLIED_IMMEDIATE));

		theResponse.addExtension(theRespExt);

		responseStats = EPPCodecTst.testEncodeDecode(theResponse);
		System.out.println(responseStats);

		EPPCodecTst.printEnd("testDomainTransfer");
	}

	/**
	 * Unit test for the extension to the delete response.
	 */
	public void testDomainDelete() {

		EPPCodecTst.printStart("testDomainDelete");

		// Create Response
		EPPResponse theResponse;

		EPPTransId respTransId = new EPPTransId("ABC-12345", "54321-XYZ");
		theResponse = new EPPResponse(respTransId);

		EPPFeeDelData theRespExt = new EPPFeeDelData("USD", new EPPFeeCredit(
				new BigDecimal("-5.00"), "AGP Credit"));
		theRespExt.setBalance(new BigDecimal("1005.00"));

		theResponse.addExtension(theRespExt);

		EPPEncodeDecodeStats commandStats = EPPCodecTst
				.testEncodeDecode(theResponse);
		System.out.println(commandStats);

		EPPCodecTst.printEnd("testDomainDelete");
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
	 * associated with <code>EPPFeeTst</code>.
	 *
	 * @return DOCUMENT ME!
	 */
	public static Test suite() {
		EPPCodecTst.initEnvironment();

		TestSuite suite = new TestSuite(EPPFeeTst.class);

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
					"com.verisign.epp.codec.fee.v07.EPPFeeExtFactory");
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
				TestThread thread = new TestThread("EPPFeeTst Thread " + i,
						EPPFeeTst.suite());
				thread.start();
			}
		}
		else { // Single threaded mode.
			junit.textui.TestRunner.run(EPPFeeTst.suite());
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
