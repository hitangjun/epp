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
import com.hihexo.epp.model.NSPollParam;
import com.verisign.epp.codec.contact.EPPContactTransferResp;
import com.verisign.epp.codec.domain.EPPDomainTransferResp;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPResult;
import com.verisign.epp.codec.lowbalancepoll.EPPLowBalancePollResponse;
import com.verisign.epp.codec.rgppoll.EPPRgpPollResponse;
import com.verisign.epp.interfaces.EPPCommandException;
import com.verisign.epp.interfaces.EPPSession;
import com.verisign.epp.namestore.interfaces.NSDomain;
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
 * handling of NameStore poll messages.  This
 * also functions as a sample for handling all of the possible
 * poll messages generated by NameStore.    
 * 
 * @see NSDomain
 * @see com.verisign.epp.interfaces.EPPDomain
 */
@Controller
@RequestMapping("/poll")
public class NSPollController extends BaseNSController {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(NSPollController.class);
	@RequestMapping(value = "/send", method= RequestMethod.POST)
	@SystemControllerLog(description = "pollmessage")
	@ResponseBody
	public Object poll(HttpServletRequest request, @RequestBody NSPollParam params) {
		printStart("Poll");

		EPPSession theSession = null;
		try {
			theSession = this.borrowSession();
			EPPResponse theResponse = null;
			String domainName = params.getDomainName();
			String authStr =  params.getAuthStr();
			logger.debug("params="+params);
			logger.debug("domainName="+domainName);
			logger.debug("authStr="+authStr);
			try {
				NSDomain theDomain = new NSDomain(theSession);
				theDomain.addDomainName(domainName);
				theDomain.setAuthString(authStr);
				theDomain.sendCreate();

				theSession.setTransId(getClientTransId(request));
				theSession.setPollOp(EPPSession.OP_REQ);
				theResponse = theSession.sendPoll();

				while (theResponse.getResult().getCode() == EPPResult.SUCCESS_POLL_MSG) {

					// poll response
					logger.debug("Poll: Poll Response = [" + theResponse
							+ "]\n\n");

					// Domain Transfer notification
					if (theResponse instanceof EPPDomainTransferResp) {
						logger.debug("Poll: Got domain transfer notification");

						EPPDomainTransferResp theMsg = (EPPDomainTransferResp) theResponse;
						String theStatus = theMsg.getTransferStatus();
						return renderSuccess(theStatus);// FIXME: 2017/4/25 do something?
					} // Contact transfer notification
					else if (theResponse instanceof EPPContactTransferResp) {
							logger.debug("Poll: Got contact transfer notification");
							EPPContactTransferResp theMsg = (EPPContactTransferResp) theResponse;
							String theStatus = theMsg.getTransferStatus();
							return renderSuccess(theStatus); // FIXME: 2017/4/25 do something?
					} // low balance notification
					else if (theResponse instanceof EPPLowBalancePollResponse) {
						logger.debug("Poll: Got low balance notification");// FIXME: 2017/4/25 do something?
					} // RGP notification
					else if (theResponse instanceof EPPRgpPollResponse) {
						logger.debug("Poll: Got RGP notification");// FIXME: 2017/4/25 do something?
					} // Domain pending action notification
					else if (theResponse instanceof com.verisign.epp.codec.domain.EPPDomainPendActionMsg) {
						logger.debug("Poll: Got domain pending action notification");// FIXME: 2017/4/25 do something?
					} // Unknown general message
					else {
						logger.debug("Poll: Got general notification");
					}

					// Acknowledge the current message
					theSession.setPollOp(EPPSession.OP_ACK);
					theSession.setMsgID(theResponse.getMsgQueue().getId());
					theResponse = theSession.sendPoll();

					// Ack response
					logger.debug("doPoll: Poll ACK Response = [" + theResponse
							+ "]\n\n");

					theSession.setPollOp(EPPSession.OP_REQ);
					theResponse = theSession.sendPoll();
					//sleep 定时任务？ // FIXME: 2017/4/25
				}
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
			if (theSession != null) this.returnSession(theSession);
		}

		printEnd("Poll");
		return renderError("Unknown");
	}


} // End class NSPollController
