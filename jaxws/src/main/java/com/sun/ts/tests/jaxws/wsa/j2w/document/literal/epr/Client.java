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
 * $Id: Client.java 51075 2003-03-27 10:44:21Z lschwenk $
 */

package com.sun.ts.tests.jaxws.wsa.j2w.document.literal.epr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.common.XMLUtils;
import com.sun.ts.tests.jaxws.wsa.common.EprUtil;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.soap.AddressingFeature;
import jakarta.xml.ws.soap.SOAPFaultException;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;

public class Client extends BaseClient {

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.wsa.j2w.document.literal.epr.";

	// URL properties used by the test
	private static final String ENDPOINT_URL = "wsaj2wdleprtest.endpoint.1";

	private static final String WSDLLOC_URL = "wsaj2wdleprtest.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	// service and port information
	private static final String NAMESPACEURI = "http://foobar.org/";

	private static final String SERVICE_NAME = "AddNumbersService";

	private static final String PORT_NAME = "AddNumbersPort";

	private static final String PORT_TYPE_NAME = "AddNumbers";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private QName PORT_TYPE_QNAME = new QName(NAMESPACEURI, PORT_TYPE_NAME);

	private WebServiceFeature[] wsftrue = { new AddressingFeature(true, true) };

	private WebServiceFeature[] wsffalse = { new AddressingFeature(false, false) };

	AddNumbers port = null;

	BindingProvider bp = null;

	static AddNumbersService service = null;

	private Source source = null;

	private Dispatch<Object> dispatchJaxb = null;

	private Dispatch<Source> dispatchSrc = null;

	private Dispatch<SOAPMessage> dispatchSM = null;

	private String doAddNumbersRequest = "<ns1:doAddNumbers xmlns:ns1=\"http://foobar.org/\"><arg0>10</arg0><arg1>10</arg1></ns1:doAddNumbers>";

	private String doAddNumbersRequestSM = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns3:doAddNumbers xmlns:ns3=\"http://foobar.org/\"><arg0>10</arg0><arg1>10</arg1></ns3:doAddNumbers></S:Body></S:Envelope>";

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	private static final Class JAXB_OBJECT_FACTORY = com.sun.ts.tests.jaxws.wsa.j2w.document.literal.epr.ObjectFactory.class;

	private JAXBContext createJAXBContext() {
		try {
			return JAXBContext.newInstance(JAXB_OBJECT_FACTORY);
		} catch (jakarta.xml.bind.JAXBException e) {
			throw new WebServiceException(e.getMessage(), e);
		}
	}

	private Dispatch<Object> createDispatchJAXB(W3CEndpointReference myepr, WebServiceFeature[] wsf) throws Exception {
		if (wsf == null)
			return service.createDispatch(myepr, createJAXBContext(), jakarta.xml.ws.Service.Mode.PAYLOAD);
		else
			return service.createDispatch(myepr, createJAXBContext(), jakarta.xml.ws.Service.Mode.PAYLOAD, wsf);
	}

	private Dispatch<Source> createDispatchSource(W3CEndpointReference myepr, WebServiceFeature[] wsf)
			throws Exception {
		if (wsf == null)
			return service.createDispatch(myepr, Source.class, jakarta.xml.ws.Service.Mode.PAYLOAD);
		else
			return service.createDispatch(myepr, Source.class, jakarta.xml.ws.Service.Mode.PAYLOAD, wsf);
	}

