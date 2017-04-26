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

import com.hihexo.epp.common.aspect.SystemControllerLog;
import com.hihexo.epp.model.NSHostParam;
import com.hihexo.epp.model.NSHostUpdateParam;
import com.hihexo.epp.namestore.interfaces.NSSubProduct;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.host.*;
import com.verisign.epp.codec.resellerext.EPPResellerExtUpdate.Action;
import com.verisign.epp.interfaces.EPPCommandException;
import com.verisign.epp.interfaces.EPPHost;
import com.verisign.epp.interfaces.EPPSession;
import com.verisign.epp.namestore.interfaces.NSHost;
import com.verisign.epp.util.InvalidateSessionException;
import com.verisign.epp.util.TestUtil;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * the <code>NSHost</code> interface. This  utilizes the
 * EPP session pool and exercises all of the operations defined in
 * <code>NSHost</code> and the base class <code>EPPHost</code>.
 * 
 * @see com.verisign.epp.namestore.interfaces.NSHost
 * @see EPPHost
 */
@Controller
@RequestMapping("/host")
public class NSHostController extends BaseNSController{
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(NSHostController.class);
	/**
	 * <code>NSHost.sendCreate</code> command.
	 */
	@RequestMapping(value = "/create",method = RequestMethod.POST)
	@SystemControllerLog(description = "创建HOST")
	@ResponseBody
	public Object doHostCreate(HttpServletRequest request, @RequestBody NSHostParam params) {
		printStart("HostCreate");

		EPPSession theSession = null;

		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSHost theHost = new com.verisign.epp.namestore.interfaces.NSHost(theSession);

			try {
				logger.debug("\n----------------------------------------------------------------");
				EPPResponse theResponse = null;
				String theHostName = params.getInternalHostName();
				logger.debug("hostCreate: Create internal " + theHostName);

				theHost.setTransId(getClientTransId(request));
				theHost.addHostName(theHostName);
				theHost.setSubProductID(NSSubProduct.COM);

				for(String ip4 : params.getIpv4()){
					theHost.addIPV4Address(ip4);
				}
				for(String ip6 : params.getIpv4()){
					theHost.addIPV6Address(ip6);
				}

				theResponse = theHost.sendCreate();
				// -- Output all of the response attributes
				logger.debug("hostCreate: Response = [" + theResponse
						+ "]\n\n");
				resultMap.put("internalResp",theResponse);
			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				EPPResponse theResponse = null;
				logger.debug("\n----------------------------------------------------------------");
				theHost.setTransId(getClientTransId(request));

				String theHostName = params.getExternalHostName();
				logger.debug("hostCreate: Create " + theHostName
						+ " with all optional attributes");

				theHost.addHostName(theHostName);
				theHost.setSubProductID(NSSubProduct.COM);
				theResponse = theHost.sendCreate();

				// -- Output all of the response attributes
				logger.debug("hostCreate: Response = [" + theResponse
						+ "]\n\n");
				resultMap.put("externalResp",theResponse);
			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

		}
		catch (InvalidateSessionException ex) {
			this.invalidateSession(theSession);
			theSession = null;
			return renderError("unknown");
		}
		finally {
			if (theSession != null) {
				this.returnSession(theSession);
			}
		}

		printEnd("HostCreate");
		return renderSuccess(resultMap);
	}

