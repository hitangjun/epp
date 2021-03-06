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
package com.verisign.epp.codec.syncext;


// JUNIT Imports
import java.util.Calendar;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.verisign.epp.codec.domain.EPPDomainUpdateCmd;
import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPCodecTst;
import com.verisign.epp.codec.gen.EPPEncodeDecodeStats;
import com.verisign.epp.codec.gen.EPPFactory;
import com.verisign.epp.util.TestThread;

/**
 * Is a unit test of the com.verisign.epp.codec.syncext package. The unit test
 * will execute, gather statistics, and output the results of a test of each
 * com.verisign.epp.codec.syncext package concrete <code>EPPSyncExt</code>'s
 * and their expected <code>EPPResponse</code>. The unit test is dependent on
 * the use of <a href=http://www.mcwestcorp.com/Junit.html>JUNIT 3.5</a><br>
 *
 * @author $Author: jim $
 */
public class EPPSyncExtTst extends TestCase {
	/**
	 * Number of unit test iterations to run. This is set in
	 * <code>EPPCodecTst.main</code>
	 */
	static private long numIterations = 1;

	/**
	 * Creates a new EPPSyncExtTst object.
	 *
	 * @param name DOCUMENT ME!
	 */
	public EPPSyncExtTst(String name) {
		super(name);
	}

	// End EPPSyncExtTst(String)


	public void testDomainUpdateCommandWithSyncExt() {

        EPPCodecTst.printStart("testDomainUpdateCommandWithSyncExt");

        EPPDomainUpdateCmd theCommand =
            new EPPDomainUpdateCmd("example.com");
        
        theCommand.setTransId("ABC-12345-XYZ");


       // instantiate the sync update object
       EPPSyncExtUpdate syncUpdate = new EPPSyncExtUpdate();
       syncUpdate.setMonth(Calendar.DECEMBER);
       syncUpdate.setDay(30);
 
       theCommand.addExtension(syncUpdate);

        EPPEncodeDecodeStats commandStats =
            EPPCodecTst.testEncodeDecode(theCommand);

        System.out.println(commandStats);

        
        
        theCommand =
            new EPPDomainUpdateCmd("example2.com");
        
        theCommand.setTransId("ABC-12345-XYZ");


       // instantiate the sync update object
       syncUpdate = new EPPSyncExtUpdate();
       syncUpdate.setMonth(Calendar.JANUARY);
       syncUpdate.setDay(1);
 
       theCommand.addExtension(syncUpdate);

       commandStats =
            EPPCodecTst.testEncodeDecode(theCommand);

        System.out.println(commandStats);
        
        theCommand =
            new EPPDomainUpdateCmd("example3.com");
        
        theCommand.setTransId("ABC-12345-XYZ");


       // instantiate the sync update object
       syncUpdate = new EPPSyncExtUpdate();
       syncUpdate.setMonth(Calendar.FEBRUARY);
       syncUpdate.setDay(29);

       theCommand.addExtension(syncUpdate);

       commandStats =
            EPPCodecTst.testEncodeDecode(theCommand);

        System.out.println(commandStats);
        
        
        
        EPPCodecTst.printEnd("testDomainUpdateCommandWithSyncExt");
    }

	/**
	 * JUNIT <code>setUp</code> method, which sets the default client Id to
	 * "theRegistrar" and initializes the <code>EPPDomainMapFactory</code>
	 * with the <code>EPPCodec</code>.
	 */
	protected void setUp() {
	}

	// End EPPSyncExtTst.setUp();

	/**
	 * JUNIT <code>tearDown</code>, which currently does nothing.
	 */
	protected void tearDown() {
	}

	// End EPPSyncExtTst.tearDown();

	/**
	 * JUNIT <code>suite</code> static method, which returns the tests
	 * associated with <code>EPPSyncExtTst</code>.
	 *
	 * @return DOCUMENT ME!
	 */
	public static Test suite() {
		EPPCodecTst.initEnvironment();

		TestSuite suite = new TestSuite(EPPSyncExtTst.class);

		// iterations Property
		String numIterProp = System.getProperty("iterations");

		if (numIterProp != null) {
			numIterations = Integer.parseInt(numIterProp);
		}


		// Add the EPPDomainMapFactory to the EPPCodec.
		try {
			EPPFactory.getInstance().addMapFactory("com.verisign.epp.codec.domain.EPPDomainMapFactory");
            EPPFactory.getInstance().addExtFactory("com.verisign.epp.codec.syncext.EPPSyncExtFactory");
		}
		 catch (EPPCodecException e) {
			Assert.fail("EPPCodecException adding EPPDomainMapFactory or EPPSyncExtFactory to EPPCodec: "
						+ e);
		}
        return suite;
	}

	// End EPPSyncExtTst.suite()

	/**
	 * Unit test main, which accepts the following system property options:
	 * <br>
	 *
	 * <ul>
	 * <li>
	 * iterations Number of unit test iterations to run
	 * </li>
	 * <li>
	 * validate Turn XML validation on (<code>true</code>) or off (
	 * <code>false</code>). If validate is not specified, validation will be
	 * off.
	 * </li>
	 * </ul>
	 *
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		// Number of Threads
		int    numThreads = 1;
		String threadsStr = System.getProperty("threads");

		if (threadsStr != null) {
			numThreads = Integer.parseInt(threadsStr);
		}

		// Run test suite in multiple threads?
		if (numThreads > 1) {
			// Spawn each thread passing in the Test Suite
			for (int i = 0; i < numThreads; i++) {
				TestThread thread =
					new TestThread(
								   "EPPSyncExtTst Thread " + i,
								   EPPSyncExtTst.suite());
				thread.start();
			}
		}
		else { // Single threaded mode.
			junit.textui.TestRunner.run(EPPSyncExtTst.suite());
		}
	}

	// End EPPSyncExtTst.main(String [])

	/**
	 * Sets the number of iterations to run per test.
	 *
	 * @param aNumIterations number of iterations to run per test
	 */
	public static void setNumIterations(long aNumIterations) {
		numIterations = aNumIterations;
	}

	// End EPPSyncExtTst.setNumIterations(long)

}
