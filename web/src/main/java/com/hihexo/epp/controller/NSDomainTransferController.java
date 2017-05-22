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


import com.hihexo.epp.common.aspect.SystemControllerLog;
import com.hihexo.epp.common.base.ResultVo;
import com.hihexo.epp.model.NSDomainParam;
import com.hihexo.epp.model.NSDomainTransferParam;
import com.hihexo.epp.namestore.interfaces.NSSubProduct;
import com.verisign.epp.codec.domain.EPPDomainTransferResp;
import com.verisign.epp.interfaces.EPPCommandException;
import com.verisign.epp.interfaces.EPPDomain;
import com.verisign.epp.interfaces.EPPSession;
import com.verisign.epp.namestore.interfaces.NSDomain;
import com.verisign.epp.util.InvalidateSessionException;
import com.verisign.epp.util.TestUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/domain")
public class NSDomainTransferController extends BaseNSController{

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(NSDomainTransferController.class);



	/**
	 * <code>NSDomain.sendTransfer</code> command.
	 */
	@RequestMapping(value = "/transfer/request",method = RequestMethod.POST) 	@SystemControllerLog(description = "转出域名")
	@ResponseBody
	public ResultVo  doDomainTransfer(HttpServletRequest request,@RequestBody  NSDomainTransferParam params) {
		printStart("doDomainTransfer");

		EPPSession theSession = null;
		EPPDomainTransferResp theResponse = null;

		try {
			theSession = this.borrowSession();
			NSDomain theDomain = new NSDomain(theSession);
			String theDomainName = this.makeDomainName();

			try {

				logger.debug("\ndomainTransfer: Domain " + theDomainName
						+ " transfer request");

				theDomain.setTransferOpCode(EPPDomain.TRANSFER_REQUEST);

				theDomain.setTransId(getClientTransId(request));

				theDomain.setAuthString(params.getAuthStr());

				theDomain.addDomainName(params.getDomainName());
				theDomain.setSubProductID(params.getDomainProductID());

				theDomain.setPeriodLength(params.getPeriod());

				theDomain.setPeriodUnit(params.getPeriodUnit());

				if(StringUtils.isNotEmpty(params.getAllocationToken())){
					theDomain.setAllocationToken(params.getAllocationToken());
				}

				// Execute the transfer query
				theResponse = theDomain.sendTransfer();

				// -- Output all of the response attributes
				logger.debug("domainTransfer: Response = [" + theResponse
						+ "]\n\n");

				// -- Output required response attributes using accessors
				logger.debug("domainTransfer: name = "
						+ theResponse.getName());

				logger.debug("domainTransfer: request client = "
						+ theResponse.getRequestClient());

				logger.debug("domainTransfer: action client = "
						+ theResponse.getActionClient());

				logger.debug("domainTransfer: transfer status = "
						+ theResponse.getTransferStatus());

				logger.debug("domainTransfer: request date = "
						+ theResponse.getRequestDate());

				logger.debug("domainTransfer: action date = "
						+ theResponse.getActionDate());

				// -- Output optional response attributes using accessors
				if (theResponse.getExpirationDate() != null) {
					logger.debug("domainTransfer: expiration date = "
							+ theResponse.getExpirationDate());
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

		printEnd("doDomainTransfer");
		return renderError("fail");
	}

	@RequestMapping(value = "/transfer/query",method = RequestMethod.POST) 	@SystemControllerLog(description = "转出域名")
	@ResponseBody
	public ResultVo  doDomainTransferQuery(HttpServletRequest request,@RequestBody  NSDomainParam params) {
		printStart("doDomainTransfer");

		EPPSession theSession = null;
		EPPDomainTransferResp theResponse = null;

		try {
			theSession = this.borrowSession();
			NSDomain theDomain = new NSDomain(theSession);
			String theDomainName = this.makeDomainName();

			// Transfer Query
			try {
				logger.debug
						("\n----------------------------------------------------------------");

				logger.debug("\ndomainTransfer: Domain " + theDomainName
						+ " transfer query");

				theDomain.setTransferOpCode(EPPDomain.TRANSFER_QUERY);

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(params.getDomainName());
				theDomain.setSubProductID(params.getDomainProductID());

				// Execute the transfer query
				theResponse = theDomain.sendTransfer();

				// -- Output all of the response attributes
				logger.debug("domainTransferQuery: Response = ["
						+ theResponse + "]\n\n");

				// -- Output required response attributes using accessors
				logger.debug("domainTransferQuery: name = "
						+ theResponse.getName());

				logger.debug("domainTransferQuery: request client = "
						+ theResponse.getRequestClient());

				logger.debug("domainTransferQuery: action client = "
						+ theResponse.getActionClient());

				logger.debug("domainTransferQuery: transfer status = "
						+ theResponse.getTransferStatus());

				logger.debug("domainTransferQuery: request date = "
						+ theResponse.getRequestDate());

				logger.debug("domainTransferQuery: action date = "
						+ theResponse.getActionDate());

				// -- Output optional response attributes using accessors
				if (theResponse.getExpirationDate() != null) {
					logger.debug
							("domainTransferQuery: expiration date = "
									+ theResponse.getExpirationDate());
				}
				return renderSuccess(theResponse);
			}

			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
				return renderError(ex.getMessage());
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

		printEnd("doDomainTransfer");
		return renderError("unknown");
	}

	@RequestMapping(value = "/transfer/cancel",method = RequestMethod.POST) 	@SystemControllerLog(description = "转出域名")
	@ResponseBody
	public ResultVo  doDomainTransferCancel(HttpServletRequest request,@RequestBody  NSDomainParam params) {
		printStart("doDomainTransfer");

		EPPSession theSession = null;
		EPPDomainTransferResp theResponse = null;

		try {
			theSession = this.borrowSession();
			NSDomain theDomain = new NSDomain(theSession);
			String theDomainName = this.makeDomainName();

			// Transfer Cancel
			try {
				logger.debug
						("\n----------------------------------------------------------------");

				logger.debug("\ndomainTransfer: Domain " + theDomainName
						+ " transfer cancel");

				theDomain.setTransferOpCode(EPPDomain.TRANSFER_CANCEL);

				theDomain.addDomainName(params.getDomainName());
				theDomain.setSubProductID(params.getDomainProductID());

				// Execute the transfer cancel
				theResponse = theDomain.sendTransfer();

				// -- Output all of the response attributes
				logger.debug("domainTransfer: Response = [" + theResponse
						+ "]\n\n");
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

		printEnd("doDomainTransfer");
		return renderError("fail");
	}

	@RequestMapping(value = "/transfer/reject",method = RequestMethod.POST) 	@SystemControllerLog(description = "转出域名")
	@ResponseBody
	public ResultVo  doDomainTransferReject(HttpServletRequest request,@RequestBody  NSDomainParam params) {
		printStart("doDomainTransfer");

		EPPSession theSession = null;
		EPPDomainTransferResp theResponse = null;

		try {
			theSession = this.borrowSession();
			NSDomain theDomain = new NSDomain(theSession);
			String theDomainName = this.makeDomainName();

			// Transfer Reject
			try {
				logger.debug
						("\n----------------------------------------------------------------");

				logger.debug("\ndomainTransfer: Domain " + theDomainName
						+ " transfer reject");

				theDomain.setTransferOpCode(EPPDomain.TRANSFER_REJECT);

				theDomain.addDomainName(params.getDomainName());
				theDomain.setSubProductID(params.getDomainProductID());

				// Execute the transfer cancel
				theResponse = theDomain.sendTransfer();

				// -- Output all of the response attributes
				logger.debug("domainTransfer: Response = [" + theResponse
						+ "]\n\n");
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

		printEnd("doDomainTransfer");
		return renderError("fail");
	}

	@RequestMapping(value = "/transfer/approve",method = RequestMethod.POST) 	@SystemControllerLog(description = "转出域名")
	@ResponseBody
	public ResultVo  doDomainTransferApprove(HttpServletRequest request,@RequestBody  NSDomainParam params) {
		printStart("doDomainTransfer");

		EPPSession theSession = null;
		EPPDomainTransferResp theResponse = null;

		try {
			theSession = this.borrowSession();
			NSDomain theDomain = new NSDomain(theSession);
			String theDomainName = this.makeDomainName();

			// Transfer Approve
			try {
				logger.debug
						("\n----------------------------------------------------------------");

				logger.debug("\ndomainTransfer: Domain transfer approve");

				theDomain.setTransferOpCode(EPPDomain.TRANSFER_APPROVE);

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				// Execute the transfer cancel
				theResponse = theDomain.sendTransfer();

				// -- Output all of the response attributes
				logger.debug("domainTransfer: Response = [" + theResponse
						+ "]\n\n");
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

		printEnd("doDomainTransfer");
		return renderError("fail");
	}

} // End class NSDomainTst
