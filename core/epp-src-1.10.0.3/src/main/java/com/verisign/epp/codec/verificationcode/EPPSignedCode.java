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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.verisign.epp.codec.gen.EPPCodecComponent;
import com.verisign.epp.codec.gen.EPPCodecException;
import com.verisign.epp.codec.gen.EPPDecodeException;
import com.verisign.epp.codec.gen.EPPEncodeException;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.exception.EPPException;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.EPPXMLErrorHandler;
import com.verisign.epp.util.EPPXMLSignatureParserPool;
import com.verisign.epp.util.EqualityUtil;

/**
 * Class for the signed code, which contains the verification code and the
 * <code>XMLSignature</code>.
 */
public class EPPSignedCode implements EPPCodecComponent {

	/**
	 * Serial version id for this class.
	 */
	private static final long serialVersionUID = 3210389145062853193L;

	/**
	 * Log4j category for logging
	 */
	private static Logger cat = Logger.getLogger(EPPSignedCode.class.getName(),
			EPPCatFactory.getInstance().getFactory());

	/**
	 * Constant for the code local name for signedCode element
	 */
	public static final String ELM_SIGNED_CODE_LOCALNAME = "signedCode";

	/**
	 * Constant for the code tag for signedCode element
	 */
	public static final String ELM_SIGNED_CODE = EPPVerificationCodeExtFactory.NS
			+ ":" + ELM_SIGNED_CODE_LOCALNAME;

	/**
	 * The ID attribute name
	 */
	private static final String ATTR_ID = "id";

	/**
	 * The ID attribute value used for the signed code
	 */
	private static final String ATTR_ID_VALUE = "signedCode";

	/**
	 * XML Signature Factory
	 */
	private static XMLSignatureFactory sigFactory = XMLSignatureFactory
			.getInstance("DOM");

	/**
	 * Case where constructing the signed code data (i.e. without signature),
	 * XML is not valid as per schema (Signature is required as per schema). In
	 * this case, ignore the XML parsing exception and create a DOM object.
	 */
	private static final boolean IGNORE_XML_PARSE_EXCEPTION = true;

	/**
	 * When we construct the signed code object from EPP request, throw any
	 * parsing exception back to caller.
	 */
	private static final boolean DO_NOT_IGNORE_XML_PARSE_EXCEPTION = false;

	/**
	 * XML local name for the root element of the signed code. This should be
	 * set to <code>ELM_SIGNED_CODE_LOCALNAME</code>.
	 */
	private String localName = ELM_SIGNED_CODE_LOCALNAME;

	/**
	 * Value used for the ID attribute name with the default of
	 * <code>ATTR_ID_VALUE</code>.
	 */
	private String attrIdValue = ATTR_ID_VALUE;

	/**
	 * Verification code value.
	 */
	private EPPVerificationCode code;

	/**
	 * Sign code DOM element.
	 */
	private Element signedCodeElement = null;

	/**
	 * Trust anchor of validated signed code.
	 */
	private TrustAnchor trustAnchor;
	
	/**
	 * Create an <code>EPPSignedCode</code> instance.
	 */
	public EPPSignedCode() {
	}

	/**
	 * Convert an <code>EPPEncodedSignedCode</code> into an
	 * <code>EPPSignedCode</code>.
	 * 
	 * @param aEncodedSignedCode
	 *            <code>EPPEncodedSignedCode</code> to convert from.
	 */
	public EPPSignedCode(EPPEncodedSignedCodeValue aEncodedSignedCode) {

		EPPSignedCode encodedSignedCode = (EPPEncodedSignedCodeValue) aEncodedSignedCode;

		this.code = encodedSignedCode.code;
		this.signedCodeElement = encodedSignedCode.signedCodeElement;
	}

	/**
	 * Create an <code>EPPSignedCode</code> with the code of the signed code.
	 * The default encoding is XML and the signature must be generated by
	 * calling {@link #sign(PrivateKey)}. Once the object is created using this
	 * constructor, one should not update the object. In the case the object
	 * gets updated, changes will not be included in XML/signature.
	 * 
	 * @param aCode
	 *            Verification code
	 * @throws EPPEncodeException
	 *             Thrown if any errors prevent encoding.
	 */
	public EPPSignedCode(EPPVerificationCode aCode) throws EPPEncodeException {
		this.code = aCode;
		parseAndSetSignedCodeElement(aCode);
	}

