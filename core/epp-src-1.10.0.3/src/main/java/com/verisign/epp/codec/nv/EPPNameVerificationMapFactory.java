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

package com.verisign.epp.codec.nv;

import java.util.LinkedHashSet;
import java.util.Set;

import org.w3c.dom.Element;

import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPCommand;
import com.verisign.epp.codec.gen.EPPMapFactory;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPService;
import com.verisign.epp.codec.gen.EPPUtil;

/**
 * Factory for the Name Verification command and response objects.
 */
public class EPPNameVerificationMapFactory extends EPPMapFactory {
	/** XML Namespace */
	public static final String NS = "urn:ietf:params:xml:ns:nv-1.0";

	/** XML Namespace Prefix */
	public static final String NS_PREFIX = "nv";

	/** EPP IDN Map Extension XML Schema. */
	public static final String NS_SCHEMA = "urn:ietf:params:xml:ns:nv-1.0 nv-1.0.xsd";

	/**
	 * XML tag name associated with Name Verification (NV) authorization information. This
	 * value will be passed to the authInfo object.
	 */
	public static final String ELM_NV_AUTHINFO = NS_PREFIX + ":" + "authInfo";
	
	
	/**
	 * Service object associated with EPPNameVerificationMapFactory. The service object is
	 * used when creating the Greeting or the Login.
	 */
	private EPPService service;

	/**
	 * Default constructor for <code>EPPNameVerificationMapFactory</code>.
	 */
	public EPPNameVerificationMapFactory() {
		service = new EPPService(NS_PREFIX, NS, NS_SCHEMA);
		service.setServiceType(EPPService.OBJ_SERVICE);
	}

	/**
	 * Creates a concrete <code>EPPCommand</code> from an XML element that
	 * represents an EPP command element.
	 *
	 * @param aMapElement
	 *            Command XML element.
	 *
	 * @return Concrete <code>EPPCommand</code> associated with the Command XML
	 *         element.
	 *
	 * @exception EPPCodecException
	 *                Error creating the concrete <code>EPPCommand</code>
	 */
	public EPPCommand createCommand(Element aMapElement)
			throws EPPCodecException {
		String name = aMapElement.getLocalName();

		if (!aMapElement.getNamespaceURI().equals(NS)) {
			throw new EPPCodecException("Invalid mapping type " + name);
		}

		if (name.equals(EPPUtil.getLocalName(EPPNameVerificationCheckCmd.ELM_NAME))) {
			return new EPPNameVerificationCheckCmd();
		}
		else if (name.equals(EPPUtil.getLocalName(EPPNameVerificationInfoCmd.ELM_NAME))) {
			return new EPPNameVerificationInfoCmd();
		}
		else if (name.equals(EPPUtil.getLocalName(EPPNameVerificationCreateCmd.ELM_NAME))) {
			return new EPPNameVerificationCreateCmd();
		}
		else if (name.equals(EPPUtil.getLocalName(EPPNameVerificationUpdateCmd.ELM_NAME))) {
			return new EPPNameVerificationUpdateCmd();
		}
		else {
			throw new EPPCodecException("Invalid command element " + name);
		}
	}

	/**
	 * creates a concrete <code>EPPResponse</code> from the passed in XML
	 * Element tree. <code>aMapElement</code> must be the root node for the
	 * response extension.
	 * 
	 * @param aMapElement
	 *            Mapping Extension EPP XML Element.
	 * 
	 * @return Concrete <code>EPPResponse</code> instance associated with
	 *         <code>aMapElement</code>.
	 * 
	 * @exception EPPCodecException
	 *                Error creating concrete <code>EPPResponse</code>
	 */
	public EPPResponse createResponse(Element aMapElement)
			throws EPPCodecException {
		String name = aMapElement.getLocalName();

		if (!aMapElement.getNamespaceURI().equals(NS)) {
			throw new EPPCodecException("Invalid mapping type " + name);
		}

		if (name.equals(EPPUtil.getLocalName(EPPNameVerificationCheckResp.ELM_NAME))) {
			return new EPPNameVerificationCheckResp();
		}
		else if (name.equals(EPPUtil.getLocalName(EPPNameVerificationInfoResp.ELM_NAME))) {
			return new EPPNameVerificationInfoResp();
		}
		else if (name
				.equals(EPPUtil.getLocalName(EPPNameVerificationCreateResp.ELM_NAME))) {
			return new EPPNameVerificationCreateResp();
		}
		else if (name
				.equals(EPPUtil.getLocalName(EPPNameVerificationPendActionMsg.ELM_NAME))) {
			return new EPPNameVerificationPendActionMsg();
		}
		else {
			throw new EPPCodecException("Invalid response element " + name);
		}
	}

	/**
	 * Gets the service information associated with the concrete
	 * <code>EPPExtFactory</code>. The service information is used by
	 * <code>EPPFactory</code> for extracting the XML namespace associated with
	 * the extension factory.
	 *
	 * @return service description associated with the concrete
	 *         <code>EPPExtFactory</code>.
	 */
	public EPPService getService() {
		return service;
	}

	/**
	 * Gets the list of XML schemas that need to be pre-loaded into the XML
	 * Parser.
	 *
	 * @return <code>Set</code> of <code>String</code> XML Schema names that
	 *         should be pre-loaded in the XML Parser.
	 * 
	 * @see com.verisign.epp.codec.gen.EPPMapFactory#getXmlSchemas()
	 */
	public Set getXmlSchemas() {
		Set theSchemas = new LinkedHashSet();
		theSchemas.add("xmldsig-core-schema.xsd");
		theSchemas.add("verificationCode-1.0.xsd");
		theSchemas.add("nv-1.0.xsd");
		return theSchemas;
	}

}
