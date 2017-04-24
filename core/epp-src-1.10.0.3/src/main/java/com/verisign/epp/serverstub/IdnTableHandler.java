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

package com.verisign.epp.serverstub;

import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPResult;
import com.verisign.epp.codec.gen.EPPTransId;
import com.verisign.epp.codec.idntable.EPPIdnTableCheckCmd;
import com.verisign.epp.codec.idntable.EPPIdnTableCheckDomain;
import com.verisign.epp.codec.idntable.EPPIdnTableCheckResp;
import com.verisign.epp.codec.idntable.EPPIdnTableCheckTable;
import com.verisign.epp.codec.idntable.EPPIdnTableDomainLabel;
import com.verisign.epp.codec.idntable.EPPIdnTableInfoCmd;
import com.verisign.epp.codec.idntable.EPPIdnTableInfoDomain;
import com.verisign.epp.codec.idntable.EPPIdnTableInfoDomainTable;
import com.verisign.epp.codec.idntable.EPPIdnTableInfoListItem;
import com.verisign.epp.codec.idntable.EPPIdnTableInfoResp;
import com.verisign.epp.codec.idntable.EPPIdnTableInfoTable;
import com.verisign.epp.framework.EPPEvent;
import com.verisign.epp.framework.EPPEventResponse;
import com.verisign.epp.framework.EPPHandleEventException;
import com.verisign.epp.framework.EPPIdnTableHandler;
import com.verisign.epp.util.EPPCatFactory;

/**
 * The <code>IdnMapDomainHandler</code> class extends <code>DomainHandler</code>
 * to include handling the IDN Map Extension.
 */
public class IdnTableHandler extends EPPIdnTableHandler {

