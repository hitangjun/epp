/***********************************************************
Copyright (C) 2004 VeriSign, Inc.

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

package com.verisign.epp.util;

// Log4j Imports
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.parsers.XML11Configuration;
import org.apache.xerces.parsers.XMLGrammarCachingConfiguration;
import org.apache.xerces.parsers.XMLGrammarPreparser;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.verisign.epp.codec.gen.EPPFactory;

/**
 * XML Parser that pre-caches the XML schemas from the XML schemas registered in
 * the {@link EPPFactory} for improved XML parsing performance.
 */
public class EPPSchemaCachingParser extends DocumentBuilder implements EPPSchemaCacher {
	/**
	 * Name of the EPP XML Parser Pool managed by <code>GenericPoolManager</code>
	 * .
	 */
	public static final String POOL = "EPP_XML_PARSER_POOL";

	/** Log4j category for logging */
	private static Logger cat = Logger.getLogger(EPPSchemaCachingParser.class.getName(),
	      EPPCatFactory.getInstance().getFactory());

	/**
	 * A default prime number to use for the SymbolTable size. Some other sample
	 * big primes: 1299709 1299721 1299743 1299763 1299791 1299811 1299817
	 * 1299821 1299827
	 */
	public static final int BIG_PRIME = 2039;

	/** Namespaces feature id (http://xml.org/sax/features/namespaces). \ */
	public static final String NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces";

	/** Validation feature id (http://xml.org/sax/features/validation). */
	public static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";

	/**
	 * Schema validation feature id
	 * (http://apache.org/xml/features/validation/schema).
	 */
	public static final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";

	/**
	 * Schema full checking feature id
	 * (http://apache.org/xml/features/validation/schema-full-checking). Enable
	 * full schema grammar constraint checking, including checking which may be
	 * time-consuming or memory intensive. Currently, particle unique attribution
	 * constraint checking and particle derivation resriction checking are
	 * controlled by this option.
	 */
	public static final String SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking";

	/** Property identifier: The XML Grammar pool feature */
	public static final String XMLGRAMMAR_POOL = Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;

	/** Property identifier: The symbol table feature */
	protected static final String SYMBOL_TABLE = Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;

	/**
	 * Constant for setting the defer node expansion parser feature. The
	 * recommendation is to set this to false for EPP packets since the are not
	 * large documents.
	 */
	public static final String DEFER_NODE_EXPANSION = "http://apache.org/xml/features/dom/defer-node-expansion";

	/**
	 * Apache constant for loading external DTD's when they are seen in the
	 * instance document
	 */
	public static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

	/**
	 * True: Load the DTD and use it to add default attributes and set attribute
	 * types when parsing. False: Build the grammar but do not use the default
	 * attributes and attribute types information it contains.
	 */
	public static final String LOAD_DTD_GRAMMAR = "http://apache.org/xml/features/nonvalidating/load-dtd-grammar";

	/**
	 * True: Create EntityReference nodes in the DOM tree. The EntityReference
	 * nodes and their child nodes will be read-only. False: Do not create
	 * EntityReference nodes in the DOM tree. No EntityReference nodes will be
	 * created, only the nodes corresponding to their fully expanded substitution
	 * text will be created
	 */
	public static final String CREATE_ENTITY_REF_NODES = "http://apache.org/xml/features/dom/create-entity-ref-nodes";

	/**
	 * True: Include text nodes that can be considered "ignorable whitespace" in
	 * the DOM tree. False: Do not include ignorable whitespace in the DOM tree.
	 */
	public static final String INCLUDE_IGNORABLE_WHITE_SPACE = "http://apache.org/xml/features/dom/include-ignorable-whitespace";

	/**
	 * True: Include external general entities. False: Do not include external
	 * general entities
	 */
	public static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";

	/**
	 * True: Include external parameter entities and the external DTD subset.
	 * False: Do not include external parameter entities or the external DTD
	 * subset.
	 */
	public static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";

	/**
	 * attempt to normalize data in an entity reference or CDATA section. To
	 * preserve character data within entity references and CDATA sections, turn
	 * off http://apache.org/xml/features/validation/schema/normalized-value
	 * feature.
	 */
	public static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";

