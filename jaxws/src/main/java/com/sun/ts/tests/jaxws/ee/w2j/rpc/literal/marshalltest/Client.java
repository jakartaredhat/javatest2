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

package com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.marshalltest;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Data;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;

import jakarta.xml.ws.WebServiceException;

public class Client extends BaseClient {

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.marshalltest.";

	// service and port information
	private static final String NAMESPACEURI = "http://marshalltestservice.org/MarshallTestService.wsdl";

	private static final String SERVICE_NAME = "MarshallTestService";

	private static final String PORT_NAME1 = "MarshallTestPort1";

	private static final String PORT_NAME2 = "MarshallTestPort2";

	private static final String PORT_NAME3 = "MarshallTestPort3";

	private static final String PORT_NAME4 = "MarshallTestPort4";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME1 = new QName(NAMESPACEURI, PORT_NAME1);

	private QName PORT_QNAME2 = new QName(NAMESPACEURI, PORT_NAME2);

	private QName PORT_QNAME3 = new QName(NAMESPACEURI, PORT_NAME3);

	private QName PORT_QNAME4 = new QName(NAMESPACEURI, PORT_NAME4);

	// URL properties used by the test
	private static final String ENDPOINT_URL = "w2jrlmarshalltest.endpoint.1";

	private static final String ENDPOINT2_URL = "w2jrlmarshalltest.endpoint.2";

	private static final String ENDPOINT3_URL = "w2jrlmarshalltest.endpoint.3";

	private static final String ENDPOINT4_URL = "w2jrlmarshalltest.endpoint.4";

	private static final String WSDLLOC_URL = "w2jrlmarshalltest.wsdlloc.1";

	private String url = null;

	private String url2 = null;

	private String url3 = null;

	private String url4 = null;

	private URL wsdlurl = null;

	MarshallTest port = null;

	NewSchemaTest port2 = null;

	CompoundTest port3 = null;

	OneWayTest port4 = null;

	static MarshallTestService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

