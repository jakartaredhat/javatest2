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

package com.sun.ts.tests.jaxws.wsa.w2j.document.literal.addressingfeature;

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
import com.sun.ts.tests.jaxws.wsa.common.WsaSOAPUtils;

import jakarta.xml.ws.Holder;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.soap.AddressingFeature;
import jakarta.xml.ws.soap.SOAPFaultException;

public class Client extends BaseClient {

	// URL properties used by the test
	private static final String ENDPOINT_URL1 = "wsaw2jdladdressingfeature.endpoint.1";

	private static final String ENDPOINT_URL2 = "wsaw2jdladdressingfeature.endpoint.2";

	private static final String ENDPOINT_URL3 = "wsaw2jdladdressingfeature.endpoint.3";

	private static final String WSDLLOC_URL = "wsaw2jdladdressingfeature.wsdlloc.1";

	// service and port information
	private static final String NAMESPACEURI = "http://addressingfeatureservice.org/wsdl";

	private static final String SERVICE_NAME = "AddressingFeatureTestService";

	private static final String PORT_NAME1 = "AddressingFeatureTest1Port";

	private static final String PORT_NAME2 = "AddressingFeatureTest2Port";

	private static final String PORT_NAME3 = "AddressingFeatureTest3Port";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME1 = new QName(NAMESPACEURI, PORT_NAME1);

	private QName PORT_QNAME2 = new QName(NAMESPACEURI, PORT_NAME2);

	private QName PORT_QNAME3 = new QName(NAMESPACEURI, PORT_NAME3);

	private String url1 = null;

	private String url2 = null;

	private String url3 = null;

	private URL wsdlurl = null;

	private String ctxroot = null;

	private AddressingFeatureTest1 port1a = null;

	private AddressingFeatureTest1 port1b = null;

	private AddressingFeatureTest1 port1c = null;

	private AddressingFeatureTest2 port2a = null;

	private AddressingFeatureTest2 port2b = null;

	private AddressingFeatureTest2 port2c = null;

	private AddressingFeatureTest3 port3a = null;

	private AddressingFeatureTest3 port3b = null;

	private WebServiceFeature[] nonEnabledwsf = { new AddressingFeature(false) };

	private WebServiceFeature[] enabledRequiredwsf = { new AddressingFeature(true, true) };

	private WebServiceFeature[] enabledNotRequiredwsf = { new AddressingFeature(true, false) };

