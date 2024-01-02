/*
 * Copyright (c) 2007, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

/*
 * $Id$
 */

package com.sun.ts.tests.jaxws.mapping.j2wmapping.rpc.literal.withannotations;

import java.lang.System.Logger.Level;
import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.common.XMLUtils;
import com.sun.ts.tests.jaxws.sharedclients.DescriptionClient;
import com.sun.ts.tests.jaxws.wsi.constants.DescriptionConstants;
import com.sun.ts.tests.jaxws.wsi.constants.SOAPConstants;
import com.sun.ts.tests.jaxws.wsi.constants.SchemaConstants;
import com.sun.ts.tests.jaxws.wsi.j2w.NamespaceAttributeVerifier;
import com.sun.ts.tests.jaxws.wsi.utils.DescriptionUtils;

public class Client implements DescriptionConstants, SOAPConstants, SchemaConstants {

			private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	/*
	 * The client.
	 */
	private DescriptionClient client;

	private DescriptionClient client2;

	private DescriptionClient client3;

	private DescriptionClient client4;

	private DescriptionClient client5;

	private DescriptionClient client6;

	/*
	 * The document.
	 */
	private Document document;

	private static final int PARAM_IN = 0;

	private static final int PARAM_OUT = 1;

	private static final int PARAM_INOUT = 2;

	/**
	 * The generated WSDL file.
	 */
	private static final String WSDLLOC_URL = "j2wrlwithannotations.wsdlloc.1";

	private static final String WSDLLOC_URL2 = "j2wrlwithannotations.wsdlloc.2";

	private static final String WSDLLOC_URL3 = "j2wrlwithannotations.wsdlloc.3";

	private static final String WSDLLOC_URL4 = "j2wrlwithannotations.wsdlloc.4";

	private static final String WSDLLOC_URL5 = "j2wrlwithannotations.wsdlloc.5";

	private static final String WSDLLOC_URL6 = "j2wrlwithannotations.wsdlloc.6";

	private static final String TSHOME = "ts.home";

	private String baseURL, wsdlFile, wsdlFile2, wsdlFile3, wsdlFile4, wsdlFile5, wsdlFile6;

	private String tshome = "/tmp";

	private String wsdlFileUrl = null, wsdlFileUrl2 = null, wsdlFileUrl3 = null, wsdlFileUrl4 = null,
			wsdlFileUrl5 = null, wsdlFileUrl6 = null;

	private static final String EXPECTED_TARGETNAMESPACE = "http://rpclitservice.org/wsdl";

	private static final String EXPECTED_PORTTYPE_NAME = "MYJ2WRLSharedEndpoint";

	private static final String EXPECTED_PORT_NAME = "J2WRLSharedEndpointPort";

	private static final String EXPECTED_SERVICE_NAME = "J2WRLSharedService";

	private static final String EXPECTED_ADDRESSING_PORTTYPE_NAME = "AddressingEndpoint";

	private static final String ONEWAY_METHOD = "oneWayOperation";

	private static final String ONEWAY_METHOD_PARAMS = "oneWayOperationWithParams";

	private static final String TWOWAY_METHOD_WITH_FAULTS = "operationWithHeaderAndHeaderFaultAndFault";

	private static final String Exception = "MyFault";

	private static final String HEADERFAULT = "ConfigHeaderFault";

	private static final String PARAMETER_RESULT_NAMING_METHOD = "stringOperation";

	private static final String INPUT_PART_NAME = "ivalue0";

	private static final String OUTPUT_PART_NAME = "rvalue";

	private static final String THREE_INPUT_PART_METHOD = "oneTwoThree";

	private static final int THREE_INPUT_PART_COUNT = 3;

	private static final String[] EXPECTED_OPERATION_NAMES = { "arrayOperationFromClient", "getBean", "arrayOperation",
			"stringOperation", "oneTwoThree", "holderMethodDefault", "holderMethodInOut", "holderMethodOut",
			"helloWorld", "helloWorld2", "oneWayOperation", "oneWayOperationWithParams",
			"operationWithHeaderAndHeaderFaultAndFault", "hello", "bye", };

	private static final int[][] EXPECTED_PART_COUNTS = { { 1, 1, 0 }, // arrayOperationFromClient
			{ 0, 1, 0 }, // getBean
			{ 0, 1, 0 }, // arrayOperation
			{ 1, 1, 0 }, // stringOperation
			{ 3, 1, 0 }, // oneTwoThree
			{ 1, 2, 0 }, // holderMethodDefault
			{ 1, 2, 0 }, // holderMethodInOut
			{ 0, 2, 0 }, // holderMethodOut
			{ 0, 1, 0 }, // helloWorld
			{ 1, 1, 0 }, // helloWorld2
			{ 0, 0, 0 }, // oneWayOperation
			{ 3, 0, 0 }, // oneWayOperationWithParams
			{ 1, 0, 3 }, // operationWithHeaderAndHeaderFaultAndFault
			{ 1, 1, 0 }, // hello
			{ 1, 1, 0 }, // bye
	};

	private static final String[][] EXPECTED_TYPE_ELEMENT_MAPPINGS = { { "stringArray", "string", "", "", "" }, // arrayOperationFromClient
			{ "", "j2WRLSharedBean", "", "", "" }, // getBean
			{ "", "stringArray", "", "", "" }, // arrayOperation
			{ "string", "string", "", "", "" }, // stringOperation
			{ "int", "long", "double", "string", "" }, // oneTwoThree
			{ "string", "string", "string", "", "" }, // holderMethodDefault
			{ "string", "string", "string", "", "" }, // holderMethodInOut
			{ "string", "string", "", "", "" }, // holderMethodOut
			{ "", "string", "", "", "" }, // helloWorld
			{ "string", "string", "", "", "" }, // helloWorld2
			{ "", "", "", "", "" }, // oneWayOperation
			{ "int", "long", "double", "", "" }, // oneWayOperationWithParams
			{ "ConfigHeader", "ConfigHeaderFault", "MyFault", "MyOtherFault", "", "" }, // operationWithHeaderAndHeaderFaultAndFault
			{ "string", "string", "", "", "" }, // hello
			{ "string", "string", "", "", "" }, // bye
	};

	private String testName = null;

	private boolean debug = false;

	private Hashtable ht2 = null;

	// private methods here
	private boolean VerifyTypeElementMappings(Element[] iparts, Element[] oparts, Element[] fparts, String opname)
			throws Exception {
		boolean ok = true;

		if (!PartCountsOk(iparts, oparts, fparts, opname))
			ok = false;
		if (!TypesElementsOk(iparts, oparts, fparts, opname))
			ok = false;

		return ok;
	}

	private boolean PartCountsOk(Element[] iparts, Element[] oparts, Element[] fparts, String opname) {
		boolean ok = true;
		int index, icnt, ocnt, fcnt;

		logger.log(Level.INFO, "Verify part counts for operation: " + opname);
		index = GetIndex(opname);
		if (index == -1) {
			TestUtil.logErr("Unexpected operation name: " + opname);
			ok = false;
		} else {
			icnt = EXPECTED_PART_COUNTS[index][0];
			ocnt = EXPECTED_PART_COUNTS[index][1];
			fcnt = EXPECTED_PART_COUNTS[index][2];
			if ((iparts == null && icnt != 0) || (iparts != null && iparts.length != icnt)) {
				TestUtil.logErr("Incorrect number of input parts");
				ok = false;
			}
			if ((oparts == null && ocnt != 0) || (oparts != null && oparts.length != ocnt)) {
				TestUtil.logErr("Incorrect number of output parts");
				ok = false;
			}
			if ((fparts == null && fcnt != 0) || (fparts != null && fparts.length != fcnt)) {
				TestUtil.logErr("Incorrect number of Exception parts");
				ok = false;
			}
		}
		return ok;
	}

	private boolean TypesElementsOk(Element[] iparts, Element[] oparts, Element[] fparts, String opname) {
		boolean ok = true;
		int index;

		logger.log(Level.INFO, "Verify types for operation: " + opname);
		index = GetIndex(opname);
		if (index == -1) {
			TestUtil.logErr("Unexpected operation name: " + opname);
			ok = false;
		} else {
			ArrayList alist1 = new ArrayList();
			ArrayList alist2 = new ArrayList();
			String expectedtypes[] = null;
			String types[] = null;
			String str = null;
			for (int i = 0; i < 5; i++) {
				if (!EXPECTED_TYPE_ELEMENT_MAPPINGS[index][i].equals(""))
					alist1.add(EXPECTED_TYPE_ELEMENT_MAPPINGS[index][i]);
			}
			if (alist1.size() > 0)
				expectedtypes = (String[]) alist1.toArray(new String[alist1.size()]);
			if (iparts != null) {
				for (int i = 0; i < iparts.length; i++) {
					if (opname.equals(EXPECTED_OPERATION_NAMES[12]))
						str = iparts[i].getAttribute(XSD_ELEMENT_ATTR);
					else
						str = iparts[i].getAttribute(XSD_TYPE_ATTR);
					logger.log(Level.INFO, "type=" + str);
					alist2.add(str.substring(str.indexOf(":") + 1));
				}
			}
			if (oparts != null) {
				for (int i = 0; i < oparts.length; i++) {
					if (opname.equals(EXPECTED_OPERATION_NAMES[12]))
						str = oparts[i].getAttribute(XSD_ELEMENT_ATTR);
					else
						str = oparts[i].getAttribute(XSD_TYPE_ATTR);
					logger.log(Level.INFO, "type=" + str);
					alist2.add(str.substring(str.indexOf(":") + 1));
				}
			}
			if (fparts != null) {
				for (int i = 0; i < fparts.length; i++) {
					str = fparts[i].getAttribute(XSD_ELEMENT_ATTR);
					alist2.add(str.substring(str.indexOf(":") + 1));
					logger.log(Level.INFO, "type=" + str);
				}
			}
			if (alist2.size() > 0)
				types = (String[]) alist2.toArray(new String[alist2.size()]);
			if (!CompareTypesElementsOk(expectedtypes, types, opname))
				ok = false;
		}

		return ok;
	}

