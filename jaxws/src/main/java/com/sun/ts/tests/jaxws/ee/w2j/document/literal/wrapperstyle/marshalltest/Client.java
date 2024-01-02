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

package com.sun.ts.tests.jaxws.ee.w2j.document.literal.wrapperstyle.marshalltest;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.List;

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

public class Client extends BaseClient {

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.ee.w2j.document.literal.wrapperstyle.marshalltest.";

	// service and port information
	private static final String NAMESPACEURI = "http://MarshallTest.org/";

	private static final String SERVICE_NAME = "MarshallTestService";

	private static final String PORT_NAME = "MarshallTestPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	// URL properties used by the test
	private static final String ENDPOINT_URL = "w2jdlwmarshalltest.endpoint.1";

	private static final String WSDLLOC_URL = "w2jdlwmarshalltest.wsdlloc.1";

	private String url = null;

	private URL wsdlurl = null;

	MarshallTest port = null;

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
		file = JAXWS_Util.getURLFromProp(WSDLLOC_URL);
		wsdlurl = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		logger.log(Level.INFO, "Service Endpoint URL: " + url);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
	}

	protected void getPortStandalone() throws Exception {
		port = (MarshallTest) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, MarshallTestService.class, PORT_QNAME,
				MarshallTest.class);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtaining service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (MarshallTest) service.getMarshallTestPort();
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

	private void printSeperationLine() {
		logger.log(Level.INFO, "---------------------------");
	}

	/*
	 * @testName: MarshallSimpleTypesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2001; JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 * JAXWS:SPEC:10011; JAXWS:SPEC:2024; JAXWS:SPEC:2027;
	 * 
	 * 
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC methods for each
	 * primitive type. For each type pass its value as input to the corresponding
	 * RPC method and receive it back as the return value. Compare results of each
	 * value/type of what was sent and what was returned. Verify they are equal.
	 */
	@Test
	public void MarshallSimpleTypesTest() throws Exception {
		logger.log(Level.INFO, "MarshallSimpleTypesTest");
		boolean pass = true;

		if (!StringTest())
			pass = false;
		printSeperationLine();
		if (!IntegerTest())
			pass = false;
		printSeperationLine();
		if (!IntTest())
			pass = false;
		printSeperationLine();
		if (!LongTest())
			pass = false;
		printSeperationLine();
		if (!ShortTest())
			pass = false;
		printSeperationLine();
		if (!DecimalTest())
			pass = false;
		printSeperationLine();
		if (!FloatTest())
			pass = false;
		printSeperationLine();
		if (!DoubleTest())
			pass = false;
		printSeperationLine();
		if (!BooleanTest())
			pass = false;
		printSeperationLine();
		if (!ByteTest())
			pass = false;
		printSeperationLine();
		if (!QNameTest())
			pass = false;
		printSeperationLine();
		if (!DateTimeTest())
			pass = false;
		printSeperationLine();
		if (!Base64BinaryTest())
			pass = false;
		printSeperationLine();
		if (!HexBinaryTest())
			pass = false;
		printSeperationLine();

		if (!pass)
			throw new Exception("MarshallSimpleTypesTest failed");
	}

	/*
	 * @testName: MarshallArraysOfSimpleTypesTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2001; JAXWS:SPEC:2017; JAXWS:SPEC:2040;
	 * JAXWS:SPEC:10011; JAXWS:SPEC:2024; JAXWS:SPEC:2027;
	 * 
	 * @test_Strategy: Create a stub instance to our service definition interface,
	 * set the target endpoint to the servlet, and invoke the RPC methods for each
	 * primitive type. For each primitive type pass its value as input to the
	 * corresponding RPC method and receive it back as the return value. Compare
	 * results of each value/type of what was sent and what was returned. Verify
	 * they are equal.
	 */
	@Test
	public void MarshallArraysOfSimpleTypesTest() throws Exception {
		logger.log(Level.INFO, "MarshallArraysOfSimpleTypesTest");
		boolean pass = true;

		if (!StringArrayTest())
			pass = false;
		printSeperationLine();
		if (!IntegerArrayTest())
			pass = false;
		printSeperationLine();
		if (!IntArrayTest())
			pass = false;
		printSeperationLine();
		if (!LongArrayTest())
			pass = false;
		printSeperationLine();
		if (!ShortArrayTest())
			pass = false;
		printSeperationLine();
		if (!DecimalArrayTest())
			pass = false;
		printSeperationLine();
		if (!FloatArrayTest())
			pass = false;
		printSeperationLine();
		if (!DoubleArrayTest())
			pass = false;
		printSeperationLine();
		if (!BooleanArrayTest())
			pass = false;
		printSeperationLine();
		if (!ByteArrayTest())
			pass = false;
		printSeperationLine();
		if (!QNameArrayTest())
			pass = false;
		printSeperationLine();
		if (!DateTimeArrayTest())
			pass = false;
		printSeperationLine();

		if (!pass)
			throw new Exception("MarshallArraysOfSimpleTypesTest failed");
	}

	private boolean printTestStatus(boolean pass, String test) {
		if (pass)
			logger.log(Level.INFO, "" + test + " ... PASSED");
		else
			TestUtil.logErr("" + test + " ... FAILED");

		return pass;
	}

	private boolean StringTest() {
		logger.log(Level.INFO, "StringTest");
		boolean pass = true;
		String values[] = JAXWS_Data.String_data;
		String response;
		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				response = port.echoString(values[i]);

				if (values[i] == null && response == null) {
					continue;
				} else if (!response.equals(values[i])) {
					TestUtil.logErr("StringTest failed - expected " + values[i] + ",  received: " + response);
					pass = false;
				}
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "StringTest");
		return pass;
	}

	private boolean IntegerTest() {
		logger.log(Level.INFO, "IntegerTest");
		boolean pass = true;
		BigInteger values[] = JAXWS_Data.BigInteger_data;
		BigInteger response;

		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				response = port.echoInteger(values[i]);

				if (values[i] == null && response == null) {
					continue;
				}

				if (!response.equals(values[i])) {
					TestUtil.logErr("IntegerTest failed - expected " + values[i] + ",  received: " + response);
					pass = false;
				}
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "IntegerTest");
		return pass;
	}

	private boolean IntTest() {
		logger.log(Level.INFO, "IntTest");
		boolean pass = true;
		int values[] = JAXWS_Data.int_data;
		int response;

		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				response = port.echoInt(values[i]);

				if (response != values[i]) {
					TestUtil.logErr("IntTest failed - expected " + values[i] + ",  received: " + response);
					pass = false;
				}
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "IntTest");
		return pass;
	}

	private boolean LongTest() {
		logger.log(Level.INFO, "LongTest");
		boolean pass = true;
		long values[] = JAXWS_Data.long_data;
		long response;

		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				response = port.echoLong(values[i]);

				if (response != values[i]) {
					TestUtil.logErr("LongTest failed - expected " + values[i] + ",  received: " + response);
					pass = false;
				}
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "LongTest");
		return pass;
	}

	private boolean ShortTest() {
		logger.log(Level.INFO, "ShortTest");
		boolean pass = true;
		short values[] = JAXWS_Data.short_data;
		short response;

		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				response = port.echoShort(values[i]);

				if (response != values[i]) {
					TestUtil.logErr("ShortTest failed - expected " + values[i] + ",  received: " + response);
					pass = false;
				}
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "ShortTest");
		return pass;
	}

	private boolean DecimalTest() {
		logger.log(Level.INFO, "DecimalTest");
		boolean pass = true;
		BigDecimal values[] = JAXWS_Data.BigDecimal_data;
		BigDecimal response;

		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				response = port.echoDecimal(values[i]);

				if (values[i] == null && response == null) {
					continue;
				} else if (!response.equals(values[i])) {
					TestUtil.logErr("DecimalTest failed - expected " + values[i] + ",  received: " + response);
					pass = false;
				}
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "DecimalTest");
		return pass;
	}

	private boolean FloatTest() {
		logger.log(Level.INFO, "FloatTest");
		boolean pass = true;
		float values[] = JAXWS_Data.float_data;
		float response;

		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				response = port.echoFloat(values[i]);

				if (response != values[i]) {
					TestUtil.logErr("FloatTest failed - expected " + values[i] + ",  received: " + response);
					pass = false;
				}
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "FloatTest");
		return pass;
	}

	private boolean DoubleTest() {
		logger.log(Level.INFO, "DoubleTest");
		boolean pass = true;
		double values[] = JAXWS_Data.double_data;
		double response;

		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				response = port.echoDouble(values[i]);

				if (response != values[i]) {
					TestUtil.logErr("DoubleTest failed - expected " + values[i] + ",  received: " + response);
					pass = false;
				}
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "DoubleTest");
		return pass;
	}

	private boolean BooleanTest() {
		logger.log(Level.INFO, "BooleanTest");
		boolean pass = true;
		boolean values[] = JAXWS_Data.boolean_data;
		boolean response;

		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				response = port.echoBoolean(values[i]);

				if (!response == values[i]) {
					TestUtil.logErr("BooleanTest failed - expected " + values[i] + ",  received: " + response);
					pass = false;
				}
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "BooleanTest");
		return pass;
	}

	private boolean ByteTest() {
		logger.log(Level.INFO, "ByteTest");
		boolean pass = true;
		byte values[] = JAXWS_Data.byte_data;
		byte response;

		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				response = port.echoByte(values[i]);

				if (response != values[i]) {
					TestUtil.logErr("ByteTest failed - expected " + values[i] + ",  received: " + response);
					pass = false;
				}
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "ByteTest");
		return pass;
	}

	private boolean QNameTest() {
		logger.log(Level.INFO, "QNameTest");
		boolean pass = true;
		QName values[] = JAXWS_Data.QName_data;
		QName response;

		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				response = port.echoQName(values[i]);
				if (values[i] == null && response == null) {
					continue;
				} else if (!response.equals(values[i])) {
					TestUtil.logErr("QNameTest failed - expected " + values[i] + ",  received: " + response);
					pass = false;
				}
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "QNameTest");
		return pass;
	}

	private boolean DateTimeTest() {
		logger.log(Level.INFO, "DateTimeTest");
		boolean pass = true;
		XMLGregorianCalendar values[] = JAXWS_Data.XMLGregorianCalendar_data;
		XMLGregorianCalendar response;

		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			for (int i = 0; i < values.length; i++) {
				response = port.echoDateTime(values[i]);
				if (!JAXWS_Data.compareValues(values[i], response, "XMLGregorianCalendar"))
					pass = false;

			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "DateTimeTest");
		return pass;
	}

	private boolean Base64BinaryTest() {
		logger.log(Level.INFO, "Base64BinaryTest");
		boolean pass = false;
		byte values[] = JAXWS_Data.byte_data;
		byte[] response;

		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			response = port.echoBase64Binary(values);
			pass = JAXWS_Data.compareArrayValues(values, response, "byte");
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
		byte[] response;

		logger.log(Level.INFO, "Passing/Returning data to/from JAXWS Service");
		try {
			response = port.echoHexBinary(values);
			pass = JAXWS_Data.compareArrayValues(values, response, "byte");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "HexBinaryTest");
		return pass;
	}

	private boolean StringArrayTest() {
		logger.log(Level.INFO, "StringArrayTest");
		boolean pass = false;
		List<String> values = JAXWS_Data.list_String_nonull_data;
		List<String> response;
		logger.log(Level.INFO, "Passing/Returning array data to/from JAXWS Service");
		try {
			response = port.echoStringArray(values);
			logger.log(Level.INFO, "Compare response with input ....");
			pass = JAXWS_Data.compareArrayValues(values, response, "String");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "StringArrayTest");
		return pass;
	}

	private boolean IntegerArrayTest() {
		logger.log(Level.INFO, "IntegerArrayTest");
		boolean pass = false;
		List<BigInteger> values = JAXWS_Data.list_BigInteger_nonull_data;
		List<BigInteger> response;
		logger.log(Level.INFO, "Passing/Returning array data to/from JAXWS Service");
		try {
			response = port.echoIntegerArray(values);
			pass = JAXWS_Data.compareArrayValues(values, response, "BigInteger");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "IntegerArrayTest");
		return pass;
	}

	private boolean IntArrayTest() {
		logger.log(Level.INFO, "IntArrayTest");
		boolean pass = false;
		List<Integer> values = JAXWS_Data.list_Integer_nonull_data;
		List<Integer> response;

		logger.log(Level.INFO, "Passing/Returning array data to/from JAXWS Service");
		try {
			response = port.echoIntArray(values);
			pass = JAXWS_Data.compareArrayValues(values, response, "Integer");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "IntArrayTest");
		return pass;
	}

	private boolean LongArrayTest() {
		logger.log(Level.INFO, "LongArrayTest");
		boolean pass = false;
		List<Long> values = JAXWS_Data.list_Long_nonull_data;
		List<Long> response;

		logger.log(Level.INFO, "Passing/Returning array data to/from JAXWS Service");
		try {
			response = port.echoLongArray(values);
			pass = JAXWS_Data.compareArrayValues(values, response, "Long");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "LongArrayTest");
		return pass;
	}

	private boolean ShortArrayTest() {
		logger.log(Level.INFO, "ShortArrayTest");
		boolean pass = false;
		List<Short> values = JAXWS_Data.list_Short_nonull_data;
		List<Short> response;

		logger.log(Level.INFO, "Passing/Returning array data to/from JAXWS Service");
		try {
			response = port.echoShortArray(values);
			pass = JAXWS_Data.compareArrayValues(values, response, "Short");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "ShortArrayTest");
		return pass;
	}

	private boolean FloatArrayTest() {
		logger.log(Level.INFO, "FloatArrayTest");
		boolean pass = false;
		List<Float> values = JAXWS_Data.list_Float_nonull_data;
		List<Float> response;

		logger.log(Level.INFO, "Passing/Returning array data to/from JAXWS Service");
		try {
			response = port.echoFloatArray(values);
			pass = JAXWS_Data.compareArrayValues(values, response, "Float");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "FloatArrayTest");
		return pass;
	}

	private boolean DoubleArrayTest() {
		logger.log(Level.INFO, "DoubleArrayTest");
		boolean pass = false;
		List<Double> values = JAXWS_Data.list_Double_nonull_data;
		List<Double> response;

		logger.log(Level.INFO, "Passing/Returning array data to/from JAXWS Service");
		try {
			response = port.echoDoubleArray(values);
			pass = JAXWS_Data.compareArrayValues(values, response, "Double");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "DoubleArrayTest");
		return pass;
	}

	private boolean DecimalArrayTest() {
		logger.log(Level.INFO, "DecimalArrayTest");
		boolean pass = false;
		List<BigDecimal> values = JAXWS_Data.list_BigDecimal_nonull_data;
		List<BigDecimal> response;

		logger.log(Level.INFO, "Passing/Returning array data to/from JAXWS Service");
		try {
			response = port.echoDecimalArray(values);
			pass = JAXWS_Data.compareArrayValues(values, response, "BigDecimal");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "DecimalArrayTest");
		return pass;
	}

	private boolean BooleanArrayTest() {
		logger.log(Level.INFO, "BooleanArrayTest");
		boolean pass = false;
		List<Boolean> values = JAXWS_Data.list_Boolean_nonull_data;
		List<Boolean> response;

		logger.log(Level.INFO, "Passing/Returning array data to/from JAXWS Service");
		try {
			response = port.echoBooleanArray(values);
			pass = JAXWS_Data.compareArrayValues(values, response, "Boolean");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "BooleanArrayTest");
		return pass;
	}

	private boolean ByteArrayTest() {
		logger.log(Level.INFO, "ByteArrayTest");
		boolean pass = false;
		List<Byte> values = JAXWS_Data.list_Byte_nonull_data;
		List<Byte> response;

		logger.log(Level.INFO, "Passing/Returning array data to/from JAXWS Service");
		try {
			response = port.echoByteArray(values);
			pass = JAXWS_Data.compareArrayValues(values, response, "byte");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "ByteArrayTest");
		return pass;
	}

	private boolean QNameArrayTest() {
		logger.log(Level.INFO, "QNameArrayTest");
		boolean pass = false;
		List<QName> values = JAXWS_Data.list_QName_nonull_data;
		List<QName> response;

		logger.log(Level.INFO, "Passing/Returning array data to/from JAXWS Service");
		try {
			response = port.echoQNameArray(values);
			pass = JAXWS_Data.compareArrayValues(values, response, "QName");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "QNameArrayTest");
		return pass;
	}

	private boolean DateTimeArrayTest() {
		logger.log(Level.INFO, "DateTimeArrayTest");
		boolean pass = false;
		List<XMLGregorianCalendar> values = JAXWS_Data.list_XMLGregorianCalendar_nonull_data;
		List<XMLGregorianCalendar> response;

		logger.log(Level.INFO, "Passing/Returning array data to/from JAXWS Service");
		try {
			response = port.echoDateTimeArray(values);
			pass = JAXWS_Data.compareArrayValues(values, response, "XMLGregorianCalendar");
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			pass = false;
		}

		printTestStatus(pass, "DateTimeArrayTest");
		return pass;
	}
}
