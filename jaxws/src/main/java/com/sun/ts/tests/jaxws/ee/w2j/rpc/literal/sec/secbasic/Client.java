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

package com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.sec.secbasic;

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

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;

import jakarta.xml.ws.WebServiceException;

public class Client extends BaseClient {
	private static final long serialVersionUID = 1L;

	
	String vehicle = null;

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.sec.secbasic.";

	// service and port information
	private static final String NAMESPACEURI = "http://BasicAuthServiceTestService.org/wsdl";

	private static final String SERVICE_NAME = "BasicAuthServiceTestService";

	private static final String PORT_NAME1 = "HelloUnprotectedPort";

	private static final String PORT_NAME2 = "HelloProtectedPort";

	private static final String PORT_NAME3 = "HelloGuestPort";

	private static final String PORT_NAME4 = "HelloProtectedPort1";

	private static final String PORT_NAME5 = "HelloProtectedPort2";

	private static final String PORT_NAME6 = "HelloProtectedPort3";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME1 = new QName(NAMESPACEURI, PORT_NAME1);

	private QName PORT_QNAME2 = new QName(NAMESPACEURI, PORT_NAME2);

	private QName PORT_QNAME3 = new QName(NAMESPACEURI, PORT_NAME3);

	private QName PORT_QNAME4 = new QName(NAMESPACEURI, PORT_NAME4);

	private QName PORT_QNAME5 = new QName(NAMESPACEURI, PORT_NAME5);

	private QName PORT_QNAME6 = new QName(NAMESPACEURI, PORT_NAME6);

	// URL properties used by the test
	private static final String ENDPOINT1_URL = "secbasic.endpoint.1";

	private static final String ENDPOINT2_URL = "secbasic.endpoint.2";

	private static final String ENDPOINT3_URL = "secbasic.endpoint.3";

	private static final String ENDPOINT4_URL = "secbasic.endpoint.4";

	private static final String ENDPOINT5_URL = "secbasic.endpoint.5";

	private static final String ENDPOINT6_URL = "secbasic.endpoint.6";

	private static final String WSDLLOC1_URL = "secbasic.wsdlloc.1";

	private static final String WSDLLOC2_URL = "secbasic.wsdlloc.2";

	private static final String WSDLLOC3_URL = "secbasic.wsdlloc.3";

	private static final String WSDLLOC4_URL = "secbasic.wsdlloc.4";

	private static final String WSDLLOC5_URL = "secbasic.wsdlloc.5";

	private static final String WSDLLOC6_URL = "secbasic.wsdlloc.6";

	private String request = null;

	private String url1 = null;

	private String url2 = null;

	private String url3 = null;

	private String url4 = null;

	private String url5 = null;

	private String url6 = null;

	private URL wsdlurl1 = null;

	private URL wsdlurl2 = null;

	private URL wsdlurl3 = null;

	private URL wsdlurl4 = null;

	private URL wsdlurl5 = null;

	private URL wsdlurl6 = null;

	transient HelloUnprotected port1 = null;

	transient HelloProtected port2noid = null;

	transient HelloProtected1 port2validid = null;

	transient HelloProtected2 port2invalidid = null;

	transient HelloProtected3 port2unauthid = null;

	transient HelloGuest port3 = null;

