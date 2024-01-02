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
 * $Id: Client.java 52702 2007-02-13 17:07:20Z adf $
 */
package com.sun.ts.tests.jaxws.wsa.w2j.document.literal.respectbindingfeature;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.wsa.common.WsaSOAPUtils;

import jakarta.xml.ws.Holder;
import jakarta.xml.ws.RespectBindingFeature;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.soap.AddressingFeature;
import jakarta.xml.ws.soap.SOAPFaultException;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.wsa.w2j.document.literal.respectbindingfeature.";

	// URL properties used by the test
	private static final String ENDPOINT_URL2 = "wsaw2jdlrespectbindingfeature.endpoint.1";

	private static final String ENDPOINT_URL21 = "wsaw2jdlrespectbindingfeature.endpoint.2";

	private static final String ENDPOINT_URL3 = "wsaw2jdlrespectbindingfeature.endpoint.3";

	private static final String ENDPOINT_URL31 = "wsaw2jdlrespectbindingfeature.endpoint.4";

	private static final String WSDLLOC_URL = "wsaw2jdlrespectbindingfeature.wsdlloc.2";

	// service and port information
	private static final String NAMESPACEURI = "http://respectbindingfeatureservice.org/wsdl";

	private static final String SERVICE_NAME = "RespectBindingFeatureTestService";

	private static final String PORT_NAME2 = "RespectBindingFeatureTest2Port";

	private static final String PORT_NAME21 = "RespectBindingFeatureTest21Port";

	private static final String PORT_NAME3 = "RespectBindingFeatureTest3Port";

	private static final String PORT_NAME31 = "RespectBindingFeatureTest31Port";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME2 = new QName(NAMESPACEURI, PORT_NAME2);

	private QName PORT_QNAME21 = new QName(NAMESPACEURI, PORT_NAME21);

	private QName PORT_QNAME3 = new QName(NAMESPACEURI, PORT_NAME3);

	private QName PORT_QNAME31 = new QName(NAMESPACEURI, PORT_NAME31);

	private String url2 = null;

	private String url21 = null;

	private String url3 = null;

	private String url31 = null;

	private URL wsdlurl = null;

	private String ctxroot = null;

	private RespectBindingFeatureTest2 port4a = null;

	private RespectBindingFeatureTest2 port5a = null;

	private RespectBindingFeatureTest2 port6a = null;

	private RespectBindingFeatureTest3 port7a = null;

	private RespectBindingFeatureTest3 port8a = null;

	private WebServiceFeature[] nonEnabledAddressingEnabledRespectBindingwsf = { new AddressingFeature(false),
			new RespectBindingFeature(true) };

	private WebServiceFeature[] enabledRequiredAddressingEnabledRespectBindingwsf = { new AddressingFeature(true, true),
			new RespectBindingFeature(true) };

	private WebServiceFeature[] enabledNotRequiredAddressingEnabledRespectBindingwsf = {
			new AddressingFeature(true, false), new RespectBindingFeature(true) };

	static RespectBindingFeatureTestService service = null;

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(WSDLLOC_URL);
		wsdlurl = ctsurl.getURL(PROTOCOL, hostname, portnum, file);

		file = JAXWS_Util.getURLFromProp(ENDPOINT_URL2);
		url2 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT_URL21);
		url21 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT_URL3);
		url3 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT_URL31);
		url31 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		logger.log(Level.INFO, "Service Endpoint URL2: " + url2);
		logger.log(Level.INFO, "Service Endpoint URL21: " + url21);
		logger.log(Level.INFO, "Service Endpoint URL3: " + url3);
		logger.log(Level.INFO, "Service Endpoint URL31: " + url31);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
	}

	/*************************************************************************************************
	 * Table to cover RespectBinding/RespectBindingFeature which has only
	 * enabled/disabled param.
	 * 
	 * These scenarios exist for RespectBinding/RespectBindingFeatures (using
	 * addressingfeature tests (scenarios) as my starting point and converting that
	 * test into RespectBinding/Feature by copying and modifying it appropriately
	 * corresponding to this scenario table):
	 * 
	 * There exist the following 20+ scenarios for RespectBindingFeature:
	 * 
	 * ------------------- ------------------- --------------------- ---------------
	 * Client Server RespectBindingFeature Expected Result -------------------
	 * ------------------- --------------------- --------------- 1).
	 * Enabled/NotRequired Enabled/NotRequired N/A N/A-not TCK test 2).
	 * Enabled/Required Enabled/NotRequired N/A N/A-not TCK test 3). NotEnabled
	 * Enabled/NotRequired N/A N/A-not TCK test 4a) Enabled/NotRequired
	 * Enabled/Required S-Enabled/C-Enabled Expect No Error 4b) Enabled/NotRequired
	 * Enabled/Required S-Enabled/C-Disabled N/A-not TCK test 4c)
	 * Enabled/NotRequired Enabled/Required S-Disabled/C-Enabled N/A-not TCK test
	 * 4d) Enabled/NotRequired Enabled/Required S-Disabled/C-Disabled N/A-not TCK
	 * test 5a) Enabled/Required Enabled/Required S-Enabled/C-Enabled Expect No
	 * Error 5b) Enabled/Required Enabled/Required S-Enabled/C-Disabled N/A-not TCK
	 * test 5c) Enabled/Required Enabled/Required S-Disabled/C-Enabled N/A-not TCK
	 * test 5d) Enabled/Required Enabled/Required S-Disabled/C-Disabled N/A-not TCK
	 * test 6a) NotEnabled Enabled/Required S-Enabled/C-Enabled Expect Exception 6b)
	 * NotEnabled Enabled/Required S-Enabled/C-Disabled N/A-not TCK test 6c)
	 * NotEnabled Enabled/Required S-Disabled/C-Enabled N/A-not TCK test 6d)
	 * NotEnabled Enabled/Required S-Disabled/C-Disabled N/A-not TCK test 7a)
	 * Enabled/NotRequired NotEnabled S-Enabled/C-Enabled Expect No Error 7b)
	 * Enabled/NotRequired NotEnabled S-Enabled/C-Disabled N/A-not TCK test 7c)
	 * Enabled/NotRequired NotEnabled S-Disabled/C-Enabled N/A-not TCK test 7d)
	 * Enabled/NotRequired NotEnabled S-Disabled/C-Disabled N/A-not TCK test 8a)
	 * Enabled/Required NotEnabled S-Enabled/C-Enabled Expect Exception 8b)
	 * Enabled/Required NotEnabled S-Enabled/C-Disabled N/A-not TCK test 8c)
	 * Enabled/Required NotEnabled S-Disabled/C-Enabled N/A-not TCK test 8d)
	 * Enabled/Required NotEnabled S-Disabled/C-Disabled N/A-not TCK test
	 * 
	 * test scenarios 4a-b, 5a-b, 6a-b use port2 test scenarios 4c-d, 5c-d, 6c-d use
	 * port21 test scenarios 7a-b, 8a-b use port3 test scenarios 7c-d, 8c-d use
	 * port31
	 * 
	 * where port2 is configured via- WSDL: <wsam:Addressing/> Impl2.java:
	 * 
	 * @BindingType(value=SOAPBinding.SOAP11HTTP_BINDING) @RespectBinding(enabled=true)
	 * 
	 *                                                    where port21 is configured
	 *                                                    via- WSDL:
	 *                                                    <wsam:Addressing/>
	 *                                                    Impl21.java:
	 * @BindingType(value=SOAPBinding.SOAP11HTTP_BINDING) @RespectBinding(enabled=false)
	 * 
	 *                                                    where port3 is configured
	 *                                                    via- WSDL:
	 *                                                    <wsam:Addressing/>
	 *                                                    Impl3.java:
	 * @BindingType(value=SOAPBinding.SOAP11HTTP_BINDING) @Addressing(enabled=false) @RespectBinding(enabled=true)
	 * 
	 *                                                    where port31 is configured
	 *                                                    via- WSDL:
	 *                                                    <wsam:Addressing/>
	 *                                                    Impl31.java: @BindingType(value=SOAPBinding.SOAP11HTTP_BINDING) @Addressing(enabled=false) @RespectBinding(enabled=false)
	 *************************************************************************************************/

	protected void getPortStandalone() throws Exception {
		logger.log(Level.INFO, "******************************Retrieving Port 4a************************\n");
		// client side Addressing enabled/NotRequired; server side
		// Addressing/Required
		port4a = (RespectBindingFeatureTest2) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME,
				RespectBindingFeatureTestService.class, PORT_QNAME2, RespectBindingFeatureTest2.class,
				enabledNotRequiredAddressingEnabledRespectBindingwsf);
		JAXWS_Util.setTargetEndpointAddress(port4a, url2);

		logger.log(Level.INFO, "******************************Retrieving Port 5a************************\n");
		// client side Addressing enabled/Required; server side Addressing/Required
		port5a = (RespectBindingFeatureTest2) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME,
				RespectBindingFeatureTestService.class, PORT_QNAME2, RespectBindingFeatureTest2.class,
				enabledRequiredAddressingEnabledRespectBindingwsf);
		JAXWS_Util.setTargetEndpointAddress(port5a, url2);

		logger.log(Level.INFO, "******************************Retrieving Port 6a************************\n");
		// client side Addressing off; server side Addressing/Required
		port6a = (RespectBindingFeatureTest2) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME,
				RespectBindingFeatureTestService.class, PORT_QNAME2, RespectBindingFeatureTest2.class,
				nonEnabledAddressingEnabledRespectBindingwsf);
		JAXWS_Util.setTargetEndpointAddress(port6a, url2);

		logger.log(Level.INFO, "******************************Retrieving Port 7a************************\n");
		// client side Addressing enabled/NotRequired; server side off (in WSDL
		// enabled, but overridden to false in IMPL to turn it off)
		port7a = (RespectBindingFeatureTest3) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME,
				RespectBindingFeatureTestService.class, PORT_QNAME3, RespectBindingFeatureTest3.class,
				enabledNotRequiredAddressingEnabledRespectBindingwsf);
		JAXWS_Util.setTargetEndpointAddress(port7a, url3);

		logger.log(Level.INFO, "******************************Retrieving Port 8a************************\n");
		// client side Addressing enabled/Required; server side off (in WSDL
		// enabled, but overridden to false in IMPL to turn it off)
		port8a = (RespectBindingFeatureTest3) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME,
				RespectBindingFeatureTestService.class, PORT_QNAME3, RespectBindingFeatureTest3.class,
				enabledRequiredAddressingEnabledRespectBindingwsf);
		JAXWS_Util.setTargetEndpointAddress(port8a, url3);

	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);

		port4a = (RespectBindingFeatureTest2) service.getPort(RespectBindingFeatureTest2.class,
				enabledNotRequiredAddressingEnabledRespectBindingwsf);
		port5a = (RespectBindingFeatureTest2) service.getPort(RespectBindingFeatureTest2.class,
				enabledRequiredAddressingEnabledRespectBindingwsf);
		port6a = (RespectBindingFeatureTest2) service.getPort(RespectBindingFeatureTest2.class,
				nonEnabledAddressingEnabledRespectBindingwsf);
		port7a = (RespectBindingFeatureTest3) service.getPort(RespectBindingFeatureTest3.class,
				enabledNotRequiredAddressingEnabledRespectBindingwsf);
		port8a = (RespectBindingFeatureTest3) service.getPort(RespectBindingFeatureTest3.class,
				enabledRequiredAddressingEnabledRespectBindingwsf);

		// debug dumping of ports
		Object[] portsTodump = new Object[] { port4a, port5a, port6a, port7a, port8a };
		dumpTargetEndpointAddressForPort(portsTodump);
	}

	protected void getService() {
		service = (RespectBindingFeatureTestService) getSharedObject();
	}

	private void dumpTargetEndpointAddressForPort(Object[] portsTodump) {
		try {
			for (int i = 0; i < portsTodump.length; i++) {
				logger.log(Level.INFO, "port=" + portsTodump[i]);
				logger.log(Level.INFO, "Obtained port" + i);
				JAXWS_Util.dumpTargetEndpointAddress(portsTodump[i]);
			}
		} catch (java.lang.Exception e) {
			TestUtil.printStackTrace(e);
			TestUtil.logErr("Error dumping EndpointAddress for port");
		}
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
	 * @testName: afCltEnabledNotREQSvrEnabledREQrbfSvrEnabledCltEnabledTest
	 *
	 * @assertion_ids: WSAMD:SPEC:3001.1; WSAMD:SPEC:3001.2; WSAMD:SPEC:3001.4;
	 * JAXWS:SPEC:6011; JAXWS:SPEC:6011.2; JAXWS:SPEC:6012; JAXWS:SPEC:6012.1;
	 * JAXWS:SPEC:6012.2; JAXWS:SPEC:6012.4; JAXWS:SPEC:6012.5; JAXWS:SPEC:7022;
	 * JAXWS:SPEC:7022.1; JAXWS:SPEC:7022; JAXWS:JAVADOC:189;
	 * 
	 *
	 * @test_Strategy: Test RespectBinding Feature. Addressing Client
	 * Enabled/NotRequired, Server Enabled/Required; RespectBinding Server Enabled,
	 * Client Enabled. Addressing headers MUST be present on SOAPRequest and
	 * SOAPResponse.
	 */
	@Test
	public void afCltEnabledNotREQSvrEnabledREQrbfSvrEnabledCltEnabledTest() throws Exception {
		TestUtil.logMsg("afCltEnabledNotREQSvrEnabledREQrbfSvrEnabledCltEnabledTest");
		logger.log(Level.INFO, "Verify Addressing headers are present on SOAPRequest and SOAPResponse");
		boolean pass = true;
		try {
			// 4a) Enabled/NotRequired Enabled/Required S-Enabled/C-Enabled
			port4a.addNumbers(new Holder("afCltEnabledNotREQSvrEnabledREQrbfSvrEnabledCltEnabledTest"), 10, 10);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("afCltEnabledNotREQSvrEnabledREQrbfSvrEnabledCltEnabledTest failed", e);
		}

		if (!pass) {
			throw new Exception("afCltEnabledNotREQSvrEnabledREQrbfSvrEnabledCltEnabledTest failed");
		}
	}

	/*
	 * @testName: afCltEnabledREQSvrEnabledREQrbfSvrEnabledCltEnabledTest
	 *
	 * @assertion_ids: WSAMD:SPEC:3001.1; WSAMD:SPEC:3001.2; WSAMD:SPEC:3001.4;
	 * JAXWS:SPEC:6011; JAXWS:SPEC:6011.2; JAXWS:SPEC:6012; JAXWS:SPEC:6012.1;
	 * JAXWS:SPEC:6012.2; JAXWS:SPEC:6012.4; JAXWS:SPEC:6012.5; JAXWS:SPEC:7022;
	 * JAXWS:SPEC:7022.1; JAXWS:JAVADOC:189;
	 *
	 * @test_Strategy: Test RespectBinding Feature. Addressing Client
	 * Enabled/Required, Server Enabled/Required; RespectBinding Server Enabled,
	 * Client Enabled. Addressing headers MUST be present on SOAPRequest and
	 * SOAPResponse.
	 */
	@Test
	public void afCltEnabledREQSvrEnabledREQrbfSvrEnabledCltEnabledTest() throws Exception {
		logger.log(Level.INFO, "afCltEnabledREQSvrEnabledREQrbfSvrEnabledCltEnabledTest");
		logger.log(Level.INFO, "Verify Addressing headers are present on SOAPRequest and SOAPResponse");
		boolean pass = true;
		try {
			// 5a) Enabled/Required Enabled/Required S-Enabled/C-Enabled
			port5a.addNumbers(new Holder("afCltEnabledREQSvrEnabledREQrbfSvrEnabledCltEnabledTest"), 10, 10);
		} catch (Exception e) {
			TestUtil.logErr("Caught Exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("afCltEnabledREQSvrEnabledREQrbfSvrEnabledCltEnabledTest failed", e);
		}

		if (!pass) {
			throw new Exception("afCltEnabledREQSvrEnabledREQrbfSvrEnabledCltEnabledTest failed");
		}
	}

	/*
	 * @testName: afCltNotEnabledSvrEnabledREQrbfSvrEnabledCltEnabledTest
	 *
	 * @assertion_ids: WSAMD:SPEC:3001.1; WSAMD:SPEC:3001.3; WSAMD:SPEC:3001.4;
	 * JAXWS:SPEC:6011; JAXWS:SPEC:6011.2; JAXWS:SPEC:6012; JAXWS:SPEC:6012.1;
	 * JAXWS:SPEC:6012.3; JAXWS:SPEC:6012.5; JAXWS:SPEC:7022; JAXWS:SPEC:7022.1;
	 * JAXWS:JAVADOC:189;
	 *
	 * @test_Strategy: Test RespectBinding Feature. Addressing Client NotEnabled,
	 * Server Enabled/Required; RespectBinding Server Enabled, Client Enabled. This
	 * scenario MUST throw back a SOAP Exception. Make sure the SOAP Exception has
	 * the correct information in it. The SOAP Exception Exceptioncode must be:
	 * MessageAddressingHeaderRequired.
	 */
	@Test
	public void afCltNotEnabledSvrEnabledREQrbfSvrEnabledCltEnabledTest() throws Exception {
		logger.log(Level.INFO, "afCltNotEnabledSvrEnabledREQrbfSvrEnabledCltEnabledTest");
		logger.log(Level.INFO, "Verify MessageAddressingHeaderRequired soap Exception is thrown by endpoint");
		boolean pass = true;
		try {
			// 6a) NotEnabled Enabled/Required S-Enabled/C-Enabled
			port6a.addNumbers(new Holder("afCltNotEnabledSvrEnabledREQrbfSvrEnabledCltEnabledTest"), 10, 10);
			TestUtil.logErr("SOAPFaultException was not thrown back");
			pass = false;
		} catch (SOAPFaultException sfe) {
			try {
				TestUtil.logMsg("Caught expected SOAPFaultException: " + sfe.getMessage());
				logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(sfe));
				logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(sfe));
				String ExceptionString = WsaSOAPUtils.getFaultString(sfe);
				if (WsaSOAPUtils.isMessageAddressingHeaderRequiredFaultCode(sfe))
					logger.log(Level.INFO, "SOAPFault contains expected Exceptioncode MessageAddressingHeaderRequired");
				else {
					String Exceptioncode = WsaSOAPUtils.getFaultCode(sfe);
					TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
							+ ", expected: MessageAddressingHeaderRequired");
					pass = false;
				}
				if (WsaSOAPUtils.getFaultString(sfe) == null) {
					TestUtil.logErr("FaultString MUST EXIST via SOAP Specification");
					pass = false;
				}
				if (WsaSOAPUtils.getFaultDetail(sfe) != null) {
					TestUtil.logErr("FaultDetail MUST NOT EXIST via SOAP Specification");
					pass = false;
				}
			} catch (Exception e2) {
				TestUtil.logErr("Caught unexpected exception: " + e2.getMessage());
				throw new Exception("afCltNotEnabledSvrEnabledREQrbfSvrEnabledCltEnabledTest failed", e2);
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught Exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("afCltNotEnabledSvrEnabledREQrbfSvrEnabledCltEnabledTest failed", e);
		}

		if (!pass) {
			throw new Exception("afCltNotEnabledSvrEnabledREQrbfSvrEnabledCltEnabledTest failed");
		}
	}

	/*
	 * @testName: afCltEnabledNotREQSvrNotEnabledrbfSvrEnabledCltEnabledTest
	 *
	 * @assertion_ids: WSAMD:SPEC:3001.1; WSAMD:SPEC:3001.2; WSAMD:SPEC:3001.4;
	 * JAXWS:SPEC:6012.2; JAXWS:SPEC:6012.3; JAXWS:SPEC:6012.4; JAXWS:SPEC:6012.6;
	 * JAXWS:SPEC:6016.1; JAXWS:SPEC:7020; JAXWS:SPEC:7020.1; JAXWS:JAVADOC:191;
	 * JAXWS:SPEC:7022; JAXWS:SPEC:7022.1; JAXWS:JAVADOC:189;
	 *
	 * @test_Strategy: Test RespectBinding Feature. Addressing Client
	 * Enabled/NotRequired, Server NotEnabled; RespectBinding Server Enabled, Client
	 * Enabled. Addressing headers MAY be present on SOAPRequest and MUST NOT be
	 * present on SOAPResponse
	 */
	@Test
	public void afCltEnabledNotREQSvrNotEnabledrbfSvrEnabledCltEnabledTest() throws Exception {
		TestUtil.logMsg("afCltEnabledNotREQSvrNotEnabledrbfSvrEnabledCltEnabledTest");
		logger.log(Level.INFO, "Verify Addressing headers are present on SOAPRequest and not present on SOAPResponse");
		boolean pass = true;
		try {
			// 7a) Enabled/NotRequired NotEnabled S-Enabled/C-Enabled
			port7a.addNumbers(new Holder("afCltEnabledNotREQSvrNotEnabledrbfSvrEnabledCltEnabledTest"), 10, 10);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("afCltEnabledNotREQSvrNotEnabledrbfSvrEnabledCltEnabledTest failed", e);
		}

		if (!pass) {
			throw new Exception("afCltEnabledNotREQSvrNotEnabledrbfSvrEnabledCltEnabledTest failed");
		}
	}

	/*
	 * @testName: afCltEnabledREQSvrNotEnabledrbfSvrEnabledCltEnabledTest
	 *
	 * @assertion_ids: WSAMD:SPEC:3001.1; WSAMD:SPEC:3001.2; WSAMD:SPEC:3001.4;
	 * JAXWS:SPEC:6012.2; JAXWS:SPEC:6012.3; JAXWS:SPEC:6012.4; JAXWS:SPEC:6012.6;
	 * JAXWS:SPEC:6016.1; JAXWS:SPEC:7020; JAXWS:SPEC:7020.1; JAXWS:JAVADOC:191;
	 * JAXWS:SPEC:7022; JAXWS:SPEC:7022.1; JAXWS:JAVADOC:189;
	 *
	 * @test_Strategy: Test RespectBinding Feature. Addressing Client
	 * Enabled/Required, Server NotEnabled; RespectBinding Server Enabled, Client
	 * Enabled. This scenario MUST throw back a WebServiceException.
	 */
	@Test
	public void afCltEnabledREQSvrNotEnabledrbfSvrEnabledCltEnabledTest() throws Exception {
		logger.log(Level.INFO, "afCltEnabledREQSvrNotEnabledrbfSvrEnabledCltEnabledTest");
		logger.log(Level.INFO, "Verify WebServiceException thrown");
		boolean pass = true;
		try {
			// 8a) Enabled/Required NotEnabled S-Enabled/C-Enabled
			port8a.addNumbers(new Holder("afCltEnabledREQSvrNotEnabledrbfSvrEnabledCltEnabledTest"), 10, 10);
			TestUtil.logErr("WebServiceException was not thrown back");
			pass = false;
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught expected WebServiceException: " + e.getMessage());
		} catch (Exception e) {
			TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("afCltEnabledREQSvrNotEnabledrbfSvrEnabledCltEnabledTest failed", e);
		}

		if (!pass) {
			throw new Exception("afCltEnabledREQSvrNotEnabledrbfSvrEnabledCltEnabledTest failed");
		}
	}
}
