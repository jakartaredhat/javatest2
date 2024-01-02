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

package com.sun.ts.tests.jaxws.wsi.j2w.rpc.literal.R2001;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

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
	 * @testName: testWSDLImports
	 *
	 * @assertion_ids: WSI:SPEC:R2001
	 *
	 * @test_Strategy: Retrieve the WSDL, generated by the Java-to-WSDL tool, and
	 *                 examine all wsdl:import statements, ensuring that the
	 *                 resource at the specified location is a WSDL document.
	 *
	 * @throws Exception
	 */
	@Test
	public void testWSDLImports() throws Exception {
		Document document = client.getDocument();
		Element[] imports = DescriptionUtils.getImports(document);
		for (int i = 0; i < imports.length; i++) {
			verifyImport(imports[i]);
		}
	}

	protected void verifyImport(Element element) throws Exception {
		String location = element.getAttribute(WSDL_LOCATION_ATTR);
		Document document = DescriptionUtils.getDocumentFromLocation(location);
		if (!DescriptionUtils.isDescription(document)) {
			throw new Exception("Document imported from '" + location + "' is not a description (BP-R2001)");
		}
	}
}
