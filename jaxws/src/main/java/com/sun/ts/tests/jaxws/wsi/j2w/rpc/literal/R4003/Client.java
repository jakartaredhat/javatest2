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
 * @(#)Client.java	1.3	03/05/09
 */

package com.sun.ts.tests.jaxws.wsi.j2w.rpc.literal.R4003;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.tests.jaxws.common.BaseClient;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.sharedclients.SOAPClient;
import com.sun.ts.tests.jaxws.sharedclients.rpclitclient.J2WRLShared;
import com.sun.ts.tests.jaxws.sharedclients.rpclitclient.J2WRLSharedClient;

public class Client extends BaseClient {
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
	 * @testName: testDescriptionEncoding
	 *
	 * @assertion_ids: WSI:SPEC:R4003
	 *
	 * @test_Strategy: Retrieve the WSDL, generated by the Java-to-WSDL tool, and
	 *                 examine its encoding.
	 *
	 * @throws Exception
	 */
	@Test
	public void testDescriptionEncoding() throws Exception {
		String descriptionURL = client.getDescriptionURL();
		try {
			URL url = new URL(descriptionURL);
			verifyEncoding(url);
		} catch (MalformedURLException e) {
			throw new Exception("The description URL '" + descriptionURL + "' is invalid (BP-R4003)", e);
		}
	}

	protected void verifyEncoding(URL url) throws Exception {
		byte[] bytes = getDescriptionBytes(url);
		int utf;
		String description;
		try {
			description = new String(bytes, "UTF-8");
			utf = 8;
		} catch (Throwable t) {
			try {
				description = new String(bytes, "UTF-16");
				utf = 16;
			} catch (Throwable t2) {
				throw new Exception("Description at '" + url
						+ "' cannot be created using neither 'UTF-8' nor 'UTF-16' encoding (BP-R4003)", t2);
			}
		}
		if (description.startsWith("<?xml")) {
			int index;
			index = description.indexOf("?>");
			if (index == -1) {
				throw new Exception("Description at '" + url + "' has open '<?xml ...' declaration (BP-R4003)");
			}
			String declaration = (description.substring(5, index)).toLowerCase();
			index = declaration.indexOf("encoding");
			if (index != -1) {
				String encoding = (declaration.substring(index + 8)).toLowerCase();
				if (encoding.indexOf("utf-8") != -1) {
					return;
				}
				if (encoding.indexOf("utf-16") != -1) {
					return;
				}
				throw new Exception(
						"Description at '" + url + "' is not 'UTF-8','utf-8','utf-16' or 'UTF-16' (BP-R4003)");
			}
		}
	}

	protected byte[] getDescriptionBytes(URL url) throws Exception {
		try {
			InputStream is = url.openStream();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			do {
				length = is.read(buffer);
				if (length > 0) {
					os.write(buffer, 0, length);
				}
			} while (length > 0);
			is.close();
			os.flush();
			return os.toByteArray();
		} catch (IOException e) {
			throw new Exception("Unable to read description from '" + url + "' (BP-R4003)", e);
		}
	}
}
