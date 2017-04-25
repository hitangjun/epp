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
import com.hihexo.epp.namestore.interfaces.NSSubProduct;
import com.verisign.epp.codec.coaext.EPPCoaExtAttr;
import com.verisign.epp.codec.coaext.EPPCoaExtInfData;
import com.verisign.epp.codec.coaext.EPPCoaExtKey;
import com.verisign.epp.codec.coaext.EPPCoaExtValue;
import com.verisign.epp.codec.domain.*;
import com.verisign.epp.codec.gen.EPPFactory;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.premiumdomain.EPPPremiumDomainCheck;
import com.verisign.epp.codec.premiumdomain.EPPPremiumDomainCheckResp;
import com.verisign.epp.codec.premiumdomain.EPPPremiumDomainCheckResult;
import com.verisign.epp.codec.premiumdomain.EPPPremiumDomainReAssignCmd;
import com.verisign.epp.codec.relateddomainext.EPPRelatedDomainExtAuthInfo;
import com.verisign.epp.codec.relateddomainext.EPPRelatedDomainExtCreateResp;
import com.verisign.epp.codec.relateddomainext.EPPRelatedDomainExtDomain;
import com.verisign.epp.codec.relateddomainext.EPPRelatedDomainExtPeriod;
import com.verisign.epp.codec.resellerext.EPPResellerExtUpdate.Action;
import com.verisign.epp.codec.rgpext.EPPRgpExtReport;
import com.verisign.epp.codec.rgpext.EPPRgpExtReportText;
import com.verisign.epp.codec.secdnsext.v11.EPPSecDNSAlgorithm;
import com.verisign.epp.codec.secdnsext.v11.EPPSecDNSExtDsData;
import com.verisign.epp.codec.secdnsext.v11.EPPSecDNSExtInfData;
import com.verisign.epp.codec.secdnsext.v11.EPPSecDNSExtKeyData;
import com.verisign.epp.codec.whois.EPPWhoisInfData;
import com.verisign.epp.interfaces.EPPCommandException;
import com.verisign.epp.interfaces.EPPDomain;
import com.verisign.epp.interfaces.EPPSession;
import com.verisign.epp.namestore.interfaces.NSDomain;
import com.verisign.epp.util.InvalidateSessionException;
import com.verisign.epp.util.TestUtil;
import org.junit.Assert;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/domain")
public class NSDomainController extends BaseNSController{
	
	public static org.slf4j.Logger logger = LoggerFactory.getLogger(NSDomainController.class);

