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

package com.verisign.epp.codec.changepoll;

import java.util.GregorianCalendar;
import java.util.Vector;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.verisign.epp.codec.domain.EPPDomainContact;
import com.verisign.epp.codec.domain.EPPDomainInfoResp;
import com.verisign.epp.codec.domain.EPPDomainMapFactory;
import com.verisign.epp.codec.domain.EPPDomainStatus;
import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPCodecTst;
import com.verisign.epp.codec.gen.EPPEncodeDecodeStats;
import com.verisign.epp.codec.gen.EPPFactory;
import com.verisign.epp.codec.gen.EPPMsgQueue;
import com.verisign.epp.codec.gen.EPPResult;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.util.TestThread;

/**
 * Is a unit test of the com.verisign.epp.codec.changepoll package. The unit
 * test will execute, gather statistics, and output the results of a test of
 * each com.verisign.epp.codec.changepoll package concrete extension
 * <code>EPPCodecComponent</code>'s.
 */
public class EPPChangePollTst extends TestCase {
	/**
	 * Number of unit test iterations to run. This is set in
	 * <code>EPPCodecTst.main</code>
	 */
	static private long numIterations = 1;

	/**
	 * Creates a new EPPChangePollTst object.
	 *
	 * @param name
	 *            Name of test
	 */
	public EPPChangePollTst(String name) {
		super(name);
	}

