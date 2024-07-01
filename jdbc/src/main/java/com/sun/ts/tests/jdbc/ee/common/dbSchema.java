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
 * @(#)dbSchema.java	1.22 03/05/16
 */
package com.sun.ts.tests.jdbc.ee.common;

import java.lang.System.Logger;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.sun.ts.lib.util.TestUtil;

/**
 * The dbSchema class creates the database and tables using Sun's J2EE Reference
 * Implementation.
 * 
 */

public class dbSchema {

	private static final Logger logger = (Logger) System.getLogger(dbSchema.class.getName());

	private String pTableName = null;

	private String fTableName = null;

	// used only in dropTable() as properties are not passed as a parameter.
	private Properties props = null;

	public void createData(Connection conn) throws RemoteException {
		logger.log(Logger.Level.TRACE, "createData");

		// Get the name of the tables
		pTableName = System.getProperty("ptable", "");
		fTableName = System.getProperty("ftable", "");
		try {
			// Create the database tables
			createTable(conn);
			logger.log(Logger.Level.TRACE, "Initialized the tables " + pTableName + " and " + fTableName);
		} catch (Exception e) {
			TestUtil.printStackTrace(e);

			dbUnConnect(conn);
			throw new RemoteException(e.getMessage());
		}
	}

	public void destroyData(Connection conn) throws RemoteException {
		logger.log(Logger.Level.TRACE, "destroyData");
		try {
			dropTables(conn);
			logger.log(Logger.Level.TRACE, "Deleted all rows from tables " + pTableName + " and " + fTableName);
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,
					"Exception while attempting to Delete all rows from tables " + pTableName + " and " + fTableName,
					e);
			throw new RemoteException(e.getMessage());
		}
	}

	public void dbUnConnect(Connection conn) throws RemoteException {
		logger.log(Logger.Level.TRACE, "dbUnConnect");
		// Close the DB connections
		try {
			conn.close();
			logger.log(Logger.Level.INFO, "Closed the database connection");
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Exception occured while trying to close the DB connection", e);
			throw new RemoteException(e.getMessage());
		}
	}

	private void createTable(Connection conn) throws RemoteException {
		logger.log(Logger.Level.TRACE, "createTable");

		// Remove all rows from the table
		try {
			dropTables(conn);
			logger.log(Logger.Level.TRACE,
					"Deleted all rows from Tables " + pTableName + " and " + fTableName + " dropped");
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR,
					"SQLException encountered while deleting rows for Tables " + pTableName + " and " + fTableName, e);
		}

		// Get the size of the table as int
		String strTabSize = System.getProperty("cofSize");
		logger.log(Logger.Level.TRACE, "strTabSize: " + strTabSize);

		Integer intTabSize = new Integer(strTabSize);
		logger.log(Logger.Level.TRACE, "intTabSize: " + intTabSize.toString());
		logger.log(Logger.Level.TRACE, "intTabSize: " + intTabSize.toString());

		int tSize = intTabSize.intValue();
		logger.log(Logger.Level.TRACE, "tSize: " + tSize);

		String strTabTypeSize = System.getProperty("cofTypeSize");
		logger.log(Logger.Level.TRACE, "strTabTypeSize: " + strTabTypeSize);

		Integer intTabTypeSize = new Integer(strTabTypeSize);
		logger.log(Logger.Level.TRACE, "intTabTypeSize: " + intTabTypeSize.toString());
		logger.log(Logger.Level.TRACE, "intTabTypeSize: " + intTabTypeSize.toString());

		int tTypeSize = intTabTypeSize.intValue();
		logger.log(Logger.Level.TRACE, "tTypeSize: " + tTypeSize);

		try {

			// Add the prescribed table rows
			logger.log(Logger.Level.TRACE, "Adding the " + pTableName + " table rows");
			String updateString1 = System.getProperty("Dbschema_Tab2_Insert", "");
			PreparedStatement pStmt1 = conn.prepareStatement(updateString1);
			for (int j = 1; j <= tTypeSize; j++) {
				String sTypeDesc = "Type-" + j;
				int newType = j;
				pStmt1.setInt(1, newType);
				pStmt1.setString(2, sTypeDesc);
				pStmt1.executeUpdate();
			}

			// Add the prescribed table rows
			logger.log(Logger.Level.TRACE, "Adding the " + fTableName + " table rows");
			String updateString = System.getProperty("Dbschema_Tab1_Insert", "");
			PreparedStatement pStmt = conn.prepareStatement(updateString);
			for (int i = 1; i <= tSize; i++) {
				// Perform the insert(s)
				int newKey = i;
				String newName = fTableName + "-" + i;
				float newPrice = i + (float) .00;
				int newType = i % 5;
				if (newType == 0)
					newType = 5;
				pStmt.setInt(1, newKey);
				pStmt.setString(2, newName);
				pStmt.setFloat(3, newPrice);
				pStmt.setInt(4, newType);
				pStmt.executeUpdate();
			}

			pStmt.close();
			pStmt1.close();
		} catch (SQLException e) {
			logger.log(Logger.Level.ERROR, "SQLException creating " + fTableName + " or " + pTableName + " table", e);
			dropTables(conn);
			throw new RemoteException(e.getMessage());
		}
	}

	private void dropTables(Connection conn) throws RemoteException {
		logger.log(Logger.Level.TRACE, "dropTables");
		// Delete the fTable
		String removeString = props.getProperty("Dbschema_Tab1_Delete", "");
		String removeString1 = props.getProperty("Dbschema_Tab2_Delete", "");
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(removeString);
			stmt.close();
		} catch (SQLException e) {
			TestUtil.printStackTrace(e);

			throw new RemoteException(e.getMessage());
		} finally {
			try {
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(removeString1);
				stmt.close();
			} catch (SQLException e) {
				TestUtil.printStackTrace(e);

				throw new RemoteException(e.getMessage());
			}
		}

	}
}
