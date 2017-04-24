/***********************************************************
Copyright (C) 2015 VeriSign, Inc.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-0107  USA

http://www.verisign.com/nds/naming/namestore/techdocs.html
 ***********************************************************/
package com.verisign.epp.codec.nv;

import com.verisign.epp.codec.gen.EPPCodecComponent;

/**
 * Interface implemented by the various concrete info results, including 
 * signed code and input.   
 * 
 * @see com.verisign.epp.codec.nv.EPPNameVerificationInfoCmd
 * @see com.verisign.epp.codec.nv.EPPNameVerificationInfoSignedCodeResult
 * @see com.verisign.epp.codec.nv.EPPNameVerificationInfoInputResult
 */
public interface EPPNameVerificationInfoResult extends EPPCodecComponent {
	
}