	private int GetIndex(String opname) {
		for (int i = 0; i < EXPECTED_OPERATION_NAMES.length; i++) {
			if (opname.equals(EXPECTED_OPERATION_NAMES[i]))
				return i;
		}
		return -1;
	}

	private boolean CompareTypesElementsOk(String[] expectedtypes, String[] types, String opname) {
		boolean ok = true;
		boolean found = false;

		if (expectedtypes == null && types == null) {
			;
		} else if (expectedtypes == null && types != null) {
			TestUtil.logErr("Incorrect number of types: got " + types.length + ", expected none");
			ok = false;
		} else if (expectedtypes != null && types == null) {
			TestUtil.logErr("Incorrect number of types: got none" + ", expected " + expectedtypes.length);
			ok = false;
		} else if (expectedtypes != null && types.length != expectedtypes.length) {
			TestUtil.logErr("Incorrect number of types: got " + types.length + ", expected " + expectedtypes.length);
			ok = false;
		} else {
			for (int i = 0; i < types.length; i++) {
				for (int j = 0; j < types.length; j++) {
					if (types[i].equals(expectedtypes[j]))
						found = true;
				}
				if (!found)
					ok = false;
				else
					found = false;
			}
			StringBuilder got = new StringBuilder("[ ");
			StringBuilder exp = new StringBuilder("[ ");
			for (int i = 0; i < types.length; i++) {
				got.append(types[i]).append(" ");
				exp.append(expectedtypes[i]).append(" ");
			}
			got.append("]");
			exp.append("]");
			if (!ok)
				TestUtil.logErr("Incorrect types: got " + got + ", expected " + exp);
			else
				logger.log(Level.INFO, "Types are correct: " + got);
		}
		return ok;
	}

	private boolean findName(String[] names, String name) {
		for (int i = 0; i < names.length; i++) {
			if (names[i].equals(name))
				return true;
		}
		return false;
	}

	private boolean verifyClassification(Document document, Element operation, String partName, int mode) {

		// Get input message name
		String imessageName = DescriptionUtils.getInputMessageName(operation);
		// Get part names for message name
		String[] ipartNames = DescriptionUtils.getPartNames(document, imessageName);

		// If partName is not known take it as first part name
		if (partName.equals(""))
			partName = ipartNames[0];

		// Get output message name
		String omessageName = DescriptionUtils.getOutputMessageName(operation);
		// Get part names for message name
		String[] opartNames = DescriptionUtils.getPartNames(document, omessageName);
		if (mode == PARAM_INOUT) {
			TestUtil.logMsg("Verify part " + partName + " is in both input and output");
			if (findName(ipartNames, partName) && findName(opartNames, partName))
				logger.log(Level.INFO, "verify passed:  partname in both input and output message");
			else {
				TestUtil.logErr("verify failed: partname not in both input and output message");
				return false;
			}
		} else if (mode == PARAM_OUT) {
			logger.log(Level.INFO, "Verify part " + partName + " is in output only");
			if (!findName(ipartNames, partName) && findName(opartNames, partName))
				logger.log(Level.INFO, "verify passed:  partname in output message");
			else {
				TestUtil.logErr("verify failed: partname not in output message");
				return false;
			}
		}
		return true;

	}

	private boolean ProcessWsdlDocument(Document document) throws Exception {
		boolean pass = true;

		// Process wsdl <import> elements (imported wsdl's)
		Document[] wsdlDocs = DescriptionUtils.getWsdlDocuments(document, tshome + baseURL);
		if (wsdlDocs != null) {
			for (int i = 0; i < wsdlDocs.length; i++) {
				logger.log(Level.INFO,
						"Process imported wsdl file: " + wsdlDocs[i].getDocumentElement().getAttribute("WsdlFile"));
				wsdlDocs[i].getDocumentElement().removeAttribute("WsdlFile");
				pass = ProcessWsdlDocument(wsdlDocs[i]);
			}
		}

		// Process wsdl <types> element (If none then we are done)
		Element types = DescriptionUtils.getTypes(document);
		if (types == null)
			return pass;

		// Verify namespaceURI on wsdl <schema> elements if any
		Element[] schemas = DescriptionUtils.getChildElements(types, null, WSDL_SCHEMA_LOCAL_NAME);
		logger.log(Level.INFO, "Verify namespaceURI on wsdl <schema> elements");
		if (schemas != null) {
			for (int i = 0; i < schemas.length; i++) {
				String namespaceURI = schemas[i].getNamespaceURI();
				if (!namespaceURI.equals(XSD_NAMESPACE_URI)) {
					TestUtil.logErr("Encountered 'schema' element with invalid namespaceURI '" + namespaceURI);
					pass = false;
				}
			}
		}

		// Verify schema element types defined in wsdl <types> element if any
		NodeList list = types.getElementsByTagNameNS(XSD_NAMESPACE_URI, XSD_ELEMENT_LOCAL_NAME);
		TestUtil.logMsg("Verify schema element types defined in wsdl <types> element");
		if (list.getLength() != 0) {
			if (!VerifyElementTypes(list))
				pass = false;
		}

		// Process schema <import> elements (imported schema's)
		Document[] schemaDocs = DescriptionUtils.getSchemaDocuments(document, tshome + baseURL);
		if (schemaDocs != null) {
			for (int i = 0; i < schemaDocs.length; i++) {
				logger.log(Level.INFO, "Process imported schema file: "
						+ schemaDocs[i].getDocumentElement().getAttribute("SchemaFile"));
				schemaDocs[i].getDocumentElement().removeAttribute("SchemaFile");
				pass = ProcessSchemaDocument(schemaDocs[i]);
			}
		}

		return pass;
	}

	private boolean ProcessSchemaDocument(Document document) throws Exception {
		boolean pass = true;

		// Process schema <import> elements (imported schema's)
		Document[] schemaDocs = DescriptionUtils.getSchemaDocuments(document, tshome + baseURL);
		if (schemaDocs != null) {
			for (int i = 0; i < schemaDocs.length; i++) {
				logger.log(Level.INFO, "Process imported schema file: "
						+ schemaDocs[i].getDocumentElement().getAttribute("SchemaFile"));
				schemaDocs[i].getDocumentElement().removeAttribute("SchemaFile");
				pass = ProcessSchemaDocument(schemaDocs[i]);
			}
		}

		// Verify schema element types defined in imported schema if any
		logger.log(Level.INFO, "Verify schema element types defined in imported schema");
		ht2 = new Hashtable();
		String[] s = DescriptionUtils.getAllPrefixAndNamespace(document);
		for (int i = 0; i < s.length; i++) {
			String prefix = s[i].substring(0, s[i].indexOf(":"));
			String namespace = s[i].substring(s[i].indexOf(":") + 1, s[i].length());
			ht2.put(prefix, namespace);
		}
		NodeList list = document.getDocumentElement().getElementsByTagNameNS(XSD_NAMESPACE_URI, XSD_ELEMENT_LOCAL_NAME);
		if (!VerifyElementTypes(list))
			pass = false;

		return pass;
	}

	private boolean VerifyElementTypes(NodeList list) {
		boolean pass = true;
		for (int j = 0; j < list.getLength(); j++) {
			boolean ok = false;
			Element element = (Element) list.item(j);
			String type = element.getAttribute(XSD_TYPE_ATTR);
			String name = element.getAttribute(XSD_NAME_ATTR);
			String prefix = element.getPrefix();
			String uri = element.getNamespaceURI();
			Hashtable ht = new Hashtable();
			ht.put(prefix, uri);
			if (type.contains(":")) {
				prefix = type.substring(0, type.indexOf(":"));
				Attr attributes[] = DescriptionUtils.getElementAttributes(element);
				for (int k = 0; k < attributes.length; k++) {
					String aname = attributes[k].getName();
					String avalue = attributes[k].getValue();
					if (aname.startsWith("xmlns:")) {
						String aprefix = aname.substring(6, aname.length());
						ht.put(aprefix, avalue);
						if (prefix.equals(aprefix))
							uri = avalue;
					}
				}
			}
			// Verify element type and prefix has associated (prefix,namespace) in
			// hashtable
			logger.log(Level.INFO, "Verify 'element' name=" + name + " type=" + type + " prefix=" + prefix);
			// Check hashtable prefix's of current element
			Enumeration e = ht.keys();
			while (e.hasMoreElements()) {
				String cprefix = (String) e.nextElement();
				if (prefix.equals(cprefix)) {
					ok = true;
					break;
				}
			}
			// Check hashtable prefix's of document root element
			if (!ok) {
				e = ht2.keys();
				while (e.hasMoreElements()) {
					String cprefix = (String) e.nextElement();
					if (prefix.equals(cprefix)) {
						ok = true;
						break;
					}
				}
			}
			if (!ok) {
				TestUtil.logErr("Encountered 'element' " + name + " with invalid prefix " + prefix);
				pass = false;
			}
		}
		return pass;
	}

