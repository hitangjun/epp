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
package com.hihexo.epp.controller;

//----------------------------------------------
//
// imports...
//
//----------------------------------------------
// Java Core Imports

import com.hihexo.epp.namestore.interfaces.NSSubProduct;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.host.EPPHostAddress;
import com.verisign.epp.codec.host.EPPHostCheckResp;
import com.verisign.epp.codec.host.EPPHostCheckResult;
import com.verisign.epp.codec.host.EPPHostInfoResp;
import com.verisign.epp.codec.resellerext.EPPResellerExtUpdate.Action;
import com.verisign.epp.interfaces.EPPCommandException;
import com.verisign.epp.interfaces.EPPHost;
import com.verisign.epp.interfaces.EPPSession;
import com.verisign.epp.namestore.interfaces.NSHost;
import com.verisign.epp.util.InvalidateSessionException;
import com.verisign.epp.util.TestUtil;

import javax.servlet.http.HttpServletRequest;

// Log4j imports
// EPP Imports

/**
 * Test of the use of the <code>NSHost</code> interface. This test utilizes the
 * EPP session pool and exercises all of the operations defined in
 * <code>NSHost</code> and the base class <code>EPPHost</code>.
 * 
 * @see com.verisign.epp.namestore.interfaces.NSHost
 * @see EPPHost
 */
public class NSHostController extends BaseNSController{

	// End NSHostController(String)

	/**
	 * <code>NSHost.sendCreate</code> command.
	 */
	public void doHostCreate(HttpServletRequest request) {
		printStart("testHostCreate");

		EPPSession theSession = null;
		EPPResponse theResponse = null;

		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSHost theHost = new com.verisign.epp.namestore.interfaces.NSHost(theSession);

			try {
				System.out
						.println("\n----------------------------------------------------------------");

				String theHostName = this.makeInternalHost();

				System.out
						.println("hostCreate: Create internal " + theHostName);

				theHost.setTransId(getClientTransId(request));

				theHost.addHostName(theHostName);
				theHost.setSubProductID(NSSubProduct.COM);

				theHost.addIPV4Address(this.makeIP());
				theHost.addIPV6Address("1080:0:0:0:8:800:200C:417A");
				theHost.addIPV6Address("::FFFF:129.144.52.38");

				theResponse = theHost.sendCreate();

				// -- Output all of the response attributes
				System.out.println("hostCreate: Response = [" + theResponse
						+ "]\n\n");

			}

			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				System.out
						.println("\n----------------------------------------------------------------");

				theHost.setTransId(getClientTransId(request));

				String theHostName = this.makeExternalHost();

				System.out.println("hostCreate: Create " + theHostName
						+ " with all optional attributes");

				theHost.addHostName(theHostName);
				theHost.setSubProductID(NSSubProduct.COM);

				theResponse = theHost.sendCreate();

				// -- Output all of the response attributes
				System.out.println("hostCreate: Response = [" + theResponse
						+ "]\n\n");
			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

		}
		catch (InvalidateSessionException ex) {
			this.invalidateSession(theSession);
			theSession = null;
		}
		finally {
			if (theSession != null)
				this.returnSession(theSession);
		}

