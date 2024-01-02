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
 * $URL$ $LastChangedDate$
 */

package com.sun.ts.tests.jaxws.wsi.w2j.document.literal.R2712;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.MessageFormat;
import java.util.Iterator;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.wsi.requests.SOAPRequests;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPMessage;

public class Client extends BaseClient implements SOAPRequests {

	private W2JDLR2712ClientOne client1;

	private W2JDLR2712ClientTwo client2;

	private static String GLOBAL_ELEMENT = "HelloResponseElement";

	static W2JDLR2712TestService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		return createWebArchive(Client.class);
	}

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
		client1 = (W2JDLR2712ClientOne) ClientFactory.getClient(W2JDLR2712ClientOne.class, service);
		client2 = (W2JDLR2712ClientTwo) ClientFactory.getClient(W2JDLR2712ClientTwo.class, service);
		logger.log(Level.INFO, "setup ok");
	}

	@AfterEach
	public void cleanup() {
		logger.log(Level.INFO, "cleanup");
	}

	/**
	 * @testName: ChildElementInstanceOfGlobalElementInRequest
	 *
	 * @assertion_ids: WSI:SPEC:R2712
	 *
	 * @test_Strategy: Send a request and verify the child elements using a servlet
	 *                 filter.
	 *
	 * @throws Exception
	 */
	@Test
	public void ChildElementInstanceOfGlobalElementInRequest() throws Exception {
		HelloResponse result;
		HelloRequest hr = new HelloRequest();
		hr.setString("ChildElementInstanceOfGlobalElementInRequest");
		try {
			result = client2.hello(hr);
		} catch (Exception e) {
			throw new Exception("Failure with (BP-R2712)", e);
		}
	}

	/**
	 * @testName: ChildElementInstanceOfGlobalElementInResponse
	 *
	 * @assertion_ids: WSI:SPEC:R2712
	 *
	 * @test_Strategy: Send a request to the endpoint which inturn sends a response
	 *                 back. Verify the child elements in that response.
	 *
	 * @throws Exception
	 */
	@Test
	public void ChildElementInstanceOfGlobalElementInResponse() throws Exception {
		SOAPMessage response;
		try {
			String request = MessageFormat.format(R2712_REQUEST, "ChildElementInstanceOfGlobalElementInResponse");
			String expected = "PASSED";
			response = client1.makeSaajRequest(request);
			String result = verifyChildElement(response);
			if (!result.equals(expected)) {
				TestUtil.logErr("ERROR: incorrect result");
				TestUtil.logErr("expected=" + expected);
				TestUtil.logErr("actual=" + result);
				throw new Exception("Failure with (BP-R2712)");
			}
		} catch (Exception e) {
			throw new Exception("Failure with (BP-R2712)", e);
		}

	}

	/**
	 * Verifies that the correct child element of the soap:body is returned
	 *
	 * @param request the SOAPMessage response.
	 *
	 * @return "PASSED" if valid; an error message otherwise.
	 *
	 * @throws Exception
	 */
	protected String verifyChildElement(SOAPMessage sm) throws Exception {
		String result = "FAILED";
		System.out.println("Getting children of body element ...");
		Iterator children = sm.getSOAPBody().getChildElements();
		SOAPElement child;
		String localName;
		int count = 0;
		while (children.hasNext()) {
			count++;
			System.out.println("Getting operation name ...");
			child = (SOAPElement) children.next();
			localName = child.getElementName().getLocalName();
			System.out.println("child localname: " + localName);
			if (localName.equals(GLOBAL_ELEMENT)) {
				if (count == 1) {
					result = "PASSED";
				} else {
					result = "Error: The element '" + GLOBAL_ELEMENT + "' was found " + count
							+ " time(s) in the soap:body";
				}
			} else {
				result = "Error: Expected element '" + GLOBAL_ELEMENT + "' in soap:body, instead got '" + localName
						+ "'";
			}

		}
		if (count == 0) {
			result = "Error: no child elements were found in soap:body";
		}
		System.out.println("result=" + result);
		JAXWS_Util.printSOAPMessage(sm, System.out);
		return result;
	}
}