	/**
	 * Create an <code>EPPSignedCode</code> with the type and code of the signed
	 * code. The default encoding is XML and the signature must be generated by
	 * calling {@link #sign(PrivateKey)}. Once the object is created using this
	 * constructor, one should not update the object. In the case the object
	 * gets updated, changes will not be included in XML/signature.
	 * 
	 * @param aCode
	 *            Verification code
	 * @param aType
	 *            Type of the verification code
	 * @throws EPPEncodeException
	 *             Thrown if any errors prevent encoding.
	 */
	public EPPSignedCode(String aCode, String aType) throws EPPEncodeException {
		this.code = new EPPVerificationCode(aCode, aType);
		parseAndSetSignedCodeElement(this.code);
	}

	/**
	 * Create the <code>EPPSignedCode</code> object from the input
	 * <code>byte[]</code> (XML).
	 * 
	 * @param aSignedCodeArray
	 *            <code>byte[]</code> to decode the attribute values
	 * @throws EPPDecodeException
	 *             Error decoding the <code>byte[]</code>.
	 */
	public EPPSignedCode(byte[] aSignedCodeArray) throws EPPDecodeException {
		cat.debug("EPPSignedCode(byte[]): enter");
		this.decode(aSignedCodeArray);
		cat.debug("EPPSignedCode.decode(byte[]): exit");
	}

	/**
	 * Decode the <code>EPPSignedCode</code> component
	 * 
	 * @param aElement
	 *            Root element of the <code>EPPSignedCode</code>
	 * @throws EPPDecodeException
	 *             Error decoding the <code>EPPSignedCode</code>
	 */
	@Override
	public void decode(Element aElement) throws EPPDecodeException {
		cat.debug("EPPSignedCode.decode(Element): enter");
		decodeSignedCodeElement(aElement);
		cat.debug("EPPSignedCode.decode(Element): exit - normal");
	}

	/**
	 * Create a DOM document from byte array. Initialized the instance variables
	 * like code, issuer etc. Sets the signedCodeElement.
	 * 
	 * @param aSignedCodeArray
	 *            <code>byte[]</code> to decode the attribute values
	 * @throws EPPDecodeException
	 *             Error decoding the <code>byte[]</code>.
	 */
	public void decode(byte[] aSignedCodeArray) throws EPPDecodeException {
		Element elm;
		elm = parseAndGetDocElement(aSignedCodeArray);
		initializeObject(elm);
		this.signedCodeElement = elm;
	}

	/**
	 * Initialized the signedCode elements from DOM element and sets localName,
	 * attributeIdValue and code attributes of object to instance variables.
	 * Once these variables are set, can not be changed.
	 * 
	 * @param aElement
	 *            Root element of the <code>EPPSignedCode</code>
	 * @throws EPPDecodeException
	 *             Error decoding the <code>EPPSignedCode</code>
	 */
	private void initializeObject(Element aElement) throws EPPDecodeException {

		this.localName = aElement.getLocalName();

		// ID attribute name
		this.attrIdValue = aElement.getAttribute(ATTR_ID);

		// Code
		this.code = (EPPVerificationCode) EPPUtil.decodeComp(aElement,
				EPPVerificationCodeExtFactory.NS, EPPVerificationCode.ELM_NAME,
				EPPVerificationCode.class);
	}

	/**
	 * This method decode the specified DOM element. Initialized the instance
	 * variables like code. Sets the signedCodeElement.
	 * 
	 * @param aElement
	 *            Root element of the <code>EPPSignedCode</code>
	 * @throws EPPDecodeException
	 *             Error decoding the <code>EPPSignedCode</code>
	 */
	private void decodeSignedCodeElement(Element aElement)
			throws EPPDecodeException {
		initializeObject(aElement);
		try {
			this.signedCodeElement = parseAndGetDocElement(getByteArrayForElement(aElement));
		}
		catch (EPPEncodeException e) {
			throw new EPPDecodeException(e);
		}
	}

