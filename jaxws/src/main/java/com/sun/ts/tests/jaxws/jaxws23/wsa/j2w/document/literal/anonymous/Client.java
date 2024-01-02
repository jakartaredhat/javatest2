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

package com.sun.ts.tests.jaxws.jaxws23.wsa.j2w.document.literal.anonymous;

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

import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.AddressingFeature;

/*
 * Test Repeatable annotation on WebServiceRef
 * @Repeatable(value=WebServiceRefs.class)
 */
public class Client extends BaseClient {

	private static final long serialVersionUID = 23L;

	private boolean endpointPublishSupport;

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.jaxws23.wsa.j2w.document.literal.anonymous.";

	private static final String MINPORT = "port.range.min";

	private int minPort = -1;

	private static final String MAXPORT = "port.range.max";

	private int maxPort = -1;

	private int javaseServerPort;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	// URL properties used by the test
	private static final String ENDPOINT_URL23001 = "wsaj2wdlanonymoustest23.endpoint.23001";

	private static final String WSDLLOC_URL23001 = "wsaj2wdlanonymoustest23.wsdlloc.23001";

	private static final String ENDPOINT_URL23002 = "wsaj2wdlanonymoustest23.endpoint.23002";

	private static final String WSDLLOC_URL23002 = "wsaj2wdlanonymoustest23.wsdlloc.23002";

	// NonAnonymousProcessor's
	private static final String NONANONYMOUSPROCESSOR = "/NonAnonymousProcessor";

	private static final String NONANONYMOUSPROCESSOR2 = "/NonAnonymousProcessor2";

	// service and port information
	private static final String NAMESPACEURI = "http://example.com/";

	private static final String TARGET_NAMESPACE = NAMESPACEURI;

	private static final String SERVICE_NAME23001 = "AddNumbersService23001";

	private static final String SERVICE_NAME23002 = "AddNumbersService23002";

	private static final String PORT_NAME23001 = "AddNumbersPort23001";

	private static final String PORT_NAME23002 = "AddNumbersPort23002";

	private static QName SERVICE_QNAME23001 = new QName(NAMESPACEURI, SERVICE_NAME23001);

	private static QName SERVICE_QNAME23002 = new QName(NAMESPACEURI, SERVICE_NAME23002);

	private static QName PORT_QNAME23001 = new QName(NAMESPACEURI, PORT_NAME23001);

	private static QName PORT_QNAME23002 = new QName(NAMESPACEURI, PORT_NAME23002);

	private static AddressingFeature ENABLED_ADDRESSING_FEATURE = new AddressingFeature(true, true);

	private static AddressingFeature DISABLED_ADDRESSING_FEATURE = new AddressingFeature(false);

	private String file23001 = null;

	private String file23002 = null;

	private String urlToNonAnonymousProcessor = null;

	private String urlToNonAnonymousProcessor2 = null;

	private String url23001 = null;

	private String url23002 = null;

	private URL wsdlurl23001 = null;

	private URL wsdlurl23002 = null;

	AddNumbersPortType23001 port23001 = null;

	AddNumbersPortType23002 port23002 = null;

	static AddNumbersService23001 service23001 = null;

	static AddNumbersService23002 service23002 = null;

	String ReplyToHeaderForAnonymousResponsesSoapMsg = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To><MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:{1}</MessageID><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{2}</Address></ReplyTo><Action xmlns=\"http://www.w3.org/2005/08/addressing\">http://example.com/AddNumbersPortType23001/add</Action></S:Header><S:Body><addNumbers xmlns=\"http://example.com/\"><number1>10</number1><number2>10</number2><testName>testAnonymousResponsesReplyToHeader</testName></addNumbers></S:Body></S:Envelope>";

	String ExceptionToHeaderForAnonymousResponsesSoapMsg = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To><MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:{1}</MessageID><FaultTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{2}</Address></FaultTo><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{3}</Address></ReplyTo><Action xmlns=\"http://www.w3.org/2005/08/addressing\">http://example.com/AddNumbersPortType23001/add</Action></S:Header><S:Body><addNumbers xmlns=\"http://example.com/\"><number1>-10</number1><number2>-10</number2><testName>testAnonymousResponsesFaultToHeader</testName></addNumbers></S:Body></S:Envelope>";

	String ReplyToHeaderForNonAnonymousResponsesSoapMsg = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To><MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:{1}</MessageID><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{2}</Address></ReplyTo><Action xmlns=\"http://www.w3.org/2005/08/addressing\">http://example.com/AddNumbersPortType23002/add</Action></S:Header><S:Body><addNumbers xmlns=\"http://example.com/\"><number1>10</number1><number2>10</number2><testName>testNonAnonymousResponsesReplyToHeader</testName></addNumbers></S:Body></S:Envelope>";

