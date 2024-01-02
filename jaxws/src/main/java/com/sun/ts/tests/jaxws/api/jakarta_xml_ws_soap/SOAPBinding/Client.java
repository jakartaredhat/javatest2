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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws_soap.SOAPBinding;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.Constants;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.Hello;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloService;
import com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.ObjectFactory;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.soap.SOAPBinding;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	// Need to create jaxbContext
	private static final ObjectFactory of = new ObjectFactory();

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.api.jakarta_xml_ws_soap.SOAPBinding.";

	private static final String SHARED_CLIENT_PKG = "com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.";

	// service and port info
	private static final String NAMESPACEURI = "http://helloservice.org/wsdl";

	private static final String SERVICE_NAME = "HelloService";

	private static final String PORT_NAME = "HelloPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.sharedclients.doclithelloclient.HelloService.class;

	private TSURL ctsurl = new TSURL();

	private String hostname = HOSTNAME;

	private int portnum = PORTNUM;

	// URL properties used by the test
	private static final String ENDPOINT_URL = "dlhelloservice.endpoint.1";

	private static final String WSDLLOC_URL = "dlhelloservice.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	private static int NUM_ROLES = 3;

	private String uri0 = null;

	private String uri1 = null;

	private String uri2 = null;

	private Binding binding = null;

	private BindingProvider bp = null;

	private Hello port = null;

	static HelloService service = null;

	private void getPorts() throws Exception {
		logger.log(Level.INFO, "Get port  = " + PORT_NAME);
		port = (Hello) service.getPort(Hello.class);
		logger.log(Level.INFO, "port=" + port);
	}

	protected void getPortStandalone() throws Exception {
		getPorts();
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		getPorts();
		logger.log(Level.INFO, "Get Target Endpoint Address for port=" + port);
		String url = JAXWS_Util.getTargetEndpointAddress(port);
		logger.log(Level.INFO, "Target Endpoint Address=" + url);
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

	/* Test setup */

	/*
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * 
	 * @class.setup_props: webServerHost; webServerPort; platform.mode;
	 */
	@BeforeEach
	public void setup() throws Exception {
		boolean pass = true;

		// Initialize QNAMES used in the test
		SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

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
			Binding binding = null;
			modeProperty = System.getProperty(MODEPROP);
			if (modeProperty.equals("standalone")) {
				logger.log(Level.INFO, "Create Service object");
				getTestURLs();
				service = (HelloService) JAXWS_Util.getService(wsdlurl, SERVICE_QNAME, SERVICE_CLASS);
				getPorts();
			} else {
				logger.log(Level.INFO, "WebServiceRef is not set in Client (get it from specific vehicle)");
				service = (HelloService) getSharedObject();
				getTestURLs();
				getPorts();

			}
			bp = (BindingProvider) port;

			uri0 = "http://schemas.xmlsoap.org/soap/actor/next";
			uri1 = "http://role1.com/";
			uri2 = "http://role2.com/";
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
	 * @testName: getSOAPBindingTest
	 *
	 * @assertion_ids: JAXWS:SPEC:3039;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void getSOAPBindingTest() throws Exception {
		TestUtil.logTrace("getSOAPBindingTest");
		boolean pass = true;
		logger.log(Level.INFO, "Get Binding interface for Dispatch object");
		binding = bp.getBinding();
		if (binding == null) {
			TestUtil.logErr("getBinding() returned null");
			pass = false;
		} else {
			if (binding instanceof SOAPBinding) {
				logger.log(Level.INFO, "binding is a SOAPBinding instance");
			} else {
				TestUtil.logErr("binding is not a SOAPBinding instance");
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("getSOAPBindingTest failed");
	}

	/*
	 * @testName: setGetRolesForDispatchObjTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:107; JAXWS:JAVADOC:111; WS4EE:SPEC:5005;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void setGetRolesForDispatchObjTest() throws Exception {
		TestUtil.logTrace("setGetRolesForDispatchObjTest");
		boolean pass = true;
		logger.log(Level.INFO, "Get Binding interface for Dispatch object");
		binding = bp.getBinding();
		java.util.Set<java.lang.String> roles = null;
		if (binding == null) {
			TestUtil.logErr("getBinding() returned null");
			pass = false;
		} else {
			logger.log(Level.INFO, "getBinding() returned Binding object (cast to a SOAPBinding object)");
			logger.log(Level.INFO, "Get SOAPBinding interface from Dispatch object Binding interface");
			SOAPBinding soapbinding = (SOAPBinding) binding;

			roles = soapbinding.getRoles();
			logger.log(Level.INFO, "Roles that are already set are:");
			int j = 0;
			for (java.lang.String r : roles) {
				logger.log(Level.INFO, "Role[" + j + "]=" + r);
				j++;
			}

			logger.log(Level.INFO, "Set roles for Dispatch object by calling SOAPBinding.setRoles()");
			roles = new java.util.HashSet<java.lang.String>();
			roles.add(uri1);
			roles.add(uri2);
			soapbinding.setRoles(roles);
			logger.log(Level.INFO, "Get roles for Dispatch object by calling SOAPBinding.getRoles()");
			roles = soapbinding.getRoles();
			logger.log(Level.INFO, "Verify that roles were set correctly");
			if (roles == null) {
				TestUtil.logErr("getRoles() returned null (unexpected)");
				pass = false;
			} else {
				if (roles.size() != NUM_ROLES) {
					TestUtil.logErr("Expected " + NUM_ROLES + " roles, got " + roles.size() + " roles");
					pass = false;
				}
				logger.log(Level.INFO, "Roles are:");
				int i = 0;
				for (java.lang.String r : roles) {
					logger.log(Level.INFO, "Role[" + i + "]=" + r);
					if (!r.equals(uri0) && !r.equals(uri1) && !r.equals(uri2)) {
						TestUtil.logErr("Role[" + i + "]=" + r + " was unexpected");
						pass = false;
					}
					i++;
				}
			}
		}
		if (!pass)
			throw new Exception("setGetRolesForDispatchObjTest failed");
	}

	/*
	 * @testName: soapBindingConstantsTest
	 *
	 * @assertion_ids: WS4EE:SPEC:5005;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void soapBindingConstantsTest() throws Exception {
		TestUtil.logTrace("soapBindingConstantsTest");
		boolean pass = true;

		logger.log(Level.INFO, "Verify that SOAP11HTTP_BINDING constant value is correct");
		if (!SOAPBinding.SOAP11HTTP_BINDING.equals(Constants.EXPECTED_SOAP11HTTP_BINDING)) {
			TestUtil.logErr("SOAP11HTTP_BINDING is incorrect");
			TestUtil.logErr("Got: " + SOAPBinding.SOAP11HTTP_BINDING);
			TestUtil.logErr("Expected: " + Constants.EXPECTED_SOAP11HTTP_BINDING);
			pass = false;
		}
		logger.log(Level.INFO, "Verify that SOAP12HTTP_BINDING constant value is correct");
		if (!SOAPBinding.SOAP12HTTP_BINDING.equals(Constants.EXPECTED_SOAP12HTTP_BINDING)) {
			TestUtil.logErr("SOAP12HTTP_BINDING is incorrect");
			TestUtil.logErr("Got: " + SOAPBinding.SOAP12HTTP_BINDING);
			TestUtil.logErr("Expected: " + Constants.EXPECTED_SOAP12HTTP_BINDING);
			pass = false;
		}
		logger.log(Level.INFO, "Verify that SOAP11HTTP_MTOM_BINDING constant value is correct");
		if (!SOAPBinding.SOAP11HTTP_MTOM_BINDING.equals(Constants.EXPECTED_SOAP11HTTP_MTOM_BINDING)) {
			TestUtil.logErr("SOAP11HTTP_MTOM_BINDING is incorrect");
			TestUtil.logErr("Got: " + SOAPBinding.SOAP11HTTP_MTOM_BINDING);
			TestUtil.logErr("Expected: " + Constants.EXPECTED_SOAP11HTTP_MTOM_BINDING);
			pass = false;
		}
		logger.log(Level.INFO, "Verify that SOAP12HTTP_MTOM_BINDING constant value is correct");
		if (!SOAPBinding.SOAP12HTTP_MTOM_BINDING.equals(Constants.EXPECTED_SOAP12HTTP_MTOM_BINDING)) {
			TestUtil.logErr("SOAP12HTTP_MTOM_BINDING is incorrect");
			TestUtil.logErr("Got: " + SOAPBinding.SOAP12HTTP_MTOM_BINDING);
			TestUtil.logErr("Expected: " + Constants.EXPECTED_SOAP12HTTP_MTOM_BINDING);
			pass = false;
		}
		if (!pass)
			throw new Exception("soapBindingConstantsTest failed");
	}

	/*
	 * @testName: getMessageFactoryTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:106; WS4EE:SPEC:5005;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void getMessageFactoryTest() throws Exception {
		TestUtil.logTrace("getMessageFactoryTest");
		boolean pass = true;
		logger.log(Level.INFO, "Get Binding interface for Dispatch object");
		binding = bp.getBinding();
		if (binding == null) {
			TestUtil.logErr("getBinding() returned null");
			pass = false;
		} else {
			logger.log(Level.INFO, "getBinding() returned Binding object (cast to a SOAPBinding object)");
			logger.log(Level.INFO, "Get SOAPBinding interface from Dispatch object Binding interface");
			SOAPBinding soapbinding = (SOAPBinding) binding;

			MessageFactory factory = soapbinding.getMessageFactory();

			if (factory != null) {
				logger.log(Level.INFO, "MessageFactory returned is null" + factory);
			} else {
				TestUtil.logErr("MessageFactory returned is null");
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("getMessageFactoryTest failed");
	}

	/*
	 * @testName: getSOAPFactoryTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:108; WS4EE:SPEC:5005;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void getSOAPFactoryTest() throws Exception {
		TestUtil.logTrace("getSOAPFactoryTest");
		boolean pass = true;
		logger.log(Level.INFO, "Get Binding interface for Dispatch object");
		binding = bp.getBinding();
		if (binding == null) {
			TestUtil.logErr("getBinding() returned null");
			pass = false;
		} else {
			logger.log(Level.INFO, "getBinding() returned Binding object (cast to a SOAPBinding object)");
			logger.log(Level.INFO, "Get SOAPBinding interface from Dispatch object Binding interface");
			SOAPBinding soapbinding = (SOAPBinding) binding;

			SOAPFactory factory = soapbinding.getSOAPFactory();

			if (factory != null) {
				logger.log(Level.INFO, "SOAPFactory returned is null" + factory);
			} else {
				TestUtil.logErr("SOAPFactory returned is null");
				pass = false;
			}

		}

		if (!pass)
			throw new Exception("getSOAPFactoryTest failed");
	}

	/*
	 * @testName: isSetMTOMEnabledTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:109; JAXWS:JAVADOC:110; WS4EE:SPEC:5005;
	 * WS4EE:SPEC:5006; JAXWS:SPEC:10023;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void isSetMTOMEnabledTest() throws Exception {
		TestUtil.logTrace("isSetMTOMEnabledTest");
		boolean pass = true;
		logger.log(Level.INFO, "Get Binding interface for Dispatch object");
		binding = bp.getBinding();
		if (binding == null) {
			TestUtil.logErr("getBinding() returned null");
			pass = false;
		} else {
			logger.log(Level.INFO, "getBinding() returned Binding object (cast to a SOAPBinding object)");
			logger.log(Level.INFO, "Get SOAPBinding interface from Dispatch object Binding interface");
			SOAPBinding soapbinding = (SOAPBinding) binding;

			logger.log(Level.INFO, "Checking MTOMEnabled for false");
			boolean enabled = soapbinding.isMTOMEnabled();
			if (enabled) {
				TestUtil.logErr("MTOM is enabled and should be disabled");
				pass = false;
			} else {
				logger.log(Level.INFO, "MTOM is disabled as expected");
			}

			logger.log(Level.INFO, "Setting MTOMEnabled");
			soapbinding.setMTOMEnabled(true);

			logger.log(Level.INFO, "Checking MTOMEnabled for true");
			enabled = soapbinding.isMTOMEnabled();
			if (!enabled) {
				TestUtil.logErr("MTOM is disabled and should be enabled");
				pass = false;
			} else {
				logger.log(Level.INFO, "MTOM is enabled as expected");
			}

		}
		if (!pass)
			throw new Exception("isSetMTOMEnabledTest failed");

	}
}
