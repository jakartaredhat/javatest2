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

package com.sun.ts.tests.jaxws.wsa.w2j.document.literal.action;

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
import com.sun.ts.tests.jaxws.wsa.j2w.document.literal.requestresponse.AddNumbersService;

public class Client extends BaseClient {

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.wsa.w2j.document.literal.action.";

	// URL properties used by the test
	private static final String ENDPOINT_URL = "wsaw2jdlactiontest.endpoint.1";

	private static final String WSDLLOC_URL = "wsaw2jdlactiontest.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	// service and port information
	private static final String NAMESPACEURI = "http://example.com/";

	private static final String SERVICE_NAME = "AddNumbersService";

	private static final String PORT_NAME = "AddNumbersPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	AddNumbersPortType port = null;

	static AddNumbersService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
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
		port = (AddNumbersPortType) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, AddNumbersService.class, PORT_QNAME,
				AddNumbersPortType.class);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (AddNumbersPortType) service.getPort(AddNumbersPortType.class);
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
	 * @testName: testAddNumbersDefaultAddNumbersFaultAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.3; JAXWS:SPEC:2089;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3; JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test default action pattern for WSDL Exception element
	 *
	 */
	@Test
	public void testAddNumbersDefaultAddNumbersFaultAction() throws Exception {
		logger.log(Level.INFO, "testAddNumbersDefaultAddNumbersFaultAction");
		boolean pass = true;
		try {
			port.addNumbers(-10, 10);
			TestUtil.logErr("AddNumbersFault_Exception must be thrown");
			pass = false;
		} catch (AddNumbersFault_Exception ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (TooBigNumbersFault_Exception ex) {
			TestUtil.logErr("Caught unexpected TooBigNumbersFault_Exception");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testAddNumbersDefaultAddNumbersFaultAction failed", ex);
		}
		if (!pass)
			throw new Exception("testAddNumbersDefaultAddNumbersFaultAction failed");
	}

	/*
	 * @testName: testAddNumbersDefaultTooBigNumbersFaultAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.3; JAXWS:SPEC:2089;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3; JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test default action pattern for WSDL Exception element
	 *
	 */
	@Test
	public void testAddNumbersDefaultTooBigNumbersFaultAction() throws Exception {
		logger.log(Level.INFO, "testAddNumbersDefaultTooBigNumbersFaultAction");
		boolean pass = true;
		try {
			port.addNumbers(20, 20);
			TestUtil.logErr("TooBigNumbersFault_Exception must be thrown");
			pass = false;
		} catch (TooBigNumbersFault_Exception ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (AddNumbersFault_Exception ex) {
			TestUtil.logErr("Caught unexpected AddNumbersFault_Exception");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testAddNumbersDefaultTooBigNumbersFaultAction failed", ex);
		}
		if (!pass)
			throw new Exception("testAddNumbersDefaultTooBigNumbersFaultAction failed");
	}

	/*
	 * @testName: testAddNumbers2ExplicitAddNumbers2FaultAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4003; WSAMD:SPEC:4003.3; JAXWS:SPEC:2089;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3; JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test explicit association for WSDL Exception element
	 *
	 */
	@Test
	public void testAddNumbers2ExplicitAddNumbers2FaultAction() throws Exception {
		logger.log(Level.INFO, "testAddNumbers2ExplicitAddNumbers2FaultAction");
		boolean pass = true;
		try {
			port.addNumbers2(-10, 10);
			TestUtil.logErr("AddNumbers2Fault must be thrown");
			pass = false;
		} catch (AddNumbers2Fault ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (TooBigNumbers2Fault ex) {
			TestUtil.logErr("Caught unexpected TooBigNumbers2Fault");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testAddNumbers2ExplicitAddNumbers2FaultAction failed", ex);
		}
		if (!pass)
			throw new Exception("testAddNumbers2ExplicitAddNumbers2FaultAction failed");
	}

	/*
	 * @testName: testAddNumbers2ExplicitTooBigNumbers2FaultAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4003; WSAMD:SPEC:4003.3; JAXWS:SPEC:2089;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3; JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test explicit association for WSDL Exception element
	 *
	 */
	@Test
	public void testAddNumbers2ExplicitTooBigNumbers2FaultAction() throws Exception {
		logger.log(Level.INFO, "testAddNumbers2ExplicitTooBigNumbers2FaultAction");
		boolean pass = true;
		try {
			port.addNumbers2(20, 20);
			TestUtil.logErr("TooBigNumbers2Fault must be thrown");
			pass = false;
		} catch (TooBigNumbers2Fault ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (AddNumbers2Fault ex) {
			TestUtil.logErr("Caught unexpected AddNumbers2Fault");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testAddNumbers2ExplicitTooBigNumbers2FaultAction failed", ex);
		}
		if (!pass)
			throw new Exception("testAddNumbers2ExplicitTooBigNumbers2FaultAction failed");
	}

	/*
	 * @testName: testAddNumbers3ExplicitAddNumbers3FaultAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4003; WSAMD:SPEC:4003.3; JAXWS:SPEC:2089;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3; JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test explicit association for WSDL Exception element
	 *
	 */
	@Test
	public void testAddNumbers3ExplicitAddNumbers3FaultAction() throws Exception {
		logger.log(Level.INFO, "testAddNumbers3ExplicitAddNumbers3FaultAction");
		boolean pass = true;
		try {
			port.addNumbers3(-10, 10);
			TestUtil.logErr("AddNumbers3Fault must be thrown");
			pass = false;
		} catch (AddNumbers3Fault ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (TooBigNumbers3Fault ex) {
			TestUtil.logErr("Caught unexpected TooBigNumbers3Fault");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testAddNumbers3ExplicitAddNumbers3FaultAction failed", ex);
		}
		if (!pass)
			throw new Exception("testAddNumbers3ExplicitAddNumbers3FaultAction failed");
	}

	/*
	 * @testName: testAddNumbers3DefaultTooBigNumbers3FaultAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.3; JAXWS:SPEC:2089;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3; JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test default action pattern for WSDL Exception element
	 *
	 */
	@Test
	public void testAddNumbers3DefaultTooBigNumbers3FaultAction() throws Exception {
		logger.log(Level.INFO, "testAddNumbers3DefaultTooBigNumbers3FaultAction");
		boolean pass = true;
		try {
			port.addNumbers3(20, 20);
			TestUtil.logErr("TooBigNumbers3Fault must be thrown");
			pass = false;
		} catch (TooBigNumbers3Fault ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (AddNumbers3Fault ex) {
			TestUtil.logErr("Caught unexpected AddNumbers3Fault");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testAddNumbers3DefaultTooBigNumbers3FaultAction failed", ex);
		}
		if (!pass)
			throw new Exception("testAddNumbers3DefaultTooBigNumbers3FaultAction failed");
	}

	/*
	 * @testName: testAddNumbers4DefaultAddNumbers4FaultAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.3; JAXWS:SPEC:2089;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3; JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test default action pattern for WSDL Exception element
	 *
	 */
	@Test
	public void testAddNumbers4DefaultAddNumbers4FaultAction() throws Exception {
		logger.log(Level.INFO, "testAddNumbers4DefaultAddNumbers4FaultAction");
		boolean pass = true;
		try {
			port.addNumbers4(-10, 10);
			TestUtil.logErr("AddNumbers4Fault must be thrown");
			pass = false;
		} catch (AddNumbers4Fault ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (TooBigNumbers4Fault ex) {
			TestUtil.logErr("Caught unexpected TooBigNumbers4Fault");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testAddNumbers4DefaultAddNumbers4FaultAction failed", ex);
		}
		if (!pass)
			throw new Exception("testAddNumbers4DefaultAddNumbers4FaultAction failed");
	}

	/*
	 * @testName: testAddNumbers4ExplicitTooBigNumbers4FaultAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4003; WSAMD:SPEC:4003.3; JAXWS:SPEC:2089;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3; JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test explicit association for WSDL Exception element
	 *
	 */
	@Test
	public void testAddNumbers4ExplicitTooBigNumbers4FaultAction() throws Exception {
		logger.log(Level.INFO, "testAddNumbers4ExplicitTooBigNumbers4FaultAction");
		boolean pass = true;
		try {
			port.addNumbers4(20, 20);
			TestUtil.logErr("TooBigNumbers4Fault must be thrown");
			pass = false;
		} catch (TooBigNumbers4Fault ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (AddNumbers4Fault ex) {
			TestUtil.logErr("Caught unexpected AddNumbers4Fault");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testAddNumbers4ExplicitTooBigNumbers4FaultAction failed", ex);
		}
		if (!pass)
			throw new Exception("testAddNumbers4ExplicitTooBigNumbers4FaultAction failed");
	}

	/*
	 * @testName: testAddNumbers5ExplicitAddNumbers5FaultAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4003; WSAMD:SPEC:4003.3; JAXWS:SPEC:2089;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3; JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test explicit association for WSDL Exception element
	 *
	 */
	@Test
	public void testAddNumbers5ExplicitAddNumbers5FaultAction() throws Exception {
		logger.log(Level.INFO, "testAddNumbers5ExplicitAddNumbers5FaultAction");
		boolean pass = true;
		try {
			port.addNumbers5(-10, 20);
			TestUtil.logErr("AddNumbers5Fault must be thrown");
			pass = false;
		} catch (AddNumbers5Fault ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testAddNumbers5ExplicitAddNumbers5FaultAction failed", ex);
		}
		if (!pass)
			throw new Exception("testAddNumbers5ExplicitAddNumbers5FaultAction failed");
	}

	/*
	 * @testName: testAddNumbers6EmptyAddNumbers6FaultAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.3; JAXWS:SPEC:2089;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3; JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test default association for WSDL Exception element when an
	 * the Action value is empty string ""
	 *
	 */
	@Test
	public void testAddNumbers6EmptyAddNumbers6FaultAction() throws Exception {
		logger.log(Level.INFO, "testAddNumbers6EmptyAddNumbers6FaultAction");
		boolean pass = true;
		try {
			port.addNumbers6(-10, 20);
			TestUtil.logErr("AddNumbers6Fault must be thrown");
			pass = false;
		} catch (AddNumbers6Fault ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testAddNumbers6EmptyAddNumbers6FaultAction failed", ex);
		}
		if (!pass)
			throw new Exception("testAddNumbers6EmptyAddNumbers6FaultAction failed");
	}

	/*
	 * @testName: testDefaultInputOutputActionExplicitMessageNames
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.1; WSAMD:SPEC:4004.2;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3; JAXWS:SPEC:2089; JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test default association for WSDL input/output elements and
	 * explicit message names specified
	 *
	 */
	@Test
	public void testDefaultInputOutputActionExplicitMessageNames() throws Exception {
		logger.log(Level.INFO, "testDefaultInputOutputActionExplicitMessageNames");
		boolean pass = true;
		try {
			port.addNumbers2(10, 10);
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testDefaultInputOutputActionExplicitMessageNames failed", ex);
		}
		if (!pass)
			throw new Exception("testDefaultInputOutputActionExplicitMessageNames failed");
	}

	/*
	 * @testName: testDefaultInputOutputAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.3; JAXWS:SPEC:2089;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3; JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test default association for WSDL input/output elements and
	 * no message names specified
	 *
	 */
	@Test
	public void testDefaultInputOutputAction() throws Exception {
		logger.log(Level.INFO, "testDefaultInputOutputAction");
		boolean pass = true;
		try {
			port.addNumbers3(10, 10);
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testDefaultInputOutputAction failed", ex);
		}
		if (!pass)
			throw new Exception("testDefaultInputOutputAction failed");
	}

	/*
	 * @testName: testEmptyInputOutputAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.3; JAXWS:SPEC:2089;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3; JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test default association for WSDL input/output elements when
	 * the Action value is empty string ""
	 *
	 */
	@Test
	public void testEmptyInputOutputAction() throws Exception {
		logger.log(Level.INFO, "testEmptyInputOutputAction");
		boolean pass = true;
		try {
			port.addNumbers4(10, 10);
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testEmptyInputOutputAction failed", ex);
		}
		if (!pass)
			throw new Exception("testEmptyInputOutputAction failed");
	}

	/*
	 * @testName: testExplicitInputOutputActions
	 *
	 * @assertion_ids: WSAMD:SPEC:4003; WSAMD:SPEC:4003.1; WSAMD:SPEC:4003.2;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3; JAXWS:SPEC:2089; JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test explicit association for WSDL input/output elements
	 *
	 */
	@Test
	public void testExplicitInputOutputActions() throws Exception {
		logger.log(Level.INFO, "testExplicitInputOutputActions");
		boolean pass = true;
		try {
			port.addNumbers5(10, 10);
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testExplicitInputOutputActions failed", ex);
		}
		if (!pass)
			throw new Exception("testExplicitInputOutputActions failed");
	}

	/*
	 * @testName: testExplicitInputDefaultOutputAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4003; WSAMD:SPEC:4003.1; WSAMD:SPEC:4004;
	 * WSAMD:SPEC:4004.2; JAXWS:SPEC:2089; JAXWS:SPEC:7018; JAXWS:SPEC:7018.1;
	 * JAXWS:SPEC:7018.2; JAXWS:SPEC:7017; JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.3;
	 * JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test explicit association for WSDL input element and default
	 * association for WSDL output element
	 *
	 */
	@Test
	public void testExplicitInputDefaultOutputAction() throws Exception {
		logger.log(Level.INFO, "testExplicitInputDefaultOutputAction");
		boolean pass = true;
		try {
			port.addNumbers6(10, 10);
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testExplicitInputDefaultOutputAction failed", ex);
		}
		if (!pass)
			throw new Exception("testExplicitInputDefaultOutputAction failed");
	}
}
