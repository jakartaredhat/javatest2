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
 * @(#)TxBeanEJB.java	1.24 03/05/16
 */

package com.sun.ts.tests.jta.ee.txpropagationtest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.ts.lib.util.RemoteLoggingInitException;
import com.sun.ts.lib.util.TSNamingContext;
import com.sun.ts.lib.util.TestUtil;

import jakarta.ejb.CreateException;
import jakarta.ejb.EJBException;
import jakarta.ejb.SessionBean;
import jakarta.ejb.SessionContext;

public class TxBeanEJB implements SessionBean {

	private static final Logger logger = LoggerFactory.getLogger(TxBeanEJB.class.getName());

	// testProps represent the test specific properties passed in
	// from the test harness.
	private Properties testProps = null;

	private SessionContext sctx = null;

	// con1 will be used for the dbTable1 connection
	// con2 will be used for the dbTable2 connection
	private transient Connection con1, con2;

	private transient Statement stmt;

	private transient PreparedStatement pStmt;

	// dbUser1 and dbPassword1 are used for con1 - dbTable1
	// dbUser2 and dbPassword2 are used for con2 - dbTable2
	private String dbUser1, dbPassword1, dbTable1, dbTable2;

	// TableSizes
	// dbSize1 is the size of dbTable1
	// dbSize1 is the size of dbTable2
	private Integer dbSize1;

	private Integer dbSize2;

	// DataSources
	// ds1 is associated with dbTable1
	// ds2 is associated with dbTable2
	private DataSource ds1, ds2;

	private TSNamingContext context = null;

	// Required EJB methods
	public void ejbCreate(Properties p) throws CreateException {
		logger.trace("ejbCreate");
		testProps = p;
		try {
			TestUtil.init(p);
			context = new TSNamingContext();
			// Get the dbTable1 DataSource
			dbTable1 = TestUtil.getTableName(p.getProperty("JTA_Tab1_Delete"));
			dbSize1 = (Integer) context.lookup("java:comp/env/size");
			ds1 = (DataSource) context.lookup("java:comp/env/jdbc/DB1");
			logger.trace("ds1: " + ds1);
			logger.info(dbTable1 + " DataSource lookup OK!");

			// Get the dbTable2 DataSource
			dbTable2 = TestUtil.getTableName(p.getProperty("JTA_Tab2_Delete"));
			dbSize2 = (Integer) context.lookup("java:comp/env/size");
			ds2 = (DataSource) context.lookup("java:comp/env/jdbc/DB1");
			logger.trace("ds2: " + ds2);
			logger.info(dbTable2 + " DataSource lookup OK!");

		} catch (Exception e) {
			logger.error("Unexpected exception getting the DB DataSource", e);
			throw new CreateException(e.getMessage());
		}
	}

	public void setSessionContext(SessionContext sc) {
		logger.trace("setSessionContext");
		this.sctx = sc;
	}

	public void ejbRemove() {
		logger.trace("ejbRemove");
	}

	public void ejbActivate() {
		logger.trace("ejbActivate");
	}

	public void ejbPassivate() {
		logger.trace("ejbPassivate");
	}

	// ===========================================================
	// The TxBean interface implementation

	// Database methods
	public void dbConnect(String tName) {
		logger.trace("dbConnect");
		try {
			if (tName.equals(dbTable1)) {
				// Make the dbTable1 connection
				conTable1();
				logger.info("Made the JDBC connection to " + dbTable1 + " DB");
			} else {
				// Make the dbTable2 connection
				conTable2();
				logger.info("Made the JDBC connection to " + dbTable2 + " DB");
			}
		} catch (Exception e) {
			logger.error("Unexpected exception on JDBC connection", e);
			throw new EJBException(e.getMessage());
		}
	}

	public void createData(String tName) {
		logger.trace("createData");
		try {
			if (tName.equals(dbTable1)) {
				// Create the dbTable1 table
				createTable1();
				logger.info("Created the table " + dbTable1 + " ");
			} else {
				// Create the dbTable2 table
				createTable2();
				logger.info("Created the table " + dbTable2 + " ");
			}
		} catch (Exception e) {
			logger.error("Exception creating table", e);
			throw new EJBException(e.getMessage());
		}
	}

	public boolean insert(String tName, int key) {
		logger.trace("insert");
		// Insert a row into the specified table
		int newKey = key;
		String newName = null;
		float newPrice = (float) .00 + newKey;

		try {
			if (tName.equals(dbTable1)) {
				// Prepare the new dbTable1 row entry
				newName = dbTable1 + "-" + newKey;

				String updateString = TestUtil.getProperty("JTA_Tab1_Insert");
				pStmt = con1.prepareStatement(updateString);
			} else {
				// Prepare the new dbTable2 row entry
				newName = dbTable2 + "-" + newKey;

				String updateString = TestUtil.getProperty("JTA_Tab2_Insert");
				pStmt = con2.prepareStatement(updateString);
			}

			// Perform the insert(s)
			pStmt.setInt(1, newKey);
			pStmt.setString(2, newName);
			pStmt.setFloat(3, newPrice);
			pStmt.executeUpdate();
			pStmt.close();

			logger.trace("Inserted a row into the table " + tName);
			return true;

		} catch (Exception e) {
			logger.error("Exception inserting a row into table " + tName + ";\n" + e.getMessage(), e);
			return false;
		}
	}

	public void delete(String tName, int fromKey, int toKey) {
		logger.trace("delete");
		// Delete row(s) from the specified table
		try {
			if (tName.equals(dbTable1)) {
				String updateString = TestUtil.getProperty("JTA_Delete1");
				pStmt = con1.prepareStatement(updateString);
			} else {
				String updateString = TestUtil.getProperty("JTA_Delete2");
				pStmt = con2.prepareStatement(updateString);
			}

			// Perform the delete(s)
			for (int i = fromKey; i <= toKey; i++) {
				pStmt.setInt(1, i);
				pStmt.executeUpdate();
			}
			pStmt.close();

			logger.trace("Deleted row(s) " + fromKey + " thru " + toKey + " from the table " + tName);

		} catch (Exception e) {
			logger.error("Exception deleting row(s) " + fromKey + " thru " + toKey + " from the table " + tName, e);
			throw new EJBException(e.getMessage());
		}
	}

	public void destroyData(String tName) {
		logger.trace("destroyData");
		try {
			if (tName.equals(dbTable1)) {
				dropTable1();
				logger.info("Deleted all rows from table " + dbTable1);
			} else {
				dropTable2();
				logger.info("Deleted all rows from table " + dbTable2);
			}
		} catch (Exception e) {
			logger.error("Exception occured trying to drop table", e);
			throw new EJBException(e.getMessage());
		}
	}

	public void dbUnConnect(String tName) {
		logger.trace("dbUnConnect");
		// Close the DB connections
		try {
			if (tName.equals(dbTable1)) {
				con1.close();
				con1 = null;
				logger.trace("Closed " + dbTable1 + " connection");
			} else {
				con2.close();
				con2 = null;
				logger.trace("Closed " + dbTable2 + " connection");
			}
		} catch (Exception e) {
			logger.error("Exception occured trying to close the DB connection", e);
			throw new EJBException(e.getMessage());
		}
	}

	// Test Results methods
	public Vector getResults(String tName) {
		logger.trace("getResults");
		ResultSet rs;
		Vector queryResults = new Vector();
		int i;
		String query, s, name;
		float f;

		try {
			if (tName.equals(dbTable1)) {
				query = TestUtil.getProperty("JTA_Tab1_Select");
				stmt = con1.createStatement();
				rs = stmt.executeQuery(query);
				name = "COF_NAME";
			} else {
				query = TestUtil.getProperty("JTA_Tab2_Select");
				stmt = con2.createStatement();
				rs = stmt.executeQuery(query);
				name = "CHOC_NAME";
			}

			while (rs.next()) {
				i = rs.getInt("KEY_ID");
				s = rs.getString(name);
				f = rs.getFloat("PRICE");
				queryResults.addElement(new Integer(i));
				queryResults.addElement(s);
				queryResults.addElement(new Float(f));
			}

			stmt.close();
			logger.info("Obtained " + tName + " table ResultSet");

		} catch (Exception e) {
			logger.error("Exception obtaining " + tName + " table ResultSet", e);
			throw new EJBException(e.getMessage());
		}
		return queryResults;
	}

	public void initLogging(Properties p) {
		logger.trace("initLogging");
		this.testProps = p;
		try {
			TestUtil.init(p);
		} catch (RemoteLoggingInitException e) {
			TestUtil.printStackTrace(e);
			throw new EJBException(e.getMessage());
		}
	}

	// private methods
	private void conTable1() {
		logger.trace("conTable1");
		try {
			// Get connection info for dbTable1 DB
			// dbUser1 = testProps.getProperty("user1");
			// dbPassword1 = testProps.getProperty("password1");
			// logger.trace("DB user1: " + dbUser1);
			// logger.trace("DB password1: " + dbPassword1);

			// Make the JDBC DB connection to dbTable1 DB
			// con1 = ds1.getConnection(dbUser1, dbPassword1);
			con1 = ds1.getConnection();
			logger.trace("con1: " + con1.toString());
		} catch (SQLException e) {
			logger.error("SQLException connecting to " + dbTable1 + " DB", e);
			throw new EJBException(e.getMessage());
		} catch (Exception ee) {
			logger.error("Exception connecting to " + dbTable1 + " DB", ee);
			throw new EJBException(ee.getMessage());
		}
	}

	private void conTable2() {
		logger.trace("conTable2");
		try {
			// Get connection info for dbTable2 DB
			// dbUser2 = testProps.getProperty("user2");
			// dbPassword2 = testProps.getProperty("password2");
			// logger.trace("DB user2: " + dbUser2);
			// logger.trace("DB password2: " + dbPassword2);

			// Make the JDBC DB connection to dbTable2 DB
			// con2 = ds2.getConnection(dbUser1, dbPassword1);
			con2 = ds2.getConnection();
			logger.trace("con2: " + con2.toString());
		} catch (SQLException e) {
			logger.error("SQLException connecting to " + dbTable2 + " DB", e);
			throw new EJBException(e.getMessage());
		} catch (Exception ee) {
			logger.error("Exception connecting to " + dbTable2 + " DB", ee);
			throw new EJBException(ee.getMessage());
		}
	}

	private void createTable1() {
		logger.trace("createTable1");
		// drop dbTable1 table if it exists
		try {
			dropTable1();
			logger.trace("All rows deleted from table " + dbTable1);
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			logger.info("SQLException encountered in createTable1: " + e.getMessage());
		}

		try {

			// Add the prescribed table rows
			logger.info("Adding the " + dbTable1 + " table rows");
			String updateString = TestUtil.getProperty("JTA_Tab1_Insert");
			pStmt = con1.prepareStatement(updateString);

			for (int i = 1; i <= dbSize1.intValue(); i++) {
				// Perform the insert(s)
				int newKey = i;
				String newName = dbTable1 + "-" + i;
				float newPrice = i + (float) .00;

				pStmt.setInt(1, newKey);
				pStmt.setString(2, newName);
				pStmt.setFloat(3, newPrice);

				pStmt.executeUpdate();
			}

			pStmt.close();
		} catch (SQLException e) {
			logger.error("SQLException creating " + dbTable1 + " table", e);
			throw new EJBException(e.getMessage());
		}
	}

	private void createTable2() {
		logger.trace("createTable2");
		try {
			dropTable2();
			logger.trace("All rows deleted from table " + dbTable2);
		} catch (Exception e) {
			TestUtil.printStackTrace(e);
			logger.info("SQLException encountered in createTable2: " + e.getMessage());
		}

		try {

			// Add the prescribed table rows
			logger.info("Adding the " + dbTable2 + " table rows");
			String updateString = TestUtil.getProperty("JTA_Tab2_Insert");

			pStmt = con2.prepareStatement(updateString);

			for (int i = 1; i <= dbSize2.intValue(); i++) {
				// Perform the insert(s)
				int newKey = i;
				String newName = dbTable2 + "-" + i;
				float newPrice = i + (float) .00;

				pStmt.setInt(1, newKey);
				pStmt.setString(2, newName);
				pStmt.setFloat(3, newPrice);

				pStmt.executeUpdate();
			}

			pStmt.close();
		} catch (SQLException e) {
			logger.error("SQLException creating " + dbTable2 + " table", e);
			throw new EJBException(e.getMessage());
		}
	}

	private void dropTable1() {
		logger.trace("dropTable1");
		// Delete the dbTable1 table
		String removeString = TestUtil.getProperty("JTA_Tab1_Delete");
		try {
			stmt = con1.createStatement();
			stmt.executeUpdate(removeString);
			stmt.close();
		} catch (SQLException e) {
			// logger.error("SQLException dropping "+dbTable1+" table", e);
			throw new EJBException(e.getMessage());
		}
	}

	private void dropTable2() {
		logger.trace("dropTable2");
		// Delete the dbTable2 table
		String removeString = TestUtil.getProperty("JTA_Tab2_Delete");
		try {
			stmt = con2.createStatement();
			stmt.executeUpdate(removeString);
			stmt.close();
		} catch (SQLException e) {
			logger.error("SQLException dropping " + dbTable2 + " table", e);
			throw new EJBException(e.getMessage());
		}
	}
}