	/*
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * 
	 * @class.setup_props: ts.home;
	 */
	@BeforeEach
	public void setup() throws Exception {
		try {
			wsdlFile = JAXWS_Util.getURLFromProp(WSDLLOC_URL);
		} catch (Exception e) {
			throw new Exception("Failed getting wsdl prop", e);
		}
		baseURL = wsdlFile.substring(0, wsdlFile.lastIndexOf("/") + 1);
		wsdlFileUrl = "file:" + tshome + wsdlFile;
		logger.log(Level.INFO, "wsdlFileUrl=" + wsdlFileUrl);
		client = new DescriptionClient();
		client.setURL(wsdlFileUrl);
		try {
			wsdlFile2 = JAXWS_Util.getURLFromProp(WSDLLOC_URL2);
		} catch (Exception e) {
			throw new Exception("Failed getting wsdl prop", e);
		}
		baseURL = wsdlFile2.substring(0, wsdlFile2.lastIndexOf("/") + 1);
		wsdlFileUrl2 = "file:" + tshome + wsdlFile2;
		logger.log(Level.INFO, "wsdlFileUrl2=" + wsdlFileUrl2);
		client2 = new DescriptionClient();
		client2.setURL(wsdlFileUrl2);
		try {
			wsdlFile3 = JAXWS_Util.getURLFromProp(WSDLLOC_URL3);
		} catch (Exception e) {
			throw new Exception("Failed getting wsdl prop", e);
		}
		baseURL = wsdlFile3.substring(0, wsdlFile3.lastIndexOf("/") + 1);
		wsdlFileUrl3 = "file:" + tshome + wsdlFile3;
		logger.log(Level.INFO, "wsdlFileUrl3=" + wsdlFileUrl3);
		client3 = new DescriptionClient();
		client3.setURL(wsdlFileUrl3);
		try {
			wsdlFile4 = JAXWS_Util.getURLFromProp(WSDLLOC_URL4);
		} catch (Exception e) {
			throw new Exception("Failed getting wsdl prop", e);
		}
		baseURL = wsdlFile4.substring(0, wsdlFile4.lastIndexOf("/") + 1);
		wsdlFileUrl4 = "file:" + tshome + wsdlFile4;
		logger.log(Level.INFO, "wsdlFileUrl4=" + wsdlFileUrl4);
		client4 = new DescriptionClient();
		client4.setURL(wsdlFileUrl4);
		try {
			wsdlFile5 = JAXWS_Util.getURLFromProp(WSDLLOC_URL5);
		} catch (Exception e) {
			throw new Exception("Failed getting wsdl prop", e);
		}
		baseURL = wsdlFile5.substring(0, wsdlFile5.lastIndexOf("/") + 1);
		wsdlFileUrl5 = "file:" + tshome + wsdlFile5;
		logger.log(Level.INFO, "wsdlFileUrl5=" + wsdlFileUrl5);
		client5 = new DescriptionClient();
		client5.setURL(wsdlFileUrl5);
		try {
			wsdlFile6 = JAXWS_Util.getURLFromProp(WSDLLOC_URL6);
		} catch (Exception e) {
			throw new Exception("Failed getting wsdl prop", e);
		}
		baseURL = wsdlFile6.substring(0, wsdlFile6.lastIndexOf("/") + 1);
		wsdlFileUrl6 = "file:" + tshome + wsdlFile6;
		logger.log(Level.INFO, "wsdlFileUrl6=" + wsdlFileUrl6);
		client6 = new DescriptionClient();
		client6.setURL(wsdlFileUrl6);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {

		logger.log(Level.INFO, "cleanup");
	}

	/*
	 * @testName: VerifyTargetNamespaceWithAnnotation
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3008;
	 * JAXWS:SPEC:3013; JAXWS:SPEC:3019; JAXWS:SPEC:3027; JAXWS:SPEC:3033;
	 * JAXWS:SPEC:3034; JAXWS:SPEC:3035; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify that the targetNamespace in the generated WSDL matches
	 * the targetNamespace as specified in the jakarta.jws.WebService annotation.
	 * (Java to WSDL 1.1 Mapping). Conformance requirement done: - Package name
	 * mapping
	 *
	 */
	@Test
	public void VerifyTargetNamespaceWithAnnotation() throws Exception {
		logger.log(Level.INFO, "VerifyTargetNamespaceWithAnnotation");
		boolean pass = true;

		try {
			String targetNamespace = DescriptionUtils.getTargetNamespaceAttr(client.getDocument());
			TestUtil.logMsg("Verify that targetNamespace is correct in generated WSDL");
			if (!targetNamespace.equals(EXPECTED_TARGETNAMESPACE)) {
				TestUtil.logErr("TargetNamespace is incorrect, expected: " + EXPECTED_TARGETNAMESPACE + ", got: "
						+ targetNamespace);
				pass = false;
			} else
				logger.log(Level.INFO, "TargetNamespace is correct: " + targetNamespace);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception:" + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("VerifyTargetNamespaceWithAnnotation failed");
	}

	/*
	 * @testName: VerifySOAPElementNamespaceUseAttributeWithAnnotation
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3008;
	 * JAXWS:SPEC:3013; JAXWS:SPEC:3019; JAXWS:SPEC:3027; JAXWS:SPEC:3033;
	 * JAXWS:SPEC:3034; JAXWS:SPEC:3035; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify the namespace and use attributes for all SOAP Elements
	 * in the generated WSDL (Java to WSDL 1.1 Mapping and BP1.1). Conformance
	 * requirement done: - verify use attribute of literal for all SOAP elements -
	 * verify namespace attribute on all soap:body elements
	 *
	 *
	 */
	@Test
	public void VerifySOAPElementNamespaceUseAttributeWithAnnotation() throws Exception {
		logger.log(Level.INFO, "VerifySOAPElementNamespaceUseAttributeWithAnnotation");
		boolean pass = true;

		Document document = client.getDocument();
		logger.log(Level.INFO, "Verify that namespace and use attribute does exist on soap:body elements");
		NamespaceAttributeVerifier verifier = new NamespaceAttributeVerifier(document, 2717);
		verifier.verify();
		logger.log(Level.INFO, "Verify that namespace attribute does not exist for soap:fault, "
				+ "soap:header, soap:headerfault elements");
		verifier = new NamespaceAttributeVerifier(document, 2726);
		verifier.verify();
		if (!pass)
			throw new Exception("VerifySOAPElementNamespaceUseAttributeWithAnnotation failed");
		else
			logger.log(Level.INFO, "Verification passed");
	}

	/*
	 * @testName: VerifySOAPBindingTransportStyleAttributeWithAnnotation
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3008;
	 * JAXWS:SPEC:3013; JAXWS:SPEC:3019; JAXWS:SPEC:3027; JAXWS:SPEC:3033;
	 * JAXWS:SPEC:3034; JAXWS:SPEC:3035; JAXWS:SPEC:3041; JAXWS:SPEC:3042;
	 * JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify the soap:binding transport and style attributes in the
	 * generated WSDL. (Java to WSDL 1.1 Mapping and BP1.1). Conformance requirement
	 * done: - SOAP binding support - SOAP binding style required - SOAP binding
	 * transport required
	 *
	 *
	 */
	@Test
	public void VerifySOAPBindingTransportStyleAttributeWithAnnotation() throws Exception {
		logger.log(Level.INFO, "VerifySOAPBindingTransportStyleAttributeWithAnnotation");
		boolean pass = true;

		Document document = client.getDocument();
		logger.log(Level.INFO, "Verify soap:binding transport and style attribute");
		Element[] bindings = DescriptionUtils.getBindings(document);
		for (int i = 0; i < bindings.length; i++) {
			Element soapBinding = DescriptionUtils.getChildElement(bindings[i], SOAP_NAMESPACE_URI,
					SOAP_BINDING_LOCAL_NAME);
			if (soapBinding == null) {
				TestUtil.logErr("soap:binding is null unexpected");
				pass = false;
			} else {
				String style = soapBinding.getAttribute(SOAP_STYLE_ATTR);
				if (!style.equals(SOAP_RPC)) {
					TestUtil.logErr(
							"soap:binding style attribute incorrect, expected: " + SOAP_RPC + ", got: " + style);
					pass = false;
				} else
					logger.log(Level.INFO, "soap:binding style attribute is correct: " + style);
				String transport = soapBinding.getAttribute(SOAP_TRANSPORT_ATTR);
				if (!transport.equals(SOAP_TRANSPORT)) {
					TestUtil.logErr("soap:binding transport attribute incorrect, expected: " + SOAP_TRANSPORT
							+ ", got: " + transport);
					pass = false;
				} else
					logger.log(Level.INFO, "soap:binding transport attribute is correct: " + transport);
			}
		}
		if (!pass)
			throw new Exception("VerifySOAPBindingTransportStyleAttributeWithAnnotation failed");
		else
			logger.log(Level.INFO, "Verification passed");
	}

	/*
	 * @testName: VerifyServiceNameWithAnnotation
	 *
	 * @assertion_ids: JAXWS:SPEC:3037; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify the wsdl:service element name is correct in the
	 * generated WSDL. Conformance requirement done: - service naming
	 *
	 *
	 */
	@Test
	public void VerifyServiceNameWithAnnotation() throws Exception {
		logger.log(Level.INFO, "VerifyServiceNameWithAnnotation");
		boolean pass = true;

		logger.log(Level.INFO, "Checking for service name verification of: " + EXPECTED_SERVICE_NAME);
		pass = DescriptionUtils.isServiceNameAttr(client.getDocument(), EXPECTED_SERVICE_NAME);
		if (!pass)
			throw new Exception("VerifyServiceNameWithAnnotation failed");
		else
			logger.log(Level.INFO, "Verification passed");
	}

	/*
	 * @testName: VerifyPortTypeNameWithAnnotation
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3008;
	 * JAXWS:SPEC:3013; JAXWS:SPEC:3019; JAXWS:SPEC:3027; JAXWS:SPEC:3033;
	 * JAXWS:SPEC:3034; JAXWS:SPEC:3035; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify the wsdl:portType name is correct in the generated
	 * WSDL. Conformance requirement done: - portType naming
	 *
	 *
	 */
	@Test
	public void VerifyPortTypeNameWithAnnotation() throws Exception {
		logger.log(Level.INFO, "VerifyPortTypeNameWithAnnotation");
		boolean pass = true;

		logger.log(Level.INFO, "Checking for portType name verification of: " + EXPECTED_PORTTYPE_NAME);
		pass = DescriptionUtils.isPortTypeNameAttr(client.getDocument(), EXPECTED_PORTTYPE_NAME);
		if (!pass)
			throw new Exception("VerifyPortTypeNameWithAnnotation failed");
		else
			logger.log(Level.INFO, "Verification passed");
	}

	/*
	 * @testName: VerifyPortNameWithAnnotation
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3008;
	 * JAXWS:SPEC:3012; JAXWS:SPEC:3019; JAXWS:SPEC:3020; JAXWS:SPEC:3033;
	 * JAXWS:SPEC:3034; JAXWS:SPEC:3035; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify the wsdl:port name is correct in the generated WSDL.
	 * Conformance requirement done: - port naming
	 *
	 *
	 */
	@Test
	public void VerifyPortNameWithAnnotation() throws Exception {
		logger.log(Level.INFO, "VerifyPortNameWithAnnotation");
		boolean pass = true;

		logger.log(Level.INFO, "Checking for port name verification of: " + EXPECTED_PORT_NAME);
		pass = DescriptionUtils.isPortNameAttr(client.getDocument(), EXPECTED_PORT_NAME);
		if (!pass)
			throw new Exception("VerifyPortNameWithAnnotation failed");
		else
			logger.log(Level.INFO, "Verification passed");
	}

	/*
	 * @testName: VerifyOperationNames
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3008;
	 * JAXWS:SPEC:3010; JAXWS:SPEC:3013; JAXWS:SPEC:3019; JAXWS:SPEC:3027;
	 * JAXWS:SPEC:3033; JAXWS:SPEC:3034; JAXWS:SPEC:3035; JAXWS:SPEC:3003;
	 * JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify wsdl:portType operation names are correct in the
	 * generated WSDL. Verify all public method mapping to wsdl:operation elements.
	 * Conformance requirement done: - Inheritance flattening - Operation naming -
	 * Method name diambiguation
	 * 
	 *
	 */
	@Test
	public void VerifyOperationNames() throws Exception {
		logger.log(Level.INFO, "VerifyOperationNames");
		boolean pass = true;

		logger.log(Level.INFO, "Checking for operation names for portType: " + EXPECTED_PORTTYPE_NAME);
		String opNames[] = DescriptionUtils.getPortTypeOperationNames(client.getDocument(), EXPECTED_PORTTYPE_NAME);
		if (opNames.length == EXPECTED_OPERATION_NAMES.length)
			logger.log(Level.INFO, "operation name count is as expected: " + EXPECTED_OPERATION_NAMES.length);
		else {
			pass = false;
			TestUtil.logErr(
					"operation count expected: " + EXPECTED_OPERATION_NAMES.length + ", received: " + opNames.length);
		}
		int cnt = 0;
		for (int i = 0; i < opNames.length; i++) {
			boolean found = false;
			for (int j = 0; j < EXPECTED_OPERATION_NAMES.length; j++) {
				if (opNames[i].equals(EXPECTED_OPERATION_NAMES[j])) {
					found = true;
					cnt++;
					logger.log(Level.INFO, "Matched operation name ... " + opNames[i]);
					break;
				}
			}
			if (!found) {
				TestUtil.logErr("UnMatched operation name: " + opNames[i]);
				pass = false;
			}
		}
		if (cnt == EXPECTED_OPERATION_NAMES.length)
			logger.log(Level.INFO, "All operation names matched expected ...");

		if (!pass)
			throw new Exception("VerifyOperationNames failed");
		else
			logger.log(Level.INFO, "Verification passed");
	}

	/*
	 * @testName: VerifyOneWayAndTwoWayOperations
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3008;
	 * JAXWS:SPEC:3011; JAXWS:SPEC:3013; JAXWS:SPEC:3019; JAXWS:SPEC:3027;
	 * JAXWS:SPEC:3033; JAXWS:SPEC:3034; JAXWS:SPEC:3035; JAXWS:SPEC:3014;
	 * JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify operations are correctly identifed as OneWay or
	 * TwoWay. Conformance requirement done: - One-way mapping - Two-way mapping -
	 * Two-way mapping with Exceptions - Method and parameters where expected in
	 * both wsdl:portType and wsdl:binding
	 * 
	 *
	 */
	@Test
	public void VerifyOneWayAndTwoWayOperations() throws Exception {
		logger.log(Level.INFO, "VerifyOneWayAndTwoWayOperations");
		boolean pass = true;

		logger.log(Level.INFO, "Checking for operation names for portType: " + EXPECTED_PORTTYPE_NAME);
		Element operations[] = DescriptionUtils.getPortTypeOperationNameElements(client.getDocument(),
				EXPECTED_PORTTYPE_NAME);
		for (int i = 0; i < operations.length; i++) {
			String name = operations[i].getAttribute(WSDL_NAME_ATTR);
			Element e = DescriptionUtils.getBindingOperationNameElement(client.getDocument(), name);
			if (name.equals(ONEWAY_METHOD) || name.equals(ONEWAY_METHOD_PARAMS)) {
				TestUtil.logMsg("Checking for one way operation for operation: " + name);
				logger.log(Level.INFO, "verify in portType operation ...");
				if (DescriptionUtils.isOneWay(operations[i]))
					logger.log(Level.INFO, "is One way");
				else {
					TestUtil.logErr("is not One way");
					pass = false;
				}
				logger.log(Level.INFO, "verify in binding operation ...");
				if (DescriptionUtils.isOneWay(e))
					logger.log(Level.INFO, "is One way");
				else {
					TestUtil.logErr("is not One way");
					pass = false;
				}
			} else {
				TestUtil.logMsg("Checking for two way operation for operation: " + name);
				logger.log(Level.INFO, "verify in portType operation ...");
				if (DescriptionUtils.isTwoWay(operations[i]))
					logger.log(Level.INFO, "is Two way");
				else {
					TestUtil.logErr("is not Two way");
					pass = false;
				}
				logger.log(Level.INFO, "verify in binding operation ...");
				if (DescriptionUtils.isTwoWay(e))
					logger.log(Level.INFO, "is Two way");
				else {
					TestUtil.logErr("is not Two way");
					pass = false;
				}
				if (name.equals(TWOWAY_METHOD_WITH_FAULTS)) {
					logger.log(Level.INFO, "Checking for Exceptions for operation: " + name);
					logger.log(Level.INFO, "Expected Exceptions are: " + Exception + "," + HEADERFAULT);
					logger.log(Level.INFO, "verify in portType operation ...");
					if (DescriptionUtils.hasFault(operations[i], Exception)
							&& DescriptionUtils.hasFault(operations[i], HEADERFAULT))
						logger.log(Level.INFO, "fault match");
					else {
						TestUtil.logErr(
								"fault mismatch, did not get expected Exceptions: " + Exception + "," + HEADERFAULT);
						pass = false;
					}
					logger.log(Level.INFO, "verify in binding operation ...");
					if (DescriptionUtils.hasFault(e, Exception) && DescriptionUtils.hasFault(e, HEADERFAULT))
						logger.log(Level.INFO, "fault match");
					else {
						TestUtil.logErr(
								"fault mismatch, did not get expected Exceptions: " + Exception + "," + HEADERFAULT);
						pass = false;
					}
				}
			}
		}
		if (!pass)
			throw new Exception("VerifyOneWayAndTwoWayOperations failed");
		else
			logger.log(Level.INFO, "Verification passed");
	}

	/*
	 * @testName: VerifyParameterAndResultNaming
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3008;
	 * JAXWS:SPEC:3013; JAXWS:SPEC:3019; JAXWS:SPEC:3027; JAXWS:SPEC:3033;
	 * JAXWS:SPEC:3034; JAXWS:SPEC:3035; JAXWS:SPEC:3017; JAXWS:SPEC:3018;
	 * JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify use of annotations to name parameters and results.
	 * Conformance requirement done: - Parameter naming - Result naming
	 * 
	 *
	 */
	@Test
	public void VerifyParameterAndResultNaming() throws Exception {
		logger.log(Level.INFO, "VerifyParameterAndResultNaming");
		boolean pass = true;

		TestUtil.logMsg("Checking for operation name " + PARAMETER_RESULT_NAMING_METHOD + " in portType "
				+ EXPECTED_PORTTYPE_NAME);
		Element operation = DescriptionUtils.getPortTypeOperationNameElement(client.getDocument(),
				EXPECTED_PORTTYPE_NAME, PARAMETER_RESULT_NAMING_METHOD);
		Element input = DescriptionUtils.getChildElement(operation, WSDL_NAMESPACE_URI, WSDL_INPUT_LOCAL_NAME);
		Element output = DescriptionUtils.getChildElement(operation, WSDL_NAMESPACE_URI, WSDL_OUTPUT_LOCAL_NAME);
		String imsg = input.getAttribute(WSDL_MESSAGE_ATTR);
		String omsg = output.getAttribute(WSDL_MESSAGE_ATTR);
		String imessage = imsg.substring(imsg.indexOf(":") + 1);
		String omessage = omsg.substring(omsg.indexOf(":") + 1);
		logger.log(Level.INFO, "input message name is " + imessage);
		logger.log(Level.INFO, "output message name is " + omessage);
		String ipart = DescriptionUtils.getPartName(client.getDocument(), imessage);
		String opart = DescriptionUtils.getPartName(client.getDocument(), omessage);

		// Check that both annotated input and output arguments are correctly named
		// in wsdl.
		if (ipart.equals(INPUT_PART_NAME))
			logger.log(Level.INFO, "Parameter naming passed, expected: " + INPUT_PART_NAME + ", received: " + ipart);
		else {
			TestUtil.logErr("Parameter naming failed, expected: " + INPUT_PART_NAME + ", received: " + ipart);
			pass = false;
		}

		if (opart.equals(OUTPUT_PART_NAME))
			logger.log(Level.INFO, "Result naming passed, expected: " + OUTPUT_PART_NAME + ", received: " + opart);
		else {
			TestUtil.logErr("Result naming failed, expected: " + OUTPUT_PART_NAME + ", received: " + opart);
			pass = false;
		}

		if (!pass)
			throw new Exception("VerifyParameterAndResultNaming failed");
		else
			logger.log(Level.INFO, "Verification passed");
	}

	/*
	 * @testName: VerifyWsdlMessagePartAndAttrStyle
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3008;
	 * JAXWS:SPEC:3013; JAXWS:SPEC:3019; JAXWS:SPEC:3027; JAXWS:SPEC:3033;
	 * JAXWS:SPEC:3034; JAXWS:SPEC:3035; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify each message has zero or more wsdl:part child elements
	 * that refer via a type attribute to a named type declaration. Conformance
	 * requirement done: - Zero or more wsdl:part child elements with type
	 * attribute, one per method parameter and for a non-void return value.
	 *
	 */
	@Test
	public void VerifyWsdlMessagePartAndAttrStyle() throws Exception {
		logger.log(Level.INFO, "VerifyWsdlMessagePartAndAttrStyle");
		boolean pass = true;

		TestUtil.logMsg("Getting all input and output messages for portType operations" + " in portType "
				+ EXPECTED_PORTTYPE_NAME);
		String imessages[] = DescriptionUtils.getInputMessageNames(client.getDocument(), EXPECTED_PORTTYPE_NAME);
		Arrays.sort(imessages);
		for (int i = 0; i < imessages.length; i++)
			logger.log(Level.INFO, "input message: " + imessages[i]);
		String omessages[] = DescriptionUtils.getOutputMessageNames(client.getDocument(), EXPECTED_PORTTYPE_NAME);
		Arrays.sort(omessages);
		for (int i = 0; i < omessages.length; i++)
			logger.log(Level.INFO, "output message: " + omessages[i]);

		// Header parts are not checked but filtered out
		String headerParts[] = DescriptionUtils.getSoapHeaderElementsPartAttr(client.getDocument());
		for (int i = 0; i < headerParts.length; i++)
			logger.log(Level.INFO, "part=" + headerParts[i]);

		Element messages[] = DescriptionUtils.getMessages(client.getDocument());
		for (int i = 0; i < messages.length; i++) {
			String messageName = messages[i].getAttribute(WSDL_NAME_ATTR);
			logger.log(Level.INFO, "Checking message: " + messageName);
			if (Arrays.binarySearch(imessages, messageName) >= 0 || Arrays.binarySearch(omessages, messageName) >= 0) {
				Element[] parts = DescriptionUtils.getChildElements(messages[i], WSDL_NAMESPACE_URI,
						WSDL_PART_LOCAL_NAME);
				if (parts.length == 0) {
					logger.log(Level.INFO, "    no parts exist for message " + messageName);
					continue;
				} else
					logger.log(Level.INFO, "    check that type attribute exists for all its message parts ...");
				int partCnt = 0;
				for (int j = 0; j < parts.length; j++) {
					String part = parts[j].getAttribute(WSDL_NAME_ATTR);
					String element = parts[j].getAttribute(WSDL_ELEMENT_ATTR);
					String type = parts[j].getAttribute(WSDL_TYPE_ATTR);
					logger.log(Level.INFO, "    part=" + part + ", element=" + element + ", type=" + type);
					boolean isHeader = false;
					for (int k = 0; k < headerParts.length; k++) {
						if (part.equals(headerParts[k])) {
							isHeader = true;
							logger.log(Level.INFO, "    skipping, this part is a header");
							break;
						} else
							partCnt++;
					}
					if (isHeader)
						continue; // Skip header parts
					if (type == null || type.equals("")) {
						TestUtil.logErr("    no type attribute for this part");
						pass = false;
					}
					if (element != null && !element.equals("")) {
						TestUtil.logErr("    a element attribute exists for this part");
						pass = false;
					}
				}
				logger.log(Level.INFO, "message: " + messageName + ", parts: " + partCnt);
				if (messageName.equals(THREE_INPUT_PART_METHOD)) {
					if (partCnt == THREE_INPUT_PART_COUNT)
						logger.log(Level.INFO, "Verification of multiple parts method " + THREE_INPUT_PART_METHOD
								+ " indeed has " + THREE_INPUT_PART_COUNT + " for part count");
					else {
						TestUtil.logErr("Verification of multiple parts method " + THREE_INPUT_PART_METHOD
								+ " failed, expected part count of " + THREE_INPUT_PART_COUNT
								+ ", received part count of " + partCnt);
					}
				}
			} else
				logger.log(Level.INFO, "Skipping message parts checking ...");
		}

		if (!pass)
			throw new Exception("VerifyWsdlMessagePartAndAttrStyle failed");
		else
			logger.log(Level.INFO, "Verification passed");
	}

	/*
	 * @testName: VerifySchemaNamespacesImportsElementsTypes
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3008;
	 * JAXWS:SPEC:3013; JAXWS:SPEC:3019; JAXWS:SPEC:3027; JAXWS:SPEC:3033;
	 * JAXWS:SPEC:3034; JAXWS:SPEC:3035; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify schema namespaces, schema imports, schema elements,
	 * schema types.
	 *
	 */
	@Test
	public void VerifySchemaNamespacesImportsElementsTypes() throws Exception {
		logger.log(Level.INFO, "VerifySchemaNamespacesImportsElementsTypes");
		boolean pass = true;

		pass = ProcessWsdlDocument(client.getDocument());
		if (!pass)
			throw new Exception("VerifySchemaNamespacesImportsElementsTypes failed");
		else
			logger.log(Level.INFO, "Verification passed");
	}

	/*
	 * @testName: VerifyParameterClassificationForHolders
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3008;
	 * JAXWS:SPEC:3013; JAXWS:SPEC:3016; JAXWS:SPEC:3019; JAXWS:SPEC:3027;
	 * JAXWS:SPEC:3033; JAXWS:SPEC:3034; JAXWS:SPEC:3035; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify correct classification for holder parameters
	 *
	 * Conformance Requirement: - Parameter classification
	 *
	 */
	@Test
	public void VerifyParameterClassificationForHolders() throws Exception {
		logger.log(Level.INFO, "VerifyParameterClassificationForHolders");
		boolean pass = true;

		Document document = client.getDocument();

		Element holderMethodDefault = DescriptionUtils.getPortTypeOperationNameElement(document, EXPECTED_PORTTYPE_NAME,
				"holderMethodDefault");

		Element holderMethodInOut = DescriptionUtils.getPortTypeOperationNameElement(document, EXPECTED_PORTTYPE_NAME,
				"holderMethodInOut");

		Element holderMethodOut = DescriptionUtils.getPortTypeOperationNameElement(document, EXPECTED_PORTTYPE_NAME,
				"holderMethodOut");

		pass = verifyClassification(document, holderMethodDefault, "", PARAM_INOUT)
				&& verifyClassification(document, holderMethodInOut, "varStringInOut", PARAM_INOUT)
				&& verifyClassification(document, holderMethodOut, "varStringOut", PARAM_OUT);

		if (!pass)
			throw new Exception("VerifyParameterClassificationForHolders failed");
		else
			logger.log(Level.INFO, "Verification passed");

	}

	/*
	 * @testName: VerifyInputOutputReturnAndFaultTypeElementMappings
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3008;
	 * JAXWS:SPEC:3035; JAXWS:SPEC:3013; JAXWS:SPEC:3019; JAXWS:SPEC:3027;
	 * JAXWS:SPEC:3033; JAXWS:SPEC:3034; JAXWS:SPEC:3058; JAXWS:SPEC:3028;
	 * JAXWS:SPEC:3029; JAXWS:SPEC:3030; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify that the Java types for all in, out, in/out
	 * parameter's and return value's are mapped to named XML Schema types using the
	 * mapping defined in JAXB. Conformance requirement done: - Each method
	 * parameter and return type is mapped to a valid XML schema type
	 * 
	 *
	 */
	@Test
	public void VerifyInputOutputReturnAndFaultTypeElementMappings() throws Exception {
		logger.log(Level.INFO, "VerifyInputOutputReturnAndFaultTypeElementMappings");
		boolean pass = true;

		logger.log(Level.INFO, "Get all operation elements for portType: " + EXPECTED_PORTTYPE_NAME);
		Element operations[] = DescriptionUtils.getPortTypeOperationNameElements(client.getDocument(),
				EXPECTED_PORTTYPE_NAME);
		for (int i = 0; i < operations.length; i++) {
			String opname = operations[i].getAttribute(WSDL_NAME_ATTR);
			Element input = DescriptionUtils.getChildElement(operations[i], WSDL_NAMESPACE_URI, WSDL_INPUT_LOCAL_NAME);
			Element output = DescriptionUtils.getChildElement(operations[i], WSDL_NAMESPACE_URI,
					WSDL_OUTPUT_LOCAL_NAME);
			Element fault[] = DescriptionUtils.getChildElements(operations[i], WSDL_NAMESPACE_URI,
					WSDL_FAULT_LOCAL_NAME);
			String imsg = null;
			String omsg = null;
			String fmsg[] = null;
			String imsgstr = null;
			String omsgstr = null;
			String fmsgstr[] = null;
			ArrayList alist1 = new ArrayList();
			ArrayList alist2 = new ArrayList();
			if (input != null) {
				imsg = input.getAttribute(WSDL_MESSAGE_ATTR);
				imsgstr = imsg.substring(imsg.indexOf(":") + 1);
			}
			if (output != null) {
				omsg = output.getAttribute(WSDL_MESSAGE_ATTR);
				omsgstr = omsg.substring(omsg.indexOf(":") + 1);
			}
			if (fault != null) {
				for (int k = 0; k < fault.length; k++) {
					String str1 = fault[k].getAttribute(WSDL_MESSAGE_ATTR);
					alist1.add(str1);
					String str2 = str1.substring(str1.indexOf(":") + 1);
					alist2.add(str2);
				}
				if (alist1.size() > 0)
					fmsg = (String[]) alist1.toArray(new String[alist1.size()]);
				if (alist2.size() > 0)
					fmsgstr = (String[]) alist2.toArray(new String[alist2.size()]);
			}
			logger.log(Level.INFO, "operation name is " + opname);
			logger.log(Level.INFO, "input message name is " + imsgstr);
			logger.log(Level.INFO, "output message name is " + omsgstr);
			if (fmsgstr == null)
				logger.log(Level.INFO, "fault message name is null");
			else {
				for (int k = 0; k < fmsgstr.length; k++)
					logger.log(Level.INFO, "fault message name is " + fmsgstr[k]);
			}
			Element[] iparts = null;
			Element[] oparts = null;
			Element[] fparts = null;
			if (imsgstr != null) {
				iparts = DescriptionUtils.getPartElements(client.getDocument(), imsgstr);
			}
			if (omsgstr != null) {
				oparts = DescriptionUtils.getPartElements(client.getDocument(), omsgstr);
			}
			if (fmsgstr != null) {
				Element[] parts = null;
				ArrayList alist = new ArrayList();
				for (int k = 0; k < fmsgstr.length; k++) {
					parts = DescriptionUtils.getPartElements(client.getDocument(), fmsgstr[k]);
					if (parts != null) {
						for (int l = 0; l < parts.length; l++)
							alist.add(parts[l]);
					}
				}
				if (alist.size() > 0)
					fparts = (Element[]) alist.toArray(new Element[alist.size()]);
			}
			if (!VerifyTypeElementMappings(iparts, oparts, fparts, opname))
				pass = false;
		}
		if (!pass)
			throw new Exception("VerifyInputOutputReturnAndFaultTypeElementMappings failed");
		else
			logger.log(Level.INFO, "Verification passed");
	}

	/*
	 * @testName: ActionFaultActionAndAddressingAnnotationsTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:130; JAXWS:JAVADOC:131; JAXWS:JAVADOC:132;
	 * JAXWS:JAVADOC:144; JAXWS:SPEC:3043; JAXWS:SPEC:3044; JAXWS:SPEC:3045;
	 * JAXWS:SPEC:3049; JAXWS:SPEC:3050; JAXWS:SPEC:3051; JAXWS:SPEC:3055;
	 * JAXWS:SPEC:3059;
	 *
	 * @test_Strategy: Generate classes from a wsdl and verify existence of
	 * annotations. Verify wsam:Addressing extension element is generated for
	 * Addressing(enabled=true). Verify that wsp:Optional element is not generated
	 * for Addressing(required=true). Verify the Action and ExceptionAction
	 * annotations are correct in the generated WSDL.
	 */
	@Test
	public void ActionFaultActionAndAddressingAnnotationsTest() throws Exception {
		TestUtil.logTrace("ActionFaultActionAndAddressingAnnotationsTest");
		boolean pass = true;

		if (!VerifyBindingHasAddressing()) {
			pass = false;
			TestUtil.logErr("VerifyBindingHasAddressing - FAILED");
		} else {
			logger.log(Level.INFO, "VerifyBindingHasAddressing - PASSED");
		}
		if (!VerifyActionFaultAction()) {
			pass = false;
			TestUtil.logErr("VerifyActionFaultAction - FAILED");
		} else {
			logger.log(Level.INFO, "VerifyActionFaultAction - PASSED");
		}

		if (!pass)
			throw new Exception("ActionFaultActionAndAddressingAnnotationsTest failed");
	}

	/*
	 * @testName: AddressingNotEnabledAnnotationTest
	 *
	 * @assertion_ids: JAXWS:SPEC:3048;
	 *
	 * @test_Strategy: Generate wsdl from classes and verify wsam:Addressing
	 * Extension element is not generated for Addressing(enabled=false) annotation
	 * on 3Impl.java Verify the Addressing annotation is correct in the generated
	 * WSDL.
	 */
	@Test
	public void AddressingNotEnabledAnnotationTest() throws Exception {
		TestUtil.logTrace("AddressingNotEnabledAnnotationTest");
		boolean pass = true;
		if (VerifyBindingHasNoAddressing()) {
			logger.log(Level.INFO, "VerifyBindingHasNoAddressing - PASSED");
		} else {
			pass = false;
			TestUtil.logErr("VerifyBindingHasNoAddressing - FAILED");
		}

		if (!pass) {
			throw new Exception("AddressingNotEnabledAnnotationTest failed");
		}
	}

	/*
	 * @testName: VerifyAnonymousResponsesAddressingElementTest
	 *
	 * @assertion_ids: JAXWS:SPEC:3047; JAXWS:SPEC:3059; JAXWS:JAVADOC:228;
	 * JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Generate wsdl from classes and verify wsam:AnonymousResponses
	 * element is generated for Addressing(enabled=true,required=true,responses=
	 * AddressingFeature.ANONYMOUS). Verify the Addressing annotation is correct in
	 * the generated WSDL.
	 */
	@Test
	public void VerifyAnonymousResponsesAddressingElementTest() throws Exception {
		TestUtil.logTrace("VerifyAnonymousResponsesAddressingElementTest");
		boolean pass = true;

		testName = "VerifyAnonymousResponsesAddressingElementTest";
		if (!VerifyBindingHasAnonymousResponses()) {
			pass = false;
			TestUtil.logErr("VerifyBindingHasAnonymousResponses - FAILED");
		} else {
			logger.log(Level.INFO, "VerifyBindingHasAnonymousResponses - PASSED");
		}

		if (!pass) {
			throw new Exception("VerifyAnonymousResponsesAddressingElementTest failed");
		}
	}

	/*
	 * @testName: VerifyNonAnonymousResponsesAddressingElementTest
	 *
	 * @assertion_ids: JAXWS:SPEC:3047; JAXWS:SPEC:3059; JAXWS:JAVADOC:228;
	 * JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Generate wsdl from classes and verify
	 * wsam:NonAnonymousResponses element is generated for
	 * Addressing(enabled=true,required=true,responses=
	 * AddressingFeature.NON_ANONYMOUS). Verify the Addressing annotation is correct
	 * in the generated WSDL.
	 */
	@Test
	public void VerifyNonAnonymousResponsesAddressingElementTest() throws Exception {
		TestUtil.logTrace("VerifyNonAnonymousResponsesAddressingElementTest");
		boolean pass = true;

		testName = "VerifyNonAnonymousResponsesAddressingElementTest";
		if (!VerifyBindingHasNonAnonymousResponses()) {
			pass = false;
			TestUtil.logErr("VerifyBindingHasNonAnonymousResponses - FAILED");
		} else {
			logger.log(Level.INFO, "VerifyBindingHasNonAnonymousResponses - PASSED");
		}

		if (!pass) {
			throw new Exception("VerifyNonAnonymousResponsesAddressingElementTest failed");
		}
	}

	/*
	 * @testName: VerifyAllResponsesAddressingElementsTest
	 *
	 * @assertion_ids: JAXWS:SPEC:3047; JAXWS:SPEC:3059; JAXWS:JAVADOC:228;
	 * JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Generate wsdl from classes and verify wsam:AnonymousResponses
	 * and wsam:NonAnonymousResponses elements are generated for Addressing(
	 * enabled=true,required=true,responses=AddressingFeature.ALL). Verify the
	 * Addressing annotation is correct in the generated WSDL.
	 */
	@Test
	public void VerifyAllResponsesAddressingElementsTest() throws Exception {
		TestUtil.logTrace("VerifyAllResponsesAddressingElementsTest");
		boolean pass = true;
		boolean hasAnonymousResponsesElement = false;
		boolean hasNonAnonymousResponsesElement = false;

		testName = "VerifyAllResponsesAddressingElementsTest";
		if (VerifyBindingHasAnonymousResponses())
			hasAnonymousResponsesElement = true;

		if (VerifyBindingHasNonAnonymousResponses())
			hasNonAnonymousResponsesElement = true;

		if (hasAnonymousResponsesElement && hasNonAnonymousResponsesElement)
			logger.log(Level.INFO, "VerifyAllResponsesAddressingElementsTest - PASSED");
		else if (!hasAnonymousResponsesElement && !hasNonAnonymousResponsesElement)
			logger.log(Level.INFO, "VerifyAllResponsesAddressingElementsTest - PASSED");
		else {
			TestUtil.logErr("VerifyAllResponsesAddressingElementsTest - FAILED");
			pass = false;
		}

		if (!pass) {
			throw new Exception("VerifyAllResponsesAddressingElementsTest failed");
		}
	}

	private boolean VerifyBindingHasAddressing() throws Exception {
		logger.log(Level.INFO, "VerifyBindingHasAddressing");
		boolean pass = false;
		boolean debug = true;

		Element[] bindings = DescriptionUtils.getBindings(client2.getDocument());
		logger.log(Level.INFO, "Find wsam:Addressing policys via [wsp:Policy] or [wsp:PolicyReference] tags");
		NodeList policyrefs = bindings[0].getElementsByTagNameNS(WSP_NAMESPACE_URI, WSP_POLICYREFERENCE_LOCAL_NAME);
		Element[] policys = DescriptionUtils.getChildElements(client2.getDocument(), WSP_NAMESPACE_URI,
				WSP_POLICY_LOCAL_NAME);
		Element[] bpolicys = DescriptionUtils.getChildElements(bindings[0], WSP_NAMESPACE_URI, WSP_POLICY_LOCAL_NAME);
		if (policyrefs.getLength() == 0 && policys.length == 0 && bpolicys.length == 0) {
			TestUtil.logErr("No wsam:Addressing policy element was found on binding");
			return false;
		}
		logger.log(Level.INFO, "Search for wsa:Addressing policy element via wsp:PolicyReference tag on binding");
		for (int i = 0; i < policyrefs.getLength(); i++) {
			Element element = (Element) policyrefs.item(i);
			String uri = element.getAttribute("URI");
			for (int j = 0; j < policys.length; j++) {
				NamedNodeMap map = policys[j].getAttributes();
				for (int k = 0; k < map.getLength(); k++) {
					Node node = map.item(k);
					String nodeValue = "#" + node.getNodeValue();
					if (uri.equals(nodeValue)) {
						NodeList list = policys[j].getElementsByTagNameNS(WSAM_NAMESPACE_URI,
								WSAM_ADDRESSING_LOCAL_NAME);
						Element addressing = (Element) list.item(0);
						if (addressing != null) {
							logger.log(Level.INFO, "Found wsa:Addressing policy element");
							if (debug)
								XMLUtils.xmlDumpDOMNodes(addressing);
							pass = true;
							NamedNodeMap map2 = addressing.getAttributes();
							for (int m = 0; m < map2.getLength(); m++) {
								Node node2 = map2.item(m);
								if (node2.getNodeName().equals(WSP_OPTIONAL_ATTR))
									logger.log(Level.INFO, "Found wsp:Optional attribute. It should be false.");
								if (node2.getNodeName().equals(WSP_OPTIONAL_ATTR)
										&& !node2.getNodeValue().equals("false")) {
									TestUtil.logErr("wsp:Optional attribute should not exist or should be false");
									pass = false;
									break;
								}
							}
							break;
						}
					}
				}
				if (!pass)
					break;
			}
		}
		if (!pass) {
			logger.log(Level.INFO, "Search for wsa:Addressing policy element via explicit wsp:Policy tag on binding");
			Element addressing = DescriptionUtils.getChildElement(bindings[0], WSAM_NAMESPACE_URI,
					WSAM_ADDRESSING_LOCAL_NAME);
			if (addressing == null) {
				TestUtil.logErr("No wsam:Addressing policy element was found on binding");
				pass = false;
			} else {
				logger.log(Level.INFO, "Found wsa:Addressing policy element");
				if (debug)
					XMLUtils.xmlDumpDOMNodes(addressing);
				pass = true;
				NamedNodeMap map2 = addressing.getAttributes();
				for (int m = 0; m < map2.getLength(); m++) {
					Node node2 = map2.item(m);
					if (node2.getNodeName().equals(WSP_OPTIONAL_ATTR))
						TestUtil.logMsg("Found wsp:Optional attribute. It should be false.");
					if (node2.getNodeName().equals(WSP_OPTIONAL_ATTR) && !node2.getNodeValue().equals("false")) {
						TestUtil.logErr("wsp:Optional attribute should not exist or should be false");
						pass = false;
						break;
					}
				}
			}
		}
		return pass;
	}

	private boolean VerifyBindingHasAnonymousResponses() throws Exception {
		return VerifyBindingHasAnonymousOrNonAnonymousResponses(WSAM_ANONYMOUS_RESPONSES_LOCAL_NAME);
	}

	private boolean VerifyBindingHasNonAnonymousResponses() throws Exception {
		return VerifyBindingHasAnonymousOrNonAnonymousResponses(WSAM_NONANONYMOUS_RESPONSES_LOCAL_NAME);
	}

	private boolean VerifyBindingHasAnonymousOrNonAnonymousResponses(String responseType) throws Exception {
		logger.log(Level.INFO, "VerifyBindingHasAnonymousOrNonAnonymousResponses");
		boolean pass = false;
		boolean debug = true;

		Element[] bindings;
		if (testName.equals("VerifyAnonymousResponsesAddressingElementTest"))
			bindings = DescriptionUtils.getBindings(client4.getDocument());
		else if (testName.equals("VerifyNonAnonymousResponsesAddressingElementTest"))
			bindings = DescriptionUtils.getBindings(client5.getDocument());
		else
			bindings = DescriptionUtils.getBindings(client6.getDocument());
		logger.log(Level.INFO, "Find wsam:Addressing policys via [wsp:Policy] or [wsp:PolicyReference] tags");
		NodeList policyrefs = bindings[0].getElementsByTagNameNS(WSP_NAMESPACE_URI, WSP_POLICYREFERENCE_LOCAL_NAME);
		Element[] bpolicys = DescriptionUtils.getChildElements(bindings[0], WSP_NAMESPACE_URI, WSP_POLICY_LOCAL_NAME);
		Element[] policys;
		if (testName.equals("VerifyAnonymousResponsesAddressingElementTest"))
			policys = DescriptionUtils.getChildElements(client4.getDocument(), WSP_NAMESPACE_URI,
					WSP_POLICY_LOCAL_NAME);
		else if (testName.equals("VerifyNonAnonymousResponsesAddressingElementTest"))
			policys = DescriptionUtils.getChildElements(client5.getDocument(), WSP_NAMESPACE_URI,
					WSP_POLICY_LOCAL_NAME);
		else
			policys = DescriptionUtils.getChildElements(client6.getDocument(), WSP_NAMESPACE_URI,
					WSP_POLICY_LOCAL_NAME);
		if (policyrefs.getLength() == 0 && policys.length == 0 && bpolicys.length == 0) {
			TestUtil.logErr("No wsam:Addressing policy element was found on binding");
			return false;
		}
		logger.log(Level.INFO, "Search for wsa:Addressing policy element via wsp:PolicyReference tag on binding");
		for (int i = 0; i < policyrefs.getLength(); i++) {
			Element element = (Element) policyrefs.item(i);
			String uri = element.getAttribute("URI");
			for (int j = 0; j < policys.length; j++) {
				NamedNodeMap map = policys[j].getAttributes();
				Node node;
				String nodeValue;
				for (int k = 0; k < map.getLength(); k++) {
					node = map.item(k);
					nodeValue = "#" + node.getNodeValue();
					if (uri.equals(nodeValue)) {
						NodeList list = policys[j].getElementsByTagNameNS(WSAM_NAMESPACE_URI,
								WSAM_ADDRESSING_LOCAL_NAME);
						Element addressing = (Element) list.item(0);
						if (addressing != null) {
							logger.log(Level.INFO, "Found wsa:Addressing policy element");
							if (debug)
								XMLUtils.xmlDumpDOMNodes(addressing);
							NamedNodeMap map2 = addressing.getAttributes();
							Node node2;
							for (int m = 0; m < map2.getLength(); m++) {
								node2 = map2.item(m);
								if (node2.getNodeName().equals(WSP_OPTIONAL_ATTR))
									logger.log(Level.INFO, "Found wsp:Optional attribute. It should be false.");
								if (node2.getNodeName().equals(WSP_OPTIONAL_ATTR)
										&& !node2.getNodeValue().equals("false")) {
									TestUtil.logErr("wsp:Optional attribute should not exist or should be false");
									pass = false;
									break;
								}
							}
							if (!pass) {
								list = policys[j].getElementsByTagNameNS(WSAM_NAMESPACE_URI, responseType);
								Element anonymousOrNonanonymous = (Element) list.item(0);
								if (anonymousOrNonanonymous != null) {
									if (responseType.equals(WSAM_ANONYMOUS_RESPONSES_LOCAL_NAME))
										logger.log(Level.INFO, "Found wsa:Anonymous policy element");
									else
										logger.log(Level.INFO, "Found wsa:NonAnonymous policy element");
									pass = true;
									map2 = anonymousOrNonanonymous.getAttributes();
									for (int m = 0; m < map2.getLength(); m++) {
										node2 = map2.item(m);
										if (node2.getNodeName().equals(WSP_OPTIONAL_ATTR))
											logger.log(Level.INFO, "Found wsp:Optional attribute. It should be false.");
										if (node2.getNodeName().equals(WSP_OPTIONAL_ATTR)
												&& !node2.getNodeValue().equals("false")) {
											TestUtil.logErr(
													"wsp:Optional attribute should not exist or should be false");
											pass = false;
											break;
										}
									}
								} else if (testName.equals("VerifyAllResponsesAddressingElementsTest"))
									pass = true;
							}
						}
					}
				}
				if (!pass)
					break;
			}
		}
		if (!pass) {
			logger.log(Level.INFO, "Search for wsa:Addressing policy element via explicit wsp:Policy tag on binding");
			Element addressing = DescriptionUtils.getChildElement(bindings[0], WSAM_NAMESPACE_URI,
					WSAM_ADDRESSING_LOCAL_NAME);
			if (addressing == null) {
				TestUtil.logErr("No wsam:Addressing policy element was found on binding");
				pass = false;
			} else {
				logger.log(Level.INFO, "Found wsa:Addressing policy element");
				if (debug)
					XMLUtils.xmlDumpDOMNodes(addressing);
				NamedNodeMap map2 = addressing.getAttributes();
				Node node2;
				for (int m = 0; m < map2.getLength(); m++) {
					node2 = map2.item(m);
					if (node2.getNodeName().equals(WSP_OPTIONAL_ATTR))
						TestUtil.logMsg("Found wsp:Optional attribute. It should be false.");
					if (node2.getNodeName().equals(WSP_OPTIONAL_ATTR) && !node2.getNodeValue().equals("false")) {
						TestUtil.logErr("wsp:Optional attribute should not exist or should be false");
						pass = false;
						break;
					}
				}
			}
			if (!pass) {
				Element anonymousOrNonanonymous;
				if (responseType.equals(WSAM_ANONYMOUS_RESPONSES_LOCAL_NAME))
					anonymousOrNonanonymous = DescriptionUtils.getChildElement(bindings[0], WSAM_NAMESPACE_URI,
							WSAM_ANONYMOUS_RESPONSES_LOCAL_NAME);
				else
					anonymousOrNonanonymous = DescriptionUtils.getChildElement(bindings[0], WSAM_NAMESPACE_URI,
							WSAM_NONANONYMOUS_RESPONSES_LOCAL_NAME);
				if (anonymousOrNonanonymous != null) {
					if (responseType.equals(WSAM_ANONYMOUS_RESPONSES_LOCAL_NAME))
						logger.log(Level.INFO, "Found wsa:Anonymous policy element");
					else
						logger.log(Level.INFO, "Found wsa:NonAnonymous policy element");
					pass = true;
					logger.log(Level.INFO, "See if wsp:Optional attribute exists");
					NamedNodeMap map2 = anonymousOrNonanonymous.getAttributes();
					Node node2;
					for (int m = 0; m < map2.getLength(); m++) {
						node2 = map2.item(m);
						if (node2.getNodeName().equals(WSP_OPTIONAL_ATTR))
							TestUtil.logMsg("Found wsp:Optional attribute. It should be false.");
						if (node2.getNodeName().equals(WSP_OPTIONAL_ATTR) && !node2.getNodeValue().equals("false")) {
							TestUtil.logErr("wsp:Optional attribute should not exist or should be false");
							pass = false;
							break;
						}
					}
				} else if (testName.equals("VerifyAllResponsesAddressingElementsTest"))
					pass = true;
			}
		}
		return pass;
	}

	private boolean VerifyBindingHasNoAddressing() throws Exception {
		logger.log(Level.INFO, "VerifyBindingHasNoAddressing");
		boolean pass = false;

		logger.log(Level.INFO, "Make sure wsa:Addressing policy assertion does not exist");
		Element[] bindings = DescriptionUtils.getBindings(client3.getDocument());
		NodeList policyrefs = bindings[0].getElementsByTagNameNS(WSP_NAMESPACE_URI, WSP_POLICYREFERENCE_LOCAL_NAME);
		Element[] policys = DescriptionUtils.getChildElements(bindings[0], WSP_NAMESPACE_URI, WSP_POLICY_LOCAL_NAME);
		if (policyrefs.getLength() == 0 && policys.length == 0) {
			return true;
		}
		for (int i = 0; i < policyrefs.getLength(); i++) {
			Element element = (Element) policyrefs.item(i);
			String uri = element.getAttribute("URI");
			for (int j = 0; j < policys.length; j++) {
				NamedNodeMap map = policys[j].getAttributes();
				for (int k = 0; k < map.getLength(); k++) {
					Node node = map.item(k);
					String nodeValue = "#" + node.getNodeValue();
					if (uri.equals(nodeValue)) {
						NodeList list = policys[j].getElementsByTagNameNS(WSAM_NAMESPACE_URI,
								WSAM_ADDRESSING_LOCAL_NAME);
						Element addressing = (Element) list.item(0);
						if (addressing == null)
							pass = true;
						else {
							TestUtil.logErr("wsam:Addressing Extension Element exists on binding");
							pass = false;
							break;
						}
					}
				}
				if (!pass)
					break;
			}
		}
		if (!pass) {
			Element addressing = DescriptionUtils.getChildElement(bindings[0], WSAM_NAMESPACE_URI,
					WSAM_ADDRESSING_LOCAL_NAME);
			if (addressing == null)
				pass = true;
			else {
				TestUtil.logErr("wsam:Addressing Extension Element must not exist on binding");
				pass = false;
			}
		}
		return pass;
	}

	private boolean VerifyActionFaultAction() throws Exception {
		logger.log(Level.INFO, "VerifyActionFaultAction");
		boolean pass = true;

		logger.log(Level.INFO, "Checking for operation names for portType: " + EXPECTED_ADDRESSING_PORTTYPE_NAME);
		Element operations[] = DescriptionUtils.getPortTypeOperationNameElements(client2.getDocument(),
				EXPECTED_ADDRESSING_PORTTYPE_NAME);

		for (int i = 0; i < operations.length; i++) {
			String name = operations[i].getAttribute(WSDL_NAME_ATTR);
			logger.log(Level.INFO, "Operation-> " + name);

			Element input = DescriptionUtils.getChildElement(operations[i], WSDL_NAMESPACE_URI, WSDL_INPUT_LOCAL_NAME);
			Element output = DescriptionUtils.getChildElement(operations[i], WSDL_NAMESPACE_URI,
					WSDL_OUTPUT_LOCAL_NAME);
			Element fault = DescriptionUtils.getChildElement(operations[i], WSDL_NAMESPACE_URI,
					WSDL_FAULT_LOCAL_NAME);

			String iaction = "", oaction = "", faction = "";

			if (input != null)
				iaction = input.getAttributeNS(WSAM_NAMESPACE_URI, WSAM_ACTION_LOCAL_NAME);
			if (output != null)
				oaction = output.getAttributeNS(WSAM_NAMESPACE_URI, WSAM_ACTION_LOCAL_NAME);
			if (fault != null)
				faction = fault.getAttributeNS(WSAM_NAMESPACE_URI, WSAM_ACTION_LOCAL_NAME);

			logger.log(Level.INFO, "iaction=" + iaction);
			logger.log(Level.INFO, "oaction=" + oaction);
			logger.log(Level.INFO, "faction=" + faction);

			if (name.equals("address1")) {
				if (iaction.equals("input1")
						&& oaction.equals("http://rpclitservice.org/wsdl/AddressingEndpoint/address1Response")
						&& faction.equals(""))
					logger.log(Level.INFO, "verification PASSED for " + name);
				else {
					TestUtil.logErr("verification FAILED for " + name);
					pass = false;
				}
			} else if (name.equals("address2")) {
				if (iaction.equals("http://rpclitservice.org/wsdl/AddressingEndpoint/address2Request")
						&& oaction.equals("output2") && faction.equals(""))
					logger.log(Level.INFO, "verification PASSED for " + name);
				else {
					TestUtil.logErr("verification FAILED for " + name);
					pass = false;
				}

			} else if (name.equals("address3")) {
				if (iaction.equals("http://rpclitservice.org/wsdl/AddressingEndpoint/address3Request")
						&& oaction.equals("http://rpclitservice.org/wsdl/AddressingEndpoint/address3Response")
						&& faction.equals("fault1"))
					logger.log(Level.INFO, "verification PASSED for " + name);
				else {
					TestUtil.logErr("verification FAILED for " + name);
					pass = false;
				}

			} else if (name.equals("address4")) {
				if (iaction.equals("http://rpclitservice.org/wsdl/AddressingEndpoint/address4Request")
						&& oaction.equals("output4") && faction.equals("fault1"))
					logger.log(Level.INFO, "verification PASSED for " + name);
				else {
					TestUtil.logErr("verification FAILED for " + name);
					pass = false;
				}

			} else if (name.equals("address5")) {
				if (iaction.equals("input5") && oaction.equals("output5") && faction.equals("fault1"))
					logger.log(Level.INFO, "verification PASSED for " + name);
				else {
					TestUtil.logErr("verification FAILED for " + name);
					pass = false;
				}

			} else if (name.equals("address6")) {
				if (iaction.equals("http://rpclitservice.org/wsdl/AddressingEndpoint/address6Request")
						&& oaction.equals("http://rpclitservice.org/wsdl/AddressingEndpoint/address6Response")
						&& faction.equals("http://rpclitservice.org/wsdl/AddressingEndpoint/address6/Fault/MyFault1"))
					logger.log(Level.INFO, "verification PASSED for " + name);
				else {
					TestUtil.logErr("verification FAILED for " + name);
					pass = false;
				}

			} else if (name.equals("address7")) {
				if (iaction.equals("http://rpclitservice.org/wsdl/AddressingEndpoint/address7Request")
						&& oaction.equals("http://rpclitservice.org/wsdl/AddressingEndpoint/address7Response")
						&& faction.equals("http://rpclitservice.org/wsdl/AddressingEndpoint/address7/Fault/MyFault1"))
					logger.log(Level.INFO, "verification PASSED for " + name);
				else {
					TestUtil.logErr("verification FAILED for " + name);
					pass = false;
				}
			}

		}
		return pass;
	}
}
