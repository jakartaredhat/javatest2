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

package com.sun.ts.tests.jaxws.ee.j2w.document.literal.webmethod.client;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;

import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.j2w.document.literal.webmethod.client.";

	// URL properties used by the test
	private static final String ENDPOINT_URL = "j2wdlwebmethod.endpoint.1";

	private static final String WSDLLOC_URL = "j2wdlwebmethod.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	// ServiceName and PortName mapping configuration going java-to-wsdl
	private static final String SERVICE_NAME = "TestService";

	private static final String PORT_NAME = "TestPort";

	private static final String NAMESPACEURI = "http://test.org/wsdl";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	Test port = null;

	static TestService service = null;

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		service = (TestService) getSharedObject();
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
		port = (Test) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, TestService.class, PORT_QNAME, Test.class);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (Test) service.getPort(Test.class);
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "Obtained port");
		getTargetEndpointAddress(port);
		// JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	private void getTargetEndpointAddress(Object port) throws Exception {
		logger.log(Level.INFO, "Get Target Endpoint Address for port=" + port);
		String url = JAXWS_Util.getTargetEndpointAddress(port);
		logger.log(Level.INFO, "Target Endpoint Address=" + url);
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

	private void assertEquals(String s1, String s2, String m) throws Exception {
		logger.log(Level.INFO, "assert method exists for " + m);
		if (!s1.equals(s2))
			throw new Exception("" + m);
		else
			logger.log(Level.INFO, "method exists for " + m);
	}

	private void assertMethodNonExistant(String m) throws Exception {
		logger.log(Level.INFO, "assert method does not exist for " + m);
		try {
			port.getClass().getMethod(m, new Class[] { String.class });
			throw new Exception("" + m + " should not be a Web Method");
		} catch (NoSuchMethodException e) {
			logger.log(Level.INFO, "method does not exist for " + m);
		}
	}

	/*
	 * @testName: webMethodTestMapping
	 *
	 * @assertion_ids: JAXWS:SPEC:3010; JAXWS:SPEC:3011; JAXWS:SPEC:3006;
	 * JAXWS:SPEC:3036;
	 *
	 * @test_Strategy:
	 *
	 * Description
	 */
	@org.junit.jupiter.api.Test
	public void webMethodTestMapping() throws Exception {
		logger.log(Level.INFO, "webMethodTestMapping");
		boolean pass = true;

		try {
			// Methods (method1,method3,method4,method5,overridetoString) should exist
			assertEquals("foo", port.method1("foo"), "webMethodTestMapping: port.method1");
			assertEquals("foo", port.method3("foo"), "webMethodTestMapping: port.method3");
			assertEquals("foo", port.method4("foo"), "webMethodTestMapping: port.method4");
			assertEquals("foo", port.method5("foo"), "webMethodTestMapping: port.method5");
			assertEquals("TestImplBaseBase", port.overridetoString(), "webMethodTestMapping: port.overridetoString");

			// Methods (method2,method6-9) should not exist
			assertMethodNonExistant("webMethodTestMapping: port.method2");
			assertMethodNonExistant("webMethodTestMapping: port.method6");
			assertMethodNonExistant("webMethodTestMapping: port.method7");
			assertMethodNonExistant("webMethodTestMapping: port.method8");
			assertMethodNonExistant("webMethodTestMapping: port.method9");
			assertMethodNonExistant("webMethodTestMapping: port.method10");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("webMethodTestMapping failed", e);
		}
	}

}