		printEnd("testHostCreate");
	}

	/**
	 * <code>NSHost.sendHostCheck</code> command.
	 */
	public void doHostCheck(HttpServletRequest request) {
		printStart("testHostCheck");

		EPPSession theSession = null;
		EPPHostCheckResp theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSHost theHost = new com.verisign.epp.namestore.interfaces.NSHost(theSession);

			try {

				System.out
						.println("\n----------------------------------------------------------------");

				String theHostName = this.makeInternalHost();
				System.out.println("hostCheck: Check single host name ("
						+ theHostName + ")");

				theHost.setTransId(getClientTransId(request));

				theHost.addHostName(theHostName);
				theHost.setSubProductID(NSSubProduct.COM);

				theResponse = theHost.sendCheck();

				System.out.println("Response Type = " + theResponse.getType());

				System.out.println("Response.TransId.ServerTransId = "
						+ theResponse.getTransId().getServerTransId());

				System.out.println("Response.TransId.ServerTransId = "
						+ theResponse.getTransId().getClientTransId());

				// Output all of the response attributes
				System.out.println("\nhostCheck: Response = [" + theResponse
						+ "]");

				// For each result
				for (int i = 0; i < theResponse.getCheckResults().size(); i++) {
					EPPHostCheckResult currResult = (EPPHostCheckResult) theResponse
							.getCheckResults().elementAt(i);

					if (currResult.isAvailable()) {
						System.out.println("hostCheck: Host "
								+ currResult.getName() + " is available");
					}
					else {
						System.out.println("hostCheck: Host "
								+ currResult.getName() + " is not available");
					}
				}

				this.handleResponse(theResponse);
			}
			catch (Exception e) {
				TestUtil.handleException(theSession, e);
			}

			try {
				// Check multiple host names
				System.out
						.println("\n----------------------------------------------------------------");
				System.out
						.println("hostCheck: Check multiple host names (ns1.example.com, ns2.example.com, ns3.example.com)");
				theHost.setTransId(getClientTransId(request));

				/**
				 * Add ns(1-3).example.com
				 */
				theHost.addHostName("ns1.example.com");
				theHost.addHostName("ns2.example.com");
				theHost.addHostName("ns3.example.com");
				theHost.setSubProductID(NSSubProduct.COM);

				for (int i = 0; i <= 10; i++) {
					theHost.addHostName(this.makeInternalHost());
				}

				theResponse = theHost.sendCheck();

				// Output all of the response attributes
				System.out.println("\nhostCheck: Response = [" + theResponse
						+ "]");
				System.out.println("Client Transaction Id = "
						+ theResponse.getTransId().getClientTransId());
				System.out.println("Server Transaction Id = "
						+ theResponse.getTransId().getServerTransId());

				// For each result
				for (int i = 0; i < theResponse.getCheckResults().size(); i++) {
					EPPHostCheckResult currResult = (EPPHostCheckResult) theResponse
							.getCheckResults().elementAt(i);

					if (currResult.isAvailable()) {
						System.out.println("hostCheck: Host "
								+ currResult.getName() + " is available");
					}
					else {
						System.out.println("hostCheck: Host "
								+ currResult.getName() + " is not available");
					}
				}

				this.handleResponse(theResponse);
			}
			catch (EPPCommandException e) {
				TestUtil.handleException(theSession, e);
			}

		}
		catch (InvalidateSessionException ex) {
			this.invalidateSession(theSession);
			theSession = null;
		}
		finally {
			if (theSession != null)
				this.returnSession(theSession);
		}

		printEnd("testHostCheck");
	}

	/**
	 * <code>NSHost.sendHostInfo</code> command.
	 */
	public void doHostInfo(HttpServletRequest request) {
		printStart("testHostInfo");

		EPPSession theSession = null;
		EPPHostInfoResp theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSHost theHost = new com.verisign.epp.namestore.interfaces.NSHost(theSession);

			try {
				System.out.println("\nhostInfo: Host info");

				theHost.setTransId(getClientTransId(request));

				theHost.addHostName(this.makeInternalHost());
				theHost.setSubProductID(NSSubProduct.COM);

				theResponse = theHost.sendInfo();

				// -- Output all of the response attributes
				System.out.println("hostInfo: Response = [" + theResponse
						+ "]\n\n");

				// -- Output required response attributes using accessors
				System.out.println("hostInfo: name = " + theResponse.getName());
				System.out.println("hostInfo: client id = "
						+ theResponse.getClientId());
				System.out.println("hostInfo: created by = "
						+ theResponse.getCreatedBy());
				System.out.println("hostInfo: create date = "
						+ theResponse.getCreatedDate());

				// -- Output optional response attributes using accessors
				// Addresses
				if (theResponse.getAddresses() != null) {
					// For each Address
					for (int i = 0; i < theResponse.getAddresses().size(); i++) {
						EPPHostAddress currAddress = (EPPHostAddress) theResponse
								.getAddresses().elementAt(i);

						System.out.print("hostInfo: address " + (i + 1));

						// Address Name
						System.out.print(" name = " + currAddress.getName());

						// IPV4 Address?
						if (currAddress.getType() == EPPHostAddress.IPV4) {
							System.out.println(", type = IPV4");
						}

						// IPV6 Address?
						else if (currAddress.getType() == EPPHostAddress.IPV4) {
							System.out.println(", type = IPV6");
						}
					}
				}

				// Last Updated By
				if (theResponse.getLastUpdatedBy() != null) {
					System.out.println("hostInfo: last updated by = "
							+ theResponse.getLastUpdatedBy());
				}

				// Last Updated Date
				if (theResponse.getLastUpdatedDate() != null) {
					System.out.println("hostInfo: last updated date = "
							+ theResponse.getLastUpdatedDate());
				}

				this.handleResponse(theResponse);

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

		}
		catch (InvalidateSessionException ex) {
			this.invalidateSession(theSession);
			theSession = null;
		}
		finally {
			if (theSession != null)
				this.returnSession(theSession);
		}

		printEnd("testHostInfo");
	}

	/**
	 * <code>NSHost.sendDelete</code> command.
	 */
	public void doHostDelete(HttpServletRequest request) {
		printStart("testHostDelete");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSHost theHost = new com.verisign.epp.namestore.interfaces.NSHost(theSession);

			try {
				System.out.println("\nhostDelete: Host delete");

				theHost.setTransId(getClientTransId(request));

				theHost.addHostName(this.makeInternalHost());
				theHost.setSubProductID(NSSubProduct.COM);

				theResponse = theHost.sendDelete();

				// -- Output all of the response attributes
				System.out.println("hostDelete: Response = [" + theResponse
						+ "]\n\n");

				this.handleResponse(theResponse);

			}

			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

		}
		catch (InvalidateSessionException ex) {
			this.invalidateSession(theSession);
			theSession = null;
		}
		finally {
			if (theSession != null)
				this.returnSession(theSession);
		}

		printEnd("testHostDelete");
	}

	/**
	 * <code>NSHost.sendUpdate</code> command.
	 */
	public void doHostUpdate(HttpServletRequest request) {
		printStart("testHostUpdate");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSHost theHost = new com.verisign.epp.namestore.interfaces.NSHost(theSession);

			try {

				theHost.setTransId(getClientTransId(request));

				String theHostName = this.makeInternalHost();

				System.out.println("\nhostUpdate: Host " + theHostName
						+ " update");

				theHost.addHostName(theHostName);
				theHost.setSubProductID(NSSubProduct.COM);

				// Add attributes
				theHost.addIPV4Address(this.makeIP());

				// Remove attributes
				theHost.removeIPV6Address("1080:0:0:0:8:800:200C:417A");

				theHost.addStatus(EPPHost.STAT_OK, "Hello_World",
						EPPHost.DEFAULT_LANG);

				theHost.removeStatus(EPPHost.STAT_OK,
						"Hello World with spaces", EPPHost.DEFAULT_LANG);

				// Execute update
				theResponse = theHost.sendUpdate();

				// -- Output all of the response attributes
				System.out.println("hostUpdate: Response = [" + theResponse
						+ "]\n\n");

				this.handleResponse(theResponse);

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

		}
		catch (InvalidateSessionException ex) {
			this.invalidateSession(theSession);
			theSession = null;
		}
		finally {
			if (theSession != null)
				this.returnSession(theSession);
		}

		printEnd("testHostUpdate");
	}

	/**
	 * Unit test using {@link com.verisign.epp.namestore.interfaces.NSHost#setResellerId(String)} to set the reseller
	 * identifier on create and
	 * {@link com.verisign.epp.namestore.interfaces.NSHost#updateResellerId(Action, String)}
	 * to update the reseller identifier of an existing host.
	 */
	public void doResellerId(HttpServletRequest request) {
		printStart("testResellerId");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSHost theHost = new NSHost(theSession);

			// Reseller Identifier with Host Create
			try {
				System.out
						.println("\ntestResellerId: Host create with reseller identifier");

				theHost.setTransId("ABC-12345");

				theHost.addHostName("ns1.reseller.example");
				theHost.setSubProductID("example");
				theHost.addIPV4Address(this.makeIP());
				theHost.addIPV6Address("1080:0:0:0:8:800:200C:417A");
				theHost.addIPV6Address("::FFFF:129.144.52.38");
				theHost.setResellerId("myreseller");

				theResponse = theHost.sendCreate();

				// -- Output all of the response attributes
				System.out.println("testResellerId: Create Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Reseller Identifier with Host Update - ADD
			try {
				System.out
						.println("\ntestResellerId: Host update with reseller identifier - ADD");

				theHost.setTransId(getClientTransId(request));

				theHost.addHostName("example1.tld");
				theHost.setSubProductID("tld");

				theHost.updateResellerId(Action.ADD, "myreseller");

				theResponse = theHost.sendUpdate();

				// -- Output all of the response attributes
				System.out.println("testResellerId: ADD Update Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);
			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Reseller Identifier with Host Update - CHG
			try {
				System.out
						.println("\ntestResellerId: Host update with reseller identifier - CHG");

				theHost.setTransId(getClientTransId(request));

				theHost.addHostName("example1.tld");
				theHost.setSubProductID("tld");

				theHost.updateResellerId(Action.CHG, "myreseller2");

				theResponse = theHost.sendUpdate();

				// -- Output all of the response attributes
				System.out.println("testResellerId: CHG Update Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);
			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Reseller Identifier with Host Update - REM
			try {
				System.out
						.println("\ntestResellerId: Host update with reseller identifier - REM");

				theHost.setTransId(getClientTransId(request));

				theHost.addHostName("example1.tld");
				theHost.setSubProductID("tld");

				theHost.updateResellerId(Action.REM, "myreseller2");

				theResponse = theHost.sendUpdate();

				// -- Output all of the response attributes
				System.out.println("testResellerId: REM Update Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);
			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

		}
		catch (InvalidateSessionException ex) {
			this.invalidateSession(theSession);
			theSession = null;
		}
		finally {
			if (theSession != null)
				this.returnSession(theSession);
		}

		printEnd("testResellerId");
	}

} // End class NSHostController
