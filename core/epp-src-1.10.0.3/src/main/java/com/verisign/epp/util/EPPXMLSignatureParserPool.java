/*******************************************************************************
 * The information in this document is proprietary to VeriSign and the VeriSign
 * Registry Business. It may not be used, reproduced, or disclosed without the
 * written approval of the General Manager of VeriSign Information Services.
 * 
 * PRIVILEGED AND CONFIDENTIAL VERISIGN PROPRIETARY INFORMATION (REGISTRY
 * SENSITIVE INFORMATION)
 * Copyright (c) 2006 VeriSign, Inc. All rights reserved.
 * **********************************************************
 */

// jgould -- Dec 2, 2015

package com.verisign.epp.util;

import org.apache.log4j.Logger;

import com.codestudio.util.GenericPool;
import com.codestudio.util.GenericPoolManager;
import com.codestudio.util.GenericPoolMetaData;

/**
 * Singleton parser pool class that initializes the parser pool 
 * at initialization, and provides a {@link #getPool()} to 
 * get the initialized pool.  
  */
public class EPPXMLSignatureParserPool {

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPXMLSignatureParserPool.class.getName(),
			EPPCatFactory.getInstance().getFactory());
	
	/**
	 * Name of PoolMan parser pool initialized by
	 * <code>EPPXMLSignatureParserPool</code>.
	 */
	public static final String POOL = "EPPXMLSignatureParserPool";

	private static EPPXMLSignatureParserPool instance = new EPPXMLSignatureParserPool();

	/**
	 * Singleton <code>getInstance</code> method for accessing the <code>EPPXMLSignatureParserPool</code> 
	 * instance.
	 * 
	 * @return <code>EPPXMLSignatureParserPool</code> Singleton instance.
	 */
	public static EPPXMLSignatureParserPool getInstance() {
		return instance;
	}

	/**
	 * Default constructor that will initialize the parser pool.
	 */
	private EPPXMLSignatureParserPool() {
		cat.info("EPPXMLSignatureParserPool: initializing pool");
		GenericPoolMetaData parserMeta = new GenericPoolMetaData();

		parserMeta.setName(POOL);
		parserMeta
				.setObjectType("com.verisign.epp.util.EPPXMLSignatureSchemaCachingParser");
		parserMeta.setInitialObjects(EPPEnv.getXMLSignatureParserInitObjs());
		parserMeta.setMinimumSize(EPPEnv.getXMLSignatureParserMinSize());
		parserMeta.setMaximumSize(EPPEnv.getXMLSignatureParserMaxSize());
		parserMeta.setMaximumSoft(EPPEnv.getXMLSignatureParserMaxSoft());
		parserMeta.setObjectTimeout(EPPEnv.getXMLSignatureParserObjTimeout());
		parserMeta.setUserTimeout(EPPEnv.getXMLSignatureParserUserTimeout());
		parserMeta.setSkimmerFrequency(EPPEnv
				.getXMLSignatureParserSkimmerFreq());
		parserMeta.setShrinkBy(EPPEnv.getXMLSignatureParserShrinkBy());
		parserMeta.setLogFile(EPPEnv.getXMLSignatureParserLogFile());
		parserMeta.setDebugging(EPPEnv.getXMLSignatureParserDebug());

		GenericPool parserPool = new GenericPool(parserMeta);
		GenericPoolManager.getInstance().addPool(POOL, parserPool);
		cat.info("EPPXMLSignatureParserPool: pool initialized");
	}

	/**
	 * Gets the pool initialized by <code>EPPXMLSignatureParserPool</code> for
	 * getting and returning XML Signature parsers.
	 * 
	 * @return XML Signature parser pool initialized by
	 *         <code>EPPXMLSignatureParserPool</code>.
	 */
	public GenericPool getPool() {
		return (GenericPool) GenericPoolManager.getInstance().getPool(POOL);
	}

}
