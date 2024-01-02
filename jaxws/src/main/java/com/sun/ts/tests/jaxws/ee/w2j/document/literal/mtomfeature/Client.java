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

package com.sun.ts.tests.jaxws.ee.w2j.document.literal.mtomfeature;

import java.awt.Image;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.AttachmentHelper;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.ee.w2j.document.literal.marshalltest.MarshallTestService;

import jakarta.activation.DataHandler;
import jakarta.xml.ws.Holder;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.soap.MTOMFeature;

public class Client extends BaseClient {

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.w2j.document.literal.mtomfeature.";

	// service and port information
	private static final String NAMESPACEURI = "http://mtomfeatureservice.org/wsdl";

	private static final String SERVICE_NAME = "MTOMFeatureTestService";

	private static final String PORT_NAME1 = "MTOMFeatureTest1Port";

	private static final String PORT_NAME2 = "MTOMFeatureTest2Port";

	private static final String PORT_NAME3 = "MTOMFeatureTest3Port";

	private static final String PORT_NAME4 = "MTOMFeatureTest4Port";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME1 = new QName(NAMESPACEURI, PORT_NAME1);

	private QName PORT_QNAME2 = new QName(NAMESPACEURI, PORT_NAME2);

	private QName PORT_QNAME3 = new QName(NAMESPACEURI, PORT_NAME3);

	private QName PORT_QNAME4 = new QName(NAMESPACEURI, PORT_NAME4);

	private URL docURL1 = null;

	private URL docURL2 = null;

	private URL docURL3 = null;

	private URL docURL4 = null;

	private URL docURL11 = null;

	private URL docURL12 = null;

	private URL docURL13 = null;

	private URL docURL14 = null;

	private URL docURLSmallJpeg = null;

	private URL docURLBigJpeg = null;

	String SDOC1 = "text.xml";

	String SDOC2 = "application.xml";

	String SDOC3 = "attach.html";

	String SDOC4 = "small.jpg";

	String SDOC11 = "text2.xml";

	String SDOC12 = "application2.xml";

	String SDOC13 = "attach2.html";

	String SDOC14 = "big.jpg";

	String SDOCSmallJpeg = "small.jpg";

	String SDOCBigJpeg = "big.jpg";

	// URL properties used by the test
	private static final String ENDPOINT_URL1 = "mtomfeature.endpoint.1";

	private static final String ENDPOINT_URL2 = "mtomfeature.endpoint.2";

	private static final String ENDPOINT_URL3 = "mtomfeature.endpoint.3";

	private static final String ENDPOINT_URL4 = "mtomfeature.endpoint.4";

	private static final String WSDLLOC_URL = "mtomfeature.wsdlloc.1";

	private static final String CTXROOT = "mtomfeature.ctxroot.1";

	private String url1 = null;

	private String url2 = null;

	private String url3 = null;

	private String url4 = null;

	private URL wsdlurl = null;

	private String ctxroot = null;

	private MTOMFeatureTest1 port1 = null;

	private MTOMFeatureTest2 port2 = null;

	private MTOMFeatureTest3 port3_1 = null;

	private MTOMFeatureTest3 port3_2 = null;

	private MTOMFeatureTest4 port4_1 = null;

	private MTOMFeatureTest4 port4_2 = null;

	private WebServiceFeature[] mtomenabled = { new MTOMFeature(true) };

	private WebServiceFeature[] mtomenabledtheshold2000 = { new MTOMFeature(true, 2000) };

	private WebServiceFeature[] mtomdisabledtheshold2000 = { new MTOMFeature(false, 2000) };

