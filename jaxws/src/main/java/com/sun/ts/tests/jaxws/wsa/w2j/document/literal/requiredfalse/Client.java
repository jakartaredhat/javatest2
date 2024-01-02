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

package com.sun.ts.tests.jaxws.wsa.w2j.document.literal.requiredfalse;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.wsa.w2j.document.literal.refps.AddNumbersService;

public class Client extends BaseClient {

	// URL properties used by the test
	private static final String ENDPOINT_URL = "wsaw2jdlrequiredfalsetest.endpoint.1";

	private static final String WSDLLOC_URL = "wsaw2jdlrequiredfalsetest.wsdlloc.1";

	private String url = null;

	// service and port information
	private static final String NAMESPACEURI = "http://example.com/";

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
	 * @testName: testDefaultActions
	 *
	 * @assertion_ids: WSAMD:SPEC:3000; WSAMD:SPEC:3000.1; WSAMD:SPEC:3000.2;
	 * WSAMD:SPEC:3000.3; WSAMD:SPEC:3000.4; WSAMD:SPEC:4004; WSAMD:SPEC:4004.1;
	 * WSAMD:SPEC:4004.2;
	 *
	 * @test_Strategy:
	 *
	 */
	@Test
	public void testDefaultActions() throws Exception {
		logger.log(Level.INFO, "testDefaultActions");
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
			throw new Exception("testDefaultActions failed", e);
		}

