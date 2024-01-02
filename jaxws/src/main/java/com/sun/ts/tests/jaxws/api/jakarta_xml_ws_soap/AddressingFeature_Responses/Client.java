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
 * $Id: Client.java 53493 2007-05-22 17:06:35Z adf $
 */

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws_soap.AddressingFeature_Responses;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	// Expected Enum Constant Summary
	private final static jakarta.xml.ws.soap.AddressingFeature.Responses expectedEnums[] = {
			jakarta.xml.ws.soap.AddressingFeature.Responses.ALL,
			jakarta.xml.ws.soap.AddressingFeature.Responses.ANONYMOUS,
			jakarta.xml.ws.soap.AddressingFeature.Responses.NON_ANONYMOUS, };

	private boolean findEnums(jakarta.xml.ws.soap.AddressingFeature.Responses[] args) {
		boolean pass = true;
		boolean found;
		for (jakarta.xml.ws.soap.AddressingFeature.Responses a : args) {
			found = false;
			logger.log(Level.INFO, "Searching expected list of enums for " + a);
			for (jakarta.xml.ws.soap.AddressingFeature.Responses b : expectedEnums) {
				if (a == b) {
					found = true;
					break;
				}
			}
			if (!found) {
				pass = false;
				TestUtil.logErr("No enum found for " + a);
			} else {
				logger.log(Level.INFO, "Enum found for " + a);
			}
		}
		return pass;
	}

	private void printEnums(jakarta.xml.ws.soap.AddressingFeature.Responses[] args) {
		logger.log(Level.INFO, "Print Enums");
		logger.log(Level.INFO, "-----------");
		for (jakarta.xml.ws.soap.AddressingFeature.Responses c : args)
			logger.log(Level.INFO, "" + c);
	}

	/* Test setup */

	/*
	 * @class.setup_props:
	 */
	@BeforeEach
	public void setup() throws Exception {
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: valuesTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:226;
	 *
	 * @test_Strategy: Verify
	 * jakarta.xml.ws.soap.AddressingFeature.Responses.values() returns array
	 * containing the constants of this enum type.
	 */
	@Test
	public void valuesTest() throws Exception {
		TestUtil.logTrace("valuesTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Call jakarta.xml.ws.soap.AddressingFeature.Responses.values() ...");
			jakarta.xml.ws.soap.AddressingFeature.Responses[] responses = jakarta.xml.ws.soap.AddressingFeature.Responses
					.values();
			printEnums(responses);
			pass = findEnums(responses);
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("valuesTest failed", e);
		}

		if (!pass)
			throw new Exception("valuesTest failed");
	}

	/*
	 * @testName: valueOfTest
	 *
	 * @assertion_ids: JAXWS:JAVADOC:225;
	 *
	 * @test_Strategy: Verify
	 * jakarta.xml.ws.soap.AddressingFeature.Responses.valueOf(String name) returns
	 * the enum constant of this type with specified name.
	 */
	@Test
	public void valueOfTest() throws Exception {
		TestUtil.logTrace("valuesTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Call jakarta.xml.ws.soap.AddressingFeature.Responses.valueOf(ALL) ...");
			jakarta.xml.ws.soap.AddressingFeature.Responses responses = jakarta.xml.ws.soap.AddressingFeature.Responses
					.valueOf("ALL");
			if (responses != jakarta.xml.ws.soap.AddressingFeature.Responses.ALL) {
				TestUtil.logErr("jakarta.xml.ws.soap.AddressingFeature.Responses.valueOf(ALL) failed:" + " expected: "
						+ jakarta.xml.ws.soap.AddressingFeature.Responses.ALL + ", received: " + responses);
				pass = false;
			} else {
				logger.log(Level.INFO, "jakarta.xml.ws.soap.AddressingFeature.Responses.valueOf(ALL) passed");
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("valuesTest failed", e);
		}

		if (!pass)
			throw new Exception("valuesTest failed");
	}
}
