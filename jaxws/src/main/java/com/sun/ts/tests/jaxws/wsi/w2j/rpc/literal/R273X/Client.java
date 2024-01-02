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

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.R273X;

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
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.wsi.requests.SOAPRequests;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;

public class Client extends BaseClient implements SOAPRequests {

	private static String OPERATION_NAME = "echoFooBarResponse";

	private static String OPERATION_URI = "http://w2jrlr273Xtestservice.org/W2JRLR273XTestService.wsdl";

	private static String PART_ACCESSOR_NAME = "fooBarResponse";

	private static String PART1_NAME = "foo";

	private static String PART1_URI = "http://w2jrlr273Xtestservice.org/xsd";

	private static String PART2_NAME = "bar";

	private static String PART2_URI = "http://w2jrlr273Xtestservice.org/xsd";

	/**
	 * The clients.
	 */
	private W2JRLR273XClient1 client1;

	private W2JRLR273XClient2 client2;

	static W2JRLR273XTestService service = null;

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
		client1 = (W2JRLR273XClient1) ClientFactory.getClient(W2JRLR273XClient1.class, service);
		client2 = (W2JRLR273XClient2) ClientFactory.getClient(W2JRLR273XClient2.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testNamespacesForChildrenOfPartAccessorsOnRequest
	 *
	 * @assertion_ids: WSI:SPEC:R2735; WSI:SPEC:R2737;
	 *
	 * @test_Strategy: A request is made from the generated client. The endpoint is
	 *                 replaced by a Servlet Filter, that verifies the request. The
	 *                 returned object is interrogated for success of failure.
	 *
	 * @throws Exception
	 */
	@Test
	public void testNamespacesForChildrenOfPartAccessorsOnRequest() throws Exception {
		FooBar fb = new FooBar();
		String expected = "I am a foo request";
		fb.setFoo(expected);
		fb.setBar("I am a bar request");
		FooBar fb2;
		try {
			fb2 = client2.echoFooBar(fb);
			if (!fb2.getFoo().equals(expected)) {
				TestUtil.logErr("Expected value=" + expected);
				TestUtil.logErr("Actual value=" + fb2.getFoo());
				throw new Exception("testNamespacesForChildrenOfPartAccessorsOnRequest failed (BP-R273X)");
			}
		} catch (Exception e) {
			throw new Exception("Unable to invoke echoFooBar operation (BP-R273X)", e);
		}
	}

	/**
	 * @testName: testNamespacesForChildrenOfPartAccessorsOnResponse
	 *
	 * @assertion_ids: WSI:SPEC:R2735; WSI:SPEC:R2737;
	 *
	 * @test_Strategy: A valid request is made to the endpoint and the returned
	 *                 response is investigated for the presence of namespaces for
	 *                 children of part accessor.
	 *
	 * @throws Exception
	 */
	@Test
	public void testNamespacesForChildrenOfPartAccessorsOnResponse() throws Exception {
		SOAPMessage response;
		try {
			response = client1.makeSaajRequest(R273X_REQUEST);
			String result = verifyNamespacesForChildrenOfPartAccessors(response);
			if (!result.equals("OK")) {
				TestUtil.logErr("Expected value=OK");
				TestUtil.logErr("Actual value=" + result);
				throw new Exception("testNamespacesForChildrenOfPartAccessorsOnResponse failed (BP-R273X)");
			}
		} catch (Exception e) {
			throw new Exception("Unable to invoke echoFooBar operation (BP-R273X)", e);
		}
	}

	/**
	 * Verifies that the children of part accessor for rpc-literal SOAP messages
	 * parameter and return values are namespace qualified.
	 * 
	 * @param request the SOAPMessage response.
	 * 
	 * @return "OK" if valid; "NOT OK" otherwise.
	 * 
	 * @throws Exception
	 */
	private String verifyNamespacesForChildrenOfPartAccessors(SOAPMessage sm) throws Exception {
		String result = "NOT OK";
		System.out.println("Getting children of body element ...");
		Iterator children = sm.getSOAPBody().getChildElements();
		SOAPElement child;
		String localName, uri;
		boolean flg1 = false, flg2 = false, flg3 = false;
		if (children.hasNext()) {
			System.out.println("Getting operation name ...");
			child = (SOAPElement) children.next();
			localName = child.getElementName().getLocalName();
			uri = child.getElementName().getURI();
			System.out.println("child localname: " + localName);
			System.out.println("child uri: " + uri);
			if (localName.equals(OPERATION_NAME) && uri.equals(OPERATION_URI)) {
				children = child.getChildElements();
				if (children.hasNext()) {
					System.out.println("Getting part accessor name ...");
					child = (SOAPElement) children.next();
					localName = child.getElementName().getLocalName();
					uri = child.getElementName().getURI();
					System.out.println("  child localname: " + localName);
					System.out.println("  child uri: " + uri);
					if (localName.equals(PART_ACCESSOR_NAME) && (uri == null || uri.equals("")))
						flg1 = true;
					// All children of part accessors MUST be namespace qualified
					if (!children.hasNext()) {
						System.out.println("Getting children of part accessor ...");
						children = child.getChildElements();
						int i = 0;
						System.out.println("Verifying namespaces exist and are correct for children");
						while (children.hasNext()) {
							i++;
							child = (SOAPElement) children.next();
							localName = child.getElementName().getLocalName();
							uri = child.getElementName().getURI();
							System.out.println("    child localname: " + localName);
							System.out.println("    child uri: " + uri);
							switch (i) {
							case 1:
								if (localName.equals(PART1_NAME) && uri.equals(PART1_URI))
									flg2 = true;
								break;
							case 2:
								if (localName.equals(PART2_NAME) && uri.equals(PART2_URI))
									flg3 = true;
								break;
							}
						}
					}
				}
			}
		}
		if (flg1 && flg2 && flg3)
			result = "OK";
		System.out.println("result=" + result);
		JAXWS_Util.printSOAPMessage(sm, System.out);
		return result;
	}
}