	@RequestMapping(value = "/create",method = RequestMethod.POST)
	@SystemControllerLog(description = "创建域名")
	public void doDomainCreate(HttpServletRequest request) {
		printStart("doDomainCreate");

		EPPSession theSession = null;
		EPPDomainCreateResp theResponse = null;

		try {
			theSession = this.borrowSession();
			NSDomain theDomain = new NSDomain(theSession);

			try {
				logger.debug("\n----------------------------------------------------------------");
				String theDomainName = this.makeDomainName();
				logger.debug("domainCreate: Create " + theDomainName
						+ " with no optional attributes");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);
				theDomain.setAuthString(getAuthString());

				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("domainCreate: Response = [" + theResponse
						+ "]\n\n");

				// -- Output response attributes using accessors
				logger.debug("domainCreate: name = "
						+ theResponse.getName());
				logger.debug("domainCreate: expiration date = "
						+ theResponse.getExpirationDate());
			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				logger.debug("\n----------------------------------------------------------------");

				theDomain.setTransId(getClientTransId(request));

				String theDomainName = this.makeDomainName();
				logger.debug("domainCreate: Create " + theDomainName
						+ " with all optional attributes");
				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

//				for (int i = 0; i <= 20; i++) {
					theDomain.addHostName(this.makeHostName(theDomainName));//DNS 解析？？// FIXME: 2017/4/25
//				}

				// Is the contact mapping supported?
				//不同的联系人信息
				if (EPPFactory.getInstance().hasService(
						EPPDomainMapFactory.NS_CONTACT)) {
					// Add domain contacts
					theDomain.addContact("SH0000",
							EPPDomain.CONTACT_ADMINISTRATIVE);
					theDomain.addContact("SH0000", EPPDomain.CONTACT_TECHNICAL);
					theDomain.addContact("SH0000", EPPDomain.CONTACT_BILLING);
				}

				//设置注册期限 10年
				theDomain.setPeriodLength(10);
				theDomain.setPeriodUnit(EPPDomain.PERIOD_YEAR);
				theDomain.setAuthString(getAuthString());

				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("domainCreate: Response = [" + theResponse
						+ "]\n\n");

				// -- Output response attributes using accessors
				logger.debug("domainCreate: name = "
						+ theResponse.getName());

				logger.debug("domainCreate: expiration date = "
						+ theResponse.getExpirationDate());
			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				logger.debug("\n----------------------------------------------------------------");

				String theDomainName = this.makeDomainName();

				logger.debug("domainCreate: Create " + theDomainName
						+ " with SecDNS Extension");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				theDomain.setAuthString(getAuthString());

				// -- Add secDNS Extension
				// instantiate a secDNS:keyData object
				EPPSecDNSExtKeyData keyData = new EPPSecDNSExtKeyData();
				keyData.setFlags(EPPSecDNSExtKeyData.FLAGS_ZONE_KEY_SEP);
				keyData.setProtocol(EPPSecDNSExtKeyData.DEFAULT_PROTOCOL);
				keyData.setAlg(EPPSecDNSAlgorithm.RSASHA1);
				keyData.setPubKey("AQPmsXk3Q1ngNSzsH1lrX63mRIhtwkkK+5Zj"
						+ "vxykBCV1NYne83+8RXkBElGb/YJ1n4TacMUs"
						+ "poZap7caJj7MdOaADKmzB2ci0vwpubNyW0t2"
						+ "AnaQqpy1ce+07Y8RkbTC6xCeEw1UQZ73PzIO"
						+ "OvJDdjwPxWaO9F7zSxnGpGt0WtuItQ==");

				// instantiate another secDNS:keyData object
				EPPSecDNSExtKeyData keyData2 = new EPPSecDNSExtKeyData(
						EPPSecDNSExtKeyData.FLAGS_ZONE_KEY_SEP,
						EPPSecDNSExtKeyData.DEFAULT_PROTOCOL,
						EPPSecDNSAlgorithm.RSASHA1,
						"AQOxXpFbRp7+zPBoTt6zL7Af0aEKzpS4JbVB"
								+ "5ofk5E5HpXuUmU+Hnt9hm2kMph6LZdEEL142"
								+ "nq0HrgiETFCsN/YM4Zn+meRkELLpCG93Cu/H"
								+ "hwvxfaZenUAAA6Vb9FwXQ1EMYRW05K/gh2Ge"
								+ "w5Sk/0o6Ev7DKG2YiDJYA17QsaZtFw==");

				// instantiate a secDNS:dsData object
				EPPSecDNSExtDsData dsData = new EPPSecDNSExtDsData();
				dsData.setKeyTag(34095);
				dsData.setAlg(EPPSecDNSAlgorithm.RSASHA1);
				dsData.setDigestType(EPPSecDNSExtDsData.SHA1_DIGEST_TYPE);
				dsData.setDigest("6BD4FFFF11566D6E6A5BA44ED0018797564AA289");
				dsData.setKeyData(keyData);

				// instantiate another secDNS:dsData object
				EPPSecDNSExtDsData dsData2 = new EPPSecDNSExtDsData(10563,
						EPPSecDNSAlgorithm.RSASHA1,
						EPPSecDNSExtDsData.SHA1_DIGEST_TYPE,
						"9C20674BFF957211D129B0DFE9410AF753559D4B", keyData2);

				// dsData Records
				List dsDataRecords = new ArrayList();
				dsDataRecords.add(dsData);
				dsDataRecords.add(dsData2);

				//
				theDomain.setSecDNSCreate(dsDataRecords);
				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("domainCreate: Response = [" + theResponse
						+ "]\n\n");

				// -- Output response attributes using accessors
				logger.debug("domainCreate: name = "
						+ theResponse.getName());

				logger.debug("domainCreate: expiration date = "
						+ theResponse.getExpirationDate());

			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				logger.debug
						("\n----------------------------------------------------------------");

				String theDomainName = this.makeDomainName();

				logger.debug("domainCreate: Create " + theDomainName
						+ " with SecDNS and COA Extensions");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				theDomain.setAuthString("ClientX");

				// -- Add secDNS Extension
				// instantiate a secDNS:keyData object
				EPPSecDNSExtKeyData keyData = new EPPSecDNSExtKeyData();
				keyData.setFlags(EPPSecDNSExtKeyData.FLAGS_ZONE_KEY_SEP);
				keyData.setProtocol(EPPSecDNSExtKeyData.DEFAULT_PROTOCOL);
				keyData.setAlg(EPPSecDNSAlgorithm.RSASHA1);
				keyData.setPubKey("AQPmsXk3Q1ngNSzsH1lrX63mRIhtwkkK+5Zj"
						+ "vxykBCV1NYne83+8RXkBElGb/YJ1n4TacMUs"
						+ "poZap7caJj7MdOaADKmzB2ci0vwpubNyW0t2"
						+ "AnaQqpy1ce+07Y8RkbTC6xCeEw1UQZ73PzIO"
						+ "OvJDdjwPxWaO9F7zSxnGpGt0WtuItQ==");

				// instantiate another secDNS:keyData object
				EPPSecDNSExtKeyData keyData2 = new EPPSecDNSExtKeyData(
						EPPSecDNSExtKeyData.FLAGS_ZONE_KEY_SEP,
						EPPSecDNSExtKeyData.DEFAULT_PROTOCOL,
						EPPSecDNSAlgorithm.RSASHA1,
						"AQOxXpFbRp7+zPBoTt6zL7Af0aEKzpS4JbVB"
								+ "5ofk5E5HpXuUmU+Hnt9hm2kMph6LZdEEL142"
								+ "nq0HrgiETFCsN/YM4Zn+meRkELLpCG93Cu/H"
								+ "hwvxfaZenUAAA6Vb9FwXQ1EMYRW05K/gh2Ge"
								+ "w5Sk/0o6Ev7DKG2YiDJYA17QsaZtFw==");

				// instantiate a secDNS:dsData object
				EPPSecDNSExtDsData dsData = new EPPSecDNSExtDsData();
				dsData.setKeyTag(34095);
				dsData.setAlg(EPPSecDNSAlgorithm.RSASHA1);
				dsData.setDigestType(EPPSecDNSExtDsData.SHA1_DIGEST_TYPE);
				dsData.setDigest("6BD4FFFF11566D6E6A5BA44ED0018797564AA289");
				dsData.setKeyData(keyData);

				// instantiate another secDNS:dsData object
				EPPSecDNSExtDsData dsData2 = new EPPSecDNSExtDsData(10563,
						EPPSecDNSAlgorithm.RSASHA1,
						EPPSecDNSExtDsData.SHA1_DIGEST_TYPE,
						"9C20674BFF957211D129B0DFE9410AF753559D4B", keyData2);

				// dsData Records
				List dsDataRecords = new ArrayList();
				dsDataRecords.add(dsData);
				dsDataRecords.add(dsData2);

				theDomain.setSecDNSCreate(dsDataRecords);

				// Client Object Attributes
				EPPCoaExtKey key = new EPPCoaExtKey("KEY1");
				EPPCoaExtValue value = new EPPCoaExtValue("value1");
				EPPCoaExtAttr attr = new EPPCoaExtAttr();
				attr.setKey(key);
				attr.setValue(value);

				List attrList = new ArrayList();
				attrList.add(attr);

				theDomain.setCoaCreate(attrList);

				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("domainCreate: Response = [" + theResponse
						+ "]\n\n");

				// -- Output response attributes using accessors
				logger.debug("domainCreate: name = "
						+ theResponse.getName());

				logger.debug("domainCreate: expiration date = "
						+ theResponse.getExpirationDate());

			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				logger.debug
						("\n----------------------------------------------------------------");

				String theDomainName = this.makeDomainName();

				logger.debug("domainCreate: Create " + theDomainName
						+ " with COA Extension");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				theDomain.setAuthString("ClientX");

				// Client Object Attributes
				EPPCoaExtKey key = new EPPCoaExtKey("KEY1");
				EPPCoaExtValue value = new EPPCoaExtValue("value1");
				EPPCoaExtAttr attr = new EPPCoaExtAttr();
				attr.setKey(key);
				attr.setValue(value);

				List attrList = new ArrayList();
				attrList.add(attr);

				theDomain.setCoaCreate(attrList);

				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("domainCreate: Response = [" + theResponse
						+ "]\n\n");

				// -- Output response attributes using accessors
				logger.debug("domainCreate: name = "
						+ theResponse.getName());

				logger.debug("domainCreate: expiration date = "
						+ theResponse.getExpirationDate());

			}
			catch (Exception ex) {
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

		printEnd("doDomainCreate");
	}

	@RequestMapping(value = "/relatedcreate",method = RequestMethod.POST) 	@SystemControllerLog(description = "关联域名")
	public void  doRelatedDomainCreate(HttpServletRequest request) {
		printStart("testRelatedDomainCreate ");

		EPPSession theSession = null;
		EPPDomainCreateResp theResponse = null;

		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {
				logger.debug
						("\ntestRelatedDomainCreate: Domain create of example.com and related domains");

				theDomain.addDomainName("example.com");
				theDomain.setTransId("ABC-12349");
				theDomain.setAuthString("2fooBAR");

				final EPPRelatedDomainExtAuthInfo authInfo = new EPPRelatedDomainExtAuthInfo(
						"relDom123!");
				final EPPRelatedDomainExtPeriod period = new EPPRelatedDomainExtPeriod(
						5);
				theDomain.addRelatedDomain(new EPPRelatedDomainExtDomain(
						"domain1.com", authInfo, period));
				theDomain.addRelatedDomain(new EPPRelatedDomainExtDomain(
						"domain2.com", authInfo, period));
				theDomain.addRelatedDomain(new EPPRelatedDomainExtDomain(
						"xn--idn.com", authInfo, period, "CHI"));

				theResponse = theDomain.sendRelatedCreate();

				// -- Output all of the response attributes
				logger.debug("testRelatedDomainCreate: Response = ["
						+ theResponse + "]\n\n");

				// -- Output the relDom:infData extension
				if (theResponse
						.hasExtension(EPPRelatedDomainExtCreateResp.class)) {
					final EPPRelatedDomainExtCreateResp relatedDomainCreData = (EPPRelatedDomainExtCreateResp) theResponse
							.getExtension(EPPRelatedDomainExtCreateResp.class);
					logger.debug
							("testRelatedDomainCreate: EPPRelatedDomainExtCreateResp = ["
									+ relatedDomainCreData + "]\n\n");

				}
				else {
					Assert.fail("testRelatedDomainCreate: EPPRelatedDomainExtCreateResp extension not included for domain-create with related domains.");
				}
			}
			catch (Exception ex) {
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

		printEnd("testRelatedDomainCreate");
	}

	/**
	 * <code>EPPDomain.sendCreate</code> for secDNS 1.1 using the
	 * DS Data Interface. The VeriSign servers only support the DS Data
	 * Interface. The following tests will be executed:<br>
	 * <ol>
	 * <li>Create for a Secure Delegation using the DS Data Interface with one
	 * DS.
	 * <li>Create for a Secure Delegation using the DS Data Interface with two
	 * DS. One DS created from key data.
	 * </ol>
	 */
	@RequestMapping(value = "/createdsdata",method = RequestMethod.POST)
	@SystemControllerLog(description = "创建DSdata")
	public void  testCreateDsDataInterface(HttpServletRequest request) {
		printStart("testCreateDsDataInterface");

		EPPSession theSession = null;
		EPPDomainCreateResp theResponse = null;

		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {
				logger.debug
						("\n----------------------------------------------------------------");

				String theDomainName = this.makeDomainName();

				logger.debug
						("testCreateDsDataInterface(1): domain = "
								+ theDomainName
								+ ", Create for a Secure Delegation using the DS Data Interface with one DS");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);
				theDomain.setAuthString("ClientX");

				// Add DS
				List dsDataList = new ArrayList();
				dsDataList.add(new EPPSecDNSExtDsData(12345,
						EPPSecDNSAlgorithm.DSA,
						EPPSecDNSExtDsData.SHA1_DIGEST_TYPE,
						"49FD46E6C4B45C55D4AC"));
				theDomain.setSecDNSCreate(dsDataList);

				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("testCreateDsDataInterface(1): Response = ["
						+ theResponse + "]\n\n");
			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				logger.debug
						("\n----------------------------------------------------------------");

				theDomain.setTransId(getClientTransId(request));

				String theDomainName = this.makeDomainName();

				logger.debug
						("testCreateDsDataInterface(2): domain = "
								+ theDomainName
								+ ", Create for a Secure Delegation using the DS Data Interface with two DS");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);
				theDomain.setAuthString("ClientX");

				// Add DS
				List dsDataList = new ArrayList();
				dsDataList.add(new EPPSecDNSExtDsData(12345,
						EPPSecDNSAlgorithm.DSA,
						EPPSecDNSExtDsData.SHA1_DIGEST_TYPE,
						"49FD46E6C4B45C55D4AC"));

				// Key Data associated with DS to add
				EPPSecDNSExtKeyData keyData = new EPPSecDNSExtKeyData();
				keyData.setFlags(EPPSecDNSExtKeyData.FLAGS_ZONE_KEY_SEP);
				keyData.setProtocol(EPPSecDNSExtKeyData.DEFAULT_PROTOCOL);
				keyData.setAlg(EPPSecDNSAlgorithm.RSASHA1);
				keyData.setPubKey("AQPmsXk3Q1ngNSzsH1lrX63mRIhtwkkK+5Zj"
						+ "vxykBCV1NYne83+8RXkBElGb/YJ1n4TacMUs"
						+ "poZap7caJj7MdOaADKmzB2ci0vwpubNyW0t2"
						+ "AnaQqpy1ce+07Y8RkbTC6xCeEw1UQZ73PzIO"
						+ "OvJDdjwPxWaO9F7zSxnGpGt0WtuItQ==");

				dsDataList.add(keyData.toDsData(
						"testCreateDsDataInterface.com",
						EPPSecDNSExtDsData.SHA1_DIGEST_TYPE));

				theDomain.setSecDNSCreate(dsDataList);

				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("testCreateDsDataInterface(2): Response = ["
						+ theResponse + "]\n\n");
			}
			catch (Exception ex) {
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

		printEnd("testCreateDsDataInterface");
	}

	/**
	 * <code>EPPDomain.sendUpdate</code> for secDNS 1.1 using the
	 * DS Data Interface. The VeriSign servers only support the DS Data
	 * Interface. The following tests will be executed:<br>
	 * <ol>
	 * <li>Adding and Removing DS Data using the DS Data Interface.
	 * <li>Remove all DS using &lt;secDNS:rem&gt; with &lt;secDNS:all&gt;.
	 * <li>Replacing all DS Data using the DS Data Interface.
	 * </ol>
	 */
	@RequestMapping(value = "/updatedsdata",method = RequestMethod.POST) 	@SystemControllerLog(description = "更新DSdata")
	public void  testUpdateDsDataInterface(HttpServletRequest request) {
		printStart("testUpdateDsDataInterface");

		EPPSession theSession = null;
		EPPResponse theResponse = null;

		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {
				logger.debug
						("\n----------------------------------------------------------------");

				String theDomainName = this.makeDomainName();

				logger.debug
						("testUpdateDsDataInterface(1): domain = "
								+ theDomainName
								+ ", Adding and Removing DS Data using the DS Data Interface");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);
				theDomain.setAuthString("ClientX");

				List addDsDataList = new ArrayList();
				addDsDataList.add(new EPPSecDNSExtDsData(12345,
						EPPSecDNSAlgorithm.DSA,
						EPPSecDNSExtDsData.SHA1_DIGEST_TYPE,
						"49FD46E6C4B45C55D4AC"));
				List remDsDataList = new ArrayList();
				remDsDataList.add(new EPPSecDNSExtDsData(12345,
						EPPSecDNSAlgorithm.DSA,
						EPPSecDNSExtDsData.SHA1_DIGEST_TYPE,
						"38EC35D5B3A34B44C39B"));
				// Add and remove DS data
				theDomain.setSecDNSUpdate(addDsDataList, remDsDataList);

				theResponse = theDomain.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("testUpdateDsDataInterface(1): Response = ["
						+ theResponse + "]\n\n");
			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				logger.debug("\n----------------------------------------------------------------");

				theDomain.setTransId(getClientTransId(request));

				String theDomainName = this.makeDomainName();

				logger.debug("testUpdateDsDataInterface(2): domain = "
								+ theDomainName
								+ ", Remove all DS and Key Data using <secDNS:rem> with <secDNS:all>");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);
				theDomain.setAuthString("ClientX");

				// Remove all DS data
				theDomain.setSecDNSUpdate(null, com.verisign.epp.namestore.interfaces.NSDomain.REM_ALL_DS);

				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("testUpdateDsDataInterface(2): Response = ["
						+ theResponse + "]\n\n");
			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				logger.debug("\n----------------------------------------------------------------");

				theDomain.setTransId(getClientTransId(request));

				String theDomainName = this.makeDomainName();

				logger.debug("testUpdateDsDataInterface(3): domain = "
								+ theDomainName
								+ ", Replacing all DS Data using the DS Data Interface");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);
				theDomain.setAuthString("ClientX");

