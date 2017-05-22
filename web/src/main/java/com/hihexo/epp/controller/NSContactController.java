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
import com.hihexo.epp.model.NSDomainTransferParam;
import com.verisign.epp.codec.contact.*;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.resellerext.EPPResellerExtUpdate.Action;
import com.verisign.epp.interfaces.EPPCommandException;
import com.verisign.epp.interfaces.EPPContact;
import com.verisign.epp.interfaces.EPPSession;
import com.verisign.epp.namestore.interfaces.NSContact;
import com.verisign.epp.namestore.interfaces.NSSubProduct;
import com.verisign.epp.util.InvalidateSessionException;
import com.verisign.epp.util.TestUtil;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Vector;

/**
 * <code>NSContact</code> interface. This test utilizes
 * the EPP session pool and exercises all of the operations defined in
 * <code>NSContact</code> and the base class <code>EPPContact</code>.
 * 
 * @see NSContact
 * @see EPPContact
 */
@Controller
@RequestMapping("/contact")
public class NSContactController extends BaseNSController{
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(NSContactController.class);
	/**
	 * <code>NSContact.sendCreate</code> command.
	 */
	@RequestMapping(value = "/transfer/request",method = RequestMethod.POST) 	@SystemControllerLog(description = "创建联系人")
	@ResponseBody
	public ResultVo testContactCreate(HttpServletRequest request, @RequestBody NSDomainTransferParam params) {
		printStart("testContactCreate");

		EPPSession theSession = null;
		EPPResponse theResponse = null;

		try {
			theSession = this.borrowSession();
			NSContact theContact = new NSContact(theSession);

			try {
				logger.debug("\n----------------------------------------------------------------");

				String theContactName = this.makeContactName();

				logger.debug("testContactCreate: Create "
						+ theContactName);

				theContact.setTransId("ABC-12345-XYZ");
				theContact.setAuthorizationId("ClientXYZ");
				theContact.addContactId(theContactName);
				theContact.setVoicePhone("+1.7035555555");
				theContact.setVoiceExt("123");
				theContact.setFaxNumber("+1.7035555556");
				theContact.setFaxExt("456");
				theContact.setEmail("jdoe@example.com");

				// Streets
				Vector streets = new Vector();
				streets.addElement("123 Example Dr.");
				streets.addElement("Suite 100");
				streets.addElement("This is third line");

				EPPContactAddress address = new EPPContactAddress();

				address.setStreets(streets);
				address.setCity("Dulles");
				address.setStateProvince("VA");
				address.setPostalCode("20166-6503");
				address.setCountry("US");

				EPPContactPostalDefinition name = new EPPContactPostalDefinition(
						EPPContactPostalDefinition.ATTR_TYPE_LOC);

				name.setName("John Doe");
				name.setOrg("Example Inc.");
				name.setAddress(address);

				theContact.addPostalInfo(name);

				// this is not a valid Example but it will do
				EPPContactAddress Intaddress = new EPPContactAddress();

				Intaddress.setStreets(streets);
				Intaddress.setCity("Dulles");
				Intaddress.setStateProvince("VA");
				Intaddress.setPostalCode("20166-6503");
				Intaddress.setCountry("US");

				EPPContactPostalDefinition Intname = new EPPContactPostalDefinition(
						EPPContactPostalDefinition.ATTR_TYPE_INT);

				Intname.setName("John Doe");
				Intname.setOrg("Example Inc.");
				Intname.setAddress(Intaddress);

				theContact.addPostalInfo(Intname);

				// disclose names
				Vector names = new Vector();

				// names.addElement(new
				// EPPContactDiscloseName(EPPContactDiscloseName.ATTR_TYPE_LOC));
				names.addElement(new EPPContactDiscloseName(
						EPPContactDiscloseName.ATTR_TYPE_INT));

				// disclose orgs
				Vector orgs = new Vector();
				orgs.addElement(new EPPContactDiscloseOrg(
						EPPContactDiscloseOrg.ATTR_TYPE_LOC));
				orgs.addElement(new EPPContactDiscloseOrg(
						EPPContactDiscloseOrg.ATTR_TYPE_INT));

				// disclose addresses
				Vector addresses = new Vector();
				addresses.addElement(new EPPContactDiscloseAddress(
						EPPContactDiscloseAddress.ATTR_TYPE_LOC));
				addresses.addElement(new EPPContactDiscloseAddress(
						EPPContactDiscloseAddress.ATTR_TYPE_INT));

				// disclose
				EPPContactDisclose disclose = new EPPContactDisclose();
				disclose.setFlag("0");
				disclose.setNames(names);
				disclose.setOrgs(orgs);
				disclose.setAddresses(addresses);
				disclose.setVoice("");
				disclose.setFax("");
				disclose.setEmail("");

				theContact.setDisclose(disclose);

				theResponse = theContact.sendCreate();

				// -- Output all of the response attributes
				logger.debug("testContactCreate: Response == ["
						+ theResponse + "]\n\n");
				return renderSuccess(theResponse);
			}

			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

		}
		catch (InvalidateSessionException ex) {
			this.invalidateSession(theSession);
			theSession = null;
			return renderError(ex.getMessage());
		}
		finally {
			if (theSession != null) {
				this.returnSession(theSession);
			}

		}

		printEnd("testContactCreate");
		return renderError("unknown");
	}


