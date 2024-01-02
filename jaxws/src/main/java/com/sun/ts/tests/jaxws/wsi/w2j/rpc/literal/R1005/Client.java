/*
 * Copyright (c) 2007, 2024 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.R1005;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Iterator;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.wsi.constants.WSIConstants;
import com.sun.ts.tests.jaxws.wsi.requests.SOAPRequests;

import jakarta.xml.soap.Name;
import jakarta.xml.soap.Node;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

public class Client extends BaseClient implements WSIConstants, SOAPRequests {

	private W2JRLR1005Client client;

	static SimpleTest service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	/**
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * @class.setup_props: webServerHost; webServerPort; platform.mode;
	 *
	 * @param args
	 * @param properties
	 *
	 * @throws Exception
	 */
	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		client = (W2JRLR1005Client) ClientFactory.getClient(W2JRLR1005Client.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testNoEncodingStyleOnResponseEnvelopeElements
	 *
	 * @assertion_ids: WSI:SPEC:R1005
	 *
	 * @test_Strategy: Make a request and inspect response elements with a namespace
	 *                 of "http://schemas.xmlsoap.org/soap/envelope/" to ensure they
	 *                 don't have soap:encodingStyle attribute.
	 *
	 * @throws Exception
	 */
	@Test
	public void testNoEncodingStyleOnResponseEnvelopeElements() throws Exception {
		SOAPMessage response = null;
		try {
			response = client.makeSaajRequest(HELLOWORLD_WITH_HANDLER);
		} catch (Exception e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
		try {
			validateNoEncodingStyleOnEnvelopeElements(response);
		} catch (SOAPException se) {
			throw new Exception("Error creating response object", se);
		}
		client.logMessageInHarness(response);
	}

	/**
	 * @testName: testNoEncodingStyleOnRequestEnvelopeElements
	 *
	 * @assertion_ids: WSI:SPEC:R1005
	 *
	 * @test_Strategy: Make a request and inspect its elements on the server with a
	 *                 namespace of "http://schemas.xmlsoap.org/soap/envelope/" to
	 *                 ensure they don't have soap:encodingStyle attribute.
	 *
	 * @throws Exception
	 */
	@Test
	public void testNoEncodingStyleOnRequestEnvelopeElements() throws Exception {
		String response = null;
		try {
			response = client.helloWorld();
			logger.log(Level.INFO, "response=" + response);
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("Test didn't complete properly: ", e);
		}
		if (response.startsWith("failed")) {
			throw new Exception(response);
		}
	}

	private void validateNoEncodingStyleOnEnvelopeElements(SOAPMessage response) throws Exception, SOAPException {
		validateNoEncodingStyleOnEnvelopeElements(response.getSOAPPart().getEnvelope(), response);
	}

	private void validateNoEncodingStyleOnEnvelopeElements(Iterator soapElements, SOAPMessage response)
			throws Exception {
		Node n;
		while (soapElements.hasNext()) {
			n = (Node) soapElements.next();
			if (n instanceof SOAPElement) {
				validateNoEncodingStyleOnEnvelopeElements((SOAPElement) n, response);
			}
		}
	}

	private void validateNoEncodingStyleOnEnvelopeElements(SOAPElement element, SOAPMessage response) throws Exception {
		boolean fails = hasEncodingStyleAttr(element);
		if (fails) {
			client.logMessageInHarness(response);
			throw new Exception("Invalid element: elements with namespace http://schemas.xmlsoap.org/soap/envelope/"
					+ " cannot have soap:encodingStyle attribute (BP-R1005):  "
					+ element.getElementName().getQualifiedName());
		}
		validateNoEncodingStyleOnEnvelopeElements(element.getChildElements(), response);
	}

	private boolean hasEncodingStyleAttr(SOAPElement elem) {
		Iterator attrs = elem.getAllAttributes();
		Name name;
		String uri;
		while (attrs.hasNext()) {
			name = (Name) attrs.next();
			uri = name.getURI();
			if (uri == null) {
				uri = "";
			}
			if (name.getLocalName().equals(SOAP_ENC_STYLE) && uri.equals(SOAP_ENV_NS)) {
				return true;
			}
		}
		return false;
	}
}
