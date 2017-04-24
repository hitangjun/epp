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

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
//----------------------------------------------
//
// imports...
//
//----------------------------------------------
// Java Core Imports
// W3C Imports
import org.w3c.dom.Element;

// EPP Imports
import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EqualityUtil;

/**
 * <code>EPPNameVerificationDocument</code> represents an individual
 * verification document that includes a type and the &quot;base64&quot;
 * content.
 */
public class EPPNameVerificationDocument implements EPPCodecComponent {
	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPNameVerificationDocument.class.getName(), EPPCatFactory
					.getInstance().getFactory());

	/**
	 * Constant for the result local name
	 */
	public static final String ELM_LOCALNAME = "document";

	/**
	 * Constant for the profile tag
	 */
	public static final String ELM_NAME = EPPNameVerificationMapFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * File Type that include:<br>
	 * <ul>
	 * <li><code>PDF</code> - PDF file type</li>
	 * <li><code>JPG</code> - JPG file type.</li>
	 * </ul>
	 */
	public enum FileType {
		PDF("pdf"), JPG("jpg");

		private final String fileTypeStr;

		/**
		 * Define the string value for the enumerated value.
		 * 
		 * @param aFileTypeStr
		 *            Enumerated value string
		 */
		FileType(String aFileTypeStr) {
			this.fileTypeStr = aFileTypeStr;
		}

		/**
		 * Get the file type enumerated value given the matching string.
		 * 
		 * @param aString
		 *            <code>FileType</code> enumerated string to convert to an
		 *            enumerated <code>FileType</code> instance.
		 * 
		 * @return Enumerated <code>FileType</code> value matching the
		 *         <code>String</code>.
		 * 
		 * @throws InvalidParameterException
		 *             If <code>aString</code> does not match an enumerated
		 *             <code>FileType</code> string value.
		 */
		public static FileType getFileType(String aString) {
			if (aString.equals(PDF.fileTypeStr)) {
				return PDF;
			}
			else if (aString.equals(JPG.fileTypeStr)) {
				return JPG;
			}
			else {
				throw new InvalidParameterException("File type enum value of "
						+ aString + " is not valid.");
			}
		}

		/**
		 * Convert the enumerated <code>Role</code> value to a
		 * <code>String</code>.
		 */
		public String toString() {
			return this.fileTypeStr;
		}
	}

	/**
	 * XML Element Name for the label <code>fileType</code>.
	 */
	private final static String ELM_FILE_TYPE = "fileType";

	/**
	 * XML Element Name for the <code>fileContent</code>.
	 */
	private final static String ELM_FILE_CONTENT = "fileContent";

	/**
	 * Type of the document file
	 */
	private FileType fileType;

	/**
	 * &quot;base64&quot; encoded content of the file.
	 */
	private String fileContent;

	/**
	 * Default constructor for <code>EPPNameVerificationDocument</code>.
	 */
	public EPPNameVerificationDocument() {
	}

	/**
	 * Constructor for <code>EPPNameVerificationDocument</code> that two
	 * attributes of the document of the file type and file content.
	 *
	 * @param aFileType
	 *            Type of the file
	 * @param aFileContent
	 *            &quot;base64&quot; encoded content of the file
	 */
	public EPPNameVerificationDocument(FileType aFileType, String aFileContent) {
		this.fileType = aFileType;
		this.fileContent = aFileContent;
	}

	/**
	 * Gets the type of the document file.
	 * 
	 * @return Type of the document file.
	 */
	public FileType getFileType() {
		return this.fileType;
	}

	/**
	 * Sets the type of the document file.
	 * 
	 * @param aFileType
	 *            Type of the document file.
	 */
	public void setFileType(FileType aFileType) {
		this.fileType = aFileType;
	}

	/**
	 * Gets the &quot;base64&quot; file content.
	 * 
	 * @return &quot;base64&quot; file content.
	 */
	public String getFileContent() {
		return this.fileContent;
	}

	/**
	 * Sets the &quot;base64&quot; file content.
	 * 
	 * @param aFileContent
	 *            the fileContent to set
	 */
	public void setFileContent(String aFileContent) {
		this.fileContent = aFileContent;
	}

	/**
	 * Encode a DOM Element tree from the attributes of the
	 * <code>EPPNameVerificationDocument</code> instance.
	 *
	 * @param aDocument
	 *            DOM Document that is being built. Used as an Element factory.
	 *
	 * @return Element Root DOM Element representing the
	 *         <code>EPPNameVerificationDocument</code> instance.
	 *
	 * @exception EPPEncodeException
	 *                Unable to encode <code>EPPNameVerificationDocument</code>
	 *                instance.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {
		// Validate state
		if (fileType == null) {
			throw new EPPEncodeException(
					"fileType required attribute is not set");
		}
		if (fileContent == null) {
			throw new EPPEncodeException(
					"fileContent required attribute is not set");
		}

		Element root = aDocument.createElementNS(
				EPPNameVerificationMapFactory.NS, ELM_NAME);

		// File Type
		EPPUtil.encodeString(aDocument, root, this.fileType.toString(),
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":" + ELM_FILE_TYPE);

		// File Content
		EPPUtil.encodeString(aDocument, root, this.fileContent,
				EPPNameVerificationMapFactory.NS,
				EPPNameVerificationMapFactory.NS_PREFIX + ":"
						+ ELM_FILE_CONTENT);

		return root;
	}

	/**
	 * Decode the <code>EPPNameVerificationDocument</code> attributes from the
	 * aElement DOM Element tree.
	 *
	 * @param aElement
	 *            Root DOM Element to decode
	 *            <code>EPPNameVerificationDocument</code> from.
	 *
	 * @exception EPPDecodeException
	 *                Unable to decode aElement.
	 */
	public void decode(Element aElement) throws EPPDecodeException {

		// File Type
		String theFileTypeStr = EPPUtil.decodeString(aElement,
				EPPNameVerificationMapFactory.NS, ELM_FILE_TYPE);
		this.fileType = FileType.getFileType(theFileTypeStr);

		// File Content
		this.fileContent = EPPUtil.decodeString(aElement,
				EPPNameVerificationMapFactory.NS, ELM_FILE_CONTENT);
	}

	/**
	 * Compare an instance of <code>EPPNameVerificationDocument</code> with this
	 * instance.
	 *
	 * @param aObject
	 *            Object to compare with.
	 *
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	public boolean equals(Object aObject) {
		if (!(aObject instanceof EPPNameVerificationDocument)) {
			return false;
		}

		EPPNameVerificationDocument other = (EPPNameVerificationDocument) aObject;

		// File Type
		if (!EqualityUtil.equals(this.fileType, other.fileType)) {
			cat.error("EPPNameVerificationDocument.equals(): fileType not equal");
			return false;
		}

		// File Content
		if (!EqualityUtil.equals(this.fileContent, other.fileContent)) {
			cat.error("EPPNameVerificationDocument.equals(): fileContent not equal");
			return false;
		}

		return true;
	}

	/**
	 * Clone <code>EPPNameVerificationDocument</code>.
	 *
	 * @return clone of <code>EPPNameVerificationDocument</code>
	 *
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPNameVerificationDocument clone = null;

		clone = (EPPNameVerificationDocument) super.clone();

		return clone;
	}

	/**
	 * Implementation of <code>Object.toString</code>, which will result in an
	 * indented XML <code>String</code> representation of the concrete
	 * <code>EPPCodecComponent</code>.
	 *
	 * @return Indented XML <code>String</code> if successful;
	 *         <code>ERROR</code> otherwise.
	 */
	public String toString() {
		return EPPUtil.toString(this);
	}

}
