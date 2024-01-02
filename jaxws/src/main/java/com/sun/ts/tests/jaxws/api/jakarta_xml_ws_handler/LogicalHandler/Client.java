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
 *  $Id$
 */

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws_handler.LogicalHandler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.Constants;
import com.sun.ts.tests.jaxws.common.HandlerTracker;
import com.sun.ts.tests.jaxws.common.Handler_Util;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.DLHandlerService;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.GetTrackerData;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.GetTrackerDataAction;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.Hello;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.Hello2;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.MyActionType;
import com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.MyResultType;

import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.ProtocolException;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.handler.HandlerResolver;
import jakarta.xml.ws.handler.PortInfo;
import jakarta.xml.ws.soap.SOAPFaultException;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	// URL properties used by the test
	private static final String WSDLLOC_URL = "dlhandlerservice.wsdlloc.1";

	private static final String ENDPOINT1_URL = "dlhandlerservice.endpoint.1";

	private static final String ENDPOINT2_URL = "dlhandlerservice.endpoint.2";

	private static final String ENDPOINT4_URL = "dlhandlerservice.endpoint.4";

	private String url1 = null;

	private String url2 = null;

	private String url4 = null;

	private URL wsdlurl = null;

	// service and port information
	private static final String NAMESPACEURI = "http://dlhandlerservice.org/wsdl";

	private static final String SERVICE_NAME = "DLHandlerService";

	private static final String PORT_NAME1 = "HelloPort";

	private static final String PORT_NAME2 = "Hello2Port";

	private static final String PORT_NAME4 = "GetTrackerDataPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME1 = new QName(NAMESPACEURI, PORT_NAME1);

	private QName PORT_QNAME2 = new QName(NAMESPACEURI, PORT_NAME2);

	private static final Class SERVICE_CLASS = com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.DLHandlerService.class;

	private static final String THEBINDINGPROTOCOL = jakarta.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING;

	private static final String LOGICAL = "Logical";

	private static final String TEST_TYPE = LOGICAL + "Test";

	private Handler handler = null;

	Hello port1 = null;

	Hello2 port2 = null;

	GetTrackerData port4 = null;

	static DLHandlerService service = null;

	BindingProvider bp1 = null;

	BindingProvider bp2 = null;

	BindingProvider bp4 = null;

	Binding binding1 = null;

	Binding binding2 = null;

	Binding binding4 = null;

	List<Binding> listOfBindings = new ArrayList<Binding>();

	List<Handler> port1HandlerChain = null;

	List<Handler> port2HandlerChain = null;

	List<Handler> port4HandlerChain = null;

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(ENDPOINT1_URL);
		url1 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC_URL);
		wsdlurl = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT2_URL);
		url2 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT4_URL);
		url4 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		logger.log(Level.INFO, "Service Endpoint1 URL: " + url1);
		logger.log(Level.INFO, "Service Endpoint2 URL: " + url2);
		logger.log(Level.INFO, "Service Endpoint4 URL: " + url4);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
	}

	protected void getPortStandalone() throws Exception {
		getPorts();
		JAXWS_Util.setTargetEndpointAddress(port1, url1);
		JAXWS_Util.setTargetEndpointAddress(port2, url2);
		JAXWS_Util.setTargetEndpointAddress(port4, url4);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		getPorts();
		getTargetEndpointAddress(port1, port2, port4);
	}

	private void getTargetEndpointAddress(Object port1, Object port2, Object port4) throws Exception {
		logger.log(Level.INFO, "Get Target Endpoint Address for port1=" + port1);
		String url1 = JAXWS_Util.getTargetEndpointAddress(port1);
		logger.log(Level.INFO, "Target Endpoint Address=" + url1);
		logger.log(Level.INFO, "Get Target Endpoint Address for port2=" + port2);
		String url2 = JAXWS_Util.getTargetEndpointAddress(port2);
		logger.log(Level.INFO, "Target Endpoint Address=" + url2);
		logger.log(Level.INFO, "Get Target Endpoint Address for port4=" + port4);
		String url4 = JAXWS_Util.getTargetEndpointAddress(port4);
		logger.log(Level.INFO, "Target Endpoint Address=" + url4);
	}

	private void getPorts() throws Exception {
		TestUtil.logTrace("entering getPorts");

		logger.log(Level.INFO, "Get port 1 = " + PORT_NAME1);
		port1 = (Hello) service.getPort(Hello.class);
		logger.log(Level.INFO, "port1=" + port1);

		logger.log(Level.INFO, "Get port 2 = " + PORT_NAME2);
		port2 = (Hello2) service.getPort(Hello2.class);
		logger.log(Level.INFO, "port2=" + port2);

		logger.log(Level.INFO, "Get port 4 = " + PORT_NAME4);
		port4 = (GetTrackerData) service.getPort(GetTrackerData.class);
		logger.log(Level.INFO, "port4=" + port4);

		logger.log(Level.INFO, "Get binding for port 1 = " + PORT_NAME1);
		bp1 = (BindingProvider) port1;
		binding1 = bp1.getBinding();
		port1HandlerChain = binding1.getHandlerChain();
		logger.log(Level.INFO, "Port1 HandlerChain =" + port1HandlerChain);
		logger.log(Level.INFO, "Port1 HandlerChain size = " + port1HandlerChain.size());

		logger.log(Level.INFO, "------------------------------------------------------");

		logger.log(Level.INFO, "Get binding for port 2 = " + PORT_NAME2);
		bp2 = (BindingProvider) port2;
		binding2 = bp2.getBinding();
		port2HandlerChain = binding2.getHandlerChain();
		logger.log(Level.INFO, "Port2 HandlerChain=" + port2HandlerChain);
		logger.log(Level.INFO, "Port2 HandlerChain size = " + port2HandlerChain.size());

		logger.log(Level.INFO, "------------------------------------------------------");

		logger.log(Level.INFO, "Get binding for port 4 = " + PORT_NAME4);
		bp4 = (BindingProvider) port4;
		binding4 = bp4.getBinding();
		port4HandlerChain = binding4.getHandlerChain();
		logger.log(Level.INFO, "Port4 HandlerChain=" + port4HandlerChain);
		logger.log(Level.INFO, "Port4 HandlerChain size = " + port4HandlerChain.size());

		listOfBindings.add(binding1);
		listOfBindings.add(binding2);
		listOfBindings.add(binding4);

		TestUtil.logTrace("leaving getPorts");
	}

	public void getService() {
		service = (DLHandlerService) getSharedObject();
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
	 * @testName: ClientLogicalHandlerTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9012;
	 * JAXWS:SPEC:9014; JAXWS:SPEC:9015.1; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 * WS4EE:SPEC:6010; WS4EE:SPEC:6015.1; WS4EE:SPEC:6015.2; WS4EE:SPEC:6015.3;
	 * WS4EE:SPEC:6008; WS4EE:SPEC:6028; WS4EE:SPEC:6005; WS4EE:SPEC:6051;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the client-side logical
	 * message handler callbacks are called.
	 */
	@Test
	public void ClientLogicalHandlerTest() throws Exception {
		TestUtil.logTrace("ClientLogicalHandlerTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);
					if (info.getBindingID().equals(THEBINDINGPROTOCOL)) {
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Programmatically registering the following protocol based handlers through the binding: \n"
										+ "ClientSOAPHandler1, ClientLogicalHandler1");
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler1 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler1();
						handlerList.add(handler);
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler1 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler1();
						handlerList.add(handler);
					}
					if (info.getPortName().equals(PORT_QNAME1)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME1);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler2();
						handlerList.add(handler);
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler2 and add to HandlerChain");
						handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler2();
						handlerList.add(handler);
						if (info.getPortName().equals(PORT_QNAME1)) {
							logger.log(Level.INFO,
									"Construct HandleInfo for ClientSOAPHandler3 and add to HandlerChain");
							handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler3();
							handlerList.add(handler);
							logger.log(Level.INFO,
									"Construct HandleInfo for ClientLogicalHandler3 and add to HandlerChain");
							handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler3();
							handlerList.add(handler);
						}
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});

			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "Getting existing Handlers for Port1");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding1.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding1.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}
			if (pass) {

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				Handler_Util.clearHandlers(listOfBindings);

				logger.log(Level.INFO, "Get client side results back from Tracker");
				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				// verify client-side callbacks

				logger.log(Level.INFO, "Verify handleMessage()/init callbacks");
				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandlerCallBacks("Client", LOGICAL, clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO,
						"Verifying callbacks where LogicalHandlers were called before SOAPHandlers on Client-Side");
				if (!Handler_Util.VerifyLogicalVerseSOAPHandlerOrder(clientSideMsgs)) {
					TestUtil.logErr("Client-Side Logical verses SOAP Handler Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Logical verses SOAP Handler Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalHandlerTest failed");
	}

	/*
	 * @testName: ServerLogicalHandlerTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9002; JAXWS:SPEC:9007; JAXWS:SPEC:9014;
	 * JAXWS:SPEC:9015.1; JAXWS:SPEC:9017; JAXWS:SPEC:9018; WS4EE:SPEC:6010;
	 * WS4EE:SPEC:6008; WS4EE:SPEC:6028; WS4EE:SPEC:6005; WS4EE:SPEC:6051;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the server-side soap
	 * message handler callbacks are called.
	 */
	@Test
	public void ServerLogicalHandlerTest() throws Exception {
		TestUtil.logTrace("ServerLogicalHandlerTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				logger.log(Level.INFO, "Invoking RPC method port1.doHandlerTest1()");
				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				MyResultType mr = null;
				try {
					mr = port1.doHandlerTest1(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				if (mr != null) {
					String result = mr.getErrors();
					if (!result.equals("")) {
						pass = false;
						TestUtil.logErr("The serverside tests for MessageContext.Scope failed:" + result);
					}
				}

				List<String> serverSideMsgs = null;

				logger.log(Level.INFO, "Get server side result back from endpoint");
				GetTrackerDataAction gtda = new GetTrackerDataAction();
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				logger.log(Level.INFO, "Verifying Server-Side Handler callbacks");
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);

				if (!Handler_Util.VerifyHandlerCallBacks("Server", LOGICAL, serverSideMsgs)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}

				logger.log(Level.INFO,
						"Verifying callbacks where LogicalHandlers are called before SOAPHandlers on Server-Side");
				if (!Handler_Util.VerifyLogicalVerseSOAPHandlerOrder(serverSideMsgs)) {
					TestUtil.logErr("Server-Side Logical verses SOAP Handler Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Logical verses SOAP Handler Callbacks are (correct)");
				}
				gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side throwables back from endpoint");
				gtda.setAction("getArrayThrowables");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideThrowables = port4.getTrackerData(gtda).getResult();
				if (serverSideThrowables.size() >= 1) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					Iterator iterator = serverSideThrowables.iterator();
					while (iterator.hasNext()) {
						TestUtil.logErr((String) iterator.next());
					}
					pass = false;
				}

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("ServerLogicalHandlerTest failed");
	}

	/*
	 * @testName: ClientLogicalInboundHandleMessageThrowsRuntimeExceptionTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9015.4.2; JAXWS:SPEC:9016.1; JAXWS:SPEC:9017;
	 * JAXWS:SPEC:9018; WS4EE:SPEC:6008; WS4EE:SPEC:6028; WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ClientLogicalHandler4 throws a RuntimeException in handleMessage method
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doInbound()
	 * ClientLogicalHandler4.handleMessage().doInbound() ClientLogicalHandler4
	 * Throwing an inbound RuntimeException ClientLogicalHandler6.close()
	 * ClientLogicalHandler4.close() ClientLogicalHandler5.close()
	 */
	@Test
	public void ClientLogicalInboundHandleMessageThrowsRuntimeExceptionTest() throws Exception {
		TestUtil.logTrace("ClientLogicalInboundHandleMessageThrowsRuntimeExceptionTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);

					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});
			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalInboundHandleMessageThrowsRuntimeExceptionTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting RuntimeException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected RuntimeException");
					pass = false;
				} catch (RuntimeException e) {
					logger.log(Level.INFO, "Did get expected RuntimeException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "ClientLogicalHandler4.handleMessage throwing an inbound RuntimeException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected RuntimeException text");
					else {
						TestUtil.logErr("Did not get expected RuntimeException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandlerExceptionCallBacks("Client", LOGICAL, false, Constants.INBOUND,
						clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalInboundHandleMessageThrowsRuntimeExceptionTest failed");
	}

	/*
	 * @testName: ClientLogicalInboundHandleMessageThrowsSOAPFaultTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9015.3.2; WS4EE:SPEC:6008; WS4EE:SPEC:6028;
	 * WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ClientLogicalHandler4 throws a SOAPFaultException in handleMessage method
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doInbound()
	 * ClientLogicalHandler4.handleMessage().doInbound() ClientLogicalHandler4
	 * Throwing an inbound SOAPFaultException ClientLogicalHandler6.close()
	 * ClientLogicalHandler4.close() ClientLogicalHandler5.close()
	 */
	@Test
	public void ClientLogicalInboundHandleMessageThrowsSOAPFaultTest() throws Exception {
		TestUtil.logTrace("ClientLogicalInboundHandleMessageThrowsSOAPFaultTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);

					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});
			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalInboundHandleMessageThrowsSOAPFaultTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting WebServiceException that wraps a SOAPFaultException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected WebServiceException");
					pass = false;
				} catch (WebServiceException e) {
					logger.log(Level.INFO, "Did get expected WebServiceException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "SOAPFaultException: ClientLogicalHandler4.handleMessage throwing an inbound SOAPFaultException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected WebServiceException text");
					else {
						TestUtil.logErr("Did not get expected WebServiceException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandleMessageExceptionCallBacks("Client", LOGICAL, clientSideMsgs,
						Constants.INBOUND)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalInboundHandleMessageThrowsSOAPFaultTest failed");
	}

	/*
	 * @testName: ClientLogicalOutboundHandleMessageThrowsSOAPFaultTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9015.3.1; WS4EE:SPEC:6008; WS4EE:SPEC:6028;
	 * WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ClientLogicalHandler4 throws a SOAPFaultException in handleMessage method
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound() ClientLogicalHandler4
	 * Throwing an inbound SOAPFaultException ClientLogicalHandler4.close()
	 * ClientLogicalHandler5.close()
	 */
	@Test
	public void ClientLogicalOutboundHandleMessageThrowsSOAPFaultTest() throws Exception {
		TestUtil.logTrace("ClientLogicalOutboundHandleMessageThrowsSOAPFaultTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);

					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});
			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalOutboundHandleMessageThrowsSOAPFaultTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting SOAPFaultException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected SOAPFaultException");
					pass = false;
				} catch (SOAPFaultException e) {
					logger.log(Level.INFO, "Did get expected SOAPFaultException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "ClientLogicalHandler4.handleMessage throwing an outbound SOAPFaultException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected SOAPFaultException text");
					else {
						TestUtil.logErr("Did not get expected SOAPFaultException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandleMessageExceptionCallBacks("Client", LOGICAL, clientSideMsgs,
						Constants.OUTBOUND)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalOutboundHandleMessageThrowsSOAPFaultTest failed");
	}

	/*
	 * @testName: ServerLogicalInboundHandleMessageThrowsRuntimeExceptionTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9015.4.1; JAXWS:SPEC:9016.1; JAXWS:SPEC:9017;
	 * JAXWS:SPEC:9018; WS4EE:SPEC:6008; WS4EE:SPEC:6028; WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the server-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ServerLogicalHandler4 throws a RuntimeException in handleMessage method
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ServerLogicalHandler6.handleMessage().doInbound()
	 * ServerLogicalHandler4.handleMessage().doInbound() ServerLogicalHandler4
	 * Throwing an inbound RuntimeException ServerLogicalHandler4.close()
	 * ServerLogicalHandler6.close()
	 */
	@Test
	public void ServerLogicalInboundHandleMessageThrowsRuntimeExceptionTest() throws Exception {
		TestUtil.logTrace("ServerLogicalInboundHandleMessageThrowsRuntimeExceptionTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalInboundHandleMessageThrowsRuntimeExceptionTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting SOAPFaultException wrapped in a WebServiceException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected WebServiceException");
					pass = false;
				} catch (WebServiceException e) {
					logger.log(Level.INFO, "Did get expected WebServiceException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					if (e instanceof jakarta.xml.ws.soap.SOAPFaultException)
						logger.log(Level.INFO, "Did get expected nested SOAPFaultException");
					else {
						TestUtil.logErr("Did not get expected nested SOAPFaultException");
						TestUtil.printStackTrace(e);
						pass = false;
					}
					String tmp = "ServerLogicalHandler4.handleMessage throwing an inbound RuntimeException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected nested SOAPFaultException text");
					else {
						TestUtil.logErr("Did not get expected nested SOAPFaultException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				GetTrackerDataAction gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side result back from endpoint");
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideMsgs = null;
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);
				if (!Handler_Util.VerifyHandlerExceptionCallBacks("Server", LOGICAL, false, Constants.INBOUND,
						serverSideMsgs)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}
				gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side throwables back from endpoint");
				gtda.setAction("getArrayThrowables");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideThrowables = port4.getTrackerData(gtda).getResult();
				if (serverSideThrowables.size() >= 1) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					Iterator iterator = serverSideThrowables.iterator();
					while (iterator.hasNext()) {
						TestUtil.logErr((String) iterator.next());
					}
					pass = false;
				}

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("ServerLogicalInboundHandleMessageThrowsRuntimeExceptionTest failed");
	}

	/*
	 * @testName: ServerLogicalInboundHandleMessageThrowsSOAPFaultTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9015.3.1; WS4EE:SPEC:6008; WS4EE:SPEC:6028;
	 * WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the server-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ServerLogicalHandler4 throws a RuntimeException in handleMessage method while
	 * processing an inbound message.
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ServerLogicalHandler6.handleMessage().doInbound()
	 * ServerLogicalHandler4.handleMessage().doInbound() ServerLogicalHandler4
	 * Throwing an inbound SOAPFaultException
	 * ServerLogicalHandler6.handleException() ServerLogicalHandler4.close()
	 * ServerLogicalHandler6.close()
	 */
	@Test
	public void ServerLogicalInboundHandleMessageThrowsSOAPFaultTest() throws Exception {
		TestUtil.logTrace("ServerLogicalInboundHandleMessageThrowsSOAPFaultTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalInboundHandleMessageThrowsSOAPFaultTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting SOAPFaultException wrapped by a WebServiceException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected WebServiceException");
					pass = false;
				} catch (WebServiceException e) {
					logger.log(Level.INFO, "Did get expected WebServiceException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					if (e instanceof jakarta.xml.ws.soap.SOAPFaultException)
						logger.log(Level.INFO, "Did get expected nested SOAPFaultException");
					else {
						TestUtil.logErr("Did not get expected nested SOAPFaultException");
						TestUtil.printStackTrace(e);
						pass = false;
					}
					String tmp = "ServerLogicalHandler4.handleMessage throwing an inbound SOAPFaultException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected nested SOAPFaultException text");
					else {
						TestUtil.logErr("Did not get expected nested SOAPFaultException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				GetTrackerDataAction gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side result back from endpoint");
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideMsgs = null;
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);
				if (!Handler_Util.VerifyHandleMessageExceptionCallBacks("Server", LOGICAL, serverSideMsgs,
						Constants.INBOUND)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}
				gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side throwables back from endpoint");
				gtda.setAction("getArrayThrowables");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideThrowables = port4.getTrackerData(gtda).getResult();
				if (serverSideThrowables.size() >= 1) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					Iterator iterator = serverSideThrowables.iterator();
					while (iterator.hasNext()) {
						TestUtil.logErr((String) iterator.next());
					}
					pass = false;
				}

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("ServerLogicalInboundHandleMessageThrowsSOAPFaultTest failed");
	}

	/*
	 * @testName: ServerLogicalOutboundHandleMessageThrowsSOAPFaultTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9015.3.2; WS4EE:SPEC:6008; WS4EE:SPEC:6028;
	 * WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the server-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ServerLogicalHandler4 throws a SOAPFaultException in handleMessage method
	 * while processing an outbound message.
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ServerLogicalHandler6.handleMessage().doInbound()
	 * ServerLogicalHandler4.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleMessage().doOutbound()
	 * ServerLogicalHandler4.handleMessage().doOutbound() ServerLogicalHandler4
	 * Throwing an outbound SOAPFaultException ServerLogicalHandler5.close()
	 * ServerLogicalHandler4.close() ServerLogicalHandler6.close()
	 */
	@Test
	public void ServerLogicalOutboundHandleMessageThrowsSOAPFaultTest() throws Exception {
		TestUtil.logTrace("ServerLogicalOutboundHandleMessageThrowsSOAPFaultTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalOutboundHandleMessageThrowsSOAPFaultTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting SOAPFaultException wrapped by a WebServiceException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected WebServiceException");
					pass = false;
				} catch (WebServiceException e) {
					logger.log(Level.INFO, "Did get expected WebServiceException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					if (e instanceof jakarta.xml.ws.soap.SOAPFaultException)
						logger.log(Level.INFO, "Did get expected nested SOAPFaultException");
					else {
						TestUtil.logErr("Did not get expected nested SOAPFaultException");
						TestUtil.printStackTrace(e);
						pass = false;
					}
					String tmp = "ServerLogicalHandler4.handleMessage throwing an outbound SOAPFaultException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected nested SOAPFaultException text");
					else {
						TestUtil.logErr("Did not get expected nested SOAPFaultException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				GetTrackerDataAction gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side result back from endpoint");
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideMsgs = null;
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);
				if (!Handler_Util.VerifyHandleMessageExceptionCallBacks("Server", LOGICAL, serverSideMsgs,
						Constants.OUTBOUND)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}
				gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side throwables back from endpoint");
				gtda.setAction("getArrayThrowables");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideThrowables = port4.getTrackerData(gtda).getResult();
				if (serverSideThrowables.size() >= 1) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					Iterator iterator = serverSideThrowables.iterator();
					while (iterator.hasNext()) {
						TestUtil.logErr((String) iterator.next());
					}
					pass = false;
				}

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("ServerLogicalOutboundHandleMessageThrowsSOAPFaultTest failed");
	}

	/*
	 * @testName: ClientLogicalOutboundHandleMessageThrowsRuntimeExceptionTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9015.4.1; JAXWS:SPEC:9016.1; JAXWS:SPEC:9017;
	 * JAXWS:SPEC:9018; WS4EE:SPEC:6008; WS4EE:SPEC:6028; WS4EE:SPEC:6005;
	 *
	 * 
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ClientLogicalHandler4 throws a RuntimeException in handleMessage method while
	 * processing an outbound message.
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound() ClientLogicalHandler4
	 * Throwing an inbound RuntimeException ClientLogicalHandler4.close()
	 * ClientLogicalHandler5.close()
	 */
	@Test
	public void ClientLogicalOutboundHandleMessageThrowsRuntimeExceptionTest() throws Exception {
		TestUtil.logTrace("ClientLogicalOutboundHandleMessageThrowsRuntimeExceptionTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);

					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}
			});
			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalOutboundHandleMessageThrowsRuntimeExceptionTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting RuntimeException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected RuntimeException");
					pass = false;
				} catch (RuntimeException e) {
					logger.log(Level.INFO, "Did get expected RuntimeException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "ClientLogicalHandler4.handleMessage throwing an outbound RuntimeException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected RuntimeException text");
					else {
						TestUtil.logErr("Did not get expected RuntimeException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandlerExceptionCallBacks("Client", LOGICAL, false, Constants.OUTBOUND,
						clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
			}

		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalOutboundHandleMessageThrowsRuntimeExceptionTest failed");
	}

	/*
	 * @testName: ServerLogicalOutboundHandleMessageThrowsRuntimeExceptionTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9015.4.2; JAXWS:SPEC:9016.1; JAXWS:SPEC:9017;
	 * JAXWS:SPEC:9018; WS4EE:SPEC:6008; WS4EE:SPEC:6028; WS4EE:SPEC:6005;
	 *
	 * 
	 * @test_Strategy: Invoke an RPC method. Verify that the server-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ServerLogicalHandler4 throws a RuntimeException in handleMessage method while
	 * processing an outbound message.
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ServerLogicalHandler6.handleMessage().doInbound()
	 * ServerLogicalHandler4.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleMessage().doOutbound()
	 * ServerLogicalHandler4.handleMessage().doOutbound() ServerLogicalHandler4
	 * Throwing an outbound RuntimeException ServerLogicalHandler5.close()
	 * ServerLogicalHandler4.close() ServerLogicalHandler6.close()
	 */
	@Test
	public void ServerLogicalOutboundHandleMessageThrowsRuntimeExceptionTest() throws Exception {
		TestUtil.logTrace("ServerLogicalOutboundHandleMessageThrowsRuntimeExceptionTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalOutboundHandleMessageThrowsRuntimeExceptionTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting RuntimeException wrapped by a WebServiceException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected WebServiceException");
					pass = false;
				} catch (WebServiceException e) {
					logger.log(Level.INFO, "Did get expected WebServiceException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					if (e instanceof jakarta.xml.ws.soap.SOAPFaultException)
						logger.log(Level.INFO, "Did get expected nested SOAPFaultException");
					else {
						TestUtil.logErr("Did not get expected nested SOAPFaultException");
						TestUtil.printStackTrace(e);
						pass = false;
					}
					String tmp = "ServerLogicalHandler4.handleMessage throwing an outbound RuntimeException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected nested RuntimeException text");
					else {
						TestUtil.logErr("Did not get expected nested RuntimeException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				GetTrackerDataAction gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side result back from endpoint");
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideMsgs = null;
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);
				if (!Handler_Util.VerifyHandlerExceptionCallBacks("Server", LOGICAL, false, Constants.OUTBOUND,
						serverSideMsgs)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}
				gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side throwables back from endpoint");
				gtda.setAction("getArrayThrowables");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideThrowables = port4.getTrackerData(gtda).getResult();
				if (serverSideThrowables.size() >= 1) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					Iterator iterator = serverSideThrowables.iterator();
					while (iterator.hasNext()) {
						TestUtil.logErr((String) iterator.next());
					}
					pass = false;
				}

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("ServerLogicalOutboundHandleMessageThrowsRuntimeExceptionTest failed");
	}

	/*
	 * @testName: ServerEndpointRemoteRuntimeExceptionTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9016.4; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 * WS4EE:SPEC:6008; WS4EE:SPEC:6028; WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the server-side handlers
	 * callbacks are called. Endpoint throws a RuntimeException that is wrapped by a
	 * WebServiceException
	 *
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ServerLogicalHandler6.handleMessage().doInbound()
	 * ServerLogicalHandler4.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleException()
	 * ServerLogicalHandler4.handleException()
	 * ServerLogicalHandler6.handleException() ServerLogicalHandler5.close()
	 * ServerLogicalHandler4.close() ServerLogicalHandler6.close()
	 * 
	 */
	@Test
	public void ServerEndpointRemoteRuntimeExceptionTest() throws Exception {
		TestUtil.logTrace("ServerEndpointRemoteRuntimeExceptionTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				MyActionType ma = new MyActionType();
				ma.setAction("EndpointRemoteRuntimeExceptionTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting RuntimeException wrapped by a WebServiceException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected WebServiceException");
					pass = false;
				} catch (WebServiceException e) {
					logger.log(Level.INFO, "Did get expected WebServiceException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					if (baos.toString().indexOf("RuntimeException") > -1)
						logger.log(Level.INFO, "Did get expected nested RuntimeException");
					else {
						TestUtil.logErr("Did not get expected nested RuntimeException");
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				GetTrackerDataAction gtda = new GetTrackerDataAction();
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideMsgs = null;
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);
				if (!Handler_Util.VerifyHandlerExceptionCallBacks("Server", LOGICAL, true, "", serverSideMsgs)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}
				gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side throwables back from endpoint");
				gtda.setAction("getArrayThrowables");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideThrowables = port4.getTrackerData(gtda).getResult();
				if (serverSideThrowables.size() >= 1) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					Iterator iterator = serverSideThrowables.iterator();
					while (iterator.hasNext()) {
						TestUtil.logErr((String) iterator.next());
					}
					pass = false;
				}

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("ServerEndpointRemoteRuntimeExceptionTest failed");
	}

	/*
	 * @testName: ServerEndpointRemoteSOAPFaultExceptionTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9016.3; JAXWS:SPEC:9017; JAXWS:SPEC:9018;
	 * WS4EE:SPEC:6008; WS4EE:SPEC:6028; WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method and ensure that the server-side handler
	 * callbacks are called. Endpoint throws a SOAPFaultException that is wrapped by
	 * a WebServiceException
	 *
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ServerLogicalHandler6.handleMessage().doInbound()
	 * ServerLogicalHandler4.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleException()
	 * ServerLogicalHandler4.handleException()
	 * ServerLogicalHandler6.handleException() ServerLogicalHandler5.close()
	 * ServerLogicalHandler4.close() ServerLogicalHandler6.close()
	 * 
	 */
	@Test
	public void ServerEndpointRemoteSOAPFaultExceptionTest() throws Exception {
		TestUtil.logTrace("ServerEndpointRemoteSOAPFaultExceptionTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				MyActionType ma = new MyActionType();
				ma.setAction("EndpointRemoteSOAPFaultExceptionTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting SOAPFaultException wrapped by a WebServiceException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected WebServiceException");
					pass = false;
				} catch (WebServiceException e) {
					logger.log(Level.INFO, "Did get expected WebServiceException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					if (baos.toString().indexOf("SOAPFaultException") > -1)
						logger.log(Level.INFO, "Did get expected nested SOAPFaultException");
					else {
						TestUtil.logErr("Did not get expected nested SOAPFaultException");
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				GetTrackerDataAction gtda = new GetTrackerDataAction();
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideMsgs = null;
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);
				if (!Handler_Util.VerifyHandlerExceptionCallBacks("Server", LOGICAL, true, "", serverSideMsgs)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}
				gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side throwables back from endpoint");
				gtda.setAction("getArrayThrowables");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideThrowables = port4.getTrackerData(gtda).getResult();
				if (serverSideThrowables.size() >= 1) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					Iterator iterator = serverSideThrowables.iterator();
					while (iterator.hasNext()) {
						TestUtil.logErr((String) iterator.next());
					}
					pass = false;
				}

				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}
		if (!pass)
			throw new Exception("ServerEndpointRemoteSOAPFaultExceptionTest failed");
	}

	/*
	 * @testName: ServerLogicalInboundHandleMessageFalseTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9015.2.1; WS4EE:SPEC:6008; WS4EE:SPEC:6028;
	 * WS4EE:SPEC:6005; JAXWS:SPEC:11003; JAXWS:SPEC:10007
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the server-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ServerLogicalHandler4 returns false in the handleMessage method while
	 * processing an inbound message.
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ServerLogicalHandler6.handleMessage().doInbound()
	 * ServerLogicalHandler4.handleMessage().doInbound()
	 * ServerLogicalHandler6.handleMessage().doOutbound()
	 * ServerLogicalHandler4.close() ServerLogicalHandler6.close()
	 */
	@Test
	public void ServerLogicalInboundHandleMessageFalseTest() throws Exception {
		TestUtil.logTrace("ServerLogicalInboundHandleMessageFalseTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalInboundHandleMessageFalseTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					port2.doHandlerTest2(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				GetTrackerDataAction gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side result back from endpoint");
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideMsgs = null;
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);
				if (!Handler_Util.VerifyHandleMessageFalseCallBacks("Server", LOGICAL, serverSideMsgs,
						Constants.INBOUND)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("ServerLogicalInboundHandleMessageFalseTest failed");
	}

	/*
	 * @testName: ServerLogicalOutboundHandleMessageFalseTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9015.2.2; WS4EE:SPEC:6008; WS4EE:SPEC:6028;
	 * WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the server-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ServerLogicalHandler4 returns false in the handleMessage method while
	 * processing an outbound message.
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ServerLogicalHandler6.handleMessage().doInbound()
	 * ServerLogicalHandler4.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleMessage().doOutbound()
	 * ServerLogicalHandler4.handleMessage().doOutbound()
	 * ServerLogicalHandler5.close() ServerLogicalHandler4.close()
	 * ServerLogicalHandler6.close()
	 */
	@Test
	public void ServerLogicalOutboundHandleMessageFalseTest() throws Exception {
		TestUtil.logTrace("ServerLogicalOutboundHandleMessageFalseTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalOutboundHandleMessageFalseTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					port2.doHandlerTest2(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				GetTrackerDataAction gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side result back from endpoint");
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideMsgs = null;
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);
				if (!Handler_Util.VerifyHandleMessageFalseCallBacks("Server", LOGICAL, serverSideMsgs,
						Constants.OUTBOUND)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("ServerLogicalOutboundHandleMessageFalseTest failed");
	}

	/*
	 * @testName: ClientLogicalInboundHandleMessageFalseTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9015.2.2; WS4EE:SPEC:6008; WS4EE:SPEC:6028;
	 * WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ClientLogicalHandler4 returns false in the handleMessage method while
	 * processing an inbound message.
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doInbound()
	 * ClientLogicalHandler4.handleMessage().doInbound()
	 * ClientLogicalHandler6.close() ClientLogicalHandler4.close()
	 * ClientLogicalHandler5.close()
	 */
	@Test
	public void ClientLogicalInboundHandleMessageFalseTest() throws Exception {
		TestUtil.logTrace("ClientLogicalInboundHandleMessageFalseTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);

					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});
			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalInboundHandleMessageFalseTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					port2.doHandlerTest2(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandleMessageFalseCallBacks("Client", LOGICAL, clientSideMsgs,
						Constants.INBOUND)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalInboundHandleMessageFalseTest failed");
	}

	/*
	 * @testName: ClientLogicalOutboundHandleMessageFalseTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9015.2.1; WS4EE:SPEC:6008; WS4EE:SPEC:6028;
	 * WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ClientLogicalHandler4 returns false in the handleMessage method while
	 * processing an outbound message.
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound()
	 * ClientLogicalHandler5.handleMessage().doInbound()
	 * ClientLogicalHandler4.close() ClientLogicalHandler5.close()
	 */
	@Test
	public void ClientLogicalOutboundHandleMessageFalseTest() throws Exception {
		TestUtil.logTrace("ClientLogicalOutboundHandleMessageFalseTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);

					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});
			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalOutboundHandleMessageFalseTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					port2.doHandlerTest2(ma);
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandleMessageFalseCallBacks("Client", LOGICAL, clientSideMsgs,
						Constants.OUTBOUND)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalOutboundHandleMessageFalseTest failed");
	}

	/*
	 * @testName: ClientLogicalOutboundHandleFaultFalseTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9016.2; WS4EE:SPEC:6008; WS4EE:SPEC:6028;
	 * WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side handleFault
	 * callbacks are called by the JAXWS RUNTIME. ClientLogicalHandler6 throws a
	 * SOAPFaultException ClientLogicalHandler4 returns a false for handleFault
	 * method while processing an outbound message.
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doOutbound() ClientLogicalHandler6
	 * Throwing an outbound SOAPFaultException
	 * ClientLogicalHandler4.handleException() ClientLogicalHandler6.close()
	 * ClientLogicalHandler4.close() ClientLogicalHandler5.close()
	 * 
	 */
	@Test
	public void ClientLogicalOutboundHandleFaultFalseTest() throws Exception {
		TestUtil.logTrace("ClientLogicalOutboundHandleFaultFalseTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);

					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});
			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalOutboundHandleFaultFalseTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting RuntimeException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected RuntimeException");
					pass = false;
				} catch (RuntimeException e) {
					logger.log(Level.INFO, "Did get expected RuntimeException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "ClientLogicalHandler6.handleMessage throws SOAPFaultException for ClientLogicalOutboundHandleFaultFalseTest";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected RuntimeException text");
					else {
						TestUtil.logErr("Did not get expected RuntimeException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandleFaultFalseCallBacks("Client", LOGICAL, clientSideMsgs,
						Constants.OUTBOUND)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalOutboundHandleFaultFalseTest failed");
	}

	/*
	 * @testName: ServerLogicalInboundHandleFaultFalseTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9016.2; WS4EE:SPEC:6008; WS4EE:SPEC:6028;
	 * WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the server-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ServerLogicalHandler5 throws a SOAPFaultException ServerLogicalHandler4
	 * returns a false for handleFault method while processing an outbound message.
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ServerLogicalHandler6.handleMessage().doInbound()
	 * ServerLogicalHandler4.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleMessage().doInbound() ServerLogicalHandler5
	 * Throwing an inbound SOAPFaultException
	 * ServerLogicalHandler4.handleException() ServerLogicalHandler5.close()
	 * ServerLogicalHandler4.close() ServerLogicalHandler6.close()
	 */
	@Test
	public void ServerLogicalInboundHandleFaultFalseTest() throws Exception {
		TestUtil.logTrace("ServerLogicalInboundHandleFaultFalseTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalInboundHandleFaultFalseTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting RuntimeException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected RuntimeException");
					pass = false;
				} catch (RuntimeException e) {
					logger.log(Level.INFO, "Did get expected RuntimeException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "ServerLogicalHandler5.handleMessage throws SOAPFaultException for ServerLogicalInboundHandleFaultFalseTest";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected RuntimeException text");
					else {
						TestUtil.logErr("Did not get expected RuntimeException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				GetTrackerDataAction gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side result back from endpoint");
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideMsgs = null;
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);
				if (!Handler_Util.VerifyHandleFaultFalseCallBacks("Server", LOGICAL, serverSideMsgs,
						Constants.INBOUND)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("ServerLogicalInboundHandleFaultFalseTest failed");
	}

	/*
	 * @testName: ClientLogicalOutboundHandleFaultThrowsRuntimeExceptionTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9016.4; WS4EE:SPEC:6008; WS4EE:SPEC:6028;
	 * WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ClientLogicalHandler6 throws a SOAPFaultException ClientLogicalHandler4
	 * throws a RuntimeException in handleFault method processing an inbound
	 * message. ------------------------------------------------------- This is the
	 * expected order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doOutbound() ClientLogicalHandler6
	 * Throwing an outbound SOAPFaultException
	 * ClientLogicalHandler4.handleException() ClientLogicalHandler4 Throwing an
	 * inbound RuntimeException ClientLogicalHandler6.close()
	 * ClientLogicalHandler4.close() ClientLogicalHandler5.close()
	 */
	@Test
	public void ClientLogicalOutboundHandleFaultThrowsRuntimeExceptionTest() throws Exception {
		TestUtil.logTrace("ClientLogicalOutboundHandleFaultThrowsRuntimeExceptionTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);

					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});
			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalOutboundHandleFaultThrowsRuntimeExceptionTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);

				try {
					logger.log(Level.INFO, "Expecting RuntimeException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected RuntimeException");
					pass = false;
				} catch (RuntimeException e) {
					logger.log(Level.INFO, "Did get expected RuntimeException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "ClientLogicalHandler4.handleFault throwing an inbound RuntimeException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected RuntimeException text");
					else {
						TestUtil.logErr("Did not get expected RuntimeException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandleFaultRuntimeExceptionCallBacks("Client", LOGICAL, clientSideMsgs,
						Constants.OUTBOUND)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalOutboundHandleFaultThrowsRuntimeExceptionTest failed");
	}

	/*
	 * @testName: ServerLogicalInboundHandleFaultThrowsRuntimeExceptionTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9016.4; WS4EE:SPEC:6008; WS4EE:SPEC:6028;
	 * WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the server-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ServerLogicalHandler5 throws a SOAPFaultException ServerLogicalHandler4
	 * throws a RuntimeException in the handleFault method processing an outbound
	 * message. ------------------------------------------------------- This is the
	 * expected order -------------------------------------------------------
	 * ServerLogicalHandler6.handleMessage().doInbound()
	 * ServerLogicalHandler4.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleMessage().doInbound() ServerLogicalHandler5
	 * Throwing an inbound SOAPFaultException
	 * ServerLogicalHandler4.handleException() ServerLogicalHandler4 Throwing an
	 * outbound RuntimeException ServerLogicalHandler5.close()
	 * ServerLogicalHandler4.close() ServerLogicalHandler6.close()
	 */
	@Test
	public void ServerLogicalInboundHandleFaultThrowsRuntimeExceptionTest() throws Exception {
		TestUtil.logTrace("ServerLogicalInboundHandleFaultThrowsRuntimeExceptionTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalInboundHandleFaultThrowsRuntimeExceptionTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting RuntimeException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected RuntimeException");
					pass = false;
				} catch (RuntimeException e) {
					logger.log(Level.INFO, "Did get expected RuntimeException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "ServerLogicalHandler4.handleFault throwing an outbound RuntimeException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected RuntimeException text");
					else {
						TestUtil.logErr("Did not get expected RuntimeException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				GetTrackerDataAction gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side result back from endpoint");
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideMsgs = null;
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);
				if (!Handler_Util.VerifyHandleFaultRuntimeExceptionCallBacks("Server", LOGICAL, serverSideMsgs,
						Constants.INBOUND)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("ServerLogicalInboundHandleFaultThrowsRuntimeExceptionTest failed");
	}

	/*
	 * @testName: ClientLogicalOutboundHandleFaultThrowsSOAPFaultExceptionTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9016.3; WS4EE:SPEC:6008; WS4EE:SPEC:6028;
	 * WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ClientLogicalHandler6 throws a SOAPFaultException ClientLogicalHandler4
	 * throws a SOAPFaultException in the handleFault method processing an inbound
	 * message. ------------------------------------------------------- This is the
	 * expected order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doOutbound() ClientLogicalHandler6
	 * Throwing an outbound SOAPFaultException
	 * ClientLogicalHandler4.handleException() ClientLogicalHandler4 Throwing an
	 * inbound SOAPFaultException ClientLogicalHandler6.close()
	 * ClientLogicalHandler4.close() ClientLogicalHandler5.close()
	 */
	@Test
	public void ClientLogicalOutboundHandleFaultThrowsSOAPFaultExceptionTest() throws Exception {
		TestUtil.logTrace("ClientLogicalOutboundHandleFaultThrowsSOAPFaultExceptionTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);

					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});
			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalOutboundHandleFaultThrowsSOAPFaultExceptionTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);

				try {
					logger.log(Level.INFO, "Expecting WebServiceException that wraps a SOAPFaultException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected WebServiceException");
					pass = false;
				} catch (WebServiceException e) {
					logger.log(Level.INFO, "Did get expected WebServiceException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "SOAPFaultException: ClientLogicalHandler4.handleFault throwing an inbound SOAPFaultException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected WebServiceException text");
					else {
						TestUtil.logErr("Did not get expected WebServiceException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandleFaultSOAPFaultExceptionCallBacks("Client", LOGICAL, clientSideMsgs,
						Constants.OUTBOUND)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalOutboundHandleFaultThrowsSOAPFaultExceptionTest failed");
	}

	/*
	 * @testName: ServerLogicalInboundHandleFaultThrowsSOAPFaultExceptionTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9016.3; WS4EE:SPEC:6008; WS4EE:SPEC:6028;
	 * WS4EE:SPEC:6005;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the server-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ServerLogicalHandler5 throws a SOAPFaultException ServerLogicalHandler4
	 * throws a SOAPFaultException in handleFailt method processing an outbound
	 * message. ------------------------------------------------------- This is the
	 * expected order -------------------------------------------------------
	 * ServerLogicalHandler6.handleMessage().doInbound()
	 * ServerLogicalHandler4.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleMessage().doInbound() ServerLogicalHandler5
	 * Throwing an inbound SOAPFaultException
	 * ServerLogicalHandler4.handleException() ServerLogicalHandler4 Throwing an
	 * outbound SOAPFaultException ServerLogicalHandler5.close()
	 * ServerLogicalHandler4.close() ServerLogicalHandler6.close()
	 */
	@Test
	public void ServerLogicalInboundHandleFaultThrowsSOAPFaultExceptionTest() throws Exception {
		TestUtil.logTrace("ServerLogicalInboundHandleFaultThrowsSOAPFaultExceptionTest");
		boolean pass = true;
		if (!setupPorts()) {
			pass = false;
		}
		if (pass) {
			Handler_Util.clearHandlers(listOfBindings);
			try {
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();

				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalInboundHandleFaultThrowsSOAPFaultExceptionTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting SOAPFaultException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected SOAPFaultException");
					pass = false;
				} catch (SOAPFaultException e) {
					logger.log(Level.INFO, "Did get expected SOAPFaultException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "ServerLogicalHandler4.handleFault throwing an outbound SOAPFaultException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected SOAPFaultException text");
					else {
						TestUtil.logErr("Did not get expected SOAPFaultException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				GetTrackerDataAction gtda = new GetTrackerDataAction();
				logger.log(Level.INFO, "Get server side result back from endpoint");
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideMsgs = null;
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);
				if (!Handler_Util.VerifyHandleFaultSOAPFaultExceptionCallBacks("Server", LOGICAL, serverSideMsgs,
						Constants.INBOUND)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			} catch (Exception e) {
				TestUtil.logErr("Exception occurred: " + e);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("ServerLogicalInboundHandleFaultThrowsSOAPFaultExceptionTest failed");
	}

	/*
	 * @testName: ServerLogicalInboundHandlerThrowsSOAPFaultToClientHandlersTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9016; JAXWS:SPEC:9016.2;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side handler
	 * callbacks are called by the JAXWS RUNTIME. Server handler throws a
	 * SOAPFaultException while processing an inbound message and Client should
	 * properly process exception.
	 *
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleException() ClientLogicalHandler6 received
	 * SOAPFault from Inbound ServerLogicalHandler6 ClientLogicalHandler6.close()
	 * ClientLogicalHandler4.close() ClientLogicalHandler5.close()
	 *
	 * ServerLogicalHandler6.handleMessage().doInbound() ServerLogicalHandler6
	 * Throwing an inbound SOAPFaultException ServerLogicalHandler6.close()
	 *
	 */
	@Test
	public void ServerLogicalInboundHandlerThrowsSOAPFaultToClientHandlersTest() throws Exception {
		TestUtil.logTrace("ServerLogicalInboundHandlerThrowsSOAPFaultToClientHandlersTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);
					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}
			});

			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalInboundHandlerThrowsSOAPFaultToClientHandlersTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);

				try {
					logger.log(Level.INFO, "Expecting RuntimeException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected RuntimeException");
					pass = false;
				} catch (RuntimeException e) {
					logger.log(Level.INFO, "Did get expected RuntimeException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "ServerLogicalHandler6.handleMessage throws SOAPFaultException for ServerLogicalInboundHandlerThrowsSOAPFaultToClientHandlersTest";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected RuntimeException text");
					else {
						TestUtil.logErr("Did not get expected RuntimeException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyServerToClientHandlerExceptionCallBacks("Client", LOGICAL, Constants.OUTBOUND,
						clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
				GetTrackerDataAction gtda = new GetTrackerDataAction();

				logger.log(Level.INFO, "Get server side result back from endpoint");
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideMsgs = null;
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);
				if (!Handler_Util.VerifyServerToClientHandlerExceptionCallBacks("Server", LOGICAL, Constants.INBOUND,
						serverSideMsgs)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ServerLogicalInboundHandlerThrowsSOAPFaultToClientHandlersTest failed");
	}

	/*
	 * @testName: ServerLogicalOutboundHandlerThrowsSOAPFaultToClientHandlersTest
	 *
	 * @assertion_ids: JAXWS:SPEC:9016; JAXWS:SPEC:9016.2;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side handler
	 * callbacks are called by the JAXWS RUNTIME. Server handler throws a
	 * SOAPFaultException while processing an inbound message and Client should
	 * properly process exception.
	 *
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleException() ClientLogicalHandler6 received
	 * SOAPFault from Outbound ServerLogicalHandler6 ClientLogicalHandler6.close()
	 * ClientLogicalHandler4.close() ClientLogicalHandler5.close()
	 *
	 * ServerLogicalHandler6.handleMessage().doInbound()
	 * ServerLogicalHandler4.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleMessage().doInbound()
	 * ServerLogicalHandler5.handleMessage().doOutbound()
	 * ServerLogicalHandler4.handleMessage().doOutbound()
	 * ServerLogicalHandler6.handleMessage().doOutbound() ServerLogicalHandler6
	 * Throwing an inbound SOAPFaultException ServerLogicalHandler6.close()
	 *
	 */
	@Test
	public void ServerLogicalOutboundHandlerThrowsSOAPFaultToClientHandlersTest() throws Exception {
		TestUtil.logTrace("ServerLogicalOutboundHandlerThrowsSOAPFaultToClientHandlersTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);
					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}
			});

			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ServerLogicalOutboundHandlerThrowsSOAPFaultToClientHandlersTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);

				try {
					logger.log(Level.INFO, "Expecting RuntimeException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected RuntimeException");
					pass = false;
				} catch (RuntimeException e) {
					logger.log(Level.INFO, "Did get expected RuntimeException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "ServerLogicalHandler6.handleMessage throws SOAPFaultException for ServerLogicalOutboundHandlerThrowsSOAPFaultToClientHandlersTest";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected RuntimeException text");
					else {
						TestUtil.logErr("Did not get expected RuntimeException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyServerToClientHandlerExceptionCallBacks("Client", LOGICAL, Constants.INBOUND,
						clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
				GetTrackerDataAction gtda = new GetTrackerDataAction();

				logger.log(Level.INFO, "Get server side result back from endpoint");
				gtda.setAction("getArrayMessages1");
				gtda.setHarnessloghost(harnessHost);
				gtda.setHarnesslogport(harnessLogPort);
				gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
				List<String> serverSideMsgs = null;
				try {
					serverSideMsgs = port4.getTrackerData(gtda).getResult();
				} catch (Exception e) {
					TestUtil.logErr("Endpoint threw an exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				serverSideMsgs = JAXWS_Util.getMessagesStartingFrom(serverSideMsgs, Constants.INBOUND);
				if (!Handler_Util.VerifyServerToClientHandlerExceptionCallBacks("Server", LOGICAL, Constants.OUTBOUND,
						serverSideMsgs)) {
					TestUtil.logErr("Server-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Server-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Purging server-side tracker data");
				purgeServerSideTrackerData();
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ServerLogicalOutboundHandlerThrowsSOAPFaultToClientHandlersTest failed");
	}

	/*
	 * @testName: ClientLogicalInboundHandleMessageThrowsWebServiceExceptionTest
	 *
	 * @assertion_ids: JAXWS:SPEC:4021;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ClientLogicalHandler4 throws a WebServiceException in handleMessage method
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doInbound()
	 * ClientLogicalHandler4.handleMessage().doInbound() ClientLogicalHandler4
	 * Throwing an inbound WebServiceException ClientLogicalHandler6.close()
	 * ClientLogicalHandler4.close() ClientLogicalHandler5.close()
	 */
	@Test
	public void ClientLogicalInboundHandleMessageThrowsWebServiceExceptionTest() throws Exception {
		TestUtil.logTrace("ClientLogicalInboundHandleMessageThrowsWebServiceExceptionTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);

					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});
			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalInboundHandleMessageThrowsWebServiceExceptionTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting WebServiceException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected WebServiceException");
					pass = false;
				} catch (WebServiceException e) {
					logger.log(Level.INFO, "Did get expected WebServiceException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "ClientLogicalHandler4.handleMessage throwing an inbound WebServiceException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected WebServiceException text");
					else {
						TestUtil.logErr("Did not get expected WebServiceException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandlerExceptionCallBacks("Client", LOGICAL, false, Constants.INBOUND,
						clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalInboundHandleMessageThrowsWebServiceExceptionTest failed");
	}

	/*
	 * @testName: ClientLogicalOutboundHandleMessageThrowsWebServiceExceptionTest
	 *
	 * @assertion_ids: JAXWS:SPEC:4021;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ClientLogicalHandler4 throws a WebServiceException in handleMessage method
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound() ClientLogicalHandler4
	 * Throwing an outbound WebServiceException ClientLogicalHandler4.close()
	 * ClientLogicalHandler5.close()
	 */
	@Test
	public void ClientLogicalOutboundHandleMessageThrowsWebServiceExceptionTest() throws Exception {
		TestUtil.logTrace("ClientLogicalOutboundHandleMessageThrowsWebServiceExceptionTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);

					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});
			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalOutboundHandleMessageThrowsWebServiceExceptionTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting WebServiceException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected WebServiceException");
					pass = false;
				} catch (WebServiceException e) {
					logger.log(Level.INFO, "Did get expected WebServiceException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "ClientLogicalHandler4.handleMessage throwing an outbound WebServiceException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected WebServiceException text");
					else {
						TestUtil.logErr("Did not get expected WebServiceException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandlerExceptionCallBacks("Client", LOGICAL, false, Constants.OUTBOUND,
						clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalOutboundHandleMessageThrowsWebServiceExceptionTest failed");
	}

	/*
	 * @testName: ClientLogicalInboundHandleMessageThrowsProtocolExceptionTest
	 *
	 * @assertion_ids: JAXWS:SPEC:4021;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ClientLogicalHandler4 throws a ProtocolException in handleMessage method
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doOutbound()
	 * ClientLogicalHandler6.handleMessage().doInbound()
	 * ClientLogicalHandler4.handleMessage().doInbound() ClientLogicalHandler4
	 * Throwing an inbound ProtocolException ClientLogicalHandler6.close()
	 * ClientLogicalHandler4.close() ClientLogicalHandler5.close()
	 */
	@Test
	public void ClientLogicalInboundHandleMessageThrowsProtocolExceptionTest() throws Exception {
		TestUtil.logTrace("ClientLogicalInboundHandleMessageThrowsProtocolExceptionTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);

					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});
			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalInboundHandleMessageThrowsProtocolExceptionTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting ProtocolException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected ProtocolException");
					pass = false;
				} catch (ProtocolException e) {
					logger.log(Level.INFO, "Did get expected ProtocolException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "ClientLogicalHandler4.handleMessage throwing an inbound ProtocolException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected ProtocolException text");
					else {
						TestUtil.logErr("Did not get expected ProtocolException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandlerExceptionCallBacks("Client", LOGICAL, false, Constants.INBOUND,
						clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalInboundHandleMessageThrowsProtocolExceptionTest failed");
	}

	/*
	 * @testName: ClientLogicalOutboundHandleMessageThrowsProtocolExceptionTest
	 *
	 * @assertion_ids: JAXWS:SPEC:4021;
	 *
	 * @test_Strategy: Invoke an RPC method. Verify that the client-side
	 * handleMessage callbacks are called by the JAXWS RUNTIME.
	 * ClientLogicalHandler4 throws a ProtocolException in handleMessage method
	 * ------------------------------------------------------- This is the expected
	 * order -------------------------------------------------------
	 * ClientLogicalHandler5.handleMessage().doOutbound()
	 * ClientLogicalHandler4.handleMessage().doOutbound() ClientLogicalHandler4
	 * Throwing an outbound ProtocolException ClientLogicalHandler4.close()
	 * ClientLogicalHandler5.close()
	 */
	@Test
	public void ClientLogicalOutboundHandleMessageThrowsProtocolExceptionTest() throws Exception {
		TestUtil.logTrace("ClientLogicalOutboundHandleMessageThrowsProtocolExceptionTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO,
					"Programatically registering the client side handlers by creating new HandlerResolver.");
			service.setHandlerResolver(new HandlerResolver() {
				public List<Handler> getHandlerChain(PortInfo info) {
					List<Handler> handlerList = new ArrayList<Handler>();
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO,
							"Programmatically registering the following service based handlers through the binding: \n"
									+ "ClientSOAPHandler5, ClientLogicalHandler5");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler5();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler5 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler5();
					handlerList.add(handler);

					if (info.getPortName().equals(PORT_QNAME2)) {
						logger.log(Level.INFO, "----------------------------------------------");
						TestUtil.logMsg("Create port based handlers for port: " + PORT_QNAME2);
						logger.log(Level.INFO, "----------------------------------------------");
						logger.log(Level.INFO,
								"Construct HandleInfo for ClientLogicalHandler4 and add to HandlerChain");
						Handler h4 = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler4();
						handlerList.add(h4);
					}
					logger.log(Level.INFO, "HandlerChainList=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					return handlerList;
				}

			});
			if (!setupPorts()) {
				pass = false;
			} else {
				try {
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Getting existing Handlers for Port2");
					logger.log(Level.INFO, "----------------------------------------------");
					List<Handler> handlerList = binding2.getHandlerChain();

					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Programmatically registering the following handlers through the binding: \n"
							+ "ClientSOAPHandler6, ClientLogicalHandler6");
					logger.log(Level.INFO, "----------------------------------------------");
					logger.log(Level.INFO, "Construct HandleInfo for ClientSOAPHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientSOAPHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "Construct HandleInfo for ClientLogicalHandler6 and add to HandlerChain");
					handler = new com.sun.ts.tests.jaxws.sharedclients.dlhandlerclient.ClientLogicalHandler6();
					handlerList.add(handler);
					logger.log(Level.INFO, "HandlerChain=" + handlerList);
					logger.log(Level.INFO, "HandlerChain size = " + handlerList.size());
					binding2.setHandlerChain(handlerList);
				} catch (Exception e) {
					TestUtil.logErr("ERROR: Adding handlers to the binding failed with the following exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}
			}

			if (pass) {

				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();

				MyActionType ma = new MyActionType();
				ma.setAction("ClientLogicalOutboundHandleMessageThrowsProtocolExceptionTest");
				ma.setTestType(TEST_TYPE);
				ma.setHarnessloghost(harnessHost);
				ma.setHarnesslogport(harnessLogPort);
				ma.setHarnesslogtraceflag(harnessLogTraceFlag);
				try {
					logger.log(Level.INFO, "Expecting ProtocolException");
					port2.doHandlerTest2(ma);
					TestUtil.logErr("Did not get expected ProtocolException");
					pass = false;
				} catch (ProtocolException e) {
					logger.log(Level.INFO, "Did get expected ProtocolException");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos, true);
					e.printStackTrace(ps);
					String tmp = "ClientLogicalHandler4.handleMessage throwing an outbound ProtocolException";
					if (baos.toString().indexOf(tmp) > -1)
						logger.log(Level.INFO, "Did get expected ProtocolException text");
					else {
						TestUtil.logErr("Did not get expected ProtocolException text");
						TestUtil.logErr("expected:" + tmp);
						TestUtil.printStackTrace(e);
						pass = false;
					}
				} catch (Exception e) {
					logger.log(Level.INFO, "Got unexpected exception:");
					TestUtil.printStackTrace(e);
					pass = false;
				}

				List<String> clientSideMsgs = HandlerTracker.getListMessages1();

				logger.log(Level.INFO, "Verifying Client-Side JAXWS-RUNTIME Callbacks");
				if (!Handler_Util.VerifyHandlerExceptionCallBacks("Client", LOGICAL, false, Constants.OUTBOUND,
						clientSideMsgs)) {
					TestUtil.logErr("Client-Side Callbacks are (incorrect)");
					pass = false;
				} else {
					logger.log(Level.INFO, "Client-Side Callbacks are (correct)");
				}
				logger.log(Level.INFO, "Get client side throwables back from Tracker");
				String[] clientSideThrowables = HandlerTracker.getArrayThrowables();
				int len = clientSideThrowables.length;
				if (len > 0) {
					TestUtil.logErr("There were exceptions thrown in the Client Handlers");
					for (int i = 0; i <= len - 1; i++) {
						TestUtil.logErr(clientSideThrowables[i]);
						pass = false;
					}
				} else {
					logger.log(Level.INFO, "There were no Client Handler exceptions");
				}

				Handler_Util.clearHandlers(listOfBindings);
				logger.log(Level.INFO, "Purging client-side tracker data");
				HandlerTracker.purge();
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("ClientLogicalOutboundHandleMessageThrowsProtocolExceptionTest failed");
	}

	private void purgeServerSideTrackerData() {
		try {
			GetTrackerDataAction gtda = new GetTrackerDataAction();
			gtda.setAction("purge");
			gtda.setHarnessloghost(harnessHost);
			gtda.setHarnesslogport(harnessLogPort);
			gtda.setHarnesslogtraceflag(harnessLogTraceFlag);
			port4.getTrackerData(gtda);
		} catch (Exception e) {
			TestUtil.logErr("Call to purge server-side tracker data failed:" + e);
		}
	}

}
