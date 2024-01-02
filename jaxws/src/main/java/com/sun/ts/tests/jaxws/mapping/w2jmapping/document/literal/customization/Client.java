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
 * @(#)Client.java	1.39 05/08/26
 */

package com.sun.ts.tests.jaxws.mapping.w2jmapping.document.literal.customization;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.AnnotationUtils;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;

import jakarta.xml.ws.WebServiceFeature;

public class Client {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static final String PKG = "com.sun.ts.tests.jaxws.mapping.w2jmapping.document.literal.customization.";

	// Expected mappings for wsdl:fault element mapping
	private static final String EXPECTED_FAULT_WRAPPER = PKG + "W2JDLFault";

	private static final String EXPECTED_FAULT_WRAPPER2 = PKG + "MyFault";

	private static final String EXPECTED_FAULT_BEAN = PKG + "MyFaultReason";

	// Expected mappings for wsdl:service and wsdl:port element mapping
	private static final String EXPECTED_SERVICE_CLASS_NAME = "W2JDLCustomization";

	private static final String EXPECTED_SERVICE = PKG + "W2JDLCustomization";

	private static final String EXPECTED_ENDPOINT = PKG + "W2JDLCustomizationEndpoint";

	private static final String EXPECTED_SERVICE_INTERFACE = "jakarta.xml.ws.Service";

	private static final String EXPECTED_SERVICE_EXCEPTION = "jakarta.xml.ws.WebServiceException";

	private static final String EXPECTED_GET_PORTNAME_METHOD = "getW2JDLCustomizationEndpointPort";

	// Used for soap:header binding test
	private static final String EXPECTED_HEADER_TYPE = PKG + "MyHeader";

	// Used for wrapper style tests
	private static final String ENABLEWRAPPER_TRUE_METHOD = "wrapperElement1";

	private static final String EXPECTED_ENABLEWRAPPER_TRUE_RETURN_TYPE = "java.lang.String";

	private static final String EXPECTED_ENABLEWRAPPER_TRUE_PARAMETER_TYPE = "java.lang.String";

	private static final String ENABLEWRAPPER_FALSE_METHOD = "wrapperElement2";

	private static final String EXPECTED_ENABLEWRAPPER_FALSE_RETURN_TYPE = PKG + "WrapperElement22";

	private static final String EXPECTED_ENABLEWRAPPER_FALSE_PARAMETER_TYPE = PKG + "WrapperElement2";

	// Used for soap:header and soap:fault binding test
	private static final String EXPECTED_HEADER2_TYPE = PKG + "ConfigHeader";

	private static final String EXPECTED_FAULT1_EXCEPTION = PKG + "Fault1";

	private static final String EXPECTED_FAULT2_EXCEPTION = PKG + "Fault2";

	/*
	 * @class.setup_props: ts.home;
	 */
	@BeforeEach
	public void setup() throws Exception {
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {

		logger.log(Level.INFO, "cleanup");
	}

	/*
	 * @testName: PortTypeTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2001; JAXWS:SPEC:2002; JAXWS:SPEC:2003;
	 * JAXWS:SPEC:2004; JAXWS:SPEC:2006; JAXWS:SPEC:2010; JAXWS:SPEC:2041;
	 * JAXWS:SPEC:2007; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify wsdl:definitions and wsdl:portType mapping
	 */
	@Test
	public void PortTypeTest() throws Exception {
		TestUtil.logTrace("PortTypeTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Verify wsdl:portType mapping");
			Class.forName(EXPECTED_ENDPOINT, false, this.getClass().getClassLoader());
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("PortTypeTest failed", e);
		}

		if (!pass)
			throw new Exception("PortTypeTest failed");
	}

	/*
	 * @testName: OperationTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2001; JAXWS:SPEC:2002; JAXWS:SPEC:2003;
	 * JAXWS:SPEC:2004; JAXWS:SPEC:2013; JAXWS:SPEC:2014; JAXWS:SPEC:2017;
	 * JAXWS:SPEC:2041; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify mapping of wsdl:operation
	 */
	@Test
	public void OperationTest() throws Exception {
		TestUtil.logTrace("OperationTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Verify wsdl:operation mapping");
			Class c = Class.forName(EXPECTED_ENDPOINT, false, this.getClass().getClassLoader());
			String methodName = "helloOperation";
			if (!JAXWS_Util.doesMethodExist(c, methodName)) {
				TestUtil.logErr("Method " + methodName + ", was not found");
				pass = false;
			}
			methodName = "onewayOperation";
			if (!JAXWS_Util.doesMethodExist(c, methodName)) {
				TestUtil.logErr("Method " + methodName + ", was not found");
				pass = false;
			}
			methodName = "mode1Operation";
			if (!JAXWS_Util.doesMethodExist(c, methodName)) {
				TestUtil.logErr("Method " + methodName + ", was not found");
				pass = false;
			}
			methodName = "mode2Operation";
			if (!JAXWS_Util.doesMethodExist(c, methodName)) {
				TestUtil.logErr("Method " + methodName + ", was not found");
				pass = false;
			}
			methodName = "mode3Operation";
			if (!JAXWS_Util.doesMethodExist(c, methodName)) {
				TestUtil.logErr("Method " + methodName + ", was not found");
				pass = false;
			}
			if (!pass) {
				TestUtil.logErr("One ofthe operations does not exist in the SEI");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("OperationTest failed", e);
		}

		if (!pass)
			throw new Exception("OperationTest failed");
	}

	/*
	 * @testName: FaultTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2001; JAXWS:SPEC:2002; JAXWS:SPEC:2003;
	 * JAXWS:SPEC:2004; JAXWS:SPEC:2043; JAXWS:SPEC:2044; JAXWS:SPEC:2041;
	 * JAXWS:SPEC:8009; JAXWS:SPEC:2061; JAXWS:SPEC:2074; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify wsdl:fault element mapping
	 */
	@Test
	public void FaultTest() throws Exception {
		TestUtil.logTrace("FaultTest");
		boolean pass = true;

		logger.log(Level.INFO, "Verify wsdl:fault mapping");
		logger.log(Level.INFO, "Loading Exception wrapper " + EXPECTED_FAULT_WRAPPER);
		Class faultWrapper = null;
		try {
			faultWrapper = Class.forName(EXPECTED_FAULT_WRAPPER);
		} catch (Exception e) {
			TestUtil.logErr("Exception loading class: " + e);
			pass = false;
		}

		// Check to ensure Wrapper Exception class is annotated using the WebFault
		// annotation.
		boolean found = AnnotationUtils.verifyWebFaultAnnotation(faultWrapper, "MyFaultReason",
				"http://w2jdlcustomization/types", EXPECTED_FAULT_BEAN);
		if (!found) {
			TestUtil.logErr(
					"Wrapper Exception Class is not annotated with WebFault annotation - " + EXPECTED_FAULT_WRAPPER);
			pass = false;
		} else
			logger.log(Level.INFO,
					"Wrapper Exception Class is annotated with WebFault annotation - " + EXPECTED_FAULT_WRAPPER);

		logger.log(Level.INFO, "Loading Exception bean " + EXPECTED_FAULT_BEAN);
		try {
			Class.forName(EXPECTED_FAULT_BEAN);
		} catch (Exception e) {
			TestUtil.logErr("Exception loading class: " + e);
			pass = false;
		}

		logger.log(Level.INFO, "Instantiate Exception bean and call its methods ... " + EXPECTED_FAULT_BEAN);
		com.sun.ts.tests.jaxws.mapping.w2jmapping.document.literal.customization.MyFaultReason mfr = new com.sun.ts.tests.jaxws.mapping.w2jmapping.document.literal.customization.MyFaultReason();
		logger.log(Level.INFO, "setMessage to foo");
		mfr.setMessage("foo");
		logger.log(Level.INFO, "getMessage=" + mfr.getMessage());

		logger.log(Level.INFO, "Instantiate Exception wrapper exception constructor 1 ... " + EXPECTED_FAULT_WRAPPER);
		com.sun.ts.tests.jaxws.mapping.w2jmapping.document.literal.customization.W2JDLFault mf = new com.sun.ts.tests.jaxws.mapping.w2jmapping.document.literal.customization.W2JDLFault(
				"myfault", mfr);

		logger.log(Level.INFO, "getFaultInfo from wrapper exception ... ");
		mfr = mf.getFaultInfo();

		logger.log(Level.INFO, "Instantiate Exception wrapper exception constructor 2 ... " + EXPECTED_FAULT_WRAPPER);
		mf = new com.sun.ts.tests.jaxws.mapping.w2jmapping.document.literal.customization.W2JDLFault("myfault", mfr,
				new Exception("foo"));

		// A generate endpoint interface must be created from wsdl:portType name
		logger.log(Level.INFO, "Loading endpoint interface " + EXPECTED_ENDPOINT);
		Class endpointClass = null;
		try {
			endpointClass = Class.forName(EXPECTED_ENDPOINT);
		} catch (Exception e) {
			TestUtil.logErr("Exception loading class: " + e);
			pass = false;
		}

		// Exception Equivalence Test
		try {
			Method[] methods = endpointClass.getMethods();
			Method helloOp = null;
			Method helloOp2 = null;
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().equals("helloOperation"))
					helloOp = methods[i];
				if (methods[i].getName().equals("helloOperation2"))
					helloOp2 = methods[i];
			}

			found = false;
			Class exceptions[] = helloOp.getExceptionTypes();
			for (int i = 0; i < exceptions.length; i++) {
				String name = exceptions[i].getName();
				logger.log(Level.INFO, "exceptions[" + i + "]=" + name);
				if (name.equals(EXPECTED_FAULT_WRAPPER))
					found = true;
			}
			if (!found) {
				TestUtil.logErr("helloOperation does not declare throws of exception " + EXPECTED_FAULT_WRAPPER);
				pass = false;
			} else
				logger.log(Level.INFO, "helloOperation does declare throws of exception " + EXPECTED_FAULT_WRAPPER);

			found = false;
			exceptions = helloOp2.getExceptionTypes();
			for (int i = 0; i < exceptions.length; i++) {
				String name = exceptions[i].getName();
				logger.log(Level.INFO, "exceptions[" + i + "]=" + name);
				if (name.equals(EXPECTED_FAULT_WRAPPER2))
					found = true;
			}
			if (!found) {
				TestUtil.logErr("helloOperation2 does not declare throws of exception " + EXPECTED_FAULT_WRAPPER2);
				pass = false;
			} else
				logger.log(Level.INFO, "helloOperation2 does declare throws of exception " + EXPECTED_FAULT_WRAPPER2);
		} catch (Exception e) {
			TestUtil.logErr("Exception: " + e);
		}

		if (!pass)
			throw new Exception("FaultTest failed");
	}

	/*
	 * @testName: ServiceAndPortTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2001; JAXWS:SPEC:2002; JAXWS:SPEC:2003;
	 * JAXWS:SPEC:2004; JAXWS:SPEC:2054; JAXWS:SPEC:2055; JAXWS:SPEC:2056;
	 * JAXWS:SPEC:2041; JAXWS:SPEC:2045; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify wsdl:service and wsdl:port element mapping
	 */
	@Test
	public void ServiceAndPortTest() throws Exception {
		TestUtil.logTrace("ServiceAndPortTest");
		boolean pass = true;

		logger.log(Level.INFO, "Verify wsdl:service and wsdl:port mapping");
		// A generate service interface must be created from wsdl:service name
		logger.log(Level.INFO, "Loading service interface " + EXPECTED_SERVICE);
		Class serviceClass = null;
		try {
			serviceClass = Class.forName(EXPECTED_SERVICE);
		} catch (Exception e) {
			TestUtil.logErr("Exception loading class: " + e);
			pass = false;
		}

		// A generate endpoint interface must be created from wsdl:portType name
		logger.log(Level.INFO, "Loading endpoint interface " + EXPECTED_ENDPOINT);
		try {
			Class.forName(EXPECTED_ENDPOINT);
		} catch (Exception e) {
			TestUtil.logErr("Exception loading class: " + e);
			pass = false;
		}

		// Service Class Interface MUST extend Service interface
		boolean found = false;
		if (serviceClass != null) {
			String name = serviceClass.getSuperclass().getName();
			if (name.equals(EXPECTED_SERVICE_INTERFACE)) {
				found = true;
			}
			if (!found) {
				TestUtil.logErr("Service Class Interface " + name + " does not extend " + EXPECTED_SERVICE_INTERFACE);
				pass = false;
			} else
				logger.log(Level.INFO,
						"Service Class Interface " + name + " does extend " + EXPECTED_SERVICE_INTERFACE);
		}

		// Service Class Interface MUST have a getPortName() method based on
		// wsd:port name
		found = false;
		Method m = null;
		if (serviceClass != null) {
			try {
				m = serviceClass.getDeclaredMethod(EXPECTED_GET_PORTNAME_METHOD, (Class[]) null);
			} catch (Exception e) {
				TestUtil.logErr("Exception: " + e);
				pass = false;
			}
			// getPortName() method MUST return Endpoint Interface Type
			if (m != null) {
				logger.log(Level.INFO, "Service Class Interface " + serviceClass.getName() + " does have port method "
						+ EXPECTED_GET_PORTNAME_METHOD);
				Class<?> returnType = m.getReturnType();
				found = false;
				if (returnType != null) {
					String name = returnType.getName();
					logger.log(Level.INFO, "returnType=" + name);
					if (name.equals(EXPECTED_ENDPOINT))
						found = true;
				}
				if (!found) {
					TestUtil.logErr("Service Port Method " + EXPECTED_GET_PORTNAME_METHOD + " does not return type as "
							+ EXPECTED_ENDPOINT);
					pass = false;
				} else
					logger.log(Level.INFO, "Service Port Method " + EXPECTED_GET_PORTNAME_METHOD
							+ " does return type as " + EXPECTED_ENDPOINT);
			} else {
				TestUtil.logErr("Service Class Interface " + serviceClass.getName() + " does not have port method "
						+ EXPECTED_GET_PORTNAME_METHOD);
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("ServiceAndPortTest failed");
	}

	/*
	 * @testName: ServiceConstructorsTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2001; JAXWS:SPEC:2002; JAXWS:SPEC:2003;
	 * JAXWS:SPEC:2004; JAXWS:SPEC:2054; JAXWS:SPEC:2055; JAXWS:SPEC:2056;
	 * JAXWS:SPEC:2041; JAXWS:SPEC:2045; JAXWS:SPEC:7013; JAXWS:SPEC:4032;
	 *
	 * @test_Strategy: Verify that all expected constructors exist on service
	 * interface
	 */
	@Test
	public void ServiceConstructorsTest() throws Exception {
		TestUtil.logTrace("ServiceConstructorsTest");
		boolean pass = true;

		logger.log(Level.INFO, "Verify that all expected constructors exist on service interface");
		logger.log(Level.INFO, "Loading service interface class: " + EXPECTED_SERVICE);
		Class serviceClass = null;
		try {
			serviceClass = Class.forName(EXPECTED_SERVICE);
		} catch (Exception e) {
			TestUtil.logErr("Exception loading class: " + e);
			pass = false;
		}
		if (serviceClass != null) {
			try {
				logger.log(Level.INFO, "Verify constructor: " + EXPECTED_SERVICE_CLASS_NAME + "()");
				Constructor ctr = serviceClass.getConstructor();
			} catch (Exception e) {
				TestUtil.logErr("Constructor does not exist");
				pass = false;
			}
			try {
				logger.log(Level.INFO,
						"Verify constructor: " + EXPECTED_SERVICE_CLASS_NAME + "(WebServiceFeature... features)");
				Constructor ctr = serviceClass.getConstructor(WebServiceFeature[].class);
			} catch (Exception e) {
				TestUtil.logErr("Constructor does not exist");
				pass = false;
			}
			try {
				logger.log(Level.INFO, "Verify constructor: " + EXPECTED_SERVICE_CLASS_NAME + "(URL wsdlLocation)");
				Constructor ctr = serviceClass.getConstructor(URL.class);
			} catch (Exception e) {
				TestUtil.logErr("Constructor does not exist");
				pass = false;
			}
			try {
				logger.log(Level.INFO, "Verify constructor: " + EXPECTED_SERVICE_CLASS_NAME
						+ "(URL wsdlLocation, WebServiceFeature... features)");
				Constructor ctr = serviceClass.getConstructor(URL.class, WebServiceFeature[].class);
			} catch (Exception e) {
				TestUtil.logErr("Constructor does not exist");
				pass = false;
			}
			try {
				logger.log(Level.INFO,
						"Verify constructor: " + EXPECTED_SERVICE_CLASS_NAME + "(URL wsdlLocation, QName serviceName)");
				Constructor ctr = serviceClass.getConstructor(URL.class, QName.class);
			} catch (Exception e) {
				TestUtil.logErr("Constructor does not exist");
				pass = false;
			}
			try {
				logger.log(Level.INFO, "Verify constructor: " + EXPECTED_SERVICE_CLASS_NAME
						+ "(URL wsdlLocation, QName serviceName, WebServiceFeature... features)");
				Constructor ctr = serviceClass.getConstructor(URL.class, QName.class, WebServiceFeature[].class);
			} catch (Exception e) {
				TestUtil.logErr("Constructor does not exist");
				pass = false;
			}
		}

		if (!pass)
			throw new Exception("ServiceConstructorsTest failed");
	}

	/*
	 * @testName: HeaderTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2001; JAXWS:SPEC:2002; JAXWS:SPEC:2003;
	 * JAXWS:SPEC:2004; JAXWS:SPEC:2041; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify soap:header (Header Binding Extension)
	 */
	@Test
	public void HeaderTest() throws Exception {
		TestUtil.logTrace("HeaderTest");
		boolean pass = true;

		logger.log(Level.INFO, "Verify soap:header (Header Binding Extension)");
		// A generate endpoint interface must be created from wsdl:portType name
		logger.log(Level.INFO, "Loading endpoint interface " + EXPECTED_ENDPOINT);
		try {
			Class.forName(EXPECTED_ENDPOINT);
		} catch (Exception e) {
			TestUtil.logErr("Exception loading class: " + e);
			pass = false;
		}

		// The Header generated type
		logger.log(Level.INFO, "Loading header type " + EXPECTED_HEADER_TYPE);
		try {
			Class.forName(EXPECTED_HEADER_TYPE);
		} catch (Exception e) {
			TestUtil.logErr("Exception loading class: " + e);
			pass = false;
		}

		if (!pass)
			throw new Exception("HeaderTest failed");
	}

	/*
	 * @testName: SoapHeaderAndFaultTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2001; JAXWS:SPEC:2002; JAXWS:SPEC:2003;
	 * JAXWS:SPEC:2004; JAXWS:SPEC:2041; JAXWS:SPEC:8011; JAXWS:SPEC:2074;
	 * JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify soap:header and soap:fault mappings
	 */
	@Test
	public void SoapHeaderAndFaultTest() throws Exception {
		TestUtil.logTrace("SoapHeaderAndFaultTest");
		boolean pass = true;

		logger.log(Level.INFO, "Verify soap:header and soap:fault mappings");
		// The generated endpoint interface must be created from wsdl:portType name
		logger.log(Level.INFO, "Loading endpoint interface " + EXPECTED_ENDPOINT);
		Class endpointClass = null;
		try {
			endpointClass = Class.forName(EXPECTED_ENDPOINT);
		} catch (Exception e) {
			TestUtil.logErr("Exception loading class: " + e);
			pass = false;
		}

		// The generated Header type
		logger.log(Level.INFO, "Loading header type " + EXPECTED_HEADER2_TYPE);
		try {
			Class.forName(EXPECTED_HEADER2_TYPE);
		} catch (Exception e) {
			TestUtil.logErr("Exception loading class: " + e);
			pass = false;
		}

		// Customization via jaxws:enableAdditionalSOAPHeaderMapping means a header
		// type
		// as parameter to this method. Customization via jaxws:bindings in wsdl to
		// override
		// the generated header Exception exception class name.
		try {
			Method theMethod = null;
			Method[] methods = endpointClass.getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().equals("operationWithHeaderAndFaults")) {
					theMethod = methods[i];
					break;
				}
			}
			if (theMethod != null) {
				boolean found = false;
				Class parameters[] = theMethod.getParameterTypes();
				logger.log(Level.INFO, "Verify the jaxws:enableAdditionalSOAPHeaderMapping customization");
				logger.log(Level.INFO, "Verify that the soap:header is mapped to a parameter of the operation");
				for (int i = 0; i < parameters.length; i++) {
					String name = parameters[i].getName();
					logger.log(Level.INFO, "parameters[" + i + "]=" + name);
					if (name.equals(EXPECTED_HEADER2_TYPE))
						found = true;
				}
				if (!found) {
					TestUtil.logErr("operationWithHeaderAndFaults does not declare a header as parameter for type "
							+ EXPECTED_HEADER2_TYPE);
					pass = false;
				} else {
					logger.log(Level.INFO,
							"operationWithHeaderAndHeaderFaultsAndFaults does declare a header as parameter for type "
									+ EXPECTED_HEADER2_TYPE);
				}

				Class[] exceptions = theMethod.getExceptionTypes();
				boolean exception1 = false;
				boolean exception2 = false;
				logger.log(Level.INFO,
						"Verify the jaxws:bindings customization (override generate soap:headerfault and soap:fault exception name)");
				logger.log(Level.INFO, "Verify that each soap:headerfault is mapped to the expected exception name");
				logger.log(Level.INFO, "Verify that each soap:fault is mapped to the expected exception name");
				for (int j = 0; j < exceptions.length; j++) {
					String exceptName = exceptions[j].getName();
					logger.log(Level.INFO, "exception[" + j + "]=" + exceptName);
					if (exceptName.equals(EXPECTED_FAULT1_EXCEPTION)) {
						exception1 = true;
					} else if (exceptName.equals(EXPECTED_FAULT2_EXCEPTION)) {
						exception2 = true;
					}
				}
				if (!exception1) {
					TestUtil.logErr("The method: " + theMethod.getName() + " did not declare exception\n"
							+ EXPECTED_FAULT1_EXCEPTION);
					pass = false;
				}
				if (!exception2) {
					TestUtil.logErr("The method: " + theMethod.getName() + " did not declare exception\n"
							+ EXPECTED_FAULT2_EXCEPTION);
					pass = false;
				}
			} else {
				TestUtil.logErr("The method: operationWithHeaderAndFaults was not found\n");
			}
		} catch (Exception e) {
			TestUtil.logErr("Exception: " + e);
		}

		if (!pass)
			throw new Exception("SoapHeaderAndFaultTest failed");
	}

	/*
	 * @testName: WrapperStyleTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2001; JAXWS:SPEC:2002; JAXWS:SPEC:2003;
	 * JAXWS:SPEC:2024; JAXWS:SPEC:2025; JAXWS:SPEC:2027; JAXWS:SPEC:2041;
	 * JAXWS:SPEC:2072; JAXWS:SPEC:2073; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify Wrapper Style mapping
	 */
	@Test
	public void WrapperStyleTest() throws Exception {
		TestUtil.logTrace("WrapperStyleTest");
		boolean pass = true;
		try {
			Class c = Class.forName(EXPECTED_ENDPOINT, false, this.getClass().getClassLoader());
			Class returnType = JAXWS_Util.getMethodReturnType(c, ENABLEWRAPPER_TRUE_METHOD);
			if (returnType != null) {
				String sReturnType = returnType.getName();
				if (!sReturnType.equals(EXPECTED_ENABLEWRAPPER_TRUE_RETURN_TYPE)) {
					TestUtil.logErr("The return type for method: " + ENABLEWRAPPER_TRUE_METHOD + " was wrong");
					TestUtil.logErr("expected=" + EXPECTED_ENABLEWRAPPER_TRUE_RETURN_TYPE);
					TestUtil.logErr("actual=" + sReturnType);
					pass = false;
				}
			} else {
				TestUtil.logErr(
						"The method: " + ENABLEWRAPPER_TRUE_METHOD + " was not found for class:" + EXPECTED_ENDPOINT);
				pass = false;
			}
			Class parameterType = JAXWS_Util.getMethodParameterType(c, ENABLEWRAPPER_TRUE_METHOD, 0);
			if (parameterType != null) {
				String sParameterType = parameterType.getName();
				if (!sParameterType.equals(EXPECTED_ENABLEWRAPPER_TRUE_PARAMETER_TYPE)) {
					TestUtil.logErr("The parameter type for method: " + ENABLEWRAPPER_TRUE_METHOD + " was wrong");
					TestUtil.logErr("expected=" + EXPECTED_ENABLEWRAPPER_TRUE_PARAMETER_TYPE);
					TestUtil.logErr("actual=" + sParameterType);
					pass = false;
				}
			} else {
				TestUtil.logErr("The method: " + ENABLEWRAPPER_TRUE_METHOD + " was not found for class:"
						+ EXPECTED_ENDPOINT + " or the specified parameter did not exist");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("WrapperStyleTest failed", e);
		}

		if (!pass)
			throw new Exception("WrapperStyleTest failed");
	}

	/*
	 * @testName: NonWrapperStyleTest
	 *
	 * @assertion_ids: JAXWS:SPEC:2001; JAXWS:SPEC:2002; JAXWS:SPEC:2003;
	 * JAXWS:SPEC:2024; JAXWS:SPEC:2025; JAXWS:SPEC:2026; JAXWS:SPEC:2022;
	 * JAXWS:SPEC:2041; JAXWS:SPEC:7013;
	 *
	 * @test_Strategy: Verify NonWrapper Style mapping
	 */
	@Test
	public void NonWrapperStyleTest() throws Exception {
		TestUtil.logTrace("NonWrapperStyleTest");
		boolean pass = true;
		try {
			Class c = Class.forName(EXPECTED_ENDPOINT, false, this.getClass().getClassLoader());
			Class returnType = JAXWS_Util.getMethodReturnType(c, ENABLEWRAPPER_FALSE_METHOD);
			if (returnType != null) {
				String sReturnType = returnType.getName();
				if (!sReturnType.equals(EXPECTED_ENABLEWRAPPER_FALSE_RETURN_TYPE)) {
					TestUtil.logErr("The return type for method: " + ENABLEWRAPPER_FALSE_METHOD + " was wrong");
					TestUtil.logErr("expected=" + EXPECTED_ENABLEWRAPPER_FALSE_RETURN_TYPE);
					TestUtil.logErr("actual=" + sReturnType);
					pass = false;
				}
			} else {
				TestUtil.logErr(
						"The method: " + ENABLEWRAPPER_FALSE_METHOD + " was not found for class:" + EXPECTED_ENDPOINT);
				pass = false;
			}
			Class parameterType = JAXWS_Util.getMethodParameterType(c, ENABLEWRAPPER_FALSE_METHOD, 0);
			if (parameterType != null) {
				String sParameterType = parameterType.getName();
				if (!sParameterType.equals(EXPECTED_ENABLEWRAPPER_FALSE_PARAMETER_TYPE)) {
					TestUtil.logErr("The parameter type for method: " + ENABLEWRAPPER_FALSE_METHOD + " was wrong");
					TestUtil.logErr("expected=" + EXPECTED_ENABLEWRAPPER_FALSE_PARAMETER_TYPE);
					TestUtil.logErr("actual=" + sParameterType);
					pass = false;
				}
			} else {
				TestUtil.logErr("The method: " + ENABLEWRAPPER_FALSE_METHOD + " was not found for class:"
						+ EXPECTED_ENDPOINT + " or the specified parameter did not exist");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("NonWrapperStyleTest failed", e);
		}

		if (!pass)
			throw new Exception("NonWrapperStyleTest failed");
	}

}