	/**
	 * <code>NSContact.sendContactCheck</code> command.
	 */
	public void testContactCheck() {
		printStart("testContactCheck");

		EPPSession theSession = null;
		EPPContactCheckResp theResponse = null;
		try {
			theSession = this.borrowSession();
			NSContact theContact = new NSContact(theSession);

			try {

				logger.debug("\n----------------------------------------------------------------");

				String theContactName = this.makeContactName();
				logger.debug("testContactCheck: Check single contact id ("
								+ theContactName + ")");
				theContact.setTransId("ABC-12345-XYZ");

				theContact.addContactId(theContactName);
				theContact.setSubProductID(NSSubProduct.COM);

				theResponse = theContact.sendCheck();

				logger.debug("Response Type = " + theResponse.getType());

				logger.debug("Response.TransId.ServerTransId = "
						+ theResponse.getTransId().getServerTransId());

				logger.debug("Response.TransId.ServerTransId = "
						+ theResponse.getTransId().getClientTransId());

				// Output all of the response attributes
				logger.debug("\ntestContactCheck: Response = ["
						+ theResponse + "]");

				// For each result
				for (int i = 0; i < theResponse.getCheckResults().size(); i++) {
					EPPContactCheckResult currResult = (EPPContactCheckResult) theResponse
							.getCheckResults().elementAt(i);

					if (currResult.isAvailable()) {
						logger.debug("testContactCheck: Contact "
								+ currResult.getId() + " is available");
					}
					else {
						logger.debug("testContactCheck: Contact "
								+ currResult.getId() + " is not available");
					}
				}

				this.handleResponse(theResponse);
			}
			catch (Exception e) {
				TestUtil.handleException(theSession, e);
			}

			try {
				// Check multiple contact names
				logger.debug("\n----------------------------------------------------------------");
				logger.debug("testContactCheck: Check multiple contact names (ns1.example.com, ns2.example.com, ns3.example.com)");
				theContact.setTransId("ABC-12345-XYZ");

				/**
				 * Add multiple contact ids
				 */
				for (int i = 0; i <= 10; i++) {
					theContact.addContactId(this.makeContactName());
				}
				theContact.setSubProductID(NSSubProduct.COM);

				theResponse = theContact.sendCheck();

				// Output all of the response attributes
				logger.debug("\ntestContactCheck: Response = ["
						+ theResponse + "]");
				logger.debug("Client Transaction Id = "
						+ theResponse.getTransId().getClientTransId());
				logger.debug("Server Transaction Id = "
						+ theResponse.getTransId().getServerTransId());

				// For each result
				for (int i = 0; i < theResponse.getCheckResults().size(); i++) {
					EPPContactCheckResult currResult = (EPPContactCheckResult) theResponse
							.getCheckResults().elementAt(i);

					if (currResult.isAvailable()) {
						logger.debug("testContactCheck: Contact "
								+ currResult.getId() + " is available");
					}
					else {
						logger.debug("testContactCheck: Contact "
								+ currResult.getId() + " is not available");
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

		printEnd("testContactCheck");
	}

	/**
	 * <code>NSContact.sendContactInfo</code> command.
	 */
	public void testContactInfo() {
		printStart("testContactInfo");

		EPPSession theSession = null;
		EPPContactInfoResp theResponse = null;
		try {
			theSession = this.borrowSession();
			NSContact theContact = new NSContact(theSession);

			try {
				logger.debug("\ntestContactInfo: Contact info");

				theContact.setTransId("ABC-12345-XYZ");

				theContact.addContactId(this.makeContactName());
				theContact.setSubProductID(NSSubProduct.COM);

				theResponse = theContact.sendInfo();

				// -- Output all of the response attributes
				logger.debug("testContactInfo: Response = ["
						+ theResponse + "]\n\n");

				// -- Output required response attributes using accessors
				logger.debug("testContactInfo: id = "
						+ theResponse.getId());

				Vector postalContacts = null;

				if (theResponse.getPostalInfo().size() > 0) {
					postalContacts = theResponse.getPostalInfo();

					for (int j = 0; j < postalContacts.size(); j++) {
						// Name
						logger.debug("testContactInfo:\t\tname = "
								+ ((EPPContactPostalDefinition) postalContacts
										.elementAt(j)).getName());

						// Organization
						System.out
								.println("testContactInfo:\t\torganization = "
										+ ((EPPContactPostalDefinition) postalContacts
												.elementAt(j)).getOrg());

						EPPContactAddress address = ((EPPContactPostalDefinition) postalContacts
								.elementAt(j)).getAddress();

						for (int i = 0; i < address.getStreets().size(); i++) {
							logger.debug("testContactInfo:\t\tstreet"
									+ (i + 1) + " = "
									+ address.getStreets().elementAt(i));
						}

						// Address City
						logger.debug("testContactInfo:\t\tcity = "
								+ address.getCity());

						// Address State/Province
						System.out
								.println("testContactInfo:\t\tstate province = "
										+ address.getStateProvince());

						// Address Postal Code
						logger.debug("testContactInfo:\t\tpostal code = "
								+ address.getPostalCode());

						// Address County
						logger.debug("testContactInfo:\t\tcountry = "
								+ address.getCountry());
					}
				}

				// Contact E-mail
				logger.debug("testContactInfo:\temail = "
						+ theResponse.getEmail());

				// Contact Voice
				logger.debug("testContactInfo:\tvoice = "
						+ theResponse.getVoice());

				// Contact Voice Extension
				logger.debug("testContactInfo:\tvoice ext = "
						+ theResponse.getVoiceExt());

				// Contact Fax
				logger.debug("testContactInfo:\tfax = "
						+ theResponse.getFax());

				// Contact Fax Extension
				logger.debug("testContactInfo:\tfax ext = "
						+ theResponse.getFaxExt());

				// Client Id
				logger.debug("testContactInfo: client id = "
						+ theResponse.getClientId());

				// Created By
				logger.debug("testContactInfo: created by = "
						+ theResponse.getCreatedBy());

				// Created Date
				logger.debug("testContactInfo: create date = "
						+ theResponse.getCreatedDate());

				// -- Output optional response attributes using accessors
				// Contact Fax
				if (theResponse.getFax() != null) {
					logger.debug("testContactInfo:\tfax = "
							+ theResponse.getFax());
				}

				// Contact Voice
				if (theResponse.getVoice() != null) {
					logger.debug("testContactInfo:\tVoice = "
							+ theResponse.getVoice());
				}

				// Last Updated By
				if (theResponse.getLastUpdatedBy() != null) {
					logger.debug("testContactInfo: last updated by = "
							+ theResponse.getLastUpdatedBy());
				}

				// Last Updated Date
				if (theResponse.getLastUpdatedDate() != null) {
					logger.debug("testContactInfo: last updated date = "
							+ theResponse.getLastUpdatedDate());
				}

				// Last Transfer Date
				if (theResponse.getLastTransferDate() != null) {
					logger.debug("testContactInfo: last updated date = "
							+ theResponse.getLastTransferDate());
				}

				// Authorization Id
				if (theResponse.getAuthInfo() != null) {
					logger.debug("testContactInfo: authorization info = "
							+ theResponse.getAuthInfo().getPassword());
				}

				// Disclose
				if (theResponse.getDisclose() != null) {
					logger.debug("testContactInfo: disclose info = "
							+ theResponse.getDisclose());
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

		printEnd("testContactInfo");
	}

	/**
	 * <code>NSContact.sendDelete</code> command.
	 */
	public void testContactDelete() {
		printStart("testContactDelete");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			NSContact theContact = new NSContact(theSession);

			try {
				logger.debug("\ntestContactDelete: Contact delete");

				theContact.setTransId("ABC-12345-XYZ");

				theContact.addContactId(this.makeContactName());
				theContact.setSubProductID(NSSubProduct.COM);

				theResponse = theContact.sendDelete();

				// -- Output all of the response attributes
				logger.debug("testContactDelete: Response = ["
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

		printEnd("testContactDelete");
	}

	/**
	 * <code>NSContact.sendUpdate</code> command.
	 */
	public void testContactUpdate() {
		printStart("testContactUpdate");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			NSContact theContact = new NSContact(theSession);

			try {

				theContact.setTransId("ABC-12345-XYZ");

				String theContactName = this.makeContactName();

				logger.debug("\ncontactUpdate: Contact " + theContactName
						+ " update");

				theContact.addContactId(theContactName);
				theContact.setSubProductID(NSSubProduct.COM);

				// Streets
				Vector streets = new Vector();
				streets.addElement("123 Example Dr.");
				streets.addElement("Suite 100");
				streets.addElement("This is third line");

				// Address
				EPPContactAddress address = new EPPContactAddress(streets,
						"Dulles", "VA", "20166-6503", "US");

				EPPContactPostalDefinition postal = new EPPContactPostalDefinition(
						"Joe Brown", "Example Corp.",
						EPPContactPostalDefinition.ATTR_TYPE_LOC, address);

				// statuses
				theContact.addStatus(EPPContact.STAT_PENDING_DELETE);

				theContact.addPostalInfo(postal);
				theContact.setVoicePhone("+1.7035555555");
				theContact.setVoiceExt("456");
				theContact.setFaxNumber("+1.7035555555");
				theContact.setFaxExt("789");
				theContact.setAuthorizationId("ClientXYZ");

				// disclose names
				Vector names = new Vector();

				// names.addElement(new
				// EPPContactDiscloseName(EPPContactDiscloseName.ATTR_TYPE_LOC));
				names.addElement(new EPPContactDiscloseName(
						EPPContactDiscloseName.ATTR_TYPE_INT));

				// disclose orgs
				Vector orgs = new Vector();
				orgs.addElement(new EPPContactDiscloseOrg(
						EPPContactDiscloseOrg.ATTR_TYPE_LOC));
				orgs.addElement(new EPPContactDiscloseOrg(
						EPPContactDiscloseOrg.ATTR_TYPE_INT));

				// disclose addresses
				Vector addresses = new Vector();
				addresses.addElement(new EPPContactDiscloseAddress(
						EPPContactDiscloseAddress.ATTR_TYPE_LOC));
				addresses.addElement(new EPPContactDiscloseAddress(
						EPPContactDiscloseAddress.ATTR_TYPE_INT));

				// disclose
				EPPContactDisclose disclose = new EPPContactDisclose();
				disclose.setFlag("0");
				disclose.setNames(names);
				disclose.setOrgs(orgs);
				disclose.setAddresses(addresses);
				disclose.setVoice("");
				disclose.setFax("");
				disclose.setEmail("");

				theContact.setDisclose(disclose);

				// Execute update
				theResponse = theContact.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("contactUpdate: Response = [" + theResponse
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

		printEnd("testContactUpdate");
	}

	/**
	 * Unit test using {@link NSContact#setResellerId(String)} to set the
	 * reseller identifier on create and
	 * {@link NSContact#updateResellerId(Action, String)}
	 * to update the reseller identifier of an existing contact.
	 */
	public void testResellerId() {
		printStart("testResellerId");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			NSContact theContact = new NSContact(theSession);

			// Reseller Identifier with Host Create
			try {
				System.out
						.println("\ntestResellerId: Contact create with reseller identifier");

				theContact.setTransId("ABC-12345-XYZ");
				theContact.setAuthorizationId("ClientXYZ");
				theContact.addContactId("resellercontact");
				theContact.setVoicePhone("+1.7035555555");
				theContact.setVoiceExt("123");
				theContact.setFaxNumber("+1.7035555556");
				theContact.setFaxExt("456");
				theContact.setEmail("jdoe@example.com");

				// Streets
				Vector streets = new Vector();
				streets.addElement("123 Example Dr.");
				streets.addElement("Suite 100");
				streets.addElement("This is third line");

				EPPContactAddress address = new EPPContactAddress();

				address.setStreets(streets);
				address.setCity("Dulles");
				address.setStateProvince("VA");
				address.setPostalCode("20166-6503");
				address.setCountry("US");

				EPPContactPostalDefinition name = new EPPContactPostalDefinition(
						EPPContactPostalDefinition.ATTR_TYPE_LOC);

				name.setName("John Doe");
				name.setOrg("Example Inc.");
				name.setAddress(address);

				theContact.addPostalInfo(name);

				// this is not a valid Example but it will do
				EPPContactAddress Intaddress = new EPPContactAddress();

				Intaddress.setStreets(streets);
				Intaddress.setCity("Dulles");
				Intaddress.setStateProvince("VA");
				Intaddress.setPostalCode("20166-6503");
				Intaddress.setCountry("US");

				EPPContactPostalDefinition Intname = new EPPContactPostalDefinition(
						EPPContactPostalDefinition.ATTR_TYPE_INT);

				Intname.setName("John Doe");
				Intname.setOrg("Example Inc.");
				Intname.setAddress(Intaddress);

				theContact.addPostalInfo(Intname);

				// disclose names
				Vector names = new Vector();

				// names.addElement(new
				// EPPContactDiscloseName(EPPContactDiscloseName.ATTR_TYPE_LOC));
				names.addElement(new EPPContactDiscloseName(
						EPPContactDiscloseName.ATTR_TYPE_INT));

				// disclose orgs
				Vector orgs = new Vector();
				orgs.addElement(new EPPContactDiscloseOrg(
						EPPContactDiscloseOrg.ATTR_TYPE_LOC));
				orgs.addElement(new EPPContactDiscloseOrg(
						EPPContactDiscloseOrg.ATTR_TYPE_INT));

				// disclose addresses
				Vector addresses = new Vector();
				addresses.addElement(new EPPContactDiscloseAddress(
						EPPContactDiscloseAddress.ATTR_TYPE_LOC));
				addresses.addElement(new EPPContactDiscloseAddress(
						EPPContactDiscloseAddress.ATTR_TYPE_INT));

				// disclose
				EPPContactDisclose disclose = new EPPContactDisclose();
				disclose.setFlag("0");
				disclose.setNames(names);
				disclose.setOrgs(orgs);
				disclose.setAddresses(addresses);
				disclose.setVoice("");
				disclose.setFax("");
				disclose.setEmail("");

				theContact.setDisclose(disclose);

				theContact.setResellerId("myreseller");

				theResponse = theContact.sendCreate();

				// -- Output all of the response attributes
				logger.debug("testResellerId: Create Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Reseller Identifier with Contact Update - ADD
			try {
				System.out
						.println("\ntestResellerId: Contact update with reseller identifier - ADD");

				theContact.setTransId("ABC-12345-XYZ");

				theContact.addContactId("resellercontact1");
				theContact.setSubProductID("tld");

				theContact.updateResellerId(Action.ADD, "myreseller");

				theResponse = theContact.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("testResellerId: ADD Update Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);
			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Reseller Identifier with Contact Update - CHG
			try {
				System.out
						.println("\ntestResellerId: Contact update with reseller identifier - CHG");

				theContact.setTransId("ABC-12345-XYZ");

				theContact.addContactId("resellercontact1");
				theContact.setSubProductID("tld");

				theContact.updateResellerId(Action.CHG, "myreseller2");

				theResponse = theContact.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("testResellerId: CHG Update Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);
			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Reseller Identifier with Contact Update - REM
			try {
				System.out
						.println("\ntestResellerId: Contact update with reseller identifier - REM");

				theContact.setTransId("ABC-12345-XYZ");

				theContact.addContactId("resellercontact1");
				theContact.setSubProductID("tld");

				theContact.updateResellerId(Action.REM, "myreseller2");

				theResponse = theContact.sendUpdate();

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
	}

} // End class NSContactController
