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

package com.sun.ts.tests.jaxws.wsa.j2w.document.literal.action;

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

import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.soap.AddressingFeature;
import jakarta.xml.ws.soap.SOAPFaultException;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.wsa.j2w.document.literal.action.";

	// URL properties used by the test
	private static final String ENDPOINT_URL = "wsaj2wdlactiontest.endpoint.1";

	private static final String WSDLLOC_URL = "wsaj2wdlactiontest.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	// service and port information
	private static final String NAMESPACEURI = "http://foobar.org/";

	private static final String SERVICE_NAME = "AddNumbersService";

	private static final String PORT_NAME = "AddNumbersPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private WebServiceFeature[] enabledRequiredwsf = { new AddressingFeature(true, true) };

	private WebServiceFeature[] disabledNotRequiredwsf = { new AddressingFeature(false, false) };

	AddNumbers portEnabled = null;

	AddNumbers portDisabled = null;

	static AddNumbersService service = null;

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
		logger.log(Level.INFO, "Obtain port");
		portEnabled = (AddNumbers) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, AddNumbersService.class, PORT_QNAME,
				AddNumbers.class, enabledRequiredwsf);
		portDisabled = (AddNumbers) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, AddNumbersService.class, PORT_QNAME,
				AddNumbers.class, disabledNotRequiredwsf);
		JAXWS_Util.setTargetEndpointAddress(portEnabled, url);
		JAXWS_Util.setTargetEndpointAddress(portDisabled, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		logger.log(Level.INFO, "Obtain port");
		portEnabled = (AddNumbers) service.getPort(AddNumbers.class, enabledRequiredwsf);
		portDisabled = (AddNumbers) service.getPort(AddNumbers.class, disabledNotRequiredwsf);
		JAXWS_Util.dumpTargetEndpointAddress(portEnabled);
		JAXWS_Util.dumpTargetEndpointAddress(portDisabled);
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
	 * @testName: testNoActionOnInputOutput
	 *
	 * @assertion_ids: JAXWS:SPEC:7017; JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.2;
	 * JAXWS:SPEC:7017.3; JAXWS:SPEC:10025; JAXWS:SPEC:10026; WSAMD:SPEC:4004;
	 * WSAMD:SPEC:4004.1; WSAMD:SPEC:4004.2; WSAMD:SPEC:4004.4; WSAMD:SPEC:4004.5;
	 * JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Test no action for input/output elements.
	 *
	 */
	@Test
	public void testNoActionOnInputOutput() throws Exception {
		logger.log(Level.INFO, "testNoActionOnInputOutput ");
		boolean pass = true;
		try {
			int result = portEnabled.addNumbersNoAction(10, 10);
			logger.log(Level.INFO, "WSA:Action headers are correct");
			if (result != 20) {
				TestUtil.logErr("Expected result=20, got result=" + result);
				pass = false;
			}
		} catch (AddNumbersException ex) {
			TestUtil.logErr("Caught unexpected AddNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testNoActionOnInputOutput  failed", ex);
		}
		if (!pass)
			throw new Exception("testNoActionOnInputOutput  failed");
	}

	/*
	 * @testName: testEmptyActionOnInputOutput
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.1; WSAMD:SPEC:4004.2;
	 * WSAMD:SPEC:4004.4; WSAMD:SPEC:4004.5; JAXWS:SPEC:3055; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.2; JAXWS:SPEC:7017.3; JAXWS:SPEC:10025;
	 * JAXWS:SPEC:10026;
	 *
	 * @test_Strategy: Test default action for WSDL input/output elements and no
	 * explicit message names
	 *
	 */
	@Test
	public void testEmptyActionOnInputOutput() throws Exception {
		logger.log(Level.INFO, "testEmptyActionOnInputOutput");
		boolean pass = true;
		try {
			int result = portEnabled.addNumbersEmptyAction(10, 10);
			logger.log(Level.INFO, "WSA:Action headers are correct");
			if (result != 20) {
				TestUtil.logErr("Expected result=20, got result=" + result);
				pass = false;
			}
		} catch (AddNumbersException ex) {
			TestUtil.logErr("Caught unexpected AddNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testEmptyActionOnInputOutput failed", ex);
		}
		if (!pass)
			throw new Exception("testEmptyActionOnInputOutput failed");
	}

	/*
	 * @testName: testExplicitInputOutputActions1
	 *
	 * @assertion_ids: JAXWS:SPEC:7017; JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.2;
	 * JAXWS:SPEC:7017.3; JAXWS:SPEC:10025; JAXWS:SPEC:10026; WSAMD:SPEC:4003;
	 * WSAMD:SPEC:4003.1; WSAMD:SPEC:4003.2; JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Test explicit action for input/output elements
	 *
	 */
	@Test
	public void testExplicitInputOutputActions1() throws Exception {
		logger.log(Level.INFO, "testExplicitInputOutputActions1");
		boolean pass = true;
		try {
			int result = portEnabled.addNumbers(10, 10);
			logger.log(Level.INFO, "WSA:Action headers are correct");
			if (result != 20) {
				TestUtil.logErr("Expected result=20, got result=" + result);
				pass = false;
			}
		} catch (AddNumbersException ex) {
			TestUtil.logErr("Caught unexpected AddNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testExplicitInputOutputActions1 failed", ex);
		}
		if (!pass)
			throw new Exception("testExplicitInputOutputActions1 failed");
	}

	/*
	 * @testName: testExplicitInputOutputActions2
	 *
	 * @assertion_ids: JAXWS:SPEC:7017; JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.2;
	 * JAXWS:SPEC:7017.3; JAXWS:SPEC:10025; JAXWS:SPEC:10026; WSAMD:SPEC:4003;
	 * WSAMD:SPEC:4003.1; WSAMD:SPEC:4003.2; JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Test explicit action for input/output elements
	 *
	 */
	@Test
	public void testExplicitInputOutputActions2() throws Exception {
		logger.log(Level.INFO, "testExplicitInputOutputActions2");
		boolean pass = true;
		try {
			int result = portEnabled.addNumbers2(10, 10);
			logger.log(Level.INFO, "WSA:Action headers are correct");
			if (result != 20) {
				TestUtil.logErr("Expected result=20, got result=" + result);
				pass = false;
			}
		} catch (AddNumbersException ex) {
			TestUtil.logErr("Caught unexpected AddNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testExplicitInputOutputActions2 failed", ex);
		}
		if (!pass)
			throw new Exception("testExplicitInputOutputActions2 failed");
	}

	/*
	 * @testName: testDefaultOutputActionExplicitInputAction
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.2; WSAMD:SPEC:4003;
	 * WSAMD:SPEC:4003.1; WSAMD:SPEC:4004.5; JAXWS:SPEC:3055; JAXWS:SPEC:7017;
	 * JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.2; JAXWS:SPEC:7017.3; JAXWS:SPEC:10025;
	 * JAXWS:SPEC:10026;
	 *
	 * @test_Strategy: Test default action for WSDL output element Test explicit
	 * action for WSDL input element
	 *
	 */
	@Test
	public void testDefaultOutputActionExplicitInputAction() throws Exception {
		logger.log(Level.INFO, "testDefaultOutputActionExplicitInputAction");
		boolean pass = true;
		try {
			int result = portEnabled.addNumbers3(10, 10);
			logger.log(Level.INFO, "WSA:Action headers are correct");
			if (result != 20) {
				TestUtil.logErr("Expected result=20, got result=" + result);
				pass = false;
			}
		} catch (AddNumbersException ex) {
			TestUtil.logErr("Caught unexpected AddNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testDefaultOutputActionExplicitInputAction failed", ex);
		}
		if (!pass)
			throw new Exception("testDefaultOutputActionExplicitInputAction failed");
	}

	/*
	 * @testName: testSendingWrongSOAPActionHTTPHeaderValue
	 *
	 * @assertion_ids: JAXWS:SPEC:7017; JAXWS:SPEC:7017.1; JAXWS:SPEC:7017.2;
	 * JAXWS:SPEC:7017.3; JAXWS:SPEC:10025; JAXWS:SPEC:10026; WSAMD:SPEC:4003;
	 * WSAMD:SPEC:4003.1; WSAMD:SPEC:4003.2; JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Test sedning wrong SOAPAction HTTP Value for operation with
	 * explicit input/output action elements
	 * 
	 */
	@Test
	public void testSendingWrongSOAPActionHTTPHeaderValue() throws Exception {
		logger.log(Level.INFO, "testSendingWrongSOAPActionHTTPHeaderValue");
		boolean pass = true;
		try {
			int result = portEnabled.addNumbers4(10, 10);
			logger.log(Level.INFO, "WSA:Action headers are correct");
			if (result != 20) {
				TestUtil.logErr("Expected result=20, got result=" + result);
				pass = false;
			}
		} catch (SOAPFaultException ex) {
			logger.log(Level.INFO, "Caught expected SOAPFaultException");
			String detailName = null;
			try {
				detailName = ex.getFault().getDetail().getFirstChild().getLocalName();
			} catch (Exception e) {
			}
			if (detailName != null)
				logger.log(Level.INFO, "DetailName = " + detailName);
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testSendingWrongSOAPActionHTTPHeaderValue failed", ex);
		}
		if (!pass)
			throw new Exception("testSendingWrongSOAPActionHTTPHeaderValue failed");
	}

	/*
	 * @testName: testOneFaultExplicitAction
	 *
	 * @assertion_ids: JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2;
	 * JAXWS:SPEC:10025; JAXWS:SPEC:10026; JAXWS:JAVADOC:131; JAXWS:JAVADOC:132;
	 * JAXWS:JAVADOC:143; JAXWS:JAVADOC:144; WSAMD:SPEC:4003; WSAMD:SPEC:4003.3;
	 * JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Test explicit action for Exception element
	 *
	 */
	@Test
	public void testOneFaultExplicitAction() throws Exception {
		logger.log(Level.INFO, "testOneFaultExplicitAction");
		boolean pass = true;
		try {
			portEnabled.addNumbersFault1(-10, 10);
		} catch (AddNumbersException ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testOneFaultExplicitAction failed", ex);
		}
		if (!pass)
			throw new Exception("testOneFaultExplicitAction failed");
	}

	/*
	 * @testName: testTwoFaultsExplicitAction1
	 *
	 * @assertion_ids: JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2;
	 * JAXWS:SPEC:10025; JAXWS:SPEC:10026; JAXWS:JAVADOC:131; JAXWS:JAVADOC:132;
	 * JAXWS:JAVADOC:143; JAXWS:JAVADOC:144; WSAMD:SPEC:4003; WSAMD:SPEC:4003.3;
	 * JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Test explicit action for Exception element
	 *
	 */
	@Test
	public void testTwoFaultsExplicitAction1() throws Exception {
		logger.log(Level.INFO, "testTwoFaultsExplicitAction1");
		boolean pass = true;
		try {
			portEnabled.addNumbersFault2(-10, 10);
		} catch (AddNumbersException ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (TooBigNumbersException ex) {
			TestUtil.logErr("Caught unexpected TooBigNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testTwoFaultsExplicitAction1 failed", ex);
		}
		if (!pass)
			throw new Exception("testTwoFaultsExplicitAction1 failed");
	}

	/*
	 * @testName: testTwoFaultsExplicitAction2
	 *
	 * @assertion_ids: JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2;
	 * JAXWS:SPEC:10025; JAXWS:SPEC:10026; JAXWS:JAVADOC:131; JAXWS:JAVADOC:132;
	 * JAXWS:JAVADOC:143; JAXWS:JAVADOC:144; WSAMD:SPEC:4003; WSAMD:SPEC:4003.3;
	 * JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Test explicit action for Exception element
	 *
	 */
	@Test
	public void testTwoFaultsExplicitAction2() throws Exception {
		logger.log(Level.INFO, "testTwoFaultsExplicitAction2");
		boolean pass = true;
		try {
			portEnabled.addNumbersFault2(20, 10);
		} catch (TooBigNumbersException ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (AddNumbersException ex) {
			TestUtil.logErr("Caught unexpected AddNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("WSA:Action headers are incorrect");
			throw new Exception("testTwoFaultsExplicitAction2 failed", ex);
		}
		if (!pass)
			throw new Exception("testTwoFaultsExplicitAction2 failed");
	}

	/*
	 * @testName: testTwoFaultsExplicitAddNumbersFault3
	 *
	 * @assertion_ids: JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2;
	 * JAXWS:SPEC:10025; JAXWS:SPEC:10026; JAXWS:JAVADOC:131; JAXWS:JAVADOC:132;
	 * JAXWS:JAVADOC:143; JAXWS:JAVADOC:144; WSAMD:SPEC:4003; WSAMD:SPEC:4003.3;
	 * JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Test explicit action for Exception element
	 *
	 */
	@Test
	public void testTwoFaultsExplicitAddNumbersFault3() throws Exception {
		logger.log(Level.INFO, "testTwoFaultsExplicitAddNumbersFault3");
		boolean pass = true;
		try {
			portEnabled.addNumbersFault3(-10, 10);
		} catch (AddNumbersException ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (TooBigNumbersException ex) {
			TestUtil.logErr("Caught unexpected TooBigNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testTwoFaultsExplicitAddNumbersFault3 failed", ex);
		}
		if (!pass)
			throw new Exception("testTwoFaultsExplicitAddNumbersFault3 failed");
	}

	/*
	 * @testName: testTwoFaultsDefaultTooBigNumbersFault3
	 *
	 * @assertion_ids: JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2;
	 * JAXWS:SPEC:10025; JAXWS:SPEC:10026; JAXWS:JAVADOC:131; JAXWS:JAVADOC:132;
	 * JAXWS:JAVADOC:143; JAXWS:JAVADOC:144; WSAMD:SPEC:4003; WSAMD:SPEC:4003.3;
	 * JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Test no action for Exception element
	 *
	 */
	@Test
	public void testTwoFaultsDefaultTooBigNumbersFault3() throws Exception {
		logger.log(Level.INFO, "testTwoFaultsDefaultTooBigNumbersFault3");
		boolean pass = true;
		try {
			portEnabled.addNumbersFault3(20, 10);
		} catch (TooBigNumbersException ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (AddNumbersException ex) {
			TestUtil.logErr("Caught unexpected AddNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testTwoFaultsDefaultTooBigNumbersFault3 failed", ex);
		}
		if (!pass)
			throw new Exception("testTwoFaultsDefaultTooBigNumbersFault3 failed");
	}

	/*
	 * @testName: testTwoFaultsExplicitAddNumbersFault4
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.3; JAXWS:SPEC:3055;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:10025;
	 * JAXWS:SPEC:10026;
	 *
	 * @test_Strategy: Test explicit action for WSDL Exception element
	 *
	 */
	@Test
	public void testTwoFaultsExplicitAddNumbersFault4() throws Exception {
		logger.log(Level.INFO, "testTwoFaultsExplicitAddNumbersFault4");
		boolean pass = true;
		try {
			portEnabled.addNumbersFault4(-10, 10);
		} catch (AddNumbersException ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (TooBigNumbersException ex) {
			TestUtil.logErr("Caught unexpected TooBigNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testTwoFaultsExplicitAddNumbersFault4 failed", ex);
		}
		if (!pass)
			throw new Exception("testTwoFaultsExplicitAddNumbersFault4 failed");
	}

	/*
	 * @testName: testTwoFaultsDefaultTooBigNumbersFault4
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.3; JAXWS:SPEC:3055;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:10025;
	 * JAXWS:SPEC:10026;
	 *
	 * @test_Strategy: Test default action for WSDL Exception element
	 *
	 */
	@Test
	public void testTwoFaultsDefaultTooBigNumbersFault4() throws Exception {
		logger.log(Level.INFO, "testTwoFaultsDefaultTooBigNumbersFault4");
		boolean pass = true;
		try {
			portEnabled.addNumbersFault4(20, 10);
		} catch (TooBigNumbersException ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (AddNumbersException ex) {
			TestUtil.logErr("Caught unexpected AddNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testTwoFaultsDefaultTooBigNumbersFault4 failed", ex);
		}
		if (!pass)
			throw new Exception("testTwoFaultsDefaultTooBigNumbersFault4 failed");
	}

	/*
	 * @testName: testTwoFaultsDefaultAddNumbersFault5
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.3; JAXWS:SPEC:3055;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:10025;
	 * JAXWS:SPEC:10026;
	 *
	 * @test_Strategy: Test default action for WSDL Exception element
	 *
	 */
	@Test
	public void testTwoFaultsDefaultAddNumbersFault5() throws Exception {
		logger.log(Level.INFO, "testTwoFaultsDefaultAddNumbersFault5");
		boolean pass = true;
		try {
			portEnabled.addNumbersFault5(-10, 10);
		} catch (AddNumbersException ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (TooBigNumbersException ex) {
			TestUtil.logErr("Caught unexpected TooBigNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testTwoFaultsDefaultAddNumbersFault5 failed", ex);
		}
		if (!pass)
			throw new Exception("testTwoFaultsDefaultAddNumbersFault5 failed");
	}

	/*
	 * @testName: testTwoFaultsExplicitTooBigNumbersFault5
	 *
	 * @assertion_ids: WSAMD:SPEC:4003; WSAMD:SPEC:4003.3; JAXWS:JAVADOC:143;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:10025;
	 * JAXWS:SPEC:10026; JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Test explicit action for WSDL Exception element
	 *
	 */
	@Test
	public void testTwoFaultsExplicitTooBigNumbersFault5() throws Exception {
		logger.log(Level.INFO, "testTwoFaultsExplicitTooBigNumbersFault5");
		boolean pass = true;
		try {
			portEnabled.addNumbersFault5(20, 10);
		} catch (TooBigNumbersException ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (AddNumbersException ex) {
			TestUtil.logErr("Caught unexpected AddNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testTwoFaultsExplicitTooBigNumbersFault5 failed", ex);
		}
		if (!pass)
			throw new Exception("testTwoFaultsExplicitTooBigNumbersFault5 failed");
	}

	/*
	 * @testName: testOnlyFaultActionsBothExplicit1
	 *
	 * @assertion_ids: WSAMD:SPEC:4003; WSAMD:SPEC:4003.3; JAXWS:JAVADOC:143;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:10025;
	 * JAXWS:SPEC:10026; JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Test explicit action for WSDL Exception element
	 *
	 */
	@Test
	public void testOnlyFaultActionsBothExplicit1() throws Exception {
		logger.log(Level.INFO, "testOnlyFaultActionsBothExplicit1");
		boolean pass = true;
		try {
			portEnabled.addNumbersFault6(-10, 10);
		} catch (AddNumbersException ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (TooBigNumbersException ex) {
			TestUtil.logErr("Caught unexpected TooBigNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testOnlyFaultActionsBothExplicit1 failed", ex);
		}
		if (!pass)
			throw new Exception("testOnlyFaultActionsBothExplicit1 failed");
	}

	/*
	 * @testName: testOnlyFaultActionsBothExplicit2
	 *
	 * @assertion_ids: WSAMD:SPEC:4003; WSAMD:SPEC:4003.3; JAXWS:JAVADOC:143;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:10025;
	 * JAXWS:SPEC:10026; JAXWS:SPEC:3055;
	 *
	 * @test_Strategy: Test explicit action for WSDL Exception element
	 *
	 */
	@Test
	public void testOnlyFaultActionsBothExplicit2() throws Exception {
		logger.log(Level.INFO, "testOnlyFaultActionsBothExplicit2");
		boolean pass = true;
		try {
			portEnabled.addNumbersFault6(20, 10);
		} catch (TooBigNumbersException ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (AddNumbersException ex) {
			TestUtil.logErr("Caught unexpected AddNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testOnlyFaultActionsBothExplicit2 failed", ex);
		}
		if (!pass)
			throw new Exception("testOnlyFaultActionsBothExplicit2 failed");
	}

	/*
	 * @testName: testOnlyFaultActionsFault7BothEmpty1
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.3; JAXWS:SPEC:3055;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:10025;
	 * JAXWS:SPEC:10026;
	 *
	 * @test_Strategy: Test default action for WSDL Exception element
	 *
	 */
	@Test
	public void testOnlyFaultActionsFault7BothEmpty1() throws Exception {
		logger.log(Level.INFO, "testOnlyFaultActionsFault7BothEmpty1");
		boolean pass = true;
		try {
			portEnabled.addNumbersFault7(-10, 10);
		} catch (AddNumbersException ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (TooBigNumbersException ex) {
			TestUtil.logErr("Caught unexpected TooBigNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testOnlyFaultActionsFault7BothEmpty1 failed", ex);
		}
		if (!pass)
			throw new Exception("testOnlyFaultActionsFault7BothEmpty1 failed");
	}

	/*
	 * @testName: testOnlyFaultActionsFault7BothEmpty2
	 *
	 * @assertion_ids: WSAMD:SPEC:4004; WSAMD:SPEC:4004.3; JAXWS:SPEC:3055;
	 * JAXWS:SPEC:7018; JAXWS:SPEC:7018.1; JAXWS:SPEC:7018.2; JAXWS:SPEC:10025;
	 * JAXWS:SPEC:10026;
	 *
	 * @test_Strategy: Test default action for WSDL Exception element
	 *
	 */
	@Test
	public void testOnlyFaultActionsFault7BothEmpty2() throws Exception {
		logger.log(Level.INFO, "testOnlyFaultActionsFault7BothEmpty2");
		boolean pass = true;
		try {
			portEnabled.addNumbersFault7(20, 10);
		} catch (TooBigNumbersException ex) {
			logger.log(Level.INFO, "WSA:Action headers are correct");
		} catch (AddNumbersException ex) {
			TestUtil.logErr("Caught unexpected AddNumbersException");
			pass = false;
		} catch (Exception ex) {
			TestUtil.logErr("Caught unexpected Exception " + ex.getMessage());
			throw new Exception("testOnlyFaultActionsFault7BothEmpty2 failed", ex);
		}
		if (!pass)
			throw new Exception("testOnlyFaultActionsFault7BothEmpty2 failed");
	}
}