	/**
	 * <code>NSHost.sendHostCheck</code> command.
	 */
	@RequestMapping(value = "/check",method = RequestMethod.POST)
	@SystemControllerLog(description = "核查HOST")
	@ResponseBody
	public Object doHostCheck(HttpServletRequest request, @RequestBody NSHostParam params) {
		printStart("HostCheck");

		EPPSession theSession = null;
		EPPHostCheckResp theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSHost theHost = new com.verisign.epp.namestore.interfaces.NSHost(theSession);

			try {

				logger.debug("\n----------------------------------------------------------------");

				String theHostName = params.getInternalHostName();
				logger.debug("hostCheck: Check single host name ("
						+ theHostName + ")");

				theHost.setTransId(getClientTransId(request));

				theHost.addHostName(theHostName);
				theHost.setSubProductID(NSSubProduct.COM);

				theResponse = theHost.sendCheck();

				logger.debug("Response Type = " + theResponse.getType());
				logger.debug("Response.TransId.ServerTransId = "
						+ theResponse.getTransId().getServerTransId());
				logger.debug("Response.TransId.ServerTransId = "
						+ theResponse.getTransId().getClientTransId());
				// Output all of the response attributes
				logger.debug("\nhostCheck: Response = [" + theResponse
						+ "]");

				// For each result
				getHostCheckResult(theResponse);
				this.handleResponse(theResponse);
				return renderSuccess(theResponse);
			}
			catch (Exception e) {
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

		printEnd("HostCheck");
		return renderError("unknown");
	}

	/**
	 * <code>NSHost.sendHostCheck</code> command.
	 */
	@RequestMapping(value = "/checkmulti",method = RequestMethod.POST)
	@SystemControllerLog(description = "核查HOST")
	@ResponseBody
	public Object doHostCheckMulti(HttpServletRequest request, @RequestBody NSHostParam params) {
		printStart("HostCheckMulti");

		EPPSession theSession = null;
		EPPHostCheckResp theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSHost theHost = new com.verisign.epp.namestore.interfaces.NSHost(theSession);

			try {
				// Check multiple host names
				logger.debug("\n----------------------------------------------------------------");
				logger.debug("hostCheck: Check multiple host names ");
				theHost.setTransId(getClientTransId(request));

				for(String host:params.getMultiHost()){
					theHost.addHostName(host);
				}
				theHost.setSubProductID(NSSubProduct.COM);

				theResponse = theHost.sendCheck();

				// Output all of the response attributes
				logger.debug("\nhostCheck: Response = [" + theResponse
						+ "]");
				logger.debug("Client Transaction Id = "
						+ theResponse.getTransId().getClientTransId());
				logger.debug("Server Transaction Id = "
						+ theResponse.getTransId().getServerTransId());

				// For each result
				getHostCheckResult(theResponse);
				this.handleResponse(theResponse);
				
				return renderSuccess(theResponse);
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

		printEnd("HostCheck");
		return renderError("unknown");
	}

	private void getHostCheckResult(EPPHostCheckResp theResponse) {
		for (int i = 0; i < theResponse.getCheckResults().size(); i++) {
            EPPHostCheckResult currResult = (EPPHostCheckResult) theResponse
                    .getCheckResults().elementAt(i);

            if (currResult.isAvailable()) {
                logger.debug("hostCheck: Host "
                        + currResult.getName() + " is available");
            }
            else {
                logger.debug("hostCheck: Host "
                        + currResult.getName() + " is not available");
            }
        }
	}



	/**
	 * <code>NSHost.sendHostInfo</code> command.
	 */
	@RequestMapping(value = "/info",method = RequestMethod.POST)
	@SystemControllerLog(description = "查询HOST")
	@ResponseBody
	public Object doHostInfo(HttpServletRequest request, @RequestBody NSHostParam params) {
		printStart("HostInfo");

		EPPSession theSession = null;
		EPPHostInfoResp theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSHost theHost = new com.verisign.epp.namestore.interfaces.NSHost(theSession);

			try {
				logger.debug("\nhostInfo: Host info");

				theHost.setTransId(getClientTransId(request));

				theHost.addHostName(params.getInternalHostName());
				theHost.setSubProductID(NSSubProduct.COM);

				theResponse = theHost.sendInfo();

				// -- Output all of the response attributes
				logger.debug("hostInfo: Response = [" + theResponse
						+ "]\n\n");

				// -- Output required response attributes using accessors
				logger.debug("hostInfo: name = " + theResponse.getName());
				logger.debug("hostInfo: client id = "
						+ theResponse.getClientId());
				logger.debug("hostInfo: created by = "
						+ theResponse.getCreatedBy());
				logger.debug("hostInfo: create date = "
						+ theResponse.getCreatedDate());

				// -- Output optional response attributes using accessors
				// Addresses
				getHostAddress(theResponse);

				// Last Updated By
				if (theResponse.getLastUpdatedBy() != null) {
					logger.debug("hostInfo: last updated by = "
							+ theResponse.getLastUpdatedBy());
				}

				// Last Updated Date
				if (theResponse.getLastUpdatedDate() != null) {
					logger.debug("hostInfo: last updated date = "
							+ theResponse.getLastUpdatedDate());
				}

				this.handleResponse(theResponse);
				return renderSuccess(theResponse);
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

		printEnd("HostInfo");
		return renderError("unknown");
	}

	private void getHostAddress(EPPHostInfoResp theResponse) {
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
                    logger.debug(", type = IPV4");
                }

                // IPV6 Address?
                else if (currAddress.getType() == EPPHostAddress.IPV4) {
                    logger.debug(", type = IPV6");
                }
            }
        }
	}

