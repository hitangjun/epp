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

package com.verisign.epp.codec.idntable;

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
 * Is a unit test of the com.verisign.epp.codec.idntable package. 
 */
public class EPPIdnTableTst extends TestCase {
	/**
	 * Number of unit test iterations to run. This is set in
	 * <code>EPPCodecTst.main</code>
	 */
	static private long numIterations = 1;

	/**
	 * Creates a new EPPIdnTableTst object.
	 *
	 * @param name
	 *            Name of test
	 */
	public EPPIdnTableTst(String name) {
		super(name);
	}

	/**
	 * Unit test of the IDN Table Check Command with both Domain Check Form and
	 * Table Check Form.
	 */
	public void testCheckCommand() {
		EPPCodecTst.printStart("testCheckCommand");

		EPPEncodeDecodeStats theStats;

		// Domain Check Form
		EPPIdnTableCheckCmd theCommand = new EPPIdnTableCheckCmd("ABC-12345");

		theCommand.addDomain(new EPPIdnTableDomainLabel("idn1.example",
				EPPIdnTableDomainLabel.Form.U_LABEL));
		theCommand.addDomain(new EPPIdnTableDomainLabel("idn2.example",
				EPPIdnTableDomainLabel.Form.A_LABEL));
		theCommand.addDomain(new EPPIdnTableDomainLabel("idn3.example"));

		Assert.assertEquals(theCommand.getForm(),
				EPPIdnTableCheckCmd.Form.DOMAIN_FORM);

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		// Table Check Form
		theCommand = new EPPIdnTableCheckCmd("ABC-12345");

		theCommand.addTable("CHI");
		theCommand.addTable("JPN");
		theCommand.addTable("INVALID");

		Assert.assertEquals(theCommand.getForm(),
				EPPIdnTableCheckCmd.Form.TABLE_FORM);

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testCheckCommand");
	}

	/**
	 * Unit test of the IDN Table Check Response with both Domain Check Form and
	 * Table Check Form.
	 */
	public void testCheckResponse() {
		EPPCodecTst.printStart("testCheckResponse");

		EPPEncodeDecodeStats theStats;

		EPPTransId theTransId = new EPPTransId("ABC-12345", "54321-XYZ");

		// Domain Check Form
		EPPIdnTableCheckResp theResp = new EPPIdnTableCheckResp(theTransId);

		EPPIdnTableCheckDomain theCheckDomain = new EPPIdnTableCheckDomain(
				"idn1.example", true);
		theCheckDomain.addTable("CHI");
		theResp.addDomain(theCheckDomain);

		theCheckDomain = new EPPIdnTableCheckDomain("idn2.example", true);
		theCheckDomain.setIdnmap(true);
		theCheckDomain.addTable("CHI");
		theCheckDomain.addTable("JPN");
		theResp.addDomain(theCheckDomain);

		theCheckDomain = new EPPIdnTableCheckDomain("idn3.example",
				"Commingled scripts");
		theResp.addDomain(theCheckDomain);

		Assert.assertEquals(theResp.getForm(),
				EPPIdnTableCheckResp.Form.DOMAIN_FORM);

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		// Table Check Form
		theResp = new EPPIdnTableCheckResp(theTransId);

		theResp.addTable(new EPPIdnTableCheckTable("CHI", true));
		theResp.addTable(new EPPIdnTableCheckTable("JPN", true));
		theResp.addTable(new EPPIdnTableCheckTable("INVALID", false));

		Assert.assertEquals(theResp.getForm(),
				EPPIdnTableCheckResp.Form.TABLE_FORM);

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testCheckResponse");
	}

	/**
	 * Unit test of the IDN Table Info Command with Domain Info Form, Table Info
	 * Form, and List Info Form.
	 */
	public void testInfoCommand() {
		EPPCodecTst.printStart("testInfoCommand");

		EPPEncodeDecodeStats theStats;

		// Domain Info Form
		EPPIdnTableInfoCmd theCommand = new EPPIdnTableInfoCmd("ABC-12345",
				EPPIdnTableInfoCmd.Form.DOMAIN_FORM, "idn1.example");

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		theCommand = new EPPIdnTableInfoCmd("ABC-12345");
		theCommand.setDomain("xn--idn1.example");
		Assert.assertEquals(theCommand.getForm(),
				EPPIdnTableInfoCmd.Form.DOMAIN_FORM);

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		// Table Info Form
		theCommand = new EPPIdnTableInfoCmd("ABC-12345",
				EPPIdnTableInfoCmd.Form.TABLE_FORM, "CHI");

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		theCommand = new EPPIdnTableInfoCmd("ABC-12345");
		theCommand.setTable("THAI");
		Assert.assertEquals(theCommand.getForm(),
				EPPIdnTableInfoCmd.Form.TABLE_FORM);

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		// List Info Form
		theCommand = new EPPIdnTableInfoCmd("ABC-12345",
				EPPIdnTableInfoCmd.Form.LIST_FORM, null);

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		theCommand = new EPPIdnTableInfoCmd("ABC-54321");
		theCommand.setForm(EPPIdnTableInfoCmd.Form.LIST_FORM);
		Assert.assertEquals(theCommand.getForm(),
				EPPIdnTableInfoCmd.Form.LIST_FORM);

		theStats = EPPCodecTst.testEncodeDecode(theCommand);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testInfoCommand");
	}