	static AddressingFeatureTestService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(ENDPOINT_URL1);
		url1 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC_URL);
		wsdlurl = ctsurl.getURL(PROTOCOL, hostname, portnum, file);

		file = JAXWS_Util.getURLFromProp(ENDPOINT_URL2);
		url2 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT_URL3);
		url3 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		logger.log(Level.INFO, "Service Endpoint URL1: " + url1);
		logger.log(Level.INFO, "Service Endpoint URL2: " + url2);
		logger.log(Level.INFO, "Service Endpoint URL3: " + url3);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
	}

	protected void getPortStandalone() throws Exception {
		logger.log(Level.INFO, "******************************Retrieving Port 1************************\n");
		// client side Addressing enabled/NotRequired; server side
		// Addressing/NotRequired
		port1a = (AddressingFeatureTest1) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, AddressingFeatureTestService.class,
				PORT_QNAME1, AddressingFeatureTest1.class, enabledNotRequiredwsf);
		JAXWS_Util.setTargetEndpointAddress(port1a, url1);

		logger.log(Level.INFO, "******************************Retrieving Port 2************************\n");
		// client side Addressing enabled/Required; server side
		// Addressing/NotRequired
		port1b = (AddressingFeatureTest1) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, AddressingFeatureTestService.class,
				PORT_QNAME1, AddressingFeatureTest1.class, enabledRequiredwsf);
		JAXWS_Util.setTargetEndpointAddress(port1b, url1);

		logger.log(Level.INFO, "******************************Retrieving Port 3************************\n");
		// client side Addressing off; server side Addressing/NotRequired
		port1c = (AddressingFeatureTest1) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, AddressingFeatureTestService.class,
				PORT_QNAME1, AddressingFeatureTest1.class, nonEnabledwsf);
		JAXWS_Util.setTargetEndpointAddress(port1c, url1);

		logger.log(Level.INFO, "******************************Retrieving Port 4************************\n");
		// client side Addressing enabled/NotRequired; server side
		// Addressing/Required
		port2a = (AddressingFeatureTest2) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, AddressingFeatureTestService.class,
				PORT_QNAME2, AddressingFeatureTest2.class, enabledNotRequiredwsf);
		JAXWS_Util.setTargetEndpointAddress(port2a, url2);

		logger.log(Level.INFO, "******************************Retrieving Port 5************************\n");
		// client side Addressing enabled/Required; server side Addressing/Required
		port2b = (AddressingFeatureTest2) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, AddressingFeatureTestService.class,
				PORT_QNAME2, AddressingFeatureTest2.class, enabledRequiredwsf);
		JAXWS_Util.setTargetEndpointAddress(port2b, url2);

		logger.log(Level.INFO, "******************************Retrieving Port 6************************\n");
		// client side Addressing off; server side Addressing/Required
		port2c = (AddressingFeatureTest2) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, AddressingFeatureTestService.class,
				PORT_QNAME2, AddressingFeatureTest2.class, nonEnabledwsf);
		JAXWS_Util.setTargetEndpointAddress(port2c, url2);

		logger.log(Level.INFO, "******************************Retrieving Port 7************************\n");
		// client side Addressing enabled/NotRequired; server side off (in WSDL
		// enabled, but overridden to false in IMPL to turn it off)
		port3a = (AddressingFeatureTest3) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, AddressingFeatureTestService.class,
				PORT_QNAME3, AddressingFeatureTest3.class, enabledNotRequiredwsf);
		JAXWS_Util.setTargetEndpointAddress(port3a, url3);

		logger.log(Level.INFO, "******************************Retrieving Port 8************************\n");
		// client side Addressing enabled/Required; server side off (in WSDL
		// enabled, but overridden to false in IMPL to turn it off)
		port3b = (AddressingFeatureTest3) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, AddressingFeatureTestService.class,
				PORT_QNAME3, AddressingFeatureTest3.class, enabledRequiredwsf);
		JAXWS_Util.setTargetEndpointAddress(port3b, url3);
	}

	protected void getPortJavaEE() throws Exception {
		try {
			logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
			logger.log(Level.INFO, "service=" + service);

			port1a = (AddressingFeatureTest1) service.getPort(AddressingFeatureTest1.class, enabledNotRequiredwsf);
			JAXWS_Util.dumpTargetEndpointAddress(port1a);
			JAXWS_Util.setSOAPLogging(port1a);
			port1b = (AddressingFeatureTest1) service.getPort(AddressingFeatureTest1.class, enabledRequiredwsf);
			JAXWS_Util.dumpTargetEndpointAddress(port1b);
			JAXWS_Util.setSOAPLogging(port1b);
			port1c = (AddressingFeatureTest1) service.getPort(AddressingFeatureTest1.class, nonEnabledwsf);
			JAXWS_Util.dumpTargetEndpointAddress(port1c);
			JAXWS_Util.setSOAPLogging(port1c);

			port2a = (AddressingFeatureTest2) service.getPort(AddressingFeatureTest2.class, enabledNotRequiredwsf);
			JAXWS_Util.dumpTargetEndpointAddress(port2a);
			JAXWS_Util.setSOAPLogging(port2a);
			port2b = (AddressingFeatureTest2) service.getPort(AddressingFeatureTest2.class, enabledRequiredwsf);
			JAXWS_Util.dumpTargetEndpointAddress(port2b);
			JAXWS_Util.setSOAPLogging(port2b);
			port2c = (AddressingFeatureTest2) service.getPort(AddressingFeatureTest2.class, nonEnabledwsf);
			JAXWS_Util.dumpTargetEndpointAddress(port2c);
			JAXWS_Util.setSOAPLogging(port2c);

			port3a = (AddressingFeatureTest3) service.getPort(AddressingFeatureTest3.class, enabledNotRequiredwsf);
			JAXWS_Util.dumpTargetEndpointAddress(port3a);
			JAXWS_Util.setSOAPLogging(port3a);
			port3b = (AddressingFeatureTest3) service.getPort(AddressingFeatureTest3.class, enabledRequiredwsf);
			JAXWS_Util.dumpTargetEndpointAddress(port3b);
			JAXWS_Util.setSOAPLogging(port3b);

		} catch (Throwable t) {
			TestUtil.printStackTrace(t);
			throw new Exception(t.toString());
		}

		// debug dumping of ports
		Object[] portsTodump = new Object[] { port1a, port1b, port1c, port2a, port2b, port2c, port3a, port3b };
		dumpTargetEndpointAddressForPort(portsTodump);
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

	protected void getService() {
		service = (AddressingFeatureTestService) getSharedObject();
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
	 * @testName: afClientEnabledNotREQServerEnabledNotREQTest
	 *
	 * @assertion_ids: WSAMD:SPEC:3001.1; WSAMD:SPEC:3001.2; WSAMD:SPEC:3001.4;
	 * JAXWS:SPEC:6011; JAXWS:SPEC:6011.2; JAXWS:SPEC:6012; JAXWS:SPEC:6012.1;
	 * JAXWS:SPEC:6012.2; JAXWS:SPEC:6012.4; JAXWS:SPEC:6012.5; JAXWS:SPEC:7020;
	 * JAXWS:SPEC:7020.2; JAXWS:SPEC:10025; JAXWS:JAVADOC:190;
	 *
	 * @test_Strategy: Test Addressing Feature. Client enabled/NotRequired, Server
	 * enabled/NotRequired. Addressing headers MAY be present on SOAPRequest and
	 * SOAPResponse since Addressing is Optional. If addressing headers exist check
	 * them otherwise don't.
	 */
	@Test
	public void afClientEnabledNotREQServerEnabledNotREQTest() throws Exception {
		logger.log(Level.INFO, "afClientEnabledNotREQServerEnabledNotREQTest");
		logger.log(Level.INFO, "Verify Addressing headers may be present on SOAPRequest and SOAPResponse");
		boolean pass = true;
		try {
			port1a.addNumbers(new Holder("ClientEnabledNotREQServerEnabledNotREQ"), 10, 10);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("afClientEnabledNotREQServerEnabledNotREQTest failed", e);
		}

		if (!pass) {
			throw new Exception("afClientEnabledNotREQServerEnabledNotREQTest failed");
		}
	}

	/*
	 * @testName: afClientEnabledREQServerEnabledNotREQTest
	 *
	 * @assertion_ids: WSAMD:SPEC:3001.1; WSAMD:SPEC:3001.2; WSAMD:SPEC:3001.4;
	 * JAXWS:SPEC:6011; JAXWS:SPEC:6011.2; JAXWS:SPEC:6012; JAXWS:SPEC:6012.1;
	 * JAXWS:SPEC:6012.2; JAXWS:SPEC:6012.4; JAXWS:SPEC:6012.5; JAXWS:SPEC:7020;
	 * JAXWS:SPEC:7020.2; JAXWS:SPEC:10025; JAXWS:SPEC:4031; JAXWS:JAVADOC:190;
	 *
	 * @test_Strategy: Test Addressing Feature. Client enabled/Required, Server
	 * enabled/NotRequired. Addressing headers MUST be present on SOAPRequest and
	 * SOAPResponse.
	 */
	@Test
	public void afClientEnabledREQServerEnabledNotREQTest() throws Exception {
		logger.log(Level.INFO, "afClientEnabledREQServerEnabledNotREQTest");
		logger.log(Level.INFO, "Verify Addressing headers are present on SOAPRequest and SOAPResponse");
		boolean pass = true;
		try {
			port1b.addNumbers(new Holder("ClientEnabledREQServerEnabledNotREQ"), 10, 10);
		} catch (Exception e) {
			TestUtil.logErr("Caught Exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("afClientEnabledREQServerEnabledNotREQTest failed", e);
		}

		if (!pass) {
			throw new Exception("afClientEnabledREQServerEnabledNotREQTest failed");
		}
	}

	/*
	 * @testName: afClientNotEnabledServerEnabledNotREQTest
	 *
	 * @assertion_ids: WSAMD:SPEC:3001.1; WSAMD:SPEC:3001.3; WSAMD:SPEC:3001.4;
	 * JAXWS:SPEC:6011; JAXWS:SPEC:6011.2; JAXWS:SPEC:6012; JAXWS:SPEC:6012.1;
	 * JAXWS:SPEC:6012.3; JAXWS:SPEC:6012.5; JAXWS:SPEC:7020; JAXWS:SPEC:4031;
	 * JAXWS:SPEC:7020.2; JAXWS:SPEC:10025; JAXWS:JAVADOC:190;
	 *
	 * @test_Strategy: Test Addressing Feature. Client Not Enabled, Server
	 * enabled/NotRequired. Addressing headers MUST not be present on SOAPRequest
	 * and SOAPResponse.
	 */
	@Test
	public void afClientNotEnabledServerEnabledNotREQTest() throws Exception {
		logger.log(Level.INFO, "afClientNotEnabledServerEnabledNotREQTest");
		logger.log(Level.INFO, "Verify Addressing headers are not present on SOAPRequest and SOAPResponse");
		boolean pass = true;
		try {
			port1c.addNumbers(new Holder("ClientNotEnabledServerEnabledNotREQ"), 10, 10);
		} catch (Exception e) {
			TestUtil.logErr("Caught Exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("afClientNotEnabledServerEnabledNotREQTest failed", e);
		}

		if (!pass) {
			throw new Exception("afClientNotEnabledServerEnabledNotREQTest failed");
		}
	}

	/*
	 * @testName: afClientEnabledNotREQServerEnabledREQTest
	 *
	 * @assertion_ids: WSAMD:SPEC:3001.1; WSAMD:SPEC:3001.2; WSAMD:SPEC:3001.4;
	 * JAXWS:SPEC:6011; JAXWS:SPEC:6011.2; JAXWS:SPEC:6012; JAXWS:SPEC:6012.1;
	 * JAXWS:SPEC:6012.2; JAXWS:SPEC:6012.4; JAXWS:SPEC:6012.5; JAXWS:SPEC:3046;
	 *
	 * @test_Strategy: Test Addressing Feature. Client enabled/NotRequired, Server
	 * enabled/Required. If the Client does not send Addressing headers then the
	 * Server MUST throw back a SOAP Exception with a
	 * MessageAddressingHeaderRequired Exception code since the Server mandates
	 * Addressing Required. If the Client does send Addressing headers then they
	 * MUST be present on SOAPRequest and SOAPResponse since the Server mandates
	 * requires addressing.
	 */
	@Test
	public void afClientEnabledNotREQServerEnabledREQTest() throws Exception {
		logger.log(Level.INFO, "afClientEnabledNotREQServerEnabledREQTest");
		logger.log(Level.INFO, "Verify Addressing headers may be present on SOAPRequest and SOAPResponse");
		logger.log(Level.INFO, "or a MessageAddressingHeaderRequired soap Exception is thrown by endpoint");
		boolean pass = true;
		try {
			port2a.addNumbers(new Holder("ClientEnabledNotREQServerEnabledREQ"), 10, 10);
		} catch (SOAPFaultException sfe) {
			try {
				TestUtil.logMsg("Caught expected SOAPFaultException: " + sfe.getMessage());
				logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
				logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(sfe));
				logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(sfe));
				if (WsaSOAPUtils.isMessageAddressingHeaderRequiredFaultCode(sfe)) {
					logger.log(Level.INFO, "SOAPFault contains expected Exceptioncode MessageAddressingHeaderRequired");
				} else {
					String Exceptioncode = WsaSOAPUtils.getFaultCode(sfe);
					TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
							+ ", expected: MessageAddressingHeaderRequired");
					pass = false;
				}
				if (WsaSOAPUtils.getFaultString(sfe) == null) {
					TestUtil.logErr("The Exceptionstring element MUST EXIST for SOAP 1.1 Exceptions");
					pass = false;
				}
				if (WsaSOAPUtils.getFaultDetail(sfe) != null) {
					TestUtil.logErr("The Exceptiondetail element MUST NOT EXIST for SOAP 1.1 "
							+ "Faults related to header entries");
					pass = false;
				}
			} catch (Exception e2) {
				TestUtil.logErr("Caught unexpected exception: " + e2.getMessage());
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("afClientEnabledNotREQServerEnabledREQTest failed", e);
		}

		if (!pass) {
			throw new Exception("afClientEnabledNotREQServerEnabledREQTest failed");
		}
	}

	/*
	 * @testName: afClientEnabledREQServerEnabledREQTest
	 *
	 * @assertion_ids: WSAMD:SPEC:3001.1; WSAMD:SPEC:3001.2; WSAMD:SPEC:3001.4;
	 * JAXWS:SPEC:6011; JAXWS:SPEC:6011.2; JAXWS:SPEC:6012; JAXWS:SPEC:6012.1;
	 * JAXWS:SPEC:6012.2; JAXWS:SPEC:6012.4; JAXWS:SPEC:6012.5; JAXWS:SPEC:3046;
	 *
	 * @test_Strategy: Test Addressing Feature. Client enabled/Required, Server
	 * enabled/Required. Addressing headers MUST be present on SOAPRequest and
	 * SOAPResponse.
	 */
	@Test
	public void afClientEnabledREQServerEnabledREQTest() throws Exception {
		logger.log(Level.INFO, "afClientEnabledREQServerEnabledREQTest");
		logger.log(Level.INFO, "Verify Addressing headers are present on SOAPRequest and SOAPResponse");
		boolean pass = true;
		try {
			port2b.addNumbers(new Holder("ClientEnabledREQServerEnabledREQ"), 10, 10);
		} catch (Exception e) {
			TestUtil.logErr("Caught Exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("afClientEnabledREQServerEnabledREQTest failed", e);
		}

		if (!pass) {
			throw new Exception("afClientEnabledREQServerEnabledREQTest failed");
		}
	}

	/*
	 * @testName: afClientNotEnabledServerEnabledREQTest
	 *
	 * @assertion_ids: WSAMD:SPEC:3001.1; WSAMD:SPEC:3001.3; WSAMD:SPEC:3001.4;
	 * JAXWS:SPEC:4031; JAXWS:SPEC:6011; JAXWS:SPEC:6011.2; JAXWS:SPEC:6012;
	 * JAXWS:SPEC:6012.1; JAXWS:SPEC:6012.3; JAXWS:SPEC:6012.5; JAXWS:SPEC:3046;
	 * WSASB:SPEC:6004.3;
	 *
	 * @test_Strategy: Test Addressing Feature. Client Not Enabled, Server
	 * enabled/Required. This scenario MUST throw back a SOAP Exception. Make sure
	 * the SOAP Exception has the correct information in it. The SOAP Exception
	 * Exceptioncode must be: MessageAddressingHeaderRequired.
	 */
	@Test
	public void afClientNotEnabledServerEnabledREQTest() throws Exception {
		logger.log(Level.INFO, "afClientNotEnabledServerEnabledREQTest");
		logger.log(Level.INFO, "Verify MessageAddressingHeaderRequired soap Exception is thrown by endpoint");
		boolean pass = true;
		try {
			port2c.addNumbers(new Holder("ClientNotEnabledServerEnabledREQ"), 10, 10);
			TestUtil.logErr("SOAPFaultException was not thrown back");
			pass = false;
		} catch (SOAPFaultException sfe) {
			try {
				TestUtil.logMsg("Caught expected SOAPFaultException: " + sfe.getMessage());
				logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
				logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(sfe));
				logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(sfe));
				if (WsaSOAPUtils.isMessageAddressingHeaderRequiredFaultCode(sfe)) {
					logger.log(Level.INFO, "SOAPFault contains expected Exceptioncode MessageAddressingHeaderRequired");
				} else {
					String Exceptioncode = WsaSOAPUtils.getFaultCode(sfe);
					TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
							+ ", expected: MessageAddressingHeaderRequired");
					pass = false;
				}
				if (WsaSOAPUtils.getFaultString(sfe) == null) {
					TestUtil.logErr("The Exceptionstring element MUST EXIST for SOAP 1.1 Exceptions");
					pass = false;
				}
				if (WsaSOAPUtils.getFaultDetail(sfe) != null) {
					TestUtil.logErr("The Exceptiondetail element MUST NOT EXIST for SOAP 1.1 "
							+ "Faults related to header entries");
					pass = false;
				}
			} catch (Exception e2) {
				TestUtil.logErr("Caught unexpected exception: " + e2.getMessage());
				throw new Exception("afClientNotEnabledServerEnabledREQTest failed", e2);
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught Exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("afClientNotEnabledServerEnabledREQTest failed", e);
		}

		if (!pass) {
			throw new Exception("afClientNotEnabledServerEnabledREQTest failed");
		}
	}

	/*
	 * @testName: afClientEnabledNotREQServerNotEnabledTest
	 *
	 * @assertion_ids: WSAMD:SPEC:3001.1; WSAMD:SPEC:3001.2; WSAMD:SPEC:3001.4;
	 * JAXWS:SPEC:6012.2; JAXWS:SPEC:6012.3; JAXWS:SPEC:6012.4; JAXWS:SPEC:6012.6;
	 * JAXWS:SPEC:6016.1; JAXWS:SPEC:7020; JAXWS:SPEC:7020.1; JAXWS:JAVADOC:191;
	 *
	 * @test_Strategy: Test Addressing Feature. Client enabled/NotRequired, Server
	 * notenabled. Addressing headers MAY be present in the SOAPRequest but MUST not
	 * be present in the SOAPResponse.
	 */
	@Test
	public void afClientEnabledNotREQServerNotEnabledTest() throws Exception {
		logger.log(Level.INFO, "afClientEnabledNotREQServerEnableNotREQTest");
		TestUtil.logMsg("Verify Addressing headers are NOT present on SOAPResponse");
		boolean pass = true;
		try {
			port3a.addNumbers(new Holder("ClientEnabledNotREQServerNotEnabled"), 10, 10);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("afClientEnabledNotREQServerNotEnabledTest failed", e);
		}

		if (!pass) {
			throw new Exception("afClientEnabledNotREQServerNotEnabledTest failed");
		}
	}

	/*
	 * @testName: afClientEnabledREQServerNotEnabledTest
	 *
	 * @assertion_ids: WSAMD:SPEC:3001.1; WSAMD:SPEC:3001.2; WSAMD:SPEC:3001.4;
	 * JAXWS:SPEC:6012.2; JAXWS:SPEC:6012.3; JAXWS:SPEC:6012.4; JAXWS:SPEC:6012.6;
	 * JAXWS:SPEC:6016.1; JAXWS:SPEC:7020; JAXWS:SPEC:7020.1; JAXWS:JAVADOC:191;
	 *
	 * @test_Strategy: Test Addressing Feature. Client enabled/Required, Server not
	 * enabled. This scenario MUST throw back a WebServiceException.
	 */
	@Test
	public void afClientEnabledREQServerNotEnabledTest() throws Exception {
		logger.log(Level.INFO, "afClientEnabledREQServerNotEnabledTest");
		logger.log(Level.INFO, "Verify WebServiceException is thrown");
		boolean pass = true;
		try {
			port3b.addNumbers(new Holder("ClientEnabledREQServerNotEnabled"), 10, 10);
			TestUtil.logErr("WebServiceException was not thrown back");
			pass = false;
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught expected WebServiceException: " + e.getMessage());
		} catch (Exception e) {
			TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("afClientEnabledREQServerNotEnabledTest failed", e);
		}

		if (!pass) {
			throw new Exception("afClientEnabledREQServerNotEnabledTest failed");
		}
	}

}
