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
 * @(#)dbMetaClient9.java	1.28 03/05/16
 */

package com.sun.ts.tests.jdbc.ee.dbMeta.dbMeta9;

import java.io.IOException;
import java.io.Serializable;
import java.lang.System.Logger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.sun.ts.lib.util.TSNamingContextInterface;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jdbc.ee.common.DataSourceConnection;
import com.sun.ts.tests.jdbc.ee.common.DriverManagerConnection;
import com.sun.ts.tests.jdbc.ee.common.JDBCTestMsg;
import com.sun.ts.tests.jdbc.ee.common.dbSchema;

// Merant DataSource class
//import com.merant.sequelink.jdbcx.datasource.*;

/**
 * The dbMetaClient9 class tests methods of DatabaseMetaData interface using
 * Sun's J2EE Reference Implementation.
 * 
 */
@ExtendWith(ArquillianExtension.class)
public class dbMetaClient9 implements Serializable {
	
	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "dbMetaClient9_servlet_vehicle_web.war");
		archive.addPackages(true, "com.sun.ts.tests.jdbc.ee.common");
		archive.addClasses(dbMetaClient9.class);
		archive.addAsWebInfResource(dbMetaClient9.class.getPackage(), "servlet_vehicle_web.xml", "web.xml");
		return archive;
	};

	private static final String testName = "jdbc.ee.dbMeta.dbMeta9";

	private static final Logger logger = (Logger) System.getLogger(dbMetaClient9.class.getName());

	// Naming specific member variables
	private TSNamingContextInterface jc = null;

	// Harness requirements

	private transient Connection conn = null;

	private transient DatabaseMetaData dbmd = null;

	private DataSource ds1 = null;

	private dbSchema dbSch = null;

	private String dbName = null, dbUser = null, drManager = null;

	private String sCatalogName = null, sSchemaName = null, sPtable = null, sFtable = null;

	private JDBCTestMsg msg = null;

	/* Test setup: */
	/*
	 * @class.setup_props: Driver, the Driver name; db1, the database name with url;
	 * user1, the database user name; password1, the database password; db2, the
	 * database name with url; user2, the database user name; password2, the
	 * database password; DriverManager, flag for DriverManager; ptable, the primary
	 * table; ftable, the foreign table; cofSize, the initial size of the ptable;
	 * cofTypeSize, the initial size of the ftable; binarySize, size of binary data
	 * type; varbinarySize, size of varbinary data type; longvarbinarySize, size of
	 * longvarbinary data type;
	 * 
	 * @class.testArgs: -ap tssql.stmt
	 */
	@BeforeEach
	public void setup() throws Exception {
		try {
			try {
				drManager = System.getProperty("DriverManager", "");
				dbName = System.getProperty("db1", "");
				dbUser = System.getProperty("user1", "");
				sPtable = System.getProperty("ptable", "TSTABLE1");
				sFtable = System.getProperty("ftable", "TSTABLE2");
				if (dbName.length() == 0)
					throw new Exception("Invalid db1  Database Name");
				if (dbUser.length() == 0)
					throw new Exception("Invalid Login Id");
				if (sPtable.length() == 0)
					throw new Exception("Invalid Primary table");
				if (sFtable.length() == 0)
					throw new Exception("Invalid Foreign table");
				if (drManager.length() == 0)
					throw new Exception("Invalid DriverManager Name");

				int nLocdbname = dbName.indexOf('=');
				sCatalogName = dbName.substring(nLocdbname + 1);
				sCatalogName = sCatalogName.trim();
				sSchemaName = dbUser;

				if (drManager.equals("yes")) {
					logger.log(Logger.Level.TRACE, "Using DriverManager");
					DriverManagerConnection dmCon = new DriverManagerConnection();
					conn = dmCon.getConnection();
				} else {
					logger.log(Logger.Level.TRACE, "Using DataSource");
					DataSourceConnection dsCon = new DataSourceConnection();
					conn = dsCon.getConnection();
				}
				dbSch = new dbSchema();
				dbSch.createData(conn);
				dbmd = conn.getMetaData();
				msg = new JDBCTestMsg();
			} catch (SQLException ex) {
				logger.log(Logger.Level.ERROR, "SQL Exception : " + ex.getMessage(), ex);
			}
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "Setup Failed!");
			TestUtil.printStackTrace(e);
		}
	}

	/*
	 * @testName: testGetTypeInfo
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1072; JDBC:JAVADOC:1073;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the getTypeInfo() method on that object. It should return a
	 * ResultSet object Validate the column names and column ordering.
	 */
	@Test
	public void testGetTypeInfo() throws Exception {
		try {
			ResultSetMetaData rsmd = null;
			String sColumnNames[] = { "TYPE_NAME", "DATA_TYPE", "PRECISION", "LITERAL_PREFIX", "LITERAL_SUFFIX",
					"CREATE_PARAMS", "NULLABLE", "CASE_SENSITIVE", "SEARCHABLE", "UNSIGNED_ATTRIBUTE",
					"FIXED_PREC_SCALE", "AUTO_INCREMENT", "LOCAL_TYPE_NAME", "MINIMUM_SCALE", "MAXIMUM_SCALE",
					"SQL_DATA_TYPE", "SQL_DATETIME_SUB", "NUM_PREC_RADIX" };
			boolean statusColumnMatch = true;
			boolean statusColumnCount = true;
			String sRetStr = new String();
			sRetStr = "";

			int iColumnNamesLength = sColumnNames.length;
			msg.setMsg("Calling DatabaseMetaData.getTypeInfo");

			msg.setMsg("invoke getTypeInfo method");
			ResultSet oRet_ResultSet = dbmd.getTypeInfo();

			// Store all the Type names returned
			rsmd = oRet_ResultSet.getMetaData();
			int iCount = rsmd.getColumnCount();

			msg.setMsg("Minimum Column Count is:" + iColumnNamesLength);

			msg.setMsg("Comparing Column Lengths");
			if (iColumnNamesLength > iCount)
				statusColumnCount = false;
			else if (iColumnNamesLength < iCount) {
				iCount = iColumnNamesLength;
				statusColumnCount = true;
			} else
				statusColumnCount = true;

			msg.setMsg("Comparing Column Names...");

			while (iColumnNamesLength > 0) {
				if (sColumnNames[iColumnNamesLength - 1].equalsIgnoreCase(rsmd.getColumnName(iCount))) {
					statusColumnMatch = true;
				} else {
					statusColumnMatch = false;
					break;
				}
				iCount--;
				iColumnNamesLength--;
			}

			if ((statusColumnMatch == false) && (statusColumnCount == true)) {
				msg.printTestError("Columns return are not same either in order or name",
						"Call to getTypeInfo Failed!");

			}

			msg.setMsg("Store all the Type names returned");
			while (oRet_ResultSet.next())
				sRetStr += oRet_ResultSet.getString(1) + ",";
			if (sRetStr == "")
				msg.setMsg("getTypeInfo did not return any type names");
			else
				msg.setMsg("The Type names returned are : " + sRetStr);

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to getTypeInfo is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to getTypeInfo is Failed!");

		}
	}

	/*
	 * @testName: testSupportsResultSetType1
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1076; JDBC:JAVADOC:1077;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsResultSetType(int resType) method with Type
	 * TYPE_FORWARD_ONLY on that object.It should return a boolean value; either
	 * true or false.
	 *
	 */
	@Test
	public void testSupportsResultSetType1() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsResultSetType(TYPE_FORWARD_ONLY)");
			// invoke supportsResultSetType method
			boolean retValue = dbmd.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY);
			if (retValue)
				msg.setMsg("TYPE_FORWARD_ONLY ResultSetType is supported");
			else
				msg.setMsg("TYPE_FORWARD_ONLY ResultSetType is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsResultSetType1 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsResultSetType1 is Failed!");

		}
	}

	/*
	 * @testName: testSupportsResultSetType2
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1076; JDBC:JAVADOC:1077;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsResultSetType() method with Type
	 * TYPE_SCROLL_INSENSITIVE on that object.It should return a boolean value;
	 * either true or false.
	 *
	 */
	@Test
	public void testSupportsResultSetType2() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsResultSetType(TYPE_SCROLL_INSENSITIVE)");
			// invoke supportsResultSetType method
			boolean retValue = dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
			if (retValue)
				msg.setMsg("TYPE_SCROLL_INSENSITIVE ResultSetType is supported");
			else {
				msg.setMsg("TYPE_SCROLL_INSENSITIVE ResultSetType is not supported");

			}
			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsResultSetType2 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsResultSetType2 is Failed!");

		}
	}

	/*
	 * @testName: testSupportsResultSetType3
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1076; JDBC:JAVADOC:1077;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsResultSetType() method with Type
	 * TYPE_SCROLL_SENSITIVE on that object.It should return a boolean value; either
	 * true or false.
	 *
	 */
	@Test
	public void testSupportsResultSetType3() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsResultSetType(TYPE_SCROLL_SENSITIVE)");
			// invoke supportsResultSetType method
			boolean retValue = dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
			if (retValue)
				msg.setMsg("TYPE_SCROLL_SENSITIVE ResultSetType is supported");
			else
				msg.setMsg("TYPE_SCROLL_SENSITIVE ResultSetType is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsResultSetType3 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsResultSetType3 is Failed!");

		}
	}

	/*
	 * @testName: testSupportsResultSetConcurrency1
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1078; JDBC:JAVADOC:1079;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsResultSetConcurrency(int resType, int rsConcur)
	 * method on that object with TYPE_FORWARD_ONLY and CONCUR_READ_ONLY. It should
	 * return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsResultSetConcurrency1() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsResultSetConcurrency(TYPE_FORWARD_ONLY, CONCUR_READ_ONLY)");
			// invoke supportsResultSetConcurrency method
			boolean retValue = dbmd.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			if (retValue)
				msg.setMsg("supportsResultSetConcurrency(TYPE_FORWARD_ONLY, CONCUR_READ_ONLY) is supported");
			else
				msg.setMsg("supportsResultSetConcurrency(TYPE_FORWARD_ONLY, CONCUR_READ_ONLY) is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsResultSetConcurrency1 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsResultSetConcurrency1 is Failed!");

		}
	}

	/*
	 * @testName: testSupportsResultSetConcurrency2
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1078; JDBC:JAVADOC:1079;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsResultSetConcurrency(int resType, int rsConcur)
	 * method on that object with TYPE_FORWARD_ONLY and CONCUR_UPDATABLE. It should
	 * return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsResultSetConcurrency2() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.supportsResultSetConcurrency(TYPE_FORWARD_ONLY, CONCUR_UPDATABLE)");
			// invoke supportsResultSetConcurrency method
			boolean retValue = dbmd.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_UPDATABLE);
			if (retValue)
				msg.setMsg("supportsResultSetConcurrency(TYPE_FORWARD_ONLY, CONCUR_UPDATABLE) is supported");
			else
				msg.setMsg("supportsResultSetConcurrency(TYPE_FORWARD_ONLY, CONCUR_UPDATABLE) is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsResultSetConcurrency2 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsResultSetConcurrency2 is Failed!");

		}
	}

	/*
	 * @testName: testSupportsResultSetConcurrency3
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1078; JDBC:JAVADOC:1079;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsResultSetConcurrency(int resType, int rsConcur)
	 * method on that object with TYPE_SCROLL_INSENSITIVE and CONCUR_READ_ONLY. It
	 * should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsResultSetConcurrency3() throws Exception {
		try {
			msg.setMsg(
					"Calling DatabaseMetaData.supportsResultSetConcurrency(TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY)");
			// invoke supportsResultSetConcurrency method
			boolean retValue = dbmd.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			if (retValue)
				msg.setMsg("supportsResultSetConcurrency(TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY) is supported");
			else
				msg.setMsg("supportsResultSetConcurrency(TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY) is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsResultSetConcurrency3 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsResultSetConcurrency3 is Failed!");

		}
	}

	/*
	 * @testName: testSupportsResultSetConcurrency4
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1078; JDBC:JAVADOC:1079;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsResultSetConcurrency(int resType, int rsConcur)
	 * method on that object with TYPE_SCROLL_INSENSITIVE and CONCUR_UPDATABLE. It
	 * should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsResultSetConcurrency4() throws Exception {
		try {
			msg.setMsg(
					"Calling DatabaseMetaData.supportsResultSetConcurrency(TYPE_SCROLL_INSENSITIVE, CONCUR_UPDATABLE)");
			// invoke supportsResultSetConcurrency method
			boolean retValue = dbmd.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			if (retValue)
				msg.setMsg("supportsResultSetConcurrency(TYPE_SCROLL_INSENSITIVE, CONCUR_UPDATABLE) is supported");
			else
				msg.setMsg("supportsResultSetConcurrency(TYPE_SCROLL_INSENSITIVE, CONCUR_UPDATABLE) is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsResultSetConcurrency4 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsResultSetConcurrency4 is Failed!");

		}
	}

	/*
	 * @testName: testSupportsResultSetConcurrency5
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1078; JDBC:JAVADOC:1079;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsResultSetConcurrency(int resType, int rsConcur)
	 * method on that object with TYPE_SCROLL_SENSITIVE and CONCUR_READ_ONLY. It
	 * should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsResultSetConcurrency5() throws Exception {
		try {
			msg.setMsg(
					"Calling DatabaseMetaData.supportsResultSetConcurrency(TYPE_SCROLL_SENSITIVE, CONCUR_READ_ONLY)");
			// invoke supportsResultSetConcurrency method
			boolean retValue = dbmd.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			if (retValue)
				msg.setMsg("supportsResultSetConcurrency(TYPE_SCROLL_SENSITIVE, CONCUR_READ_ONLY) is supported");
			else
				msg.setMsg("supportsResultSetConcurrency(TYPE_SCROLL_SENSITIVE, CONCUR_READ_ONLY) is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsResultSetConcurrency5 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsResultSetConcurrency5 is Failed!");

		}
	}

	/*
	 * @testName: testSupportsResultSetConcurrency6
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1078; JDBC:JAVADOC:1079;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the supportsResultSetConcurrency(int resType, int rsConcur)
	 * method on that object with TYPE_SCROLL_SENSITIVE and CONCUR_UPDATABLE. It
	 * should return a boolean value; either true or false.
	 *
	 */
	@Test
	public void testSupportsResultSetConcurrency6() throws Exception {
		try {
			msg.setMsg(
					"Calling DatabaseMetaData.supportsResultSetConcurrency(TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE)");
			// invoke supportsResultSetConcurrency method
			boolean retValue = dbmd.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			if (retValue)
				msg.setMsg("supportsResultSetConcurrency(TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE) is supported");
			else
				msg.setMsg("supportsResultSetConcurrency(TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE) is not supported");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to supportsResultSetConcurrency6 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to supportsResultSetConcurrency6 is Failed!");

		}
	}

	/*
	 * @testName: testOwnUpdatesAreVisible1
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1080; JDBC:JAVADOC:1081;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the ownUpdatesAreVisible(int resType) method on that object
	 * with ResultSet.TYPE_FORWARD_ONLY. It should return a boolean value; either
	 * true or false.
	 *
	 */
	@Test
	public void testOwnUpdatesAreVisible1() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.ownUpdatesAreVisible(TYPE_FORWARD_ONLY)");
			// invoke ownUpdatesAreVisible method
			boolean retValue = dbmd.ownUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY);
			if (retValue)
				msg.setMsg("Result Set's own updates are visible for TYPE_FORWARD_ONLY");
			else
				msg.setMsg("Result Set's own updates are not visible for TYPE_FORWARD_ONLY");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to ownUpdatesAreVisible1 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to ownUpdatesAreVisible1 is Failed!");

		}
	}

	/*
	 * @testName: testOwnUpdatesAreVisible2
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1080; JDBC:JAVADOC:1081;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the ownUpdatesAreVisible(int resType) method on that object
	 * with ResultSet.TYPE_SCROLL_INSENSITIVE. It should return a boolean value;
	 * either true or false.
	 *
	 */
	@Test
	public void testOwnUpdatesAreVisible2() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.ownUpdatesAreVisible(TYPE_SCROLL_INSENSITIVE)");
			// invoke ownUpdatesAreVisible method
			boolean retValue = dbmd.ownUpdatesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE);
			if (retValue)
				msg.setMsg("Result Set's own updates are visible for TYPE_SCROLL_INSENSITIVE");
			else
				msg.setMsg("Result Set's own updates are not visible for TYPE_SCROLL_INSENSITIVE");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to ownUpdatesAreVisible2 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to ownUpdatesAreVisible2 is Failed!");

		}
	}

	/*
	 * @testName: testOwnUpdatesAreVisible3
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1080; JDBC:JAVADOC:1081;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the ownUpdatesAreVisible(int resType) method on that object
	 * with ResultSet.TYPE_SCROLL_SENSITIVE. It should return a boolean value;
	 * either true or false.
	 *
	 */
	@Test
	public void testOwnUpdatesAreVisible3() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.ownUpdatesAreVisible(TYPE_SCROLL_SENSITIVE)");
			// invoke ownUpdatesAreVisible method
			boolean retValue = dbmd.ownUpdatesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE);
			if (retValue)
				msg.setMsg("Result Set's own updates are visible for TYPE_SCROLL_SENSITIVE");
			else
				msg.setMsg("Result Set's own updates are not visible for TYPE_SCROLL_SENSITIVE");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to ownUpdatesAreVisible3 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to ownUpdatesAreVisible3 is Failed!");

		}
	}

	/*
	 * @testName: testOwnDeletesAreVisible1
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1082; JDBC:JAVADOC:1083;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the ownDeletesAreVisible(int resType) method on that object
	 * with ResultSet.TYPE_FORWARD_ONLY. It should return a boolean value; either
	 * true or false.
	 *
	 */
	@Test
	public void testOwnDeletesAreVisible1() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.ownDeletesAreVisible(TYPE_FORWARD_ONLY)");
			// invoke ownDeletesAreVisible method
			boolean retValue = dbmd.ownDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY);
			if (retValue)
				msg.setMsg("Result Set's own deletes are visible for TYPE_FORWARD_ONLY");
			else
				msg.setMsg("Result Set's own deletes are not visible for TYPE_FORWARD_ONLY");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to ownDeletesAreVisible1 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to ownDeletesAreVisible1 is Failed!");

		}
	}

	/*
	 * @testName: testOwnDeletesAreVisible2
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1082; JDBC:JAVADOC:1083;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the ownDeletesAreVisible(int resType) method on that object
	 * with ResultSet.TYPE_SCROLL_INSENSITIVE. It should return a boolean value;
	 * either true or false.
	 *
	 */
	@Test
	public void testOwnDeletesAreVisible2() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.ownDeletesAreVisible(TYPE_SCROLL_INSENSITIVE)");
			// invoke ownDeletesAreVisible method
			boolean retValue = dbmd.ownDeletesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE);
			if (retValue)
				msg.setMsg("Result Set's own deletes are visible for TYPE_SCROLL_INSENSITIVE");
			else
				msg.setMsg("Result Set's own deletes are not visible for TYPE_SCROLL_INSENSITIVE");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to ownDeletesAreVisible2 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to ownDeletesAreVisible2 is Failed!");

		}
	}

	/*
	 * @testName: testOwnDeletesAreVisible3
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1082; JDBC:JAVADOC:1083;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the ownDeletesAreVisible(int resType) method on that object
	 * with ResultSet.TYPE_SCROLL_SENSITIVE. It should return a boolean value;
	 * either true or false.
	 *
	 */
	@Test
	public void testOwnDeletesAreVisible3() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.ownDeletesAreVisible(TYPE_SCROLL_SENSITIVE)");
			// invoke ownDeletesAreVisible method
			boolean retValue = dbmd.ownDeletesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE);
			if (retValue)
				msg.setMsg("Result Set's own deletes are visible for TYPE_SCROLL_SENSITIVE");
			else
				msg.setMsg("Result Set's own deletes are not visible for TYPE_SCROLL_SENSITIVE");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to ownDeletesAreVisible3 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to ownDeletesAreVisible3 is Failed!");

		}
	}

	/*
	 * @testName: testOwnInsertsAreVisible1
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1084; JDBC:JAVADOC:1085;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the ownInsertsAreVisible(int resType) method on that object
	 * with ResultSet.TYPE_FORWARD_ONLY. It should return a boolean value; either
	 * true or false.
	 *
	 */
	@Test
	public void testOwnInsertsAreVisible1() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.ownInsertsAreVisible(TYPE_FORWARD_ONLY)");
			// invoke ownInsertsAreVisible method
			boolean retValue = dbmd.ownInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY);
			if (retValue)
				msg.setMsg("Result Set's own inserts are visible for TYPE_FORWARD_ONLY");
			else
				msg.setMsg("Result Set's own inserts are not visible for TYPE_FORWARD_ONLY");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to ownInsertsAreVisible1 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to ownInsertsAreVisible1 is Failed!");

		}
	}

	/*
	 * @testName: testOwnInsertsAreVisible2
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1084; JDBC:JAVADOC:1085;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the ownInsertsAreVisible(int resType) method on that object
	 * with ResultSet.TYPE_SCROLL_INSENSITIVE. It should return a boolean value;
	 * either true or false.
	 *
	 */
	@Test
	public void testOwnInsertsAreVisible2() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.ownInsertsAreVisible(TYPE_SCROLL_INSENSITIVE)");
			// invoke ownInsertsAreVisible method
			boolean retValue = dbmd.ownInsertsAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE);
			if (retValue)
				msg.setMsg("Result Set's own inserts are visible for TYPE_SCROLL_INSENSITIVE");
			else
				msg.setMsg("Result Set's own inserts are not visible for TYPE_SCROLL_INSENSITIVE");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to ownInsertsAreVisible2 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to ownInsertsAreVisible2 is Failed!");

		}
	}

	/*
	 * @testName: testOwnInsertsAreVisible3
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1084; JDBC:JAVADOC:1085;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the ownInsertsAreVisible(int resType) method on that object
	 * with ResultSet.TYPE_SCROLL_SENSITIVE. It should return a boolean value;
	 * either true or false.
	 *
	 */
	@Test
	public void testOwnInsertsAreVisible3() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.ownInsertsAreVisible(TYPE_SCROLL_SENSITIVE)");
			// invoke ownInsertsAreVisible method
			boolean retValue = dbmd.ownInsertsAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE);
			if (retValue)
				msg.setMsg("Result Set's own inserts are visible for TYPE_SCROLL_SENSITIVE");
			else
				msg.setMsg("Result Set's own inserts are not visible for TYPE_SCROLL_SENSITIVE");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to ownInsertsAreVisible3 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to ownInsertsAreVisible3 is Failed!");

		}
	}

	/*
	 * @testName: testOthersUpdatesAreVisible1
	 * 
	 * @assertion_ids: JDBC:SPEC:8; JDBC:JAVADOC:1086; JDBC:JAVADOC:1087;
	 * JavaEE:SPEC:193;
	 *
	 * @test_Strategy: Get a DatabaseMetadata object from the connection to the
	 * database and call the otherUpdatesAreVisible(int resType) method on that
	 * object with ResultSet.TYPE_FORWARD_ONLY. It should return a boolean value;
	 * either true or false.
	 *
	 */
	@Test
	public void testOthersUpdatesAreVisible1() throws Exception {
		try {
			msg.setMsg("Calling DatabaseMetaData.othersUpdatesAreVisible(TYPE_FORWARD_ONLY)");
			// invoke othersUpdatesAreVisible method
			boolean retValue = dbmd.othersUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY);
			if (retValue)
				msg.setMsg("Updates made by others are visible for TYPE_FORWARD_ONLY");
			else
				msg.setMsg("Updates made by others are not visible for TYPE_FORWARD_ONLY");

			msg.printTestMsg();
		} catch (SQLException sqle) {
			msg.printSQLError(sqle, "Call to othersUpdatesAreVisible1 is Failed!");

		} catch (Exception e) {
			msg.printError(e, "Call to othersUpdatesAreVisible1 is Failed!");

		}
	}

	/* cleanup */
	@AfterEach
	public void cleanup() throws Exception {
		try {
			// Close the database
			dbSch.destroyData(conn);
			dbSch.dbUnConnect(conn);
			logger.log(Logger.Level.INFO, "Cleanup ok;");
		} catch (Exception e) {
			logger.log(Logger.Level.ERROR, "An error occurred while closing the database connection", e);
		}
	}
}
