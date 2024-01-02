/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.R1011;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.wsi.requests.SOAPRequests;

public class Client extends BaseClient implements SOAPRequests {

	/**
	 * The string to be echoed for request two.
	 */
	private static final String STRING_2 = "R1011-2";

	/**
	 * The one client.
	 */
	private W2JRLR1011ClientOne client1;

	/**
	 * The other client.
	 */
	private W2JRLR1011ClientTwo client2;

	static W2JRLR1011TestService service = null;

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
		client1 = (W2JRLR1011ClientOne) ClientFactory.getClient(W2JRLR1011ClientOne.class, service);
		client2 = (W2JRLR1011ClientTwo) ClientFactory.getClient(W2JRLR1011ClientTwo.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testResponseChildren
	 *
	 * @assertion_ids: WSI:SPEC:R1011
	 *
	 * @test_Strategy: A valid request is made to the endpoint and the returned
	 *                 response is investigated in order to determine the document
	 *                 composition.
	 *
	 * @throws Exception
	 */
	@Test
	public void testResponseChildren() throws Exception {
		Document document;
		try {
			InputStream is = client1.makeHTTPRequest(R1011_REQUEST);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(is);
		} catch (Exception e) {
			throw new Exception("Unable to invoke echoString operation (BP-R1011)", e);
		}
		Element envelope = document.getDocumentElement();
		if (!isElement(envelope, "http://schemas.xmlsoap.org/soap/envelope/", "Envelope")) {
			throw new Exception("Expected 'env:Envelope' element not received (BP-R1011)");
		}
		NodeList list = envelope.getChildNodes();
		boolean hasBody = false;
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (hasBody) {
				throw new Exception("Child of 'env:Envelope' following 'env:Body' (BP-R1011)");
			} else {
				hasBody = isElement((Element) node, "http://schemas.xmlsoap.org/soap/envelope/", "Body");
			}
		}
	}

	protected boolean isElement(Element element, String namespaceURI, String localName) {
		if (!namespaceURI.equals(element.getNamespaceURI())) {
			return false;
		}
		return localName.equals(element.getLocalName());
	}

	/**
	 * @testName: testRequestChildren
	 *
	 * @assertion_ids: WSI:SPEC:R1011
	 *
	 * @test_Strategy: A request is made from the generated client. A handler
	 *                 verifies the encoding. The returned string indicates the
	 *                 success or failure.
	 *
	 * @throws Exception
	 */
	@Test
	public void testRequestChildren() throws Exception {
		String result;
		try {
			System.out.println("request=" + STRING_2);
			result = client2.echoString(STRING_2);
			System.out.println("result=" + result);
		} catch (Exception e) {
			throw new Exception("Unable to invoke echoString operation (BP-R1011)", e);
		}
		if (!result.equals(STRING_2)) {
			if (result.equals("EXCEPTION")) {
				throw new Exception("Endpoint unable to process request (BP-R1011)");
			} else {
				throw new Exception("Request contains invalid 'soap:Envelope' children (BP-R1011)");
			}
		}
	}
}
