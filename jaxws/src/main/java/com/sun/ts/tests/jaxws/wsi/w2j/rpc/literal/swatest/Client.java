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

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.swatest;

import java.awt.Image;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.nio.charset.Charset;

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
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.wsi.requests.SOAPRequests;

import jakarta.activation.DataHandler;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.Holder;

public class Client extends BaseClient implements SOAPRequests {

	// URL properties used by the test
	private static final String ENDPOINT_URL = "wsirlswatest.endpoint.1";

	private static final String ENDPOINT_URL2 = "wsirlswatest.endpoint.2";

	private static final String WSDLLOC_URL = "wsirlswatest.wsdlloc.1";

	private static final String CTXROOT = "wsirlswatest.ctxroot.1";

	private String surl = null;

	private String file = null;

	private String surl2 = null;

	private String file2 = null;

	private String ctxroot = null;

	private URL wsdlurl = null;

	private static final String NAMESPACEURI = "http://SwaTestService.org/wsdl";

	private static final String SERVICE_NAME = "WSIRLSwaTestService";

	private static final String PORT_NAME = "SwaTestOnePort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private DataHandler dh1 = null;

	private DataHandler dh2 = null;

	private DataHandler dh3 = null;

	private DataHandler dh4 = null;

	private DataHandler dh5 = null;

	private DataHandler dh6 = null;

	private DataHandler dh7 = null;

	private URL url1 = null;

	private URL url2 = null;

	private URL url3 = null;

	private URL url4 = null;

	private URL url5 = null;

	private URL url6 = null;

	private URL url7 = null;

