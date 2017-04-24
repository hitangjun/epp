/*******************************************************************************
 * The information in this document is proprietary to VeriSign and the VeriSign
 * Registry Business. It may not be used, reproduced, or disclosed without the
 * written approval of the General Manager of VeriSign Information Services.
 * 
 * PRIVILEGED AND CONFIDENTIAL VERISIGN PROPRIETARY INFORMATION (REGISTRY
 * SENSITIVE INFORMATION)
 * Copyright (c) 2016 VeriSign, Inc. All rights reserved.
 * **********************************************************
 */
package com.verisign.epp.pool;

/**
 * Interface that can be implemented to generate client transaction identifiers
 * used when creating a session in a session pool. Without setting a concrete
 * <code>ClientTransIdGenerator</code> no client transaction identifier will be
 * set with the EPP Login Command when creating the session or EPP Logout Command 
 * when terminating the session. It is up to the
 * concrete client transaction identifier generator to create a valid and unique
 * client transaction identifier.
 */
public interface EPPClientTransIdGenerator {

	/**
	 * Generates a client transaction identifier for use in creating a session
	 * in the session pool.
	 * 
	 * @return Generated client transaction identifier if one is to be set;
	 *         <code>null</code> otherwise.
	 */
	public String genClientTransId();
}