	/**
	 * Encode the signed code to a <code>byte[]</code>.
	 * 
	 * @return <code>byte[]</code> representing signed code
	 * @throws EPPEncodeException
	 *             Error encoding the signed code
	 */
	public byte[] encode() throws EPPEncodeException {
		return getByteArrayForElement(this.signedCodeElement);
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
	@Override
	public Element encode(Document aDocument) throws EPPEncodeException {
		cat.debug("EPPSignedCode.encode(Document): enter");

		if (aDocument == null) {
			throw new EPPEncodeException("aDocument is null"
					+ " on in EPPSignedCode.encode(Document)");
		}

		// By this time, signedCode element must have been set.
		if (this.signedCodeElement == null) {
			throw new EPPEncodeException("Signed code is not decoded properly.");
		}
		Node newAddedNode = aDocument.importNode(this.signedCodeElement, true);

		cat.debug("EPPSignedCode.encode(Document): exit - normal");

		return (Element) newAddedNode;
	}

	/**
	 * Validates required attributes. Throws EPPEncodeException if required
	 * attributes is not set.
	 * 
	 * @throws EPPEncodeException
	 *             Thrown if any required parameter is missing.
	 */
	private void validateRequiredAttributes() throws EPPEncodeException {
		// Validate required attributes
		if (this.code == null) {
			throw new EPPEncodeException("code is required.");
		}
	}

	/**
	 * Encode all attributes and create a DOM signed code element. * @param aId
	 * Identifier of signed code
	 * 
	 * @param aCode
	 *            Verification code
	 * @throws EPPEncodeException
	 *             Thrown if any errors prevent encoding.
	 */
	private void parseAndSetSignedCodeElement(EPPVerificationCode aCode)
			throws EPPEncodeException {

		this.code = aCode;
		validateRequiredAttributes();

		Document document = new DocumentImpl();

		Element root = document.createElementNS(
				EPPVerificationCodeExtFactory.NS,
				EPPVerificationCodeExtFactory.NS_PREFIX + ":" + this.localName);

		// Add the "id" Attribute
		root.setAttribute(ATTR_ID, this.attrIdValue);
		root.setIdAttribute(ATTR_ID, true);

		// Code
		EPPUtil.encodeComp(document, root, this.code);

		document.appendChild(root);

		byte[] xmlBytes = getByteArrayForElement(root);

		// Creating signedCode element from byte array instead of assigning the
		// root
		// element to signedCodeElement. This way, byte array remains the same
		// even if
		// we do encode/decode multiple time.
		try {
			this.signedCodeElement = parseAndGetDocElement(xmlBytes,
					IGNORE_XML_PARSE_EXCEPTION);
		}
		catch (EPPDecodeException e) {
			throw new EPPEncodeException(e);
		}
	}

	/**
	 * Clone <code>EPPSignedCode</code>. Signature element is not cloned.
	 * 
	 * @return clone of <code>EPPSignedCode</code>
	 * @exception CloneNotSupportedException
	 *                standard Object.clone exception
	 */
	public Object clone() throws CloneNotSupportedException {
		EPPSignedCode clone = (EPPSignedCode) super.clone();

		return clone;
	}

	/**
	 * Digitally sign the signed code using the passed private key. No
	 * certificates will be added using this method. If certificates need to be
	 * added use {@link #sign(PrivateKey, Certificate[])}.
	 * 
	 * @param aPrivateKey
	 *            Private key used to sign the signed code
	 * @throws EPPException
	 *             Error creating the digital signature
	 */
	public void sign(PrivateKey aPrivateKey) throws EPPException {
		cat.debug("EPPSignedCode.sign(PrivateKey): enter");
		this.sign(aPrivateKey, null);
		cat.debug("EPPSignedCode.sign(PrivateKey): exit");
	}

	/**
	 * Digitally sign the signed code using the passed private key and a chain
	 * of certificates.
	 * 
	 * @param aPrivateKey
	 *            Private key used to sign the signed code
	 * @param aCertChain
	 *            Certificate chain to include in the XMLSignature associated
	 *            with the private key. Pass <code>null</code> to not include
	 *            the certificate chain in the XMLSignature.
	 * @throws EPPException
	 *             Error creating the digital signature
	 */
	public void sign(PrivateKey aPrivateKey, Certificate[] aCertChain)
			throws EPPException {
		cat.debug("EPPSignedCode.sign(PrivateKey, Certificate[]): enter");

		// Required parameter is null?
		if (aPrivateKey == null) {
			throw new EPPException(
					"EPPSignedCode.sign(PrivateKey, Certificate[]): null aPrivateKey parameter");
		}

		try {
			DigestMethod digestMethod = sigFactory.newDigestMethod(
					DigestMethod.SHA256, null);

			List<Transform> transforms = new ArrayList<Transform>();
			transforms.add(sigFactory.newTransform(Transform.ENVELOPED,
					(TransformParameterSpec) null));
			Reference xmlSigRef = sigFactory.newReference("#"
					+ this.attrIdValue, digestMethod, transforms, null, null);

			// Create the SignedInfo
			SignedInfo signedInfo = sigFactory.newSignedInfo(sigFactory
					.newCanonicalizationMethod(
							CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
							(C14NMethodParameterSpec) null), sigFactory
					.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
					Collections.singletonList(xmlSigRef));

			// Add certificate chain to signature?
			KeyInfo keyInfo = null;
			if (aCertChain != null) {
				cat.debug("EPPSignedCode.sign(PrivateKey, Certificate[]): certificate chain passed");

				KeyInfoFactory keyInfoFactory = sigFactory.getKeyInfoFactory();
				List<X509Certificate> certChain = new ArrayList<X509Certificate>();
				for (Certificate cert : aCertChain) {
					if (cert == null || !(cert instanceof X509Certificate)) {
						throw new EPPException(
								"EPPSignedCode.sign(PrivateKey, Certificate[]): Null or invalid certificate type");
					}
					certChain.add((X509Certificate) cert);
					cat.debug("EPPSignedCode.sign(PrivateKey, Certificate[]): Added certificate ["
							+ cert + "] to X509Certificate list");
				}
				List<X509Data> certDataList = new ArrayList<X509Data>();
				certDataList.add(keyInfoFactory.newX509Data(certChain));
				keyInfo = keyInfoFactory.newKeyInfo(certDataList);
			}
			else {
				cat.debug("EPPSignedCode.sign(PrivateKey, Certificate[]): null certificate chain passed, no certificates added");
			}

			DOMSignContext signContext = new DOMSignContext(aPrivateKey,
					this.signedCodeElement);
			signContext.setDefaultNamespacePrefix("dsig");
			XMLSignature signature = sigFactory.newXMLSignature(signedInfo,
					keyInfo);
			signature.sign(signContext);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			cat.debug("Error signing the EPPSignedCode: " + ex);
			throw new EPPException(
					"EPPSignedCode.sign(PrivateKey, Certificate[]): Error signing the EPPSignedCode");
		}
		cat.debug("EPPSignedCode.sign(PrivateKey, Certificate[]): exit");
	}

	/**
	 * Validate the signature attribute against the signed code attributes by
	 * using the public key of the certificate or the top certificate in the
	 * certificate chain contained in the <code>XMLSignature</code> with using
	 * the passed PKIX parameters to the PKIX <code>CertPathValidator</code>
	 * algorithm. The trust store can be loaded and used to create an instance
	 * of <code>PKIXParameters</code> to verify the certificate chain included
	 * in the <code>XMLSignature</code> with the trust anchors included in the
	 * trust store. This method will automatically synchronize the
	 * <code>aPKIXParameters</code> parameter when used, since it is not
	 * thread-safe. Use {@link #validate(PKIXParameters, boolean)} to explicitly
	 * set the <code>aPKIXParameters</code> synchronization setting.
	 * 
	 * @param aPKIXParameters
	 *            Parameters used as input for the PKIX
	 *            <code>CertPathValidator</code> algorithm.
	 * @return <code>true</code> if valid; <code>false</code> otherwise.
	 */
	public boolean validate(PKIXParameters aPKIXParameters) {
		return this.validate(aPKIXParameters, true);
	}

	/**
	 * Returns the signature element from signed code DOM object.
	 * 
	 * @return signature element from signedCode DOM object.
	 */
	private Element findSignatureElement() {
		return EPPUtil.getElementByTagNameNS(this.signedCodeElement,
				XMLSignature.XMLNS, "Signature");
	}

	/**
	 * Validate the signature attribute against the signed code attributes by
	 * using the public key of the certificate or the top certificate in the
	 * certificate chain contained in the <code>XMLSignature</code> with using
	 * the passed PKIX parameters to the PKIX <code>CertPathValidator</code>
	 * algorithm. The trust store can be loaded and used to create an instance
	 * of <code>PKIXParameters</code> to verify the certificate chain included
	 * in the <code>XMLSignature</code> with the trust anchors included in the
	 * trust store.
	 * 
	 * @param aPKIXParameters
	 *            Parameters used as input for the PKIX
	 *            <code>CertPathValidator</code> algorithm.
	 * @param aSynchronizePKIXParameters
	 *            Should the <code>aPKIXParameters</code> be synchronized inside
	 *            the method? If there is no reason to synchronize, then
	 *            <code>false</code> can be passed to increase performance.
	 * @return <code>true</code> if valid; <code>false</code> otherwise.
	 */
	public boolean validate(PKIXParameters aPKIXParameters,
			boolean aSynchronizePKIXParameters) {

		cat.debug("validate(PKIXParameters): enter");

		boolean valid = false;

		try {
			Element sigElement = findSignatureElement();

			DOMStructure domStructure = new DOMStructure(sigElement);
			XMLSignature signature = sigFactory
					.unmarshalXMLSignature(domStructure);

			// No key info found?
			if (signature.getKeyInfo() == null) {
				throw new Exception("No key info found in Signature");
			}

			List<X509Certificate> certificates = null;

			List<XMLStructure> keyContent = signature.getKeyInfo().getContent();

			// For each signature keyInfo item
			for (XMLStructure currInfo : keyContent) {

				// X509 data?
				if (currInfo instanceof X509Data) {

					List<?> x509Data = ((X509Data) currInfo).getContent();

					if (x509Data == null) {
						continue;
					}

					// For each X509Data element
					for (Object currX509Data : x509Data) {

						// X509Certificate?
						if (currX509Data instanceof X509Certificate) {

							if (certificates == null) {
								certificates = new ArrayList<X509Certificate>();
							}

							X509Certificate x509Cert = (X509Certificate) currX509Data;

							// Check validity of certificate
							x509Cert.checkValidity();

							cat.debug("validate(PKIXParameters): Found X509Certificate ["
									+ x509Cert + "]");
							certificates.add(x509Cert);
						}
					}
				}

				// No Certificates found?
				if (certificates == null || certificates.isEmpty()) {
					throw new Exception("No certificates found in Signature");
				}

				CertificateFactory certFactory = CertificateFactory
						.getInstance("X.509");
				CertPath certPath = certFactory.generateCertPath(certificates);

				// Validate certificate path against trust anchors
				// (aPKIXParameters)
				CertPathValidator pathValidator = CertPathValidator
						.getInstance("PKIX");

				PKIXCertPathValidatorResult result = null;
				
				// Must synchronize around the use of PKIXParameters
				// since it is NOT thread-safe.
				if (aSynchronizePKIXParameters) {
					synchronized (aPKIXParameters) {
						result = (PKIXCertPathValidatorResult) pathValidator.validate(certPath, aPKIXParameters);
					}
				}
				else {
					result = (PKIXCertPathValidatorResult) pathValidator.validate(certPath, aPKIXParameters);
				}
				
				this.trustAnchor = result.getTrustAnchor();
			}

			// Get the public key from top certificate to validate signature
			X509Certificate cert = certificates.get(0);
			cat.debug("validate(PKIXParameters): Getting public key from top certificate ["
					+ cert + "]");
			PublicKey publicKey = cert.getPublicKey();

			valid = this.validate(sigElement, publicKey);
		}
		catch (Exception ex) {
			cat.debug("validate(PKIXParameters): Error validating the EPPSignedCode: "
					+ ex);
			valid = false;
		}

		cat.debug("validate(PKIXParameters): exit, valid = " + valid);
		return valid;

	}

	/**
	 * Validate the signature attribute against the signed code attributes.
	 * 
	 * @param aPublicKey
	 *            Public used to validate the signature
	 * @return <code>true</code> if valid; <code>false</code> otherwise.
	 */
	public boolean validate(PublicKey aPublicKey) {
		cat.debug("validate(PublicKey): enter");

		boolean valid = false;

		try {
			Element sigElm = this.findSignatureElement();
			valid = this.validate(sigElm, aPublicKey);
		}
		catch (Exception ex) {
			cat.debug("validate(PublicKey): Error validating the EPPSignedCode: "
					+ ex);
			valid = false;
		}

		cat.debug("validate(PublicKey): exit, valid = " + valid);
		return valid;
	}

	/**
	 * Generate the byte array for specified DOM element and return the byte
	 * array.
	 * 
	 * @param aElement
	 *            DOM element for which to get the byte array
	 * @return <code>byte[]</code> representing DOM element
	 * @throws EPPEncodeException
	 *             thrown if any errors prevent transforming.
	 */
	private byte[] getByteArrayForElement(Element aElement)
			throws EPPEncodeException {
		cat.debug("EPPSignedCode.getByteArrayForElement(): enter");

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {

			TransformerFactory transFac = TransformerFactory.newInstance();
			Transformer trans = transFac.newTransformer();

			trans.transform(new DOMSource(aElement), new StreamResult(os));
		}
		catch (Exception ex) {
			cat.debug("Error encoding signed code to byte[]: " + ex);
			throw new EPPEncodeException("Error encoding signed code to byte[]");
		}

		cat.debug("EPPSignedCode.getByteArrayForElement(): exit");
		return os.toByteArray();
	}

	/**
	 * Parse the input byte array and return the DOM document element.
	 * 
	 * @param aSignedCodeArray
	 *            <code>byte[]</code> to parse to create DOM element.
	 * @return DOM element created from <code>byte[]</code>.
	 * @throws EPPDecodeException
	 *             thrown if any errors parsing <code>byte[]</code>.
	 */
	private Element parseAndGetDocElement(byte[] aSignedCodeArray)
			throws EPPDecodeException {
		return parseAndGetDocElement(aSignedCodeArray,
				DO_NOT_IGNORE_XML_PARSE_EXCEPTION);
	}

	/**
	 * Parse the input byte array and return the DOM document element.
	 * 
	 * @param aSignedCodeArray
	 *            <code>byte[]</code> to parse to create DOM element.
	 * @return DOM element created from <code>byte[]</code>.
	 * @throws EPPDecodeException
	 *             thrown if any errors parsing <code>byte[]</code>.
	 */
	private Element parseAndGetDocElement(byte[] aSignedCodeArray,
			boolean aIgnoreParsingError) throws EPPDecodeException {
		Element elm;
		ByteArrayInputStream is = null;
		DocumentBuilder parser = null;
		try {
			is = new ByteArrayInputStream(aSignedCodeArray);

			parser = (DocumentBuilder) EPPXMLSignatureParserPool.getInstance()
					.getPool().requestObject();

			if (aIgnoreParsingError) {
				// Disable error handler
				parser.setErrorHandler(null);
			}

			Document doc = parser.parse(is);

			elm = doc.getDocumentElement();
			elm.setIdAttribute("id", true);
			return elm;
		}
		catch (SAXParseException ex) {
			// Error generated by parser
			cat.debug(
					"parseAndGetDocElement():Got SAXParseException while decoding signed code array",
					ex);
			throw new EPPDecodeException("[SAXParseException]" + "\nline      "
					+ ex.getLineNumber() + "\ncolumn    "
					+ ex.getColumnNumber() + "\nuri       " + ex.getSystemId()
					+ "\nMessage : " + ex.getMessage(),
					EPPCodecException.SAX_EXCEPTION);
		}
		catch (SAXException ex) {
			// Error generated by this application
			cat.debug(
					"parseAndGetDocElement():Got SAXException while decoding signed code array",
					ex);
			throw new EPPDecodeException("[SAXException] " + ex,
					EPPCodecException.SAX_EXCEPTION);
		}
		catch (Exception ex) {
			throw new EPPDecodeException("Error decoding signed code array: "
					+ ex);
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
			if (parser != null) {
				if (aIgnoreParsingError) {
					// Re-enable error handler
					parser.setErrorHandler(new EPPXMLErrorHandler());
				}

				EPPXMLSignatureParserPool.getInstance().getPool()
						.returnObject(parser);
			}
		}
	}

	/**
	 * implements a deep <code>EPPSignedCode</code> compare.
	 * 
	 * @param aObject
	 *            <code>EPPSignedCode</code> instance to compare with
	 * @return true if equal false otherwise
	 */
	public boolean equals(Object aObject) {

		if (!(aObject instanceof EPPSignedCode)) {
			cat.error("EPPSignedCode.equals(): aObject is not an EPPSignedCode");
			return false;
		}

		EPPSignedCode other = (EPPSignedCode) aObject;

		// Code
		if (!EqualityUtil.equals(this.code, other.code)) {
			cat.error("EPPSignedCode.equals(): code not equal");
			return false;
		}

		return true;
	}

	/**
	 * Gets the XML local name for the signed code.
	 * 
	 * @return Either <code>ELM_SIGNED_MARK_LOCALNAME</code> or
	 *         <code>ELM_ENCODED_SIGNED_MARK_LOCALNAME</code>
	 */
	public String getLocalName() {
		return this.localName;
	}

	/**
	 * Gets the code value associated with the signed code.
	 * 
	 * @return The code associated with the signed code if defined:
	 *         <code>null</code> otherwise.
	 */
	public EPPVerificationCode getCode() {
		return this.code;
	}

	/**
	 * Gets the code value of the contained code.
	 * 
	 * @return Verification code value
	 */
	public String getCodeValue() {
		return this.code.getCode();
	}

	/**
	 * Gets the code type of the contained code.
	 * 
	 * @return Verification code type
	 */
	public String getCodeType() {
		return this.code.getType();
	}

	/**
	 * Gets the &quot;id&quot; attribute value.
	 * 
	 * @return Value of the &quot;id&quot; attribute value.
	 */
	public String getAttrIdValue() {
		return this.attrIdValue;
	}

	/**
	 * Has the Trust Anchor been set?
	 * 
	 * @return <code>true</code> if the Trust Anchor has been set; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasTrustAnchor() {
		return (this.trustAnchor != null ? true : false);
	}
	
	/**
	 * Gets the Trust Anchor associated with the validated signed code. 
	 * 
	 * @return Trust Anchor if defined; <code>null</code> otherwise.
	 */
	public TrustAnchor getTrustAnchor() {
		return this.trustAnchor;
	}


	/**
	 * Validate the signature attribute against the signed code attributes given
	 * the <code>Signature</code> DOM <code>Element</code>.
	 * 
	 * @param aSigElm
	 *            DOM <code>Signature Element</code>
	 * @param aPublicKey
	 *            Public used to validate the signature
	 * @return <code>true</code> if valid; <code>false</code> otherwise.
	 */
	private boolean validate(Element aSigElm, PublicKey aPublicKey) {
		cat.debug("validate(Element, PublicKey): enter");

		boolean valid = false;

		try {
			DOMValidateContext valContext = new DOMValidateContext(aPublicKey,
					aSigElm);
			XMLSignature signature = sigFactory
					.unmarshalXMLSignature(valContext);

			if (signature.validate(valContext)) {
				valid = true;
			}
			else {
				valid = false;
				cat.debug("validate(Element, PublicKey): validation status = "
						+ signature.getSignatureValue().validate(valContext));
				Iterator<?> i = signature.getSignedInfo().getReferences()
						.iterator();

				for (int j = 0; i.hasNext(); j++) {
					Reference next = (Reference) i.next();

					cat.debug("validate(Element, PublicKey): ref[" + j
							+ "], URI = " + next.getURI()
							+ ", validity status = "
							+ next.validate(valContext));
				}
			}
		}
		catch (Exception ex) {
			cat.debug("validate(Element, PublicKey): Error validating the EPPSignedCode: "
					+ ex);
			valid = false;
		}

		cat.debug("validate(Element, PublicKey): exit, valid = " + valid);
		return valid;
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