	/**
	 * The symbolTableSize to use for this instance. // 104659 104677 104681
	 * 104683 104693 104701 104707 104711 104717 104723 104729
	 */
	private int symbolTableSize = BIG_PRIME;

	/**
	 * The parser implementation that EPPSchemaCachingParser delegates to to do
	 * the real work
	 */
	private DOMParser parserImpl = null;

	/** The grammar pool instance that this parser will use */
	private XMLGrammarPool grammarPool = new XMLGrammarPoolImpl();

	/**
	 * Create a new instance of EPPSchemaCachingParser. Defaults are: Namespace
	 * aware: true Validation: true Schema Validation: true Full Schema checking:
	 * true
	 */
	public EPPSchemaCachingParser() {
		try {
			init();
		}
		catch (EPPParserException e) {
			cat.error("Couldn't instantiate parser instance", e);
		}
	}

	/**
	 * Creates an EPPSchemaCachingParser instance with the specified symboltable
	 * size
	 *
	 * @param aSymbolTableSize
	 */
	public EPPSchemaCachingParser(int aSymbolTableSize) {
		this.symbolTableSize = aSymbolTableSize;

		try {
			init();
		}
		catch (EPPParserException e) {
			cat.error("Couldn't instantiate parser instance", e);
		}
	}

	/**
	 * Helper method to initialize this instance of
	 * <code>EPPSchemaCachingParser</code>.
	 *
	 * @throws EPPParserException
	 *            Error initializing <code>EPPSchemaCachingParser</code>
	 */
	protected void init() throws EPPParserException {
		cat.debug("init() enter");

		cat.debug("Creating parser instance with symbol table size: " + this.symbolTableSize);

		if (this.symbolTableSize == 0) {
			this.parserImpl = new DOMParser(new XMLGrammarCachingConfiguration());
		}
		else {
			SymbolTable symbolTable = new SymbolTable(this.symbolTableSize);
			this.parserImpl = new DOMParser(new XML11Configuration(symbolTable));
		}

		// Set the use of entity resolver?
		if (EPPEnv.isUseEntityResolver()) {
			cat.debug("Setting Entity Resolver to " + EPPSchemaCachingEntityResolver.class.getName());
			this.parserImpl.setEntityResolver(new EPPSchemaCachingEntityResolver(this));
		}
		else {
			cat.debug("Not setting Entity Resolver");
			this.parserImpl.setEntityResolver(null);
		}

		// setup the default behavior for this parser
		cat.debug("Setting default parser features.");

		try {

			if (EPPEnv.getValidating()) {
				this.parserImpl.setFeature(VALIDATION_FEATURE_ID, true);
				this.parserImpl.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);
				cat.debug("Schema validation enabled.");
			}

			if (EPPEnv.getFullSchemaChecking()) {
				this.parserImpl.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);
				cat.debug("Full schema checking enabled.");
			}

			if (EPPEnv.getValidating() || EPPEnv.getFullSchemaChecking()) {
				this.parserImpl.setErrorHandler(new EPPXMLErrorHandler());
			}

