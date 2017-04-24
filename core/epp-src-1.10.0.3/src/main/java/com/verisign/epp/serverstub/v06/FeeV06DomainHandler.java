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

package com.verisign.epp.serverstub.v06;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.domain.EPPDomainCheckCmd;
import com.verisign.epp.codec.domain.EPPDomainCreateCmd;
import com.verisign.epp.codec.domain.EPPDomainDeleteCmd;
import com.verisign.epp.codec.domain.EPPDomainInfoCmd;
import com.verisign.epp.codec.domain.EPPDomainRenewCmd;
import com.verisign.epp.codec.domain.EPPDomainTransferCmd;
import com.verisign.epp.codec.domain.EPPDomainUpdateCmd;
import com.verisign.epp.codec.fee.v06.EPPFeeCheck;
import com.verisign.epp.codec.fee.v06.EPPFeeChkData;
import com.verisign.epp.codec.fee.v06.EPPFeeCreData;
import com.verisign.epp.codec.fee.v06.EPPFeeCreate;
import com.verisign.epp.codec.fee.v06.EPPFeeCredit;
import com.verisign.epp.codec.fee.v06.EPPFeeDelData;
import com.verisign.epp.codec.fee.v06.EPPFeeDomain;
import com.verisign.epp.codec.fee.v06.EPPFeeDomainResult;
import com.verisign.epp.codec.fee.v06.EPPFeeInfData;
import com.verisign.epp.codec.fee.v06.EPPFeeInfo;
import com.verisign.epp.codec.fee.v06.EPPFeePeriod;
import com.verisign.epp.codec.fee.v06.EPPFeeRenData;
import com.verisign.epp.codec.fee.v06.EPPFeeRenew;
import com.verisign.epp.codec.fee.v06.EPPFeeTransfer;
import com.verisign.epp.codec.fee.v06.EPPFeeTrnData;
import com.verisign.epp.codec.fee.v06.EPPFeeUpdData;
import com.verisign.epp.codec.fee.v06.EPPFeeUpdate;
import com.verisign.epp.codec.fee.v06.EPPFeeValue;
import com.verisign.epp.codec.gen.EPPCommand;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.serverstub.FeeSubDomainHandler;
import com.verisign.epp.util.EPPCatFactory;

/**
 * The <code>FeeDomainHandler</code> class extends <code>DomainHandler</code> to
 * include handling the fee extension.
 */