	/** Log4j category for logging */
	private static Logger cat = Logger.getLogger(IdnTableHandler.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Constructs an instance of IdnMapDomainHandler
	 */
	public IdnTableHandler() {
	}

	/**
	 * Do any pre-handling of commands.
	 *
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>IdnTableHandler</code> This is assumed to be an instance
	 *            of SessionData here.
	 *
	 * @exception EPPHandleEventException
	 *                Thrown if an error condition occurs. It must contain an
	 *                <code>EPPEventResponse</code>
	 */
	protected void preHandleEvent(EPPEvent aEvent, Object aData)
			throws EPPHandleEventException {

		super.preHandleEvent(aEvent, aData);

	}

	/**
	 * Override base handler <code>doIdnTableCheck</code> method and add
	 * handling of the IDN Table Info Command.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>IdnTableHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doIdnTableCheck(EPPEvent aEvent, Object aData) {
		cat.debug("doIdnTableCheck: enter");

		EPPIdnTableCheckCmd theCommand = (EPPIdnTableCheckCmd) aEvent
				.getMessage();

		EPPTransId theTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");

		EPPIdnTableCheckResp theResp = new EPPIdnTableCheckResp(theTransId);

		switch (theCommand.getForm()) {
			case DOMAIN_FORM:
				cat.debug("doIdnTableCheck: Command with domain form.");
				List<EPPIdnTableDomainLabel> theDomains = theCommand
						.getDomains();

				for (EPPIdnTableDomainLabel theDomainLabel : theDomains) {

					if (theDomainLabel.getDomain().equalsIgnoreCase(
							"idn1.example")) {
						EPPIdnTableCheckDomain theCheckDomain = new EPPIdnTableCheckDomain(
								"idn1.example", true, true);
						theCheckDomain.addTable("CHI");
						theResp.addDomain(theCheckDomain);
					}
					else if (theDomainLabel.getDomain().equalsIgnoreCase(
							"idn2.example")) {
						EPPIdnTableCheckDomain theCheckDomain = new EPPIdnTableCheckDomain(
								"idn2.example", true, true);
						theCheckDomain.setIdnmap(true);
						theCheckDomain.addTable("CHI");
						theCheckDomain.addTable("JPN");
						theResp.addDomain(theCheckDomain);
					}
					else if (theDomainLabel.getDomain().equalsIgnoreCase(
							"idn3.example")) {
						EPPIdnTableCheckDomain theCheckDomain = new EPPIdnTableCheckDomain(
								"idn3.example", "Commingled scripts");
						theResp.addDomain(theCheckDomain);
					}
					else {
						// Treat it like a non-IDN domain name, where it is a
						// valid domain name, but
						// it does not require the IDN mapping extension.
						EPPIdnTableCheckDomain theCheckDomain = new EPPIdnTableCheckDomain(
								theDomainLabel.getDomain(), true, false);
						theResp.addDomain(theCheckDomain);
					}
				}
				break;
			case TABLE_FORM:
				cat.debug("doIdnTableCheck: Command with table form.");
				List<String> theTables = theCommand.getTables();

				for (String theTableName : theTables) {
					if (theTableName.equalsIgnoreCase("CHI")) {
						theResp.addTable(new EPPIdnTableCheckTable("CHI", true));
					}
					else if (theTableName.equalsIgnoreCase("JPN")) {
						theResp.addTable(new EPPIdnTableCheckTable("JPN", true));
					}
					else if (theTableName.equalsIgnoreCase("INVALID")) {
						theResp.addTable(new EPPIdnTableCheckTable("INVALID",
								false));
					}
					else {
						// Provide default Table Check Form response where the
						// table does not exist
						theResp.addTable(new EPPIdnTableCheckTable(
								theTableName, false));
					}
				}
				break;
			case UNDEFINED_FORM:
				// Return error for undefined form
				cat.error("doIdnTableCheck: Command with undefined form.");
				EPPResponse theErrorResponse = new EPPResponse(theTransId);
				theErrorResponse.setResult(EPPResult.PARAM_VALUE_POLICY_ERROR);
				theErrorResponse.getResult().addExtValueReason(
						"IDN Table Check using undefined form");
				return new EPPEventResponse(theErrorResponse);

		}

		cat.debug("doIdnTableCheck: exit");
		return new EPPEventResponse(theResp);
	}

	/**
	 * Override base handler <code>doIdnTableInfo</code> method and add handling
	 * of the IDN Table Info Command.
	 * 
	 * @param aEvent
	 *            The <code>EPPEvent</code> that is being handled
	 * @param aData
	 *            Any data that a Server needs to send to this
	 *            <code>IdnTableHandler</code>
	 *
	 * @return EPPEventResponse The response that should be sent back to the
	 *         client.
	 */
	protected EPPEventResponse doIdnTableInfo(EPPEvent aEvent, Object aData) {
		cat.debug("doIdnTableInfo: enter");

		EPPIdnTableInfoCmd theCommand = (EPPIdnTableInfoCmd) aEvent
				.getMessage();

		EPPTransId theTransId = new EPPTransId(theCommand.getTransId(),
				"54321-XYZ");

		EPPIdnTableInfoResp theResp = new EPPIdnTableInfoResp(theTransId);

		switch (theCommand.getForm()) {
			case DOMAIN_FORM:
				cat.debug("doIdnTableInfo: Command with domain form.");
				// Provide Domain Info Form response
				if (theCommand.getDomain().equalsIgnoreCase("idn1.example")) {
					EPPIdnTableInfoDomain theDomain = new EPPIdnTableInfoDomain(
							"idn1.example", true);
					theDomain.addTable(new EPPIdnTableInfoDomainTable("THAI",
							EPPIdnTableInfoDomainTable.Type.SCRIPT, "THAI",
							"en", false));
					theResp.setDomain(theDomain);
				}
				else if (theCommand.getDomain().equalsIgnoreCase(
						"xn--idn1.example")) {
					EPPIdnTableInfoDomain theDomain = new EPPIdnTableInfoDomain(
							"xn--idn1.example", true);
					theDomain.setIdnmap(true);
					theDomain.setUname("idn1.example");
					theDomain.addTable(new EPPIdnTableInfoDomainTable("CHI",
							EPPIdnTableInfoDomainTable.Type.LANGUAGE,
							"Chinese (CHI)", "en", true));
					theDomain.addTable(new EPPIdnTableInfoDomainTable("JPN",
							EPPIdnTableInfoDomainTable.Type.LANGUAGE,
							"Japanese (JPN)", "en", false));
					theResp.setDomain(theDomain);
				}
				else {
					// Return default response for a non-IDN domain that does
					// not require the IDN mapping extension on create.
					EPPIdnTableInfoDomain theDomain = new EPPIdnTableInfoDomain(
							theCommand.getDomain(), true, false);
					theResp.setDomain(theDomain);
				}
				break;
			case TABLE_FORM:
				cat.debug("doIdnTableInfo: Command with table form.");
				// Provide Table Info Form response
				if (theCommand.getTable().equalsIgnoreCase("CHI")) {
					EPPIdnTableInfoTable theTable = new EPPIdnTableInfoTable(
							"CHI", EPPIdnTableInfoTable.Type.LANGUAGE,
							"Chinese (CHI)", new GregorianCalendar(2014, 11,
									24, 9, 30).getTime());
					theTable.setVersion("1.0");
					theTable.setEffectiveDate(new GregorianCalendar(2014, 11,
							24).getTime());
					theTable.setVariantGen(new Boolean(true));
					theTable.setUrl("https://www.iana.org/domains/idn-tables/tables/tld_chi_1.0.txt");
					theResp.setTable(theTable);
				}
				else if (theCommand.getTable().equalsIgnoreCase("THAI")) {
					EPPIdnTableInfoTable theTable = new EPPIdnTableInfoTable(
							"THAI", EPPIdnTableInfoTable.Type.SCRIPT, "Thai",
							new GregorianCalendar(2014, 8, 16, 9, 20).getTime());
					theTable.setVersion("1.0");
					theTable.setEffectiveDate(new GregorianCalendar(2014, 11,
							24).getTime());
					theTable.setVariantGen(new Boolean(false));
					theTable.setUrl("https://www.iana.org/domains/idn-tables/tables/tld_thai_1.0.txt");
					theResp.setTable(theTable);
				}
				else {
					EPPResponse theErrorResponse = new EPPResponse(theTransId);
					theErrorResponse.setResult(EPPResult.OBJECT_DOES_NOT_EXIST);
					theErrorResponse.getResult().addExtValueReason(
							"IDN Table \"" + theCommand.getTable()
									+ "\" does not exist");
					return new EPPEventResponse(theErrorResponse);
				}
				break;
			case LIST_FORM:
				cat.debug("doIdnTableInfo: Command with list form.");
				// Provide List Form response
				theResp.addListItem(new EPPIdnTableInfoListItem("CHI",
						new GregorianCalendar(2015, 2, 4, 9, 30).getTime()));
				theResp.addListItem(new EPPIdnTableInfoListItem("JPN",
						new GregorianCalendar(2015, 1, 1, 9, 40).getTime()));
				theResp.addListItem(new EPPIdnTableInfoListItem("THAI",
						new GregorianCalendar(2014, 8, 16, 9, 20).getTime()));
				break;
			case UNDEFINED_FORM:
				// Return error
				cat.error("doIdnTableInfo: Command with undefined form.");
				EPPResponse theErrorResponse = new EPPResponse(theTransId);
				theErrorResponse.setResult(EPPResult.PARAM_VALUE_POLICY_ERROR);
				theErrorResponse.getResult().addExtValueReason(
						"IDN Table Info using undefined form");
				return new EPPEventResponse(theErrorResponse);

		}

		cat.debug("doIdnTableInfo: exit");
		return new EPPEventResponse(theResp);
	}

}
