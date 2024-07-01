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
 * @(#)rsSchema.java	1.23 03/05/16
 */
package com.sun.ts.tests.jdbc.ee.common;

import java.lang.System.Logger;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.StringTokenizer;

import com.sun.ts.lib.util.TestUtil;

/**
 * The rsSchema class creates the database and tables using Sun's J2EE Reference
 * Implementation.
 * 
 * @author
 * @version 1.7, 06/16/99
 */

public class rsSchema {

	private static final Logger logger = (Logger) System.getLogger(rsSchema.class.getName());

	private Properties props = null;

	private Statement stmt = null;

	public void createTab(String sTableName, Properties sqlProps, Connection conn) throws RemoteException {
		String execString = null;
		String sKeyName = null;
		String binarySize = null;
		String varbinarySize = null;
		String createString = null, createString1 = null, createString2 = null;

		logger.log(Logger.Level.TRACE, "createTab");
		// drop table if it exists
		try {
			props = sqlProps;
			dropTab(sTableName, conn);
			logger.log(Logger.Level.TRACE, "deleted rows from table " + sTableName);
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,
					"Exception encountered deleting rows from  table: " + sTableName + ": " + e.getMessage(), e);
		}

		try {

			stmt = conn.createStatement();
			if ((sTableName.startsWith("Binary_Tab"))) {
				binarySize = props.getProperty("binarySize");
				logger.log(Logger.Level.TRACE, "Binary Table Size : " + binarySize);

				String insertString = props.getProperty("Binary_Tab_Insert");
				logger.log(Logger.Level.TRACE, "Insert String " + insertString);

				logger.log(Logger.Level.TRACE, "Adding rows to the table");
				stmt.executeUpdate(insertString);
				logger.log(Logger.Level.TRACE, "Successfully inserted the row");

			} else if ((sTableName.startsWith("Varbinary_Tab"))) {
				varbinarySize = props.getProperty("varbinarySize");
				logger.log(Logger.Level.TRACE, "Varbinary Table Size : " + varbinarySize);
				String insertString = props.getProperty("Varbinary_Tab_Insert");
				logger.log(Logger.Level.TRACE, "Insert String " + insertString);

				logger.log(Logger.Level.TRACE, "Adding rows to the table");
				stmt.executeUpdate(insertString);
				logger.log(Logger.Level.TRACE, "Successfully inserted the row");

			} else {

				// Add the prescribed table rows

				logger.log(Logger.Level.TRACE, "Adding rows to the table" + sTableName);

				sKeyName = sTableName.concat("_Insert");

				logger.log(Logger.Level.TRACE, "sKeyName :" + sKeyName);
				execString = sqlProps.getProperty(sKeyName);
				stmt.executeUpdate(execString);
				logger.log(Logger.Level.TRACE, "Rows added to the table " + sTableName);
				// stmt.close();
			}

		} catch (SQLException e) {
			logger.log(Logger.Level.ERROR, "SQLException creating the Table" + sTableName + ": " + e.getMessage(), e);
			dropTab(sTableName, conn);
			throw new RemoteException(e.getMessage());
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Setup Failed!", e);
			System.exit(1);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqle) {
					logger.log(Logger.Level.ERROR, "Error closing Statement!", sqle);
					throw new RemoteException(sqle.getMessage());
				}
			}
		}

	}

	public void dropTab(String sTableName, Connection conn) throws RemoteException {
		logger.log(Logger.Level.TRACE, "dropTab");
		// Delete the Table
		String sTag = sTableName.concat("_Delete");
		String removeString = props.getProperty(sTag);
		logger.log(Logger.Level.TRACE, "Executable String " + removeString);
		try {
			// Since scrollable resultSet is optional, the parameters
			// are commented out.
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(removeString);
			stmt.close();
		} catch (SQLException e) {
			TestUtil.printStackTrace(e);

			throw new RemoteException(e.getMessage());
		}
	}

	public void dbUnConnect(Connection conn) throws RemoteException {
		logger.log(Logger.Level.TRACE, "dbUnConnect");
		// Close the DB connections
		try {
			conn.close();
			logger.log(Logger.Level.INFO, "Closed the Data Base connection");
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Exception occured while trying to close the DB connection", e);
			throw new RemoteException(e.getMessage());
		}
	}

	/*
	 * This method returns a boolean value based on the string that is pulled out of
	 * the table. A "1" or a case insensitive "true" string results in a returned
	 * value of (boolean) true.
	 *
	 */
	public boolean extractValAsBoolVal(String sTableName, int count, Properties sqlProps, Connection conn)
			throws Exception {
		boolean rval = false; // assume false to be returned

		String str = this.extractVal(sTableName, count, sqlProps, conn);
		str = str.trim();

		logger.log(Logger.Level.TRACE,
				"Extracted value:  " + str + "  from table:  " + sTableName + "   using column:  " + count);

		if (str.equals("1") || str.equalsIgnoreCase("true")) {
			rval = true;
		}

		logger.log(Logger.Level.TRACE, "Returning boolean value of:  " + rval);

		return rval;
	}

	/*
	 * This method returns a Boolean object based on the string that is pulled out
	 * of the table. A "1" or a case insensitive "true" string results in a returned
	 * Boolean object that equates to true.
	 *
	 */
	public Boolean extractValAsBoolObj(String sTableName, int count, Properties sqlProps, Connection conn)
			throws Exception {
		Boolean bObj;

		String str = this.extractVal(sTableName, count, sqlProps, conn);
		str = str.trim();

		logger.log(Logger.Level.TRACE,
				"Extracted value:  " + str + "  from table:  " + sTableName + "   using column:  " + count);

		if (str.equals("1") || str.equalsIgnoreCase("true")) {
			bObj = Boolean.valueOf(true);
		} else {
			bObj = Boolean.valueOf(false);
		}

		logger.log(Logger.Level.TRACE, "Returning Boolean object sith val:  " + bObj.booleanValue());

		return bObj;
	}

	/*
	 * This method returns a String based on the string that is pulled out of the
	 * table. A case insensitive "true" string results in a returned String object
	 * with a value of "1". A case insensitive "false" string results in a returned
	 * Integer object with a value of 0. All other strings are returned as obtained
	 * from the table. The main purpose of this method is to interpret boolean
	 * values (eg "true" or "false") as a numeric value (ie "1" and "0",
	 * respectively).
	 *
	 */
	public String extractValAsNumericString(String sTableName, int count, Properties sqlProps, Connection conn)
			throws Exception {
		String str = this.extractVal(sTableName, count, sqlProps, conn);
		str = str.trim();

		logger.log(Logger.Level.TRACE,
				"Extracted value:  " + str + "  from table:  " + sTableName + "   using column:  " + count);

		if (str.equalsIgnoreCase("true")) {
			str = "1";
		} else if (str.equalsIgnoreCase("false")) {
			str = "0";
		}

		logger.log(Logger.Level.TRACE, "Returning NumericString of:  " + str);

		return str;
	}

	public String extractVal(String sTableName, int count, Properties sqlProps, Connection conn) throws Exception {
		String sKeyName = null, insertString = null;
		String retStr = null, parameters = null;
		StringTokenizer sToken = null;
		try {
			sKeyName = sTableName.concat("_Insert");

			insertString = sqlProps.getProperty(sKeyName, "");

			parameters = insertString.substring(insertString.indexOf("(", 1) + 1, insertString.indexOf(")", 1));

			sToken = new StringTokenizer(parameters, ",");
			int i = 1;
			do {
				retStr = (String) sToken.nextElement();
			} while (count != i++);

			return retStr;
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Exception " + e.getMessage(), e);
			throw new Exception("Call to extractVal is Failed!", e);
		}
	}
}