	/**
	 * Unit test of the IDN Table Info Response with Domain Info Form, Table
	 * Info Form, and List Info Form.
	 */
	public void testInfoResponse() {
		EPPCodecTst.printStart("testInfoResponse");

		EPPEncodeDecodeStats theStats;

		EPPTransId theTransId = new EPPTransId("ABC-12345", "54321-XYZ");

		// Domain Info Form
		EPPIdnTableInfoDomain theDomain = new EPPIdnTableInfoDomain(
				"idn1.example", true);
		theDomain.addTable(new EPPIdnTableInfoDomainTable("THAI",
				EPPIdnTableInfoDomainTable.Type.SCRIPT, "THAI", "en", false));

		EPPIdnTableInfoResp theResp = new EPPIdnTableInfoResp(theTransId,
				theDomain);

		Assert.assertEquals(theResp.getForm(),
				EPPIdnTableInfoResp.Form.DOMAIN_FORM);

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		theDomain = new EPPIdnTableInfoDomain("xn--idn1.example", true);
		theDomain.setIdnmap(true);
		theDomain.setUname("idn1.example");
		theDomain.addTable(new EPPIdnTableInfoDomainTable("CHI",
				EPPIdnTableInfoDomainTable.Type.LANGUAGE, "Chinese (CHI)",
				"en", true));
		theDomain.addTable(new EPPIdnTableInfoDomainTable("JPN",
				EPPIdnTableInfoDomainTable.Type.LANGUAGE, "Japanese (JPN)",
				"en", false));

		theResp = new EPPIdnTableInfoResp(theTransId, theDomain);

		Assert.assertEquals(theResp.getForm(),
				EPPIdnTableInfoResp.Form.DOMAIN_FORM);

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		// Table Info Form
		EPPIdnTableInfoTable theTable = new EPPIdnTableInfoTable("CHI",
				EPPIdnTableInfoTable.Type.LANGUAGE, "Chinese (CHI)",
				new GregorianCalendar(2014, 11, 24, 9, 30).getTime());
		theTable.setVersion("1.0");
		theTable.setEffectiveDate(new GregorianCalendar(2014, 11, 24).getTime());
		theTable.setVariantGen(new Boolean(true));
		theTable.setUrl("https://www.iana.org/domains/idn-tables/tables/tld_chi_1.0.txt");

		theResp = new EPPIdnTableInfoResp(theTransId, theTable);

		Assert.assertEquals(theResp.getForm(),
				EPPIdnTableInfoResp.Form.TABLE_FORM);

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		theTable = new EPPIdnTableInfoTable("THAI",
				EPPIdnTableInfoTable.Type.SCRIPT, "Thai",
				new GregorianCalendar(2014, 8, 16, 9, 20).getTime());
		theTable.setVersion("1.0");
		theTable.setEffectiveDate(new GregorianCalendar(2014, 11, 24).getTime());
		theTable.setVariantGen(new Boolean(false));
		theTable.setUrl("https://www.iana.org/domains/idn-tables/tables/tld_thai_1.0.txt");

		theResp = new EPPIdnTableInfoResp(theTransId, theTable);

		Assert.assertEquals(theResp.getForm(),
				EPPIdnTableInfoResp.Form.TABLE_FORM);

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		// List Info Form
		theResp = new EPPIdnTableInfoResp(theTransId);

		theResp.addListItem(new EPPIdnTableInfoListItem("CHI",
				new GregorianCalendar(2015, 2, 4, 9, 30).getTime()));
		theResp.addListItem(new EPPIdnTableInfoListItem("JPN",
				new GregorianCalendar(2015, 1, 1, 9, 40).getTime()));
		theResp.addListItem(new EPPIdnTableInfoListItem("THAI",
				new GregorianCalendar(2014, 8, 16, 9, 20).getTime()));

		Assert.assertEquals(theResp.getForm(),
				EPPIdnTableInfoResp.Form.LIST_FORM);

		theStats = EPPCodecTst.testEncodeDecode(theResp);
		System.out.println(theStats);

		EPPCodecTst.printEnd("testInfoResponse");
	}

	/**
	 * JUNIT <code>setUp</code> method, which sets the default client Id to
	 * "theRegistrar" and initializes the <code>EPPIdnTableMapFactory</code>
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
	 * associated with <code>EPPIdnTableTst</code>.
	 *
	 * @return Tests to run
	 */
	public static Test suite() {
		EPPCodecTst.initEnvironment();

		TestSuite suite = new TestSuite(EPPIdnTableTst.class);

		// iterations Property
		String numIterProp = System.getProperty("iterations");

		if (numIterProp != null) {
			numIterations = Integer.parseInt(numIterProp);
		}

		// Add the EPPIdnTableMapFactory to the EPPCodec.
		try {
			EPPFactory.getInstance().addMapFactory(
					"com.verisign.epp.codec.idntable.EPPIdnTableMapFactory");
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
				TestThread thread = new TestThread("EPPIdnTableTst Thread " + i,
						EPPIdnTableTst.suite());
				thread.start();
			}
		}
		else { // Single threaded mode.
			junit.textui.TestRunner.run(EPPIdnTableTst.suite());
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
