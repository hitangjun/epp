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
import com.hihexo.epp.model.*;
import com.hihexo.epp.namestore.interfaces.NSSubProduct;
import com.verisign.epp.codec.coaext.EPPCoaExtAttr;
import com.verisign.epp.codec.coaext.EPPCoaExtKey;
import com.verisign.epp.codec.coaext.EPPCoaExtValue;
import com.verisign.epp.codec.domain.*;
import com.verisign.epp.codec.gen.EPPFactory;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.premiumdomain.EPPPremiumDomainCheck;
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
import com.verisign.epp.codec.secdnsext.v11.EPPSecDNSExtKeyData;
import com.verisign.epp.interfaces.EPPCommandException;
import com.verisign.epp.interfaces.EPPDomain;
import com.verisign.epp.interfaces.EPPSession;
import com.verisign.epp.namestore.interfaces.NSDomain;
import com.verisign.epp.util.InvalidateSessionException;
import com.verisign.epp.util.TestUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/domain")
public class NSDomainController extends BaseNSController{
	
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(NSDomainController.class);

	@ApiOperation(value="createDomain", notes="" +
			"参数示例：" +
			"{\n" +
			"  \"adminContact\": \"hihexo\",\n" +
			"  \"authStr\": \"string\",\n" +
			"  \"billContact\": \"hihexo\",\n" +
			"  \"domainName\": \"testcreatedomain.com\",\n" +
			"  \"hostNames\": [\n" +
			"    \"1234host.testcreatedomain.com\"\n" +
			"  ],\n" +
			"  \"period\": 1,\n" +
			"  \"periodUnit\": \"y\",\n" +
			"  \"coaExtKeyValuesMap\": {\n" +
			"    \"mykey1\": \"v1\",\n" +
			"    \"myk2\": \"v2\"\n" +
			"  },\n" +
			"  \"techContact\": \"hihexo\"\n" +
			"}" +
			"")
	@RequestMapping(value = "/create",method = RequestMethod.POST)
	@SystemControllerLog(description = "创建域名")
	@ResponseBody
	public Object doDomainCreate(HttpServletRequest request, @RequestBody NSDomainCreateParam params) {
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
				theDomain.setSubProductID(params.getDomainProductID());
				theDomain.setAuthString(getAuthString(params));


				for (String s:params.getHostNames()) {
					theDomain.addHostName(s);//DNS 解析？？// FIXME: 2017/4/25
				}

				// Is the contact mapping supported?
				//不同的联系人信息
				if (EPPFactory.getInstance().hasService(
						EPPDomainMapFactory.NS_CONTACT)) {
					if(StringUtils.isNotEmpty(params.getAdminContact())){
						theDomain.addContact(params.getAdminContact(),
								EPPDomain.CONTACT_ADMINISTRATIVE);
					}
					if(StringUtils.isNotEmpty(params.getTechContact())){
						theDomain.addContact(params.getTechContact(),
								EPPDomain.CONTACT_TECHNICAL);
					}
					if(StringUtils.isNotEmpty(params.getBillContact())){
						theDomain.addContact(params.getBillContact(),
								EPPDomain.CONTACT_BILLING);
					}
				}

				//设置注册期限 单位：年
				theDomain.setPeriodLength(params.getPeriod());
				theDomain.setPeriodUnit(params.getPeriodUnit());
				theDomain.setAuthString(getAuthString(params));
/**
				// -- Add secDNS Extension
				// instantiate a secDNS:keyData object
				EPPSecDNSExtKeyData keyData = params.getKeyData();
				// instantiate another secDNS:keyData object
				EPPSecDNSExtKeyData keyData2 = params.getKeyData2();

				EPPSecDNSExtDsData dsData = params.getDsData();
				EPPSecDNSExtDsData dsData2 = params.getDsData2();

				// dsData Records
				List dsDataRecords = new ArrayList();
				// instantiate a secDNS:dsData object
				if(dsData!=null){
					// instantiate another secDNS:dsData object
					dsData.setKeyData(keyData);
					dsDataRecords.add(dsData);
				}
				if(dsData2 !=null){
					dsData2.setKeyData(keyData2);
					dsDataRecords.add(dsData2);
				}

				if(dsDataRecords.size()>0){
					theDomain.setSecDNSCreate(dsDataRecords);
				}
 **/
				// Client Object Attributes
				if(params.getCoaExtKeyValuesMap() != null && params.getCoaExtKeyValuesMap().size() > 0){
					List attrList = new ArrayList();
					for (Map.Entry<String, String> entry : params.getCoaExtKeyValuesMap().entrySet()) {
						EPPCoaExtKey key = new EPPCoaExtKey(entry.getKey());
						EPPCoaExtValue value = new EPPCoaExtValue(entry.getValue());
						EPPCoaExtAttr attr = new EPPCoaExtAttr();
						attr.setKey(key);
						attr.setValue(value);
						attrList.add(attr);
					}
					theDomain.setCoaCreate(attrList);
				}


				theResponse = theDomain.sendCreate();

				// -- Output all of the response attributes
				logger.debug("domainCreate: Response = [" + theResponse
						+ "]\n\n");

				// -- Output response attributes using accessors
				logger.debug("domainCreate: name = "
						+ theResponse.getName());

				logger.debug("domainCreate: expiration date = "
						+ theResponse.getExpirationDate());
				return renderSuccess(theResponse);
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
		return renderError("unknown");
	}

