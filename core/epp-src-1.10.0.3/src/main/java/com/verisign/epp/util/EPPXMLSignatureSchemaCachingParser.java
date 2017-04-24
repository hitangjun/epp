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
 ***********************************************************/

package com.verisign.epp.util;

import org.apache.log4j.Logger;

/**
 * Extension of the <code>EPPSchemaCachingParser</code> that disables the 
 * <code>NORMALIZE_DATA</code> feature that causes an issue in validating 
 * XML signatures.  The <code>NORMALIZE_DATA</code> will trim elements based 
 * the rules defined by the XML schema, which invalidates the XML signature.  
 */
public class EPPXMLSignatureSchemaCachingParser extends EPPSchemaCachingParser {

	/** 
	 * Log4j category for logging 
	 */
	private static Logger cat = Logger.getLogger(EPPXMLSignatureSchemaCachingParser.class
			.getName(), EPPCatFactory.getInstance().getFactory());

	/**
	 * Default constructor.
	 */
	public EPPXMLSignatureSchemaCachingParser() {
		super();
	}

	/**
	 * 
	 * @param aSymbolTableSize
	 */
	public EPPXMLSignatureSchemaCachingParser(int aSymbolTableSize) {
		super(aSymbolTableSize);
	}
	
	/**
	 * Helper method to initialize this instance of <code>EPPSchemaCachingParser</code>.
	 * 
	 * @throws EPPParserException
	 *             Error initializing <code>EPPSchemaCachingParser</code>
	 */
	protected void init() throws EPPParserException {
		cat.debug("init() enter");
		
		super.init();
		
		// Disable the NORMALIZE_DATA feature of the parser.
		try {
			super.setFeature(EPPSchemaCachingParser.NORMALIZE_DATA, false);			
		}
		catch (Exception ex) {
			cat.error("setting NORMALIZE_DATA feature failed", ex);
			throw new EPPParserException(ex);
		}
		
		cat.debug("init(); exit");
	}

}
