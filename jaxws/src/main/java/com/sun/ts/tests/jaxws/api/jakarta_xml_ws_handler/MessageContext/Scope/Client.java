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

package com.sun.ts.tests.jaxws.api.jakarta_xml_ws_handler.MessageContext.Scope;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;

import jakarta.xml.ws.handler.MessageContext;

import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	// Expected Enum Constant Summary
	private final static MessageContext.Scope expectedEnums[] = { MessageContext.Scope.APPLICATION,
			MessageContext.Scope.HANDLER, };

	private boolean findEnums(MessageContext.Scope[] args) {
		boolean pass = true;
		boolean found;
		for (MessageContext.Scope a : args) {
			found = false;
			logger.log(Level.INFO, "Searching expected list of enums for " + a);
			for (MessageContext.Scope b : expectedEnums) {
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

	private void printEnums(MessageContext.Scope[] args) {
		logger.log(Level.INFO, "Print Enums");
		logger.log(Level.INFO, "-----------");
		for (MessageContext.Scope c : args)
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
	 * @assertion_ids: JAXWS:JAVADOC:95; WS4EE:SPEC:6012;
	 *
	 * @test_Strategy: Verify MessageContext.Scope.values() returns array containing
	 * the constants of this enum type.
	 */
	@Test
	public void valuesTest() throws Exception {
		TestUtil.logTrace("valuesTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Call MessageContext.Scope.values() ...");
			MessageContext.Scope[] methods = MessageContext.Scope.values();
			printEnums(methods);
			pass = findEnums(methods);
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
	 * @assertion_ids: JAXWS:JAVADOC:94; WS4EE:SPEC:6012;
	 *
	 * @test_Strategy: Verify MessageContext.Scope.valueOf(String name) returns the
	 * enum constant of this type with specified name.
	 */
	@Test
	public void valueOfTest() throws Exception {
		TestUtil.logTrace("valuesTest");
		boolean pass = true;
		try {
			logger.log(Level.INFO, "Call MessageContext.Scope.valueOf(APPLICATION) ...");
			MessageContext.Scope method = MessageContext.Scope.valueOf("APPLICATION");
			if (method != MessageContext.Scope.APPLICATION) {
				TestUtil.logErr("MessageContext.Scope.valueOf(APPLICATION) failed:" + " expected: "
						+ MessageContext.Scope.APPLICATION + ", received: " + method);
				pass = false;
			} else {
				logger.log(Level.INFO, "MessageContext.Scope.valueOf(APPLICATION) passed");
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
