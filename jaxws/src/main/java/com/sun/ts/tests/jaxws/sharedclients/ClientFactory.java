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

package com.sun.ts.tests.jaxws.sharedclients;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.util.TestUtil;

import jakarta.xml.ws.Service;

public class ClientFactory {

	private static final Logger logger = (Logger) System.getLogger(ClientFactory.class.getName());

	public static SOAPClient getClient(Class clazz) throws Exception {
		return getClient(clazz, null);
	}

	public static SOAPClient getClient(Class clazz, Service theService) throws Exception {
		int mode;
		String property = System.getProperty("platform.mode");
		if (property == null) {
			throw new Exception("The 'platform.mode' property value is not defined");
		}
		if (property.equalsIgnoreCase("standalone")) {
			mode = SOAPClient.MODE_STANDALONE;
		} else if (property.equalsIgnoreCase("jakartaEE")) {
			mode = SOAPClient.MODE_JavaEE;

		} else {
			throw new Exception("The 'platform.mode' property value '" + property + "' is invalid");
		}
		String webServerHost = System.getProperty("webServerHost");
		int webServerPort;
		if (SecureClient.class.isAssignableFrom(clazz)) {
			webServerPort = Integer.parseInt(System.getProperty("secureWebServerPort"));
		} else {
			webServerPort = Integer.parseInt(System.getProperty("webServerPort"));
		}
		try {
			if (mode == SOAPClient.MODE_STANDALONE) {
				Constructor ctr = clazz.getConstructor(new Class[] { String.class, int.class, int.class });
				return (SOAPClient) ctr.newInstance(
						new Object[] { webServerHost, Integer.valueOf(webServerPort), Integer.valueOf(mode) });
			} else {
				Constructor ctr = clazz.getConstructor(
						new Class[] { String.class, int.class, int.class, jakarta.xml.ws.Service.class });
				return (SOAPClient) ctr.newInstance(new Object[] { webServerHost, Integer.valueOf(webServerPort),
						Integer.valueOf(mode), getWebServiceRef(theService) });
			}
		} catch (NoSuchMethodException e) {
			throw new Exception("Client '" + clazz.getName() + "' does not have required constructor", e);
		} catch (ClassCastException e) {
			throw new Exception("Client '" + clazz.getName() + "' does not extend '" + SOAPClient.class.getName() + "'",
					e);
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			throw new Exception("Unable to instantiate '" + clazz.getName() + "'", e);
		}
	}

	private static jakarta.xml.ws.Service getWebServiceRef(Service theService) {
		Service service = theService;
		logger.log(Level.INFO, "WebServiceRef is not set in Client (get it from specific vehicle)");
		//service = (Service) theTest.getSharedObject();
		logger.log(Level.INFO, "service=" + service);
		return null;
	}

	private ClientFactory() {
		super();
	}
}
