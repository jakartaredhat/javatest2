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

package com.sun.ts.tests.jaxws.wsa.j2w.document.literal.refps;

import java.util.List;

import org.w3c.dom.Element;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;


import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.HTTPSOAPHandler;
import com.sun.ts.tests.jaxws.common.Handler_Util;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.wsa.common.EprUtil;

import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;

public class ServerSOAPHandler extends HTTPSOAPHandler {

			private static final Logger logger = (Logger) System.getLogger(ServerSOAPHandler.class.getName());


	protected void processInboundMessage(SOAPMessageContext context) {
		logger.log(Level.INFO, "In ServerSOAPHandler:processInboundMessage");
		System.out.println("In ServerSOAPHandler:processInboundMessage");
		SOAPMessage msg = ((SOAPMessageContext) context).getMessage();
		JAXWS_Util.dumpSOAPMessage(msg, true);
		JAXWS_Util.dumpSOAPMessage(msg, false);
		if (Handler_Util.checkForMsg(context, "RequestReferenceParametersTest")) {
			List<Element> rp = (List<Element>) context.get(MessageContext.REFERENCE_PARAMETERS);
			boolean result1 = false;
			boolean result2 = false;
			result1 = verifyReferenceParameters(rp, "MyParam1", "Hello");
			result2 = verifyReferenceParameters(rp, "MyParam2", "There");
			if ((!result1) && (!result2)) {
				throw new RuntimeException(
						"In ServerSOAPHandler:processInboundMessage: Error: Reference Parameter MyParam1 and MyParam2 were either not found or their values were wrong");
			} else if (!result1) {
				throw new RuntimeException(
						"In ServerSOAPHandler:processInboundMessage: Error: Reference Parameter MyParam1 was not found or it's value was wrong");
			} else if (!result2) {
				throw new RuntimeException(
						"In ServerSOAPHandler:processInboundMessage: Error: Reference Parameter MyParam2 was not found or it's value was wrong");
			}
		}
	}

	protected boolean verifyReferenceParameters(List<Element> e, String name, String value) {
		boolean result = false;
		logger.log(Level.INFO, "in ServerSOAPHandler:verifyReferenceParameters");
		logger.log(Level.INFO, "Number of elements=" + e.size());
		logger.log(Level.INFO, "Searching for Reference Parameter '" + name + "' and its value '" + value + "'");
		System.out.println("in ServerSOAPHandler:verifyReferenceParameters");
		System.out.println("Number of elements=" + e.size());
		System.out.println("Searching for Reference Parameter '" + name + "' and its value '" + value + "'");
		if (e.size() > 0) {
			for (int i = 0; i < e.size(); i++) {
				Element element = (Element) e.get(i);
				boolean actual = EprUtil.validateReferenceParameter(element, name, value);
				if (actual) {
					result = true;
					break;
				}
			}
			if (!result) {
				TestUtil.logErr("Reference Parameter '" + name + "' with value '" + value + "' was not found");
				System.err.println("Reference Parameter '" + name + "' with value '" + value + "' was not found");
			}
		}
		return result;
	}
}
