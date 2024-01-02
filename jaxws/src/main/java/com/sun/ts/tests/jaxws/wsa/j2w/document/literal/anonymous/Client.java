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
 * $Id: Client.java 52501 2007-01-24 02:29:49Z af70133 $
 */

package com.sun.ts.tests.jaxws.wsa.j2w.document.literal.anonymous;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.wsa.common.W3CAddressingConstants;
import com.sun.ts.tests.jaxws.wsa.common.WsaSOAPUtils;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Endpoint;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.AddressingFeature;
import jakarta.xml.ws.soap.SOAPFaultException;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	private boolean endpointPublishSupport;

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.wsa.j2w.document.literal.anonymous.";

	// URL properties used by the test
	private static final String ENDPOINT_URL3 = "wsaj2wdlanonymoustest.endpoint.3";

	private static final String WSDLLOC_URL3 = "wsaj2wdlanonymoustest.wsdlloc.3";

	private static final String ENDPOINT_URL4 = "wsaj2wdlanonymoustest.endpoint.4";

	private static final String WSDLLOC_URL4 = "wsaj2wdlanonymoustest.wsdlloc.4";

	// NonAnonymousProcessor's
	private static final String NONANONYMOUSPROCESSOR = "/NonAnonymousProcessor";

	private static final String NONANONYMOUSPROCESSOR2 = "/NonAnonymousProcessor2";

	// service and port information
	private static final String NAMESPACEURI = "http://example.com/";

	private static final String TARGET_NAMESPACE = NAMESPACEURI;

	private static final String SERVICE_NAME3 = "AddNumbersService3";

	private static final String SERVICE_NAME4 = "AddNumbersService4";

	private static final String PORT_NAME3 = "AddNumbersPort3";

	private static final String PORT_NAME4 = "AddNumbersPort4";

	private static QName SERVICE_QNAME3 = new QName(NAMESPACEURI, SERVICE_NAME3);

	private static QName SERVICE_QNAME4 = new QName(NAMESPACEURI, SERVICE_NAME4);

	private static QName PORT_QNAME3 = new QName(NAMESPACEURI, PORT_NAME3);

	private static QName PORT_QNAME4 = new QName(NAMESPACEURI, PORT_NAME4);

	private static AddressingFeature ENABLED_ADDRESSING_FEATURE = new AddressingFeature(true, true);

	private static AddressingFeature DISABLED_ADDRESSING_FEATURE = new AddressingFeature(false);

	private String file3 = null;

	private String file4 = null;

	private String urlToNonAnonymousProcessor = null;

	private String urlToNonAnonymousProcessor2 = null;

	private String url3 = null;

	private String url4 = null;

	private URL wsdlurl3 = null;

	private URL wsdlurl4 = null;

	AddNumbersPortType3 port3 = null;

	AddNumbersPortType4 port4 = null;

	static AddNumbersService3 service3 = null;

	static AddNumbersService4 service4 = null;

	String ReplyToHeaderForAnonymousResponsesSoapMsg = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To><MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:{1}</MessageID><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{2}</Address></ReplyTo><Action xmlns=\"http://www.w3.org/2005/08/addressing\">http://example.com/AddNumbersPortType3/add</Action></S:Header><S:Body><addNumbers xmlns=\"http://example.com/\"><number1>10</number1><number2>10</number2><testName>testAnonymousResponsesReplyToHeader</testName></addNumbers></S:Body></S:Envelope>";

	String ExceptionToHeaderForAnonymousResponsesSoapMsg = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To><MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:{1}</MessageID><FaultTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{2}</Address></FaultTo><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{3}</Address></ReplyTo><Action xmlns=\"http://www.w3.org/2005/08/addressing\">http://example.com/AddNumbersPortType3/add</Action></S:Header><S:Body><addNumbers xmlns=\"http://example.com/\"><number1>-10</number1><number2>-10</number2><testName>testAnonymousResponsesFaultToHeader</testName></addNumbers></S:Body></S:Envelope>";

	String ReplyToHeaderForNonAnonymousResponsesSoapMsg = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To><MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:{1}</MessageID><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{2}</Address></ReplyTo><Action xmlns=\"http://www.w3.org/2005/08/addressing\">http://example.com/AddNumbersPortType4/add</Action></S:Header><S:Body><addNumbers xmlns=\"http://example.com/\"><number1>10</number1><number2>10</number2><testName>testNonAnonymousResponsesReplyToHeader</testName></addNumbers></S:Body></S:Envelope>";

	String ExceptionToHeaderForNonAnonymousResponsesSoapMsg = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To><MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:{1}</MessageID><FaultTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{2}</Address></FaultTo><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{3}</Address></ReplyTo><Action xmlns=\"http://www.w3.org/2005/08/addressing\">http://example.com/AddNumbersPortType4/add</Action></S:Header><S:Body><addNumbers xmlns=\"http://example.com/\"><number1>-10</number1><number2>-10</number2><testName>testNonAnonymousResponsesFaultToHeader</testName></addNumbers></S:Body></S:Envelope>";

	String TestNonAnonymousResponsesAssertionSoapMsg = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Header><To xmlns=\"http://www.w3.org/2005/08/addressing\">{0}</To><MessageID xmlns=\"http://www.w3.org/2005/08/addressing\">uuid:{1}</MessageID><ReplyTo xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>{2}</Address></ReplyTo><Action xmlns=\"http://www.w3.org/2005/08/addressing\">http://example.com/AddNumbersPortType4/add</Action></S:Header><S:Body><addNumbers xmlns=\"http://example.com/\"><number1>10</number1><number2>10</number2><testName>testNonAnonymousResponsesAssertion</testName></addNumbers></S:Body></S:Envelope>";

	private Dispatch<SOAPMessage> createDispatchSOAPMessage(Service service, QName port) throws Exception {
		return service.createDispatch(port, SOAPMessage.class, jakarta.xml.ws.Service.Mode.MESSAGE,
				DISABLED_ADDRESSING_FEATURE);
	}

	private Dispatch<SOAPMessage> createAnonymousResponsesDispatch() {
		return service3.createDispatch(PORT_QNAME3, SOAPMessage.class, Service.Mode.MESSAGE,
				ENABLED_ADDRESSING_FEATURE);
	}

	private Dispatch<SOAPMessage> createNonAnonymousResponsesDispatch() {
		return service4.createDispatch(PORT_QNAME4, SOAPMessage.class, Service.Mode.MESSAGE,
				ENABLED_ADDRESSING_FEATURE);
	}

	private Dispatch<SOAPMessage> createAnonymousResponsesDispatchWithoutAddressing() {
		return service3.createDispatch(PORT_QNAME3, SOAPMessage.class, Service.Mode.MESSAGE,
				DISABLED_ADDRESSING_FEATURE);
	}

	private Dispatch<SOAPMessage> createNonAnonymousResponsesDispatchWithoutAddressing() {
		return service4.createDispatch(PORT_QNAME4, SOAPMessage.class, Service.Mode.MESSAGE,
				DISABLED_ADDRESSING_FEATURE);
	}

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		file3 = JAXWS_Util.getURLFromProp(ENDPOINT_URL3);
		url3 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file3);
		file4 = JAXWS_Util.getURLFromProp(ENDPOINT_URL4);
		url4 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file4);
		if (endpointPublishSupport) {
			urlToNonAnonymousProcessor = ctsurl.getURLString(PROTOCOL, hostname, javaseServerPort,
					NONANONYMOUSPROCESSOR);
			urlToNonAnonymousProcessor2 = ctsurl.getURLString(PROTOCOL, hostname, javaseServerPort,
					NONANONYMOUSPROCESSOR2);
		} else {
			urlToNonAnonymousProcessor = ctsurl.getURLString(PROTOCOL, hostname, portnum, NONANONYMOUSPROCESSOR);
			urlToNonAnonymousProcessor2 = ctsurl.getURLString(PROTOCOL, hostname, portnum, NONANONYMOUSPROCESSOR2);
		}

		file3 = JAXWS_Util.getURLFromProp(WSDLLOC_URL3);
		wsdlurl3 = ctsurl.getURL(PROTOCOL, hostname, portnum, file3);
		file4 = JAXWS_Util.getURLFromProp(WSDLLOC_URL4);
		wsdlurl4 = ctsurl.getURL(PROTOCOL, hostname, portnum, file4);
		logger.log(Level.INFO, "NonAnonymousProcessor Endpoint: " + urlToNonAnonymousProcessor);
		logger.log(Level.INFO, "NonAnonymousProcessor2 Endpoint: " + urlToNonAnonymousProcessor2);
		logger.log(Level.INFO, "Service Endpoint URL3: " + url3);
		logger.log(Level.INFO, "Service Endpoint URL4: " + url4);
		logger.log(Level.INFO, "WSDL Location URL3:    " + wsdlurl3);
		logger.log(Level.INFO, "WSDL Location URL4:    " + wsdlurl4);
	}

	protected void getPortsStandalone() throws Exception {
		port3 = (AddNumbersPortType3) JAXWS_Util.getPort(service3, PORT_QNAME3, AddNumbersPortType3.class);
		port4 = (AddNumbersPortType4) JAXWS_Util.getPort(service4, PORT_QNAME4, AddNumbersPortType4.class);
		logger.log(Level.INFO, "port3=" + port3);
		logger.log(Level.INFO, "port4=" + port4);
		JAXWS_Util.setTargetEndpointAddress(port3, url3);
		JAXWS_Util.setTargetEndpointAddress(port4, url4);
		JAXWS_Util.setSOAPLogging(port3);
		JAXWS_Util.setSOAPLogging(port4);
	}

	protected void getPortsJavaEE() throws Exception {
		javax.naming.InitialContext ic = new javax.naming.InitialContext();

		logger.log(Level.INFO, "Obtain service3 via JNDI lookup");
		service3 = (AddNumbersService3) ic.lookup("java:comp/env/service/WSAJ2WDLAnonymousTest3");
		logger.log(Level.INFO, "service3=" + service3);
		port3 = (AddNumbersPortType3) service3.getPort(AddNumbersPortType3.class);

		logger.log(Level.INFO, "Obtain service4 via JNDI lookup");
		service4 = (AddNumbersService4) ic.lookup("java:comp/env/service/WSAJ2WDLAnonymousTest4");
		logger.log(Level.INFO, "service4=" + service4);
		port4 = (AddNumbersPortType4) service4.getPort(AddNumbersPortType4.class);

		JAXWS_Util.dumpTargetEndpointAddress(port3);
		JAXWS_Util.dumpTargetEndpointAddress(port4);
	}

	/* Test setup */

	/*
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * 
	 * @class.setup_props: webServerHost; webServerPort; platform.mode;
	 * port.range.min; port.range.max; http.server.supports.endpoint.publish;
	 */
	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		try {

			endpointPublishSupport = Boolean.parseBoolean(System.getProperty(ENDPOINTPUBLISHPROP));

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
				service3 = (AddNumbersService3) JAXWS_Util.getService(wsdlurl3, SERVICE_QNAME3,
						AddNumbersService3.class);
				service4 = (AddNumbersService4) JAXWS_Util.getService(wsdlurl4, SERVICE_QNAME4,
						AddNumbersService4.class);
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
	 * @assertion_ids: WSAMD:SPEC:3003; WSAMD:SPEC:3003.1; WSAMD:SPEC:3003.2;
	 *
	 * @test_Strategy: Invocation on port marked with AnonymousResponses assertion
	 * Verify that wsa:ReplyTo in the SOAPRequest is the anonymous URI. Verify that
	 * wsa:To in the SOAPResponse is the anonymous URI.
	 */
	@Test
	public void testAnonymousResponsesAssertion() throws Exception {
		logger.log(Level.INFO, "testAnonymousResponsesAssertion");
		boolean pass = true;

		try {
			port3.addNumbers(10, 10, "testAnonymousResponsesAssertion");
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
	 * @assertion_ids: WSAMD:SPEC:3003; WSAMD:SPEC:3003.1; WSAMD:SPEC:3003.3;
	 *
	 * @test_Strategy: Invocation on port marked with NonAnonymousResponses
	 * assertion. The <ReplyTo> header may or may not be set by default depending on
	 * the implementation. The test has to account for this.
	 */
	@Test
	public void testNonAnonymousResponsesAssertion() throws Exception {
		logger.log(Level.INFO, "testNonAnonymousResponsesAssertion");
		boolean pass = true;

		try {
			logger.log(Level.INFO, "Expect a WebServiceException on port invocation");
			port4.addNumbers(10, 10, "testNonAnonymousResponsesAssertion");
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught WebServiceException ignore: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}

		if (!pass)
			throw new Exception("testNonAnonymousResponsesAssertion failed");
	}

	/*
	 * @testName: testNonAnonymousResponsesWithReplyToSetToValidProviderEndpoint
	 *
	 * @assertion_ids: WSAMD:SPEC:3003; WSAMD:SPEC:3003.1; WSAMD:SPEC:3003.2;
	 * WSASB:SPEC:6012; WSASB:SPEC:6012.7; WSASB:SPEC:6013;
	 *
	 * @test_Strategy: Test for NonAnonymousResponses assertion where <ReplyTo>
	 * header is set to a NonAnonymousProvider which will process the SOAP response.
	 * Verify that the NonAnonymousProvider recieved the SOAP response.
	 */
	@Test
	public void testNonAnonymousResponsesWithReplyToSetToValidProviderEndpoint() throws Exception {
		logger.log(Level.INFO, "testNonAnonymousResponsesWithReplyToSetToValidProviderEndpoint");
		boolean pass = true;

		SOAPMessage request = null, response = null;
		Dispatch<SOAPMessage> dispatchSM;
		Endpoint responseProcessor = null;
		Exchanger<SOAPMessage> respMsgExchanger = new Exchanger<SOAPMessage>();

		try {
			if (modeProperty.equals("standalone") && endpointPublishSupport) {
				responseProcessor = Endpoint.create(new NonAnonymousRespProcessor(respMsgExchanger));
				responseProcessor.publish(urlToNonAnonymousProcessor);
				String soapmsg = MessageFormat.format(ReplyToHeaderForNonAnonymousResponsesSoapMsg, url4,
						UUID.randomUUID(), urlToNonAnonymousProcessor);
				dispatchSM = createDispatchSOAPMessage(service4, PORT_QNAME4);
				request = JAXWS_Util.makeSOAPMessage(soapmsg);
				logger.log(Level.INFO, "Dumping SOAP Request ...");
				JAXWS_Util.dumpSOAPMessage(request, false);
				dispatchSM.invokeAsync(request);
				response = respMsgExchanger.exchange(null, 30L, TimeUnit.SECONDS);
				if (response != null) {
					System.out.println("****************************");
					response.writeTo(System.out);
					System.out.println("\n****************************");
				} else {
					pass = false;
				}
			}
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught WebServiceException ignore: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		} finally {
			if (responseProcessor != null)
				responseProcessor.stop();
		}
		if (!pass)
			throw new Exception("testNonAnonymousResponsesWithReplyToSetToValidProviderEndpoint failed");
	}

	/*
	 * @testName: testNonAnonymousResponsesWithFaultToSetToValidProviderEndpoint
	 *
	 * @assertion_ids: WSAMD:SPEC:3003; WSAMD:SPEC:3003.1; WSAMD:SPEC:3003.2;
	 * WSASB:SPEC:6012; WSASB:SPEC:6012.7; WSASB:SPEC:6013;
	 *
	 * @test_Strategy: Test for NonAnonymousResponses assertion where <FaultTo>
	 * header is set to a NonAnonymousProvider which will process the SOAP response.
	 * Verify that the NonAnonymousProvider received the SOAPFault message.
	 */
	@Test
	public void testNonAnonymousResponsesWithFaultToSetToValidProviderEndpoint() throws Exception {
		logger.log(Level.INFO, "testNonAnonymousResponsesWithFaultToSetToValidProviderEndpoint");
		boolean pass = true;

		SOAPMessage request = null, response = null;
		Dispatch<SOAPMessage> dispatchSM;
		Endpoint responseProcessor = null;
		Endpoint responseProcessor2 = null;
		Exchanger<SOAPMessage> respMsgExchanger = new Exchanger<SOAPMessage>();
		Exchanger<SOAPMessage> respMsgExchanger2 = new Exchanger<SOAPMessage>();

		try {
			if (modeProperty.equals("standalone") && endpointPublishSupport) {
				responseProcessor = Endpoint.create(new NonAnonymousRespProcessor(respMsgExchanger));
				responseProcessor.publish(urlToNonAnonymousProcessor);
				responseProcessor2 = Endpoint.create(new NonAnonymousRespProcessor2(respMsgExchanger2));
				responseProcessor2.publish(urlToNonAnonymousProcessor2);
				String soapmsg = MessageFormat.format(ExceptionToHeaderForNonAnonymousResponsesSoapMsg, url4,
						UUID.randomUUID(), urlToNonAnonymousProcessor2, urlToNonAnonymousProcessor);
				dispatchSM = createDispatchSOAPMessage(service4, PORT_QNAME4);
				request = JAXWS_Util.makeSOAPMessage(soapmsg);
				logger.log(Level.INFO, "Dumping SOAP Request ...");
				JAXWS_Util.dumpSOAPMessage(request, false);
				dispatchSM.invokeAsync(request);
				response = respMsgExchanger2.exchange(null, 30L, TimeUnit.SECONDS);
				if (response != null) {
					System.out.println("****************************");
					response.writeTo(System.out);
					System.out.println("\n****************************");
				} else {
					pass = false;
				}
			}
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught WebServiceException ignore: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		} finally {
			if (responseProcessor != null)
				responseProcessor.stop();
			if (responseProcessor2 != null)
				responseProcessor2.stop();
		}
		if (!pass)
			throw new Exception("testNonAnonymousResponsesWithFaultToSetToValidProviderEndpoint failed");
	}

	/*
	 * @testName: testAnonymousResponsesWithReplyToSetToNone
	 *
	 * @assertion_ids: WSAMD:SPEC:3003; WSAMD:SPEC:3003.1; WSAMD:SPEC:3003.2;
	 * WSASB:SPEC:6012; WSASB:SPEC:6012.7; WSASB:SPEC:6013;
	 *
	 * @test_Strategy: Test for AnonymousResponses assertion where <ReplyTo> header
	 * is set to the None URI. This value must be accepted.
	 *
	 */
	@Test
	public void testAnonymousResponsesWithReplyToSetToNone() throws Exception {
		logger.log(Level.INFO, "testAnonymousResponsesWithReplyToSetToNone");
		boolean pass = true;
		boolean done = false;

		SOAPMessage response = null;
		Dispatch<SOAPMessage> dispatchSM;
		try {
			String soapmsg = MessageFormat.format(ReplyToHeaderForAnonymousResponsesSoapMsg, url3, UUID.randomUUID(),
					W3CAddressingConstants.WSA_NONE_ADDRESS);
			dispatchSM = createDispatchSOAPMessage(service3, PORT_QNAME3);
			SOAPMessage request = JAXWS_Util.makeSOAPMessage(soapmsg);
			logger.log(Level.INFO, "Dumping SOAP Request ...");
			JAXWS_Util.dumpSOAPMessage(request, false);
			response = dispatchSM.invoke(request);
			logger.log(Level.INFO, "Dumping SOAP Response ...");
			JAXWS_Util.dumpSOAPMessage(response, false);
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught WebServiceException ignore: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("testAnonymousResponsesWithReplyToSetToNone failed");
	}

	/*
	 * @testName: testAnonymousResponsesWithFaultToSetToNone
	 *
	 * @assertion_ids: WSAMD:SPEC:3003; WSAMD:SPEC:3003.1; WSAMD:SPEC:3003.2;
	 * WSASB:SPEC:6012; WSASB:SPEC:6012.7; WSASB:SPEC:6013;
	 *
	 * @test_Strategy: Test for AnonymousResponses assertion where <FaultTo> header
	 * is set to the None URI. This value must be accepted.
	 *
	 */
	@Test
	public void testAnonymousResponsesWithFaultToSetToNone() throws Exception {
		logger.log(Level.INFO, "testAnonymousResponsesWithFaultToSetToNone");
		boolean pass = true;
		boolean done = false;

		SOAPMessage response = null;
		Dispatch<SOAPMessage> dispatchSM;
		try {
			String soapmsg = MessageFormat.format(ExceptionToHeaderForAnonymousResponsesSoapMsg, url3,
					UUID.randomUUID(), W3CAddressingConstants.WSA_NONE_ADDRESS,
					W3CAddressingConstants.WSA_ANONYMOUS_ADDRESS);
			dispatchSM = createDispatchSOAPMessage(service3, PORT_QNAME3);
			SOAPMessage request = JAXWS_Util.makeSOAPMessage(soapmsg);
			logger.log(Level.INFO, "Dumping SOAP Request ...");
			JAXWS_Util.dumpSOAPMessage(request, false);
			response = dispatchSM.invoke(request);
			logger.log(Level.INFO, "Dumping SOAP Response ...");
			JAXWS_Util.dumpSOAPMessage(response, false);
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught WebServiceException ignore: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("testAnonymousResponsesWithFaultToSetToNone failed");
	}

	/*
	 * @testName: testNonAnonymousResponsesWithReplyToSetToNone
	 *
	 * @assertion_ids: WSAMD:SPEC:3003; WSAMD:SPEC:3003.1; WSAMD:SPEC:3003.2;
	 * WSASB:SPEC:6012; WSASB:SPEC:6012.7; WSASB:SPEC:6013;
	 *
	 * @test_Strategy: Test for NonAnonymousResponses assertion where <ReplyTo>
	 * header is set to the None URI. This value must be accepted.
	 */
	@Test
	public void testNonAnonymousResponsesWithReplyToSetToNone() throws Exception {
		logger.log(Level.INFO, "testNonAnonymousResponsesWithReplyToSetToNone");
		boolean pass = true;

		SOAPMessage response = null;
		Dispatch<SOAPMessage> dispatchSM;
		try {
			String soapmsg = MessageFormat.format(ReplyToHeaderForNonAnonymousResponsesSoapMsg, url4, UUID.randomUUID(),
					W3CAddressingConstants.WSA_NONE_ADDRESS);
			dispatchSM = createDispatchSOAPMessage(service4, PORT_QNAME4);
			SOAPMessage request = JAXWS_Util.makeSOAPMessage(soapmsg);
			logger.log(Level.INFO, "Dumping SOAP Request ...");
			JAXWS_Util.dumpSOAPMessage(request, false);
			response = dispatchSM.invoke(request);
			logger.log(Level.INFO, "Dumping SOAP Response ...");
			JAXWS_Util.dumpSOAPMessage(response, false);
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught WebServiceException ignore: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("testNonAnonymousResponsesWithReplyToSetToNone failed");
	}

	/*
	 * @testName: testNonAnonymousResponsesWithFaultToSetToNone
	 *
	 * @assertion_ids: WSAMD:SPEC:3003; WSAMD:SPEC:3003.1; WSAMD:SPEC:3003.2;
	 * WSASB:SPEC:6012; WSASB:SPEC:6012.7; WSASB:SPEC:6013;
	 *
	 * @test_Strategy: Test for NonAnonymousResponses assertion where <FaultTo>
	 * header is set to the None URI. This value must be accepted.
	 */
	@Test
	public void testNonAnonymousResponsesWithFaultToSetToNone() throws Exception {
		logger.log(Level.INFO, "testNonAnonymousResponsesWithFaultToSetToNone");
		boolean pass = true;

		SOAPMessage response = null;
		Dispatch<SOAPMessage> dispatchSM;
		try {
			String soapmsg = MessageFormat.format(ExceptionToHeaderForNonAnonymousResponsesSoapMsg, url4,
					UUID.randomUUID(), W3CAddressingConstants.WSA_NONE_ADDRESS,
					W3CAddressingConstants.WSA_ANONYMOUS_ADDRESS);
			dispatchSM = createDispatchSOAPMessage(service4, PORT_QNAME4);
			SOAPMessage request = JAXWS_Util.makeSOAPMessage(soapmsg);
			logger.log(Level.INFO, "Dumping SOAP Request ...");
			JAXWS_Util.dumpSOAPMessage(request, false);
			response = dispatchSM.invoke(request);
			logger.log(Level.INFO, "Dumping SOAP Response ...");
			JAXWS_Util.dumpSOAPMessage(response, false);
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught WebServiceException ignore: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("testNonAnonymousResponsesWithFaultToSetToNone failed");
	}

	/*
	 * @testName: testOnlyAnonymousAddressSupportedFaultBadReplyTo
	 *
	 * @assertion_ids: WSAMD:SPEC:3003; WSAMD:SPEC:3003.1; WSAMD:SPEC:3003.2;
	 * WSASB:SPEC:6012; WSASB:SPEC:6012.7; WSASB:SPEC:6013;
	 *
	 * @test_Strategy: Test for OnlyAnonymousAddressSupported Exception from client
	 * runtime. Pass in soap message with <ReplyTo> header not equal to Anonymous
	 * URI. Expect SOAPFault.
	 *
	 */
	@Test
	public void testOnlyAnonymousAddressSupportedFaultBadReplyTo() throws Exception {
		logger.log(Level.INFO, "testOnlyAnonymousAddressSupportedFaultBadReplyTo");
		boolean pass = true;
		boolean done = false;

		SOAPMessage response = null;
		Dispatch<SOAPMessage> dispatchSM;
		try {
			String soapmsg = MessageFormat.format(ReplyToHeaderForAnonymousResponsesSoapMsg, url3, UUID.randomUUID(),
					url3 + "/badurl");
			dispatchSM = createDispatchSOAPMessage(service3, PORT_QNAME3);
			SOAPMessage request = JAXWS_Util.makeSOAPMessage(soapmsg);
			logger.log(Level.INFO, "Dumping SOAP Request ...");
			JAXWS_Util.dumpSOAPMessage(request, false);
			response = dispatchSM.invoke(request);
			logger.log(Level.INFO, "Dumping SOAP Response ...");
			JAXWS_Util.dumpSOAPMessage(response, false);
		} catch (SOAPFaultException e) {
			try {
				logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
				logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(e));
				logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(e));
				if (WsaSOAPUtils.isOnlyAnonymousAddressSupportedFaultCode(e))
					logger.log(Level.INFO, "SOAPFault contains expected Exceptioncode OnlyAnonymousAddressSupported");
				else {
					String Exceptioncode = WsaSOAPUtils.getFaultCode(e);
					TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
							+ ", expected: OnlyAnonymousAddressSupported");
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
				done = true;
			} catch (SOAPException e2) {
				TestUtil.logErr("Caught unexpected exception: ", e2);
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("testOnlyAnonymousAddressSupportedFaultBadReplyTo failed");
		if (done)
			return;

		try {
			if (response == null)
				throw new Exception("Expected a SOAPFault to be returned in SOAPResponse");
			if (!response.getSOAPPart().getEnvelope().getBody().hasFault())
				throw new Exception("Expected a SOAPFault to be returned in SOAPResponse");
		} catch (SOAPException e) {
			throw new Exception("Expected a SOAPFault to be returned in SOAPResponse");
		}
		try {
			logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
			logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(response));
			logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(response));
			if (WsaSOAPUtils.isOnlyAnonymousAddressSupportedFaultCode(response))
				logger.log(Level.INFO, "SOAPFault contains expected Exceptioncode OnlyAnonymousAddressSupported");
			else {
				String Exceptioncode = WsaSOAPUtils.getFaultCode(response);
				TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
						+ ", expected: OnlyAnonymousAddressSupported");
				pass = false;
			}
			if (WsaSOAPUtils.getFaultString(response) == null) {
				TestUtil.logErr("The Exceptionstring element MUST EXIST for SOAP 1.1 Exceptions");
				pass = false;
			}
			if (WsaSOAPUtils.getFaultDetail(response) != null) {
				TestUtil.logErr("The Exceptiondetail element MUST NOT EXIST for SOAP 1.1 "
						+ "Faults related to header entries");
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}

		if (!pass)
			throw new Exception("testOnlyAnonymousAddressSupportedFaultBadReplyTo failed");
	}

	/*
	 * @testName: testOnlyAnonymousAddressSupportedFaultBadFaultTo
	 *
	 * @assertion_ids: WSAMD:SPEC:3003; WSAMD:SPEC:3003.1; WSAMD:SPEC:3003.2;
	 * WSASB:SPEC:6012; WSASB:SPEC:6012.7; WSASB:SPEC:6013;
	 *
	 * @test_Strategy: Test for OnlyAnonymousAddressSupported Exception from client
	 * runtime. Pass in soap message with <FaultTo> header not equal to Anonymous
	 * URI. Expect SOAPFault.
	 *
	 */
	@Test
	public void testOnlyAnonymousAddressSupportedFaultBadFaultTo() throws Exception {
		logger.log(Level.INFO, "testOnlyAnonymousAddressSupportedFaultBadFaultTo");
		boolean pass = true;
		boolean done = false;

		SOAPMessage response = null;
		Dispatch<SOAPMessage> dispatchSM;
		try {
			String soapmsg = MessageFormat.format(ExceptionToHeaderForAnonymousResponsesSoapMsg, url3,
					UUID.randomUUID(), url3 + "/badurl", W3CAddressingConstants.WSA_ANONYMOUS_ADDRESS);
			dispatchSM = createDispatchSOAPMessage(service3, PORT_QNAME3);
			SOAPMessage request = JAXWS_Util.makeSOAPMessage(soapmsg);
			logger.log(Level.INFO, "Dumping SOAP Request ...");
			JAXWS_Util.dumpSOAPMessage(request, false);
			response = dispatchSM.invoke(request);
			logger.log(Level.INFO, "Dumping SOAP Response ...");
			JAXWS_Util.dumpSOAPMessage(response, false);
		} catch (SOAPFaultException e) {
			try {
				logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
				logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(e));
				logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(e));
				if (WsaSOAPUtils.isOnlyAnonymousAddressSupportedFaultCode(e))
					logger.log(Level.INFO, "SOAPFault contains expected Exceptioncode OnlyAnonymousAddressSupported");
				else {
					String Exceptioncode = WsaSOAPUtils.getFaultCode(e);
					TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
							+ ", expected: OnlyAnonymousAddressSupported");
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
				done = true;
			} catch (SOAPException e2) {
				TestUtil.logErr("Caught unexpected exception: ", e2);
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("testOnlyAnonymousAddressSupportedFaultBadFaultTo failed");
		if (done)
			return;

		try {
			if (response == null)
				throw new Exception("Expected a SOAPFault to be returned in SOAPResponse");
			if (!response.getSOAPPart().getEnvelope().getBody().hasFault())
				throw new Exception("Expected a SOAPFault to be returned in SOAPResponse");
		} catch (SOAPException e) {
			throw new Exception("Expected a SOAPFault to be returned in SOAPResponse");
		}

		try {
			logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
			logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(response));
			logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(response));
			if (WsaSOAPUtils.isOnlyAnonymousAddressSupportedFaultCode(response))
				logger.log(Level.INFO, "SOAPFault contains expected Exceptioncode OnlyAnonymousAddressSupported");
			else {
				String Exceptioncode = WsaSOAPUtils.getFaultCode(response);
				TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
						+ ", expected: OnlyAnonymousAddressSupported");
				pass = false;
			}
			if (WsaSOAPUtils.getFaultString(response) == null) {
				TestUtil.logErr("The Exceptionstring element MUST EXIST for SOAP 1.1 Exceptions");
				pass = false;
			}
			if (WsaSOAPUtils.getFaultDetail(response) != null) {
				TestUtil.logErr("The Exceptiondetail element MUST NOT EXIST for SOAP 1.1 "
						+ "Faults related to header entries");
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}

		if (!pass)
			throw new Exception("testOnlyAnonymousAddressSupportedFaultBadFaultTo failed");
	}

	/*
	 * @testName: testOnlyNonAnonymousAddressSupportedFaultBadReplyTo
	 *
	 * @assertion_ids: WSAMD:SPEC:3003; WSAMD:SPEC:3003.1; WSAMD:SPEC:3003.3;
	 * WSASB:SPEC:6012; WSASB:SPEC:6012.8; WSASB:SPEC:6013;
	 *
	 * @test_Strategy: Test for OnlyNonAnonymousAddressSupported Exception from
	 * client runtime. Pass in soap message with <ReplyTo> header equal to Anonymous
	 * URI. Expect SOAPFault.
	 *
	 */
	@Test
	public void testOnlyNonAnonymousAddressSupportedFaultBadReplyTo() throws Exception {
		logger.log(Level.INFO, "testOnlyNonAnonymousAddressSupportedFaultBadReplyTo");
		boolean pass = true;
		boolean done = false;

		SOAPMessage response = null;
		Dispatch<SOAPMessage> dispatchSM;
		try {
			String soapmsg = MessageFormat.format(ReplyToHeaderForNonAnonymousResponsesSoapMsg, url4, UUID.randomUUID(),
					W3CAddressingConstants.WSA_ANONYMOUS_ADDRESS);
			dispatchSM = createDispatchSOAPMessage(service4, PORT_QNAME4);
			SOAPMessage request = JAXWS_Util.makeSOAPMessage(soapmsg);
			logger.log(Level.INFO, "Dumping SOAP Request ...");
			JAXWS_Util.dumpSOAPMessage(request, false);
			response = dispatchSM.invoke(request);
			logger.log(Level.INFO, "Dumping SOAP Response ...");
			JAXWS_Util.dumpSOAPMessage(response, false);
		} catch (SOAPFaultException e) {
			try {
				logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
				logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(e));
				logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(e));
				if (WsaSOAPUtils.isOnlyNonAnonymousAddressSupportedFaultCode(e))
					logger.log(Level.INFO,
							"SOAPFault contains expected Exceptioncode OnlyNonAnonymousAddressSupported");
				else {
					String Exceptioncode = WsaSOAPUtils.getFaultCode(e);
					TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
							+ ", expected: OnlyNonAnonymousAddressSupported");
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
				done = true;
			} catch (SOAPException e2) {
				TestUtil.logErr("Caught unexpected exception: ", e2);
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}

		if (!pass)
			throw new Exception("testOnlyNonAnonymousAddressSupportedFaultBadReplyTo failed");

		if (done)
			return;

		try {
			if (response == null)
				throw new Exception("Expected a SOAPFault to be returned in SOAPResponse");
			if (!response.getSOAPPart().getEnvelope().getBody().hasFault())
				throw new Exception("Expected a SOAPFault to be returned in SOAPResponse");
		} catch (SOAPException e) {
			throw new Exception("Expected a SOAPFault to be returned in SOAPResponse");
		}
		try {
			logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
			logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(response));
			logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(response));
			if (WsaSOAPUtils.isOnlyNonAnonymousAddressSupportedFaultCode(response))
				logger.log(Level.INFO, "SOAPFault contains expected Exceptioncode OnlyNonAnonymousAddressSupported");
			else {
				String Exceptioncode = WsaSOAPUtils.getFaultCode(response);
				TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
						+ ", expected: OnlyNonAnonymousAddressSupported");
				pass = false;
			}
			if (WsaSOAPUtils.getFaultString(response) == null) {
				TestUtil.logErr("The Exceptionstring element MUST EXIST for SOAP 1.1 Exceptions");
				pass = false;
			}
			if (WsaSOAPUtils.getFaultDetail(response) != null) {
				TestUtil.logErr("The Exceptiondetail element MUST NOT EXIST for SOAP 1.1 "
						+ "Faults related to header entries");
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}

		if (!pass)
			throw new Exception("testOnlyNonAnonymousAddressSupportedFaultBadReplyTo failed");
	}

	/*
	 * @testName: testOnlyNonAnonymousAddressSupportedFaultBadFaultTo
	 *
	 * @assertion_ids: WSAMD:SPEC:3003; WSAMD:SPEC:3003.1; WSAMD:SPEC:3003.3;
	 * WSASB:SPEC:6012; WSASB:SPEC:6012.8; WSASB:SPEC:6013;
	 *
	 * @test_Strategy: Test for OnlyNonAnonymousAddressSupported Exception from
	 * client runtime. Pass in soap message with <FaultTo> header equal to Anonymous
	 * URI. Expect SOAPFault.
	 *
	 */
	@Test
	public void testOnlyNonAnonymousAddressSupportedFaultBadFaultTo() throws Exception {
		logger.log(Level.INFO, "testOnlyNonAnonymousAddressSupportedFaultBadFaultTo");
		boolean pass = true;
		boolean done = false;

		SOAPMessage response = null;
		Dispatch<SOAPMessage> dispatchSM;
		try {
			String soapmsg = MessageFormat.format(ExceptionToHeaderForNonAnonymousResponsesSoapMsg, url4,
					UUID.randomUUID(), W3CAddressingConstants.WSA_ANONYMOUS_ADDRESS,
					W3CAddressingConstants.WSA_ANONYMOUS_ADDRESS);
			dispatchSM = createDispatchSOAPMessage(service4, PORT_QNAME4);
			SOAPMessage request = JAXWS_Util.makeSOAPMessage(soapmsg);
			logger.log(Level.INFO, "Dumping SOAP Request ...");
			JAXWS_Util.dumpSOAPMessage(request, false);
			response = dispatchSM.invoke(request);
			logger.log(Level.INFO, "Dumping SOAP Response ...");
			JAXWS_Util.dumpSOAPMessage(response, false);
		} catch (SOAPFaultException e) {
			try {
				logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
				logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(e));
				logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(e));
				if (WsaSOAPUtils.isOnlyNonAnonymousAddressSupportedFaultCode(e))
					logger.log(Level.INFO,
							"SOAPFault contains expected Exceptioncode OnlyNonAnonymousAddressSupported");
				else {
					String Exceptioncode = WsaSOAPUtils.getFaultCode(e);
					TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
							+ ", expected: OnlyNonAnonymousAddressSupported");
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
				done = true;
			} catch (SOAPException e2) {
				TestUtil.logErr("Caught unexpected exception: ", e2);
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught unexpected exception: ", e);
			pass = false;
		}

		if (!pass)
			throw new Exception("testOnlyNonAnonymousAddressSupportedFaultBadFaultTo failed");

		if (done)
			return;

		try {
			if (response == null)
				throw new Exception("Expected a SOAPFault to be returned in SOAPResponse");
			if (!response.getSOAPPart().getEnvelope().getBody().hasFault())
				throw new Exception("Expected a SOAPFault to be returned in SOAPResponse");
		} catch (SOAPException e) {
			throw new Exception("Expected a SOAPFault to be returned in SOAPResponse");
		}
		try {
			logger.log(Level.INFO, "Verify the SOAPFault Exceptioncode");
			logger.log(Level.INFO, "FaultCode=" + WsaSOAPUtils.getFaultCode(response));
			logger.log(Level.INFO, "FaultString=" + WsaSOAPUtils.getFaultString(response));
			if (WsaSOAPUtils.isOnlyNonAnonymousAddressSupportedFaultCode(response))
				logger.log(Level.INFO, "SOAPFault contains expected Exceptioncode OnlyNonAnonymousAddressSupported");
			else {
				String Exceptioncode = WsaSOAPUtils.getFaultCode(response);
				TestUtil.logErr("SOAPFault contains unexpected Exceptioncode got: " + Exceptioncode
						+ ", expected: OnlyNonAnonymousAddressSupported");
				pass = false;
			}
			if (WsaSOAPUtils.getFaultString(response) == null) {
				TestUtil.logErr("The Exceptionstring element MUST EXIST for SOAP 1.1 Exceptions");
				pass = false;
			}
			if (WsaSOAPUtils.getFaultDetail(response) != null) {
				TestUtil.logErr("The Exceptiondetail element MUST NOT EXIST for SOAP 1.1 "
						+ "Faults related to header entries");
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}

		if (!pass)
			throw new Exception("testOnlyNonAnonymousAddressSupportedFaultBadFaultTo failed");
	}
}
