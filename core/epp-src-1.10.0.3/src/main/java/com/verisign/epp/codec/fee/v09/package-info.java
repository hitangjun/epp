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

/**
 * Fee Extension v09 packet encoder and decoder classes (CODEC).  
 * The classes in this package can be used to create a fee extension 
 * object that can be encoded to XML and decoded from XML.  The 
 * <code>EPPFeeExtFactory</code> must be registered with the <code>EPPFactory</code>
 * to be able to decode the XML into fee extension objects.  The 
 * <code>EPPFeeTst</code> class can be used to unit test all of the classes 
 * in the package, which includes creating the objects, encoding the 
 * XML, decoding the XML, comparing the original object with the 
 * decoded object, and finally serializing and de-serializing the object.     
 */
package com.verisign.epp.codec.fee.v09;