			// set other properties of the parser -
			this.parserImpl.setFeature(LOAD_EXTERNAL_DTD, false);
			this.parserImpl.setFeature(LOAD_DTD_GRAMMAR, false);
			this.parserImpl.setFeature(CREATE_ENTITY_REF_NODES, false);
			this.parserImpl.setFeature(INCLUDE_IGNORABLE_WHITE_SPACE, false);
			this.parserImpl.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
			this.parserImpl.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);
			this.parserImpl.setFeature(NORMALIZE_DATA, true);

			this.parserImpl.setFeature(NAMESPACES_FEATURE_ID, true);
			this.parserImpl.setFeature(DEFER_NODE_EXPANSION, false);
		}
		catch (SAXException e) {
			cat.error("setting features of parserImpl failed", e);
			throw new EPPParserException(e);
		}

		// Pre-load the XML schemas based on the registered EPPMapFactory
		// and EPPExtFactory instances.
		Set theSchemas = EPPFactory.getInstance().getXmlSchemas();
		Iterator theSchemasIter = theSchemas.iterator();
		while (theSchemasIter.hasNext()) {
			String theSchemaName = (String) theSchemasIter.next();

			cat.debug("init(): Pre-loading XML schema \"" + theSchemaName + "\"");

			// lookup the file name in this classes's classpath under "schemas"
			InputStream theSchemaStream = getClass().getClassLoader().getResourceAsStream("schemas/" + theSchemaName);

			this.addSchemaToCache(new XMLInputSource(theSchemaName, theSchemaName, theSchemaName, theSchemaStream, null));
		}

		cat.debug("init() exit");
	}

	/**
	 * Lock the schema cache. True param locks it, false unlocks it
	 *
	 * @param aBoolean
	 *           True param locks it, false unlocks it
	 */
	public void setLockSchemaCache(boolean aBoolean) {
		cat.debug("Setting lockSchemaCache to: " + aBoolean);

		if (aBoolean) {
			this.grammarPool.lockPool();
		}
		else {
			this.grammarPool.unlockPool();
		}
	}

	/**
	 * Addes the XMLInputSource instance to the current cache of schemas. Only
	 * addes the schema to the cache if it isn't already in the cache.
	 *
	 * @param aSchema
	 *           The schema instance to be cached
	 *
	 * @throws EPPParserException
	 *            Error adding schema to the cache
	 */
	public void addSchemaToCache(XMLInputSource aSchema) throws EPPParserException {
		cat.debug("addSchemaToCache(XMLInputSource) enter");

		// Use the XMLGrammarParser to load this schema
		SymbolTable preparserSymTable = new SymbolTable(BIG_PRIME);
		XMLGrammarPreparser preparser = new XMLGrammarPreparser(preparserSymTable);

		// Register the XML Schema type as the type of grammar loader
		// we want to use.
		preparser.registerPreparser(XMLGrammarDescription.XML_SCHEMA, null);

		// Set the GrammarPool instance so the grammar will be added
		preparser.setGrammarPool(this.grammarPool);

		// Use the EPPSchemaParsingEntityResolver so that imported schemas
		// will be resolved by looking them up in the classpath
		preparser.setEntityResolver(new EPPSchemaParsingEntityResolver());

		// Turn on Namespace awareness and schema validation in the preparser
		preparser.setFeature(NAMESPACES_FEATURE_ID, true);
		preparser.setFeature(VALIDATION_FEATURE_ID, true);
		preparser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);
		preparser.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, true);

		cat.debug("parsing schema to add it to the pool: " + aSchema.getSystemId());

		try {
			preparser.preparseGrammar(XMLGrammarDescription.XML_SCHEMA, aSchema);
		}
		catch (IOException e) {
			cat.error("Error while attempting to add schema: " + aSchema.getSystemId());

			throw new EPPParserException(e);
		}

		try {
			this.parserImpl.setProperty(XMLGRAMMAR_POOL, this.grammarPool);
		}
		catch (SAXException e) {
			cat.error("Couldn't reset grammar pool when new grammar detected");
			throw new EPPParserException(e);
		}

		cat.debug("addSchemaToCache(XMLInputSource) exit");
	}

	/**
	 * Returns true if the parser is setup to be Namespace aware, false
	 * otherwise.
	 *
	 * @return true if the parser is setup to be Namespace aware, false
	 *         otherwise.
	 *
	 * @throws IllegalStateException
	 *            Parser is not in a state to query it's properties
	 */
	public boolean isNamespaceAware() {
		try {
			return this.parserImpl.getFeature(Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE);
		}
		catch (SAXException x) {
			throw new IllegalStateException(x.getMessage());
		}
	}

	/**
	 * Sets the EntityResolver of this DocumentBuilder
	 *
	 * @param aEntityResolver
	 *           The EntityResolver instance that this DocumentBuilder will use
	 *           when parsing.
	 */
	public void setEntityResolver(EntityResolver aEntityResolver) {
		this.parserImpl.setEntityResolver(aEntityResolver);
	}

	/**
	 * Returns the DOM implementation
	 *
	 * @return the DOM implementation
	 */
	public DOMImplementation getDOMImplementation() {
		return DOMImplementationImpl.getDOMImplementation();
	}

	/**
	 * Non-preferred: use the getDOMImplementation() method instead of this one
	 * to get a DOM Level 2 DOMImplementation object and then use DOM Level 2
	 * methods to create a DOM Document object.
	 *
	 * @return DOCUMENT ME!
	 */
	public Document newDocument() {
		return new org.apache.xerces.dom.DocumentImpl();
	}

	/**
	 * Parses the InputSource passed in which should be an XML instance. Returns
	 * a DOM document.
	 *
	 * @param aXmlInstance
	 *           The XML instance to parse
	 *
	 * @return The DOM Document that represents the XML instance
	 *
	 * @throws org.xml.sax.SAXException
	 *            Thrown if any errors occur
	 * @throws java.io.IOException
	 *            Thrown if any errors occur
	 * @throws IllegalArgumentException
	 *            DOCUMENT ME!
	 */
	public Document parse(InputSource aXmlInstance) throws org.xml.sax.SAXException, java.io.IOException {
		if (aXmlInstance == null) {
			throw new IllegalArgumentException("InputSource cannot be null");
		}

		this.parserImpl.parse(aXmlInstance);

		return this.parserImpl.getDocument();
	}

	/**
	 * Implemented as part of standard Document Builder API. Delegates to
	 * DOMParser
	 *
	 * @param aInputStream
	 *           The InputStream instance to parse
	 * @param aSystemId
	 *
	 * @return The DOM Document created after the parsing episode
	 *
	 * @throws org.xml.sax.SAXException
	 * @throws java.io.IOException
	 */
	public Document parse(InputStream aInputStream, String aSystemId)
	      throws org.xml.sax.SAXException, java.io.IOException {
		this.parserImpl.parse(new InputSource(aInputStream));

		return this.parserImpl.getDocument();
	}

	/**
	 * Implemented as part of standard Document Builder API. Delegates to
	 * DOMParser
	 *
	 * @param aUri
	 *           The URI to parse
	 *
	 * @return The DOM Document created after the parsing episode
	 *
	 * @throws org.xml.sax.SAXException
	 * @throws java.io.IOException
	 */
	public Document parse(String aUri) throws org.xml.sax.SAXException, java.io.IOException {
		this.parserImpl.parse(aUri);

		return this.parserImpl.getDocument();
	}

	/**
	 * Implemented as part of standard Document Builder API. Delegates to
	 * DOMParser
	 *
	 * @param aInputStream
	 *           The InputStream to parse
	 *
	 * @return The DOM Document created after parsing the stream
	 *
	 * @throws org.xml.sax.SAXException
	 * @throws java.io.IOException
	 */
	public Document parse(InputStream aInputStream) throws org.xml.sax.SAXException, java.io.IOException {
		this.parserImpl.parse(new InputSource(aInputStream));

		return this.parserImpl.getDocument();
	}

	/**
	 * Parse a file to a DOM Document.
	 *
	 * @param aFile
	 *           XML file to parse to a DOM Document
	 *
	 * @return The DOM Document created after parsing the file
	 *
	 * @throws java.io.IOException
	 *            Error opening XML file
	 * @throws SAXException
	 *            Error parsing XML file
	 */
	public Document parse(File aFile) throws org.xml.sax.SAXException, java.io.IOException {

		FileInputStream fileStream = new FileInputStream(aFile);

		return this.parse(fileStream);
	}

	/**
	 * Sets the ErrorHandler this DocumentBuilder instance.
	 *
	 * @param aErrorHandler
	 *           The ErrorHandler instance to use
	 */
	public void setErrorHandler(ErrorHandler aErrorHandler) {
		this.parserImpl.setErrorHandler(aErrorHandler);
	}

	/**
	 * Returns true if this parser is setup to validate XML instances, false
	 * otherwise
	 *
	 * @return Returns true if this parser is setup to validate XML instances,
	 *         false otherwise
	 *
	 * @throws IllegalStateException
	 *            Error setting the validation feature
	 */
	public boolean isValidating() {
		try {
			return this.parserImpl.getFeature(Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE);
		}
		catch (SAXException x) {
			throw new IllegalStateException(x.getMessage());
		}
	}

	/**
	 * Gets the symbol table size
	 *
	 * @return the symbol table size
	 */
	public int getSymbolTableSize() {
		return this.symbolTableSize;
	}

	/**
	 * Sets the symbol table size. If set to 0 then the symbol table size will
	 * use the default provided with the ParserConfiguration.
	 *
	 * @param aSymbolTableSize
	 *           The new value of the symbol table size
	 *
	 * @throws EPPParserException
	 *            Error setting the symbol table size
	 */
	public synchronized void setSymbolTableSize(int aSymbolTableSize) throws EPPParserException {
		cat.debug("setSymbolTableSize(int symbolTableSize) enter");
		this.symbolTableSize = aSymbolTableSize;

		if (aSymbolTableSize == 0) {
			this.parserImpl = new DOMParser(new XMLGrammarCachingConfiguration());
		}
		else {
			SymbolTable symtable = new SymbolTable(this.symbolTableSize);

			try {
				this.parserImpl.setProperty(SYMBOL_TABLE, symtable);
			}
			catch (SAXException e) {
				cat.error("Couldn't reset symbol table to size " + this.symbolTableSize);
				throw new EPPParserException(e);
			}
		}

		cat.debug("setSymbolTableSize(int symbolTableSize) exit");
	}

	/**
	 * Set the state of any feature in a SAX2 parser. The parser might not
	 * recognize the feature, and if it does recognize it, it might not be able
	 * to fulfill the request.
	 *
	 * @param aFeatureId
	 *           The unique identifier (URI) of the feature.
	 * @param aBoolean
	 *           The requested state of the feature (true or false).
	 *
	 * @exception SAXNotSupportedException
	 *               If the requested feature is known, but the requested state
	 *               is not supported.
	 * @exception SAXNotRecognizedException
	 *               If the requested feature is not known.
	 */
	public void setFeature(String aFeatureId, boolean aBoolean)
	      throws SAXNotSupportedException, SAXNotRecognizedException {
		this.parserImpl.setFeature(aFeatureId, aBoolean);
	}

	/**
	 * Set the state of any property in a SAX2 parser. The parser might not
	 * recognize the feature, and if it does recognize it, it might not be able
	 * to fulfill the request.
	 *
	 * @param aProperty
	 *           The unique identifier (URI) of the feature.
	 * @param aObject
	 *           The requested state of the feature (true or false).
	 *
	 * @exception SAXNotSupportedException
	 *               If the requested feature is known, but the requested state
	 *               is not supported.
	 * @exception SAXNotRecognizedException
	 *               If the requested feature is not known.
	 */
	public void setProperty(String aProperty, Object aObject)
	      throws SAXNotSupportedException, SAXNotRecognizedException {
		this.parserImpl.setProperty(aProperty, aObject);
	}

	/**
	 * Query the state of a feature. Query the current state of any feature in a
	 * SAX2 parser. The parser might not recognize the feature.
	 *
	 * @param aFeatureId
	 *           The unique identifier (URI) of the feature being set.
	 *
	 * @return The current state of the feature.
	 *
	 * @exception SAXNotSupportedException
	 *               If the requested feature is known but not supported.
	 * @exception SAXNotRecognizedException
	 *               If the requested feature is not known.
	 */
	public boolean getFeature(String aFeatureId) throws SAXNotSupportedException, SAXNotRecognizedException {
		return this.parserImpl.getFeature(aFeatureId);
	}

	/**
	 * Query the value of a property. Return the current value of a property in a
	 * SAX2 parser. The parser might not recognize the property.
	 *
	 * @param aPropertyId
	 *           The unique identifier (URI) of the property being set.
	 *
	 * @return The current value of the property.
	 *
	 * @exception SAXNotSupportedException
	 *               If the requested property is known but not supported.
	 * @exception SAXNotRecognizedException
	 *               If the requested property is not known.
	 */
	public Object getProperty(String aPropertyId) throws SAXNotSupportedException, SAXNotRecognizedException {
		return this.parserImpl.getProperty(aPropertyId);
	}
}
