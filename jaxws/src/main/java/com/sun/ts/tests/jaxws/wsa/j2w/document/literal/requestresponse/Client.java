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

package com.sun.ts.tests.jaxws.wsa.j2w.document.literal.requestresponse;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.text.MessageFormat;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.wsa.common.WsaSOAPUtils;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Holder;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.soap.AddressingFeature;
import jakarta.xml.ws.soap.SOAPFaultException;

public class Client extends BaseClient {

	// URL properties used by the test
	private static final String ENDPOINT_URL = "wsaj2wdlrequestresponsetest.endpoint.1";

	private static final String WSDLLOC_URL = "wsaj2wdlrequestresponsetest.wsdlloc.1";

	private String url = null;

	// service and port information
	private static final String NAMESPACEURI = "http://example.com";

	private static final String SERVICE_NAME = "AddNumbersService";

	private static final String PORT_NAME = "AddNumbersPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private URL wsdlurl = null;

	AddNumbersPortType port = null;

	static AddNumbersService service = null;

	String invalidCardinality = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To><Action xmlns=\"http://www.w3.org/2005/08/addressing\">inputAction</Action><MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:{1}</MessageID><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{2}</Address></ReplyTo><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{3}</Address></ReplyTo></S:Header><S:Body><ns1:addNumbers2 xmlns:ns1=\"http://example.com\"><number1>10</number1><number2>10</number2><testName>invalidCardinality</testName></ns1:addNumbers2></S:Body></S:Envelope>";

	String actionMismatch = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To><Action xmlns=\"http://www.w3.org/2005/08/addressing\">inputAction</Action><MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:{1}</MessageID><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{2}</Address></ReplyTo></S:Header><S:Body><ns1:addNumbers2 xmlns:ns1=\"http://example.com\"><number1>10</number1><number2>10</number2><testName>actionMismatch</testName></ns1:addNumbers2></S:Body></S:Envelope>";

	String actionNotSupported = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To><Action xmlns=\"http://www.w3.org/2005/08/addressing\">ActionNotSupported</Action><MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:{1}</MessageID><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{2}</Address></ReplyTo></S:Header><S:Body><ns1:addNumbers2 xmlns:ns1=\"http://example.com\"><number1>10</number1><number2>10</number2><testName>actionNotSupported</testName></ns1:addNumbers2></S:Body></S:Envelope>";

	String missingActionHeader = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To><MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:{1}</MessageID><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{2}</Address></ReplyTo></S:Header><S:Body><ns1:addNumbers2 xmlns:ns1=\"http://example.com\"><number1>10</number1><number2>10</number2><testName>missingActionHeader</testName></ns1:addNumbers2></S:Body></S:Envelope>";

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	private static AddressingFeature ENABLED_ADDRESSING_FEATURE = new AddressingFeature(true, true);

	private static AddressingFeature DISABLED_ADDRESSING_FEATURE = new AddressingFeature(false);

	private WebServiceFeature[] enabledRequiredwsf = { ENABLED_ADDRESSING_FEATURE };

	private Dispatch<SOAPMessage> createDispatchSOAPMessage(QName port) throws Exception {
		return service.createDispatch(port, SOAPMessage.class, jakarta.xml.ws.Service.Mode.MESSAGE,
				DISABLED_ADDRESSING_FEATURE);
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
				AddNumbersPortType.class, enabledRequiredwsf);
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
	 * @testName: testDefaultRequestResponseAction
	 *
	 * @assertion_ids: WSACORE:SPEC:2001; WSACORE:SPEC:3002; WSACORE:SPEC:3003;
	 * WSACORE:SPEC:3003.1; WSACORE:SPEC:3003.2; WSACORE:SPEC:3003.3;
	 * WSACORE:SPEC:3005; WSACORE:SPEC:3007; WSACORE:SPEC:3009; WSACORE:SPEC:3010;
	 * WSACORE:SPEC:3010.1; WSACORE:SPEC:3010.2; WSACORE:SPEC:3017;
	 * WSACORE:SPEC:3022; WSACORE:SPEC:3022.2; WSACORE:SPEC:3022.2.1;
	 * WSACORE:SPEC:3002.2.2; WSACORE:SPEC:3023; WSACORE:SPEC:3023.1;
	 * WSACORE:SPEC:3023.1.1; WSACORE:SPEC:3023.1.2; WSACORE:SPEC:3023.4;
	 * WSACORE:SPEC:3023.4.1; WSASB:SPEC:5000; WSASB:SPEC:6000; WSAMD:SPEC:4001;
	 * WSAMD:SPEC:4001.1; WSAMD:SPEC:5001;
	 *
	 * @test_Strategy: Test default action pattern for WSDL input/output
	 *
	 */
	@Test
	public void testDefaultRequestResponseAction() throws Exception {
		logger.log(Level.INFO, "testDefaultRequestResponseAction");
		boolean pass = true;

		try {
			int number = port.addNumbers(10, 10, new Holder("testDefaultRequestResponseAction"));
			logger.log(Level.INFO, "number=" + number);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testDefaultRequestResponseAction failed", e);
		}

		if (!pass)
			throw new Exception("testDefaultRequestResponseAction failed");
	}