	static MTOMFeatureTestService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		service = (MTOMFeatureTestService) getSharedObject();
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
		file = JAXWS_Util.getURLFromProp(ENDPOINT_URL4);
		url4 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		ctxroot = JAXWS_Util.getURLFromProp(CTXROOT);
		logger.log(Level.INFO, "Service Endpoint URL1: " + url1);
		logger.log(Level.INFO, "Service Endpoint URL2: " + url2);
		logger.log(Level.INFO, "Service Endpoint URL3: " + url3);
		logger.log(Level.INFO, "Service Endpoint URL4: " + url4);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
		logger.log(Level.INFO, "Context Root:         " + ctxroot);
	}

	protected void getPortStandalone() throws Exception {
		port1 = (MTOMFeatureTest1) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, MTOMFeatureTestService.class, PORT_QNAME1,
				MTOMFeatureTest1.class, mtomenabled);
		JAXWS_Util.setTargetEndpointAddress(port1, url1);
		port2 = (MTOMFeatureTest2) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, MTOMFeatureTestService.class, PORT_QNAME2,
				MTOMFeatureTest2.class, mtomenabled);
		JAXWS_Util.setTargetEndpointAddress(port2, url2);
		port3_1 = (MTOMFeatureTest3) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, MTOMFeatureTestService.class,
				PORT_QNAME3, MTOMFeatureTest3.class, mtomenabledtheshold2000);
		JAXWS_Util.setTargetEndpointAddress(port3_1, url3);
		port3_2 = (MTOMFeatureTest3) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, MTOMFeatureTestService.class,
				PORT_QNAME3, MTOMFeatureTest3.class, mtomdisabledtheshold2000);
		JAXWS_Util.setTargetEndpointAddress(port3_2, url3);
		port4_1 = (MTOMFeatureTest4) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, MTOMFeatureTestService.class,
				PORT_QNAME4, MTOMFeatureTest4.class, mtomenabledtheshold2000);
		JAXWS_Util.setTargetEndpointAddress(port4_1, url4);
		port4_2 = (MTOMFeatureTest4) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, MTOMFeatureTestService.class,
				PORT_QNAME4, MTOMFeatureTest4.class, mtomdisabledtheshold2000);
		JAXWS_Util.setTargetEndpointAddress(port4_2, url4);

	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port1 = (MTOMFeatureTest1) service.getPort(MTOMFeatureTest1.class, mtomenabled);
		port2 = (MTOMFeatureTest2) service.getPort(MTOMFeatureTest2.class, mtomenabled);
		port3_1 = (MTOMFeatureTest3) service.getPort(MTOMFeatureTest3.class, mtomenabledtheshold2000);
		port3_2 = (MTOMFeatureTest3) service.getPort(MTOMFeatureTest3.class, mtomdisabledtheshold2000);
		port4_1 = (MTOMFeatureTest4) service.getPort(MTOMFeatureTest4.class, mtomenabledtheshold2000);
		port4_2 = (MTOMFeatureTest4) service.getPort(MTOMFeatureTest4.class, mtomdisabledtheshold2000);
		// SOAPBinding binding = (SOAPBinding)((BindingProvider)port).getBinding();
		// JAXWS_Util.setSOAPLogging(port);
		logger.log(Level.INFO, "port=" + port1);
		logger.log(Level.INFO, "Obtained port1");
		JAXWS_Util.dumpTargetEndpointAddress(port1);
		logger.log(Level.INFO, "port=" + port2);
		logger.log(Level.INFO, "Obtained port2");
		JAXWS_Util.dumpTargetEndpointAddress(port2);
		logger.log(Level.INFO, "port=" + port3_1);
		logger.log(Level.INFO, "Obtained port3_1");
		JAXWS_Util.dumpTargetEndpointAddress(port3_1);
		logger.log(Level.INFO, "port=" + port3_2);
		logger.log(Level.INFO, "Obtained port3_2");
		JAXWS_Util.dumpTargetEndpointAddress(port3_2);
		logger.log(Level.INFO, "port=" + port4_1);
		logger.log(Level.INFO, "Obtained port4_1");
		JAXWS_Util.dumpTargetEndpointAddress(port4_1);
		logger.log(Level.INFO, "port=" + port4_2);
		logger.log(Level.INFO, "Obtained port4_2");
		JAXWS_Util.dumpTargetEndpointAddress(port4_2);
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
		docURL1 = ctsurl.getURL(PROTOCOL, hostname, portnum, ctxroot + "/" + SDOC1);
		docURL2 = ctsurl.getURL(PROTOCOL, hostname, portnum, ctxroot + "/" + SDOC2);
		docURL3 = ctsurl.getURL(PROTOCOL, hostname, portnum, ctxroot + "/" + SDOC3);
		docURL4 = ctsurl.getURL(PROTOCOL, hostname, portnum, ctxroot + "/" + SDOC4);
		docURL11 = ctsurl.getURL(PROTOCOL, hostname, portnum, ctxroot + "/" + SDOC11);
		docURL12 = ctsurl.getURL(PROTOCOL, hostname, portnum, ctxroot + "/" + SDOC12);
		docURL13 = ctsurl.getURL(PROTOCOL, hostname, portnum, ctxroot + "/" + SDOC13);
		docURL14 = ctsurl.getURL(PROTOCOL, hostname, portnum, ctxroot + "/" + SDOC14);
		docURLSmallJpeg = ctsurl.getURL(PROTOCOL, hostname, portnum, ctxroot + "/" + SDOCSmallJpeg);
		docURLBigJpeg = ctsurl.getURL(PROTOCOL, hostname, portnum, ctxroot + "/" + SDOCBigJpeg);

	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: ClientEnabledServerEnabledMTOMInTest
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * WS4EE:SPEC:5006; JAXWS:JAVADOC:192; JAXWS:SPEC:7021; JAXWS:SPEC:7021.1;
	 *
	 * @test_Strategy: Test MTOM attachments on input.
	 */
	@Test
	public void ClientEnabledServerEnabledMTOMInTest() throws Exception {
		logger.log(Level.INFO, "ClientEnabledServerEnabledMTOMInTest");
		boolean pass = true;

		try {
			DataType data = new DataType();

			data.setDocName1(SDOC1);
			data.setDocName2(SDOC2);
			data.setDocName3(SDOC3);
			data.setDocName4(SDOC4);

			data.setDocUrl1(docURL1.toString());
			data.setDocUrl2(docURL2.toString());
			data.setDocUrl3(docURL3.toString());
			data.setDocUrl4(docURL4.toString());

			StreamSource doc1 = AttachmentHelper.getSourceDoc(docURL1);
			StreamSource doc2 = AttachmentHelper.getSourceDoc(docURL2);
			DataHandler doc3 = AttachmentHelper.getDataHandlerDoc(docURL3);
			Image doc4 = AttachmentHelper.getImageDoc(docURL4);

			data.setDoc1(doc1);
			data.setDoc2(doc2);
			data.setDoc3(doc3);
			data.setDoc4(doc4);

			TestUtil.logMsg("Send 4 documents using MTOM via webservice method mtomIn()");
			logger.log(Level.INFO, "Documents to send: [" + SDOC1 + "," + SDOC2 + "," + SDOC3 + "," + SDOC4 + "]");
			String result = port1.mtomIn(data);
			if (!result.equals("")) {
				TestUtil.logErr("An error occurred with one or more of the attachments");
				TestUtil.logErr("result=" + result);
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientEnabledServerEnabledMTOMInTest failed");
	}

	/*
	 * @testName: ClientEnabledServerEnabledMTOMInOutTest
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * WS4EE:SPEC:5006; JAXWS:JAVADOC:192; JAXWS:SPEC:7021; JAXWS:SPEC:7021.1;
	 *
	 * @test_Strategy: Test MTOM attachments on input and output.
	 */
	@Test
	public void ClientEnabledServerEnabledMTOMInOutTest() throws Exception {
		logger.log(Level.INFO, "ClientEnabledServerEnabledMTOMInOutTest");
		boolean pass = true;

		try {
			Holder<String> hDocName1 = new Holder<String>(SDOC1);
			Holder<String> hDocName2 = new Holder<String>(SDOC2);
			Holder<String> hDocName3 = new Holder<String>(SDOC3);
			Holder<String> hDocName4 = new Holder<String>(SDOC4);

			Holder<String> hDocUrl1 = new Holder<String>(docURL1.toString());
			Holder<String> hDocUrl2 = new Holder<String>(docURL2.toString());
			Holder<String> hDocUrl3 = new Holder<String>(docURL3.toString());
			Holder<String> hDocUrl4 = new Holder<String>(docURL4.toString());
			Holder<String> hDocUrl11 = new Holder<String>(docURL11.toString());
			Holder<String> hDocUrl12 = new Holder<String>(docURL12.toString());
			Holder<String> hDocUrl13 = new Holder<String>(docURL13.toString());
			Holder<String> hDocUrl14 = new Holder<String>(docURL14.toString());

			StreamSource doc1 = AttachmentHelper.getSourceDoc(docURL1);
			StreamSource doc2 = AttachmentHelper.getSourceDoc(docURL2);
			DataHandler doc3 = AttachmentHelper.getDataHandlerDoc(docURL3);
			Image doc4 = AttachmentHelper.getImageDoc(docURL4);

			Holder<Source> hDoc1 = new Holder<Source>(doc1);
			Holder<Source> hDoc2 = new Holder<Source>(doc2);
			Holder<DataHandler> hDoc3 = new Holder<DataHandler>(doc3);
			Holder<Image> hDoc4 = new Holder<Image>(doc4);
			Holder<String> hResult = new Holder<String>("");
			logger.log(Level.INFO, "Send and receieve 4 documents using MTOM via webservice method mtomInOut()");
			logger.log(Level.INFO, "Documents to send: [" + SDOC1 + "," + SDOC2 + "," + SDOC3 + "," + SDOC4 + "]");
			logger.log(Level.INFO,
					"Documents to receive: [" + SDOC11 + "," + SDOC12 + "," + SDOC13 + "," + SDOC14 + "]");
			port1.mtomInOut(hDocName1, hDocName2, hDocName3, hDocName4, hDocUrl1, hDocUrl2, hDocUrl3, hDocUrl4,
					hDocUrl11, hDocUrl12, hDocUrl13, hDocUrl14, hDoc1, hDoc2, hDoc3, hDoc4, hResult);
			if (!(hResult.value).equals("")) {
				TestUtil.logErr("Server-side errors occurred:\n" + hResult.value);
				pass = false;
			}
			logger.log(Level.INFO, "Verify the contents of the received documents");

			doc1 = AttachmentHelper.getSourceDoc(docURL11);
			doc2 = AttachmentHelper.getSourceDoc(docURL12);
			doc3 = AttachmentHelper.getDataHandlerDoc(docURL13);
			doc4 = AttachmentHelper.getImageDoc(docURL14);

			// Now test the documents that were sent back by Server
			String tmpRes = AttachmentHelper.validateAttachmentData(doc1, hDoc1.value, SDOC11);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error for doc11:|" + tmpRes + "|");
				pass = false;
			}
			tmpRes = AttachmentHelper.validateAttachmentData(doc2, hDoc2.value, SDOC12);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error for doc12:|" + tmpRes + "|");
				pass = false;
			}
			tmpRes = AttachmentHelper.validateAttachmentData(doc3, hDoc3.value, SDOC13);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error for doc13:|" + tmpRes + "|");
				pass = false;
			}
			tmpRes = AttachmentHelper.validateAttachmentData(doc4, hDoc4.value, SDOC14);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error for doc14:|" + tmpRes + "|");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "All received documents are as expected (ok)");

		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientEnabledServerEnabledMTOMInOutTest failed");
	}

	/*
	 * @testName: ClientEnabledServerEnabledMTOMOutTest
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * WS4EE:SPEC:5006; JAXWS:JAVADOC:192; JAXWS:SPEC:7021; JAXWS:SPEC:7021.1;
	 *
	 * @test_Strategy: Test MTOM attachments on output.
	 */
	@Test
	public void ClientEnabledServerEnabledMTOMOutTest() throws Exception {
		logger.log(Level.INFO, "ClientEnabledServerEnabledMTOMOutTest");
		boolean pass = true;

		try {

			StreamSource doc1 = AttachmentHelper.getSourceDoc(docURL1);
			StreamSource doc2 = AttachmentHelper.getSourceDoc(docURL2);
			DataHandler doc3 = AttachmentHelper.getDataHandlerDoc(docURL3);
			Image doc4 = AttachmentHelper.getImageDoc(docURL4);

			String urls = docURL1.toString() + "," + docURL2.toString() + "," + docURL3.toString() + ","
					+ docURL4.toString();
			TestUtil.logTrace("urls=" + urls);
			logger.log(Level.INFO, "Receive 4 documents using MTOM via webservice method mtomOut()");
			logger.log(Level.INFO, "Documents to receive: [" + SDOC1 + "," + SDOC2 + "," + SDOC3 + "," + SDOC4 + "]");
			DataType data = port1.mtomOut(urls);
			logger.log(Level.INFO, "Verify the contents of the received documents");
			String tmpRes = AttachmentHelper.validateAttachmentData(doc1, data.getDoc1(), SDOC1);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error doc1:|" + tmpRes + "|");
				pass = false;
			}
			tmpRes = AttachmentHelper.validateAttachmentData(doc2, data.getDoc2(), SDOC2);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error doc2:|" + tmpRes + "|");
				pass = false;
			}
			tmpRes = AttachmentHelper.validateAttachmentData(doc3, data.getDoc3(), SDOC3);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error doc3:|" + tmpRes + "|");
				pass = false;
			}
			tmpRes = AttachmentHelper.validateAttachmentData(doc4, data.getDoc4(), SDOC4);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error doc4:|" + tmpRes + "|");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "All received documents are as expected (ok)");
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientEnabledServerEnabledMTOMOutTest failed");
	}

	/*
	 * @testName: ClientEnabledServerEnabledMTOMOut2Test
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * WS4EE:SPEC:5006; JAXWS:JAVADOC:192; JAXWS:SPEC:7021; JAXWS:SPEC:7021.1;
	 *
	 * @test_Strategy: Test MTOM attachments on output.
	 */
	@Test
	public void ClientEnabledServerEnabledMTOMOut2Test() throws Exception {
		logger.log(Level.INFO, "ClientEnabledServerEnabledMTOMOut2Test");
		boolean pass = true;

		try {

			Image doc = AttachmentHelper.getImageDoc(docURLSmallJpeg);
			String urls = docURLSmallJpeg.toString();
			logger.log(Level.INFO, "urls=" + urls);
			logger.log(Level.INFO, "Receive 1 document using MTOM via webservice method mtomOut2()");
			logger.log(Level.INFO, "Document to receive: [" + SDOCSmallJpeg + "]");
			DataType3 data = port1.mtomOut2(urls);
			logger.log(Level.INFO, "Verify the content of the received document");
			String tmpRes = AttachmentHelper.validateAttachmentData(doc, data.getDoc(), SDOCSmallJpeg);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error:|" + tmpRes + "|");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "The received document is as expected (ok)");
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientEnabledServerEnabledMTOMOut2Test failed");
	}

	/*
	 * @testName: ClientEnabledServerDisabledMTOMInTest
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * WS4EE:SPEC:5006; JAXWS:JAVADOC:192; JAXWS:SPEC:7021; JAXWS:SPEC:7021.1;
	 *
	 * @test_Strategy: Test MTOM attachments on input.
	 */
	@Test
	public void ClientEnabledServerDisabledMTOMInTest() throws Exception {
		logger.log(Level.INFO, "ClientEnabledServerDisabledMTOMInTest");
		boolean pass = true;

		try {
			DataType data = new DataType();

			data.setDocName1(SDOC1);
			data.setDocName2(SDOC2);
			data.setDocName3(SDOC3);
			data.setDocName4(SDOC4);

			data.setDocUrl1(docURL1.toString());
			data.setDocUrl2(docURL2.toString());
			data.setDocUrl3(docURL3.toString());
			data.setDocUrl4(docURL4.toString());

			StreamSource doc1 = AttachmentHelper.getSourceDoc(docURL1);
			StreamSource doc2 = AttachmentHelper.getSourceDoc(docURL2);
			DataHandler doc3 = AttachmentHelper.getDataHandlerDoc(docURL3);
			Image doc4 = AttachmentHelper.getImageDoc(docURL4);

			data.setDoc1(doc1);
			data.setDoc2(doc2);
			data.setDoc3(doc3);
			data.setDoc4(doc4);

			TestUtil.logMsg("Send 4 documents using MTOM via webservice method mtomIn()");
			logger.log(Level.INFO, "Documents to send: [" + SDOC1 + "," + SDOC2 + "," + SDOC3 + "," + SDOC4 + "]");
			String result = port2.mtomIn(data);
			if (!result.equals("")) {
				TestUtil.logErr("An error occurred with one or more of the attachments");
				TestUtil.logErr("result=" + result);
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientEnabledServerDisabledMTOMInTest failed");
	}

	/*
	 * @testName: ClientEnabledServerDisabledMTOMInOutTest
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * WS4EE:SPEC:5006; JAXWS:JAVADOC:192; JAXWS:SPEC:7021; JAXWS:SPEC:7021.1;
	 *
	 * @test_Strategy: Test MTOM attachments on input and output.
	 */
	@Test
	public void ClientEnabledServerDisabledMTOMInOutTest() throws Exception {
		logger.log(Level.INFO, "ClientEnabledServerDisabledMTOMInOutTest");
		boolean pass = true;

		try {
			Holder<String> hDocName1 = new Holder<String>(SDOC1);
			Holder<String> hDocName2 = new Holder<String>(SDOC2);
			Holder<String> hDocName3 = new Holder<String>(SDOC3);
			Holder<String> hDocName4 = new Holder<String>(SDOC4);

			Holder<String> hDocUrl1 = new Holder<String>(docURL1.toString());
			Holder<String> hDocUrl2 = new Holder<String>(docURL2.toString());
			Holder<String> hDocUrl3 = new Holder<String>(docURL3.toString());
			Holder<String> hDocUrl4 = new Holder<String>(docURL4.toString());
			Holder<String> hDocUrl11 = new Holder<String>(docURL11.toString());
			Holder<String> hDocUrl12 = new Holder<String>(docURL12.toString());
			Holder<String> hDocUrl13 = new Holder<String>(docURL13.toString());
			Holder<String> hDocUrl14 = new Holder<String>(docURL14.toString());

			StreamSource doc1 = AttachmentHelper.getSourceDoc(docURL1);
			StreamSource doc2 = AttachmentHelper.getSourceDoc(docURL2);
			DataHandler doc3 = AttachmentHelper.getDataHandlerDoc(docURL3);
			Image doc4 = AttachmentHelper.getImageDoc(docURL4);

			Holder<Source> hDoc1 = new Holder<Source>(doc1);
			Holder<Source> hDoc2 = new Holder<Source>(doc2);
			Holder<DataHandler> hDoc3 = new Holder<DataHandler>(doc3);
			Holder<Image> hDoc4 = new Holder<Image>(doc4);
			Holder<String> hResult = new Holder<String>("");
			logger.log(Level.INFO, "Send and receieve 4 documents using MTOM via webservice method mtomInOut()");
			logger.log(Level.INFO, "Documents to send: [" + SDOC1 + "," + SDOC2 + "," + SDOC3 + "," + SDOC4 + "]");
			logger.log(Level.INFO,
					"Documents to receive: [" + SDOC11 + "," + SDOC12 + "," + SDOC13 + "," + SDOC14 + "]");
			port2.mtomInOut(hDocName1, hDocName2, hDocName3, hDocName4, hDocUrl1, hDocUrl2, hDocUrl3, hDocUrl4,
					hDocUrl11, hDocUrl12, hDocUrl13, hDocUrl14, hDoc1, hDoc2, hDoc3, hDoc4, hResult);
			if (!(hResult.value).equals("")) {
				TestUtil.logErr("Server-side errors occurred:\n" + hResult.value);
				pass = false;
			}
			logger.log(Level.INFO, "Verify the contents of the received documents");

			doc1 = AttachmentHelper.getSourceDoc(docURL11);
			doc2 = AttachmentHelper.getSourceDoc(docURL12);
			doc3 = AttachmentHelper.getDataHandlerDoc(docURL13);
			doc4 = AttachmentHelper.getImageDoc(docURL14);

			// Now test the documents that were sent back by Server
			String tmpRes = AttachmentHelper.validateAttachmentData(doc1, hDoc1.value, SDOC11);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error doc11:|" + tmpRes + "|");
				pass = false;
			}
			tmpRes = AttachmentHelper.validateAttachmentData(doc2, hDoc2.value, SDOC12);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error doc12:|" + tmpRes + "|");
				pass = false;
			}
			tmpRes = AttachmentHelper.validateAttachmentData(doc3, hDoc3.value, SDOC13);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error doc13:|" + tmpRes + "|");
				pass = false;
			}
			tmpRes = AttachmentHelper.validateAttachmentData(doc4, hDoc4.value, SDOC14);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error doc14:|" + tmpRes + "|");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "All received documents are as expected (ok)");

		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientEnabledServerDisabledMTOMInOutTest failed");
	}

	/*
	 * @testName: ClientEnabledServerDisabledMTOMOutTest
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * WS4EE:SPEC:5006; JAXWS:JAVADOC:192; JAXWS:SPEC:7021; JAXWS:SPEC:7021.1;
	 *
	 * @test_Strategy: Test MTOM attachments on output.
	 */
	@Test
	public void ClientEnabledServerDisabledMTOMOutTest() throws Exception {
		logger.log(Level.INFO, "ClientEnabledServerDisabledMTOMOutTest");
		boolean pass = true;

		try {

			StreamSource doc1 = AttachmentHelper.getSourceDoc(docURL1);
			StreamSource doc2 = AttachmentHelper.getSourceDoc(docURL2);
			DataHandler doc3 = AttachmentHelper.getDataHandlerDoc(docURL3);
			Image doc4 = AttachmentHelper.getImageDoc(docURL4);

			String urls = docURL1.toString() + "," + docURL2.toString() + "," + docURL3.toString() + ","
					+ docURL4.toString();
			TestUtil.logTrace("urls=" + urls);
			logger.log(Level.INFO, "Receive 4 documents using MTOM via webservice method mtomOut()");
			logger.log(Level.INFO, "Documents to receive: [" + SDOC1 + "," + SDOC2 + "," + SDOC3 + "," + SDOC4 + "]");
			DataType data = port2.mtomOut(urls);
			logger.log(Level.INFO, "Verify the contents of the received documents");
			String tmpRes = AttachmentHelper.validateAttachmentData(doc1, data.getDoc1(), SDOC1);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error doc1:|" + tmpRes + "|");
				pass = false;
			}
			tmpRes = AttachmentHelper.validateAttachmentData(doc2, data.getDoc2(), SDOC2);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error doc2:|" + tmpRes + "|");
				pass = false;
			}
			tmpRes = AttachmentHelper.validateAttachmentData(doc3, data.getDoc3(), SDOC3);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error doc3:|" + tmpRes + "|");
				pass = false;
			}
			tmpRes = AttachmentHelper.validateAttachmentData(doc4, data.getDoc4(), SDOC4);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error doc4:|" + tmpRes + "|");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "All received documents are as expected (ok)");
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientEnabledServerDisabledMTOMOutTest failed");
	}

	/*
	 * @testName: ClientEnabledServerDisabledMTOMOut2Test
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * WS4EE:SPEC:5006; JAXWS:JAVADOC:192; JAXWS:SPEC:7021; JAXWS:SPEC:7021.1;
	 *
	 * @test_Strategy: Test MTOM attachments on output.
	 */
	@Test
	public void ClientEnabledServerDisabledMTOMOut2Test() throws Exception {
		logger.log(Level.INFO, "ClientEnabledServerDisabledMTOMOut2Test");
		boolean pass = true;

		try {

			Image doc = AttachmentHelper.getImageDoc(docURLSmallJpeg);
			String urls = docURLSmallJpeg.toString();
			logger.log(Level.INFO, "urls=" + urls);
			logger.log(Level.INFO, "Receive 1 document using MTOM via webservice method mtomOut2()");
			logger.log(Level.INFO, "Document to receive: [" + SDOCSmallJpeg + "]");
			DataType3 data = port2.mtomOut2(urls);
			logger.log(Level.INFO, "Verify the content of the received document");
			String tmpRes = AttachmentHelper.validateAttachmentData(doc, data.getDoc(), SDOCSmallJpeg);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error:|" + tmpRes + "|");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "The received document is as expected (ok)");
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientEnabledServerDisabledMTOMOut2Test failed");
	}

	/*
	 * @testName: ClientEnabledServerEnabledLT2000Test
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * JAXWS:SPEC:6015.4; JAXWS:SPEC:6015.5; JAXWS:JAVADOC:192; JAXWS:JAVADOC:193;
	 * JAXWS:SPEC:7021; JAXWS:SPEC:7021.1; JAXWS:SPEC:7021.2;
	 *
	 * @test_Strategy: Test that XOP is enabled when sending an attachment that is
	 * less than 2000 bytes. The endpoint has mtom enabled with the threshold set to
	 * 2000
	 */
	@Test
	public void ClientEnabledServerEnabledLT2000Test() throws Exception {
		logger.log(Level.INFO, "ClientEnabledServerEnabledLT2000Test");
		boolean pass = true;

		try {
			DataType3 d = new DataType3();
			d.setTestName("ClientEnabledServerEnabledLT2000Test");
			Image doc = AttachmentHelper.getImageDoc(docURLSmallJpeg);
			d.setDoc(doc);

			logger.log(Level.INFO, "Sending a jpg document that is below the threshold [" + SDOCSmallJpeg + "]");
			port3_1.threshold2000(d);

			logger.log(Level.INFO, "Verify the content of the received document");
			String tmpRes = AttachmentHelper.validateAttachmentData(doc, d.getDoc(), SDOCSmallJpeg);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error:|" + tmpRes + "|");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "The received document is as expected (ok)");
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientEnabledServerEnabledGT2000Test failed");
	}

	/*
	 * @testName: ClientEnabledServerEnabledGT2000Test
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * JAXWS:SPEC:6015.4; JAXWS:SPEC:6015.5; JAXWS:JAVADOC:192; JAXWS:JAVADOC:193;
	 * JAXWS:SPEC:7021; JAXWS:SPEC:7021.1; JAXWS:SPEC:7021.2;
	 *
	 * @test_Strategy: Test that XOP is enabled when sending an attachment that is
	 * greater than 2000 bytes. The endpoint has mtom enabled with the threshold set
	 * to 2000
	 */
	@Test
	public void ClientEnabledServerEnabledGT2000Test() throws Exception {
		logger.log(Level.INFO, "ClientEnabledServerEnabledGT2000Test");
		boolean pass = true;

		try {
			DataType3 d = new DataType3();
			d.setTestName("ClientEnabledServerEnabledGT2000Test");
			Image doc = AttachmentHelper.getImageDoc(docURLBigJpeg);
			d.setDoc(doc);

			logger.log(Level.INFO, "Sending a jpg document that is above the threshold [" + SDOCBigJpeg + "]");
			port3_1.threshold2000(d);

			logger.log(Level.INFO, "Verify the content of the received document");
			String tmpRes = AttachmentHelper.validateAttachmentData(doc, d.getDoc(), SDOCBigJpeg);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error:|" + tmpRes + "|");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "The received document is as expected (ok)");
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientEnabledServerEnabledGT2000Test failed");
	}

	/*
	 * @testName: ClientDisabledServerEnabledLT2000Test
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * JAXWS:SPEC:6015.4; JAXWS:SPEC:6015.5; JAXWS:JAVADOC:192; JAXWS:JAVADOC:193;
	 * JAXWS:SPEC:7021; JAXWS:SPEC:7021.1; JAXWS:SPEC:7021.2;
	 *
	 * @test_Strategy: Test that XOP is disabled when sending an attachment that is
	 * less than 2000 bytes. The endpoint has mtom enabled with the threshold set to
	 * 2000
	 */
	@Test
	public void ClientDisabledServerEnabledLT2000Test() throws Exception {
		logger.log(Level.INFO, "ClientDisabledServerEnabledLT2000Test");
		boolean pass = true;

		try {
			DataType3 d = new DataType3();
			d.setTestName("ClientDisabledServerEnabledLT2000Test");
			Image doc = AttachmentHelper.getImageDoc(docURLSmallJpeg);
			d.setDoc(doc);

			logger.log(Level.INFO, "Sending a jpg document that is below the threshold [" + SDOCSmallJpeg + "]");
			port3_2.threshold2000(d);

			logger.log(Level.INFO, "Verify the content of the received document");
			String tmpRes = AttachmentHelper.validateAttachmentData(doc, d.getDoc(), SDOCSmallJpeg);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error:|" + tmpRes + "|");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "The received document is as expected (ok)");
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientDisabledServerEnabledLT2000Test failed");
	}

	/*
	 * @testName: ClientDisabledServerEnabledGT2000Test
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * JAXWS:SPEC:6015.4; JAXWS:SPEC:6015.5; JAXWS:JAVADOC:192; JAXWS:JAVADOC:193;
	 * JAXWS:SPEC:7021; JAXWS:SPEC:7021.1; JAXWS:SPEC:7021.2;
	 *
	 * @test_Strategy: Test that XOP is disabled when sending an attachment that is
	 * greater than 2000 bytes. The endpoint has mtom enabled with the threshold set
	 * to 2000
	 */
	@Test
	public void ClientDisabledServerEnabledGT2000Test() throws Exception {
		logger.log(Level.INFO, "ClientDisabledServerEnabledGT2000Test");
		boolean pass = true;

		try {
			DataType3 d = new DataType3();
			d.setTestName("ClientDisabledServerEnabledGT2000Test");
			Image doc = AttachmentHelper.getImageDoc(docURLBigJpeg);
			d.setDoc(doc);

			logger.log(Level.INFO, "Sending a jpg document that is above the threshold [" + SDOCBigJpeg + "]");
			port3_2.threshold2000(d);

			logger.log(Level.INFO, "Verify the content of the received document");
			String tmpRes = AttachmentHelper.validateAttachmentData(doc, d.getDoc(), SDOCBigJpeg);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error:|" + tmpRes + "|");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "The received document is as expected (ok)");
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientDisabledServerEnabledGT2000Test failed");
	}

	/*
	 * @testName: ClientEnabledServerDisabledLT2000Test
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * JAXWS:SPEC:6015.4; JAXWS:SPEC:6015.5; JAXWS:JAVADOC:192; JAXWS:JAVADOC:193;
	 * JAXWS:SPEC:7021; JAXWS:SPEC:7021.1; JAXWS:SPEC:7021.2;
	 *
	 * @test_Strategy: Test that XOP is enabled when sending an attachment that is
	 * less than 2000 bytes. The endpoint has mtom disabled with the threshold set
	 * to 2000
	 */
	@Test
	public void ClientEnabledServerDisabledLT2000Test() throws Exception {
		logger.log(Level.INFO, "ClientEnabledServerDisabledLT2000Test");
		boolean pass = true;

		try {
			DataType3 d = new DataType3();
			d.setTestName("ClientEnabledServerDisabledLT2000Test");
			Image doc = AttachmentHelper.getImageDoc(docURLSmallJpeg);
			d.setDoc(doc);

			logger.log(Level.INFO, "Sending a jpg document that is below the threshold [" + SDOCSmallJpeg + "]");
			port4_1.threshold2000(d);

			logger.log(Level.INFO, "Verify the content of the received document");
			String tmpRes = AttachmentHelper.validateAttachmentData(doc, d.getDoc(), SDOCSmallJpeg);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error:|" + tmpRes + "|");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "The received document is as expected (ok)");
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientEnabledServerDisabledLT2000Test failed");
	}

	/*
	 * @testName: ClientEnabledServerDisabledGT2000Test
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * JAXWS:SPEC:6015.4; JAXWS:SPEC:6015.5; JAXWS:JAVADOC:192; JAXWS:JAVADOC:193;
	 * JAXWS:SPEC:7021; JAXWS:SPEC:7021.1; JAXWS:SPEC:7021.2;
	 *
	 * @test_Strategy: Test that XOP is enabled when sending an attachment that is
	 * greater than 2000 bytes. The endpoint has mtom disabled with the threshold
	 * set to 2000
	 */
	@Test
	public void ClientEnabledServerDisabledGT2000Test() throws Exception {
		logger.log(Level.INFO, "ClientEnabledServerDisabledGT2000Test");
		boolean pass = true;

		try {
			DataType3 d = new DataType3();
			d.setTestName("ClientEnabledServerDisabledGT2000Test");
			Image doc = AttachmentHelper.getImageDoc(docURLBigJpeg);
			d.setDoc(doc);

			logger.log(Level.INFO, "Sending a jpg document that is above the threshold [" + SDOCBigJpeg + "]");
			port4_1.threshold2000(d);

			logger.log(Level.INFO, "Verify the content of the received document");
			String tmpRes = AttachmentHelper.validateAttachmentData(doc, d.getDoc(), SDOCBigJpeg);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error:|" + tmpRes + "|");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "The received document is as expected (ok)");
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientEnabledServerDisabledGT2000Test failed");
	}

	/*
	 * @testName: ClientDisabledServerDisabledLT2000Test
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * JAXWS:SPEC:6015.4; JAXWS:SPEC:6015.5; JAXWS:JAVADOC:192; JAXWS:JAVADOC:193;
	 * JAXWS:SPEC:7021; JAXWS:SPEC:7021.1; JAXWS:SPEC:7021.2;
	 *
	 * @test_Strategy: Test that XOP is enabled when sending an attachment that is
	 * less than 2000 bytes. The endpoint has mtom disabled with the threshold set
	 * to 2000
	 */
	@Test
	public void ClientDisabledServerDisabledLT2000Test() throws Exception {
		logger.log(Level.INFO, "ClientDisabledServerDisabledLT2000Test");
		boolean pass = true;

		try {
			DataType3 d = new DataType3();
			d.setTestName("ClientDisabledServerDisabledLT2000Test");
			Image doc = AttachmentHelper.getImageDoc(docURLSmallJpeg);
			d.setDoc(doc);

			logger.log(Level.INFO, "Sending a jpg document that is below the threshold [" + SDOCSmallJpeg + "]");
			port4_2.threshold2000(d);

			logger.log(Level.INFO, "Verify the content of the received document");
			String tmpRes = AttachmentHelper.validateAttachmentData(doc, d.getDoc(), SDOCSmallJpeg);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error:|" + tmpRes + "|");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "The received document is as expected (ok)");
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientDisabledServerDisabledLT2000Test failed");
	}

	/*
	 * @testName: ClientDisabledServerDisabledGT2000Test
	 *
	 * @assertion_ids: JAXWS:SPEC:6015; JAXWS:SPEC:6015.2; JAXWS:SPEC:6015.3;
	 * JAXWS:SPEC:6015.4; JAXWS:SPEC:6015.5; JAXWS:JAVADOC:192; JAXWS:JAVADOC:193;
	 * JAXWS:SPEC:7021; JAXWS:SPEC:7021.1; JAXWS:SPEC:7021.2;
	 *
	 * @test_Strategy: Test that XOP is enabled when sending an attachment that is
	 * less than 2000 bytes. The endpoint has mtom disabled with the threshold set
	 * to 2000
	 */
	@Test
	public void ClientDisabledServerDisabledGT2000Test() throws Exception {
		logger.log(Level.INFO, "ClientDisabledServerDisabledGT2000Test");
		boolean pass = true;

		try {
			DataType3 d = new DataType3();
			d.setTestName("ClientDisabledServerDisabledGT2000Test");
			Image doc = AttachmentHelper.getImageDoc(docURLBigJpeg);
			d.setDoc(doc);

			logger.log(Level.INFO, "Sending a jpg document that is above the threshold [" + SDOCBigJpeg + "]");
			port4_2.threshold2000(d);

			logger.log(Level.INFO, "Verify the content of the received document");
			String tmpRes = AttachmentHelper.validateAttachmentData(doc, d.getDoc(), SDOCBigJpeg);
			if (tmpRes != null) {
				TestUtil.logErr("Client-side error:|" + tmpRes + "|");
				pass = false;
			}
			if (pass)
				logger.log(Level.INFO, "The received document is as expected (ok)");
		} catch (Exception e) {
			TestUtil.logErr("Exception occurred");
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("ClientDisabledServerDisabledGT2000Test failed");
	}

}