	protected void getService() {
		service = (MarshallTestService) getSharedObject();
	}

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(ENDPOINT_URL);
		url = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT2_URL);
		url2 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT3_URL);
		url3 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(ENDPOINT4_URL);
		url4 = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC_URL);
		wsdlurl = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		logger.log(Level.INFO, "Service Endpoint URL: " + url);
		logger.log(Level.INFO, "Service Endpoint URL: " + url2);
		logger.log(Level.INFO, "Service Endpoint URL: " + url3);
		logger.log(Level.INFO, "Service Endpoint URL: " + url4);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
	}

	protected void getPortStandalone() throws Exception {
		port = (MarshallTest) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, MarshallTestService.class, PORT_QNAME1,
				MarshallTest.class);
		port2 = (NewSchemaTest) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, MarshallTestService.class, PORT_QNAME2,
				NewSchemaTest.class);
		port3 = (CompoundTest) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, MarshallTestService.class, PORT_QNAME3,
				CompoundTest.class);
		port4 = (OneWayTest) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, MarshallTestService.class, PORT_QNAME4,
				OneWayTest.class);
		JAXWS_Util.setTargetEndpointAddress(port, url);
		JAXWS_Util.setTargetEndpointAddress(port2, url2);
		JAXWS_Util.setTargetEndpointAddress(port3, url3);
		JAXWS_Util.setTargetEndpointAddress(port4, url4);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = service.getMarshallTestPort1();
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "Obtained port");
		port2 = service.getMarshallTestPort2();
		logger.log(Level.INFO, "port2=" + port2);
		logger.log(Level.INFO, "Obtained port2");
		port3 = service.getMarshallTestPort3();
		logger.log(Level.INFO, "port3=" + port3);
		logger.log(Level.INFO, "Obtained port3");
		port4 = service.getMarshallTestPort4();
		logger.log(Level.INFO, "port4=" + port4);
		logger.log(Level.INFO, "Obtained port4");
		JAXWS_Util.dumpTargetEndpointAddress(port);
		JAXWS_Util.dumpTargetEndpointAddress(port2);
		JAXWS_Util.dumpTargetEndpointAddress(port3);
		JAXWS_Util.dumpTargetEndpointAddress(port4);
		// JAXWS_Util.setTargetEndpointAddress(port, url);
		// JAXWS_Util.setTargetEndpointAddress(port2, url2);
		// JAXWS_Util.setTargetEndpointAddress(port3, url3);
		// JAXWS_Util.setTargetEndpointAddress(port4, url4);
	}

	private JavaBean JavaBean_data[] = null;

	private JavaBean2 JavaBean2_data[] = null;

	/* Test setup */

	/*
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * 
	 * @class.setup_props: webServerHost; webServerPort; platform.mode;
	 */
	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		JavaBean_data = new JavaBean[2];
		JavaBean_data[0] = new JavaBean();
		JavaBean_data[1] = new JavaBean();
		JavaBean2_data = new JavaBean2[2];
		JavaBean2_data[0] = new JavaBean2();
		JavaBean2_data[1] = new JavaBean2();
	}

	private void printSeperationLine() {
		logger.log(Level.INFO, "---------------------------");
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	// ====================================================================
	// Java Primitive Types
	// ====================================================================

	/*
	 * @testName: MarshallPrimitiveTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC methods for each
	 * primitive type. For each primitive type pass its value as input to the
	 * corresponding RPC method and receive it back as the return value. Compare
	 * results of each value/type of what was sent and what was returned. Verify
	 * they are equal.
	 *
	 * Description
	 */
	@Test
	public void MarshallPrimitiveTest() throws Exception {
		logger.log(Level.INFO, "MarshallPrimitiveTest");
		boolean pass = true;

		if (!booleanTest())
			pass = false;
		if (!byteTest())
			pass = false;
		if (!shortTest())
			pass = false;
		if (!intTest())
			pass = false;
		if (!longTest())
			pass = false;
		if (!floatTest())
			pass = false;
		if (!doubleTest())
			pass = false;

		if (!pass)
			throw new Exception("MarshallPrimitiveTest failed");
	}

	// ====================================================================
	// Standard Java Classes
	// ====================================================================

	/*
	 * @testName: MarshallStandardJavaClassesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC methods for each
	 * standard java class type. For each standard java class above pass its value
	 * as input to the corresponding RPC method and receive it back as the return
	 * value. Compare results of each value/type of what was sent sent and what was
	 * returned. Verify they are equal.
	 *
	 * Description
	 */
	@Test
	public void MarshallStandardJavaClassesTest() throws Exception {
		logger.log(Level.INFO, "MarshallStandardJavaClassesTest");
		boolean pass = true;

		if (!StringTest())
			pass = false;
		if (!CalendarTest())
			pass = false;
		if (!BigIntegerTest())
			pass = false;
		if (!BigDecimalTest())
			pass = false;

		if (!pass)
			throw new Exception("MarshallStandardJavaClassesTest failed");
	}

	// ====================================================================
	// JavaBeans Class
	// ====================================================================

	/*
	 * @testName: MarshallJavaBeanTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method
	 * JavaBeanTest. Pass a JavaBean value to the RPC method and receive it back as
	 * the return value. Compare results of JavaBean value from what was sent and
	 * what was returned. Verify they are equal.
	 *
	 * Description
	 */
	@Test
	public void MarshallJavaBeanTest() throws Exception {
		logger.log(Level.INFO, "MarshallJavaBeanTest");
		boolean pass = true;

		init_JavaBean_Data();
		JavaBean values[] = JavaBean_data;
		JavaBeanTestResponse response;
		JavaBeanTest request;
		logger.log(Level.INFO, "Passing/Returning JavaBean JavaBean class to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new JavaBeanTest();
				request.setJavaBean(values[i]);
				response = port.javaBeanTest(request);
				if (!compareJavaBeans(values[i], response.getJavaBean()))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallJavaBeanTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallJavaBeanTest failed");
	}

	// ====================================================================
	// Java Array Single-Dimensional for all supported JAX-WS types
	// ====================================================================

	/*
	 * @testName: MarshallJavaArrayTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, invoke the RPC methods for each java
	 * type supported. For each java type supported pass an arrary of values as
	 * input to the corresponding RPC method and receive it back as the return
	 * value. Compare results of each array type of what was sent and what was
	 * returned. Verify they are equal.
	 *
	 * Description
	 */
	@Test
	public void MarshallJavaArrayTest() throws Exception {
		logger.log(Level.INFO, "MarshallJavaArrayTest");
		boolean pass = true;

		if (!booleanArrayTest())
			pass = false;
		if (!byteArrayTest())
			pass = false;
		if (!shortArrayTest())
			pass = false;
		if (!intArrayTest())
			pass = false;
		if (!longArrayTest())
			pass = false;
		if (!floatArrayTest())
			pass = false;
		if (!doubleArrayTest())
			pass = false;
		if (!StringArrayTest())
			pass = false;
		if (!CalendarArrayTest())
			pass = false;
		if (!BigIntegerArrayTest())
			pass = false;
		if (!BigDecimalArrayTest())
			pass = false;
		if (!JavaBeanArrayTest())
			pass = false;
		if (!QNameArrayTest())
			pass = false;

		if (!pass)
			throw new Exception("MarshallJavaArrayTest failed");
	}

	// ====================================================================
	// The void type
	// ====================================================================

	/*
	 * @testName: MarshallVoidTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method voidTest.
	 * Verify normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallVoidTest() throws Exception {
		logger.log(Level.INFO, "MarshallVoidTest");
		boolean pass = true;
		VoidTestResponse response;
		logger.log(Level.INFO, "Handling a void type to/from JAXWS Service");
		try {
			port.voidTest(new VoidTest());
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallVoidTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallVoidTest failed");
	}

	// =======================================================================
	// Marshall Other XML Schema Types (Any, List, Anonymous, ...)
	// =======================================================================

	/*
	 * @testName: MarshallNormalizedStringTypeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallNormalizedStringTypeTest() throws Exception {
		logger.log(Level.INFO, "MarshallNormalizedStringTypeTest");
		boolean pass = true;

		String request = "123-ABC12";

		try {
			String response = port2.echoNormalizedStringTypeTest(request);
			logger.log(Level.INFO, "response=" + response);
			if (!request.equals(response)) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallNormalizedStringTypeTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallNormalizedStringTypeTest failed");
	}

	/*
	 * @testName: MarshallIntegerRangeTypeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallIntegerRangeTypeTest() throws Exception {
		logger.log(Level.INFO, "MarshallIntegerRangeTypeTest");
		boolean pass = true;

		BigInteger request = new BigInteger("101");

		try {
			logger.log(Level.INFO, "Sending request: " + request);
			BigInteger response = port2.echoIntegerRangeTypeTest(request);
			logger.log(Level.INFO, "response=" + response);
			if (!request.equals(response)) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallIntegerRangeTypeTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallIntegerRangeTypeTest failed");
	}

	/*
	 * @testName: MarshallEnumTypesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallEnumTypesTest() throws Exception {
		logger.log(Level.INFO, "MarshallEnumTypesTest");
		boolean pass = true;

		if (!StringEnumTypeTest())
			pass = false;
		if (!ByteEnumTypeTest())
			pass = false;
		if (!ShortEnumTypeTest())
			pass = false;
		if (!IntegerEnumTypeTest())
			pass = false;
		if (!IntEnumTypeTest())
			pass = false;
		if (!LongEnumTypeTest())
			pass = false;
		if (!DecimalEnumTypeTest())
			pass = false;
		if (!FloatEnumTypeTest())
			pass = false;
		if (!DoubleEnumTypeTest())
			pass = false;

		if (!pass)
			throw new Exception("MarshallEnumTypesTest failed");
	}

	/*
	 * @testName: MarshallAnonymousTypeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallAnonymousTypeTest() throws Exception {
		logger.log(Level.INFO, "MarshallAnonymousTypeTest");
		boolean pass = true;

		try {
			init_FooAnonymousType_Data();
			FooAnonymousType request = FooAnonymousType_data;
			FooAnonymousType response = port2.echoAnonymousTypeTest(request);
			if (!compareFooAnonymousTypeData(request, response))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallAnonymousTypeTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallAnonymousTypeTest failed");
	}

	/*
	 * @testName: MarshallVariousSchemaTypesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallVariousSchemaTypesTest() throws Exception {
		logger.log(Level.INFO, "MarshallVariousSchemaTypesTest");
		boolean pass = true;

		try {
			init_FooVariousSchemaTypes_Data();
			FooVariousSchemaTypes request = FooVariousSchemaTypes_data;
			logger.log(Level.INFO, "Send: " + request.getFooA() + "|" + request.getFooB() + "|" + request.getFooC()
					+ "|" + request.getFooD() + "|" + request.getFooE() + "|" + request.getFooF());
			FooVariousSchemaTypes response = port2.echoVariousSchemaTypesTest(request);
			logger.log(Level.INFO, "Recv: " + response.getFooA() + "|" + response.getFooB() + "|" + response.getFooC()
					+ "|" + response.getFooD() + "|" + response.getFooE() + "|" + response.getFooF());
			if (response.getFooA() == request.getFooA() && response.getFooB().equals(request.getFooB())
					&& response.getFooC().equals(request.getFooC()) && response.getFooD().equals(request.getFooD())
					&& response.getFooE() == request.getFooE() && response.getFooF() == request.getFooF()) {
				logger.log(Level.INFO, "Result match");
			} else {
				TestUtil.logErr("Result mismatch");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallVariousSchemaTypesTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallVariousSchemaTypesTest failed");
	}

	/*
	 * @testName: MarshallVariousSchemaTypesListTypeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040; JAXWS:SPEC:2080;
	 * JAXWS:SPEC:3054; JAXWS:SPEC:3052; JAXWS:SPEC:2084; JAXWS:SPEC:2085;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallVariousSchemaTypesListTypeTest() throws Exception {
		logger.log(Level.INFO, "MarshallVariousSchemaTypesListTypeTest");
		boolean pass = true;

		try {
			init_FooVariousSchemaTypes_Data();
			FooVariousSchemaTypesListType request = FooVariousSchemaTypesListType_data;

			FooVariousSchemaTypes fooSend[] = request.getFooA()
					.toArray(new FooVariousSchemaTypes[request.getFooA().size()]);
			FooVariousSchemaTypesListType response = port2.echoVariousSchemaTypesListTypeTest(request);
			FooVariousSchemaTypes fooRecv[] = response.getFooA()
					.toArray(new FooVariousSchemaTypes[response.getFooA().size()]);
			if (fooRecv.length == fooSend.length) {
				for (int i = 0; i < fooSend.length; i++) {
					if (fooRecv[i].getFooA() == fooSend[i].getFooA()
							&& fooRecv[i].getFooB().equals(fooSend[i].getFooB())
							&& fooRecv[i].getFooC().equals(fooSend[i].getFooC())
							&& fooRecv[i].getFooD().equals(fooSend[i].getFooD())) {
						logger.log(Level.INFO, "Result match");
					} else {
						TestUtil.logErr("Result mismatch");
						logger.log(Level.INFO,
								"Send: " + fooSend[i].getFooA() + "|" + fooSend[i].getFooB() + "|"
										+ fooSend[i].getFooC() + "|" + fooSend[i].getFooD() + "|" + fooSend[i].getFooE()
										+ "|" + fooSend[i].getFooF());
						logger.log(Level.INFO,
								"Recv: " + fooRecv[i].getFooA() + "|" + fooRecv[i].getFooB() + "|"
										+ fooRecv[i].getFooC() + "|" + fooRecv[i].getFooD() + "|" + fooRecv[i].getFooE()
										+ "|" + fooRecv[i].getFooF());
						pass = false;
					}
				}
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallVariousSchemaTypesListTypeTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallVariousSchemaTypesListTypeTest failed");
	}

	/*
	 * @testName: MarshallAnnotationTypeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallAnnotationTypeTest() throws Exception {
		logger.log(Level.INFO, "MarshallAnnotationTypeTest");
		boolean pass = true;

		FooAnnotationType request = FooAnnotationType.UNKNOWN;
		try {
			FooAnnotationType response = port2.echoAnnotationTypeTest(request);
			if (!response.equals(request)) {
				TestUtil.logErr("Wrong response expected: <" + request + ">, received: <" + response + ">");
				pass = false;
			} else {
				logger.log(Level.INFO, "Correct response expected: <" + request + ">, received: <" + response + ">");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallAnnotationTypeTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallAnnotationTypeTest failed");
	}

	/*
	 * @testName: MarshallAnySimpleTypeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallAnySimpleTypeTest() throws Exception {
		logger.log(Level.INFO, "MarshallAnySimpleTypeTest");
		boolean pass = true;

		String request = "hello,there";
		FooAnySimpleType req = null;
		try {
			req = new FooAnySimpleType();
			req.setFooAnySimpleType(request);
			FooAnySimpleType response = port2.echoAnySimpleTypeTest(req);
			String res = (String) response.getFooAnySimpleType();
			if (!res.equals(request)) {
				TestUtil.logErr("Wrong response expected: <" + request + ">, received: <" + res + ">");
				pass = false;
			} else {
				logger.log(Level.INFO, "Correct response expected: <" + request + ">, received: <" + res + ">");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallAnySimpleTypeTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallAnySimpleTypeTest failed");
	}

	/*
	 * @testName: MarshallAnyURITypeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallAnyURITypeTest() throws Exception {
		logger.log(Level.INFO, "MarshallAnyURITypeTest");
		boolean pass = true;

		try {
			String uriReq = "http://example.com/myURI";
			FooAnyURIType request = new FooAnyURIType();
			request.setFooAnyURIType(uriReq);
			FooAnyURIType response = port2.echoAnyURITypeTest(request);
			String uriRes = response.getFooAnyURIType();
			if (!uriRes.equals(uriReq)) {
				TestUtil.logErr("Wrong response expected: <" + uriReq + ">, received: <" + uriRes + ">");
				pass = false;
			} else {
				logger.log(Level.INFO, "Correct response expected: <" + uriReq + ">, received: <" + uriRes + ">");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallAnyURITypeTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallAnyURITypeTest failed");
	}

	/*
	 * @testName: MarshallLanguageTypeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallLanguageTypeTest() throws Exception {
		logger.log(Level.INFO, "MarshallLanguageTypeTest");
		boolean pass = true;

		String request = "english";
		LanguageElem req = null;

		try {
			req = new LanguageElem();
			req.setLanguageElem(request);
			LanguageElem ret = port2.echoLanguageTypeTest(req);
			String response = ret.getLanguageElem();
			if (!response.equals(request)) {
				TestUtil.logErr("Wrong response expected: <" + request + ">, received: <" + response + ">");
				pass = false;
			} else {
				logger.log(Level.INFO, "Correct response expected: <" + request + ">, received: <" + response + ">");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallLanguageTypeTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallLanguageTypeTest failed");
	}

	/*
	 * @testName: MarshallTokenTypeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallTokenTypeTest() throws Exception {
		logger.log(Level.INFO, "MarshallTokenTypeTest");
		boolean pass = true;

		String request = "token";
		TokenElem req = null;
		try {
			req = new TokenElem();
			req.setTokenElem(request);
			TokenElem ret = port2.echoTokenTypeTest(req);
			String response = ret.getTokenElem();
			if (!response.equals(request)) {
				TestUtil.logErr("Wrong response expected: <" + request + ">, received: <" + response + ">");
				pass = false;
			} else {
				logger.log(Level.INFO, "Correct response expected: <" + request + ">, received: <" + response + ">");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallTokenTypeTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallTokenTypeTest failed");
	}

	/*
	 * @testName: MarshallNameTypeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallNameTypeTest() throws Exception {
		logger.log(Level.INFO, "MarshallNameTypeTest");
		boolean pass = true;

		String request = "name";
		NameElem req = null;
		try {
			req = new NameElem();
			req.setNameElem(request);
			NameElem ret = port2.echoNameTypeTest(req);
			String response = ret.getNameElem();
			if (!response.equals(request)) {
				TestUtil.logErr("Wrong response expected: <" + request + ">, received: <" + response + ">");
				pass = false;
			} else {
				logger.log(Level.INFO, "Correct response expected: <" + request + ">, received: <" + response + ">");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallNameTypeTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallNameTypeTest failed");
	}

	/*
	 * @testName: MarshallNCNameTypeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallNCNameTypeTest() throws Exception {
		logger.log(Level.INFO, "MarshallNCNameTypeTest");
		boolean pass = true;

		String request = "ncname";
		NCNameElem req = null;
		try {
			req = new NCNameElem();
			req.setNCNameElem(request);
			NCNameElem ret = port2.echoNCNameTypeTest(req);
			String response = ret.getNCNameElem();
			if (!response.equals(request)) {
				TestUtil.logErr("Wrong response expected: <" + request + ">, received: <" + response + ">");
				pass = false;
			} else {
				logger.log(Level.INFO, "Correct response expected: <" + request + ">, received: <" + response + ">");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallNCNameTypeTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallNCNameTypeTest failed");
	}

	/*
	 * @testName: MarshallIDTypeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallIDTypeTest() throws Exception {
		logger.log(Level.INFO, "MarshallIDTypeTest");
		boolean pass = true;

		String request = "id";
		IDElem req = null;
		try {
			req = new IDElem();
			req.setIDElem(request);
			IDElem ret = port2.echoIDTypeTest(req);
			String response = ret.getIDElem();
			if (!response.equals(request)) {
				TestUtil.logErr("Wrong response expected: <" + request + ">, received: <" + response + ">");
				pass = false;
			} else {
				logger.log(Level.INFO, "Correct response expected: <" + request + ">, received: <" + response + ">");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallIDTypeTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallIDTypeTest failed");
	}

	/*
	 * @testName: MarshallStructXMLSchemaTypesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method voidTest.
	 * Verify normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallStructXMLSchemaTypesTest() throws Exception {
		logger.log(Level.INFO, "MarshallStructXMLSchemaTypesTest");
		boolean pass = true;

		try {
			InitExpectedFooTypeData();
			if (!sendFoo1Test())
				pass = false;
			if (!sendFoo2Test())
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallStructXMLSchemaTypesTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallStructXMLSchemaTypesTest failed");
	}

	// =======================================================================
	// Marshall Literal Exceptions (Foobad1 ... FooBad5)
	// =======================================================================
	/*
	 * @testName: MarshallLiteralFaultsTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040; JAXWS:SPEC:2044;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method voidTest.
	 * Verify literal Exceptions.
	 *
	 * Description
	 */
	@Test
	public void MarshallLiteralFaultsTest() throws Exception {
		logger.log(Level.INFO, "MarshallLiteralFaults");
		boolean pass = true;

		if (!fooFaultTest())
			pass = false;

		if (!pass)
			throw new Exception("MarshallLiteralFaults failed");
	}

	// =======================================================================
	// Marshall XML Schema Complex Types (Person, Employee, Document)
	// =======================================================================
	/*
	 * @testName: MarshallComplexTypesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC methods. Test with
	 * complex types. For each type pass its value as input to the corresponding RPC
	 * method and receive it back as the return value. Compare results of each
	 * value/type of what was sent and what was returned. Verify they are equal.
	 *
	 */
	@Test
	public void MarshallComplexTypesTest() throws Exception {
		String testname = "MarshallComplexTypesTest";
		TestUtil.logTrace(testname);
		boolean pass = true;
		try {
			boolean b1 = doPersonTest();
			if (b1)
				logger.log(Level.INFO, "Person echo success!");
			boolean b2 = doEmployeeTest();
			if (b2)
				logger.log(Level.INFO, "Employee echo success!");
			boolean b3 = doDocumentTest();
			if (b3)
				logger.log(Level.INFO, "Document echo success!");
			if (!b1 || !b2 || !b3)
				pass = false;

		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception(testname);
		}

		if (!pass)
			throw new Exception(testname + " failed");
	}

	// ====================================================================
	// One Way Request
	// ====================================================================

	/*
	 * @testName: MarshallOneWayTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method voidTest.
	 * Verify normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallOneWayTest() throws Exception {
		logger.log(Level.INFO, "MarshallOneWayTest");
		boolean pass = true;
		logger.log(Level.INFO, "Testing oneway operation to JAXWS Service");
		try {
			OneWayMessage v = new OneWayMessage();
			v.setStringValue("A One Way Test");
			port4.oneWayMethod(v);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallOneWayTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallOneWayTest failed");
	}

	// ====================================================================
	// Other Simple Types
	// ====================================================================

	/*
	 * @testName: MarshallOtherSimpleTypesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC methods for each
	 * standard java class type. For each other simple type above (QName,
	 * Base64Binary, and HexBinary) pass its value as input to the corresponding RPC
	 * method and receive it back as the return value. Compare results of each
	 * value/type of what was sent sent and what was returned. Verify they are
	 * equal.
	 */
	@Test
	public void MarshallOtherSimpleTypesTest() throws Exception {
		logger.log(Level.INFO, "MarshallOtherSimpleTypesTest");
		boolean pass = true;

		if (!QNameTest())
			pass = false;
		if (!Base64BinaryTest())
			pass = false;
		if (!HexBinaryTest())
			pass = false;

		if (!pass)
			throw new Exception("MarshallOtherSimpleTypesTest failed");
	}

	/*
	 * @testName: MarshallUnsignedTypesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC methods for each
	 * standard java class type. For each supported unsigned simple type pass its
	 * value as input to the corresponding RPC method and receive it back as the
	 * return value. Compare results of each value/type of what was sent sent and
	 * what was returned. Verify they are equal.
	 */
	@Test
	public void MarshallUnsignedTypesTest() throws Exception {
		logger.log(Level.INFO, "MarshallUnsignedTypesTest");
		boolean pass = true;

		if (!UnsignedShortTest())
			pass = false;
		if (!UnsignedIntTest())
			pass = false;
		if (!UnsignedByteTest())
			pass = false;
		if (!UnsignedLongTest())
			pass = false;

		if (!pass)
			throw new Exception("MarshallUnsignedTypesTest failed");
	}

	/*
	 * @testName: MarshallBigIntegerTypesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC methods for each
	 * standard java class type. For each supported unsigned simple type pass its
	 * value as input to the corresponding RPC method and receive it back as the
	 * return value. Compare results of each value/type of what was sent sent and
	 * what was returned. Verify they are equal.
	 */
	@Test
	public void MarshallBigIntegerTypesTest() throws Exception {
		logger.log(Level.INFO, "MarshallBigIntegerTypesTest");
		boolean pass = true;

		if (!NonPositiveIntegerTest())
			pass = false;
		if (!NonNegativeIntegerTest())
			pass = false;
		if (!PositiveIntegerTest())
			pass = false;
		if (!NegativeIntegerTest())
			pass = false;

		if (!pass)
			throw new Exception("MarshallBigIntegerTypesTest failed");
	}

	/*
	 * @testName: MarshallDateTimeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method. Verify
	 * normal invocation and return.
	 *
	 * Description
	 */
	@Test
	public void MarshallDateTimeTest() throws Exception {
		logger.log(Level.INFO, "MarshallDateTimeTest");
		boolean pass = true;

		if (!TimeTest())
			pass = false;
		if (!DateTest())
			pass = false;
		if (!GYearMonthTest())
			pass = false;
		if (!GYearTest())
			pass = false;
		if (!GMonthDayTest())
			pass = false;
		if (!GDayTest())
			pass = false;
		if (!GMonthTest())
			pass = false;

		if (!pass)
			throw new Exception("MarshallDateTimeTest failed");
	}

	/*
	 * @testName: MarshallListTypesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040; JAXWS:SPEC:2080;
	 * JAXWS:SPEC:3054; JAXWS:SPEC:3052; JAXWS:SPEC:2084; JAXWS:SPEC:2085;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC methods for each
	 * standard java class type. For each supported unsigned simple type pass its
	 * value as input to the corresponding RPC method and receive it back as the
	 * return value. Compare results of each value/type of what was sent sent and
	 * what was returned. Verify they are equal.
	 */
	@Test
	public void MarshallListTypesTest() throws Exception {
		logger.log(Level.INFO, "MarshallListTypesTest");
		boolean pass = true;

		if (!StringListTest())
			pass = false;
		if (!IntListTest())
			pass = false;
		if (!FloatListTest())
			pass = false;
		if (!DecimalListTest())
			pass = false;
		if (!DoubleListTest())
			pass = false;
		if (!IntegerListTest())
			pass = false;
		if (!LongListTest())
			pass = false;
		if (!ShortListTest())
			pass = false;
		if (!ByteListTest())
			pass = false;

		if (!pass)
			throw new Exception("MarshallListTypesTest failed");
	}

	/*
	 * @testName: MarshallDurationTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy:
	 */
	@Test
	public void MarshallDurationTest() throws Exception {
		logger.log(Level.INFO, "MarshallDurationTest");
		boolean pass = true;
		Duration values[] = JAXWS_Data.Duration_data;
		DurationTestResponse response;
		DurationTest request;
		try {
			logger.log(Level.INFO, "Passing/Returning Duration class to/from JAXWS Service");
			for (int i = 0; i < values.length; i++) {
				request = new DurationTest();
				request.setDurationValue(values[i]);
				response = port.durationTest(request);
				if (!JAXWS_Data.compareValues(values[i], response.getDurationValue(), "Duration"))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("MarshallDurationTest failed");
	}

	/*
	 * @testName: MarshallMapSimpleTypesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: In NewSchemaDefs.xsd create a schema of simpletypes derived
	 * via restriction for string, integer, int, long, short, decimal, float,
	 * double, boolean, byte and qname. Verify that what was sent to the service
	 * endpoint is echoed back. Description
	 */
	@Test
	public void MarshallMapSimpleTypesTest() throws Exception {
		logger.log(Level.INFO, "MarshallMapSimpleTypesTest");
		boolean pass = true;
		String request = "This is from MarshallMapSimpleTypesTest";
		BigDecimalTestResponse bdResponse;
		BigDecimalTest bdRequest;
		logger.log(Level.INFO, "Top of  MarshallMapSimpleTypesTest");
		try {
			String response = port2.echoFooStringTypeTest(request);
			logger.log(Level.INFO, "Received response: " + response);
			if (!request.equals(response)) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Simple String passes");

			// -----------------------------------------------
			BigInteger birequest = new BigInteger("5");
			BigInteger biresponse = port2.echoFooIntegerTypeTest(birequest);
			if (!birequest.toString().equals(biresponse.toString())) {
				TestUtil.logErr(
						"Result mismatch - expected: " + birequest.toString() + ", received: " + biresponse.toString());
				TestUtil.logErr("Integer failed");
				pass = false;

			} else
				logger.log(Level.INFO, "Simple Integer passes");

			// -----------------------------------------------
			int irequest = 10;
			int iresponse = port2.echoFooIntTypeTest(irequest);
			if (irequest != iresponse) {
				TestUtil.logErr("Result mismatch - expected: " + irequest + ", received: " + iresponse);
				TestUtil.logErr("Simple int failed");
				pass = false;
			} else
				logger.log(Level.INFO, "Simple int passes");

			// -----------------------------------------------

			long lrequest = 1000000000000000000L;
			long lresponse = port2.echoFooLongTypeTest(lrequest);
			if (lrequest != lresponse) {
				TestUtil.logErr("Result mismatch - expected: " + lrequest + ", received: " + lresponse);
				TestUtil.logErr("Simple long failed");
				pass = false;

			} else
				logger.log(Level.INFO, "Simple long passes");

			// -----------------------------------------------

			short srequest = 32765;
			short sresponse = port2.echoFooShortTypeTest(srequest);
			if (srequest != sresponse) {
				TestUtil.logErr("Result mismatch - expected: " + srequest + ", received: " + sresponse);
				TestUtil.logErr("Simple short failed");
				pass = false;

			} else
				logger.log(Level.INFO, "Simple short passes");

			// -----------------------------------------------
			logger.log(Level.INFO, "Testing BigDecimal");
			BigDecimal bdrequest = new BigDecimal("3512359.1456");
			BigDecimal bdresponse = port2.echoFooDecimalTypeTest(bdrequest);
			if (!bdrequest.toString().equals(bdresponse.toString())) {
				TestUtil.logErr(
						"Result mismatch - expected: " + bdrequest.toString() + ", received: " + bdresponse.toString());
				TestUtil.logErr("decimal failed");
				pass = false;

			} else
				logger.log(Level.INFO, "Simple decimal passes");

			// -----------------------------------------------
			logger.log(Level.INFO, "Testing float");
			float frequest = 489.57f;
			float fresponse = port2.echoFooFloatTypeTest(frequest);
			if (frequest != fresponse) {
				TestUtil.logErr("Result mismatch - expected: " + frequest + ", received: " + fresponse);
				TestUtil.logErr("float failed");
				pass = false;

			} else
				logger.log(Level.INFO, "Simple float passes");

			// -----------------------------------------------
			logger.log(Level.INFO, "Testing double ");
			double drequest = 5;
			double dresponse = port2.echoFooDoubleTypeTest(drequest);
			if (drequest != dresponse) {
				TestUtil.logErr("Result mismatch - expected: " + drequest + ", received: " + dresponse);
				TestUtil.logErr("double failed");
				pass = false;

			} else
				logger.log(Level.INFO, "Simple double passes");

			// -----------------------------------------------
			logger.log(Level.INFO, "Testing boolean");
			boolean brequest = true;
			boolean bresponse = port2.echoFooBooleanTypeTest(brequest);
			if (brequest != bresponse) {
				TestUtil.logErr("Result mismatch - expected: " + brequest + ", received: " + bresponse);
				TestUtil.logErr("boolean failed");
				pass = false;

			} else
				logger.log(Level.INFO, "Simple boolean passes");
			// -----------------------------------------------
			logger.log(Level.INFO, "Testing byte");
			byte btrequest = 127;
			byte btresponse = port2.echoFooByteTypeTest(btrequest);
			if (btrequest != btresponse) {
				TestUtil.logErr("Result mismatch - expected: " + btrequest + ", received: " + btresponse);
				TestUtil.logErr("byte failed");
				pass = false;

			} else
				logger.log(Level.INFO, "Simple byte passes");

			// -----------------------------------------------
			logger.log(Level.INFO, "Testing QName");
			QName qnrequest = new QName("http://marshalltestservice.org/types2", "localPart");
			QName qnresponse = port2.echoFooQNameTypeTest(qnrequest);
			if (!qnresponse.toString().equals(qnrequest.toString())) {
				TestUtil.logErr(
						"Result mismatch - expected: " + qnrequest.toString() + ", received: " + qnresponse.toString());
				TestUtil.logErr("QName failed");
				pass = false;

			} else
				logger.log(Level.INFO, "Simple QName passes");

		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		if (!pass)
			throw new Exception("MarshallMapSimpleTypesTest failed");
	}

	/*
	 * @testName: MarshallIncludedStringTypeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 *
	 * @test_Strategy: test xsd:include by including a schema with a simpleType in
	 * NewSchemaDefs.xsd.
	 *
	 *
	 * Description
	 */
	@Test
	public void MarshallIncludedStringTypeTest() throws Exception {
		logger.log(Level.INFO, "MarshallIncludedStringTypeTest");
		boolean pass = true;
		com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.marshalltest.IncludedStringRequest request = null;
		String myString = "Please echo this back to me ";
		com.sun.ts.tests.jaxws.ee.w2j.rpc.literal.marshalltest.IncludedStringResponse response = null;
		try {
			request = new IncludedStringRequest();
			request.setMyString(myString);
			response = port2.echoIncludedStringTest(request);
			String resp = response.getMyString();
			if (!myString.equals(resp)) {
				pass = false;
				logger.log(Level.INFO, "Expected: " + myString + " but returned " + resp);
			} else
				logger.log(Level.INFO, "Good String echoed back as expected");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("MarshallNOTATIONTypeTest failed", e);
		}

		if (!pass)
			throw new Exception("MarshallIncludedStringTypeTest failed");
	}

	/*
	 * @testName: ClientPassNullTest
	 *
	 * @assertion_ids: JAXWS:SPEC:3040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method voidTest.
	 *
	 */
	@Test
	public void ClientPassNullTest() throws Exception {
		logger.log(Level.INFO, "ClientPassNullTest");
		boolean pass = true;
		String2TestResponse response;
		String2Test request;
		try {
			logger.log(Level.INFO, "Try passing null to endpoint method");
			port.nullTest(null);
			TestUtil.logErr("WebServiceException was not thrown when client tries passing a null");
			pass = false;
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "WebServiceException was thrown when client tries passing a null");
		}
		if (!pass)
			throw new Exception("ClientPassNullTest failed");
	}

	/*
	 * @testName: EndpointPassNullTest
	 *
	 * @assertion_ids: JAXWS:SPEC:3040;
	 *
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC method voidTest.
	 *
	 */
	@Test
	public void EndpointPassNullTest() throws Exception {
		logger.log(Level.INFO, "EndpointPassNullTest");
		boolean pass = true;
		String2TestResponse response;
		String2Test request;
		try {
			logger.log(Level.INFO, "Endpoint tries returning a null to client");
			request = new String2Test();
			request.setStringValue("foobar");
			port.nullTest(request);
			TestUtil.logErr("WebServiceException was not thrown when endpoint tries passing a null");
			pass = false;
		} catch (WebServiceException e) {
			logger.log(Level.INFO, "WebServiceException was thrown when endpoint tries passing a null");
		}
		if (!pass)
			throw new Exception("EndpointPassNullTest failed");
	}

	private boolean printTestStatus(boolean pass, String test) {
		if (pass)
			logger.log(Level.INFO, "" + test + " ... PASSED");
		else
			TestUtil.logErr("" + test + " ... FAILED");

		return pass;
	}

	private boolean sendFoo1Test() {
		logger.log(Level.INFO, "MarshallOtherXMLSchemaTypesTest:(sendFoo1Test)");
		boolean pass = true;
		logger.log(Level.INFO, "Passing/Returning OtherXMLSchemaDataTypes to/from JAXWS Service");
		try {
			FooStatusType response = port2.sendFoo1Test(FooType_data);
			if (!response.isFooA())
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallOtherXMLSchemaTypesTest:(sendFoo1Test)");
		return pass;
	}

	private boolean sendFoo2Test() {
		logger.log(Level.INFO, "MarshallStructXMLSchemaTypesTest:(sendFoo2Test)");
		boolean pass = true;
		logger.log(Level.INFO, "Passing/Returning StructXMLSchemaDataTypes to/from JAXWS Service");
		try {
			FooType response = port2.sendFoo2Test(FooType_data);
			if (!CompareWithExpectedFooTypeData(response))
				pass = false;
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallStructXMLSchemaTypesTest:(sendFoo2Test)");
		return pass;
	}

	private boolean fooFaultTest() {
		logger.log(Level.INFO, "MarshallLiteralFaultsTest:(fooFaultTest)");
		boolean pass = true;
		logger.log(Level.INFO, "Testing Literal Exceptions");
		try {
			logger.log(Level.INFO, "Throw a FooFault with reason - FooBad1");
			FooStringRequest f = new FooStringRequest();
			f.setVarString("FooBad1");
			FooStringResponse response = port2.fooFaultTest(f);
			TestUtil.logErr("Did not throw a FooFault");
			pass = false;
		} catch (FooFault e) {
			logger.log(Level.INFO, "Did throw a FooFault");
			FooFaultException ffe = e.getFaultInfo();
			logger.log(Level.INFO, "Reason=" + ffe.getWhyTheFault());
			if (ffe.getWhyTheFault().equals("FooBad1"))
				logger.log(Level.INFO, "Reason for exception - expected");
			else {
				pass = false;
				TestUtil.logErr("Reason for exception - unexpected", e);
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		try {
			logger.log(Level.INFO, "Throw a FooFault with reason - FooBad5");
			FooStringRequest f = new FooStringRequest();
			f.setVarString("FooBad5");
			FooStringResponse response = port2.fooFaultTest(f);
			TestUtil.logErr("Did not throw a FooFault");
			pass = false;
		} catch (FooFault e) {
			logger.log(Level.INFO, "Did throw a FooFault");
			FooFaultException ffe = e.getFaultInfo();
			logger.log(Level.INFO, "Reason=" + ffe.getWhyTheFault());
			if (ffe.getWhyTheFault().equals("FooBad5"))
				logger.log(Level.INFO, "Reason for exception - expected");
			else {
				pass = false;
				TestUtil.logErr("Reason for exception - unexpected", e);
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallLiteralFaultsTest:(fooFaultTest)");
		return pass;
	}

	private boolean booleanTest() {
		logger.log(Level.INFO, "MarshallPrimitiveTest:(booleanTest)");
		boolean pass = true;
		boolean values[] = JAXWS_Data.boolean_data;
		BooleanTestResponse response;
		BooleanTest request;
		logger.log(Level.INFO, "Passing/Returning boolean data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new BooleanTest();
				request.setBooleanValue(values[i]);
				response = port.booleanTest(request);
				if (!JAXWS_Data.compareValues(values[i], response.isBooleanValue()))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallPrimitiveTest:(booleanTest)");
		return pass;
	}

	private boolean byteTest() {
		logger.log(Level.INFO, "MarshallPrimitiveTest:(byteTest)");
		boolean pass = true;
		byte values[] = JAXWS_Data.byte_data;
		ByteTestResponse response;
		ByteTest request;
		logger.log(Level.INFO, "Passing/Returning byte data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new ByteTest();
				request.setByteValue(values[i]);
				response = port.byteTest(request);
				if (!JAXWS_Data.compareValues(values[i], response.getByteValue()))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallPrimitiveTest:(byteTest)");
		return pass;
	}

	private boolean shortTest() {
		logger.log(Level.INFO, "MarshallPrimitiveTest:(shortTest)");
		boolean pass = true;
		short values[] = JAXWS_Data.short_data;
		ShortTestResponse response;
		ShortTest request;
		logger.log(Level.INFO, "Passing/Returning short data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new ShortTest();
				request.setShortValue(values[i]);
				response = port.shortTest(request);
				if (!JAXWS_Data.compareValues(values[i], response.getShortValue()))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallPrimitiveTest:(shortTest)");
		return pass;
	}

	private boolean intTest() {
		logger.log(Level.INFO, "MarshallPrimitiveTest:(intTest)");
		boolean pass = true;
		int values[] = JAXWS_Data.int_data;
		IntTestResponse response;
		IntTest request;
		logger.log(Level.INFO, "Passing/Returning int data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new IntTest();
				request.setIntValue(values[i]);
				response = port.intTest(request);
				if (!JAXWS_Data.compareValues(values[i], response.getIntValue()))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallPrimitiveTest:(intTest)");
		return pass;
	}

	private boolean longTest() {
		logger.log(Level.INFO, "MarshallPrimitiveTest:(longTest)");
		boolean pass = true;
		long values[] = JAXWS_Data.long_data;
		LongTestResponse response;
		LongTest request;
		logger.log(Level.INFO, "Passing/Returning long data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new LongTest();
				request.setLongValue(values[i]);
				response = port.longTest(request);
				if (!JAXWS_Data.compareValues(values[i], response.getLongValue()))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallPrimitiveTest:(longTest)");
		return pass;
	}

	private boolean floatTest() {
		logger.log(Level.INFO, "MarshallPrimitiveTest:(floatTest)");
		boolean pass = true;
		float values[] = JAXWS_Data.float_data;
		FloatTestResponse response;
		FloatTest request;
		logger.log(Level.INFO, "Passing/Returning float data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new FloatTest();
				request.setFloatValue(values[i]);
				response = port.floatTest(request);
				if (!JAXWS_Data.compareValues(values[i], response.getFloatValue()))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallPrimitiveTest:(floatTest)");
		return pass;
	}

	private boolean doubleTest() {
		logger.log(Level.INFO, "MarshallPrimitiveTest:(doubleTest)");
		boolean pass = true;
		double values[] = JAXWS_Data.double_data;
		DoubleTestResponse response;
		DoubleTest request;
		logger.log(Level.INFO, "Passing/Returning double data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new DoubleTest();
				request.setDoubleValue(values[i]);
				response = port.doubleTest(request);
				if (!JAXWS_Data.compareValues(values[i], response.getDoubleValue()))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallPrimitiveTest:(doubleTest)");
		return pass;
	}

	private boolean StringTest() {
		logger.log(Level.INFO, "MarshallStandardJavaClassesTest:(StringTest)");
		boolean pass = true;
		String values[] = JAXWS_Data.String_data;
		StringTestResponse response;
		StringTest request;
		try {
			logger.log(Level.INFO, "Passing/Returning String class to/from JAXWS Service");
			for (int i = 0; i < values.length; i++) {
				request = new StringTest();
				request.setStringValue(values[i]);
				response = port.stringTest(request);
				if (!JAXWS_Data.compareValues(values[i], response.getStringValue(), "String"))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallStandardJavaClassesTest:(StringTest)");
		return pass;
	}

	private boolean CalendarTest() {
		logger.log(Level.INFO, "MarshallStandardJavaClassesTest:(CalendarTest)");
		boolean pass = true;
		XMLGregorianCalendar values[] = JAXWS_Data.XMLGregorianCalendar_data;
		CalendarTestResponse response;
		CalendarTest request;
		logger.log(Level.INFO, "Passing/Returning Calendar class to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new CalendarTest();
				request.setCalendar(values[i]);
				response = port.calendarTest(request);
				if (!JAXWS_Data.compareValues(values[i], response.getCalendar(), "XMLGregorianCalendar"))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallStandardJavaClassesTest:(CalendarTest)");
		return pass;
	}

	private boolean BigIntegerTest() {
		logger.log(Level.INFO, "MarshallStandardJavaClassesTest:(BigIntegerTest)");
		boolean pass = true;
		BigInteger values[] = JAXWS_Data.BigInteger_data;
		BigIntegerTestResponse response;
		BigIntegerTest request;
		logger.log(Level.INFO, "Passing/Returning BigInteger class to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new BigIntegerTest();
				request.setBigInteger(values[i]);
				response = port.bigIntegerTest(request);
				if (!JAXWS_Data.compareValues(values[i], response.getBigInteger(), "BigInteger"))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallStandardJavaClassesTest:(BigIntegerTest)");
		return pass;
	}

	private boolean BigDecimalTest() {
		logger.log(Level.INFO, "MarshallStandardJavaClassesTest:(BigDecimalTest)");
		boolean pass = true;
		BigDecimal values[] = JAXWS_Data.BigDecimal_data;
		BigDecimalTestResponse response;
		BigDecimalTest request;
		logger.log(Level.INFO, "Passing/Returning BigDecimal class to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new BigDecimalTest();
				request.setBigDecimal(values[i]);
				response = port.bigDecimalTest(request);
				if (!JAXWS_Data.compareValues(values[i], response.getBigDecimal(), "BigDecimal"))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallStandardJavaClassesTest:(BigDecimalTest)");
		return pass;
	}

	private boolean booleanArrayTest() {
		logger.log(Level.INFO, "MarshallJavaArrayTest:(booleanArrayTest)");
		boolean pass = true;
		boolean values[] = JAXWS_Data.boolean_data;
		BooleanArrayTestResponse response;
		BooleanArrayTest request;
		logger.log(Level.INFO, "Passing/Returning boolean array to/from JAXWS Service");
		try {
			request = new BooleanArrayTest();
			for (int i = 0; i < values.length; i++)
				request.getBooleanArray().add(values[i]);
			response = port.booleanArrayTest(request);
			pass = JAXWS_Data.compareArrayValues(values, response.getBooleanArray(), "boolean");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallJavaArrayTest:(booleanArrayTest)");
		return pass;
	}

	private boolean byteArrayTest() {
		logger.log(Level.INFO, "MarshallJavaArrayTest:(byteArrayTest)");
		boolean pass = true;
		byte values[] = JAXWS_Data.byte_data;
		ByteArrayTestResponse response;
		ByteArrayTest request;
		try {
			request = new ByteArrayTest();
			request.setByteArray(values);
			response = port.byteArrayTest(request);
			pass = JAXWS_Data.compareArrayValues(values, response.getByteArray(), "byte");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallJavaArrayTest:(byteArrayTest)");
		return pass;
	}

	private boolean shortArrayTest() {
		logger.log(Level.INFO, "MarshallJavaArrayTest:(shortArrayTest)");
		boolean pass = true;
		short values[] = JAXWS_Data.short_data;
		ShortArrayTestResponse response;
		ShortArrayTest request;
		try {
			request = new ShortArrayTest();
			for (int i = 0; i < values.length; i++)
				request.getShortArray().add(values[i]);
			response = port.shortArrayTest(request);
			pass = JAXWS_Data.compareArrayValues(values, response.getShortArray(), "short");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallJavaArrayTest:(shortArrayTest)");
		return pass;
	}

	private boolean intArrayTest() {
		logger.log(Level.INFO, "MarshallJavaArrayTest:(intArrayTest)");
		boolean pass = true;
		int values[] = JAXWS_Data.int_data;
		IntArrayTestResponse response;
		IntArrayTest request;
		try {
			request = new IntArrayTest();
			for (int i = 0; i < values.length; i++)
				request.getIntArray().add(values[i]);
			response = port.intArrayTest(request);
			pass = JAXWS_Data.compareArrayValues(values, response.getIntArray(), "int");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallJavaArrayTest:(intArrayTest)");
		return pass;
	}

	private boolean longArrayTest() {
		logger.log(Level.INFO, "MarshallJavaArrayTest:(longArrayTest)");
		boolean pass = true;
		long values[] = JAXWS_Data.long_data;
		LongArrayTestResponse response;
		LongArrayTest request;
		try {
			request = new LongArrayTest();
			for (int i = 0; i < values.length; i++)
				request.getLongArray().add(values[i]);
			response = port.longArrayTest(request);
			pass = JAXWS_Data.compareArrayValues(values, response.getLongArray(), "long");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallJavaArrayTest:(longArrayTest)");
		return pass;
	}

	private boolean floatArrayTest() {
		logger.log(Level.INFO, "MarshallJavaArrayTest:(floatArrayTest)");
		boolean pass = true;
		float values[] = JAXWS_Data.float_data;
		FloatArrayTestResponse response;
		FloatArrayTest request;
		try {
			request = new FloatArrayTest();
			for (int i = 0; i < values.length; i++)
				request.getFloatArray().add(values[i]);
			response = port.floatArrayTest(request);
			pass = JAXWS_Data.compareArrayValues(values, response.getFloatArray(), "float");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallJavaArrayTest:(floatArrayTest)");
		return pass;
	}

	private boolean doubleArrayTest() {
		logger.log(Level.INFO, "MarshallJavaArrayTest:(doubleArrayTest)");
		boolean pass = true;
		double values[] = JAXWS_Data.double_data;
		DoubleArrayTestResponse response;
		DoubleArrayTest request;
		try {
			request = new DoubleArrayTest();
			for (int i = 0; i < values.length; i++)
				request.getDoubleArray().add(values[i]);
			response = port.doubleArrayTest(request);
			pass = JAXWS_Data.compareArrayValues(values, response.getDoubleArray(), "double");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallJavaArrayTest:(doubleArrayTest)");
		return pass;
	}

	private boolean StringArrayTest() {
		logger.log(Level.INFO, "MarshallJavaArrayTest:(StringArrayTest)");
		boolean pass = true;
		String values[] = JAXWS_Data.String_nonull_data;
		StringArrayTestResponse response;
		StringArrayTest request;
		try {
			request = new StringArrayTest();
			for (int i = 0; i < values.length; i++)
				request.getStringArray().add(values[i]);
			response = port.stringArrayTest(request);
			pass = JAXWS_Data.compareArrayValues(values, response.getStringArray(), "String");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallJavaArrayTest:(StringArrayTest)");
		return pass;
	}

	private boolean CalendarArrayTest() {
		logger.log(Level.INFO, "MarshallJavaArrayTest:(CalendarArrayTest)");
		boolean pass = true;
		XMLGregorianCalendar values[] = JAXWS_Data.XMLGregorianCalendar_nonull_data;
		CalendarArrayTestResponse response;
		CalendarArrayTest request;
		try {
			request = new CalendarArrayTest();
			for (int i = 0; i < values.length; i++)
				request.getCalendarArray().add(values[i]);
			response = port.calendarArrayTest(request);
			pass = JAXWS_Data.compareArrayValues(values, response.getCalendarArray(), "XMLGregorianCalendar");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
			;
		}
		printTestStatus(pass, "MarshallJavaArrayTest:(CalendarArrayTest)");
		return pass;
	}

	private boolean BigIntegerArrayTest() {
		logger.log(Level.INFO, "MarshallJavaArrayTest:(BigIntegerArrayTest)");
		boolean pass = true;
		BigInteger values[] = JAXWS_Data.BigInteger_nonull_data;
		BigIntegerArrayTestResponse response;
		BigIntegerArrayTest request;
		try {
			request = new BigIntegerArrayTest();
			for (int i = 0; i < values.length; i++)
				request.getBigIntegerArray().add(values[i]);
			response = port.bigIntegerArrayTest(request);
			pass = JAXWS_Data.compareArrayValues(values, response.getBigIntegerArray(), "BigInteger");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallJavaArrayTest:(BigIntegerArrayTest)");
		return pass;
	}

	private boolean BigDecimalArrayTest() {
		logger.log(Level.INFO, "MarshallJavaArrayTest:(BigDecimalArrayTest)");
		boolean pass = true;
		BigDecimal values[] = JAXWS_Data.BigDecimal_nonull_data;
		BigDecimalArrayTestResponse response;
		BigDecimalArrayTest request;
		try {
			request = new BigDecimalArrayTest();
			for (int i = 0; i < values.length; i++)
				request.getBigDecimalArray().add(values[i]);
			response = port.bigDecimalArrayTest(request);
			pass = JAXWS_Data.compareArrayValues(values, response.getBigDecimalArray(), "BigDecimal");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallJavaArrayTest:(BigDecimalArrayTest)");
		return pass;
	}

	private boolean JavaBeanArrayTest() {
		logger.log(Level.INFO, "MarshallJavaArrayTest:(JavaBeanArrayTest)");
		boolean pass = true;

		init_JavaBean_Data();
		JavaBean values[] = JavaBean_data;
		JavaBeanArrayTestResponse response;
		JavaBeanArrayTest request;
		JavaBean result[] = new JavaBean[values.length];
		try {
			request = new JavaBeanArrayTest();
			for (int i = 0; i < values.length; i++)
				request.getJavaBeanArray().add(values[i]);
			response = port.javaBeanArrayTest(request);
			List<JavaBean> l = response.getJavaBeanArray();
			Iterator i = l.iterator();
			int j = 0;
			while (i.hasNext()) {
				result[j++] = (JavaBean) i.next();
			}
			pass = compareArrayValues(values, result, "JavaBean");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "MarshallJavaArrayTest:(JavaBeanArrayTest)");
		return pass;
	}

	private void init_JavaBean_Data() {
		JavaBean_data[0].setMyBoolean(false);
		JavaBean_data[0].setMyByte(Byte.MIN_VALUE);
		JavaBean_data[0].setMyShort(Short.MIN_VALUE);
		JavaBean_data[0].setMyInt(Integer.MIN_VALUE);
		JavaBean_data[0].setMyLong(Long.MIN_VALUE);
		JavaBean_data[0].setMyFloat(Float.MIN_VALUE);
		JavaBean_data[0].setMyDouble(Double.MIN_VALUE);
		JavaBean_data[0].setMyString("");
		JavaBean_data[0].setMyBigInteger(JAXWS_Data.BigInteger_data[0]);
		JavaBean_data[0].setMyBigDecimal(JAXWS_Data.BigDecimal_data[0]);
		JavaBean_data[0].setMyCalendar(JAXWS_Data.XMLGregorianCalendar_data[0]);

		JavaBean_data[1].setMyBoolean(true);
		JavaBean_data[1].setMyByte(Byte.MAX_VALUE);
		JavaBean_data[1].setMyShort(Short.MAX_VALUE);
		JavaBean_data[1].setMyInt(Integer.MAX_VALUE);
		JavaBean_data[1].setMyLong(Long.MAX_VALUE);
		JavaBean_data[1].setMyFloat(Float.MAX_VALUE);
		JavaBean_data[1].setMyDouble(Double.MAX_VALUE);
		JavaBean_data[1].setMyString("");
		JavaBean_data[1].setMyBigInteger(JAXWS_Data.BigInteger_data[1]);
		JavaBean_data[1].setMyBigDecimal(JAXWS_Data.BigDecimal_data[1]);
		JavaBean_data[1].setMyCalendar(JAXWS_Data.XMLGregorianCalendar_data[1]);

		JavaBean2_data[0].setMyBoolean(false);
		JavaBean2_data[0].setMyByte(Byte.MIN_VALUE);
		JavaBean2_data[0].setMyShort(Short.MIN_VALUE);
		JavaBean2_data[0].setMyInt(Integer.MIN_VALUE);
		JavaBean2_data[0].setMyLong(Long.MIN_VALUE);
		JavaBean2_data[0].setMyFloat(Float.MIN_VALUE);
		JavaBean2_data[0].setMyDouble(Double.MIN_VALUE);
		JavaBean2_data[0].setMyString("");
		JavaBean2_data[0].setMyBigInteger(JAXWS_Data.BigInteger_data[0]);
		JavaBean2_data[0].setMyBigDecimal(JAXWS_Data.BigDecimal_data[0]);
		JavaBean2_data[0].setMyCalendar(JAXWS_Data.XMLGregorianCalendar_data[0]);

		JavaBean2_data[1].setMyBoolean(true);
		JavaBean2_data[1].setMyByte(Byte.MAX_VALUE);
		JavaBean2_data[1].setMyShort(Short.MAX_VALUE);
		JavaBean2_data[1].setMyInt(Integer.MAX_VALUE);
		JavaBean2_data[1].setMyLong(Long.MAX_VALUE);
		JavaBean2_data[1].setMyFloat(Float.MAX_VALUE);
		JavaBean2_data[1].setMyDouble(Double.MAX_VALUE);
		JavaBean2_data[1].setMyString("");
		JavaBean2_data[1].setMyBigInteger(JAXWS_Data.BigInteger_data[1]);
		JavaBean2_data[1].setMyBigDecimal(JAXWS_Data.BigDecimal_data[1]);
		JavaBean2_data[1].setMyCalendar(JAXWS_Data.XMLGregorianCalendar_data[1]);

		JavaBean_data[0].setMyJavaBean(JavaBean2_data[0]);
		JavaBean_data[1].setMyJavaBean(JavaBean2_data[1]);

	}

	public String toStringJavaBean(JavaBean v) {
		return "myBoolean: " + v.isMyBoolean() + ", myByte: " + v.getMyByte() + ", myShort: " + v.getMyShort()
				+ ", myInt: " + v.getMyInt() + ", myLong: " + v.getMyLong() + ", myFloat: " + v.getMyFloat()
				+ ", myDouble: " + v.getMyDouble() + ", myString: " + v.getMyString() + ", myBigInteger: "
				+ v.getMyBigInteger() + ", myBigDecimal: " + v.getMyBigDecimal() + ", myJavaBean: "
				+ toStringJavaBean2(v.getMyJavaBean()) + ", myCalendar: " + v.getMyCalendar();
	}

	public String toStringJavaBean2(JavaBean2 v) {
		return "myBoolean: " + v.isMyBoolean() + ", myByte: " + v.getMyByte() + ", myShort: " + v.getMyShort()
				+ ", myInt: " + v.getMyInt() + ", myLong: " + v.getMyLong() + ", myFloat: " + v.getMyFloat()
				+ ", myDouble: " + v.getMyDouble() + ", myString: " + v.getMyString() + ", myBigInteger: "
				+ v.getMyBigInteger() + ", myBigDecimal: " + v.getMyBigDecimal() + ", myCalendar: " + v.getMyCalendar();
	}

	public boolean compareJavaBeans(JavaBean e, JavaBean r) {
		return e.isMyBoolean() == r.isMyBoolean() && e.getMyByte() == r.getMyByte() && e.getMyShort() == r.getMyShort()
				&& e.getMyInt() == r.getMyInt() && e.getMyLong() == r.getMyLong() && e.getMyFloat() == r.getMyFloat()
				&& e.getMyDouble() == r.getMyDouble() && e.getMyString().equals(r.getMyString())
				&& e.getMyBigInteger().equals(r.getMyBigInteger()) && e.getMyBigDecimal().equals(r.getMyBigDecimal())
				&& compareJavaBean2(e.getMyJavaBean(), r.getMyJavaBean())
				&& JAXWS_Data.compareXMLGregorianCalendars(e.getMyCalendar(), r.getMyCalendar());
	}

	public boolean compareJavaBean2(JavaBean2 e, JavaBean2 r) {
		return e.isMyBoolean() == r.isMyBoolean() && e.getMyByte() == r.getMyByte() && e.getMyShort() == r.getMyShort()
				&& e.getMyInt() == r.getMyInt() && e.getMyLong() == r.getMyLong() && e.getMyFloat() == r.getMyFloat()
				&& e.getMyDouble() == r.getMyDouble() && e.getMyString().equals(r.getMyString())
				&& e.getMyBigInteger().equals(r.getMyBigInteger()) && e.getMyBigDecimal().equals(r.getMyBigDecimal())
				&& JAXWS_Data.compareXMLGregorianCalendars(e.getMyCalendar(), r.getMyCalendar());
	}

	// ==================================================================
	// Various utility classes used for dumping/comparing data
	// ==================================================================

	private void dumpArrayValues(Object o, String t) {
		System.out.println("JAXWS_Data:dumpArrayValues");
		System.out.println("Dumping " + t + " array, size=" + getArraySize(o, t));
		if (t.equals("JavaBean")) {
			JavaBean[] v = (JavaBean[]) o;
			for (int i = 0; i < v.length; i++)
				System.out.println("- " + toStringJavaBean(v[i]));
		}
	}

	private int getArraySize(Object o, String t) {
		System.out.println("JAXWS_Data:getArraySize");
		if (t.equals("JavaBean")) {
			return ((JavaBean[]) o).length;
		}
		return -1;
	}

	private boolean compareValues(Object e, Object r, String t) {
		boolean pass = true;

		if (t.equals("JavaBean")) {
			JavaBean exp = (JavaBean) e;
			JavaBean rec = (JavaBean) r;
			if (rec == exp)
				return true;
			if ((rec == null && exp != null) || (rec != null && exp == null)) {
				pass = false;
			} else if (!compareJavaBeans(exp, rec)) {
				System.out.println(
						"Value Mismatch: expected " + toStringJavaBean(exp) + ", received " + toStringJavaBean(rec));
				pass = false;
			}
		}
		return pass;
	}

	private boolean compareArrayValues(Object e, Object r, String t) {
		System.out.println("JAXWS_Data:compareArrayValues");
		boolean pass = true;

		if (t.equals("JavaBean")) {
			JavaBean[] exp = (JavaBean[]) e;
			JavaBean[] rec = (JavaBean[]) r;
			if (rec.length != exp.length) {
				System.out.println("Array Size MisMatch: expected " + exp.length + ", received " + rec.length);
				pass = false;
			}
			for (int i = 0; i < rec.length; i++) {
				if (rec[i] == exp[i])
					continue;
				if ((rec[i] == null && exp[i] != null) || (rec[i] != null && exp[i] == null)) {
					pass = false;
				} else if (!compareJavaBeans(exp[i], rec[i])) {
					System.out.println("Array Mismatch: expected " + toStringJavaBean(exp[i]) + ", received "
							+ toStringJavaBean(rec[i]));
					pass = false;
				}
			}
		}
		return pass;
	}

	private String returnArrayValues(Object o, String t) {
		StringBuilder values = new StringBuilder();
		if (t.equals("JavaBean")) {
			JavaBean[] v = (JavaBean[]) o;
			for (int i = 0; i < v.length; i++)
				values.append(", ").append(toStringJavaBean(v[i]));
		}
		return values.toString();
	}

	private boolean doPersonTest() {
		logger.log(Level.INFO, "doPersonTest");
		boolean pass = true;
		// Person arguments
		String name = "ChildPerson";
		String sex = "F";
		int age = 10;
		float id = (float) Short.MAX_VALUE;
		boolean adult = false;
		Person person = null;
		try {
			person = new Person();
			person.setName(name);
			person.setAge(age);
			person.setSex(sex);
			person.setAdult(adult);
			person.setId(id);
			EchoPersonRequest request = new EchoPersonRequest();
			request.setPerson(person);
			EchoPersonResponse response = port3.echoPerson(request);
			person = response.getPerson();
			String resultName = person.getName();
			String resultSex = person.getSex();
			int resultAge = person.getAge();
			boolean resultAdult = person.isAdult();
			float resultId = person.getId();
			if (!resultName.equals(name) || !resultSex.equals(sex) || (resultAge != age) || (resultId != id)
					|| resultAdult) {
				TestUtil.logErr("Result mismatch in Person");
				pass = false;
				TestUtil.logErr("Expected: <" + name + "," + sex + "," + age + "," + id + "," + adult + ">, Got: <"
						+ resultName + "," + resultSex + "," + resultAge + "," + resultId + "," + resultAdult + ">");
			} else {
				logger.log(Level.INFO,
						"Results match!\nExpected: <" + name + "," + sex + "," + age + "," + id + "," + adult
								+ ">, Got: <" + resultName + "," + resultSex + "," + resultAge + "," + resultId + ","
								+ resultAdult + ">");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean doEmployeeTest() {
		logger.log(Level.INFO, "doEmployeeTest");
		boolean pass = true;
		// Person arguments
		String name = "AdultPerson";
		String sex = "M";
		int age = 30;
		boolean adult = true;
		float id = (float) Short.MAX_VALUE;
		// Employee other arguments
		double salary = 200000;
		int empId = 1;
		try {
			Person person = new Person();
			person.setName(name);
			person.setSex(sex);
			person.setAge(age);
			person.setAdult(adult);
			person.setId(id);
			Employee employee = new Employee();
			employee.setPerson(person);
			employee.setSalary(salary);
			employee.setId(empId);
			EchoEmployeeRequest request = new EchoEmployeeRequest();
			request.setEmployee(employee);
			EchoEmployeeResponse response = port3.echoEmployee(request);
			employee = response.getEmployee();
			// verify results
			person = employee.getPerson();
			String resultName = person.getName();
			String resultSex = person.getSex();
			int resultAge = person.getAge();
			float resultId = person.getId();
			boolean resultAdult = person.isAdult();
			if (!resultName.equals(name) || !resultSex.equals(sex) || (resultAdult != adult) || (resultAge != age)
					|| (resultId != id) || !resultAdult) {
				TestUtil.logErr("Result mismatch in Person");
				pass = false;
				TestUtil.logErr("Expected: <" + name + "," + sex + "," + age + "," + id + "," + adult + ">, Got: <"
						+ resultName + "," + resultSex + "," + resultAge + "," + resultId + "," + resultAdult + ">");

			}
			float resultSalary = (float) employee.getSalary();
			int resultEmpId = employee.getId();
			if (!pass || (resultSalary != salary) || (resultEmpId != empId)) {
				TestUtil.logErr("Result mismatch in Employee");
				pass = false;
				TestUtil.logErr(
						"Expected: <" + salary + ", " + empId + ">, Got: <" + resultSalary + "," + resultEmpId + ">");
			} else {
				logger.log(Level.INFO,
						"Results match\nExpected: <" + name + "," + sex + "," + age + "," + id + "," + adult
								+ ">, Got: <" + resultName + "," + resultSex + "," + resultAge + "," + resultId + ","
								+ resultAdult + ">\n" + "Expected: <" + salary + ", " + empId + ">, Got: <"
								+ resultSalary + "," + resultEmpId + ">");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean doDocumentTest() {
		String stringID = "myID";
		String stringValue = "myValue";
		logger.log(Level.INFO, "doDocumentTest");
		boolean pass = true;
		try {
			Document request = new Document();
			request.setID(stringID);
			request.setValue(stringValue);
			Document response = port3.echoDocument(request);
			if (!response.getID().equals(stringID)) {
				TestUtil.logErr("Result mismatch in Document ID");
				TestUtil.logErr("Expected: <" + stringID + ">, Got: <" + response.getID() + ">");
			}
			if (!response.getValue().equals(stringValue)) {
				TestUtil.logErr("Result mismatch in Document Value");
				TestUtil.logErr("Expected: <" + stringValue + ">, Got: <" + response.getValue() + ">");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean QNameTest() {
		logger.log(Level.INFO, "QNameTest");
		boolean pass = true;
		QName arg = new QName("http://foo.bar.com", "localPart");
		logger.log(Level.INFO, "arg=" + arg);
		QNameTestResponse response;
		QNameTest request;
		logger.log(Level.INFO, "Passing Returning data to/from JAXWS Service");
		try {
			request = new QNameTest();
			request.setQname1(arg);
			response = port.qnameTest(request);
			logger.log(Level.INFO, "response=" + response);
			logger.log(Level.INFO, "result=" + response.getResult());
			if (!response.getResult().equals(arg)) {
				TestUtil.logErr("QNameTest failed - expected " + arg + ",  received: " + response.getResult());
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "QNameTest");
		return pass;
	}

	private boolean Base64BinaryTest() {
		logger.log(Level.INFO, "Base64BinaryTest");
		boolean pass = false;
		byte values[] = JAXWS_Data.byte_data;
		Base64BinaryTest request;
		Base64BinaryTestResponse response;
		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			request = new Base64BinaryTest();
			request.setBase64Binary1(values);
			response = port.base64BinaryTest(request);
			byte ret[] = response.getResult();

			TestUtil.logTrace("base64 returned : " + ret[0] + "," + ret[1] + "," + ret[2]);
			pass = JAXWS_Data.compareArrayValues(values, response.getResult(), "byte");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "Base64BinaryTest");
		return pass;
	}

	private boolean HexBinaryTest() {
		logger.log(Level.INFO, "HexBinaryTest");
		boolean pass = false;
		byte values[] = JAXWS_Data.byte_data;
		HexBinaryTest request;
		HexBinaryTestResponse response;
		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			request = new HexBinaryTest();
			request.setHexBinary1(values);

			response = port.hexBinaryTest(request);
			byte ret[] = response.getResult();
			TestUtil.logTrace("HexBinary returned : " + ret[0] + "," + ret[1] + "," + ret[2]);
			pass = JAXWS_Data.compareArrayValues(values, response.getResult(), "byte");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "HexBinaryTest");
		return pass;
	}

	private FooType FooType_data = null;

	private FooVariousSchemaTypes FooVariousSchemaTypes_data = null;

	private FooVariousSchemaTypes FooVariousSchemaTypes_array_data[] = null;

	private FooVariousSchemaTypesListType FooVariousSchemaTypesListType_data = null;

	private FooAnonymousType FooAnonymousType_data = null;

	private int getIteratorCount(Iterator i) {
		int count = 0;
		while (i.hasNext() && count < 100) {
			++count;
		}
		return count;
	}

	private void InitExpectedFooTypeData() throws Exception {
		init_FooVariousSchemaTypes_Data();
		init_FooAnonymousType_Data();
		init_FooType_Data();
	}

	private boolean CompareWithExpectedFooTypeData(FooType f) {
		boolean valid = true;

		if (f.isFooA() != true) {
			TestUtil.logErr("isFooA() returned " + f.isFooA() + ", expected " + !f.isFooA());
			valid = false;
		}
		if (f.getFooB() != Byte.MAX_VALUE) {
			TestUtil.logErr("getFooB() returned " + f.getFooB() + ", expected " + Byte.MAX_VALUE);
			valid = false;
		}
		if (f.getFooC() != Short.MAX_VALUE) {
			TestUtil.logErr("getFooC() returned " + f.getFooC() + ", expected " + Short.MAX_VALUE);
			valid = false;
		}
		if (f.getFooD() != Integer.MAX_VALUE) {
			TestUtil.logErr("getFooD() returned " + f.getFooD() + ", expected " + Integer.MAX_VALUE);
			valid = false;
		}
		if (f.getFooE() != Long.MAX_VALUE) {
			TestUtil.logErr("getFooE() returned " + f.getFooE() + ", expected " + Long.MAX_VALUE);
			valid = false;
		}
		if (f.getFooF() != Float.MAX_VALUE) {
			TestUtil.logErr("getFooF() returned " + f.getFooF() + ", expected " + Float.MAX_VALUE);
			valid = false;
		}
		if (f.getFooG() != Double.MAX_VALUE) {
			TestUtil.logErr("getFooG() returned " + f.getFooG() + ", expected " + Double.MAX_VALUE);
			valid = false;
		}
		if (!f.getFooH().equals("foostringH")) {
			TestUtil.logErr("getFooH() returned " + f.getFooH() + ", expected foostringH");
			valid = false;
		}
		if (!f.getFooI().equals("123-ABC12")) {
			TestUtil.logErr("getFooI() returned " + f.getFooI() + ", expected 123-ABC12");
			valid = false;
		}
		FooVariousSchemaTypes fnst = f.getFooJ();
		if (fnst == null) {
			TestUtil.logErr("getFooJ() returned null, " + "expected FooVariousSchemaTypes");
			valid = false;
		}
		if (fnst != null) {
			logger.log(Level.INFO,
					"Send: " + FooVariousSchemaTypes_data.getFooA() + "|" + FooVariousSchemaTypes_data.getFooB() + "|"
							+ FooVariousSchemaTypes_data.getFooC() + "|" + FooVariousSchemaTypes_data.getFooD() + "|"
							+ FooVariousSchemaTypes_data.getFooE() + "|" + FooVariousSchemaTypes_data.getFooF());
			logger.log(Level.INFO, "Recv: " + fnst.getFooA() + "|" + fnst.getFooB() + "|" + fnst.getFooC() + "|"
					+ fnst.getFooD() + "|" + fnst.getFooE() + "|" + fnst.getFooF());
			if (fnst.getFooA() == FooVariousSchemaTypes_data.getFooA()
					&& fnst.getFooB().equals(FooVariousSchemaTypes_data.getFooB())
					&& fnst.getFooC().equals(FooVariousSchemaTypes_data.getFooC())
					&& fnst.getFooD().equals(FooVariousSchemaTypes_data.getFooD())
					&& fnst.getFooE() == FooVariousSchemaTypes_data.getFooE()
					&& fnst.getFooF() == FooVariousSchemaTypes_data.getFooF()) {
				logger.log(Level.INFO, "Result match");
			} else {
				TestUtil.logErr("Result mismatch");
				valid = false;
			}
		}
		if (!f.getFooK().equals(new BigInteger("101"))) {
			TestUtil.logErr("getFooK() returned " + f.getFooK() + ", expected 101");
			valid = false;
		}
		if (!(f.getFooM().equals("hello,there"))) {
			TestUtil.logErr("getFooM() returned " + f.getFooM() + ", expected hello,there");
			valid = false;
		}
		if (!compareFooAnonymousTypeData(f.getFooN(), FooAnonymousType_data))
			valid = false;
		return valid;
	}

	private void init_FooVariousSchemaTypes_Data() throws Exception {
		FooVariousSchemaTypes_data = new FooVariousSchemaTypes();
		FooVariousSchemaTypes_data.setFooA(1);
		FooVariousSchemaTypes_data.setFooB(new BigInteger("1000"));
		FooVariousSchemaTypes_data.setFooC("NORMALIZEDSTRING");
		FooVariousSchemaTypes_data.setFooD("NMTOKEN");
		FooVariousSchemaTypes_data.setFooE(1);
		FooVariousSchemaTypes_data.setFooF((short) 1);

		FooVariousSchemaTypes_array_data = new FooVariousSchemaTypes[2];

		FooVariousSchemaTypes_array_data[0] = new FooVariousSchemaTypes();
		FooVariousSchemaTypes_array_data[1] = new FooVariousSchemaTypes();
		FooVariousSchemaTypes_array_data[0].setFooA(256);
		FooVariousSchemaTypes_array_data[1].setFooA(0);
		FooVariousSchemaTypes_array_data[0].setFooB(JAXWS_Data.BigInteger_data[0]);
		FooVariousSchemaTypes_array_data[1].setFooB(JAXWS_Data.BigInteger_data[1]);
		FooVariousSchemaTypes_array_data[0].setFooC("NORMALIZEDSTRING1");
		FooVariousSchemaTypes_array_data[1].setFooC("NORMALIZEDSTRING2");
		FooVariousSchemaTypes_array_data[0].setFooD("NMTOKEN1");
		FooVariousSchemaTypes_array_data[1].setFooD("NMTOKEN2");
		FooVariousSchemaTypes_array_data[0].setFooE(0);
		FooVariousSchemaTypes_array_data[1].setFooE(1);
		FooVariousSchemaTypes_array_data[0].setFooF((short) 0);
		FooVariousSchemaTypes_array_data[1].setFooF((short) 1);

		FooVariousSchemaTypesListType_data = new FooVariousSchemaTypesListType();

		for (int i = 0; i < FooVariousSchemaTypes_array_data.length; i++) {
			FooVariousSchemaTypesListType_data.getFooA().add(FooVariousSchemaTypes_array_data[i]);
		}
	}

	private void init_FooAnonymousType_Data() throws Exception {
		FooAnonymousType.FooAnonymousElement fe1 = new FooAnonymousType.FooAnonymousElement();
		FooAnonymousType.FooAnonymousElement fe2 = new FooAnonymousType.FooAnonymousElement();
		fe1.setFooA("foo");
		fe1.setFooB(1);
		fe1.setFooC(true);
		fe2.setFooA("bar");
		fe2.setFooB(0);
		fe2.setFooC(false);

		FooAnonymousType_data = new FooAnonymousType();
		FooAnonymousType_data.getFooAnonymousElement().add(fe1);
		FooAnonymousType_data.getFooAnonymousElement().add(fe2);
	}

	private boolean compareFooAnonymousTypeData(FooAnonymousType request, FooAnonymousType response) {
		boolean valid = true;

		Object[] req = request.getFooAnonymousElement().toArray();
		Object[] res = response.getFooAnonymousElement().toArray();
		if (req.length == res.length) {
			logger.log(Level.INFO, "Array length match - checking array elements");
			for (int i = 0; i < req.length; i++) {
				FooAnonymousType.FooAnonymousElement exp = (FooAnonymousType.FooAnonymousElement) req[i];
				FooAnonymousType.FooAnonymousElement rec = (FooAnonymousType.FooAnonymousElement) res[i];
				logger.log(Level.INFO, "Request: " + exp.getFooA() + "|" + exp.getFooB() + "|" + exp.isFooC());
				logger.log(Level.INFO, "Response: " + rec.getFooA() + "|" + rec.getFooB() + "|" + rec.isFooC());
				if (!exp.getFooA().equals(rec.getFooA()) || exp.getFooB() != rec.getFooB()
						|| exp.isFooC() != rec.isFooC()) {
					valid = false;
					TestUtil.logErr("Element results mismatch ...");
					break;
				} else
					logger.log(Level.INFO, "Element results match ...");
			}
		} else {
			TestUtil.logErr("Array length mismatch - expected: " + req.length + ", received: " + res.length);
		}
		return valid;
	}

	private void init_FooType_Data() throws Exception {
		FooType_data = new FooType();
		FooType_data.setFooA(true);
		FooType_data.setFooB(Byte.MAX_VALUE);
		FooType_data.setFooC(Short.MAX_VALUE);
		FooType_data.setFooD(Integer.MAX_VALUE);
		FooType_data.setFooE(Long.MAX_VALUE);
		FooType_data.setFooF(Float.MAX_VALUE);
		FooType_data.setFooG(Double.MAX_VALUE);
		FooType_data.setFooH("foostringH");
		FooType_data.setFooI("123-ABC12");
		FooType_data.setFooJ(FooVariousSchemaTypes_data);
		FooType_data.setFooK(new BigInteger("101"));
		FooType_data.setFooM("hello,there");
		FooType_data.setFooN(FooAnonymousType_data);
	}

	private boolean QNameArrayTest() {
		logger.log(Level.INFO, "QNameArrayTest");
		boolean pass = true;
		QName values[] = JAXWS_Data.QName_nonull_data;
		QNameArrayTest request;
		QNameArrayTestResponse response;
		QName result[] = new QName[values.length];

		logger.log(Level.INFO, "Passing/Returning array data to/from JAXWS Service");
		try {
			request = new QNameArrayTest();
			for (int i = 0; i < values.length; i++)
				request.getQnameArray1().add(values[i]);
			response = port.qnameArrayTest(request);
			List<QName> l = response.getResult();
			Iterator i = l.iterator();
			int j = 0;
			while (i.hasNext()) {
				result[j++] = (QName) i.next();
			}
			pass = JAXWS_Data.compareArrayValues(values, result, "QName");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "QNameArrayTest");
		return pass;
	}

	private boolean StringEnumTypeTest() {
		boolean pass = true;
		FooStringEnumType request = FooStringEnumType.MA;
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			FooStringEnumType response = port2.echoStringEnumTypeTest(request);
			logger.log(Level.INFO, "Received respnse: " + response);
			if (!request.equals(response)) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean ByteEnumTypeTest() {
		boolean pass = true;
		byte request = 1;
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			byte response = port2.echoByteEnumTypeTest(request);
			logger.log(Level.INFO, "Received respnse: " + response);
			if (request != response) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean ShortEnumTypeTest() {
		boolean pass = true;
		short request = 1;
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			short response = port2.echoShortEnumTypeTest(request);
			logger.log(Level.INFO, "Received respnse: " + response);
			if (request != response) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean IntegerEnumTypeTest() {
		boolean pass = true;
		BigInteger request = new BigInteger("1");
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			BigInteger response = port2.echoIntegerEnumTypeTest(request);
			logger.log(Level.INFO, "Received respnse: " + response);
			if (!request.equals(response)) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean IntEnumTypeTest() {
		boolean pass = true;
		int request = 1;
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			int response = port2.echoIntEnumTypeTest(request);
			logger.log(Level.INFO, "Received respnse: " + response);
			if (request != response) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean LongEnumTypeTest() {
		boolean pass = true;
		long request = 1;
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			long response = port2.echoLongEnumTypeTest(request);
			logger.log(Level.INFO, "Received respnse: " + response);
			if (request != response) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean DecimalEnumTypeTest() {
		boolean pass = true;
		BigDecimal request = new BigDecimal("1.1");
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			BigDecimal response = port2.echoDecimalEnumTypeTest(request);
			logger.log(Level.INFO, "Received respnse: " + response);
			if (!request.equals(response)) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean FloatEnumTypeTest() {
		boolean pass = true;
		float request = 1.1f;
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			float response = port2.echoFloatEnumTypeTest(request);
			logger.log(Level.INFO, "Received respnse: " + response);
			if (request != response) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean DoubleEnumTypeTest() {
		boolean pass = true;
		double request = 1.1;
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			double response = port2.echoDoubleEnumTypeTest(request);
			logger.log(Level.INFO, "Received respnse: " + response);
			if (request != response) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean UnsignedShortTest() {
		TestUtil.logTrace("UnsignedShortTest ...");
		boolean pass = true;
		int request = 100;
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			int response = port2.echoUnsignedShortTest(request);
			logger.log(Level.INFO, "Received respnse: " + response);
			if (request != response) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean UnsignedIntTest() {
		TestUtil.logTrace("UnsignedIntTest ...");
		boolean pass = true;
		long request = 100;
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			long response = port2.echoUnsignedIntTest(request);
			logger.log(Level.INFO, "Received respnse: " + response);
			if (request != response) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean UnsignedByteTest() {
		TestUtil.logTrace("UnsignedByteTest ...");
		boolean pass = true;
		short request = 100;
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			short response = port2.echoUnsignedByteTest(request);
			logger.log(Level.INFO, "Received respnse: " + response);
			if (request != response) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean UnsignedLongTest() {
		TestUtil.logTrace("UnsignedLongTest ...");
		boolean pass = true;
		BigInteger request = new BigInteger("100");
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			BigInteger response = port2.echoUnsignedLongTest(request);
			logger.log(Level.INFO, "Received response: " + response);
			if (!request.equals(response)) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean NonPositiveIntegerTest() {
		TestUtil.logTrace("NonPositiveIntegerTest ...");
		boolean pass = true;
		BigInteger request = new BigInteger("-100");
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			BigInteger response = port2.echoNonPositiveIntegerTest(request);
			logger.log(Level.INFO, "Received response: " + response);
			if (!request.equals(response)) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean NonNegativeIntegerTest() {
		TestUtil.logTrace("NonNegativeIntegerTest ...");
		boolean pass = true;
		BigInteger request = new BigInteger("100");
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			BigInteger response = port2.echoNonNegativeIntegerTest(request);
			logger.log(Level.INFO, "Received response: " + response);
			if (!request.equals(response)) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean NegativeIntegerTest() {
		TestUtil.logTrace("NegativeIntegerTest ...");
		boolean pass = true;
		BigInteger request = new BigInteger("-100");
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			BigInteger response = port2.echoNegativeIntegerTest(request);
			logger.log(Level.INFO, "Received response: " + response);
			if (!request.equals(response)) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean PositiveIntegerTest() {
		TestUtil.logTrace("PositiveIntegerTest ...");
		boolean pass = true;
		BigInteger request = new BigInteger("100");
		try {
			logger.log(Level.INFO, "Sending request: " + request);
			BigInteger response = port2.echoPositiveIntegerTest(request);
			logger.log(Level.INFO, "Received response: " + response);
			if (!request.equals(response)) {
				TestUtil.logErr("Result mismatch - expected: " + request + ", received: " + response);
				pass = false;

			} else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean TimeTest() {
		logger.log(Level.INFO, "(TimeTest)");
		boolean pass = true;
		XMLGregorianCalendar values[] = JAXWS_Data.XMLGregorianCalendar_nonull_data;
		XMLGregorianCalendar request;
		XMLGregorianCalendar response;
		logger.log(Level.INFO, "Passing/Returning Time class to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = values[i];
				response = port2.echoTimeTest(request);
				if (!JAXWS_Data.compareValues(values[i], response, "XMLGregorianCalendar"))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "(TimeTest)");
		return pass;
	}

	private boolean DateTest() {
		logger.log(Level.INFO, "(DateTest)");
		boolean pass = true;
		XMLGregorianCalendar values[] = JAXWS_Data.XMLGregorianCalendar_nonull_data;
		XMLGregorianCalendar request = null;
		XMLGregorianCalendar response = null;
		logger.log(Level.INFO, "Passing/Returning Time class to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = values[i];
				response = port2.echoDateTest(request);
				if (!JAXWS_Data.compareValues(values[i], response, "XMLGregorianCalendar"))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "(DateTest)");
		return pass;
	}

	private boolean StringListTest() {
		TestUtil.logTrace("StringListTest ...");
		boolean pass = true;
		String[] request = new String[] { "foo", "bar", "foobar" };
		try {
			String[] response = port2.echoStringListTypeTest(request);
			if (!JAXWS_Data.compareArrayValues(request, response, "String"))
				pass = false;
			else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean DecimalListTest() {
		TestUtil.logTrace("DecimalListTest...");
		boolean pass = true;
		BigDecimal[] request = new BigDecimal[] { new BigDecimal("3512359.1456"), new BigDecimal("1"),
				new BigDecimal("2") };
		try {
			BigDecimal[] response = port2.echoDecimalListTypeTest(request);
			if (!JAXWS_Data.compareArrayValues(request, response, "BigDecimal"))
				pass = false;
			else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean FloatListTest() {
		TestUtil.logTrace("FloatListTest...");
		boolean pass = true;
		Float[] request = new Float[] { Float.valueOf(Float.MIN_VALUE), Float.valueOf(Float.MAX_VALUE),
				Float.valueOf(1) };
		try {
			Float[] response = port2.echoFloatListTypeTest(request);
			if (!JAXWS_Data.compareArrayValues(request, response, "Float"))
				pass = false;
			else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean IntListTest() {
		TestUtil.logTrace("IntListTest...");
		boolean pass = true;
		Integer[] request = new Integer[] { Integer.valueOf(Integer.MIN_VALUE), Integer.valueOf(Integer.MAX_VALUE),
				Integer.valueOf(1) };
		try {
			Integer[] response = port2.echoIntListTypeTest(request);
			if (!JAXWS_Data.compareArrayValues(request, response, "Integer"))
				pass = false;
			else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean IntegerListTest() {
		TestUtil.logTrace("IntegerListTest...");
		boolean pass = true;
		BigInteger[] request = new BigInteger[] { new BigInteger("0"), new BigInteger("1"), new BigInteger("2") };
		try {
			BigInteger[] response = port2.echoIntegerListTypeTest(request);
			if (!JAXWS_Data.compareArrayValues(request, response, "BigInteger"))
				pass = false;
			else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean DoubleListTest() {
		TestUtil.logTrace("DoubleListTest...");
		boolean pass = true;
		Double[] request = new Double[] { Double.valueOf(Double.MIN_VALUE), Double.valueOf(Double.MAX_VALUE),
				Double.valueOf(1) };
		try {
			Double[] response = port2.echoDoubleListTypeTest(request);
			if (!JAXWS_Data.compareArrayValues(request, response, "Double"))
				pass = false;
			else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean ByteListTest() {
		TestUtil.logTrace("ByteListTest...");
		boolean pass = true;
		Byte[] request = new Byte[] { Byte.valueOf(Byte.MIN_VALUE), Byte.valueOf(Byte.MAX_VALUE) };
		try {
			Byte[] response = port2.echoByteListTypeTest(request);
			if (!JAXWS_Data.compareArrayValues(request, response, "Byte"))
				pass = false;
			else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean LongListTest() {
		TestUtil.logTrace("LongListTest...");
		boolean pass = true;
		Long[] request = new Long[] { Long.valueOf(Long.MIN_VALUE), Long.valueOf(Long.MAX_VALUE), Long.valueOf(1) };
		try {
			Long[] response = port2.echoLongListTypeTest(request);
			if (!JAXWS_Data.compareArrayValues(request, response, "Long"))
				pass = false;
			else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean ShortListTest() {
		TestUtil.logTrace("ShortListTest...");
		boolean pass = true;
		Short[] request = new Short[] { Short.valueOf(Short.MIN_VALUE), Short.valueOf(Short.MAX_VALUE) };
		try {
			Short[] response = port2.echoShortListTypeTest(request);
			if (!JAXWS_Data.compareArrayValues(request, response, "Short"))
				pass = false;
			else
				logger.log(Level.INFO, "Result match");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		return pass;
	}

	private boolean GYearMonthTest() {
		logger.log(Level.INFO, "MarshallDateTimeTest(GYearMonthTest)");
		boolean pass = true;
		XMLGregorianCalendar values[] = JAXWS_Data.XMLGregorianCalendar_data;
		GYearMonthTest request = null;
		GYearMonthTestResponse response = null;
		logger.log(Level.INFO, "Passing/Returning Time class to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new GYearMonthTest();
				request.setValue(values[i]);
				response = port.gYearMonthTest(request);
				if (!JAXWS_Data.compareDate(values[i], response.getResult(), "YM"))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "(GYearMonthTest)");
		return pass;
	}

	private boolean GYearTest() {
		logger.log(Level.INFO, "MarshallDateTimeTest(GYearTest)");
		boolean pass = true;
		XMLGregorianCalendar values[] = JAXWS_Data.XMLGregorianCalendar_data;
		GYearTest request = null;
		GYearTestResponse response = null;
		logger.log(Level.INFO, "Passing/Returning Time class to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new GYearTest();
				request.setValue(values[i]);
				response = port.gYearTest(request);
				if (!JAXWS_Data.compareDate(values[i], response.getResult(), "Y"))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "(GYearTest)");
		return pass;
	}

	private boolean GMonthDayTest() {
		logger.log(Level.INFO, "MarshallDateTimeTest(GMonthDayTest)");
		boolean pass = true;
		XMLGregorianCalendar values[] = JAXWS_Data.XMLGregorianCalendar_data;
		GMonthDayTest request = null;
		GMonthDayTestResponse response = null;
		logger.log(Level.INFO, "Passing/Returning Time class to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new GMonthDayTest();
				request.setValue(values[i]);
				response = port.gMonthDayTest(request);
				if (!JAXWS_Data.compareDate(values[i], response.getResult(), "MD"))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "(GMonthDayTest)");
		return pass;
	}

	private boolean GDayTest() {
		logger.log(Level.INFO, "MarshallDateTimeTest(GDayTest)");
		boolean pass = true;
		XMLGregorianCalendar values[] = JAXWS_Data.XMLGregorianCalendar_data;
		GDayTest request = null;
		GDayTestResponse response = null;
		logger.log(Level.INFO, "Passing/Returning Time class to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new GDayTest();
				request.setValue(values[i]);
				response = port.gDayTest(request);
				if (!JAXWS_Data.compareDate(values[i], response.getResult(), "D"))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "(GDayTest)");
		return pass;
	}

	private boolean GMonthTest() {
		logger.log(Level.INFO, "MarshallDateTimeTest(GMonthTest)");
		boolean pass = true;
		XMLGregorianCalendar values[] = JAXWS_Data.XMLGregorianCalendar_data;
		GMonthTest request = null;
		GMonthTestResponse response = null;
		logger.log(Level.INFO, "Passing/Returning Time class to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				request = new GMonthTest();
				request.setValue(values[i]);
				response = port.gMonthTest(request);
				if (values[i] != null && response.getResult() == null) { // BUG 16793203
					pass = false;
					TestUtil.logErr("response.getResult() is null");
				}
				if (!JAXWS_Data.compareDate(values[i], response.getResult(), "M"))
					pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}
		printTestStatus(pass, "(GMonthTest)");
		return pass;
	}
}