	/*
	 * @testName: testExplicitRequestResponseAction
	 *
	 * @assertion_ids: WSACORE:SPEC:2001; WSACORE:SPEC:3002; WSACORE:SPEC:3003;
	 * WSACORE:SPEC:3003.1; WSACORE:SPEC:3003.2; WSACORE:SPEC:3003.3;
	 * WSACORE:SPEC:3005; WSACORE:SPEC:3007; WSACORE:SPEC:3009; WSACORE:SPEC:3010;
	 * WSACORE:SPEC:3010.1; WSACORE:SPEC:3010.2; WSACORE:SPEC:3017;
	 * WSACORE:SPEC:3022; WSACORE:SPEC:3022.2; WSACORE:SPEC:3022.2.1;
	 * WSACORE:SPEC:3002.2.2; WSACORE:SPEC:3023; WSACORE:SPEC:3023.1;
	 * WSACORE:SPEC:3023.1.1; WSACORE:SPEC:3023.1.2; WSACORE:SPEC:3023.4;
	 * WSACORE:SPEC:3023.4.1; WSASB:SPEC:5000; WSASB:SPEC:6000;
	 *
	 * @test_Strategy: Test explicit action pattern for WSDL input/output
	 *
	 */
	@Test
	public void testExplicitRequestResponseAction() throws Exception {
		logger.log(Level.INFO, "testExplicitRequestResponseAction");
		boolean pass = true;

		try {
			int number = port.addNumbers2(10, 10, new Holder("testExplicitRequestResponseAction"));
			logger.log(Level.INFO, "number=" + number);
		} catch (Exception e) {
			TestUtil.logErr("Caught Exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testExplicitRequestResponseAction failed", e);
		}

		if (!pass)
			throw new Exception("testExplicitRequestResponseAction failed");
	}

