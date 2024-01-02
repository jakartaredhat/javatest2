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
 * $Id: Client.java 59593 2009-09-28 15:35:32Z af70133 $
 */

package com.sun.ts.tests.jaxws.wsi.w2j.document.literal.swareftest;

import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;

import javax.xml.namespace.QName;
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

import jakarta.activation.DataHandler;

public class Client extends BaseClient {

	// URL properties used by the test
	private static final String ENDPOINT_URL = "wsidlswareftest.endpoint.1";

	private static final String WSDLLOC_URL = "wsidlswareftest.wsdlloc.1";

	private static final String CTXROOT = "wsidlswareftest.ctxroot.1";

	private String surl = null;

	private String file = null;

	private String ctxroot = null;

	private URL wsdlurl = null;

	private static final String NAMESPACEURI = "http://SwaRefTestService.org/wsdl";

	private static final String SERVICE_NAME = "WSIDLSwaRefTestService";

	private static final String PORT_NAME = "SwaRefTestPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private DataHandler dh1 = null;

	private DataHandler dh2 = null;

	private DataHandler dh3 = null;

	private DataHandler dh4 = null;

	private URL url1 = null;

	private URL url2 = null;

	private URL url3 = null;

	private URL url4 = null;

	static WSIDLSwaRefTestService service = null;

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
		ctxroot = JAXWS_Util.getURLFromProp(CTXROOT);
		logger.log(Level.INFO, "Service Endpoint URL: " + surl);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
		logger.log(Level.INFO, "Context Root:         " + ctxroot);
	}

	SwaRefTest port = null;

	protected void getPortStandalone() throws Exception {
		port = (SwaRefTest) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, WSIDLSwaRefTestService.class, PORT_QNAME,
				SwaRefTest.class);
		JAXWS_Util.setTargetEndpointAddress(port, surl);
	}

	private void getTargetEndpointAddress(Object port) throws Exception {
		logger.log(Level.INFO, "Get Target Endpoint Address for port=" + port);
		String url = JAXWS_Util.getTargetEndpointAddress(port);
		logger.log(Level.INFO, "Target Endpoint Address=" + url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (SwaRefTest) service.getPort(SwaRefTest.class);
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "Obtained port");
		getTargetEndpointAddress(port);
	}

	protected void getService() {
		service = (WSIDLSwaRefTestService) getSharedObject();
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
		logger.log(Level.INFO, "Create URL's to attachments");
		url1 = ctsurl.getURL("http", hostname, portnum, ctxroot + "/attach.txt");
		url2 = ctsurl.getURL("http", hostname, portnum, ctxroot + "/attach.html");
		url3 = ctsurl.getURL("http", hostname, portnum, ctxroot + "/attach.xml");
		url4 = ctsurl.getURL("http", hostname, portnum, ctxroot + "/attach.jpeg");
		logger.log(Level.INFO, "url1=" + url1);
		logger.log(Level.INFO, "url2=" + url2);
		logger.log(Level.INFO, "url3=" + url3);
		logger.log(Level.INFO, "url4=" + url4);
		logger.log(Level.INFO, "Create DataHandler's to attachments");
		dh1 = new DataHandler(url1);
		dh2 = new DataHandler(url2);
		dh3 = new DataHandler(url3);
		dh4 = new DataHandler(javax.imageio.ImageIO.read(url4), "image/jpeg");
		logger.log(Level.INFO, "dh1.getContentType()=" + dh1.getContentType());
		logger.log(Level.INFO, "dh2.getContentType()=" + dh2.getContentType());
		logger.log(Level.INFO, "dh3.getContentType()=" + dh3.getContentType());
		logger.log(Level.INFO, "dh4.getContentType()=" + dh4.getContentType());
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: EchoSingleSwaRefAttachmentTest
	 *
	 * @assertion_ids: WSI:SPEC:R2901; WSI:SPEC:R2907; WSI:SPEC:R2909;
	 * WSI:SPEC:R2910; WSI:SPEC:R2911; WSI:SPEC:R2931; WSI:SPEC:R2921;
	 * WSI:SPEC:R2926; WSI:SPEC:R2929; WSI:SPEC:R2946; JAXWS:SPEC:10011;
	 * WSI:SPEC:R2927; JAXWS:SPEC:2051; JAXWS:SPEC:2052; JAXWS:SPEC:2053;
	 * WSI:SPEC:R2940; WSI:SPEC:R2928;
	 *
	 * @test_Strategy: Send and Receive a single attachment via swaRef type.
	 *
	 */
	@Test
	public void EchoSingleSwaRefAttachmentTest() throws Exception {
		logger.log(Level.INFO, "EchoSingleSwaRefAttachmentTest");
		boolean pass = true;

		try {
			TestUtil.logMsg("Send and receive (text/xml) attachment via the swaRef type");
			SwaRefTypeRequest request = new SwaRefTypeRequest();
			DataHandler swaRefInput = dh3;
			request.setAttachment(swaRefInput);
			SwaRefTypeResponse response = port.echoSingleSwaRefAttachment(request);
			if (!ValidateSingleSwaRefAttachmentTestCase(request, response, "text/xml"))
				pass = false;
			logger.log(Level.INFO, "Send and receive (text/plain) attachment via the swaRef type");
			swaRefInput = dh1;
			request.setAttachment(swaRefInput);
			response = port.echoSingleSwaRefAttachment(request);
			if (!ValidateSingleSwaRefAttachmentTestCase(request, response, "text/plain"))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("EchoSingleSwaRefAttachmentTest failed", e);
		}
		if (!pass)
			throw new Exception("EchoSingleSwaRefAttachmentTest failed");
	}

	/*
	 * @testName: EchoMultipleSwaRefAttachmentsTest
	 *
	 * @assertion_ids: WSI:SPEC:R2901; WSI:SPEC:R2907; WSI:SPEC:R2909;
	 * WSI:SPEC:R2910; WSI:SPEC:R2911; WSI:SPEC:R2931; WSI:SPEC:R2921;
	 * WSI:SPEC:R2926; WSI:SPEC:R2929; WSI:SPEC:R2946; JAXWS:SPEC:10011;
	 * WSI:SPEC:R2927; JAXWS:SPEC:2051; JAXWS:SPEC:2052; JAXWS:SPEC:2053;
	 * WSI:SPEC:R2940; WSI:SPEC:R2928;
	 *
	 * @test_Strategy: Send and Receive multiple attachments via swaRef type.
	 *
	 */
	@Test
	public void EchoMultipleSwaRefAttachmentsTest() throws Exception {
		logger.log(Level.INFO, "SwaRefAttachmentsTest2");
		boolean pass = true;

		try {
			logger.log(Level.INFO,
					"Send and receive (text/xml, text/plain, text/html) attachments via the swaRef type");
			SwaRefTypeRequest2 request = new SwaRefTypeRequest2();
			request.setAttachment1(dh3);
			request.setAttachment2(dh1);
			request.setAttachment3(dh4);
			SwaRefTypeResponse2 response = port.echoMultipleSwaRefAttachments(request);
			if (!ValidateMultipleSwaRefAttachmentsTestCase(request, response))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("EchoMultipleSwaRefAttachmentsTest failed", e);
		}
		if (!pass)
			throw new Exception("EchoMultipleSwaRefAttachmentsTest failed");
	}

	/*******************************************************************************
	 * Validate request, response and attachments (swaRefAttachments)
	 ******************************************************************************/
	private boolean ValidateSingleSwaRefAttachmentTestCase(SwaRefTypeRequest request, SwaRefTypeResponse response,
			String type) {
		boolean result = true;
		logger.log(Level.INFO, "--------------------------------------------------------");
		logger.log(Level.INFO, "Validating the request, the response, and the attachment");
		logger.log(Level.INFO, "--------------------------------------------------------");
		if (type.equals("text/xml")) {
			try {
				StreamSource sr1 = new StreamSource(request.getAttachment().getInputStream());
				StreamSource sr2 = new StreamSource(response.getAttachment().getInputStream());
				String tmpStr = AttachmentHelper.validateAttachmentData(sr1, sr2, "XmlAttachment");
				if (tmpStr != null) {
					TestUtil.logErr(tmpStr);
					result = false;
				}
			} catch (Exception e) {
				result = false;
				TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
				TestUtil.printStackTrace(e);
			}
		} else if (type.equals("image/jpeg")) {
			try {
				Image image1 = javax.imageio.ImageIO.read(request.getAttachment().getInputStream());
				Image image2 = javax.imageio.ImageIO.read(response.getAttachment().getInputStream());
				if (!AttachmentHelper.compareImages(image1, image2, new Rectangle(0, 0, 100, 120), "JpegAttachment"))
					result = false;
			} catch (Exception e) {
				result = false;
				TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
				TestUtil.printStackTrace(e);
			}
		} else if (type.equals("text/plain")) {
			try {
				byte data1[] = new byte[4096];
				byte data2[] = new byte[4096];
				InputStream is = request.getAttachment().getInputStream();
				int count1 = AttachmentHelper.readTheData(is, data1, 4096);
				is = response.getAttachment().getInputStream();
				int count2 = AttachmentHelper.readTheData(is, data2, 4096);
				if (!AttachmentHelper.validateAttachmentData(count1, data1, count2, data2, "PlainTextAttachment"))
					result = false;
			} catch (Exception e) {
				result = false;
				TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
				TestUtil.printStackTrace(e);
			}
		}
		return result;
	}

	private boolean ValidateMultipleSwaRefAttachmentsTestCase(SwaRefTypeRequest2 request,
			SwaRefTypeResponse2 response) {
		boolean result = true;
		TestUtil.logMsg("---------------------------------------------------------");
		TestUtil.logMsg("Validating the request, the response, and the attachments");
		TestUtil.logMsg("---------------------------------------------------------");
		try {
			StreamSource sr1 = new StreamSource(request.getAttachment1().getInputStream());
			StreamSource sr2 = new StreamSource(response.getAttachment1().getInputStream());
			String tmpStr = AttachmentHelper.validateAttachmentData(sr1, sr2, "XmlAttachment");
			if (tmpStr != null) {
				TestUtil.logErr(tmpStr);
				result = false;
			}
			byte data1[] = new byte[4096];
			byte data2[] = new byte[4096];
			InputStream is = request.getAttachment2().getInputStream();
			int count1 = AttachmentHelper.readTheData(is, data1, 4096);
			is = response.getAttachment2().getInputStream();
			int count2 = AttachmentHelper.readTheData(is, data2, 4096);
			if (!AttachmentHelper.validateAttachmentData(count1, data1, count2, data2, "PlainTextAttachment"))
				result = false;
			Image image1 = javax.imageio.ImageIO.read(request.getAttachment3().getInputStream());
			Image image2 = javax.imageio.ImageIO.read(response.getAttachment3().getInputStream());
			if (!AttachmentHelper.compareImages(image1, image2, new Rectangle(0, 0, 100, 120), "JpegAttachment"))
				result = false;
		} catch (Exception e) {
			result = false;
			TestUtil.logErr("Caught unexpected exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
		}
		return result;
	}
}
