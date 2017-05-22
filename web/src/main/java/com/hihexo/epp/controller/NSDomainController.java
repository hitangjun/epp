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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/domain")
public class NSDomainController extends BaseNSController{
	
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(NSDomainController.class);

	@ApiOperation(value="createDomain", notes="未实现SecDNSCreate（安全的带证书验证dns设置？）" +
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
				if(StringUtils.isNotEmpty(params.getAllocationToken())){
					theDomain.setAllocationToken(params.getAllocationToken());
				}

				if(StringUtils.isNotEmpty(params.getIdnLangTag())){
					theDomain.setIDNLangTag(params.getIdnLangTag());
				}

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

		printEnd("testRelatedDomainCreate");
		return renderError("unknown");
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
				if(StringUtils.isNotEmpty(params.getAllocationToken())){
					theDomain.setAllocationToken(params.getAllocationToken());
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

				if(StringUtils.isNotEmpty(params.getAllocationToken())){
					theDomain.setAllocationToken(params.getAllocationToken());
				}

				theResponse = theDomain.sendInfo();
				this.handleResponse(theResponse);
				return renderSuccess(theResponse);
			}
			catch (Exception ex) {
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

				if(StringUtils.isNotEmpty(params.getAllocationToken())){
					theDomain.setAllocationToken(params.getAllocationToken());
				}

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

				if(StringUtils.isNotEmpty(params.getAllocationToken())){
					theDomain.setAllocationToken(params.getAllocationToken());
				}

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
				return renderError(ex.getMessage());
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

				if(StringUtils.isNotEmpty(params.getAllocationToken())){
					theDomain.setAllocationToken(params.getAllocationToken());
				}

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

		printEnd("doDomainSync");
		return renderError("fail");
	}

	/**
	 * <code>NSDomain.sendRestoreRequest</code> command.
	 */
	@RequestMapping(value = "/restorerequest",method = RequestMethod.POST) 	@SystemControllerLog(description = "RestoreRequest")
	@ResponseBody
	public ResultVo  doDomainRestoreRequest(HttpServletRequest request,@RequestBody  NSDomainParam params) {
		printStart("doDomainRestoreRequest");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);
			String theDomainName = params.getDomainName();

			try {
				theDomain.setTransId(getClientTransId(request));

				logger.debug("\ndomainRestoreRequest: Domain "
						+ theDomainName + " restore request");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(params.getDomainProductID());

				// Execute restore request
				theResponse = theDomain.sendRestoreRequest();

				// -- Output all of the response attributes
				logger.debug("domainRestoreRequest: Response = ["
						+ theResponse + "]\n\n");
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

		printEnd("doDomainRestoreRequest");
		return renderError("fail");
	}

	/**
	 * <code>NSDomain.sendRestoreReport</code> command.
	 */
	@ApiOperation(value = "域名恢复报告上送？" ,notes = "")
	@RequestMapping(value = "/restorereport",method = RequestMethod.POST) 	@SystemControllerLog(description = "restore报告")
	@ResponseBody
	public ResultVo  doDomainRestoreReport(HttpServletRequest request,@RequestBody  NSDomainRestoreReportParam params) {
		printStart("doDomainRestoreReport");

		EPPSession theSession = null;
		EPPResponse theResponse = null;
		try {
			theSession = this.borrowSession();
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);
			String theDomainName = params.getDomainName();

			try {
				theDomain.setTransId(getClientTransId(request));

				logger.debug("\ndomainRestoreReport: Domain "
						+ theDomainName + " restore request");

				theDomain.addDomainName(theDomainName);
				theDomain.setSubProductID(params.getDomainProductID());

				EPPRgpExtReport theReport = new EPPRgpExtReport();
				theReport
						.setPreData(params.getPreData());
				theReport
						.setPostData(params.getPostData());

				DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
				DateTime dateTime = DateTime.parse(params.getDeleteTime(), format);

				theReport.setDeleteTime(dateTime.toDate());
				dateTime = DateTime.parse(params.getRestoreTime(), format);
				theReport.setRestoreTime(dateTime.toDate());

				theReport.setRestoreReason(new EPPRgpExtReportText(params.getRestoreReason()));

				theReport
						.setStatement1(new EPPRgpExtReportText(params.getRestoreStatement1()));

				theReport
						.setStatement2(new EPPRgpExtReportText(params.getRestoreStatement2()));

				theReport.setOther(params.getOtherStuff());

				// Execute restore report
				theDomain.setReport(theReport);
				theResponse = theDomain.sendRestoreReport();

				// -- Output all of the response attributes
				logger.debug("domainRestoreReport: Response = ["
						+ theResponse + "]\n\n");
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

		printEnd("doDomainRestoreReport");
		return renderError("fail");
	}

	/**
	 * using {@link com.verisign.epp.namestore.interfaces.NSDomain#setResellerId(String)} to set the
	 * reseller identifier on create and
	 * {@link com.verisign.epp.namestore.interfaces.NSDomain#updateResellerId(Action, String)}
	 * to update the reseller identifier of an existing domain.
	 *
	 */
	@ApiOperation(value = "更新resslerid信息",notes = "创建或更新域名的resslerId信息，待实现")
	@RequestMapping(value = "/resellerid",method = RequestMethod.POST) 	@SystemControllerLog(description = "ResellerId")
	@ResponseBody
	@Deprecated
	public ResultVo  testResellerId(HttpServletRequest request, @RequestBody NSDomainParam params) {
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
				return renderError(ex.getMessage());
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
		return renderError("fail");
	}

	/**
	 * Unit test processing responses asynchronous from the commands. This is a
	 * test of the use of pipelining.
	 */
	@ApiOperation(value = "异步发送命令" ,notes = "通过队列TransId查询响应结果，应用于自定义任务，因为共享同一个session，或者同时发送多个请求给server时，使用该接口逻辑，待实现具体逻辑")
	@RequestMapping(value = "/asynccommands",method = RequestMethod.POST) 	@SystemControllerLog(description = "AsyncCommands")
	@ResponseBody
	@Deprecated
	public ResultVo  doAsyncCommands(HttpServletRequest request, @RequestBody NSDomainParam params ) {
		printStart("testAsyncCommands");

		EPPSession theSession = null;
		int previousSessionMode = EPPSession.MODE_SYNC;
		try {
			List clientTransIdQueue = new ArrayList();

			theSession = this.borrowSession();
			if (!theSession.isModeSupported(EPPSession.MODE_ASYNC)) {
				logger.debug("doAsyncCommands: Session "
						+ theSession.getClass().getName()
						+ " does not support MODE_ASYNC, skipping ");
				printEnd("doAsyncCommands (skipped)");
				return renderError("not support MODE_ASYNC");
			}
			previousSessionMode = theSession.setMode(EPPSession.MODE_ASYNC);
			com.verisign.epp.namestore.interfaces.NSDomain theDomain = new com.verisign.epp.namestore.interfaces.NSDomain(theSession);
			String theDomainName = params.getDomainName();

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
			return renderError(ex.getMessage());
		}
		finally {

			if (theSession != null) {
				theSession.setMode(previousSessionMode);
				this.returnSession(theSession);
			}
		}

		printEnd("testAsyncCommands");
		return renderError("fail");
	}

} // End class NSDomainTst
