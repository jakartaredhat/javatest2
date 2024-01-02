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
 * $Id: Client.java 56875 2009-02-23 21:04:59Z af70133 $
 */

package com.sun.ts.tests.jaxws.mapping.w2jmapping.document.literal.eprsubtypes;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Method;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;

import jakarta.xml.ws.Holder;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;

public class Client {

	private static final String EXPECTED_SEI_CLASS = "com.sun.ts.tests.jaxws.mapping.w2jmapping.document.literal.eprsubtypes.Hello";

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

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
	 * @testName: VerifyEPRSubTypesTest1
	 *
	 * @assertion_ids: JAXWS:SPEC:2086;
	 *
	 * @test_Strategy: Generate classes from a wsdl/xsd that contain
	 * wsa:EndpointReference types and verify that JAXB correctly maps all
	 * wsa:EndpointReference types to W3CEndpointReference. Verify that the hello
	 * method on the generated sei class has the correct signatures for return type
	 * and method parameters. They should all be of type W3CEndpointReference. Any
	 * schema element of the type wsa:EndpointReference or its subtypes MUST be
	 * mapped to jakarta.xml.ws.wsaddressing.W3CEndpointReferencedefault.
	 */
	@Test
	public void VerifyEPRSubTypesTest1() throws Exception {
		TestUtil.logTrace("VerifyEPRSubTypesTest1");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Load class: " + EXPECTED_SEI_CLASS);
			Class seiClass = Class.forName(EXPECTED_SEI_CLASS);
			logger.log(Level.INFO, "seiClass=" + seiClass);
			logger.log(Level.INFO, "Verify that the hello method parameters map to W3CEndpointReference");
			Method m;
			try {
				Holder<W3CEndpointReference> eprHolder = new Holder<W3CEndpointReference>();
				Class eprHolderClass = eprHolder.getClass();
				m = seiClass.getDeclaredMethod("hello", W3CEndpointReference.class, W3CEndpointReference.class,
						W3CEndpointReference.class, eprHolderClass, eprHolderClass, eprHolderClass);
			} catch (Exception e) {
				TestUtil.logErr("The hello method parameters do not map to W3CEndpointReference");
				TestUtil.logErr("Caught exception: " + e.getMessage());
				throw new Exception("VerifyEPRSubTypesTest1 failed", e);
			}
			logger.log(Level.INFO, "Verify that hello method return type maps to void");
			Class retType = m.getReturnType();
			logger.log(Level.INFO, "retType=" + retType);
			if (!retType.equals(void.class)) {
				TestUtil.logErr("The hello method return does not map to void");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			throw new Exception("VerifyEPRSubTypesTest1 failed", e);
		}

		if (!pass)
			throw new Exception("VerifyEPRSubTypesTest1 failed");
	}

	/*
	 * @testName: VerifyEPRSubTypesTest2
	 *
	 * @assertion_ids: JAXWS:SPEC:2086;
	 *
	 * @test_Strategy: Generate classes from a wsdl/xsd that contain
	 * wsa:EndpointReference types and verify that JAXB correctly maps all
	 * wsa:EndpointReference types to W3CEndpointReference. Verify that the hello2
	 * method on the generated sei class has the correct signatures for return type
	 * and method parameters. They should all be of type W3CEndpointReference. Any
	 * schema element of the type wsa:EndpointReference or its subtypes MUST be
	 * mapped to jakarta.xml.ws.wsaddressing.W3CEndpointReferencedefault.
	 */
	@Test
	public void VerifyEPRSubTypesTest2() throws Exception {
		TestUtil.logTrace("VerifyEPRSubTypesTest2");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Load class: " + EXPECTED_SEI_CLASS);
			Class seiClass = Class.forName(EXPECTED_SEI_CLASS);
			logger.log(Level.INFO, "seiClass=" + seiClass);
			logger.log(Level.INFO, "Verify that the hello2 method parameters map to W3CEndpointReference");
			Method m;
			try {
				Holder<W3CEndpointReference> eprHolder = new Holder<W3CEndpointReference>();
				Class eprHolderClass = eprHolder.getClass();
				m = seiClass.getDeclaredMethod("hello2", eprHolderClass, eprHolderClass, eprHolderClass);
			} catch (Exception e) {
				TestUtil.logErr("The hello2 method parameters do not map to W3CEndpointReference");
				TestUtil.logErr("Caught exception: " + e.getMessage());
				throw new Exception("VerifyEPRSubTypesTest2 failed", e);
			}
			logger.log(Level.INFO, "Verify that hello2 method return type maps to void");
			Class retType = m.getReturnType();
			logger.log(Level.INFO, "retType=" + retType);
			if (!retType.equals(void.class)) {
				TestUtil.logErr("The hello2 method return does not map to void");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			throw new Exception("VerifyEPRSubTypesTest2 failed", e);
		}

		if (!pass)
			throw new Exception("VerifyEPRSubTypesTest2 failed");
	}

	/*
	 * @testName: VerifyEPRSubTypesTest3
	 *
	 * @assertion_ids: JAXWS:SPEC:2086;
	 *
	 * @test_Strategy: Generate classes from a wsdl/xsd that contain
	 * wsa:EndpointReference types and verify that JAXB correctly maps all
	 * wsa:EndpointReference types to W3CEndpointReference. Verify that the hello3
	 * method on the generated sei class has the correct signatures for return type
	 * and method parameters. They should all be of type W3CEndpointReference. Any
	 * schema element of the type wsa:EndpointReference or its subtypes MUST be
	 * mapped to jakarta.xml.ws.wsaddressing.W3CEndpointReferencedefault.
	 */
	@Test
	public void VerifyEPRSubTypesTest3() throws Exception {
		TestUtil.logTrace("VerifyEPRSubTypesTest3");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Load class: " + EXPECTED_SEI_CLASS);
			Class seiClass = Class.forName(EXPECTED_SEI_CLASS);
			logger.log(Level.INFO, "seiClass=" + seiClass);
			logger.log(Level.INFO, "Verify that the hello3 method parameters map to W3CEndpointReference");
			Method m;
			try {
				m = seiClass.getDeclaredMethod("hello3", W3CEndpointReference.class);
			} catch (Exception e) {
				TestUtil.logErr("The hello3 method parameters do not map to W3CEndpointReference");
				TestUtil.logErr("Caught exception: " + e.getMessage());
				throw new Exception("VerifyEPRSubTypesTest3 failed", e);
			}
			logger.log(Level.INFO, "Verify that hello3 method return type maps to void");
			Class retType = m.getReturnType();
			logger.log(Level.INFO, "retType=" + retType);
			if (!retType.equals(W3CEndpointReference.class)) {
				TestUtil.logErr("The hello3 method return does not map to W3CEndpointReference");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			throw new Exception("VerifyEPRSubTypesTest3 failed", e);
		}

		if (!pass)
			throw new Exception("VerifyEPRSubTypesTest3 failed");
	}
}