	private Dispatch<SOAPMessage> createDispatchSOAPMessage(W3CEndpointReference myepr, WebServiceFeature[] wsf)
			throws Exception {
		if (wsf == null)
			return service.createDispatch(myepr, SOAPMessage.class, jakarta.xml.ws.Service.Mode.MESSAGE);
		else
			return service.createDispatch(myepr, SOAPMessage.class, jakarta.xml.ws.Service.Mode.MESSAGE, wsf);
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
		logger.log(Level.INFO, "Obtain service via Service.create(URL, QName)");
		service = (AddNumbersService) JAXWS_Util.getService(wsdlurl, SERVICE_QNAME, AddNumbersService.class);
		logger.log(Level.INFO, "service=" + service);
		logger.log(Level.INFO, "Obtain port");
		port = (AddNumbers) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, AddNumbersService.class, PORT_QNAME,
				AddNumbers.class, wsftrue);
		bp = (BindingProvider) port;
		JAXWS_Util.setTargetEndpointAddress(port, url);
		JAXWS_Util.dumpTargetEndpointAddress(port);
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "bindingProvider=" + bp);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		logger.log(Level.INFO, "Obtain port");
		port = (AddNumbers) service.getPort(AddNumbers.class, wsftrue);
		bp = (BindingProvider) port;
		JAXWS_Util.dumpTargetEndpointAddress(port);
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "bindingProvider=" + bp);
	}

	protected void getService() {
		service = (AddNumbersService) getSharedObject();
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
	 * @testName: EPRGetEPRViaWSCTest1
	 *
	 * @assertion_ids: WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:158; JAXWS:SPEC:4027.3; JAXWS:SPEC:4027.4; JAXWS:SPEC:4027.5;
	 * WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2; WSAMD:SPEC:2001;
	 * WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3; WSAMD:SPEC:2002;
	 * WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3; WSAMD:SPEC:2002.4;
	 * JAXWS:SPEC:5027; JAXWS:SPEC:5028;
	 *
	 * @test_Strategy: Retrieve EPR via WebServiceContext.getEndpointReference().
	 *
	 */
	@Test
	public void EPRGetEPRViaWSCTest1() throws Exception {
		logger.log(Level.INFO, "EPRGetEPRViaWSCTest1");
		boolean pass = true;
		try {
			TestUtil.logMsg("Retrieve EPR via WebServiceContext.getEndpointReference()");
			W3CEndpointReference epr = port.getW3CEPR1();
			if (epr != null) {
				logger.log(Level.INFO, "---------------------------");
				logger.log(Level.INFO, "DUMP OF ENDPOINT REFERENCE");
				logger.log(Level.INFO, "---------------------------");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				epr.writeTo(new StreamResult(baos));
				logger.log(Level.INFO, baos.toString());
				DOMResult dr = new DOMResult();
				epr.writeTo(dr);
				XMLUtils.xmlDumpDOMNodes(dr.getNode(), false);
				logger.log(Level.INFO, "Validate the EPR for correctness)");
				if (!EprUtil.validateEPR(epr, url, SERVICE_QNAME, PORT_QNAME, PORT_TYPE_QNAME, Boolean.TRUE,
						wsdlurl.toString()))
					pass = false;
			} else {
				TestUtil.logErr("EPR is null (unexpected)");
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRGetEPRViaWSCTest1 failed");
	}

	/*
	 * @testName: EPRGetEPRViaWSCTest2
	 *
	 * @assertion_ids: WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:159; JAXWS:SPEC:4027.3; JAXWS:SPEC:4027.4; JAXWS:SPEC:4027.5;
	 * WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2; WSAMD:SPEC:2001;
	 * WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3; WSAMD:SPEC:2002;
	 * WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3; WSAMD:SPEC:2002.4;
	 * JAXWS:SPEC:5027; JAXWS:SPEC:5028;
	 *
	 * @test_Strategy: Retrieve EPR via WebServiceContext.getEndpointReference(
	 * java.lang.Class).
	 */
	@Test
	public void EPRGetEPRViaWSCTest2() throws Exception {
		logger.log(Level.INFO, "EPRGetEPRViaWSCTest2");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Retrieve EPR via WebServiceContext.getEndpointReference(java.lang.Class)");
			W3CEndpointReference epr = port.getW3CEPR2();
			if (epr != null) {
				logger.log(Level.INFO, "---------------------------");
				logger.log(Level.INFO, "DUMP OF ENDPOINT REFERENCE");
				logger.log(Level.INFO, "---------------------------");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				epr.writeTo(new StreamResult(baos));
				logger.log(Level.INFO, baos.toString());
				DOMResult dr = new DOMResult();
				epr.writeTo(dr);
				XMLUtils.xmlDumpDOMNodes(dr.getNode(), false);
				if (!EprUtil.validateEPR(epr, url, SERVICE_QNAME, PORT_QNAME, PORT_TYPE_QNAME, Boolean.TRUE,
						wsdlurl.toString()))
					pass = false;
			} else {
				TestUtil.logErr("EPR is null (unexpected)");
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRGetEPRViaWSCTest2 failed");
	}

	/*
	 * @testName: EPRGetEPRViaBPTest1
	 *
	 * @assertion_ids: WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:186; JAXWS:SPEC:4027.3; JAXWS:SPEC:4027.4; JAXWS:SPEC:4027.5;
	 * WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2; WSAMD:SPEC:2001;
	 * WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3; WSAMD:SPEC:2002;
	 * WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3; WSAMD:SPEC:2002.4;
	 * JAXWS:SPEC:5027; JAXWS:SPEC:4022;
	 *
	 * @test_Strategy: Retrieve EPR via BindingProvider.getEndpointReference().
	 *
	 */
	@Test
	public void EPRGetEPRViaBPTest1() throws Exception {
		logger.log(Level.INFO, "EPRGetEPRViaBPTest1");
		boolean pass = true;
		try {
			TestUtil.logMsg("Retrieve EPR via BindingProvider.getEndpointReference()");
			W3CEndpointReference epr = (W3CEndpointReference) bp.getEndpointReference();
			if (epr != null) {
				logger.log(Level.INFO, "---------------------------");
				logger.log(Level.INFO, "DUMP OF ENDPOINT REFERENCE");
				logger.log(Level.INFO, "---------------------------");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				epr.writeTo(new StreamResult(baos));
				logger.log(Level.INFO, baos.toString());
				DOMResult dr = new DOMResult();
				epr.writeTo(dr);
				XMLUtils.xmlDumpDOMNodes(dr.getNode(), false);
				if (!EprUtil.validateEPR(epr, url, SERVICE_QNAME, PORT_QNAME, PORT_TYPE_QNAME, Boolean.TRUE,
						wsdlurl.toString()))
					pass = false;
			} else {
				TestUtil.logErr("EPR is null (unexpected)");
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRGetEPRViaBPTest1 failed");
	}

	/*
	 * @testName: EPRGetEPRViaBPTest2
	 *
	 * @assertion_ids: WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:187; JAXWS:SPEC:4027.3; JAXWS:SPEC:4027.4; JAXWS:SPEC:4027.5;
	 * WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2; WSAMD:SPEC:2001;
	 * WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3; WSAMD:SPEC:2002;
	 * WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3; WSAMD:SPEC:2002.4;
	 * JAXWS:SPEC:5027; JAXWS:SPEC:4022;
	 *
	 * @test_Strategy: Retrieve EPR via BindingProvider.getEndpointReference(
	 * java.lang.Class).
	 */
	@Test
	public void EPRGetEPRViaBPTest2() throws Exception {
		logger.log(Level.INFO, "EPRGetEPRViaBPTest2");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Retrieve EPR via BindingProvider.getEndpointReference(" + "java.lang.Class)");
			W3CEndpointReference epr = (W3CEndpointReference) bp
					.getEndpointReference(jakarta.xml.ws.wsaddressing.W3CEndpointReference.class);
			if (epr != null) {
				logger.log(Level.INFO, "---------------------------");
				logger.log(Level.INFO, "DUMP OF ENDPOINT REFERENCE");
				logger.log(Level.INFO, "---------------------------");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				epr.writeTo(new StreamResult(baos));
				logger.log(Level.INFO, baos.toString());
				DOMResult dr = new DOMResult();
				epr.writeTo(dr);
				XMLUtils.xmlDumpDOMNodes(dr.getNode(), false);
				if (!EprUtil.validateEPR(epr, url, SERVICE_QNAME, PORT_QNAME, PORT_TYPE_QNAME, Boolean.TRUE,
						wsdlurl.toString()))
					pass = false;
			} else {
				TestUtil.logErr("EPR is null (unexpected)");
				pass = false;
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRGetEPRViaBPTest2 failed");
	}

	/*
	 * @testName: EPRGetEPRViaBPWithUnsupportedEPRClassTest
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:187; JAXWS:SPEC:4027.3; JAXWS:SPEC:4027.4; JAXWS:SPEC:4027.5;
	 * JAXWS:SPEC:4027.6; JAXWS:SPEC:5024.3;
	 *
	 * @test_Strategy: Retrieve EPR via BindingProvider.getEndpointReference(
	 * java.lang.Class). Pass in an invalid Class. Expect a WebServiceException to
	 * be thrown.
	 */
	@Test
	public void EPRGetEPRViaBPWithUnsupportedEPRClassTest() throws Exception {
		logger.log(Level.INFO, "EPRGetEPRViaBPWithUnsupportedEPRClassTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Retrieve EPR via BindingProvider.getEndpointReference(" + "java.lang.Class)");
			logger.log(Level.INFO, "Pass in unsupported EPR Class and expect WebServiceException");
			MyEPR epr = (MyEPR) bp.getEndpointReference(MyEPR.class);
			TestUtil.logErr("Did not throw expected WebServiceException");
			pass = false;
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught expected WebServiceException: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRGetEPRViaBPWithUnsupportedEPRClassTest failed");
	}

	/*
	 * @testName: EPRWriteToAndReadFromTest
	 *
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:141; JAXWS:JAVADOC:142;
	 *
	 * @test_Strategy: Retrieve EPR via BindingProvider.getEndpointReference().
	 * Write EPR as an XML Infoset Object using writeTo() method and then read EPR
	 * back from the XML Infoset Object using readfrom() method.
	 */
	@Test
	public void EPRWriteToAndReadFromTest() throws Exception {
		logger.log(Level.INFO, "EPRWriteToAndReadFromTest");
		boolean pass = true;
		try {
			TestUtil.logMsg("Retrieve EPR via BindingProvider.getEndpointReference()");
			W3CEndpointReference epr = (W3CEndpointReference) bp.getEndpointReference();
			logger.log(Level.INFO, "Write the EPR to a ByteArrayOutputStream Object");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			epr.writeTo(new StreamResult(baos));
			logger.log(Level.INFO, baos.toString());
			logger.log(Level.INFO, "Read the EPR from a ByteArrayInputStream Object");
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			epr = (W3CEndpointReference) EndpointReference.readFrom(new StreamSource(bais));
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRWriteToAndReadFromTest failed");
	}

	/*
	 * @testName: ServiceGetPortViaWSCAndWSFTrueTest
	 *
	 * @assertion_ids: WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:SPEC:4028; JAXWS:SPEC:4028.1; JAXWS:JAVADOC:140;
	 *
	 * @test_Strategy: Retrieve EPR via WebServiceContext.getEndpointReference().
	 * From the returned EPR get the port via Service.getPort(EPR, AddNumbers.class,
	 * wsf). WebServiceFeature is passed with Addressing=true. Verify invocation
	 * behavior.
	 *
	 */
	@Test
	public void ServiceGetPortViaWSCAndWSFTrueTest() throws Exception {
		logger.log(Level.INFO, "ServiceGetPortViaWSCAndWSFTrueTest");
		boolean pass = true;
		try {
			TestUtil.logMsg("Retrieve EPR via WebServiceContext.getEndpointReference()");
			W3CEndpointReference epr = port.getW3CEPR1();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			epr.writeTo(new StreamResult(baos));
			logger.log(Level.INFO, baos.toString());
			logger.log(Level.INFO, "Retrieve the port from the EPR via Service.getPort()");
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=true");
			AddNumbers retport = (AddNumbers) service.getPort(epr, AddNumbers.class, wsftrue);
			if (retport == null) {
				TestUtil.logErr("Service.getPort(EPR, Class, wsftrue) returned null (unexpected)");
				pass = false;
			} else {
				logger.log(Level.INFO, "Verify invocation behavior on port");
				int result = retport.doAddNumbers(10, 10);
				logger.log(Level.INFO, "Invocation succeeded (expected) now check result");
				if (result != 20) {
					TestUtil.logErr("Expected result=20, got result=" + result);
					pass = false;
				} else
					logger.log(Level.INFO, "Got expected result=20");
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("ServiceGetPortViaWSCAndWSFTrueTest failed");
	}

	/*
	 * @testName: ServiceGetPortViaWSCAndWSFFalseTest
	 *
	 * @assertion_ids: WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:SPEC:4028; JAXWS:SPEC:4028.1; JAXWS:JAVADOC:140;
	 *
	 * @test_Strategy: Retrieve EPR via WebServiceContext.getEndpointReference().
	 * From the returned EPR get the port via Service.getPort(EPR, AddNumbers.class,
	 * wsf). WebServiceFeature is passed with Addressing=false. Verify invocation
	 * behavior.
	 *
	 */
	@Test
	public void ServiceGetPortViaWSCAndWSFFalseTest() throws Exception {
		logger.log(Level.INFO, "ServiceGetPortViaWSCAndWSFFalseTest");
		boolean pass = true;
		try {
			TestUtil.logMsg("Retrieve EPR via WebServiceContext.getEndpointReference()");
			W3CEndpointReference epr = port.getW3CEPR1();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			epr.writeTo(new StreamResult(baos));
			logger.log(Level.INFO, baos.toString());
			logger.log(Level.INFO, "Retrieve the port from the EPR via Service.getPort()");
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=false");
			AddNumbers retport = (AddNumbers) service.getPort(epr, AddNumbers.class, wsffalse);
			if (retport == null) {
				TestUtil.logErr("Service.getPort(EPR, Class, wsffalse) returned null (unexpected)");
				pass = false;
			} else {
				logger.log(Level.INFO, "Verify invocation behavior on port");
				int result = retport.doAddNumbers(10, 10);
				TestUtil.logErr("Did not throw expected SOAPFaultException");
				pass = false;
			}
		} catch (SOAPFaultException e) {
			logger.log(Level.INFO, "Caught expected SOAPFaultException: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("ServiceGetPortViaWSCAndWSFFalseTest failed");
	}

	/*
	 * @testName: ServiceGetPortViaBPAndWSFTrueTest
	 *
	 * @assertion_ids: WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:SPEC:4028; JAXWS:SPEC:4028.1; JAXWS:JAVADOC:140;
	 *
	 * @test_Strategy: Retrieve EPR via BindingProvider.getEndpointReference(). From
	 * the returned EPR get the port via Service.getPort(EPR, AddNumbers.class,
	 * wsf). WebServiceFeature is passed with Addressing=true. Verify invocation
	 * behavior.
	 *
	 */
	@Test
	public void ServiceGetPortViaBPAndWSFTrueTest() throws Exception {
		logger.log(Level.INFO, "ServiceGetPortViaBPAndWSFTrueTest");
		boolean pass = true;
		try {
			TestUtil.logMsg("Retrieve EPR via BindingProvider.getEndpointReference()");
			W3CEndpointReference epr = (W3CEndpointReference) bp.getEndpointReference();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			epr.writeTo(new StreamResult(baos));
			logger.log(Level.INFO, baos.toString());
			logger.log(Level.INFO, "Retrieve the port from the EPR via Service.getPort()");
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=true");
			AddNumbers retport = (AddNumbers) service.getPort(epr, AddNumbers.class, wsftrue);
			if (retport == null) {
				TestUtil.logErr("Service.getPort(EPR, Class, wsftrue) returned null (unexpected)");
				pass = false;
			} else {
				logger.log(Level.INFO, "Verify invocation behavior on port");
				int result = retport.doAddNumbers(10, 10);
				logger.log(Level.INFO, "Invocation succeeded (expected) now check result");
				if (result != 20) {
					TestUtil.logErr("Expected result=20, got result=" + result);
					pass = false;
				} else
					logger.log(Level.INFO, "Got expected result=20");
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("ServiceGetPortViaBPAndWSFTrueTest failed");
	}

	/*
	 * @testName: ServiceGetPortViaBPAndWSFFalseTest
	 *
	 * @assertion_ids: WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:SPEC:4028; JAXWS:SPEC:4028.1; JAXWS:JAVADOC:140;
	 *
	 * @test_Strategy: Retrieve EPR via BindingProvider.getEndpointReference(). From
	 * the returned EPR get the port via Service.getPort(EPR, AddNumbers.class,
	 * wsf). WebServiceFeature is passed with Addressing=false. Verify invocation
	 * behavior.
	 *
	 */
	@Test
	public void ServiceGetPortViaBPAndWSFFalseTest() throws Exception {
		logger.log(Level.INFO, "ServiceGetPortViaBPAndWSFFalseTest");
		boolean pass = true;
		try {
			TestUtil.logMsg("Retrieve EPR via BindingProvider.getEndpointReference()");
			W3CEndpointReference epr = (W3CEndpointReference) bp.getEndpointReference();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			epr.writeTo(new StreamResult(baos));
			logger.log(Level.INFO, baos.toString());
			logger.log(Level.INFO, "Retrieve the port from the EPR via Service.getPort()");
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=false");
			AddNumbers retport = (AddNumbers) service.getPort(epr, AddNumbers.class, wsffalse);
			if (retport == null) {
				TestUtil.logErr("Service.getPort(EPR, Class, wsffalse) returned null (unexpected)");
				pass = false;
			} else {
				logger.log(Level.INFO, "Verify invocation behavior on port");
				int result = retport.doAddNumbers(10, 10);
				TestUtil.logErr("Did not throw expected SOAPFaultException");
				pass = false;
			}
		} catch (SOAPFaultException e) {
			logger.log(Level.INFO, "Caught expected SOAPFaultException: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("ServiceGetPortViaBPAndWSFFalseTest failed");
	}

	/*
	 * @testName: EPRGetPortViaWSCAndNoWSFTest
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:SPEC:4028; JAXWS:SPEC:4028.1; JAXWS:JAVADOC:140;
	 *
	 * @test_Strategy: Retrieve EPR via WebServiceContext.getEndpointReference().
	 * From the returned EPR get the port via EndpointReference.getPort
	 * (AddNumbers.class). No WebServiceFeature is passed (DEFAULT CASE). Verify
	 * invocation behavior.
	 *
	 */
	@Test
	public void EPRGetPortViaWSCAndNoWSFTest() throws Exception {
		logger.log(Level.INFO, "EPRGetPortViaWSCAndNoWSFTest");
		boolean pass = true;
		try {
			TestUtil.logMsg("Retrieve EPR via WebServiceContext.getEndpointReference()");
			W3CEndpointReference epr = port.getW3CEPR1();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			epr.writeTo(new StreamResult(baos));
			logger.log(Level.INFO, baos.toString());
			logger.log(Level.INFO, "Retrieve port from EPR via EndpointReference.getPort()");
			logger.log(Level.INFO, "Don't pass a WebServiceFeature (DEFAULT CASE)");
			AddNumbers retport = (AddNumbers) epr.getPort(AddNumbers.class);
			if (retport == null) {
				TestUtil.logErr("EPR.getPort(Class) returned null (unexpected)");
				pass = false;
			} else {
				logger.log(Level.INFO, "Verify invocation behavior on port");
				int result = retport.doAddNumbers(10, 10);
				logger.log(Level.INFO, "Invocation succeeded (expected) now check result");
				if (result != 20) {
					TestUtil.logErr("Expected result=20, got result=" + result);
					pass = false;
				} else
					logger.log(Level.INFO, "Got expected result=20");
			}
		} catch (SOAPFaultException e) {
			logger.log(Level.INFO, "Caught expected SOAPFaultException: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRGetPortViaWSCAndNoWSFTest failed");
	}

	/*
	 * @testName: EPRGetPortViaWSCAndWSFTrueTest
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:SPEC:4028; JAXWS:SPEC:4028.1; JAXWS:JAVADOC:140;
	 *
	 * @test_Strategy: Retrieve EPR via WebServiceContext.getEndpointReference().
	 * From the returned EPR get the port via EndpointReference.getPort
	 * (AddNumbers.class, wsf). WebServiceFeature is passed with Addressing=true.
	 * Verify invocation behavior.
	 *
	 */
	@Test
	public void EPRGetPortViaWSCAndWSFTrueTest() throws Exception {
		logger.log(Level.INFO, "EPRGetPortViaWSCAndWSFTrueTest");
		boolean pass = true;
		try {
			TestUtil.logMsg("Retrieve EPR via WebServiceContext.getEndpointReference()");
			W3CEndpointReference epr = port.getW3CEPR1();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			epr.writeTo(new StreamResult(baos));
			logger.log(Level.INFO, baos.toString());
			logger.log(Level.INFO, "Retrieve the port from the EPR via EndpointReference.getPort()");
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=true");
			AddNumbers retport = (AddNumbers) epr.getPort(AddNumbers.class, wsftrue);
			if (retport == null) {
				TestUtil.logErr("EPR.getPort(Class, wsftrue) returned null (unexpected)");
				pass = false;
			} else {
				logger.log(Level.INFO, "Verify invocation behavior on port");
				int result = retport.doAddNumbers(10, 10);
				logger.log(Level.INFO, "Invocation succeeded (expected) now check result");
				if (result != 20) {
					TestUtil.logErr("Expected result=20, got result=" + result);
					pass = false;
				} else
					logger.log(Level.INFO, "Got expected result=20");
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRGetPortViaWSCAndWSFTrueTest failed");
	}

	/*
	 * @testName: EPRGetPortViaWSCAndWSFFalseTest
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:SPEC:4028; JAXWS:SPEC:4028.1; JAXWS:JAVADOC:140;
	 *
	 * @test_Strategy: Retrieve EPR via WebServiceContext.getEndpointReference().
	 * From the returned EPR get the port via EndpointReference.getPort
	 * (AddNumbers.class, wsf). WebServiceFeature is passed with Addressing=false.
	 * Verify invocation behavior.
	 *
	 */
	@Test
	public void EPRGetPortViaWSCAndWSFFalseTest() throws Exception {
		logger.log(Level.INFO, "EPRGetPortViaWSCAndWSFFalseTest");
		boolean pass = true;
		try {
			TestUtil.logMsg("Retrieve EPR via WebServiceContext.getEndpointReference()");
			W3CEndpointReference epr = port.getW3CEPR1();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			epr.writeTo(new StreamResult(baos));
			logger.log(Level.INFO, baos.toString());
			logger.log(Level.INFO, "Retrieve the port from the EPR via EndpointReference.getPort()");
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=false");
			AddNumbers retport = (AddNumbers) epr.getPort(AddNumbers.class, wsffalse);
			if (retport == null) {
				TestUtil.logErr("EPR.getPort(Class, wsffalse) returned null (unexpected)");
				pass = false;
			} else {
				logger.log(Level.INFO, "Verify invocation behavior on port");
				int result = retport.doAddNumbers(10, 10);
				TestUtil.logErr("Did not throw expected SOAPFaultException");
				pass = false;
			}
		} catch (SOAPFaultException e) {
			logger.log(Level.INFO, "Caught expected SOAPFaultException: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRGetPortViaWSCAndWSFFalseTest failed");
	}

	/*
	 * @testName: EPRGetPortViaBPAndNoWSFTest
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:SPEC:4028; JAXWS:SPEC:4028.1; JAXWS:JAVADOC:140;
	 *
	 * @test_Strategy: Retrieve EPR via BindingProvider.getEndpointReference(). From
	 * the returned EPR get the port via EndpointReference.getPort
	 * (AddNumbers.class). No WebServiceFeature is passed (DEFAULT CASE). Verify
	 * invocation behavior.
	 *
	 */
	@Test
	public void EPRGetPortViaBPAndNoWSFTest() throws Exception {
		logger.log(Level.INFO, "EPRGetPortViaBPAndNoWSFTest");
		boolean pass = true;
		try {
			TestUtil.logMsg("Retrieve EPR via BindingProvider.getEndpointReference()");
			W3CEndpointReference epr = (W3CEndpointReference) bp.getEndpointReference();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			epr.writeTo(new StreamResult(baos));
			logger.log(Level.INFO, baos.toString());
			logger.log(Level.INFO, "Retrieve port from EPR via EndpointReference.getPort()");
			logger.log(Level.INFO, "Don't pass a WebServiceFeature (DEFAULT CASE)");
			AddNumbers retport = (AddNumbers) epr.getPort(AddNumbers.class);
			if (retport == null) {
				TestUtil.logErr("EPR.getPort(Class) returned null (unexpected)");
				pass = false;
			} else {
				logger.log(Level.INFO, "Verify invocation behavior on port");
				int result = retport.doAddNumbers(10, 10);
				logger.log(Level.INFO, "Invocation succeeded (expected) now check result");
				if (result != 20) {
					TestUtil.logErr("Expected result=20, got result=" + result);
					pass = false;
				} else
					logger.log(Level.INFO, "Got expected result=20");
			}
		} catch (SOAPFaultException e) {
			logger.log(Level.INFO, "Caught expected SOAPFaultException: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRGetPortViaBPAndNoWSFTest failed");
	}

	/*
	 * @testName: EPRGetPortViaBPAndWSFTrueTest
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:SPEC:4028; JAXWS:SPEC:4028.1; JAXWS:JAVADOC:140;
	 *
	 * @test_Strategy: Retrieve EPR via BindingProvider.getEndpointReference(). From
	 * the returned EPR get the port via EndpointReference.getPort
	 * (AddNumbers.class, wsf). WebServiceFeature is passed with Addressing=true.
	 * Verify invocation behavior.
	 *
	 */
	@Test
	public void EPRGetPortViaBPAndWSFTrueTest() throws Exception {
		logger.log(Level.INFO, "EPRGetPortViaBPAndWSFTrueTest");
		boolean pass = true;
		try {
			TestUtil.logMsg("Retrieve EPR via BindingProvider.getEndpointReference()");
			W3CEndpointReference epr = (W3CEndpointReference) bp.getEndpointReference();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			epr.writeTo(new StreamResult(baos));
			logger.log(Level.INFO, baos.toString());
			logger.log(Level.INFO, "Retrieve the port from the EPR via EndpointReference.getPort()");
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=true");
			AddNumbers retport = (AddNumbers) epr.getPort(AddNumbers.class, wsftrue);
			if (retport == null) {
				TestUtil.logErr("EPR.getPort(Class, wsftrue) returned null (unexpected)");
				pass = false;
			} else {
				logger.log(Level.INFO, "Verify invocation behavior on port");
				int result = retport.doAddNumbers(10, 10);
				logger.log(Level.INFO, "Invocation succeeded (expected) now check result");
				if (result != 20) {
					TestUtil.logErr("Expected result=20, got result=" + result);
					pass = false;
				} else
					logger.log(Level.INFO, "Got expected result=20");
			}
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRGetPortViaBPAndWSFTrueTest failed");
	}

	/*
	 * @testName: EPRGetPortViaBPAndWSFFalseTest
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:SPEC:4028; JAXWS:SPEC:4028.1; JAXWS:JAVADOC:140;
	 *
	 * @test_Strategy: Retrieve EPR via BindingProvider.getEndpointReference(). From
	 * the returned EPR get the port via EndpointReference.getPort
	 * (AddNumbers.class, wsf). WebServiceFeature is passed with Addressing=false.
	 * Verify invocation behavior.
	 *
	 */
	@Test
	public void EPRGetPortViaBPAndWSFFalseTest() throws Exception {
		logger.log(Level.INFO, "EPRGetPortViaBPAndWSFFalseTest");
		boolean pass = true;
		try {
			TestUtil.logMsg("Retrieve EPR via BindingProvider.getEndpointReference()");
			W3CEndpointReference epr = (W3CEndpointReference) bp.getEndpointReference();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			epr.writeTo(new StreamResult(baos));
			logger.log(Level.INFO, baos.toString());
			logger.log(Level.INFO, "Retrieve the port from the EPR via EndpointReference.getPort()");
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=false");
			AddNumbers retport = (AddNumbers) epr.getPort(AddNumbers.class, wsffalse);
			if (retport == null) {
				TestUtil.logErr("EPR.getPort(Class, wsffalse) returned null (unexpected)");
				pass = false;
			} else {
				logger.log(Level.INFO, "Verify invocation behavior on port");
				int result = retport.doAddNumbers(10, 10);
				TestUtil.logErr("Did not throw expected SOAPFaultException");
				pass = false;
			}
		} catch (SOAPFaultException e) {
			logger.log(Level.INFO, "Caught expected SOAPFaultException: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRGetPortViaBPAndWSFFalseTest failed");
	}

	/*
	 * @testName: EPRViaWSCCreateDispatchWSFTrueAndInvokeTest1
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:154; JAXWS:SPEC:4030;
	 *
	 * @test_Strategy: Retrieve EPR via WebServiceContext.getEndpointReference().
	 * Create a Dispatch object via Service.createDispatch() using the returned EPR,
	 * perform invocation via Dispatch.invoke() and then verify the result. Pass
	 * WebServiceFeature with Addressing=true.
	 *
	 */
	@Test
	public void EPRViaWSCCreateDispatchWSFTrueAndInvokeTest1() throws Exception {
		logger.log(Level.INFO, "EPRViaWSCCreateDispatchWSFTrueAndInvokeTest1");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create a Dispatch object of type Source");
			W3CEndpointReference myepr = port.getW3CEPR1();
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=true");
			dispatchSrc = createDispatchSource(myepr, wsftrue);
			Source requestMsg = JAXWS_Util.makeSource(doAddNumbersRequest, "StreamSource");
			logger.log(Level.INFO, "Perform Dispatch invocation");
			Source responseMsg = dispatchSrc.invoke(requestMsg);
			TestUtil.logMsg("Dispatch invocation succeeded (expected) now check result");
			String responseStr = JAXWS_Util.getDOMResultAsString(JAXWS_Util.getSourceAsDOMResult(responseMsg));
			logger.log(Level.INFO, "responseStr=" + responseStr);
			if (responseStr.indexOf("doAddNumbersResponse") == -1 || responseStr.indexOf("return") == -1
					|| responseStr.indexOf("20") == -1) {
				TestUtil.logErr("Unexpected response results");
				pass = false;
			} else
				logger.log(Level.INFO, "Got expected response results");
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRViaWSCCreateDispatchWSFTrueAndInvokeTest1 failed");
	}

	/*
	 * @testName: EPRViaWSCCreateDispatchWSFTrueAndInvokeTest2
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:154; JAXWS:SPEC:4030;
	 *
	 * @test_Strategy: Retrieve EPR via WebServiceContext.getEndpointReference(
	 * java.lang.Class). Create a Dispatch object via Service. createDispatch()
	 * using the returned EPR, perform invocation via Dispatch.invoke() and then
	 * verify the result. Pass WebServiceFeature with Addressing=true.
	 *
	 */
	@Test
	public void EPRViaWSCCreateDispatchWSFTrueAndInvokeTest2() throws Exception {
		logger.log(Level.INFO, "EPRViaWSCCreateDispatchWSFTrueAndInvokeTest2");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create a Dispatch object of type Source");
			W3CEndpointReference myepr = port.getW3CEPR2();
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=true");
			dispatchSrc = createDispatchSource(myepr, wsftrue);
			Source requestMsg = JAXWS_Util.makeSource(doAddNumbersRequest, "StreamSource");
			logger.log(Level.INFO, "Perform Dispatch invocation");
			Source responseMsg = dispatchSrc.invoke(requestMsg);
			TestUtil.logMsg("Dispatch invocation succeeded (expected) now check result");
			String responseStr = JAXWS_Util.getDOMResultAsString(JAXWS_Util.getSourceAsDOMResult(responseMsg));
			logger.log(Level.INFO, "responseStr=" + responseStr);
			if (responseStr.indexOf("doAddNumbersResponse") == -1 || responseStr.indexOf("return") == -1
					|| responseStr.indexOf("20") == -1) {
				TestUtil.logErr("Unexpected response results");
				pass = false;
			} else
				logger.log(Level.INFO, "Got expected response results");
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRViaWSCCreateDispatchWSFTrueAndInvokeTest2 failed");
	}

	/*
	 * @testName: EPRViaWSCCreateDispatchWSFFalseAndInvokeTest3
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:154; JAXWS:SPEC:4030;
	 *
	 * @test_Strategy: Retrieve EPR via WebServiceContext.getEndpointReference().
	 * Create a Dispatch object via Service.createDispatch() using the returned EPR.
	 * Pass WebServiceFeature with Addressing=false. Expect a WebServiceException to
	 * be thrown.
	 *
	 */
	@Test
	public void EPRViaWSCCreateDispatchWSFFalseAndInvokeTest3() throws Exception {
		logger.log(Level.INFO, "EPRViaWSCCreateDispatchWSFFalseAndInvokeTest3");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create a Dispatch object of type Source");
			W3CEndpointReference myepr = port.getW3CEPR1();
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=false");
			dispatchSrc = createDispatchSource(myepr, wsffalse);
			Source requestMsg = JAXWS_Util.makeSource(doAddNumbersRequest, "StreamSource");
			logger.log(Level.INFO, "Perform Dispatch invocation");
			Source responseMsg = dispatchSrc.invoke(requestMsg);
			TestUtil.logErr("Did not throw expected WebServiceException");
			pass = false;
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught expected WebServiceException: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRViaWSCCreateDispatchWSFFalseAndInvokeTest3 failed");
	}

	/*
	 * @testName: EPRViaWSCCreateJAXBDispatchWSFTrueAndInvokeTest1
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:154; JAXWS:SPEC:4030;
	 *
	 * @test_Strategy: Retrieve EPR via WebServiceContext.getEndpointReference().
	 * Create a JAXB Dispatch object via Service.createDispatch() using the returned
	 * EPR, perform invocation via Dispatch.invoke() and then verify the result.
	 * Pass WebServiceFeature with Addressing=true.
	 *
	 */
	@Test
	public void EPRViaWSCCreateJAXBDispatchWSFTrueAndInvokeTest1() throws Exception {
		logger.log(Level.INFO, "EPRViaWSCCreateJAXBDispatchWSFTrueAndInvokeTest1");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create a Dispatch object of type JAXB");
			W3CEndpointReference myepr = port.getW3CEPR1();
			ObjectFactory of = new ObjectFactory();
			DoAddNumbers numbers = of.createDoAddNumbers();
			numbers.setArg0(10);
			numbers.setArg1(10);
			JAXBElement<DoAddNumbers> request = of.createDoAddNumbers(numbers);
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=true");
			dispatchJaxb = createDispatchJAXB(myepr, wsftrue);
			java.util.Map<String, Object> reqContext = dispatchJaxb.getRequestContext();
			logger.log(Level.INFO, "Perform Dispatch invocation");
			JAXBElement<DoAddNumbersResponse> response = (JAXBElement<DoAddNumbersResponse>) dispatchJaxb
					.invoke(request);
			TestUtil.logMsg("Dispatch invocation succeeded (expected) now check result");
			int result = response.getValue().getReturn();
			logger.log(Level.INFO, "result=" + result);
			if (result != 20) {
				TestUtil.logErr("Expected result=20, got result=" + result);
				pass = false;
			} else
				logger.log(Level.INFO, "Got expected response results");
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRViaWSCCreateJAXBDispatchWSFTrueAndInvokeTest1 failed");
	}

	/*
	 * @testName: EPRViaWSCCreateJAXBDispatchWSFFalseAndInvokeTest2
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:154; JAXWS:SPEC:4030;
	 *
	 * @test_Strategy: Retrieve EPR via WebServiceContext.getEndpointReference().
	 * Create a JAXB Dispatch object via Service.createDispatch() using the returned
	 * EPR, perform invocation via Dispatch. invoke() and then verify the result.
	 * Pass WebServiceFeature with Addressing=false. Expect a WebServiceException to
	 * be thrown.
	 *
	 */
	@Test
	public void EPRViaWSCCreateJAXBDispatchWSFFalseAndInvokeTest2() throws Exception {
		logger.log(Level.INFO, "EPRViaWSCCreateJAXBDispatchWSFFalseAndInvokeTest2");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create a Dispatch object of type JAXB");
			W3CEndpointReference myepr = port.getW3CEPR1();
			ObjectFactory of = new ObjectFactory();
			DoAddNumbers numbers = of.createDoAddNumbers();
			numbers.setArg0(10);
			numbers.setArg1(10);
			JAXBElement<DoAddNumbers> request = of.createDoAddNumbers(numbers);
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=false");
			dispatchJaxb = createDispatchJAXB(myepr, wsffalse);
			java.util.Map<String, Object> reqContext = dispatchJaxb.getRequestContext();
			logger.log(Level.INFO, "Perform Dispatch invocation");
			JAXBElement<DoAddNumbersResponse> response = (JAXBElement<DoAddNumbersResponse>) dispatchJaxb
					.invoke(request);
			TestUtil.logErr("Did not throw expected WebServiceException");
			pass = false;
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught expected WebServiceException: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRViaWSCCreateJAXBDispatchWSFFalseAndInvokeTest2 failed");
	}

	/*
	 * @testName: EPRViaBPCreateDispatchWSFTrueAndInvokeTest1
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:154; JAXWS:SPEC:4030;
	 *
	 * @test_Strategy: Retrieve EPR via BindingProvider.getEndpointReference().
	 * Create a Dispatch object via Service.createDispatch() using the returned EPR,
	 * perform invocation via Dispatch.invoke() and then verify the result. Pass
	 * WebServiceFeature with Addressing=true.
	 *
	 */
	@Test
	public void EPRViaBPCreateDispatchWSFTrueAndInvokeTest1() throws Exception {
		logger.log(Level.INFO, "EPRViaBPCreateDispatchWSFTrueAndInvokeTest1");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create a Dispatch object of type Source");
			W3CEndpointReference myepr = (W3CEndpointReference) bp.getEndpointReference();
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=true");
			dispatchSrc = createDispatchSource(myepr, wsftrue);
			Source requestMsg = JAXWS_Util.makeSource(doAddNumbersRequest, "StreamSource");
			logger.log(Level.INFO, "Perform Dispatch invocation");
			Source responseMsg = dispatchSrc.invoke(requestMsg);
			TestUtil.logMsg("Dispatch invocation succeeded (expected) now check result");
			String responseStr = JAXWS_Util.getDOMResultAsString(JAXWS_Util.getSourceAsDOMResult(responseMsg));
			logger.log(Level.INFO, "responseStr=" + responseStr);
			if (responseStr.indexOf("doAddNumbersResponse") == -1 || responseStr.indexOf("return") == -1
					|| responseStr.indexOf("20") == -1) {
				TestUtil.logErr("Unexpected response results");
				pass = false;
			} else
				logger.log(Level.INFO, "Got expected response results");
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRViaBPCreateDispatchWSFTrueAndInvokeTest1 failed");
	}

	/*
	 * @testName: EPRViaBPCreateDispatchWSFTrueAndInvokeTest2
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:154; JAXWS:SPEC:4030;
	 *
	 * @test_Strategy: Retrieve EPR via BindingProvider.getEndpointReference(
	 * java.lang.Class). Create a Dispatch object via Service. createDispatch()
	 * using the returned EPR, perform invocation and then verify the result. Pass
	 * WebServiceFeature with Addressing=true.
	 *
	 */
	@Test
	public void EPRViaBPCreateDispatchWSFTrueAndInvokeTest2() throws Exception {
		logger.log(Level.INFO, "EPRViaBPCreateDispatchWSFTrueAndInvokeTest2");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create a Dispatch object of type SOAPMessage");
			W3CEndpointReference myepr = (W3CEndpointReference) bp
					.getEndpointReference(jakarta.xml.ws.wsaddressing.W3CEndpointReference.class);
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=true");
			dispatchSM = createDispatchSOAPMessage(myepr, wsftrue);
			SOAPMessage requestMsg = JAXWS_Util.makeSOAPMessage(doAddNumbersRequestSM);
			logger.log(Level.INFO, "Perform Dispatch invocation");
			SOAPMessage responseMsg = dispatchSM.invoke(requestMsg);
			TestUtil.logMsg("Dispatch invocation succeeded (expected) now check result");
			String responseStr = JAXWS_Util.getSOAPMessageAsString(responseMsg);
			logger.log(Level.INFO, "responseStr=" + responseStr);
			if (responseStr.indexOf("doAddNumbersResponse") == -1 || responseStr.indexOf("return") == -1
					|| responseStr.indexOf("20") == -1) {
				TestUtil.logErr("Unexpected response results");
				pass = false;
			} else
				logger.log(Level.INFO, "Got expected response results");
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRViaBPCreateDispatchWSFTrueAndInvokeTest2 failed");
	}

	/*
	 * @testName: EPRViaBPCreateDispatchWSFFalseAndInvokeTest3
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:154; JAXWS:SPEC:4030;
	 *
	 * @test_Strategy: Retrieve EPR via BindingProvider.getEndpointReference(
	 * java.lang.Class). Create a Dispatch object via Service. createDispatch()
	 * using the returned EPR, perform invocation via Dispatch.invoke() and then
	 * verify the result. Pass WebServiceFeature with Addressing=false. Expect a
	 * WebServiceException to be thrown.
	 *
	 */
	@Test
	public void EPRViaBPCreateDispatchWSFFalseAndInvokeTest3() throws Exception {
		logger.log(Level.INFO, "EPRViaBPCreateDispatchWSFFalseAndInvokeTest3");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create a Dispatch object of type SOAPMessage");
			W3CEndpointReference myepr = (W3CEndpointReference) bp
					.getEndpointReference(jakarta.xml.ws.wsaddressing.W3CEndpointReference.class);
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=false");
			dispatchSM = createDispatchSOAPMessage(myepr, wsffalse);
			SOAPMessage requestMsg = JAXWS_Util.makeSOAPMessage(doAddNumbersRequestSM);
			logger.log(Level.INFO, "Perform Dispatch invocation");
			SOAPMessage responseMsg = dispatchSM.invoke(requestMsg);
			TestUtil.logErr("Did not throw expected WebServiceException");
			pass = false;
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught expected WebServiceException: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRViaBPCreateDispatchWSFFalseAndInvokeTest3 failed");
	}

	/*
	 * @testName: EPRViaBPCreateJAXBDispatchWSFTrueAndInvokeTest1
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:154; JAXWS:SPEC:4030;
	 *
	 * @test_Strategy: Retrieve EPR via BindingProvider.getEndpointReference(
	 * java.lang.Class). Create a JAXB Dispatch object via Service. createDispatch()
	 * using the returned EPR, perform invocation via Dispatch.invoke() and then
	 * verify the result. Pass WebServiceFeature with Addressing=true.
	 *
	 */
	@Test
	public void EPRViaBPCreateJAXBDispatchWSFTrueAndInvokeTest1() throws Exception {
		logger.log(Level.INFO, "EPRViaBPCreateJAXBDispatchWSFTrueAndInvokeTest1");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create a Dispatch object of type JAXB");
			W3CEndpointReference myepr = (W3CEndpointReference) bp
					.getEndpointReference(jakarta.xml.ws.wsaddressing.W3CEndpointReference.class);
			ObjectFactory of = new ObjectFactory();
			DoAddNumbers numbers = of.createDoAddNumbers();
			numbers.setArg0(10);
			numbers.setArg1(10);
			JAXBElement<DoAddNumbers> request = of.createDoAddNumbers(numbers);
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=true");
			dispatchJaxb = createDispatchJAXB(myepr, wsftrue);
			java.util.Map<String, Object> reqContext = dispatchJaxb.getRequestContext();
			logger.log(Level.INFO, "Perform Dispatch invocation");
			JAXBElement<DoAddNumbersResponse> response = (JAXBElement<DoAddNumbersResponse>) dispatchJaxb
					.invoke(request);
			TestUtil.logMsg("Dispatch invocation succeeded (expected) now check result");
			int result = response.getValue().getReturn();
			logger.log(Level.INFO, "result=" + result);
			if (result != 20) {
				TestUtil.logErr("Expected result=20, got result=" + result);
				pass = false;
			} else
				logger.log(Level.INFO, "Got expected response results");
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRViaBPCreateJAXBDispatchWSFTrueAndInvokeTest1 failed");
	}

	/*
	 * @testName: EPRViaBPCreateJAXBDispatchWSFFalseAndInvokeTest2
	 *
	 * @assertion_ids: WSAMD:SPEC:2000; WSAMD:SPEC:2000.1; WSAMD:SPEC:2000.2;
	 * WSAMD:SPEC:2001; WSAMD:SPEC:2001.1; WSAMD:SPEC:2001.2; WSAMD:SPEC:2001.3;
	 * WSAMD:SPEC:2002; WSAMD:SPEC:2002.1; WSAMD:SPEC:2002.2; WSAMD:SPEC:2002.3;
	 * WSAMD:SPEC:2002.4; WSACORE:SPEC:2007; WSACORE:SPEC:2008; WSACORE:SPEC:2009;
	 * JAXWS:JAVADOC:154; JAXWS:SPEC:4030;
	 *
	 * @test_Strategy: Retrieve EPR via BindingProvider.getEndpointReference(
	 * java.lang.Class). Create a JAXB Dispatch object via Service. createDispatch()
	 * using the returned EPR, perform invocation via Dispatch.invoke() and then
	 * verify the result. Pass WebServiceFeature with Addressing=false. Expect a
	 * WebServiceException to be thrown.
	 *
	 */
	@Test
	public void EPRViaBPCreateJAXBDispatchWSFFalseAndInvokeTest2() throws Exception {
		logger.log(Level.INFO, "EPRViaBPCreateJAXBDispatchWSFFalseAndInvokeTest2");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Create a Dispatch object of type JAXB");
			W3CEndpointReference myepr = (W3CEndpointReference) bp
					.getEndpointReference(jakarta.xml.ws.wsaddressing.W3CEndpointReference.class);
			ObjectFactory of = new ObjectFactory();
			DoAddNumbers numbers = of.createDoAddNumbers();
			numbers.setArg0(10);
			numbers.setArg1(10);
			JAXBElement<DoAddNumbers> request = of.createDoAddNumbers(numbers);
			logger.log(Level.INFO, "Pass WebServiceFeature with Addressing=false");
			dispatchJaxb = createDispatchJAXB(myepr, wsffalse);
			java.util.Map<String, Object> reqContext = dispatchJaxb.getRequestContext();
			logger.log(Level.INFO, "Perform Dispatch invocation");
			JAXBElement<DoAddNumbersResponse> response = (JAXBElement<DoAddNumbersResponse>) dispatchJaxb
					.invoke(request);
			TestUtil.logErr("Did not throw expected WebServiceException");
			pass = false;
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "Caught expected WebServiceException: " + e.getMessage());
		} catch (Exception e) {
			pass = false;
			TestUtil.logErr("Caught unexpected exception: ", e);
		}
		if (!pass)
			throw new Exception("EPRViaBPCreateJAXBDispatchWSFFalseAndInvokeTest2 failed");
	}
}
