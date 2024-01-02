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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws_soap.SOAPFaultException;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;

import jakarta.xml.soap.SOAPFault;
import jakarta.xml.ws.soap.SOAPFaultException;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private jakarta.xml.soap.Detail detail = null;

	private jakarta.xml.soap.DetailEntry detailentry = null;

	private jakarta.xml.soap.SOAPFault soapfault = null;

	private jakarta.xml.soap.Name name = null;

	private jakarta.xml.soap.MessageFactory msgfactory = null;

	/* Test setup */

	/*
	 * @class.setup_props:
	 */
	@BeforeEach
	public void setup() throws Exception {
		try {
			// Create a soap message factory instance.
			logger.log(Level.INFO, "Create a SOAP MessageFactory instance");
			msgfactory = jakarta.xml.soap.MessageFactory.newInstance();

			// Create a soap message.
			logger.log(Level.INFO, "Create a SOAPMessage");
			jakarta.xml.soap.SOAPMessage soapmsg = msgfactory.createMessage();

			// Retrieve the soap part from the soap message..
			logger.log(Level.INFO, "Get SOAP Part");
			jakarta.xml.soap.SOAPPart sp = soapmsg.getSOAPPart();

			// Retrieve the envelope from the soap part.
			logger.log(Level.INFO, "Get SOAP Envelope");
			jakarta.xml.soap.SOAPEnvelope envelope = sp.getEnvelope();

			// Retrieve the soap body from the envelope.
			logger.log(Level.INFO, "Get SOAP Body");
			jakarta.xml.soap.SOAPBody body = envelope.getBody();

			// Add a soap Fault to the soap body.
			soapfault = body.addFault();

			// Add a detail to the soap Fault.
			detail = soapfault.addDetail();
			name = envelope.createName("GetLastTradePrice", "WOMBAT", "http://www.wombat.org/trader");
			detailentry = detail.addDetailEntry(name);
		} catch (Exception e) {
			throw new Exception("setup failed:", e);
		}
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: SOAPFaultExceptionConstructorTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:113;
	 *
	 * @test_Strategy: Create instance via SOAPFaultException constructor. Verify
	 * SOAPFaultException object created successfully.
	 */
	@Test
	public void SOAPFaultExceptionConstructorTest() throws Exception {
		TestUtil.logTrace("SOAPFaultExceptionConstructorTest");
		boolean pass = true;
		logger.log(Level.INFO, "Create instance via SOAPFaultException(jakarta.xml.soap.SOAPFault");
		SOAPFaultException sf = new SOAPFaultException(soapfault);
		if (sf != null) {
			logger.log(Level.INFO, "SOAPFaultException object created successfully");
		} else {
			TestUtil.logErr("SOAPFaultException object not created");
			pass = false;
		}

		if (!pass)
			throw new Exception("SOAPFaultExceptionConstructorTest failed");
	}

	/*
	 * @testName: getFaultTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:112;
	 *
	 * @test_Strategy: Create instance via SOAPFaultException constructor. Get the
	 * embedded SOAPFault instance and verify it is what was set.
	 */
	@Test
	public void getFaultTest() throws Exception {
		TestUtil.logTrace("getFaultTest");
		SOAPFault theFault;
		boolean pass = true;
		logger.log(Level.INFO, "Create instance via SOAPFaultException(jakarta.xml.soap.SOAPFault");
		SOAPFaultException sf = new SOAPFaultException(soapfault);
		if (sf != null) {
			logger.log(Level.INFO, "SOAPFaultException object created successfully");
			theFault = sf.getFault();
			if (theFault.equals(soapfault)) {
				logger.log(Level.INFO, "SOAPFault returned match");
			} else {
				TestUtil.logErr("SOAPFault returned mismatch - expected: " + soapfault + ", received: " + theFault);
				pass = false;
			}
		} else {
			TestUtil.logErr("SOAPFaultException object not created");
			pass = false;
		}

		if (!pass)
			throw new Exception("getFaultTest failed");
	}
}
