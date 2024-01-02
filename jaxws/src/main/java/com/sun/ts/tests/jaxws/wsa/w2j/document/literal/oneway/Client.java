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

package com.sun.ts.tests.jaxws.wsa.w2j.document.literal.oneway;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.text.MessageFormat;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;

import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.soap.SOAPFaultException;

public class Client extends BaseClient {

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.wsa.w2j.document.literal.oneway.";

	// URL properties used by the test
	private static final String ENDPOINT_URL = "wsaw2jdlonewaytest.endpoint.1";

	private static final String WSDLLOC_URL = "wsaw2jdlonewaytest.wsdlloc.1";

	private String url = null;

	// service and port information
	private static final String NAMESPACEURI = "http://example.com";

	private static final String SERVICE_NAME = "AddNumbersService";

	private static final String PORT_NAME = "AddNumbersPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private URL wsdlurl = null;

	private AddNumbersClient1 client1;

	AddNumbersPortType port = null;

	String noToHeaderSoapmsg = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><Action xmlns=\"http://www.w3.org/2005/08/addressing\">http://example.com/AddNumbersPortType/add</Action></S:Header><S:Body><addNumbers xmlns=\"http://example.com\"><number1>10</number1><number2>10</number2></addNumbers></S:Body></S:Envelope>";

	String noActionHeaderSoapmsg = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To></S:Header><S:Body><addNumbers xmlns=\"http://example.com\"><number1>10</number1><number2>10</number2></addNumbers></S:Body></S:Envelope>";

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
	 * @testName: testDefaultOneWayAction
	 *
	 * @assertion_ids: WSACORE:SPEC:3001; WSACORE:SPEC:3005; WSACORE:SPEC:3017;
	 * WSACORE:SPEC:3022.2; WSACORE:SPEC:3022.2.1; WSACORE:SPEC:3022.2.2;
	 * WSAMD:SPEC:5000
	 *
	 * @test_Strategy: Test default action pattern for WSDL input
	 *
	 */
	@Test
	public void testDefaultOneWayAction() throws Exception {
		logger.log(Level.INFO, "testDefaultOneWayAction");
		boolean pass = true;

		try {
			port.addNumbers(10, 10);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testDefaultOneWayAction failed", e);
		}

		if (!pass)
			throw new Exception("testDefaultOneWayAction failed");
	}

	/*
	 * @testName: testExplicitOneWayAction
	 *
	 * @assertion_ids: WSACORE:SPEC:3001; WSACORE:SPEC:3005; WSACORE:SPEC:3009;
	 * WSACORE:SPEC:3017; WSACORE:SPEC:3022.2; WSACORE:SPEC:3022.2.1;
	 * WSACORE:SPEC:3022.2.2; WSAMD:SPEC:5000
	 *
	 * @test_Strategy: Test default action pattern for WSDL input
	 *
	 */
	@Test
	public void testExplicitOneWayAction() throws Exception {
		logger.log(Level.INFO, "testExplicitOneWayAction");
		boolean pass = true;

		try {
			port.addNumbers2(10, 10);
		} catch (Exception e) {
			TestUtil.logErr("Caught Exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testExplicitOneWayAction failed", e);
		}

		if (!pass)
			throw new Exception("testExplicitOneWayAction failed");
	}

	/*
	 * @testName: noToHeaderOneWayTest
	 *
	 * @assertion_ids: WSASB:SPEC:6005; WSASB:SPEC:6006; WSASB:SPEC:6013;
	 *
	 * @test_Strategy: Send a message that doesn't contain wsa:To
	 *
	 */
	@Test
	public void noToHeaderOneWayTest() throws Exception {
		logger.log(Level.INFO, "noToHeaderOneWayTest");
		boolean pass = true;

		SOAPMessage response = null;
		try {
			String soapmsg = noToHeaderSoapmsg;
			response = client1.makeSaajRequest(soapmsg);
			JAXWS_Util.dumpSOAPMessage(response, false);
		} catch (Exception e) {
			TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("noToHeaderOneWayTest failed", e);
		}
		if (!pass)
			throw new Exception("noToHeaderOneWayTest failed");
	}

	/*
	 * @testName: noActionHeaderOneWayTest
	 *
	 * @assertion_ids: WSASB:SPEC:6005; WSASB:SPEC:6006; WSASB:SPEC:6013;
	 *
	 * @test_Strategy: Send a message that doesn't contain wsa:Action
	 *
	 */
	@Test
	public void noActionHeaderOneWayTest() throws Exception {
		logger.log(Level.INFO, "noActionHeaderOneWayTest");
		boolean pass = true;

		String soapmsg = MessageFormat.format(noActionHeaderSoapmsg, url);
		SOAPMessage response = null;
		try {
			response = client1.makeSaajRequest(soapmsg);
			JAXWS_Util.dumpSOAPMessage(response, false);
		} catch (SOAPFaultException e) {
			logger.log(Level.INFO, "Caught expected SOAPFaultException: " + e.getMessage());
		} catch (Exception e) {
			TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("noActionHeaderOneWayTest failed", e);
		}
		if (!pass)
			throw new Exception("noActionHeaderOneWayTest failed");
	}

}