	String ExceptionToHeaderForNonAnonymousResponsesSoapMsg = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To><MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:{1}</MessageID><FaultTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{2}</Address></FaultTo><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{3}</Address></ReplyTo><Action xmlns=\"http://www.w3.org/2005/08/addressing\">http://example.com/AddNumbersPortType23002/add</Action></S:Header><S:Body><addNumbers xmlns=\"http://example.com/\"><number1>-10</number1><number2>-10</number2><testName>testNonAnonymousResponsesFaultToHeader</testName></addNumbers></S:Body></S:Envelope>";

	String TestNonAnonymousResponsesAssertionSoapMsg = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To><MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:{1}</MessageID><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{2}</Address></ReplyTo><Action xmlns=\"http://www.w3.org/2005/08/addressing\">http://example.com/AddNumbersPortType23002/add</Action></S:Header><S:Body><addNumbers xmlns=\"http://example.com/\"><number1>10</number1><number2>10</number2><testName>testNonAnonymousResponsesAssertion</testName></addNumbers></S:Body></S:Envelope>";

	private Dispatch<SOAPMessage> createDispatchSOAPMessage(Service service, QName port) throws Exception {
		return service.createDispatch(port, SOAPMessage.class, jakarta.xml.ws.Service.Mode.MESSAGE,
				DISABLED_ADDRESSING_FEATURE);
	}

	private Dispatch<SOAPMessage> createAnonymousResponsesDispatch() {
		return service23001.createDispatch(PORT_QNAME23001, SOAPMessage.class, Service.Mode.MESSAGE,
				ENABLED_ADDRESSING_FEATURE);
	}

	private Dispatch<SOAPMessage> createNonAnonymousResponsesDispatch() {
		return service23002.createDispatch(PORT_QNAME23002, SOAPMessage.class, Service.Mode.MESSAGE,
				ENABLED_ADDRESSING_FEATURE);
	}

	private Dispatch<SOAPMessage> createAnonymousResponsesDispatchWithoutAddressing() {
		return service23001.createDispatch(PORT_QNAME23001, SOAPMessage.class, Service.Mode.MESSAGE,
				DISABLED_ADDRESSING_FEATURE);
	}