	transient javax.naming.InitialContext ic = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(ENDPOINT1_URL);
		url1 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT2_URL);
		url2 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT3_URL);
		url3 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT4_URL);
		url4 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT5_URL);
		url5 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT6_URL);
		url6 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC1_URL);
		wsdlurl1 = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC2_URL);
		wsdlurl2 = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC3_URL);
		wsdlurl3 = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC4_URL);
		wsdlurl4 = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC5_URL);
		wsdlurl5 = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC6_URL);
		wsdlurl6 = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		logger.log(Level.INFO, "Service Endpoint1 URL: " + url1);
		logger.log(Level.INFO, "Service Endpoint2 URL: " + url2);
		logger.log(Level.INFO, "Service Endpoint3 URL: " + url3);
		logger.log(Level.INFO, "Service Endpoint4 URL: " + url4);
		logger.log(Level.INFO, "Service Endpoint5 URL: " + url5);
		logger.log(Level.INFO, "Service Endpoint6 URL: " + url6);
		logger.log(Level.INFO, "WSDL Location URL1: " + wsdlurl1);
		logger.log(Level.INFO, "WSDL Location URL2: " + wsdlurl2);
		logger.log(Level.INFO, "WSDL Location URL3: " + wsdlurl3);
		logger.log(Level.INFO, "WSDL Location URL4: " + wsdlurl4);
		logger.log(Level.INFO, "WSDL Location URL5: " + wsdlurl5);
		logger.log(Level.INFO, "WSDL Location URL6: " + wsdlurl6);
	}

	private void getUnprotectedServiceStubStandalone() throws Exception {
		port1 = (HelloUnprotected) JAXWS_Util.getPort(wsdlurl1, SERVICE_QNAME, BasicAuthServiceTestService.class,
				PORT_QNAME1, HelloUnprotected.class);
		JAXWS_Util.setTargetEndpointAddress(port1, url1);
	}

	private void getProtectedNoIdServiceStubStandalone() throws Exception {
		port2noid = (HelloProtected) JAXWS_Util.getPort(wsdlurl2, SERVICE_QNAME, BasicAuthServiceTestService.class,
				PORT_QNAME2, HelloProtected.class);
		JAXWS_Util.setTargetEndpointAddress(port2noid, url2);
	}

	private void getProtectedValidIdServiceStubStandalone() throws Exception {
		port2validid = (HelloProtected1) JAXWS_Util.getPort(wsdlurl4, SERVICE_QNAME, BasicAuthServiceTestService.class,
				PORT_QNAME4, HelloProtected1.class);
		JAXWS_Util.setTargetEndpointAddress(port2validid, url4);
	}

	private void getProtectedInvalidIdServiceStubStandalone() throws Exception {
		port2invalidid = (HelloProtected2) JAXWS_Util.getPort(wsdlurl5, SERVICE_QNAME,
				BasicAuthServiceTestService.class, PORT_QNAME5, HelloProtected2.class);
		JAXWS_Util.setTargetEndpointAddress(port2invalidid, url5);
	}

	private void getProtectedUnauthIdServiceStubStandalone() throws Exception {
		port2unauthid = (HelloProtected3) JAXWS_Util.getPort(wsdlurl6, SERVICE_QNAME, BasicAuthServiceTestService.class,
				PORT_QNAME6, HelloProtected3.class);
		JAXWS_Util.setTargetEndpointAddress(port2unauthid, url6);
	}

	private void getGuestServiceStubStandalone() throws Exception {
		port3 = (HelloGuest) JAXWS_Util.getPort(wsdlurl3, SERVICE_QNAME, BasicAuthServiceTestService.class, PORT_QNAME3,
				HelloGuest.class);
		JAXWS_Util.setTargetEndpointAddress(port3, url3);
	}

	private void getUnprotectedServiceStub() throws Exception {
		try {
			BasicAuthServiceTestService service = (BasicAuthServiceTestService) ic
					.lookup("java:comp/env/service/unprotected");
			port1 = (HelloUnprotected) service.getPort(HelloUnprotected.class);
			JAXWS_Util.dumpTargetEndpointAddress(port1);
			JAXWS_Util.setSOAPLogging(port1);
		} catch (Throwable t) {
			TestUtil.printStackTrace(t);
			throw new Exception(t.toString());
		}
	}

	private void getProtectedNoIdServiceStub() throws Exception {
		try {
			BasicAuthServiceTestService service = (BasicAuthServiceTestService) ic
					.lookup("java:comp/env/service/protectednoid");
			port2noid = (HelloProtected) service.getPort(HelloProtected.class);
			JAXWS_Util.dumpTargetEndpointAddress(port2noid);
			JAXWS_Util.setSOAPLogging(port2noid);
		} catch (Throwable t) {
			TestUtil.printStackTrace(t);
			throw new Exception(t.toString());
		}
	}

	private void getProtectedValidIdServiceStub() throws Exception {
		try {
			BasicAuthServiceTestService service = (BasicAuthServiceTestService) ic
					.lookup("java:comp/env/service/protectedvalidid");
			port2validid = (HelloProtected1) service.getPort(HelloProtected1.class);
			JAXWS_Util.dumpTargetEndpointAddress(port2validid);
			JAXWS_Util.setSOAPLogging(port2validid);
		} catch (Throwable t) {
			TestUtil.printStackTrace(t);
			throw new Exception(t.toString());
		}
	}

	private void getProtectedInvalidIdServiceStub() throws Exception {
		try {
			BasicAuthServiceTestService service = (BasicAuthServiceTestService) ic
					.lookup("java:comp/env/service/protectedinvalidid");
			port2invalidid = (HelloProtected2) service.getPort(HelloProtected2.class);
			JAXWS_Util.dumpTargetEndpointAddress(port2invalidid);
			JAXWS_Util.setSOAPLogging(port2invalidid);
		} catch (Throwable t) {
			TestUtil.printStackTrace(t);
			throw new Exception(t.toString());
		}
	}

	private void getProtectedUnauthIdServiceStub() throws Exception {
		try {
			BasicAuthServiceTestService service = (BasicAuthServiceTestService) ic
					.lookup("java:comp/env/service/protectedunauthid");
			port2unauthid = (HelloProtected3) service.getPort(HelloProtected3.class);
			JAXWS_Util.dumpTargetEndpointAddress(port2unauthid);
			JAXWS_Util.setSOAPLogging(port2unauthid);
		} catch (Throwable t) {
			TestUtil.printStackTrace(t);
			throw new Exception(t.toString());
		}
	}

	private void getGuestServiceStub() throws Exception {
		try {
			BasicAuthServiceTestService service = (BasicAuthServiceTestService) ic
					.lookup("java:comp/env/service/guest");
			port3 = (HelloGuest) service.getPort(HelloGuest.class);
			JAXWS_Util.dumpTargetEndpointAddress(port3);
			JAXWS_Util.setSOAPLogging(port3);
		} catch (Throwable t) {
			TestUtil.printStackTrace(t);
			throw new Exception(t.toString());
		}
	}

	/*
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * 
	 * @class.setup_props: webServerHost; webServerPort; user; password; authuser;
	 * authpassword; platform.mode;
	 */
	@BeforeEach
	public void setup() throws Exception {
		try {
			hostname = System.getProperty(WEBSERVERHOSTPROP);
			portnum = Integer.parseInt(System.getProperty(WEBSERVERPORTPROP));
			username = System.getProperty(UserNameProp);
			password = System.getProperty(PasswordProp);
			unauthUsername = System.getProperty(unauthUserNameProp);
			unauthPassword = System.getProperty(unauthPasswordProp);
			modeProperty = System.getProperty(MODEPROP);
			if (modeProperty.equals("standalone")) {
				getTestURLs();
			} else {
				ic = new javax.naming.InitialContext();
				getTestURLs();
			}
			vehicle = System.getProperty("vehicle");
		} catch (Exception e) {
			TestUtil.logErr("setup failed:", e);
		}
	}

	/*
	 * @testName: BasicAuthTest1
	 *
	 * @assertion_ids: JAXWS:SPEC:11006; JAXWS:SPEC:11007; JAXWS:SPEC:10017;
	 * JAXWS:SPEC:10018; WS4EE:SPEC:113; WS4EE:SPEC:114; WS4EE:SPEC:115;
	 * WS4EE:SPEC:117; WS4EE:SPEC:213; WS4EE:SPEC:219; WS4EE:SPEC:221;
	 * WS4EE:SPEC:223; WS4EE:SPEC:224; WS4EE:SPEC:228; WS4EE:SPEC:248;
	 * WS4EE:SPEC:249; WS4EE:SPEC:183; WS4EE:SPEC:184; WS4EE:SPEC:185;
	 * WS4EE:SPEC:186; WS4EE:SPEC:187; WS4EE:SPEC:4000; WS4EE:SPEC:4002;
	 * WS4EE:SPEC:5000; WS4EE:SPEC:5002; WS4EE:SPEC:32; WS4EE:SPEC:4011;
	 * WS4EE:SPEC:9000; WS4EE:SPEC:9001; JAXWS:SPEC:129;
	 *
	 * @test_Strategy: 1. Invoke RPC on a protected JAXWS service definition without
	 * authenticating. 2. The JAXWS runtime must deny access and throw a
	 * WebServiceException (UnAuthorized).
	 *
	 * Description Test BASIC authentication as specified in the JAXWS
	 * Specification.
	 *
	 * 1. If user has not been authenticated and user attempts to access a protected
	 * JAXWS service definition, the JAXWS runtime must deny access and throw a
	 * WebService- Exception (UnAuthorized).
	 */

	@Test
	public void BasicAuthTest1() throws Exception {
		TestUtil.logTrace("BasicAuthTest1");
		boolean pass = true;
		String expected = "Hello, foo!";
		try {
			if (vehicle.equals("wsappclient")) {
				logger.log(Level.INFO, "Skipping BasicAuthTest1 test for appclient vehicle");
				return;
			}
			logger.log(Level.INFO, "Get stub for Protected Service Definition");
			if (modeProperty.equals("standalone"))
				getProtectedNoIdServiceStubStandalone();
			else
				getProtectedNoIdServiceStub();
			logger.log(Level.INFO, "Invoke RPC method without authenticating");
			logger.log(Level.INFO, "JAXWS runtime must throw a WebServiceException");
			try {
				String response = port2noid.helloProtected("foo");
				TestUtil.logErr("Authorization was allowed - failed");
				TestUtil.logErr("Did not get expected WebServiceException");
				pass = false;
			} catch (WebServiceException e) {
				logger.log(Level.INFO, "Got expected WebServiceException");
				logger.log(Level.INFO, "Detail exception message: " + e.getMessage());
				logger.log(Level.INFO, "Authorization was not allowed - passed");
				logger.log(Level.INFO, "RPC invocation was denied - passed");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("BasicAuthTest1 failed: ", e);
		}
		if (!pass)
			throw new Exception("BasicAuthTest1 failed");
	}

	/*
	 * @testName: BasicAuthTest2
	 *
	 * @assertion_ids: JAXWS:SPEC:11006; JAXWS:SPEC:11007; JAXWS:SPEC:10017;
	 * JAXWS:SPEC:10018; WS4EE:SPEC:113; WS4EE:SPEC:114; WS4EE:SPEC:115;
	 * WS4EE:SPEC:117; WS4EE:SPEC:213; WS4EE:SPEC:219; WS4EE:SPEC:221;
	 * WS4EE:SPEC:223; WS4EE:SPEC:224; WS4EE:SPEC:228; WS4EE:SPEC:248;
	 * WS4EE:SPEC:249; WS4EE:SPEC:183; WS4EE:SPEC:184; WS4EE:SPEC:185;
	 * WS4EE:SPEC:186; WS4EE:SPEC:187; WS4EE:SPEC:4000; WS4EE:SPEC:4002;
	 * WS4EE:SPEC:5000; WS4EE:SPEC:5002; WS4EE:SPEC:32; WS4EE:SPEC:4011;
	 * WS4EE:SPEC:9000; WS4EE:SPEC:9001; JAXWS:SPEC:129;
	 *
	 * @test_Strategy: 1. Invoke RPC on a protected JAXWS service definition
	 * authenticating with a valid username and password. 2. The JAXWS runtime must
	 * allow access.
	 *
	 * Description Test BASIC authentication as specified in the JAXWS
	 * Specification.
	 *
	 * 1. If user has not been authenticated and user attempts to access a protected
	 * JAXWS service definition, and user enters a valid username and password, then
	 * the JAXWS runtime must allow access.
	 */

	@Test
	public void BasicAuthTest2() throws Exception {
		TestUtil.logTrace("BasicAuthTest2");
		boolean pass = true;
		String expected = "Hello, foo!";
		try {
			logger.log(Level.INFO, "Get stub for Protected Service Definition");
			if (modeProperty.equals("standalone")) {
				getProtectedValidIdServiceStubStandalone();
				JAXWS_Util.setUserNameAndPassword(port2validid, username, password);
			} else
				getProtectedValidIdServiceStub();
			logger.log(Level.INFO, "Invoke RPC method authenticating with a valid" + " username/password");
			logger.log(Level.INFO, "User is in the required security role to access" + " the resource");
			logger.log(Level.INFO, "JAXWS runtime must allow access");
			logger.log(Level.INFO, "Username=" + username + ", Password=" + password);
			try {
				String response = port2validid.helloProtected1("foo");
				logger.log(Level.INFO, "Authorization was allowed - passed");
				logger.log(Level.INFO, "RPC invocation was allowed - passed");
				logger.log(Level.INFO, "Checking return response");
				if (!response.equals(expected)) {
					TestUtil.logErr(
							"Received incorrect response - expected [" + expected + "], received: [" + response + "]");
					pass = false;
				} else {
					logger.log(Level.INFO, "Received expected response: [" + response + "]");
				}
			} catch (WebServiceException e) {
				TestUtil.logErr("Authorization was not allowed - failed", e);
				TestUtil.logErr("RPC invocation was denied - failed");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("BasicAuthTest2 failed: ", e);
		}
		if (!pass)
			throw new Exception("BasicAuthTest2 failed");
	}

	/*
	 * @testName: BasicAuthTest3
	 *
	 * @assertion_ids: JAXWS:SPEC:11006; JAXWS:SPEC:11007; JAXWS:SPEC:10017;
	 * JAXWS:SPEC:10018; WS4EE:SPEC:113; WS4EE:SPEC:114; WS4EE:SPEC:115;
	 * WS4EE:SPEC:117; WS4EE:SPEC:213; WS4EE:SPEC:219; WS4EE:SPEC:221;
	 * WS4EE:SPEC:223; WS4EE:SPEC:224; WS4EE:SPEC:228; WS4EE:SPEC:248;
	 * WS4EE:SPEC:249; WS4EE:SPEC:183; WS4EE:SPEC:184; WS4EE:SPEC:185;
	 * WS4EE:SPEC:186; WS4EE:SPEC:187; WS4EE:SPEC:4000; WS4EE:SPEC:4002;
	 * WS4EE:SPEC:5000; WS4EE:SPEC:5002; WS4EE:SPEC:32; WS4EE:SPEC:4011;
	 * WS4EE:SPEC:9000; WS4EE:SPEC:9001; JAXWS:SPEC:129;
	 *
	 * @test_Strategy: 1. Invoke RPC on a protected JAXWS service definition
	 * authenticating with invalid username and password. 2. The JAXWS runtime must
	 * deny access and throw a WebServiceException (UnAuthorized).
	 *
	 * Description Test BASIC authentication as specified in the JAXWS
	 * Specification.
	 *
	 * 1. If user has not been authenticated and user attempts to access a protected
	 * JAXWS service definition, and user enters an invalid username and password,
	 * then the JAXWS runtime must deny access and throw a WebService- Exception
	 * (UnAuthorized).
	 */

	@Test
	public void BasicAuthTest3() throws Exception {
		TestUtil.logTrace("BasicAuthTest3");
		boolean pass = true;
		String expected = "Hello, foo!";
		try {
			if (vehicle.equals("wsappclient")) {
				logger.log(Level.INFO, "Skipping BasicAuthTest3 test for appclient vehicle");
				return;
			}
			logger.log(Level.INFO, "Get stub for Protected Service Definition");
			if (modeProperty.equals("standalone")) {
				getProtectedInvalidIdServiceStubStandalone();
				JAXWS_Util.setUserNameAndPassword(port2invalidid, "invalid", "invalid");
			} else
				getProtectedInvalidIdServiceStub();
			logger.log(Level.INFO, "Invoke RPC method authenticating with an" + " invalid username/password");
			logger.log(Level.INFO, "Username=invalid, Password=invalid");
			logger.log(Level.INFO, "Username=invalid, Password=invalid");
			logger.log(Level.INFO, "JAXWS runtime must throw a WebServiceException");
			try {
				String response = port2invalidid.helloProtected2("foo");
				TestUtil.logErr("Did not get expected WebServiceException");
				TestUtil.logErr("Authorization was allowed - failed");
				TestUtil.logErr("RPC invocation was allowed - failed");
				pass = false;
			} catch (WebServiceException e) {
				logger.log(Level.INFO, "Got expected WebServiceException");
				logger.log(Level.INFO, "Detail exception message: " + e.getMessage());
				logger.log(Level.INFO, "Authorization was not allowed - passed");
				logger.log(Level.INFO, "RPC invocation was denied - passed");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("BasicAuthTest3 failed: ", e);
		}
		if (!pass)
			throw new Exception("BasicAuthTest3 failed");
	}

	/*
	 * @testName: BasicAuthTest4
	 *
	 * @assertion_ids: JAXWS:SPEC:11006; JAXWS:SPEC:11007; JAXWS:SPEC:10017;
	 * JAXWS:SPEC:10018; WS4EE:SPEC:113; WS4EE:SPEC:114; WS4EE:SPEC:115;
	 * WS4EE:SPEC:117; WS4EE:SPEC:213; WS4EE:SPEC:219; WS4EE:SPEC:221;
	 * WS4EE:SPEC:223; WS4EE:SPEC:224; WS4EE:SPEC:228; WS4EE:SPEC:248;
	 * WS4EE:SPEC:249; WS4EE:SPEC:183; WS4EE:SPEC:184; WS4EE:SPEC:185;
	 * WS4EE:SPEC:186; WS4EE:SPEC:187; WS4EE:SPEC:4000; WS4EE:SPEC:4002;
	 * WS4EE:SPEC:5000; WS4EE:SPEC:5002; WS4EE:SPEC:32; WS4EE:SPEC:4011;
	 * WS4EE:SPEC:9000; WS4EE:SPEC:9001; JAXWS:SPEC:129;
	 *
	 * @test_Strategy: 1. Invoke RPC on a protected JAXWS service definition
	 * authenticating with valid username and password but user is not in the
	 * required secuirty role allowed by the JAXWS service definition. 2. The JAXWS
	 * runtime must deny access and throw a WebServiceException (UnAuthorized).
	 *
	 * Description Test BASIC authentication as specified in the JAXWS
	 * Specification.
	 *
	 * 1. If user has not been authenticated and user attempts to access a protected
	 * JAXWS service definition, and user enters a valid username and password, but
	 * user is not in the required security role allowed by the JAXWS service
	 * definition then the JAXWS runtime must deny access and throw a
	 * WebServiceException (UnAuthorized).
	 */

	@Test
	public void BasicAuthTest4() throws Exception {
		TestUtil.logTrace("BasicAuthTest4");
		boolean pass = true;
		String expected = "Hello, foo!";
		try {
			logger.log(Level.INFO, "Get stub for Protected Service Definition");
			if (modeProperty.equals("standalone")) {
				getProtectedUnauthIdServiceStubStandalone();
				JAXWS_Util.setUserNameAndPassword(port2unauthid, unauthUsername, unauthPassword);
			} else
				getProtectedUnauthIdServiceStub();
			logger.log(Level.INFO, "Invoke RPC method authenticating with a valid" + " username/password");
			logger.log(Level.INFO, "User is not in the required security role to" + " access the resource");
			logger.log(Level.INFO, "Username=" + unauthUsername + ", Password=" + unauthPassword);
			logger.log(Level.INFO, "JAXWS runtime must throw a WebServiceException");
			try {
				String response = port2unauthid.helloProtected3("foo");
				TestUtil.logErr("Did not get expected WebServiceException");
				TestUtil.logErr("Authorization was allowed - failed");
				TestUtil.logErr("RPC invocation was allowed - failed");
				pass = false;
			} catch (WebServiceException e) {
				logger.log(Level.INFO, "Got expected WebServiceException");
				logger.log(Level.INFO, "Detail exception message: " + e.getMessage());
				logger.log(Level.INFO, "Authorization was not allowed - passed");
				logger.log(Level.INFO, "RPC invocation was denied - passed");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("BasicAuthTest4 failed: ", e);
		}
		if (!pass)
			throw new Exception("BasicAuthTest4 failed");
	}

	/*
	 * @testName: BasicAuthTest5
	 *
	 * @assertion_ids: JAXWS:SPEC:11006; JAXWS:SPEC:11007; JAXWS:SPEC:10017;
	 * JAXWS:SPEC:10018; WS4EE:SPEC:113; WS4EE:SPEC:114; WS4EE:SPEC:115;
	 * WS4EE:SPEC:117; WS4EE:SPEC:213; WS4EE:SPEC:219; WS4EE:SPEC:221;
	 * WS4EE:SPEC:223; WS4EE:SPEC:224; WS4EE:SPEC:228; WS4EE:SPEC:248;
	 * WS4EE:SPEC:249; WS4EE:SPEC:183; WS4EE:SPEC:184; WS4EE:SPEC:185;
	 * WS4EE:SPEC:186; WS4EE:SPEC:187; WS4EE:SPEC:4000; WS4EE:SPEC:4002;
	 * WS4EE:SPEC:5000; WS4EE:SPEC:5002; WS4EE:SPEC:32; WS4EE:SPEC:4011;
	 * WS4EE:SPEC:9000; WS4EE:SPEC:9001; JAXWS:SPEC:129;
	 *
	 * @test_Strategy: 1. Invoke RPC on a unprotected JAXWS service definition. 2.
	 * The JAXWS runtime must allow access without the need to authenticate.
	 *
	 * Description Test BASIC authentication as specified in the JAXWS
	 * Specification.
	 *
	 * 1. If user has not been authenticated and user attempts to access an
	 * unprotected JAXWS service definition, then the JAXWS runtime must allow
	 * access without the need to authenticate.
	 */

	@Test
	public void BasicAuthTest5() throws Exception {
		TestUtil.logTrace("BasicAuthTest5");
		boolean pass = true;
		String expected = "Hello, foo!";
		try {
			logger.log(Level.INFO, "Get stub for Unprotected Service Definition");
			if (modeProperty.equals("standalone"))
				getUnprotectedServiceStubStandalone();
			else
				getUnprotectedServiceStub();
			logger.log(Level.INFO, "Invoke RPC method without authenticating");
			logger.log(Level.INFO, "JAXWS runtime must allow access without the" + " need to authenticate user");
			try {
				String response = port1.helloUnprotected("foo");
				logger.log(Level.INFO, "Authorization was allowed - passed");
				logger.log(Level.INFO, "RPC invocation was allowed - passed");
				logger.log(Level.INFO, "Checking return response");
				if (!response.equals(expected)) {
					TestUtil.logErr(
							"Received incorrect response - expected [" + expected + "], received: [" + response + "]");
					pass = false;
				} else {
					logger.log(Level.INFO, "Received expected response: [" + response + "]");
				}
			} catch (WebServiceException e) {
				TestUtil.logErr("Authorization was not allowed - failed", e);
				TestUtil.logErr("RPC invocation was denied - failed");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("BasicAuthTest5 failed: ", e);
		}
		if (!pass)
			throw new Exception("BasicAuthTest5 failed");
	}

	/*
	 * @testName: BasicAuthTest6
	 *
	 * @assertion_ids: JAXWS:SPEC:11006; JAXWS:SPEC:11007; JAXWS:SPEC:10017;
	 * JAXWS:SPEC:10018; WS4EE:SPEC:113; WS4EE:SPEC:114; WS4EE:SPEC:115;
	 * WS4EE:SPEC:117; WS4EE:SPEC:213; WS4EE:SPEC:219; WS4EE:SPEC:221;
	 * WS4EE:SPEC:223; WS4EE:SPEC:224; WS4EE:SPEC:228; WS4EE:SPEC:248;
	 * WS4EE:SPEC:249; WS4EE:SPEC:183; WS4EE:SPEC:184; WS4EE:SPEC:185;
	 * WS4EE:SPEC:186; WS4EE:SPEC:187; WS4EE:SPEC:4000; WS4EE:SPEC:4002;
	 * WS4EE:SPEC:5000; WS4EE:SPEC:5002; WS4EE:SPEC:32; WS4EE:SPEC:4011;
	 * WS4EE:SPEC:9000; WS4EE:SPEC:9001; JAXWS:SPEC:129;
	 *
	 * @test_Strategy: 1. Invoke RPC on a guest JAXWS service definition. 2. The
	 * JAXWS runtime must allow access since all users have access to the guest
	 * JAXWS service definition.
	 *
	 * Description Test BASIC authentication as specified in the JAXWS
	 * Specification.
	 *
	 * 1. If user has not been authenticated and user attempts to access a guest
	 * JAXWS service definition, and a user enters a valid username and password,
	 * then the JAXWS runtime must allow access since all users have access to the
	 * guest JAXWS service definition.
	 */

	@Test
	public void BasicAuthTest6() throws Exception {
		TestUtil.logTrace("BasicAuthTest6");
		boolean pass = true;
		String expected = "Hello, foo!";
		try {
			logger.log(Level.INFO, "Get stub for Guest Service Definition");
			if (modeProperty.equals("standalone")) {
				getGuestServiceStubStandalone();
				JAXWS_Util.setUserNameAndPassword(port3, unauthUsername, unauthPassword);
			} else
				getGuestServiceStub();
			logger.log(Level.INFO, "Invoke RPC method authenticating with a" + " valid username/password");
			logger.log(Level.INFO, "Username=" + unauthUsername + ", Password=" + unauthPassword);
			logger.log(Level.INFO, "JAXWS runtime must allow access since all" + " users have guest access");
			try {
				String response = port3.helloGuest("foo");
				logger.log(Level.INFO, "Authorization was allowed - passed");
				logger.log(Level.INFO, "RPC invocation was allowed - passed");
			} catch (WebServiceException e) {
				TestUtil.logErr("Authorization was not allowed - failed", e);
				TestUtil.logErr("RPC invocation was denied - failed");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("BasicAuthTest6 failed: ", e);
		}
		if (!pass)
			throw new Exception("BasicAuthTest6 failed");
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup");
	}
}
