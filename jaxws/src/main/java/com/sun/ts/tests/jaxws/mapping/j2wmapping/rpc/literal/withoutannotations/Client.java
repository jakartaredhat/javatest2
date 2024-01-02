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

package com.sun.ts.tests.jaxws.mapping.j2wmapping.rpc.literal.withoutannotations;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.sharedclients.DescriptionClient;
import com.sun.ts.tests.jaxws.wsi.constants.DescriptionConstants;
import com.sun.ts.tests.jaxws.wsi.constants.SOAPConstants;
import com.sun.ts.tests.jaxws.wsi.j2w.NamespaceAttributeVerifier;
import com.sun.ts.tests.jaxws.wsi.utils.DescriptionUtils;

public class Client implements DescriptionConstants, SOAPConstants {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	/*
	 * The client.
	 */
	private DescriptionClient client;

	/*
	 * The document.
	 */
	private Document document;

	/**
	 * The generated WSDL file.
	 */
	private static final String WSDLLOC_URL = "j2wrlwithoutannotations.wsdlloc.1";

	private String wsdlFile;

	private String tshome = "/tmp";

	private String wsdlFileUrl = null;

	private static final String EXPECTED_TARGETNAMESPACE = "http://withoutannotations.literal.rpc.j2wmapping.mapping.jaxws.tests.ts.sun.com/";

	private static final String EXPECTED_PORTTYPE_NAME = "J2WRLSharedEndpoint";

	private static final String EXPECTED_PORT_NAME = "J2WRLSharedEndpointImplPort";

	private static final String EXPECTED_SERVICE_NAME = "J2WRLSharedEndpointImplService";

	private boolean debug = false;

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
		wsdlFileUrl = "file:" + tshome + wsdlFile;
		logger.log(Level.INFO, "wsdlFileUrl=" + wsdlFileUrl);
		client = new DescriptionClient();
		client.setURL(wsdlFileUrl);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {

		logger.log(Level.INFO, "cleanup");
	}

