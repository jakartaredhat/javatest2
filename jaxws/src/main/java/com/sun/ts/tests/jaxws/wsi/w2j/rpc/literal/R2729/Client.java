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

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.R2729;

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
import org.w3c.dom.NodeList;

import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.wsi.requests.SOAPRequests;

public class Client extends BaseClient implements SOAPRequests {
	/**
	 * The string to be echoed.
	 */
	private static final String STRING = "R2729";

	/**
	 * The client.
	 */
	private W2JRLR2729Client client;

	static W2JRLR2729TestService service = null;

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
		client = (W2JRLR2729Client) ClientFactory.getClient(W2JRLR2729Client.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testResponseWrapperElement
	 *
	 * @assertion_ids: WSI:SPEC:R2729
	 *
	 * @test_Strategy: A request to the echoString operation is made and the
	 *                 returned wrapper element must be "echoStringResponse".
	 *
	 * @throws Exception
	 */
	@Test
	public void testResponseWrapperElement() throws Exception {
		Document document;
		try {
			InputStream is = client.makeHTTPRequest(R2729_REQUEST);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(is);
		} catch (Exception e) {
			throw new Exception("Unable to invoke 'echoString' operation (BP-R2729)", e);
		}
		Element envelope = document.getDocumentElement();
		System.out.println("got " + envelope.getNamespaceURI() + ":" + envelope.getLocalName());
		NodeList list = envelope.getElementsByTagNameNS("http://w2jrlr2729testservice.org/W2JRLR2729TestService.wsdl",
				"echoStringResponse");
		if (list.getLength() == 0) {
			throw new Exception("Required 'echoStringResponse' element not present in message (BP-R2729)");
		}
	}
}
