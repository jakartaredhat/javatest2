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
package com.sun.ts.tests.jaxws.wsa.j2w.document.literal.addressingfeature;

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

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.wsa.j2w.document.literal.addressingfeature.";

	// URL properties used by the test
	private static final String ENDPOINT_URL1 = "wsaj2wdladdressingfeaturetest.endpoint.1";

	private static final String ENDPOINT_URL2 = "wsaj2wdladdressingfeaturetest.endpoint.2";

	private static final String ENDPOINT_URL3 = "wsaj2wdladdressingfeaturetest.endpoint.3";

	private static final String ENDPOINT_URL4 = "wsaj2wdladdressingfeaturetest.endpoint.4";

	private static final String WSDLLOC_URL1 = "wsaj2wdladdressingfeaturetest.wsdlloc.1";

	private static final String WSDLLOC_URL2 = "wsaj2wdladdressingfeaturetest.wsdlloc.2";

	private static final String WSDLLOC_URL3 = "wsaj2wdladdressingfeaturetest.wsdlloc.3";

	private static final String WSDLLOC_URL4 = "wsaj2wdladdressingfeaturetest.wsdlloc.4";

	// service and port information
	private static final String NAMESPACEURI = "http://addressingfeatureservice.org/wsdl";

	private static final String SERVICE1_NAME = "AddressingFeatureTest1Service";

	private static final String SERVICE2_NAME = "AddressingFeatureTest2Service";

	private static final String SERVICE3_NAME = "AddressingFeatureTest3Service";

	private static final String SERVICE4_NAME = "AddressingFeatureTest4Service";

	private static final String PORT_NAME1 = "AddressingFeatureTest1Port";

	private static final String PORT_NAME2 = "AddressingFeatureTest2Port";

	private static final String PORT_NAME3 = "AddressingFeatureTest3Port";

	private static final String PORT_NAME4 = "AddressingFeatureTest4Port";

	private QName SERVICE1_QNAME = new QName(NAMESPACEURI, SERVICE1_NAME);

	private QName SERVICE2_QNAME = new QName(NAMESPACEURI, SERVICE2_NAME);

	private QName SERVICE3_QNAME = new QName(NAMESPACEURI, SERVICE3_NAME);

	private QName SERVICE4_QNAME = new QName(NAMESPACEURI, SERVICE4_NAME);

	private QName PORT_QNAME1 = new QName(NAMESPACEURI, PORT_NAME1);

	private QName PORT_QNAME2 = new QName(NAMESPACEURI, PORT_NAME2);

	private QName PORT_QNAME3 = new QName(NAMESPACEURI, PORT_NAME3);

	private QName PORT_QNAME4 = new QName(NAMESPACEURI, PORT_NAME4);

	private String url1 = null;

	private String url2 = null;

	private String url3 = null;

	private String url4 = null;

	private URL wsdlurl1 = null;

	private URL wsdlurl2 = null;

	private URL wsdlurl3 = null;

	private URL wsdlurl4 = null;

	private String ctxroot = null;

	private AddressingFeatureTest1 port1 = null;

	private AddressingFeatureTest2 port2 = null;

	private AddressingFeatureTest3 port3 = null;

	private AddressingFeatureTest4 port4 = null;

	private AddressingFeatureTest1 port5 = null;

	private AddressingFeatureTest2 port6 = null;

	private AddressingFeatureTest4 port7 = null;

	private WebServiceFeature[] enabledNotRequiredwsf = { new AddressingFeature(true, false) };

	private WebServiceFeature[] nonEnabledwsf = { new AddressingFeature(false) };

	private WebServiceFeature[] enabledRequiredwsf = { new AddressingFeature(true, true) };

	static AddressingFeatureTest1Service service1 = null;

	static AddressingFeatureTest2Service service2 = null;

	static AddressingFeatureTest3Service service3 = null;

	static AddressingFeatureTest4Service service4 = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(ENDPOINT_URL1);
		url1 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC_URL1);
		wsdlurl1 = ctsurl.getURL(PROTOCOL, hostname, portnum, file);

		String file2 = JAXWS_Util.getURLFromProp(ENDPOINT_URL2);
		url2 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file2);
		file2 = JAXWS_Util.getURLFromProp(WSDLLOC_URL2);
		wsdlurl2 = ctsurl.getURL(PROTOCOL, hostname, portnum, file2);

		String file3 = JAXWS_Util.getURLFromProp(ENDPOINT_URL3);
		url3 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file3);
		file3 = JAXWS_Util.getURLFromProp(WSDLLOC_URL3);
		wsdlurl3 = ctsurl.getURL(PROTOCOL, hostname, portnum, file3);

		String file4 = JAXWS_Util.getURLFromProp(ENDPOINT_URL4);
		url4 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file4);
		file4 = JAXWS_Util.getURLFromProp(WSDLLOC_URL4);
		wsdlurl4 = ctsurl.getURL(PROTOCOL, hostname, portnum, file4);

		logger.log(Level.INFO, "Service Endpoint URL1: " + url1);
		logger.log(Level.INFO, "Service Endpoint URL2: " + url2);
		logger.log(Level.INFO, "Service Endpoint URL3: " + url3);
		logger.log(Level.INFO, "Service Endpoint URL4: " + url4);
		logger.log(Level.INFO, "WSDL Location URL1:    " + wsdlurl1);
		logger.log(Level.INFO, "WSDL Location URL2:    " + wsdlurl2);
		logger.log(Level.INFO, "WSDL Location URL3:    " + wsdlurl3);
		logger.log(Level.INFO, "WSDL Location URL4:    " + wsdlurl4);
	}

	protected void getPortStandalone() throws Exception {
		logger.log(Level.INFO, "******************************Retrieving Port 1************************\n");
		// client side Addressing enabled/NotRequired; server side
		// Addressing/NotRequired
		port1 = (AddressingFeatureTest1) JAXWS_Util.getPort(wsdlurl1, SERVICE1_QNAME,
				AddressingFeatureTest1Service.class, PORT_QNAME1, AddressingFeatureTest1.class, enabledNotRequiredwsf);
		JAXWS_Util.setTargetEndpointAddress(port1, url1);

		logger.log(Level.INFO, "******************************Retrieving Port 2************************\n");
		// client side Addressing enabled/NotRequired; server side
		// Addressing/Required
		port2 = (AddressingFeatureTest2) JAXWS_Util.getPort(wsdlurl2, SERVICE2_QNAME,
				AddressingFeatureTest2Service.class, PORT_QNAME2, AddressingFeatureTest2.class, enabledNotRequiredwsf);
		JAXWS_Util.setTargetEndpointAddress(port2, url2);

		logger.log(Level.INFO, "******************************Retrieving Port 3************************\n");
		// client side Addressing enabled/NotRequired; server side Addressing (using
		// default)
		port3 = (AddressingFeatureTest3) JAXWS_Util.getPort(wsdlurl3, SERVICE3_QNAME,
				AddressingFeatureTest3Service.class, PORT_QNAME3, AddressingFeatureTest3.class, enabledNotRequiredwsf);
		JAXWS_Util.setTargetEndpointAddress(port3, url3);

		logger.log(Level.INFO, "******************************Retrieving Port 4************************\n");
		// client side Addressing enabled/NotRequired; server side Addressing off
		port4 = (AddressingFeatureTest4) JAXWS_Util.getPort(wsdlurl4, SERVICE4_QNAME,
				AddressingFeatureTest4Service.class, PORT_QNAME4, AddressingFeatureTest4.class, enabledNotRequiredwsf);
		JAXWS_Util.setTargetEndpointAddress(port4, url4);

		logger.log(Level.INFO, "******************************Retrieving Port 5************************\n");
		// client side Addressing off; server side Addressing/NotRequired
		port5 = (AddressingFeatureTest1) JAXWS_Util.getPort(wsdlurl1, SERVICE1_QNAME,
				AddressingFeatureTest1Service.class, PORT_QNAME1, AddressingFeatureTest1.class, nonEnabledwsf);
		JAXWS_Util.setTargetEndpointAddress(port5, url1);

		logger.log(Level.INFO, "******************************Retrieving Port 6************************\n");
		// client side Addressing off; server side Addressing/Required
		port6 = (AddressingFeatureTest2) JAXWS_Util.getPort(wsdlurl2, SERVICE2_QNAME,
				AddressingFeatureTest2Service.class, PORT_QNAME2, AddressingFeatureTest2.class, nonEnabledwsf);
		JAXWS_Util.setTargetEndpointAddress(port6, url2);

		logger.log(Level.INFO, "******************************Retrieving Port 7************************\n");
		// client side Addressing enabled/Required; server side off
		port7 = (AddressingFeatureTest4) JAXWS_Util.getPort(wsdlurl4, SERVICE4_QNAME,
				AddressingFeatureTest4Service.class, PORT_QNAME4, AddressingFeatureTest4.class, enabledRequiredwsf);
		JAXWS_Util.setTargetEndpointAddress(port7, url4);
	}

	protected void getPortJavaEE() throws Exception {
		javax.naming.InitialContext ic = new javax.naming.InitialContext();
		logger.log(Level.INFO, "Obtain service1 via WebServiceRef annotation");
		AddressingFeatureTest1Service service1 = (AddressingFeatureTest1Service) ic
				.lookup("java:comp/env/service/WSAJ2WDLAddressingFeatureTest1");
		logger.log(Level.INFO, "service1=" + service1);
		logger.log(Level.INFO, "******************************Retrieving Port 1************************\n");
		port1 = (AddressingFeatureTest1) service1.getPort(AddressingFeatureTest1.class, enabledNotRequiredwsf);

		logger.log(Level.INFO, "Obtain service2 via WebServiceRef annotation");
		AddressingFeatureTest2Service service2 = (AddressingFeatureTest2Service) ic
				.lookup("java:comp/env/service/WSAJ2WDLAddressingFeatureTest2");
		logger.log(Level.INFO, "service2=" + service2);
		logger.log(Level.INFO, "******************************Retrieving Port 2************************\n");
		port2 = (AddressingFeatureTest2) service2.getPort(AddressingFeatureTest2.class, enabledNotRequiredwsf);

		logger.log(Level.INFO, "Obtain service3 via WebServiceRef annotation");
		AddressingFeatureTest3Service service3 = (AddressingFeatureTest3Service) ic
				.lookup("java:comp/env/service/WSAJ2WDLAddressingFeatureTest3");
		logger.log(Level.INFO, "service3=" + service3);
		logger.log(Level.INFO, "******************************Retrieving Port 3************************\n");
		port3 = (AddressingFeatureTest3) service3.getPort(AddressingFeatureTest3.class, enabledNotRequiredwsf);

		logger.log(Level.INFO, "Obtain service4 via WebServiceRef annotation");
		AddressingFeatureTest4Service service4 = (AddressingFeatureTest4Service) ic
				.lookup("java:comp/env/service/WSAJ2WDLAddressingFeatureTest4");
		logger.log(Level.INFO, "service4=" + service4);
		logger.log(Level.INFO, "******************************Retrieving Port 4************************\n");
		port4 = (AddressingFeatureTest4) service4.getPort(AddressingFeatureTest4.class, enabledNotRequiredwsf);

		logger.log(Level.INFO, "******************************Retrieving Port 5************************\n");
		port5 = (AddressingFeatureTest1) service1.getPort(AddressingFeatureTest1.class, nonEnabledwsf);

		logger.log(Level.INFO, "******************************Retrieving Port 6************************\n");
		port6 = (AddressingFeatureTest2) service2.getPort(AddressingFeatureTest2.class, nonEnabledwsf);

		logger.log(Level.INFO, "******************************Retrieving Port 7************************\n");
		port7 = (AddressingFeatureTest4) service4.getPort(AddressingFeatureTest4.class, enabledRequiredwsf);

		Object[] portsTodump = new Object[] { port1, port2, port3, port4, port5, port6, port7 };
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

	/* Test setup */

	/*
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * 
	 * @class.setup_props: webServerHost; webServerPort; platform.mode;
	 */
	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		logger.log(Level.INFO, "setup ok");

	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: afClientEnabledNotREQServerEnabledNotREQTest
	 *
	 * @assertion_ids: JAXWS:SPEC:3047;
	 *
	 * @test_Strategy: Test Addressing Feature. Client enabled/NotRequired, Server
	 * enabled/NotRequired. Addressing headers MAY be present on SOAPRequest and
	 * SOAPResponse since Addressing is Optional. If addressing headers exist check
	 * them otherwise don't.
	 */
	@Test
	public void afClientEnabledNotREQServerEnabledNotREQTest() throws Exception {
		logger.log(Level.INFO, "afClientEnabledNotREQServerEnabledNotREQTest");
		boolean pass = true;
		try {
			// client side and server side Addressing enabled/NotRequired
			port1.addNumbers1(new Holder("ClientEnabledNotREQServerEnabledNotREQ"), 10, 10);
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
	 * @testName: afClientEnabledNotREQServerEnabledREQTest
	 *
	 * @assertion_ids: JAXWS:SPEC:3047;
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
		boolean pass = true;
		try {
			// client side Addressing enabled/NotRequired; server side
			// Addressing/Required
			port2.addNumbers2(new Holder("ClientEnabledNotREQServerEnabledREQ"), 10, 10);
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
	 * @testName: afClientEnabledNotREQServerUsingDefaultsTest
	 *
	 * @assertion_ids: JAXWS:SPEC:3047;
	 *
	 * @test_Strategy: Test Addressing Feature. Client enabled/NotRequired, Server
	 * using defaults. Addressing headers MAY be present on SOAPRequest and
	 * SOAPResponse since Addressing is Optional. If addressing headers exist check
	 * them otherwise don't.
	 */
	@Test
	public void afClientEnabledNotREQServerUsingDefaultsTest() throws Exception {
		logger.log(Level.INFO, "afClientEnabledNotREQServerUsingDefaultsTest");
		boolean pass = true;
		try {
			// client side Addressing enabled/NotRequired; Server side using defaults
			port3.addNumbers3(new Holder("ClientEnabledNotREQServerUsingDefaults"), 10, 10);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("afClientEnabledNotREQServerUsingDefaultsTest failed", e);
		}

		if (!pass) {
			throw new Exception("afClientEnabledNotREQServerUsingDefaultsTest failed");
		}
	}

	/*
	 * @testName: afClientEnabledNotREQServerNotEnabledTest
	 *
	 * @assertion_ids: JAXWS:SPEC:3047;
	 *
	 * @test_Strategy: Test Addressing Feature. Client enabled/NotRequired, Server
	 * NotEnabled. Addressing headers MAY be present on SOAPRequest but MUST NOT be
	 * present on SOAPResponse.
	 */
	@Test
	public void afClientEnabledNotREQServerNotEnabledTest() throws Exception {
		logger.log(Level.INFO, "afClientEnabledNotREQServerNotEnabledTest");
		boolean pass = true;
		try {
			// client side Addressing enabled/NotRequired; Server
			// NotEnabled/NotRequired
			port4.addNumbers4(new Holder("ClientEnabledNotREQServerNotEnabled"), 10, 10);
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
	 * @testName: afClientNotEnabledServerEnabledNotREQTest
	 *
	 * @assertion_ids: WSAMD:SPEC:3001.1; WSAMD:SPEC:3001.3; WSAMD:SPEC:3001.4;
	 * JAXWS:SPEC:6011; JAXWS:SPEC:6011.2; JAXWS:SPEC:6012; JAXWS:SPEC:6012.3;
	 * JAXWS:SPEC:6012.5; JAXWS:SPEC:7020; JAXWS:SPEC:7020.2; JAXWS:SPEC:10025;
	 * JAXWS:JAVADOC:190; JAXWS:SPEC:4034; JAXWS:SPEC:6012.1;
	 *
	 * @test_Strategy: Test Addressing Feature. Client NotEnabled, Server
	 * enabled/NotRequired. Addressing headers MUST not be present on SOAPRequest
	 * and SOAPResponse.
	 */
	@Test
	public void afClientNotEnabledServerEnabledNotREQTest() throws Exception {
		logger.log(Level.INFO, "afClientNotEnabledServerEnabledNotREQTest");
		logger.log(Level.INFO, "Verify Addressing headers are NOT present on SOAPRequest and SOAPResponse");
		boolean pass = true;
		try {
			port5.addNumbers1(new Holder("ClientNotEnabledServerEnabledNotREQ"), 10, 10);
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
			port6.addNumbers2(new Holder("ClientNotEnabledServerEnabledREQ"), 10, 10);
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
			port7.addNumbers4(new Holder("ClientEnabledREQServerNotEnabled"), 10, 10);
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
