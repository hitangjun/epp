/***********************************************************
Copyright (C) 2013 VeriSign, Inc.

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
package com.verisign.epp.codec.registry;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.codec.registry.EPPRegistryServices.EPPRegistryURI;
import com.verisign.epp.util.EPPCatFactory;

public class EPPRegistryServicesExt implements EPPCodecComponent {
	private static final long serialVersionUID = -5458358915104740335L;
	
	/** Log4j category for logging */
	private static Logger cat = Logger.getLogger(EPPRegistryServicesExt.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	public final static String ELM_NAME = "registry:svcExtension";
	
	// EPPRegistryExtURI
	private List extURIs = new ArrayList();

	public Element encode(Document aDocument) throws EPPEncodeException {
		if (extURIs == null || extURIs.size() == 0) {
			throw new EPPEncodeException(
					"Invalid state on EPPRegistryServices.encode: extURIs is required.");
		}
		
		Element root = aDocument.createElementNS(EPPRegistryMapFactory.NS,
				ELM_NAME);
		EPPUtil.encodeCompList(aDocument, root, extURIs);

		return root;
	}

	public void decode(Element aElement) throws EPPDecodeException {
		extURIs = EPPUtil.decodeCompList(aElement, EPPRegistryMapFactory.NS,
				EPPRegistryURI.ELM_EXT_URI, EPPRegistryExtURI.class);
	}
	
	public Object clone() throws CloneNotSupportedException {
		EPPRegistryServicesExt clone = (EPPRegistryServicesExt) super.clone();

		if (extURIs != null) {
			clone.extURIs = (List) ((ArrayList) extURIs).clone();
		}

		return clone;
	}
	
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPRegistryServicesExt)) {
			return false;
		}

		EPPRegistryServicesExt theComp = (EPPRegistryServicesExt) aObject;
		
		if (!((extURIs == null) ? (theComp.extURIs == null) : EPPUtil
				.equalLists(extURIs, theComp.extURIs))) {
			cat.error("EPPRegistryServices.equals(): extURIs not equal");
			return false;
		}

		return true;
	}
	
	public String toString() {
		return EPPUtil.toString(this);
	}
	
	public List getExtURIs() {
		return extURIs;
	}

	public void setExtURIs(List extURIs) {
		this.extURIs = extURIs;
	}

	public void addExtURI(EPPRegistryExtURI uri) {
		if (this.extURIs == null) {
			this.extURIs = new ArrayList();
		}
		this.extURIs.add(uri);
	}

	public static class EPPRegistryExtURI extends EPPRegistryURI {
		private static final long serialVersionUID = -8864002579337501286L;

		public EPPRegistryExtURI() {
			super();
		}

		public EPPRegistryExtURI(String uri, Boolean required) {
			super(uri, required);
		}

		public String getRootName() {
			return EPPRegistryURI.ELM_EXT_URI;
		}
		
		public boolean equals(Object aObject) {
			return super.equals(aObject);
		}
	}
}
