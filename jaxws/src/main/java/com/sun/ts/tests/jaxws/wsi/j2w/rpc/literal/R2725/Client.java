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

package com.sun.ts.tests.jaxws.wsi.j2w.rpc.literal.R2725;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.sharedclients.simpleclient.SimpleTest;
import com.sun.ts.tests.jaxws.sharedclients.simpleclient.SimpleTestClient;
import com.sun.ts.tests.jaxws.wsi.requests.SOAPRequests;
import com.sun.ts.tests.jaxws.wsi.utils.SOAPUtils;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

public class Client extends BaseClient implements SOAPRequests {

	private SimpleTestClient client;

	static SimpleTest service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	/**
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * @class.setup_props: webServerHost; webServerPort; platform.mode;
	 *
	 * @param args
	 * @param properties
	 *
	 * @throws Exception
	 */
	public void setup(String[] args, Properties properties) throws Exception {
		super.setup();
		client = (SimpleTestClient) ClientFactory.getClient(SimpleTestClient.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {

		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testVersionMismatchFaultcode
	 *
	 * @assertion_ids: WSI:SPEC:R2725
	 *
	 * @test_Strategy: Make a request and inspect response to ensure Exceptioncode
	 *                 "VersionMismatch" was sent.
	 *
	 * @throws Exception
	 */
	@Test
	public void testVersionMismatchFaultcode() throws Exception {
		SOAPMessage response = null;
		try {
			response = client.makeSaajRequest(BAD_SOAP_ENVELOPE);
			client.logMessageInHarness(response);
		} catch (Exception e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
		try {
			validateVersionMismatchFaultcode(response);
		} catch (SOAPException se) {
			throw new Exception("Error creating response object", se);
		}
	}

	/**
	 * @testName: testVersionMismatchFaultcodeWithMustUnderstand
	 *
	 * @assertion_ids: WSI:SPEC:R2725
	 *
	 * @test_Strategy: Make a request and inspect response to ensure Exceptioncode
	 *                 "VersionMismatch" was sent.
	 *
	 * @throws Exception
	 */
	@Test
	public void testVersionMismatchFaultcodeWithMustUnderstand() throws Exception {
		SOAPMessage response = null;
		try {
			response = client.makeSaajRequest(BAD_SOAP_ENVELOPE_WITH_HEADER);
			client.logMessageInHarness(response);
		} catch (Exception e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
		try {
			validateVersionMismatchFaultcode(response);
		} catch (SOAPException se) {
			throw new Exception("Error creating response object", se);
		}
	}

	/**
	 * @testName: testVersionMismatchFaultcodeWithNonExistantOperation
	 *
	 * @assertion_ids: WSI:SPEC:R2725
	 *
	 * @test_Strategy: Make a request and inspect response to ensure Exceptioncode
	 *                 "VersionMismatch" was sent.
	 *
	 * @throws Exception
	 */
	@Test
	public void testVersionMismatchFaultcodeWithNonExistantOperation() throws Exception {
		SOAPMessage response = null;
		try {
			response = client.makeSaajRequest(BAD_SOAP_ENVELOPE_NON_EXISTANT_OPERATION);
			client.logMessageInHarness(response);
		} catch (Exception e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
		try {
			validateVersionMismatchFaultcode(response);
		} catch (SOAPException se) {
			throw new Exception("Error creating response object", se);
		}
	}

	/**
	 * @testName: testMustUnderstandFaultcode
	 *
	 * @assertion_ids: WSI:SPEC:R2725
	 *
	 * @test_Strategy: Make a request and inspect response to ensure Exceptioncode
	 *                 "MustUnderstand" was sent.
	 *
	 * @throws Exception
	 */
	@Test
	public void testMustUnderstandFaultcode() throws Exception {
		SOAPMessage response = null;
		try {
			response = client.makeSaajRequest(MUST_UNDERSTAND_HEADER);
			client.logMessageInHarness(response);
		} catch (Exception e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
		try {
			validateMustUnderstandFaultcode(response);
		} catch (SOAPException se) {
			throw new Exception("Error creating response object", se);
		}
	}

	/**
	 * @testName: testMustUnderstandFaultcodeWithNonExistantOperation
	 *
	 * @assertion_ids: WSI:SPEC:R2725
	 *
	 * @test_Strategy: Make a request and inspect response to ensure Exceptioncode
	 *                 "MustUnderstand" was sent.
	 *
	 * @throws Exception
	 */
	@Test
	public void testMustUnderstandFaultcodeWithNonExistantOperation() throws Exception {
		SOAPMessage response = null;
		try {
			response = client.makeSaajRequest(MUST_UNDERSTAND_HEADER_NON_EXISTANT_OPERATION);
			client.logMessageInHarness(response);
		} catch (Exception e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
		try {
			validateMustUnderstandFaultcode(response);
		} catch (SOAPException se) {
			throw new Exception("Error creating response object", se);
		}
	}

	/**
	 * @testName: testClientFaultcode
	 *
	 * @assertion_ids: WSI:SPEC:R2725
	 *
	 * @test_Strategy: Make a request and inspect response to ensure Exceptioncode
	 *                 "Client" was sent.
	 *
	 * @throws Exception
	 */
	@Test
	public void testClientFaultcode() throws Exception {
		SOAPMessage response = null;
		try {
			response = client.makeSaajRequest(NON_EXISTANT_OPERATION);
			client.logMessageInHarness(response);
		} catch (Exception e) {
			throw new Exception("Test didn't complete properly: ", e);
		}
		try {
			validateClientFaultcode(response);
		} catch (SOAPException se) {
			throw new Exception("Error creating response object", se);
		}
	}

	private void validateVersionMismatchFaultcode(SOAPMessage response) throws Exception, SOAPException {
		if (!SOAPUtils.isVersionMismatchFaultcode(response)) {
			throw new Exception("Invalid soap:Fault:  Exceptioncode must be \"VersionMismatch\" (BP-R2725)");
		}
	}

	private void validateClientFaultcode(SOAPMessage response) throws Exception, SOAPException {
		if (!SOAPUtils.isClientFaultcode(response)) {
			throw new Exception("Invalid soap:Fault:  Exceptioncode must be \"Client\" (BP-R2725)");
		}
	}

	private void validateMustUnderstandFaultcode(SOAPMessage response) throws Exception, SOAPException {
		if (!SOAPUtils.isMustUnderstandFaultcode(response)) {
			throw new Exception("Invalid soap:Fault:  Exceptioncode must be \"MustUnderstand\" (BP-R2725)");
		}
	}
}
