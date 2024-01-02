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

package com.sun.ts.tests.jaxws.ee.w2j.document.literal.customization.embedded;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;

import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.ee.w2j.document.literal.customization.embedded.custom.pkg.CustomizationEmbeddedTestService;
import com.sun.ts.tests.jaxws.ee.w2j.document.literal.customization.embedded.custom.pkg.HelloException;

public class Client extends BaseClient {
	// need to create jaxbContext
	private static final ObjectFactory of = new ObjectFactory();

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.w2j.document.literal.customization.embedded.";

	// service and port information
	private static final String NAMESPACEURI = "http://customizationembeddedtest.org/wsdl";

	private static final String SERVICE_NAME = "myService";

	private static final String PORT_NAME = "HelloPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	// URL properties used by the test
	private static final String ENDPOINT_URL = "w2jcustomizationembeddedtest.endpoint.1";

	private static final String WSDLLOC_URL = "w2jcustomizationembeddedtest.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	Hello port = null;

	static CustomizationEmbeddedTestService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		service = (CustomizationEmbeddedTestService) getSharedObject();
	}

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(ENDPOINT_URL);
		url = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC_URL);
		wsdlurl = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		logger.log(Level.INFO, "Service Endpoint URL: " + url);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
	}

	protected void getPortStandalone() throws Exception {
		port = (Hello) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, CustomizationEmbeddedTestService.class, PORT_QNAME,
				Hello.class);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (Hello) JAXWS_Util.getPort(service, PORT_QNAME, Hello.class);
		// port = (Hello) service.getMyHelloPort();
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "Obtained port");
		JAXWS_Util.dumpTargetEndpointAddress(port);
		// JAXWS_Util.setSOAPLogging(port);
	}

	/* Test setup */

	/*
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * 
	 * @class.setup_props: webServerHost; webServerPort; platform.mode;
	 */
	@BeforeEach
	public void setup() throws Exception {
		super.setup();
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: CustomizationEmbeddedTest
	 *
	 * @assertion_ids: JAXWS:SPEC:8000; JAXWS:SPEC:8001; JAXWS:SPEC:8002;
	 * JAXWS:SPEC:8005; JAXWS:SPEC:8006; JAXWS:SPEC:8007; JAXWS:SPEC:8010;
	 * JAXWS:SPEC:8012; JAXWS:SPEC:8013; JAXWS:SPEC:2064; JAXWS:SPEC:2024;
	 * JAXWS:SPEC:2028; JAXWS:SPEC:7000; JAXWS:SPEC:8009;
	 *
	 * @test_Strategy: Embedded annotations in the wsdl are used to change aspects
	 * of the wsdl file. If the endpoint is reachable then the customization worked.
	 *
	 *
	 * 
	 */
	@Test
	public void CustomizationEmbeddedTest() throws Exception {
		TestUtil.logTrace("CustomizationEmbeddedTest");
		boolean pass = true;
		String reqStr = "Hello";
		String reqStr2 = "World";
		String resStr = "Hello, World!";
		try {
			// wrapper style
			logger.log(Level.INFO, "Testing wrapper style enableWrapperStyle=true ...");
			String result = port.hello1(reqStr);
			logger.log(Level.INFO, "result=" + result);
			if (!result.equals(resStr)) {
				TestUtil.logErr("expected: " + resStr + ", received: " + result);
				pass = false;
			}

			// non-wrapper style
			logger.log(Level.INFO, "Testing non-wrapper style enableWrapperStyle=false ...");
			Hello2 hello2 = of.createHello2();
			hello2.setArgument(reqStr);
			HelloResponse h = port.hello2(hello2);
			result = h.getResponse();
			logger.log(Level.INFO, "result=" + result);
			if (!result.equals(resStr)) {
				TestUtil.logErr("expected: " + resStr + ", received: " + result);
				pass = false;
			}

			// non-wrapper style
			logger.log(Level.INFO, "Testing non-wrapper style enableWrapperStyle=false ...");
			HelloRequest3 hello3 = of.createHelloRequest3();
			hello3.setHelloRequest1(reqStr);
			hello3.setHelloRequest2(reqStr2);
			HelloResponse3 h3 = port.hello3(hello3);
			result = h3.getResponse();
			logger.log(Level.INFO, "result=" + result);
			if (!result.equals(resStr)) {
				TestUtil.logErr("expected: " + resStr + ", received: " + result);
				pass = false;
			}

			logger.log(Level.INFO, "Testing Exception Exception Case ...");
			hello3 = of.createHelloRequest3();
			hello3.setHelloRequest1("HelloException");
			hello3.setHelloRequest2(reqStr2);
			try {
				port.hello3(hello3);
				TestUtil.logErr("HelloException expected but not thrown");
				pass = false;
			} catch (HelloException e) {
				logger.log(Level.INFO, "Got expected HelloException");
			}
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}

		if (!pass)
			throw new Exception("CustomizationEmbeddedTest failed");
	}

	/*
	 * @testName: jaxbCustomizationTest
	 *
	 * @assertion_ids: JAXWS:SPEC:8005;
	 *
	 * @test_Strategy: A jaxb customization test
	 *
	 */
	@Test
	public void jaxbCustomizationTest() throws Exception {
		TestUtil.logTrace("jaxbCustomizationTest");
		boolean pass = true;
		String resStr = "FooBarPopeyeOlive";
		EchoRequest echoRequest = new EchoRequest();
		Name[] names = { new Name(), new Name() };
		names[0].setFirst("Foo");
		names[0].setLast("Bar");
		names[1].setFirst("Popeye");
		names[1].setLast("Olive");
		echoRequest.setName(names);
		try {
			logger.log(Level.INFO, "Testing jaxb customization test ...");
			EchoResponse echoResponse = port.echo(echoRequest);
			String result = echoResponse.getReturn();
			if (!result.equals(resStr)) {
				TestUtil.logErr("expected: " + resStr + ", received: " + result);
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			e.printStackTrace();
		}

		if (!pass)
			throw new Exception("jaxbCustomizationTest failed");
	}

}