	/*
	 * @testName: testMessageAddressingHeaderRequiredFault
	 *
	 * @assertion_ids: WSASB:SPEC:6005; WSASB:SPEC:6006; WSASB:SPEC:6013;
	 * WSASB:SPEC:6004.4; WSASB:SPEC:6004.1; WSASB:SPEC:6004.2; WSASB:SPEC:6001;
	 *
	 * @test_Strategy: Send a message that doesn't contain wsa:Action header. Expect
	 * MessageAddressingHeaderRequired Exception. Cannot test missing wsa:To,
	 * wsa:ReplyTo, or wsa:MessageID headers as these are optional in WSA Core Spec.
	 */
	@Test
	public void testMessageAddressingHeaderRequiredException() throws Exception {
		logger.log(Level.INFO, "testMessageAddressingHeaderRequiredFault");
		boolean pass = true;

		SOAPMessage response = null;
		Dispatch<SOAPMessage> dispatchSM;
		try {
			String soapmsg = MessageFormat.format(missingActionHeader, url, UUID.randomUUID(),
					WsaSOAPUtils.getAddrVerAnonUri());
			dispatchSM = createDispatchSOAPMessage(PORT_QNAME);
			SOAPMessage request = JAXWS_Util.makeSOAPMessage(soapmsg);
			logger.log(Level.INFO, "Dumping SOAP Request ...");
			JAXWS_Util.dumpSOAPMessage(request, false);
			response = dispatchSM.invoke(request);
			logger.log(Level.INFO, "Dumping SOAP Response ...");
			JAXWS_Util.dumpSOAPMessage(response, false);
		} catch (SOAPFaultException e) {
			try {
				logger.log(Level.INFO, "Caught SOAPFaultException");
				logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
				logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(e));
				logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(e));
				if (WsaSOAPUtils.isMessageAddressingHeaderRequiredFaultCode(e))
					logger.log(Level.INFO, "SOAPFault contains expected Exceptioncode MessageAddressingHeaderRequired");
				else {
					String Exceptioncode = WsaSOAPUtils.getFaultCode(e);
					TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
							+ ", expected: MessageAddressingHeaderRequired");
					pass = false;
				}
				if (WsaSOAPUtils.getFaultString(e) == null) {
					TestUtil.logErr("The Exceptionstring element MUST EXIST for SOAP 1.1 Exceptions");
					pass = false;
				}
				if (WsaSOAPUtils.getFaultDetail(e) != null) {
					TestUtil.logErr("The Exceptiondetail element MUST NOT EXIST for SOAP 1.1 "
							+ "Faults related to header entries");
					pass = false;
				}
			} catch (SOAPException e2) {
				TestUtil.logErr("Caught unexpected exception: " + e2.getMessage());
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("testMessageAddressingHeaderRequiredFault failed", e);
		}
		if (!pass)
			throw new Exception("testMessageAddressingHeaderRequiredFault failed");
	}

	/*
	 * @testName: testInvalidCardinalityFault
	 *
	 * @assertion_ids: WSASB:SPEC:6005; WSASB:SPEC:6006; WSASB:SPEC:6013;
	 * WSASB:SPEC:6012.3; WSASB:SPEC:6004.4; WSASB:SPEC:6004.1; WSASB:SPEC:6004.2;
	 * WSASB:SPEC:6001;
	 *
	 * @test_Strategy: Test for InvalidCardinality Exception. Send a message that
	 * contains two wsa:ReplyTo headers. Expect an InvalidCardinality Exception.
	 */
	@Test
	public void testInvalidCardinalityException() throws Exception {
		logger.log(Level.INFO, "testInvalidCardinalityFault");
		boolean pass = true;
		SOAPMessage response = null;
		Dispatch<SOAPMessage> dispatchSM;
		try {
			String soapmsg = MessageFormat.format(invalidCardinality, url, UUID.randomUUID(),
					WsaSOAPUtils.getAddrVerAnonUri(), WsaSOAPUtils.getAddrVerAnonUri());
			dispatchSM = createDispatchSOAPMessage(PORT_QNAME);
			SOAPMessage request = JAXWS_Util.makeSOAPMessage(soapmsg);
			logger.log(Level.INFO, "Dumping SOAP Request ...");
			JAXWS_Util.dumpSOAPMessage(request, false);
			response = dispatchSM.invoke(request);
			logger.log(Level.INFO, "Dumping SOAP Response ...");
			JAXWS_Util.dumpSOAPMessage(response, false);
			TestUtil.logErr("No SOAPFaultException occurred which was expected");
			pass = false;
		} catch (SOAPFaultException s) {
			try {
				logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
				logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(s));
				logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(s));
				if (WsaSOAPUtils.isInvalidCARDINALITYFaultCode(s))
					logger.log(Level.INFO, "SOAPFault contains expected Exceptioncode InvalidCardinality");
				else {
					String faultcode = WsaSOAPUtils.getFaultCode(s);
					TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + faultcode
							+ ", expected: InvalidCardinality");
					pass = false;
				}
				String faultdetail[] = WsaSOAPUtils.getFaultDetail(s);
				if (faultdetail != null) {
					StringBuffer output = new StringBuffer("FaultDetail:");
					for (int i = 0; faultdetail[i] != null; i++) {
						output.append(" " + faultdetail[i]);
					}
					TestUtil.logErr(output.toString());

					if (WsaSOAPUtils.isProblemHeaderQNameFaultDetail(faultdetail[0]))
						logger.log(Level.INFO, "FaultDetail contains expected ProblemHeaderQName");
					else {
						TestUtil.logErr("FaultDetail contains unexpected value got: " + faultdetail[0]
								+ ", expected: ProblemHeaderQName");
						pass = false;
					}
				}
			} catch (Exception e2) {
				TestUtil.logErr("Caught unexpected exception: " + e2.getMessage());
				throw new Exception("testInvalidCardinalityFault failed", e2);
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
			throw new Exception("testInvalidCardinalityFault failed", e);
		}
		if (!pass)
			throw new Exception("testInvalidCardinalityFault failed");
	}

	/*
	 * @testName: testActionMismatchOrActionNotSupportedFaultCase1
	 *
	 * @assertion_ids: WSASB:SPEC:6005; WSASB:SPEC:6006; WSASB:SPEC:6015;
	 * WSASB:SPEC:6004.4; WSASB:SPEC:6004.1; WSASB:SPEC:6004.2; WSASB:SPEC:6001;
	 * JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test for ActionMismatch or ActionNotSupportedfault. Set the
	 * SOAPACTIONURI to a wrong value.
	 *
	 */
	@Test
	public void testActionMismatchOrActionNotSupportedFaultCase1() throws Exception {
		logger.log(Level.INFO, "testActionMismatchOrActionNotSupportedFaultCase1");
		boolean pass = true;
		SOAPMessage response = null;
		Dispatch<SOAPMessage> dispatchSM;
		try {
			String soapmsg = MessageFormat.format(actionMismatch, url, UUID.randomUUID(),
					WsaSOAPUtils.getAddrVerAnonUri());
			dispatchSM = createDispatchSOAPMessage(PORT_QNAME);
			JAXWS_Util.setSOAPACTIONURI(dispatchSM, "ActionMismatch");
			SOAPMessage request = JAXWS_Util.makeSOAPMessage(soapmsg);
			logger.log(Level.INFO, "Dumping SOAP Request ...");
			JAXWS_Util.dumpSOAPMessage(request, false);
			response = dispatchSM.invoke(request);
			logger.log(Level.INFO, "Dumping SOAP Response ...");
			JAXWS_Util.dumpSOAPMessage(response, false);
			TestUtil.logErr("No SOAPFaultException occurred which was expected");
			pass = false;
		} catch (SOAPFaultException s) {
			try {
				logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
				logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(s));
				logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(s));
				if (WsaSOAPUtils.isActionMismatchFaultCode(s) || WsaSOAPUtils.isActionNotSupportedFaultCode(s))
					logger.log(Level.INFO,
							"SOAPFault contains expected Exceptioncode ActionMismatch or ActionNotSupported");
				else {
					String Exceptioncode = WsaSOAPUtils.getFaultCode(s);
					TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
							+ ", expected: ActionMismatch or ActionNotSupported");
					pass = false;
				}
				if (WsaSOAPUtils.getFaultString(s) == null) {
					TestUtil.logErr("The Exceptionstring element MUST EXIST for SOAP 1.1 Exceptions");
					pass = false;
				}
				if (WsaSOAPUtils.getFaultDetail(s) != null) {
					TestUtil.logErr("The Exceptiondetail element MUST NOT EXIST for SOAP 1.1 "
							+ "Faults related to header entries");
					pass = false;
				}
			} catch (Exception e2) {
				TestUtil.logErr("Caught unexpected exception: " + e2.getMessage());
				throw new Exception("testActionMismatchOrActionNotSupportedFaultCase1 failed", e2);
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
			throw new Exception("testActionMismatchOrActionNotSupportedFaultCase1 failed", e);
		}
		if (!pass)
			throw new Exception("testActionMismatchOrActionNotSupportedFaultCase1 failed");
	}

	/*
	 * @testName: testActionMismatchOrActionNotSupportedFaultCase2
	 *
	 * @assertion_ids: WSASB:SPEC:6005; WSASB:SPEC:6006; WSASB:SPEC:6015;
	 * WSASB:SPEC:6004.4; WSASB:SPEC:6004.1; WSASB:SPEC:6004.2; WSASB:SPEC:6001;
	 * JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test for ActionMismatch or ActionNotSupportedfault. Set the
	 * SOAPACTIONURI to a wrong value.
	 *
	 */
	@Test
	public void testActionMismatchOrActionNotSupportedFaultCase2() throws Exception {
		logger.log(Level.INFO, "testActionMismatchOrActionNotSupportedFaultCase2");
		boolean pass = true;
		SOAPMessage response = null;
		Dispatch<SOAPMessage> dispatchSM;
		try {
			String soapmsg = MessageFormat.format(actionNotSupported, url, UUID.randomUUID(),
					WsaSOAPUtils.getAddrVerAnonUri());
			dispatchSM = createDispatchSOAPMessage(PORT_QNAME);
			JAXWS_Util.setSOAPACTIONURI(dispatchSM, "ActionNotSupported1");
			SOAPMessage request = JAXWS_Util.makeSOAPMessage(soapmsg);
			logger.log(Level.INFO, "Dumping SOAP Request ...");
			JAXWS_Util.dumpSOAPMessage(request, false);
			response = dispatchSM.invoke(request);
			logger.log(Level.INFO, "Dumping SOAP Response ...");
			JAXWS_Util.dumpSOAPMessage(response, false);
			TestUtil.logErr("No SOAPFaultException occurred which was expected");
			pass = false;
		} catch (SOAPFaultException s) {
			try {
				logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
				logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(s));
				logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(s));
				if (WsaSOAPUtils.isActionMismatchFaultCode(s) || WsaSOAPUtils.isActionNotSupportedFaultCode(s))
					logger.log(Level.INFO,
							"SOAPFault contains expected Exceptioncode ActionMismatch or ActionNotSupported");
				else {
					String Exceptioncode = WsaSOAPUtils.getFaultCode(s);
					TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
							+ ", expected: ActionMismatch or ActionNotSupported");
					pass = false;
				}
				if (WsaSOAPUtils.getFaultString(s) == null) {
					TestUtil.logErr("The Exceptionstring element MUST EXIST for SOAP 1.1 Exceptions");
					pass = false;
				}
				if (WsaSOAPUtils.getFaultDetail(s) != null) {
					TestUtil.logErr("The Exceptiondetail element MUST NOT EXIST for SOAP 1.1 "
							+ "Faults related to header entries");
					pass = false;
				}
			} catch (Exception e2) {
				TestUtil.logErr("Caught unexpected exception: " + e2.getMessage());
				throw new Exception("testActionMismatchOrActionNotSupportedFaultCase2 failed", e2);
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
			throw new Exception("testActionMismatchOrActionNotSupportedFaultCase2 failed", e);
		}
		if (!pass)
			throw new Exception("testActionMismatchOrActionNotSupportedFaultCase2 failed");
	}

	/*
	 * @testName: testActionMismatchOrActionNotSupportedFaultCase3
	 *
	 * @assertion_ids: WSASB:SPEC:6005; WSASB:SPEC:6006; WSASB:SPEC:6015;
	 * WSASB:SPEC:6004.4; WSASB:SPEC:6004.1; WSASB:SPEC:6004.2; WSASB:SPEC:6001;
	 * JAXWS:SPEC:10027;
	 *
	 * @test_Strategy: Test for ActionMismatch or ActionNotSupportedfault. Set the
	 * SOAPACTIONURI to a wrong value.
	 *
	 */
	@Test
	public void testActionMismatchOrActionNotSupportedFaultCase3() throws Exception {
		logger.log(Level.INFO, "testActionMismatchOrActionNotSupportedFaultCase3");
		boolean pass = true;
		try {
			JAXWS_Util.setSOAPACTIONURI(port, "ActionNotSupported2");
			int number = port.addNumbers2(10, 10, new Holder("ActionNotSupported2"));
			TestUtil.logErr("No SOAPFaultException occurred which was expected");
			pass = false;
		} catch (SOAPFaultException s) {
			try {
				logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
				logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(s));
				logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(s));
				if (WsaSOAPUtils.isActionMismatchFaultCode(s) || WsaSOAPUtils.isActionNotSupportedFaultCode(s))
					logger.log(Level.INFO,
							"SOAPFault contains expected Exceptioncode ActionMismatch or ActionNotSupported");
				else {
					String Exceptioncode = WsaSOAPUtils.getFaultCode(s);
					TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
							+ ", expected: ActionMismatch or ActionNotSupported");
					pass = false;
				}
				if (WsaSOAPUtils.getFaultString(s) == null) {
					TestUtil.logErr("The Exceptionstring element MUST EXIST for SOAP 1.1 Exceptions");
					pass = false;
				}
				if (WsaSOAPUtils.getFaultDetail(s) != null) {
					TestUtil.logErr("The Exceptiondetail element MUST NOT EXIST for SOAP 1.1 "
							+ "Faults related to header entries");
					pass = false;
				}
			} catch (Exception e2) {
				TestUtil.logErr("Caught unexpected exception: " + e2.getMessage());
				throw new Exception("testActionMismatchOrActionNotSupportedFaultCase3 failed", e2);
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
			throw new Exception("testActionMismatchOrActionNotSupportedFaultCase3 failed", e);
		}
		if (!pass)
			throw new Exception("testActionMismatchOrActionNotSupportedFaultCase3 failed");
	}
}
