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
 * $Id: ServerSOAPHandler.java 52501 2007-01-24 02:29:49Z lschwenk $
 */

package com.sun.ts.tests.jaxws.wsa.w2j.document.literal.requiredfalse;

import com.sun.ts.tests.jaxws.wsa.common.ActionNotSupportedException;
import com.sun.ts.tests.jaxws.wsa.common.WsaBaseSOAPHandler;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;


import jakarta.xml.ws.handler.soap.SOAPMessageContext;

public class ServerSOAPHandler extends WsaBaseSOAPHandler {

			private static final Logger logger = (Logger) System.getLogger(ServerSOAPHandler.class.getName());

	protected void checkInboundAction(SOAPMessageContext context, String oper, String action) {
		logger.log(Level.INFO,
				"ServerSOAPHandler.checkInboundAction: [operation=" + oper + ", input action=" + action + "]");
		System.out
				.println("ServerSOAPHandler.checkInboundAction: [operation=" + oper + ", input action=" + action + "]");
		if (oper.equals("addNumbers")) {
			if (!action.equals(TestConstants.ADD_NUMBERS_IN_ACTION)) {
				throw new ActionNotSupportedException(action);
			}
		} else if (oper.equals("addNumbers2")) {
			if (!action.equals(TestConstants.ADD_NUMBERS2_IN_ACTION)) {
				throw new ActionNotSupportedException(action);
			}
		} else if (oper.equals("addNumbers3")) {
			if (!action.equals(TestConstants.ADD_NUMBERS3_IN_ACTION)) {
				throw new ActionNotSupportedException(action);
			}
		} else if (oper.equals("addNumbers4")) {
			if (!action.equals(TestConstants.ADD_NUMBERS4_IN_ACTION)) {
				throw new ActionNotSupportedException(action);
			}
		} else if (oper.equals("addNumbers5")) {
			if (!action.equals(TestConstants.ADD_NUMBERS5_IN_ACTION)) {
				throw new ActionNotSupportedException(action);
			}
		} else if (oper.equals("addNumbers6")) {
			if (!action.equals(TestConstants.ADD_NUMBERS6_IN_ACTION)) {
				throw new ActionNotSupportedException(action);
			}
		} else if (oper.equals("addNumbers7")) {
			if (!action.equals(TestConstants.ADD_NUMBERS7_IN_ACTION)) {
				throw new ActionNotSupportedException(action);
			}
		} else if (oper.equals("addNumbers8")) {
			if (!action.equals(TestConstants.ADD_NUMBERS8_IN_ACTION)) {
				throw new ActionNotSupportedException(action);
			}
		}
	}

	protected String whichHandler() {
		return "ServerSOAPHandler";
	}
}
