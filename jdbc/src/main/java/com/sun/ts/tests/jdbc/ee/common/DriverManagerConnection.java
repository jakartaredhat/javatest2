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

package com.sun.ts.tests.jdbc.ee.common;

import java.lang.System.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.util.TestUtil;

public class DriverManagerConnection implements JDBCTestConnectionManager {

	private static final Logger logger = (Logger) System.getLogger(DriverManagerConnection.class.getName());

	public Connection getConnection() throws ClassNotFoundException, SQLException, Exception {
		Connection con = null;
		String dbName, dbUser, dbPassword, dbDriver;
		dbName = dbUser = dbPassword = dbDriver = null;

		dbName = System.getProperty("db1", "");
		dbUser = System.getProperty("user1", "");
		dbPassword = System.getProperty("password1", "");
		dbDriver = System.getProperty("Driver", "");

		logger.log(Logger.Level.TRACE, "Database1 : " + dbName);
		logger.log(Logger.Level.TRACE, "Username  : " + dbUser);
		logger.log(Logger.Level.TRACE, "Password  : " + dbPassword);
		logger.log(Logger.Level.TRACE, "Driver    : " + dbDriver);

		logger.log(Logger.Level.TRACE, "About to load the driver class");
		Class.forName(dbDriver);
		logger.log(Logger.Level.INFO, "Successfully loaded the driver class");

		logger.log(Logger.Level.TRACE, "About to make the DB connection");
		con = DriverManager.getConnection(dbName, dbUser, dbPassword);
		logger.log(Logger.Level.INFO, "Made the JDBC connection to the DB");

		return con;
	}

}
