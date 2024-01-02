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
 * $Id: Client.java 52501 2007-01-24 02:29:49Z lschwenk $
 */

package com.sun.ts.tests.jaxws.wsa.w2j.document.literal.delimiter;

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

public class Client extends BaseClient {

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.wsa.w2j.document.literal.delimiter.";

	// URL properties used by the test
	private static final String ENDPOINT_URL = "wsaw2jdldelimitertest.endpoint.1";

	private static final String WSDLLOC_URL = "wsaw2jdldelimitertest.wsdlloc.1";

	private String url = null;

	// service and port information
	private static final String NAMESPACEURI = "urn:example.com";

	private static final String SERVICE_NAME = "AddNumbersService";

	private static final String PORT_NAME = "AddNumbersPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private URL wsdlurl = null;

	AddNumbersPortType port = null;

	static AddNumbersService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

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
		port = (AddNumbersPortType) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, AddNumbersService.class, PORT_QNAME,
				AddNumbersPortType.class);
		logger.log(Level.INFO, "port=" + port);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (AddNumbersPortType) service.getAddNumbersPort();
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "Obtained port");
		JAXWS_Util.dumpTargetEndpointAddress(port);
	}

	protected void getService() {
		service = (AddNumbersService) getSharedObject();
	}

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
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
	 * @testName: testURNDefaultInputOutputActions
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.1; WSAMD:SPEC:4004.2;
	 *
	 * @test_Strategy: Test default action pattern for WSDL input/output with URN
	 * targetNamespace
	 *
	 */
	@Test
	public void testURNDefaultInputOutputActions() throws Exception {
		logger.log(Level.INFO, "testURNDefaultInputOutputActions");
		boolean pass = true;

		try {
			int result = port.addNumbers(10, 10);
			if (result != 20) {
				TestUtil.logErr("result mismatch, expected 20, received " + result);
				pass = false;
			} else
				logger.log(Level.INFO, "result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testURNDefaultInputOutputActions failed", e);
		}

		if (!pass)
			throw new Exception("testURNDefaultInputOutputActions failed");
	}

	/*
	 * @testName: testURNDefaultFaultAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.3;
	 *
	 * @test_Strategy: Test default action pattern for WSDL Exception with URN
	 * targetNamespace
	 *
	 */
	@Test
	public void testURNDefaultFaultAction() throws Exception {
		logger.log(Level.INFO, "testURNDefaultFaultAction");
		boolean pass = true;

		try {
			port.addNumbers(-10, 10);
			TestUtil.logErr("AddNumbersFault_Exception must be thrown");
			pass = false;
		} catch (AddNumbersFault_Exception ex) {
			logger.log(Level.INFO, "AddNumbersFault_Exception was thrown as expected");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testURNDefaultFaultAction failed", e);
		}

		if (!pass)
			throw new Exception("testURNDefaultAddFaultAction failed");
	}

	/*
	 * @testName: testURNExplicitInputOutputActions
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4003; WSAMD:SPEC:4003.1;
	 * WSAMD:SPEC:4003.1; JAXWS:SPEC:2077; JAXWS:SPEC:2078; JAXWS:SPEC:2079;
	 *
	 * @test_Strategy: Test explicit association for WSDL input/output with URN
	 * targetNamespace
	 *
	 */
	@Test
	public void testURNExplicitInputOutputActions() throws Exception {
		logger.log(Level.INFO, "testURNExplicitInputOutputActions");
		boolean pass = true;

		try {
			int result = port.addNumbers2(10, 10);
			if (result != 20) {
				TestUtil.logErr("result mismatch, expected 20, received " + result);
				pass = false;
			} else
				logger.log(Level.INFO, "result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testURNExplicitInputOutputActions failed", e);
		}

		if (!pass)
			throw new Exception("testURNExplicitInputOutputActions failed");
	}

	/*
	 * @testName: testURNExplicitFaultAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4003; WSAMD:SPEC:4003.3;
	 * JAXWS:SPEC:2080; JAXWS:SPEC:2081; JAXWS:SPEC:2082; JAXWS:SPEC:2083;
	 *
	 * @test_Strategy: Test explicit association for WSDL Exception with URN
	 * targetNamespace
	 *
	 */
	@Test
	public void testURNExplicitFaultAction() throws Exception {
		logger.log(Level.INFO, "testURNExplicitFaultAction");
		boolean pass = true;

		try {
			int result = port.addNumbers2(-10, 10);
			TestUtil.logErr("AddNumbersFault_Exception must be thrown");
			pass = false;
		} catch (AddNumbersFault_Exception ex) {
			logger.log(Level.INFO, "AddNumbersFault_Exception was thrown as expected");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testURNExplicitFaultAction failed", e);
		}

		if (!pass)
			throw new Exception("testURNExplicitFaultAction failed");
	}
}
