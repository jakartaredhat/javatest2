/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.ts.tests.jaxws.wsi.j2w.rpc.literal.R2304;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.sharedclients.SOAPClient;
import com.sun.ts.tests.jaxws.sharedclients.rpclitclient.J2WRLShared;
import com.sun.ts.tests.jaxws.sharedclients.rpclitclient.J2WRLSharedClient;
import com.sun.ts.tests.jaxws.wsi.constants.DescriptionConstants;
import com.sun.ts.tests.jaxws.wsi.utils.DescriptionUtils;

public class Client extends BaseClient implements DescriptionConstants {
	/**
	 * The client.
	 */
	private SOAPClient client;

	static J2WRLShared service = null;

	/**
	 * The names.
	 */
	private ArrayList names;

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
	@BeforeEach
	public void setup() throws Exception {
		super.setup();
		client = ClientFactory.getClient(J2WRLSharedClient.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {

		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: testOperations
	 *
	 * @assertion_ids: WSI:SPEC:R2304
	 *
	 * @test_Strategy: Retrieve the WSDL, generated by the Java-to-WSDL tool, and
	 *                 examine the operation(s) making sure they are uniquely named.
	 *
	 * @throws Exception
	 */
	@Test
	public void testOperations() throws Exception {
		names = new ArrayList();
		Document document = client.getDocument();
		Element[] portTypes = DescriptionUtils.getPortTypes(document);
		for (int i = 0; i < portTypes.length; i++) {
			verifyPortType(portTypes[i]);
		}
	}

	protected void verifyPortType(Element element) throws Exception {
		Element[] children = DescriptionUtils.getChildElements(element, WSDL_NAMESPACE_URI, WSDL_OPERATION_LOCAL_NAME);
		for (int i = 0; i < children.length; i++) {
			verifyOperation(children[i]);
		}
	}

	protected void verifyOperation(Element element) throws Exception {
		String name = element.getAttribute(WSDL_NAME_ATTR);
		if (names.contains(name)) {
			throw new Exception("Duplicate operation '" + name + "' encountered (BP-R2304)");
		}
		names.add(name);
	}
}
