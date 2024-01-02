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
 * $Id$
 */

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws.WebServiceContext;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.sharedclients.hellosecureclient.Hello;
import com.sun.ts.tests.jaxws.sharedclients.hellosecureclient.HelloService;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {
	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.api.jakarta_xml_ws.WebServiceContext.";

	// service and port information
	private static final String NAMESPACEURI = "http://helloservice.org/wsdl";

	private static final String SERVICE_NAME = "HelloService";

	private static final String PORT_NAME = "HelloPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	// URL properties used by the test
	private static final String ENDPOINT_URL = "hellosecureservice.endpoint.1";

	private static final String WSDLLOC_URL = "hellosecureservice.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	Hello port = null;

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.sharedclients.hellosecureclient.HelloService.class;

	static HelloService service = null;

	private void getPorts() throws Exception {
		logger.log(Level.INFO, "Get port  = " + PORT_NAME);
		port = (Hello) service.getPort(Hello.class);
		logger.log(Level.INFO, "port=" + port);
	}

	private void getPortsStandalone() throws Exception {
		getPorts();
		JAXWS_Util.setTargetEndpointAddress(port, url);
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
		port = (Hello) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, HelloService.class, PORT_QNAME, Hello.class);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	private void getPortsJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		getPorts();
		logger.log(Level.INFO, "Get Target Endpoint Address for port=" + port);
		String url = JAXWS_Util.getTargetEndpointAddress(port);
		logger.log(Level.INFO, "Target Endpoint Address=" + url);
	}

	/* Test setup */

	/*
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * 
	 * @class.setup_props: webServerHost; webServerPort; platform.mode; user;
	 * password; authuser; authpassword;
	 */
	@BeforeEach
	public void setup() throws Exception {
		boolean pass = true;
		try {
			hostname = System.getProperty(WEBSERVERHOSTPROP);
			if (hostname == null)
				pass = false;
			else if (hostname.equals(""))
				pass = false;
			try {
				portnum = Integer.parseInt(System.getProperty(WEBSERVERPORTPROP));
			} catch (Exception e) {
				TestUtil.printStackTrace(e);
				pass = false;
			}
			username = System.getProperty(UserNameProp);
			password = System.getProperty(PasswordProp);
			unauthUsername = System.getProperty(unauthUserNameProp);
			unauthPassword = System.getProperty(unauthPasswordProp);
			logger.log(Level.INFO, "Username=" + username + ", Password=" + password);
			logger.log(Level.INFO, "unauthUsername=" + unauthUsername + ", unauthPassword=" + unauthPassword);
			modeProperty = System.getProperty(MODEPROP);

			if (modeProperty.equals("standalone")) {
				logger.log(Level.INFO, "Create Service object");
				getTestURLs();
				service = (HelloService) JAXWS_Util.getService(wsdlurl, SERVICE_QNAME, SERVICE_CLASS);
				getPortsStandalone();
				JAXWS_Util.setUserNameAndPassword(port, username, password);
			} else {
				getTestURLs();
				logger.log(Level.INFO, "WebServiceRef is not set in Client (get it from specific vehicle)");
				service = (HelloService) getSharedObject();
				getPortsJavaEE();
			}

		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("setup failed:", e);
		}
		if (!pass) {
			TestUtil.logErr("Please specify host & port of web server " + "in config properties: " + WEBSERVERHOSTPROP
					+ ", " + WEBSERVERPORTPROP);
			throw new Exception("setup failed:");
		}
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: getMessageContextTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:69; WS4EE:SPEC:5004;
	 *
	 * @test_Strategy: Call WebServiceContext.getMessageContext() api.
	 *
	 * Description
	 */
	@Test
	public void getMessageContextTest() throws Exception {
		boolean pass = true;
		try {
			logger.log(Level.INFO, "getMessageContextTest: test access to MessageContext information");
			pass = port.getMessageContextTest();
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("getMessageContextTest failed", e);
		}

		if (!pass)
			throw new Exception("getMessageContextTest failed");
	}

	/*
	 * @testName: getEndpointReferenceTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:158; JAXWS:SPEC:5028;
	 *
	 * @test_Strategy: Call
	 * getEndpointReference(org.w3c.dom.Element...referenceParameters) and ensure
	 * that EndpointReference was able to be retrieved.
	 *
	 * Description
	 */
	@Test
	public void getEndpointReferenceTest() throws Exception {
		boolean pass = true;
		try {
			logger.log(Level.INFO, "getEndpointReferenceTest: test access to EndpointReference information");
			pass = port.getEndpointReferenceTest();
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("getEndpointReferenceTest failed", e);
		}

		if (!pass) {
			throw new Exception("getEndpointReferenceTest failed");
		}
	}

	/*
	 * @testName: getEndpointReference2Test
	 *
	 * @assertion_ids: JAXWS:JAVADOC:159; JAXWS:SPEC:5028;
	 *
	 * @test_Strategy: Call getEndpointReference(java.lang.Class class,
	 * org.w3c.dom.Element...referenceParameters) and ensure that EndpointReference
	 * was able to be retrieved.
	 *
	 * Description
	 */
	@Test
	public void getEndpointReference2Test() throws Exception {
		boolean pass = true;
		try {
			logger.log(Level.INFO, "getEndpointReference2Test: test access to MessageContext information");
			pass = port.getEndpointReference2Test();
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("getEndpointReference2Test failed", e);
		}

		if (!pass)
			throw new Exception("getEndpointReference2Test failed");
	}

	/*
	 * @testName: getUserPrincipalTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:70; WS4EE:SPEC:5004;
	 *
	 * @test_Strategy: Call WebServiceContext.getUserPrincipal() api.
	 *
	 * Description
	 */
	@Test
	public void getUserPrincipalTest() throws Exception {
		boolean pass = true;
		try {
			logger.log(Level.INFO, "getUserPrincipalTest: test access to UserPrincipal information");
			pass = port.getUserPrincipalTest();
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("getUserPrincipalTest failed", e);
		}

		if (!pass)
			throw new Exception("getUserPrincipalTest failed");
	}

	/*
	 * @testName: isUserInRoleTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:71; WS4EE:SPEC:5004;
	 *
	 * @test_Strategy: Call WebServiceContext.getisUserInRole() api.
	 *
	 * Description
	 */
	@Test
	public void isUserInRoleTest() throws Exception {
		boolean pass = true;
		try {
			TestUtil.logMsg("isUserInRoleTest: test access to isUserInRole information");
			logger.log(Level.INFO,
					"Invoking RPC method isUserInRoleTest() and " + "expect true for Adminstrator role ...");
			boolean yes = port.isUserInRoleTest("Administrator");
			if (yes)
				logger.log(Level.INFO, "Administrator role - correct");
			else {
				TestUtil.logErr("Not Administrator role - incorrect");
				pass = false;
			}
			logger.log(Level.INFO, "Invoking RPC method isUserInRoleTest() and " + "expect false for Manager role ...");
			yes = port.isUserInRoleTest("Manager");
			if (!yes) {
				logger.log(Level.INFO, "Not Manager role - correct");
			} else {
				TestUtil.logErr("Manager role - incorrect");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("isUserInRoleTest failed", e);
		}

		if (!pass)
			throw new Exception("isUserInRoleTest failed");
	}
}