	private Dispatch<SOAPMessage> createNonAnonymousResponsesDispatchWithoutAddressing() {
		return service23002.createDispatch(PORT_QNAME23002, SOAPMessage.class, Service.Mode.MESSAGE,
				DISABLED_ADDRESSING_FEATURE);
	}

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		file23001 = JAXWS_Util.getURLFromProp(ENDPOINT_URL23001);
		url23001 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file23001);
		file23002 = JAXWS_Util.getURLFromProp(ENDPOINT_URL23002);
		url23002 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file23002);
		if (endpointPublishSupport) {
			urlToNonAnonymousProcessor = ctsurl.getURLString(PROTOCOL, hostname, javaseServerPort,
					NONANONYMOUSPROCESSOR);
			urlToNonAnonymousProcessor2 = ctsurl.getURLString(PROTOCOL, hostname, javaseServerPort,
					NONANONYMOUSPROCESSOR2);
		} else {
			urlToNonAnonymousProcessor = ctsurl.getURLString(PROTOCOL, hostname, portnum, NONANONYMOUSPROCESSOR);
			urlToNonAnonymousProcessor2 = ctsurl.getURLString(PROTOCOL, hostname, portnum, NONANONYMOUSPROCESSOR2);
		}

		file23001 = JAXWS_Util.getURLFromProp(WSDLLOC_URL23001);
		wsdlurl23001 = ctsurl.getURL(PROTOCOL, hostname, portnum, file23001);
		file23002 = JAXWS_Util.getURLFromProp(WSDLLOC_URL23002);
		wsdlurl23002 = ctsurl.getURL(PROTOCOL, hostname, portnum, file23002);
		logger.log(Level.INFO, "NonAnonymousProcessor Endpoint: " + urlToNonAnonymousProcessor);
		logger.log(Level.INFO, "NonAnonymousProcessor2 Endpoint: " + urlToNonAnonymousProcessor2);
		logger.log(Level.INFO, "Service Endpoint URL23001: " + url23001);
		logger.log(Level.INFO, "Service Endpoint URL23002: " + url23002);
		logger.log(Level.INFO, "WSDL Location URL23001:    " + wsdlurl23001);
		logger.log(Level.INFO, "WSDL Location URL23002:    " + wsdlurl23002);
	}

	private void getPortsStandalone() throws Exception {
		port23001 = (AddNumbersPortType23001) JAXWS_Util.getPort(service23001, PORT_QNAME23001,
				AddNumbersPortType23001.class);
		port23002 = (AddNumbersPortType23002) JAXWS_Util.getPort(service23002, PORT_QNAME23002,
				AddNumbersPortType23002.class);
		logger.log(Level.INFO, "port3=" + port23001);
		logger.log(Level.INFO, "port4=" + port23002);
		JAXWS_Util.setTargetEndpointAddress(port23001, url23001);
		JAXWS_Util.setTargetEndpointAddress(port23002, url23002);
		JAXWS_Util.setSOAPLogging(port23001);
		JAXWS_Util.setSOAPLogging(port23002);
	}

	private void getPortsJavaEE() throws Exception {
		javax.naming.InitialContext ic = new javax.naming.InitialContext();

		logger.log(Level.INFO, "Obtain service23001 via JNDI lookup");
		service23001 = (AddNumbersService23001) ic.lookup("java:comp/env/service/WSAJ2WDLAnonymousTest23001");
		logger.log(Level.INFO, "service23001=" + service23001);
		port23001 = (AddNumbersPortType23001) service23001.getPort(AddNumbersPortType23001.class);

		logger.log(Level.INFO, "Obtain service23002 via JNDI lookup");
		service23002 = (AddNumbersService23002) ic.lookup("java:comp/env/service/WSAJ2WDLAnonymousTest23002");
		logger.log(Level.INFO, "service23002=" + service23002);
		port23002 = (AddNumbersPortType23002) service23002.getPort(AddNumbersPortType23002.class);

		JAXWS_Util.dumpTargetEndpointAddress(port23001);
		JAXWS_Util.dumpTargetEndpointAddress(port23002);
	}

	/* Test setup */

	/*
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * 
	 * @class.setup_props: webServerHost; webServerPort; platform.mode;
	 * port.range.min; port.range.max; http.server.supports.endpoint.publish;
	 */
	@Override
	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		try {
			endpointPublishSupport = Boolean.parseBoolean(System.getProperty(ENDPOINTPUBLISHPROP));
			modeProperty = System.getProperty(MODEPROP);

			if (endpointPublishSupport) {
				try {
					maxPort = Integer.parseInt(System.getProperty(MAXPORT));
				} catch (Exception e) {
					maxPort = -1;
				}
				try {
					minPort = Integer.parseInt(System.getProperty(MINPORT));
				} catch (Exception e) {
					minPort = -1;
				}

				logger.log(Level.INFO, "minPort=" + minPort);
				logger.log(Level.INFO, "maxPort=" + maxPort);

				javaseServerPort = JAXWS_Util.getFreePort();
				if (javaseServerPort <= 0) {
					logger.log(Level.INFO, "Free port not found, use standard webserver port.");
					javaseServerPort = portnum;
				}
				getTestURLs();
				service23001 = (AddNumbersService23001) JAXWS_Util.getService(wsdlurl23001, SERVICE_QNAME23001,
						AddNumbersService23001.class);
				service23002 = (AddNumbersService23002) JAXWS_Util.getService(wsdlurl23002, SERVICE_QNAME23002,
						AddNumbersService23002.class);
				getPortsStandalone();
			} else {
				logger.log(Level.INFO, "WebServiceRef is not set in Client (get it from specific vehicle)");
				getTestURLs();
				getPortsJavaEE();
			}
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
	 * @testName: testAnonymousResponsesAssertion
	 *
	 * @assertion_ids: JAXWS:JAVADOC:83; JAXWS:JAVADOC:84; JAXWS:JAVADOC:86;
	 * WSAMD:SPEC:3003; WSAMD:SPEC:3003.1; WSAMD:SPEC:3003.2;
	 *
	 * @test_Strategy: Invocation on port marked with AnonymousResponses assertion
	 * Verify that wsa:ReplyTo in the SOAPRequest is the anonymous URI. Verify that
	 * wsa:To in the SOAPResponse is the anonymous URI.
	 * 
	 * Test multiple @WebServiceRef annotations can be used due
	 * to @Repeatable(value=WebServiceRefs.class)
	 */
	@Test
	public void testAnonymousResponsesAssertion() throws Exception {
		logger.log(Level.INFO, "testAnonymousResponsesAssertion");
		boolean pass = true;

		try {
			port23001.addNumbers(10, 10, "testAnonymousResponsesAssertion");
		} catch (Exception e) {
			TestUtil.logErr("Caught unexpected exception: ", e);
			pass = false;
		}

		if (!pass)
			throw new Exception("testAnonymousResponsesAssertion failed");
	}

	/*
	 * @testName: testNonAnonymousResponsesAssertion
	 *
	 * @assertion_ids: JAXWS:JAVADOC:83; JAXWS:JAVADOC:84; JAXWS:JAVADOC:86;
	 * WSAMD:SPEC:3003; WSAMD:SPEC:3003.1; WSAMD:SPEC:3003.3;
	 *
	 * @test_Strategy: Invocation on port marked with NonAnonymousResponses
	 * assertion. The <ReplyTo> header may or may not be set by default depending on
	 * the implementation. The test has to account for this.
	 * 
	 * Test multiple @WebServiceRef annotations can be used due
	 * to @Repeatable(value=WebServiceRefs.class)
	 */
	@Test
	public void testNonAnonymousResponsesAssertion() throws Exception {
		logger.log(Level.INFO, "testNonAnonymousResponsesAssertion");
		boolean pass = true;

		try {
			logger.log(Level.INFO, "Expect a WebServiceException on port invocation");
			port23002.addNumbers(10, 10, "testNonAnonymousResponsesAssertion");
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught WebServiceException ignore: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}

		if (!pass)
			throw new Exception("testNonAnonymousResponsesAssertion failed");
	}
}