				List addDsDataList = new ArrayList();
				addDsDataList.add(new EPPSecDNSExtDsData(12345,
						EPPSecDNSAlgorithm.DSA,
						EPPSecDNSExtDsData.SHA1_DIGEST_TYPE,
						"49FD46E6C4B45C55D4AC"));
				// Replace all DS data
				theDomain.setSecDNSUpdate(addDsDataList, com.verisign.epp.namestore.interfaces.NSDomain.REM_ALL_DS);

				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("testUpdateDsDataInterface(3): Response = ["
						+ theResponse + "]\n\n");
			}
			catch (Exception ex) {
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

		printEnd("testUpdateDsDataInterface");
	}

	/**
	 * <code>NSDomain.sendDomainCheck</code> command.
	 */
	@RequestMapping(value = "/check",method = RequestMethod.POST) 	@SystemControllerLog(description = "核查域名")
	public void  doDomainCheck(HttpServletRequest request) {
		printStart("doDomainCheck");

		EPPSession theSession = null;
		EPPDomainCheckResp theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {

				logger.debug("\n----------------------------------------------------------------");

				String theDomainName = this.makeDomainName();
				logger.debug("domainCheck: Check single domain name ("
						+ theDomainName + ")");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				theResponse = theDomain.sendCheck();

				logger.debug("Response Type = " + theResponse.getType());

				logger.debug("Response.TransId.ServerTransId = "
						+ theResponse.getTransId().getServerTransId());

				logger.debug("Response.TransId.ServerTransId = "
						+ theResponse.getTransId().getClientTransId());

				// Output all of the response attributes
				logger.debug("\ndomainCheck: Response = [" + theResponse
						+ "]");

				// For each result
				for (int i = 0; i < theResponse.getCheckResults().size(); i++) {
					EPPDomainCheckResult currResult = (EPPDomainCheckResult) theResponse
							.getCheckResults().elementAt(i);

					if (currResult.isAvailable()) {
						logger.debug("domainCheck: Domain "
								+ currResult.getName() + " is available");
					}
					else {
						logger.debug("domainCheck: Domain "
								+ currResult.getName() + " is not available");
					}
				}

				this.handleResponse(theResponse);
			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				// Check multiple domain names

				logger.debug("\n----------------------------------------------------------------");
				logger.debug("domainCheck: Check multiple domain names (example1.com, example2.com, example3.com)");
				theDomain.setTransId(getClientTransId(request));

				/**
				 * Add example(1-3).com
				 */
				theDomain.addDomainName("example1.com");
				theDomain.addDomainName("example2.com");
				theDomain.addDomainName("example3.com");
				theDomain.setSubProductID(NSSubProduct.COM);

				for (int i = 0; i <= 10; i++) {
					theDomain.addDomainName(this.makeDomainName());
				}

				theResponse = theDomain.sendCheck();

				// Output all of the response attributes
				logger.debug("\ndomainCheck: Response = [" + theResponse
						+ "]");
				logger.debug("Client Transaction Id = "
						+ theResponse.getTransId().getClientTransId());
				logger.debug("Server Transaction Id = "
						+ theResponse.getTransId().getServerTransId());

				// For each result
				for (int i = 0; i < theResponse.getCheckResults().size(); i++) {
					EPPDomainCheckResult currResult = (EPPDomainCheckResult) theResponse
							.getCheckResults().elementAt(i);

					if (currResult.isAvailable()) {
						logger.debug("domainCheck: Domain "
								+ currResult.getName() + " is available");
					}
					else {
						logger.debug("domainCheck: Domain "
								+ currResult.getName() + " is not available");
					}
				}

				this.handleResponse(theResponse);
			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {

				logger.debug
						("\n----------------------------------------------------------------");

				String theDomainName = "non-premiumdomain.tv";
				logger.debug
						("nonPremiumDomainCheck: Check single domain name With Flag True ("
								+ theDomainName + ")");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.TV);

				EPPPremiumDomainCheck extension = new EPPPremiumDomainCheck(
						true);
				theDomain.addExtension(extension);

				theResponse = theDomain.sendCheck();

				// Output all of the response attributes
				logger.debug("\nnonPremiumDomainCheck: Response = ["
						+ theResponse + "]");

				this.handleResponse(theResponse);
			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {

				logger.debug
						("\n----------------------------------------------------------------");

				String theDomainName = "premium.tv";
				logger.debug
						("premiumDomainCheck: Check single domain name With Flag True ("
								+ theDomainName + ")");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.TV);

				EPPPremiumDomainCheck extension = new EPPPremiumDomainCheck(
						true);
				theDomain.addExtension(extension);

				theResponse = theDomain.sendCheck();

				// Output all of the response attributes
				logger.debug("\npremiumDomainCheck: Response = ["
						+ theResponse + "]");

				this.handleResponse(theResponse);
			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				// Check multiple domain names
				logger.debug
						("\n----------------------------------------------------------------");
				logger.debug
						("premiumDomainCheck: Check multiple domain names With Flag True ");
				theDomain.setTransId(getClientTransId(request));

				theDomain.setSubProductID(NSSubProduct.TV);

				for (int i = 1; i <= 3; i++) {
					theDomain.addDomainName("premium" + i + ".tv");
				}

				EPPPremiumDomainCheck extension = new EPPPremiumDomainCheck(
						true);
				theDomain.addExtension(extension);

				theResponse = theDomain.sendCheck();

				// Output all of the response attributes
				logger.debug("\npremiumDomainCheck: Response = ["
						+ theResponse + "]");

				// For each result
				for (int i = 0; i < theResponse.getCheckResults().size(); i++) {
					EPPDomainCheckResult currResult = (EPPDomainCheckResult) theResponse
							.getCheckResults().elementAt(i);

					if (currResult.isAvailable()) {
						logger.debug("domainCheck: Domain "
								+ currResult.getName() + " is available");
					}
					else {
						logger.debug("domainCheck: Domain "
								+ currResult.getName() + " is not available");
					}
				}

				if (theResponse.hasExtension(EPPPremiumDomainCheckResp.class)) {
					EPPPremiumDomainCheckResp premiumDomainCheckResponse = (EPPPremiumDomainCheckResp) theResponse
							.getExtension(EPPPremiumDomainCheckResp.class);

					// For each result
					for (int i = 0; i < premiumDomainCheckResponse
							.getCheckResults().size(); i++) {
						EPPPremiumDomainCheckResult currResult = (EPPPremiumDomainCheckResult) premiumDomainCheckResponse
								.getCheckResults().elementAt(i);

						if (currResult.isPremium()) {
							logger.debug("domainCheck: Domain "
									+ currResult.getName() + " is premium");
							if (currResult.getPrice() != null) {
								logger.debug
										("domainCheck: Premium price is $"
												+ currResult.getPrice());
								logger.debug
										("domainCheck: Premium renewal price is $"
												+ currResult.getRenewalPrice());
							}
						}
						else {
							logger.debug("domainCheck: Domain "
									+ currResult.getName() + " is not premium");
						}
					}
				}
				this.handleResponse(theResponse);
			}
			catch (Exception ex) {
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

		printEnd("doDomainCheck");
	}

	/**
	 * <code>NSDomain.sendDomainInfo</code> command.
	 */
	@RequestMapping(value = "/info",method = RequestMethod.POST) 	@SystemControllerLog(description = "域名信息")
	public void  doDomainInfo(HttpServletRequest request) {
		printStart("doDomainInfo");

		EPPSession theSession = null;
		EPPDomainInfoResp theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {
				logger.debug("\ndomainInfo: Standard Domain info");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(this.makeDomainName());
				theDomain.setSubProductID(NSSubProduct.COM);
				theDomain.setHosts(com.verisign.epp.namestore.interfaces.NSDomain.HOSTS_ALL);

				theResponse = theDomain.sendInfo();

				// -- Output all of the response attributes
				logger.debug("domainInfo: Response = [" + theResponse
						+ "]\n\n");

				// -- Output required response attributes using accessors
				logger.debug("domainInfo: name            = "
						+ theResponse.getName());

				logger.debug("domainInfo: client id       = "
						+ theResponse.getClientId());

				logger.debug("domainInfo: created by      = "
						+ theResponse.getCreatedBy());

				logger.debug("domainInfo: create date     = "
						+ theResponse.getCreatedDate());

				logger.debug("domainInfo: expiration date = "
						+ theResponse.getExpirationDate());

				logger.debug("domainInfo: Registrant      = "
						+ theResponse.getRegistrant());

				/**
				 * Process Contacts
				 */
				if (theResponse.getContacts() != null) {
					for (int i = 0; i < theResponse.getContacts().size(); i++) {
						EPPDomainContact myContact = (EPPDomainContact) theResponse
								.getContacts().elementAt(i);

						logger.debug("Contact Name : "
								+ myContact.getName());

						logger.debug("Contact Type : "
								+ myContact.getType());
					}
				}

				/**
				 * Get AuthInfo
				 */
				if (theResponse.getAuthInfo() != null) {
					logger.debug("Authorization        : "
							+ theResponse.getAuthInfo().getPassword());

					logger.debug("Authorization (Roid) : "
							+ theResponse.getAuthInfo().getRoid());
				}

				/**
				 * Get Hosts
				 */
				if (theResponse.getHosts() != null) {
					for (int i = 0; i < theResponse.getHosts().size(); i++) {
						logger.debug("Host Name : "
								+ theResponse.getHosts().elementAt(i));
					}
				}

				/**
				 * Get Ns
				 */
				if (theResponse.getNses() != null) {
					for (int i = 0; i < theResponse.getNses().size(); i++) {
						logger.debug("Name Server : "
								+ theResponse.getNses().elementAt(i));
					}
				}

				/**
				 * Get Status
				 */
				if (theResponse.getStatuses() != null) {
					for (int i = 0; i < theResponse.getStatuses().size(); i++) {
						EPPDomainStatus myStatus = (EPPDomainStatus) theResponse
								.getStatuses().elementAt(i);

						logger.debug("Lang     : " + myStatus.getLang());

						logger.debug
								("Status   : " + myStatus.getStatus());
					}
				}

				this.handleResponse(theResponse);

			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Request whois information with domain info response.
			// NOTE - This might not be supported by the target Registry server.
			// Check that the server supports the Whois Info Extension.
			try {
				logger.debug
						("\ndomainInfo: Domain info with whois information");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(this.makeDomainName());
				theDomain.setSubProductID(NSSubProduct.COM);
				theDomain.setWhoisInfo(true);

				theResponse = theDomain.sendInfo();

				// -- Output all of the response attributes
				logger.debug("domainInfo: Response = [" + theResponse
						+ "]\n\n");

				// Output the whois information
				if (theResponse.hasExtension(EPPWhoisInfData.class)) {
					EPPWhoisInfData theWhoisInf = (EPPWhoisInfData) theResponse
							.getExtension(EPPWhoisInfData.class);

					logger.debug("domainInfo: registrar    = "
							+ theWhoisInf.getRegistrar());

					logger.debug("domainInfo: whois server = "
							+ theWhoisInf.getWhoisServer());

					logger.debug("domainInfo: url          = "
							+ theWhoisInf.getURL());

					logger.debug("domainInfo: iris server  = "
							+ theWhoisInf.getIrisServer());
				}

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				logger.debug
						("\ndomainInfo: Domain info with SecDNS extension");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName("secdns.com");
				theDomain.setSubProductID(NSSubProduct.COM);
				theResponse = theDomain.sendInfo();

				// -- Output all of the response attributes
				logger.debug("domainInfo: Response DNSSEC = ["
						+ theResponse + "]\n\n");

				// -- Output the secDNS:infData extension
				if (theResponse.hasExtension(EPPSecDNSExtInfData.class)) {
					EPPSecDNSExtInfData infData = (EPPSecDNSExtInfData) theResponse
							.getExtension(EPPSecDNSExtInfData.class);

					Collection dsDataVec = infData.getDsData();
					EPPSecDNSExtDsData dsData = null;
					if (dsDataVec == null) {
						logger.debug
								("domainInfo: secDNS:infData dsDataVec = "
										+ dsDataVec);
					}
					else {
						int i = 0;
						Iterator iter = dsDataVec.iterator();
						while (iter.hasNext()) {
							dsData = (EPPSecDNSExtDsData) iter.next();
							// logger.debug("domainInfo:
							// secDNS:infData/dsData[" + i + "] = "
							// + dsData);
							logger.debug
									("domainInfo: secDNS:infData/dsData["
											+ i
											+ "]/keyTag = "
											+ dsData.getKeyTag());
							logger.debug
									("domainInfo: secDNS:infData/dsData["
											+ i + "]/alg = " + dsData.getAlg());
							logger.debug
									("domainInfo: secDNS:infData/dsData["
											+ i
											+ "]/digestType = "
											+ dsData.getDigestType());
							logger.debug
									("domainInfo: secDNS:infData/dsData["
											+ i
											+ "]/digest = "
											+ dsData.getDigest());

							EPPSecDNSExtKeyData keyData = dsData.getKeyData();
							if (keyData == null) {
								logger.debug
										("domainInfo: secDNS:infData/dsData["
												+ i + "]/keyData = " + keyData);
							}
							else {
								// logger.debug("domainInfo:
								// secDNS:infData/dsData[" + i + "]/keyData = "
								// + keyData);
								logger.debug("domainInfo: secDNS:infData/dsData["
												+ i
												+ "]/keyData/flags = "
												+ keyData.getFlags());
								logger.debug("domainInfo: secDNS:infData/dsData["
												+ i
												+ "]/keyData/protocol = "
												+ keyData.getProtocol());
								logger.debug("domainInfo: secDNS:infData/dsData["
												+ i
												+ "]/keyData/alg = "
												+ keyData.getAlg());
								logger.debug("domainInfo: secDNS:infData/dsData["
												+ i
												+ "]/keyData/pubKey = "
												+ keyData.getPubKey());
							}

							i++;

						} // end while
					}

				}
				else {
					Assert.fail("domainInfo: no EPPSecDNSExtInfData extension");
				}
			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				logger.debug
						("\ndomainInfo: Domain info with COA extension");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName("coa-full-info-owned.com");
				theDomain.setSubProductID(NSSubProduct.COM);
				theResponse = theDomain.sendInfo();

				// -- Output all of the response attributes
				logger.debug("domainInfo: Response COA = [" + theResponse
						+ "]\n\n");

				if (theResponse.hasExtension(EPPCoaExtInfData.class)) {

					EPPCoaExtInfData coaInfData = (EPPCoaExtInfData) theResponse
							.getExtension(EPPCoaExtInfData.class);

					for (Iterator iterator = coaInfData.getAttrs().iterator(); iterator
							.hasNext();) {
						EPPCoaExtAttr attr = (EPPCoaExtAttr) iterator.next();
						String key = attr.getKey().getKey();
						String value = attr.getValue().getValue();
						logger.debug("Client Object Attribute: key='"
								+ key + "', value='" + value + "'");

					}
				}
				else {
					Assert.fail("domainInfo: no EPPCoaExtInfData extension");
				}

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				logger.debug
						("\ndomainInfo: Domain info for domain with RGP statuses");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName("graceperiod.com");
				theDomain.setSubProductID(NSSubProduct.COM);
				theResponse = theDomain.sendInfo();

				// -- Output all of the response attributes
				logger.debug
						("domainInfo: Response for graceperiod.com = ["
								+ theResponse + "]\n\n");

				this.printRgpStatuses(theResponse);

				theDomain.addDomainName("pendingperiod.com");
				theDomain.setSubProductID(NSSubProduct.COM);
				theResponse = theDomain.sendInfo();

				// -- Output all of the response attributes
				logger.debug
						("domainInfo: Response for pendingperiod.com = ["
								+ theResponse + "]\n\n");

				this.printRgpStatuses(theResponse);

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

		printEnd("doDomainInfo");
	}

	/**
	 * <code>NSDomain.sendDelete</code> command.
	 */
	@RequestMapping(value = "/delete",method = RequestMethod.POST) 	@SystemControllerLog(description = "删除域名")
	public void  doDomainDelete(HttpServletRequest request) {
		printStart("doDomainDelete");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {
				logger.debug("\ndomainDelete: Domain delete");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(this.makeDomainName());
				theDomain.setSubProductID(NSSubProduct.COM);

				theResponse = theDomain.sendDelete();

				// -- Output all of the response attributes
				logger.debug("domainDelete: Response = [" + theResponse
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

		printEnd("doDomainDelete");
	}

	/**
	 * <code>NSDomain.sendDomainRenew</code> command.
	 */
	@RequestMapping(value = "/renew",method = RequestMethod.POST) 	@SystemControllerLog(description = "续费域名")
	public void  doDomainRenew(HttpServletRequest request) {
		printStart("doDomainRenew");

		EPPSession theSession = null;
		EPPDomainRenewResp theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {
				theDomain.setTransId(getClientTransId(request));

				String theDomainName = this.makeDomainName();

				logger.debug("\ndomainRenew: Domain " + theDomainName
						+ " renew");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				theDomain.setExpirationDate(new GregorianCalendar(2004, 2, 3)
						.getTime());

				theDomain.setPeriodLength(10);

				theDomain.setPeriodUnit(EPPDomain.PERIOD_YEAR);

				theResponse = theDomain.sendRenew();

				// -- Output all of the response attributes
				logger.debug("domainRenew: Response = [" + theResponse
						+ "]\n\n");

				// -- Output response attributes using accessors
				logger.debug("domainRenew: name = "
						+ theResponse.getName());

				logger.debug("domainRenew: expiration date = "
						+ theResponse.getExpirationDate());

			}

			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			this.handleResponse(theResponse);

		}
		catch (InvalidateSessionException ex) {
			this.invalidateSession(theSession);
			theSession = null;
		}
		finally {
			if (theSession != null)
				this.returnSession(theSession);
		}

		printEnd("doDomainRenew");
	}

	/**
	 * <code>NSDomain.sendUpdate</code> command.
	 */
	@RequestMapping(value = "/update",method = RequestMethod.POST) 	@SystemControllerLog(description = "更新域名")
	public void  doDomainUpdate(HttpServletRequest request) {
		printStart("doDomainUpdate");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {

				theDomain.setTransId(getClientTransId(request));

				String theDomainName = this.makeDomainName();

				logger.debug("\ndomainUpdate: Domain " + theDomainName
						+ " update");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				// Add attributes
				// Is the contact mapping supported?
				if (EPPFactory.getInstance().hasService(
						EPPDomainMapFactory.NS_CONTACT)) {
					theDomain.setUpdateAttrib(EPPDomain.CONTACT, "SH0000",
							EPPDomain.CONTACT_BILLING, EPPDomain.ADD);
				}

				theDomain.setUpdateAttrib(EPPDomain.HOST,
						this.makeHostName(theDomainName), EPPDomain.ADD);

				theDomain.setUpdateAttrib(EPPDomain.STATUS,
						new EPPDomainStatus(EPPDomain.STATUS_CLIENT_HOLD),
						EPPDomain.ADD);

				// Remove attributes
				theDomain.setUpdateAttrib(EPPDomain.HOST,
						this.makeHostName(theDomainName), EPPDomain.REMOVE);

				theDomain.setUpdateAttrib(EPPDomain.STATUS,
						new EPPDomainStatus(EPPDomain.STATUS_CLIENT_HOLD),
						EPPDomain.REMOVE);

				// Is the contact mapping supported?
				if (EPPFactory.getInstance().hasService(
						EPPDomainMapFactory.NS_CONTACT)) {
					theDomain.setUpdateAttrib(EPPDomain.CONTACT, "SH0000",
							EPPDomain.CONTACT_BILLING, EPPDomain.REMOVE);
				}

				// Update the authInfo value
				theDomain.setAuthString("new-auth-info-123");

				// Execute update
				theResponse = theDomain.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("domainUpdate: Response = [" + theResponse
						+ "]\n\n");

				this.handleResponse(theResponse);

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {

				theDomain.setTransId(getClientTransId(request));

				String theDomainName = this.makeDomainName();

				logger.debug("\ndomainUpdate: Domain " + theDomainName
						+ " update/add with SecDNS extension");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				// instantiate a secDNS:keyData object
				EPPSecDNSExtKeyData keyData = new EPPSecDNSExtKeyData();
				keyData.setFlags(EPPSecDNSExtKeyData.FLAGS_ZONE_KEY_SEP);
				keyData.setProtocol(EPPSecDNSExtKeyData.DEFAULT_PROTOCOL);
				keyData.setAlg(EPPSecDNSAlgorithm.RSASHA1);
				keyData.setPubKey("AQPmsXk3Q1ngNSzsH1lrX63mRIhtwkkK+5Zj"
						+ "vxykBCV1NYne83+8RXkBElGb/YJ1n4TacMUs"
						+ "poZap7caJj7MdOaADKmzB2ci0vwpubNyW0t2"
						+ "AnaQqpy1ce+07Y8RkbTC6xCeEw1UQZ73PzIO"
						+ "OvJDdjwPxWaO9F7zSxnGpGt0WtuItQ==");

				// instantiate another secDNS:keyData object
				EPPSecDNSExtKeyData keyData2 = new EPPSecDNSExtKeyData(
						EPPSecDNSExtKeyData.FLAGS_ZONE_KEY_SEP,
						EPPSecDNSExtKeyData.DEFAULT_PROTOCOL,
						EPPSecDNSAlgorithm.RSASHA1,
						"AQOxXpFbRp7+zPBoTt6zL7Af0aEKzpS4JbVB"
								+ "5ofk5E5HpXuUmU+Hnt9hm2kMph6LZdEEL142"
								+ "nq0HrgiETFCsN/YM4Zn+meRkELLpCG93Cu/H"
								+ "hwvxfaZenUAAA6Vb9FwXQ1EMYRW05K/gh2Ge"
								+ "w5Sk/0o6Ev7DKG2YiDJYA17QsaZtFw==");

				// instantiate a secDNS:dsData object
				EPPSecDNSExtDsData dsData = new EPPSecDNSExtDsData();
				dsData.setKeyTag(34095);
				dsData.setAlg(EPPSecDNSAlgorithm.RSASHA1);
				dsData.setDigestType(EPPSecDNSExtDsData.SHA1_DIGEST_TYPE);
				dsData.setDigest("6BD4FFFF11566D6E6A5BA44ED0018797564AA289");
				dsData.setKeyData(keyData);

				// instantiate another secDNS:dsData object
				EPPSecDNSExtDsData dsData2 = new EPPSecDNSExtDsData(10563,
						EPPSecDNSAlgorithm.RSASHA1,
						EPPSecDNSExtDsData.SHA1_DIGEST_TYPE,
						"9C20674BFF957211D129B0DFE9410AF753559D4B", keyData2);

				// instantiate the add DS Data
				List addDsData = new ArrayList();
				addDsData.add(dsData);
				addDsData.add(dsData2);

				theDomain.setSecDNSUpdate(addDsData, null);

				// Execute update
				theResponse = theDomain.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("domainUpdate: Response = [" + theResponse
						+ "]\n\n");

				this.handleResponse(theResponse);

				// Send DNSSEC update with urgent set to true
				theDomain.setTransId("ABC-12345-XYZ-2");

				theDomainName = this.makeDomainName();

				logger.debug
						("\ndomainUpdate: Domain "
								+ theDomainName
								+ " update/add with SecDNS extension and urgent = true");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				theDomain.setSecDNSUpdate(addDsData, null);

				// Execute update
				theResponse = theDomain.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("domainUpdate: Response = [" + theResponse
						+ "]\n\n");

				this.handleResponse(theResponse);

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {

				theDomain.setTransId(getClientTransId(request));

				String theDomainName = this.makeDomainName();

				logger.debug("\ndomainUpdate: Domain " + theDomainName
						+ " update/rem with SecDNS extension");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				// instantiate a secDNS:dsData object
				EPPSecDNSExtDsData dsData = new EPPSecDNSExtDsData();
				dsData.setKeyTag(34095);
				dsData.setAlg(EPPSecDNSAlgorithm.RSASHA1);
				dsData.setDigestType(EPPSecDNSExtDsData.SHA1_DIGEST_TYPE);
				dsData.setDigest("6BD4FFFF11566D6E6A5BA44ED0018797564AA289");

				// instantiate another secDNS:dsData object
				EPPSecDNSExtDsData dsData2 = new EPPSecDNSExtDsData(10563,
						EPPSecDNSAlgorithm.RSASHA1,
						EPPSecDNSExtDsData.SHA1_DIGEST_TYPE,
						"9C20674BFF957211D129B0DFE9410AF753559D4B");

				// instantiate the secDNS:update object
				List rmvDsData = new ArrayList();
				rmvDsData.add(dsData);
				rmvDsData.add(dsData2);

				theDomain.setSecDNSUpdate(null, rmvDsData);

				// Execute update
				theResponse = theDomain.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("domainUpdate: Response = [" + theResponse
						+ "]\n\n");

				this.handleResponse(theResponse);

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				theDomain.setTransId(getClientTransId(request));
				String theDomainName = "premium.tv";

				logger.debug("\npremiumDomainUpdate: Domain "
						+ theDomainName + " update with ReAssign extension");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.TV);

				EPPPremiumDomainReAssignCmd extension = new EPPPremiumDomainReAssignCmd();
				extension.setShortName("testregistrar");
				theDomain.addExtension(extension);

				theResponse = theDomain.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("domainUpdate: Response = [" + theResponse
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

		printEnd("doDomainUpdate");
	}

	/**
	 * <code>NSDomain.sendTransfer</code> command.
	 */
	@RequestMapping(value = "/transfer",method = RequestMethod.POST) 	@SystemControllerLog(description = "创建域名")
	public void  doDomainTransfer(HttpServletRequest request) {
		printStart("doDomainTransfer");

		EPPSession theSession = null;
		EPPDomainTransferResp theResponse = null;

		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);
			String theDomainName = this.makeDomainName();

			try {

				logger.debug("\ndomainTransfer: Domain " + theDomainName
						+ " transfer request");

				theDomain.setTransferOpCode(EPPDomain.TRANSFER_REQUEST);

				theDomain.setTransId(getClientTransId(request));

				theDomain.setAuthString("ClientX");

				theDomain.addDomainName(this.makeDomainName());
				theDomain.setSubProductID(NSSubProduct.COM);

				theDomain.setPeriodLength(10);

				theDomain.setPeriodUnit(EPPDomain.PERIOD_YEAR);

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

			}

			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Transfer Query
			try {
				logger.debug
						("\n----------------------------------------------------------------");

				logger.debug("\ndomainTransfer: Domain " + theDomainName
						+ " transfer query");

				theDomain.setTransferOpCode(EPPDomain.TRANSFER_QUERY);

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

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
			}

			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Transfer Cancel
			try {
				logger.debug
						("\n----------------------------------------------------------------");

				logger.debug("\ndomainTransfer: Domain " + theDomainName
						+ " transfer cancel");

				theDomain.setTransferOpCode(EPPDomain.TRANSFER_CANCEL);

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				// Execute the transfer cancel
				theResponse = theDomain.sendTransfer();

				// -- Output all of the response attributes
				logger.debug("domainTransfer: Response = [" + theResponse
						+ "]\n\n");
			}

			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Transfer Reject
			try {
				logger.debug
						("\n----------------------------------------------------------------");

				logger.debug("\ndomainTransfer: Domain " + theDomainName
						+ " transfer reject");

				theDomain.setTransferOpCode(EPPDomain.TRANSFER_REJECT);

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				// Execute the transfer cancel
				theResponse = theDomain.sendTransfer();

				// -- Output all of the response attributes
				logger.debug("domainTransfer: Response = [" + theResponse
						+ "]\n\n");
			}

			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

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
	}

	/**
	 * <code>NSDomain.sendSync</code> command.
	 */
	@RequestMapping(value = "/sync",method = RequestMethod.POST) 	@SystemControllerLog(description = "创建域名")
	public void  doDomainSync(HttpServletRequest request) {
		printStart("doDomainSync");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {

				theDomain.setTransId(getClientTransId(request));

				String theDomainName = this.makeDomainName();

				logger.debug("\ndomainSync: Domain " + theDomainName
						+ " sync");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				// Set to June 15th
				theDomain.setDay(15);
				theDomain.setMonth(Calendar.JUNE);

				// Execute update
				theResponse = theDomain.sendSync();

				// -- Output all of the response attributes
				logger.debug("domainSync: Response = [" + theResponse
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

		printEnd("doDomainSync");
	}

	/**
	 * <code>NSDomain.sendRestoreRequest</code> command.
	 */
	@RequestMapping(value = "/restorerequest",method = RequestMethod.POST) 	@SystemControllerLog(description = "RestoreRequest")
	public void  doDomainRestoreRequest(HttpServletRequest request) {
		printStart("doDomainRestoreRequest");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);
			String theDomainName = this.makeDomainName();

			try {
				theDomain.setTransId(getClientTransId(request));

				logger.debug("\ndomainRestoreRequest: Domain "
						+ theDomainName + " restore request");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				// Execute restore request
				theResponse = theDomain.sendRestoreRequest();

				// -- Output all of the response attributes
				logger.debug("domainRestoreRequest: Response = ["
						+ theResponse + "]\n\n");

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			this.handleResponse(theResponse);

		}
		catch (InvalidateSessionException ex) {
			this.invalidateSession(theSession);
			theSession = null;
		}
		finally {
			if (theSession != null)
				this.returnSession(theSession);
		}

		printEnd("doDomainRestoreRequest");
	}

	/**
	 * <code>NSDomain.sendRestoreReport</code> command.
	 */
	@RequestMapping(value = "/restorereport",method = RequestMethod.POST) 	@SystemControllerLog(description = "restore报告")
	public void  doDomainRestoreReport(HttpServletRequest request) {
		printStart("doDomainRestoreReport");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);
			String theDomainName = this.makeDomainName();

			try {
				theDomain.setTransId(getClientTransId(request));

				logger.debug("\ndomainRestoreReport: Domain "
						+ theDomainName + " restore request");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				EPPRgpExtReport theReport = new EPPRgpExtReport();
				theReport
						.setPreData("Pre-delete whois data goes here. Both XML and free text are allowed");
				theReport
						.setPostData("Post-delete whois data goes here. Both XML and free text are allowed");
				theReport.setDeleteTime(new Date());
				theReport.setRestoreTime(new Date());

				theReport.setRestoreReason(new EPPRgpExtReportText(
						"Registrant Error"));

				theReport
						.setStatement1(new EPPRgpExtReportText(
								"This registrar has not"
										+ " restored the Registered Domain in order to "
										+ "assume the rights to use or sell the Registered"
										+ " Name for itself or for any third party"));

				theReport
						.setStatement2(new EPPRgpExtReportText(
								"The information in this report "
										+ " is true to best of this registrar's knowledge, and this"
										+ "registrar acknowledges that intentionally supplying false"
										+ " information in this report shall "
										+ "constitute  an incurable material breach of the Registry-Registrar"
										+ " Agreement"));

				theReport.setOther("other stuff");

				// Execute restore report
				theDomain.setReport(theReport);
				theResponse = theDomain.sendRestoreReport();

				// -- Output all of the response attributes
				logger.debug("domainRestoreReport: Response = ["
						+ theResponse + "]\n\n");

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			this.handleResponse(theResponse);

		}
		catch (InvalidateSessionException ex) {
			this.invalidateSession(theSession);
			theSession = null;
		}
		finally {
			if (theSession != null)
				this.returnSession(theSession);
		}

		printEnd("doDomainRestoreReport");
	}

	/**
	 * <code>NSDomain.sendCreate</code> command with IDN tag
	 * extension.
	 */
	@RequestMapping(value = "/idncreate",method = RequestMethod.POST)
	@SystemControllerLog(description = "创建idn")
	public void  doDomainIDNCreate(HttpServletRequest request) {
		printStart("doDomainIDNCreate");

		EPPSession theSession = null;
		EPPDomainCreateResp theResponse = null;

		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {
				logger.debug
						("\n----------------------------------------------------------------");

				String theDomainName = this.makeDomainName();

				logger.debug("domainCreate: Create " + theDomainName
						+ " with IDN tag");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				theDomain.setAuthString("ClientX");

				theDomain.setIDNLangTag("en");

				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("domainCreate: Response = [" + theResponse
						+ "]\n\n");

				// -- Output response attributes using accessors
				logger.debug("domainCreate: name = "
						+ theResponse.getName());

				logger.debug("domainCreate: expiration date = "
						+ theResponse.getExpirationDate());

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

		printEnd("doDomainIDNCreate");
	}

	/**
	 * Unit test using NSDomain.setAllocationToken(String) with domain check,
	 * domain info, domain create, domain update, and domain transfer.
	 */
	@RequestMapping(value = "/allocationtoken",method = RequestMethod.POST) 	@SystemControllerLog(description = "AllocationToken")
	public void  testAllocationToken(HttpServletRequest request) {
		printStart("testAllocationToken");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			// Allocation Token with Domain Check
			try {
				logger.debug
						("\ntestAllocationToken: Domain check with allocation token");

				theDomain.setTransId("ABC-12345");

				theDomain.addDomainName("example.tld");
				theDomain.setSubProductID("tld");

				theDomain.setAllocationToken("abc123");

				theResponse = theDomain.sendCheck();

				// -- Output all of the response attributes
				logger.debug("testAllocationToken: Check Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Allocation Token with Domain Info
			try {
				logger.debug
						("\ntestAllocationToken: Domain info with allocation token");

				theDomain.setTransId("ABC-12345");

				theDomain.addDomainName("example.tld");
				theDomain.setSubProductID("tld");

				// Set empty allocation token extension
				theDomain.setAllocationToken(null);

				theResponse = theDomain.sendInfo();

				// -- Output all of the response attributes
				logger.debug("testAllocationToken: Info Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Allocation Token with Domain Create
			try {
				logger.debug
						("\ntestAllocationToken: Domain create with allocation token");

				theDomain.setTransId("ABC-12345");

				theDomain.addDomainName("example.tld");
				theDomain.setSubProductID("tld");
				theDomain.setRegistrant("jd1234");
				theDomain.addContact("sh8013", com.verisign.epp.namestore.interfaces.NSDomain.CONTACT_ADMINISTRATIVE);
				theDomain.addContact("sh8013", com.verisign.epp.namestore.interfaces.NSDomain.CONTACT_TECHNICAL);
				theDomain.setAuthString("2fooBAR");
				theDomain.setAllocationToken("abc123");

				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("testAllocationToken: Create Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Allocation Token with Domain Transfer
			try {
				logger.debug
						("\ntestAllocationToken: Domain transfer request with allocation token");

				theDomain.setTransId("ABC-12345");

				theDomain.addDomainName("example.tld");
				theDomain.setSubProductID("tld");
				theDomain.setAuthString("2fooBAR");
				theDomain.setTransferOpCode(com.verisign.epp.namestore.interfaces.NSDomain.TRANSFER_REQUEST);

				theDomain.setAllocationToken("abc123");

				theResponse = theDomain.sendTransfer();

				// -- Output all of the response attributes
				logger.debug("testAllocationToken: Transfer Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);
			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Allocation Token with Domain Update
			try {
				logger.debug
						("\ntestAllocationToken: Domain update with allocation token");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName("example1.tld");
				theDomain.setSubProductID("tld");

				theDomain.setAllocationToken("abc123");

				theResponse = theDomain.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("testAllocationToken: Update Response = ["
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

		printEnd("testAllocationToken");
	}

	/**
	 * Unit test using {@link com.verisign.epp.namestore.interfaces.NSDomain#setResellerId(String)} to set the
	 * reseller identifier on create and
	 * {@link com.verisign.epp.namestore.interfaces.NSDomain#updateResellerId(Action, String)}
	 * to update the reseller identifier of an existing domain.
	 */
	@RequestMapping(value = "/resellerid",method = RequestMethod.POST) 	@SystemControllerLog(description = "ResellerId")
	public void  testResellerId(HttpServletRequest request) {
		printStart("testResellerId");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			// Reseller Identifier with Domain Create
			try {
				logger.debug
						("\ntestResellerId: Domain create with reseller identifier");

				theDomain.setTransId("ABC-12345");

				theDomain.addDomainName("reseller.example");
				theDomain.setSubProductID("example");
				theDomain.setRegistrant("jd1234");
				theDomain.addContact("sh8013", com.verisign.epp.namestore.interfaces.NSDomain.CONTACT_ADMINISTRATIVE);
				theDomain.addContact("sh8013", com.verisign.epp.namestore.interfaces.NSDomain.CONTACT_TECHNICAL);
				theDomain.setAuthString("2fooBAR");
				theDomain.setResellerId("myreseller");

				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("testResellerId: Create Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Reseller Identifier with Domain Update - ADD
			try {
				logger.debug
						("\ntestResellerId: Domain update with reseller identifier - ADD");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName("example1.tld");
				theDomain.setSubProductID("tld");

				theDomain.updateResellerId(Action.ADD, "myreseller");

				theResponse = theDomain.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("testResellerId: ADD Update Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);
			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Reseller Identifier with Domain Update - CHG
			try {
				logger.debug
						("\ntestResellerId: Domain update with reseller identifier - CHG");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName("example1.tld");
				theDomain.setSubProductID("tld");

				theDomain.updateResellerId(Action.CHG, "myreseller2");

				theResponse = theDomain.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("testResellerId: CHG Update Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);
			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			// Reseller Identifier with Domain Update - REM
			try {
				logger.debug
						("\ntestResellerId: Domain update with reseller identifier - REM");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName("example1.tld");
				theDomain.setSubProductID("tld");

				theDomain.updateResellerId(Action.REM, "myreseller2");

				theResponse = theDomain.sendUpdate();

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

	/**
	 * Unit test processing responses asynchronous from the commands. This is a
	 * test of the use of pipelining.
	 */
	@RequestMapping(value = "/asynccommands",method = RequestMethod.POST) 	@SystemControllerLog(description = "AsyncCommands")
	public void  testAsyncCommands() {
		printStart("testAsyncCommands");

		EPPSession theSession = null;
		int previousSessionMode = EPPSession.MODE_SYNC;
		try {
			List clientTransIdQueue = new ArrayList();

			theSession = this.borrowSession();
			if (!theSession.isModeSupported(EPPSession.MODE_ASYNC)) {
				logger.debug("testAsyncCommands: Session "
						+ theSession.getClass().getName()
						+ " does not support MODE_ASYNC, skipping test");
				printEnd("testAsyncCommands (skipped)");
				return;
			}
			previousSessionMode = theSession.setMode(EPPSession.MODE_ASYNC);
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);
			String theDomainName = this.makeDomainName();

			// Send 3 commands first
			try {
				// Async domain check
				String theClientTransId = "ASYNC-CMD-"
						+ System.currentTimeMillis();
				clientTransIdQueue.add(theClientTransId);
				theDomain.setTransId(theClientTransId);
				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);
				logger.debug("domainCheck: Async check of domain name ("
						+ theDomainName + ")");
				theDomain.sendCheck();

				// Async domain create
				theClientTransId = "ASYNC-CMD-" + System.currentTimeMillis();
				clientTransIdQueue.add(theClientTransId);
				theDomain.setTransId(theClientTransId);
				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);
				theDomain.setAuthString("ClientX");
				logger.debug("domainCheck: Async create of domain name ("
						+ theDomainName + ")");
				theDomain.sendCreate();

				// Async domain delete
				theClientTransId = "ASYNC-CMD-" + System.currentTimeMillis();
				clientTransIdQueue.add(theClientTransId);
				theDomain.setTransId(theClientTransId);
				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);
				logger.debug("domainCheck: Async delete of domain name ("
						+ theDomainName + ")");
				theDomain.sendDelete();

			}
			catch (EPPCommandException ex) {
				Assert.fail("testAsyncCommands(): Exception sending asynchronous command: "
						+ ex);
			}

			// Receive 3 responses
			try {
				while (clientTransIdQueue.size() > 0) {
					String theClientTransId = (String) clientTransIdQueue
							.get(0);
					clientTransIdQueue.remove(0);
					EPPResponse theResponse = theSession.readResponse();
					if (theClientTransId.equals(theResponse.getTransId()
							.getClientTransId())) {
						logger.debug
								("Successfully received client transaction id "
										+ theClientTransId + " asynchronously");
					}
					else {
						Assert.fail("testAsyncCommands(): Received response with client transid "
								+ theResponse.getTransId().getClientTransId()
								+ " != expected transid " + theClientTransId);
					}
				}
			}
			catch (EPPCommandException ex) {
				Assert.fail("testAsyncCommands(): Exception receiving asynchronous response: "
						+ ex);
			}

		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("testAsyncCommands(): Exception invalidating session: "
					+ ex);
		}
		finally {

			if (theSession != null) {
				theSession.setMode(previousSessionMode);
				this.returnSession(theSession);
			}
		}

		printEnd("testAsyncCommands");
	}

	/**
	 * support secDNS-1.0 with NSDomain for backward compatibility.
	 */
	@RequestMapping(value = "/secdns10",method = RequestMethod.POST) 	@SystemControllerLog(description = "SecDNS10")
	public void  testSecDNS10(HttpServletRequest request) {
		printStart("testSecDNS10");

		EPPSession theSession = null;
		EPPDomainCreateResp theResponse = null;

		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {
				logger.debug
						("\n----------------------------------------------------------------");

				String theDomainName = this.makeDomainName();

				logger.debug("domainCreate: Create " + theDomainName
						+ " with no optional attributes");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				theDomain.setAuthString("ClientX");

				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("domainCreate: Response = [" + theResponse
						+ "]\n\n");

				// -- Output response attributes using accessors
				logger.debug("domainCreate: name = "
						+ theResponse.getName());

				logger.debug("domainCreate: expiration date = "
						+ theResponse.getExpirationDate());

			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				logger.debug
						("\n----------------------------------------------------------------");

				theDomain.setTransId(getClientTransId(request));

				String theDomainName = this.makeDomainName();

				logger.debug("domainCreate: Create " + theDomainName
						+ " with all optional attributes");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				for (int i = 0; i <= 20; i++) {
					theDomain.addHostName(this.makeHostName(theDomainName));
				}

				// Is the contact mapping supported?
				if (EPPFactory.getInstance().hasService(
						EPPDomainMapFactory.NS_CONTACT)) {
					// Add domain contacts
					theDomain.addContact("SH0000",
							EPPDomain.CONTACT_ADMINISTRATIVE);

					theDomain.addContact("SH0000", EPPDomain.CONTACT_TECHNICAL);

					theDomain.addContact("SH0000", EPPDomain.CONTACT_BILLING);
				}

				theDomain.setPeriodLength(10);

				theDomain.setPeriodUnit(EPPDomain.PERIOD_YEAR);

				theDomain.setAuthString("ClientX");

				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("domainCreate: Response = [" + theResponse
						+ "]\n\n");

				// -- Output response attributes using accessors
				logger.debug("domainCreate: name = "
						+ theResponse.getName());

				logger.debug("domainCreate: expiration date = "
						+ theResponse.getExpirationDate());
			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {
				logger.debug
						("\n----------------------------------------------------------------");

				String theDomainName = this.makeDomainName();

				logger.debug("domainCreate: Create " + theDomainName
						+ " with SecDNS Extension");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				theDomain.setAuthString("ClientX");

				// -- Add secDNS Extension
				// instantiate a secDNS:keyData object
				EPPSecDNSExtKeyData keyData = new EPPSecDNSExtKeyData();
				keyData.setFlags(EPPSecDNSExtKeyData.FLAGS_ZONE_KEY_SEP);
				keyData.setProtocol(EPPSecDNSExtKeyData.DEFAULT_PROTOCOL);
				keyData.setAlg(EPPSecDNSAlgorithm.RSASHA1);
				keyData.setPubKey("AQPmsXk3Q1ngNSzsH1lrX63mRIhtwkkK+5Zj"
						+ "vxykBCV1NYne83+8RXkBElGb/YJ1n4TacMUs"
						+ "poZap7caJj7MdOaADKmzB2ci0vwpubNyW0t2"
						+ "AnaQqpy1ce+07Y8RkbTC6xCeEw1UQZ73PzIO"
						+ "OvJDdjwPxWaO9F7zSxnGpGt0WtuItQ==");

				// instantiate another secDNS:keyData object
				EPPSecDNSExtKeyData keyData2 = new EPPSecDNSExtKeyData(
						EPPSecDNSExtKeyData.FLAGS_ZONE_KEY_SEP,
						EPPSecDNSExtKeyData.DEFAULT_PROTOCOL,
						EPPSecDNSAlgorithm.RSASHA1,
						"AQOxXpFbRp7+zPBoTt6zL7Af0aEKzpS4JbVB"
								+ "5ofk5E5HpXuUmU+Hnt9hm2kMph6LZdEEL142"
								+ "nq0HrgiETFCsN/YM4Zn+meRkELLpCG93Cu/H"
								+ "hwvxfaZenUAAA6Vb9FwXQ1EMYRW05K/gh2Ge"
								+ "w5Sk/0o6Ev7DKG2YiDJYA17QsaZtFw==");

				// instantiate a secDNS:dsData object
				EPPSecDNSExtDsData dsData = new EPPSecDNSExtDsData();
				dsData.setKeyTag(34095);
				dsData.setAlg(EPPSecDNSAlgorithm.RSASHA1);
				dsData.setDigestType(EPPSecDNSExtDsData.SHA1_DIGEST_TYPE);
				dsData.setDigest("6BD4FFFF11566D6E6A5BA44ED0018797564AA289");
				dsData.setKeyData(keyData);

				// instantiate another secDNS:dsData object
				EPPSecDNSExtDsData dsData2 = new EPPSecDNSExtDsData(10563,
						EPPSecDNSAlgorithm.RSASHA1,
						EPPSecDNSExtDsData.SHA1_DIGEST_TYPE,
						"9C20674BFF957211D129B0DFE9410AF753559D4B", keyData2);

				// dsData Records
				List dsDataRecords = new ArrayList();
				dsDataRecords.add(dsData);
				dsDataRecords.add(dsData2);

				theDomain.setSecDNSCreate(dsDataRecords);
				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("domainCreate: Response = [" + theResponse
						+ "]\n\n");

				// -- Output response attributes using accessors
				logger.debug("domainCreate: name = "
						+ theResponse.getName());

				logger.debug("domainCreate: expiration date = "
						+ theResponse.getExpirationDate());

			}
			catch (Exception ex) {
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

		printEnd("testSecDNS10");
	}



	/**
	 * <code>NSDomain.sendCreate</code> command with COA extension.
	 */
	@RequestMapping(value = "/coacreate",method = RequestMethod.POST) 	@SystemControllerLog(description = "CoaCreate")
	public void  doDomainCoaCreate(HttpServletRequest request) {
		printStart("doDomainCreate");

		EPPSession theSession = null;
		EPPDomainCreateResp theResponse = null;

		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {
				logger.debug
						("\n----------------------------------------------------------------");

				String theDomainName = this.makeDomainName();

				logger.debug("domainCreate: Create " + theDomainName
						+ " with COA Extension");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				theDomain.setAuthString("ClientX");

				// Client Object Attributes
				EPPCoaExtAttr attr = new EPPCoaExtAttr("KEY1", "value1");
				List attrList = new ArrayList();
				attrList.add(attr);
				theDomain.setCoaCreate(attrList);

				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("domainCreate: Response = [" + theResponse
						+ "]\n\n");

				// -- Output response attributes using accessors
				logger.debug("domainCreate: name = "
						+ theResponse.getName());

				logger.debug("domainCreate: expiration date = "
						+ theResponse.getExpirationDate());

			}
			catch (Exception ex) {
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

		printEnd("doDomainCoaCreate");
	}

	/**
	 * <code>NSDomain.sendUpdate</code> command with COA extension.
	 */
	@RequestMapping(value = "/coaupdate",method = RequestMethod.POST) 	@SystemControllerLog(description = "创建域名")  
	public void  doDomainCoaUpdate(HttpServletRequest request) {
		printStart("doDomainCoaUpdate");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new NSDomain(theSession);

			try {

				theDomain.setTransId(getClientTransId(request));

				String theDomainName = this.makeDomainName();

				logger.debug("\ndomainUpdate: Domain " + theDomainName
						+ " update adding a COA.");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				// Add attributes
				// Is the contact mapping supported?
				if (EPPFactory.getInstance().hasService(
						EPPDomainMapFactory.NS_CONTACT)) {
					theDomain.setUpdateAttrib(EPPDomain.CONTACT, "SH0000",
							EPPDomain.CONTACT_BILLING, EPPDomain.ADD);
				}

				theDomain.setUpdateAttrib(EPPDomain.HOST,
						this.makeHostName(theDomainName), EPPDomain.ADD);

				theDomain.setUpdateAttrib(EPPDomain.STATUS,
						new EPPDomainStatus(EPPDomain.STATUS_CLIENT_HOLD),
						EPPDomain.ADD);

				// Remove attributes
				theDomain.setUpdateAttrib(EPPDomain.HOST,
						this.makeHostName(theDomainName), EPPDomain.REMOVE);

				theDomain.setUpdateAttrib(EPPDomain.STATUS,
						new EPPDomainStatus(EPPDomain.STATUS_CLIENT_HOLD),
						EPPDomain.REMOVE);

				// Is the contact mapping supported?
				if (EPPFactory.getInstance().hasService(
						EPPDomainMapFactory.NS_CONTACT)) {
					theDomain.setUpdateAttrib(EPPDomain.CONTACT, "SH0000",
							EPPDomain.CONTACT_BILLING, EPPDomain.REMOVE);
				}

				// Update the authInfo value
				theDomain.setAuthString("new-auth-info-123");

				EPPCoaExtAttr attr = new EPPCoaExtAttr("KEY1", "value1");
				List addAttrs = new ArrayList();
				addAttrs.add(attr);

				theDomain.setCoaUpdateForPut(addAttrs);

				// Execute update
				theResponse = theDomain.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("domainUpdate adding a COA: Response = ["
						+ theResponse + "]\n\n");

				this.handleResponse(theResponse);

			}
			catch (EPPCommandException ex) {
				TestUtil.handleException(theSession, ex);
			}

			try {

				theDomain.setTransId(getClientTransId(request));

				String theDomainName = this.makeDomainName();

				logger.debug("\ndomainUpdate: Domain " + theDomainName
						+ " update removing a COA.");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(NSSubProduct.COM);

				// Add attributes
				// Is the contact mapping supported?
				if (EPPFactory.getInstance().hasService(
						EPPDomainMapFactory.NS_CONTACT)) {
					theDomain.setUpdateAttrib(EPPDomain.CONTACT, "SH0000",
							EPPDomain.CONTACT_BILLING, EPPDomain.ADD);
				}

				theDomain.setUpdateAttrib(EPPDomain.HOST,
						this.makeHostName(theDomainName), EPPDomain.ADD);

				theDomain.setUpdateAttrib(EPPDomain.STATUS,
						new EPPDomainStatus(EPPDomain.STATUS_CLIENT_HOLD),
						EPPDomain.ADD);

				// Remove attributes
				theDomain.setUpdateAttrib(EPPDomain.HOST,
						this.makeHostName(theDomainName), EPPDomain.REMOVE);

				theDomain.setUpdateAttrib(EPPDomain.STATUS,
						new EPPDomainStatus(EPPDomain.STATUS_CLIENT_HOLD),
						EPPDomain.REMOVE);

				// Is the contact mapping supported?
				if (EPPFactory.getInstance().hasService(
						EPPDomainMapFactory.NS_CONTACT)) {
					theDomain.setUpdateAttrib(EPPDomain.CONTACT, "SH0000",
							EPPDomain.CONTACT_BILLING, EPPDomain.REMOVE);
				}

				// Update the authInfo value
				theDomain.setAuthString("new-auth-info-123");

				EPPCoaExtKey key = new EPPCoaExtKey("KEY1");
				List remAttrs = new ArrayList();
				remAttrs.add(key);

				theDomain.setCoaUpdateForRem(remAttrs);

				// Execute update
				theResponse = theDomain.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("domainUpdate removing a COA: Response = ["
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

		printEnd("doDomainCoaUpdate");
	}

} // End class NSDomainTst
