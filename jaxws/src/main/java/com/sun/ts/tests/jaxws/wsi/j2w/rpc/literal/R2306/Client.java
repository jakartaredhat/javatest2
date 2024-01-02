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

package com.sun.ts.tests.jaxws.wsi.j2w.rpc.literal.R2306;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Attr;
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
	 * The document.
	 */
	private Document document;

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
	 * @testName: testMessageParts
	 *
	 * @assertion_ids: WSI:SPEC:R2306
	 *
	 * @test_Strategy: Retrieve the WSDL, generated by the Java-to-WSDL tool, and
	 *                 examine the port type(s) making sure they have correct, valid
	 *                 messages.
	 *
	 * @throws Exception
	 */
	@Test
	public void testMessageParts() throws Exception {
		document = client.getDocument();
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
		Element input = getInput(element);
		if (input != null) {
			Element message = getMessage(input);
			verifyMessage(message);
		}
		Element output = getOutput(element);
		if (output != null) {
			Element message = getMessage(output);
			verifyMessage(message);
		}
	}

	protected void verifyMessage(Element element) throws Exception {
		Element[] children = DescriptionUtils.getChildElements(element, WSDL_NAMESPACE_URI, WSDL_PART_LOCAL_NAME);
		for (int i = 0; i < children.length; i++) {
			Attr attr;
			attr = children[i].getAttributeNode(WSDL_TYPE_ATTR);
			if (attr == null) {
				return;
			}
			attr = children[i].getAttributeNode(WSDL_ELEMENT_ATTR);
			if (attr != null) {
				String messageName = element.getAttribute(WSDL_NAME_ATTR);
				String partName = children[i].getAttribute(WSDL_NAME_ATTR);
				throw new Exception("Part '" + partName + "' in message '" + messageName
						+ "' references both a type and an element (BP-R2306)");
			}

		}
	}

	protected Element getOutput(Element element) throws Exception {
		Element[] children = DescriptionUtils.getChildElements(element, WSDL_NAMESPACE_URI, WSDL_OUTPUT_LOCAL_NAME);
		if (children.length > 0) {
			return children[0];
		}
		return null;
	}

	protected Element getInput(Element element) throws Exception {
		Element[] children = DescriptionUtils.getChildElements(element, WSDL_NAMESPACE_URI, WSDL_INPUT_LOCAL_NAME);
		if (children.length > 0) {
			return children[0];
		}
		return null;
	}

	protected Element getMessage(Element element) throws Exception {
		String message = element.getAttribute(DescriptionUtils.WSDL_MESSAGE_ATTR);
		int index = message.indexOf(':');
		if (index == -1) {
			throw new Exception(
					"In- or output element refers to unqualified message name '" + message + "' (BP-R2306)");
		}
		String localName = message.substring(index + 1);
		Element[] children = DescriptionUtils.getChildElements(document, WSDL_NAMESPACE_URI, WSDL_MESSAGE_LOCAL_NAME);
		for (int i = 0; i < children.length; i++) {
			String name = children[i].getAttribute(WSDL_NAME_ATTR);
			if (name.equals(localName)) {
				return children[i];
			}
		}
		throw new Exception("Message '" + localName + "' not found (BP-R2306)");
	}
}