	/*
	 * @testName: VerifyTargetNamespaceWithoutAnnotation
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3013;
	 * JAXWS:SPEC:3019; JAXWS:SPEC:3027; JAXWS:SPEC:3033; JAXWS:SPEC:3034;
	 * JAXWS:SPEC:3035;
	 *
	 * @test_Strategy: Verify that the targetNamespace in the generated WSDL is
	 * derived from the package name when no targetNamespace is specified in the
	 * jakarta.jws.WebService annotation. This is the default value case. (Java to
	 * WSDL 1.1 Mapping). Conformance requirement done: - Package name mapping
	 *
	 */
	@Test
	public void VerifyTargetNamespaceWithoutAnnotation() throws Exception {
		logger.log(Level.INFO, "VerifyTargetNamespaceWithoutAnnotation");
		boolean pass = true;

		try {
			document = client.getDocument();
			String targetNamespace = document.getDocumentElement().getAttribute(WSDL_TARGETNAMESPACE_ATTR);
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
			throw new Exception("VerifyTargetNamespaceWithoutAnnotation failed");
	}

	/*
	 * @testName: VerifyOneWayOperationWithoutAnnotation
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3011;
	 * JAXWS:SPEC:3013; JAXWS:SPEC:3019; JAXWS:SPEC:3027; JAXWS:SPEC:3033;
	 * JAXWS:SPEC:3034; JAXWS:SPEC:3035;
	 *
	 * @test_Strategy: Verify that the operation oneWayOperation in the generated
	 * WSDL is either mapped one way or two way when it is not specified as Oneway
	 * by using the jakarta.jws.Oneway annotation. The spec is vague in this regard
	 * so you must assume that the mapping could be one way or two way. (Java to
	 * WSDL 1.1 Mapping). Conformance requirement done: - One-way mapping or Two-way
	 * mapping
	 *
	 *
	 */
	@Test
	public void VerifyOneWayOperationWithoutAnnotation() throws Exception {
		logger.log(Level.INFO, "VerifyOneWayOperationWithoutAnnotation");
		boolean pass = true;
		boolean foundOperation = false;
		boolean foundInputElement = false;
		boolean foundOutputElement = false;
		String operationName = null;

		try {
			document = client.getDocument();
			logger.log(Level.INFO, "Verify that operation=oneWayOperation is mapped to oneway in generated WSDL");
			Element[] bindings = DescriptionUtils.getBindings(document);
			for (int i = 0; i < bindings.length; i++) {
				Element[] operations = DescriptionUtils.getChildElements(bindings[i], WSDL_NAMESPACE_URI,
						WSDL_OPERATION_LOCAL_NAME);
				for (int j = 0; j < operations.length; j++) {
					operationName = operations[j].getAttribute("name");
					if (operationName.equals("oneWayOperation")) {
						if (debug)
							DescriptionUtils.dumpDOMNodes(operations[j]);
						foundOperation = true;
						Element[] children = DescriptionUtils.getChildElements(operations[j]);
						logger.log(Level.INFO, "Verify that operation=oneWayOperation has an <input> element");
						for (int k = 0; k < children.length; k++) {
							String localName = children[k].getLocalName();
							if (localName.equals(WSDL_OUTPUT_LOCAL_NAME)) {
								foundOutputElement = true;
							} else if (localName.equals(WSDL_INPUT_LOCAL_NAME)) {
								foundInputElement = true;
							} else if (localName.equals(WSDL_FAULT_LOCAL_NAME)) {
								TestUtil.logErr(
										"Operation name: " + operationName + " should not have a <fault> element");
								pass = false;
							} else if (localName.equals(SOAP_HEADER_LOCAL_NAME)) {
								TestUtil.logErr("Operation name: " + operationName
										+ " should not have a <soap header> element");
								pass = false;
							} else if (localName.equals(SOAP_HEADERFAULT_LOCAL_NAME)) {
								TestUtil.logErr("Operation name: " + operationName
										+ " should not have a <soap headerfault> element");
								pass = false;
							}
						}
						if (foundOperation)
							break;
					}
					if (foundOperation)
						break;
				}
			}
			if (!foundOperation) {
				TestUtil.logErr("Could not find operation=oneWayOperation in generated WSDL");
				pass = false;
			}
			if (!foundInputElement) {
				TestUtil.logErr("Operation name: " + operationName + " should have an <input> element");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "The operation=oneWayOperation in correctly mapped to oneway in generated WSDL");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception:" + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("VerifyOneWayOperationWithoutAnnotation failed");
	}

	/*
	 * @testName: VerifySOAPElementNamespaceUseAttributeWithoutAnnotation
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3013;
	 * JAXWS:SPEC:3019; JAXWS:SPEC:3027; JAXWS:SPEC:3033; JAXWS:SPEC:3034;
	 * JAXWS:SPEC:3035;
	 *
	 * @test_Strategy: Verify the namespace and use attributes for all SOAP Elements
	 * in the generated WSDL (Java to WSDL 1.1 Mapping and BP1.1). Conformance
	 * requirement done: - verify use attribute of literal for all SOAP elements -
	 * verify namespace attribute on all soap:body elements
	 *
	 *
	 */
	@Test
	public void VerifySOAPElementNamespaceUseAttributeWithoutAnnotation() throws Exception {
		logger.log(Level.INFO, "VerifySOAPElementNamespaceUseAttributeWithoutAnnotation");
		boolean pass = true;

		Document document = client.getDocument();
		logger.log(Level.INFO, "Verify that namespace and use attribute does exist on soap:body elements");
		NamespaceAttributeVerifier verifier = new NamespaceAttributeVerifier(document, 2717);
		verifier.verify();
		logger.log(Level.INFO, "Verify that namespace attribute does not exist for soap:fault, "
				+ "soap:header, soap:headerfault elements");
		verifier = new NamespaceAttributeVerifier(document, 2726);
		verifier.verify();
		if (pass)
			logger.log(Level.INFO, "Verification passed");
		if (!pass)
			throw new Exception("VerifySOAPElementNamespaceUseAttributeWithoutAnnotation failed");
	}

	/*
	 * @testName: VerifySOAPBindingTransportStyleAttributeWithoutAnnotation
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3013;
	 * JAXWS:SPEC:3019; JAXWS:SPEC:3027; JAXWS:SPEC:3033; JAXWS:SPEC:3034;
	 * JAXWS:SPEC:3035; JAXWS:SPEC:3041; JAXWS:SPEC:3042;
	 *
	 * @test_Strategy: Verify the soap:binding transport and style attributes in the
	 * generated WSDL. (Java to WSDL 1.1 Mapping and BP1.1). Conformance requirement
	 * done: - SOAP binding support - SOAP binding style required - SOAP binding
	 * transport required
	 *
	 *
	 */
	@Test
	public void VerifySOAPBindingTransportStyleAttributeWithoutAnnotation() throws Exception {
		TestUtil.logMsg("VerifySOAPBindingTransportStyleAttributeWithoutAnnotation");
		boolean pass = true;

		Document document = client.getDocument();
		logger.log(Level.INFO, "Verify soap:binding transport and style attribute");
		Element[] bindings = DescriptionUtils.getBindings(document);
		for (int i = 0; i < bindings.length; i++) {
			if (debug)
				DescriptionUtils.dumpDOMNodes(bindings[i]);
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
				}
				String transport = soapBinding.getAttribute(SOAP_TRANSPORT_ATTR);
				if (!transport.equals(SOAP_TRANSPORT)) {
					TestUtil.logErr("soap:binding transport attribute incorrect, expected: " + SOAP_TRANSPORT
							+ ", got: " + transport);
					pass = false;
				}
			}
		}
		if (pass)
			logger.log(Level.INFO, "Verification passed");
		if (!pass)
			throw new Exception("VerifySOAPBindingTransportStyleAttributeWithoutAnnotation failed");
	}

