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

package com.sun.ts.tests.jaxws.sharedclients;

import java.io.Serializable;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;

public abstract class Client implements Serializable {

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	private static final String DEFAULT_SCHEME = "http";

	private String webServerHost;

	private int webServerPort;

	private int mode;

	private TSURL ctsURL;

	public Client(String webServerHost, int webServerPort, int mode) throws Exception {
		this.webServerHost = webServerHost;
		this.webServerPort = webServerPort;
		this.mode = mode;
		ctsURL = new TSURL();
	}

	protected String getWebServerHost() {
		return webServerHost;
	}

	protected int getWebServerPort() {
		return webServerPort;
	}

	protected String getScheme() {
		return DEFAULT_SCHEME;
	}

	protected TSURL getCTSURL() {
		return ctsURL;
	}

	/**
	 * Returns the endpoint URL string.
	 *
	 * @return the endpoint URL string.
	 *
	 * @throws java.lang.Exception
	 */
	protected String getEndpointURLString() throws Exception {
		String file = null;
		try {
			file = JAXWS_Util.getURLFromProp(getEndpointURLProperty());
		} catch (Exception e) {
			throw new Exception("Unable to obtain endpoint URL string", e);
		}
		String url = null;
		if (file != null) {
			url = ctsURL.getURLString(getScheme(), getWebServerHost(), getWebServerPort(), file);
			logger.log(Level.INFO, "endpoint url=" + url);
		}
		return url;
	}

	protected String getWSDLURLString() throws Exception {
		String file = null;
		try {
			file = JAXWS_Util.getURLFromProp(getWSDLURLProperty());
		} catch (Exception e) {
			throw new Exception("Unable to obtain wsdl URL string", e);
		}
		String url = null;
		if (file != null) {
			url = ctsURL.getURLString(getScheme(), getWebServerHost(), getWebServerPort(), file);
			logger.log(Level.INFO, "wsdl url=" + url);
		}
		return url;
	}

	protected abstract String getEndpointURLProperty();

	protected abstract String getWSDLURLProperty();

}