public class FeeV06DomainHandler implements FeeSubDomainHandler {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(FeeV06DomainHandler.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Handle an EPP Domain Check Command.
	 * 
	 * @param aCheckCommand
	 *            Command sent by the client
	 * @param aResponse
	 *            Response created previously up the stack
	 * @param aData
	 *            Server data. This is assumed to be an instance of
	 *            <code>SessionData</code>.
	 */
	public void doDomainCheck(EPPDomainCheckCmd aCheckCommand,
			EPPResponse aResponse, Object aData) {

		cat.info("doDomainCheck: fee check extension = "
				+ (EPPFeeCheck) aCheckCommand.getExtension(EPPFeeCheck.class));

		EPPFeeChkData theChkDataExt = new EPPFeeChkData();
		EPPFeeDomainResult theFeeResult;

		EPPFeeCheck theFeeExt = (EPPFeeCheck) aCheckCommand
				.getExtension(EPPFeeCheck.class);

		for (EPPFeeDomain domain : theFeeExt.getDomains()) {

			// Set the base result attributes
			theFeeResult = new EPPFeeDomainResult();
			theFeeResult.setName(domain.getName());
			theFeeResult.setCommand(domain.getCommand());
			theFeeResult.setPeriod(domain.getPeriod());

			if (domain.hasCurrency()) {
				theFeeResult.getCurrency();
			}
			else {
				theFeeResult.setCurrency("USD");
			}

			// Customize the response to match the I-D example
			if (domain.getName().equalsIgnoreCase("example.com")) {
				theFeeResult.setCurrency("USD");
				theFeeResult.addFee(new EPPFeeValue(new BigDecimal("5.00"),
						"Application Fee", false, null,
						EPPFeeValue.APPLIED_IMMEDIATE));
				theFeeResult.addFee(new EPPFeeValue(new BigDecimal("5.00"),
						"Registration Fee", true, null, null));
			}
			else if (domain.getName().equalsIgnoreCase("example.net")) {
				theFeeResult.setCurrency("EUR");
				theFeeResult.addFee(new EPPFeeValue(new BigDecimal("5.00")));
			}
			else if (domain.getName().equalsIgnoreCase("example.org")) {
				theFeeResult.setCurrency("EUR");
				theFeeResult.addFee(new EPPFeeValue(new BigDecimal("2.50"),
						"Transfer Fee", true, null, null));
				theFeeResult.addFee(new EPPFeeValue(new BigDecimal("10.00"),
						"Renewal Fee", true, null, null));
			}
			else if (domain.getName().equalsIgnoreCase("example.xyz")) {
				theFeeResult.setCurrency("GDB");
				theFeeResult.addFee(new EPPFeeValue(new BigDecimal("25.00"),
						"Restore Fee", true, null,
						EPPFeeValue.APPLIED_IMMEDIATE));
				theFeeResult.addFee(new EPPFeeValue(new BigDecimal("5.00"),
						"Renewal Fee", true, null,
						EPPFeeValue.APPLIED_IMMEDIATE));
			}
			else { // default case not defined in the I-D
				theFeeResult.setCurrency("USD");
				theFeeResult.addFee(new EPPFeeValue(new BigDecimal("5.00"),
						"Application Fee", true, "P5D",
						EPPFeeValue.APPLIED_DELAYED));
				theFeeResult.addFee(new EPPFeeValue(new BigDecimal("5.00"),
						"Registration Fee", true, "P20D",
						EPPFeeValue.APPLIED_IMMEDIATE));
				theFeeResult.setClassification("Test Classification");
			}

			theChkDataExt.addCheckResult(theFeeResult);
		}

		// Add extension to the response
		aResponse.addExtension(theChkDataExt);
	}

	/**
	 * Handle an EPP Domain Info Command.
	 * 
	 * @param aInfoCommand
	 *            Command sent by the client
	 * @param aResponse
	 *            Response created previously up the stack
	 * @param aData
	 *            Server data. This is assumed to be an instance of
	 *            <code>SessionData</code>.
	 */
	public void doDomainInfo(EPPDomainInfoCmd aInfoCommand,
			EPPResponse aResponse, Object aData) {

		cat.info("doDomainInfo: fee info extension = "
				+ (EPPFeeInfo) aInfoCommand.getExtension(EPPFeeInfo.class));

		EPPFeeInfData theInfData = new EPPFeeInfData();

		EPPFeeInfo theFeeExt = (EPPFeeInfo) aInfoCommand
				.getExtension(EPPFeeInfo.class);

		theInfData.setCommand(theFeeExt.getCommand());
		theInfData.setPeriod(theFeeExt.getPeriod());
		if (theFeeExt.hasCurrency()) {
			theInfData.setCurrency(theFeeExt.getCurrency());
		}
		else {
			theInfData.setCurrency("USD");
		}

		theInfData.addFee(new EPPFeeValue(new BigDecimal("10.00"), null, true,
				"P5D", EPPFeeValue.APPLIED_IMMEDIATE));
		theInfData.setClassification("premium-tier1");

		// Add extension to the response
		aResponse.addExtension(theInfData);
	}

	/**
	 * Handle an EPP Domain Create Command.
	 * 
	 * @param aCreateCommand
	 *            Command sent by the client
	 * @param aResponse
	 *            Response created previously up the stack
	 * @param aData
	 *            Server data. This is assumed to be an instance of
	 *            <code>SessionData</code>.
	 */
	public void doDomainCreate(EPPDomainCreateCmd aCreateCommand,
			EPPResponse aResponse, Object aData) {
		cat.info("doDomainCreate: fee create extension = "
				+ (EPPFeeCreate) aCreateCommand
						.getExtension(EPPFeeCreate.class));

		EPPFeeCreData theCreData = new EPPFeeCreData();

		EPPFeeCreate theFeeExt = (EPPFeeCreate) aCreateCommand
				.getExtension(EPPFeeCreate.class);

		theCreData.setFees(theFeeExt.getFees());
		theCreData.setCurrency(theFeeExt.getCurrency());
		theCreData.setBalance(new BigDecimal("-5.00"));
		theCreData.setCreditLimit(new BigDecimal("1000.00"));

		// Add extension to the response
		aResponse.addExtension(theCreData);
	}

	/**
	 * Handle an EPP Domain Delete Command.
	 * 
	 * @param aDeleteCommand
	 *            Command sent by the client
	 * @param aResponse
	 *            Response created previously up the stack
	 * @param aData
	 *            Server data. This is assumed to be an instance of
	 *            <code>SessionData</code>.
	 */
	public void doDomainDelete(EPPDomainDeleteCmd aDeleteCommand,
			EPPResponse aResponse, Object aData) {

		cat.info("doDomainDelete: fee delete extension to be added for "
				+ aDeleteCommand.getName());

		EPPFeeDelData theRespExt = new EPPFeeDelData("USD", new EPPFeeCredit(
				new BigDecimal("-5.00"), "AGP Credit"));
		theRespExt.setBalance(new BigDecimal("1005.00"));

		// Add extension to the response
		aResponse.addExtension(theRespExt);
	}

	/**
	 * Handle an EPP Domain Renew Command.
	 * 
	 * @param aRenewCommand
	 *            Command sent by the client
	 * @param aResponse
	 *            Response created previously up the stack
	 * @param aData
	 *            Server data. This is assumed to be an instance of
	 *            <code>SessionData</code>.
	 */
	public void doDomainRenew(EPPDomainRenewCmd aRenewCommand,
			EPPResponse aResponse, Object aData) {
		cat.info("doDomainRenew: fee renew extension = "
				+ (EPPFeeRenew) aRenewCommand.getExtension(EPPFeeRenew.class));

		EPPFeeRenData theRenData = new EPPFeeRenData();

		EPPFeeRenew theFeeExt = (EPPFeeRenew) aRenewCommand
				.getExtension(EPPFeeRenew.class);

		theRenData.setFees(theFeeExt.getFees());
		theRenData.setCurrency(theFeeExt.getCurrency());
		theRenData.setBalance(new BigDecimal("-5.00"));
		theRenData.setCreditLimit(new BigDecimal("1000.00"));

		// Add extension to the response
		aResponse.addExtension(theRenData);
	}

	/**
	 * Handle an EPP Domain Update Command.
	 * 
	 * @param aUpdateCommand
	 *            Command sent by the client
	 * @param aResponse
	 *            Response created previously up the stack
	 * @param aData
	 *            Server data. This is assumed to be an instance of
	 *            <code>SessionData</code>.
	 */
	public void doDomainUpdate(EPPDomainUpdateCmd aUpdateCommand,
			EPPResponse aResponse, Object aData) {
		cat.info("doDomainUpdate: fee update extension = "
				+ (EPPFeeUpdate) aUpdateCommand
						.getExtension(EPPFeeUpdate.class));

		EPPFeeUpdData theUpdData = new EPPFeeUpdData();

		EPPFeeUpdate theFeeExt = (EPPFeeUpdate) aUpdateCommand
				.getExtension(EPPFeeUpdate.class);

		theUpdData.setFees(theFeeExt.getFees());
		theUpdData.setCurrency(theFeeExt.getCurrency());
		theUpdData.setBalance(new BigDecimal("-5.00"));
		theUpdData.setCreditLimit(new BigDecimal("1000.00"));

		// Add extension to the response
		aResponse.addExtension(theUpdData);
	}

	/**
	 * Handle an EPP Domain Transfer Command.
	 * 
	 * @param aTransferCommand
	 *            Command sent by the client
	 * @param aResponse
	 *            Response created previously up the stack
	 * @param aData
	 *            Server data. This is assumed to be an instance of
	 *            <code>SessionData</code>.
	 */
	public void doDomainTransfer(EPPDomainTransferCmd aTransferCommand,
			EPPResponse aResponse, Object aData) {

		// Transfer Query?
		if (aTransferCommand.getOp().equals(EPPCommand.OP_QUERY)) {

			EPPFeeTrnData theTrnData = new EPPFeeTrnData("USD",
					new EPPFeeValue(new BigDecimal("5.00")));
			theTrnData.setPeriod(new EPPFeePeriod(1));

			// Add extension to the response
			aResponse.addExtension(theTrnData);

		} // Transfer Request?
		else if (aTransferCommand.getOp().equals(EPPCommand.OP_REQUEST)) {

			// Fee Extension Set?
			if (aTransferCommand.hasExtension(EPPFeeTransfer.class)) {
				cat.info("doDomainTransfer: fee transfer request extension = "
						+ (EPPFeeTransfer) aTransferCommand
								.getExtension(EPPFeeTransfer.class));

				EPPFeeTransfer theFeeExt = (EPPFeeTransfer) aTransferCommand
						.getExtension(EPPFeeTransfer.class);

				EPPFeeTrnData theTrnData = new EPPFeeTrnData();

				theTrnData.setFees(theFeeExt.getFees());
				theTrnData.setCurrency(theFeeExt.getCurrency());
				theTrnData.setBalance(new BigDecimal("-5.00"));
				theTrnData.setCreditLimit(new BigDecimal("1000.00"));

				// Add extension to the response
				aResponse.addExtension(theTrnData);
			}
			else {
				cat.info("doDomainTransfer: fee transfer request = null");
				System.out
						.println("doDomainTransfer: fee transfer request = null");
			}
		}

	}

}