	/*
	 * @testName: VerifyPortTypeNameWithoutAnnotation
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3013;
	 * JAXWS:SPEC:3019; JAXWS:SPEC:3027; JAXWS:SPEC:3033; JAXWS:SPEC:3034;
	 * JAXWS:SPEC:3035;
	 *
	 * @test_Strategy: Verify the wsdl:portType name is correct in the generated
	 * WSDL. Conformance requirement done: - portType naming
	 *
	 *
	 */
	@Test
	public void VerifyPortTypeNameWithoutAnnotation() throws Exception {
		logger.log(Level.INFO, "VerifyPortTypeNameWithoutAnnotation");
		boolean pass = true;

		logger.log(Level.INFO, "Checking for portType name verification of: " + EXPECTED_PORTTYPE_NAME);
		pass = DescriptionUtils.isPortTypeNameAttr(client.getDocument(), EXPECTED_PORTTYPE_NAME);
		if (!pass)
			throw new Exception("VerifyPortTypeNameWithoutAnnotation failed");
		else
			logger.log(Level.INFO, "Verification passed");
	}

	/*
	 * @testName: VerifyServiceNameWithoutAnnotation
	 *
	 * @assertion_ids: JAXWS:SPEC:3037;
	 *
	 * @test_Strategy: Verify the wsdl:service name is correct in the generated
	 * WSDL. Conformance requirement done: - service naming
	 *
	 *
	 */
	@Test
	public void VerifyServiceNameWithoutAnnotation() throws Exception {
		logger.log(Level.INFO, "VerifyServiceNameWithoutAnnotation");
		boolean pass = true;

		logger.log(Level.INFO, "Checking for service name verification of: " + EXPECTED_SERVICE_NAME);
		pass = DescriptionUtils.isServiceNameAttr(client.getDocument(), EXPECTED_SERVICE_NAME);
		if (!pass)
			throw new Exception("VerifyServiceNameWithoutAnnotation failed");
		else
			logger.log(Level.INFO, "Verification passed");
	}

	/*
	 * @testName: VerifyPortNameWithoutAnnotation
	 *
	 * @assertion_ids: JAXWS:SPEC:3000; JAXWS:SPEC:3001; JAXWS:SPEC:3002;
	 * JAXWS:SPEC:3004; JAXWS:SPEC:3005; JAXWS:SPEC:3007; JAXWS:SPEC:3008;
	 * JAXWS:SPEC:3012; JAXWS:SPEC:3019; JAXWS:SPEC:3020; JAXWS:SPEC:3033;
	 * JAXWS:SPEC:3034; JAXWS:SPEC:3035;
	 *
	 * @test_Strategy: Verify the wsdl:port name is correct in the generated WSDL.
	 * Conformance requirement done: - port naming
	 *
	 *
	 */
	@Test
	public void VerifyPortNameWithoutAnnotation() throws Exception {
		logger.log(Level.INFO, "VerifyPortNameWithoutAnnotation");
		boolean pass = true;

		logger.log(Level.INFO, "Checking for port name verification of: " + EXPECTED_PORT_NAME);
		pass = DescriptionUtils.isPortNameAttr(client.getDocument(), EXPECTED_PORT_NAME);
		if (!pass)
			throw new Exception("VerifyPortNameWithoutAnnotation failed");
		else
			logger.log(Level.INFO, "Verification passed");
	}
}