	static WSIRLSwaTestService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	/***********************************************************************
	 * All the test cases in this file test all of the assertions specified in the
	 * WSI Attachment Profile 1.0 specification.
	 **********************************************************************/
	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		file = JAXWS_Util.getURLFromProp(ENDPOINT_URL);
		surl = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC_URL);
		wsdlurl = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		file2 = JAXWS_Util.getURLFromProp(ENDPOINT_URL2);
		surl2 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file2);
		ctxroot = JAXWS_Util.getURLFromProp(CTXROOT);
		logger.log(Level.INFO, "Service Endpoint URL: " + surl);
		logger.log(Level.INFO, "Service Endpoint URL2: " + surl2);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
		logger.log(Level.INFO, "Context Root:         " + ctxroot);
	}

	SwaTest1 port = null;

	private SwaTestClient1 client1;

	private SwaTestClient2 client2;

	protected void getPortStandalone() throws Exception {
		port = (SwaTest1) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, WSIRLSwaTestService.class, PORT_QNAME,
				SwaTest1.class);
		JAXWS_Util.setTargetEndpointAddress(port, surl);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		logger.log(Level.INFO, "Get port from Service");
		port = (SwaTest1) service.getPort(com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.swatest.SwaTest1.class);
		logger.log(Level.INFO, "Port obtained");
		JAXWS_Util.dumpTargetEndpointAddress(port);
	}

	protected void getService() {
		service = (WSIRLSwaTestService) getSharedObject();
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
		boolean pass = true;

		client1 = (SwaTestClient1) ClientFactory.getClient(SwaTestClient1.class, service);
		client2 = (SwaTestClient2) ClientFactory.getClient(SwaTestClient2.class, service);
		if (!modeProperty.equals("standalone")) {
			ctxroot = JAXWS_Util.getURLFromProp(CTXROOT);
		}
		logger.log(Level.INFO, "Create URL's to attachments");
		url1 = ctsurl.getURL("http", hostname, portnum, ctxroot + "/attach.txt");
		url2 = ctsurl.getURL("http", hostname, portnum, ctxroot + "/attach.html");
		url3 = ctsurl.getURL("http", hostname, portnum, ctxroot + "/attach.xml");
		url4 = ctsurl.getURL("http", hostname, portnum, ctxroot + "/attach.jpeg");
		url5 = ctsurl.getURL("http", hostname, portnum, ctxroot + "/attach2.jpeg");
		url6 = ctsurl.getURL("http", hostname, portnum, ctxroot + "/attach2.xml");
		url7 = ctsurl.getURL("http", hostname, portnum, ctxroot + "/attach.gif");
		logger.log(Level.INFO, "url1=" + url1);
		logger.log(Level.INFO, "url2=" + url2);
		logger.log(Level.INFO, "url3=" + url3);
		logger.log(Level.INFO, "url4=" + url4);
		logger.log(Level.INFO, "url5=" + url5);
		logger.log(Level.INFO, "url6=" + url6);
		logger.log(Level.INFO, "url7=" + url7);
		logger.log(Level.INFO, "Create DataHandler's to attachments");
		dh1 = new DataHandler(url1);
		dh2 = new DataHandler(url2);
		dh3 = new DataHandler(url3);
		dh4 = new DataHandler(javax.imageio.ImageIO.read(url4), "image/jpeg");
		dh5 = new DataHandler(javax.imageio.ImageIO.read(url5), "image/jpeg");
		dh6 = new DataHandler(url6);
		dh7 = new DataHandler(javax.imageio.ImageIO.read(url7), "image/gif");
		logger.log(Level.INFO, "dh1.getContentType()=" + dh1.getContentType());
		logger.log(Level.INFO, "dh2.getContentType()=" + dh2.getContentType());
		logger.log(Level.INFO, "dh3.getContentType()=" + dh3.getContentType());
		logger.log(Level.INFO, "dh4.getContentType()=" + dh4.getContentType());
		logger.log(Level.INFO, "dh5.getContentType()=" + dh5.getContentType());
		logger.log(Level.INFO, "dh6.getContentType()=" + dh6.getContentType());
		logger.log(Level.INFO, "dh7.getContentType()=" + dh7.getContentType());
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: GetMultipleAttachmentsTest
	 *
	 * @assertion_ids: WSI:SPEC:R2901; WSI:SPEC:R2907; WSI:SPEC:R2909;
	 * WSI:SPEC:R2910; WSI:SPEC:R2911; WSI:SPEC:R2931; WSI:SPEC:R2921;
	 * WSI:SPEC:R2926; WSI:SPEC:R2929; WSI:SPEC:R2946; JAXWS:SPEC:10011;
	 * WSI:SPEC:R9801; JAXWS:SPEC:2051; JAXWS:SPEC:2052; JAXWS:SPEC:2053;
	 *
	 * @test_Strategy: Get multiple attachments. Multiple attachments should be
	 * returned in the soap response.
	 *
	 */
	@Test
	public void GetMultipleAttachmentsTest() throws Exception {
		logger.log(Level.INFO, "GetMultipleAttachmentsTest");
		boolean pass = true;

		try {
			InputRequestGet request = new InputRequestGet();
			request.setMimeType1("text/plain");
			request.setMimeType2("text/html");
			request.setUrl1(url1.toString());
			request.setUrl2(url2.toString());
			logger.log(Level.INFO, "Get 2 attachments (text/plain) and (text/html)");
			Holder<jakarta.activation.DataHandler> attach1 = new Holder<jakarta.activation.DataHandler>();
			Holder<jakarta.activation.DataHandler> attach2 = new Holder<jakarta.activation.DataHandler>();
			Holder<OutputResponse> response = new Holder<OutputResponse>();
			port.getMultipleAttachments(request, response, attach1, attach2);
			if (!ValidateRequestResponseAttachmentsGetTestCase(request, response.value, attach1, attach2))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("GetMultipleAttachmentsTest failed", e);
		}
		if (!pass)
			throw new Exception("GetMultipleAttachmentsTest failed");
	}

	/*
	 * @testName: PutMultipleAttachmentsTest
	 *
	 * @assertion_ids: WSI:SPEC:R2901; WSI:SPEC:R2907; WSI:SPEC:R2909;
	 * WSI:SPEC:R2910; WSI:SPEC:R2911; WSI:SPEC:R2931; WSI:SPEC:R2921;
	 * WSI:SPEC:R2926; WSI:SPEC:R2929; WSI:SPEC:R2946; JAXWS:SPEC:10011;
	 * WSI:SPEC:R9801; JAXWS:SPEC:2051; JAXWS:SPEC:2052; JAXWS:SPEC:2053;
	 *
	 * @test_Strategy: Put multiple attachments. Multiple attachments should be sent
	 * in the soap request and a status result is returned in the soap response.
	 *
	 */
	@Test
	public void PutMultipleAttachmentsTest() throws Exception {
		logger.log(Level.INFO, "PutMultipleAttachmentsTest");
		boolean pass = true;

		try {
			InputRequestPut request = new InputRequestPut();
			request.setMimeType1("text/plain");
			request.setMimeType2("text/html");
			request.setHeader("notused");
			DataHandler attach1 = dh1;
			DataHandler attach2 = dh2;
			logger.log(Level.INFO, "Put 2 attachments (text/plain) and (text/html)");
			String response = port.putMultipleAttachments(request, attach1, attach2);
			if (!response.equals("ok")) {
				TestUtil.logErr("Return status is " + response + ", expected ok");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("PutMultipleAttachmentsTest failed", e);
		}

		if (!pass)
			throw new Exception("PutMultipleAttachmentsTest failed");
	}

	/*
	 * @testName: EchoMultipleAttachmentsTest
	 *
	 * @assertion_ids: WSI:SPEC:R2901; WSI:SPEC:R2907; WSI:SPEC:R2909;
	 * WSI:SPEC:R2910; WSI:SPEC:R2911; WSI:SPEC:R2931; WSI:SPEC:R2921;
	 * WSI:SPEC:R2926; WSI:SPEC:R2929; WSI:SPEC:R2946; JAXWS:SPEC:10011;
	 * WSI:SPEC:R9801; JAXWS:SPEC:2051; JAXWS:SPEC:2052; JAXWS:SPEC:2053;
	 *
	 * @test_Strategy: Echo multiple attachments. Multiple attachments should be
	 * sent in the soap request and returned in the soap response.
	 *
	 */
	@Test
	public void EchoMultipleAttachmentsTest() throws Exception {
		logger.log(Level.INFO, "EchoMultipleAttachmentsTest");
		boolean pass = true;

		try {
			InputRequest request = new InputRequest();
			request.setMimeType1("text/plain");
			request.setMimeType2("text/html");
			Holder<jakarta.activation.DataHandler> attach1 = new Holder<jakarta.activation.DataHandler>();
			Holder<jakarta.activation.DataHandler> attach2 = new Holder<jakarta.activation.DataHandler>();
			attach1.value = dh1;
			attach2.value = dh2;
			logger.log(Level.INFO, "Echo 2 attachments (text/plain) and (text/html)");
			OutputResponse response = port.echoMultipleAttachments(request, attach1, attach2);
			if (!ValidateRequestResponseAttachmentsEchoTestCase(request, response, attach1, attach2))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("EchoMultipleAttachmentsTest failed", e);
		}

		if (!pass)
			throw new Exception("EchoMultipleAttachmentsTest failed");
	}

	/*
	 * @testName: EchoGifImageTypeTest
	 *
	 * @assertion_ids: WSI:SPEC:R2901; WSI:SPEC:R2907; WSI:SPEC:R2909;
	 * WSI:SPEC:R2910; WSI:SPEC:R2911; WSI:SPEC:R2931; WSI:SPEC:R2921;
	 * WSI:SPEC:R2926; WSI:SPEC:R2929; WSI:SPEC:R2946; JAXWS:SPEC:10011;
	 * WSI:SPEC:R9801; JAXWS:SPEC:2051; JAXWS:SPEC:2052; JAXWS:SPEC:2053;
	 *
	 * @test_Strategy: Echo an image/gif attachment. This is a special test case.
	 * Due to the limitations of the platform a JAXWS implementation is required
	 * only to support decoding of images of type image/gif, but not encoding of
	 * them. This test case should throw an exception if the JAXWS implementation
	 * does not support encoding of images of types image/gif and it should pass if
	 * it does support encoding of images of type image/gif.
	 *
	 */
	@Test
	public void EchoGifImageTypeTest() throws Exception {
		logger.log(Level.INFO, "EchoGifImageTypeTest");
		boolean pass = true;

		try {
			VoidRequest request = new VoidRequest();
			logger.log(Level.INFO, "Echo attachment (image/gif)");
			logger.log(Level.INFO,
					"Due to the limitation of the platform, a JAXWS "
							+ "implementation is not\nrequired to encode images of type "
							+ "image/gif. Therefore this test case\nshould throw an exception "
							+ "if the JAXWS implementation does not support\nencoding of images "
							+ "of type image/gif and it should pass if it does\nsupport "
							+ "encoding of images of type image/gif.");
			Holder<java.awt.Image> attach1 = new Holder<java.awt.Image>();
			attach1.value = javax.imageio.ImageIO.read(url7);
			String response = port.echoGifImageType(request, attach1);
			if (!response.equals("ok")) {
				TestUtil.logErr("Return status is " + response + ", expected ok");
				pass = false;
			} else {
				Image image1 = javax.imageio.ImageIO.read(url7);
				Image image2 = attach1.value;
				if (!AttachmentHelper.compareImages(image1, image2, new Rectangle(0, 0, 100, 120), "Attachment1"))
					pass = false;
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "Caught expected exception: " + e.getMessage());
		}

		if (!pass)
			throw new Exception("EchoGifImageTypeTest failed");
	}

	/*
	 * @testName: EchoNoAttachmentsTest
	 *
	 * @assertion_ids: WSI:SPEC:R2917; JAXWS:SPEC:10011; WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Echo no attachments. No attachments should be sent in the
	 * soap request or returned in the soap response.
	 *
	 */
	@Test
	public void EchoNoAttachmentsTest() throws Exception {
		logger.log(Level.INFO, "EchoNoAttachmentsTest");
		boolean pass = true;

		try {
			InputRequestString request = new InputRequestString();
			request.setMyString("Hello");
			logger.log(Level.INFO, "Echo no attachments");
			String response = port.echoNoAttachments(request);
			if (!response.equals(request.getMyString())) {
				TestUtil.logErr("OutputString is not equal to InputString");
				TestUtil.logErr("InputString=" + request.getMyString());
				TestUtil.logErr("OutputString=" + response);
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("EchoNoAttachmentsTest failed", e);
		}

		if (!pass)
			throw new Exception("EchoNoAttachmentsTest failed");
	}

	/*
	 * @testName: EchoAllAttachmentTypesTest
	 *
	 * @assertion_ids: WSI:SPEC:R2901; WSI:SPEC:R2907; WSI:SPEC:R2909;
	 * WSI:SPEC:R2910; WSI:SPEC:R2911; WSI:SPEC:R2931; WSI:SPEC:R2921;
	 * WSI:SPEC:R2926; WSI:SPEC:R2929; WSI:SPEC:R2946; JAXWS:SPEC:10011;
	 * WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Echo all attachment types. Attachments for each supported
	 * mime type is sent in the soap request and then returned in the soap response.
	 * This test sends and returns attachments for all the supported mime types.
	 *
	 */
	@Test
	public void EchoAllAttachmentTypesTest() throws Exception {
		logger.log(Level.INFO, "EchoAllAttachmentTypesTest");
		boolean pass = true;

		try {
			logger.log(Level.INFO, "Echo all attachments types: (text/plain), (text/html), (text/xml), (image/jpeg)");
			Holder<jakarta.activation.DataHandler> attach1 = new Holder<jakarta.activation.DataHandler>();
			attach1.value = dh1;
			Holder<jakarta.activation.DataHandler> attach2 = new Holder<jakarta.activation.DataHandler>();
			attach2.value = dh2;
			Holder<javax.xml.transform.Source> attach3 = new Holder<javax.xml.transform.Source>();
			attach3.value = new StreamSource(dh3.getInputStream());
			Holder<java.awt.Image> attach4 = new Holder<java.awt.Image>();
			Holder<java.awt.Image> attach5 = new Holder<java.awt.Image>();
			attach4.value = javax.imageio.ImageIO.read(url4);
			attach5.value = javax.imageio.ImageIO.read(url5);
			Holder<javax.xml.transform.Source> attach6 = new Holder<javax.xml.transform.Source>();
			attach6.value = new StreamSource(dh6.getInputStream());
			VoidRequest request = new VoidRequest();
			OutputResponseAll response = port.echoAllAttachmentTypes(request, attach1, attach2, attach3, attach4,
					attach5, attach6);
			if (!ValidateRequestResponseAttachmentsEchoAllTestCase(request, response, attach1, attach2, attach3,
					attach4, attach5, attach6))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("EchoAllAttachmentTypesTest failed", e);
		}

		if (!pass)
			throw new Exception("EchoAllAttachmentTypesTest failed");
	}

	/*
	 * @testName: PutAllAttachmentTypesTest
	 *
	 * @assertion_ids: WSI:SPEC:R2901; WSI:SPEC:R2907; WSI:SPEC:R2909;
	 * WSI:SPEC:R2910; WSI:SPEC:R2911; WSI:SPEC:R2931; WSI:SPEC:R2921;
	 * WSI:SPEC:R2926; WSI:SPEC:R2929; WSI:SPEC:R2946; JAXWS:SPEC:10011;
	 * WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Put all attachment types. Attachments for each supported mime
	 * type is sent in the soap request and a status result is returned in the soap
	 * response.
	 */
	@Test
	public void PutAllAttachmentTypesTest() throws Exception {
		logger.log(Level.INFO, "PutAllAttachmentTypesTest");
		boolean pass = true;

		try {
			logger.log(Level.INFO, "Put all attachments types: (text/plain), (text/html), (text/xml), (image/jpeg)");
			DataHandler attach1 = dh1;
			DataHandler attach2 = dh2;
			Source attach3 = new StreamSource(dh3.getInputStream());
			Image attach4 = javax.imageio.ImageIO.read(url4);
			Image attach5 = javax.imageio.ImageIO.read(url5);
			InputRequestPutAll request = new InputRequestPutAll();
			request.setMimeType1("text/plain");
			request.setMimeType2("text/html");
			request.setMimeType3("text/xml");
			request.setMimeType4("image/jpeg");
			request.setMimeType5("image/jpeg");
			request.setMimeType6("text/xml");
			Source attach6 = new StreamSource(dh6.getInputStream());
			String response = port.putAllAttachmentTypes(request, attach1, attach2, attach3, attach4, attach5, attach6);
			if (!response.equals("ok")) {
				TestUtil.logErr("Return status is " + response + ", expected ok");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("PutAllAttachmentTypesTest failed", e);
		}

		if (!pass)
			throw new Exception("PutAllAttachmentTypesTest failed");
	}

	/*
	 * @testName: GetAllAttachmentTypesTest
	 *
	 * @assertion_ids: WSI:SPEC:R2901; WSI:SPEC:R2907; WSI:SPEC:R2909;
	 * WSI:SPEC:R2910; WSI:SPEC:R2911; WSI:SPEC:R2931; WSI:SPEC:R2921;
	 * WSI:SPEC:R2926; WSI:SPEC:R2929; WSI:SPEC:R2946; JAXWS:SPEC:10011;
	 * WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Get all attachment types. Attachments for each supported mime
	 * type should be returned in the soap response.
	 *
	 */
	@Test
	public void GetAllAttachmentTypesTest() throws Exception {
		logger.log(Level.INFO, "GetAllAttachmentTypesTest");
		boolean pass = true;

		try {
			InputRequestGetAll request = new InputRequestGetAll();
			request.setMimeType1("text/plain");
			request.setMimeType2("text/html");
			request.setMimeType3("text/xml");
			request.setMimeType4("image/jpeg");
			request.setMimeType5("image/jpeg");
			request.setMimeType6("text/xml");
			request.setUrl1(url1.toString());
			request.setUrl2(url2.toString());
			request.setUrl3(url3.toString());
			request.setUrl4(url4.toString());
			request.setUrl5(url5.toString());
			request.setUrl6(url6.toString());
			logger.log(Level.INFO, "Get all attachments types: (text/plain), (text/html), (text/xml), (image/jpeg)");
			Holder<jakarta.activation.DataHandler> attach1 = new Holder<jakarta.activation.DataHandler>();
			Holder<jakarta.activation.DataHandler> attach2 = new Holder<jakarta.activation.DataHandler>();
			Holder<javax.xml.transform.Source> attach3 = new Holder<javax.xml.transform.Source>();
			Holder<java.awt.Image> attach4 = new Holder<java.awt.Image>();
			Holder<java.awt.Image> attach5 = new Holder<java.awt.Image>();
			Holder<javax.xml.transform.Source> attach6 = new Holder<javax.xml.transform.Source>();
			Holder<OutputResponseGetAll> response = new Holder<OutputResponseGetAll>();
			port.getAllAttachmentTypes(request, response, attach1, attach2, attach3, attach4, attach5, attach6);
			if (!ValidateRequestResponseAttachmentsGetAllTestCase(request, response.value, attach1, attach2, attach3,
					attach4, attach5, attach6))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("GetAllAttachmentTypesTest failed", e);
		}
		if (!pass)
			throw new Exception("GetAllAttachmentTypesTest failed");
	}

	/*
	 * @testName: EchoAttachmentsAndThrowAFaultTest
	 *
	 * @assertion_ids: WSI:SPEC:R2913; WSI:SPEC:R2920; WSI:SPEC:R2930;
	 * WSI:SPEC:R2946; JAXWS:SPEC:10011; WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Echo attachments and throw back a soap Exception. Multiple
	 * attachments should be sent in the soap request and the endpoint should throw
	 * back a soap Exception.
	 *
	 */
	@Test
	public void EchoAttachmentsAndThrowAFaultTest() throws Exception {
		logger.log(Level.INFO, "EchoAttachmentsAndThrowAFaultTest");
		boolean pass = true;

		try {
			InputRequest request = new InputRequest();
			request.setMimeType1("text/plain");
			request.setMimeType2("text/html");
			Holder<jakarta.activation.DataHandler> attach1 = new Holder<jakarta.activation.DataHandler>();
			Holder<jakarta.activation.DataHandler> attach2 = new Holder<jakarta.activation.DataHandler>();
			attach1.value = dh1;
			attach2.value = dh2;
			logger.log(Level.INFO, "Echo attachments and throw a Exception");
			OutputResponse response = port.echoAttachmentsAndThrowAFault(request, attach1, attach2);
			pass = false;
		} catch (MyFault e) {
			logger.log(Level.INFO, "Caught expected MyFault exception: " + e.getMessage());
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("EchoAttachmentsAndThrowAFaultTest failed", e);
		}

		if (!pass)
			throw new Exception("EchoAttachmentsAndThrowAFaultTest failed");
	}

	/*
	 * @testName: EchoAttachmentsWithHeaderTest
	 *
	 * @assertion_ids: WSI:SPEC:R2905; WSI:SPEC:2906; WSI:SPEC:R2946;
	 * WSI:SPEC:R2946; JAXWS:SPEC:10011; WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Send a header with attachments using the soapbind:header
	 * element which must be a child of the root part mime:part element.
	 *
	 */
	@Test
	public void EchoAttachmentsWithHeaderTest() throws Exception {
		logger.log(Level.INFO, "EchoAttachmentsWithHeaderTest");
		boolean pass = true;

		try {
			InputRequest request = new InputRequest();
			request.setMimeType1("text/plain");
			request.setMimeType2("text/html");
			Holder<jakarta.activation.DataHandler> attach1 = new Holder<jakarta.activation.DataHandler>();
			Holder<jakarta.activation.DataHandler> attach2 = new Holder<jakarta.activation.DataHandler>();
			attach1.value = dh1;
			attach2.value = dh2;
			MyHeader header = new MyHeader();
			header.setMessage("do not throw my header Exception");
			logger.log(Level.INFO, "Echo attachments with a header");
			OutputResponse response = port.echoAttachmentsWithHeader(request, header, attach1, attach2);
			if (!ValidateRequestResponseAttachmentsEchoWithHeaderTestCase(request, response, attach1, attach2))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("EchoAttachmentsWithHeaderTest failed", e);
		}

		if (!pass)
			throw new Exception("EchoAttachmentsWithHeaderTest failed");
	}

	/*
	 * @testName: EchoAttachmentsWithHeaderAndThrowAFaultTest
	 *
	 * @assertion_ids: WSI:SPEC:R2905; WSI:SPEC:2906; WSI:SPEC:2913; WSI:SPEC:R2946;
	 * JAXWS:SPEC:10011; WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Send a header with attachments and throw a Exception back
	 * using the soapbind:fault element.
	 */
	@Test
	public void EchoAttachmentsWithHeaderAndThrowAFaultTest() throws Exception {
		logger.log(Level.INFO, "EchoAttachmentsWithHeaderAndThrowAFaultTest");
		boolean pass = true;

		try {
			InputRequest request = new InputRequest();
			request.setMimeType1("text/plain");
			request.setMimeType2("text/html");
			Holder<jakarta.activation.DataHandler> attach1 = new Holder<jakarta.activation.DataHandler>();
			Holder<jakarta.activation.DataHandler> attach2 = new Holder<jakarta.activation.DataHandler>();
			attach1.value = dh1;
			attach2.value = dh2;
			MyHeader header = new MyHeader();
			header.setMessage("do throw a Exception");
			logger.log(Level.INFO, "Echo attachments with a header and throw a Exception");
			OutputResponse response = port.echoAttachmentsWithHeader(request, header, attach1, attach2);
			pass = false;
		} catch (MyFault e) {
			logger.log(Level.INFO, "Caught expected MyFault exception: " + e.getMessage());
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("EchoAttachmentsWithHeaderAndThrowAFaultTest failed", e);
		}

		if (!pass)
			throw new Exception("EchoAttachmentsWithHeaderAndThrowAFaultTest failed");
	}

	/*
	 * @testName: VerifyPutOfSOAPEnvelopesInAttachmentsTest
	 *
	 * @assertion_ids: WSI:SPEC:R2919; JAXWS:SPEC:10011; WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Put multiple attachments. Multiple attachments should be sent
	 * in the soap request and a status results is returned in the soap response.
	 * The attachments contain SOAP Envelopes.
	 *
	 */
	@Test
	public void VerifyPutOfSOAPEnvelopesInAttachmentsTest() throws Exception {
		logger.log(Level.INFO, "VerifyPutOfSOAPEnvelopesInAttachmentsTest");
		boolean pass = true;

		try {
			InputRequestPut request = new InputRequestPut();
			request.setMimeType1("text/xml");
			request.setMimeType2("text/xml");
			request.setHeader("notused");
			StreamSource xmlSrc1 = new StreamSource(new StringReader(R0007_REQUEST));
			StreamSource xmlSrc2 = new StreamSource(new StringReader(R1011_REQUEST));
			DataHandler attach1 = new DataHandler(xmlSrc1, "text/xml");
			DataHandler attach2 = new DataHandler(xmlSrc2, "text/xml");
			logger.log(Level.INFO, "Put 2 attachments that contain SOAP envelopes");
			String response = port.putMultipleAttachments(request, attach1, attach2);
			if (!response.equals("ok")) {
				TestUtil.logErr("Return status is " + response + ", expected ok");
				pass = false;
			} else
				logger.log(Level.INFO, "Got expected response=ok");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("VerifyPutOfSOAPEnvelopesInAttachmentsTest failed", e);
		}

		if (!pass)
			throw new Exception("VerifyPutOfSOAPEnvelopesInAttachmentsTest failed");
	}

	/*
	 * @testName: VerifyUTF8EncodingOfRootPartWithoutAttachments
	 *
	 * @assertion_ids: WSI:SPEC:R2915; JAXWS:SPEC:10011; WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Test UTF8 encoding of the root part of a multipart/related
	 * message without attachments.
	 *
	 */
	@Test
	public void VerifyUTF8EncodingOfRootPartWithoutAttachments() throws Exception {
		logger.log(Level.INFO, "VerifyUTF8EncodingOfRootPartWithoutAttachments");
		boolean pass = true;
		SOAPMessage request;
		String requestAsString;
		SOAPMessage response;
		StreamSource ssrc;

		try {
			logger.log(Level.INFO, "Construct SOAP RPC request without attachments");
			ssrc = new StreamSource(new ByteArrayInputStream(R2915_UTF8_REQUEST_NO_ATTACHMENTS.getBytes()));
			request = MessageFactory.newInstance().createMessage();
			request.getSOAPPart().setContent(ssrc);
		} catch (Exception e) {
			throw new Exception("Unable to construct SOAP message request (R2915)", e);
		}
		try {
			logger.log(Level.INFO, "Send SOAP RPC request without attachments using UTF8 encoding");
			Charset cs = Charset.forName("UTF-8");
			response = client1.makeSaajRequest(request, cs);
		} catch (Exception e) {
			throw new Exception("Unable to invoke RPC operation (R2915)", e);
		}
		try {
			SOAPBody body = response.getSOAPPart().getEnvelope().getBody();
			if (body.hasFault()) {
				throw new Exception("Unexpected SOAP Exception returned in response (R2915)");
			}
		} catch (SOAPException e) {
			throw new Exception("Invalid SOAP message returned (R2915)", e);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("VerifyUTF8EncodingOfRootPartWithoutAttachments failed", e);
		}

		if (!pass)
			throw new Exception("VerifyUTF8EncodingOfRootPartWithoutAttachments failed");
	}

	/*
	 * @testName: VerifyUTF16EncodingOfRootPartWithoutAttachments
	 *
	 * @assertion_ids: WSI:SPEC:R2915; JAXWS:SPEC:10011; WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Test UTF16 encoding of the root part of a multipart/related
	 * message without attachments.
	 *
	 */
	@Test
	public void VerifyUTF16EncodingOfRootPartWithoutAttachments() throws Exception {
		logger.log(Level.INFO, "VerifyUTF16EncodingOfRootPartWithoutAttachments");
		boolean pass = true;
		SOAPMessage response;

		try {
			logger.log(Level.INFO, "Send SOAP RPC request without attachments using UTF16 encoding");
			Charset cs = Charset.forName("UTF-16");
			response = client1.makeSaajRequest(R2915_UTF16_REQUEST_NO_ATTACHMENTS, cs);
		} catch (Exception e) {
			throw new Exception("Unable to invoke RPC operation (R2915)", e);
		}
		try {
			SOAPBody body = response.getSOAPPart().getEnvelope().getBody();
			if (body.hasFault()) {
				throw new Exception("Unexpected SOAP Exception returned in response (R2915)");
			}
		} catch (SOAPException e) {
			throw new Exception("Invalid SOAP message returned (R2915)", e);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("VerifyUTF16EncodingOfRootPartWithoutAttachments failed", e);
		}

		if (!pass)
			throw new Exception("VerifyUTF16EncodingOfRootPartWithoutAttachments failed");
	}

	/*
	 * @testName: VerifyRequestContentTypeHttpHeaderWithAttachments
	 *
	 * @assertion_ids: WSI:SPEC:R2925; WSI:SPEC:R2932; WSI:SPEC:R2945;
	 * JAXWS:SPEC:10011; WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Verify that the Content-Type HTTP header is correct in the
	 * SOAP request.
	 *
	 */
	@Test
	public void VerifyRequestContentTypeHttpHeaderWithAttachments() throws Exception {
		logger.log(Level.INFO, "VerifyRequestContentTypeHttpHeaderWithAttachments");
		boolean pass = true;
		String expected = "ok";
		String response = null;
		try {
			InputRequestPut request = new InputRequestPut();
			request.setMimeType1("text/plain");
			request.setMimeType2("text/html");
			request.setHeader("notused");
			DataHandler attach1 = dh1;
			DataHandler attach2 = dh2;
			logger.log(Level.INFO, "Send SOAP RPC request with 2 attachments");
			response = client2.putMultipleAttachments(request, attach1, attach2);
		} catch (RuntimeException r) {
			response = r.getMessage();
			if (response.indexOf("FAILED") != -1) {
				response = response.substring(response.indexOf("FAILED"));
				TestUtil.logErr("HTTP header Content-Type is incorrect <got:" + response
						+ ", expected:multipart/related and text/xml>");
				pass = false;
			} else {
				response = response.substring(response.indexOf("PASSED"));
				logger.log(Level.INFO, "HTTP header Content-Type is correct <got:" + response + ">");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("VerifyRequestContentTypeHttpHeaderWithAttachments failed", e);
		}

		if (!pass)
			throw new Exception("VerifyRequestContentTypeHttpHeaderWithAttachments failed");
	}

	/*
	 * @testName: VerifyRequestContentTypeHttpHeaderWithoutAttachments
	 *
	 * @assertion_ids: WSI:SPEC:R2917; WSI:SPEC:R2932; WSI:SPEC:R2945;
	 * JAXWS:SPEC:10011; WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Verify that the Content-Type HTTP header is correct in the
	 * SOAP request.
	 *
	 */
	@Test
	public void VerifyRequestContentTypeHttpHeaderWithoutAttachments() throws Exception {
		logger.log(Level.INFO, "VerifyRequestContentTypeHttpHeaderWithoutAttachments");
		boolean pass = true;
		String response = null;

		try {
			InputRequestString request = new InputRequestString();
			request.setMyString("Hello");
			logger.log(Level.INFO, "Send SOAP RPC request without attachments");
			response = client2.echoNoAttachments(request);
		} catch (RuntimeException r) {
			response = r.getMessage();
			if (response.indexOf("FAILED") != -1) {
				response = response.substring(response.indexOf("FAILED"));
				TestUtil.logErr("HTTP header Content-Type is incorrect <got:" + response
						+ ", expected:multipart/related and text/xml>");
				pass = false;
			} else {
				response = response.substring(response.indexOf("PASSED"));
				logger.log(Level.INFO, "HTTP header Content-Type is correct <got:" + response + ">");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("VerifyRequestContentTypeHttpHeaderWithoutAttachments failed", e);
		}

		if (!pass)
			throw new Exception("VerifyRequestContentTypeHttpHeaderWithoutAttachments failed");
	}

	/*
	 * @testName: VerifyResponseContentTypeHttpHeaderWithAttachments
	 *
	 * @assertion_ids: WSI:SPEC:R2925; JAXWS:SPEC:10011; WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Verify that the Content-Type HTTP header is correct in the
	 * SOAP response.
	 *
	 */
	@Test
	public void VerifyResponseContentTypeHttpHeaderWithAttachments() throws Exception {
		logger.log(Level.INFO, "VerifyResponseContentTypeHttpHeaderWithAttachments");
		boolean pass = true;
		SOAPMessage request = null;
		SOAPMessage response = null;

		try {
			logger.log(Level.INFO, "Construct SOAP RPC request to get 2 attachments");
			String requestString = doSubstitution(R2925_REQUEST);
			StreamSource ssrc = new StreamSource(new ByteArrayInputStream(requestString.getBytes()));
			request = MessageFactory.newInstance().createMessage();
			request.getSOAPPart().setContent(ssrc);
			InputStream is = client1.makeHTTPRequest(requestString);
			String contentTypeHeader = client1.getResponseHeader("Content-Type");
			int statusCode = client1.getStatusCode();
			logger.log(Level.INFO, "HTTP header Content-Type = " + contentTypeHeader);
			String mediaType = null;
			if (contentTypeHeader.indexOf("multipart/related") == -1)
				mediaType = "text/xml";
			else
				mediaType = "multipart/related";
			if (contentTypeHeader.indexOf("multipart/related") == -1) {
				TestUtil.logErr(
						"HTTP header Content-Type is incorrect <got:" + mediaType + ", expected:multipart/related>");
				pass = false;
			} else
				logger.log(Level.INFO, "HTTP header Content-Type is correct: " + mediaType);
			if (statusCode < 200 || statusCode > 299) {
				TestUtil.logErr("Unexpected HTTP status code of: " + statusCode);
				pass = false;
			}
		} catch (Exception e) {
			throw new Exception("Unable to construct SOAP message request (R2925)", e);
		}

		if (!pass)
			throw new Exception("VerifyResponseContentTypeHttpHeaderWithAttachments failed");
	}

	/*
	 * @testName: VerifyResponseContentTypeHttpHeaderWithoutAttachments
	 *
	 * @assertion_ids: WSI:SPEC:R2917; JAXWS:SPEC:10011; WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Verify that the Content-Type HTTP header is correct in the
	 * SOAP response.
	 *
	 */
	@Test
	public void VerifyResponseContentTypeHttpHeaderWithoutAttachments() throws Exception {
		logger.log(Level.INFO, "VerifyResponseContentTypeHttpHeaderWithoutAttachments");
		boolean pass = true;
		SOAPMessage request = null;
		SOAPMessage response = null;

		try {
			logger.log(Level.INFO, "Construct SOAP RPC request to get no attachments");
			StreamSource ssrc = new StreamSource(new ByteArrayInputStream(R2917_REQUEST.getBytes()));
			request = MessageFactory.newInstance().createMessage();
			request.getSOAPPart().setContent(ssrc);
			InputStream is = client1.makeHTTPRequest(R2917_REQUEST);
			String contentTypeHeader = client1.getResponseHeader("Content-Type");
			int statusCode = client1.getStatusCode();
			logger.log(Level.INFO, "HTTP header Content-Type = " + contentTypeHeader);
			String mediaType = null;
			if (contentTypeHeader.indexOf("multipart/related") == -1)
				mediaType = "text/xml";
			else
				mediaType = "multipart/related";
			if (contentTypeHeader.indexOf("multipart/related") == -1 && contentTypeHeader.indexOf("text/xml") == -1) {
				TestUtil.logErr("HTTP header Content-Type is incorrect <got:" + mediaType
						+ ", expected:multipart/related or text/xml>");
				pass = false;
			} else
				logger.log(Level.INFO, "HTTP header Content-Type is correct: " + mediaType);
			if (statusCode < 200 || statusCode > 299) {
				TestUtil.logErr("Unexpected HTTP status code of: " + statusCode);
				pass = false;
			}
		} catch (Exception e) {
			throw new Exception("Unable to construct SOAP message request (R2917)", e);
		}

		if (!pass)
			throw new Exception("VerifyResponseContentTypeHttpHeaderWithoutAttachments failed");
	}

	/*
	 * @testName: VerifyRequestContentTransferEncodingMimeHeadersWithAttachments
	 *
	 * @assertion_ids: WSI:SPEC:R2934; WSI:SPEC:R2935; JAXWS:SPEC:10011;
	 * WSI:SPEC:R9801;
	 *
	 * @test_Strategy: Verify that the Content-Transfer-Encoding mime header(s) if
	 * set is correct in the SOAP request.
	 *
	 */
	@Test
	public void VerifyRequestContentTransferEncodingMimeHeadersWithAttachments() throws Exception {
		logger.log(Level.INFO, "VerifyRequestContentTransferEncodingMimeHeadersWithAttachments");
		boolean pass = true;
		String response = null;

		try {
			InputRequestPut request = new InputRequestPut();
			request.setMimeType1("text/plain");
			request.setMimeType2("text/html");
			request.setHeader("Check-Content-Transfer-Encoding");
			DataHandler attach1 = dh1;
			DataHandler attach2 = dh2;
			logger.log(Level.INFO, "Send SOAP RPC request with 2 attachments");
			response = client2.putMultipleAttachments(request, attach1, attach2);
		} catch (RuntimeException r) {
			response = r.getMessage();
			if (response.indexOf("FAILED") != -1) {
				response = response.substring(response.indexOf("FAILED"));
				TestUtil.logErr("HTTP header Content-Transfer-Encoding is incorrect <got:" + response
						+ ", expected: 7bit, 8bit, binary, quoted-printable, base64>");
				pass = false;
			} else {
				response = response.substring(response.indexOf("PASSED"));
				logger.log(Level.INFO, "HTTP header Content-Type is correct <got:" + response + ">");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("VerifyRequestContentTransferEncodingMimeHeadersWithAttachments failed", e);
		}

		if (!pass)
			throw new Exception("VerifyRequestContentTransferEncodingMimeHeadersWithAttachments failed");
	}

	/*******************************************************************************
	 * Validate request, response and attachments (getMultipleAttachments)
	 ******************************************************************************/
	private boolean ValidateRequestResponseAttachmentsGetTestCase(InputRequestGet request, OutputResponse response,
			Holder<jakarta.activation.DataHandler> attach1, Holder<jakarta.activation.DataHandler> attach2) {
		boolean result = true;
		TestUtil.logMsg("---------------------------------------------------------");
		TestUtil.logMsg("Validating the request, the response, and the attachments");
		TestUtil.logMsg("---------------------------------------------------------");
		logger.log(Level.INFO, "Check if the mime types are correct");
		if (!response.getMimeType1().equals(request.getMimeType1())) {
			TestUtil.logErr("MimeType1 is not equal in request and response");
			TestUtil.logErr("Request MimeType1 = " + request.getMimeType1());
			TestUtil.logErr("Response MimeType1 = " + response.getMimeType1());
			result = false;
		}
		if (!response.getMimeType2().equals(request.getMimeType2())) {
			TestUtil.logErr("MimeType2 is not equal in request and response");
			TestUtil.logErr("Request MimeType2 = " + request.getMimeType2());
			TestUtil.logErr("Response MimeType2 = " + response.getMimeType2());
			result = false;
		} else {
			logger.log(Level.INFO, "The mime types are correct");
		}
		logger.log(Level.INFO, "Check if the response result is correct");
		if (!response.getResult().equals("ok")) {
			TestUtil.logErr("Return status is " + response + ", expected ok");
			TestUtil.logErr("Return Reason is: " + response.getReason());
			result = false;
		} else {
			logger.log(Level.INFO, "The response result is correct");
		}
		try {
			logger.log(Level.INFO, "Check if the attachment contents are correct");
			DataHandler dh1 = new DataHandler(new URL(request.getUrl1()));
			DataHandler dh2 = new DataHandler(new URL(request.getUrl2()));
			byte data1[] = new byte[4096];
			byte data2[] = new byte[4096];
			InputStream is = dh1.getInputStream();
			int count1 = AttachmentHelper.readTheData(is, data1, 4096);
			is = attach1.value.getInputStream();
			int count2 = AttachmentHelper.readTheData(is, data2, 4096);
			if (!AttachmentHelper.validateAttachmentData(count1, data1, count2, data2, "Attachment1"))
				result = false;
			is = dh2.getInputStream();
			count1 = AttachmentHelper.readTheData(is, data1, 4096);
			is = attach2.value.getInputStream();
			count2 = AttachmentHelper.readTheData(is, data2, 4096);
			if (!AttachmentHelper.validateAttachmentData(count1, data1, count2, data2, "Attachment2"))
				result = false;
			if (result)
				logger.log(Level.INFO, "The attachment contents are equal");
		} catch (Exception e) {
			result = false;
			TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
		}
		return result;
	}

	/*******************************************************************************
	 * Validate request, response and attachments (echoMultipleAttachments)
	 ******************************************************************************/
	private boolean ValidateRequestResponseAttachmentsEchoTestCase(InputRequest request, OutputResponse response,
			Holder<jakarta.activation.DataHandler> attach1, Holder<jakarta.activation.DataHandler> attach2) {
		boolean result = true;
		TestUtil.logMsg("---------------------------------------------------------");
		TestUtil.logMsg("Validating the request, the response, and the attachments");
		TestUtil.logMsg("---------------------------------------------------------");
		logger.log(Level.INFO, "Check if the mime types are correct");
		if (!response.getMimeType1().equals(request.getMimeType1())) {
			TestUtil.logErr("MimeType1 is not equal in request and response");
			TestUtil.logErr("Request MimeType1 = " + request.getMimeType1());
			TestUtil.logErr("Response MimeType1 = " + response.getMimeType1());
			result = false;
		}
		if (!response.getMimeType2().equals(request.getMimeType2())) {
			TestUtil.logErr("MimeType2 is not equal in request and response");
			TestUtil.logErr("Request MimeType2 = " + request.getMimeType2());
			TestUtil.logErr("Response MimeType2 = " + response.getMimeType2());
			result = false;
		} else {
			logger.log(Level.INFO, "The mime types are correct");
		}
		logger.log(Level.INFO, "Check if the response result is correct");
		if (!response.getResult().equals("ok")) {
			TestUtil.logErr("Return status is " + response + ", expected ok");
			TestUtil.logErr("Return Reason is: " + response.getReason());
			result = false;
		} else {
			logger.log(Level.INFO, "The response result is correct");
		}
		try {
			logger.log(Level.INFO, "Check if the attachment contents are correct");
			DataHandler dh1 = new DataHandler(url1);
			byte data1[] = new byte[4096];
			byte data2[] = new byte[4096];
			InputStream is = dh1.getInputStream();
			int count1 = AttachmentHelper.readTheData(is, data1, 4096);
			is = attach1.value.getInputStream();
			int count2 = AttachmentHelper.readTheData(is, data2, 4096);
			if (!AttachmentHelper.validateAttachmentData(count1, data1, count2, data2, "Attachment1"))
				result = false;

			dh1 = new DataHandler(url2);
			is = dh1.getInputStream();
			count1 = AttachmentHelper.readTheData(is, data1, 4096);
			is = attach2.value.getInputStream();
			count2 = AttachmentHelper.readTheData(is, data2, 4096);
			if (!AttachmentHelper.validateAttachmentData(count1, data1, count2, data2, "Attachment2"))
				result = false;
		} catch (Exception e) {
			result = false;
			TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
		}
		return result;
	}

	/*******************************************************************************
	 * Validate request, response and attachments (echoAttachmentsWithHeader)
	 ******************************************************************************/
	private boolean ValidateRequestResponseAttachmentsEchoWithHeaderTestCase(InputRequest request,
			OutputResponse response, Holder<jakarta.activation.DataHandler> attach1,
			Holder<jakarta.activation.DataHandler> attach2) {
		boolean result = true;
		TestUtil.logMsg("---------------------------------------------------------");
		TestUtil.logMsg("Validating the request, the response, and the attachments");
		TestUtil.logMsg("---------------------------------------------------------");
		logger.log(Level.INFO, "Check if the mime types are correct");
		if (!response.getMimeType1().equals(request.getMimeType1())) {
			TestUtil.logErr("MimeType1 is not equal in request and response");
			TestUtil.logErr("Request MimeType1 = " + request.getMimeType1());
			TestUtil.logErr("Response MimeType1 = " + response.getMimeType1());
			result = false;
		}
		if (!response.getMimeType2().equals(request.getMimeType2())) {
			TestUtil.logErr("MimeType2 is not equal in request and response");
			TestUtil.logErr("Request MimeType2 = " + request.getMimeType2());
			TestUtil.logErr("Response MimeType2 = " + response.getMimeType2());
			result = false;
		} else {
			logger.log(Level.INFO, "The mime types are correct");
		}
		logger.log(Level.INFO, "Check if the response result is correct");
		if (!response.getResult().equals("ok")) {
			TestUtil.logErr("Return status is " + response + ", expected ok");
			TestUtil.logErr("Return Reason is: " + response.getReason());
			result = false;
		} else {
			logger.log(Level.INFO, "The response result is correct");
		}
		try {
			logger.log(Level.INFO, "Check if the attachment contents are correct");
			DataHandler dh1 = new DataHandler(url1);
			DataHandler dh2 = new DataHandler(url2);
			byte data1[] = new byte[4096];
			byte data2[] = new byte[4096];
			InputStream is = dh1.getInputStream();
			int count1 = AttachmentHelper.readTheData(is, data1, 4096);
			is = attach1.value.getInputStream();
			int count2 = AttachmentHelper.readTheData(is, data2, 4096);
			if (!AttachmentHelper.validateAttachmentData(count1, data1, count2, data2, "Attachment1"))
				result = false;
			is = dh2.getInputStream();
			count1 = AttachmentHelper.readTheData(is, data1, 4096);
			is = attach2.value.getInputStream();
			count2 = AttachmentHelper.readTheData(is, data2, 4096);
			if (!AttachmentHelper.validateAttachmentData(count1, data1, count2, data2, "Attachment2"))
				result = false;
			if (result)
				logger.log(Level.INFO, "The attachment contents are equal");
		} catch (Exception e) {
			result = false;
			TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
		}
		return result;
	}

	/*******************************************************************************
	 * Validate request, response and attachments (echoAllAttachmentTypes)
	 ******************************************************************************/
	private boolean ValidateRequestResponseAttachmentsEchoAllTestCase(VoidRequest request, OutputResponseAll response,
			Holder<jakarta.activation.DataHandler> attach1, Holder<jakarta.activation.DataHandler> attach2,
			Holder<javax.xml.transform.Source> attach3, Holder<java.awt.Image> attach4, Holder<java.awt.Image> attach5,
			Holder<javax.xml.transform.Source> attach6) {
		boolean result = true;
		TestUtil.logMsg("---------------------------------------------------------");
		TestUtil.logMsg("Validating the request, the response, and the attachments");
		TestUtil.logMsg("---------------------------------------------------------");
		logger.log(Level.INFO, "Check if the response result is correct");
		if (!response.getResult().equals("ok")) {
			TestUtil.logErr("Return status is " + response + ", expected ok");
			TestUtil.logErr("Return Reason is: " + response.getReason());
			result = false;
		} else {
			logger.log(Level.INFO, "The response result is correct");
		}
		try {
			logger.log(Level.INFO, "Check if the attachment contents are correct");
			DataHandler dh1 = new DataHandler(url1);
			byte data1[] = new byte[4096];
			byte data2[] = new byte[4096];
			InputStream is = dh1.getInputStream();
			int count1 = AttachmentHelper.readTheData(is, data1, 4096);
			is = attach1.value.getInputStream();
			int count2 = AttachmentHelper.readTheData(is, data2, 4096);
			if (!AttachmentHelper.validateAttachmentData(count1, data1, count2, data2, "Attachment1"))
				result = false;

			dh1 = new DataHandler(url2);
			is = dh1.getInputStream();
			count1 = AttachmentHelper.readTheData(is, data1, 4096);
			is = attach2.value.getInputStream();
			count2 = AttachmentHelper.readTheData(is, data2, 4096);
			if (!AttachmentHelper.validateAttachmentData(count1, data1, count2, data2, "Attachment2"))
				result = false;

			dh1 = new DataHandler(url3);
			StreamSource sr1 = new StreamSource(dh1.getInputStream());
			StreamSource sr2 = (StreamSource) attach3.value;
			String tmpStr = AttachmentHelper.validateAttachmentData(sr1, sr2, "Attachment3");
			if (tmpStr != null) {
				TestUtil.logErr(tmpStr);
				result = false;
			} else
				logger.log(Level.INFO, "Attachment3 xml content is equal in attachment");

			Image image1 = javax.imageio.ImageIO.read(url4);
			Image image2 = attach4.value;
			if (!AttachmentHelper.compareImages(image1, image2, new Rectangle(0, 0, 100, 120), "Attachment4"))
				result = false;

			image1 = javax.imageio.ImageIO.read(url5);
			image2 = attach5.value;
			if (!AttachmentHelper.compareImages(image1, image2, new Rectangle(0, 0, 100, 120), "Attachment5"))
				result = false;

			dh1 = new DataHandler(url6);
			sr1 = new StreamSource(dh1.getInputStream());
			sr2 = (StreamSource) attach6.value;
			tmpStr = AttachmentHelper.validateAttachmentData(sr1, sr2, "Attachment6");
			if (tmpStr != null) {
				TestUtil.logErr(tmpStr);
				result = false;
			} else
				logger.log(Level.INFO, "Attachment6 xml content is equal in attachment");

		} catch (Exception e) {
			result = false;
			TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
		}
		return result;
	}

	/*******************************************************************************
	 * Validate request, response and attachments (getAllAttachmentTypes)
	 ******************************************************************************/
	private boolean ValidateRequestResponseAttachmentsGetAllTestCase(InputRequestGetAll request,
			OutputResponseGetAll response, Holder<jakarta.activation.DataHandler> attach1,
			Holder<jakarta.activation.DataHandler> attach2, Holder<javax.xml.transform.Source> attach3,
			Holder<java.awt.Image> attach4, Holder<java.awt.Image> attach5,
			Holder<javax.xml.transform.Source> attach6) {
		boolean result = true;
		TestUtil.logMsg("---------------------------------------------------------");
		TestUtil.logMsg("Validating the request, the response, and the attachments");
		TestUtil.logMsg("---------------------------------------------------------");
		logger.log(Level.INFO, "Check if the mime types are correct");
		if (!response.getMimeType1().equals(request.getMimeType1())) {
			TestUtil.logErr("MimeType1 is not equal in request and response");
			TestUtil.logErr("Request MimeType1 = " + request.getMimeType1());
			TestUtil.logErr("Response MimeType1 = " + response.getMimeType1());
			result = false;
		}
		if (!response.getMimeType2().equals(request.getMimeType2())) {
			TestUtil.logErr("MimeType2 is not equal in request and response");
			TestUtil.logErr("Request MimeType2 = " + request.getMimeType2());
			TestUtil.logErr("Response MimeType2 = " + response.getMimeType2());
			result = false;
		}
		if (!response.getMimeType3().equals(request.getMimeType3())) {
			TestUtil.logErr("MimeType3 is not equal in request and response");
			TestUtil.logErr("Request MimeType3 = " + request.getMimeType3());
			TestUtil.logErr("Response MimeType3 = " + response.getMimeType3());
			result = false;
		}
		if (!response.getMimeType4().equals(request.getMimeType4())) {
			TestUtil.logErr("MimeType4 is not equal in request and response");
			TestUtil.logErr("Request MimeType4 = " + request.getMimeType4());
			TestUtil.logErr("Response MimeType4 = " + response.getMimeType4());
			result = false;
		}
		if (!response.getMimeType5().equals(request.getMimeType5())) {
			TestUtil.logErr("MimeType5 is not equal in request and response");
			TestUtil.logErr("Request MimeType5 = " + request.getMimeType5());
			TestUtil.logErr("Response MimeType5 = " + response.getMimeType5());
			result = false;
		}
		if (!response.getMimeType6().equals(request.getMimeType6())) {
			TestUtil.logErr("MimeType6 is not equal in request and response");
			TestUtil.logErr("Request MimeType6 = " + request.getMimeType6());
			TestUtil.logErr("Response MimeType6 = " + response.getMimeType6());
			result = false;
		} else {
			logger.log(Level.INFO, "The mime types are correct");
		}
		logger.log(Level.INFO, "Check if the response result is correct");
		if (!response.getResult().equals("ok")) {
			TestUtil.logErr("Return status is " + response + ", expected ok");
			TestUtil.logErr("Return Reason is: " + response.getReason());
			result = false;
		} else {
			logger.log(Level.INFO, "The response result is correct");
		}
		try {
			logger.log(Level.INFO, "Check if the attachment contents are correct");
			DataHandler dh1 = new DataHandler(url1);
			byte data1[] = new byte[4096];
			byte data2[] = new byte[4096];
			InputStream is = dh1.getInputStream();
			int count1 = AttachmentHelper.readTheData(is, data1, 4096);
			is = attach1.value.getInputStream();
			int count2 = AttachmentHelper.readTheData(is, data2, 4096);
			if (!AttachmentHelper.validateAttachmentData(count1, data1, count2, data2, "Attachment1"))
				result = false;

			dh1 = new DataHandler(url2);
			is = dh1.getInputStream();
			count1 = AttachmentHelper.readTheData(is, data1, 4096);
			is = attach2.value.getInputStream();
			count2 = AttachmentHelper.readTheData(is, data2, 4096);
			if (!AttachmentHelper.validateAttachmentData(count1, data1, count2, data2, "Attachment2"))
				result = false;

			dh1 = new DataHandler(url3);
			StreamSource sr1 = new StreamSource(dh1.getInputStream());
			StreamSource sr2 = (StreamSource) attach3.value;
			String tmpStr = AttachmentHelper.validateAttachmentData(sr1, sr2, "Attachment3");
			if (tmpStr != null) {
				TestUtil.logErr(tmpStr);
				result = false;
			} else
				logger.log(Level.INFO, "Attachment3 xml content is equal in attachment");

			Image image1 = javax.imageio.ImageIO.read(url4);
			Image image2 = attach4.value;
			if (!AttachmentHelper.compareImages(image1, image2, new Rectangle(0, 0, 100, 120), "Attachment4"))
				result = false;

			image1 = javax.imageio.ImageIO.read(url5);
			image2 = attach5.value;
			if (!AttachmentHelper.compareImages(image1, image2, new Rectangle(0, 0, 100, 120), "Attachment5"))
				result = false;

			dh1 = new DataHandler(url6);
			sr1 = new StreamSource(dh1.getInputStream());
			sr2 = (StreamSource) attach6.value;
			tmpStr = AttachmentHelper.validateAttachmentData(sr1, sr2, "Attachment6");
			if (tmpStr != null) {
				TestUtil.logErr(tmpStr);
				result = false;
			} else
				logger.log(Level.INFO, "Attachment6 xml content is equal in attachment");

		} catch (Exception e) {
			result = false;
			TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
		}
		return result;
	}

	private String doSubstitution(String s) {
		String tmp = s.replaceAll("localhost", hostname);
		tmp = tmp.replaceAll("8080", new Integer(portnum).toString());
		String modified = tmp.replaceAll("/WSIRLSwaTest", ctxroot);
		return modified;
	}
}
