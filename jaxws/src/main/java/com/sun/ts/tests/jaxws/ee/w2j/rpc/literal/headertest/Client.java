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

package com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.headertest;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.math.BigDecimal;
import java.math.BigInteger;
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

public class Client extends BaseClient {

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.headertest.";

	// URL properties used by the test
	private static final String ENDPOINT_URL = "w2jrlheadertest.endpoint.1";

	private static final String WSDLLOC_URL = "w2jrlheadertest.wsdlloc.1";

	private String url = null;

	// service and port information
	private static final String NAMESPACEURI = "http://headertestservice.org/HeaderTestService.wsdl";

	private static final String SERVICE_NAME = "HeaderTestService";

	private static final String PORT_NAME = "HeaderTestPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private URL wsdlurl = null;

	HeaderTest port = null;

	static HeaderTestService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		service = (HeaderTestService) getSharedObject();
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
		port = (HeaderTest) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, HeaderTestService.class, PORT_QNAME,
				HeaderTest.class);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (HeaderTest) service.getHeaderTestPort();
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "Obtained port");
		JAXWS_Util.dumpTargetEndpointAddress(port);
		// JAXWS_Util.setSOAPLogging(port);
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
	 * @testName: GoodOrderTestWithSoapHeaderAndMUFalse
	 *
	 * @assertion_ids: JAXWS:SPEC:2048; JAXWS:SPEC:2049; JAXWS:SPEC:10008;
	 * JAXWS:SPEC:3038; WSI:SPEC:R1013; WSI:SPEC:R1034; WSI:SPEC:R1032;
	 * WSI:SPEC:R9802; WSI:SPEC:R2209;
	 *
	 * @test_Strategy: Call submitOrder() with a valid product code passing a soap
	 * header (ConfigHeader) with mustUnderstand=false. The soap header is simply
	 * ignored. The RPC request must succeed.
	 *
	 */
	@Test
	public void GoodOrderTestWithSoapHeaderAndMUFalse() throws Exception {
		logger.log(Level.INFO, "GoodOrderTestWithSoapHeaderAndMUFalse");
		boolean pass = true;

		ProductOrderRequest poRequest;
		ConfigHeader ch;
		try {
			poRequest = new ProductOrderRequest();
			ProductOrderItem poi = new ProductOrderItem();
			poi.setProductName("Product-1");
			poi.setProductCode(new BigInteger("100"));
			poi.setQuantity(10);
			poi.setPrice(new BigDecimal(119.00));
			CustomerInfo ci = new CustomerInfo();
			ci.setCreditcard("1201-4465-1567-9823");
			ci.setName("John Doe");
			ci.setStreet("1 Network Drive");
			ci.setCity("Burlington");
			ci.setState("Ma");
			ci.setZip("01837");
			ci.setCountry("USA");
			poRequest.getItem().add(poi);
			poRequest.setCustomerInfo(ci);
			ch = new ConfigHeader();
			ch.setMustUnderstand(false);
			ch.setMessage("Config Header");
			ch.setTestName("GoodOrderTestWithSoapHeaderAndMUFalse");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("GoodOrderTestWithSoapHeaderAndMUFalse failed", e);
		}

		try {
			TestUtil.logMsg("Submit good order with soap header (ConfigHeader:MU=false)");
			logger.log(Level.INFO, "ConfigHeader must be ignored because MU=false");
			logger.log(Level.INFO, "The service endpoint simply ignores the soap header");
			logger.log(Level.INFO, "The RPC request must succeed");
			ProductOrderResponse poResponse = port.submitOrder(poRequest, ch);
			if (!ProductOrdersEqual(poRequest, poResponse))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("GoodOrderTestWithSoapHeaderAndMUFalse failed", e);
		}

		if (!pass)
			throw new Exception("GoodOrderTestWithSoapHeaderAndMUFalse failed");
	}

	/*
	 * @testName: GoodOrderTestWithSoapHeaderAndMUTrue
	 *
	 * @assertion_ids: JAXWS:SPEC:2048; JAXWS:SPEC:2049; JAXWS:SPEC:10008;
	 * JAXWS:SPEC:3038; WSI:SPEC:R1013; WSI:SPEC:R1034; WSI:SPEC:R1032;
	 * WSI:SPEC:R9802; WSI:SPEC:R2209;
	 *
	 * @test_Strategy: Call submitOrder() with a valid product code passing a soap
	 * header (ConfigHeader) with mustUnderstand=true. The soap header is understood
	 * by the service endpoint and the soap header is valid. The RPC request must
	 * succeed.
	 */
	@Test
	public void GoodOrderTestWithSoapHeaderAndMUTrue() throws Exception {
		logger.log(Level.INFO, "GoodOrderTestWithSoapHeaderAndMUTrue");
		boolean pass = true;

		ProductOrderRequest poRequest;
		ConfigHeader ch;
		try {
			poRequest = new ProductOrderRequest();
			ProductOrderItem poi = new ProductOrderItem();
			poi.setProductName("Product-1");
			poi.setProductCode(new BigInteger("100"));
			poi.setQuantity(10);
			poi.setPrice(new BigDecimal(119.00));
			CustomerInfo ci = new CustomerInfo();
			ci.setCreditcard("1201-4465-1567-9823");
			ci.setName("John Doe");
			ci.setStreet("1 Network Drive");
			ci.setCity("Burlington");
			ci.setState("Ma");
			ci.setZip("01837");
			ci.setCountry("USA");
			poRequest.getItem().add(poi);
			poRequest.setCustomerInfo(ci);
			ch = new ConfigHeader();
			ch.setMustUnderstand(true);
			ch.setMessage("Config Header");
			ch.setTestName("GoodOrderTestWithSoapHeaderAndMUTrue");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("GoodOrderTestWithSoapHeaderAndMUTrue failed", e);
		}

		try {
			TestUtil.logMsg("Submit good order with soap header (ConfigHeader:MU=true)");
			TestUtil.logMsg("ConfigHeader must be understood and valid bacause MU=true");
			logger.log(Level.INFO, "The service endpoint understands and validates the soap header as ok");
			logger.log(Level.INFO, "The RPC request must succeed");
			ProductOrderResponse poResponse = port.submitOrder(poRequest, ch);
			logger.log(Level.INFO, "GoodOrderTestWithMUTrueHeader succeeded (expected)");
			if (!ProductOrdersEqual(poRequest, poResponse))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("GoodOrderTestWithSoapHeaderAndMUTrue failed", e);
		}

		if (!pass)
			throw new Exception("GoodOrderTestWithSoapHeaderAndMUTrue failed");
	}

	/*
	 * @testName: SoapHeaderFaultTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2048; JAXWS:SPEC:2049; JAXWS:SPEC:10008;
	 * WSI:SPEC:R1013; WSI:SPEC:R1034; WSI:SPEC:R1032; WSI:SPEC:R9802;
	 * WSI:SPEC:R2209;
	 *
	 * @test_Strategy: Call submitOrder() passing soap header (ConfigHeader) with
	 * mustUnderstand attribute=true and the soap header (ConfigHeader) is not
	 * understood. The service endpoint must throw back the SOAP Header Exception
	 * (ConfigFault).
	 *
	 */
	@Test
	public void SoapHeaderFaultTest() throws Exception {
		logger.log(Level.INFO, "SoapHeaderFaultTest");
		boolean pass = true;

		ProductOrderRequest poRequest;
		ConfigHeader ch;
		try {
			poRequest = new ProductOrderRequest();
			ProductOrderItem poi = new ProductOrderItem();
			poi.setProductName("Product-1");
			poi.setProductCode(new BigInteger("100"));
			poi.setQuantity(10);
			poi.setPrice(new BigDecimal(119.00));
			CustomerInfo ci = new CustomerInfo();
			ci.setCreditcard("1201-4465-1567-9823");
			ci.setName("John Doe");
			ci.setStreet("1 Network Drive");
			ci.setCity("Burlington");
			ci.setState("Ma");
			ci.setZip("01837");
			ci.setCountry("USA");
			poRequest.getItem().add(poi);
			poRequest.setCustomerInfo(ci);
			ch = new ConfigHeader();
			ch.setMustUnderstand(true);
			ch.setMessage("Config Header");
			ch.setTestName("SoapHeaderFaultTest");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("SoapHeaderFaultTest failed", e);
		}

		try {
			TestUtil.logMsg("Submit good order with soap header (ConfigHeader:MU=true)");
			TestUtil.logMsg("ConfigHeader must be understood and valid bacause MU=true");
			TestUtil.logMsg("The service endpoint does not understand the soap header");
			logger.log(Level.INFO, "The RPC request must fail with a ConfigFault");
			ProductOrderResponse poResponse = port.submitOrder(poRequest, ch);
			TestUtil.logErr("Did not throw expected ConfigFault");
			pass = false;
		} catch (ConfigFault e) {
			logger.log(Level.INFO, "Caught expected ConfigFault");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("SoapHeaderFaultTest failed", e);
		}

		if (!pass)
			throw new Exception("SoapHeaderFaultTest failed");
	}

	/*
	 * @testName: SoapFaultTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2048; JAXWS:SPEC:2049; JAXWS:SPEC:10008;
	 * JAXWS:SPEC:3028; JAXWS:SPEC:2044; WSI:SPEC:R1013; WSI:SPEC:R1034;
	 * WSI:SPEC:R1032; WSI:SPEC:R9802; WSI:SPEC:R2209;
	 *
	 * @test_Strategy: Call submitOrder() passing soap header (ConfigHeader) with
	 * mustUnderstand attribute=false so the soap header (ConfigHeader) will simply
	 * be ignored. The submitOrder() contains an invalid product code so the service
	 * endpoint must throw back the SOAP Exception (BadOrderFault).
	 *
	 */
	@Test
	public void SoapFaultTest() throws Exception {
		logger.log(Level.INFO, "SoapFaultTest");
		boolean pass = true;

		ProductOrderRequest poRequest;
		ConfigHeader ch;
		try {
			poRequest = new ProductOrderRequest();
			ProductOrderItem poi = new ProductOrderItem();
			poi.setProductName("Product-1");
			poi.setProductCode(new BigInteger("1234123412341234"));
			poi.setQuantity(10);
			poi.setPrice(new BigDecimal(119.00));
			CustomerInfo ci = new CustomerInfo();
			ci.setCreditcard("1201-4465-1567-9823");
			ci.setName("John Doe");
			ci.setStreet("1 Network Drive");
			ci.setCity("Burlington");
			ci.setState("Ma");
			ci.setZip("01837");
			ci.setCountry("USA");
			poRequest.getItem().add(poi);
			poRequest.setCustomerInfo(ci);
			ch = new ConfigHeader();
			ch.setMustUnderstand(false);
			ch.setMessage("Config Header");
			ch.setTestName("SoapFaultTest");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("SoapFaultTest failed", e);
		}

		try {
			TestUtil.logMsg("Submit bad order with soap header (ConfigHeader:MU=false)");
			logger.log(Level.INFO, "ConfigHeader must be ignored because MU=false");
			logger.log(Level.INFO, "The service endpoint simply ignores the soap header");
			TestUtil.logMsg("Order contains bad product code (must throw BadOrderFault)");
			logger.log(Level.INFO, "The RPC request must fail with a BadOrderFault");
			ProductOrderResponse poResponse = port.submitOrder(poRequest, ch);
			TestUtil.logErr("Did not throw expected BadOrderFault");
			pass = false;
		} catch (BadOrderFault e) {
			logger.log(Level.INFO, "Caught expected BadOrderFault");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("SoapFaultTest failed", e);
		}

		if (!pass)
			throw new Exception("SoapFaultTest failed");
	}

	private boolean ProductOrdersEqual(ProductOrderRequest req, ProductOrderResponse resp) {
		boolean equal = true;
		logger.log(Level.INFO, "Performing data comparison of request/response (should be equal)");
		Object[] reqArray = req.getItem().toArray();
		Object[] respArray = resp.getItem().toArray();
		ProductOrderItem reqItem = null;
		ProductOrderItem respItem = null;
		if (reqArray == null || respArray == null) {
			TestUtil.logErr("Data comparison error (unexpected)");
			TestUtil.logErr("Got:      Item Array = " + respItem);
			TestUtil.logErr("Expected: Item Array = " + reqItem);
			equal = false;
		} else if (reqArray.length != respArray.length) {
			TestUtil.logErr("Data comparison error (unexpected)");
			TestUtil.logErr("Got:      Item Array length = " + respArray.length);
			TestUtil.logErr("Expected: Item Array length = " + reqArray.length);
			equal = false;
		} else {
			reqItem = (ProductOrderItem) reqArray[0];
			respItem = (ProductOrderItem) respArray[0];
		}
		if (equal) {
			if (!reqItem.getProductName().equals(respItem.getProductName())
					|| !reqItem.getProductCode().equals(respItem.getProductCode())
					|| reqItem.getQuantity() != respItem.getQuantity()
					|| !reqItem.getPrice().equals(respItem.getPrice())) {
				TestUtil.logErr("Data comparison error (unexpected)");
				TestUtil.logErr("Got:      <" + respItem.getProductName() + "," + respItem.getProductCode() + ","
						+ respItem.getQuantity() + "," + respItem.getPrice() + ">");
				TestUtil.logErr("Expected: <" + reqItem.getProductName() + "," + reqItem.getProductCode() + ","
						+ reqItem.getQuantity() + "," + reqItem.getPrice() + ">");
				equal = false;
			} else {
				logger.log(Level.INFO, "Data comparison ok (expected)");
				logger.log(Level.INFO, "Got:      <" + respItem.getProductName() + "," + respItem.getProductCode() + ","
						+ respItem.getQuantity() + "," + respItem.getPrice() + ">");
				logger.log(Level.INFO, "Expected: <" + reqItem.getProductName() + "," + reqItem.getProductCode() + ","
						+ reqItem.getQuantity() + "," + reqItem.getPrice() + ">");
			}
		}
		return equal;
	}

}
