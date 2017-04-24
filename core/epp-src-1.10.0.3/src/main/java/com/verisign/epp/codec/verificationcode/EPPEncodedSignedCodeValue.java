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

package com.verisign.epp.codec.verificationcode;

import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EPPSchemaCachingParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Class for the encoded signed code, which contains the code and the
 * <code>XMLSignature</code> itself. This class extends
 * <code>EPPSignedCode</code>.
 */
public class EPPEncodedSignedCodeValue extends EPPSignedCode {

	/**
	 * Serial version id for this class.
	 */
	private static final long serialVersionUID = -2581814950269930902L;

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(
			EPPEncodedSignedCodeValue.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * Constant for the code local name for encoded signedCode element
	 */
	public static final String ELM_LOCALNAME = "code";

	/**
	 * Constant for the code tag for signedCode element
	 */
	public static final String ELM_NAME = EPPVerificationCodeExtFactory.NS_PREFIX
			+ ":" + ELM_LOCALNAME;

	/**
	 * Create an <code>EPPEncodedSignedCodeValue</code> instance.
	 */
	public EPPEncodedSignedCodeValue() {
		super();
	}

	/**
	 * Construct Encoded SignedCode object from SignedCode object.
	 * 
	 * @param aSignedCode
	 *            SignedCode object
	 * @throws EPPEncodeException
	 *             Error encoding the SignedCode <code>byte[]</code>.
	 * @throws EPPDecodeException
	 *             Error decoding the encoded SignedCode <code>byte[]</code>.
	 */
	public EPPEncodedSignedCodeValue(EPPSignedCode aSignedCode)
			throws EPPEncodeException, EPPDecodeException {
		super(aSignedCode.encode());
	}

	/**
	 * Create an <code>EPPEncodedSignedCodeValue</code> with the code of the
	 * signed code.
	 * 
	 * @param aCode
	 *            Verification code
	 * @throws EPPEncodeException
	 *             Thrown if any errors prevent encoding.
	 */
	public EPPEncodedSignedCodeValue(EPPVerificationCode aCode)
			throws EPPEncodeException {
		super(aCode);
	}

	/**
	 * Create an <code>EPPEncodedSignedCodeValue</code> with the code and type
	 * of the signed code.
	 * 
	 * @param aCode
	 *            Verification code
	 * @param aType
	 *            Verification code type.
	 * @throws EPPEncodeException
	 *             Thrown if any errors prevent encoding.
	 */
	public EPPEncodedSignedCodeValue(String aCode, String aType)
			throws EPPEncodeException {
		super(aCode, aType);
	}

	/**
	 * Creates an <code>EPPEncodedSignedCodeValue</code> that is initialized by
	 * decoding the input <code>byte[]</code>.
	 * 
	 * @param aEncodedSignedCodeArray
	 *            <code>byte[]</code> to decode the attribute values
	 * @throws EPPDecodeException
	 *             Error decoding the input <code>byte[]</code>.
	 */
	public EPPEncodedSignedCodeValue(byte[] aEncodedSignedCodeArray)
			throws EPPDecodeException {
		cat.debug("EPPSignedCode(byte[]): enter");

		byte[] signedCodeXML = null;
		Element elm;
		ByteArrayInputStream is = null;
		try {
			is = new ByteArrayInputStream(aEncodedSignedCodeArray);
			DocumentBuilder parser = new EPPSchemaCachingParser();
			// Disable the validation
			parser.setErrorHandler(null);
			Document doc = parser.parse(is);
			elm = doc.getDocumentElement();
			String base64SignedCode = EPPUtil.getTextContent(elm);
			signedCodeXML = Base64.decodeBase64(base64SignedCode);
		}
		catch (Exception ex) {
			throw new EPPDecodeException(
					"Error decoding signed code array: " + ex);
		}
		finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				}
				catch (IOException e) {
				}
			}
		}

		super.decode(signedCodeXML);

		cat.debug("EPPSignedCode.decode(byte[]): exit");
	}

	/**
	 * Creates an <code>EPPEncodedSignedCodeValue</code> by decoding the a
	 * Signed Code Data (SMD) that is in a PEM-like input stream that includes
	 * the encoded signed code with a leading line &quot;-----BEGIN ENCODED
	 * SCODE-----&quot; and a trailing &quot;-----END ENCODED SCODE-----&quot;.
	 * 
	 * @param aScode
	 *            <code>InputStream</code> containing a Signed Code (SCODE)
	 * @throws EPPDecodeException
	 *             Error decoding the Signed Code (SCODE)
	 */
	public EPPEncodedSignedCodeValue(InputStream aScode)
			throws EPPDecodeException {
		cat.debug(
				"EPPEncodedSignedCodeValue.EPPEncodedSignedCodeValue(InputStream): enter");

		// Parse for the encoded signed code
		StringBuffer smdBuffer = new StringBuffer();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(aScode));
		String currLine;
		boolean addToSCODE = false;

		try {
			while ((currLine = bufferedReader.readLine()) != null) {

				if (currLine.equals("-----BEGIN ENCODED SCODE-----")) {
					addToSCODE = true;
				}
				else if (currLine.equals("-----END ENCODED SCODE-----")) {
					addToSCODE = false;
				}
				else if (addToSCODE) {
					smdBuffer.append(currLine);
				}
				else {
					// Ignore line
				}

			}
		}
		catch (IOException e) {
			throw new EPPDecodeException("Error reading SCODE: " + e);
		}

		// Base64 decode encoded signed code to signed code
		byte[] signedCodeXML = Base64.decodeBase64(smdBuffer.toString());

		// Decode the signed code XML
		super.decode(signedCodeXML);

		cat.debug(
				"EPPEncodedSignedCodeValue.EPPEncodedSignedCodeValue(InputStream): exit");
	}

	/**
	 * Decode the <code>EPPSignedCode</code> component
	 * 
	 * @param aElement
	 *            Root element of the <code>EPPSignedCode</code>
	 * @throws EPPDecodeException
	 *             Error decoding the <code>EPPSignedCode</code>
	 */
	public void decode(Element aElement) throws EPPDecodeException {
		cat.debug("EPPEncodedSignedCodeValue.decode(Element): enter");

		String base64SignedCode = EPPUtil.getTextContent(aElement);
		byte[] signedCodeXML = Base64.decodeBase64(base64SignedCode);
		super.decode(signedCodeXML);

		cat.debug("EPPEncodedSignedCodeValue.decode(Element): exit - normal");
	}

	/**
	 * Decode the Base64 encoded signed code value with no wrapping XML.
	 * 
	 * @param aBase64SignedCode
	 *            Base64 encoded <code>EPPSignedCode</code>, which does not
	 *            include the wrapper &lt;verificationCode:code&gt; XML element.
	 * @throws EPPDecodeException
	 *             Error decoding the Base64 encoded <code>EPPSignedCode</code>
	 */
	public void decodeValue(byte[] aBase64SignedCode)
			throws EPPDecodeException {
		cat.debug("EPPEncodedSignedCodeValue.decodeValue(byte[]): enter");

		byte[] signedCodeXML = Base64.decodeBase64(aBase64SignedCode);
		super.decode(signedCodeXML);

		cat.debug("EPPEncodedSignedCodeValue.decodeValue(byte[]): exit");
	}

	/**
	 * Decode the Base64 encoded signed code value with no wrapping XML.
	 * 
	 * @param aBase64SignedCode
	 *            Base64 encoded <code>EPPSignedCode</code>, which does not
	 *            include the wrapper &lt;verificationCode:code&gt; XML element.
	 * @throws EPPDecodeException
	 *             Error decoding the Base64 encoded <code>EPPSignedCode</code>
	 */
	public void decodeValue(String aBase64SignedCode)
			throws EPPDecodeException {
		cat.debug("EPPEncodedSignedCodeValue.decodeValue(String): enter");

		byte[] signedCodeXML = Base64.decodeBase64(aBase64SignedCode);
		super.decode(signedCodeXML);

		cat.debug("EPPEncodedSignedCodeValue.decodeValue(String): exit");
	}


	/**
	 * Encode the encoded signed code to a <code>byte[]</code>. This returns the
	 * XML with the wrapping &lt;verificationCode:code&gt; element with the
	 * Base64 encoded signed code value.
	 * 
	 * @return Encoded signed code XML
	 * @throws EPPEncodeException
	 *             Error encoding the signed code
	 */
	public byte[] encode() throws EPPEncodeException {
		cat.debug("EPPEncodedSignedCodeValue.encode(): enter");

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			Document doc = new DocumentImpl();
			Element root = this.encode(doc);
			doc.appendChild(root);

			TransformerFactory transFac = TransformerFactory.newInstance();
			Transformer trans = transFac.newTransformer();

			trans.transform(new DOMSource(root), new StreamResult(os));
		}
		catch (EPPEncodeException ex) {
			throw ex;
		}
		catch (Exception ex) {
			cat.error("Error encoding tradecode to byte[]: " + ex);
			throw new EPPEncodeException("Error encoding tradecode to byte[]");
		}

		cat.debug("EPPEncodedSignedCodeValue.encode(): exit");
		return os.toByteArray();
	}

	/**
	 * Sets all this instance's data in the given XML document
	 * 
	 * @param aDocument
	 *            a DOM Document to attach data to.
	 * @return The root element of this component.
	 * @throws EPPEncodeException
	 *             Thrown if any errors prevent encoding.
	 */
	public Element encode(Document aDocument) throws EPPEncodeException {
		cat.debug("EPPEncodedSignedCodeValue.encode(Document): enter");

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPSignedCode.encode(Document)");
		}

		Element root = aDocument.createElementNS(
				EPPVerificationCodeExtFactory.NS,
				EPPVerificationCodeExtFactory.NS_PREFIX + ":" + ELM_LOCALNAME);

		byte[] signedCodeXml = super.encode();

		String base64EncodedText = new String(
				Base64.encodeBase64(signedCodeXml, true));

		Text currVal = aDocument.createTextNode(base64EncodedText);
		root.appendChild(currVal);
		cat.debug("EPPEncodedSignedCodeValue.encode(Document): exit - encoded");
		return root;
	}

	/**
	 * Encodes the Base64 signed code value without the wrapping XML elements, 
	 * and with the option for chunking the Base64 into 76 character blocks.
	 * 
	 * @param aIsChunked
	 *            Chunk the Base64 output into 76 character blocks?
	 *            
	 * @return Base64 signed code value as a <code>String</code>
	 * 
	 * @throws EPPEncodeException
	 *             Error encoding the Base64 signed code value
	 */
	public byte[] encodeValueByteArray(boolean aIsChunked) throws EPPEncodeException {
		cat.debug("EPPEncodedSignedCodeValue.encodeValueByteArray(boolean): enter");
		byte[] signedCodeXml = super.encode();

		cat.debug("EPPEncodedSignedCodeValue.encodeValueByteArray(boolean): exit");
		return Base64.encodeBase64(signedCodeXml, aIsChunked);
	}
	
	/**
	 * Encodes the Base64 signed code value without the wrapping XML elements
	 * 
	 * @return Base64 signed code value as a <code>String</code> that is chunked into 
	 * 76 character blocks.
	 * 
	 * @throws EPPEncodeException
	 *             Error encoding the Base64 signed code value
	 */
	public byte[] encodeValueByteArray() throws EPPEncodeException {
		cat.debug("EPPEncodedSignedCodeValue.encodeValueByteArray(): enter");
		return this.encodeValueByteArray(true);
	}
	
	/**
	 * Encodes the Base64 signed code value without the wrapping XML elements, 
	 * and with the option for chunking the Base64 into 76 character blocks.
	 * 
	 * @param aIsChunked
	 *            Chunk the Base64 output into 76 character blocks?
	 *            
	 * @return Base64 signed code value as a <code>String</code>
	 * 
	 * @throws EPPEncodeException
	 *             Error encoding the Base64 signed code value
	 */
	public String encodeValue(boolean aIsChunked) throws EPPEncodeException {
		cat.debug("EPPEncodedSignedCodeValue.encodeValue(boolean): enter");
		byte[] signedCodeXml = super.encode();

		String base64EncodedText = new String(
				Base64.encodeBase64(signedCodeXml, aIsChunked));

		cat.debug("EPPEncodedSignedCodeValue.encodeValue(boolean): exit");
		return base64EncodedText;
	}

	/**
	 * Encodes the Base64 signed code value without the wrapping XML elements
	 * 
	 * @return Base64 signed code value as a <code>String</code> that is chunked into 
	 * 76 character blocks.
	 * 
	 * @throws EPPEncodeException
	 *             Error encoding the Base64 signed code value
	 */
	public String encodeValue() throws EPPEncodeException {
		cat.debug("EPPEncodedSignedCodeValue.encodeValue(): enter");
		return this.encodeValue(true);
	}
	
	
	/**
	 * Clone <code>EPPEncodedSignedCodeValue</code>.
	 * 
	 * @return clone of <code>EPPEncodedSignedCodeValue</code>
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		return (EPPEncodedSignedCodeValue) super.clone();
	}

}