	/**
	 * <code>NSHost.sendDelete</code> command.
	 */
	@RequestMapping(value = "/delete",method = RequestMethod.POST)
	@SystemControllerLog(description = "删除HOST")
	@ResponseBody
	public Object doHostDelete(HttpServletRequest request, @RequestBody NSHostParam params) {
		printStart("HostDelete");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSHost theHost = new com.verisign.epp.namestore.interfaces.NSHost(theSession);

			try {
				logger.debug("\nhostDelete: Host delete");

				theHost.setTransId(getClientTransId(request));

				theHost.addHostName(params.getInternalHostName());
				theHost.setSubProductID(NSSubProduct.COM);

				theResponse = theHost.sendDelete();

				// -- Output all of the response attributes
				logger.debug("hostDelete: Response = [" + theResponse
						+ "]\n\n");

				this.handleResponse(theResponse);
				return renderSuccess(theResponse);
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

		printEnd("HostDelete");
		return renderError("unknown");
	}

	/**
	 * <code>NSHost.sendUpdate</code> command.
	 */
	@RequestMapping(value = "/update",method = RequestMethod.POST)
	@SystemControllerLog(description = "更新HOST")
	@ResponseBody
	public Object doHostUpdate(HttpServletRequest request, @RequestBody NSHostUpdateParam params) {
		printStart("HostUpdate");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSHost theHost = new com.verisign.epp.namestore.interfaces.NSHost(theSession);

			try {

				theHost.setTransId(getClientTransId(request));

				String theHostName = params.getInternalHostName();

				logger.debug("\nhostUpdate: Host " + theHostName
						+ " update");

				theHost.addHostName(theHostName);
				theHost.setSubProductID(NSSubProduct.COM);

				// Add attributes
				for(String s:params.getAddIpv4()){
					theHost.addIPV4Address(s);
				}
				for(String s:params.getAddIpv6()){
					theHost.addIPV4Address(s);
				}
				for(String s:params.getRemoveIpv4()){
					theHost.removeIPV4Address(s);
				}
				for(String s:params.getRemoveIpv6()){
					theHost.removeIPV6Address(s);
				}
				for(String s:params.getAddHostStatus()){
					theHost.addStatus(s);
				}
				for(String s:params.getRemoveHostStatus()){
					theHost.removeStatus(s);
				}

				// Execute update
				theResponse = theHost.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("hostUpdate: Response = [" + theResponse
						+ "]\n\n");

				this.handleResponse(theResponse);
				return renderSuccess(theResponse);
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

		printEnd("HostUpdate");
		return renderError("unknown");
	}

	/**
	 * Unit  using {@link com.verisign.epp.namestore.interfaces.NSHost#setResellerId(String)} to set the reseller
	 * identifier on create and
	 * {@link com.verisign.epp.namestore.interfaces.NSHost#updateResellerId(Action, String)}
	 * to update the reseller identifier of an existing host.
	 */
	@RequestMapping(value = "/resellerid",method = RequestMethod.POST)
	@SystemControllerLog(description = "resellerid")
	@ResponseBody
	public Object doResellerId(HttpServletRequest request, @RequestBody NSHostParam params) {
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
				logger.debug("testResellerId: Create Response = ["
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
				logger.debug("testResellerId: ADD Update Response = ["
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
				logger.debug("testResellerId: CHG Update Response = ["
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
				logger.debug("testResellerId: REM Update Response = ["
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
		return renderError("unknown");
	}

} // End class NSHostController