	/**
	 * Unit test of extending the domain info response to support a poll message
	 * with the change poll extension.
	 */
	public void testChangePoll() {
		EPPCodecTst.printStart("testChangePoll");

		EPPTransId respTransId = new EPPTransId("ABC-12345", "54321-XYZ");
		EPPEncodeDecodeStats theStats;

		Vector statuses = new Vector();
		statuses.addElement(new EPPDomainStatus(
				EPPDomainStatus.ELM_STATUS_SERVER_UPDATE_PROHIBITED));
		statuses.addElement(new EPPDomainStatus(
				EPPDomainStatus.ELM_STATUS_SERVER_DELETE_PROHIBITED));
		statuses.addElement(new EPPDomainStatus(
				EPPDomainStatus.ELM_STATUS_SERVER_TRANSFER_PROHIBITED));

		EPPDomainInfoResp theResponse = new EPPDomainInfoResp(respTransId,
				"EXAMPLE1-REP", "domain.example", "ClientX", statuses,
				"ClientY", new GregorianCalendar(2012, 04, 03).getTime(), null);

		theResponse.setResult(EPPResult.SUCCESS_POLL_MSG);
		theResponse.setMsgQueue(new EPPMsgQueue(new Long(1), "202",
				new GregorianCalendar(2013, 10, 22).getTime(),
				"Registry initiated update of domain."));

		// Test with all EPPDomainInfoResp attributes set.
		theResponse.setRegistrant("jd1234");

		// Is contacts supported?
		if (EPPFactory.getInstance().hasService(EPPDomainMapFactory.NS_CONTACT)) {
			Vector contacts = new Vector();
			contacts.addElement(new EPPDomainContact("sh8013",
					EPPDomainContact.TYPE_ADMINISTRATIVE));
			contacts.addElement(new EPPDomainContact("sh8013",
					EPPDomainContact.TYPE_TECHNICAL));
			theResponse.setContacts(contacts);
		}

		theResponse.setLastUpdatedBy("ClientZ");
		theResponse.setLastUpdatedDate(new GregorianCalendar(2013, 10, 22)
				.getTime());
		theResponse.setExpirationDate(new GregorianCalendar(2014, 04, 03)
				.getTime());

		// Add URS Lock Change Poll Extension with Before State
		EPPDomainInfoResp ursLockResponse = null;

		try {
			ursLockResponse = (EPPDomainInfoResp) theResponse.clone();
		}
		catch (CloneNotSupportedException e1) {
			Assert.fail("Exception cloning domain info response for URS Lock Change Poll Extension with Before State");
		}
		EPPChangeData changeData = new EPPChangeData(new EPPChangeOperation(
				EPPChangeOperation.OPERATION_UPDATE), new GregorianCalendar(
				2013, 10, 22).getTime(), "12345-XYZ", "URS Admin",
				EPPChangeData.STATE_BEFORE, new EPPChangeCaseId("urs123",
						EPPChangeCaseId.TYPE_URS), "URS Lock", null);

		ursLockResponse.addExtension(changeData);

		theStats = EPPCodecTst.testEncodeDecode(ursLockResponse);
		System.out.println(theStats);
		
		try {
			ursLockResponse = (EPPDomainInfoResp) theResponse.clone();
		}
		catch (CloneNotSupportedException e1) {
			Assert.fail("Exception cloning domain info response for URS Lock Change Poll Extension with After State");
		}
		changeData = new EPPChangeData(new EPPChangeOperation(
				EPPChangeOperation.OPERATION_UPDATE), new GregorianCalendar(
				2013, 10, 22).getTime(), "12345-XYZ", "URS Admin",
				EPPChangeData.STATE_AFTER, new EPPChangeCaseId("urs123",
						EPPChangeCaseId.TYPE_URS), "URS Lock", null);

		ursLockResponse.addExtension(changeData);

		theStats = EPPCodecTst.testEncodeDecode(ursLockResponse);
		System.out.println(theStats);
		

		// Add Customer Sync Change Poll Extension
		EPPDomainInfoResp custSyncResponse = null;
		try {
			custSyncResponse = (EPPDomainInfoResp) theResponse.clone();
		}
		catch (CloneNotSupportedException e) {
			Assert.fail("Exception cloning domain info response for Customer Sync Change Poll Extension");
		}
		changeData = new EPPChangeData(new EPPChangeOperation(
				EPPChangeOperation.OPERATION_CUSTOM, "sync"),
				new GregorianCalendar(2013, 10, 22).getTime(), "12345-XYZ",
				"CSR", EPPChangeData.STATE_AFTER, null,
				"Customer sync request", null);

		custSyncResponse.addExtension(changeData);

		theStats = EPPCodecTst.testEncodeDecode(custSyncResponse);
		System.out.println(theStats);

		// Add Delete Purge Change Poll Extension
		EPPDomainInfoResp deletePurgeResponse = null;
		try {
			custSyncResponse = (EPPDomainInfoResp) theResponse.clone();
		}
		catch (CloneNotSupportedException e) {
			Assert.fail("Exception cloning domain info response for Delete Purge Change Poll Extension");
		}
		changeData = new EPPChangeData(new EPPChangeOperation(
				EPPChangeOperation.OPERATION_DELETE, "purge"),
				new GregorianCalendar(2013, 10, 22).getTime(), "12345-XYZ",
				"CSR", EPPChangeData.STATE_AFTER, new EPPChangeCaseId("internal123",
						EPPChangeCaseId.TYPE_CUSTOM, "Internal"),
				"La demande du Client", "fr");

		custSyncResponse.addExtension(changeData);

		theStats = EPPCodecTst.testEncodeDecode(custSyncResponse);
		System.out.println(theStats);
		
		
		
		EPPCodecTst.printEnd("testChangePoll");
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
	 * associated with <code>EPPChangePollTst</code>.
	 *
	 * @return Test suite
	 */
	public static Test suite() {
		EPPCodecTst.initEnvironment();

		TestSuite suite = new TestSuite(EPPChangePollTst.class);

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
			EPPFactory
					.getInstance()
					.addExtFactory(
							"com.verisign.epp.codec.changepoll.EPPChangePollExtFactory");
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
				TestThread thread = new TestThread("EPPChangePollTst Thread "
						+ i, EPPChangePollTst.suite());
				thread.start();
			}
		}
		else { // Single threaded mode.
			junit.textui.TestRunner.run(EPPChangePollTst.suite());
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
