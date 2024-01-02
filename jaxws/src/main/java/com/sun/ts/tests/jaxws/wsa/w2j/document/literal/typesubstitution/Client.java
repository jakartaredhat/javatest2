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
 * $Id: Client.java 51063 2006-08-11 19:56:36Z lschwenk $
 */

package com.sun.ts.tests.jaxws.wsa.w2j.document.literal.typesubstitution;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaxws.common.JAXWS_Util;
import com.sun.ts.tests.jaxws.common.BaseClient;

public class Client extends BaseClient {

	private static final String PKG_NAME = "com.sun.ts.tests.jaxws.wsa.w2j.document.literal.typesubstitution.";

	// URL properties used by the test
	private static final String ENDPOINT_URL = "wsaw2jdltypesubstitution.endpoint.1";

	private static final String WSDLLOC_URL = "wsaw2jdltypesubstitution.wsdlloc.1";

	private String url = null;

	// service and port information
	private static final String NAMESPACEURI = "http://typesubstitution/wsdl";

	private static final String SERVICE_NAME = "CarDealerService";

	private static final String PORT_NAME = "CarDealerPort";

	private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

	private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

	private URL wsdlurl = null;

	CarDealer port = null;

	static CarDealerService service = null;

	private static final Logger logger = (Logger) System.getLogger(Client.class.getName());

	protected void getTestURLs() throws Exception {
		logger.log(Level.INFO, "Get URL's used by the test");
		String file = JAXWS_Util.getURLFromProp(ENDPOINT_URL);
		url = ctsurl.getURLString(PROTOCOL, hostname, portnum, file);
		file = JAXWS_Util.getURLFromProp(WSDLLOC_URL);
		wsdlurl = ctsurl.getURL(PROTOCOL, hostname, portnum, file);
		logger.log(Level.INFO, "Service Endpoint URL: " + url);
		logger.log(Level.INFO, "WSDL Location URL:    " + wsdlurl);
	}

	protected void getPortStandalone() throws Exception {
		port = (CarDealer) JAXWS_Util.getPort(wsdlurl, SERVICE_QNAME, CarDealerService.class, PORT_QNAME,
				CarDealer.class);
		logger.log(Level.INFO, "port=" + port);
		JAXWS_Util.setTargetEndpointAddress(port, url);
	}

	protected void getPortJavaEE() throws Exception {
		logger.log(Level.INFO, "Obtain service via WebServiceRef annotation");
		logger.log(Level.INFO, "service=" + service);
		port = (CarDealer) service.getCarDealerPort();
		logger.log(Level.INFO, "port=" + port);
		logger.log(Level.INFO, "Obtained port");
		JAXWS_Util.dumpTargetEndpointAddress(port);
	}

	protected void getService() {
		service = (CarDealerService) getSharedObject();
	}

	/* Test setup */

	/*
	 * @class.testArgs: -ap jaxws-url-props.dat
	 * 
	 * @class.setup_props: webServerHost; webServerPort; platform.mode;
	 */
	@BeforeEach
	public void setup() throws Exception {
		super.setup();
	}

	@AfterEach
	public void cleanup() throws Exception {
		logger.log(Level.INFO, "cleanup ok");
	}

	/*
	 * @testName: getCars
	 *
	 * @assertion_ids: JAXWS:SPEC:2076;
	 *
	 * @test_Strategy:
	 *
	 */
	@Test
	public void getCars() throws Exception {
		logger.log(Level.INFO, "getCars");
		boolean pass = true;

		try {
			List<Car> cars = port.getSedans();
			Iterator<Car> i = cars.iterator();
			int ncars = 0;
			while (i.hasNext()) {
				Car car = i.next();
				ncars++;
				String make = car.getMake();
				String model = car.getModel();
				String year = car.getYear();
				String color;
				logger.log(Level.INFO, "Make=" + make);
				logger.log(Level.INFO, "Model=" + model);
				logger.log(Level.INFO, "Year=" + year);
				if (car instanceof Toyota) {
					Toyota t = (Toyota) car;
					color = t.getColor();
					logger.log(Level.INFO, "Color=" + color);
					if (!make.equals("Toyota") && !model.equals("Camry") && !year.equals("1998")
							&& !color.equals("white")) {
						TestUtil.logErr("data mismatch expected Toyota Camry 1998 white");
						pass = false;
					} else
						logger.log(Level.INFO, "Toyota car matches");
				} else if (car instanceof Ford) {
					Ford t = (Ford) car;
					color = t.getColor();
					logger.log(Level.INFO, "Color=" + color);
					if (!make.equals("Ford") && !model.equals("Mustang") && !year.equals("1999")
							&& !color.equals("red")) {
						TestUtil.logErr("data mismatch expected Ford Mustang 1999 red");
						pass = false;
					} else
						logger.log(Level.INFO, "Ford car matches");
				} else {
					TestUtil.logErr("data mismatch - no car of this type expected");
					pass = false;
				}
			}
			logger.log(Level.INFO, "List returned " + ncars + " cars");
			if (ncars != 2) {
				TestUtil.logErr("expected only 2 cars");
				pass = false;
			}
		} catch (Exception e) {
			TestUtil.logErr("Caught exception: " + e.getMessage());
			TestUtil.printStackTrace(e);
			throw new Exception("getCars failed", e);
		}

		if (!pass)
			throw new Exception("getCars failed");
	}

}