	@RequestMapping(value = "/relatedcreate",method = RequestMethod.POST) 	@SystemControllerLog(description = "关联域名")
	@ResponseBody
	public Object  doRelatedDomainCreate(HttpServletRequest request, @RequestBody NSRelatedDomainCreateParam params) {
		printStart("RelatedDomainCreate ");

		EPPSession theSession = null;
		EPPDomainCreateResp theResponse = null;

		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {
				logger.debug
						("\ntestRelatedDomainCreate: Domain create of example.com and related domains");

				theDomain.addDomainName(params.getDomainName());
				theDomain.setTransId(getClientTransId(request));
				theDomain.setAuthString(params.getAuthStr());

				final EPPRelatedDomainExtAuthInfo authInfo = new EPPRelatedDomainExtAuthInfo(
						params.getRelatedDomainPwd());
				final EPPRelatedDomainExtPeriod period = new EPPRelatedDomainExtPeriod(
						params.getPeriodUnit(),params.getRelatedPeriod());

				for(String s : params.getRelatedDomain()){
					theDomain.addRelatedDomain(new EPPRelatedDomainExtDomain(
							s, authInfo, period));
				}

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
					return renderSuccess(theResponse);
				}
				else {
					Assert.fail("RelatedDomainCreate: EPPRelatedDomainExtCreateResp extension not included for domain-create with related domains.");
					return renderError("EPPRelatedDomainExtCreateResp extension not included for domain-create with related domains.");
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
		return renderError("unknown");
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
	@ApiOperation(value="createdsdata",notes="{\n" +
			"  \"authStr\": \"authstring\",\n" +
			"  \"domainName\": \"hihexo.com\",\n" +
			"  \"dsData\": {\n" +
			"    \"alg\": 3,\n" +
			"    \"digest\": \"49FD46E6C4B45C55D4AC\",\n" +
			"    \"digestType\": 1,\n" +
			"    \"keyTag\": 12345\n" +
			"  },\n" +
			"  \"keyData\": {\n" +
			"    \"alg\": 5,\n" +
			"    \"flags\": 257,\n" +
			"    \"protocol\": 3,\n" +
			"    \"pubKey\": \"AQPmsXk3Q1ngNSzsH1lrX63mRIhtwkkK+5ZjvxykBCV1NYne83+8RXkBElGb/YJ1n4TacMUspoZap7caJj7MdOaADKmzB2ci0vwpubNyW0t2AnaQqpy1ce+07Y8RkbTC6xCeEw1UQZ73PzIOOvJDdjwPxWaO9F7zSxnGpGt0WtuItQ==\"\n" +
			"  }\n" +
			"}")
//	@RequestMapping(value = "/createdsdata",method = RequestMethod.POST)
//	@SystemControllerLog(description = "创建DSdata")
//	@ResponseBody
	public Object  doCreateDsDataInterface(HttpServletRequest request, @RequestBody NSDomainDsDataCreateParam params) {
		printStart("CreateDsDataInterface");

		EPPSession theSession = null;
		EPPDomainCreateResp theResponse = null;

		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {
				logger.debug
						("\n----------------------------------------------------------------");

				String theDomainName = params.getDomainName();

				logger.debug
						("CreateDsDataInterface(1): domain = "
								+ theDomainName
								+ ", Create for a Secure Delegation using the DS Data Interface with one DS");

				theDomain.setTransId(getClientTransId(request));
				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(params.getDomainProductID());
				theDomain.setAuthString(params.getAuthStr());

				// Add DS
				List dsDataList = new ArrayList();
//				dsDataList.add(new EPPSecDNSExtDsData(12345,
//						EPPSecDNSAlgorithm.DSA,
//						EPPSecDNSExtDsData.SHA1_DIGEST_TYPE,
//						"49FD46E6C4B45C55D4AC"));

				// Key Data associated with DS to add
//				EPPSecDNSExtKeyData keyData = new EPPSecDNSExtKeyData();
//				keyData.setFlags(EPPSecDNSExtKeyData.FLAGS_ZONE_KEY_SEP);
//				keyData.setProtocol(EPPSecDNSExtKeyData.DEFAULT_PROTOCOL);
//				keyData.setAlg(EPPSecDNSAlgorithm.RSASHA1);
//				keyData.setPubKey("AQPmsXk3Q1ngNSzsH1lrX63mRIhtwkkK+5Zj"
//						+ "vxykBCV1NYne83+8RXkBElGb/YJ1n4TacMUs"
//						+ "poZap7caJj7MdOaADKmzB2ci0vwpubNyW0t2"
//						+ "AnaQqpy1ce+07Y8RkbTC6xCeEw1UQZ73PzIO"
//						+ "OvJDdjwPxWaO9F7zSxnGpGt0WtuItQ==");

//				dsDataList.add(keyData.toDsData(
//						"testCreateDsDataInterface.com",
//						EPPSecDNSExtDsData.SHA1_DIGEST_TYPE));

				if(params.getDsData()!=null){
					dsDataList.add(params.getDsData());
				}
				if(params.getKeyData()!=null){
					dsDataList.add(params.getKeyData().toDsData(params.getDomainName(),EPPSecDNSExtDsData.SHA1_DIGEST_TYPE));
				}
				if(dsDataList.size() > 0){
					theDomain.setSecDNSCreate(dsDataList);
				}

				theResponse = theDomain.sendCreate();

				return renderSuccess(theResponse);
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

		printEnd("CreateDsDataInterface");
		return renderError("unknown");
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
//	@RequestMapping(value = "/updatedsdata",method = RequestMethod.POST) 	@SystemControllerLog(description = "更新DSdata")
//	@ResponseBody
	public ResultVo doUpdateDsDataInterface(HttpServletRequest request, @RequestBody NSDomainParam params) {
		printStart("UpdateDsDataInterface");

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
				theDomain.setSubProductID(params.getDomainProductID());
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
		return null;
	}

	/**
	 * <code>NSDomain.sendDomainCheck</code> command.
	 */
	@ApiOperation(value = "domaincheck",notes = "" +
			"{\n" +
			"  \"authStr\": \"authString\",\n" +
			"  \"checkDomainNames\": [\n" +
			"    \"secDns.com\",\n" +
			"    \"secDnc.com\"\n" +
			"  ],\n" +
			"  \"domainProductID\": \"dotCom\",\n" +
			"  \"isCheckPremium\": false\n" +
			"}" +
			"" +
			"{\n" +
			"  \"authStr\": \"authString\",\n" +
			"  \"checkDomainNames\": [\n" +
			"    \"secDnc.tv\"\n" +
			"  ],\n" +
			"  \"domainProductID\": \"dotTv\",\n" +
			"  \"isCheckPremium\": true\n" +
			"}" +
			"")
	@RequestMapping(value = "/check",method = RequestMethod.POST) 	@SystemControllerLog(description = "核查域名")
	@ResponseBody
	public ResultVo  doDomainCheck(HttpServletRequest request,@RequestBody NSDomainCheckParam params) {
		printStart("doDomainCheck");

		EPPSession theSession = null;
		EPPDomainCheckResp theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {

				logger.debug("\n----------------------------------------------------------------");

				String theDomainName = params.getDomainName();
				logger.debug("domainCheck: Check single domain name ("
						+ theDomainName + ")");

				theDomain.setTransId(getClientTransId(request));
				for(String s:params.getCheckDomainNames()){
					theDomain.addDomainName(s);
				}
				theDomain.setSubProductID(params.getDomainProductID());

				if(params.getIsCheckPremium()){
					EPPPremiumDomainCheck extension = new EPPPremiumDomainCheck(
							true);
					theDomain.addExtension(extension);
				}

				theResponse = theDomain.sendCheck();
				this.handleResponse(theResponse);
			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
				return renderError(ex.getMessage());
			}

			return renderSuccess(theResponse);

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
		return renderError("unknown");
	}

	/**
	 * <code>NSDomain.sendDomainInfo</code> command.
	 */
	@ApiOperation(value = "info",notes = "{\n" +
			"  \"authStr\": \"authstring\",\n" +
			"  \"domainName\": \"secdns.com\",\n" +
			"  \"domainProductID\": \"dotCom\"\n" +
			"}")
	@RequestMapping(value = "/info",method = RequestMethod.POST) 	@SystemControllerLog(description = "域名信息")
	@ResponseBody
	public ResultVo  doDomainInfo(HttpServletRequest request,@RequestBody NSDomainInfoParam params) {
		printStart("doDomainInfo");

		EPPSession theSession = null;
		EPPDomainInfoResp theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {
				logger.debug("\ndomainInfo: Standard Domain info");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(params.getDomainName());
				theDomain.setSubProductID(params.getDomainProductID());
				theDomain.setHosts(com.verisign.epp.namestore.interfaces.NSDomain.HOSTS_ALL);
				theDomain.setWhoisInfo(true);
				theResponse = theDomain.sendInfo();
				this.handleResponse(theResponse);
				return renderSuccess(theResponse);
			}
			catch (Exception ex) {
				TestUtil.handleException(theSession, ex);
				return renderError("unknown");
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
		return renderError("unknown");
	}

	/**
	 * <code>NSDomain.sendDelete</code> command.
	 */
	@ApiOperation(value = "delete", notes = "")
	@RequestMapping(value = "/delete",method = RequestMethod.POST) 	@SystemControllerLog(description = "删除域名")
	@ResponseBody
	public ResultVo doDomainDelete(HttpServletRequest request, @RequestBody NSDomainParam params ) {
		printStart("doDomainDelete");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {
				logger.debug("\ndomainDelete: Domain delete");

				theDomain.setTransId(getClientTransId(request));

				theDomain.addDomainName(params.getDomainName());
				theDomain.setSubProductID(params.getDomainProductID());

				theResponse = theDomain.sendDelete();

				// -- Output all of the response attributes
				logger.debug("domainDelete: Response = [" + theResponse
						+ "]\n\n");

				this.handleResponse(theResponse);
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

		printEnd("doDomainDelete");
		return renderError("unknown");
	}

	/**
	 * <code>NSDomain.sendDomainRenew</code> command.
	 */
	@ApiOperation(value = "renew", notes = "")
	@RequestMapping(value = "/renew",method = RequestMethod.POST) 	@SystemControllerLog(description = "续费域名")
	@ResponseBody
	public ResultVo  doDomainRenew(HttpServletRequest request,@RequestBody NSDomainRenewParam params ) {
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

				theDomain.addDomainName(params.getDomainName());
				theDomain.setSubProductID(params.getDomainProductID());

				DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
				DateTime dateTime = DateTime.parse(params.getExpiretime(), format);
				theDomain.setExpirationDate(dateTime.toDate());

				theDomain.setPeriodLength(params.getPeriod());
				theDomain.setPeriodUnit(params.getPeriodUnit());

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
			return renderSuccess(theResponse);
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
		return renderError("unknown");
	}

	/**
	 * <code>NSDomain.sendUpdate</code> command.
	 */
	@RequestMapping(value = "/update",method = RequestMethod.POST) 	@SystemControllerLog(description = "更新域名")
	@ResponseBody
	public ResultVo  doDomainUpdate(HttpServletRequest request,@RequestBody NSDomainUpdateParam params ) {
		printStart("doDomainUpdate");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {

				theDomain.setTransId(getClientTransId(request));

				String theDomainName = params.getDomainName();

				logger.debug("\ndomainUpdate: Domain " + theDomainName
						+ " update");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(params.getDomainProductID());

				// Add attributes
				// Is the contact mapping supported?
				if (EPPFactory.getInstance().hasService(
						EPPDomainMapFactory.NS_CONTACT)) {
					for(String s:params.getAddBillContact()){
						theDomain.setUpdateAttrib(EPPDomain.CONTACT, s,
								EPPDomain.CONTACT_BILLING, EPPDomain.ADD);
					}
				}

				for(String s:params.getAddHostName()){
					theDomain.setUpdateAttrib(EPPDomain.HOST,s, EPPDomain.ADD);
				}

				for(String s:params.getAddDomainStatus()){
					theDomain.setUpdateAttrib(EPPDomain.STATUS,
							new EPPDomainStatus(s),
							EPPDomain.ADD);
				}

				// Remove attributes
				for(String s:params.getRemoveHostName()){
					theDomain.setUpdateAttrib(EPPDomain.HOST,s, EPPDomain.REMOVE);
				}

				for(String s:params.getRemoveDomainStatus()){
					theDomain.setUpdateAttrib(EPPDomain.STATUS,
							new EPPDomainStatus(s),
							EPPDomain.REMOVE);
				}

				// Is the contact mapping supported?
				if (EPPFactory.getInstance().hasService(
						EPPDomainMapFactory.NS_CONTACT)) {
					for(String s:params.getRemoveBillContact()){
						theDomain.setUpdateAttrib(EPPDomain.CONTACT, s,
								EPPDomain.CONTACT_BILLING, EPPDomain.REMOVE);
					}
				}

				// Update the authInfo value
				if(StringUtils.isNotEmpty(params.getNewAuthStr())){
					theDomain.setAuthString(params.getNewAuthStr());
				}

				if(StringUtils.isNotEmpty(params.getRegistararShortName())){
					EPPPremiumDomainReAssignCmd extension = new EPPPremiumDomainReAssignCmd();
					extension.setShortName(params.getRegistararShortName());
					theDomain.addExtension(extension);
				}

				// Execute update
				theResponse = theDomain.sendUpdate();

				// -- Output all of the response attributes
				logger.debug("domainUpdate: Response = [" + theResponse
						+ "]\n\n");

				this.handleResponse(theResponse);
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

		printEnd("doDomainUpdate");
		return renderError("unknown");
	}

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
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);
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
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);
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
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);
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
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);
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
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);
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
	/**
	 * <code>NSDomain.sendSync</code> command.
	 */
	@RequestMapping(value = "/sync",method = RequestMethod.POST) 	@SystemControllerLog(description = "同步域名")
	@ResponseBody
	public ResultVo  doDomainSync(HttpServletRequest request,@RequestBody  NSDomainSyncParam params) {
		printStart("doDomainSync");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);

			try {

				theDomain.setTransId(getClientTransId(request));

				String theDomainName = params.getDomainName();

				logger.debug("\ndomainSync: Domain " + theDomainName
						+ " sync");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(params.getDomainProductID());

				// Set to June 15th
				theDomain.setDay(params.getExpirateDay());
				theDomain.setMonth(params.getExpirateMonth());

				// Execute update
				theResponse = theDomain.sendSync();

				// -- Output all of the response attributes
				logger.debug("domainSync: Response = [" + theResponse
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

		printEnd("doDomainSync");
		return renderError("fail");
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
	public void  doAsyncCommands() {
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
	public void  doSecDNS10(HttpServletRequest request) {
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
