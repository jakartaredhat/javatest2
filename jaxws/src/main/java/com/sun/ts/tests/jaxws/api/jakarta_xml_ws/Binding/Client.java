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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws.Binding;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.DLHandlerService;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.Hello;

import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.soap.SOAPBinding;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {
	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.api.jakarta_xml_ws.Binding.";

	private static final String SHARED_CLIENT_PKG = "com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.";

	private static final String NAMESPACEURI = "http://dlhandlerservice.org/wsdl";

	private static final String SERVICE_NAME = "DLHandlerService";

	private static final String PORT_NAME = "HelloPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.DLHandlerService.class;

	private TSURL ctsurl = new TSURL();

	private String hostname = HOSTNAME;

	private int portnum = PORTNUM;

	// URL properties used by the test
	private static final String ENDPOINT_URL = "dlhandlerservice.endpoint.1";

	private static final String WSDLLOC_URL = "dlhandlerservice.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	private Binding binding = null;

	private BindingProvider bp = null;

	private Dispatch dispatch = null;

	private Hello port = null;

	static DLHandlerService service = null;

	private void getPorts() throws Exception {
		logger.log(Level.INFO, "Get port  = " + PORT_NAME);
		port = (Hello) service.getPort(Hello.class);
		logger.log(Level.INFO, "port=" + port);
	}

	private void getPortsStandalone() throws Exception {
		getPorts();
		bp = (BindingProvider) port;
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	private void getPortsJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		getPorts();
		logger.log(Level.INFO, "Get Target Endpoint Address for port=" + port);
		String url = JAXWS_Util.getTargetEndpointAddress(port);
		logger.log(Level.INFO, "Target Endpoint Address=" + url);
		bp = (BindingProvider) port;
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
	 */@BeforeEach
	public void setup() throws Exception {
		try {
			super.setup();
			if (modeProperty.equals("standalone")) {
				logger.log(Level.INFO, "Create Service object");
				getTestURLs();
				service = (DLHandlerService) JAXWS_Util.getService(wsdlurl, SERVICE_QNAME, SERVICE_CLASS);
				getPortsStandalone();
			} else {
				getTestURLs();
				logger.log(Level.INFO, "WebServiceRef is not set in Client (get it from specific vehicle)");
				service = (DLHandlerService) getSharedObject();
				getPortsJavaEE();
			}
			TestUtil.logMsg("Create a Dispatch object for SOAP 1.1 over HTTP binding");
			dispatch = service.createDispatch(PORT_QNAME, Source.class, jakarta.xml.ws.Service.Mode.PAYLOAD);
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("setup failed:", e);
		}
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: getBindingIDTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:133;
	 *
	 * @test_Strategy:
	 * 
	 */
	@Test
	public void getBindingIDTest() throws Exception {
		TestUtil.logTrace("getBindingIDTest");
		boolean pass = true;
		try {
			binding = dispatch.getBinding();
			logger.log(Level.INFO, "Dispatch object = " + dispatch);
			logger.log(Level.INFO, "Binding object = " + binding);
			String bindingID = binding.getBindingID();
			logger.log(Level.INFO, "bindingID=" + bindingID);
			if (!bindingID.equals(SOAPBinding.SOAP11HTTP_BINDING)) {
				TestUtil.logErr("bindingID is not expected SOAP11HTTP_BINDING");
				pass = false;
			} else
				logger.log(Level.INFO, "bindingID is expected SOAP11HTTP_BINDING");
		} catch (WebServiceException e) {
			TestUtil.logErr("Caught unexpected WebServiceException", e);
			pass = false;
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("getBindingIDTest failed", e);
		}

		if (!pass)
			throw new Exception("getBindingIDTest failed");
	}

	/*
	 * @testName: SetAndGetHandlerChainForDispatchObjTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:2; JAXWS:JAVADOC:3;
	 *
	 * @test_Strategy: Sets the handler chain for a protocol binding instance. Gets
	 * the handler chain for a protocol binding instance.
	 */
	@Test
	public void SetAndGetHandlerChainForDispatchObjTest() throws Exception {
		TestUtil.logTrace("SetAndGetHandlerChainForDispatchObjTest");
		boolean pass = true;
		try {
			binding = dispatch.getBinding();
			logger.log(Level.INFO, "Dispatch object = " + dispatch);
			logger.log(Level.INFO, "Binding object = " + binding);
			logger.log(Level.INFO, "Test setHandlerChain()/getHandlerChain() for Dispatch object");
			logger.log(Level.INFO, "Create a handler chain for SOAP 1.1 over HTTP protocol binding");
			logger.log(Level.INFO, "List<Handler> hc = new ArrayList<Handler>()");
			List<Handler> hc = new ArrayList<Handler>();
			TestUtil.logMsg("Construct ClientLogicalHandler1 and add to HandlerChain");
			hc.add(new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler1());
			TestUtil.logMsg("Construct ClientLogicalHandler2 and add to HandlerChain");
			hc.add(new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler2());
			TestUtil.logMsg("Construct ClientLogicalHandler3 and add to HandlerChain");
			hc.add(new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler3());
			logger.log(Level.INFO, "Set handler chain for protocol binding instance");
			TestUtil.logMsg("Calling Binding.setHandlerChain(java.util.List<Handler>)");
			binding.setHandlerChain(hc);
			TestUtil.logMsg("Now get the handler chain for protocol binding instance");
			TestUtil.logMsg("Calling java.util.List<Handler> Binding.getHandlerChain()");
			List<Handler> hl = binding.getHandlerChain();
			logger.log(Level.INFO, "HandlerChainList=" + hl);
			logger.log(Level.INFO, "HandlerChainSize = " + hl.size());
			if (hl.size() != 3) {
				TestUtil.logErr("Wrong size returned for HandlerChain");
				TestUtil.logErr("handlerchain1 size=" + hl.size() + ", handlerchain1 size=3");
				pass = false;
			}
			Class c1 = Class.forName("com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler1");
			Class c2 = Class.forName("com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler2");
			Class c3 = Class.forName("com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler3");
			logger.log(Level.INFO, "Walk through HandlerChain and verify contents");
			for (Handler hi : hl) {
				Class c = (Class) hi.getClass();
				logger.log(Level.INFO, "Handler object = " + hi);
				logger.log(Level.INFO, "Class object = " + c);
				if (!c.equals(c1) && !c.equals(c2) && !c.equals(c3)) {
					TestUtil.logErr("Expected object1: " + c1);
					TestUtil.logErr("Expected object2: " + c2);
					TestUtil.logErr("Expected object3: " + c3);
					TestUtil.logErr("Unexpected object in chain: " + c);
					pass = false;
				}
			}
		} catch (UnsupportedOperationException e) {
			logger.log(Level.INFO, "Caught UnsupportedOperationException");
		} catch (WebServiceException e) {
			TestUtil.logErr("Caught unexpected WebServiceException", e);
			pass = false;
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("SetAndGetHandlerChainForDispatchObjTest failed", e);
		}

		if (!pass)
			throw new Exception("SetAndGetHandlerChainForDispatchObjTest failed");
	}

	/*
	 * @testName: SetAndGetHandlerChainForStubObjTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:2; JAXWS:JAVADOC:3;
	 *
	 * @test_Strategy: Sets the handler chain for a protocol binding instance. Gets
	 * the handler chain for a protocol binding instance.
	 */
	@Test
	public void SetAndGetHandlerChainForStubObjTest() throws Exception {
		TestUtil.logTrace("SetAndGetHandlerChainForStubObjTest");
		boolean pass = true;
		try {
			binding = bp.getBinding();
			logger.log(Level.INFO, "Stub object = " + port);
			logger.log(Level.INFO, "Binding object = " + binding);
			TestUtil.logMsg("Test setHandlerChain()/getHandlerChain() for Stub object");
			logger.log(Level.INFO, "Create a handler chain for SOAP 1.1 over HTTP protocol binding");
			logger.log(Level.INFO, "List<Handler> hc = new ArrayList<Handler>()");
			List<Handler> hc = new ArrayList<Handler>();
			TestUtil.logMsg("Construct ClientLogicalHandler1 and add to HandlerChain");
			hc.add(new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler1());
			TestUtil.logMsg("Construct ClientLogicalHandler2 and add to HandlerChain");
			hc.add(new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler2());
			TestUtil.logMsg("Construct ClientLogicalHandler3 and add to HandlerChain");
			hc.add(new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler3());
			logger.log(Level.INFO, "Set handler chain for protocol binding instance");
			TestUtil.logMsg("Calling Binding.setHandlerChain(java.util.List<Handler>)");
			binding.setHandlerChain(hc);
			TestUtil.logMsg("Now get the handler chain for protocol binding instance");
			TestUtil.logMsg("Calling java.util.List<Handler> Binding.getHandlerChain()");
			List<Handler> hl = binding.getHandlerChain();
			logger.log(Level.INFO, "HandlerChainList=" + hl);
			logger.log(Level.INFO, "HandlerChainSize = " + hl.size());
			if (hl.size() != 3) {
				TestUtil.logErr("Wrong size returned for HandlerChain");
				TestUtil.logErr("handlerchain1 size=" + hl.size() + ", handlerchain1 size=3");
				pass = false;
			}
			Class c1 = Class.forName("com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler1");
			Class c2 = Class.forName("com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler2");
			Class c3 = Class.forName("com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler3");
			logger.log(Level.INFO, "Walk through HandlerChain and verify contents");
			for (Handler hi : hl) {
				Class c = (Class) hi.getClass();
				logger.log(Level.INFO, "Handler object = " + hi);
				logger.log(Level.INFO, "Class object = " + c);
				if (!c.equals(c1) && !c.equals(c2) && !c.equals(c3)) {
					TestUtil.logErr("Expected object1: " + c1);
					TestUtil.logErr("Expected object2: " + c2);
					TestUtil.logErr("Expected object3: " + c3);
					TestUtil.logErr("Unexpected object in chain: " + c);
					pass = false;
				}
			}
		} catch (UnsupportedOperationException e) {
			logger.log(Level.INFO, "Caught UnsupportedOperationException");
		} catch (WebServiceException e) {
			TestUtil.logErr("Caught unexpected WebServiceException", e);
			pass = false;
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("SetAndGetHandlerChainForStubObjTest failed", e);
		}

		if (!pass)
			throw new Exception("SetAndGetHandlerChainForStubObjTest failed");
	}
}
