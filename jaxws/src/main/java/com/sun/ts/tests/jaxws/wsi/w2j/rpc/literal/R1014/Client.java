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

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.R1014;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Iterator;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.wsi.requests.SOAPRequests;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

public class Client extends BaseClient implements SOAPRequests {

	private W2JRLR1014Client client;

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
		client = (W2JRLR1014Client) ClientFactory.getClient(W2JRLR1014Client.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testBodyChildrenAreQualifiedOnResponse
	 *
	 * @assertion_ids: WSI:SPEC:R1014
	 *
	 * @test_Strategy: Make a request and inspect response soap:Body children to
	 *                 ensure they are namespace qualified.
	 *
	 * @throws Exception
	 */
	@Test
	public void testBodyChildrenAreQualifiedOnResponse() throws Exception {
		SOAPMessage response = null;
		try {
			response = client.makeSaajRequest(HELLOWORLD_WITH_HANDLER);
		} catch (Exception e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
		try {
			validateBodyChildrenAreQualified(response);
		} catch (SOAPException se) {
			throw new Exception("Error creating response object", se);
		}
		client.logMessageInHarness(response);
	}

	/**
	 * @testName: testBodyChildrenAreQualifiedOnRequest
	 *
	 * @assertion_ids: WSI:SPEC:R1014
	 *
	 * @test_Strategy: Make a request and inspect response to see if request was
	 *                 conformant, as determined by server side handler.
	 *
	 * @throws Exception
	 */
	@Test
	public void testBodyChildrenAreQualifiedOnRequest() throws Exception {
		String response = null;
		try {
			response = client.helloWorld();
		} catch (Exception e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
		if (response.startsWith("failed")) {
			throw new Exception(response);
		}
	}

	private void validateBodyChildrenAreQualified(SOAPMessage response) throws Exception, SOAPException {
		Iterator bodyChildren = response.getSOAPPart().getEnvelope().getBody().getChildElements();
		SOAPElement child;
		String uri;
		while (bodyChildren.hasNext()) {
			child = (SOAPElement) bodyChildren.next();
			uri = child.getElementName().getURI();
			if (uri == null || uri.equals("")) {
				client.logMessageInHarness(response);
				throw new Exception("Invalid element: child elements of soap:Body" + " must be qualified (BP-R1014):  "
						+ child.getElementName().getQualifiedName());
			}
		}
	}
}
