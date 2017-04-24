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

import java.security.SecureRandom;
import java.util.Random;

/**
 * Client transaction identifier generator that is 
 * used to generate a client transaction identifier 
 * when creating or terminating sessions in the session pool.  The format of the 
 * client transaction identifier is &lt;prefix&gt;'-'&lt;epoch time&gt;'-'&lt;random&gt;, where 
 * the default &lt;prefix&gt; is &quot;SAMPLE&quot;.  
 */
public class ClientTransIdGenerator implements EPPClientTransIdGenerator {
	
	/**
	 * Prefix to use in generating the client transaction identifier.
	 */
	private String prefix = "POOL";
	
	/**
	 * Random number generator
	 */
	private Random random = new SecureRandom();

	/**
	 * Default constructor.  
	 */
	public ClientTransIdGenerator() {
	}
	
	/**
	 * Constructor that takes the client transaction identifier prefix to use.  
	 * The sample format will be <prefix>'-'<epoch time>'-'<random>.
	 */
	public ClientTransIdGenerator(String aPrefix) {
		this.prefix = aPrefix;
	}

	/**
	 * Generates a sample client transaction identifier with the 
	 * format &lt;prefix&gt;'-'&lt;epoch time&gt;'-'&lt;random&gt; that is clipped 
	 * to the maximum 64 characters if needed.
     *
     * @return Generated client transaction identifier
	 */
	public String genClientTransId() {
		
		String clientTransId = this.prefix;
		clientTransId += "-";
		clientTransId += System.currentTimeMillis();
		clientTransId += "-";
		clientTransId += random.nextInt(1000);
		
		// Clip if above maximum length
		if (clientTransId.length() > 64) {
			clientTransId = clientTransId.substring(0, 64);
		}
		
		return clientTransId;
	}

}