		if (!pass)
			throw new Exception("testDefaultActions failed");
	}

	/*
	 * @testName: testActionWithExplicitNames
	 *
	 * @assertion_ids: WSAMD:SPEC:3000; WSAMD:SPEC:3000.1; WSAMD:SPEC:3000.2;
	 * WSAMD:SPEC:3000.3; WSAMD:SPEC:3000.4; WSAMD:SPEC:4004;
	 *
	 * @test_Strategy:
	 *
	 */
	@Test
	public void testActionWithExplicitNames() throws Exception {
		logger.log(Level.INFO, "testActionWithExplicitNames");
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
			throw new Exception("testActionWithExplicitNames failed", e);
		}

		if (!pass)
			throw new Exception("testActionWithExplicitNames failed");
	}

	/*
	 * @testName: testActionWithInputNameOnly
	 *
	 * @assertion_ids: WSAMD:SPEC:3000; WSAMD:SPEC:3000.1; WSAMD:SPEC:3000.2;
	 * WSAMD:SPEC:3000.3; WSAMD:SPEC:3000.4; WSAMD:SPEC:4004; WSAMD:SPEC:4004.2;
	 *
	 * @test_Strategy:
	 *
	 */
	@Test
	public void testActionWithInputNameOnly() throws Exception {
		logger.log(Level.INFO, "testActionWithInputNameOnly");
		boolean pass = true;

		try {
			int result = port.addNumbers3(10, 10);
			if (result != 20) {
				TestUtil.logErr("result mismatch, expected 20, received " + result);
				pass = false;
			} else
				logger.log(Level.INFO, "result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testActionWithInputNameOnly failed", e);
		}

		if (!pass)
			throw new Exception("testActionWithInputNameOnly failed");
	}

	/*
	 * @testName: testActionWithOutputNameOnly
	 *
	 * @assertion_ids: WSAMD:SPEC:3000; WSAMD:SPEC:3000.1; WSAMD:SPEC:3000.2;
	 * WSAMD:SPEC:3000.3; WSAMD:SPEC:3000.4; WSAMD:SPEC:4004; WSAMD:SPEC:4004.1;
	 *
	 * @test_Strategy:
	 *
	 */
	@Test
	public void testActionWithOutputNameOnly() throws Exception {
		logger.log(Level.INFO, "testActionWithOutputNameOnly");
		boolean pass = true;

		try {
			int result = port.addNumbers4(10, 10);
			if (result != 20) {
				TestUtil.logErr("result mismatch, expected 20, received " + result);
				pass = false;
			} else
				logger.log(Level.INFO, "result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testActionWithOutputNameOnly failed", e);
		}

		if (!pass)
			throw new Exception("testActionWithOutputNameOnly failed");
	}

	/*
	 * @testName: testExplicitActionsBoth
	 *
	 * @assertion_ids: WSAMD:SPEC:3000; WSAMD:SPEC:3000.1; WSAMD:SPEC:3000.2;
	 * WSAMD:SPEC:3000.3; WSAMD:SPEC:3000.4; WSAMD:SPEC:4003; WSAMD:SPEC:4003.1;
	 * WSAMD:SPEC:4003.2;
	 *
	 * @test_Strategy:
	 *
	 */
	@Test
	public void testExplicitActionsBoth() throws Exception {
		logger.log(Level.INFO, "testExplicitActionsBoth");
		boolean pass = true;

		try {
			int result = port.addNumbers5(10, 10);
			if (result != 20) {
				TestUtil.logErr("result mismatch, expected 20, received " + result);
				pass = false;
			} else
				logger.log(Level.INFO, "result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testExplicitActionsBoth failed", e);
		}

		if (!pass)
			throw new Exception("testExplicitActionsBoth failed");
	}

	/*
	 * @testName: testExplicitActionsInputOnly
	 *
	 * @assertion_ids: WSAMD:SPEC:3000; WSAMD:SPEC:3000.1; WSAMD:SPEC:3000.2;
	 * WSAMD:SPEC:3000.3; WSAMD:SPEC:3000.4; WSAMD:SPEC:4000; WSAMD:SPEC:4003;
	 * WSAMD:SPEC:4003.1;
	 *
	 * @test_Strategy:
	 *
	 */
	@Test
	public void testExplicitActionsInputOnly() throws Exception {
		logger.log(Level.INFO, "testExplicitActionsInputOnly");
		boolean pass = true;

		try {
			int result = port.addNumbers6(10, 10);
			if (result != 20) {
				TestUtil.logErr("result mismatch, expected 20, received " + result);
				pass = false;
			} else
				logger.log(Level.INFO, "result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testExplicitActionsInputOnly failed", e);
		}

		if (!pass)
			throw new Exception("testExplicitActionsInputOnly failed");
	}

	/*
	 * @testName: testExplicitActionsOutputOnly
	 *
	 * @assertion_ids: WSAMD:SPEC:3000; WSAMD:SPEC:3000.1; WSAMD:SPEC:3000.2;
	 * WSAMD:SPEC:3000.3; WSAMD:SPEC:3000.4; WSAMD:SPEC:4000; WSAMD:SPEC:4003;
	 * WSAMD:SPEC:4003.2;
	 *
	 * @test_Strategy:
	 *
	 */
	@Test
	public void testExplicitActionsOutputOnly() throws Exception {
		logger.log(Level.INFO, "testExplicitActionsOutputOnly");
		boolean pass = true;

		try {
			int result = port.addNumbers7(10, 10);
			if (result != 20) {
				TestUtil.logErr("result mismatch, expected 20, received " + result);
				pass = false;
			} else
				logger.log(Level.INFO, "result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testExplicitActionsOutputOnly failed", e);
		}

		if (!pass)
			throw new Exception("testExplicitActionsOutputOnly failed");
	}

	/*
	 * @testName: TestEmptyActions
	 *
	 * @assertion_ids: WSAMD:SPEC:3000; WSAMD:SPEC:3000.1; WSAMD:SPEC:3000.2;
	 * WSAMD:SPEC:3000.3; WSAMD:SPEC:3000.4; WSAMD:SPEC:4004; WSAMD:SPEC:4003;
	 * WSAMD:SPEC:4003.1; WSAMD:SPEC:4003.2;
	 *
	 * @test_Strategy:
	 *
	 */
	@Test
	public void TestEmptyActions() throws Exception {
		logger.log(Level.INFO, "TestEmptyActions");
		boolean pass = true;

		try {
			int result = port.addNumbers8(10, 10);
			if (result != 20) {
				TestUtil.logErr("result mismatch, expected 20, received " + result);
				pass = false;
			} else
				logger.log(Level.INFO, "result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("TestEmptyActions failed", e);
		}

		if (!pass)
			throw new Exception("TestEmptyActions failed");
	}
}
